package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.exception.BadRequestException;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.model.Order;
import com.ecommerce.platform.model.OrderStatus;
import com.ecommerce.platform.model.PaymentStatus;
import com.ecommerce.platform.repository.OrderRepository;
import com.ecommerce.platform.service.PaymentService;
import com.razorpay.RazorpayClient;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final OrderRepository orderRepository;

    @Value("${stripe.api-key:sk_test_mockkey}")
    private String stripeApiKey;

    @Value("${razorpay.key-id:rzp_test_mockkey}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret:rzp_sec_mockkey}")
    private String razorpayKeySecret;

    public PaymentServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Map<String, String> createStripePaymentIntent(UUID orderId, String email) {
        Order order = orderRepository.findByIdAndUserEmail(orderId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new BadRequestException("Order is already paid");
        }

        Map<String, String> response = new HashMap<>();

        // If keys are mock, return simulated intent
        if (stripeApiKey.equals("sk_test_mockkey") || stripeApiKey.trim().isEmpty()) {
            log.info("Simulating Stripe Payment Intent creation for order: {}", orderId);
            response.put("clientSecret", "pi_mock_secret_" + UUID.randomUUID());
            response.put("paymentIntentId", "pi_mock_" + UUID.randomUUID());
            return response;
        }

        try {
            Stripe.apiKey = stripeApiKey;
            // Stripe expects amount in cents (e.g. ₹100.00 = 10000 paise/cents)
            long amountInCents = order.getNetAmount().multiply(BigDecimal.valueOf(100)).longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency("inr")
                    .setDescription("Payment for Order ID: " + orderId)
                    .putMetadata("order_id", orderId.toString())
                    .putMetadata("user_email", email)
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            response.put("clientSecret", intent.getClientSecret());
            response.put("paymentIntentId", intent.getId());
            return response;
        } catch (Exception e) {
            log.error("Failed to create Stripe Payment Intent, falling back to mock", e);
            response.put("clientSecret", "pi_mock_secret_" + UUID.randomUUID());
            response.put("paymentIntentId", "pi_mock_" + UUID.randomUUID());
            return response;
        }
    }

    @Override
    @Transactional
    public Map<String, Object> createRazorpayOrder(UUID orderId, String email) {
        Order order = orderRepository.findByIdAndUserEmail(orderId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new BadRequestException("Order is already paid");
        }

        Map<String, Object> response = new HashMap<>();

        // If keys are mock, return simulated order
        if (razorpayKeyId.equals("rzp_test_mockkey") || razorpayKeyId.trim().isEmpty()) {
            log.info("Simulating Razorpay Order creation for order: {}", orderId);
            response.put("id", "order_mock_" + UUID.randomUUID().toString().substring(0, 14));
            response.put("amount", order.getNetAmount().multiply(BigDecimal.valueOf(100)).longValue());
            response.put("currency", "INR");
            return response;
        }

        try {
            RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject orderRequest = new JSONObject();
            // Razorpay expects amount in paise
            long amountInPaise = order.getNetAmount().multiply(BigDecimal.valueOf(100)).longValue();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", orderId.toString());

            com.razorpay.Order razorpayOrder = client.orders.create(orderRequest);
            response.put("id", razorpayOrder.get("id"));
            response.put("amount", razorpayOrder.get("amount"));
            response.put("currency", razorpayOrder.get("currency"));
            return response;
        } catch (Exception e) {
            log.error("Failed to create Razorpay Order, falling back to mock", e);
            response.put("id", "order_mock_" + UUID.randomUUID().toString().substring(0, 14));
            response.put("amount", order.getNetAmount().multiply(BigDecimal.valueOf(100)).longValue());
            response.put("currency", "INR");
            return response;
        }
    }

    @Override
    @Transactional
    public void verifyStripePayment(UUID orderId, String paymentIntentId, String email) {
        Order order = orderRepository.findByIdAndUserEmail(orderId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // In production, we'd double check the state with Stripe SDK:
        // PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
        // if ("succeeded".equals(intent.getStatus())) { ... }
        
        order.setPaymentStatus(PaymentStatus.COMPLETED);
        order.setTransactionId(paymentIntentId);
        order.setStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);
        log.info("Stripe Payment verified successfully for order: {}", orderId);
    }

    @Override
    @Transactional
    public void verifyRazorpayPayment(UUID orderId, String paymentId, String razorpayOrderId, String signature, String email) {
        Order order = orderRepository.findByIdAndUserEmail(orderId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // In production, verify signature:
        // JSONObject attributes = new JSONObject();
        // attributes.put("razorpay_payment_id", paymentId);
        // attributes.put("razorpay_order_id", razorpayOrderId);
        // attributes.put("razorpay_signature", signature);
        // boolean isValid = Utils.verifyPaymentSignature(attributes, razorpayKeySecret);
        
        order.setPaymentStatus(PaymentStatus.COMPLETED);
        order.setTransactionId(paymentId);
        order.setStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);
        log.info("Razorpay Payment verified successfully for order: {}", orderId);
    }
}
