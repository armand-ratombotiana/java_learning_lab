# Module 27: Spring REST API - Deep Dive

**Difficulty Level**: Intermediate to Advanced  
**Prerequisites**: Modules 01-26, specifically Module 25 (Spring Boot Basics)  
**Estimated Reading Time**: 75 minutes  

---

## 📚 Table of Contents

1. [Introduction to REST in Spring](#intro)
2. [Controllers and Request Mappings](#controllers)
3. [Request Parameters and Path Variables](#params)
4. [Request and Response Bodies](#bodies)
5. [Exception Handling (@ControllerAdvice)](#exception)
6. [ResponseEntity & Status Codes](#response-entity)
7. [Content Negotiation & Media Types](#content-neg)

---

## 1. Introduction to REST in Spring <a name="intro"></a>
Spring MVC provides powerful annotations to create RESTful web services effortlessly. Using `@RestController`, Spring automatically handles the serialization of Java objects to JSON (or XML) and vice versa, typically via Jackson.

---

## 2. Controllers and Request Mappings <a name="controllers"></a>
The `@RestController` annotation combines `@Controller` and `@ResponseBody`. The `@RequestMapping` defines the base path.

```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }
}
```

---

## 3. Request Parameters and Path Variables <a name="params"></a>
- **@PathVariable**: Extracts values from the URI path.
- **@RequestParam**: Extracts query parameters.

```java
// GET /api/v1/users/123
@GetMapping("/{id}")
public User getUserById(@PathVariable("id") Long id) {
    return userService.findById(id);
}

// GET /api/v1/users/search?name=John&active=true
@GetMapping("/search")
public List<User> searchUsers(
        @RequestParam(name = "name") String name,
        @RequestParam(name = "active", defaultValue = "true") boolean isActive) {
    return userService.search(name, isActive);
}
```

---

## 4. Request and Response Bodies <a name="bodies"></a>
`@RequestBody` binds the HTTP request body to a domain object. Validation can be applied using `@Valid`.

```java
@PostMapping
public User createUser(@Valid @RequestBody UserDto userDto) {
    return userService.create(userDto);
}
```

---

## 5. Exception Handling (@ControllerAdvice) <a name="exception"></a>
Global exception handling separates error-handling logic from business logic.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(UserNotFoundException ex) {
        return new ErrorResponse("USER_NOT_FOUND", ex.getMessage());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        return new ErrorResponse("VALIDATION_FAILED", "Invalid input data");
    }
}
```

---

## 6. ResponseEntity & Status Codes <a name="response-entity"></a>
Use `ResponseEntity` for full control over the HTTP response (status code, headers, and body).

```java
@PostMapping
public ResponseEntity<User> create(@RequestBody User user) {
    User savedUser = userService.save(user);
    URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedUser.getId())
            .toUri();
            
    return ResponseEntity.created(location).body(savedUser); // Returns 201 Created
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.noContent().build(); // Returns 204 No Content
}
```

---

## 7. Content Negotiation & Media Types <a name="content-neg"></a>
Define what content type an endpoint consumes and produces. By default, it's `application/json`.

```java
@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
public User processXml(@RequestBody User user) {
    return user;
}
```