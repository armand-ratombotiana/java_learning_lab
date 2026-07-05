package com.learning.backend12;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Data Transfer Object for User — documented with @Schema.
 *
 * @Schema provides rich metadata about the data model in the OpenAPI spec.
 * Swagger UI displays these descriptions and constraints.
 */
@Schema(description = "User data transfer object representing a registered user")
public class UserDto {

    @Schema(description = "Unique user identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    @Schema(description = "Full name of the user", example = "John Doe", minLength = 2, maxLength = 50)
    private String name;

    @NotBlank
    @Email
    @Schema(description = "Email address (must be unique)", example = "john.doe@example.com")
    private String email;

    @Past
    @Schema(description = "Date of birth (must be in the past)", example = "1990-01-15")
    private LocalDate birthDate;

    @Schema(description = "Whether the user account is active", example = "true", defaultValue = "true")
    private boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
