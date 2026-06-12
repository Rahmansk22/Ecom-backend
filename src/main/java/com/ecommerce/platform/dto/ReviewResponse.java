package com.ecommerce.platform.dto;

import java.time.Instant;
import java.util.UUID;

public record ReviewResponse(
    UUID id,
    String reviewerName,
    Integer rating,
    String title,
    String comment,
    Instant createdAt
) {}
