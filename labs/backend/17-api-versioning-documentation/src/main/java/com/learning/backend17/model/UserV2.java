package com.learning.backend17.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Enhanced user entity for V2 API")
public record UserV2(
    @Schema(description = "Unique identifier", example = "1")
    Long id,

    @Schema(description = "First name", example = "John")
    String firstName,

    @Schema(description = "Last name", example = "Doe")
    String lastName,

    @Schema(description = "Email address", example = "john@example.com")
    String email,

    @Schema(description = "Phone number", example = "+1-555-0123")
    String phone,

    @Schema(description = "Account creation timestamp")
    LocalDateTime createdAt,

    @Schema(description = "Whether the account is active", example = "true")
    boolean active
) {}
