package com.danieldev.ecommerce.order.service;

import com.danieldev.ecommerce.order.dto.OrderItemRequest;
import com.danieldev.ecommerce.order.dto.OrderRequest;
import com.danieldev.ecommerce.order.dto.OrderResponse;
import com.danieldev.ecommerce.order.entity.Order;
import com.danieldev.ecommerce.order.mapper.OrderMapper;
import com.danieldev.ecommerce.order.mapper.OrderMapperImpl;
import com.danieldev.ecommerce.order.repository.OrderRepository;
import com.danieldev.ecommerce.order.service.impl.OrderServiceImpl;
import com.danieldev.ecommerce.product.entity.Category;
import com.danieldev.ecommerce.product.entity.Product;
import com.danieldev.ecommerce.product.repository.ProductRepository;
import com.danieldev.ecommerce.security.CustomUserPrincipal;
import com.danieldev.ecommerce.shared.exception.BusinessException;
import com.danieldev.ecommerce.user.entity.Customer;
import com.danieldev.ecommerce.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final OrderMapper orderMapper = new OrderMapperImpl();
    private final OrderServiceImpl orderService = new OrderServiceImpl(orderRepository, productRepository, userRepository, orderMapper);

    @Test
    void createOrder_withValidItems_deductsStockCalculatesTotalAndSaves() {
        Customer customer = new Customer();
        customer.setId(1L);

        Product p1 = new Product("Keyboard", "RGB", new BigDecimal("50.00"), 10, null, new Category("Tech"));
        p1.setId(10L);
        Product p2 = new Product("Mouse", "Wireless", new BigDecimal("25.00"), 5, null, new Category("Tech"));
        p2.setId(20L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(p1));
        when(productRepository.findById(20L)).thenReturn(Optional.of(p2));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(100L);
            return o;
        });

        OrderRequest request = new OrderRequest(List.of(
                new OrderItemRequest(10L, 2),
                new OrderItemRequest(20L, 1)
        ));

        OrderResponse response = orderService.createOrder(1L, request);

        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getTotal()).isEqualByComparingTo("125.00");
        assertThat(response.getItems()).hasSize(2);
        assertThat(p1.getStock()).isEqualTo(8);
        assertThat(p2.getStock()).isEqualTo(4);
    }

    @Test
    void createOrder_whenStockIsInsufficient_throws409Conflict() {
        Customer customer = new Customer();
        customer.setId(1L);

        Product p1 = new Product("Monitor", "4K", new BigDecimal("300.00"), 1, null, new Category("Tech"));
        p1.setId(10L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(p1));

        OrderRequest request = new OrderRequest(List.of(new OrderItemRequest(10L, 3)));

        assertThatThrownBy(() -> orderService.createOrder(1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.CONFLICT);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrderById_whenAccessedByAnotherCustomer_throwsAccessDenied() {
        Customer owner = new Customer();
        owner.setId(1L);

        Order order = new Order();
        order.setId(50L);
        order.setCustomer(owner);

        when(orderRepository.findWithDetailsById(50L)).thenReturn(Optional.of(order));

        CustomUserPrincipal stranger = new CustomUserPrincipal(99L, "other@demo.com", "CUSTOMER");

        assertThatThrownBy(() -> orderService.getOrderById(50L, stranger))
                .isInstanceOf(AccessDeniedException.class);
    }
}