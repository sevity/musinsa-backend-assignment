// dto/LowestByCategoryResponse.java
package com.musinsa.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class LowestByCategoryResponse {
    private final List<CategoryBrandPriceDto> items;
    private final int total;
}
