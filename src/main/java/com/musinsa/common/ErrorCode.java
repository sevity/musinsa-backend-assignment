package com.musinsa.common;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public enum ErrorCode {

    // ───────── 4xx Client Error ─────────
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND,      "BRAND_NOT_FOUND",      "브랜드를 찾을 수 없습니다."),
    BRAND_ALREADY_EXISTS(HttpStatus.CONFLICT,  "BRAND_ALREADY_EXISTS", "이미 존재하는 브랜드입니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND,    "PRODUCT_NOT_FOUND",    "상품을 찾을 수 없습니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST,   "VALIDATION_ERROR",     "요청 값이 유효하지 않습니다."),

    // ───────── 5xx Server Error ─────────
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String code, String defaultMessage) {
        this.status = status;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
