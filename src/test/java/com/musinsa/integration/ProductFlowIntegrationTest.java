// ────────────────────────────────────────────────────────────────
// File : src/test/java/com/musinsa/integration/ProductFlowIntegrationTest.java
// ────────────────────────────────────────────────────────────────
package com.musinsa.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.domain.Brand;
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

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductFlowIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @Autowired
    BrandRepository brandRepo;

    @Autowired
    ProductRepository productRepo;

    @Autowired
    ObjectMapper om;

    Long productId;   // POST 결과로 받은 ID 저장

    String url(String p) { return "http://localhost:" + port + p; }

    @BeforeEach
    void setup() {
        // 테스트용 브랜드 삽입
        brandRepo.save(new Brand("FOO"));
    }

    @DisplayName("상품 CRUD – 생성 → 가격 변경 → 삭제까지 전체 플로우")
    @Test
    void productCrudFlow() {
        /* ---------- 1) 생성 ---------- */
        Map<String, Object> createBody = Map.of(
                "brand", "FOO",
                "category", "상의",
                "price", 3500
        );

        ResponseEntity<Map> createRes =
                rest.postForEntity(url("/api/v1/products"), createBody, Map.class);

        assertThat(createRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        productId = ((Number) createRes.getBody().get("id")).longValue();

        /* ---------- 2) 가격 업데이트 ---------- */
        Map<String, Object> updateBody = Map.of("price", 7777);
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> req = new HttpEntity<>(updateBody, h);

        ResponseEntity<Void> upRes = rest.exchange(
                url("/api/v1/products/" + productId), HttpMethod.PUT, req, Void.class);

        assertThat(upRes.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        /* ---------- 3) 삭제 ---------- */
        rest.delete(url("/api/v1/products/" + productId));

        /* ---------- 4) DB에 실제로 삭제됐는지 확인 ---------- */
        boolean exists = productRepo.existsById(productId);
        assertThat(exists).isFalse();
    }
}
