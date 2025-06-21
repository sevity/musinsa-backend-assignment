// src/main/java/com/musinsa/dto/ProductResponse.java
package com.musinsa.dto;

import com.musinsa.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String brand;
    private Category category;
    private Integer price;
}
