# Reactive Programming Projects - Module 14

This module covers reactive programming concepts, Project Reactor, reactive streams, and building non-blocking reactive applications.

## Mini-Project: Reactive REST API (2-4 hours)

### Overview
Build a reactive REST API using Spring WebFlux and Project Reactor with Flux/Mono, error handling, and reactive database access.

### Project Structure
```
reactive-demo/
├── pom.xml
├── src/main/java/com/learning/reactive/
│   ├── ReactiveDemoApplication.java
│   ├── controller/ProductController.java
│   ├── model/Product.java
│   ├── repository/ProductRepository.java
│   └── service/ProductService.java
└── src/main/resources/
    └── application.yml
```

### Implementation

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>reactive-demo</artifactId>
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
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
</project>
```

#### Product Model (Reactive)
```java
package com.learning.reactive.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Table("products")
public class Product {
    
    @Id
    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String category;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    
    public Product() {}
    
    public Product(String sku, String name, BigDecimal price, Integer stockQuantity) {
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.active = true;
        this.createdAt = Instant.now();
    }
    
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
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
```

#### Reactive Repository
```java
package com.learning.reactive.repository;

import com.learning.reactive.model.Product;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends R2dbcRepository<Product, Long> {
    
    Mono<Product> findBySku(String sku);
    
    Flux<Product> findByCategory(String category);
    
    Flux<Product> findByActiveTrue();
    
    Mono<Product> findByNameContaining(String name);
    
    Mono<Void> deleteBySku(String sku);
}
```

#### Reactive Service
```java
package com.learning.reactive.service;

import com.learning.reactive.model.Product;
import com.learning.reactive.repository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public Flux<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Mono<Product> getProductById(Long id) {
        return productRepository.findById(id)
            .switchIfEmpty(Mono.error(new ProductNotFoundException("Product not found: " + id)));
    }
    
    public Mono<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku)
            .switchIfEmpty(Mono.error(new ProductNotFoundException("Product not found: " + sku)));
    }
    
    public Flux<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    public Mono<Product> createProduct(Product product) {
        return productRepository.save(product);
    }
    
    public Mono<Product> updateProduct(Long id, Product product) {
        return productRepository.findById(id)
            .flatMap(existing -> {
                existing.setName(product.getName());
                existing.setDescription(product.getDescription());
                existing.setPrice(product.getPrice());
                existing.setStockQuantity(product.getStockQuantity());
                existing.setCategory(product.getCategory());
                existing.setActive(product.isActive());
                existing.setUpdatedAt(java.time.Instant.now());
                return productRepository.save(existing);
            });
    }
    
    public Mono<Void> deleteProduct(Long id) {
        return productRepository.findById(id)
            .flatMap(product -> productRepository.deleteById(id));
    }
    
    public Flux<Product> searchProducts(String query) {
        return productRepository.findByActiveTrue()
            .filter(p -> p.getName().toLowerCase().contains(query.toLowerCase()) ||
                        (p.getDescription() != null && 
                         p.getDescription().toLowerCase().contains(query.toLowerCase())));
    }
    
    public Mono<Product> updateStock(Long id, int quantity) {
        return productRepository.findById(id)
            .flatMap(product -> {
                int newStock = product.getStockQuantity() + quantity;
                if (newStock < 0) {
                    return Mono.error(new IllegalArgumentException("Insufficient stock"));
                }
                product.setStockQuantity(newStock);
                return productRepository.save(product);
            });
    }
    
    public Flux<Product> getProductsInPriceRange(BigDecimal min, BigDecimal max) {
        return productRepository.findByActiveTrue()
            .filter(p -> p.getPrice().compareTo(min) >= 0 && p.getPrice().compareTo(max) <= 0);
    }
    
    public Mono<Long> countProducts() {
        return productRepository.count();
    }
    
    static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(String message) {
            super(message);
        }
    }
}
```

#### Reactive Controller
```java
package com.learning.reactive.controller;

import com.learning.reactive.model.Product;
import com.learning.reactive.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping
    public Flux<Product> getAllProducts() {
        return productService.getAllProducts();
    }
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
            .map(ResponseEntity::ok)
            .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }
    
    @GetMapping("/sku/{sku}")
    public Mono<ResponseEntity<Product>> getProductBySku(@PathVariable String sku) {
        return productService.getProductBySku(sku)
            .map(ResponseEntity::ok)
            .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }
    
    @GetMapping("/category/{category}")
    public Flux<Product> getProductsByCategory(@PathVariable String category) {
        return productService.getProductsByCategory(category);
    }
    
    @GetMapping("/search")
    public Flux<Product> searchProducts(@RequestParam String q) {
        return productService.searchProducts(q);
    }
    
    @GetMapping("/price-range")
    public Flux<Product> getProductsInPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return productService.getProductsInPriceRange(min, max);
    }
    
    @GetMapping("/count")
    public Mono<Long> countProducts() {
        return productService.countProducts();
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
        return productService.updateProduct(id, product)
            .map(ResponseEntity::ok)
            .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }
    
    @PatchMapping("/{id}/stock")
    public Mono<ResponseEntity<Product>> updateStock(
            @PathVariable Long id,
            @RequestParam int quantity) {
        return productService.updateStock(id, quantity)
            .map(ResponseEntity::ok)
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }
    
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id)
            .then(Mono.just(ResponseEntity.noContent().build()))
            .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }
}
```

#### Application Properties
```yaml
server:
  port: 8080
  netty:
    connection-timeout: 60s

spring:
  r2dbc:
    url: r2dbc:h2:mem:///testdb
    username: sa
    password: 
    pool:
      enabled: true
      initial-size: 10
      max-size: 20

logging:
  level:
    reactor: DEBUG
    org.springframework.webflux: DEBUG
```

### Build and Run Instructions
```bash
# Build the project
cd reactive-demo
mvn clean package

# Run the application
java -jar target/reactive-demo-1.0.0.jar

# Test endpoints
curl http://localhost:8080/api/products
curl http://localhost:8080/api/products/1
curl "http://localhost:8080/api/products/search?q=laptop"
curl "http://localhost:8080/api/products/price-range?min=10&max=100"
```

---

## Real-World Project: Real-Time Data Processing System (8+ hours)

### Overview
Build a complete real-time data processing system using Project Reactor, Kafka, WebSocket, and reactive microservices with backpressure handling.

### Project Structure
```
realtime-processing/
├── pom.xml
├── src/main/java/com/learning/realtime/
│   ├── RealtimeProcessingApplication.java
│   ├── stream/
│   │   ├── controller/StreamController.java
│   │   ├── service/DataStreamService.java
│   │   └── processor/DataProcessor.java
│   ├── websocket/
│   │   ├── config/WebSocketConfig.java
│   │   ├── handler/RealTimeHandler.java
│   │   └── client/WebSocketClient.java
│   ├── kafka/
│   │   ├── config/KafkaConfig.java
│   │   ├── producer/DataProducer.java
│   │   └── consumer/DataConsumer.java
│   ├── service/
│   │   ├── AnalyticsService.java
│   │   └── AggregationService.java
│   └── model/
│       ├── SensorData.java
│       ├── MetricEvent.java
│       └── AggregatedData.java
└── docker-compose.yml
```

### Implementation

#### Main Application
```java
package com.learning.realtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories
public class RealtimeProcessingApplication {
    public static void main(String[] args) {
        SpringApplication.run(RealtimeProcessingApplication.class, args);
    }
}
```

#### Sensor Data Model
```java
package com.learning.realtime.model;

import java.time.Instant;

public class SensorData {
    
    private String sensorId;
    private String sensorType;
    private Double value;
    private String unit;
    private Instant timestamp;
    private String location;
    private Double temperature;
    private Double humidity;
    
    public SensorData() {
        this.timestamp = Instant.now();
    }
    
    public SensorData(String sensorId, String sensorType, Double value, String unit) {
        this.sensorId = sensorId;
        this.sensorType = sensorType;
        this.value = value;
        this.unit = unit;
        this.timestamp = Instant.now();
    }
    
    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public String getSensorType() { return sensorType; }
    public void setSensorType(String sensorType) { this.sensorType = sensorType; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public Double getHumidity() { return humidity; }
    public void setHumidity(Double humidity) { this.humidity = humidity; }
}
```

#### Metric Event
```java
package com.learning.realtime.model;

import java.time.Instant;

public class MetricEvent {
    
    private String eventId;
    private String eventType;
    private String source;
    private Object payload;
    private Instant timestamp;
    private Long processingTimeMs;
    
    public MetricEvent() {
        this.timestamp = Instant.now();
    }
    
    public MetricEvent(String eventType, String source, Object payload) {
        this.eventType = eventType;
        this.source = source;
        this.payload = payload;
        this.timestamp = Instant.now();
    }
    
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Object getPayload() { return payload; }
    public void setPayload(Object payload) { this.payload = payload; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public Long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(Long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
}
```

#### Aggregated Data
```java
package com.learning.realtime.model;

import java.time.Instant;

public class AggregatedData {
    
    private String metricName;
    private String groupBy;
    private Double min;
    private Double max;
    private Double avg;
    private Double sum;
    private Long count;
    private Instant windowStart;
    private Instant windowEnd;
    
    public AggregatedData() {}
    
    public AggregatedData(String metricName, String groupBy) {
        this.metricName = metricName;
        this.groupBy = groupBy;
    }
    
    public String getMetricName() { return metricName; }
    public void setMetricName(String metricName) { this.metricName = metricName; }
    public String getGroupBy() { return groupBy; }
    public void setGroupBy(String groupBy) { this.groupBy = groupBy; }
    public Double getMin() { return min; }
    public void setMin(Double min) { this.min = min; }
    public Double getMax() { return max; }
    public void setMax(Double max) { this.max = max; }
    public Double getAvg() { return avg; }
    public void setAvg(Double avg) { this.avg = avg; }
    public Double getSum() { return sum; }
    public void setSum(Double sum) { this.sum = sum; }
    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }
    public Instant getWindowStart() { return windowStart; }
    public void setWindowStart(Instant windowStart) { this.windowStart = windowStart; }
    public Instant getWindowEnd() { return windowEnd; }
    public void setWindowEnd(Instant windowEnd) { this.windowEnd = windowEnd; }
}
```

#### Data Stream Service
```java
package com.learning.realtime.stream.service;

import com.learning.realtime.model.SensorData;
import com.learning.realtime.model.MetricEvent;
import com.learning.realtime.model.AggregatedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.*;
import reactor.core.scheduler.Schedulers;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class DataStreamService {
    
    private static final Logger logger = LoggerFactory.getLogger(DataStreamService.class);
    
    private final Flux<SensorData> sensorDataStream;
    private final FluxProcessor<MetricEvent, MetricEvent> eventProcessor;
    private final Map<String, Deque<SensorData>> recentData = new ConcurrentHashMap<>();
    
    public DataStreamService() {
        this.sensorDataStream = Flux.interval(Duration.ofMillis(100))
            .map(this::generateSensorData)
            .share();
        
        this.eventProcessor = DirectProcessor.<MetricEvent>builder()
            .onOverflowBuffer(1000)
            .build();
    }
    
    public Flux<SensorData> getSensorDataStream() {
        return sensorDataStream;
    }
    
    public Flux<SensorData> getSensorDataByType(String sensorType) {
        return sensorDataStream
            .filter(data -> data.getSensorType().equals(sensorType));
    }
    
    public Flux<SensorData> getSensorDataByLocation(String location) {
        return sensorDataStream
            .filter(data -> location.equals(data.getLocation()));
    }
    
    public Flux<MetricEvent> getEventStream() {
        return eventProcessor;
    }
    
    public Mono<Long> processSensorData(SensorData data) {
        return Mono.fromCallable(() -> {
            recentData.computeIfAbsent(data.getSensorId(), k -> new ArrayDeque<>(100));
            Deque<SensorData> deque = recentData.get(data.getSensorId());
            deque.addLast(data);
            if (deque.size() > 100) {
                deque.removeFirst();
            }
            return 1L;
        }).subscribeOn(Schedulers.boundedElastic());
    }
    
    public Flux<AggregatedData> aggregateByTimeWindow(Duration window) {
        return sensorDataStream
            .window(window)
            .flatMap(windowFlux -> windowFlux
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> aggregateData(list))
            );
    }
    
    public Flux<AggregatedData> aggregateBySensor() {
        return sensorDataStream
            .groupBy(SensorData::getSensorId)
            .flatMap(groupedFlux -> groupedFlux
                .window(Duration.ofSeconds(10))
                .flatMap(windowFlux -> windowFlux
                    .collectList()
                    .map(this::aggregateData)
                )
            );
    }
    
    public Mono<Map<String, Double>> getCurrentStats() {
        return sensorDataStream
            .take(Duration.ofSeconds(1))
            .collectList()
            .map(list -> Map.of(
                "count", (double) list.size(),
                "avgValue", list.stream()
                    .mapToDouble(SensorData::getValue)
                    .average()
                    .orElse(0.0),
                "maxValue", list.stream()
                    .mapToDouble(SensorData::getValue)
                    .max()
                    .orElse(0.0)
            ));
    }
    
    public Flux<SensorData> getLatestBySensor(int count) {
        return sensorDataStream
            .scan(new HashMap<String, SensorData>(), (map, data) -> {
                map.put(data.getSensorId(), data);
                return map;
            })
            .flatMap(Flux::fromIterable)
            .distinct(SensorData::getSensorId)
            .take(count);
    }
    
    private AggregatedData aggregateData(List<SensorData> data) {
        if (data.isEmpty()) {
            return new AggregatedData();
        }
        
        DoubleSummaryStatistics stats = data.stream()
            .mapToDouble(SensorData::getValue)
            .summaryStatistics();
        
        AggregatedData result = new AggregatedData(
            data.get(0).getSensorType(),
            data.get(0).getLocation() != null ? data.get(0).getLocation() : "all"
        );
        
        result.setMin(stats.getMin());
        result.setMax(stats.getMax());
        result.setAvg(stats.getAverage());
        result.setSum(stats.getSum());
        result.setCount(stats.getCount());
        result.setWindowStart(data.get(0).getTimestamp());
        result.setWindowEnd(data.get(data.size() - 1).getTimestamp());
        
        return result;
    }
    
    private SensorData generateSensorData(long index) {
        Random random = new Random();
        String[] types = {"temperature", "pressure", "humidity", "vibration"};
        String[] locations = {"warehouse-1", "warehouse-2", "factory-floor", "storage"};
        
        SensorData data = new SensorData();
        data.setSensorId("sensor-" + (index % 20));
        data.setSensorType(types[random.nextInt(types.length)]);
        data.setValue(50.0 + random.nextDouble() * 50);
        data.setUnit("units");
        data.setLocation(locations[random.nextInt(locations.length)]);
        data.setTimestamp(Instant.now());
        
        return data;
    }
    
    public void emitEvent(MetricEvent event) {
        eventProcessor.onNext(event);
    }
}
```

#### Data Processor
```java
package com.learning.realtime.stream.processor;

import com.learning.realtime.model.SensorData;
import com.learning.realtime.model.MetricEvent;
import com.learning.realtime.stream.service.DataStreamService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
public class DataProcessor {
    
    private final DataStreamService dataStreamService;
    
    public DataProcessor(DataStreamService dataStreamService) {
        this.dataStreamService = dataStreamService;
    }
    
    public Flux<SensorData> processWithFilter() {
        return dataStreamService.getSensorDataStream()
            .filter(data -> data.getValue() > 50.0)
            .map(this::enrichData);
    }
    
    public Flux<SensorData> processWithTransformation() {
        return dataStreamService.getSensorDataStream()
            .map(this::transformData)
            .flatMap(this::validateAndProcess);
    }
    
    public Flux<MetricEvent> processWithThrottling() {
        return dataStreamService.getSensorDataStream()
            .buffer(Duration.ofSeconds(1))
            .filter(list -> !list.isEmpty())
            .map(this::createBatchEvent);
    }
    
    public Flux<SensorData> processWithBackpressure(int bufferSize) {
        return dataStreamService.getSensorDataStream()
            .onBackpressureBuffer(bufferSize, dropped -> 
                System.out.println("Dropped: " + dropped.getSensorId()))
            .map(this::processExpensiveOperation);
    }
    
    public Flux<SensorData> processWithErrorHandling() {
        return dataStreamService.getSensorDataStream()
            .doOnNext(data -> {
                if (data.getValue() < 0) {
                    throw new IllegalArgumentException("Invalid value");
                }
            })
            .onErrorContinue(IllegalArgumentException.class, (error, data) -> 
                System.out.println("Skipped invalid data: " + error.getMessage()))
            .map(this::enrichData);
    }
    
    public Flux<SensorData> processWithRetry() {
        return dataStreamService.getSensorDataStream()
            .map(this::processExpensiveOperation)
            .retry(3)
            .onErrorResume(e -> Flux.empty());
    }
    
    public Flux<SensorData> processWithCircuitBreaker() {
        return dataStreamService.getSensorDataStream()
            .transform(transform -> Flux.defer(() -> {
                if (Math.random() > 0.9) {
                    return Flux.error(new RuntimeException("Simulated failure"));
                }
                return transform;
            }))
            .doOnError(e -> System.out.println("Circuit breaker triggered"))
            .retryWhen(reactor.util.retry.Retry.backoff(3, Duration.ofMillis(100)));
    }
    
    private SensorData enrichData(SensorData data) {
        data.setTemperature(data.getValue() * 0.8);
        data.setHumidity(data.getValue() * 0.5);
        return data;
    }
    
    private Mono<SensorData> validateAndProcess(SensorData data) {
        if (data.getValue() == null || data.getValue() < 0) {
            return Mono.error(new IllegalArgumentException("Invalid sensor data"));
        }
        return Mono.just(enrichData(data));
    }
    
    private SensorData transformData(SensorData data) {
        if ("temperature".equals(data.getSensorType())) {
            data.setValue((data.getValue() * 9/5) + 32);
            data.setUnit("F");
        }
        return data;
    }
    
    private MetricEvent createBatchEvent(java.util.List<SensorData> batch) {
        return new MetricEvent(
            "BATCH_PROCESSED",
            "data-processor",
            Map.of(
                "batchSize", batch.size(),
                "avgValue", batch.stream()
                    .mapToDouble(SensorData::getValue)
                    .average()
                    .orElse(0.0)
            )
        );
    }
    
    private SensorData processExpensiveOperation(SensorData data) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return data;
    }
}
```

#### WebSocket Configuration
```java
package com.learning.realtime.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.handler.BeanWebSocketHandler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.TextMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.realtime.model.SensorData;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    private final Sinks.Many<SensorData> sensorDataSink = Sinks.many().multicast().directBestEffort();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void addWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(sensorDataHandler(), "/ws/sensors")
            .setAllowedOrigins("*");
        registry.addHandler(aggregatedDataHandler(), "/ws/aggregated")
            .setAllowedOrigins("*");
    }
    
    @Bean
    public WebSocketHandler sensorDataHandler() {
        return session -> {
            Flux<String> sensorFlux = sensorDataSink.asFlux()
                .map(data -> {
                    try {
                        return objectMapper.writeValueAsString(data);
                    } catch (Exception e) {
                        return "{}";
                    }
                });
            
            return session.send(sensorFlux.map(session::textMessage));
        };
    }
    
    @Bean
    public WebSocketHandler aggregatedDataHandler() {
        return session -> {
            Flux<String> aggregatedFlux = Flux.interval(java.time.Duration.ofSeconds(2))
                .map(i -> "{\"timestamp\":" + System.currentTimeMillis() + ",\"data\":{\"avg\":75.5,\"max\":95.0,\"min\":55.0}}");
            
            return session.send(aggregatedFlux.map(session::textMessage));
        };
    }
    
    @Bean
    public Sinks.Many<SensorData> sensorDataSink() {
        return Sinks.many().multicast().directBestEffort();
    }
    
    @Bean
    public SimpleUrlHandlerMapping urlHandlerMapping() {
        Map<String, WebSocketHandler> handlers = new HashMap<>();
        handlers.put("/ws/sensors", new BeanWebSocketHandler(sensorDataHandler()));
        
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(handlers);
        return mapping;
    }
}
```

#### WebSocket Handler
```java
package com.learning.realtime.websocket.handler;

import com.learning.realtime.model.SensorData;
import com.learning.realtime.stream.service.DataStreamService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RealTimeHandler extends TextWebSocketHandler {
    
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final DataStreamService dataStreamService;
    
    public RealTimeHandler(DataStreamService dataStreamService) {
        this.dataStreamService = dataStreamService;
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        System.out.println("WebSocket connected: " + session.getId());
        
        session.sendMessage(new TextMessage("Connected to real-time data stream"));
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println("Received: " + message.getPayload());
        
        String payload = message.getPayload();
        if (payload.startsWith("subscribe:")) {
            String topic = payload.substring(10);
            handleSubscription(session, topic);
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        System.out.println("WebSocket disconnected: " + session.getId());
    }
    
    private void handleSubscription(WebSocketSession session, String topic) {
        if ("temperature".equals(topic)) {
            Flux<SensorData> dataStream = dataStreamService.getSensorDataByType("temperature");
            
            dataStream.subscribe(data -> {
                try {
                    session.sendMessage(new TextMessage(
                        "{\"type\":\"temperature\",\"data\":" + 
                        new com.fasterxml.jackson.databind.ObjectMapper()
                            .writeValueAsString(data) + "}"
                    ));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
    
    public void broadcastToAll(SensorData data) {
        sessions.values().forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(
                        new com.fasterxml.jackson.databind.ObjectMapper()
                            .writeValueAsString(data)
                    ));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
```

#### Kafka Configuration
```java
package com.learning.realtime.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.reactive.config.EnableKafkaReactive;

@Configuration
@EnableKafkaReactive
public class KafkaConfig {
    
    @Bean
    public NewTopic sensorDataTopic() {
        return TopicBuilder.name("sensor-data")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    @Bean
    public NewTopic metricEventsTopic() {
        return TopicBuilder.name("metric-events")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    @Bean
    public NewTopic aggregatedDataTopic() {
        return TopicBuilder.name("aggregated-data")
            .partitions(3)
            .replicas(1)
            .build();
    }
}
```

#### Kafka Producer
```java
package com.learning.realtime.kafka.producer;

import com.learning.realtime.model.SensorData;
import com.learning.realtime.model.MetricEvent;
import org.springframework.stereotype.Component;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import java.time.Duration;

@Component
public class DataProducer {
    
    private final KafkaSender<String, String> sender;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    
    public DataProducer(KafkaSender<String, String> sender) {
        this.sender = sender;
        this.objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
    }
    
    public void sendSensorData(SensorData data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            
            SenderRecord<String, String, String> record = SenderRecord.create(
                "sensor-data",
                data.getSensorId(),
                null,
                System.currentTimeMillis(),
                data.getSensorId(),
                json
            );
            
            sender.send(Mono.just(record))
                .subscribe();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void sendMetricEvent(MetricEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            
            SenderRecord<String, String, String> record = SenderRecord.create(
                "metric-events",
                event.getEventType(),
                null,
                System.currentTimeMillis(),
                event.getEventType(),
                json
            );
            
            sender.send(Mono.just(record))
                .doOnSuccess(s -> System.out.println("Sent event: " + event.getEventType()))
                .subscribe();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

#### Kafka Consumer
```java
package com.learning.realtime.kafka.consumer;

import com.learning.realtime.model.SensorData;
import com.learning.realtime.model.MetricEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import java.util.List;

@Component
public class DataConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(DataConsumer.class);
    
    private final Sinks.Many<SensorData> sensorDataSink;
    private final Sinks.Many<MetricEvent> metricEventSink;
    private final ObjectMapper objectMapper;
    
    public DataConsumer() {
        this.sensorDataSink = Sinks.many().multicast().directBestEffort();
        this.metricEventSink = Sinks.many().multicast().directBestEffort();
        this.objectMapper = new ObjectMapper();
    }
    
    @KafkaListener(topics = "sensor-data", groupId = "realtime-processor")
    public void consumeSensorData(String message) {
        try {
            SensorData data = objectMapper.readValue(message, SensorData.class);
            sensorDataSink.emitNext(data, Sinks.EmitFailureHandler.FAIL_FAST);
            logger.debug("Consumed sensor data: {}", data.getSensorId());
        } catch (Exception e) {
            logger.error("Failed to process sensor data", e);
        }
    }
    
    @KafkaListener(topics = "metric-events", groupId = "realtime-processor")
    public void consumeMetricEvent(String message) {
        try {
            MetricEvent event = objectMapper.readValue(message, MetricEvent.class);
            metricEventSink.emitNext(event, Sinks.EmitFailureHandler.FAIL_FAST);
            logger.debug("Consumed metric event: {}", event.getEventType());
        } catch (Exception e) {
            logger.error("Failed to process metric event", e);
        }
    }
    
    public Flux<SensorData> getSensorDataStream() {
        return sensorDataSink.asFlux();
    }
    
    public Flux<MetricEvent> getMetricEventStream() {
        return metricEventSink.asFlux();
    }
}
```

#### Analytics Service
```java
package com.learning.realtime.service;

import com.learning.realtime.model.SensorData;
import com.learning.realtime.model.AggregatedData;
import com.learning.realtime.stream.service.DataStreamService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.DoubleSummaryStatistics;

@Service
public class AnalyticsService {
    
    private final DataStreamService dataStreamService;
    
    public AnalyticsService(DataStreamService dataStreamService) {
        this.dataStreamService = dataStreamService;
    }
    
    public Flux<AggregatedData> computeRollingAverage(Duration window) {
        return dataStreamService.getSensorDataStream()
            .window(window)
            .flatMap(windowFlux -> windowFlux
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(this::computeAggregates)
            );
    }
    
    public Mono<Double> computeMovingAverage(int count) {
        return dataStreamService.getSensorDataStream()
            .take(count)
            .map(SensorData::getValue)
            .reduce(0.0, Double::sum)
            .map(sum -> sum / count);
    }
    
    public Flux<AggregatedData> computeTrend() {
        return dataStreamService.getSensorDataStream()
            .buffer(Duration.ofSeconds(5))
            .map(this::computeTrendAggregates);
    }
    
    public Mono<Long> countAnomalies(double threshold) {
        return dataStreamService.getSensorDataStream()
            .filter(data -> data.getValue() > threshold)
            .count();
    }
    
    public Flux<SensorData> detectSpikes(double spikeThreshold) {
        return dataStreamService.getSensorDataStream()
            .scan(new double[]{0, 0}, (previous, current) -> {
                double prevValue = previous[1];
                double currentValue = current.getValue();
                return new double[]{prevValue, currentValue};
            })
            .skip(1)
            .filter(previous -> {
                double currentValue = previous[1];
                double prevValue = previous[0];
                return Math.abs(currentValue - prevValue) > spikeThreshold;
            })
            .map(previous -> {
                SensorData data = new SensorData();
                data.setValue(previous[1]);
                return data;
            });
    }
    
    private AggregatedData computeAggregates(java.util.List<SensorData> data) {
        DoubleSummaryStatistics stats = data.stream()
            .mapToDouble(SensorData::getValue)
            .summaryStatistics();
        
        AggregatedData result = new AggregatedData("rolling-average", data.get(0).getSensorType());
        result.setMin(stats.getMin());
        result.setMax(stats.getMax());
        result.setAvg(stats.getAverage());
        result.setSum(stats.getSum());
        result.setCount(stats.getCount());
        
        return result;
    }
    
    private AggregatedData computeTrendAggregates(java.util.List<SensorData> data) {
        if (data.isEmpty()) {
            return new AggregatedData("trend", "unknown");
        }
        
        DoubleSummaryStatistics stats = data.stream()
            .mapToDouble(SensorData::getValue)
            .summaryStatistics();
        
        double firstHalfAvg = data.subList(0, data.size() / 2).stream()
            .mapToDouble(SensorData::getValue)
            .average()
            .orElse(0.0);
        
        double secondHalfAvg = data.subList(data.size() / 2, data.size()).stream()
            .mapToDouble(SensorData::getValue)
            .average()
            .orElse(0.0);
        
        AggregatedData result = new AggregatedData("trend", data.get(0).getSensorType());
        result.setAvg(stats.getAverage());
        result.setCount((long) data.size());
        result.setSum(stats.getSum());
        
        result.setMin(firstHalfAvg);
        result.setMax(secondHalfAvg);
        
        return result;
    }
}
```

#### docker-compose.yml
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
      - "8081:8081"
    environment:
      - SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    ports:
      - "2181:2181"
```

### Build and Run Instructions
```bash
# Build the project
cd realtime-processing
mvn clean package

# Run with Docker
docker-compose up -d

# Run the application
java -jar target/realtime-processing-1.0.0.jar

# Test WebSocket
# Connect to ws://localhost:8080/ws/sensors

# Test REST endpoints
curl http://localhost:8080/api/stream
curl http://localhost:8080/api/analytics/stats

# Test reactive stream
curl http://localhost:8080/api/processor/filter
```

### Learning Outcomes
- Build reactive REST APIs with Spring WebFlux
- Implement Flux and Mono patterns
- Configure backpressure handling
- Set up WebSocket communication
- Integrate with Kafka for event streaming
- Implement real-time data processing
- Create aggregations and analytics