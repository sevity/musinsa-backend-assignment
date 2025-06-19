// src/main/java/com/musinsa/dto/BrandRequest.java
package com.musinsa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Map;

/** 브랜드 등록·수정 요청 */
public record BrandRequest(
        @NotBlank(message = "브랜드명을 비워둘 수 없습니다")
        String brand,

        @NotEmpty(message = "가격 정보가 필요합니다")
        Map<String, Integer> prices   // key: 한글 카테고리, value: 가격
) {}
