package com.musinsa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.common.ApiException;
import com.musinsa.common.ErrorCode;
import com.musinsa.common.GlobalExceptionHandler;
import com.musinsa.domain.Product;
import com.musinsa.dto.CreateProductRequest;
import com.musinsa.dto.UpdateProductRequest;
import com.musinsa.service.ProductService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductAdminControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductAdminController productAdminController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(productAdminController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    /* ------------------------------------------------------------------
     *  POST /api/v1/products
     * ------------------------------------------------------------------ */
    @Test
    void createProduct_success() throws Exception {
        // JSON 요청 바디 생성
        String json = objectMapper.writeValueAsString(
                Map.of("brand", "A", "category", "상의", "price", 11200)
        );
        // 서비스 레이어 반환용 Product 객체 생성
        Product created = new Product(null, null, 11200);
        created.setId(1L);

        when(productService.createProduct("A", "상의", 11200)).thenReturn(created);

        mockMvc.perform(
                        post("/api/v1/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.price").value(11200));

        verify(productService).createProduct("A", "상의", 11200);
    }

    @Test
    void createProduct_whenBrandNotFound_returns404() throws Exception {
        String json = objectMapper.writeValueAsString(
                Map.of("brand", "Z", "category", "상의", "price", 11200)
        );

        when(productService.createProduct("Z", "상의", 11200))
                .thenThrow(new ApiException(ErrorCode.BRAND_NOT_FOUND));

        mockMvc.perform(
                        post("/api/v1/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.BRAND_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.BRAND_NOT_FOUND.getDefaultMessage()));

        verify(productService).createProduct("Z", "상의", 11200);
    }

    /* ------------------------------------------------------------------
     *  PUT /api/v1/products/{id}
     * ------------------------------------------------------------------ */
    @Test
    void updateProduct_success() throws Exception {
        Long id = 1L;
        String json = objectMapper.writeValueAsString(Map.of("price", 12000));

        // 서비스 스텁: 아무 Product 하나 반환(컨트롤러는 무시)
        when(productService.updateProduct(id, 12000))
                .thenReturn(new Product(null, null, 12000));

        mockMvc.perform(
                        put("/api/v1/products/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isNoContent());   // 204 기대

        verify(productService).updateProduct(id, 12000);
    }



    @Test
    void updateProduct_whenNotFound_returns404() throws Exception {
        Long id = 999L;
        String json = objectMapper.writeValueAsString(Map.of("price", 12000));

        when(productService.updateProduct(id, 12000))
                .thenThrow(new ApiException(ErrorCode.PRODUCT_NOT_FOUND));

        mockMvc.perform(
                        put("/api/v1/products/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.PRODUCT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PRODUCT_NOT_FOUND.getDefaultMessage()));

        verify(productService).updateProduct(id, 12000);
    }

    /* ------------------------------------------------------------------
     *  DELETE /api/v1/products/{id}
     * ------------------------------------------------------------------ */
    @Test
    void deleteProduct_success() throws Exception {
        Long id = 1L;

        mockMvc.perform(
                        delete("/api/v1/products/{id}", id)
                )
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(id);
    }

    @Test
    void deleteProduct_whenNotFound_returns404() throws Exception {
        Long id = 999L;
        doThrow(new ApiException(ErrorCode.PRODUCT_NOT_FOUND))
                .when(productService).deleteProduct(id);

        mockMvc.perform(
                        delete("/api/v1/products/{id}", id)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.PRODUCT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.PRODUCT_NOT_FOUND.getDefaultMessage()));

        verify(productService).deleteProduct(id);
    }
}
