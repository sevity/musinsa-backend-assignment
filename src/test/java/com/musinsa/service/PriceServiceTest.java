// ────────────────────────────────────────────────────────────────
// File : src/test/java/com/musinsa/service/PriceServiceTest.java
// ────────────────────────────────────────────────────────────────
package com.musinsa.service;

import com.musinsa.common.ApiException;
import com.musinsa.common.ErrorCode;
import com.musinsa.domain.Brand;
import com.musinsa.domain.Category;
import com.musinsa.domain.Product;
import com.musinsa.dto.CategoryStatResponse;
import com.musinsa.dto.LowestByBrandResponse;
import com.musinsa.dto.LowestByCategoryResponse;
import com.musinsa.repository.BrandRepository;
import com.musinsa.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock private ProductRepository productRepo;
    @Mock private BrandRepository brandRepo;
    @InjectMocks private PriceService priceService;

    /*────────────────────────────────────────────────────────────────
     * getLowestByCategory
     *────────────────────────────────────────────────────────────────*/

    @Test
    void getLowestByCategory_success() {
        Map<Category, Integer> priceMap = new EnumMap<>(Category.class);
        for (Category c : Category.values()) {
            int price = c.ordinal() + 100;
            priceMap.put(c, price);
            when(productRepo.findByCategory(c))
                    .thenReturn(List.of(new Product(new Brand("B-"+c), c, price)));
        }

        LowestByCategoryResponse resp = priceService.getLowestByCategory();

        assertThat(resp.getItems()).hasSize(Category.values().length);
        int expectedTotal = priceMap.values().stream().mapToInt(Integer::intValue).sum();
        assertThat(resp.getTotal()).isEqualTo(expectedTotal);
    }

    @Test
    void getLowestByCategory_whenEmpty_throwsException() {
        Category bad = Category.TOP;
        when(productRepo.findByCategory(bad)).thenReturn(Collections.emptyList());

        ApiException ex = assertThrows(ApiException.class,
                () -> priceService.getLowestByCategory());

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
        assertThat(ex.getMessage())
                .isEqualTo(bad.getKrName() + " 카테고리에 상품이 없습니다.");
    }

    /*────────────────────────────────────────────────────────────────
     * getLowestBySingleBrand
     *────────────────────────────────────────────────────────────────*/

    @Test
    void getLowestBySingleBrand_success() {
        Brand b = new Brand("BestBrand");
        List<Product> prods = new ArrayList<>();
        int sum = 0;
        for (Category c : Category.values()) {
            Product p = new Product(b, c, c.ordinal() + 200);
            prods.add(p);
            sum += p.getPrice();
        }
        b.setProducts(prods);
        when(brandRepo.findAll()).thenReturn(List.of(b));

        LowestByBrandResponse resp = priceService.getLowestBySingleBrand();

        assertThat(resp.getBrand()).isEqualTo("BestBrand");
        assertThat(resp.getCategories()).hasSize(Category.values().length);
        assertThat(resp.getTotal()).isEqualTo(sum);
    }

    @Test
    void getLowestBySingleBrand_whenNoBrands_throwsException() {
        when(brandRepo.findAll()).thenReturn(Collections.emptyList());

        ApiException ex = assertThrows(ApiException.class,
                () -> priceService.getLowestBySingleBrand());

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.BRAND_NOT_FOUND);
        assertThat(ex.getMessage()).isEqualTo("등록된 브랜드가 없습니다.");
    }

    @Test
    void getLowestBySingleBrand_whenNoFullCover_throwsException() {
        Brand b = new Brand("Partial");
        b.setProducts(List.of(new Product(b, Category.TOP, 1000)));
        when(brandRepo.findAll()).thenReturn(List.of(b));

        ApiException ex = assertThrows(ApiException.class,
                () -> priceService.getLowestBySingleBrand());

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.BRAND_NOT_FOUND);
        assertThat(ex.getMessage())
                .isEqualTo("모든 카테고리를 가진 브랜드가 없습니다.");
    }

    /*────────────────────────────────────────────────────────────────
     * getCategoryStat
     *────────────────────────────────────────────────────────────────*/

    @Test
    void getCategoryStat_success() {
        Category c = Category.BOTTOM;
        Brand b1 = new Brand("A"), b2 = new Brand("B"), b3 = new Brand("C");
        Product p1 = new Product(b1, c, 500);
        Product p2 = new Product(b2, c, 1000);
        Product p3 = new Product(b3, c, 500);
        when(productRepo.findByCategory(c)).thenReturn(List.of(p1, p2, p3));

        CategoryStatResponse resp = priceService.getCategoryStat(c.getKrName());

        assertThat(resp.getCategory()).isEqualTo(c.getKrName());
        assertThat(resp.getLowest()).extracting("price")
                .containsExactlyInAnyOrder(500, 500);
        assertThat(resp.getHighest()).extracting("price")
                .containsExactly(1000);
    }

    @Test
    void getCategoryStat_invalidCategory_throwsValidationError() {
        String bad = "없는카테고리";

        ApiException ex = assertThrows(ApiException.class,
                () -> priceService.getCategoryStat(bad));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.VALIDATION_ERROR);
        assertThat(ex.getMessage())
                .isEqualTo("유효하지 않은 카테고리명입니다: " + bad);
    }

    @Test
    void getCategoryStat_emptyList_throwsNotFound() {
        Category c = Category.HAT;
        when(productRepo.findByCategory(c)).thenReturn(Collections.emptyList());

        ApiException ex = assertThrows(ApiException.class,
                () -> priceService.getCategoryStat(c.getKrName()));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND);
        assertThat(ex.getMessage())
                .isEqualTo("요청하신 카테고리를 찾을 수 없습니다.");
    }
}
