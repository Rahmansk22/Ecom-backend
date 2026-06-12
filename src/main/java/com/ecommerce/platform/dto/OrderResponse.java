package com.ecommerce.platform.dto;

import com.ecommerce.platform.model.OrderStatus;
import com.ecommerce.platform.model.PaymentMethod;
import com.ecommerce.platform.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
    UUID id,
    String shippingName,
    String shippingAddressLine1,
    String shippingAddressLine2,
    String shippingCity,
    String shippingState,
    String shippingCountry,
    String shippingPincode,
    String shippingPhone,
    BigDecimal totalPrice,
    BigDecimal discountAmount,
    BigDecimal shippingCharge,
    BigDecimal netAmount,
    OrderStatus status,
    PaymentStatus paymentStatus,
    PaymentMethod paymentMethod,
    String transactionId,
    Instant createdAt,
    List<OrderItemResponse> items
) {}
