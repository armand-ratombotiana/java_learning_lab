# MongoDB Module - PROJECTS.md

---

# Mini-Project: Document-Based Product Catalog

## Project Overview

**Duration**: 4-5 hours  
**Difficulty**: Intermediate  
**Concepts Used**: MongoDB Repositories, Documents, Aggregation Pipelines, Indexes, Embedded Documents

This mini-project demonstrates MongoDB with Spring Data MongoDB for a product catalog system.

---

## Project Structure

```
23-mongodb/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── model/
│   │   ├── Product.java
│   │   └── Review.java
│   ├── repository/
│   │   └── ProductRepository.java
│   ├── service/
│   │   └── ProductService.java
│   └── controller/
│       └── ProductController.java
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
    <artifactId>mongodb-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
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
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.TextIndexed;
import java.math.BigDecimal;
import java.util.List;

@Document(collection = "products")
public class Product {
    @Id
    private String id;
    
    @TextIndexed
    private String name;
    
    private String description;
    private BigDecimal price;
    private String category;
    
    @Indexed
    private List<String> tags;
    
    private List<Review> reviews;
    private Integer stockQuantity;
    private Boolean active;
    
    public Product() {}
    
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
}
```

```java
// model/Review.java
package com.learning.model;

public class Review {
    private String userId;
    private Integer rating;
    private String comment;
    private String date;
    
    public Review() {}
    
    public Review(String userId, Integer rating, String comment) {
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.date = java.time.LocalDate.now().toString();
    }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
```

---

## Step 3: Repository and Service

```java
// repository/ProductRepository.java
package com.learning.repository;

import com.learning.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByCategory(String category);
    
    @Query("{ 'price': { $gte: ?0, $lte: ?1 } }")
    List<Product> findByPriceRange(BigDecimal min, BigDecimal max);
    
    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<Product> searchByName(String name);
}
```

```java
// service/ProductService.java
package com.learning.service;

import com.learning.model.Product;
import com.learning.model.Review;
import com.learning.repository.ProductRepository;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    private final MongoTemplate mongoTemplate;
    
    public ProductService(ProductRepository productRepository, MongoTemplate mongoTemplate) {
        this.productRepository = productRepository;
        this.mongoTemplate = mongoTemplate;
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    public List<Product> searchProducts(String searchTerm) {
        return productRepository.searchByName(searchTerm);
    }
    
    public void addReviewToProduct(String productId, Review review) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            if (product.getReviews() == null) {
                product.setReviews(List.of(review));
            } else {
                product.getReviews().add(review);
            }
            productRepository.save(product);
        }
    }
    
    public List<Map> getCategoryAggregation() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.group("category").count().as("count")
        );
        
        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, "products", Map.class);
        
        return results.getMappedResults();
    }
}
```

---

## Step 4: Main Application

```java
// Main.java
package com.learning;

import com.learning.model.Product;
import com.learning.model.Review;
import com.learning.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class Main {
    
    private final ProductService productService;
    
    public Main(ProductService productService) {
        this.productService = productService;
    }
    
    @Bean
    public CommandLineRunner run() {
        return args -> {
            Product p1 = new Product();
            p1.setName("Laptop");
            p1.setDescription("High-performance laptop");
            p1.setPrice(new BigDecimal("1299.99"));
            p1.setCategory("Electronics");
            p1.setTags(List.of("computer", "laptop", "tech"));
            p1.setStockQuantity(50);
            
            productService.save(p1);
            
            List<Product> electronics = productService.getProductsByCategory("Electronics");
            System.out.println("Found " + electronics.size() + " electronics products");
        };
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

---

## Build Instructions

```bash
# Start MongoDB
docker run -p 27017:27017 mongo

cd 23-mongodb
mvn spring-boot:run
```

---

# Real-World Project: E-Commerce with MongoDB

This comprehensive project demonstrates advanced MongoDB patterns with aggregation pipelines, transactions, and complex queries.

---

## Complete Implementation

```java
// Advanced Product Repository with Aggregation
package com.learning.repository;

import com.learning.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;
import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    
    @Aggregation(pipeline = {
        "{ $match: { category: ?0 } }",
        "{ $sort: { price: -1 } }",
        "{ $limit: ?1 }"
    })
    List<Product> findTopProductsByCategory(String category, int limit);
    
    @Aggregation(pipeline = {
        "{ $unwind: '$reviews' }",
        "{ $group: { _id: '$_id', avgRating: { $avg: '$reviews.rating' } } }"
    })
    List<Product> getProductsWithAverageRating();
}
```

---

## Build Instructions

```bash
cd 23-mongodb
mvn clean compile
mvn spring-boot:run
```

This comprehensive project demonstrates advanced MongoDB patterns including aggregation pipelines, transactions, and complex queries.

---

# Production Patterns: Advanced Aggregation Pipelines

## Multi-Stage Sales Analytics Pipeline

```java
// service/SalesAnalyticsService.java
package com.learning.service;

import com.learning.model.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SalesAnalyticsService {
    
    private final MongoTemplate mongoTemplate;
    
    public SalesAnalyticsService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    public AggregationResults<DailySalesStats> getDailySalesStats(
            LocalDateTime startDate, LocalDateTime endDate) {
        
        Aggregation aggregation = Aggregation.newAggregation(
            // Stage 1: Match orders in date range
            Aggregation.match(Criteria.where("createdAt")
                .gte(startDate)
                .lte(endDate)
                .and("status").is("COMPLETED")),
            
            // Stage 2: Unwind items array
            Aggregation.unwind("items", true),
            
            // Stage 3: Group by date and calculate metrics
            Aggregation.group(
                ArithmeticOperators.DateToString.dateOf("$createdAt")
                    .toString("%Y-%m-%d"))
                .sum("items.subtotal").as("totalRevenue")
                .sum("items.quantity").as("unitsSold")
                .count().as("orderCount")
                .avg("items.subtotal").as("avgOrderValue"),
            
            // Stage 4: Project final result
            Aggregation.project()
                .and("_id").as("date")
                .andInclude("totalRevenue", "unitsSold", "orderCount", "avgOrderValue"),
            
            // Stage 5: Sort by date
            Aggregation.sort(org.springframework.data.domain.Sort.Direction.ASC, "_id")
        );
        
        return mongoTemplate.aggregate(aggregation, "orders", DailySalesStats.class);
    }
    
    public AggregationResults<ProductPerformance> getProductPerformance(int limit) {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.unwind("items", true),
            Aggregation.match(Criteria.where("status").is("COMPLETED")),
            
            Aggregation.group("items.productId")
                .first("items.productName").as("productName")
                .sum("items.quantity").as("totalUnitsSold")
                .sum("items.subtotal").as("totalRevenue")
                .count().as("orderCount")
                .avg("items.subtotal").as("avgUnitPrice"),
            
            Aggregation.addFields()
                .addField("profitMargin")
                .withValue(ArithmeticOperators.Divide
                    .divideOf(
                        ArithmeticOperators.Subtract
                            .subtractOf("$totalRevenue")
                            .from(BigDecimal.ZERO))
                    .by("$totalUnitsSold"))
                .build(),
            
            Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "totalRevenue"),
            Aggregation.limit(limit)
        );
        
        return mongoTemplate.aggregate(aggregation, "orders", ProductPerformance.class);
    }
}
```

## Customer Lifetime Value Pipeline

```java
// service/CustomerLTVService.java
package com.learning.service;

import com.learning.model.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CustomerLTVService {
    
    private final MongoTemplate mongoTemplate;
    
    public AggregationResults<CustomerLTV> calculateCustomerLTV() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("status").is("COMPLETED")),
            
            Aggregation.group("customerId")
                .sum("totalAmount").as("totalSpent")
                .count().as("orderCount")
                .min("createdAt").as("firstOrderDate")
                .max("createdAt").as("lastOrderDate")
                .addToSet("status").as("statuses"),
            
            Aggregation.addFields()
                .addField("daysSinceFirstOrder")
                .withValue(ArithmeticOperators.DateAsNumber.dateOf("$lastOrderDate")
                    .minus(ArithmeticOperators.DateAsNumber.dateOf("$firstOrderDate"))
                    .divideBy(86400000))
                .build()
                .addField("avgOrderValue")
                .withValue(ArithmeticOperators.Divide
                    .divideOf("$totalSpent")
                    .by("$orderCount"))
                .build()
                .addField("ordersPerMonth")
                .withValue(ArithmeticOperators.Divide
                    .divideOf("$orderCount")
                    .by(ArithmeticOperators.Divide
                        .divideOf(
                            ArithmeticOperators.DateAsNumber.dateOf("$lastOrderDate")
                                .minus(ArithmeticOperators.DateAsNumber.dateOf("$firstOrderDate")))
                        .by(2592000000.0)))
                .build()
                .addField("projected12MonthValue")
                .withValue(ArithmeticOperators.Multiply
                    .multiplyOf(
                        ArithmeticOperators.Divide
                            .divideOf("$totalSpent")
                            .by(ArithmeticOperators.Divide
                                .divideOf(
                                    ArithmeticOperators.DateAsNumber.dateOf("$lastOrderDate")
                                        .minus(ArithmeticOperators.DateAsNumber.dateOf("$firstOrderDate")))
                                .by(2592000000.0)))
                    .by(12))
                .build()
                .build(),
            
            Aggregation.match(Criteria.where("daysSinceFirstOrder").gt(0)),
            Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "totalSpent")
        );
        
        return mongoTemplate.aggregate(aggregation, "orders", CustomerLTV.class);
    }
}
```

## Inventory Analytics with Facet

```java
// service/InventoryAnalyticsService.java
package com.learning.service;

import com.learning.model.Product;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class InventoryAnalyticsService {
    
    private final MongoTemplate mongoTemplate;
    
    public AggregationResults<Map> getInventoryDashboard() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.facet()
                .and(
                    // Category breakdown
                    Aggregation.group("category")
                        .count().as("count")
                        .avg("price").as("avgPrice")
                        .sum("stockQuantity").as("totalStock")
                ).as("categoryStats")
                .and(
                    // Stock status
                    Aggregation.match(Criteria.where("stockQuantity").gt(0))
                        .group("_id").count().as("inStockProducts")
                ).as("inStockStats")
                .and(
                    // Low stock products
                    Aggregation.match(Criteria.where("stockQuantity")
                        .gt(0).lte(10))
                        .project()
                            .andInclude("name", "stockQuantity", "category")
                        .limit(10)
                ).as("lowStockProducts")
                .and(
                    // Out of stock count
                    Aggregation.match(Criteria.where("stockQuantity").is(0))
                        .count()
                ).as("outOfStockCount")
                .and(
                    // Price ranges
                    Aggregation.addFields()
                        .addField("priceRange")
                        .withValue(ConditionalOperators.switchCondition(
                            ConditionalOperators.Switch.CaseOperator.when(
                                ArithmeticOperators.Compare.gte("$price").then(1000))
                                .then("PREMIUM")))
                        .switchCase(
                            ConditionalOperators.Switch.CaseOperator.when(
                                ArithmeticOperators.Compare.gte("$price").then(100))
                                .then("STANDARD")))
                        .switchCase(
                            ConditionalOperators.Switch.CaseOperator.when(
                                ArithmeticOperators.Compare.gte("$price").then(0))
                                .then("BUDGET")))
                        .build())
                        .group("priceRange").count().as("count")
                ).as("priceRangeStats")
        );
        
        return mongoTemplate.aggregate(aggregation, "products", Map.class);
    }
}
```

## Lookups with Nested Joins

```java
// service/OrderLookupService.java
package com.learning.service;

import com.learning.model.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Service;

@Service
public class OrderLookupService {
    
    private final MongoTemplate mongoTemplate;
    
    public AggregationResults<EnrichedOrder> getEnrichedOrders() {
        Aggregation aggregation = Aggregation.newAggregation(
            // Match orders
            Aggregation.match(Criteria.where("status").is("COMPLETED")),
            
            // Lookup customer
            Aggregation.lookup("customers", "customerId", "_id", "customer"),
            Aggregation.unwind("customer", true),
            
            // Lookup products for each item
            Aggregation.lookup("products", "items.productId", "_id", "productDetails"),
            
            // Project final enriched order
            Aggregation.project()
                .andInclude("orderNumber", "totalAmount", "status", "createdAt")
                .and("customer.firstName")
                    .concat(" ")
                    .concatValueOf("customer.lastName")
                    .as("customerName")
                .and("customer.email").as("customerEmail")
                .and("customer.tier").as("customerTier")
                .andArrayElementOf("items", 0).as("primaryItem")
        );
        
        return mongoTemplate.aggregate(aggregation, "orders", EnrichedOrder.class);
    }
    
    public AggregationResults<MonthlyStats> getMonthlyStats(int year) {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("createdAt")
                .gte(java.time.LocalDate.of(year, 1, 1))
                .lt(java.time.LocalDate.of(year + 1, 1, 1))),
            
            Aggregation.project()
                .andInclude("totalAmount", "status")
                .and(DateOperators.Year.of("$createdAt")).as("year")
                .and(DateOperators.Month.of("$createdAt")).as("month"),
            
            Aggregation.match(Criteria.where("status").is("COMPLETED")),
            
            Aggregation.group(Fields.from(
                Fields.field("year"),
                Fields.field("month")))
                .sum("totalAmount").as("revenue")
                .count().as("orderCount"),
            
            Aggregation.sort(org.springframework.data.domain.Sort.Direction.ASC, "year", "month")
        );
        
        return mongoTemplate.aggregate(aggregation, "orders", MonthlyStats.class);
    }
}
```

## Real-Time Change Stream Processing

```java
// service/ChangeStreamService.java
package com.learning.service;

import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChangeStreamService {
    
    private final MongoTemplate mongoTemplate;
    private final Map<String, Integer> orderStatusCounts = new ConcurrentHashMap<>();
    private final Map<String, Double> categoryRevenue = new ConcurrentHashMap<>();
    
    @org.springframework.context.event.EventListener
    public void startWatchingOrderChanges() {
        Flux<ChangeStreamDocument<Document>> changeStream = 
            Flux.from(mongoTemplate.changeStream("orders", 
                org.springframework.data.mongodb.core.ChangeStreamOptions.builder()
                    .filter(org.springframework.data.mongodb.core.query.Query
                        .query(Criteria.where("operationType").is("insert")))
                    .fullDocument(FullDocument.UPDATE_LOOKUP)
                    .build(),
                Document.class))
            .subscribeOn(Schedulers.boundedElastic());
        
        changeStream.subscribe(change -> {
            Document order = change.getFullDocument();
            if (order != null) {
                processOrderChange(order);
            }
        });
    }
    
    private void processOrderChange(Document order) {
        String status = order.getString("status");
        orderStatusCounts.merge(status, 1, Integer::sum);
        
        Object totalObj = order.get("totalAmount");
        if (totalObj instanceof Number) {
            double amount = ((Number) totalObj).doubleValue();
            
            Object categoryObj = order.get("category");
            if (categoryObj != null) {
                String category = categoryObj.toString();
                categoryRevenue.merge(category, amount, Double::sum);
            }
        }
        
        System.out.println("Order change processed: " + order.getString("orderNumber"));
        printStats();
    }
    
    private void printStats() {
        System.out.println("\n=== Real-Time Stats ===");
        System.out.println("Order Status Counts:");
        orderStatusCounts.forEach((status, count) -> 
            System.out.println("  " + status + ": " + count));
        System.out.println("Category Revenue:");
        categoryRevenue.forEach((category, revenue) -> 
            System.out.println("  " + category + ": $" + String.format("%.2f", revenue)));
        System.out.println("========================\n");
    }
}
```

## Graph Lookup for Hierarchical Data

```java
// service/CategoryHierarchyService.java
package com.learning.service;

import com.learning.model.Category;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryHierarchyService {
    
    private final MongoTemplate mongoTemplate;
    
    public AggregationResults<CategoryWithDescendants> 
            getCategoryWithDescendants(String categoryId) {
        
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("_id").is(categoryId)),
            
            Aggregation.graphLookup("categories")
                .connectTo("_id")
                .connectFrom("parentId")
                .maxDepth(10)
                .depthField("depth")
                .restrict(Criteria.where("active").is(true))
                .as("descendants")
        );
        
        return mongoTemplate.aggregate(aggregation, "categories", 
            CategoryWithDescendants.class);
    }
    
    public AggregationResults<AncestorPath> getCategoryAncestorPath(String categoryId) {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("_id").is(categoryId)),
            
            Aggregation.graphLookup("categories")
                .connectTo("parentId")
                .connectFrom("_id")
                .maxDepth(10)
                .as("ancestors")
        );
        
        return mongoTemplate.aggregate(aggregation, "categories", 
            CategoryWithAncestors.class);
    }
}
```

## Bucket and Histogram Aggregations

```java
// service/AnalyticsBucketService.java
package com.learning.service;

import com.learning.model.Product;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AnalyticsBucketService {
    
    private final MongoTemplate mongoTemplate;
    
    public AggregationResults<PriceBucket> getPriceHistogram() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("active").is(true)),
            
            Aggregation.bucket("price")
                .withBoundaries(0, 50, 100, 200, 500, 1000, 5000)
                .withDefaultBucket("Other")
                .andOutput(AccumulatorOperators.Sum.sumOf(1))
                    .as("count")
                .andOutput(AccumulatorOperators.Sum.sumOf("$stockQuantity"))
                    .as("totalStock")
                .andOutput(AccumulatorOperators.Avg.avgOf("$rating"))
                    .as("avgRating")
        );
        
        return mongoTemplate.aggregate(aggregation, "products", PriceBucket.class);
    }
    
    public AggregationResults<Map> getCustomerSegments() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("status").is("COMPLETED")),
            
            Aggregation.group("customerId")
                .sum("totalAmount").as("totalSpent")
                .count().as("orderCount"),
            
            Aggregation.bucket("$totalSpent")
                .withBoundaries(0, 100, 500, 1000, 5000, 10000, Double.MAX_VALUE)
                .withDefaultBucket("VIP")
                .andOutput(AccumulatorOperators.Sum.sumOf(1))
                    .as("customerCount")
                .andOutput(AccumulatorOperators.Avg.avgOf("orderCount"))
                    .as("avgOrdersPerSegment")
        );
        
        return mongoTemplate.aggregate(aggregation, "orders", Map.class);
    }
}
```