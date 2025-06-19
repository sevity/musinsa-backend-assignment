// src/main/java/com/musinsa/controller/ProductAdminController.java
package com.musinsa.controller;

import com.musinsa.dto.CreateProductRequest;
import com.musinsa.dto.UpdateProductRequest;
import com.musinsa.service.ProductService;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductService productService;

    /** 상품 생성 */
    @PostMapping
    public ResponseEntity<ProductIdResponse> addProduct(@RequestBody @Valid CreateProductRequest req) {
        var p = productService.addProduct(req.brand(), req.category(), req.price());
        return ResponseEntity.ok(new ProductIdResponse(p.getId()));
    }

    /** 상품 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProductRequest req) {

        productService.updateProduct(id, req.brand(), req.category(), req.price());
        return ResponseEntity.ok().build();
    }

    /** 상품 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    /* 응답 DTO (내부) */
    private record ProductIdResponse(Long id) {}
}
