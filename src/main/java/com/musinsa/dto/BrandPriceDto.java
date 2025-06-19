// dto/BrandPriceDto.java

package com.musinsa.dto;

public record BrandPriceDto(
        String brand,
        int price) implements PriceItem {}

