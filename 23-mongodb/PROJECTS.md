# MongoDB Module - PROJECTS.md

---

# Mini-Project 1: Document CRUD (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Beginner  
**Concepts Used**: MongoRepository, Document save/find/delete, CRUD operations, Custom queries

This mini-project focuses on implementing CRUD operations with MongoDB documents.

---

## Project Structure

```
23-mongodb/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── model/
│   │   ├── Product.java
│   │   ├── Category.java
│   │   └── Review.java
│   ├── repository/
│   │   ├── ProductRepository.java
│   │   └── CategoryRepository.java
│   └── service/
│       └── ProductCrudService.java
└── src/main/resources/
    └── application.yml
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
    <artifactId>mongodb-crud-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
        </dependency>
    </dependencies>
</project>
```

---

## Step 2: Model Classes

```java
// model/Product.java
package com.learning.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "products")
public class Product {
    @Id
    private String id;
    
    @TextIndexed
    private String name;
    
    private String description;
    
    @Indexed
    private BigDecimal price;
    
    @Indexed
    private String category;
    
    private List<String> tags;
    private List<Review> reviews;
    private Integer stockQuantity;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Product() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.stockQuantity = 0;
    }
    
    public Product(String name, String description, BigDecimal price, String category) {
        this();
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }
    
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
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public void addReview(Review review) {
        if (this.reviews == null) {
            this.reviews = new java.util.ArrayList<>();
        }
        this.reviews.add(review);
    }
}
```

```java
// model/Category.java
package com.learning.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
public class Category {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String name;
    
    private String description;
    private String parentId;
    private Integer level;
    private Boolean active;
    
    public Category() {}
    
    public Category(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
```

```java
// model/Review.java
package com.learning.model;

import java.time.LocalDate;

public class Review {
    private String id;
    private String userId;
    private String userName;
    private Integer rating;
    private String comment;
    private LocalDate date;
    private Integer helpfulCount;
    private Boolean verified;
    
    public Review() {
        this.date = LocalDate.now();
        this.helpfulCount = 0;
        this.verified = false;
    }
    
    public Review(String userId, String userName, Integer rating, String comment) {
        this();
        this.userId = userId;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public LocalDate getDate() { return date; }
    
    public Integer getHelpfulCount() { return helpfulCount; }
    public void setHelpfulCount(Integer helpfulCount) { this.helpfulCount = helpfulCount; }
    
    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }
}
```

---

## Step 3: Repository Interfaces

```java
// repository/ProductRepository.java
package com.learning.repository;

import com.learning.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
    List<Product> findByCategory(String category);
    
    List<Product> findByCategoryAndActive(String category, Boolean active);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    @Query("{ 'price': { $gte: ?0, $lte: ?1 } }")
    List<Product> findByPriceRange(BigDecimal min, BigDecimal max);
    
    @Query("{ 'stockQuantity': { $lt: ?0 } }")
    List<Product> findLowStock(Integer threshold);
    
    @Query("{ 'tags': { $in: ?0 } }")
    List<Product> findByTagsIn(List<String> tags);
    
    List<Product> findByNameContainingIgnoreCaseAndCategory(
        String name, String category);
    
    @Query("{ 'reviews': { $exists: true, $ne: [] } }")
    List<Product> findProductsWithReviews();
    
    Long countByCategory(String category);
    
    Long countByActive(Boolean active);
    
    void deleteByCategory(String category);
}
```

```java
// repository/CategoryRepository.java
package com.learning.repository;

import com.learning.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
    
    Optional<Category> findByName(String name);
    
    List<Category> findByParentId(String parentId);
    
    List<Category> findByLevel(Integer level);
    
    List<Category> findByActive(Boolean active);
    
    @Query("{ 'parentId': null }")
    List<Category> findRootCategories();
}
```

---

## Step 4: CRUD Service

```java
// service/ProductCrudService.java
package com.learning.service;

import com.learning.model.Product;
import com.learning.model.Review;
import com.learning.repository.ProductRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductCrudService {
    
    private final ProductRepository productRepository;
    private final MongoTemplate mongoTemplate;
    
    public ProductCrudService(ProductRepository productRepository, MongoTemplate mongoTemplate) {
        this.productRepository = productRepository;
        this.mongoTemplate = mongoTemplate;
    }
    
    public Product create(Product product) {
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }
    
    public Product createWithValidation(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }
        return create(product);
    }
    
    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }
    
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    
    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    public List<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceRange(min, max);
    }
    
    public List<Product> findByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    
    public Product update(String id, Product product) {
        Optional<Product> existing = productRepository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Product not found: " + id);
        }
        
        Product toUpdate = existing.get();
        if (product.getName() != null) toUpdate.setName(product.getName());
        if (product.getDescription() != null) toUpdate.setDescription(product.getDescription());
        if (product.getPrice() != null) toUpdate.setPrice(product.getPrice());
        if (product.getCategory() != null) toUpdate.setCategory(product.getCategory());
        if (product.getTags() != null) toUpdate.setTags(product.getTags());
        if (product.getStockQuantity() != null) toUpdate.setStockQuantity(product.getStockQuantity());
        if (product.getActive() != null) toUpdate.setActive(product.getActive());
        
        toUpdate.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(toUpdate);
    }
    
    public Product partialUpdate(String id, java.util.Map<String, Object> updates) {
        Optional<Product> existing = productRepository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Product not found: " + id);
        }
        
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update();
        
        updates.forEach((key, value) -> {
            if (value != null) {
                update.set(key, value);
            }
        });
        update.set("updatedAt", LocalDateTime.now());
        
        mongoTemplate.updateFirst(query, update, Product.class);
        return productRepository.findById(id).orElse(null);
    }
    
    public boolean delete(String id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public void deleteByCategory(String category) {
        productRepository.deleteByCategory(category);
    }
    
    public Product addReview(String productId, Review review) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }
        
        Product product = productOpt.get();
        product.addReview(review);
        product.setUpdatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }
    
    public Product incrementStock(String productId, Integer quantity) {
        Query query = new Query(Criteria.where("_id").is(productId));
        Update update = new Update().inc("stockQuantity", quantity)
            .set("updatedAt", LocalDateTime.now());
        
        mongoTemplate.updateFirst(query, update, Product.class);
        return productRepository.findById(productId).orElse(null);
    }
    
    public Product decrementStock(String productId, Integer quantity) {
        Query query = new Query(Criteria.where("_id").is(productId)
            .and("stockQuantity").gte(quantity));
        Update update = new Update().inc("stockQuantity", -quantity)
            .set("updatedAt", LocalDateTime.now());
        
        mongoTemplate.updateFirst(query, update, Product.class);
        return productRepository.findById(productId).orElse(null);
    }
    
    public List<Product> findLowStockProducts(Integer threshold) {
        return productRepository.findLowStock(threshold);
    }
    
    public long countByCategory(String category) {
        return productRepository.countByCategory(category);
    }
    
    public Product softDelete(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().set("active", false)
            .set("updatedAt", LocalDateTime.now());
        
        mongoTemplate.updateFirst(query, update, Product.class);
        return productRepository.findById(id).orElse(null);
    }
}
```

---

## Step 5: Main Application

```java
// Main.java
package com.learning;

import com.learning.model.Product;
import com.learning.model.Review;
import com.learning.service.ProductCrudService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class Main {
    
    private final ProductCrudService productCrudService;
    
    public Main(ProductCrudService productCrudService) {
        this.productCrudService = productCrudService;
    }
    
    @Bean
    public CommandLineRunner run() {
        return args -> {
            System.out.println("=== MongoDB CRUD Demo ===\n");
            
            System.out.println("--- Creating Products ---");
            Product laptop = new Product("Laptop Pro", "High-performance laptop", 
                new BigDecimal("1299.99"), "Electronics");
            laptop.setTags(List.of("computer", "laptop", "tech"));
            laptop.setStockQuantity(50);
            laptop = productCrudService.create(laptop);
            System.out.println("Created: " + laptop.getName() + " (ID: " + laptop.getId() + ")");
            
            Product phone = new Product("Smartphone X", "Latest smartphone", 
                new BigDecimal("899.99"), "Electronics");
            phone.setTags(List.of("phone", "mobile", "tech"));
            phone.setStockQuantity(100);
            phone = productCrudService.create(phone);
            System.out.println("Created: " + phone.getName() + " (ID: " + phone.getId() + ")");
            
            Product book = new Product("Java Programming", "Learn Java programming", 
                new BigDecimal("49.99"), "Books");
            book.setTags(List.of("programming", "java", "education"));
            book.setStockQuantity(200);
            book = productCrudService.create(book);
            System.out.println("Created: " + book.getName() + " (ID: " + book.getId() + ")");
            
            System.out.println("\n--- Reading Products ---");
            List<Product> electronics = productCrudService.findByCategory("Electronics");
            System.out.println("Found " + electronics.size() + " electronics products");
            
            List<Product> lowPriced = productCrudService.findByPriceRange(BigDecimal.ZERO, new BigDecimal("100"));
            System.out.println("Found " + lowPriced.size() + " products under $100");
            
            System.out.println("\n--- Updating Products ---");
            laptop.setPrice(new BigDecimal("1199.99"));
            productCrudService.update(laptop.getId(), laptop);
            System.out.println("Updated price for: " + laptop.getName());
            
            System.out.println("\n--- Adding Reviews ---");
            Review review = new Review("user1", "John Doe", 5, "Excellent product!");
            productCrudService.addReview(laptop.getId(), review);
            System.out.println("Added review to: " + laptop.getName());
            
            System.out.println("\n--- Finding Products ---");
            List<Product> searchResults = productCrudService.findByName("Laptop");
            System.out.println("Search for 'Laptop': " + searchResults.size() + " results");
            
            List<Product> lowStock = productCrudService.findLowStockProducts(10);
            System.out.println("Low stock products (threshold 10): " + lowStock.size());
            
            System.out.println("\n--- Deleting Products ---");
            productCrudService.delete(book.getId());
            System.out.println("Deleted: " + book.getName());
            
            System.out.println("\n--- Final Count ---");
            System.out.println("Total products: " + productCrudService.findAll().size());
            System.out.println("Electronics products: " + productCrudService.countByCategory("Electronics"));
            
            System.out.println("\n=== CRUD Demo Complete ===");
        };
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

---

## Step 6: Application Properties

```yaml
# src/main/resources/application.yml
spring:
  application:
    name: mongodb-crud-demo
  data:
    mongodb:
      host: localhost
      port: 27017
      database: product_catalog
```

---

## Build Instructions

```bash
# Start MongoDB
docker run -p 27017:27017 mongo

# Access MongoDB shell
docker exec -it <container_id> mongosh

cd 23-mongodb
mvn clean compile
mvn spring-boot:run
```

---

# Mini-Project 2: Aggregation Pipeline (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Aggregation stages, $match, $group, $project, $sort, $limit, $lookup, $unwind

This mini-project focuses on building aggregation pipelines with MongoDB.

---

## Project Structure

```
23-mongodb/
├── src/main/java/com/learning/
│   ├── service/
│   │   ├── SalesAggregationService.java
│   │   └── ProductAnalyticsService.java
│   └── model/
│       ├── SalesOrder.java
│       └── OrderItem.java
```

---

## Step 1: Model Classes

```java
// model/SalesOrder.java
package com.learning.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "sales_orders")
public class SalesOrder {
    @Id
    private String id;
    private String orderNumber;
    private String customerId;
    private String customerName;
    private List<OrderItem> items;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal totalAmount;
    private String status;
    private String region;
    private LocalDateTime createdAt;
    
    public SalesOrder() {
        this.createdAt = LocalDateTime.now();
        this.status = "CREATED";
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

```java
// model/OrderItem.java
package com.learning.model;

import java.math.BigDecimal;

public class OrderItem {
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    
    public OrderItem() {}
    
    public OrderItem(String productId, String productName, Integer quantity, BigDecimal price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = price.multiply(new BigDecimal(quantity));
    }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
```

---

## Step 2: Sales Aggregation Service

```java
// service/SalesAggregationService.java
package com.learning.service;

import com.learning.model.SalesOrder;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SalesAggregationService {
    
    private final MongoTemplate mongoTemplate;
    
    public SalesAggregationService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    public List<Map> getDailySalesStats(LocalDateTime startDate, LocalDateTime endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("createdAt").gte(startDate).lte(endDate)),
            Aggregation.match(Criteria.where("status").is("COMPLETED")),
            Aggregation.unwind("items", true),
            Aggregation.group(
                Fields.from(
                    Fields.field("date", "createdAt")
                )
            )
                .sum("items.subtotal").as("totalRevenue")
                .sum("items.quantity").as("unitsSold")
                .count().as("orderCount")
        );
        
        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, "sales_orders", Map.class);
        
        return results.getMappedResults();
    }
    
    public List<Map> getSalesByRegion() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("status").is("COMPLETED")),
            Aggregation.group("region")
                .sum("totalAmount").as("totalRevenue")
                .count().as("orderCount")
                .avg("totalAmount").as("avgOrderValue"),
            Aggregation.sort(Sort.Direction.DESC, "totalRevenue"),
            Aggregation.project()
                .and("_id").as("region")
                .andInclude("totalRevenue", "orderCount", "avgOrderValue")
        );
        
        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, "sales_orders", Map.class);
        
        return results.getMappedResults();
    }
    
    public List<Map> getTopSellingProducts(int limit) {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("status").is("COMPLETED")),
            Aggregation.unwind("items", true),
            Aggregation.group("items.productId")
                .first("items.productName").as("productName")
                .sum("items.quantity").as("totalQuantitySold")
                .sum("items.subtotal").as("totalRevenue")
                .count().as("orderCount"),
            Aggregation.sort(Sort.Direction.DESC, "totalRevenue"),
            Aggregation.limit(limit),
            Aggregation.project()
                .and("_id").as("productId")
                .andInclude("productName", "totalQuantitySold", "totalRevenue", "orderCount")
        );
        
        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, "sales_orders", Map.class);
        
        return results.getMappedResults();
    }
    
    public Map getCustomerPurchaseSummary(String customerId) {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("customerId").is(customerId)
                .and("status").is("COMPLETED")),
            Aggregation.group("customerId")
                .sum("totalAmount").as("totalSpent")
                .count().as("orderCount")
                .avg("totalAmount").as("avgOrderValue")
                .min("createdAt").as("firstPurchase")
                .max("createdAt").as("lastPurchase")
        );
        
        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, "sales_orders", Map.class);
        
        return results.getMappedResults().stream().findFirst().orElse(null);
    }
    
    public List<Map> getMonthlyTrends(int year) {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("createdAt")
                .gte(LocalDateTime.of(year, 1, 1, 0, 0))
                .lt(LocalDateTime.of(year + 1, 1, 1, 0, 0))),
            Aggregation.match(Criteria.where("status").is("COMPLETED")),
            Aggregation.project()
                .andInclude("totalAmount", "status")
                .and("createdAt").extractYear().as("year")
                .and("createdAt").extractMonth().as("month"),
            Aggregation.group(Fields.from(
                    Fields.field("year"),
                    Fields.field("month")
                ))
                .sum("totalAmount").as("revenue")
                .count().as("orderCount"),
            Aggregation.sort(Sort.Direction.ASC, "year", "month"),
            Aggregation.project()
                .and("_id.year").as("year")
                .and("_id.month").as("month")
                .andInclude("revenue", "orderCount")
        );
        
        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, "sales_orders", Map.class);
        
        return results.getMappedResults();
    }
    
    public List<Map> getOrderStatusDistribution() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.group("status")
                .count().as("count")
                .sum("totalAmount").as("totalValue"),
            Aggregation.sort(Sort.Direction.DESC, "count")
        );
        
        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, "sales_orders", Map.class);
        
        return results.getMappedResults();
    }
}
```

---

## Step 3: Product Analytics Service

```java
// service/ProductAnalyticsService.java
package com.learning.service;

import com.learning.model.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductAnalyticsService {
    
    private final MongoTemplate mongoTemplate;
    
    public ProductAnalyticsService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    public List<Map> getCategoryBreakdown() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("active").is(true)),
            Aggregation.group("category")
                .count().as("productCount")
                .avg("price").as("avgPrice")
                .min("price").as("minPrice")
                .max("price").as("maxPrice")
                .sum("stockQuantity").as("totalStock"),
            Aggregation.sort(Sort.Direction.DESC, "productCount"),
            Aggregation.project()
                .and("_id").as("category")
                .andInclude("productCount", "avgPrice", "minPrice", "maxPrice", "totalStock")
        );
        
        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, "products", Map.class);
        
        return results.getMappedResults();
    }
    
    public List<Map> getProductsNeedingRestock(int threshold) {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("active").is(true)
                .and("stockQuantity").lte(threshold)),
            Aggregation.project()
                .andInclude("name", "category", "stockQuantity", "price")
                .and("stockQuantity").multiply("price").as("inventoryValue"),
            Aggregation.sort(Sort.Direction.ASC, "stockQuantity")
        );
        
        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, "products", Map.class);
        
        return results.getMappedResults();
    }
    
    public List<Map> getPriceDistribution() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("active").is(true)),
            Aggregation.bucket("price")
                .withBoundaries(0, 50, 100, 200, 500, 1000, 5000, 10000)
                .withDefaultBucket("PREMIUM")
                .andOutput(AccumulatorOperators.Sum.sumOf(1)).as("count")
                .andOutput(AccumulatorOperators.Sum.sumOf("$stockQuantity")).as("totalStock"),
            Aggregation.sort(Sort.Direction.ASC, "_id")
        );
        
        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, "products", Map.class);
        
        return results.getMappedResults();
    }
    
    public List<Map> getProductsWithReviews() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("reviews").exists(true)),
            Aggregation.addFields()
                .addField("reviewCount")
                .withValue(ArrayOperators.Size.sizeOf("reviews"))
                .build()
                .addField("avgRating")
                .withValue(ArrayOperators.Avg.avgOf("reviews.rating"))
                .build(),
            Aggregation.match(Criteria.where("reviewCount").gt(0)),
            Aggregation.sort(Sort.Direction.DESC, "avgRating"),
            Aggregation.limit(10),
            Aggregation.project()
                .andInclude("name", "category", "price", "reviewCount", "avgRating")
        );
        
        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, "products", Map.class);
        
        return results.getMappedResults();
    }
    
    public Map getInventorySummary() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.facet()
                .and(
                    Aggregation.match(Criteria.where("active").is(true)),
                    Aggregation.group().count().as("totalProducts")
                        .sum("stockQuantity").as("totalStock")
                        .avg("price").as("avgPrice")
                ).as("overview")
                .and(
                    Aggregation.match(Criteria.where("stockQuantity").lte(10)),
                    Aggregation.group().count().as("lowStockCount")
                ).as("lowStock")
                .and(
                    Aggregation.match(Criteria.where("stockQuantity").is(0)),
                    Aggregation.group().count().as("outOfStockCount")
                ).as("outOfStock")
        );
        
        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, "products", Map.class);
        
        return results.getMappedResults().stream().findFirst().orElse(null);
    }
}
```

---

## Build Instructions

```bash
cd 23-mongodb
mvn clean compile
mvn spring-boot:run
```

---

# Mini-Project 3: Indexing (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Single field index, Compound index, Text index, Geospatial index, Index analysis

This mini-project focuses on creating and managing MongoDB indexes for optimal query performance.

---

## Project Structure

```
23-mongodb/
├── src/main/java/com/learning/
│   ├── config/
│   │   └── IndexConfig.java
│   ├── service/
│   │   └── IndexManagementService.java
│   └── model/
│       └── Location.java
```

---

## Step 1: Index Configuration

```java
// config/IndexConfig.java
package com.learning.config;

import com.learning.model.Product;
import com.learning.model.Location;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.*;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;

@Configuration
public class IndexConfig {
    
    private final MongoTemplate mongoTemplate;
    
    public IndexConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    @PostConstruct
    public void initIndexes() {
        createProductIndexes();
        createLocationIndexes();
    }
    
    private void createProductIndexes() {
        mongoTemplate.indexOps(Product.class)
            .ensureIndex(new Index().on("category", Sort.Direction.ASC));
        
        mongoTemplate.indexOps(Product.class)
            .ensureIndex(new Index()
                .on("category", Sort.Direction.ASC)
                .on("price", Sort.Direction.ASC));
        
        mongoTemplate.indexOps(Product.class)
            .ensureIndex(new Index()
                .on("stockQuantity", Sort.Direction.ASC)
                .on("price", Sort.Direction.ASC)
                .named("low_stock_price_idx"));
        
        TextIndexDefinition textIndex = new TextIndexDefinitionBuilder()
            .onField("name")
            .onField("description")
            .onField("tags", 1.5f)
            .named("product_text_idx")
            .build();
        mongoTemplate.indexOps(Product.class).ensureIndex(textIndex);
    }
    
    private void createLocationIndexes() {
        mongoTemplate.indexOps(Location.class)
            .ensureIndex(GeospatialIndex.geo2dsphere("coordinates"));
        
        mongoTemplate.indexOps(Location.class)
            .ensureIndex(new Index()
                .on("type", Sort.Direction.ASC)
                .on("coordinates", Sort.Direction.GEO_2DSPHERE));
    }
}
```

---

## Step 2: Index Management Service

```java
// service/IndexManagementService.java
package com.learning.service;

import com.learning.model.Product;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndexManagementService {
    
    private final MongoTemplate mongoTemplate;
    
    public IndexManagementService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    public void createCompoundIndex(String collectionName, IndexDefinition indexDefinition) {
        mongoTemplate.indexOps(collectionName).ensureIndex(indexDefinition);
        System.out.println("Index created on collection: " + collectionName);
    }
    
    public void createCompoundIndexOnProducts() {
        IndexDefinition index = new Index()
            .on("category", Sort.Direction.ASC)
            .on("price", Sort.Direction.ASC)
            .on("name", Sort.Direction.ASC)
            .named("product_category_price_name_idx")
            .unique();
        
        createCompoundIndex("products", index);
    }
    
    public void createTTLIndex(String collectionName, String fieldName, int expireAfterSeconds) {
        Index index = new Index()
            .on(fieldName, Sort.Direction.ASC)
            .expire(expireAfterSeconds);
        
        mongoTemplate.indexOps(collectionName).ensureIndex(index);
        System.out.println("TTL index created on " + collectionName + "." + fieldName);
    }
    
    public void createPartialIndex(String collectionName) {
        Index index = new Index()
            .on("category", Sort.Direction.ASC)
            .on("price", Sort.Direction.ASC)
            .partial(IndexPartial.of(
                new org.springframework.data.mongodb.core.query.Criteria("active").is(true)
            ))
            .named("active_products_idx");
        
        mongoTemplate.indexOps(collectionName).ensureIndex(index);
    }
    
    public List<Document> listIndexes(String collectionName) {
        IndexOperations indexOps = mongoTemplate.indexOps(collectionName);
        return indexOps.getIndexInfo();
    }
    
    public void dropIndex(String collectionName, String indexName) {
        mongoTemplate.indexOps(collectionName).dropIndex(indexName);
        System.out.println("Index dropped: " + indexName);
    }
    
    public void dropAllIndexes(String collectionName) {
        mongoTemplate.indexOps(collectionName).dropAllIndexes();
        System.out.println("All indexes dropped from: " + collectionName);
    }
    
    public Document getIndexStats(String collectionName, String indexName) {
        return mongoTemplate.executeCommand(
            new Document("indexStats", collectionName)
        );
    }
    
    public List<Document> explainQuery(String collectionName, 
            org.springframework.data.mongodb.core.query.Query query) {
        
        return mongoTemplate.executeQuery(query, collection -> {
            Document result = collection.find(query.getQueryObject())
                .explain();
            return List.of(result);
        });
    }
    
    public void createHashedIndex(String collectionName, String fieldName) {
        Index index = new Index()
            .on(fieldName, Sort.Direction.ASC)
            .named("hashed_" + fieldName + "_idx");
        
        mongoTemplate.indexOps(collectionName).ensureIndex(index);
    }
}
```

---

## Step 3: Location Model for Geospatial Indexing

```java
// model/Location.java
package com.learning.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "locations")
public class Location {
    @Id
    private String id;
    private String name;
    private String type;
    private GeoJsonPoint coordinates;
    private String address;
    private String city;
    private String region;
    
    public Location() {}
    
    public Location(String name, String type, double longitude, double latitude) {
        this.name = name;
        this.type = type;
        this.coordinates = new GeoJsonPoint(longitude, latitude);
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public GeoJsonPoint getCoordinates() { return coordinates; }
    public void setCoordinates(GeoJsonPoint coordinates) { this.coordinates = coordinates; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
}
```

---

## Build Instructions

```bash
cd 23-mongodb
mvn clean compile
mvn spring-boot:run
```

---

# Mini-Project 4: Data Modeling (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Embedded documents, Referenced documents, One-to-many, Many-to-many, Schema design

This mini-project focuses on designing effective MongoDB data models.

---

## Project Structure

```
23-mongodb/
├── src/main/java/com/learning/
│   ├── model/
│   │   ├── BlogPost.java
│   │   ├── Comment.java
│   │   ├── Author.java
│   │   ├── Tag.java
│   │   └── BlogPostWithReferences.java
│   ├── service/
│   │   └── BlogPostService.java
```

---

## Step 1: Embedded Document Models

```java
// model/BlogPost.java
package com.learning.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "blog_posts_embedded")
public class BlogPost {
    @Id
    private String id;
    private String title;
    private String content;
    private Author author;
    private List<Comment> comments;
    private List<String> tags;
    private Integer viewCount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    
    public static class Author {
        private String authorId;
        private String name;
        private String email;
        private String bio;
        
        public Author() {}
        
        public Author(String authorId, String name, String email) {
            this.authorId = authorId;
            this.name = name;
            this.email = email;
        }
        
        public String getAuthorId() { return authorId; }
        public void setAuthorId(String authorId) { this.authorId = authorId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
    }
    
    public static class Comment {
        private String commentId;
        private String authorName;
        private String content;
        private Integer likes;
        private List<Comment> replies;
        private LocalDateTime createdAt;
        
        public Comment() {
            this.createdAt = LocalDateTime.now();
            this.likes = 0;
        }
        
        public Comment(String authorName, String content) {
            this();
            this.authorName = authorName;
            this.content = content;
        }
        
        public String getCommentId() { return commentId; }
        public void setCommentId(String commentId) { this.commentId = commentId; }
        
        public String getAuthorName() { return authorName; }
        public void setAuthorName(String authorName) { this.authorName = authorName; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public Integer getLikes() { return likes; }
        public void setLikes(Integer likes) { this.likes = likes; }
        
        public List<Comment> getReplies() { return replies; }
        public void setReplies(List<Comment> replies) { this.replies = replies; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
    }
    
    public BlogPost() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.viewCount = 0;
        this.status = "DRAFT";
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }
    
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public void addComment(Comment comment) {
        if (this.comments == null) {
            this.comments = new java.util.ArrayList<>();
        }
        this.comments.add(comment);
    }
    
    public int getCommentCount() {
        return comments != null ? comments.size() : 0;
    }
}
```

---

## Step 2: Referenced Document Models

```java
// model/Author.java
package com.learning.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "authors")
public class Author {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String name;
    
    @Indexed(unique = true)
    private String email;
    
    private String bio;
    private String avatarUrl;
    private Integer postCount;
    
    public Author() {}
    
    public Author(String name, String email) {
        this();
        this.name = name;
        this.email = email;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    
    public Integer getPostCount() { return postCount; }
    public void setPostCount(Integer postCount) { this.postCount = postCount; }
}
```

```java
// model/Tag.java
package com.learning.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tags")
public class Tag {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String name;
    
    private String slug;
    private String description;
    private Integer usageCount;
    
    public Tag() {}
    
    public Tag(String name, String slug) {
        this();
        this.name = name;
        this.slug = slug;
        this.usageCount = 0;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }
}
```

```java
// model/BlogPostWithReferences.java
package com.learning.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "blog_posts_referenced")
public class BlogPostWithReferences {
    @Id
    private String id;
    
    @Indexed
    private String title;
    
    @Indexed
    private String content;
    
    @Indexed
    private String authorId;
    
    @Indexed
    private List<String> tagIds;
    
    private Integer viewCount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    
    public BlogPostWithReferences() {
        this.createdAt = LocalDateTime.now();
        this.viewCount = 0;
        this.status = "DRAFT";
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    
    public List<String> getTagIds() { return tagIds; }
    public void setTagIds(List<String> tagIds) { this.tagIds = tagIds; }
    
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
}
```

---

## Step 3: Blog Post Service with Lookup

```java
// service/BlogPostService.java
package com.learning.service;

import com.learning.model.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlogPostService {
    
    private final MongoTemplate mongoTemplate;
    
    public BlogPostService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    public BlogPost createPost(String title, String content, String authorName, String authorEmail) {
        BlogPost post = new BlogPost();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(new BlogPost.Author(null, authorName, authorEmail));
        post.setStatus("PUBLISHED");
        post.setPublishedAt(LocalDateTime.now());
        
        return mongoTemplate.save(post);
    }
    
    public BlogPost addComment(String postId, String authorName, String content) {
        BlogPost post = mongoTemplate.findById(postId, BlogPost.class);
        if (post == null) {
            throw new IllegalArgumentException("Post not found: " + postId);
        }
        
        BlogPost.Comment comment = new BlogPost.Comment(authorName, content);
        post.addComment(comment);
        
        return mongoTemplate.save(post);
    }
    
    public List<BlogPost> getPostsByAuthor(String authorName) {
        return mongoTemplate.find(
            new org.springframework.data.mongodb.core.query.Query(
                Criteria.where("author.name").is(authorName)
            ),
            BlogPost.class
        );
    }
    
    public List<BlogPost> getPostsByTag(String tag) {
        return mongoTemplate.find(
            new org.springframework.data.mongodb.core.query.Query(
                Criteria.where("tags").is(tag)
            ),
            BlogPost.class
        );
    }
    
    public List<Map> getPostsWithAuthorDetails() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.lookup("authors", "author.authorId", "_id", "authorDetails"),
            Aggregation.unwind("authorDetails", true),
            Aggregation.project()
                .andInclude("title", "content", "tags", "viewCount", "status", "createdAt")
                .and("author.name").as("authorName")
                .and("authorDetails.name").as("authorDetails.name")
                .and("authorDetails.email").as("authorDetails.email")
        );
        
        return mongoTemplate.aggregate(aggregation, "blog_posts_referenced", Map.class)
            .getMappedResults();
    }
    
    public Map getAuthorStats(String authorId) {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("authorId").is(authorId)
                .and("status").is("PUBLISHED")),
            Aggregation.group("authorId")
                .count().as("postCount")
                .sum("viewCount").as("totalViews"),
            Aggregation.lookup("authors", "_id", "_id", "authorDetails"),
            Aggregation.unwind("authorDetails")
        );
        
        return mongoTemplate.aggregate(aggregation, "blog_posts_referenced", Map.class)
            .getMappedResults().stream().findFirst().orElse(null);
    }
    
    public void incrementViewCount(String postId) {
        mongoTemplate.updateFirst(
            new org.springframework.data.mongodb.core.query.Query(
                Criteria.where("_id").is(postId)
            ),
            new org.springframework.data.mongodb.core.query.Update()
                .inc("viewCount", 1),
            BlogPost.class
        );
    }
}
```

---

## Build Instructions

```bash
cd 23-mongodb
mvn clean compile
mvn spring-boot:run
```

---

# Real-World Project: Content Management System

## Project Overview

**Duration**: 20+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Complex document modeling, Multi-collection relationships, Full-text search, Change streams

This comprehensive project implements a complete content management system using MongoDB.

---

## Project Structure

```
23-mongodb/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── model/
│   │   ├── ContentItem.java
│   │   ├── MediaAsset.java
│   │   ├── Category.java
│   │   └── Taxonomy.java
│   ├── service/
│   │   ├── ContentService.java
│   │   └── SearchService.java
│   └── repository/
│       ├── ContentRepository.java
│       └── MediaRepository.java
└── src/main/resources/
    └── application.yml
```

---

## Step 1: Advanced Content Models

```java
// model/ContentItem.java
package com.learning.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "content_items")
public class ContentItem {
    @Id
    private String id;
    
    @TextIndexed
    private String title;
    
    @TextIndexed
    private String content;
    
    @Indexed
    private String slug;
    
    @Indexed
    private String type;
    
    @Indexed
    private String status;
    
    @Indexed
    private String authorId;
    
    private String categoryId;
    private List<String> tagIds;
    private List<MediaAsset> media;
    private Map<String, Object> metadata;
    private List<ContentVersion> versions;
    private Integer viewCount;
    private Integer likeCount;
    private String[] featuredImageIds;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime scheduledAt;
    
    public static class ContentVersion {
        private String versionId;
        private String content;
        private String authorId;
        private String changeNote;
        private LocalDateTime createdAt;
        
        public ContentVersion() {
            this.createdAt = LocalDateTime.now();
        }
        
        public String getVersionId() { return versionId; }
        public void setVersionId(String versionId) { this.versionId = versionId; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getAuthorId() { return authorId; }
        public void setAuthorId(String authorId) { this.authorId = authorId; }
        
        public String getChangeNote() { return changeNote; }
        public void setChangeNote(String changeNote) { this.changeNote = changeNote; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
    }
    
    public ContentItem() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.viewCount = 0;
        this.likeCount = 0;
        this.status = "DRAFT";
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    
    public List<String> getTagIds() { return tagIds; }
    public void setTagIds(List<String> tagIds) { this.tagIds = tagIds; }
    
    public List<MediaAsset> getMedia() { return media; }
    public void setMedia(List<MediaAsset> media) { this.media = media; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public List<ContentVersion> getVersions() { return versions; }
    public void setVersions(List<ContentVersion> versions) { this.versions = versions; }
    
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    
    public String[] getFeaturedImageIds() { return featuredImageIds; }
    public void setFeaturedImageIds(String[] featuredImageIds) { this.featuredImageIds = featuredImageIds; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
    
    public void addMedia(MediaAsset asset) {
        if (this.media == null) {
            this.media = new java.util.ArrayList<>();
        }
        this.media.add(asset);
    }
    
    public void createVersion(String content, String authorId, String changeNote) {
        if (this.versions == null) {
            this.versions = new java.util.ArrayList<>();
        }
        ContentVersion version = new ContentVersion();
        version.setVersionId("V" + (this.versions.size() + 1));
        version.setContent(content);
        version.setAuthorId(authorId);
        version.setChangeNote(changeNote);
        this.versions.add(version);
    }
}
```

---

## Build Instructions

```bash
# Start MongoDB with replica set for transactions
docker run -p 27017:27017 mongo

cd 23-mongodb
mvn clean compile
mvn spring-boot:run
```