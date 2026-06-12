package com.ecommerce.platform.dto;

import com.ecommerce.platform.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    String password,

    String mobile,

    @NotBlank(message = "First name is required")
    String firstName,

    String lastName,

    @NotNull(message = "Role is required")
    Role role
) {}
