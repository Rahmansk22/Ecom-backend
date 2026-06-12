package com.ecommerce.platform.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
    UUID id,
    UUID variantId,
    String productTitle,
    String sku,
    Integer quantity,
    BigDecimal priceAtPurchase,
    String imageUrl
) {}
