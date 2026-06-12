package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.UserResponse;
import com.ecommerce.platform.model.User;
import com.ecommerce.platform.model.AuditLog;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse getProfile(String email);
    UserResponse updateProfile(String email, String firstName, String lastName, String mobile, String profilePictureUrl);
    void changePassword(String email, String oldPassword, String newPassword);
    void deactivateAccount(String email);
    void deleteAccount(String email); // GDPR
    List<AuditLog> getActivityLog(String email, int page, int size);
    UserResponse registerAsSeller(String email);
}
