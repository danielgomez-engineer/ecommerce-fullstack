package com.danieldev.ecommerce.product.service;

import com.danieldev.ecommerce.product.dto.ProductRequest;
import com.danieldev.ecommerce.product.dto.ProductResponse;
import com.danieldev.ecommerce.product.entity.Category;
import com.danieldev.ecommerce.product.entity.Product;
import com.danieldev.ecommerce.product.mapper.ProductMapper;
import com.danieldev.ecommerce.product.mapper.ProductMapperImpl;
import com.danieldev.ecommerce.product.repository.CategoryRepository;
import com.danieldev.ecommerce.product.repository.ProductRepository;
import com.danieldev.ecommerce.product.service.impl.ProductServiceImpl;
import com.danieldev.ecommerce.shared.dto.PageResponse;
import com.danieldev.ecommerce.shared.exception.BusinessException;
import com.danieldev.ecommerce.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final ProductMapper productMapper = new ProductMapperImpl();
    private final ProductServiceImpl productService = new ProductServiceImpl(productRepository, categoryRepository, productMapper);

    @Test
    void search_whenMinPriceGreaterThanMaxPrice_throwsBusinessException() {
        Pageable pageable = PageRequest.of(0, 10);
        BigDecimal min = new BigDecimal("100.00");
        BigDecimal max = new BigDecimal("50.00");

        assertThatThrownBy(() -> productService.search(null, null, min, max, pageable))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("minPrice");
    }

    @Test
    @SuppressWarnings("unchecked")
    void search_withValidFilters_returnsPagedProducts() {
        Category cat = new Category("Electronics");
        cat.setId(1L);
        Product product = new Product("Headphones", "Wireless", new BigDecimal("99.99"), 10, null, cat);
        product.setId(1L);

        when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(product)));

        PageResponse<ProductResponse> result = productService.search("head", 1L, BigDecimal.TEN, new BigDecimal("200"), PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Headphones");
        assertThat(result.getContent().get(0).getCategoryName()).isEqualTo("Electronics");
    }

    @Test
    void findById_whenNotFound_throwsResourceNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_withValidCategory_savesAndReturnsProduct() {
        Category cat = new Category("Books");
        cat.setId(2L);
        ProductRequest request = ProductRequest.builder()
                .name("Clean Code")
                .description("Book")
                .price(new BigDecimal("40.00"))
                .stock(15)
                .categoryId(2L)
                .build();

        when(categoryRepository.findById(2L)).thenReturn(Optional.of(cat));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
            Product saved = inv.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        ProductResponse response = productService.create(request);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getCategoryName()).isEqualTo("Books");
    }

    @Test
    void delete_whenProductDoesNotExist_throwsResourceNotFoundException() {
        when(productRepository.existsById(5L)).thenReturn(false);

        assertThatThrownBy(() -> productService.delete(5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}