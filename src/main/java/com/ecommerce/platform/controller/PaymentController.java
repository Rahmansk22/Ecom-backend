package com.ecommerce.platform.controller;

import com.ecommerce.platform.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/stripe/create-intent/{orderId}")
    public ResponseEntity<Map<String, String>> createStripeIntent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(paymentService.createStripePaymentIntent(orderId, userDetails.getUsername()));
    }

    @PostMapping("/razorpay/create-order/{orderId}")
    public ResponseEntity<Map<String, Object>> createRazorpayOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(paymentService.createRazorpayOrder(orderId, userDetails.getUsername()));
    }

    @PostMapping("/stripe/verify/{orderId}")
    public ResponseEntity<Void> verifyStripe(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID orderId,
            @RequestBody Map<String, String> body
    ) {
        paymentService.verifyStripePayment(orderId, body.get("paymentIntentId"), userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/razorpay/verify/{orderId}")
    public ResponseEntity<Void> verifyRazorpay(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID orderId,
            @RequestBody Map<String, String> body
    ) {
        paymentService.verifyRazorpayPayment(
                orderId,
                body.get("paymentId"),
                body.get("razorpayOrderId"),
                body.get("signature"),
                userDetails.getUsername()
        );
        return ResponseEntity.ok().build();
    }
}
