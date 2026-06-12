package com.ecommerce.platform.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    UserResponse user
) {}
