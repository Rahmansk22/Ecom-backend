package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.*;
import com.ecommerce.platform.exception.BadRequestException;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.model.*;
import com.ecommerce.platform.repository.*;
import com.ecommerce.platform.service.SellerService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SellerServiceImpl implements SellerService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;
    private final OrderRepository orderRepository;

    public SellerServiceImpl(ProductRepository productRepository, UserRepository userRepository,
                             CategoryRepository categoryRepository, ProductVariantRepository variantRepository,
                             ProductImageRepository imageRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.variantRepository = variantRepository;
        this.imageRepository = imageRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getSellerProducts(String email) {
        return productRepository.findBySellerEmail(email).stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products", "categories"}, allEntries = true)
    public ProductDetailResponse addSellerProduct(String email, SellerProductRequest request) {
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found"));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        String slug = request.title().toLowerCase().replaceAll("[^a-z0-9]+", "-");
        if (productRepository.findBySlug(slug).isPresent()) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Product product = new Product();
        product.setTitle(request.title());
        product.setSlug(slug);
        product.setBrand(request.brand());
        product.setDescription(request.description());
        product.setCategory(category);
        product.setSeller(seller);
        product.setStatus(request.status() != null ? request.status() : "ACTIVE");
        product.setCountryOfOrigin(request.countryOfOrigin());
        product.setHsnCode(request.hsnCode());

        Product savedProduct = productRepository.save(product);
        List<ProductVariantDto> savedVariantDtos = new ArrayList<>();

        if (request.variants() != null) {
            for (SellerProductRequest.VariantRequest varReq : request.variants()) {
                ProductVariant variant = new ProductVariant();
                variant.setProduct(savedProduct);
                variant.setSku(varReq.sku() != null ? varReq.sku() : "SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                variant.setPrice(varReq.price());
                variant.setCompareAtPrice(varReq.compareAtPrice());
                variant.setStock(varReq.stock() != null ? varReq.stock() : 0);
                variant.setAttributesJson(varReq.attributesJson());
                ProductVariant savedVariant = variantRepository.save(variant);

                List<String> imageUrls = new ArrayList<>();
                if (varReq.imageUrls() != null) {
                    int order = 0;
                    for (String url : varReq.imageUrls()) {
                        ProductImage image = new ProductImage(null, savedVariant, url, order++);
                        imageRepository.save(image);
                        imageUrls.add(url);
                    }
                }

                savedVariantDtos.add(new ProductVariantDto(
                        savedVariant.getId(),
                        savedVariant.getSku(),
                        savedVariant.getPrice(),
                        savedVariant.getCompareAtPrice(),
                        savedVariant.getStock(),
                        savedVariant.getAttributesJson(),
                        imageUrls
                ));
            }
        }

        return new ProductDetailResponse(
                savedProduct.getId(),
                savedProduct.getTitle(),
                savedProduct.getSlug(),
                savedProduct.getDescription(),
                savedProduct.getBrand(),
                savedProduct.getCategory().getName(),
                savedProduct.getCategory().getSlug(),
                savedProduct.getCountryOfOrigin(),
                savedProduct.getHsnCode(),
                savedVariantDtos
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products", "categories"}, allEntries = true)
    public ProductDetailResponse updateSellerProduct(String email, UUID productId, SellerProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getSeller() == null || !product.getSeller().getEmail().equals(email)) {
            throw new BadRequestException("Product does not belong to this seller");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        product.setTitle(request.title());
        if (product.getSlug() == null || product.getSlug().isBlank() || "null".equals(product.getSlug())) {
            String slug = request.title().toLowerCase().replaceAll("[^a-z0-9]+", "-");
            if (productRepository.findBySlug(slug).isPresent()) {
                slug = slug + "-" + System.currentTimeMillis();
            }
            product.setSlug(slug);
        }
        product.setBrand(request.brand());
        product.setDescription(request.description());
        product.setCategory(category);
        if (request.status() != null) {
            product.setStatus(request.status());
        }
        product.setCountryOfOrigin(request.countryOfOrigin());
        product.setHsnCode(request.hsnCode());

        Product savedProduct = productRepository.save(product);

        // Update variants
        List<ProductVariant> currentVariants = variantRepository.findByProduct(savedProduct);
        
        // Simple strategy: delete current variants and create new ones (or update matched ones)
        // To preserve references, we clean up images and recreate variants
        for (ProductVariant v : currentVariants) {
            imageRepository.deleteByVariant(v);
            variantRepository.delete(v);
        }

        List<ProductVariantDto> savedVariantDtos = new ArrayList<>();
        if (request.variants() != null) {
            for (SellerProductRequest.VariantRequest varReq : request.variants()) {
                ProductVariant variant = new ProductVariant();
                variant.setProduct(savedProduct);
                variant.setSku(varReq.sku());
                variant.setPrice(varReq.price());
                variant.setCompareAtPrice(varReq.compareAtPrice());
                variant.setStock(varReq.stock());
                variant.setAttributesJson(varReq.attributesJson());
                ProductVariant savedVariant = variantRepository.save(variant);

                List<String> imageUrls = new ArrayList<>();
                if (varReq.imageUrls() != null) {
                    int order = 0;
                    for (String url : varReq.imageUrls()) {
                        ProductImage image = new ProductImage(null, savedVariant, url, order++);
                        imageRepository.save(image);
                        imageUrls.add(url);
                    }
                }

                savedVariantDtos.add(new ProductVariantDto(
                        savedVariant.getId(),
                        savedVariant.getSku(),
                        savedVariant.getPrice(),
                        savedVariant.getCompareAtPrice(),
                        savedVariant.getStock(),
                        savedVariant.getAttributesJson(),
                        imageUrls
                ));
            }
        }

        return new ProductDetailResponse(
                savedProduct.getId(),
                savedProduct.getTitle(),
                savedProduct.getSlug(),
                savedProduct.getDescription(),
                savedProduct.getBrand(),
                savedProduct.getCategory().getName(),
                savedProduct.getCategory().getSlug(),
                savedProduct.getCountryOfOrigin(),
                savedProduct.getHsnCode(),
                savedVariantDtos
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products", "categories"}, allEntries = true)
    public void deleteSellerProduct(String email, UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getSeller() == null || !product.getSeller().getEmail().equals(email)) {
            throw new BadRequestException("Product does not belong to this seller");
        }

        // Deactivate instead of hard deleting to preserve order detail constraints
        product.setStatus("INACTIVE");
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getSellerOrders(String email) {
        return orderRepository.findOrdersBySellerEmail(email).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSellerDashboardMetrics(String email) {
        List<Product> products = productRepository.findBySellerEmail(email);
        List<Order> orders = orderRepository.findOrdersBySellerEmail(email);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        int lowStockCount = 0;
        int totalInventory = 0;

        for (Product p : products) {
            List<ProductVariant> variants = variantRepository.findByProduct(p);
            for (ProductVariant v : variants) {
                totalInventory += v.getStock();
                if (v.getStock() <= 5) {
                    lowStockCount++;
                }
            }
        }

        // Calculate revenue specifically from this seller's products
        for (Order order : orders) {
            if (order.getPaymentStatus() == PaymentStatus.COMPLETED) {
                for (OrderItem item : order.getItems()) {
                    if (item.getVariant().getProduct().getSeller() != null &&
                            item.getVariant().getProduct().getSeller().getEmail().equals(email)) {
                        totalRevenue = totalRevenue.add(item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())));
                    }
                }
            }
        }

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalRevenue", totalRevenue);
        metrics.put("ordersCount", orders.size());
        metrics.put("totalProducts", products.size());
        metrics.put("totalInventory", totalInventory);
        metrics.put("lowStockCount", lowStockCount);

        return metrics;
    }

    private ProductResponse mapToSummaryResponse(Product product) {
        List<ProductVariant> variants = variantRepository.findByProduct(product);
        BigDecimal minPrice = BigDecimal.ZERO;
        BigDecimal compareAtPrice = null;
        String primaryImage = null;

        if (!variants.isEmpty()) {
            ProductVariant cheapest = variants.get(0);
            for (ProductVariant v : variants) {
                if (v.getPrice().compareTo(cheapest.getPrice()) < 0) {
                    cheapest = v;
                }
            }
            minPrice = cheapest.getPrice();
            compareAtPrice = cheapest.getCompareAtPrice();

            List<ProductImage> images = imageRepository.findByVariantOrderByDisplayOrderAsc(cheapest);
            if (images.isEmpty() && cheapest != variants.get(0)) {
                images = imageRepository.findByVariantOrderByDisplayOrderAsc(variants.get(0));
            }
            if (!images.isEmpty()) {
                primaryImage = images.get(0).getImageUrl();
            }
        }

        return new ProductResponse(
                product.getId(),
                product.getTitle(),
                product.getSlug(),
                product.getBrand(),
                product.getCategory().getName(),
                product.getCategory().getSlug(),
                minPrice,
                compareAtPrice,
                primaryImage
        );
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
