package com.learning.backend17.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new io.swagger.v3.oas.models.info.Info()
                .title("API Versioning Lab")
                .version("2.0.0")
                .description("Demonstrates URL-based, header-based, and content negotiation versioning"))
            .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
            .components(new Components()
                .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Provide a valid JWT token"))
                .addParameters("apiVersionHeader", new Parameter()
                    .in("header")
                    .name("Accept-Version")
                    .schema(new StringSchema())
                    .description("API version to use (e.g., '1', '2')")));
    }

    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
            .group("v1")
            .displayName("V1 API")
            .pathsToMatch("/v1/**")
            .addOpenApiCustomizer(openApi -> openApi
                .info(new io.swagger.v3.oas.models.info.Info()
                    .title("V1 API")
                    .version("1.0.0")
                    .description("Legacy API version 1.0. Deprecated. Use V2 instead.")))
            .build();
    }

    @Bean
    public GroupedOpenApi v2Api() {
        return GroupedOpenApi.builder()
            .group("v2")
            .displayName("V2 API")
            .pathsToMatch("/v2/**")
            .addOpenApiCustomizer(openApi -> openApi
                .info(new io.swagger.v3.oas.models.info.Info()
                    .title("V2 API")
                    .version("2.0.0")
                    .description("Current API version with enhanced features")))
            .build();
    }
}
