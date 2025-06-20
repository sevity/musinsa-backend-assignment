package com.musinsa.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private final int status;       // HTTP Status code (e.g. 404)
    private final String code;      // Enum code (e.g. BRAND_NOT_FOUND)
    private final String message;   // Human-readable message
}
