package com.musinsa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Map;

/**
 * 브랜드 등록/수정 요청 DTO
 */
@Schema(description = "브랜드 등록/수정 요청")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)   // Swagger 모델 생성용
@AllArgsConstructor(access = AccessLevel.PRIVATE)    // 빌더용 생성자
@Builder
public class BrandRequest {

        @Schema(description = "브랜드명", example = "A")
        private String brand;

        @Schema(
                description = "카테고리별 가격 맵",
                example      = "{\"상의\":10000,\"아우터\":5000,\"바지\":3000}"
        )
        private Map<String, Integer> prices;
}
