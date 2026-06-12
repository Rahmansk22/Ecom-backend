package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.AuthResponse;
import com.ecommerce.platform.dto.LoginRequest;
import com.ecommerce.platform.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request, String ipAddress);
    AuthResponse login(LoginRequest request, String ipAddress);
    AuthResponse refreshToken(String refreshTokenHeader, String ipAddress);
    void logout(String accessTokenHeader, String ipAddress);
    void logoutAll(String accessTokenHeader, String ipAddress);
}
