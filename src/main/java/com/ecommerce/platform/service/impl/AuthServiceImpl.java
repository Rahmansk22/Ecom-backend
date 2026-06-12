package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.AuthResponse;
import com.ecommerce.platform.dto.LoginRequest;
import com.ecommerce.platform.dto.RegisterRequest;
import com.ecommerce.platform.dto.UserResponse;
import com.ecommerce.platform.exception.BadRequestException;
import com.ecommerce.platform.exception.UnauthorizedException;
import com.ecommerce.platform.model.AuditLog;
import com.ecommerce.platform.model.User;
import com.ecommerce.platform.repository.UserRepository;
import com.ecommerce.platform.security.JwtService;
import com.ecommerce.platform.service.AuthService;
import com.ecommerce.platform.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuditLogRepository auditLogRepository;
    private final StringRedisTemplate redisTemplate;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
                           AuthenticationManager authenticationManager, AuditLogRepository auditLogRepository,
                           StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.auditLogRepository = auditLogRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request, String ipAddress) {
        log.info("Registering new user with email: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already exists");
        }
        if (request.mobile() != null && userRepository.existsByMobile(request.mobile())) {
            throw new BadRequestException("Mobile number already registered");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .mobile(request.mobile())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .role(request.role())
                .emailVerified(false)
                .phoneVerified(false)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        // Save refresh token to Redis (mapped to user email) to support token rotation/revocation
        redisTemplate.opsForValue().set(
                "refresh:" + savedUser.getEmail(),
                refreshToken,
                30, TimeUnit.DAYS
        );

        // Audit Logging
        auditLogRepository.save(AuditLog.builder()
                .user(savedUser)
                .action("USER_REGISTRATION")
                .ipAddress(ipAddress)
                .details("Registered user account with role: " + savedUser.getRole())
                .build());

        return buildAuthResponse(savedUser, accessToken, refreshToken);
    }

    @Override
    public AuthResponse login(LoginRequest request, String ipAddress) {
        log.info("Logging in user: {}", request.email());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (Exception ex) {
            // Log failed login audit log
            User potentialUser = userRepository.findByEmail(request.email()).orElse(null);
            auditLogRepository.save(AuditLog.builder()
                    .user(potentialUser)
                    .action("FAILED_LOGIN_ATTEMPT")
                    .ipAddress(ipAddress)
                    .details("Failed password authentication attempt for: " + request.email())
                    .build());
            throw new BadRequestException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new BadRequestException("User account is deactivated");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Cache Refresh Token
        redisTemplate.opsForValue().set(
                "refresh:" + user.getEmail(),
                refreshToken,
                30, TimeUnit.DAYS
        );

        // Audit Logging
        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .action("USER_LOGIN")
                .ipAddress(ipAddress)
                .details("Logged in from IP")
                .build());

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    @Override
    public AuthResponse refreshToken(String refreshTokenHeader, String ipAddress) {
        if (refreshTokenHeader == null || !refreshTokenHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Invalid refresh token header");
        }

        String token = refreshTokenHeader.substring(7);
        String username = jwtService.extractUsername(token);

        if (username != null) {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Check if token matches cached token in Redis
            String cachedToken = redisTemplate.opsForValue().get("refresh:" + username);
            if (cachedToken == null || !cachedToken.equals(token)) {
                // Token Reuse Detection / Theft: Revoke all refresh tokens
                redisTemplate.delete("refresh:" + username);
                auditLogRepository.save(AuditLog.builder()
                        .user(user)
                        .action("REFRESH_TOKEN_THEFT_DETECTED")
                        .ipAddress(ipAddress)
                        .details("Reused or invalid refresh token submitted. Cleared all sessions.")
                        .build());
                throw new UnauthorizedException("Session expired, please login again");
            }

            if (jwtService.isTokenValid(token, user)) {
                String newAccessToken = jwtService.generateToken(user);
                String newRefreshToken = jwtService.generateRefreshToken(user);

                // Rotate refresh token
                redisTemplate.opsForValue().set(
                        "refresh:" + username,
                        newRefreshToken,
                        30, TimeUnit.DAYS
                );

                return buildAuthResponse(user, newAccessToken, newRefreshToken);
            }
        }
        throw new BadRequestException("Invalid token signature or expiration");
    }

    @Override
    public void logout(String accessTokenHeader, String ipAddress) {
        if (accessTokenHeader != null && accessTokenHeader.startsWith("Bearer ")) {
            String token = accessTokenHeader.substring(7);
            String username = jwtService.extractUsername(token);

            if (username != null) {
                // Delete user refresh token from Redis
                redisTemplate.delete("refresh:" + username);

                // Blacklist access token in Redis (TTL: 15 minutes)
                redisTemplate.opsForValue().set(
                        "blacklist:" + token,
                        "true",
                        15, TimeUnit.MINUTES
                );

                User user = userRepository.findByEmail(username).orElse(null);
                auditLogRepository.save(AuditLog.builder()
                        .user(user)
                        .action("USER_LOGOUT")
                        .ipAddress(ipAddress)
                        .details("Logged out single device session")
                        .build());
            }
        }
    }

    @Override
    public void logoutAll(String accessTokenHeader, String ipAddress) {
        if (accessTokenHeader != null && accessTokenHeader.startsWith("Bearer ")) {
            String token = accessTokenHeader.substring(7);
            String username = jwtService.extractUsername(token);

            if (username != null) {
                // Clear refresh token to revoke all devices
                redisTemplate.delete("refresh:" + username);

                User user = userRepository.findByEmail(username).orElse(null);
                auditLogRepository.save(AuditLog.builder()
                        .user(user)
                        .action("USER_LOGOUT_ALL_DEVICES")
                        .ipAddress(ipAddress)
                        .details("Logged out all device sessions")
                        .build());
            }
        }
    }

    private AuthResponse buildAuthResponse(User user, String access, String refresh) {
        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getMobile(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfilePictureUrl(),
                user.getRole(),
                user.isEmailVerified(),
                user.isPhoneVerified(),
                user.isActive()
        );
        return new AuthResponse(access, refresh, userResponse);
    }
}
