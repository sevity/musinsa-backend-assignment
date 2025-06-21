// src/main/java/com/musinsa/service/ProductService.java
package com.musinsa.service;

import com.musinsa.common.ApiException;
import com.musinsa.common.ErrorCode;
import com.musinsa.domain.Brand;
import com.musinsa.domain.Category;
import com.musinsa.domain.Product;
import com.musinsa.dto.ProductResponse;
import com.musinsa.repository.BrandRepository;
import com.musinsa.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final BrandRepository brandRepo;
    private final ProductRepository productRepo;

    /**
     * 새로운 상품을 등록합니다.
     * - 브랜드가 존재하는지 확인
     * - 카테고리 문자열을 enum으로 변환, 유효성 검증
     * - 동일 브랜드·카테고리의 상품 중복 검사 후 등록
     */
    @Transactional
    public Product createProduct(String brandName, String categoryKr, int price) {
        Brand brand = brandRepo.findByName(brandName)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.BRAND_NOT_FOUND,
                        String.format("브랜드 '%s'를 찾을 수 없습니다.", brandName)
                ));

        Category category;
        try {
            category = Category.fromKr(categoryKr);
        } catch (IllegalArgumentException e) {
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    String.format("유효하지 않은 카테고리명입니다: '%s'.", categoryKr)
            );
        }

        // 중복 검사: 이미 존재하는 브랜드·카테고리 조합인지 확인
        if (productRepo.existsByBrandAndCategory(brand, category)) {
            throw new ApiException(
                    ErrorCode.PRODUCT_ALREADY_EXISTS,
                    String.format("브랜드 '%s'의 카테고리 '%s' 상품이 이미 존재합니다.", brandName, categoryKr)
            );
        }

        Product product = new Product(brand, category, price);
        return productRepo.save(product);
    }

    /**
     * 기존 상품의 가격을 수정합니다.
     */
    @Transactional
    public Product updateProduct(Long id, int newPrice) {
        Product p = productRepo.findById(id)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        String.format("상품 ID %d를 찾을 수 없습니다.", id)
                ));

        p.setPrice(newPrice);
        return p;
    }

    /**
     * 상품을 삭제합니다.
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product p = productRepo.findById(id)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        String.format("상품 ID %d를 찾을 수 없습니다.", id)
                ));

        productRepo.delete(p);
    }

    /**
     * 등록된 모든 상품을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepo.findAll().stream()
                .map(p -> new ProductResponse(
                        p.getId(),
                        p.getBrand().getName(),
                        p.getCategory(),
                        p.getPrice()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 단일 상품의 상세 정보를 조회합니다.
     */
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        Product p = productRepo.findById(id)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        String.format("상품 ID %d를 찾을 수 없습니다.", id)
                ));
        return new ProductResponse(
                p.getId(),
                p.getBrand().getName(),
                p.getCategory(),
                p.getPrice()
        );
    }
}
