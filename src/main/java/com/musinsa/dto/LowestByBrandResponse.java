// dto/LowestByBrandResponse.java
package com.musinsa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class LowestByBrandResponse {
    private final String brand;
    private final List<CategoryPrice> categories;
    private final int total;

    @Getter
    @AllArgsConstructor
    public static class CategoryPrice {
        private final String category;
        private final int price;
    }
}
