package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.ProductDetailResponse;
import com.ecommerce.platform.dto.ProductResponse;
import com.ecommerce.platform.dto.OrderResponse;
import com.ecommerce.platform.dto.SellerProductRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SellerService {
    List<ProductResponse> getSellerProducts(String email);
    ProductDetailResponse addSellerProduct(String email, SellerProductRequest request);
    ProductDetailResponse updateSellerProduct(String email, UUID productId, SellerProductRequest request);
    void deleteSellerProduct(String email, UUID productId);
    List<OrderResponse> getSellerOrders(String email);
    Map<String, Object> getSellerDashboardMetrics(String email);
}
