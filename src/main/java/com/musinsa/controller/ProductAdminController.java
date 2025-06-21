// src/main/java/com/musinsa/controller/ProductAdminController.java
package com.musinsa.controller;

import com.musinsa.common.ErrorResponse;
import com.musinsa.dto.CreateProductRequest;
import com.musinsa.dto.UpdateProductRequest;
import com.musinsa.dto.ProductResponse;
import com.musinsa.domain.Product;
import com.musinsa.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ProductAdmin", description = "상품 관리 API")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductAdminController {
    private final ProductService productService;

    /**
     * 개별 상품 등록
     */
    @Operation(summary = "상품 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = ProductResponse.class),
                            examples  = @ExampleObject(
                                    name  = "CreatedProduct",
                                    value = "{\n" +
                                            "  \"id\": 1,\n" +
                                            "  \"brand\": \"A\",\n" +
                                            "  \"category\": \"상의\",\n" +
                                            "  \"price\": 11200\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "브랜드 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = ErrorResponse.class),
                            examples  = @ExampleObject(
                                    name  = "BrandNotFound",
                                    value = "{\n" +
                                            "  \"status\": 404,\n" +
                                            "  \"code\": \"BRAND_NOT_FOUND\",\n" +
                                            "  \"message\": \"브랜드를 찾을 수 없습니다.\"\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "409", description = "상품 중복",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = ErrorResponse.class),
                            examples  = @ExampleObject(
                                    name  = "ProductAlreadyExists",
                                    value = "{\n" +
                                            "  \"status\": 409,\n" +
                                            "  \"code\": \"PRODUCT_ALREADY_EXISTS\",\n" +
                                            "  \"message\": \"브랜드 'A'의 카테고리 '상의' 상품이 이미 존재합니다.\"\n" +
                                            "}"
                            )
                    )
            )
    })
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "생성할 상품 정보",
                    required    = true,
                    content     = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = CreateProductRequest.class),
                            examples  = @ExampleObject(
                                    name  = "CreateRequest",
                                    value = "{\n" +
                                            "  \"brand\": \"A\",\n" +
                                            "  \"category\": \"상의\",\n" +
                                            "  \"price\": 11200\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody CreateProductRequest req
    ) {
        Product p = productService.createProduct(
                req.getBrand(),
                req.getCategory(),
                req.getPrice()
        );
        // domain.Product → dto.ProductResponse 변환
        return new ProductResponse(
                p.getId(),
                p.getBrand().getName(),
                p.getCategory(),
                p.getPrice()
        );
    }

    /**
     * 상품 목록 조회
     */
    @Operation(summary = "상품 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 리스트 반환",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = ProductResponse.class)
                    )
            )
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProductResponse> listProducts() {
        return productService.getAllProducts();
    }

    /**
     * 개별 상품 상세 조회
     */
    @Operation(summary = "상품 상세 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = ProductResponse.class),
                            examples  = @ExampleObject(
                                    name  = "ProductDetail",
                                    value = "{\n" +
                                            "  \"id\": 1,\n" +
                                            "  \"brand\": \"A\",\n" +
                                            "  \"category\": \"상의\",\n" +
                                            "  \"price\": 11200\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "상품 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = ErrorResponse.class),
                            examples  = @ExampleObject(
                                    name  = "ProductNotFound",
                                    value = "{\n" +
                                            "  \"status\": 404,\n" +
                                            "  \"code\": \"PRODUCT_NOT_FOUND\",\n" +
                                            "  \"message\": \"상품을 찾을 수 없습니다.\"\n" +
                                            "}"
                            )
                    )
            )
    })
    @GetMapping(
            value    = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ProductResponse getProduct(
            @Parameter(description = "조회할 상품 ID", example = "1")
            @PathVariable Long id
    ) {
        return productService.getProduct(id);
    }

    /**
     * 상품 가격 수정
     */
    @Operation(summary = "상품 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "수정 성공 (본문 없음)"),
            @ApiResponse(responseCode = "404", description = "상품 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = ErrorResponse.class),
                            examples  = @ExampleObject(
                                    name  = "ProductNotFound",
                                    value = "{\n" +
                                            "  \"status\": 404,\n" +
                                            "  \"code\": \"PRODUCT_NOT_FOUND\",\n" +
                                            "  \"message\": \"상품을 찾을 수 없습니다.\"\n" +
                                            "}"
                            )
                    )
            )
    })
    @PutMapping(
            value    = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProduct(
            @Parameter(description = "수정할 상품 ID", example = "1")
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 가격 정보",
                    required    = true,
                    content     = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = UpdateProductRequest.class),
                            examples  = @ExampleObject(
                                    name  = "UpdateRequest",
                                    value = "{\n" +
                                            "  \"price\": 12000\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody UpdateProductRequest req
    ) {
        productService.updateProduct(id, req.getPrice());
    }

    /**
     * 상품 삭제
     */
    @Operation(summary = "상품 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "상품 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = ErrorResponse.class),
                            examples  = @ExampleObject(
                                    name  = "ProductNotFound",
                                    value = "{\n" +
                                            "  \"status\": 404,\n" +
                                            "  \"code\": \"PRODUCT_NOT_FOUND\",\n" +
                                            "  \"message\": \"상품을 찾을 수 없습니다.\"\n" +
                                            "}"
                            )
                    )
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(
            @Parameter(description = "삭제할 상품 ID", example = "1")
            @PathVariable Long id
    ) {
        productService.deleteProduct(id);
    }
}
