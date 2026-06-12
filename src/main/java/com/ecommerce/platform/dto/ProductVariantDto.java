package com.ecommerce.platform.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductVariantDto(
    UUID id,
    String sku,
    BigDecimal price,
    BigDecimal compareAtPrice,
    Integer stock,
    String attributesJson,
    List<String> imageUrls
) implements java.io.Serializable {}
