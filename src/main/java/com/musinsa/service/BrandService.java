package com.musinsa.service;

import com.musinsa.common.ApiException;
import com.musinsa.common.ErrorCode;
import com.musinsa.domain.Brand;
import com.musinsa.domain.Category;
import com.musinsa.domain.Product;
import com.musinsa.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepo;

    @Transactional
    public void createBrand(String name, Map<String, Integer> priceMap) {
        if (brandRepo.existsByName(name)) {
            throw new ApiException(ErrorCode.BRAND_ALREADY_EXISTS);
        }
        Brand brand = new Brand(name);
        for (var e : priceMap.entrySet()) {
            Category c;
            try {
                c = Category.fromKr(e.getKey());
            } catch (IllegalArgumentException ex) {
                throw new ApiException(
                        ErrorCode.VALIDATION_ERROR,
                        "유효하지 않은 카테고리명입니다: " + e.getKey()
                );
            }
            brand.getProducts().add(new Product(brand, c, e.getValue()));
        }
        brandRepo.save(brand);
    }

    @Transactional
    public void updateBrand(String name, Map<String, Integer> priceMap) {
        Brand brand = brandRepo.findByName(name)
                .orElseThrow(() -> new ApiException(ErrorCode.BRAND_NOT_FOUND));
        // 완전 교체: 기존 상품 모두 삭제 후 재등록
        brand.getProducts().clear();
        for (var e : priceMap.entrySet()) {
            Category c;
            try {
                c = Category.fromKr(e.getKey());
            } catch (IllegalArgumentException ex) {
                throw new ApiException(
                        ErrorCode.VALIDATION_ERROR,
                        "유효하지 않은 카테고리명입니다: " + e.getKey()
                );
            }
            brand.getProducts().add(new Product(brand, c, e.getValue()));
        }
        brandRepo.save(brand);
    }

    @Transactional
    public void deleteBrand(String name) {
        Brand brand = brandRepo.findByName(name)
                .orElseThrow(() -> new ApiException(ErrorCode.BRAND_NOT_FOUND));
        brandRepo.delete(brand);
    }
}
