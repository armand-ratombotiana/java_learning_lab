# Architecture Patterns - CODE DEEP DIVE

## Table of Contents
1. [Layered Architecture Implementation](#layered-code)
2. [Microservices Implementation](#microservices-code)
3. [Event-Driven Implementation](#event-driven-code)
4. [CQRS Implementation](#cqrs-code)

---

## 1. Layered Architecture Implementation <a name="layered-code"></a>

### Project Structure

```
layered-app/
├── src/main/java/com/example/
│   ├── controller/
│   │   ├── ProductController.java
│   │   └── ExceptionHandler.java
│   ├── service/
│   │   ├── ProductService.java
│   │   └── ProductServiceImpl.java
│   ├── repository/
│   │   ├── ProductRepository.java
│   │   └── ProductRepositoryImpl.java
│   ├── model/
│   │   ├── Product.java
│   │   └── ProductDTO.java
│   └── exception/
│       ├── ProductNotFoundException.java
│       └── ValidationException.java
└── pom.xml
```

### Domain Model

```java
// model/Product.java
package com.example.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Product {
    private final String id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Product(String name, String description, BigDecimal price, int stockQuantity, String category) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public String getCategory() { return category; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters with validation
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Product name cannot be empty");
        }
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public void setPrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Price must be positive");
        }
        this.price = price;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStock(int quantity) {
        this.stockQuantity = quantity;
        this.updatedAt = LocalDateTime.now();
    }
}
```

### Data Transfer Objects

```java
// model/ProductDTO.java
package com.example.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDTO {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private String category;
    private LocalDateTime createdAt;

    public ProductDTO() {}

    public ProductDTO(String id, String name, String description, BigDecimal price, 
                      int stockQuantity, String category, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.createdAt = createdAt;
    }

    public static ProductDTO fromEntity(Product product) {
        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStockQuantity(),
            product.getCategory(),
            product.getCreatedAt()
        );
    }

    public Product toEntity() {
        return new Product(name, description, price, stockQuantity, category);
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
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
```

### Repository Layer

```java
// repository/ProductRepository.java
package com.example.repository;

import com.example.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(String id);
    List<Product> findAll();
    List<Product> findByCategory(String category);
    void deleteById(String id);
    boolean existsById(String id);
}
```

```java
// repository/ProductRepositoryImpl.java
package com.example.repository;

import com.example.model.Product;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ProductRepositoryImpl implements ProductRepository {
    private final Map<String, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public List<Product> findByCategory(String category) {
        return products.values().stream()
            .filter(p -> p.getCategory().equalsIgnoreCase(category))
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        products.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return products.containsKey(id);
    }
}
```

### Service Layer

```java
// service/ProductService.java
package com.example.service;

import com.example.model.Product;
import com.example.model.ProductDTO;
import java.util.List;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO getProductById(String id);
    List<ProductDTO> getAllProducts();
    List<ProductDTO> getProductsByCategory(String category);
    ProductDTO updateProduct(String id, ProductDTO productDTO);
    void deleteProduct(String id);
    ProductDTO updateStock(String id, int newQuantity);
}
```

```java
// service/ProductServiceImpl.java
package com.example.service;

import com.example.exception.ProductNotFoundException;
import com.example.exception.ValidationException;
import com.example.model.Product;
import com.example.model.ProductDTO;
import com.example.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;

public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        validateProductDTO(productDTO);
        
        Product product = productDTO.toEntity();
        Product savedProduct = productRepository.save(product);
        
        return ProductDTO.fromEntity(savedProduct);
    }

    @Override
    public ProductDTO getProductById(String id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        
        return ProductDTO.fromEntity(product);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
            .map(ProductDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
            .map(ProductDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public ProductDTO updateProduct(String id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        
        validateProductDTO(productDTO);
        
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setCategory(productDTO.getCategory());
        
        Product updatedProduct = productRepository.save(existingProduct);
        
        return ProductDTO.fromEntity(updatedProduct);
    }

    @Override
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public ProductDTO updateStock(String id, int newQuantity) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        
        if (newQuantity < 0) {
            throw new ValidationException("Stock quantity cannot be negative");
        }
        
        product.updateStock(newQuantity);
        productRepository.save(product);
        
        return ProductDTO.fromEntity(product);
    }

    private void validateProductDTO(ProductDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new ValidationException("Product name is required");
        }
        if (dto.getPrice() == null || dto.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Price must be positive");
        }
    }
}
```

### Controller Layer

```java
// controller/ProductController.java
package com.example.controller;

import com.example.model.ProductDTO;
import com.example.service.ProductService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO created = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable String id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable String category) {
        List<ProductDTO> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable String id,
            @RequestBody ProductDTO productDTO) {
        ProductDTO updated = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductDTO> updateStock(
            @PathVariable String id,
            @RequestParam int quantity) {
        ProductDTO updated = productService.updateStock(id, quantity);
        return ResponseEntity.ok(updated);
    }
}
```

### Exception Handling

```java
// exception/ProductNotFoundException.java
package com.example.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
```

```java
// exception/ValidationException.java
package com.example.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
```

```java
// controller/ExceptionHandler.java
package com.example.controller;

import com.example.exception.ProductNotFoundException;
import com.example.exception.ValidationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFound(ProductNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(ValidationException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        error.put("message", message);
        return ResponseEntity.status(status).body(error);
    }
}
```

---

## 2. Microservices Implementation <a name="microservices-code"></a>

### Service Structure

```
microservices/
├── product-service/
│   ├── src/main/java/...
│   └── pom.xml
├── order-service/
│   ├── src/main/java/...
│   └── pom.xml
├── api-gateway/
│   └── src/main/java/...
├── service-registry/
│   └── src/main/java/...
└── config-server/
    └── src/main/java/...
```

### Product Service Implementation

```java
// product-service/src/main/java/com/example/product/ProductServiceApplication.java
package com.example.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example.product.repository.ProductRepository;
import com.example.product.repository.InMemoryProductRepository;

@SpringBootApplication
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

    @Bean
    public ProductRepository productRepository() {
        return new InMemoryProductRepository();
    }
}
```

```java
// product-service/src/main/java/com/example/product/entity/Product.java
package com.example.product.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private String category;
    private LocalDateTime createdAt;

    public Product() {}

    public Product(String name, String description, BigDecimal price, int stock, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
```

```java
// product-service/src/main/java/com/example/product/repository/ProductRepository.java
package com.example.product.repository;

import com.example.product.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(String id);
    List<Product> findAll();
    boolean existsById(String id);
    boolean reduceStock(String id, int quantity);
}
```

```java
// product-service/src/main/java/com/example/product/repository/InMemoryProductRepository.java
package com.example.product.repository;

import com.example.product.entity.Product;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryProductRepository implements ProductRepository {
    private final Map<String, Product> products = new ConcurrentHashMap<>();

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(java.util.UUID.randomUUID().toString());
        }
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new java.util.ArrayList<>(products.values());
    }

    @Override
    public boolean existsById(String id) {
        return products.containsKey(id);
    }

    @Override
    public boolean reduceStock(String id, int quantity) {
        Product product = products.get(id);
        if (product == null || product.getStock() < quantity) {
            return false;
        }
        product.setStock(product.getStock() - quantity);
        return true;
    }
}
```

```java
// product-service/src/main/java/com/example/product/service/ProductService.java
package com.example.product.service;

import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;
import java.util.List;

public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product createProduct(Product product) {
        return repository.save(product);
    }

    public Product getProduct(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public boolean checkAndReduceStock(String productId, int quantity) {
        return repository.reduceStock(productId, quantity);
    }
}
```

```java
// product-service/src/main/java/com/example/product/controller/ProductController.java
package com.example.product.controller;

import com.example.product.entity.Product;
import com.example.product.service.ProductService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        Product product = productService.getProduct(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
}
```

### Order Service with Event Publishing

```java
// order-service/src/main/java/com/example/order/OrderServiceApplication.java
package com.example.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

```java
// order-service/src/main/java/com/example/order/entity/Order.java
package com.example.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private String id;
    private String customerId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;

    public enum OrderStatus {
        PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }

    public static class OrderItem {
        private String productId;
        private int quantity;
        private BigDecimal price;

        public OrderItem() {}

        public OrderItem(String productId, int quantity, BigDecimal price) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
        }

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }

    public Order() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
```

```java
// order-service/src/main/java/com/example/order/service/OrderService.java
package com.example.order.service;

import com.example.order.entity.Order;
import com.example.order.entity.Order.OrderItem;
import com.example.order.event.OrderEventPublisher;
import com.example.order.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final ProductClient productClient;

    public OrderService(OrderRepository orderRepository, 
                        OrderEventPublisher eventPublisher,
                        ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.productClient = productClient;
    }

    public Order createOrder(Order order) {
        for (OrderItem item : order.getItems()) {
            boolean available = productClient.checkStock(item.getProductId(), item.getQuantity());
            if (!available) {
                throw new RuntimeException("Product not available: " + item.getProductId());
            }
        }

        BigDecimal total = calculateTotal(order.getItems());
        order.setId(UUID.randomUUID().toString());
        order.setTotalAmount(total);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        eventPublisher.publishOrderCreated(savedOrder);

        return savedOrder;
    }

    private BigDecimal calculateTotal(java.util.List<OrderItem> items) {
        return items.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

```java
// order-service/src/main/java/com/example/order/event/OrderEventPublisher.java
package com.example.order.event;

import com.example.order.entity.Order;

public interface OrderEventPublisher {
    void publishOrderCreated(Order order);
    void publishOrderStatusChanged(Order order);
}
```

```java
// order-service/src/main/java/com/example/order/event/KafkaOrderEventPublisher.java
package com.example.order.event;

import com.example.order.entity.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaOrderEventPublisher implements OrderEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaOrderEventPublisher(KafkaTemplate<String, String> kafkaTemplate, 
                                    ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishOrderCreated(Order order) {
        try {
            String message = objectMapper.writeValueAsString(new OrderEvent(
                "ORDER_CREATED",
                order.getId(),
                order.getCustomerId(),
                order.getTotalAmount().toString()
            ));
            kafkaTemplate.send("order-events", order.getId(), message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize order event", e);
        }
    }

    @Override
    public void publishOrderStatusChanged(Order order) {
        try {
            String message = objectMapper.writeValueAsString(new OrderEvent(
                "ORDER_STATUS_CHANGED",
                order.getId(),
                order.getCustomerId(),
                order.getStatus().name()
            ));
            kafkaTemplate.send("order-events", order.getId(), message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize order event", e);
        }
    }

    public static class OrderEvent {
        private String type;
        private String orderId;
        private String customerId;
        private String data;

        public OrderEvent() {}

        public OrderEvent(String type, String orderId, String customerId, String data) {
            this.type = type;
            this.orderId = orderId;
            this.customerId = customerId;
            this.data = data;
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
    }
}
```

### Product Client for Inter-Service Communication

```java
// order-service/src/main/java/com/example/order/client/ProductClient.java
package com.example.order.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductClient {
    private final RestTemplate restTemplate;
    private final String productServiceUrl = "http://product-service:8081";

    public ProductClient() {
        this.restTemplate = new RestTemplate();
    }

    public boolean checkStock(String productId, int quantity) {
        try {
            String url = String.format("%s/api/products/%s/check-stock?quantity=%d", 
                productServiceUrl, productId, quantity);
            return restTemplate.getForObject(url, Boolean.class);
        } catch (Exception e) {
            return false;
        }
    }
}
```

### API Gateway

```java
// api-gateway/src/main/java/com/example/gateway/ApiGatewayApplication.java
package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("product-service", r -> r
                .path("/api/products/**")
                .uri("http://product-service:8081"))
            .route("order-service", r -> r
                .path("/api/orders/**")
                .uri("http://order-service:8082"))
            .build();
    }
}
```

---

## 3. Event-Driven Implementation <a name="event-driven-code"></a>

### Event Bus Implementation

```java
// event-bus/src/main/java/com/example/events/EventBus.java
package com.example.events;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
    private static EventBus instance;
    private final ConcurrentHashMap<String, List<EventHandler>> handlers = new ConcurrentHashMap<>();

    private EventBus() {}

    public static EventBus getInstance() {
        if (instance == null) {
            synchronized (EventBus.class) {
                if (instance == null) {
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }

    public void subscribe(String eventType, EventHandler handler) {
        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

    public void publish(Event event) {
        List<EventHandler> eventHandlers = handlers.get(event.getType());
        if (eventHandlers != null) {
            for (EventHandler handler : eventHandlers) {
                try {
                    handler.handle(event);
                } catch (Exception e) {
                    System.err.println("Error handling event: " + e.getMessage());
                }
            }
        }
    }
}
```

```java
// event-bus/src/main/java/com/example/events/Event.java
package com.example.events;

import java.time.LocalDateTime;

public class Event {
    private String id;
    private String type;
    private Object payload;
    private LocalDateTime timestamp;

    public Event() {
        this.timestamp = LocalDateTime.now();
    }

    public Event(String type, Object payload) {
        this.id = java.util.UUID.randomUUID().toString();
        this.type = type;
        this.payload = payload;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Object getPayload() { return payload; }
    public void setPayload(Object payload) { this.payload = payload; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
```

```java
// event-bus/src/main/java/com/example/events/EventHandler.java
package com.example.events;

@FunctionalInterface
public interface EventHandler {
    void handle(Event event) throws Exception;
}
```

### Domain Events

```java
// events/src/main/java/com/example/domain/OrderPlacedEvent.java
package com.example.domain;

import java.math.BigDecimal;

public class OrderPlacedEvent {
    private String orderId;
    private String customerId;
    private BigDecimal totalAmount;

    public OrderPlacedEvent() {}

    public OrderPlacedEvent(String orderId, String customerId, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
```

```java
// events/src/main/java/com/example/domain/PaymentCompletedEvent.java
package com.example.domain;

import java.math.BigDecimal;

public class PaymentCompletedEvent {
    private String paymentId;
    private String orderId;
    private BigDecimal amount;
    private String status;

    public PaymentCompletedEvent() {}

    public PaymentCompletedEvent(String paymentId, String orderId, BigDecimal amount, String status) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

### Event Handler Implementations

```java
// handlers/src/main/java/com/example/handlers/OrderEventHandler.java
package com.example.handlers;

import com.example.events.Event;
import com.example.events.EventBus;
import com.example.events.EventHandler;
import com.example.domain.OrderPlacedEvent;

public class OrderEventHandler implements EventHandler {
    private final EventBus eventBus;

    public OrderEventHandler(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.subscribe("ORDER_PLACED", this);
    }

    @Override
    public void handle(Event event) {
        OrderPlacedEvent orderEvent = (OrderPlacedEvent) event.getPayload();
        System.out.println("Processing order: " + orderEvent.getOrderId());
        
        // Process order logic
        eventBus.publish(new Event("INVENTORY_RESERVED", orderEvent));
    }
}
```

```java
// handlers/src/main/java/com/example/handlers/NotificationEventHandler.java
package com.example.handlers;

import com.example.events.Event;
import com.example.events.EventHandler;

public class NotificationEventHandler implements EventHandler {
    @Override
    public void handle(Event event) {
        switch (event.getType()) {
            case "ORDER_PLACED":
                sendOrderConfirmation(event);
                break;
            case "PAYMENT_COMPLETED":
                sendPaymentReceipt(event);
                break;
            case "SHIPMENT_CREATED":
                sendShippingNotification(event);
                break;
        }
    }

    private void sendOrderConfirmation(Event event) {
        System.out.println("Sending order confirmation email...");
    }

    private void sendPaymentReceipt(Event event) {
        System.out.println("Sending payment receipt...");
    }

    private void sendShippingNotification(Event event) {
        System.out.println("Sending shipping notification...");
    }
}
```

---

## 4. CQRS Implementation <a name="cqrs-code"></a>

### Command Side

```java
// cqrs/src/main/java/com/example/cqrs/command/CommandHandler.java
package com.example.cqrs.command;

import com.example.cqrs.model.Order;

public interface CommandHandler<T> {
    void handle(T command);
}
```

```java
// cqrs/src/main/java/com/example/cqrs/command/CreateOrderCommand.java
package com.example.cqrs.command;

import java.util.List;

public class CreateOrderCommand {
    private String customerId;
    private List<OrderItemCommand> items;

    public static class OrderItemCommand {
        private String productId;
        private int quantity;

        public OrderItemCommand() {}

        public OrderItemCommand(String productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public List<OrderItemCommand> getItems() { return items; }
    public void setItems(List<OrderItemCommand> items) { this.items = items; }
}
```

```java
// cqrs/src/main/java/com/example/cqrs/command/CreateOrderHandler.java
package com.example.cqrs.command;

import com.example.cqrs.event.OrderEventStore;
import com.example.cqrs.event.OrderCreatedEvent;
import com.example.cqrs.model.Order;
import java.time.LocalDateTime;
import java.util.UUID;

public class CreateOrderHandler implements CommandHandler<CreateOrderCommand> {
    private final OrderEventStore eventStore;

    public CreateOrderHandler(OrderEventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(CreateOrderCommand command) {
        String orderId = UUID.randomUUID().toString();
        
        OrderCreatedEvent event = new OrderCreatedEvent(
            orderId,
            command.getCustomerId(),
            command.getItems(),
            LocalDateTime.now()
        );
        
        eventStore.saveEvent(orderId, event);
    }
}
```

```java
// cqrs/src/main/java/com/example/cqrs/command/UpdateOrderStatusCommand.java
package com.example.cqrs.command;

public class UpdateOrderStatusCommand {
    private String orderId;
    private String newStatus;

    public UpdateOrderStatusCommand(String orderId, String newStatus) {
        this.orderId = orderId;
        this.newStatus = newStatus;
    }

    public String getOrderId() { return orderId; }
    public String getNewStatus() { return newStatus; }
}
```

### Event Store

```java
// cqrs/src/main/java/com/example/cqrs/event/OrderEventStore.java
package com.example.cqrs.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class OrderEventStore {
    private final ConcurrentHashMap<String, List<Object>> events = new ConcurrentHashMap<>();

    public void saveEvent(String aggregateId, Object event) {
        events.computeIfAbsent(aggregateId, k -> new ArrayList<>()).add(event);
    }

    public List<Object> getEventsForOrder(String aggregateId) {
        return new ArrayList<>(events.getOrDefault(aggregateId, new ArrayList<>()));
    }
}
```

```java
// cqrs/src/main/java/com/example/cqrs/event/OrderCreatedEvent.java
package com.example.cqrs.event;

import java.time.LocalDateTime;
import java.util.List;

public class OrderCreatedEvent {
    private String orderId;
    private String customerId;
    private List<CreateOrderCommand.OrderItemCommand> items;
    private LocalDateTime timestamp;

    public OrderCreatedEvent() {}

    public OrderCreatedEvent(String orderId, String customerId, 
                             List<CreateOrderCommand.OrderItemCommand> items,
                             LocalDateTime timestamp) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
        this.timestamp = timestamp;
    }

    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public List<CreateOrderCommand.OrderItemCommand> getItems() { return items; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
```

### Query Side

```java
// cqrs/src/main/java/com/example/cqrs/query/OrderQueryService.java
package com.example.cqrs.query;

import com.example.cqrs.projection.OrderProjection;
import com.example.cqrs.model.OrderView;
import java.util.List;

public class OrderQueryService {
    private final OrderProjection orderProjection;

    public OrderQueryService(OrderProjection orderProjection) {
        this.orderProjection = orderProjection;
    }

    public OrderView getOrderById(String orderId) {
        return orderProjection.findById(orderId);
    }

    public List<OrderView> getOrdersByCustomer(String customerId) {
        return orderProjection.findByCustomerId(customerId);
    }

    public List<OrderView> getAllOrders() {
        return orderProjection.findAll();
    }
}
```

### Read Model / Projection

```java
// cqrs/src/main/java/com/example/cqrs/projection/OrderProjection.java
package com.example.cqrs.projection;

import com.example.cqrs.model.OrderView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class OrderProjection {
    private final ConcurrentHashMap<String, OrderView> orderViews = new ConcurrentHashMap<>();

    public void project(String orderId, Object event) {
        if (event instanceof com.example.cqrs.event.OrderCreatedEvent) {
            projectOrderCreated(orderId, (com.example.cqrs.event.OrderCreatedEvent) event);
        }
    }

    private void projectOrderCreated(String orderId, com.example.cqrs.event.OrderCreatedEvent event) {
        OrderView orderView = new OrderView();
        orderView.setOrderId(event.getOrderId());
        orderView.setCustomerId(event.getCustomerId());
        orderView.setStatus("CREATED");
        orderView.setTotalItems(event.getItems().size());
        orderViews.put(orderId, orderView);
    }

    public OrderView findById(String orderId) {
        return orderViews.get(orderId);
    }

    public List<OrderView> findByCustomerId(String customerId) {
        return orderViews.values().stream()
            .filter(o -> o.getCustomerId().equals(customerId))
            .collect(Collectors.toList());
    }

    public List<OrderView> findAll() {
        return new ArrayList<>(orderViews.values());
    }
}
```

```java
// cqrs/src/main/java/com/example/cqrs/model/OrderView.java
package com.example.cqrs.model;

import java.time.LocalDateTime;
import java.util.List;

public class OrderView {
    private String orderId;
    private String customerId;
    private String status;
    private int totalItems;
    private LocalDateTime createdAt;

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
```

### CQRS Dispatcher

```java
// cqrs/src/main/java/com/example/cqrs/CqrsDispatcher.java
package com.example.cqrs;

import com.example.cqrs.command.*;
import com.example.cqrs.event.OrderEventStore;
import com.example.cqrs.projection.OrderProjection;
import com.example.cqrs.query.OrderQueryService;

public class CqrsDispatcher {
    private final OrderEventStore eventStore;
    private final OrderProjection orderProjection;
    private final CreateOrderHandler createOrderHandler;
    private final OrderQueryService queryService;

    public CqrsDispatcher(OrderEventStore eventStore, OrderProjection orderProjection) {
        this.eventStore = eventStore;
        this.orderProjection = orderProjection;
        this.createOrderHandler = new CreateOrderHandler(eventStore);
        this.queryService = new OrderQueryService(orderProjection);
    }

    public void dispatch(Object command) {
        if (command instanceof CreateOrderCommand) {
            createOrderHandler.handle((CreateOrderCommand) command);
            
            List<Object> events = eventStore.getEventsForOrder(
                ((CreateOrderCommand) command).getCustomerId() 
            );
            
            for (Object event : events) {
                orderProjection.project(
                    ((com.example.cqrs.event.OrderCreatedEvent) event).getOrderId(),
                    event
                );
            }
        }
    }

    public OrderQueryService getQueryService() {
        return queryService;
    }
}
```

---

## Summary

This deep dive covered four key architecture patterns:

1. **Layered Architecture**: Complete implementation with Controller → Service → Repository layers, DTOs, and exception handling
2. **Microservices**: Product and Order services with inter-service communication, event publishing via Kafka
3. **Event-Driven**: Event Bus implementation with domain events and handlers
4. **CQRS**: Command and query separation with event sourcing, projections, and read models

Each implementation demonstrates the core principles and can be extended for production use.