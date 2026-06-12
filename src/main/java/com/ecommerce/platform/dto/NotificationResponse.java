package com.ecommerce.platform.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
    UUID id,
    String title,
    String message,
    boolean isRead,
    Instant createdAt
) {}
