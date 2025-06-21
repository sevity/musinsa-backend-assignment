// src/main/java/com/musinsa/controller/BrandAdminController.java
package com.musinsa.controller;

import com.musinsa.common.ErrorResponse;
import com.musinsa.dto.BrandRequest;
import com.musinsa.service.BrandService;
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

@Tag(name = "BrandAdmin", description = "브랜드 관리 API")
@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandAdminController {

    private final BrandService brandService;

    /**
     * 신규 브랜드 등록
     */
    @Operation(summary = "브랜드 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "브랜드 생성 성공"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 브랜드",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = ErrorResponse.class),
                            examples  = @ExampleObject(
                                    name  = "Conflict409",
                                    value = "{\n" +
                                            "  \"status\": 409,\n" +
                                            "  \"code\": \"BRAND_ALREADY_EXISTS\",\n" +
                                            "  \"message\": \"이미 존재하는 브랜드입니다.\"\n" +
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
    public void createBrand(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "등록할 브랜드 정보",
                    required    = true,
                    content     = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = BrandRequest.class),
                            examples  = @ExampleObject(
                                    name  = "CreateBrand",
                                    value = "{\n" +
                                            "  \"brand\": \"Z\",\n" +
                                            "  \"prices\": {\n" +
                                            "    \"상의\": 10000,\n" +
                                            "    \"아우터\": 5000,\n" +
                                            "    \"바지\": 3000\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody BrandRequest req
    ) {
        brandService.createBrand(req.getBrand(), req.getPrices());
    }

    /**
     * 브랜드 이름 리스트 조회
     */
    @Operation(summary = "브랜드 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "브랜드 리스트 반환")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> listBrands() {
        return brandService.getAllBrandNames();
    }

    /**
     * 브랜드 상세(가격 맵) 조회
     */
    @Operation(summary = "브랜드 상세 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BrandRequest.class))),
            @ApiResponse(responseCode = "404", description = "브랜드 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(
            value    = "/{name}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public BrandRequest getBrand(
            @Parameter(description = "조회할 브랜드 이름", example = "Z")
            @PathVariable String name
    ) {
        return brandService.getBrand(name);
    }

    /**
     * 기존 브랜드 전체 정보 수정
     */
    @Operation(summary = "브랜드 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "브랜드 수정 성공"),
            @ApiResponse(responseCode = "404", description = "브랜드 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = ErrorResponse.class),
                            examples  = @ExampleObject(
                                    name  = "NotFound404",
                                    value = "{\n" +
                                            "  \"status\": 404,\n" +
                                            "  \"code\": \"BRAND_NOT_FOUND\",\n" +
                                            "  \"message\": \"브랜드를 찾을 수 없습니다.\"\n" +
                                            "}"
                            )
                    )
            )
    })
    @PutMapping(
            value    = "/{name}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBrand(
            @Parameter(description = "수정할 브랜드 이름", example = "A")
            @PathVariable String name,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 브랜드 정보",
                    required    = true,
                    content     = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = BrandRequest.class),
                            examples  = @ExampleObject(
                                    name  = "UpdateBrand",
                                    value = "{\n" +
                                            "  \"prices\": {\n" +
                                            "    \"상의\": 11500,\n" +
                                            "    \"아우터\": 5200\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody BrandRequest req
    ) {
        brandService.updateBrand(name, req.getPrices());
    }

    /**
     * 브랜드 삭제
     */
    @Operation(summary = "브랜드 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "브랜드 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "브랜드 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = ErrorResponse.class),
                            examples  = @ExampleObject(
                                    name  = "NotFound404",
                                    value = "{\n" +
                                            "  \"status\": 404,\n" +
                                            "  \"code\": \"BRAND_NOT_FOUND\",\n" +
                                            "  \"message\": \"브랜드를 찾을 수 없습니다.\"\n" +
                                            "}"
                            )
                    )
            )
    })
    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBrand(
            @Parameter(description = "삭제할 브랜드 이름", example = "A")
            @PathVariable String name
    ) {
        brandService.deleteBrand(name);
    }
}
