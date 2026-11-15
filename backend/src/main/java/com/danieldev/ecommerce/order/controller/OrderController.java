package com.danieldev.ecommerce.order.controller;

import com.danieldev.ecommerce.order.dto.OrderRequest;
import com.danieldev.ecommerce.order.dto.OrderResponse;
import com.danieldev.ecommerce.order.service.OrderService;
import com.danieldev.ecommerce.security.CustomUserPrincipal;
import com.danieldev.ecommerce.shared.dto.GeneralResponse;
import com.danieldev.ecommerce.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order creation and history endpoints")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create a new order for the authenticated customer")
    public ResponseEntity<GeneralResponse<OrderResponse>> create(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody OrderRequest request) {

        OrderResponse created = orderService.createOrder(principal.userId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GeneralResponse.success(created, "Order created successfully"));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get authenticated customer's order history")
    public ResponseEntity<GeneralResponse<PageResponse<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PageableDefault(size = 10) Pageable pageable) {

        PageResponse<OrderResponse> history = orderService.getMyOrders(principal.userId(), pageable);
        return ResponseEntity.ok(GeneralResponse.success(history, "Order history retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details by ID (owner or admin)")
    public ResponseEntity<GeneralResponse<OrderResponse>> getById(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long id) {

        return ResponseEntity.ok(GeneralResponse.success(orderService.getOrderById(id, principal), "Order retrieved successfully"));
    }
}