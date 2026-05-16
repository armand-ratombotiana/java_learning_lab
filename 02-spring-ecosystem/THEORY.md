# Spring Ecosystem Theory

## Table of Contents
1. [Spring Core](#spring-core)
2. [Spring MVC](#spring-mvc)
3. [Spring Data](#spring-data)

---

## 1. Spring Core

### 1.1 Introduction to Spring Framework

Spring is a comprehensive enterprise application framework that provides:
- **Inversion of Control (IoC)** - Container manages object lifecycle
- **Dependency Injection (DI)** - Objects receive dependencies rather than creating them
- **Aspect-Oriented Programming (AOP)** - Cross-cutting concerns separation
- **Transaction Management** - Declarative transaction handling

### 1.2 IoC Container and Bean Lifecycle

The Spring IoC container is the core of the framework:

```java
public class ApplicationContextDemo {
    public static void main(String[] args) {
        // AnnotationConfigApplicationContext scans packages
        var context = new AnnotationConfigApplicationContext(
            "com.example.config"
        );
        
        // Get bean by type or name
        UserService userService = context.getBean(UserService.class);
        
        context.close();
    }
}
```

**Bean Lifecycle Phases:**
1. **Instantiation** - Object is created
2. **Populate** - Properties and dependencies injected
3. **Initialization** - `InitializingBean.afterPropertiesSet()` or `@PostConstruct`
4. **Usage** - Bean is ready for use
5. **Destruction** - `DisposableBean.destroy()` or `@PreDestroy`

### 1.3 Dependency Injection Types

**Constructor Injection (Preferred):**
```java
@Component
public class OrderService {
    private final PaymentGateway paymentGateway;
    private final OrderRepository orderRepository;
    
    @Autowired
    public OrderService(PaymentGateway paymentGateway, 
                       OrderRepository orderRepository) {
        this.paymentGateway = paymentGateway;
        this.orderRepository = orderRepository;
    }
}
```

**Setter Injection:**
```java
@Component
public class NotificationService {
    private EmailService emailService;
    
    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
```

**Field Injection (Not Recommended):**
```java
@Component
public class BadExample {
    @Autowired
    private SomeDependency dependency; // Avoid this
}
```

### 1.4 Spring Configuration

**Java Configuration:**
```java
@Configuration
public class AppConfig {
    
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
        config.setUsername("user");
        config.setPassword("password");
        return new HikariDataSource(config);
    }
    
    @Bean
    public PlatformTransactionManager transactionManager(DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }
}
```

**Component Scanning:**
```java
@Configuration
@ComponentScan(basePackages = {
    "com.example.service",
    "com.example.repository",
    "com.example.controller"
})
public class AppConfig {}
```

### 1.5 Spring Profiles

```java
@Configuration
public class DataSourceConfig {
    
    @Bean
    @Profile("dev")
    public DataSource devDataSource() {
        return new EmbeddedDatabaseBuilder().build();
    }
    
    @Bean
    @Profile("prod")
    public DataSource prodDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(System.getenv("DB_URL"));
        return new HikariDataSource(config);
    }
}
```

### 1.6 AOP (Aspect-Oriented Programming)

```java
@Aspect
@Component
public class LoggingAspect {
    
    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Executing: " + joinPoint.getSignature());
    }
    
    @AfterReturning(pointcut = "execution(* *..UserService.create(..))", 
                    returning = "result")
    public void logAfter(Object result) {
        System.out.println("Method returned: " + result);
    }
    
    @Around("@annotation(Timed)")
    public Object measureTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.nanoTime();
        Object result = pjp.proceed();
        long duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println(pjp.getSignature() + " took " + duration + "ms");
        return result;
    }
}
```

---

## 2. Spring MVC

### 2.1 Request Flow

```
Request → DispatcherServlet → HandlerMapping → Controller
    → Service → Repository → Database
    → ViewResolver → View → Response
```

### 2.2 Controller Basics

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return userService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto dto) {
        UserDto created = userService.create(dto);
        return ResponseEntity
            .created(URI.create("/api/users/" + created.getId()))
            .body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

### 2.3 Request Parameters

```java
@GetMapping("/search")
public ResponseEntity<List<UserDto>> search(
        @RequestParam String query,
        @RequestParam(defaultValue = "20") int limit,
        @RequestParam(required = false) String role,
        @RequestParam(defaultValue = "id") String sortBy) {
    
    return ResponseEntity.ok(userService.search(query, limit, role, sortBy));
}
```

### 2.4 Request Body Validation

```java
public record CreateUserRequest(
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50) String username,
    
    @NotBlank(message = "Email is required")
    @Email String email,
    
    @NotNull @Min(18) @Max(150) Integer age,
    
    @Valid AddressDto address
) {}

@PostMapping
public ResponseEntity<UserDto> create(@Valid @RequestBody CreateUserRequest req) {
    return ResponseEntity.ok(userService.create(req));
}
```

### 2.5 Exception Handling

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(404)
            .body(new ErrorResponse(404, ex.getMessage()));
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(400, ex.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(500)
            .body(new ErrorResponse(500, "Internal server error"));
    }
}
```

### 2.6 REST Response Wrapping

```java
public record ApiResponse<T>(
    boolean success,
    T data,
    String message,
    LocalDateTime timestamp
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, LocalDateTime.now());
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message, LocalDateTime.now());
    }
}
```

### 2.7 File Upload/Download

```java
@PostMapping("/upload")
public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file) {
    if (file.isEmpty()) {
        return ResponseEntity.badRequest().body("File is empty");
    }
    
    Path path = Path.of("uploads", file.getOriginalFilename());
    Files.writeString(path, file.getBytes());
    
    return ResponseEntity.ok("File uploaded: " + file.getOriginalFilename());
}

@GetMapping("/download/{filename}")
public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
    Path path = Path.of("uploads", filename);
    Resource resource = new UrlResource(path.toUri());
    
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"" + filename + "\"")
        .body(resource);
}
```

---

## 3. Spring Data

### 3.1 Spring Data JPA

**Repository Interface:**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Method queries
    Optional<User> findByEmail(String email);
    
    List<User> findByRole(String role);
    
    List<User> findByRoleAndActiveTrue(String role);
    
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailCustom(@Param("email") String email);
    
    @Query(value = "SELECT * FROM users WHERE email = :email", 
           nativeQuery = true)
    Optional<User> findByEmailNative(@Param("email") String email);
    
    @Modifying
    @Query("UPDATE User u SET u.active = false WHERE u.lastLogin < :date")
    int deactivateOldUsers(@Param("date") LocalDate date);
    
    // Pagination and Sorting
    Page<User> findByRole(String role, Pageable pageable);
    
    // Custom repository implementation
}
```

**Custom Repository:**
```java
public interface UserRepositoryCustom {
    List<User> findUsersWithHighOrders(int minOrders);
}

public class UserRepositoryImpl implements UserRepositoryCustom {
    
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public List<User> findUsersWithHighOrders(int minOrders) {
        return em.createQuery(
            "SELECT u FROM User u JOIN u.orders o GROUP BY u " +
            "HAVING COUNT(o) >= :min", User.class)
            .setParameter("min", minOrders)
            .getResultList();
    }
}
```

### 3.2 Transactions

```java
@Service
public class OrderService {
    
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(CreateOrderRequest request) {
        // This method runs in a transaction
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new NotFoundException("User not found"));
        
        Order order = new Order();
        order.setUser(user);
        order.setItems(request.getItems());
        
        Order saved = orderRepository.save(order);
        
        // If this fails, entire transaction rolls back
        notificationService.sendOrderConfirmation(user, saved);
        
        return saved;
    }
    
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
```

**Propagation Levels:**
- `REQUIRED` (default) - Use existing transaction or create new
- `REQUIRES_NEW` - Always create new transaction
- `SUPPORTS` - Execute in transaction if exists, otherwise not
- `NOT_SUPPORTED` - Execute without transaction
- `MANDATORY` - Must be in transaction, throw exception otherwise
- `NEVER` - Must NOT be in transaction, throw exception otherwise
- `NESTED` - Use savepoint if transaction exists

### 3.3 Spring Data JDBC

```java
@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
    
    Optional<Account> findByAccountNumber(String accountNumber);
    
    void deleteByAccountNumber(String accountNumber);
}

// Entity with simple mapping (no complex relationships managed by Spring Data)
@Entity
public class Account {
    @Id
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    
    // getters and setters
}
```

### 3.4 Spring Data Redis

```java
@Repository
public class RedisCacheRepository {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public void cacheUser(User user) {
        redisTemplate.opsForValue().set(
            "user:" + user.getId(), 
            user, 
            Duration.ofHours(1)
        );
    }
    
    public Optional<User> getCachedUser(Long id) {
        Object cached = redisTemplate.opsForValue().get("user:" + id);
        return Optional.ofNullable((User) cached);
    }
    
    public void incrementViewCount(String key) {
        redisTemplate.opsForValue().increment(key);
    }
    
    public void addToSet(String key, String... members) {
        redisTemplate.opsForSet().add(key, members);
    }
    
    public Set<Object> getSetMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }
}
```

### 3.5 Spring Data MongoDB

```java
@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
    List<Product> findByCategory(String category);
    
    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);
    
    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<Product> searchByName(String namePattern);
    
    // Projections
    interface ProductSummary {
        String getName();
        BigDecimal getPrice();
    }
    
    List<ProductSummary> findProjectedByCategory(String category);
}
```

---

## Key Concepts Summary

| Concept | Description |
|---------|-------------|
| IoC Container | Manages bean creation and lifecycle |
| Dependency Injection | Injects dependencies rather than creating them |
| Bean Scopes | singleton, prototype, request, session, application |
| AOP | Cross-cutting concerns (logging, security, transactions) |
| Spring MVC | Web framework based on DispatcherServlet |
| Spring Data | Unified data access abstraction |
| @Transactional | Declarative transaction management |
| @ComponentScan | Auto-discovers Spring-managed beans |
| @EnableAutoConfiguration | Spring Boot auto-configuration |