package com.musinsa.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title       = "MUSINSA Backend Assignment API",
                version     = "v1",
                description = "무신사 과제용 가격 조회/관리 API"
        )
)
public class OpenApiConfig {}
