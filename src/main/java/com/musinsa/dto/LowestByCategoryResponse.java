package com.musinsa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 카테고리별 최저가 브랜드 목록 응답
 */
@Schema(description = "카테고리별 최저가 브랜드 목록 응답")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor
public class LowestByCategoryResponse {

    @Schema(description = "카테고리-브랜드-가격 목록")
    private final List<CategoryBrandPrice> items;

    @Schema(description = "총액", example = "34100")
    private final int total;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
    @AllArgsConstructor
    public static class CategoryBrandPrice {
        @Schema(description = "카테고리명", example = "상의")
        private final String category;

        @Schema(description = "브랜드명", example = "C")
        private final String brand;

        @Schema(description = "가격", example = "10000")
        private final int price;
    }
}
