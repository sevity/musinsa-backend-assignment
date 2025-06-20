package com.musinsa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 개별 상품 등록 요청 DTO
 */
@Schema(description = "상품 등록 요청")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateProductRequest {

        @Schema(description = "브랜드명", example = "A")
        private String brand;

        @Schema(description = "카테고리명", example = "상의")
        private String category;

        @Schema(description = "가격", example = "11200")
        private int price;
}
