package com.learning.backend17.controller.v1;

import com.learning.backend17.config.V1ApiMarker;
import com.learning.backend17.model.ErrorResponse;
import com.learning.backend17.model.UserV1;
import com.learning.backend17.service.UserServiceV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
@Tag(name = "V1 User API", description = "Legacy user management API (deprecated)")
public class UserControllerV1 implements V1ApiMarker {

    private static final Logger log = LoggerFactory.getLogger(UserControllerV1.class);
    private final UserServiceV1 userService;

    public UserControllerV1(UserServiceV1 userService) {
        this.userService = userService;
    }

    @Operation(summary = "List all users (V1)", description = "Returns a list of all users. Deprecated, use V2 endpoint.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful retrieval",
            content = @Content(schema = @Schema(implementation = UserV1.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Deprecated
    public ResponseEntity<List<UserV1>> getAllUsers() {
        log.info("V1: GET /users");
        return ResponseEntity.ok()
            .header("Sunset", "Sat, 31 Dec 2025 23:59:59 GMT")
            .header("Deprecation", "true")
            .body(userService.findAll());
    }

    @Operation(summary = "Get user by ID (V1)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Deprecated
    public ResponseEntity<?> getUserById(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id) {
        log.info("V1: GET /users/{}", id);
        var user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, "User not found",
                    "No user with ID " + id, "/v1/users/" + id));
        }
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Create user (V1)")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    @Deprecated
    public ResponseEntity<UserV1> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "User data", required = true)
            @RequestBody UserV1 user,
            HttpServletRequest request) {
        log.info("V1: POST /users - {}", user);
        var created = userService.save(user);
        return ResponseEntity
            .created(URI.create(request.getRequestURI() + "/" + created.id()))
            .body(created);
    }
}
