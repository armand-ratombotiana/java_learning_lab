# API Design - EXERCISES

## Exercise 1: Design User API
Design REST API for user management:
- List users (paginated)
- Get user by ID
- Create user
- Update user
- Delete user

## Exercise 2: Error Response Design
Design consistent error response format.

## Exercise 3: API Versioning
Implement v1 and v2 of an API with breaking changes.

---

## Solutions

### Exercise 1: User API

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @GetMapping
    public Page<UserResponse> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return userService.list(page, size);
    }
    
    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }
}
```

### Exercise 2: Error Response

```java
public record ErrorResponse(
    String code,
    String message,
    List<FieldError> details,
    String traceId
) {}

public record FieldError(String field, String message) {}
```