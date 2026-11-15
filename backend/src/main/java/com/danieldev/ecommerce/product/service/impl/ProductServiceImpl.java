package com.danieldev.ecommerce.product.service.impl;

import com.danieldev.ecommerce.product.dto.CategoryResponse;
import com.danieldev.ecommerce.product.dto.ProductRequest;
import com.danieldev.ecommerce.product.dto.ProductResponse;
import com.danieldev.ecommerce.product.entity.Category;
import com.danieldev.ecommerce.product.entity.Product;
import com.danieldev.ecommerce.product.mapper.ProductMapper;
import com.danieldev.ecommerce.product.repository.CategoryRepository;
import com.danieldev.ecommerce.product.repository.ProductRepository;
import com.danieldev.ecommerce.product.repository.ProductSpecifications;
import com.danieldev.ecommerce.product.service.ProductService;
import com.danieldev.ecommerce.shared.dto.PageResponse;
import com.danieldev.ecommerce.shared.exception.BusinessException;
import com.danieldev.ecommerce.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public PageResponse<ProductResponse> search(String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new BusinessException("minPrice cannot be greater than maxPrice");
        }

        Specification<Product> spec = ProductSpecifications.withFilters(name, categoryId, minPrice, maxPrice);
        Page<ProductResponse> page = productRepository.findAll(spec, pageable)
                .map(productMapper::toResponse);

        return PageResponse.from(page);
    }

    @Override
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Product product = productMapper.toEntity(request);
        product.setCategory(category);

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        productMapper.updateEntity(request, product);
        product.setCategory(category);

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(productMapper::toCategoryResponse)
                .toList();
    }
}