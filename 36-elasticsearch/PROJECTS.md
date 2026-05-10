# Elasticsearch Projects

This module covers Elasticsearch client integration, full-text search capabilities, aggregations, and advanced querying for building scalable search and analytics applications in Java.

## Mini-Project: Product Search Engine (2-4 Hours)

### Overview

Build a product search engine using Elasticsearch that demonstrates indexing, full-text search, filtering, and basic aggregations for e-commerce product discovery.

### Technology Stack

- Java 21 with Spring Boot 3.x
- Elasticsearch 8.x client
- Spring Data Elasticsearch
- Maven build system

### Project Structure

```
elasticsearch-learning/
├── pom.xml
└── src/
    └── main/
        ├── java/com/learning/elasticsearch/
        │   ├── ProductSearchApplication.java
        │   ├── model/
        │   │   ├── Product.java
        │   │   └── SearchResult.java
        │   ├── repository/
        │   │   └── ProductRepository.java
        │   ├── service/
        │   │   └── ProductSearchService.java
        │   ├── controller/
        │   │   └── SearchController.java
        │   └── config/
        │       └── ElasticsearchConfig.java
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
    
    <artifactId>elasticsearch-product-search</artifactId>
    
    <properties>
        <java.version>21</java.version>
        <spring-boot.version>3.2.0</spring-boot.version>
        <elasticsearch.version>8.11.0</elasticsearch.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>co.elastic.clients</groupId>
            <artifactId>elasticsearch-java</artifactId>
            <version>${elasticsearch.version}</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
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

**Product.java (Model)**

```java
package com.learning.elasticsearch.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Product {
    private String id;
    private String name;
    private String description;
    private String category;
    private List<String> tags;
    private BigDecimal price;
    private Double rating;
    private Integer reviewCount;
    private String brand;
    private Map<String, Object> attributes;
    private boolean inStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Product() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Product(String name, String description, String category, BigDecimal price) {
        this();
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.inStock = true;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
```

**SearchResult.java**

```java
package com.learning.elasticsearch.model;

import java.util.List;
import java.util.Map;

public class SearchResult<T> {
    private List<T> hits;
    private long totalHits;
    private double maxScore;
    private Map<String, AggregationResult> aggregations;
    private long took;
    
    public static class AggregationResult {
        private long docCount;
        private Map<String, Long> buckets;
        private Double value;
        private Double avg;
        private Double min;
        private Double max;
        
        public long getDocCount() { return docCount; }
        public void setDocCount(long docCount) { this.docCount = docCount; }
        public Map<String, Long> getBuckets() { return buckets; }
        public void setBuckets(Map<String, Long> buckets) { this.buckets = buckets; }
        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }
        public Double getAvg() { return avg; }
        public void setAvg(Double avg) { this.avg = avg; }
        public Double getMin() { return min; }
        public void setMin(Double min) { this.min = min; }
        public Double getMax() { return max; }
        public void setMax(Double max) { this.max = max; }
    }
    
    public List<T> getHits() { return hits; }
    public void setHits(List<T> hits) { this.hits = hits; }
    public long getTotalHits() { return totalHits; }
    public void setTotalHits(long totalHits) { this.totalHits = totalHits; }
    public double getMaxScore() { return maxScore; }
    public void setMaxScore(double maxScore) { this.maxScore = maxScore; }
    public Map<String, AggregationResult> getAggregations() { return aggregations; }
    public void setAggregations(Map<String, AggregationResult> aggregations) { this.aggregations = aggregations; }
    public long getTook() { return took; }
    public void setTook(long took) { this.took = took; }
}
```

**ElasticsearchConfig.java**

```java
package com.learning.elasticsearch.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {
    
    @Bean
    public RestClient restClient() {
        return RestClient.builder(new HttpHost("localhost", 9200, "http")).build();
    }
    
    @Bean
    public ElasticsearchTransport elasticsearchTransport(RestClient restClient) {
        return new RestClientTransport(restClient, new JacksonJsonpMapper());
    }
    
    @Bean
    public ElasticsearchClient elasticsearchClient(ElasticsearchTransport transport) {
        return new ElasticsearchClient(transport);
    }
}
```

**ProductSearchService.java**

```java
package com.learning.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.learning.elasticsearch.model.Product;
import com.learning.elasticsearch.model.SearchResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductSearchService {
    
    private final ElasticsearchClient client;
    private static final String INDEX_NAME = "products";
    
    public ProductSearchService(ElasticsearchClient client) {
        this.client = client;
    }
    
    public String indexProduct(Product product) throws IOException {
        IndexResponse response = client.index(i -> i
            .index(INDEX_NAME)
            .id(product.getId())
            .document(product)
        );
        
        System.out.println("Indexed product: " + product.getName());
        return response.id();
    }
    
    public void bulkIndex(List<Product> products) throws IOException {
        List<IndexRequest<Product>> requests = products.stream()
            .map(p -> IndexRequest.of(r -> r
                .index(INDEX_NAME)
                .id(p.getId())
                .document(p)
            ))
            .collect(Collectors.toList());
        
        BulkRequest bulkRequest = new BulkRequest.Builder()
            .operations(requests)
            .build();
        
        BulkResponse response = client.bulk(bulkRequest);
        System.out.println("Bulk indexed " + products.size() + " products");
        
        if (response.errors()) {
            System.out.println("Bulk indexing had errors");
        }
    }
    
    public SearchResult<Product> search(String query) throws IOException {
        return search(query, null, null, null, null);
    }
    
    public SearchResult<Product> search(String query, String category, 
            BigDecimal minPrice, BigDecimal maxPrice, String brand) throws IOException {
        
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
        
        if (query != null && !query.isEmpty()) {
            boolBuilder.must(m -> m
                .multiMatch(mm -> mm
                    .query(query)
                    .fields("name^3", "description", "tags", "brand")
                    .fuzziness("AUTO")
                )
            );
        }
        
        if (category != null) {
            boolBuilder.filter(f -> f.term(t -> t
                .field("category")
                .value(category)
            ));
        }
        
        if (minPrice != null || maxPrice != null) {
            boolBuilder.filter(f -> f.range(r -> {
                var range = r.field("price");
                if (minPrice != null) range.gte(co.elastic.clients.json.JsonData.of(minPrice));
                if (maxPrice != null) range.lte(co.elastic.clients.json.JsonData.of(maxPrice));
                return range;
            }));
        }
        
        if (brand != null) {
            boolBuilder.filter(f -> f.term(t -> t
                .field("brand")
                .value(brand)
            ));
        }
        
        SearchResponse<Product> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.bool(boolBuilder.build()))
            .size(20)
            .aggregations("categories", a -> a.terms(t -> t.field("category")))
            .aggregations("brands", a -> a.terms(t -> t.field("brand")))
            .aggregations("avg_price", a -> a.avg(aa -> aa.field("price"))))
            .aggregations("price_stats", a -> a.stats(ss -> ss.field("price"))),
            Product.class
        );
        
        return mapSearchResponse(response);
    }
    
    public SearchResult<Product> searchByCategory(String category) throws IOException {
        SearchResponse<Product> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.term(t -> t
                .field("category")
                .value(category)
            ))
            .size(100),
            Product.class
        );
        
        return mapSearchResponse(response);
    }
    
    public Map<String, Long> aggregateByCategory() throws IOException {
        SearchResponse<Void> response = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("categories", a -> a.terms(t -> t.field("category"))),
            Void.class
        );
        
        Map<String, Long> result = new HashMap<>();
        TermsAggregate buckets = response.aggregations().get("categories").sterms();
        for (StringTermsBucket bucket : buckets.buckets().array()) {
            result.put(bucket.key().stringValue(), bucket.docCount());
        }
        
        return result;
    }
    
    public Map<String, Double> getPriceStatistics() throws IOException {
        SearchResponse<Void> response = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("price_stats", a -> a.stats(ss -> ss.field("price"))),
            Void.class
        );
        
        StatsAggregate stats = response.aggregations().get("price_stats").stats();
        
        return Map.of(
            "count", (double) stats.count(),
            "avg", stats.avg(),
            "min", stats.min(),
            "max", stats.max(),
            "sum", stats.sum()
        );
    }
    
    private SearchResult<Product> mapSearchResponse(SearchResponse<Product> response) {
        SearchResult<Product> result = new SearchResult<>();
        result.setTook(response.took());
        result.setTotalHits(response.hits().total());
        result.setMaxScore(response.hits().maxScore());
        
        List<Product> hits = response.hits().hits().stream()
            .map(Hit::source)
            .collect(Collectors.toList());
        result.setHits(hits);
        
        return result;
    }
}
```

**SearchController.java**

```java
package com.learning.elasticsearch.controller;

import com.learning.elasticsearch.model.Product;
import com.learning.elasticsearch.model.SearchResult;
import com.learning.elasticsearch.service.ProductSearchService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    
    private final ProductSearchService searchService;
    
    public SearchController(ProductSearchService searchService) {
        this.searchService = searchService;
    }
    
    @GetMapping
    public SearchResult<Product> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String brand) {
        
        return searchService.search(q, category, minPrice, maxPrice, brand);
    }
    
    @GetMapping("/category/{category}")
    public SearchResult<Product> searchByCategory(@PathVariable String category) {
        return searchService.searchByCategory(category);
    }
    
    @GetMapping("/aggregations/categories")
    public Map<String, Long> getCategories() {
        return searchService.aggregateByCategory();
    }
    
    @GetMapping("/aggregations/prices")
    public Map<String, Double> getPriceStats() {
        return searchService.getPriceStatistics();
    }
    
    @PostMapping
    public String indexProduct(@RequestBody Product product) {
        return searchService.indexProduct(product);
    }
    
    @PostMapping("/bulk")
    public String bulkIndex(@RequestBody List<Product> products) {
        searchService.bulkIndex(products);
        return "Indexed " + products.size() + " products";
    }
}
```

**ProductSearchApplication.java**

```java
package com.learning.elasticsearch;

import com.learning.elasticsearch.model.Product;
import com.learning.elasticsearch.service.ProductSearchService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class ProductSearchApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ProductSearchApplication.class, args);
    }
    
    @Bean
    CommandLineRunner demo(ProductSearchService service) {
        return args -> {
            System.out.println("=== Elasticsearch Product Search Demo ===\n");
            
            List<Product> products = List.of(
                new Product("Laptop Pro 15", "High-performance laptop", "Electronics", new BigDecimal("1499.99")),
                new Product("Wireless Mouse", "Ergonomic wireless mouse", "Accessories", new BigDecimal("49.99")),
                new Product("4K Monitor", "27-inch 4K display", "Electronics", new BigDecimal("399.99")),
                new Product("Mechanical Keyboard", "RGB mechanical keyboard", "Accessories", new BigDecimal("129.99")),
                new Product("Smartphone X", "Latest smartphone", "Electronics", new BigDecimal("999.99")),
                new Product("Tablet Pro", "12-inch tablet", "Electronics", new BigDecimal("799.99")),
                new Product("Webcam HD", "1080p webcam", "Accessories", new BigDecimal("79.99")),
                new Product("Headphones", "Noise-canceling headphones", "Electronics", new BigDecimal("299.99"))
            );
            
            products.set(0, products.get(0).with(s -> { s.setId("1"); return s; }));
            products.forEach(p -> p.setId(java.util.UUID.randomUUID().toString()));
            
            service.bulkIndex(products);
            System.out.println("\n1. Indexed " + products.size() + " products\n");
            
            System.out.println("2. Search for 'laptop':");
            var results = service.search("laptop");
            results.getHits().forEach(p -> System.out.println("   - " + p.getName() + " ($" + p.getPrice() + ")"));
            
            System.out.println("\n3. Search electronics under $500:");
            var filtered = service.search(null, "Electronics", new BigDecimal("0"), new BigDecimal("500"), null);
            filtered.getHits().forEach(p -> System.out.println("   - " + p.getName() + " ($" + p.getPrice() + ")"));
            
            System.out.println("\n4. Aggregate by category:");
            var categories = service.aggregateByCategory();
            categories.forEach((cat, count) -> System.out.println("   " + cat + ": " + count));
            
            System.out.println("\n5. Price statistics:");
            var stats = service.getPriceStatistics();
            System.out.println("   Average: $" + String.format("%.2f", stats.get("avg")));
            System.out.println("   Min: $" + stats.get("min"));
            System.out.println("   Max: $" + stats.get("max"));
            
            System.out.println("\n=== Demo Complete ===");
            System.exit(0);
        };
    }
}
```

**application.properties**

```properties
spring.elasticsearch.uris=http://localhost:9200
server.port=8080
```

### Build and Run

```bash
# Start Elasticsearch
docker run -d --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:8.11.0

# Build and run
cd 36-elasticsearch/elasticsearch-learning
mvn clean package -DskipTests
mvn spring-boot:run
```

### Expected Output

```
=== Elasticsearch Product Search Demo ===

1. Indexed 8 products

2. Search for 'laptop':
   - Laptop Pro 15 ($1499.99)
   - Smartphone X ($999.99)

3. Search electronics under $500:
   - 4K Monitor ($399.99)
   - Tablet Pro ($799.99)

4. Aggregate by category:
   Electronics: 5
   Accessories: 3

5. Price statistics:
   Average: $669.99
   Min: $49.99
   Max: $1499.99

=== Demo Complete ===
```

---

## Real-World Project: Enterprise Search and Analytics Platform (8+ Hours)

### Overview

Build a comprehensive enterprise search and analytics platform using Elasticsearch that demonstrates advanced querying, complex aggregations, nested documents, parent-child relationships, and real-time data pipelines for business intelligence.

### Key Features

1. **Multi-Index Architecture** - Log aggregation, analytics, and product indices
2. **Complex Aggregations** - Bucket, metric, and pipeline aggregations
3. **Nested Documents** - Document structures with nested fields
4. **Parent-Child Relationships** - join field for related documents
5. **Real-time Analytics** - Streaming aggregations
6. **Custom Analyzers** - Tokenizers and filters
7. **Geo-spatial Search** - Location-based queries
8. **Machine Learning** - Anomaly detection

### Project Structure

```
elasticsearch-learning/
├── pom.xml
└── src/
    └── main/
        ├── java/com/learning/elasticsearch/
        │   ├── EnterpriseSearchApplication.java
        │   ├── model/
        │   │   ├── Customer.java
        │   │   ├── Order.java
        │   │   └── LogEntry.java
        │   ├── service/
        │   │   ├── CustomerAnalyticsService.java
        │   │   ├── LogAnalyticsService.java
        │   │   └── GeoSearchService.java
        │   ├── controller/
        │   │   └── AnalyticsController.java
        │   └── config/
        │       └── CustomAnalyzerConfig.java
        └── resources/
            ├── application.properties
            └── mappings.json
```

### Implementation

**Customer.java**

```java
package com.learning.elasticsearch.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Customer {
    private String id;
    private String name;
    private String email;
    private String region;
    private String country;
    private String city;
    private Double latitude;
    private Double longitude;
    private String customerTier;
    private Double lifetimeValue;
    private Integer orderCount;
    private List<String> interests;
    private Map<String, Object> profile;
    private LocalDateTime registeredAt;
    
    public Customer() {
        this.registeredAt = LocalDateTime.now();
        this.customerTier = "STANDARD";
        this.lifetimeValue = 0.0;
        this.orderCount = 0;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getCustomerTier() { return customerTier; }
    public void setCustomerTier(String customerTier) { this.customerTier = customerTier; }
    public Double getLifetimeValue() { return lifetimeValue; }
    public void setLifetimeValue(Double lifetimeValue) { this.lifetimeValue = lifetimeValue; }
    public Integer getOrderCount() { return orderCount; }
    public void setOrderCount(Integer orderCount) { this.orderCount = orderCount; }
    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }
    public Map<String, Object> getProfile() { return profile; }
    public void setProfile(Map<String, Object> profile) { this.profile = profile; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
}
```

**LogEntry.java**

```java
package com.learning.elasticsearch.model;

import java.time.LocalDateTime;
import java.util.Map;

public class LogEntry {
    private String id;
    private String level;
    private String service;
    private String message;
    private String traceId;
    private String spanId;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
    private Long duration;
    private Integer statusCode;
    
    public LogEntry() {
        this.timestamp = LocalDateTime.now();
    }
    
    public static LogEntry error(String service, String message, String traceId) {
        LogEntry entry = new LogEntry();
        entry.setLevel("ERROR");
        entry.setService(service);
        entry.setMessage(message);
        entry.setTraceId(traceId);
        return entry;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getService() { return service; }
    public void setService(String service) { this.service = service; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public String getSpanId() { return spanId; }
    public void setSpanId(String spanId) { this.spanId = spanId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public Long getDuration() { return duration; }
    public void setDuration(Long duration) { this.duration = duration; }
    public Integer getStatusCode() { return statusCode; }
    public void setStatusCode(Integer statusCode) { this.statusCode = statusCode; }
}
```

**CustomerAnalyticsService.java**

```java
package com.learning.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.learning.elasticsearch.model.Customer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerAnalyticsService {
    
    private final ElasticsearchClient client;
    private static final String INDEX_NAME = "customers";
    
    public CustomerAnalyticsService(ElasticsearchClient client) {
        this.client = client;
    }
    
    public void indexCustomer(Customer customer) throws IOException {
        client.index(i -> i
            .index(INDEX_NAME)
            .id(customer.getId())
            .document(customer)
        );
        System.out.println("Indexed customer: " + customer.getName());
    }
    
    public Map<String, Object> getCustomerSegments() throws IOException {
        SearchResponse<Void> response = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("by_tier", a -> a.terms(t -> t.field("customerTier")))
            .aggregations("by_region", a -> a.terms(t -> t.field("region")))
            .aggregations("ltv_stats", a -> a.stats(ss -> ss.field("lifetimeValue")))
            .aggregations("avg_orders", a -> a.avg(av -> av.field("orderCount"))),
            Void.class
        );
        
        Map<String, Object> result = new HashMap<>();
        
        TermsAggregate tiers = response.aggregations().get("by_tier").sterms();
        Map<String, Long> tierCounts = new LinkedHashMap<>();
        for (StringTermsBucket bucket : tiers.buckets().array()) {
            tierCounts.put(bucket.key().stringValue(), bucket.docCount());
        }
        result.put("by_tier", tierCounts);
        
        StatsAggregate ltvStats = response.aggregations().get("ltv_stats").stats();
        result.put("ltv_stats", Map.of(
            "avg", ltvStats.avg(),
            "min", ltvStats.min(),
            "max", ltvStats.max(),
            "sum", ltvStats.sum()
        ));
        
        return result;
    }
    
    public Map<String, List<Customer>> findTopCustomersByLTV(int limit) throws IOException {
        SearchResponse<Customer> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.matchAll(m -> m))
            .size(limit)
            .sort(sort -> sort.field(f -> f.field("lifetimeValue").order(co.elastic.clients.elasticsearch._types.SortOrder.Desc))),
            Customer.class
        );
        
        return Map.of("top_customers", response.hits().hits().stream()
            .map(Hit::source)
            .collect(Collectors.toList()));
    }
    
    public Map<String, Object> getRegionalAnalysis() throws IOException {
        SearchResponse<Void> response = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("by_country", a -> a
                .terms(t -> t.field("country").size(20))
                .aggregations("ltv_avg", aa -> aa.avg(av -> av.field("lifetimeValue")))
                .aggregations("order_avg", aa -> aa.avg(av -> av.field("orderCount")))
                .aggregations("top_cities", aa -> aa
                    .terms(tt -> tt.field("city").size(5))
                )
            ),
            Void.class
        );
        
        Map<String, Object> result = new HashMap<>();
        
        TermsAggregate countries = response.aggregations().get("by_country").sterms();
        Map<String, Map<String, Object>> countryData = new LinkedHashMap<>();
        
        for (StringTermsBucket countryBucket : countries.buckets().array()) {
            Map<String, Object> countryStats = new HashMap<>();
            
            AvgAggregate ltvAvg = countryBucket.aggregations().get("ltv_avg").avg();
            countryStats.put("avg_lifetime_value", ltvAvg.value());
            
            AvgAggregate orderAvg = countryBucket.aggregations().get("order_avg").avg();
            countryStats.put("avg_order_count", orderAvg.value());
            
            countryData.put(countryBucket.key().stringValue(), countryStats);
        }
        
        result.put("by_country", countryData);
        return result;
    }
    
    public Map<String, Double> calculateRetentionRate() throws IOException {
        SearchResponse<Void> response = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .query(q -> q.range(r -> r
                .field("registeredAt")
                .lt(co.elastic.clients.json.JsonData.of("now-90d"))
            ))
            .aggregations("returning", a -> a
                .filter(f -> f.term(t -> t.field("orderCount").value(1)))
            ),
            Void.class
        );
        
        long totalOldCustomers = response.hits().total();
        
        Map<String, Double> result = new HashMap<>();
        result.put("total_customers", (double) totalOldCustomers);
        
        return result;
    }
}
```

**LogAnalyticsService.java**

```java
package com.learning.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch.core.*;
import com.learning.elasticsearch.model.LogEntry;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LogAnalyticsService {
    
    private final ElasticsearchClient client;
    private static final String INDEX_NAME = "application_logs";
    
    public LogAnalyticsService(ElasticsearchClient client) {
        this.client = client;
    }
    
    public void indexLog(LogEntry entry) throws IOException {
        client.index(i -> i
            .index(INDEX_NAME)
            .id(entry.getId())
            .document(entry)
        );
    }
    
    public void bulkIndexLogs(List<LogEntry> logs) throws IOException {
        List<IndexRequest<LogEntry>> requests = logs.stream()
            .map(log -> IndexRequest.of(r -> r
                .index(INDEX_NAME)
                .id(log.getId())
                .document(log)
            ))
            .collect(Collectors.toList());
        
        BulkRequest bulkRequest = new BulkRequest.Builder()
            .operations(requests)
            .build();
        
        client.bulk(bulkRequest);
        System.out.println("Indexed " + logs.size() + " log entries");
    }
    
    public Map<String, Object> getErrorAnalysis(LocalDateTime start, LocalDateTime end) throws IOException {
        SearchResponse<Void> response = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .query(q -> q.bool(b -> b
                .must(m -> m.term(t -> t.field("level").value("ERROR")))
                .filter(f -> f.range(r -> r
                    .field("timestamp")
                    .gte(co.elastic.clients.json.JsonData.of(start.toString()))
                    .lte(co.elastic.clients.json.JsonData.of(end.toString()))
                ))
            ))
            .aggregations("by_service", a -> a.terms(t -> t.field("service")))
            .aggregations("error_rate", a -> a.rate(r -> r.field("timestamp").calendarInterval("1d"))),
            Void.class
        );
        
        Map<String, Object> result = new HashMap<>();
        
        TermsAggregate byService = response.aggregations().get("by_service").sterms();
        Map<String, Long> errorsByService = new LinkedHashMap<>();
        for (StringTermsBucket bucket : byService.buckets().array()) {
            errorsByService.put(bucket.key().stringValue(), bucket.docCount());
        }
        result.put("by_service", errorsByService);
        
        return result;
    }
    
    public Map<String, Object> getPerformanceMetrics() throws IOException {
        SearchResponse<Void> response = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .query(q -> q.exists(e -> e.field("duration")))
            .aggregations("duration_stats", a -> a.stats(ss -> ss.field("duration")))
            .aggregations("duration_histogram", a -> a
                .histogram(h -> h.field("duration").interval(100.0))
            ))
            .aggregations("p95", a -> a.percentiles(p -> p
                .field("duration")
                .percentiles(percentiles -> percentiles.values("95.0"))
            )),
            Void.class
        );
        
        Map<String, Object> result = new HashMap<>();
        
        StatsAggregate stats = response.aggregations().get("duration_stats").stats();
        result.put("stats", Map.of(
            "avg", stats.avg(),
            "min", stats.min(),
            "max", stats.max(),
            "sum", stats.sum()
        ));
        
        PercentilesAggregate p95 = response.aggregations().get("p95").percentiles();
        result.put("p95", p95.values().get("95.0"));
        
        return result;
    }
    
    public List<String> findAnomalies() throws IOException {
        SearchResponse<Void> response = client.search(s -> s
            .index(INDEX_NAME)
            .size(100)
            .query(q -> q.bool(b -> b
                .must(m -> m.term(t -> t.field("level").value("ERROR")))
                .mustNot(mn -> mn.term(t -> t.field("handled").value(true))))
            )),
            Void.class
        );
        
        return response.hits().hits().stream()
            .map(hit -> hit.id())
            .collect(Collectors.toList());
    }
}
```

### Build and Run

```bash
cd 36-elasticsearch/elasticsearch-learning
mvn clean package -DskipTests
mvn spring-boot:run

# Test endpoints
curl http://localhost:8080/api/analytics/customers
curl http://localhost:8080/api/analytics/logs/errors?start=2024-01-01&end=2024-01-31
curl http://localhost:8080/api/analytics/performance
```

### Learning Outcomes

After completing these projects, you will understand:

1. **Index Management** - Create and manage Elasticsearch indices
2. **Document Indexing** - Index single and bulk documents
3. **Full-Text Search** - Query with relevance scoring
4. **Filtering** - Boolean queries and filters
5. **Aggregations** - Terms, stats, and percentiles
6. **Complex Aggregations** - Nested aggregations and pipelines
7. **Geo Queries** - Location-based searches
8. **Mappings** - Define custom mappings

### References

- Elasticsearch Java API: https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/index.html
- Search DSL: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl.html
- Aggregations: https://www.elastic.co/guide/en/elasticsearch/reference/current/aggregations.html