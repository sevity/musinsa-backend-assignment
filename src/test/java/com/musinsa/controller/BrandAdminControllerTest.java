// ────────────────────────────────────────────────────────────────
// File : src/test/java/com/musinsa/controller/BrandAdminControllerTest.java
// ────────────────────────────────────────────────────────────────
package com.musinsa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.common.ApiException;
import com.musinsa.common.ErrorCode;
import com.musinsa.common.GlobalExceptionHandler;
import com.musinsa.dto.BrandRequest;
import com.musinsa.service.BrandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BrandAdminControllerTest {

    @Mock
    private BrandService brandService;

    @InjectMocks
    private BrandAdminController brandAdminController;

    private MockMvc mockMvc;
    private final ObjectMapper om = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(brandAdminController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    /* ------------------------------------------------------------------
     *  POST /api/v1/brands  (Create)
     * ------------------------------------------------------------------ */
    @Test
    void createBrand_success_returns201() throws Exception {
        BrandRequest req = BrandRequest.builder()
                .brand("Z")
                .prices(Map.of("상의", 10000, "아우터", 5000))
                .build();

        mockMvc.perform(
                        post("/api/v1/brands")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(req))
                )
                .andExpect(status().isCreated());

        verify(brandService).createBrand(eq("Z"), anyMap());
    }

    @Test
    void createBrand_whenDuplicate_returns409() throws Exception {
        BrandRequest req = BrandRequest.builder()
                .brand("A")
                .prices(Map.of("상의", 9000))
                .build();

        doThrow(new ApiException(ErrorCode.BRAND_ALREADY_EXISTS))
                .when(brandService).createBrand(anyString(), anyMap());

        mockMvc.perform(
                        post("/api/v1/brands")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(req))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(ErrorCode.BRAND_ALREADY_EXISTS.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.BRAND_ALREADY_EXISTS.getDefaultMessage()));
    }

    /* ------------------------------------------------------------------
     *  PUT /api/v1/brands/{name}  (Update)
     * ------------------------------------------------------------------ */
    @Test
    void updateBrand_success_returns204() throws Exception {
        BrandRequest req = BrandRequest.builder()
                .brand("A")
                .prices(Map.of("상의", 11500))
                .build();

        mockMvc.perform(
                        put("/api/v1/brands/{name}", "A")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(req))
                )
                .andExpect(status().isNoContent());

        verify(brandService).updateBrand(eq("A"), anyMap());
    }

    @Test
    void updateBrand_notFound_returns404() throws Exception {
        BrandRequest req = BrandRequest.builder()
                .brand("B")
                .prices(Map.of("상의", 11000))
                .build();

        doThrow(new ApiException(ErrorCode.BRAND_NOT_FOUND))
                .when(brandService).updateBrand(anyString(), anyMap());

        mockMvc.perform(
                        put("/api/v1/brands/{name}", "B")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(req))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.BRAND_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.BRAND_NOT_FOUND.getDefaultMessage()));
    }

    /* ------------------------------------------------------------------
     *  DELETE /api/v1/brands/{name}  (Delete)
     * ------------------------------------------------------------------ */
    @Test
    void deleteBrand_success_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/brands/{name}", "A"))
                .andExpect(status().isNoContent());

        verify(brandService).deleteBrand("A");
    }

    @Test
    void deleteBrand_notFound_returns404() throws Exception {
        doThrow(new ApiException(ErrorCode.BRAND_NOT_FOUND))
                .when(brandService).deleteBrand(anyString());

        mockMvc.perform(delete("/api/v1/brands/{name}", "X"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.BRAND_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.BRAND_NOT_FOUND.getDefaultMessage()));
    }
}
