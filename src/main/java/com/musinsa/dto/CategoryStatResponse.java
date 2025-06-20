// dto/CategoryStatResponse.java
package com.musinsa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(description = "카테고리별 최저·최고가 응답")
@Getter @Builder @NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor
public class CategoryStatResponse {
    @Schema(description = "카테고리 이름", example = "상의")
    private String category;

    @Schema(description = "최저가 브랜드 목록")
    private List<BrandPrice> lowest;

    @Schema(description = "최고가 브랜드 목록")
    private List<BrandPrice> highest;

    @Getter @NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor
    public static class BrandPrice {
        @Schema(description = "브랜드명", example = "C")
        private String brand;
        @Schema(description = "가격", example = "10000")
        private int price;
    }
}
