# Software Architecture Module - PROJECTS.md

---

# Mini-Project: Layered Architecture Implementation

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Layered Architecture, Repository Pattern, Service Layer, Dependency Injection, Separation of Concerns

This mini-project demonstrates software architecture patterns using a layered architecture approach with clean separation between presentation, business logic, and data access layers.

---

## Project Structure

```
26-architecture/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── controller/
│   │   └── ProductController.java
│   ├── service/
│   │   ├── ProductService.java
│   │   └── ValidationService.java
│   ├── repository/
│   │   └── ProductRepository.java
│   ├── model/
│   │   └── Product.java
│   └── dto/
│       └── ProductDTO.java
```

---

## Step 1: POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>architecture-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
    </dependencies>
</project>
```

---

## Step 2: Model and DTO

```java
// model/Product.java
package com.learning.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private Integer stockQuantity;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum ProductStatus {
        ACTIVE, INACTIVE, DISCONTINUED
    }
    
    // Constructors
    public Product() {}
    
    public Product(String id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.status = ProductStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
```

```java
// dto/ProductDTO.java
package com.learning.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public class ProductDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    
    // Convert to Entity
    public com.learning.model.Product toEntity() {
        com.learning.model.Product product = new com.learning.model.Product();
        product.setName(this.name);
        product.setDescription(this.description);
        product.setPrice(this.price);
        product.setCategory(this.category);
        product.setStockQuantity(this.stockQuantity != null ? this.stockQuantity : 0);
        return product;
    }
}
```

---

## Step 3: Repository Layer

```java
// repository/ProductRepository.java
package com.learning.repository;

import com.learning.model.Product;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ProductRepository {
    
    private final Map<String, Product> products = new ConcurrentHashMap<>();
    
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(UUID.randomUUID().toString());
        }
        products.put(product.getId(), product);
        return product;
    }
    
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }
    
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }
    
    public List<Product> findByCategory(String category) {
        return products.values().stream()
            .filter(p -> p.getCategory().equals(category))
            .toList();
    }
    
    public List<Product> findByStatus(Product.ProductStatus status) {
        return products.values().stream()
            .filter(p -> p.getStatus() == status)
            .toList();
    }
    
    public void deleteById(String id) {
        products.remove(id);
    }
    
    public boolean existsById(String id) {
        return products.containsKey(id);
    }
}
```

---

## Step 4: Service Layer

```java
// service/ProductService.java
package com.learning.service;

import com.learning.model.Product;
import com.learning.dto.ProductDTO;
import com.learning.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ValidationService validationService;
    
    public ProductService(
            ProductRepository productRepository, 
            ValidationService validationService) {
        this.productRepository = productRepository;
        this.validationService = validationService;
    }
    
    public Product createProduct(ProductDTO dto) {
        validationService.validateProduct(dto);
        
        Product product = dto.toEntity();
        product.setId(UUID.randomUUID().toString());
        
        return productRepository.save(product);
    }
    
    public Product updateProduct(String id, ProductDTO dto) {
        Product existing = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        
        validationService.validateProduct(dto);
        
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setCategory(dto.getCategory());
        existing.setStockQuantity(dto.getStockQuantity());
        
        return productRepository.save(existing);
    }
    
    public Optional<Product> getProduct(String id) {
        return productRepository.findById(id);
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
    
    public Product updateStock(String id, int quantity) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        
        int newStock = product.getStockQuantity() + quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        
        product.setStockQuantity(newStock);
        return productRepository.save(product);
    }
}

class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
```

```java
// service/ValidationService.java
package com.learning.service;

import com.learning.dto.ProductDTO;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationService {
    
    public void validateProduct(ProductDTO dto) {
        List<String> errors = new ArrayList<>();
        
        if (dto.getName() == null || dto.getName().isBlank()) {
            errors.add("Name is required");
        }
        
        if (dto.getPrice() != null && dto.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            errors.add("Price must be greater than 0");
        }
        
        if (dto.getCategory() == null || dto.getCategory().isBlank()) {
            errors.add("Category is required");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }
    }
}

class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
```

---

## Step 5: Controller Layer

```java
// controller/ProductController.java
package com.learning.controller;

import com.learning.model.Product;
import com.learning.dto.ProductDTO;
import com.learning.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductDTO dto) {
        Product product = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        return productService.getProduct(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String id, 
            @Valid @RequestBody ProductDTO dto) {
        Product product = productService.updateProduct(id, dto);
        return ResponseEntity.ok(product);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(
            @PathVariable String id, 
            @RequestParam int quantity) {
        Product product = productService.updateStock(id, quantity);
        return ResponseEntity.ok(product);
    }
}
```

---

## Step 6: Main Application

```java
// Main.java
package com.learning;

import com.learning.dto.ProductDTO;
import com.learning.service.ProductService;
import com.learning.service.ValidationService;
import com.learning.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.math.BigDecimal;

@SpringBootApplication
public class Main {
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
    
    @Bean
    public CommandLineRunner run(
            ProductService productService) {
        return args -> {
            System.out.println("=== Software Architecture Demo ===");
            
            // Create products
            ProductDTO dto1 = new ProductDTO();
            dto1.setName("Laptop");
            dto1.setDescription("High-performance laptop");
            dto1.setPrice(new BigDecimal("1299.99"));
            dto1.setCategory("Electronics");
            dto1.setStockQuantity(10);
            
            var product = productService.createProduct(dto1);
            System.out.println("Created product: " + product.getName());
            
            // Retrieve products
            var allProducts = productService.getAllProducts();
            System.out.println("Total products: " + allProducts.size());
        };
    }
}
```

---

## Build Instructions

```bash
cd 26-architecture
mvn clean compile
mvn spring-boot:run
# Access API: http://localhost:8080/api/products
```

---

# Real-World Project: Hexagonal Architecture Implementation

## Project Overview

**Duration**: 8+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Hexagonal Architecture, Ports and Adapters, Domain-Driven Design, CQRS Pattern, Event Sourcing

This comprehensive project implements a complete order processing system using hexagonal architecture (also known as ports and adapters), which allows the core business logic to remain independent of external frameworks and infrastructure.

---

## Complete Implementation

```java
// domain/Order.java (Hexagonal: Domain Core)
package com.learning.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Order {
    private OrderId id;
    private CustomerId customerId;
    private List<OrderLine> lines;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum OrderStatus {
        DRAFT, PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }
    
    public static Order create(CustomerId customerId) {
        Order order = new Order();
        order.id = new OrderId();
        order.customerId = customerId;
        order.lines = new ArrayList<>();
        order.status = OrderStatus.DRAFT;
        order.createdAt = LocalDateTime.now();
        return order;
    }
    
    public void addProduct(ProductId productId, int quantity, BigDecimal price) {
        OrderLine line = new OrderLine(productId, quantity, price);
        this.lines.add(line);
        calculateTotal();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void confirm() {
        if (this.status != OrderStatus.DRAFT && this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot confirm order in status: " + this.status);
        }
        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }
    
    private void calculateTotal() {
        this.totalAmount = lines.stream()
            .map(line -> line.getPrice().multiply(java.math.BigDecimal.valueOf(line.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

record OrderId(String value) {}
record CustomerId(String value) {}
record ProductId(String value) {}

class OrderLine {
    private final ProductId productId;
    private final int quantity;
    private final BigDecimal price;
    
    public OrderLine(ProductId productId, int quantity, BigDecimal price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }
    
    public ProductId getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
}
```

```java
// domain/OrderService.java (Hexagonal: Domain Service)
package com.learning.domain;

import java.math.BigDecimal;

public class OrderService {
    
    public Order createOrder(CustomerId customerId) {
        return Order.create(customerId);
    }
    
    public void addToOrder(Order order, ProductId productId, int quantity, BigDecimal price) {
        order.addProduct(productId, quantity, price);
    }
    
    public void submitOrder(Order order) {
        order.confirm();
    }
    
    public void cancelOrder(Order order) {
        // Business rule: can only cancel within 24 hours
        // and only if not yet shipped
        if (order.getStatus() == Order.OrderStatus.SHIPPED) {
            throw new IllegalStateException("Cannot cancel shipped order");
        }
        order.cancel();
    }
}

exten sion Order {
    public void cancel() {
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = java.time.LocalDateTime.now();
    }
}
```

---

## Port Interfaces (Hexagonal Architecture)

```java
// ports/in/InboundPorts.java
package com.learning.ports.in;

import com.learning.domain.*;

public interface OrderUseCase {
    Order createOrder(CustomerId customerId);
    Order addProductToOrder(OrderId orderId, ProductId productId, int quantity, BigDecimal price);
    Order submitOrder(OrderId orderId);
    Order getOrder(OrderId orderId);
    void cancelOrder(OrderId orderId);
}
```

```java
// ports/out/OutboundPorts.java
package com.learning.ports.out;

import com.learning.domain.*;

public interface OrderRepository {
    Order save(Order order);
    Order findById(OrderId orderId);
    boolean existsById(OrderId orderId);
    void delete(OrderId orderId);
}

public interface PaymentGateway {
    PaymentResult processPayment(CustomerId customerId, BigDecimal amount);
}

public interface InventoryService {
    boolean reserveInventory(ProductId productId, int quantity);
    void releaseInventory(ProductId productId, int quantity);
}

public interface ShipmentService {
    ShipmentId createShipment(Order order);
    void cancelShipment(ShipmentId shipmentId);
}

record ShipmentId(String value) {}

class PaymentResult {
    private final boolean success;
    private final String transactionId;
    private final String errorMessage;
    
    public PaymentResult(boolean success, String transactionId, String errorMessage) {
        this.success = success;
        this.transactionId = transactionId;
        this.errorMessage = errorMessage;
    }
    
    public boolean isSuccess() { return success; }
    public String getTransactionId() { return transactionId; }
    public String getErrorMessage() { return errorMessage; }
}
```

---

## Adapters (Hexagonal: Infrastructure)

```java
// adapters/in/rest/OrderController.java (Primary Adapter)
package com.learning.adapters.in.rest;

import com.learning.ports.in.OrderUseCase;
import com.learning.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderUseCase orderUseCase;
    
    public OrderController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        CustomerId customerId = new CustomerId(request.customerId());
        Order order = orderUseCase.createOrder(customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(order));
    }
    
    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderResponse> addItem(
            @PathVariable String orderId,
            @RequestBody AddItemRequest request) {
        OrderId id = new OrderId(orderId);
        ProductId productId = new ProductId(request.productId());
        Order order = orderUseCase.addProductToOrder(id, productId, request.quantity(), request.price());
        return ResponseEntity.ok(toResponse(order));
    }
    
    @PostMapping("/{orderId}/submit")
    public ResponseEntity<OrderResponse> submitOrder(@PathVariable String orderId) {
        OrderId id = new OrderId(orderId);
        Order order = orderUseCase.submitOrder(id);
        return ResponseEntity.ok(toResponse(order));
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        OrderId id = new OrderId(orderId);
        return orderUseCase.getOrder(id)
            .map(order -> ResponseEntity.ok(toResponse(order)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable String orderId) {
        OrderId id = new OrderId(orderId);
        orderUseCase.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
    
    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
            order.getId().value(),
            order.getCustomerId().value(),
            order.getStatus().name(),
            order.getTotalAmount(),
            order.getCreatedAt().toString()
        );
    }
}

record CreateOrderRequest(String customerId) {}
record AddItemRequest(String productId, int quantity, BigDecimal price) {}
record OrderResponse(String orderId, String customerId, String status, BigDecimal totalAmount, String createdAt) {}
```

```java
// adapters/out/persistence/OrderRepositoryAdapter.java (Secondary Adapter)
package com.learning.adapters.out.persistence;

import com.learning.domain.*;
import com.learning.ports.out.OrderRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OrderRepositoryAdapter implements OrderRepository {
    
    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    
    @Override
    public Order save(Order order) {
        orders.put(order.getId().value(), order);
        return order;
    }
    
    @Override
    public Order findById(OrderId orderId) {
        return orders.get(orderId.value());
    }
    
    @Override
    public boolean existsById(OrderId orderId) {
        return orders.containsKey(orderId.value());
    }
    
    @Override
    public void delete(OrderId orderId) {
        orders.remove(orderId.value());
    }
}
```

```java
// adapters/out/payment/PaymentGatewayAdapter.java
package com.learning.adapters.out.payment;

import com.learning.ports.out.PaymentGateway;
import com.learning.domain.*;
import java.util.UUID;

public class PaymentGatewayAdapter implements PaymentGateway {
    
    @Override
    public PaymentResult processPayment(CustomerId customerId, BigDecimal amount) {
        // Simulate payment processing
        String transactionId = UUID.randomUUID().toString();
        
        // In real implementation, call external payment service
        boolean success = amount.compareTo(BigDecimal.ZERO) > 0;
        
        if (success) {
            return new PaymentResult(true, transactionId, null);
        } else {
            return new PaymentResult(false, null, "Payment failed");
        }
    }
}
```

---

## Application Service (Hexagonal: Use Case Implementation)

```java
// application/OrderApplicationService.java
package com.learning.application;

import com.learning.ports.in.OrderUseCase;
import com.learning.ports.out.*;
import com.learning.domain.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class OrderApplicationService implements OrderUseCase {
    
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;
    private final InventoryService inventoryService;
    private final OrderDomainService domainService;
    
    public OrderApplicationService(
            OrderRepository orderRepository,
            PaymentGateway paymentGateway,
            InventoryService inventoryService,
            OrderDomainService domainService) {
        this.orderRepository = orderRepository;
        this.paymentGateway = paymentGateway;
        this.inventoryService = inventoryService;
        this.domainService = new OrderService();
    }
    
    @Override
    public Order createOrder(CustomerId customerId) {
        Order order = domainService.createOrder(customerId);
        return orderRepository.save(order);
    }
    
    @Override
    public Order addProductToOrder(OrderId orderId, ProductId productId, int quantity, BigDecimal price) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId.value()));
        
        // Check inventory
        if (!inventoryService.reserveInventory(productId, quantity)) {
            throw new InventoryException("Insufficient inventory for product: " + productId.value());
        }
        
        domainService.addToOrder(order, productId, quantity, price);
        return orderRepository.save(order);
    }
    
    @Override
    public Order submitOrder(OrderId orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId.value()));
        
        // Process payment
        PaymentResult paymentResult = paymentGateway.processPayment(
            order.getCustomerId(), 
            order.getTotalAmount()
        );
        
        if (!paymentResult.isSuccess()) {
            throw new PaymentException(paymentResult.getErrorMessage());
        }
        
        domainService.submitOrder(order);
        return orderRepository.save(order);
    }
    
    @Override
    public Order getOrder(OrderId orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }
    
    @Override
    public void cancelOrder(OrderId orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId.value()));
        
        domainService.cancelOrder(order);
        orderRepository.delete(orderId);
    }
}

class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) { super(message); }
}

class InventoryException extends RuntimeException {
    public InventoryException(String message) { super(message); }
}

class PaymentException extends RuntimeException {
    public PaymentException(String message) { super(message); }
}

class OrderDomainService {
    private final OrderService orderService = new OrderService();
}
```

---

## Build Instructions (Real-World Project)

```bash
cd 26-architecture
mvn clean compile
mvn spring-boot:run

# Test with curl
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId": "customer-123"}'

curl -X POST http://localhost:8080/api/orders/order-123/items \
  -H "Content-Type: application/json" \
  -d '{"productId": "product-456", "quantity": 2, "price": 99.99}'
```

---

## Architecture Patterns Summary

This module demonstrates multiple architectural patterns:

1. **Layered Architecture** (Mini-Project)
   - Controller → Service → Repository → Model
   - Clear separation of concerns
   - Easy to understand and implement

2. **Hexagonal Architecture** (Real-World Project)
   - Domain Core (pure business logic)
   - Ports (interfaces)
   - Adapters (Infrastructure)
   - Framework-independent

3. **Key Benefits**
   - Testability: Domain logic can be tested without infrastructure
   - Flexibility: Swap adapters without changing domain
   - Maintainability: Clear boundaries between components

4. **Additional Patterns Shown**
   - DTO transformation
   - Validation layer
   - Exception handling
   - REST API design