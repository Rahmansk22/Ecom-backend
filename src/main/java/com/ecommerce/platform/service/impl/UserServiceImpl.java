package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.UserResponse;
import com.ecommerce.platform.exception.BadRequestException;
import com.ecommerce.platform.model.AuditLog;
import com.ecommerce.platform.model.Role;
import com.ecommerce.platform.model.User;
import com.ecommerce.platform.repository.AuditLogRepository;
import com.ecommerce.platform.repository.UserRepository;
import com.ecommerce.platform.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogRepository auditLogRepository;
    private final StringRedisTemplate redisTemplate;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           AuditLogRepository auditLogRepository, StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogRepository = auditLogRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public UserResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(String email, String firstName, String lastName, String mobile, String profilePictureUrl) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (mobile != null && !mobile.equals(user.getMobile()) && userRepository.existsByMobile(mobile)) {
            throw new BadRequestException("Mobile number is already in use");
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMobile(mobile);
        if (profilePictureUrl != null) {
            user.setProfilePictureUrl(profilePictureUrl);
        }

        User updatedUser = userRepository.save(user);

        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .action("PROFILE_UPDATE")
                .details("Updated profile details: Name and/or Mobile")
                .build());

        return mapToResponse(updatedUser);
    }

    @Override
    @Transactional
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Incorrect old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Revoke user sessions
        redisTemplate.delete("refresh:" + email);

        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .action("PASSWORD_CHANGE")
                .details("Successfully updated account password. Sessions terminated.")
                .build());
    }

    @Override
    @Transactional
    public void deactivateAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        user.setActive(false);
        userRepository.save(user);

        // Clear Redis cache
        redisTemplate.delete("refresh:" + email);

        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .action("ACCOUNT_DEACTIVATION")
                .details("Deactivated account status")
                .build());
    }

    @Override
    @Transactional
    public void deleteAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // GDPR Compliance - Scrub sensitive user details rather than hard delete if keeping transaction integrity
        user.setEmail("scrubbed_" + user.getId() + "@ecommerce.com");
        user.setPassword("scrubbed_" + user.getId());
        user.setMobile(null);
        user.setFirstName("Deleted");
        user.setLastName("User");
        user.setProfilePictureUrl(null);
        user.setActive(false);
        userRepository.save(user);

        redisTemplate.delete("refresh:" + email);

        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .action("GDPR_ACCOUNT_DELETE")
                .details("Scrubbed personal identifier details for GDPR Compliance")
                .build());
    }

    @Override
    public List<AuditLog> getActivityLog(String email, int page, int size) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return auditLogRepository.findByUserOrderByCreatedAtDesc(user, PageRequest.of(page, size));
    }

    @Override
    @Transactional
    public UserResponse registerAsSeller(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        user.setRole(Role.SELLER);
        User updatedUser = userRepository.save(user);

        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .action("SELLER_REGISTRATION")
                .details("Upgraded user role to SELLER")
                .build());

        return mapToResponse(updatedUser);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
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
    }
}
