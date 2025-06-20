package com.musinsa.service;

import com.musinsa.common.ApiException;
import com.musinsa.common.ErrorCode;
import com.musinsa.domain.Brand;
import com.musinsa.domain.Category;
import com.musinsa.domain.Product;
import com.musinsa.repository.BrandRepository;
import com.musinsa.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final BrandRepository brandRepo;
    private final ProductRepository productRepo;

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

        if (productRepo.existsByBrandAndCategory(brand, category)) {
            throw new ApiException(ErrorCode.PRODUCT_ALREADY_EXISTS);
        }

        Product product = new Product(brand, category, price);
        return productRepo.save(product);
    }

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

    @Transactional
    public void deleteProduct(Long id) {
        Product p = productRepo.findById(id)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        String.format("상품 ID %d를 찾을 수 없습니다.", id)
                ));

        productRepo.delete(p);
    }
}
