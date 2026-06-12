package com.ecommerce.platform.dto;

import java.util.List;
import java.util.UUID;

public record ProductDetailResponse(
    UUID id,
    String title,
    String slug,
    String description,
    String brand,
    String categoryName,
    String categorySlug,
    String countryOfOrigin,
    String hsnCode,
    List<ProductVariantDto> variants
) implements java.io.Serializable {}
