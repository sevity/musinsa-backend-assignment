// ────────────────────────────────────────────────────────────────
// File : src/test/java/com/musinsa/controller/PriceControllerTest.java
// ────────────────────────────────────────────────────────────────
package com.musinsa.controller;

import com.musinsa.common.ApiException;
import com.musinsa.common.ErrorCode;
import com.musinsa.common.GlobalExceptionHandler;
import com.musinsa.dto.CategoryStatResponse;
import com.musinsa.dto.LowestByBrandResponse;
import com.musinsa.dto.LowestByCategoryResponse;
import com.musinsa.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PriceControllerTest {

    @Mock
    private PriceService priceService;

    @InjectMocks
    private PriceController priceController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(priceController)
                // 전역 예외 처리기 등록(에러 응답 포맷 검증용)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    /* ------------------------------------------------------------------
     *  /api/v1/categories/cheapest-brands
     * ------------------------------------------------------------------ */
    @Test
    void getCheapestBrandsPerCategory_success() throws Exception {
        LowestByCategoryResponse dummy =
                LowestByCategoryResponse.builder()
                        .total(34_100)
                        .build();

        when(priceService.getLowestByCategory()).thenReturn(dummy);

        mockMvc.perform(
                        get("/api/v1/categories/cheapest-brands")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(priceService).getLowestByCategory();
    }

    /* ------------------------------------------------------------------
     *  /api/v1/brands/cheapest
     * ------------------------------------------------------------------ */
    @Test
    void getCheapestBrandBundle_success() throws Exception {
        LowestByBrandResponse dummy =
                LowestByBrandResponse.builder()
                        .brand("D")
                        .total(36_100)
                        .build();

        when(priceService.getLowestBySingleBrand()).thenReturn(dummy);

        mockMvc.perform(
                        get("/api/v1/brands/cheapest")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(priceService).getLowestBySingleBrand();
    }

    /* ------------------------------------------------------------------
     *  /api/v1/categories/{category}/price-stats
     * ------------------------------------------------------------------ */
    @Test
    void getCategoryPriceStats_success() throws Exception {
        String category = "상의";

        CategoryStatResponse dummy =
                CategoryStatResponse.builder()
                        .category(category)
                        .build();

        when(priceService.getCategoryStat(category)).thenReturn(dummy);

        mockMvc.perform(
                        get("/api/v1/categories/{category}/price-stats", category)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(priceService).getCategoryStat(category);
    }

    @Test
    void getCategoryPriceStats_whenCategoryNotFound_returns404() throws Exception {
        String category = "없는카테고리";

        when(priceService.getCategoryStat(category))
                .thenThrow(new ApiException(ErrorCode.CATEGORY_NOT_FOUND));

        mockMvc.perform(
                        get("/api/v1/categories/{category}/price-stats", category)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.CATEGORY_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.CATEGORY_NOT_FOUND.getDefaultMessage()));

        verify(priceService).getCategoryStat(category);
    }

    @Test
    void getCategoryPriceStats_whenInvalidCategory_returns400() throws Exception {
        String invalid = "상1의";

        when(priceService.getCategoryStat(invalid))
                .thenThrow(new ApiException(ErrorCode.VALIDATION_ERROR,
                        "유효하지 않은 카테고리명입니다: " + invalid));

        mockMvc.perform(
                        get("/api/v1/categories/{category}/price-stats", invalid)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("$.message")
                        .value("유효하지 않은 카테고리명입니다: " + invalid));

        verify(priceService).getCategoryStat(invalid);
    }
}
