package com.danieldev.ecommerce.product.controller;

import com.danieldev.ecommerce.product.dto.ProductRequest;
import com.danieldev.ecommerce.product.dto.ProductResponse;
import com.danieldev.ecommerce.product.service.ProductService;
import com.danieldev.ecommerce.shared.dto.PageResponse;
import com.danieldev.ecommerce.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void getAll_returns200AndPageResponse() throws Exception {
        ProductResponse item = ProductResponse.builder().id(1L).name("Keyboard").price(new BigDecimal("89.50")).build();
        PageResponse<ProductResponse> page = PageResponse.<ProductResponse>builder()
                .content(List.of(item)).page(0).size(12).totalElements(1).totalPages(1).last(true).build();

        when(productService.search(any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].name").value("Keyboard"));
    }

    @Test
    void getById_whenNotFound_returns404() throws Exception {
        when(productService.findById(99L)).thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(get("/api/v1/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void create_withInvalidBody_returns400ValidationErrors() throws Exception {
        ProductRequest invalidRequest = ProductRequest.builder().name("").price(new BigDecimal("-5")).build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.name").exists())
                .andExpect(jsonPath("$.data.price").exists());
    }
}