// dto/CategoryBrandPriceDto.java
package com.musinsa.dto;

public record CategoryBrandPriceDto(
        String category,
        String brand,
        int price) implements PriceItem {}

