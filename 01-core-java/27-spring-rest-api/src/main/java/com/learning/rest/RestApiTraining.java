package com.learning.rest;

import java.util.*;

public class RestApiTraining {

    public static void main(String[] args) {
        System.out.println("=== Spring REST API Training ===");

        demonstrateHttpMethods();
        demonstrateRestAnnotations();
        demonstrateExceptionHandling();
        demonstrateBestPractices();
    }

    private static void demonstrateHttpMethods() {
        System.out.println("\n--- HTTP Methods in REST ---");

        String[] methods = {
            "GET /users - retrieve all users",
            "GET /users/1 - retrieve user by ID",
            "POST /users - create new user",
            "PUT /users/1 - update user completely",
            "PATCH /users/1 - partial update",
            "DELETE /users/1 - delete user"
        };

        for (String m : methods) System.out.println("  " + m);

        System.out.println("\nResponse Status Codes:");
        Map<String, String> statusCodes = new LinkedHashMap<>();
        statusCodes.put("200 OK", "Successful GET/PUT/PATCH");
        statusCodes.put("201 Created", "Successful POST");
        statusCodes.put("204 No Content", "Successful DELETE");
        statusCodes.put("400 Bad Request", "Invalid input");
        statusCodes.put("401 Unauthorized", "Authentication required");
        statusCodes.put("403 Forbidden", "Access denied");
        statusCodes.put("404 Not Found", "Resource not found");
        statusCodes.put("500 Internal Server Error", "Server error");

        statusCodes.forEach((k, v) -> System.out.printf("  %s: %s%n", k, v));
    }

    private static void demonstrateRestAnnotations() {
        System.out.println("\n--- Spring REST Annotations ---");

        String[] annotations = {
            "@RestController - combines @Controller + @ResponseBody",
            "@GetMapping - handle GET requests",
            "@PostMapping - handle POST requests",
            "@PutMapping - handle PUT requests",
            "@PatchMapping - handle PATCH requests",
            "@DeleteMapping - handle DELETE requests",
            "@RequestMapping - generic request mapping",
            "@PathVariable - extract URL template variables",
            "@RequestParam - extract query parameters",
            "@RequestBody - bind HTTP request body"
        };

        for (String a : annotations) System.out.println("  " + a);

        System.out.println("\nController Example:");
        String example = """
            @RestController
            @RequestMapping("/api/users")
            public class UserController {
                
                @GetMapping
                public List<User> getAllUsers() { ... }
                
                @GetMapping("/{id}")
                public User getUser(@PathVariable Long id) { ... }
                
                @PostMapping
                public ResponseEntity<User> create(
                        @RequestBody @Valid User user) { ... }
            }""";
        System.out.println(example);
    }

    private static void demonstrateExceptionHandling() {
        System.out.println("\n--- Exception Handling ---");

        String[] handlers = {
            "@ControllerAdvice - global exception handling",
            "@ExceptionHandler - specific exception handling",
            "@ResponseStatus - HTTP status for exceptions",
            "@ResponseEntity - customize response"
        };

        for (String h : handlers) System.out.println("  " + h);

        System.out.println("\nException Handler Example:");
        String example = """
            @ControllerAdvice
            public class GlobalExceptionHandler {
                
                @ExceptionHandler(ResourceNotFoundException.class)
                public ResponseEntity<ErrorResponse> handleNotFound(
                        ResourceNotFoundException ex) {
                    return ResponseEntity.status(404)
                        .body(new ErrorResponse(ex.getMessage()));
                }
            }""";
        System.out.println(example);
    }

    private static void demonstrateBestPractices() {
        System.out.println("\n--- REST API Best Practices ---");

        String[] practices = {
            "Use nouns for resources (/users, /orders)",
            "Use plural forms (/users not /user)",
            "Version APIs (/api/v1/users)",
            "Use proper HTTP methods and status codes",
            "Implement pagination (/users?page=1&size=10)",
            "Support filtering and sorting",
            "Use HATEOAS for discoverability",
            "Document with OpenAPI/Swagger",
            "Secure all endpoints",
            "Version APIs for backward compatibility"
        };

        for (String p : practices) System.out.println("  " + p);

        System.out.println("\nAPI Documentation Tools:");
        String[] docs = {"Swagger UI", "OpenAPI 3.0", "SpringDoc OpenAPI", "Redoc"};
        for (String d : docs) System.out.println("  - " + d);
    }
}