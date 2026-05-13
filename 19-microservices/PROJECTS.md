# Microservices Module - PROJECTS.md

---

# Mini-Projects Overview

| Concept | Duration | Description |
|---------|----------|-------------|
| Service Discovery | 2 hours | Eureka server, service registration |
| Feign Client | 2 hours | Declarative REST client, load balancing |
| API Gateway | 2 hours | Spring Cloud Gateway, route configuration |
| Circuit Breaker | 2 hours | Resilience4j, fallback, bulkhead |
| Real-world: E-Commerce Platform | 20+ hours | Complete microservices with Kubernetes |

---

# Mini-Project: Spring Cloud Service Discovery

## Project Overview

**Duration**: 5-6 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Spring Cloud Gateway, Service Registration, Feign Client, Load Balancing, Circuit Breaker

This mini-project demonstrates microservices architecture using Spring Cloud with Eureka service discovery, Feign for inter-service communication, and Ribbon load balancing.

---

## Project Structure

```
19-microservices/
├── pom.xml
├── service-registry/
│   ├── pom.xml
│   └── src/main/java/com/learning/ServiceRegistryApplication.java
├── product-service/
│   ├── pom.xml
│   └── src/main/java/com/learning/
│       ├── ProductServiceApplication.java
│       ├── controller/ProductController.java
│       ├── service/ProductService.java
│       └── model/Product.java
├── order-service/
│   ├── pom.xml
│   └── src/main/java/com/learning/
│       ├── OrderServiceApplication.java
│       ├── controller/OrderController.java
│       ├── service/OrderService.java
│       ├── client/ProductClient.java
│       └── model/Order.java
└── api-gateway/
    ├── pom.xml
    └── src/main/java/com/learning/ApiGatewayApplication.java
```

---

## Step 1: Parent POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>microservices-parent</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    
    <modules>
        <module>service-registry</module>
        <module>product-service</module>
        <module>order-service</module>
        <module>api-gateway</module>
    </modules>
    
    <properties>
        <java.version>17</java.version>
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

---

## Step 2: Service Registry (Eureka Server)

```xml
<!-- service-registry/pom.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.learning</groupId>
        <artifactId>microservices-parent</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    
    <artifactId>service-registry</artifactId>
    <packaging>jar</packaging>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
</project>
```

```java
// service-registry/src/main/java/com/learning/ServiceRegistryApplication.java
package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistryApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistryApplication.class, args);
    }
}
```

```yaml
# service-registry/src/main/resources/application.yml
server:
  port: 8761

spring:
  application:
    name: service-registry

eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
  server:
    enable-self-preservation: false
    wait-time-in-ms-when-sync-empty: 0
```

---

## Step 3: Product Service

```xml
<!-- product-service/pom.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.learning</groupId>
        <artifactId>microservices-parent</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    
    <artifactId>product-service</artifactId>
    <packaging>jar</packaging>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
    </dependencies>
</project>
```

```java
// product-service/src/main/java/com/learning/model/Product.java
package com.learning.model;

import java.math.BigDecimal;

public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String category;
    
    public Product() {}
    
    public Product(Long id, String name, String description, BigDecimal price, Integer stock, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
```

```java
// product-service/src/main/java/com/learning/service/ProductService.java
package com.learning.service;

import com.learning.model.Product;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductService {
    
    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    
    public ProductService() {
        initializeData();
    }
    
    private void initializeData() {
        products.put(1L, new Product(1L, "Laptop Pro", "High-performance laptop", new BigDecimal("1299.99"), 50, "Electronics"));
        products.put(2L, new Product(2L, "Smartphone X", "Latest smartphone", new BigDecimal("899.99"), 100, "Electronics"));
        products.put(3L, new Product(3L, "Wireless Headphones", "Premium headphones", new BigDecimal("299.99"), 75, "Electronics"));
        products.put(4L, new Product(4L, "Coffee Maker", "Automatic coffee maker", new BigDecimal("89.99"), 30, "Home"));
        products.put(5L, new Product(5L, "Running Shoes", "Professional running shoes", new BigDecimal("149.99"), 60, "Sports"));
    }
    
    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }
    
    public Product getProductById(Long id) {
        return products.get(id);
    }
    
    public List<Product> getProductsByCategory(String category) {
        return products.values().stream()
            .filter(p -> p.getCategory().equalsIgnoreCase(category))
            .toList();
    }
    
    public Product saveProduct(Product product) {
        Long id = products.size() + 1L;
        product.setId(id);
        products.put(id, product);
        return product;
    }
    
    public Product updateStock(Long productId, Integer quantity) {
        Product product = products.get(productId);
        if (product != null) {
            product.setStock(product.getStock() + quantity);
        }
        return product;
    }
}
```

```java
// product-service/src/main/java/com/learning/controller/ProductController.java
package com.learning.controller;

import com.learning.model.Product;
import com.learning.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }
    
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }
    
    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return productService.getProductsByCategory(category);
    }
    
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }
    
    @PutMapping("/{id}/stock")
    public Product updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        return productService.updateStock(id, quantity);
    }
}
```

```java
// product-service/src/main/java/com/learning/ProductServiceApplication.java
package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ProductServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
```

```yaml
# product-service/src/main/resources/application.yml
server:
  port: 8081

spring:
  application:
    name: product-service

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

---

## Step 4: Order Service (With Feign Client)

```xml
<!-- order-service/pom.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.learning</groupId>
        <artifactId>microservices-parent</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    
    <artifactId>order-service</artifactId>
    <packaging>jar</packaging>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
    </dependencies>
</project>
```

```java
// order-service/src/main/java/com/learning/model/Order.java
package com.learning.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime orderDate;
    private String shippingAddress;
    
    public Order() {}
    
    public Order(Long customerId, List<OrderItem> items, String shippingAddress) {
        this.customerId = customerId;
        this.items = items;
        this.shippingAddress = shippingAddress;
        this.status = "PENDING";
        this.orderDate = LocalDateTime.now();
        this.orderNumber = "ORD-" + System.currentTimeMillis();
    }
    
    public static class OrderItem {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal subtotal;
        
        public OrderItem() {}
        
        public OrderItem(Long productId, String productName, Integer quantity, BigDecimal price) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
            this.subtotal = price.multiply(new BigDecimal(quantity));
        }
        
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    
    public void calculateTotal() {
        totalAmount = items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

```java
// order-service/src/main/java/com/learning/client/ProductClient.java
package com.learning.client;

import com.learning.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "product-service")
public interface ProductClient {
    
    @GetMapping("/api/products/{id}")
    Product getProductById(@PathVariable("id") Long id);
    
    @GetMapping("/api/products")
    List<Product> getAllProducts();
    
    @GetMapping("/api/products/category/{category}")
    List<Product> getProductsByCategory(@PathVariable("category") String category);
}
```

```java
// order-service/src/main/java/com/learning/service/OrderService.java
package com.learning.service;

import com.learning.client.ProductClient;
import com.learning.model.Order;
import com.learning.model.Product;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {
    
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final ProductClient productClient;
    
    public OrderService(ProductClient productClient) {
        this.productClient = productClient;
    }
    
    public Order createOrder(Long customerId, List<Order.OrderItem> items, String shippingAddress) {
        for (Order.OrderItem item : items) {
            Product product = productClient.getProductById(item.getProductId());
            if (product == null) {
                throw new IllegalArgumentException("Product not found: " + item.getProductId());
            }
            item.setProductName(product.getName());
            item.setPrice(product.getPrice());
        }
        
        Order order = new Order(customerId, items, shippingAddress);
        order.calculateTotal();
        
        Long id = orders.size() + 1L;
        order.setId(id);
        orders.put(id, order);
        
        return order;
    }
    
    public Order getOrderById(Long id) {
        return orders.get(id);
    }
    
    public List<Order> getOrdersByCustomer(Long customerId) {
        return orders.values().stream()
            .filter(o -> o.getCustomerId().equals(customerId))
            .toList();
    }
    
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orders.get(orderId);
        if (order != null) {
            order.setStatus(status);
        }
        return order;
    }
    
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }
}
```

```java
// order-service/src/main/java/com/learning/controller/OrderController.java
package com.learning.controller;

import com.learning.model.Order;
import com.learning.service.OrderService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @PostMapping
    public Order createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(
            request.customerId(),
            request.items(),
            request.shippingAddress()
        );
    }
    
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }
    
    @GetMapping("/customer/{customerId}")
    public List<Order> getOrdersByCustomer(@PathVariable Long customerId) {
        return orderService.getOrdersByCustomer(customerId);
    }
    
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
    
    @PutMapping("/{id}/status")
    public Order updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        return orderService.updateOrderStatus(id, status);
    }
    
    public record OrderRequest(
        Long customerId,
        List<Order.OrderItem> items,
        String shippingAddress
    ) {}
}
```

```java
// order-service/src/main/java/com/learning/OrderServiceApplication.java
package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class OrderServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

```yaml
# order-service/src/main/resources/application.yml
server:
  port: 8082

spring:
  application:
    name: order-service

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

---

## Step 5: API Gateway

```xml
<!-- api-gateway/pom.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.learning</groupId>
        <artifactId>microservices-parent</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    
    <artifactId>api-gateway</artifactId>
    <packaging>jar</packaging>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
    </dependencies>
</project>
```

```java
// api-gateway/src/main/java/com/learning/ApiGatewayApplication.java
package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
```

```yaml
# api-gateway/src/main/resources/application.yml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/products/**
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/orders/**

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

---

## Build Instructions

```bash
cd 19-microservices

# Build all services
mvn clean install

# Start services in order:
# 1. Service Registry (Eureka)
cd service-registry
mvn spring-boot:run

# 2. Product Service
cd ../product-service
mvn spring-boot:run

# 3. Order Service
cd ../order-service
mvn spring-boot:run

# 4. API Gateway
cd ../api-gateway
mvn spring-boot:run
```

Test endpoints:
- Eureka Dashboard: http://localhost:8761
- Product Service: http://localhost:8081/api/products
- Order Service: http://localhost:8082/api/orders
- API Gateway: http://localhost:8080/orders

---

# Real-World Project: Microservices E-Commerce Platform

## Project Overview

**Duration**: 20+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Spring Cloud Config, Circuit Breaker, Distributed Tracing, Message Queues, Kubernetes Deployment

This comprehensive microservices platform implements a complete e-commerce system with multiple services, configuration management, resilience patterns, and containerization.

---

## Project Structure

```
19-microservices/
├── config-server/
├── service-registry/
├── product-service/
├── order-service/
├── customer-service/
├── payment-service/
├── notification-service/
├── api-gateway/
├── kubernetes/
│   ├── deployment.yaml
│   └── service.yaml
└── docker-compose.yml
```

---

## POM.xml (Parent)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>ecommerce-microservices</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    
    <properties>
        <java.version>17</java.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
    </properties>
    
    <modules>
        <module>config-server</module>
        <module>service-registry</module>
        <module>product-service</module>
        <module>order-service</module>
        <module>customer-service</module>
        <module>payment-service</module>
        <module>notification-service</module>
        <module>api-gateway</module>
    </modules>
    
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

---

## Customer Service

```xml
<!-- customer-service/pom.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.learning</groupId>
        <artifactId>ecommerce-microservices</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    
    <artifactId>customer-service</artifactId>
    
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
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
    </dependencies>
</project>
```

```java
// customer-service/src/main/java/com/learning/model/Customer.java
package com.learning.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String phone;
    
    private String address;
    
    private String city;
    
    private String state;
    
    private String zipCode;
    
    private String country;
    
    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 0;
    
    private String tier = "BRONZE";
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public String getFullName() { return firstName + " " + lastName; }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public Integer getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(Integer loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }
    
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

```java
// customer-service/src/main/java/com/learning/repository/CustomerRepository.java
package com.learning.repository;

import com.learning.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
}
```

```java
// customer-service/src/main/java/com/learning/service/CustomerService.java
package com.learning.service;

import com.learning.model.Customer;
import com.learning.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
    
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }
    
    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email).orElse(null);
    }
    
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
    
    public Customer updateCustomer(Long id, Customer customer) {
        Customer existing = customerRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setFirstName(customer.getFirstName());
            existing.setLastName(customer.getLastName());
            existing.setPhone(customer.getPhone());
            existing.setAddress(customer.getAddress());
            existing.setCity(customer.getCity());
            existing.setState(customer.getState());
            existing.setZipCode(customer.getZipCode());
            existing.setCountry(customer.getCountry());
            return customerRepository.save(existing);
        }
        return null;
    }
    
    public void addLoyaltyPoints(Long customerId, int points) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            customer.setLoyaltyPoints(customer.getLoyaltyPoints() + points);
            
            String newTier = "BRONZE";
            if (customer.getLoyaltyPoints() >= 1000) newTier = "PLATINUM";
            else if (customer.getLoyaltyPoints() >= 500) newTier = "GOLD";
            else if (customer.getLoyaltyPoints() >= 200) newTier = "SILVER";
            
            customer.setTier(newTier);
            customerRepository.save(customer);
        }
    }
    
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}
```

```java
// customer-service/src/main/java/com/learning/controller/CustomerController.java
package com.learning.controller;

import com.learning.model.Customer;
import com.learning.service.CustomerService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    private final CustomerService customerService;
    
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
    
    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }
    
    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }
    
    @GetMapping("/email/{email}")
    public Customer getCustomerByEmail(@PathVariable String email) {
        return customerService.getCustomerByEmail(email);
    }
    
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }
    
    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        return customerService.updateCustomer(id, customer);
    }
    
    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }
    
    @PostMapping("/{id}/points")
    public void addLoyaltyPoints(@PathVariable Long id, @RequestParam int points) {
        customerService.addLoyaltyPoints(id, points);
    }
}
```

```java
// customer-service/src/main/java/com/learning/CustomerServiceApplication.java
package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class CustomerServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}
```

---

## Payment Service with Circuit Breaker

```xml
<!-- payment-service/pom.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.learning</groupId>
        <artifactId>ecommerce-microservices</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    
    <artifactId>payment-service</artifactId>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
        </dependency>
    </dependencies>
</project>
```

```java
// payment-service/src/main/java/com/learning/service/PaymentService.java
package com.learning.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {
    
    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    public PaymentResult processPayment(Long orderId, BigDecimal amount, String paymentMethod) {
        simulatePaymentProcessing();
        
        return new PaymentResult(
            "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
            "COMPLETED",
            amount
        );
    }
    
    private void simulatePaymentProcessing() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public PaymentResult paymentFallback(Long orderId, BigDecimal amount, String paymentMethod, Throwable t) {
        System.out.println("Payment service fallback triggered for order: " + orderId);
        return new PaymentResult("FALLBACK-" + System.currentTimeMillis(), "PENDING", amount);
    }
    
    public record PaymentResult(String transactionId, String status, BigDecimal amount) {}
}
```

```java
// payment-service/src/main/java/com/learning/controller/PaymentController.java
package com.learning.controller;

import com.learning.service.PaymentService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @PostMapping
    public PaymentService.PaymentResult processPayment(
            @RequestParam Long orderId,
            @RequestParam BigDecimal amount,
            @RequestParam String paymentMethod) {
        return paymentService.processPayment(orderId, amount, paymentMethod);
    }
}
```

```java
// payment-service/src/main/java/com/learning/PaymentServiceApplication.java
package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloudCircuitBreaker;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class PaymentServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
```

---

## Notification Service

```java
// notification-service/src/main/java/com/learning/service/NotificationService.java
package com.learning.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class NotificationService {
    
    public void sendOrderConfirmation(String email, String orderNumber, double total) {
        String message = String.format(
            "Order Confirmation\nOrder: %s\nTotal: $%.2f\nTime: %s",
            orderNumber, total, LocalDateTime.now()
        );
        
        System.out.println("Email to " + email + ": " + message);
    }
    
    public void sendShippingNotification(String email, String orderNumber, String trackingNumber) {
        String message = String.format(
            "Your order %s has shipped!\nTracking: %s",
            orderNumber, trackingNumber
        );
        
        System.out.println("Email to " + email + ": " + message);
    }
    
    public void sendPaymentConfirmation(String email, String transactionId, double amount) {
        String message = String.format(
            "Payment Successful\nTransaction: %s\nAmount: $%.2f",
            transactionId, amount
        );
        
        System.out.println("Email to " + email + ": " + message);
    }
    
    public void sendLoyaltyPointsUpdate(String email, int newPoints, String tier) {
        String message = String.format(
            "Loyalty Points Update\nPoints: %d\nTier: %s",
            newPoints, tier
        );
        
        System.out.println("Email to " + email + ": " + message);
    }
}
```

---

## Docker Compose

```yaml
# docker-compose.yml
version: '3.8'

services:
  eureka:
    build: ./service-registry
    ports:
      - "8761:8761"
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  product-service:
    build: ./product-service
    ports:
      - "8081:8081"
    depends_on:
      - eureka
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  order-service:
    build: ./order-service
    ports:
      - "8082:8082"
    depends_on:
      - eureka
      - product-service
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  customer-service:
    build: ./customer-service
    ports:
      - "8083:8083"
    depends_on:
      - eureka
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  payment-service:
    build: ./payment-service
    ports:
      - "8084:8084"
    depends_on:
      - eureka
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  api-gateway:
    build: ./api-gateway
    ports:
      - "8080:8080"
    depends_on:
      - eureka
    environment:
      - SPRING_PROFILES_ACTIVE=docker
```

---

## Kubernetes Deployment

```yaml
# kubernetes/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product-service
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
        image: product-service:latest
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        - name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
          value: "http://eureka-service:8761/eureka/"
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

---

## Build Instructions

```bash
cd 19-microservices
mvn clean install -DskipTests

# Run with Docker Compose
docker-compose up --build

# Or run individual services
cd service-registry && mvn spring-boot:run
cd product-service && mvn spring-boot:run
cd customer-service && mvn spring-boot:run
cd order-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
```

This comprehensive microservices platform demonstrates enterprise-grade patterns including service discovery, circuit breakers, API gateway routing, distributed communication, and container orchestration.

---

# Production Patterns: Advanced Service Communication

## Retry with Exponential Backoff

```java
// service/ResilientServiceCall.java
package com.learning.service;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.function.Supplier;

@Service
public class ResilientServiceCall {
    
    private final Retry retryTemplate;
    
    public ResilientServiceCall() {
        RetryConfig config = RetryConfig.custom()
            .maxAttempts(5)
            .waitDuration(Duration.ofSeconds(2))
            .retryExceptions(Exception.class)
            .failAfterMaxAttempts(true)
            .intervalFunction(IntervalFunction.ofExponentialBackoff(2, Duration.ofSeconds(1)))
            .build();
        
        RetryRegistry registry = RetryRegistry.of(config);
        this.retryTemplate = registry.retry("service-call");
    }
    
    public <T> T executeWithRetry(Supplier<T> serviceCall) {
        return Retry.decorateSupplier(retryTemplate, serviceCall).apply();
    }
    
    public <T> T executeWithRetry(Supplier<T> serviceCall, Supplier<T> fallback) {
        try {
            return executeWithRetry(serviceCall);
        } catch (Exception e) {
            System.out.println("All retries exhausted, using fallback");
            return fallback.get();
        }
    }
}
```

## Bulkhead Isolation

```java
// service/BulkheadService.java
package com.learning.service;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import org.springframework.stereotype.Service;
import java.util.function.Supplier;

@Service
public class BulkheadService {
    
    private final Bulkhead externalApiBulkhead;
    private final Bulkhead internalCallBulkhead;
    
    public BulkheadService() {
        BulkheadConfig externalConfig = BulkheadConfig.custom()
            .maxConcurrentCalls(10)
            .maxWaitDuration(Duration.ofMillis(100))
            .build();
        
        BulkheadConfig internalConfig = BulkheadConfig.custom()
            .maxConcurrentCalls(50)
            .maxWaitDuration(Duration.ofMillis(50))
            .build();
        
        BulkheadRegistry registry = BulkheadRegistry.of(externalConfig, internalConfig);
        this.externalApiBulkhead = registry.bulkhead("external-api");
        this.internalCallBulkhead = registry.bulkhead("internal-call");
    }
    
    public <T> T callExternalApi(Supplier<T> serviceCall) {
        return Bulkhead.decorateSupplier(externalApiBulkhead, serviceCall).apply();
    }
    
    public <T> T callInternalService(Supplier<T> serviceCall) {
        return Bulkhead.decorateSupplier(internalCallBulkhead, serviceCall).apply();
    }
}
```

## Rate Limiter Pattern

```java
// service/RateLimitedService.java
package com.learning.service;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.function.Supplier;

@Service
public class RateLimitedService {
    
    private final RateLimiter externalAPIRateLimiter;
    
    public RateLimitedService() {
        RateLimiterConfig config = RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofSeconds(1))
            .limitForPeriod(10)
            .timeoutDuration(Duration.ofSeconds(5))
            .build();
        
        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        this.externalAPIRateLimiter = registry.rateLimiter("external-api");
    }
    
    public <T> T callWithRateLimit(Supplier<T> serviceCall) {
        return RateLimiter.decorateSupplier(externalAPIRateLimiter, serviceCall).apply();
    }
}
```

## Time Limiter Pattern

```java
// service/TimedServiceCall.java
package com.learning.service;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
public class TimedServiceCall {
    
    private final TimeLimiter timeLimiter;
    
    public TimedServiceCall() {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofSeconds(3))
            .cancelRunningFuture(true)
            .build();
        
        TimeLimiterRegistry registry = TimeLimiterRegistry.of(config);
        this.timeLimiter = registry.timeLimiter("service-call");
    }
    
    public <T> CompletableFuture<T> callWithTimeout(Supplier<CompletableFuture<T>> serviceCall) {
        return TimeLimiter.decorateFutureSupplier(timeLimiter, serviceCall).apply();
    }
    
    public <T> T callSyncWithTimeout(Supplier<T> serviceCall) {
        try {
            CompletableFuture<T> future = callWithTimeout(() -> 
                CompletableFuture.supplyAsync(serviceCall));
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Timeout or error occurred", e);
        }
    }
}
```

## Combined Resilience4j Pattern

```java
// service/CombinedResilienceService.java
package com.learning.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.function.Supplier;

@Service
public class CombinedResilienceService {
    
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;
    
    public CombinedResilienceService() {
        CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
            .slidingWindowSize(10)
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .permittedNumberOfCallsInHalfOpenState(5)
            .build();
        
        CircuitBreakerRegistry cbRegistry = CircuitBreakerRegistry.of(cbConfig);
        this.circuitBreaker = cbRegistry.circuitBreaker("service");
        
        RetryConfig retryConfig = RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofSeconds(1))
            .build();
        
        RetryRegistry retryRegistry = RetryRegistry.of(retryConfig);
        this.retry = retryRegistry.retry("service");
    }
    
    public <T> T executeWithResilience(Supplier<T> serviceCall) {
        Supplier<T> decoratedSupplier = CircuitBreaker.decorateSupplier(
            circuitBreaker, Retry.decorateSupplier(retry, serviceCall));
        
        return decoratedSupplier.get();
    }
    
    public <T> T executeWithResilienceAndFallback(Supplier<T> serviceCall, 
                                                  Supplier<T> fallback) {
        try {
            return executeWithResilience(serviceCall);
        } catch (Exception e) {
            System.out.println("Circuit breaker open or retries exhausted");
            return fallback.get();
        }
    }
}
```

## Service Mesh Configuration (Istio-like)

```yaml
# kubernetes/istio/virtual-service.yaml
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: product-service
spec:
  hosts:
    - product-service
  http:
    - match:
        - headers:
            x-canary:
              exact: "true"
      route:
        - destination:
            host: product-service-v2
          weight: 100
    - route:
        - destination:
            host: product-service-v1
          weight: 90
        - destination:
            host: product-service-v2
          weight: 10

---
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: product-service
spec:
  host: product-service
  trafficPolicy:
    connectionPool:
      tcp:
        maxConnections: 100
      http:
        h2UpgradePolicy: UPGRADE
        http1MaxPendingRequests: 100
        http2MaxRequests: 1000
    outlierDetection:
      consecutiveGatewayErrors: 5
      interval: 30s
      baseEjectionTime: 60s
```

## gRPC Service Implementation

```java
// grpc/ProductGrpcService.java
package com.learning.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import java.util.concurrent.ConcurrentHashMap;

@GrpcService
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {
    
    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    
    @Override
    public void getProduct(ProductRequest request, 
                          StreamObserver<ProductResponse> responseObserver) {
        Product product = products.get(request.getId());
        
        if (product == null) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND
                .withDescription("Product not found")
                .asRuntimeException());
            return;
        }
        
        ProductResponse response = ProductResponse.newBuilder()
            .setId(product.getId())
            .setName(product.getName())
            .setPrice(product.getPrice().toString())
            .setStock(product.getStock())
            .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    
    @Override
    public void listProducts(ListProductsRequest request,
                            StreamObserver<ProductResponse> responseObserver) {
        products.values().forEach(product -> {
            ProductResponse response = ProductResponse.newBuilder()
                .setId(product.getId())
                .setName(product.getName())
                .setPrice(product.getPrice().toString())
                .setStock(product.getStock())
                .build();
            responseObserver.onNext(response);
        });
        responseObserver.onCompleted();
    }
    
    @Override
    public StreamObserver<ProductRequest> createProducts(
            StreamObserver<ProductResponse> responseObserver) {
        return new StreamObserver<ProductRequest>() {
            @Override
            public void onNext(ProductRequest request) {
                Product product = new Product();
                product.setId(request.getId());
                product.setName(request.getName());
                products.put(product.getId(), product);
                
                ProductResponse response = ProductResponse.newBuilder()
                    .setId(product.getId())
                    .setName(product.getName())
                    .build();
                responseObserver.onNext(response);
            }
            
            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }
            
            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
```

## GraphQL Federation Schema

```graphql
# products subgraph schema
type Product @key(fields: "id") {
    id: ID!
    name: String!
    description: String
    price: Float!
    stock: Int!
    category: String
}

type Query {
    product(id: ID!): Product
    productsByCategory(category: String!): [Product!]!
}
```

```java
// graphql/ProductQuery.java
package com.learning.graphql;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphQLConfig {
    
    @Bean
    public GraphQLSchema productsGraphQLSchema(ProductResolver resolver) {
        return GraphQLSchema.newSchema()
            .query(typeWiring -> typeWiring
                .dataFetcher("product", resolver.getProductDataFetcher())
                .dataFetcher("productsByCategory", 
                    resolver.getProductsByCategoryDataFetcher()))
            .additionalType(Product.type)
            .build();
    }
}

@Component
public class ProductResolver {
    
    private final ProductService productService;
    
    @QueryDataFetcher
    public Product getProduct(graphql.schema.DataFetchingEnvironment env, 
                            @Argument("id") Long id) {
        return productService.getProductById(id);
    }
    
    @QueryDataFetcher
    public List<Product> getProductsByCategory(graphql.schema.DataFetchingEnvironment env,
                                              @Argument("category") String category) {
        return productService.getProductsByCategory(category);
    }
}
```

## WebClient for Reactive Service Calls

```java
// service/ReactiveProductClient.java
package com.learning.service;

import com.learning.model.Product;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Service
public class ReactiveProductClient {
    
    private final WebClient webClient;
    
    public ReactiveProductClient(WebClient.Builder builder) {
        this.webClient = builder
            .baseUrl("http://product-service/api/products")
            .defaultHeader("X-Service-Name", "order-service")
            .build();
    }
    
    public Mono<Product> getProduct(Long productId) {
        return webClient.get()
            .uri("/{id}", productId)
            .retrieve()
            .bodyToMono(Product.class)
            .timeout(Duration.ofSeconds(3))
            .onErrorResume(e -> Mono.empty());
    }
    
    public Flux<Product> getProductsByCategory(String category) {
        return webClient.get()
            .uri("/category/{category}", category)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<Product>>() {})
            .flatMapMany(Flux::fromIterable)
            .timeout(Duration.ofSeconds(5));
    }
    
    public Mono<Product> createProduct(Product product) {
        return webClient.post()
            .uri("/")
            .bodyValue(product)
            .retrieve()
            .bodyToMono(Product.class)
            .retry(3)
            .timeout(Duration.ofSeconds(10));
    }
}
```

## Distributed Tracing Configuration

```yaml
# application.yml for distributed tracing
spring:
  sleuth:
    sampler:
      probability: 1.0
    propagation:
      type: B3
  zipkin:
    base-url: http://localhost:9411
    span:
      max:
        attachment-size: 10KB
        size: 500000

management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus
  metrics:
    tags:
      application: ${spring.application.name}
```

```java
// service/TracedServiceCall.java
package com.learning.service;

import brave.Span;
import brave.Tracer;
import org.springframework.stereotype.Service;
import java.util.function.Supplier;

@Service
public class TracedServiceCall {
    
    private final Tracer tracer;
    
    public TracedServiceCall(Tracer tracer) {
        this.tracer = tracer;
    }
    
    public <T> T executeWithTracing(String operationName, Supplier<T> serviceCall) {
        Span span = tracer.nextSpan().name(operationName).start();
        
        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            span.tag("service.operation", operationName);
            span.annotate("service.call.started");
            
            T result = serviceCall.get();
            
            span.annotate("service.call.completed");
            return result;
        } catch (Exception e) {
            span.error(e);
            span.annotate("service.call.failed");
            throw e;
        } finally {
            span.finish();
        }
    }
}
```