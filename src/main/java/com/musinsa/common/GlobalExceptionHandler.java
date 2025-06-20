// src/main/java/com/musinsa/common/GlobalExceptionHandler.java
package com.musinsa.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 서비스 계층에서 직접 던지는 ApiException 처리
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {

        ErrorCode ec = ex.getErrorCode();

        // 세부 메시지가 없으면 ErrorCode 기본 메시지를 사용
        String msg = (ex.getMessage() == null || ex.getMessage().isBlank())
                ? ec.getDefaultMessage()
                : ex.getMessage();

        return ResponseEntity
                .status(ec.getStatus())               // ErrorCode에 정의된 HTTP 상태 사용
                .body(ErrorResponse.builder()
                        .status(ec.getStatus().value())
                        .code(ec.getCode())
                        .message(msg)
                        .build());
    }

    /**
     * 예상하지 못한 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {

        log.error("Unhandled exception caught by GlobalExceptionHandler", ex);

        ErrorCode ec = ErrorCode.INTERNAL_ERROR;
        return ResponseEntity
                .status(ec.getStatus())
                .body(ErrorResponse.builder()
                        .status(ec.getStatus().value())
                        .code(ec.getCode())
                        .message(ec.getDefaultMessage())
                        .build());
    }
}
