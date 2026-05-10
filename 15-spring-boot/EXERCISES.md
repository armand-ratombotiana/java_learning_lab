# Spring Boot - Exercises

## Exercise 1: Basic REST Controller
**Objective**: Create a simple REST API with CRUD operations.

### Task
Build a REST controller for a `Product` entity:
1. GET all products
2. GET product by ID
3. POST new product
4. PUT update product
5. DELETE product

### Implementation
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @GetMapping
    public List<Product> getAllProducts() { ... }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) { ... }
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) { ... }
}
```

---

## Exercise 2: Request/Response Handling
**Objective**: Master request parameters and response formatting.

### Task
Implement advanced request handling:
1. Pagination with `Pageable`
2. Sorting with `Sort`
3. Filtering with query parameters
4. Custom response wrappers

### Implementation
```java
@GetMapping
public ResponseEntity<Page<Product>> getProducts(
        @PageableDefault(size = 20, sort = "name") Pageable pageable,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Double minPrice) {
    
    Page<Product> products = productService.findByFilters(category, minPrice, pageable);
    return ResponseEntity.ok(products);
}
```

---

## Exercise 3: Exception Handling
**Objective**: Implement global exception handling.

### Task
Create comprehensive error handling:
1. Custom exception classes
2. Global exception handler with `@ControllerAdvice`
3. Error response DTOs
4. Validation error formatting

### Implementation
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(404, ex.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
```

---

## Exercise 4: HATEOAS Implementation
**Objective**: Create hypermedia-driven REST APIs.

### Task
Add HATEOAS links to resources:
1. Add self link to each resource
2. Add related resource links
3. Create link builder utilities
4. Implement resource assembler

### Implementation
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @GetMapping("/{id}")
    public EntityModel<Product> getProduct(@PathVariable Long id) {
        Product product = productService.findById(id);
        
        EntityModel<Product> model = EntityModel.of(product);
        model.add(linkTo(methodOn(ProductController.class).getProduct(id)).withSelfRel());
        model.add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("all-products"));
        
        return model;
    }
}
```

---

## Exercise 5: Spring Boot Actuator
**Objective**: Enable production monitoring.

### Task
Configure and customize actuator endpoints:
1. Enable health endpoint
2. Add custom health indicators
3. Expose metrics endpoint
4. Create custom endpoint

### Configuration
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,beans
  endpoint:
    health:
      show-details: always
      probes:
        enabled: true
  health:
    livenessstate:
      enabled: true
    readinessstate:
      enabled: true
```

### Custom Health Indicator
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        try {
            // Check database connection
            return Health.up().withDetail("database", "OK").build();
        } catch (Exception e) {
            return Health.down().withDetail("error", e.getMessage()).build();
        }
    }
}
```

---

## Exercise 6: Custom Auto-Configuration
**Objective**: Create a reusable Spring Boot starter.

### Task
Build a custom auto-configuration:
1. Create configuration properties class
2. Implement conditional auto-configuration
3. Create spring.factories or auto-configuration files
4. Package as starter

### Implementation
```java
@ConfigurationProperties(prefix = "myapp.feature")
public class FeatureProperties {
    private boolean enabled = true;
    private String defaultValue = "default";
    private int maxRetries = 3;
}

@EnableConfigurationProperties(FeatureProperties.class)
@ConditionalOnClass(FeatureService.class)
@ConditionalOnProperty(prefix = "myapp.feature", name = "enabled", havingValue = "true")
public class FeatureAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public FeatureService featureService(FeatureProperties properties) {
        return new FeatureService(properties);
    }
}
```

---

## Exercise 7: Caching Implementation
**Objective**: Add caching to improve performance.

### Task
Implement caching:
1. Enable caching
2. Configure cache manager
3. Use cache annotations
4. Implement cache eviction

### Implementation
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("products", "categories");
    }
}

@Service
public class ProductService {
    
    @Cacheable(value = "products", key = "#id")
    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow();
    }
    
    @CacheEvict(value = "products", allEntries = true)
    public Product save(Product product) {
        return productRepository.save(product);
    }
}
```

---

## Exercise 8: Async Processing
**Objective**: Implement asynchronous method execution.

### Task
Add async processing:
1. Enable async processing
2. Configure thread pool
3. Use @Async annotation
4. Handle async results

### Implementation
```java
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}

@Service
public class NotificationService {
    
    @Async("taskExecutor")
    public CompletableFuture<Void> sendEmailAsync(String email, String message) {
        emailService.send(email, message);
        return CompletableFuture.completedFuture(null);
    }
}
```

---

## Exercise 9: Profile Management
**Objective**: Use Spring profiles for environment-specific configuration.

### Task
Implement profile-based configuration:
1. Create environment-specific properties files
2. Use @Profile annotation
3. Implement profile-specific beans
4. Configure property sources

### Implementation
```java
@Configuration
@Profile("development")
public class DevConfig {
    @Bean
    public DataSource devDataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:h2:mem:devdb")
            .build();
    }
}

@Configuration
@Profile("production")
public class ProdConfig {
    @Bean
    public DataSource prodDataSource() {
        return DataSourceBuilder.create()
            .url(System.getenv("DB_URL"))
            .username(System.getenv("DB_USER"))
            .password(System.getenv("DB_PASSWORD"))
            .build();
    }
}
```

---

## Exercise 10: Validation & Constraints
**Objective**: Implement bean validation with custom validators.

### Task
Add comprehensive validation:
1. Built-in validation annotations
2. Custom validation annotations
3. Group validation
4. Message interpolation

### Implementation
```java
@Valid
public class ProductController {
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }
}

public class ProductRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100)
    private String name;
    
    @Positive
    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal price;
    
    @Valid
    private List<@Valid CategoryRequest> categories;
}

@Constraint(validatedBy = ValidSkuConstraintValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSku {
    String message() default "Invalid SKU format";
    Class[] groups() default {};
    Class[] payload() default {};
}
```

---

## Running Tests
```bash
# Run application
mvn spring-boot:run

# Test endpoints
curl http://localhost:8080/api/products
curl http://localhost:8080/actuator/health
```