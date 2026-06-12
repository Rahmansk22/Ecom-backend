package com.ecommerce.platform.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
    UUID id,
    String title,
    String slug,
    String brand,
    String categoryName,
    String categorySlug,
    BigDecimal minPrice,
    BigDecimal compareAtPrice,
    String imageUrl
) {}
