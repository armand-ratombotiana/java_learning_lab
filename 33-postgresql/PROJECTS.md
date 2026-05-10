# PostgreSQL Advanced Projects

This module covers advanced PostgreSQL features including JSONB data types, full-text search capabilities, and table partitioning strategies for building scalable data-driven Java applications.

## Mini-Project: Document Management System with JSONB (2-4 Hours)

### Overview

Build a document management system that leverages PostgreSQL's JSONB data type for flexible metadata storage and Full-Text Search for powerful document searching capabilities.

### Technology Stack

- Java 21 with Spring Boot 3.x
- PostgreSQL 15+ with JSONB and full-text search extensions
- Spring Data JPA with native JSON queries
- Maven build system

### Project Structure

```
postgresql-learning/
├── pom.xml
└── src/
    └── main/
        ├── java/com/learning/postgres/
        │   ├── DocumentManagementApplication.java
        │   ├── entity/
        │   │   └── Document.java
        │   ├── repository/
        │   │   └── DocumentRepository.java
        │   └── service/
        │       └── DocumentSearchService.java
        └── resources/
            └── application.properties
```

### Implementation

**pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.learning</groupId>
        <artifactId>java-learning-parent</artifactId>
        <version>1.0.0</version>
    </parent>
    
    <artifactId>postgresql-document-mgmt</artifactId>
    
    <properties>
        <java.version>21</java.version>
        <spring-boot.version>3.2.0</spring-boot.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.7.1</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${spring-boot.version}</version>
            </plugin>
        </plugins>
    </build>
</project>
```

**Document.java (Entity)**

```java
package com.learning.postgres.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "metadata", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata;
    
    @Column(columnDefinition = "tsvector")
    private String searchVector;
    
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
    
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public String getSearchVector() { return searchVector; }
    public void setSearchVector(String searchVector) { this.searchVector = searchVector; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
```

**Application.java (Main)**

```java
package com.learning.postgres;

import com.learning.postgres.entity.Document;
import com.learning.postgres.service.DocumentSearchService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.*;

@SpringBootApplication
@EnableJpaRepositories
public class DocumentManagementApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DocumentManagementApplication.class, args);
    }
    
    @Bean
    CommandLineRunner demo(DocumentSearchService service) {
        return args -> {
            System.out.println("=== PostgreSQL JSONB + Full-Text Search Demo ===\n");
            
            Document doc1 = new Document();
            doc1.setTitle("Java Programming Guide");
            doc1.setContent("Java is a high-level, class-based, object-oriented programming language.");
            doc1.setMetadata(Map.of(
                "author", "John Doe",
                "tags", List.of("programming", "java", "tutorial"),
                "version", "1.0",
                "category", Map.of("primary", "technology", "secondary", "development")
            ));
            
            Document doc2 = new Document();
            doc2.setTitle("PostgreSQL Advanced Features");
            doc2.setContent("PostgreSQL supports JSONB, full-text search, and partitioning for scalable applications.");
            doc2.setMetadata(Map.of(
                "author", "Jane Smith",
                "tags", List.of("database", "postgresql", "advanced"),
                "version", "2.0"
            ));
            
            Document doc3 = new Document();
            doc3.setTitle("Spring Boot Best Practices");
            doc3.setContent("Spring Boot simplifies Java development with autoconfiguration and opinionated defaults.");
            doc3.setMetadata(Map.of(
                "author", "John Doe",
                "tags", List.of("spring", "java", "best-practices"),
                "version", "1.5"
            ));
            
            service.saveDocument(doc1);
            service.saveDocument(doc2);
            service.saveDocument(doc3);
            
            System.out.println("Inserted 3 documents with JSONB metadata\n");
            
            System.out.println("1. Search for 'java programming':");
            service.search("java programming").forEach(d -> 
                System.out.println("   - " + d.getTitle()));
            
            System.out.println("\n2. Query documents with tag 'java':");
            service.findByMetadataTag("java").forEach(d -> 
                System.out.println("   - " + d.getTitle() + " (tags: " + d.getMetadata().get("tags") + ")"));
            
            System.out.println("\n3. Query documents by author 'John Doe':");
            service.findByMetadataAuthor("John Doe").forEach(d -> 
                System.out.println("   - " + d.getTitle()));
            
            System.out.println("\n4. Find documents with nested metadata category:");
            service.findByCategory("technology").forEach(d -> 
                System.out.println("   - " + d.getTitle()));
            
            System.out.println("\n=== Demo Complete ===");
        };
    }
}
```

**DocumentSearchService.java**

```java
package com.learning.postgres.service;

import com.learning.postgres.entity.Document;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentSearchService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public Document saveDocument(Document doc) {
        entityManager.persist(doc);
        updateSearchVector(doc);
        entityManager.flush();
        return doc;
    }
    
    private void updateSearchVector(Document doc) {
        String sql = "UPDATE documents SET search_vector = " +
            "to_tsvector('english', coalesce(title,'') || ' ' || coalesce(content,'')) " +
            "WHERE id = ?";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, doc.getId());
        query.executeUpdate();
    }
    
    @SuppressWarnings("unchecked")
    public List<Document> search(String searchTerm) {
        String sql = "SELECT * FROM documents WHERE search_vector @@ to_tsquery('english', ?)" +
            "ORDER BY ts_rank(search_vector, to_tsquery('english', ?)) DESC";
        Query query = entityManager.createNativeQuery(sql, Document.class);
        query.setParameter(1, searchTerm);
        query.setParameter(2, searchTerm);
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<Document> findByMetadataTag(String tag) {
        String sql = "SELECT * FROM documents WHERE metadata->'tags' ? ?";
        Query query = entityManager.createNativeQuery(sql, Document.class);
        query.setParameter(1, tag);
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<Document> findByMetadataAuthor(String author) {
        String sql = "SELECT * FROM documents WHERE metadata->>'author' = ?";
        Query query = entityManager.createNativeQuery(sql, Document.class);
        query.setParameter(1, author);
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<Document> findByCategory(String category) {
        String sql = "SELECT * FROM documents WHERE metadata->'category'->>'primary' = ?";
        Query query = entityManager.createNativeQuery(sql, Document.class);
        query.setParameter(1, category);
        return query.getResultList();
    }
}
```

**application.properties**

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/documents_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Database Setup SQL

```sql
CREATE DATABASE documents_db;

-- Enable extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "btree_gin";

-- Create document table with partitioning consideration
CREATE TABLE documents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(500) NOT NULL,
    content TEXT,
    metadata JSONB,
    search_vector tsvector,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create GIN index for JSONB queries
CREATE INDEX idx_documents_metadata ON documents USING GIN(metadata);

-- Create full-text search index
CREATE INDEX idx_documents_fts ON documents USING GIN(search_vector);

-- Create triggers for auto-updating search vector
CREATE OR REPLACE FUNCTION documents_search_trigger() RETURNS trigger AS $$
BEGIN
    NEW.search_vector := to_tsvector('english', coalesce(NEW.title,'') || ' ' || coalesce(NEW.content,''));
    RETURN NEW;
END
$$ LANGUAGE plpgsql;

CREATE TRIGGER documents_search_update 
    BEFORE INSERT OR UPDATE ON documents
    FOR EACH ROW EXECUTE FUNCTION documents_search_trigger();
```

### Build and Run

```bash
# Navigate to project directory
cd 33-postgresql/postgresql-learning

# Build the project
mvn clean package -DskipTests

# Run the application
mvn spring-boot:run
```

### Expected Output

```
=== PostgreSQL JSONB + Full-Text Search Demo ===

Inserted 3 documents with JSONB metadata

1. Search for 'java programming':
   - Java Programming Guide

2. Query documents with tag 'java':
   - Java Programming Guide (tags: [programming, java, tutorial])
   - Spring Boot Best Practices (tags: [spring, java, best-practices])

3. Query documents by author 'John Doe':
   - Java Programming Guide
   - Spring Boot Best Practices

4. Find documents with nested metadata category:
   - Java Programming Guide

=== Demo Complete ===
```

---

## Real-World Project: E-Commerce Analytics Platform with Partitioning (8+ Hours)

### Overview

Build a comprehensive e-commerce analytics platform that leverages PostgreSQL's advanced features including table partitioning for handling large datasets, JSONB for flexible product attributes, full-text search for product discovery, and advanced SQL analytics for business intelligence.

### Key Features

1. **Partitioned Orders Table** - Range partitioning by date for efficient time-based queries
2. **JSONB Product Catalog** - Flexible product attributes with variant support
3. **Full-Text Search** - Product search with ranking and relevance
4. **Analytics Queries** - Rolling aggregations and reports
5. **Materialized Views** - Cached analytics for performance

### Project Structure

```
postgresql-learning/
├── pom.xml
└── src/
    └── main/
        ├── java/com/learning/postgres/
        │   ├── EcommerceAnalyticsApplication.java
        │   ├── entity/
        │   │   ├── Product.java
        │   │   ├── Order.java
        │   │   └── OrderItem.java
        │   ├── repository/
        │   │   ├── ProductRepository.java
        │   │   └── OrderRepository.java
        │   ├── service/
        │   │   ├── ProductCatalogService.java
        │   │   ├── OrderAnalyticsService.java
        │   │   └── SearchService.java
        │   ├── controller/
        │   │   ├── ProductController.java
        │   │   └── AnalyticsController.java
        │   └── dto/
        │           ├── SalesReport.java
        │           └── ProductSearchResult.java
        └── resources/
            ├── application.properties
            └── schema.sql
```

### Implementation

**schema.sql (Partitioned Tables)**

```sql
-- Create partitioned orders table by month
CREATE TABLE orders (
    id BIGSERIAL,
    order_date DATE NOT NULL,
    customer_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id, order_date)
) PARTITION BY RANGE (order_date);

-- Create partitions for each month
CREATE TABLE orders_2024_01 PARTITION OF orders
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

CREATE TABLE orders_2024_02 PARTITION OF orders
    FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');

CREATE TABLE orders_2024_03 PARTITION OF orders
    FOR VALUES FROM ('2024-03-01') TO ('2024-04-01');

-- Additional partitions as needed

-- Create order items table
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    order_date DATE NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id, order_date) REFERENCES orders(id, order_date)
);

-- Indexes for partitioning
CREATE INDEX idx_orders_date ON orders (order_date);
CREATE INDEX idx_orders_customer ON orders (customer_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_order_items_order ON order_items (order_id, order_date);
CREATE INDEX idx_order_items_product ON order_items (product_id);

-- Products table with JSONB
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(500) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    price DECIMAL(10,2) NOT NULL,
    attributes JSONB,
    search_vector tsvector,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- GIN indexes
CREATE INDEX idx_products_attributes ON products USING GIN(attributes);
CREATE INDEX idx_products_search ON products USING GIN(search_vector);

-- Materialized view for daily sales
CREATE MATERIALIZED VIEW daily_sales AS
SELECT 
    order_date,
    COUNT(*) as order_count,
    SUM(total_amount) as revenue,
    COUNT(DISTINCT customer_id) as unique_customers
FROM orders
WHERE status = 'COMPLETED'
GROUP BY order_date
ORDER BY order_date;

-- Refresh concurrent function
CREATE OR REPLACE FUNCTION refresh_daily_sales() RETURNS void AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY daily_sales;
END;
$$ LANGUAGE plpgsql;
```

**Product.java**

```java
package com.learning.postgres.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    private String category;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> attributes;
    
    @Column(columnDefinition = "tsvector")
    private String searchVector;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updateSearchVector();
    }
    
    private void updateSearchVector() {
        String text = name + " " + (description != null ? description : "") + " " + category;
        this.searchVector = text.toLowerCase();
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
    public String getSearchVector() { return searchVector; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

**Order.java**

```java
package com.learning.postgres.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "orders")
@IdClass(OrderId.class)
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Id
    @Column(name = "order_date")
    private LocalDate orderDate;
    
    @Column(name = "customer_id")
    private Long customerId;
    
    private String status;
    
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
    
    @PrePersist
    protected void onCreate() {
        if (orderDate == null) orderDate = LocalDate.now();
        createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
```

**OrderId.java**

```java
package com.learning.postgres.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class OrderId implements Serializable {
    private Long id;
    private LocalDate orderDate;
    
    public OrderId() {}
    
    public OrderId(Long id, LocalDate orderDate) {
        this.id = id;
        this.orderDate = orderDate;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderId orderId = (OrderId) o;
        return Objects.equals(id, orderId.id) && Objects.equals(orderDate, orderId.orderDate);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, orderDate);
    }
}
```

**OrderAnalyticsService.java**

```java
package com.learning.postgres.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class OrderAnalyticsService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Transactional
    public void createOrder(Order order) {
        entityManager.persist(order);
        entityManager.flush();
        
        for (OrderItem item : order.getItems()) {
            item.setOrderDate(order.getOrderDate());
            entityManager.persist(item);
        }
    }
    
    public List<Map<String, Object>> getDailySales(LocalDate start, LocalDate end) {
        String sql = """
            SELECT 
                order_date,
                COUNT(*) as order_count,
                SUM(total_amount) as revenue,
                COUNT(DISTINCT customer_id) as unique_customers
            FROM orders
            WHERE order_date BETWEEN ? AND ?
            AND status = 'COMPLETED'
            GROUP BY order_date
            ORDER BY order_date
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, start);
        query.setParameter(2, end);
        
        return mapResults(query.getResultList());
    }
    
    public List<Map<String, Object>> getCategorySales(LocalDate start, LocalDate end) {
        String sql = """
            SELECT 
                p.category,
                COUNT(DISTINCT o.id) as order_count,
                SUM(oi.quantity) as units_sold,
                SUM(oi.quantity * oi.unit_price) as revenue
            FROM orders o
            JOIN order_items oi ON o.id = oi.order_id AND o.order_date = oi.order_date
            JOIN products p ON oi.product_id = p.id
            WHERE o.order_date BETWEEN ? AND ?
            AND o.status = 'COMPLETED'
            GROUP BY p.category
            ORDER BY revenue DESC
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, start);
        query.setParameter(2, end);
        
        return mapResults(query.getResultList());
    }
    
    public Map<String, Object> getCustomerLifetimeValue(Long customerId) {
        String sql = """
            SELECT 
                COUNT(*) as total_orders,
                SUM(total_amount) as total_revenue,
                AVG(total_amount) as avg_order_value,
                MIN(order_date) as first_order,
                MAX(order_date) as last_order
            FROM orders
            WHERE customer_id = ?
            AND status = 'COMPLETED'
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, customerId);
        
        List<Object[]> results = query.getResultList();
        if (results.isEmpty()) {
            return Map.of(
                "customerId", customerId,
                "totalOrders", 0,
                "totalRevenue", BigDecimal.ZERO
            );
        }
        
        Object[] row = results.get(0);
        return Map.of(
            "customerId", customerId,
            "totalOrders", ((Number) row[0]).intValue(),
            "totalRevenue", row[1],
            "avgOrderValue", row[2],
            "firstOrder", row[3],
            "lastOrder", row[4]
        );
    }
    
    public List<Map<String, Object>> getRollingAverageSales(int days) {
        String sql = """
            SELECT 
                order_date,
                AVG(daily_revenue) OVER (
                    ORDER BY order_date 
                    ROWS BETWEEN ? PRECEDING AND CURRENT ROW
                ) as rolling_avg
            FROM (
                SELECT 
                    order_date,
                    SUM(total_amount) as daily_revenue
                FROM orders
                WHERE status = 'COMPLETED'
                GROUP BY order_date
            ) daily
            ORDER BY order_date
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, days - 1);
        
        return mapResults(query.getResultList());
    }
    
    public void refreshMaterializedView() {
        entityManager.createNativeQuery("SELECT refresh_daily_sales()").executeUpdate();
    }
    
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> mapResults(List<Object[]> results) {
        List<Map<String, Object>> mapped = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (int i = 0; i < row.length; i++) {
                map.put("col_" + i, row[i]);
            }
            mapped.add(map);
        }
        return mapped;
    }
}
```

**ProductCatalogService.java**

```java
package com.learning.postgres.service;

import com.learning.postgres.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProductCatalogService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Transactional
    public Product createProduct(Product product) {
        entityManager.persist(product);
        return product;
    }
    
    @Transactional
    public List<Product> createProducts(List<Product> products) {
        for (Product product : products) {
            entityManager.persist(product);
        }
        return products;
    }
    
    public List<Product> searchProducts(String query, String category, 
            Map<String, Object> attributes) {
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (query != null && !query.isEmpty()) {
            sql.append(" AND search_vector @@ to_tsquery('english', ?)");
            params.add(query);
        }
        
        if (category != null) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        
        if (attributes != null && !attributes.isEmpty()) {
            for (String key : attributes.keySet()) {
                sql.append(" AND attributes->>? = ?");
                params.add(key);
                params.add(attributes.get(key).toString());
            }
        }
        
        Query nativeQuery = entityManager.createNativeQuery(sql.toString(), Product.class);
        for (int i = 0; i < params.size(); i++) {
            nativeQuery.setParameter(i + 1, params.get(i));
        }
        
        return nativeQuery.getResultList();
    }
    
    public List<Product> findByAttributePath(String key, String value) {
        String sql = "SELECT * FROM products WHERE attributes #>> ? = ?";
        Query query = entityManager.createNativeQuery(sql, Product.class);
        query.setParameter(1, "{" + key + "}");
        query.setParameter(2, value);
        return query.getResultList();
    }
    
    public Map<String, Object> getCatalogStats() {
        String sql = """
            SELECT 
                COUNT(*) as total_products,
                COUNT(DISTINCT category) as total_categories,
                MIN(price) as min_price,
                MAX(price) as max_price,
                AVG(price) as avg_price
            FROM products
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        Object[] result = (Object[]) query.getSingleResult();
        
        return Map.of(
            "totalProducts", ((Number) result[0]).longValue(),
            "totalCategories", ((Number) result[1]).intValue(),
            "minPrice", result[2],
            "maxPrice", result[3],
            "avgPrice", result[4]
        );
    }
}
```

### Build and Run

```bash
cd 33-postgresql/postgresql-learning
mvn clean package -DskipTests
mvn spring-boot:run

# The application will start on http://localhost:8080
```

### API Endpoints

```
# Product Search
GET /api/products/search?q=laptop&category=electronics
GET /api/products/filter?brand=apple&color=space-gray

# Analytics
GET /api/analytics/daily?start=2024-01-01&end=2024-03-31
GET /api/analytics/category?start=2024-01-01&end=2024-03-31
GET /api/analytics/customer/123/lifetime-value
GET /api/analytics/rolling-avg?days=30

# Health check
GET /health
```

### Learning Outcomes

After completing these projects, you will understand:

1. **JSONB Data Types** - Store and query flexible JSON data in PostgreSQL
2. **Full-Text Search** - Implement powerful search with ranking and relevance
3. **Table Partitioning** - Distribute large tables across partitions for performance
4. **Materialized Views** - Cache expensive queries for faster reporting
5. **Advanced SQL Analytics** - Rolling averages, aggregations, and time-series analysis

### References

- PostgreSQL JSONB Documentation: https://www.postgresql.org/docs/current/datatype-json.html
- Full-Text Search: https://www.postgresql.org/docs/current/textsearch.html
- Table Partitioning: https://www.postgresql.org/docs/current/ddl-partitioning.html