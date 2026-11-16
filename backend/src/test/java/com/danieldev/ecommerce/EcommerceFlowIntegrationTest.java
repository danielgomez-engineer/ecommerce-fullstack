package com.danieldev.ecommerce;

import com.danieldev.ecommerce.auth.dto.LoginRequest;
import com.danieldev.ecommerce.auth.dto.RegisterRequest;
import com.danieldev.ecommerce.order.dto.OrderItemRequest;
import com.danieldev.ecommerce.order.dto.OrderRequest;
import com.danieldev.ecommerce.product.dto.ProductRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EcommerceFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullCustomerPurchaseFlow_succeedsEndToEnd() throws Exception {
        // 1. Register new customer
        RegisterRequest registerReq = RegisterRequest.builder()
                .fullName("Integration Tester")
                .email("e2e.customer@demo.com")
                .password("password123")
                .phone("3009998877")
                .address("Calle 10 # 20-30")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.token").isNotEmpty());

        // 2. Login and extract JWT
        LoginRequest loginReq = new LoginRequest("e2e.customer@demo.com", "password123");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String customerToken = loginJson.path("data").path("token").asString();

        // 3. Search products with filters (seeded data)
        MvcResult productsResult = mockMvc.perform(get("/api/v1/products")
                        .param("name", "Keyboard")
                        .param("minPrice", "50")
                        .param("maxPrice", "150"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isNotEmpty())
                .andReturn();

        JsonNode productsJson = objectMapper.readTree(productsResult.getResponse().getContentAsString());
        long productId = productsJson.path("data").path("content").get(0).path("id").asLong();

        // 4. Create an order
        OrderRequest orderReq = new OrderRequest(List.of(new OrderItemRequest(productId, 2)));
        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("CREATED"))
                .andExpect(jsonPath("$.data.items[0].quantity").value(2));

        // 5. Retrieve customer order history
        mockMvc.perform(get("/api/v1/orders/me")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isNotEmpty());

        // 6. Exceeding stock returns 409 Conflict
        OrderRequest excessiveOrder = new OrderRequest(List.of(new OrderItemRequest(productId, 9999)));
        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(excessiveOrder)))
                .andExpect(status().isConflict());
    }

    @Test
    void adminProductCreationFlow_succeedsWithAdminRole() throws Exception {
        // Login with seeded admin (password: "password")
        LoginRequest adminLogin = new LoginRequest("admin@demo.com", "password");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();

        String adminToken = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .path("data").path("token").asString();

        ProductRequest newProduct = ProductRequest.builder()
                .name("Ultrawide Monitor 34\"")
                .description("Curved gaming and productivity display")
                .price(new BigDecimal("499.99"))
                .stock(10)
                .categoryId(1L)
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Ultrawide Monitor 34\""));
    }
}