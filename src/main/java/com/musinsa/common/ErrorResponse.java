// common/ErrorResponse.java
package com.musinsa.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class ErrorResponse {
    private final int status;
    private final String message;
}
