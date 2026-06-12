package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.ProductResponse;
import com.ecommerce.platform.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getWishlist(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(wishlistService.getWishlist(userDetails.getUsername()));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<List<ProductResponse>> addProductToWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID productId
    ) {
        return ResponseEntity.ok(wishlistService.addProductToWishlist(userDetails.getUsername(), productId));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<List<ProductResponse>> removeProductFromWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID productId
    ) {
        return ResponseEntity.ok(wishlistService.removeProductFromWishlist(userDetails.getUsername(), productId));
    }

    @GetMapping("/{productId}/check")
    public ResponseEntity<Map<String, Boolean>> checkProductInWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID productId
    ) {
        boolean inWishlist = wishlistService.isProductInWishlist(userDetails.getUsername(), productId);
        return ResponseEntity.ok(Map.of("inWishlist", inWishlist));
    }
}
