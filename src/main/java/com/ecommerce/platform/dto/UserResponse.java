package com.ecommerce.platform.dto;

import com.ecommerce.platform.model.Role;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String email,
    String mobile,
    String firstName,
    String lastName,
    String profilePictureUrl,
    Role role,
    boolean emailVerified,
    boolean phoneVerified,
    boolean active
) {}
