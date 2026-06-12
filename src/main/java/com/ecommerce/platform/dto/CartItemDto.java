package com.ecommerce.platform.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemDto(
    UUID variantId,
    String productTitle,
    String sku,
    BigDecimal price,
    String imageUrl,
    Integer quantity,
    String attributesJson,
    Integer stock,
    String productSlug
) {}
