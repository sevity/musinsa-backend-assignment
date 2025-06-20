// ────────────────────────────────────────────────────────────────
// File : src/test/java/com/musinsa/repository/BrandRepositoryTest.java
// ────────────────────────────────────────────────────────────────
package com.musinsa.repository;

import com.musinsa.domain.Brand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // H2, 매핑 그대로 사용
class BrandRepositoryTest {

    @Autowired
    BrandRepository brandRepo;

    @DisplayName("브랜드 저장 & 단건 조회")
    @Test
    void saveAndFind() {
        Brand nike = new Brand("NIKE");
        brandRepo.save(nike);

        Optional<Brand> maybe = brandRepo.findByName("NIKE");

        assertThat(maybe).isPresent()
                .get()
                .extracting(Brand::getName)
                .isEqualTo("NIKE");
    }

    @DisplayName("이름 중복 저장 시 제약조건 위배 예외 발생")
    @Test
    void uniqueConstraint() {
        Brand a = new Brand("ABC"), b = new Brand("ABC");
        brandRepo.save(a);

        assertThatThrownBy(() -> brandRepo.saveAndFlush(b))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
