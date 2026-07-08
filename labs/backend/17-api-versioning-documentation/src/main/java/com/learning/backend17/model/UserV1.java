package com.learning.backend17.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User entity for V1 API")
public record UserV1(
    @Schema(description = "Unique identifier", example = "1")
    Long id,

    @Schema(description = "Full name", example = "John Doe")
    String name,

    @Schema(description = "Email address", example = "john@example.com")
    String email
) {}
