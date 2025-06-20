// ────────────────────────────────────────────────────────────────
// File : src/test/java/com/musinsa/service/BrandServiceTest.java
// ────────────────────────────────────────────────────────────────
package com.musinsa.service;

import com.musinsa.common.ApiException;
import com.musinsa.common.ErrorCode;
import com.musinsa.domain.Brand;
import com.musinsa.domain.Category;
import com.musinsa.domain.Product;
import com.musinsa.repository.BrandRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepo;

    @InjectMocks
    private BrandService brandService;

    /*────────────────────────────────────────────────────────────────
     * createBrand
     *────────────────────────────────────────────────────────────────*/
    @Test
    void createBrand_success() {
        String name = "NewBrand";
        Map<String, Integer> priceMap = Map.of(
                Category.TOP.getKrName(), 1000,
                Category.BOTTOM.getKrName(), 2000
        );

        when(brandRepo.existsByName(name)).thenReturn(false);

        brandService.createBrand(name, priceMap);

        // save 호출 검증
        ArgumentCaptor<Brand> captor = ArgumentCaptor.forClass(Brand.class);
        verify(brandRepo).save(captor.capture());

        Brand saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo(name);

        // 제품 정보가 priceMap 크기만큼 추가됐는지 확인
        assertThat(saved.getProducts()).hasSize(priceMap.size());
        for (Product p : saved.getProducts()) {
            String kr = p.getCategory().getKrName();
            assertThat(priceMap).containsKey(kr);
            assertThat(p.getPrice()).isEqualTo(priceMap.get(kr));
            assertThat(p.getBrand()).isSameAs(saved);
        }
    }

    @Test
    void createBrand_whenAlreadyExists_throwsException() {
        String name = "ExistBrand";
        when(brandRepo.existsByName(name)).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class,
                () -> brandService.createBrand(name, Map.of()));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.BRAND_ALREADY_EXISTS);
    }

    /*────────────────────────────────────────────────────────────────
     * updateBrand
     *────────────────────────────────────────────────────────────────*/
    @Test
    void updateBrand_success() {
        String name = "UpBrand";
        Brand existing = new Brand(name);
        // 기존 제품 셋업
        existing.getProducts().add(new Product(existing, Category.TOP, 500));
        when(brandRepo.findByName(name)).thenReturn(Optional.of(existing));

        Map<String, Integer> newPrices = Map.of(
                Category.BOTTOM.getKrName(), 1500,
                Category.HAT.getKrName(), 800
        );

        brandService.updateBrand(name, newPrices);

        // clear 후 재등록, save 호출 검증
        ArgumentCaptor<Brand> captor = ArgumentCaptor.forClass(Brand.class);
        verify(brandRepo).save(captor.capture());

        Brand updated = captor.getValue();
        assertThat(updated.getName()).isEqualTo(name);
        assertThat(updated.getProducts()).hasSize(newPrices.size());
        for (Product p : updated.getProducts()) {
            String kr = p.getCategory().getKrName();
            assertThat(newPrices).containsKey(kr);
            assertThat(p.getPrice()).isEqualTo(newPrices.get(kr));
        }
    }

    @Test
    void updateBrand_whenNotFound_throwsException() {
        String name = "NoBrand";
        when(brandRepo.findByName(name)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> brandService.updateBrand(name, Map.of()));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.BRAND_NOT_FOUND);
    }

    /*────────────────────────────────────────────────────────────────
     * deleteBrand
     *────────────────────────────────────────────────────────────────*/
    @Test
    void deleteBrand_success() {
        String name = "DelBrand";
        Brand existing = new Brand(name);
        when(brandRepo.findByName(name)).thenReturn(Optional.of(existing));

        brandService.deleteBrand(name);

        verify(brandRepo).delete(existing);
    }

    @Test
    void deleteBrand_whenNotFound_throwsException() {
        String name = "Missing";
        when(brandRepo.findByName(name)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> brandService.deleteBrand(name));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.BRAND_NOT_FOUND);
    }
}
