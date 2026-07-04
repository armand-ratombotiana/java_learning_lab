# Step by Step: Building a REST API

## Step 1: Define the Model
```java
public class User {
    private Long id;
    private String name;
    private String email;
    // getters/setters
}
```

## Step 2: Create Service Layer
```java
@Service
public class UserService {
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
```

## Step 3: Create REST Controller
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping
    public List<User> getAll() { return userService.findAll(); }
}
```

## Step 4: Add Exception Handling
```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
```

## Step 5: Test with curl
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John","email":"john@test.com"}'

curl http://localhost:8080/api/users
curl http://localhost:8080/api/users/1
```
