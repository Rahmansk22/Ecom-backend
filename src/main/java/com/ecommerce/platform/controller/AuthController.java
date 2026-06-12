package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.AuthResponse;
import com.ecommerce.platform.dto.LoginRequest;
import com.ecommerce.platform.dto.RegisterRequest;
import com.ecommerce.platform.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpServletRequest
    ) {
        String ipAddress = httpServletRequest.getRemoteAddr();
        return ResponseEntity.ok(authService.register(request, ipAddress));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpServletRequest
    ) {
        String ipAddress = httpServletRequest.getRemoteAddr();
        return ResponseEntity.ok(authService.login(request, ipAddress));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestHeader("Authorization") String refreshToken,
            HttpServletRequest httpServletRequest
    ) {
        String ipAddress = httpServletRequest.getRemoteAddr();
        return ResponseEntity.ok(authService.refreshToken(refreshToken, ipAddress));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String accessToken,
            HttpServletRequest httpServletRequest
    ) {
        String ipAddress = httpServletRequest.getRemoteAddr();
        authService.logout(accessToken, ipAddress);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(
            @RequestHeader("Authorization") String accessToken,
            HttpServletRequest httpServletRequest
    ) {
        String ipAddress = httpServletRequest.getRemoteAddr();
        authService.logoutAll(accessToken, ipAddress);
        return ResponseEntity.ok().build();
    }
}
