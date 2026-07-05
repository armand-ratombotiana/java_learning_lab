package com.learning.backend02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * REST controller demonstrating CRUD operations with standard HTTP mappings.
 *
 * Key annotations:
 * - @GetMapping: maps HTTP GET requests
 * - @PostMapping: maps HTTP POST requests
 * - @PutMapping: maps HTTP PUT requests
 * - @DeleteMapping: maps HTTP DELETE requests
 * - @RequestBody: binds the HTTP request body to a method parameter
 * - @PathVariable: extracts values from URI template variables
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public java.util.List<User> getAllUsers() {
        log.info("GET /api/users");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("GET /api/users/{}", id);
        return userService.findById(id);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("POST /api/users with body: {}", request);
        User user = new User(null, request.name(), request.email());
        User saved = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @Valid @RequestBody CreateUserRequest request) {
        log.info("PUT /api/users/{} with body: {}", id, request);
        User updated = new User(id, request.name(), request.email());
        return userService.update(id, updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/users/{}", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Inner record class for the create/update request body.
     * Using Jakarta Bean Validation annotations (@NotBlank, @Email).
     */
    public record CreateUserRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank @Email(message = "Email must be valid") String email
    ) {}
}
