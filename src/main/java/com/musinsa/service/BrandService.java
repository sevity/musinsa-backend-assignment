package com.musinsa.service;

import com.musinsa.common.ApiException;
import com.musinsa.common.ErrorCode;
import com.musinsa.domain.Brand;
import com.musinsa.domain.Category;
import com.musinsa.domain.Product;
import com.musinsa.dto.BrandRequest;
import com.musinsa.repository.BrandRepository;
import com.musinsa.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepo;
    private final ProductRepository productRepo;


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

        // 1) 요청된 카테고리별 가격대로 기존 상품 업데이트 또는 신규 등록
        for (var entry : priceMap.entrySet()) {
            String krName = entry.getKey();
            int newPrice = entry.getValue();
            Category category;
            try {
                category = Category.fromKr(krName);
            } catch (IllegalArgumentException ex) {
                throw new ApiException(
                        ErrorCode.VALIDATION_ERROR,
                        "유효하지 않은 카테고리명입니다: " + krName
                );
            }

            // 기존 상품이 있으면 가격만 update
            Optional<Product> existing = productRepo.findByBrandAndCategory(brand, category);
            if (existing.isPresent()) {
                existing.get().setPrice(newPrice);
            } else {
                // 없으면 새로 추가
                Product p = new Product(brand, category, newPrice);
                brand.getProducts().add(p);
            }
        }

        // 2) 선택: priceMap에 포함되지 않은 기존 카테고리 상품은 삭제하려면 아래 주석 해제
    /*
    var toRemove = brand.getProducts().stream()
        .filter(p -> !priceMap.containsKey(p.getCategory().getKrName()))
        .collect(Collectors.toList());
    brand.getProducts().removeAll(toRemove);
    */

        // 3) 변경된 컬렉션은 영속성 컨텍스트에 반영됨
        brandRepo.save(brand);
    }


    @Transactional
    public void deleteBrand(String name) {
        Brand brand = brandRepo.findByName(name)
                .orElseThrow(() -> new ApiException(ErrorCode.BRAND_NOT_FOUND));
        brandRepo.delete(brand);
    }

    @Transactional(readOnly = true)
    public BrandRequest getBrand(String name) {
        var b = brandRepo.findByName(name)
                .orElseThrow(() -> new ApiException(ErrorCode.BRAND_NOT_FOUND));
        // @Builder 를 이용해 BrandRequest 생성
        return BrandRequest.builder()
            .brand(b.getName())
            .prices(
                 b.getProducts().stream()
                     .collect(Collectors.toMap(
                     p -> p.getCategory().getKrName(),
                     Product::getPrice
                 ))
            )
            .build();
    }

    @Transactional(readOnly = true)
    public List<String> getAllBrandNames() {
        return brandRepo.findAll()
                .stream()
                .map(Brand::getName)
                .collect(Collectors.toList());
    }
}
