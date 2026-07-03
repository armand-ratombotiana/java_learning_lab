# Architectural Patterns with Optional

## Repository Pattern with Optional

```java
public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    
    // Return Optional for queries that might not find results
    Optional<User> findActiveByEmail(String email);
    
    // Required values use orElseThrow at the service layer
    default User getById(Long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException(id));
    }
}
```

## Service Layer with Optional Chaining

```java
@Service
public class OrderService {
    private final UserRepository userRepo;
    private final InventoryService inventory;
    private final PaymentGateway paymentGateway;
    
    public Optional<Order> createOrder(CreateOrderRequest request) {
        return Optional.ofNullable(request)
            .filter(this::validateRequest)
            .flatMap(req -> findUser(req.userId()))
            .filter(user -> user.isActive())
            .flatMap(user -> checkInventory(request.items()))
            .flatMap(available -> processPayment(request))
            .map(this::saveOrder);
    }
    
    public OrderResult getOrderWithDetails(Long orderId) {
        return findOrder(orderId)
            .map(order -> new OrderResult(
                order,
                findUser(order.getUserId()),
                calculateTotals(order)
            ))
            .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
```

## Controller Layer with Safe Responses

```java
@RestController
public class UserController {
    
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return userService.findUser(id)
            .map(user -> ResponseEntity.ok(UserResponse.from(user)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody @Valid CreateUserRequest req) {
        return userService.createUser(req)
            .map(user -> ResponseEntity.created(
                URI.create("/users/" + user.id())).body(UserResponse.from(user)))
            .orElse(ResponseEntity.badRequest().body("User creation failed"));
    }
}
```

## Configuration Architecture

```java
@Configuration
public class AppConfig {
    
    @Bean
    public DataSource dataSource() {
        return createDataSource(
            getProperty("db.url").orElseThrow(() -> new ConfigException("db.url required")),
            getProperty("db.user").orElse("sa"),
            getProperty("db.password").orElse(""),
            getProperty("db.pool.size").flatMap(this::parseInt).orElse(10)
        );
    }
    
    private Optional<String> getProperty(String key) {
        return Optional.ofNullable(environment.getProperty(key));
    }
    
    private Optional<Integer> parseInt(String value) {
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
```

## Microservice Communication

```java
@Service
public class UserServiceClient {
    private final RestTemplate restTemplate;
    
    public Optional<UserDto> findUser(Long id) {
        try {
            return Optional.ofNullable(
                restTemplate.getForObject("/users/{id}", UserDto.class, id));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error fetching user", e);
            return Optional.empty();
        }
    }
}
```

## Anti-Patterns to Avoid

```java
// ANTI-PATTERN: Optional in collections
Map<String, Optional<String>> config;  // Wrong! Use nullable or default values

// ANTI-PATTERN: Optional as field type
public class Entity {
    private Optional<String> name;  // Wrong! Not serializable, extra overhead
}

// ANTI-PATTERN: Optional in constructor parameters
public class Service {
    public Service(Optional<Config> config) { }  // Wrong! Use overloading
}

// ANTI-PATTERN: Chaining orElse with orElse
opt.orElse(compute1()).orElse(compute2());  // Wrong! Use or() instead
```

## Best Practices

1. **Return Optional<T>** for methods that might not have a result
2. **Never return null** from an Optional-returning method
3. **Use orElseThrow()** for required values with clear exception messages
4. **Chain with or()** for fallback strategies (Java 9+)
5. **Use flatMap** to avoid nested Optional
6. **Keep Optional at the boundary**: Convert to/from Optional at API boundaries (controller, repository)
7. **Avoid Optional in internal data structures**: Use nullable fields internally, expose via Optional
8. **Prefer orElseGet over orElse** when the default is computed
