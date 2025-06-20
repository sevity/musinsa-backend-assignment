// ────────────────────────────────────────────────────────────────
// File : src/test/java/com/musinsa/repository/ProductRepositoryTest.java
// ────────────────────────────────────────────────────────────────
package com.musinsa.repository;

import com.musinsa.domain.Brand;
import com.musinsa.domain.Category;
import com.musinsa.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        // Hibernate 측 import.sql 로딩 완전히 비활성화
        "spring.jpa.properties.hibernate.hbm2ddl.import_files=none",

        // Spring SQL-init도 끔 (data.sql, schema.sql 등)
        "spring.sql.init.mode=never",

        // 테스트마다 스키마 생성/삭제
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ProductRepository productRepo;

    @DisplayName("카테고리별 상품 조회 & existsByBrandAndCategory 확인")
    @Test
    void findByCategoryAndExists() {
        // given: 깨끗한 H2 에만 데이터 삽입
        Brand foo = new Brand("FOO");
        em.persist(foo);

        Product top    = new Product(foo, Category.TOP,    1000);
        Product bottom = new Product(foo, Category.BOTTOM, 2000);
        em.persist(top);
        em.persist(bottom);
        em.flush();

        // when
        List<Product> tops      = productRepo.findByCategory(Category.TOP);
        boolean existsTop       = productRepo.existsByBrandAndCategory(foo, Category.TOP);
        boolean notExistsHat    = productRepo.existsByBrandAndCategory(foo, Category.HAT);

        // then
        assertThat(tops)
                .hasSize(1)
                .first()
                .extracting(Product::getPrice)
                .isEqualTo(1000);

        assertThat(existsTop).isTrue();
        assertThat(notExistsHat).isFalse();
    }
}
