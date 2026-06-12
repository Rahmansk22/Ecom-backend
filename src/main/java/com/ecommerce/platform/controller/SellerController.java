package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.ProductDetailResponse;
import com.ecommerce.platform.dto.ProductResponse;
import com.ecommerce.platform.dto.OrderResponse;
import com.ecommerce.platform.dto.SellerProductRequest;
import com.ecommerce.platform.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/seller")
@PreAuthorize("hasAnyRole('SELLER', 'ADMIN', 'SUPER_ADMIN')")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getProducts(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(sellerService.getSellerProducts(userDetails.getUsername()));
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDetailResponse> addProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SellerProductRequest request
    ) {
        return ResponseEntity.ok(sellerService.addSellerProduct(userDetails.getUsername(), request));
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductDetailResponse> updateProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID productId,
            @Valid @RequestBody SellerProductRequest request
    ) {
        return ResponseEntity.ok(sellerService.updateSellerProduct(userDetails.getUsername(), productId, request));
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID productId
    ) {
        sellerService.deleteSellerProduct(userDetails.getUsername(), productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getOrders(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(sellerService.getSellerOrders(userDetails.getUsername()));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(sellerService.getSellerDashboardMetrics(userDetails.getUsername()));
    }
}
