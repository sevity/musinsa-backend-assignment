package com.musinsa.dto;

import lombok.Getter;
import java.util.Map;

@Getter
public class BrandRequest {
        private String brand;
        private Map<String, Integer> prices;
}
