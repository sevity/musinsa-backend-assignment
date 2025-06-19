// dto/LowestByBrandResponse.java
package com.musinsa.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class LowestByBrandResponse {
    private final String brand;
    private final List<CategoryPriceDto> categories;
    private final int total;
}
