package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.ProductDetailResponse;
import com.ecommerce.platform.dto.ProductResponse;
import org.springframework.data.domain.Page;

public interface ProductService {
    Page<ProductResponse> getProducts(String categorySlug, String brand, int page, int size);
    ProductDetailResponse getProductDetail(String slug);
    Page<ProductResponse> searchProducts(String query, java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice, int page, int size);
}
