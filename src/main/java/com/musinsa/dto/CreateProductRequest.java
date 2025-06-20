// src/main/java/com/musinsa/dto/CreateProductRequest.java
package com.musinsa.dto;

import lombok.Getter;

@Getter
public class CreateProductRequest {
        private String brand;
        private String category;
        private int price;
}
