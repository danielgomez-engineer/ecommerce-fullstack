package com.danieldev.ecommerce.product.service;

import com.danieldev.ecommerce.product.dto.CategoryResponse;
import com.danieldev.ecommerce.product.dto.ProductRequest;
import com.danieldev.ecommerce.product.dto.ProductResponse;
import com.danieldev.ecommerce.shared.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    PageResponse<ProductResponse> search(String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    ProductResponse findById(Long id);
    ProductResponse create(ProductRequest request);
    ProductResponse update(Long id, ProductRequest request);
    void delete(Long id);
    List<CategoryResponse> getAllCategories();
}