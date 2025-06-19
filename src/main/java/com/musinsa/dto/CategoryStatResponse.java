// dto/CategoryStatResponse.java
package com.musinsa.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class CategoryStatResponse {
    private final String category;
    private final List<BrandPriceDto> lowest;
    private final List<BrandPriceDto> highest;
}
