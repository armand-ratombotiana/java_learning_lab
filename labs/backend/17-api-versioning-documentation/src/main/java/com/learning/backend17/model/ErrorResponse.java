package com.learning.backend17.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Standard error response")
public record ErrorResponse(
    @Schema(description = "HTTP status code", example = "400")
    int status,

    @Schema(description = "Error message", example = "Invalid input")
    String message,

    @Schema(description = "Detailed error description")
    String detail,

    @Schema(description = "Request path that caused the error", example = "/v2/users")
    String path,

    @Schema(description = "Timestamp when the error occurred")
    LocalDateTime timestamp
) {
    public ErrorResponse(int status, String message, String detail, String path) {
        this(status, message, detail, path, LocalDateTime.now());
    }
}
