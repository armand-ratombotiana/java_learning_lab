# Microservices Architecture Projects

This directory contains projects focused on microservices architecture patterns, distributed systems, service communication, and cloud-native development. These projects help you master the principles of building scalable, maintainable microservices.

## Mini-Project: Service Communication with Feign and Ribbon (2-4 hours)

### Overview

Build a microservices demonstration showing inter-service communication using Spring Cloud OpenFeign for declarative REST clients and Netflix Ribbon for client-side load balancing. This project demonstrates modern service-to-service communication patterns.

### Project Structure

```
microservices-communication/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── microservices/
                    ├── microservicea/
                    │   ├── ServiceAApplication.java
                    │   ├── controller/
                    │   └── client/
                    └── microserviceb/
                        ├── ServiceBApplication.java
                        └── controller/
```

### Implementation

```java
package com.microservices.servicea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceAApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceAApplication.class, args);
    }
}
```

```java
package com.microservices.servicea.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
    name = "service-b",
    fallback = ServiceBClientFallback.class,
    configuration = FeignConfig.class
)
public interface ServiceBClient {
    
    @GetMapping("/api/users")
    List<User> getAllUsers();
    
    @GetMapping("/api/users/{id}")
    User getUserById(@PathVariable("id") Long id);
    
    @PostMapping("/api/users")
    User createUser(@RequestBody User user);
    
    @GetMapping("/api/users/search")
    List<User> searchUsers(@RequestParam("name") String name);
    
    @GetMapping("/health")
    String healthCheck();
}
```

```java
package com.microservices.servicea.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class ServiceBClientFallback implements ServiceBClient {
    
    private static final Logger logger = LoggerFactory.getLogger(ServiceBClientFallback.class);

    @Override
    public List<User> getAllUsers() {
        logger.warn("Fallback: getAllUsers called");
        return Collections.emptyList();
    }

    @Override
    public User getUserById(Long id) {
        logger.warn("Fallback: getUserById called for id: {}", id);
        return createFallbackUser(id);
    }

    @Override
    public User createUser(User user) {
        logger.warn("Fallback: createUser called");
        return createFallbackUser(null);
    }

    @Override
    public List<User> searchUsers(String name) {
        logger.warn("Fallback: searchUsers called for name: {}", name);
        return Collections.emptyList();
    }

    @Override
    public String healthCheck() {
        logger.warn("Fallback: healthCheck called");
        return "SERVICE_UNAVAILABLE";
    }

    private User createFallbackUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("Fallback User");
        user.setEmail("fallback@example.com");
        return user;
    }
}
```

```java
package com.microservices.servicea.config;

import feign.Logger;
import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {
    
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public feign.Request.Options requestOptions() {
        return new Request.Options(
            5000, TimeUnit.MILLISECONDS,
            10000, TimeUnit.MILLISECONDS,
            true
        );
    }
}
```

```java
package com.microservices.servicea.model;

public class User {
    
    private Long id;
    private String name;
    private String email;
    private Integer age;
    private String department;

    public User() {
    }

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
```

```java
package com.microservices.servicea.controller;

import com.microservices.servicea.client.ServiceBClient;
import com.microservices.servicea.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/service-a")
public class ServiceAController {
    
    private final ServiceBClient serviceBClient;

    public ServiceAController(ServiceBClient serviceBClient) {
        this.serviceBClient = serviceBClient;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsersFromServiceB() {
        List<User> users = serviceBClient.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserFromServiceB(@PathVariable Long id) {
        User user = serviceBClient.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUserInServiceB(@RequestBody User user) {
        User created = serviceBClient.createUser(user);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<User>> searchUsersInServiceB(@RequestParam String name) {
        List<User> users = serviceBClient.searchUsers(name);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/combined")
    public ResponseEntity<Map<String, Object>> getCombinedUsers() {
        List<User> users = serviceBClient.getAllUsers();
        
        Map<String, Object> response = Map.of(
            "service", "Service A",
            "source", "Service B",
            "users", users,
            "count", users.size()
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        String serviceBHealth = serviceBClient.healthCheck();
        
        if ("OK".equals(serviceBHealth)) {
            return ResponseEntity.ok("Both services are healthy");
        } else {
            return ResponseEntity.status(503)
                .body("Service B is unavailable: " + serviceBHealth);
        }
    }
}
```

```java
package com.microservices.serviceb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceBApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceBApplication.class, args);
    }
}
```

```java
package com.microservices.serviceb.controller;

import com.microservices.serviceb.model.User;
import com.microservices.serviceb.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    @GetMapping("/department/{department}")
    public List<User> getUsersByDepartment(@PathVariable String department) {
        return userRepository.findByDepartment(department);
    }

    @GetMapping("/age-range")
    public List<User> getUsersByAgeRange(@RequestParam Integer min, @RequestParam Integer max) {
        return userRepository.findByAgeBetween(min, max);
    }
}
```

```java
package com.microservices.serviceb.repository;

import com.microservices.serviceb.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class UserRepository {
    
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserRepository() {
        initializeSampleData();
    }

    private void initializeSampleData() {
        save(new User("John Doe", "john@example.com", 30, "Engineering"));
        save(new User("Jane Smith", "jane@example.com", 28, "Marketing"));
        save(new User("Bob Johnson", "bob@example.com", 35, "Engineering"));
        save(new User("Alice Williams", "alice@example.com", 32, "Sales"));
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public List<User> findByNameContainingIgnoreCase(String name) {
        return users.values().stream()
            .filter(u -> u.getName().toLowerCase().contains(name.toLowerCase()))
            .collect(Collectors.toList());
    }

    public List<User> findByDepartment(String department) {
        return users.values().stream()
            .filter(u -> department.equalsIgnoreCase(u.getDepartment()))
            .collect(Collectors.toList());
    }

    public List<User> findByAgeBetween(int min, int max) {
        return users.values().stream()
            .filter(u -> u.getAge() >= min && u.getAge() <= max)
            .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        users.remove(id);
    }
}
```

```java
package com.microservices.serviceb.model;

public class User {
    
    private Long id;
    private String name;
    private String email;
    private Integer age;
    private String department;

    public User() {
    }

    public User(String name, String email, Integer age, String department) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.department = department;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
```

### service-a application.yml

```yaml
server:
  port: 8081

spring:
  application:
    name: service-a

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true

feign:
  client:
    config:
      default:
        connect-timeout: 5000
        read-timeout: 10000
        logger-level: full
      service-b:
        connect-timeout: 3000
        read-timeout: 5000

ribbon:
  eureka:
    enabled: true
  MaxAutoRetries: 2
  MaxAutoRetriesNextServer: 3
  ConnectTimeout: 3000
  ReadTimeout: 5000
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>

    <groupId>com.microservices</groupId>
    <artifactId>microservices-communication</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <modules>
        <module>service-a</module>
        <module>service-b</module>
    </modules>

    <properties>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

### Build and Run

```bash
# Start Eureka Server first
# Then run service-a and service-b

cd service-a
mvn spring-boot:run

cd service-b
mvn spring-boot:run

# Test
curl http://localhost:8081/api/service-a/users
curl http://localhost:8081/api/service-a/health
```

---

## Real-World Project: E-commerce Microservices Platform (8+ hours)

### Overview

Build a comprehensive e-commerce platform using microservices architecture with service discovery, API gateway, distributed tracing, circuit breaker, event-driven communication, and Kubernetes deployment manifests. This project demonstrates enterprise-grade microservices patterns.

### Architecture

```
ecommerce-platform/
├── eureka-server/           # Service Registry
├── api-gateway/             # Zuul Gateway
├── config-server/           # Config Server
├── product-service/         # Product Catalog
├── order-service/           # Order Management
├── inventory-service/      # Inventory Management
├── payment-service/         # Payment Processing
├── notification-service/    # Notifications
├── shipping-service/        # Shipping Management
└── docker/                 # Docker Compose
```

### Implementation

```java
package com.ecommerce.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
```

```java
package com.ecommerce.product.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public Product() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { 
        this.stockQuantity = stockQuantity;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
```

```java
package com.ecommerce.product.service;

import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> findBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceBetween(min, max);
    }

    @Transactional
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, Product product) {
        Product existing = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setCategory(product.getCategory());
        existing.setStockQuantity(product.getStockQuantity());
        existing.setActive(product.isActive());
        
        return productRepository.save(existing);
    }

    @Transactional
    public boolean reduceStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        
        if (product.getStockQuantity() >= quantity) {
            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product);
            return true;
        }
        return false;
    }

    @Transactional
    public void increaseStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        
        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> findLowStockProducts(Integer threshold) {
        return productRepository.findByStockQuantityLessThan(threshold);
    }
}
```

```java
package com.ecommerce.order.service;

import com.ecommerce.order.client.InventoryClient;
import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.client.NotificationClient;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final NotificationClient notificationClient;

    public OrderService(OrderRepository orderRepository,
                       ProductClient productClient,
                       InventoryClient inventoryClient,
                       NotificationClient notificationClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.inventoryClient = inventoryClient;
        this.notificationClient = notificationClient;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order(
            generateOrderNumber(),
            request.customerId(),
            request.customerEmail()
        );
        order.setShippingAddress(request.shippingAddress());
        order.setBillingAddress(request.billingAddress());
        
        for (OrderItemRequest itemRequest : request.items()) {
            ProductInfo productInfo = productClient.getProduct(itemRequest.productId());
            
            if (productInfo == null) {
                throw new RuntimeException("Product not found: " + itemRequest.productId());
            }
            
            boolean reserved = inventoryClient.reserveStock(
                itemRequest.productId(), itemRequest.quantity());
            
            if (!reserved) {
                throw new RuntimeException("Insufficient stock for product: " + productInfo.name());
            }
            
            OrderItem item = new OrderItem(
                itemRequest.productId(),
                productInfo.name(),
                itemRequest.quantity(),
                productInfo.price()
            );
            order.addItem(item);
        }
        
        order.calculateTotal();
        
        Order savedOrder = orderRepository.save(order);
        
        notificationClient.sendOrderConfirmation(savedOrder);
        
        logger.info("Order created: {}", savedOrder.getOrderNumber());
        
        return savedOrder;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);
        
        if (newStatus == OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                inventoryClient.releaseStock(item.getProductId(), item.getQuantity());
            }
            notificationClient.sendOrderCancellation(order);
        }
        
        Order updatedOrder = orderRepository.save(order);
        
        notificationClient.sendOrderStatusUpdate(updatedOrder, oldStatus);
        
        return updatedOrder;
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderNumber));
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
```

```java
package com.ecommerce.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "product-service", fallback = ProductClientFallback.class)
public interface ProductClient {
    
    @GetMapping("/api/products/{id}")
    ProductInfo getProduct(@PathVariable("id") Long id);
    
    @GetMapping("/api/products")
    List<ProductInfo> getAllProducts();
    
    @GetMapping("/api/products/category/{category}")
    List<ProductInfo> getProductsByCategory(@PathVariable("category") String category);
}

class ProductInfo {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stockQuantity;
    private boolean active;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}

@Component
class ProductClientFallback implements ProductClient {
    
    @Override
    public ProductInfo getProduct(Long id) {
        return null;
    }

    @Override
    public List<ProductInfo> getAllProducts() {
        return List.of();
    }

    @Override
    public List<ProductInfo> getProductsByCategory(String category) {
        return List.of();
    }
}
```

```java
package com.ecommerce.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "inventory-service", fallback = InventoryClientFallback.class)
public interface InventoryClient {
    
    @PostMapping("/api/inventory/{productId}/reserve")
    boolean reserveStock(@PathVariable("productId") Long productId, @RequestParam Integer quantity);
    
    @PostMapping("/api/inventory/{productId}/release")
    void releaseStock(@PathVariable("productId") Long productId, @RequestParam Integer quantity);
    
    @GetMapping("/api/inventory/{productId}")
    InventoryInfo getInventory(@PathVariable("productId") Long productId);
}

class InventoryInfo {
    private Long productId;
    private Integer availableStock;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getAvailableStock() { return availableStock; }
    public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }
}

@Component
class InventoryClientFallback implements InventoryClient {
    
    @Override
    public boolean reserveStock(Long productId, Integer quantity) {
        return false;
    }

    @Override
    public void releaseStock(Long productId, Integer quantity) {
    }

    @Override
    public InventoryInfo getInventory(Long productId) {
        return null;
    }
}
```

```java
package com.ecommerce.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "notification-service", fallback = NotificationClientFallback.class)
public interface NotificationClient {
    
    @PostMapping("/api/notifications/order-confirmation")
    void sendOrderConfirmation(Order order);
    
    @PostMapping("/api/notifications/order-status-update")
    void sendOrderStatusUpdate(Order order, String oldStatus);
    
    @PostMapping("/api/notifications/order-cancellation")
    void sendOrderCancellation(Order order);
}

@Component
class NotificationClientFallback implements NotificationClient {
    
    @Override
    public void sendOrderConfirmation(Order order) {
    }

    @Override
    public void sendOrderStatusUpdate(Order order, String oldStatus) {
    }

    @Override
    public void sendOrderCancellation(Order order) {
    }
}
```

### Kubernetes Deployment Manifests

```yaml
# kubernetes/product-service-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product-service
  labels:
    app: product-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: product-service
  template:
    metadata:
      labels:
        app: product-service
    spec:
      containers:
      - name: product-service
        image: your-registry/product-service:1.0.0
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: kubernetes
        - name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
          value: http://eureka-server:8761/eureka/
        - name: SPRING_DATASOURCE_URL
          value: jdbc:postgresql://postgres:5432/products
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8081
          initialDelaySeconds: 30
          periodSeconds: 10
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8081
          initialDelaySeconds: 60
          periodSeconds: 15
---
apiVersion: v1
kind: Service
metadata:
  name: product-service
spec:
  selector:
    app: product-service
  ports:
  - port: 80
    targetPort: 8081
  type: ClusterIP
```

```yaml
# kubernetes/ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ecommerce-ingress
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
  - host: ecommerce.example.com
    http:
      paths:
      - path: /api/products
        pathType: Prefix
        backend:
          service:
            name: product-service
            port:
              number: 80
      - path: /api/orders
        pathType: Prefix
        backend:
          service:
            name: order-service
            port:
              number: 80
```

### Docker Compose

```yaml
# docker/docker-compose.yml
version: '3.8'

services:
  eureka-server:
    image: your-registry/eureka-server:1.0.0
    ports:
      - "8761:8761"
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  config-server:
    image: your-registry/config-server:1.0.0
    ports:
      - "8888:8888"
    depends-on:
      - eureka-server

  product-service:
    image: your-registry/product-service:1.0.0
    ports:
      - "8081:8081"
    depends-on:
      - eureka-server
      - config-server

  order-service:
    image: your-registry/order-service:1.0.0
    ports:
      - "8082:8082"
    depends-on:
      - eureka-server
      - config-server

  inventory-service:
    image: your-registry/inventory-service:1.0.0
    ports:
      - "8083:8083"
    depends-on:
      - eureka-server
      - config-server

  api-gateway:
    image: your-registry/api-gateway:1.0.0
    ports:
      - "8080:8080"
    depends-on:
      - eureka-server

  postgres:
    image: postgres:15
    environment:
      POSTGRES_PASSWORD: password
      POSTGRES_DB: ecommerce
    volumes:
      - postgres-data:/var/lib/postgresql/data

volumes:
  postgres-data:
```

### Build and Deploy

```bash
# Build all services
mvn clean package -DskipTests

# Build Docker images
docker-compose -f docker/docker-compose.yml build

# Start with Docker Compose
docker-compose -f docker/docker-compose.yml up -d

# Or deploy to Kubernetes
kubectl apply -f kubernetes/
```

---

## Additional Learning Resources

- Spring Cloud Netflix: https://spring.io/projects/spring-cloud-netflix
- Microservices Patterns: https://microservices.io/
- Kubernetes Documentation: https://kubernetes.io/docs/