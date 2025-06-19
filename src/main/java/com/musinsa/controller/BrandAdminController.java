// src/main/java/com/musinsa/controller/BrandAdminController.java
package com.musinsa.controller;

import com.musinsa.dto.BrandRequest;
import com.musinsa.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandAdminController {

    private final BrandService brandService;

    /** 브랜드 등록·수정 */
    @PostMapping
    public ResponseEntity<?> upsertBrand(@RequestBody @Valid BrandRequest req) {
        brandService.upsertBrand(req.brand(), req.prices());
        return ResponseEntity.ok().build();
    }

    /** 브랜드 삭제 */
    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteBrand(@PathVariable String name) {
        brandService.deleteBrand(name);
        return ResponseEntity.ok().build();
    }
}
