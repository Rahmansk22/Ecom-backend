package com.ecommerce.platform.service;

import java.util.Map;
import java.util.UUID;

public interface PaymentService {
    Map<String, String> createStripePaymentIntent(UUID orderId, String email);
    Map<String, Object> createRazorpayOrder(UUID orderId, String email);
    void verifyStripePayment(UUID orderId, String paymentIntentId, String email);
    void verifyRazorpayPayment(UUID orderId, String paymentId, String razorpayOrderId, String signature, String email);
}
