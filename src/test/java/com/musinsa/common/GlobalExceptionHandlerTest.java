// ────────────────────────────────────────────────────────────────
// File : src/test/java/com/musinsa/common/GlobalExceptionHandlerTest.java
// ────────────────────────────────────────────────────────────────
package com.musinsa.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @RestController
        @RequestMapping("/test")
        static class TestController {
            @GetMapping("/api-ex/{code}")
            public void throwApiException(@PathVariable String code) {
                // PathVariable로 받은 code를 ErrorCode.valueOf()로 변환해 ApiException 던짐
                throw new ApiException(ErrorCode.valueOf(code));
            }

            @GetMapping("/api-ex-custom")
            public void throwApiExceptionWithCustomMessage() {
                // 특정 메시지를 전달하여 ApiException 생성
                throw new ApiException(ErrorCode.PRODUCT_NOT_FOUND, "특정 메시지");
            }

        @GetMapping("/generic-ex")
        public void throwGenericException() {
            throw new RuntimeException("unexpected");
        }
    }

    @ParameterizedTest
    @EnumSource(ErrorCode.class)
    void handleApiException_allErrorCodes_returnProperErrorResponse(ErrorCode errorCode) throws Exception {
        mockMvc.perform(get("/test/api-ex/{code}", errorCode.name())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(errorCode.getStatus().value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(errorCode.getStatus().value()))
                .andExpect(jsonPath("$.code").value(errorCode.getCode()))
                .andExpect(jsonPath("$.message").value(errorCode.getDefaultMessage()));
    }

    @Test
    void handleApiException_customMessage_overridesDefault() throws Exception {
        mockMvc.perform(get("/test/api-ex-custom")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(ErrorCode.PRODUCT_NOT_FOUND.getStatus().value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(ErrorCode.PRODUCT_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.PRODUCT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value("특정 메시지"));
    }

    @Test
    void handleGenericException_returnsInternalErrorResponse() throws Exception {
        mockMvc.perform(get("/test/generic-ex")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(ErrorCode.INTERNAL_ERROR.getStatus().value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.INTERNAL_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.INTERNAL_ERROR.getDefaultMessage()));
    }
}
