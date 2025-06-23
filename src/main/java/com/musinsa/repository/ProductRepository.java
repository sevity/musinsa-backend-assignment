// repository/ProductRepository.java
package com.musinsa.repository;

import com.musinsa.domain.Brand;
import com.musinsa.domain.Category;
import com.musinsa.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
    List<Product> findByBrandId(Long brandId);
    boolean existsByBrandIdAndCategoryAndIdNot(Long brandId, Category category, Long id);
    boolean existsByBrandAndCategory(Brand brand, Category category);
    Optional<Product> findByBrandAndCategory(Brand brand, Category category);

    /* JPQL for price statistics */
    @Query("""
        SELECT p FROM Product p
        WHERE p.category = :category
          AND p.price = (SELECT MIN(p2.price) FROM Product p2 WHERE p2.category = :category)
    """)
    List<Product> findMinPriceByCategory(@Param("category") Category category);

    @Query("""
        SELECT p FROM Product p
        WHERE p.category = :category
          AND p.price = (SELECT MAX(p2.price) FROM Product p2 WHERE p2.category = :category)
    """)
    List<Product> findMaxPriceByCategory(@Param("category") Category category);

    @Query("""
        SELECT p FROM Product p
        WHERE p.price = (
            SELECT MIN(p2.price) FROM Product p2 WHERE p2.category = p.category
        )
    """)
    List<Product> findCheapestProductsAllCategories();

}
