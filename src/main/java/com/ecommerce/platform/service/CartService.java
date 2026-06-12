package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.CartItemDto;
import java.util.List;
import java.util.UUID;

public interface CartService {
    List<CartItemDto> getCart(String email);
    List<CartItemDto> addItemToCart(String email, UUID variantId, int quantity);
    List<CartItemDto> updateItemQuantity(String email, UUID variantId, int quantity);
    List<CartItemDto> removeItemFromCart(String email, UUID variantId);
    List<CartItemDto> mergeGuestCart(String email, List<CartItemDto> guestCart);
    void clearCart(String email);
}
