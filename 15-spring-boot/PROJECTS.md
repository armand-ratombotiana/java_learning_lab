# Spring Boot Module - PROJECTS.md

---

# Mini-Projects Overview

| Concept | Duration | Description |
|---------|----------|-------------|
| Auto-configuration | 2 hours | @Conditional, @EnableAutoConfiguration |
| Starters | 2 hours | Custom starters, dependency management |
| Properties | 2 hours | application.yml, @ConfigurationProperties |
| Actuator | 2 hours | Health, metrics, endpoints, custom |
| Real-world: E-commerce Service | 8+ hours | Complete Boot app with security, caching |

---

# Mini-Project: REST API with Spring Boot

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Spring Boot, REST Controllers, Spring Data, Spring Security, Actuator

This project demonstrates Spring Boot REST API development with database integration.

---

## Project Structure

```
15-spring-boot/src/main/java/com/learning/
├── Application.java
├── controller/
│   ├── UserController.java
│   └── ProductController.java
├── service/
│   ├── UserService.java
│   └── ProductService.java
├── repository/
│   ├── UserRepository.java
│   └── ProductRepository.java
├── model/
│   ├── User.java
│   ├── Product.java
│   └── ApiResponse.java
├── dto/
│   ├── UserRequest.java
│   └── UserResponse.java
└── exception/
    ├── GlobalExceptionHandler.java
    └── ResourceNotFoundException.java
```

---

## Step 1: Maven POM

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>spring-boot-rest-api</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.1</version>
    </parent>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
</project>
```

---

## Step 2: Model Classes

```java
// model/User.java
package com.learning.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
```

```java
// model/Product.java
package com.learning.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { 
        this.stockQuantity = stockQuantity; 
    }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}

@Entity
@Table(name = "categories")
class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
```

---

## Step 3: DTOs

```java
// dto/UserRequest.java
package com.learning.dto;

import jakarta.validation.constraints.*;

public class UserRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8)
    private String password;
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
```

```java
// dto/UserResponse.java
package com.learning.dto;

import java.time.LocalDateTime;

public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
}
```

```java
// model/ApiResponse.java
package com.learning.model;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    
    public ApiResponse() {}
    
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
```

---

## Step 4: Repository

```java
// repository/UserRepository.java
package com.learning.repository;

import com.learning.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

---

## Step 5: Service

```java
// service/UserService.java
package com.learning.service;

import com.learning.model.User;
import com.learning.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        return userRepository.save(user);
    }
    
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
```

---

## Step 6: REST Controller

```java
// controller/UserController.java
package com.learning.controller;

import com.learning.model.User;
import com.learning.model.ApiResponse;
import com.learning.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(
            @Valid @RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(created));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
```

---

## Step 7: Exception Handling

```java
// exception/GlobalExceptionHandler.java
package com.learning.exception;

import com.learning.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> 
            handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("Validation failed"));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("An error occurred: " + ex.getMessage()));
    }
}
```

```java
// exception/ResourceNotFoundException.java
package com.learning.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

---

## Step 8: Application Properties

```properties
# application.properties
spring.application.name=spring-boot-rest-api

# H2 Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Server
server.port=8080

# Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

---

## Step 9: Main Application

```java
// Application.java
package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

---

## Build Instructions

```bash
cd 15-spring-boot
mvn clean compile
mvn spring-boot:run
```

Test with:
```bash
curl http://localhost:8080/api/users
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John","email":"john@example.com","password":"password123"}'
```

---

# Real-World Project: Complete Spring Boot Application

## Project Overview

**Duration**: 8+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Spring Boot Complete, Security, Data REST, Actuator, Profile Management, Caching, Async Processing

This project implements a complete production-ready Spring Boot application with security, caching, and monitoring.

---

## Project Structure

```
15-spring-boot/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Application.java
│   ├── security/
│   │   ├── SecurityConfig.java
│   │   ├── JwtTokenProvider.java
│   │   └── JwtAuthenticationFilter.java
│   ├── config/
│   │   ├── CacheConfig.java
│   │   ├── AsyncConfig.java
│   │   └── RedisConfig.java
│   ├── service/
│   │   ├── ProductService.java
│   │   ├── CacheService.java
│   │   └── AsyncService.java
│   ├── repository/
│   │   └── ProductRepository.java
│   └── controller/
│       ├── ProductController.java
│       └── AdminController.java
├── src/main/resources/
│   ├── application.yml
│   └── data.sql
└── src/test/java/com/learning/
    └── ProductServiceTest.java
```

---

## POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>spring-boot-complete</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.1</version>
    </parent>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## Security Configuration

```java
// security/SecurityConfig.java
package com.learning.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .addFilterBefore(new JwtAuthenticationFilter(), 
                UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## Cache Configuration

```java
// config/CacheConfig.java
package com.learning.config;

import org.springframework.cache.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {
    
    @Bean
    public ConcurrentMapCacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = 
            new ConcurrentMapCacheManager("products", "users");
        cacheManager.setStoreByValue(true);
        return cacheManager;
    }
}
```

---

## Async Configuration

```java
// config/AsyncConfig.java
package com.learning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

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
```

---

## Cached Product Service

```java
// service/ProductService.java
package com.learning.service;

import com.learning.model.Product;
import com.learning.repository.ProductRepository;
import org.springframework.cache.annotation.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        simulateSlowService();
        return productRepository.findById(id).orElse(null);
    }
    
    @Cacheable(value = "products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    @CachePut(value = "products", key = "#result.id")
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    @CacheEvict(value = "products", allEntries = true)
    public void clearCache() {
    }
    
    @Async("taskExecutor")
    public CompletableFuture<Product> getProductAsync(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        return CompletableFuture.completedFuture(product);
    }
    
    private void simulateSlowService() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

---

## Application Properties with Profiles

```yaml
# application.yml
spring:
  application:
    name: spring-boot-complete
  
  profiles:
    active: development
  
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,beans,env
  endpoint:
    health:
      show-details: always

logging:
  level:
    com.learning: DEBUG
```

```sql
-- data.sql
INSERT INTO products (name, description, price, stock_quantity) VALUES
('Laptop', 'High-performance laptop', 999.99, 50),
('Phone', 'Smartphone', 699.99, 100),
('Tablet', 'Tablet device', 399.99, 75);
```

---

## Main Application

```java
// Application.java
package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

---

## Build Instructions

```bash
cd 15-spring-boot
mvn clean package
mvn spring-boot:run
```

Access health endpoint:
```bash
curl http://localhost:8080/actuator/health
```

---

# Auto-Configuration Demo Module - PROJECTS.md

---

# Mini-Project: Spring Boot Auto-Configuration

## Project Overview

**Duration**: 2-3 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Custom Starters, Auto-Configuration, Conditional Annotations, Configuration Properties

This project demonstrates how to create custom Spring Boot auto-configurations.

---

## Project Structure

```
15-spring-boot/src/main/java/com/learning/autoconfigure/
├── AutoConfiguration.java
├── MyServiceAutoConfiguration.java
├── properties/
│   └── MyServiceProperties.java
└── MyService.java
```

---

## Implementation

```java
// properties/MyServiceProperties.java
package com.learning.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "my-service")
public class MyServiceProperties {
    
    private String defaultName = "World";
    private int maxRetries = 3;
    private boolean enabled = true;
    
    public String getDefaultName() { return defaultName; }
    public void setDefaultName(String defaultName) { 
        this.defaultName = defaultName; 
    }
    
    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { 
        this.maxRetries = maxRetries; 
    }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { 
        this.enabled = enabled; 
    }
}
```

```java
// MyService.java
package com.learning.autoconfigure;

public class MyService {
    
    public MyService(String defaultName) {
        this.defaultName = defaultName;
    }
    
    private String defaultName;
    private int maxRetries;
    private boolean enabled;
    
    public String greet(String name) {
        return "Hello, " + (name != null ? name : defaultName) + "!";
    }
    
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
}
```

```java
// MyServiceAutoConfiguration.java
package com.learning.autoconfigure;

import com.learning.autoconfigure.properties.MyServiceProperties;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

@EnableConfigurationProperties(MyServiceProperties.class)
@ConditionalOnClass(MyService.class)
@ConditionalOnProperty(prefix = "my-service", name = "enabled", 
    havingValue = "true", matchIfMissing = true)
public class MyServiceAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public MyService myService(MyServiceProperties properties) {
        MyService service = new MyService(properties.getDefaultName());
        service.setMaxRetries(properties.getMaxRetries());
        return service;
    }
}
```

Build as starter and use with Maven:

```xml
<!-- In spring-boot-starter project -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-autoconfigure</artifactId>
    </dependency>
</dependencies>

<dependencies>
    <!-- In starter -->
    <dependency>
        <groupId>com.learning</groupId>
        <artifactId>my-service-autoconfigure</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>com.learning</groupId>
        <artifactId>my-service-starter</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```