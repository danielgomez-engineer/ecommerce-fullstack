package com.danieldev.ecommerce.order.controller;

import com.danieldev.ecommerce.order.dto.OrderItemRequest;
import com.danieldev.ecommerce.order.dto.OrderRequest;
import com.danieldev.ecommerce.order.dto.OrderResponse;
import com.danieldev.ecommerce.order.entity.OrderStatus;
import com.danieldev.ecommerce.order.service.OrderService;
import com.danieldev.ecommerce.security.*;
import com.danieldev.ecommerce.shared.dto.PageResponse;
import com.danieldev.ecommerce.user.entity.Admin;
import com.danieldev.ecommerce.user.entity.Customer;
import com.danieldev.ecommerce.user.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import({SecurityConfig.class, JwtService.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
@TestPropertySource(properties = {
        "jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
        "jwt.expiration=3600000",
        "cors.allowed-origins=http://localhost:5173"
})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void createOrder_asCustomer_returns201() throws Exception {
        Customer customer = new Customer();
        customer.setId(5L);
        customer.setEmail("jane@demo.com");
        customer.setRole(Role.CUSTOMER);
        String token = jwtService.generateToken(customer);

        OrderRequest req = new OrderRequest(List.of(new OrderItemRequest(1L, 2)));
        OrderResponse res = OrderResponse.builder().id(10L).status(OrderStatus.CREATED).total(new BigDecimal("100.00")).build();
        when(orderService.createOrder(eq(5L), any())).thenReturn(res);

        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(10));
    }

    @Test
    void createOrder_asAdmin_returns403() throws Exception {
        Admin admin = new Admin();
        admin.setId(1L);
        admin.setEmail("admin@demo.com");
        admin.setRole(Role.ADMIN);
        String token = jwtService.generateToken(admin);

        OrderRequest req = new OrderRequest(List.of(new OrderItemRequest(1L, 1)));

        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMyOrders_asCustomer_returns200() throws Exception {
        Customer customer = new Customer();
        customer.setId(5L);
        customer.setEmail("jane@demo.com");
        customer.setRole(Role.CUSTOMER);
        String token = jwtService.generateToken(customer);

        PageResponse<OrderResponse> page = PageResponse.<OrderResponse>builder()
                .content(List.of()).page(0).size(10).totalElements(0).totalPages(0).last(true).build();
        when(orderService.getMyOrders(eq(5L), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/orders/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}