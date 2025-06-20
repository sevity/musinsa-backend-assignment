package com.musinsa.controller;

import com.musinsa.dto.BrandRequest;
import com.musinsa.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandAdminController {
    private final BrandService brandService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createBrand(@RequestBody BrandRequest req) {
        brandService.createBrand(req.getBrand(), req.getPrices());
    }

    @PutMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBrand(
            @PathVariable String name,
            @RequestBody BrandRequest req
    ) {
        brandService.updateBrand(name, req.getPrices());
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBrand(@PathVariable String name) {
        brandService.deleteBrand(name);
    }
}
