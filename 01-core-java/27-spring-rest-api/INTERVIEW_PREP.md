# Module 27: Spring REST API - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the difference between `@Controller` and `@RestController`?
**Answer**:
- `@Controller` is used to mark a class as a Spring MVC controller. By default, its methods return a `String` which the `ViewResolver` interprets as a view name (like a Thymeleaf template or JSP file) to render HTML.
- `@RestController` is a convenience annotation introduced in Spring 4.0. It is a combination of `@Controller` and `@ResponseBody`. When applied to a class, every method inherits the `@ResponseBody` behavior, meaning the returned object is automatically serialized into JSON (or XML) and written directly to the HTTP response body, bypassing the view resolution process entirely.

### Q2: Why is it bad practice to return Database Entities directly from a REST Controller?
**Answer**:
1. **Tight Coupling**: Exposing the DB entity directly couples the API contract to the database schema. If you change a database column name, your API response changes, breaking clients.
2. **Security Issues**: You might accidentally leak sensitive information (like password hashes or internal IDs) if you forget to add `@JsonIgnore` annotations on the entity.
3. **Infinite Recursion / Lazy Initialization Errors**: Entities often have relational mappings (like `@OneToMany`). Serializing them directly via Jackson can trigger an infinite loop of serialization (A holds B, B holds A) or throw a `LazyInitializationException` if the JSON serializer tries to access a lazy-loaded collection outside of an active Hibernate transaction.
*Best Practice*: Always map Database Entities to Data Transfer Objects (DTOs) tailored specifically for the API response.

### Q3: How do you handle exceptions globally in a Spring REST application?
**Answer**:
You use the `@RestControllerAdvice` (or `@ControllerAdvice`) annotation. 
By creating a dedicated class with this annotation, you can define methods annotated with `@ExceptionHandler(CustomException.class)`. Whenever `CustomException` is thrown anywhere in the application (Controllers, Services, Repositories), Spring intercepts it and routes it to your handler method. 
This centralizes error handling, preventing repetitive `try-catch` blocks in every controller method and ensuring consistent, standardized error payloads (e.g., following RFC 7807) are returned to the API clients.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Proper HTTP Semantics
**Problem**: A junior developer wrote this endpoint. What are the three primary REST/HTTP best-practice violations here, and how do you fix them?

```java
@RequestMapping(value = "/api/createUser", method = RequestMethod.POST)
public User createUser(@RequestBody User user) {
    if (user.getName() == null) {
        throw new RuntimeException("Name required");
    }
    return userService.save(user);
}
```

**Solution**:
1. **Violations**:
   - **URI Naming**: `/createUser` is a verb. REST URIs should be nouns (`/api/users`). The HTTP method (`POST`) inherently implies "create".
   - **Status Codes**: When creating a resource, the server should return a `201 Created` status code and a `Location` header pointing to the new resource, not the default `200 OK`.
   - **Validation & Error Handling**: Throwing a raw `RuntimeException` will result in a generic `500 Internal Server Error` and a messy stack trace to the client. Missing data should trigger a `400 Bad Request`.

2. **Refactored Code**:
```java
@PostMapping("/api/users") // Noun URI
public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto dto) { // DTO + Validation
    UserDto saved = userService.save(dto);
    
    // Create Location header
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(saved.getId())
        .toUri();
        
    // Return 201 Created
    return ResponseEntity.created(location).body(saved); 
}

// Ensure @RestControllerAdvice is set up elsewhere to handle MethodArgumentNotValidException!
```