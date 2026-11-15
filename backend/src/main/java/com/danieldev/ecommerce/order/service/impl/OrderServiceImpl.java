package com.danieldev.ecommerce.order.service.impl;

import com.danieldev.ecommerce.order.dto.OrderItemRequest;
import com.danieldev.ecommerce.order.dto.OrderRequest;
import com.danieldev.ecommerce.order.dto.OrderResponse;
import com.danieldev.ecommerce.order.entity.Order;
import com.danieldev.ecommerce.order.entity.OrderItem;
import com.danieldev.ecommerce.order.entity.OrderStatus;
import com.danieldev.ecommerce.order.mapper.OrderMapper;
import com.danieldev.ecommerce.order.repository.OrderRepository;
import com.danieldev.ecommerce.order.service.OrderService;
import com.danieldev.ecommerce.product.entity.Product;
import com.danieldev.ecommerce.product.repository.ProductRepository;
import com.danieldev.ecommerce.security.CustomUserPrincipal;
import com.danieldev.ecommerce.shared.dto.PageResponse;
import com.danieldev.ecommerce.shared.exception.BusinessException;
import com.danieldev.ecommerce.shared.exception.ResourceNotFoundException;
import com.danieldev.ecommerce.user.entity.Customer;
import com.danieldev.ecommerce.user.entity.User;
import com.danieldev.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!(user instanceof Customer customer)) {
            throw new BusinessException("Only customers can place orders", HttpStatus.FORBIDDEN);
        }

        // Agrupar cantidades si el mismo productId viene repetido en el request
        Map<Long, Integer> aggregatedItems = new LinkedHashMap<>();
        for (OrderItemRequest item : request.getItems()) {
            aggregatedItems.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.CREATED);

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : aggregatedItems.entrySet()) {
            Long productId = entry.getKey();
            int quantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

            if (product.getStock() < quantity) {
                throw new BusinessException(
                        "Insufficient stock for product: " + product.getName() + " (available: " + product.getStock() + ")",
                        HttpStatus.CONFLICT
                );
            }

            product.setStock(product.getStock() - quantity);

            BigDecimal unitPrice = product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setUnitPrice(unitPrice);
            orderItem.setSubtotal(subtotal);

            order.addItem(orderItem);
            total = total.add(subtotal);
        }

        order.setTotal(total);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public PageResponse<OrderResponse> getMyOrders(Long userId, Pageable pageable) {
        Page<OrderResponse> page = orderRepository.findByCustomerIdOrderByCreatedAtDesc(userId, pageable)
                .map(orderMapper::toResponse);
        return PageResponse.from(page);
    }

    @Override
    public OrderResponse getOrderById(Long orderId, CustomUserPrincipal principal) {
        Order order = orderRepository.findWithDetailsById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        boolean isAdmin = "ADMIN".equals(principal.role());
        boolean isOwner = order.getCustomer().getId().equals(principal.userId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You do not have permission to view this order");
        }

        return orderMapper.toResponse(order);
    }
}