package com.musinsa.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        var ec = ex.getErrorCode();
        return ResponseEntity.status(ec.getStatus()).body(
                ErrorResponse.builder()
                        .status(ec.getStatus().value())
                        .code(ec.getCode())
                        .message(ex.getMessage())   // 세부 메시지가 있으면 그걸 사용
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        var ec = ErrorCode.INTERNAL_ERROR;
        return ResponseEntity.status(ec.getStatus()).body(
                ErrorResponse.builder()
                        .status(ec.getStatus().value())
                        .code(ec.getCode())
                        .message(ec.getDefaultMessage())
                        .build()
        );
    }
}
