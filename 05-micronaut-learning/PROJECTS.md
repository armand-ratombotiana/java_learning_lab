# Micronaut Learning Projects

This directory contains projects focusing on Micronaut framework, a modern, JVM-based full-stack framework for building microservice applications. These projects demonstrate Micronaut's compile-time dependency injection, native GraalVM support, and cloud-native features.

## Mini-Project: Micronaut REST API with Database (2-4 hours)

### Overview

Build a reactive REST API using Micronaut with JPA/Hibernate integration, validation, and OpenAPI documentation. This project showcases Micronaut's fast startup time and low memory footprint.

### Project Structure

```
micronaut-rest-api/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── micronaut/
        │           └── learning/
        │               ├── Application.java
        │               ├── domain/
        │               │   └── Book.java
        │               ├── repository/
        │               │   └── BookRepository.java
        │               ├── service/
        │               │   └── BookService.java
        │               └── controller/
        │                   └── BookController.java
        └── resources/
            └── application.yml
```

### Implementation

```java
package com.micronaut.learning;

import io.micronaut.runtime.Micronaut;

public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
```

```java
package com.micronaut.learning.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "books")
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @NotBlank
    @Column(nullable = false)
    private String author;

    @Positive
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @NotNull
    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private boolean active = true;

    public Book() {
    }

    public Book(String title, String author, BigDecimal price, Integer stock) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public LocalDate getPublishedDate() { return publishedDate; }
    public void setPublishedDate(LocalDate publishedDate) { this.publishedDate = publishedDate; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
```

```java
package com.micronaut.learning.repository;

import com.micronaut.learning.domain.Book;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    
    Optional<Book> findByIsbn(String isbn);
    
    List<Book> findByAuthorIgnoreCase(String author);
    
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    Page<Book> findByActive(boolean active, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.price BETWEEN :minPrice AND :maxPrice")
    List<Book> findByPriceRange(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);
    
    long countByAuthor(String author);
    
    List<Book> findByStockLessThan(Integer stock);
}
```

```java
package com.micronaut.learning.service;

import com.micronaut.learning.domain.Book;
import com.micronaut.learning.repository.BookRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

@Singleton
public class BookService {
    
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public List<Book> findByAuthor(String author) {
        return bookRepository.findByAuthorIgnoreCase(author);
    }

    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    public Page<Book> findActiveBooks(Pageable pageable) {
        return bookRepository.findByActive(true, pageable);
    }

    public List<Book> findByPriceRange(java.math.BigDecimal min, java.math.BigDecimal max) {
        return bookRepository.findByPriceRange(min, max);
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public Book update(Book book) {
        return bookRepository.update(book);
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> findLowStockBooks(Integer threshold) {
        return bookRepository.findByStockLessThan(threshold);
    }

    public long countByAuthor(String author) {
        return bookRepository.countByAuthor(author);
    }
}
```

```java
package com.micronaut.learning.controller;

import com.micronaut.learning.domain.Book;
import com.micronaut.learning.service.BookService;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;

import jakarta.validation.Valid;
import java.util.List;

@Controller("/api/books")
@Validated
public class BookController {
    
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Get
    public List<Book> getAllBooks() {
        return bookService.findAll();
    }

    @Get("/{id}")
    public HttpResponse<Book> getBook(@PathVariable Long id) {
        return bookService.findById(id)
            .map(book -> HttpResponse.ok(book))
            .orElse(HttpResponse.notFound());
    }

    @Get("/isbn/{isbn}")
    public HttpResponse<Book> getBookByIsbn(@PathVariable String isbn) {
        return bookService.findByIsbn(isbn)
            .map(book -> HttpResponse.ok(book))
            .orElse(HttpResponse.notFound());
    }

    @Get("/author/{author}")
    public List<Book> getBooksByAuthor(@PathVariable String author) {
        return bookService.findByAuthor(author);
    }

    @Get("/search")
    public List<Book> searchBooks(@QueryValue String title) {
        return bookService.searchByTitle(title);
    }

    @Get("/page")
    public Page<Book> getActiveBooks(Pageable pageable) {
        return bookService.findActiveBooks(pageable);
    }

    @Get("/price-range")
    public List<Book> getBooksByPriceRange(
            @QueryValue java.math.BigDecimal min,
            @QueryValue java.math.BigDecimal max) {
        return bookService.findByPriceRange(min, max);
    }

    @Post
    public HttpResponse<Book> createBook(@Body @Valid Book book) {
        Book created = bookService.save(book);
        return HttpResponse.created(created);
    }

    @Put("/{id}")
    public HttpResponse<Book> updateBook(@PathVariable Long id, @Body @Valid Book book) {
        if (!bookService.existsById(id)) {
            return HttpResponse.notFound();
        }
        book.setId(id);
        return HttpResponse.ok(bookService.update(book));
    }

    @Delete("/{id}")
    public HttpResponse<Void> deleteBook(@PathVariable Long id) {
        if (!bookService.existsById(id)) {
            return HttpResponse.notFound();
        }
        bookService.delete(id);
        return HttpResponse.noContent();
    }

    @Get("/low-stock/{threshold}")
    public List<Book> getLowStockBooks(@PathVariable Integer threshold) {
        return bookService.findLowStockBooks(threshold);
    }
}
```

### application.yml

```yaml
micronaut:
  application:
    name: micronaut-rest-api
  server:
    host: 0.0.0.0
    port: 8080
  config-locations: classpath:application.yml

datasources:
  default:
    url: jdbc:h2:mem:books
    driverClassName: org.h2.Driver
    username: sa
    password: ""
    dialect: H2

jpa:
  default:
    entity-scan:
      packages: 'com.micronaut.learning.domain'
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        show_sql: true
        format_sql: true

endpoints:
  health:
    enabled: true
    sensitive: false
  info:
    enabled: true
  beans:
    enabled: true
  routes:
    enabled: true
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.micronaut.learning</groupId>
    <artifactId>micronaut-rest-api</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>io.micronaut.platform</groupId>
        <artifactId>micronaut-parent</artifactId>
        <version>4.2.0</version>
    </parent>

    <properties>
        <jdk.version>17</jdk.version>
        <release.version>17</release.version>
        <micronaut.version>4.2.0</micronaut.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>io.micronaut</groupId>
            <artifactId>micronaut-http-server-netty</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micronaut</groupId>
            <artifactId>micronaut-inject</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micronaut.data</groupId>
            <artifactId>micronaut-data-hibernate-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micronaut.sql</groupId>
            <artifactId>micronaut-jdbc-hikari</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micronaut</groupId>
            <artifactId>micronaut-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micronaut</groupId>
            <artifactId>micronaut-management</artifactId>
        </dependency>
        <dependency>
            <groupId>ch.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>jakarta.annotation</groupId>
            <artifactId>jakarta.annotation-api</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>io.micronaut.maven</groupId>
                <artifactId>micronaut-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>io.micronaut</groupId>
                            <artifactId>micronaut-inject-java</artifactId>
                            <version>${micronaut.version}</version>
                        </path>
                        <path>
                            <groupId>io.micronaut.data</groupId>
                            <artifactId>micronaut-data-processor</artifactId>
                            <version>4.2.0</version>
                        </path>
                        <path>
                            <groupId>io.micronaut.validation</groupId>
                            <artifactId>micronaut-validation-processor</artifactId>
                            <version>4.2.0</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### Build and Run

```bash
cd micronaut-rest-api
mvn clean compile
mvn exec:java
```

---

## Real-World Project: Micronaut Microservices with GraalVM Native Image (8+ hours)

### Overview

Build a complete microservices architecture using Micronaut with service discovery, distributed configuration, circuit breaker, rate limiting, and compile it to a native executable using GraalVM. This project demonstrates Micronaut's ability to create extremely fast, low-memory microservices.

### Architecture

```
micronaut-microservices/
├── gateway-service/        # API Gateway
├── product-service/         # Product Catalog
├── order-service/          # Order Management
├── inventory-service/      # Inventory Service
├── common/                 # Shared Code
└── kubernetes/             # K8s Configurations
```

### Implementation

```java
package com.micronaut.gateway;

import io.micronaut.runtime.Micronaut;

public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
```

```java
package com.micronaut.gateway;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

@Filter("/**")
public class GatewayFilter implements HttpServerFilter {
    
    private final RateLimiter rateLimiter;

    public GatewayFilter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, 
            ServerFilterChain chain) {
        
        String clientId = request.getHeaders().get("X-Client-Id", "default");
        
        if (!rateLimiter.allowRequest(clientId)) {
            return io.micronaut.http.HttpResponse.serverError(
                io.micronaut.core.util.CollectionUtils.mapOf(
                    "error", "Rate limit exceeded"
                )
            ).publisher();
        }
        
        return chain.proceed(request);
    }
}
```

```java
package com.micronaut.gateway;

import jakarta.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class RateLimiter {
    
    private final Map<String, RateLimitInfo> clientLimits = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS = 100;
    private static final long WINDOW_MS = TimeUnit.SECONDS.toMillis(60);

    public boolean allowRequest(String clientId) {
        RateLimitInfo info = clientLimits.computeIfAbsent(clientId, 
            k -> new RateLimitInfo());
        
        long now = System.currentTimeMillis();
        
        if (now - info.windowStart > WINDOW_MS) {
            info.windowStart = now;
            info.count.set(0);
        }
        
        return info.count.incrementAndGet() <= MAX_REQUESTS;
    }

    private static class RateLimitInfo {
        long windowStart;
        AtomicInteger count = new AtomicInteger(0);
    }
}
```

```java
package com.micronaut.product;

import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.annotation.Async;

@Controller("/products")
public class ProductController {
    
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Get
    public Iterable<Product> list() {
        return productService.findAll();
    }

    @Get("/{id}")
    public Optional<Product> get(@PathVariable Long id) {
        return productService.findById(id);
    }

    @Get("/category/{category}")
    public Iterable<Product> getByCategory(@PathVariable String category) {
        return productService.findByCategory(category);
    }

    @Post
    public Product create(@Body Product product) {
        return productService.save(product);
    }

    @Put("/{id}")
    public Product update(@PathVariable Long id, @Body Product product) {
        product.setId(id);
        return productService.update(product);
    }

    @Delete("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }

    @Post("/{id}/reduce-stock")
    public boolean reduceStock(@PathVariable Long id, @Body StockReduction reduction) {
        return productService.reduceStock(id, reduction.quantity());
    }
}
```

```java
package com.micronaut.product;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.CircuitBreaker;
import io.micronaut.retry.annotation.Retryable;
import org.reactivestreams.Publisher;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

@Client("inventory-service")
@Singleton
public interface InventoryClient {
    
    @Get("/inventory/{productId}")
    @Retryable(attempts = "3", delay = "500ms")
    @CircuitBreaker(attempts = "5", delay = "5s")
    Optional<Inventory> getInventory(Long productId);
    
    @Post("/inventory/{productId}/reserve")
    @CircuitBreaker(attempts = "5", delay = "5s")
    boolean reserveStock(Long productId, Integer quantity);
}
```

```java
package com.micronaut.inventory;

import io.micronaut.http.annotation.*;
import jakarta.validation.constraints.NotNull;

@Controller("/inventory")
public class InventoryController {
    
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Get("/{productId}")
    public Optional<Inventory> getInventory(@PathVariable Long productId) {
        return inventoryService.getInventory(productId);
    }

    @Post("/{productId}/reserve")
    public boolean reserveStock(@PathVariable Long productId, 
            @Body @NotNull Integer quantity) {
        return inventoryService.reserveStock(productId, quantity);
    }

    @Post("/{productId}/release")
    public void releaseStock(@PathVariable Long productId, 
            @Body @NotNull Integer quantity) {
        inventoryService.releaseStock(productId, quantity);
    }

    @Get("/low-stock/{threshold}")
    public List<Inventory> getLowStock(@PathVariable Integer threshold) {
        return inventoryService.getLowStockItems(threshold);
    }
}
```

```java
package com.micronaut.inventory;

import jakarta.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class InventoryService {
    
    private final Map<Long, Inventory> inventory = new ConcurrentHashMap<>();

    public InventoryService() {
        inventory.put(1L, new Inventory(1L, 100));
        inventory.put(2L, new Inventory(2L, 50));
        inventory.put(3L, new Inventory(3L, 200));
    }

    public Optional<Inventory> getInventory(Long productId) {
        return Optional.ofNullable(inventory.get(productId));
    }

    public boolean reserveStock(Long productId, Integer quantity) {
        Inventory inv = inventory.get(productId);
        if (inv == null || inv.availableQuantity() < quantity) {
            return false;
        }
        inv.reserve(quantity);
        return true;
    }

    public void releaseStock(Long productId, Integer quantity) {
        Inventory inv = inventory.get(productId);
        if (inv != null) {
            inv.release(quantity);
        }
    }

    public java.util.List<Inventory> getLowStockItems(Integer threshold) {
        return inventory.values().stream()
            .filter(inv -> inv.availableQuantity() < threshold)
            .toList();
    }
}
```

```java
package com.micronaut.inventory;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Inventory {
    
    private Long productId;
    private Integer totalQuantity;
    private Integer reservedQuantity;

    public Inventory() {
        this.totalQuantity = 0;
        this.reservedQuantity = 0;
    }

    public Inventory(Long productId, Integer totalQuantity) {
        this.productId = productId;
        this.totalQuantity = totalQuantity;
        this.reservedQuantity = 0;
    }

    public synchronized boolean reserve(Integer quantity) {
        if (availableQuantity() >= quantity) {
            reservedQuantity += quantity;
            return true;
        }
        return false;
    }

    public synchronized void release(Integer quantity) {
        reservedQuantity = Math.max(0, reservedQuantity - quantity);
    }

    public Integer availableQuantity() {
        return totalQuantity - reservedQuantity;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Integer totalQuantity) { this.totalQuantity = totalQuantity; }

    public Integer getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(Integer reservedQuantity) { this.reservedQuantity = reservedQuantity; }
}
```

```java
package com.micronaut.order;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.annotation.*;
import io.micronaut.retry.annotation.CircuitBreaker;
import com.micronaut.product.Product;

@Controller("/orders")
public class OrderController {
    
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Get
    public Iterable<Order> list() {
        return orderService.findAll();
    }

    @Get("/{id}")
    public Optional<Order> get(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @Post
    @CircuitBreaker(attempts = "3", delay = "5s")
    public Order create(@Body CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @Patch("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @Body String status) {
        return orderService.updateStatus(id, OrderStatus.valueOf(status.toUpperCase()));
    }
}
```

```java
package com.micronaut.order;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record CreateOrderRequest(
    Long customerId,
    String customerEmail,
    java.util.List<OrderItemRequest> items,
    String shippingAddress
) {}

@Serdeable
public record OrderItemRequest(
    Long productId,
    String productName,
    Integer quantity,
    java.math.BigDecimal price
) {}
```

### gateway-service application.yml

```yaml
micronaut:
  application:
    name: gateway-service
  server:
    port: 8080

endpoints:
  health:
    enabled: true

tracing:
  zipkin:
    enabled: true
    url: http://localhost:9411
    service: gateway-service
```

### product-service application.yml

```yaml
micronaut:
  application:
    name: product-service
  server:
    port: 8081

datasources:
  default:
    url: jdbc:postgresql://localhost:5432/products
    driverClassName: org.postgresql.Driver
    username: postgres
    password: password

jpa:
  default:
    hibernate:
      ddl-auto: update

tracing:
  zipkin:
    enabled: true
    url: http://localhost:9411
```

### GraalVM Configuration

```java
package com.micronaut.product;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Product {
    private Long id;
    private String name;
    private String description;
    private java.math.BigDecimal price;
    private String category;
    private Integer stock;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public java.math.BigDecimal getPrice() { return price; }
    public void setPrice(java.math.BigDecimal price) { this.price = price; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
```

### Build for GraalVM Native

```bash
# Install GraalVM and native-image
gu install native-image

# Build native executable
mvn clean package -Dpackaging=native

# Run native executable
./target/micronaut-product-service
```

### pom.xml (Product Service)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.micronaut.microservices</groupId>
    <artifactId>product-service</artifactId>
    <version>1.0.0</version>

    <parent>
        <groupId>io.micronaut.platform</groupId>
        <artifactId>micronaut-parent</artifactId>
        <version>4.2.0</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>io.micronaut</groupId>
            <artifactId>micronaut-http-server-netty</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micronaut</groupId>
            <artifactId>micronaut-inject</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micronaut.data</groupId>
            <artifactId>micronaut-data-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micronaut.sql</groupId>
            <artifactId>micronaut-jdbc-hikari</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micronaut</groupId>
            <artifactId>micronaut-tracing-zipkin</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micronaut.retry</groupId>
            <artifactId>micronaut-retry</artifactId>
        </dependency>
    </dependencies>
</project>
```

### Build and Run Services

```bash
# Build all services
mvn clean package

# Run Product Service
java -jar product-service/target/product-service-1.0.0.jar

# Run Inventory Service
java -jar inventory-service/target/inventory-service-1.0.0.jar

# Run Order Service
java -jar order-service/target/order-service-1.0.0.jar

# Run Gateway
java -jar gateway-service/target/gateway-service-1.0.0.jar

# Test through gateway
curl http://localhost:8080/products
```

---

## Additional Learning Resources

- Micronaut Documentation: https://docs.micronaut.io/
- Micronaut Data: https://micronaut-projects.github.io/micronaut-data/latest/guide/
- GraalVM with Micronaut: https://guides.micronaut.io/latest/micronaut-creating-first-graalvm-app-maven-java.html