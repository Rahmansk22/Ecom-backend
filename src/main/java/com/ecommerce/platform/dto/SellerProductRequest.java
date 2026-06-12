package com.ecommerce.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record SellerProductRequest(
    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Brand is required")
    String brand,

    @NotBlank(message = "Description is required")
    String description,

    @NotNull(message = "Category ID is required")
    UUID categoryId,

    String status,

    String countryOfOrigin,
    String hsnCode,

    List<VariantRequest> variants
) {
    public record VariantRequest(
        UUID id, // Optional for updates
        String sku,
        BigDecimal price,
        BigDecimal compareAtPrice,
        Integer stock,
        String attributesJson,
        List<String> imageUrls
    ) {}
}
