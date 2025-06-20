// src/main/java/com/musinsa/controller/PriceController.java
package com.musinsa.controller;

import com.musinsa.dto.CategoryStatResponse;
import com.musinsa.dto.LowestByBrandResponse;
import com.musinsa.dto.LowestByCategoryResponse;
import com.musinsa.common.ErrorResponse;
import com.musinsa.service.PriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Price", description = "가격 조회 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    /** 카테고리별 최저가 브랜드와 가격 목록 조회 */
    @Operation(summary = "카테고리별 최저가 브랜드와 가격 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = LowestByCategoryResponse.class),
                            examples = @ExampleObject(
                                    name  = "Success200",
                                    value = "{\n" +
                                            "  \"items\": [\n" +
                                            "    {\"category\":\"상의\",\"brand\":\"C\",\"price\":10000},\n" +
                                            "    {\"category\":\"아우터\",\"brand\":\"E\",\"price\":5000}\n" +
                                            "  ],\n" +
                                            "  \"total\": 34100\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name  = "Error500",
                                    value = "{\n" +
                                            "  \"status\": 500,\n" +
                                            "  \"code\": \"INTERNAL_ERROR\",\n" +
                                            "  \"message\": \"서버 오류가 발생했습니다.\"\n" +
                                            "}"
                            )
                    )
            )
    })
    @GetMapping(
            value    = "/categories/cheapest-brands",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public LowestByCategoryResponse getCheapestBrandsPerCategory() {
        return priceService.getLowestByCategory();
    }

    /** 단일 브랜드로 전체 카테고리 최저가 번들 조회 */
    @Operation(summary = "단일 브랜드로 전체 카테고리 최저가 번들 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = LowestByBrandResponse.class),
                            examples = @ExampleObject(
                                    name  = "Success200",
                                    value = "{\n" +
                                            "  \"brand\": \"D\",\n" +
                                            "  \"categories\": [\n" +
                                            "    {\"category\":\"상의\",\"price\":11200},\n" +
                                            "    {\"category\":\"바지\",\"price\":3000}\n" +
                                            "  ],\n" +
                                            "  \"total\": 36100\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name  = "Error500",
                                    value = "{\n" +
                                            "  \"status\": 500,\n" +
                                            "  \"code\": \"INTERNAL_ERROR\",\n" +
                                            "  \"message\": \"서버 오류가 발생했습니다.\"\n" +
                                            "}"
                            )
                    )
            )
    })
    @GetMapping(
            value    = "/brands/cheapest",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public LowestByBrandResponse getCheapestBrandBundle() {
        return priceService.getLowestBySingleBrand();
    }

    /** 특정 카테고리의 최저·최고 가격 조회 */
    @Operation(summary = "특정 카테고리의 최저·최고 가격 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = CategoryStatResponse.class),
                            examples = @ExampleObject(
                                    name  = "Success200",
                                    value = "{\n" +
                                            "  \"category\": \"상의\",\n" +
                                            "  \"lowest\": [{\"brand\":\"C\",\"price\":10000}],\n" +
                                            "  \"highest\": [{\"brand\":\"I\",\"price\":11400}]\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "카테고리 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name  = "Error404",
                                    value = "{\n" +
                                            "  \"status\": 404,\n" +
                                            "  \"code\": \"CATEGORY_NOT_FOUND\",\n" +
                                            "  \"message\": \"요청하신 카테고리를 찾을 수 없습니다.\"\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name  = "Error500",
                                    value = "{\n" +
                                            "  \"status\": 500,\n" +
                                            "  \"code\": \"INTERNAL_ERROR\",\n" +
                                            "  \"message\": \"서버 오류가 발생했습니다.\"\n" +
                                            "}"
                            )
                    )
            )
    })
    @GetMapping(
            value    = "/categories/{category}/price-stats",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public CategoryStatResponse getCategoryPriceStats(
            @Parameter(
                    description = "조회할 카테고리 이름 (예: 상의)",
                    example     = "상의"
            )
            @PathVariable String category
    ) {
        return priceService.getCategoryStat(category);
    }
}
