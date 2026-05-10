package com.learning.lab.module16;

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 16: REST APIs ===");
        restPrinciplesDemo();
        httpMethodsDemo();
        statusCodesDemo();
        restControllerDemo();
        requestResponseDemo();
        errorHandlingDemo();
    }

    static void restPrinciplesDemo() {
        System.out.println("\n--- REST Principles ---");
        System.out.println("1. Uniform Interface");
        System.out.println("   - Resources identified by URIs");
        System.out.println("   - Representations (JSON, XML)");
        System.out.println("   - Hypermedia (HATEOAS)");
        System.out.println("2. Client-Server Architecture");
        System.out.println("3. Stateless");
        System.out.println("4. Cacheable");
        System.out.println("5. Layered System");
    }

    static void httpMethodsDemo() {
        System.out.println("\n--- HTTP Methods ---");
        System.out.println("GET /users - Retrieve all users");
        System.out.println("GET /users/1 - Retrieve user by ID");
        System.out.println("POST /users - Create new user");
        System.out.println("PUT /users/1 - Update user");
        System.out.println("PATCH /users/1 - Partial update");
        System.out.println("DELETE /users/1 - Delete user");
    }

    static void statusCodesDemo() {
        System.out.println("\n--- HTTP Status Codes ---");
        System.out.println("2xx - Success");
        System.out.println("  200 OK");
        System.out.println("  201 Created");
        System.out.println("  204 No Content");
        System.out.println("4xx - Client Error");
        System.out.println("  400 Bad Request");
        System.out.println("  401 Unauthorized");
        System.out.println("  403 Forbidden");
        System.out.println("  404 Not Found");
        System.out.println("5xx - Server Error");
        System.out.println("  500 Internal Server Error");
        System.out.println("  503 Service Unavailable");
    }

    static void restControllerDemo() {
        System.out.println("\n--- REST Controller Example ---");
        System.out.println("@RestController");
        System.out.println("@RequestMapping(\"/api/users\")");
        System.out.println("public class UserController {");
        System.out.println("    private final UserService service;");
        System.out.println("    public UserController(UserService service) {");
        System.out.println("        this.service = service;");
        System.out.println("    }");
        System.out.println("    @GetMapping");
        System.out.println("    public List<User> getUsers() { return service.findAll(); }");
        System.out.println("    @GetMapping(\"/{id}\")");
        System.out.println("    public User getUser(@PathVariable Long id) {");
        System.out.println("        return service.findById(id); }");
        System.out.println("    @PostMapping");
        System.out.println("    public User create(@RequestBody @Valid User user) {");
        System.out.println("        return service.save(user); }");
        System.out.println("}");
    }

    static void requestResponseDemo() {
        System.out.println("\n--- Request/Response Examples ---");
        System.out.println("POST /api/users");
        System.out.println("Request Body:");
        System.out.println("{ \"name\": \"John\", \"email\": \"john@example.com\" }");
        System.out.println("\nResponse (201 Created):");
        System.out.println("{ \"id\": 1, \"name\": \"John\", \"email\": \"john@example.com\" }");
        
        System.out.println("\nGET /api/users/1");
        System.out.println("Response (200 OK):");
        System.out.println("{ \"id\": 1, \"name\": \"John\", \"email\": \"john@example.com\" }");
        
        System.out.println("\nPUT /api/users/1");
        System.out.println("Request Body:");
        System.out.println("{ \"name\": \"John Updated\", \"email\": \"new@example.com\" }");
    }

    static void errorHandlingDemo() {
        System.out.println("\n--- Error Handling ---");
        System.out.println("@ControllerAdvice");
        System.out.println("public class GlobalExceptionHandler {");
        System.out.println("    @ExceptionHandler(ResourceNotFoundException.class)");
        System.out.println("    public ResponseEntity<ErrorResponse> handleNotFound(...) {");
        System.out.println("        return ResponseEntity.status(404)");
        System.out.println("            .body(new ErrorResponse(404, \"Not Found\"));");
        System.out.println("    }");
        System.out.println("}");
        
        System.out.println("\nError Response Format:");
        System.out.println("{ \"timestamp\": \"2024-01-01T12:00:00Z\",");
        System.out.println("  \"status\": 404,");
        System.out.println("  \"error\": \"Not Found\",");
        System.out.println("  \"message\": \"User not found with id 1\",");
        System.out.println("  \"path\": \"/api/users/1\" }");
    }
}

class User {
    private Long id;
    private String name;
    private String email;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

interface UserService {
    List<User> findAll();
    User findById(Long id);
    User save(User user);
}
