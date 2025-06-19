// common/GlobalExceptionHandler.java
package com.musinsa.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handle(ApiException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(new ErrorResponse(e.getStatus(), e.getMessage()));
    }
}
