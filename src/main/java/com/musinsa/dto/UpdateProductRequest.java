// src/main/java/com/musinsa/dto/UpdateProductRequest.java
package com.musinsa.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** 상품 수정 요청 DTO (모든 필드 변경 가능) */
public record UpdateProductRequest(
        @NotBlank(message = "브랜드명을 비워둘 수 없습니다")
        String brand,

        @NotBlank(message = "카테고리를 비워둘 수 없습니다")
        @Pattern(
                regexp = "상의|아우터|바지|스니커즈|가방|모자|양말|액세서리",
                message = "유효하지 않은 카테고리입니다"
        )
        String category,

        @Min(value = 0, message = "가격은 0 이상이어야 합니다")
        int price
) {}
