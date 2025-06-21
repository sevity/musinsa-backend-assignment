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
import java.util.Set;
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

        /* 1) update-or-insert 요청 카테고리 */
        for (Map.Entry<String, Integer> e : priceMap.entrySet()) {

            String krName   = e.getKey();
            int    newPrice = e.getValue();

            /* ✨ 카테고리 파싱 → 예외 래핑 */
            Category cat;
            try {
                cat = Category.fromKr(krName);
            } catch (IllegalArgumentException ex) {
                throw new ApiException(
                        ErrorCode.VALIDATION_ERROR,
                        "유효하지 않은 카테고리명입니다: " + krName
                );
            }

            /* update or insert 로직 그대로 … */
            Optional<Product> found =
                    productRepo != null
                            ? productRepo.findByBrandAndCategory(brand, cat)
                            : brand.getProducts().stream()
                            .filter(p -> p.getCategory() == cat)
                            .findFirst();

            if (found.isPresent()) {
                found.get().setPrice(newPrice);
            } else {
                brand.getProducts().add(new Product(brand, cat, newPrice));
            }
        }

        /* 2) ✂️  priceMap 에 포함되지 않은 카테고리 상품 삭제  */
        Set<String> reqCats = priceMap.keySet();
        brand.getProducts().removeIf(p ->
                !reqCats.contains(p.getCategory().getKrName())
        );

        /* 3) 변경 저장 */
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
