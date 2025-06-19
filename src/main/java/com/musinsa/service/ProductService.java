// src/main/java/com/musinsa/service/ProductService.java
package com.musinsa.service;

import com.musinsa.common.ApiException;
import com.musinsa.domain.Brand;
import com.musinsa.domain.Category;
import com.musinsa.domain.Product;
import com.musinsa.repository.BrandRepository;
import com.musinsa.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepo;
    private final BrandRepository  brandRepo;

    @Transactional
    public Product addProduct(String brandName, String krCategory, int price) {
        Brand b = brandRepo.findByName(brandName)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(),
                        "Brand not found: " + brandName));

        Category c;
        try {
            c = Category.fromKr(krCategory);
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }

        Product p = new Product(b, c, price);
        return productRepo.save(p);
    }

    @Transactional
    public Product updateProduct(
            Long productId,
            String newBrandName,
            String newKrCategory,
            int newPrice
    ) {
        Product p = productRepo.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(),
                        "Product not found: " + productId));

        Brand oldBrand = p.getBrand();
        Brand newBrand = brandRepo.findByName(newBrandName)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(),
                        "Brand not found: " + newBrandName));

        Category c;
        try {
            c = Category.fromKr(newKrCategory);
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }

        if (productRepo.existsByBrandIdAndCategoryAndIdNot(newBrand.getId(), c, productId)) {
            throw new ApiException(409,
                    "A product with the same category already exists for this brand");
        }

        oldBrand.getProducts().remove(p);
        newBrand.getProducts().add(p);
        p.setBrand(newBrand);
        p.setCategory(c);
        p.setPrice(newPrice);
        return productRepo.save(p);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        if (!productRepo.existsById(productId)) {
            throw new ApiException(HttpStatus.NOT_FOUND.value(),
                    "Product not found: " + productId);
        }
        productRepo.deleteById(productId);
    }

}
