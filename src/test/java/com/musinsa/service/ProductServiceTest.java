// ────────────────────────────────────────────────────────────────
// File : src/test/java/com/musinsa/service/ProductServiceTest.java
// ────────────────────────────────────────────────────────────────
package com.musinsa.service;

import com.musinsa.common.ApiException;
import com.musinsa.common.ErrorCode;
import com.musinsa.domain.Brand;
import com.musinsa.domain.Category;
import com.musinsa.domain.Product;
import com.musinsa.repository.BrandRepository;
import com.musinsa.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private BrandRepository brandRepo;

    @Mock
    private ProductRepository productRepo;

    @InjectMocks
    private ProductService productService;

    /*────────────────────────────────────────────────────────────────
     * createProduct
     *────────────────────────────────────────────────────────────────*/

    @Test
    void createProduct_success() {
        String brandName = "BrandA";
        String categoryKr = Category.TOP.getKrName();
        int price = 12345;

        Brand brand = new Brand(brandName);
        when(brandRepo.findByName(brandName)).thenReturn(Optional.of(brand));
        when(productRepo.existsByBrandAndCategory(brand, Category.TOP)).thenReturn(false);
        Product saved = new Product(brand, Category.TOP, price);
        when(productRepo.save(any(Product.class))).thenReturn(saved);

        Product result = productService.createProduct(brandName, categoryKr, price);

        assertThat(result.getBrand()).isEqualTo(brand);
        assertThat(result.getCategory()).isEqualTo(Category.TOP);
        assertThat(result.getPrice()).isEqualTo(price);
        verify(productRepo).save(any(Product.class));
    }

    @Test
    void createProduct_brandNotFound_throwsException() {
        String brandName = "NoBrand";
        when(brandRepo.findByName(brandName)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> productService.createProduct(brandName, Category.TOP.getKrName(), 1000));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.BRAND_NOT_FOUND);
        assertThat(ex.getMessage())
                .isEqualTo(String.format("브랜드 '%s'를 찾을 수 없습니다.", brandName));
    }

    @Test
    void createProduct_invalidCategory_throwsValidationError() {
        String brandName = "BrandA";
        String badCategory = "잘못된카테고리";
        when(brandRepo.findByName(brandName)).thenReturn(Optional.of(new Brand(brandName)));

        ApiException ex = assertThrows(ApiException.class,
                () -> productService.createProduct(brandName, badCategory, 1000));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.VALIDATION_ERROR);
        assertThat(ex.getMessage())
                .isEqualTo(String.format("유효하지 않은 카테고리명입니다: '%s'.", badCategory));
    }

    @Test
    void createProduct_alreadyExists_throwsException() {
        String brandName = "BrandA";
        Brand brand = new Brand(brandName);
        when(brandRepo.findByName(brandName)).thenReturn(Optional.of(brand));
        when(productRepo.existsByBrandAndCategory(brand, Category.TOP)).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class,
                () -> productService.createProduct(brandName, Category.TOP.getKrName(), 1000));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_ALREADY_EXISTS);
        assertThat(ex.getMessage())
                .isEqualTo(ErrorCode.PRODUCT_ALREADY_EXISTS.getDefaultMessage());
    }

    /*────────────────────────────────────────────────────────────────
     * updateProduct
     *────────────────────────────────────────────────────────────────*/

    @Test
    void updateProduct_success() {
        Long id = 1L;
        int newPrice = 5555;
        Product existing = new Product(new Brand("B"), Category.BOTTOM, 1000);
        when(productRepo.findById(id)).thenReturn(Optional.of(existing));

        Product result = productService.updateProduct(id, newPrice);

        assertThat(result.getPrice()).isEqualTo(newPrice);
        verify(productRepo, never()).save(any()); // update uses entity setter only
    }

    @Test
    void updateProduct_notFound_throwsException() {
        Long id = 99L;
        when(productRepo.findById(id)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> productService.updateProduct(id, 2000));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
        assertThat(ex.getMessage())
                .isEqualTo(String.format("상품 ID %d를 찾을 수 없습니다.", id));
    }

    /*────────────────────────────────────────────────────────────────
     * deleteProduct
     *────────────────────────────────────────────────────────────────*/

    @Test
    void deleteProduct_success() {
        Long id = 2L;
        Product existing = new Product(new Brand("B"), Category.BOTTOM, 3000);
        when(productRepo.findById(id)).thenReturn(Optional.of(existing));

        productService.deleteProduct(id);

        verify(productRepo).delete(existing);
    }

    @Test
    void deleteProduct_notFound_throwsException() {
        Long id = 42L;
        when(productRepo.findById(id)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> productService.deleteProduct(id));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
        assertThat(ex.getMessage())
                .isEqualTo(String.format("상품 ID %d를 찾을 수 없습니다.", id));
    }
}
