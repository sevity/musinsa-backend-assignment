// repository/ProductRepository.java
package com.musinsa.repository;

import com.musinsa.domain.Brand;
import com.musinsa.domain.Category;
import com.musinsa.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
    List<Product> findByBrandId(Long brandId);
    boolean existsByBrandIdAndCategoryAndIdNot(Long brandId, Category category, Long id);
    boolean existsByBrandAndCategory(Brand brand, Category category);
}
