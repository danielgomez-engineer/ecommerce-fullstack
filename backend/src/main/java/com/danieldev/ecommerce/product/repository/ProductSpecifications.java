package com.danieldev.ecommerce.product.repository;

import com.danieldev.ecommerce.product.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> withFilters(String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Specification<Product>> specs = new ArrayList<>();

        if (name != null && !name.isBlank()) {
            String pattern = "%" + name.trim().toLowerCase() + "%";
            specs.add((root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern));
        }

        if (categoryId != null) {
            specs.add((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
        }

        if (minPrice != null) {
            specs.add((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        if (maxPrice != null) {
            specs.add((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        return Specification.allOf(specs);
    }
}