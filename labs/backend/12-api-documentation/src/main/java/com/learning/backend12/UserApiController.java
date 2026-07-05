package com.learning.backend12;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * REST controller with OpenAPI documentation annotations.
 *
 * @Tag groups operations in the OpenAPI spec.
 * @Operation describes a single API operation.
 * @ApiResponse documents possible responses.
 * @Parameter documents path/query parameters.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "CRUD operations for user management")
public class UserApiController {

    private static final Logger log = LoggerFactory.getLogger(UserApiController.class);
    private final Map<Long, UserDto> users = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @Operation(summary = "Get all users", description = "Returns a list of all registered users")
    @ApiResponse(responseCode = "200", description = "List of users retrieved successfully")
    @GetMapping
    public java.util.Collection<UserDto> getAllUsers() {
        log.info("GET /api/users");
        return users.values();
    }

    @Operation(summary = "Get user by ID", description = "Returns a single user by their unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Long id) {
        log.info("GET /api/users/{}", id);
        UserDto user = users.get(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Create a new user", description = "Creates a new user and returns the created entity")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "User data to create", required = true,
                content = @Content(schema = @Schema(implementation = UserDto.class)))
            @Valid @RequestBody UserDto userDto) {
        log.info("POST /api/users with body: {}", userDto);
        userDto.setId(idGen.getAndIncrement());
        users.put(userDto.getId(), userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @Operation(summary = "Delete a user", description = "Removes a user by their ID")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/users/{}", id);
        users.remove(id);
        return ResponseEntity.noContent().build();
    }
}
