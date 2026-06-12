package com.ecommerce.platform.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
    @NotBlank(message = "Label is required (e.g. Home, Office)")
    String label,

    @NotBlank(message = "Address line 1 is required")
    String addressLine1,

    String addressLine2,

    @NotBlank(message = "City is required")
    String city,

    @NotBlank(message = "State is required")
    String state,

    @NotBlank(message = "Country is required")
    String country,

    @NotBlank(message = "Pincode is required")
    String pincode,

    boolean isDefault
) {}
