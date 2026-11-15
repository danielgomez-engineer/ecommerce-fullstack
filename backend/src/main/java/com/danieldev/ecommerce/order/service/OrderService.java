package com.danieldev.ecommerce.order.service;

import com.danieldev.ecommerce.order.dto.OrderRequest;
import com.danieldev.ecommerce.order.dto.OrderResponse;
import com.danieldev.ecommerce.security.CustomUserPrincipal;
import com.danieldev.ecommerce.shared.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse createOrder(Long userId, OrderRequest request);
    PageResponse<OrderResponse> getMyOrders(Long userId, Pageable pageable);
    OrderResponse getOrderById(Long orderId, CustomUserPrincipal principal);
}