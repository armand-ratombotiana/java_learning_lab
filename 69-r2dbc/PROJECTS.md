# R2DBC Projects - Module 69

This module covers Reactive relational database access using Spring Data R2DBC.

## Mini-Project: Reactive CRUD with R2DBC (2-4 hours)

### Overview
Build a reactive REST API using Spring Data R2DBC for non-blocking database operations.

### Project Structure
```
r2dbc-demo/
├── src/main/java/com/learning/r2dbc/
│   ├── R2dbcDemoApplication.java
│   ├── entity/Product.java
│   ├── repository/ProductRepository.java
│   ├── service/ProductService.java
│   └── controller/ProductController.java
├── src/main/resources/
│   └── schema.sql
├── pom.xml
└── run.sh
```

### Implementation
```java
// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>r2dbc-demo</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-r2dbc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.r2dbc</groupId>
            <artifactId>r2dbc-postgresql</artifactId>
        </dependency>
    </dependencies>
</project>

// Product.java
package com.learning.r2dbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("products")
public class Product {
    
    @Id
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String category;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}

// ProductRepository.java
package com.learning.r2dbc.repository;

import com.learning.r2dbc.entity.Product;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository extends R2dbcRepository<Product, Long> {
    
    Flux<Product> findByCategory(String category);
    
    Flux<Product> findByNameContaining(String name);
    
    @Query("SELECT * FROM products WHERE price BETWEEN :minPrice AND :maxPrice")
    Flux<Product> findByPriceRange(Double minPrice, Double maxPrice);
    
    Mono<Product> findByName(String name);
    
    Mono<Void> deleteByCategory(String category);
}

// ProductService.java
package com.learning.r2dbc.service;

import com.learning.r2dbc.entity.Product;
import com.learning.r2dbc.repository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public Flux<Product> findAll() {
        return productRepository.findAll();
    }
    
    public Mono<Product> findById(Long id) {
        return productRepository.findById(id);
    }
    
    public Flux<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    public Flux<Product> findByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }
    
    public Mono<Product> createProduct(Product product) {
        return productRepository.save(product);
    }
    
    public Mono<Product> updateProduct(Product product) {
        return productRepository.save(product);
    }
    
    public Mono<Void> deleteProduct(Long id) {
        return productRepository.deleteById(id);
    }
    
    public Mono<Product> findByName(String name) {
        return productRepository.findByName(name);
    }
    
    public Mono<Void> deleteByCategory(String category) {
        return productRepository.deleteByCategory(category);
    }
}

// ProductController.java
package com.learning.r2dbc.controller;

import com.learning.r2dbc.entity.Product;
import com.learning.r2dbc.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping
    public Flux<Product> getAllProducts() {
        return productService.findAll();
    }
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> getProduct(@PathVariable Long id) {
        return productService.findById()
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/category/{category}")
    public Flux<Product> getProductsByCategory(@PathVariable String category) {
        return productService.findByCategory(category);
    }
    
    @GetMapping("/price-range")
    public Flux<Product> getProductsByPriceRange(
            @RequestParam Double min,
            @RequestParam Double max) {
        return productService.findByPriceRange(min, max);
    }
    
    @PostMapping
    public Mono<ResponseEntity<Product>> createProduct(@RequestBody Product product) {
        return productService.createProduct(product)
            .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(p));
    }
    
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Product>> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {
        
        product.setId(id);
        return productService.updateProduct(product)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id)
            .then(Mono.just(ResponseEntity.noContent().build()))
            .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }
}
```

---

## Real-World Project: Reactive E-Commerce with R2DBC (8+ hours)

### Overview
Build a comprehensive reactive e-commerce backend using Spring Data R2DBC with transaction management and complex queries.

### Project Structure
```
reactive-ecommerce/
├── src/main/java/com/learning/ecommerce/
│   ├── entity/
│   ├── repository/
│   ├── service/
│   ├── controller/
│   └── config/
├── pom.xml
└── docker-compose.yml
```

### Implementation
```java
// Entities
package com.learning.ecommerce.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("orders")
public class Order {
    
    @Id
    private Long id;
    
    @Column("order_number")
    private String orderNumber;
    
    @Column("customer_id")
    private Long customerId;
    
    private String status;
    
    @Column("total_amount")
    private Double totalAmount;
    
    @Column("created_at")
    private java.time.LocalDateTime createdAt;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
}

@Table("order_items")
class OrderItem {
    @Id
    private Long id;
    
    @Column("order_id")
    private Long orderId;
    
    @Column("product_id")
    private Long productId;
    
    private Integer quantity;
    
    @Column("unit_price")
    private Double unitPrice;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
}

@Table("customers")
class Customer {
    @Id
    private Long id;
    private String name;
    private String email;
    private String phone;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}

// Reactive Transaction Management
package com.learning.ecommerce.service;

import com.learning.ecommerce.entity.Order;
import com.learning.ecommerce.entity.OrderItem;
import com.learning.ecommerce.repository.OrderRepository;
import com.learning.ecommerce.repository.OrderItemRepository;
import com.learning.ecommerce.repository.ProductRepository;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderService {
    
    private final DatabaseClient databaseClient;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    
    public OrderService(DatabaseClient databaseClient,
                        OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductRepository productRepository) {
        this.databaseClient = databaseClient;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }
    
    @Transactional
    public Mono<Order> createOrder(Long customerId, CreateOrderRequest request) {
        return databaseClient.inTransaction(db ->
            db.sql("INSERT INTO orders (order_number, customer_id, status, total_amount, created_at) " +
                   "VALUES (:orderNumber, :customerId, :status, :totalAmount, :createdAt)")
                .bind("orderNumber", "ORD-" + System.currentTimeMillis())
                .bind("customerId", customerId)
                .bind("status", "PENDING")
                .bind("totalAmount", request.items().stream()
                    .mapToDouble(i -> i.unitPrice() * i.quantity()).sum())
                .bind("createdAt", LocalDateTime.now())
                .fetch()
                .rowsUpdated()
                .flatMap(result -> orderRepository.findByCustomerId(customerId)
                    .last()
                    .flatMap(order -> {
                        return Flux.fromIterable(request.items())
                            .flatMap(item -> db.sql(
                                "INSERT INTO order_items (order_id, product_id, quantity, unit_price) " +
                                "VALUES (:orderId, :productId, :quantity, :unitPrice)")
                                .bind("orderId", order.getId())
                                .bind("productId", item.productId())
                                .bind("quantity", item.quantity())
                                .bind("unitPrice", item.unitPrice())
                                .fetch()
                                .rowsUpdated()
                            )
                            .then(Mono.just(order));
                    })
            )
            .single();
    }
    
    public Flux<Order> findOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    public Mono<Order> findOrderWithItems(Long orderId) {
        return orderRepository.findById(orderId)
            .zipWith(orderItemRepository.findByOrderId(orderId).collectList(),
                (order, items) -> {
                    // Combine order with items
                    return order;
                });
    }
}

record CreateOrderRequest(List<OrderItemData> items) {
    record OrderItemData(Long productId, Integer quantity, Double unitPrice) {}
}

// Complex Queries with R2DBC
package com.learning.ecommerce.repository;

import com.learning.ecommerce.entity.Order;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository extends R2dbcRepository<Order, Long> {
    
    Flux<Order> findByCustomerId(Long customerId);
    
    @Query("SELECT * FROM orders WHERE status = :status ORDER BY created_at DESC")
    Flux<Order> findByStatus(String status);
    
    @Query("SELECT o.*, COUNT(oi.id) as item_count, SUM(oi.quantity) as total_items " +
           "FROM orders o " +
           "LEFT JOIN order_items oi ON o.id = oi.order_id " +
           "WHERE o.customer_id = :customerId " +
           "GROUP BY o.id " +
           "ORDER BY o.created_at DESC")
    Flux<OrderSummary> findOrderSummaryByCustomer(Long customerId);
    
    @Query("SELECT * FROM orders WHERE created_at >= :startDate AND created_at <= :endDate")
    Flux<Order> findOrdersByDateRange(java.time.LocalDateTime startDate, 
                                       java.time.LocalDateTime endDate);
}

interface OrderSummary {
    Long getId();
    String getOrderNumber();
    String getStatus();
    Double getTotalAmount();
    Integer getItemCount();
}
```

### Build and Run
```bash
# Start PostgreSQL
docker-compose up -d postgres

# Build the application
mvn clean package

# Run the application
java -jar target/r2dbc-demo-1.0.0.jar

# Test the API
curl http://localhost:8080/api/products
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Product","price":99.99,"stock":10}'
```

### Learning Outcomes
- Build reactive CRUD operations with R2DBC
- Implement transaction management
- Create complex reactive queries
- Handle database connections
- Build reactive APIs with WebFlux
- Optimize reactive data access