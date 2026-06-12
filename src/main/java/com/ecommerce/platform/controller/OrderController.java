package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.CheckoutRequest;
import com.ecommerce.platform.dto.OrderResponse;
import com.ecommerce.platform.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CheckoutRequest request
    ) {
        return ResponseEntity.ok(orderService.checkout(userDetails.getUsername(), request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.getOrders(userDetails.getUsername()));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(orderService.getOrderDetails(userDetails.getUsername(), orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(orderService.cancelOrder(userDetails.getUsername(), orderId));
    }
}
