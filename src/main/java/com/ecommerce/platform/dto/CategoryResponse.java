package com.ecommerce.platform.dto;

import java.util.List;
import java.util.UUID;

public record CategoryResponse(
    UUID id,
    String name,
    String slug,
    String bannerUrl,
    String iconUrl,
    List<CategoryResponse> children
) implements java.io.Serializable {}
