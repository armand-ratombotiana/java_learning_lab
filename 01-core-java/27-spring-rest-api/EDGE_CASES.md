# Module 27: Spring REST API - Edge Cases & Pitfalls

---

## Pitfall 1: Exposing Database Entities Directly

### ❌ Wrong
Returning JPA Entities directly from REST controllers. This creates tight coupling between the database schema and the API response. It also often leads to infinite recursion during JSON serialization if there are bidirectional relationships (like `@OneToMany`).
```java
@GetMapping
public List<UserEntity> getUsers() {
    return userRepository.findAll(); // ❌ Dangerous!
}
```

### ✅ Correct
Use Data Transfer Objects (DTOs) to decouple the API contract from the database schema.
```java
@GetMapping
public List<UserDto> getUsers() {
    return userService.findAll().stream()
            .map(userMapper::toDto)
            .collect(Collectors.toList());
}
```

---

## Pitfall 2: Using the Wrong HTTP Status Codes

### ❌ Wrong
Always returning `200 OK`, even for errors, and including an "error" flag inside the JSON payload.
```java
@PostMapping
public ResponseEntity<MyResponse> create() {
    try {
        doSomething();
        return ResponseEntity.ok(new MyResponse("success"));
    } catch (Exception e) {
        return ResponseEntity.ok(new MyResponse("error: " + e.getMessage())); // ❌ Bad practice
    }
}
```

### ✅ Correct
Respect HTTP semantics. Return `201 Created` for creations, `204 No Content` for deletions, `400 Bad Request` for validation errors, and `404 Not Found` for missing resources.
```java
@PostMapping
public ResponseEntity<User> create(@RequestBody User user) {
    // Return 201 Created with Location header
}
```

---

## Pitfall 3: Not Handling Exceptions Globally

### ❌ Wrong
Using `try-catch` blocks inside every single controller method to handle exceptions. This leads to massive code duplication and inconsistent error responses across the API.

### ✅ Correct
Use `@RestControllerAdvice` (or `@ControllerAdvice`) to handle exceptions globally and return a standardized `ErrorResponse` format (e.g., following RFC 7807 Problem Details).