// dto/PriceItem.java
package com.musinsa.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE) // 타입 힌트 숨김
public sealed interface PriceItem permits
        CategoryBrandPriceDto,
        CategoryPriceDto,
        BrandPriceDto {
    int price(); // 공통 속성
}
