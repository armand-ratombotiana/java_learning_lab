# JHipster Projects

This directory contains hands-on projects using JHipster, a powerful code generator for creating full-stack Java applications with Spring Boot and modern frontend frameworks like Angular or React.

## Project Overview

JHipster simplifies enterprise application development by generating a complete production-ready application with backend APIs, security, database configuration, and a responsive frontend. This module covers two projects of increasing complexity.

---

# Mini-Project: Task Management API (2-4 Hours)

## Project Description

Build a task management REST API using JHipster with Spring Boot backend and a simple React frontend. This project demonstrates core JHipster features including entity generation, REST API creation, and security configuration.

## Technologies Used

- Spring Boot 3.2.x
- JHipster 8.x
- React 18.x
- H2 Database (development)
- Spring Security with JWT

## Implementation Steps

### Step 1: Generate the Application

```bash
# Create new JHipster application
mkdir jhipster-taskapp
cd jhipster-taskapp
jhipster --blueprint=generator-jhipster

# Select options:
# - Application type: Monolithic application
# - Authentication: JWT
# - Database: H2 with Spring Data JPA
# - Build tool: Maven
# - Other frameworks: React
```

### Step 2: Generate the Task Entity

```bash
# Generate entity using JHipster entity generator
jhipster entity Task

# Entity fields:
# - title (String, required)
# - description (String)
# - completed (Boolean, default false)
# - createdAt (LocalDateTime)
# - dueDate (LocalDateTime, optional)
```

### Step 3: Configure Maven Dependencies

Update the project pom.xml to include additional dependencies:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.learning</groupId>
        <artifactId>jhipster-taskapp</artifactId>
        <version>1.0.0</version>
    </parent>
    <artifactId>jhipster-taskapp</artifactId>
    <packaging>jar</packaging>
    
    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
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
            <groupId>io.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.12.3</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.12.3</version>
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

### Step 4: Create the Task Entity Implementation

```java
package com.learning.taskapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "task")
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "completed")
    private Boolean completed = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
}
```

### Step 5: Create the Task Repository

```java
package com.learning.taskapp.repository;

import com.learning.taskapp.domain.Task;
import com.learning.taskapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByOwner(User owner);
    
    List<Task> findByOwnerAndCompleted(User owner, Boolean completed);
}
```

### Step 6: Create the Task Service

```java
package com.learning.taskapp.service;

import com.learning.taskapp.domain.Task;
import com.learning.taskapp.domain.User;
import com.learning.taskapp.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskService {
    
    private final TaskRepository taskRepository;
    
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    public Task createTask(Task task, User owner) {
        task.setOwner(owner);
        return taskRepository.save(task);
    }
    
    @Transactional(readOnly = true)
    public List<Task> getTasksByOwner(User owner) {
        return taskRepository.findByOwner(owner);
    }
    
    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(Long id, User owner) {
        return taskRepository.findById(id)
            .filter(task -> task.getOwner().equals(owner));
    }
    
    public Task updateTask(Long id, Task taskUpdate, User owner) {
        Task task = getTaskById(id, owner)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        task.setTitle(taskUpdate.getTitle());
        task.setDescription(taskUpdate.getDescription());
        task.setCompleted(taskUpdate.getCompleted());
        task.setDueDate(taskUpdate.getDueDate());
        
        return taskRepository.save(task);
    }
    
    public void deleteTask(Long id, User owner) {
        Task task = getTaskById(id, owner)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        taskRepository.delete(task);
    }
    
    public Task toggleComplete(Long id, User owner) {
        Task task = getTaskById(id, owner)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        task.setCompleted(!task.getCompleted());
        return taskRepository.save(task);
    }
}
```

### Step 7: Create the Task Controller

```java
package com.learning.taskapp.web.rest;

import com.learning.taskapp.domain.Task;
import com.learning.taskapp.domain.User;
import com.learning.taskapp.security.AuthorityConstants;
import com.learning.taskapp.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    private final TaskService taskService;
    
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    
    @PostMapping
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER +"')")
    public ResponseEntity<Task> createTask(
            @Valid @RequestBody Task task,
            @AuthenticationPrincipal User user) {
        Task created = taskService.createTask(task, user);
        return ResponseEntity.ok(created);
    }
    
    @GetMapping
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER +"')")
    public ResponseEntity<List<Task>> getAllTasks(@AuthenticationPrincipal User user) {
        List<Task> tasks = taskService.getTasksByOwner(user);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER +"')")
    public ResponseEntity<Task> getTask(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return taskService.getTaskById(id, user)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER +"')")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody Task task,
            @AuthenticationPrincipal User user) {
        Task updated = taskService.updateTask(id, task, user);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER +"')")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        taskService.deleteTask(id, user);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER +"')")
    public ResponseEntity<Task> toggleComplete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Task toggled = taskService.toggleComplete(id, user);
        return ResponseEntity.ok(toggled);
    }
}
```

### Step 8: Run and Test the Application

```bash
# Build the application
./mvnw package -DskipTests

# Run the application
./mvnw spring-boot:run

# Test the API
# Get JWT token
curl -X POST http://localhost:8080/api/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'

# Create a task
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"title":"Learn JHipster","description":"Complete JHipster tutorial","dueDate":"2024-12-31T23:59:59"}'

# Get all tasks
curl -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

## Expected Output

The mini-project produces a fully functional task management API with:
- JWT-based authentication
- CRUD operations for tasks
- Task completion toggling
- Owner-based access control

---

# Real-World Project: E-Commerce Platform with Microservices (8+ Hours)

## Project Description

Build a complete e-commerce platform using JHipster's microservices capabilities. This project includes product catalog, order management, user management, and shopping cart services with API gateway and distributed tracing.

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                   API Gateway                       │
│                   (JHipster)                      │
└─────────────────────────────────────────────────────┘
         │              │              │
    ┌────▼────┐  ┌────▼────┐  ┌────▼────┐
    │Product  │  │Order    │  │User     │
    │Service  │  │Service  │  │Service  │
    └─────────┘  └─────────┘  └─────────┘
         │              │              │
    ┌────▼────┐  ┌────▼────┐  ┌────▼────┐
    │Product  │  │Order    │  │User     │
    │DB       │  │DB       │  │DB       │
    └─────────┘  └─────────┘  └─────────┘
```

## Implementation Steps

### Step 1: Generate Microservices Architecture

```bash
# Create registry (JHipster Registry or Consul)
mkdir jhipster-registry
cd jhipster-registry
jhipster --blueprint=generator-jhipster-registry

# Create gateway
mkdir jhipster-gateway
cd jhipster-gateway
jhipster --blueprint=generator-jhipster

# Configure as API Gateway with:
# - Application type: Microservice gateway
# - Service discovery: JHipster Registry
# - Authentication: JWT
```

### Step 2: Generate Product Microservice

```bash
# Create product microservice
mkdir product-service
cd product-service
jhipster --blueprint=generator-jhipster

# Select options:
# - Application type: Microservice application
# - Service discovery: JHipster Registry
# - Database: PostgreSQL
```

### Step 3: Generate Order Microservice

```bash
# Create order microservice
mkdir order-service
cd order-service
jhipster --blueprint=generator-jhipster

# Same options as product service
```

### Step 4: Configure Product Service pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.learning</groupId>
        <artifactId>product-service</artifactId>
        <version>1.0.0</version>
    </parent>
    <artifactId>product-service</artifactId>
    <packaging>jar</packaging>
    
    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
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
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-spring-boot3</artifactId>
            <version>2.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
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

### Step 5: Create Product Domain Entities

```java
package com.learning.product.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Price is required")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<ProductReview> reviews = new HashSet<>();
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    public Set<ProductReview> getReviews() { return reviews; }
    public void setReviews(Set<ProductReview> reviews) { this.reviews = reviews; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
```

```java
package com.learning.product.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "category")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Category getParent() { return parent; }
    public void setParent(Category parent) { this.parent = parent; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
```

### Step 6: Create Order Service DTOs

```java
package com.learning.order.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class OrderItemDTO {
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    
    @NotNull(message = "Unit price is required")
    private BigDecimal unitPrice;
    
    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
```

```java
package com.learning.order.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

public class CreateOrderRequest {
    
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
    
    @NotBlank(message = "Contact email is required")
    @Email(message = "Invalid email format")
    private String contactEmail;
    
    private String paymentMethod;
    
    private List<OrderItemDTO> items;
    
    // Getters and Setters
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }
}
```

### Step 7: Implement API Gateway Configuration

```yaml
# gateway application.yml
spring:
  application:
    name: gateway
  cloud:
    gateway:
      routes:
        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
          filters:
            - StripPrefix=0
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
          filters:
            - StripPrefix=0
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/users/**
          filters:
            - StripPrefix=0
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins: "*"
            allowedMethods:
              - GET
              - POST
              - PUT
              - DELETE
              - OPTIONS
            allowedHeaders: "*"

eureka:
  client:
    serviceUrl:
      defaultZone: ${JHIPSTER_REGISTRY_URL:http://localhost:8761}/eureka/

resilience4j:
  retry:
    instances:
      productService:
        maxAttempts: 3
        waitDuration: 1000
        retryExceptions:
          - java.io.IOException
      orderService:
        maxAttempts: 3
        waitDuration: 1000
```

### Step 8: Configure Distributed Tracing

```java
package com.learning.gateway.config;

import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.micrometer.observation.ObservationRegistry;

@Configuration
public class TracingConfig {
    
    @Bean
    public Tracer tracer(ObservationRegistry observationRegistry) {
        return new BraveTracer(observationRegistry);
    }
}
```

### Step 9: Build and Run All Services

```bash
# Build all services
cd product-service && ./mvnw package -DskipTests
cd order-service && ./mvnw package -DskipTests
cd user-service && ./mvnw package -DskipTests
cd gateway && ./mvnw package -DskipTests

# Start JHipster Registry
cd jhipster-registry && docker-compose up -d

# Start all microservices
cd product-service && java -jar product-service.jar
cd order-service && java -jar order-service.jar
cd user-service && java -jar user-service.jar

# Start gateway
cd gateway && java -jar gateway.jar
```

### Step 10: Test the Complete Platform

```bash
# Register a user
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123","login":"user"}'

# Get JWT token
curl -X POST http://localhost:8080/api/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password123"}'

# Get products (via gateway)
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer <JWT_TOKEN>"

# Place an order (via gateway)
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "shippingAddress":"123 Main St",
    "contactEmail":"user@example.com",
    "paymentMethod":"CREDIT_CARD",
    "items":[
      {"productId":1,"quantity":2,"unitPrice":29.99}
    ]
  }'
```

## Expected Output

The real-world project produces a complete e-commerce platform with:
- Microservices architecture with service discovery
- API Gateway with routing and load balancing
- JWT authentication across all services
- Product catalog with categories and reviews
- Order management with inventory checking
- Distributed tracing and monitoring

## Additional Features to Implement

1. **Shopping Cart Service**: Implement cart management as a separate service
2. **Payment Integration**: Integrate with Stripe or PayPal API
3. **Inventory Management**: Real-time stock updates with optimistic locking
4. **Search Service**: Elasticsearch-based product search
5. **Notification Service**: Email and SMS notifications for order updates

## Build Instructions Summary

| Step | Command | Time |
|------|---------|------|
| Generate apps | `jhipster` | 30 min |
| Configure dependencies | Edit pom.xml | 15 min |
| Create entities | Implement domain classes | 1 hour |
| Create services | Implement business logic | 1.5 hours |
| Create controllers | Implement REST APIs | 1 hour |
| Configure gateway | Edit application.yml | 30 min |
| Test integration | Run all services | 2 hours |

Total estimated time: 8-10 hours