package com.danieldev.ecommerce.product.controller;

import com.danieldev.ecommerce.product.dto.ProductRequest;
import com.danieldev.ecommerce.product.dto.ProductResponse;
import com.danieldev.ecommerce.product.service.ProductService;
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
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog and search endpoints")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Search and paginate products with optional filters")
    public ResponseEntity<GeneralResponse<PageResponse<ProductResponse>>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 12, sort = "id") Pageable pageable) {

        PageResponse<ProductResponse> data = productService.search(name, categoryId, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(GeneralResponse.success(data, "Products retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product details by ID")
    public ResponseEntity<GeneralResponse<ProductResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(GeneralResponse.success(productService.findById(id), "Product retrieved successfully"));
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<GeneralResponse<ProductResponse>> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GeneralResponse.success(created, "Product created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product")
    public ResponseEntity<GeneralResponse<ProductResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(GeneralResponse.success(productService.update(id, request), "Product updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}