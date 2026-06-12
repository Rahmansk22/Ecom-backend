package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.ProductResponse;
import java.util.List;
import java.util.UUID;

public interface WishlistService {
    List<ProductResponse> getWishlist(String email);
    List<ProductResponse> addProductToWishlist(String email, UUID productId);
    List<ProductResponse> removeProductFromWishlist(String email, UUID productId);
    boolean isProductInWishlist(String email, UUID productId);
}
