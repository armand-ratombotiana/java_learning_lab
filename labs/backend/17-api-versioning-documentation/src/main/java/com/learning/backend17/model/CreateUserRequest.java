package com.learning.backend17.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for creating a new user")
public record CreateUserRequest(
    @Schema(description = "First name", example = "John", minLength = 1, maxLength = 50)
    @NotBlank @Size(min = 1, max = 50)
    String firstName,

    @Schema(description = "Last name", example = "Doe", minLength = 1, maxLength = 50)
    @NotBlank @Size(min = 1, max = 50)
    String lastName,

    @Schema(description = "Email address", example = "john@example.com")
    @NotBlank @Email
    String email,

    @Schema(description = "Phone number", example = "+1-555-0123")
    String phone
) {}
