package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.CartItemDto;
import com.ecommerce.platform.dto.CheckoutRequest;
import com.ecommerce.platform.dto.OrderItemResponse;
import com.ecommerce.platform.dto.OrderResponse;
import com.ecommerce.platform.exception.BadRequestException;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.model.*;
import com.ecommerce.platform.repository.*;
import com.ecommerce.platform.service.CartService;
import com.ecommerce.platform.service.KafkaProducerService;
import com.ecommerce.platform.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;
    private final CartService cartService;
    private final KafkaProducerService kafkaProducerService;

    public OrderServiceImpl(OrderRepository orderRepository, AddressRepository addressRepository,
                            UserRepository userRepository, ProductVariantRepository variantRepository,
                            ProductImageRepository imageRepository, CartService cartService,
                            KafkaProducerService kafkaProducerService) {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.variantRepository = variantRepository;
        this.imageRepository = imageRepository;
        this.cartService = cartService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    @Transactional
    public OrderResponse checkout(String email, CheckoutRequest request) {
        // 1. Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2. Fetch address and validate ownership
        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Selected address does not belong to user");
        }

        // 3. Fetch cart items
        List<CartItemDto> cartItems = cartService.getCart(email);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        // 4. Validate stock & build order items
        Order order = new Order();
        order.setUser(user);
        
        // Formatted shipping name: use User's name or custom label
        String shippingName = user.getFirstName() + " " + user.getLastName();
        order.setShippingName(shippingName);
        order.setShippingAddressLine1(address.getAddressLine1());
        order.setShippingAddressLine2(address.getAddressLine2());
        order.setShippingCity(address.getCity());
        order.setShippingState(address.getState());
        order.setShippingCountry(address.getCountry());
        order.setShippingPincode(address.getPincode());
        order.setShippingPhone(user.getMobile());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItemDto cartItem : cartItems) {
            ProductVariant variant = variantRepository.findById(cartItem.variantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variant not found for ID: " + cartItem.variantId()));

            if (variant.getStock() < cartItem.quantity()) {
                throw new BadRequestException("Insufficient stock for item: " + cartItem.productTitle() + 
                        ". Available: " + variant.getStock() + ", Requested: " + cartItem.quantity());
            }

            // Deduct stock
            variant.setStock(variant.getStock() - cartItem.quantity());
            variantRepository.save(variant);

            OrderItem orderItem = new OrderItem(order, variant, cartItem.quantity(), variant.getPrice());
            orderItems.add(orderItem);

            totalPrice = totalPrice.add(variant.getPrice().multiply(BigDecimal.valueOf(cartItem.quantity())));
        }

        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);
        order.setDiscountAmount(BigDecimal.ZERO);

        // Shipping charges: Free for orders >= 500, else 40
        BigDecimal shippingCharge = totalPrice.compareTo(BigDecimal.valueOf(500)) >= 0 ? BigDecimal.ZERO : BigDecimal.valueOf(40);
        order.setShippingCharge(shippingCharge);

        BigDecimal netAmount = totalPrice.add(shippingCharge);
        order.setNetAmount(netAmount);

        order.setStatus(OrderStatus.PLACED);
        
        // Simulating payment integration
        order.setPaymentMethod(request.paymentMethod());
        if (request.paymentMethod() == PaymentMethod.COD) {
            order.setPaymentStatus(PaymentStatus.PENDING);
        } else {
            order.setPaymentStatus(PaymentStatus.COMPLETED);
            order.setTransactionId(request.transactionId() != null ? request.transactionId() : UUID.randomUUID().toString());
        }

        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cartService.clearCart(email);

        // Trigger Kafka notifications
        try {
            kafkaProducerService.sendEvent(
                    "ORDER_PLACED",
                    user.getEmail(),
                    "Order Placed!",
                    "Your order with ID " + savedOrder.getId() + " has been successfully placed. Net Amount: ₹" + savedOrder.getNetAmount()
            );

            for (OrderItem item : savedOrder.getItems()) {
                User seller = item.getVariant().getProduct().getSeller();
                if (seller != null) {
                    kafkaProducerService.sendEvent(
                            "ORDER_RECEIVED",
                            seller.getEmail(),
                            "New Customer Order",
                            "A customer bought " + item.getQuantity() + "x " + item.getVariant().getProduct().getTitle() + " (SKU: " + item.getVariant().getSku() + ")"
                    );
                }
            }
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(OrderServiceImpl.class).warn("Failed to stream order events to Kafka", e);
        }

        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(String email) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(email).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderDetails(String email, UUID orderId) {
        Order order = orderRepository.findByIdAndUserEmail(orderId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(String email, UUID orderId) {
        Order order = orderRepository.findByIdAndUserEmail(orderId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order cannot be cancelled in its current state: " + order.getStatus());
        }

        // Restock items
        for (OrderItem item : order.getItems()) {
            ProductVariant variant = item.getVariant();
            variant.setStock(variant.getStock() + item.getQuantity());
            variantRepository.save(variant);
        }

        order.setStatus(OrderStatus.CANCELLED);
        if (order.getPaymentStatus() == PaymentStatus.COMPLETED) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        }
        Order savedOrder = orderRepository.save(order);

        // Trigger Kafka notifications
        try {
            kafkaProducerService.sendEvent(
                    "ORDER_CANCELLED",
                    email,
                    "Order Cancelled",
                    "Your order with ID " + savedOrder.getId() + " has been cancelled."
            );

            for (OrderItem item : savedOrder.getItems()) {
                User seller = item.getVariant().getProduct().getSeller();
                if (seller != null) {
                    kafkaProducerService.sendEvent(
                            "ORDER_CANCELLED_ALERT",
                            seller.getEmail(),
                            "Order Cancelled By Customer",
                            "The order containing " + item.getQuantity() + "x " + item.getVariant().getProduct().getTitle() + " has been cancelled."
                    );
                }
            }
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(OrderServiceImpl.class).warn("Failed to stream cancellation events to Kafka", e);
        }

        return mapToOrderResponse(savedOrder);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> {
                    List<ProductImage> images = imageRepository.findByVariantOrderByDisplayOrderAsc(item.getVariant());
                    String primaryImage = images.isEmpty() ? null : images.get(0).getImageUrl();
                    return new OrderItemResponse(
                            item.getId(),
                            item.getVariant().getId(),
                            item.getVariant().getProduct().getTitle(),
                            item.getVariant().getSku(),
                            item.getQuantity(),
                            item.getPriceAtPurchase(),
                            primaryImage
                    );
                })
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getShippingName(),
                order.getShippingAddressLine1(),
                order.getShippingAddressLine2(),
                order.getShippingCity(),
                order.getShippingState(),
                order.getShippingCountry(),
                order.getShippingPincode(),
                order.getShippingPhone(),
                order.getTotalPrice(),
                order.getDiscountAmount(),
                order.getShippingCharge(),
                order.getNetAmount(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getPaymentMethod(),
                order.getTransactionId(),
                order.getCreatedAt(),
                itemResponses
        );
    }
}
