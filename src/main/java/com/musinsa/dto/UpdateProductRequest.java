package com.musinsa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상품 가격 수정 요청 DTO
 */
@Schema(description = "상품 수정 요청")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateProductRequest {

        @Schema(description = "수정할 가격", example = "12000")
        private int price;
}
