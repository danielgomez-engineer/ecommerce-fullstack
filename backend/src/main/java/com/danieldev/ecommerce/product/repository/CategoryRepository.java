package com.danieldev.ecommerce.product.repository;

import com.danieldev.ecommerce.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}