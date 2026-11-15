package com.danieldev.ecommerce.product.controller;

import com.danieldev.ecommerce.product.dto.CategoryResponse;
import com.danieldev.ecommerce.product.service.ProductService;
import com.danieldev.ecommerce.shared.dto.GeneralResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Product categories")
public class CategoryController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "List all categories")
    public ResponseEntity<GeneralResponse<List<CategoryResponse>>> getAll() {
        return ResponseEntity.ok(GeneralResponse.success(productService.getAllCategories(), "Categories retrieved successfully"));
    }
}