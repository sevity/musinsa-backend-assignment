// ────────────────────────────────────────────────────────────────
// File : src/test/java/com/musinsa/integration/PriceFlowIntegrationTest.java
// ────────────────────────────────────────────────────────────────
package com.musinsa.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.common.ErrorCode;
import com.musinsa.domain.Category;
import com.musinsa.domain.Brand;
import com.musinsa.domain.Product;
import com.musinsa.dto.LowestByCategoryResponse;
import com.musinsa.repository.BrandRepository;
import com.musinsa.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PriceFlowIntegrationTest {

    @LocalServerPort int port;
    @Autowired TestRestTemplate rest;
    @Autowired BrandRepository brandRepo;
    @Autowired ProductRepository productRepo;
    @Autowired ObjectMapper om;

    @BeforeEach
    void initData() {
        // 1) DB 완전 초기화
        productRepo.deleteAll();
        brandRepo.deleteAll();

        // 2) 테스트 데이터 삽입 (Brand X 하나, 모든 카테고리)
        Brand x = brandRepo.save(new Brand("X"));
        Map<Category,Integer> priceX = Map.of(
                Category.TOP,       1000,
                Category.OUTER,     5000,
                Category.BOTTOM,    1500,
                Category.SNEAKERS,  9000,
                Category.BAG,       2000,
                Category.HAT,       1500,
                Category.SOCKS,     1700,
                Category.ACCESSORY, 1900
        );
        priceX.forEach((c,p) -> productRepo.save(new Product(x, c, p)));
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @DisplayName("카테고리별 최저가 – 전체 개수와 주요 3개 항목 검증")
    @Test
    void cheapestBrandsPerCategory_flow() throws Exception {
        ResponseEntity<String> res = rest.getForEntity(
                url("/api/v1/categories/cheapest-brands"), String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);

        LowestByCategoryResponse body = om.readValue(
                res.getBody(), new TypeReference<>() {});

        // 전체 개수
        assertThat(body.getItems()).hasSize(Category.values().length);

        // 핵심 3개 카테고리만 부분 검증
        Map<String,Integer> expected = new HashMap<>();
        expected.put(Category.TOP.getKrName(),    1000);
        expected.put(Category.BOTTOM.getKrName(), 1500);
        expected.put(Category.HAT.getKrName(),    1500);

        assertThat(body.getItems())
                .extracting("category", "price")
                .contains(
                        tuple(Category.TOP.getKrName(),    expected.get(Category.TOP.getKrName())),
                        tuple(Category.BOTTOM.getKrName(), expected.get(Category.BOTTOM.getKrName())),
                        tuple(Category.HAT.getKrName(),    expected.get(Category.HAT.getKrName()))
                );

        // total 합계 검증
        int sum = body.getItems().stream()
                .mapToInt(item -> item.getPrice())
                .sum();
        assertThat(body.getTotal()).isEqualTo(sum);
    }

    @DisplayName("유효하지 않은 카테고리명 요청 시 400 BAD_REQUEST")
    @Test
    void priceStats_invalidCategory_returnsBadRequest() {
        ResponseEntity<Map> res = rest.getForEntity(
                url("/api/v1/categories/존재안함/price-stats"), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).containsEntry("code", ErrorCode.VALIDATION_ERROR.getCode());
    }

    @DisplayName("유효하지만 데이터 없는 카테고리 요청 시 404 NOT_FOUND")
    @Test
    void priceStats_validCategoryNoData_returnsNotFound() {
        // 1) 특정 카테고리 상품만 삭제
        productRepo.deleteAll();  // 모든 상품 제거

        ResponseEntity<Map> res = rest.getForEntity(
                url("/api/v1/categories/" + Category.TOP.getKrName() + "/price-stats"),
                Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).containsEntry("code", ErrorCode.CATEGORY_NOT_FOUND.getCode());
    }
}
