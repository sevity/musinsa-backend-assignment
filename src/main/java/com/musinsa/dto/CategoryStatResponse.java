// dto/CategoryStatResponse.java
package com.musinsa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class CategoryStatResponse {
    private final String category;
    private final List<BrandPrice> lowest;
    private final List<BrandPrice> highest;

    @Getter
    @AllArgsConstructor
    public static class BrandPrice {
        private final String brand;
        private final int price;
    }
}
