package com.musinsa.service;

import com.musinsa.common.ApiException;
import com.musinsa.domain.*;
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

    public LowestByCategoryResponse getLowestByCategory() {
        int total = 0;
        List<LowestByCategoryResponse.CategoryBrandPrice> list = new ArrayList<>();

        for (Category c : Category.values()) {
            Product min = productRepo.findByCategory(c).stream()
                    .min(Comparator.comparingInt(Product::getPrice))
                    .orElseThrow(() -> new ApiException(404, c + " No Data"));

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

    public LowestByBrandResponse getLowestBySingleBrand() {
        List<Brand> brands = brandRepo.findAll();
        if (brands.isEmpty()) {
            throw new ApiException(404, "No Brand Data");
        }

        Brand best = null;
        int bestTotal = Integer.MAX_VALUE;
        Map<Category, Product> bestMap = null;

        for (Brand b : brands) {
            Map<Category, Product> map = b.getProducts().stream()
                    .collect(Collectors.toMap(Product::getCategory, p -> p));
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
            throw new ApiException(404, "No brand that has all categories");
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

    public CategoryStatResponse getCategoryStat(String krCategory) {
        Category c;
        try {
            c = Category.fromKr(krCategory);
        } catch (IllegalArgumentException e) {
            throw new ApiException(400, e.getMessage());
        }

        List<Product> list = productRepo.findByCategory(c);
        if (list.isEmpty()) {
            throw new ApiException(404, "No Data");
        }

        int minPrice = list.stream()
                .mapToInt(Product::getPrice)
                .min()
                .getAsInt();
        int maxPrice = list.stream()
                .mapToInt(Product::getPrice)
                .max()
                .getAsInt();

        List<CategoryStatResponse.BrandPrice> minList = list.stream()
                .filter(p -> p.getPrice() == minPrice)
                .map(p -> new CategoryStatResponse.BrandPrice(
                        p.getBrand().getName(),
                        p.getPrice()
                ))
                .toList();

        List<CategoryStatResponse.BrandPrice> maxList = list.stream()
                .filter(p -> p.getPrice() == maxPrice)
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
