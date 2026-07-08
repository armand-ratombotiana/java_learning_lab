package com.learning.backend17.controller.v2;

import com.learning.backend17.config.V2ApiMarker;
import com.learning.backend17.model.CreateUserRequest;
import com.learning.backend17.model.ErrorResponse;
import com.learning.backend17.model.UserV2;
import com.learning.backend17.service.UserServiceV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "V2 User API", description = "Enhanced user management API with additional fields")
public class UserControllerV2 implements V2ApiMarker {

    private static final Logger log = LoggerFactory.getLogger(UserControllerV2.class);
    private final UserServiceV2 userService;

    public UserControllerV2(UserServiceV2 userService) {
        this.userService = userService;
    }

    @Operation(summary = "List all users (V2)", description = "Returns enhanced user objects with phone, timestamps, and status")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserV2>> getAllUsers() {
        log.info("V2: GET /users");
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(summary = "Get user by ID (V2)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserById(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id) {
        log.info("V2: GET /users/{}", id);
        var user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, "User not found",
                    "No user with ID " + id, "/v2/users/" + id));
        }
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Create a new user (V2)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created",
            headers = @Header(name = "Location", description = "URL of created user")),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserV2> createUser(
            @Valid @RequestBody CreateUserRequest request,
            HttpServletRequest servletRequest) {
        log.info("V2: POST /users - {}", request);
        var created = userService.create(request);
        return ResponseEntity
            .created(URI.create(servletRequest.getRequestURI() + "/" + created.id()))
            .body(created);
    }

    @Operation(summary = "Update user (V2)")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody CreateUserRequest request) {
        log.info("V2: PUT /users/{} - {}", id, request);
        var updated = userService.update(id, request);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, "User not found",
                    "No user with ID " + id, "/v2/users/" + id));
        }
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete user (V2)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("V2: DELETE /users/{}", id);
        if (userService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
