package com.musinsa.service;

import com.musinsa.common.ApiException;
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
                .orElseThrow(() -> new ApiException(404, "Brand not found: " + brandName));
        Category category = Category.fromKr(categoryKr);
        Product product = new Product(brand, category, price);
        return productRepo.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, int newPrice) {
        Product p = productRepo.findById(id)
                .orElseThrow(() -> new ApiException(404, "Product not found: " + id));
        p.setPrice(newPrice);
        return p;
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product p = productRepo.findById(id)
                .orElseThrow(() -> new ApiException(404, "Product not found: " + id));
        productRepo.delete(p);
    }
}
