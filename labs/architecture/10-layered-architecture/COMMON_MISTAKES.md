# Common Mistakes in Layered Architecture

## 1. Layer Skipping
```java
// WRONG: Controller directly uses repository
@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository; // Direct persistence access!
}

// CORRECT: Controller uses service
@RestController
public class UserController {
    @Autowired
    private UserService userService; // Goes through business layer
}
```

## 2. Anemic Business Layer
```java
// WRONG: Service just passes through
@Service
public class UserService {
    public List<User> findAll() { return userRepository.findAll(); }
    public User findById(Long id) { return userRepository.findById(id).get(); }
    // No business logic!
}

// CORRECT: Service contains business rules
@Service
public class UserService {
    public User createUser(CreateUserRequest request) {
        validateEmailNotDuplicate(request.getEmail());
        validatePasswordStrength(request.getPassword());
        return userRepository.save(new User(request));
    }
}
```

## 3. Exposing Entity Directly
```java
// WRONG: Controller returns entity directly
@GetMapping("/{id}")
public User getUser(@PathVariable Long id) { // Exposes entity
    return userService.findById(id);
}

// CORRECT: Return DTO
@GetMapping("/{id}")
public UserResponse getUser(@PathVariable Long id) {
    return UserResponse.from(userService.findById(id));
}
```

## 4. Business Logic in Entity
```java
// WRONG: Entity contains business logic
@Entity
public class User {
    public boolean canPlaceOrder() { // Business rule in entity!
        return orderCount < limit;
    }
}

// OK in Domain-Driven Design, but in traditional layered architecture,
// business logic belongs in the service layer
```
