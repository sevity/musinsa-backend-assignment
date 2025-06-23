package com.musinsa.service;

import com.musinsa.common.ApiException;
import com.musinsa.domain.Category;
import com.musinsa.domain.Product;
import com.musinsa.common.ErrorCode;
import com.musinsa.domain.Brand;
import com.musinsa.dto.CategoryStatResponse;
import com.musinsa.dto.LowestByBrandResponse;
import com.musinsa.dto.LowestByCategoryResponse;
import com.musinsa.repository.BrandRepository;
import com.musinsa.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceService {
    private final ProductRepository productRepo;
    private final BrandRepository brandRepo;

    /**
     * 구현1) 카테고리별 최저가 조회
     */
    public LowestByCategoryResponse getLowestByCategory() {
        int total = 0;
        List<LowestByCategoryResponse.CategoryBrandPrice> list = new ArrayList<>();

        for (Category c : Category.values()) {
            Product min = productRepo.findMinPriceByCategory(c).stream()
                    .findFirst()
                    .orElseThrow(() ->
                            new ApiException(
                                    ErrorCode.PRODUCT_NOT_FOUND,
                                    c.getKrName() + " 카테고리에 상품이 없습니다."
                            )
                    );

            list.add(new LowestByCategoryResponse.CategoryBrandPrice(
                    c.getKrName(),
                    min.getBrand().getName(),
                    min.getPrice()
            ));
            total += min.getPrice();
        }

        return LowestByCategoryResponse.builder()
                .items(list)
                .total(total)
                .build();
    }

    /**
     * 구현2) 단일 브랜드 최저가 번들
     */
    public LowestByBrandResponse getLowestBySingleBrand() {
        List<Brand> brands = brandRepo.findAll();
        if (brands.isEmpty()) {
            throw new ApiException(
                    ErrorCode.BRAND_NOT_FOUND,
                    "등록된 브랜드가 없습니다."
            );
        }

        Brand best = null;
        int bestTotal = Integer.MAX_VALUE;
        Map<Category, Product> bestMap = null;

        for (Brand b : brands) {
            Map<Category, Product> map = b.getProducts().stream()
                    .collect(Collectors.toMap(
                            Product::getCategory,
                            p -> p,
                            (a, bProd) -> a.getPrice() <= bProd.getPrice() ? a : bProd
                    ));
            // 모든 카테고리 커버 확인
            if (map.size() != Category.values().length) {
                continue;
            }

            int sum = map.values().stream()
                    .mapToInt(Product::getPrice)
                    .sum();

            if (sum < bestTotal) {
                bestTotal = sum;
                best = b;
                bestMap = map;
            }
        }

        if (best == null) {
            throw new ApiException(
                    ErrorCode.BRAND_NOT_FOUND,
                    "모든 카테고리를 가진 브랜드가 없습니다."
            );
        }

        List<LowestByBrandResponse.CategoryPrice> details = bestMap.values().stream()
                .map(p -> new LowestByBrandResponse.CategoryPrice(
                        p.getCategory().getKrName(),
                        p.getPrice()
                ))
                .toList();

        return LowestByBrandResponse.builder()
                .brand(best.getName())
                .categories(details)
                .total(bestTotal)
                .build();
    }

    /**
     * 구현3) 카테고리별 최저·최고가 조회
     */
    public CategoryStatResponse getCategoryStat(String krCategory) {
        Category c;
        try {
            c = Category.fromKr(krCategory);
        } catch (IllegalArgumentException e) {
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    "유효하지 않은 카테고리명입니다: " + krCategory
            );
        }

        List<Product> minProd = productRepo.findMinPriceByCategory(c);
        if (minProd.isEmpty()) {
            throw new ApiException(
                    ErrorCode.CATEGORY_NOT_FOUND,
                    "요청하신 카테고리를 찾을 수 없습니다."
            );
        }

        List<Product> maxProd = productRepo.findMaxPriceByCategory(c);

        List<CategoryStatResponse.BrandPrice> minList = minProd.stream()
                .map(p -> new CategoryStatResponse.BrandPrice(
                        p.getBrand().getName(),
                        p.getPrice()
                ))
                .toList();

        List<CategoryStatResponse.BrandPrice> maxList = maxProd.stream()
                .map(p -> new CategoryStatResponse.BrandPrice(
                        p.getBrand().getName(),
                        p.getPrice()
                ))
                .toList();

        return CategoryStatResponse.builder()
                .category(c.getKrName())
                .lowest(minList)
                .highest(maxList)
                .build();
    }
}
