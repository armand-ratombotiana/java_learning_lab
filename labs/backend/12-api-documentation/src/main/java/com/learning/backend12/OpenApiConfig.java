package com.learning.backend12;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI documentation configuration.
 *
 * Springdoc-openapi automatically generates OpenAPI 3.0 specs from
 * annotations (@Operation, @Schema, etc.) and exposes them at /v3/api-docs
 * with a Swagger UI at /swagger-ui.html.
 *
 * This bean customizes the generated OpenAPI spec with metadata.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Backend Academy - API Documentation Lab")
                .description("REST API demonstrating OpenAPI 3.0 annotations with Springdoc.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Backend Academy")
                    .email("learn@backendacademy.dev")
                    .url("https://backendacademy.dev"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Development Server"),
                new Server().url("https://api.example.com").description("Production Server")
            ));
    }
}
