// src/main/java/com/musinsa/controller/PriceController.java
package com.musinsa.controller;

import com.musinsa.dto.*;
import com.musinsa.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    /** 카테고리별 최저가 브랜드 목록 */
    @GetMapping("/categories/cheapest-brands")
    public LowestByCategoryResponse getCheapestBrandsPerCategory() {
        return priceService.getLowestByCategory();
    }

    /** 모든 카테고리를 한 번에 가장 저렴하게 살 수 있는 ‘단일 브랜드’ */
    @GetMapping("/brands/cheapest")
    public LowestByBrandResponse getCheapestBrandBundle() {
        return priceService.getLowestBySingleBrand();
    }

    /** 특정 카테고리의 최저·최고 가격 통계 */
    @GetMapping("/categories/{category}/price-stats")
    public CategoryStatResponse getCategoryPriceStats(@PathVariable String category) {
        return priceService.getCategoryStat(category);
    }
}
