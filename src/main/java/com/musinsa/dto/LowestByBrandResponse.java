package com.musinsa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 단일 브랜드 최저가 번들 응답
 */
@Schema(description = "단일 브랜드로 전체 카테고리 최저가 응답")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor
public class LowestByBrandResponse {

    @Schema(description = "브랜드명", example = "D")
    private final String brand;

    @Schema(description = "카테고리별 가격 목록")
    private final List<CategoryPrice> categories;

    @Schema(description = "총액", example = "36100")
    private final int total;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
    @AllArgsConstructor
    public static class CategoryPrice {
        @Schema(description = "카테고리명", example = "바지")
        private final String category;

        @Schema(description = "가격", example = "3000")
        private final int price;
    }
}
