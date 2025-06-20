// dto/LowestByCategoryResponse.java
package com.musinsa.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Builder
public class LowestByCategoryResponse {

    private final List<CategoryBrandPrice> items;
    private final int total;

    @Getter
    @AllArgsConstructor
    public static class CategoryBrandPrice {
        private final String category;
        private final String brand;
        private final int price;
    }
}

