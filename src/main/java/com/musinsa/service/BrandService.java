// service/BrandService.java
package com.musinsa.service;

import com.musinsa.common.ApiException;
import com.musinsa.domain.*;
import com.musinsa.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service @RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepo;

    @Transactional
    public void upsertBrand(String name, Map<String, Integer> priceMap) {
        Brand brand = brandRepo.findByName(name).orElseGet(() -> new Brand(name));
        brand.getProducts().clear();

        for (Map.Entry<String, Integer> e : priceMap.entrySet()) {
            Category c = Category.fromKr(e.getKey());
            brand.getProducts().add(new Product(brand, c, e.getValue()));
        }
        brandRepo.save(brand);
    }

    @Transactional
    public void deleteBrand(String name) {
        Brand b = brandRepo.findByName(name)
                .orElseThrow(() -> new ApiException(404, "Brand not found"));
        brandRepo.delete(b);
    }
}
