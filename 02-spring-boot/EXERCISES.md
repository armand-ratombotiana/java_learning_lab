# Spring Boot Exercises

## Exercise 1: Create a Basic Spring Boot Application

### Task
Create a Spring Boot application that exposes a REST endpoint returning a simple greeting message.

### Solution

```java
// GreetingController.java
@RestController
public class GreetingController {
    
    @GetMapping("/greeting")
    public String greeting() {
        return "Hello, Spring Boot!";
    }
}

// Application.java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### Verification
```bash
curl http://localhost:8080/greeting
# Output: Hello, Spring Boot!
```

---

## Exercise 2: Custom Configuration Properties

### Task
Create a configuration class that binds properties from `application.yml` and expose them via REST endpoint.

### Solution

```java
// AppProperties.java
@ConfigurationProperties(prefix = "app")
public record AppProperties(String name, String version, int maxConnections) {}

// Config.java
@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class Config {}

// Controller.java
@RestController
public class InfoController {
    
    private final AppProperties properties;
    
    public InfoController(AppProperties properties) {
        this.properties = properties;
    }
    
    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        return Map.of(
            "name", properties.name(),
            "version", properties.version(),
            "maxConnections", properties.maxConnections()
        );
    }
}
```

### Configuration (application.yml)
```yaml
app:
  name: MyApplication
  version: 1.0.0
  max-connections: 100
```

---

## Exercise 3: Custom Health Indicator

### Task
Implement a custom health indicator that checks if the application can connect to a simulated service.

### Solution

```java
// ServiceHealthIndicator.java
@Component
public class ServiceHealthIndicator implements HealthIndicator {
    
    private final Map<String, Boolean> services = new ConcurrentHashMap<>();
    
    public ServiceHealthIndicator() {
        services.put("database", true);
        services.put("cache", true);
    }
    
    @Override
    public Health health() {
        List<String> downServices = services.entrySet().stream()
            .filter(e -> !e.getValue())
            .map(Map.Entry::getKey)
            .toList();
        
        if (downServices.isEmpty()) {
            return Health.up()
                .withDetail("services", "All operational")
                .build();
        }
        
        return Health.down()
            .withDetail("downServices", downServices)
            .build();
    }
    
    public void setServiceStatus(String service, boolean up) {
        services.put(service, up);
    }
}

// Test controller to toggle service status
@RestController
public class HealthTestController {
    
    private final ServiceHealthIndicator indicator;
    
    public HealthTestController(ServiceHealthIndicator indicator) {
        this.indicator = indicator;
    }
    
    @PostMapping("/admin/service/{name}/status")
    public String setServiceStatus(@PathVariable String name, @RequestParam boolean up) {
        indicator.setServiceStatus(name, up);
        return "Service " + name + " status set to " + (up ? "UP" : "DOWN");
    }
}
```

### Verification
```bash
curl http://localhost:8080/actuator/health
```

---

## Exercise 4: Profile-Based Configuration

### Task
Create different configurations for development and production environments.

### Solution

```java
// DatasourceConfig.java
public interface DatasourceConfig {
    String getUrl();
    int getMaxConnections();
}

// DevDatasourceConfig.java
@Profile("dev")
@Configuration
public class DevDatasourceConfig implements DatasourceConfig {
    
    @Override
    public String getUrl() {
        return "jdbc:h2:mem:devdb";
    }
    
    @Override
    public int getMaxConnections() {
        return 5;
    }
}

// ProdDatasourceConfig.java
@Profile("prod")
@Configuration
public class ProdDatasourceConfig implements DatasourceConfig {
    
    @Value("${DB_URL:}")
    private String dbUrl;
    
    @Override
    public String getUrl() {
        return dbUrl;
    }
    
    @Override
    public int getMaxConnections() {
        return 50;
    }
}

// Properties files
# application-dev.yml
db-url: jdbc:h2:mem:devdb

# application-prod.yml
db-url: jdbc:postgresql://prod-server:5432/mydb
```

---

## Exercise 5: Logging Configuration

### Task
Configure logging to write to both console and file with different log levels for different packages.

### Solution

```java
// LoggingConfig.java
@Configuration
public class LoggingConfig {
    
    @Bean
    public LoggingSystemCustomizer loggingSystemCustomizer() {
        return logging -> {
            logging.level("com.example.service", DEBUG);
            logging.level("com.example.controller", INFO);
            logging.level("org.springframework.web", WARN);
        };
    }
}

// Or use application.yml
logging:
  level:
    root: INFO
    com.example.service: DEBUG
    com.example.controller: INFO
    org.springframework.web: WARN
  file:
    name: logs/application.log
    max-size: 10MB
    max-history: 30
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

---

## Exercise 6: Actuator Endpoints with Customization

### Task
Configure actuator to expose specific endpoints and add custom info contributor.

### Solution

```java
// CustomInfoContributor.java
@Component
public class CustomInfoContributor implements InfoContributor {
    
    @Override
    void contribute(Info.Builder builder) {
        builder.withDetail("build", Map.of(
            "time", Instant.now().toString(),
            "java", System.getProperty("java.version")
        ));
    }
}

// application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env
      base-path: /manage
  endpoint:
    health:
      show-details: always
      probes:
        enabled: true
  info:
    env:
      enabled: true
  health:
    livenessstate:
      enabled: true
    readinessstate:
      enabled: true
```

---

## Exercise 7: Custom Actuator Endpoint

### Task
Create a custom actuator endpoint that provides application statistics.

### Solution

```java
// AppStatsEndpoint.java
@Component
@Endpoint(id = "stats")
public class AppStatsEndpoint {
    
    private final AtomicLong requestCount = new AtomicLong(0);
    private final Map<String, AtomicLong> endpointStats = new ConcurrentHashMap<>();
    
    public void recordRequest(String endpoint) {
        requestCount.incrementAndGet();
        endpointStats.computeIfAbsent(endpoint, k -> new AtomicLong(0)).incrementAndGet();
    }
    
    @ReadOperation
    public Map<String, Object> getStats() {
        return Map.of(
            "totalRequests", requestCount.get(),
            "uptime", System.currentTimeMillis() - Runtime.getRuntime().getStartTime(),
            "endpoints", endpointStats.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()))
        );
    }
    
    @WriteOperation
    public Map<String, Long> resetStats() {
        long total = requestCount.getAndSet(0);
        endpointStats.clear();
        return Map.of("reset", total);
    }
}

// Usage: GET /actuator/stats, DELETE /actuator/stats
```

---

## Exercise 8: Exception Handling

### Task
Implement global exception handling for a REST API.

### Solution

```java
// CustomException.java
public class ResourceNotFoundException extends RuntimeException {
    private final String resource;
    private final Object identifier;
    
    public ResourceNotFoundException(String resource, Object identifier) {
        super(String.format("%s not found with id: %s", resource, identifier));
        this.resource = resource;
        this.identifier = identifier;
    }
}

// GlobalExceptionHandler.java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFound(ResourceNotFoundException ex) {
        return new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .toList();
        
        return new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed: " + String.join(", ", errors),
            LocalDateTime.now()
        );
    }
}

// ErrorResponse.java
public record ErrorResponse(int status, String message, LocalDateTime timestamp) {}
```

---

## Exercise 9: Bean Validation

### Task
Implement input validation using Bean Validation annotations.

### Solution

```java
// User.java (Entity with validation)
public class User {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 150, message = "Age must be at most 150")
    private Integer age;
    
    // getters and setters
}

// UserController.java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        // save user
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User user) {
        // update user
        return ResponseEntity.ok(user);
    }
}
```

---

## Exercise 10: Integration Test with TestRestTemplate

### Task
Write integration tests for a REST controller using TestRestTemplate.

### Solution

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testCreateUser() {
        User user = new User("John", "john@example.com", 25);
        
        ResponseEntity<User> response = restTemplate.postForEntity(
            "/api/users",
            user,
            User.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getName());
    }
    
    @Test
    void testValidationErrors() {
        User invalidUser = new User("", "invalid", -5);
        
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/users",
            invalidUser,
            String.class
        );
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
```

---

## Additional Exercises

### Exercise 11: Async Configuration
Configure async task execution with custom thread pool.

### Exercise 12: Caching
Implement caching for expensive operations using Spring Cache.

### Exercise 13: Scheduled Tasks
Create a scheduled task that runs periodically.

### Exercise 14: File Upload
Implement file upload functionality with validation.

### Exercise 15: Custom Starter
Create a custom auto-configuration starter.