package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.AddToCartRequest;
import com.ecommerce.platform.dto.CartItemDto;
import com.ecommerce.platform.dto.UpdateQuantityRequest;
import com.ecommerce.platform.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<List<CartItemDto>> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCart(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<List<CartItemDto>> addItemToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddToCartRequest request
    ) {
        return ResponseEntity.ok(cartService.addItemToCart(
                userDetails.getUsername(),
                request.variantId(),
                request.quantity()
        ));
    }

    @PutMapping("/{variantId}")
    public ResponseEntity<List<CartItemDto>> updateItemQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID variantId,
            @Valid @RequestBody UpdateQuantityRequest request
    ) {
        return ResponseEntity.ok(cartService.updateItemQuantity(
                userDetails.getUsername(),
                variantId,
                request.quantity()
        ));
    }

    @DeleteMapping("/{variantId}")
    public ResponseEntity<List<CartItemDto>> removeItemFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID variantId
    ) {
        return ResponseEntity.ok(cartService.removeItemFromCart(userDetails.getUsername(), variantId));
    }

    @PostMapping("/merge")
    public ResponseEntity<List<CartItemDto>> mergeGuestCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody List<CartItemDto> guestCart
    ) {
        return ResponseEntity.ok(cartService.mergeGuestCart(userDetails.getUsername(), guestCart));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
