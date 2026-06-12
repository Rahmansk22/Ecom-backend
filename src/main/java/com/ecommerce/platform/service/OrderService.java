package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.CheckoutRequest;
import com.ecommerce.platform.dto.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse checkout(String email, CheckoutRequest request);
    List<OrderResponse> getOrders(String email);
    OrderResponse getOrderDetails(String email, UUID orderId);
    OrderResponse cancelOrder(String email, UUID orderId);
}
