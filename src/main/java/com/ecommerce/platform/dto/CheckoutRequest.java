package com.ecommerce.platform.dto;

import com.ecommerce.platform.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CheckoutRequest(
    @NotNull(message = "Address ID is required")
    UUID addressId,

    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod,

    String transactionId
) {}
