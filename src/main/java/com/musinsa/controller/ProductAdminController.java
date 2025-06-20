package com.musinsa.controller;

import com.musinsa.dto.CreateProductRequest;
import com.musinsa.dto.UpdateProductRequest;
import com.musinsa.domain.Product;
import com.musinsa.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductAdminController {
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody CreateProductRequest req) {
        return productService.createProduct(
                req.getBrand(), req.getCategory(), req.getPrice()
        );
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Product updateProduct(
            @PathVariable Long id,
            @RequestBody UpdateProductRequest req
    ) {
        return productService.updateProduct(id, req.getPrice());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
