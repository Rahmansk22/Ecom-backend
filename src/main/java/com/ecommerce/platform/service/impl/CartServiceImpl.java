package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.CartItemDto;
import com.ecommerce.platform.exception.BadRequestException;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.model.ProductImage;
import com.ecommerce.platform.model.ProductVariant;
import com.ecommerce.platform.repository.ProductImageRepository;
import com.ecommerce.platform.repository.ProductVariantRepository;
import com.ecommerce.platform.service.CartService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

    private final StringRedisTemplate redisTemplate;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;
    private final ObjectMapper objectMapper;

    public CartServiceImpl(StringRedisTemplate redisTemplate, ProductVariantRepository variantRepository,
                           ProductImageRepository imageRepository, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.variantRepository = variantRepository;
        this.imageRepository = imageRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<CartItemDto> getCart(String email) {
        List<CartItemDto> cart = fetchCartFromRedis(email);
        boolean dirty = false;
        List<CartItemDto> healedCart = new ArrayList<>();
        for (CartItemDto item : cart) {
            if (item.productSlug() == null || item.productSlug().isBlank() || "null".equals(item.productSlug())) {
                Optional<ProductVariant> optVariant = variantRepository.findById(item.variantId());
                if (optVariant.isPresent()) {
                    ProductVariant variant = optVariant.get();
                    healedCart.add(new CartItemDto(
                            item.variantId(),
                            item.productTitle(),
                            item.sku(),
                            item.price(),
                            item.imageUrl(),
                            item.quantity(),
                            item.attributesJson(),
                            variant.getStock(),
                            variant.getProduct().getSlug()
                    ));
                    dirty = true;
                } else {
                    healedCart.add(item);
                }
            } else {
                healedCart.add(item);
            }
        }
        if (dirty) {
            saveCartToRedis(email, healedCart);
        }
        return healedCart;
    }

    @Override
    public List<CartItemDto> addItemToCart(String email, UUID variantId, int quantity) {
        log.info("Adding variant {} to cart for user {}", variantId, email);
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));

        if (variant.getStock() < quantity) {
            throw new BadRequestException("Requested quantity exceeds available stock (" + variant.getStock() + ")");
        }

        List<CartItemDto> cart = getCart(email); // Use healed cart
        boolean found = false;

        for (int i = 0; i < cart.size(); i++) {
            CartItemDto item = cart.get(i);
            if (item.variantId().equals(variantId)) {
                int newQty = item.quantity() + quantity;
                if (variant.getStock() < newQty) {
                    throw new BadRequestException("Cannot add. Total quantity exceeds available stock");
                }
                cart.set(i, new CartItemDto(
                        item.variantId(),
                        item.productTitle(),
                        item.sku(),
                        item.price(),
                        item.imageUrl(),
                        newQty,
                        item.attributesJson(),
                        variant.getStock(),
                        variant.getProduct().getSlug()
                ));
                found = true;
                break;
            }
        }

        if (!found) {
            List<ProductImage> images = imageRepository.findByVariantOrderByDisplayOrderAsc(variant);
            String primaryImage = images.isEmpty() ? null : images.get(0).getImageUrl();

            cart.add(new CartItemDto(
                    variant.getId(),
                    variant.getProduct().getTitle(),
                    variant.getSku(),
                    variant.getPrice(),
                    primaryImage,
                    quantity,
                    variant.getAttributesJson(),
                    variant.getStock(),
                    variant.getProduct().getSlug()
            ));
        }

        saveCartToRedis(email, cart);
        return cart;
    }

    @Override
    public List<CartItemDto> updateItemQuantity(String email, UUID variantId, int quantity) {
        log.info("Updating quantity for variant {} in cart of {}", variantId, email);
        if (quantity <= 0) {
            return removeItemFromCart(email, variantId);
        }

        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        if (variant.getStock() < quantity) {
            throw new BadRequestException("Requested quantity exceeds available stock (" + variant.getStock() + ")");
        }

        List<CartItemDto> cart = getCart(email); // Use healed cart
        for (int i = 0; i < cart.size(); i++) {
            CartItemDto item = cart.get(i);
            if (item.variantId().equals(variantId)) {
                cart.set(i, new CartItemDto(
                        item.variantId(),
                        item.productTitle(),
                        item.sku(),
                        item.price(),
                        item.imageUrl(),
                        quantity,
                        item.attributesJson(),
                        variant.getStock(),
                        variant.getProduct().getSlug()
                ));
                break;
            }
        }

        saveCartToRedis(email, cart);
        return cart;
    }

    @Override
    public List<CartItemDto> removeItemFromCart(String email, UUID variantId) {
        log.info("Removing variant {} from cart of {}", variantId, email);
        List<CartItemDto> cart = fetchCartFromRedis(email);
        cart.removeIf(item -> item.variantId().equals(variantId));
        saveCartToRedis(email, cart);
        return cart;
    }

    @Override
    public List<CartItemDto> mergeGuestCart(String email, List<CartItemDto> guestCart) {
        log.info("Merging guest cart with user cart for {}", email);
        List<CartItemDto> userCart = getCart(email); // Use healed cart

        for (CartItemDto guestItem : guestCart) {
            Optional<ProductVariant> optVariant = variantRepository.findById(guestItem.variantId());
            if (optVariant.isPresent()) {
                ProductVariant variant = optVariant.get();
                boolean merged = false;

                for (int i = 0; i < userCart.size(); i++) {
                    CartItemDto userItem = userCart.get(i);
                    if (userItem.variantId().equals(guestItem.variantId())) {
                        int totalQty = Math.min(userItem.quantity() + guestItem.quantity(), variant.getStock());
                        userCart.set(i, new CartItemDto(
                                userItem.variantId(),
                                userItem.productTitle(),
                                userItem.sku(),
                                userItem.price(),
                                userItem.imageUrl(),
                                totalQty,
                                userItem.attributesJson(),
                                variant.getStock(),
                                variant.getProduct().getSlug()
                        ));
                        merged = true;
                        break;
                    }
                }

                if (!merged) {
                    List<ProductImage> images = imageRepository.findByVariantOrderByDisplayOrderAsc(variant);
                    String primaryImage = images.isEmpty() ? null : images.get(0).getImageUrl();

                    userCart.add(new CartItemDto(
                            variant.getId(),
                            variant.getProduct().getTitle(),
                            variant.getSku(),
                            variant.getPrice(),
                            primaryImage,
                            Math.min(guestItem.quantity(), variant.getStock()),
                            variant.getAttributesJson(),
                            variant.getStock(),
                            variant.getProduct().getSlug()
                    ));
                }
            }
        }

        saveCartToRedis(email, userCart);
        return userCart;
    }

    @Override
    public void clearCart(String email) {
        redisTemplate.delete("cart:" + email);
    }

    private List<CartItemDto> fetchCartFromRedis(String email) {
        String json = redisTemplate.opsForValue().get("cart:" + email);
        if (json == null) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<List<CartItemDto>>() {});
        } catch (Exception e) {
            log.error("Failed to parse cart json from Redis", e);
            return new ArrayList<>();
        }
    }

    private void saveCartToRedis(String email, List<CartItemDto> cart) {
        try {
            String json = objectMapper.writeValueAsString(cart);
            redisTemplate.opsForValue().set("cart:" + email, json, 7, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("Failed to serialize cart json to Redis", e);
        }
    }
}
