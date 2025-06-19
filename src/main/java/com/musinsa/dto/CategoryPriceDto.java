// dto/CategoryBrandPriceDto.java
package com.musinsa.dto;

public record CategoryPriceDto(
        String category,
        int price) implements PriceItem {}
