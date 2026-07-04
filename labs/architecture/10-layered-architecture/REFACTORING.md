# Refactoring Layered Architecture

## Common Refactorings

### 1. Extract Service from Controller
```java
// BEFORE: Controller has business logic
@RestController
public class UserController {
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException();
        }
        user.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.ok(userRepository.save(user));
    }
}

// AFTER: Extract service
@RestController
public class UserController {
    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest req) {
        return ResponseEntity.ok(userService.createUser(req));
    }
}
```

### 2. Introduce DTOs
```java
// BEFORE: Exposing entity
public User getUser(Long id) { return userRepository.findById(id).get(); }

// AFTER: Return DTO
public UserResponse getUser(Long id) {
    return UserResponse.from(userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id)));
}
```

### 3. Add Layer Tests
```java
// Add ArchUnit tests to prevent layer violations
@Test
void controllersShouldOnlyDependOnServices() {
    classes().that()
        .resideInAPackage("..controller..")
        .should().onlyDependOnClassesThat()
        .resideInAnyPackage("..controller..", "..service..", "java..", "org.springframework..")
        .check(classes);
}
```

### 4. Migrate to Hexagonal
When layered architecture becomes insufficient, extract ports and adapters from the existing layers.
