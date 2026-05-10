# Observability Projects

This directory contains hands-on projects for application observability including logging, metrics, and distributed tracing. These are essential for monitoring and debugging production applications.

## Project Overview

Observability in Java applications involves three pillars: structured logging, application metrics, and distributed tracing. This module covers hands-on projects using Micrometer, OpenTelemetry, and related tools.

---

# Mini-Project: Application Metrics (2-4 Hours)

## Project Description

Build a metrics collection system using Micrometer with Prometheus export. This project demonstrates counter, gauge, timer, and histogram metrics with Spring Boot Actuator.

## Technologies Used

- Micrometer 1.12.x
- Spring Boot Actuator
- Prometheus
- Java 21

## Implementation Steps

### Step 1: Create Project Structure

```bash
mkdir observability-metrics
cd observability-metrics
mkdir -p src/main/java/com/learning/metrics/{config,service,resource}
mkdir -p src/main/resources
```

### Step 2: Create pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>observability-metrics</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <name>Observability Metrics</name>
    <description>Application metrics with Micrometer</description>
    
    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <spring-boot.version>3.2.0</spring-boot.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
            <version>1.12.0</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 3: Create Metrics Service

```java
package com.learning.metrics.service;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.binder.jvm.*;
import io.micrometer.core.instrument.binder.system.*;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import java.util.function.DoubleSupplier;

@Service
public class MetricsService {
    
    private final MeterRegistry registry;
    private final Counter requestCounter;
    private final Counter errorCounter;
    private final Timer requestTimer;
    private final DistributionSummary responseSizeDistribution;
    
    public MetricsService(MeterRegistry registry) {
        this.registry = registry;
        
        this.requestCounter = Counter.builder("http.requests.total")
            .tag("application", "observability-metrics")
            .description("Total number of HTTP requests")
            .register(registry);
        
        this.errorCounter = Counter.builder("http.errors.total")
            .tag("application", "observability-metrics")
            .description("Total number of HTTP errors")
            .register(registry);
        
        this.requestTimer = Timer.builder("http.request.duration")
            .description("HTTP request duration")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(registry);
        
        this.responseSizeDistribution = DistributionSummary.builder("http.response.size")
            .description("HTTP response size in bytes")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(registry);
        
        registerDefaultMetrics();
    }
    
    private void registerDefaultMetrics() {
        JvmGcMetrics gcMetrics = new JvmGcMetrics();
        gcMetrics.bindTo(registry);
        
        JvmThreadMetrics threadMetrics = new JvmThreadMetrics();
        threadMetrics.bindTo(registry);
        
        JvmClassMetrics classMetrics = new JvmClassMetrics();
        classMetrics.bindTo(registry);
        
        JvmMemoryMetrics memoryMetrics = new JvmMemoryMetrics();
        memoryMetrics.bindTo(registry);
        
        ProcessorMetrics processorMetrics = new ProcessorMetrics();
        processorMetrics.bindTo(registry);
    }
    
    public void recordRequest(String endpoint, int statusCode, long durationNanos, long responseBytes) {
        requestCounter.increment();
        
        Timer.Sample sample = Timer.start(registry);
        
        registry.timer("http.server.requests")
            .tag("uri", endpoint)
            .tag("status", String.valueOf(statusCode))
            .record(durationNanos, TimeUnit.NANOSECONDS);
        
        responseSizeDistribution.record(responseBytes);
    }
    
    public void recordError(String endpoint, int statusCode) {
        errorCounter.increment();
        
        Counter.builder("http.server.errors")
            .tag("uri", endpoint)
            .tag("status", String.valueOf(statusCode))
            .register(registry)
            .increment();
    }
    
    public void recordBusinessOperation(String operation, long durationMillis) {
        Timer timer = registry.timer("business.operation.duration",
            "operation", operation);
        
        timer.record(durationMillis, TimeUnit.MILLISECONDS);
        
        Counter counter = registry.counter("business.operations.total",
            "operation", operation);
        counter.increment();
    }
    
    public void recordQueueSize(String queueName, int size) {
        Gauge.builder("queue.size", () -> (double) size)
            .tag("queue", queueName)
            .register(registry);
    }
    
    public void recordCacheHit(String cacheName) {
        Counter.builder("cache.hits")
            .tag("cache", cacheName)
            .register(registry)
            .increment();
    }
    
    public void recordCacheMiss(String cacheName) {
        Counter.builder("cache.misses")
            .tag("cache", cacheName)
            .register(registry)
            .increment();
    }
    
    public void recordDatabaseQuery(String query, long durationMillis) {
        Timer timer = registry.timer("database.query.duration",
            "query", query);
        
        timer.record(durationMillis, TimeUnit.MILLISECONDS);
    }
    
    public Timer.Sample startTimer() {
        return Timer.start(registry);
    }
    
    public void stopTimer(Timer.Sample sample, String metricName) {
        sample.stop(registry.timer(metricName));
    }
    
    public Counter getCounter(String name) {
        return registry.counter(name);
    }
    
    public Timer getTimer(String name) {
        return registry.timer(name);
    }
}
```

### Step 4: Create REST Controller

```java
package com.learning.metrics.resource;

import com.learning.metrics.service.MetricsService;
import io.micrometer.core.instrument.Timer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MetricsResource {
    
    private final MetricsService metricsService;
    
    public MetricsResource(MetricsService metricsService) {
        this.metricsService = metricsService;
    }
    
    @GetMapping("/hello")
    public ResponseEntity<Map<String, String>> hello() {
        Timer.Sample sample = metricsService.startTimer();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello, World!");
        
        sample.stop(Timer.builder("test.timer").register(
            metricsService.getTimer("test.timer").getRegistry()));
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/business-operation")
    public ResponseEntity<Map<String, Object>> businessOperation(
            @RequestParam String operation,
            @RequestParam(defaultValue = "100") long durationMs) {
        
        metricsService.recordBusinessOperation(operation, durationMs);
        
        Map<String, Object> response = new HashMap<>();
        response.put("operation", operation);
        response.put("durationMs", durationMs);
        response.put("status", "recorded");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/cache/{cacheName}")
    public ResponseEntity<Map<String, Object>> cacheOperation(
            @PathVariable String cacheName,
            @RequestParam String action) {
        
        if ("hit".equals(action)) {
            metricsService.recordCacheHit(cacheName);
        } else if ("miss".equals(action)) {
            metricsService.recordCacheMiss(cacheName);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("cache", cacheName);
        response.put("action", action);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/database-query")
    public ResponseEntity<Map<String, Object>> databaseQuery(
            @RequestParam String query,
            @RequestParam(defaultValue = "50") long durationMs) {
        
        metricsService.recordDatabaseQuery(query, durationMs);
        
        Map<String, Object> response = new HashMap<>();
        response.put("query", query);
        response.put("durationMs", durationMs);
        
        return ResponseEntity.ok(response);
    }
}
```

### Step 5: Configure Application

Create `src/main/resources/application.yaml`:

```yaml
server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,loggers
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: observability-metrics

logging:
  level:
    root: INFO
    com.learning: DEBUG
```

### Step 6: Run and Test

```bash
# Build project
cd observability-metrics
mvn clean package

# Run application
java -jar target/observability-metrics-1.0.0.jar

# Test endpoints
curl http://localhost:8080/api/hello

# Test business operations
curl -X POST "http://localhost:8080/api/business-operation?operation=user-login&durationMs=150"

# Test cache operations
curl -X POST "http://localhost:8080/api/cache/user-cache?action=hit"
curl -X POST "http://localhost:8080/api/cache/user-cache?action=miss"

# Test database queries
curl -X POST "http://localhost:8080/api/database-query?query=user-find-by-id&durationMs=25"

# Check metrics
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/prometheus
```

## Expected Output

- HTTP request metrics
- Business operation timers
- Cache hit/miss counters
- Database query metrics
- JVM metrics

---

# Real-World Project: Distributed Tracing (8+ Hours)

## Project Description

Build a complete distributed tracing system using OpenTelemetry with multiple services, span propagation, and Jaeger visualization. This project demonstrates end-to-end tracing across microservice boundaries.

## Architecture

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  Gateway     │────►│  Service A  │────►│  Service B  │
│   (Zipkin)   │     │ Trace       │     │ Trace       │
└──────────────┘     └──────────────┘     └──────────────┘
                           │                   │
                           └─────────┬─────────┘
                                     │
                              ┌──────▼──────┐
                              │   Jaeger    │
                              │  Storage   │
                              └────────────┘
```

## Implementation Steps

### Step 1: Create Tracing Project

```bash
mkdir observability-tracing
cd observability-tracing
mkdir -p src/main/java/com/learning/tracing/{config,service,interceptor,model}
mkdir -p src/main/resources
```

### Step 2: Create pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>observability-tracing</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <name>Distributed Tracing</name>
    <description>Distributed tracing with OpenTelemetry</description>
    
    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <spring-boot.version>3.2.0</spring-boot.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>io.opentelemetry</groupId>
            <artifactId>opentelemetry-exporter-zipkin</artifactId>
            <version>1.32.0</version>
        </dependency>
        <dependency>
            <groupId>io.opentelemetry</groupId>
            <artifactId>opentelemetry-sdk</artifactId>
            <version>1.32.0</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 3: Create Tracing Configuration

```java
package com.learning.tracing.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.Span;
import zipkin2.reporter.BytesEncoder;
import zipkin2.reporter.okhttp3.OkHttpSender;

@Configuration
public class TracingConfig {
    
    @Value("${tracing.service-name:tracing-service}")
    private String serviceName;
    
    @Value("${tracing.zipkin-url:http://localhost:9411}")
    private String zipkinUrl;
    
    @Bean
    public OpenTelemetry openTelemetry() {
        Resource resource = Resource.getDefault()
            .merge(Resource.create(
                io.opentelemetry.api.common.Attributes.of(
                    io.opentelemetry.api.common.AttributeKey.stringKey("service.name"),
                    serviceName
                )
            ));
        
        ZipkinSpanExporter zipkinExporter = ZipkinSpanExporter.builder()
            .setEncoder(BytesEncoder.V2_JSON)
            .setSender(new OkHttpSender(zipkinUrl + "/api/v2/spans"))
            .build();
        
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
            .setResource(resource)
            .addSpanProcessor(BatchSpanProcessor.builder(zipkinExporter)
                .setExportDelayMillis(1000)
                .setMaxQueueSize(2048)
                .setMaxExportBatchSize(512)
                .build())
            .addSpanProcessor(SimpleSpanProcessor.create(
                io.opentelemetry.sdk.trace.export.LoggingSpanExporter.create()))
            .setSampler(Sampler.alwaysOn())
            .build();
        
        return OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider)
            .setPropagators(ContextPropagators.create(
                W3CTraceContextPropagator.getInstance()))
            .build();
    }
    
    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer(serviceName);
    }
}
```

### Step 4: Create Service with Tracing

```java
package com.learning.tracing.service;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class OrderTracingService {
    
    private final Tracer tracer;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final ShippingService shippingService;
    private final Random random;
    
    public OrderTracingService(Tracer tracer,
                           ProductService productService,
                           PaymentService paymentService,
                           ShippingService shippingService) {
        this.tracer = tracer;
        this.productService = productService;
        this.paymentService = paymentService;
        this.shippingService = shippingService;
        this.random = new Random();
    }
    
    public OrderResult processOrder(OrderRequest request) {
        Span orderSpan = tracer.spanBuilder("processOrder")
            .setAttribute("order.id", request.orderId())
            .setAttribute("customer.id", request.customerId())
            .startSpan();
        
        try (Scope scope = orderSpan.makeCurrent()) {
            orderSpan.addEvent("Order processing started");
            
            OrderResult result = new OrderResult();
            
            result.setOrderId(request.orderId());
            result.setStatus("PROCESSING");
            
            Span productSpan = tracer.spanBuilder("validateProducts")
                .startSpan();
            
            try (Scope productScope = productSpan.makeCurrent()) {
                productService.validateProducts(request.items());
                productSpan.setAttribute("item.count", request.items().size());
                productSpan.setStatus(StatusCode.OK);
            } finally {
                productSpan.end();
            }
            
            Span paymentSpan = tracer.spanBuilder("processPayment")
                .startSpan();
            
            try (Scope paymentScope = paymentSpan.makeCurrent()) {
                paymentService.chargePayment(
                    request.customerId(),
                    request.amount()
                );
                paymentSpan.setAttribute("amount", request.amount());
                paymentSpan.setStatus(StatusCode.OK);
            } finally {
                paymentSpan.end();
            }
            
            int processingTime = random.nextInt(500);
            try {
                Thread.sleep(processingTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            Span shippingSpan = tracer.spanBuilder("arrangeShipping")
                .startSpan();
            
            try (Scope shippingScope = shippingSpan.makeCurrent()) {
                String trackingNumber = shippingService.arrangeShipping(
                    request.customerId(),
                    request.shippingAddress()
                );
                result.setTrackingNumber(trackingNumber);
                shippingSpan.setStatus(StatusCode.OK);
            } finally {
                shippingSpan.end();
            }
            
            result.setStatus("COMPLETED");
            result.setMessage("Order processed successfully");
            
            orderSpan.addEvent("Order processing completed");
            orderSpan.setStatus(StatusCode.OK);
            
            return result;
            
        } catch (Exception e) {
            orderSpan.setStatus(StatusCode.ERROR, e.getMessage());
            orderSpan.recordException(e);
            throw e;
        } finally {
            orderSpan.end();
        }
    }
    
    public record OrderRequest(
        String orderId,
        String customerId,
        double amount,
        String shippingAddress,
        java.util.List<String> items
    ) {}
    
    public class OrderResult {
        private String orderId;
        private String status;
        private String trackingNumber;
        private String message;
        
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getTrackingNumber() { return trackingNumber; }
        public void setTrackingNumber(String trackingNumber) { 
            this.trackingNumber = trackingNumber; 
        }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
```

```java
package com.learning.tracing.service;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
public class ProductService {
    
    private final Tracer tracer;
    private final Random random;
    private int inventoryCheckDelay;
    
    public ProductService(Tracer tracer) {
        this.tracer = tracer;
        this.random = new Random();
        this.inventoryCheckDelay = 50;
    }
    
    public void validateProducts(List<String> itemIds) {
        Span span = tracer.spanBuilder("validateProducts")
            .setAttribute("operation", "inventory.check")
            .startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            for (String itemId : itemIds) {
                Span itemSpan = tracer.spanBuilder("checkItem")
                    .setAttribute("item.id", itemId)
                    .startSpan();
                
                try (Scope itemScope = itemSpan.makeCurrent()) {
                    try {
                        Thread.sleep(random.nextInt(inventoryCheckDelay));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    itemSpan.setAttribute("available", true);
                } finally {
                    itemSpan.end();
                }
            }
        } finally {
            span.end();
        }
    }
}
```

```java
package com.learning.tracing.service;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class PaymentService {
    
    private final Tracer tracer;
    
    public PaymentService(Tracer tracer) {
        this.tracer = tracer;
    }
    
    public String chargePayment(String customerId, double amount) {
        Span span = tracer.spanBuilder("payment.charge")
            .setAttribute("customer.id", customerId)
            .setAttribute("amount", amount)
            .startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            String transactionId = "TXN-" + UUID.randomUUID().toString()
                .substring(0, 8).toUpperCase();
            
            span.setAttribute("transaction.id", transactionId);
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return transactionId;
            
        } finally {
            span.end();
        }
    }
}
```

```java
package com.learning.tracing.service;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class ShippingService {
    
    private final Tracer tracer;
    
    public ShippingService(Tracer tracer) {
        this.tracer = tracer;
    }
    
    public String arrangeShipping(String customerId, String address) {
        Span span = tracer.spanBuilder("shipping.arrange")
            .setAttribute("customer.id", customerId)
            .setAttribute("address", address)
            .startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            String trackingNumber = "TRACK-" + UUID.randomUUID().toString()
                .substring(0, 8).toUpperCase();
            
            span.setAttribute("tracking.number", trackingNumber);
            
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return trackingNumber;
            
        } finally {
            span.end();
        }
    }
}
```

### Step 5: Create REST API

```java
package com.learning.tracing.resource;

import com.learning.tracing.service.OrderTracingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderTracingResource {
    
    private final OrderTracingService orderService;
    
    public OrderTracingResource(OrderTracingService orderService) {
        this.orderService = orderService;
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody OrderRequest request) {
        OrderTracingService.OrderResult result = orderService.processOrder(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", result.getOrderId());
        response.put("status", result.getStatus());
        response.put("trackingNumber", result.getTrackingNumber());
        response.put("message", result.getMessage());
        
        return ResponseEntity.ok(response);
    }
    
    record OrderRequest(
        String orderId,
        String customerId,
        double amount,
        String shippingAddress,
        List<String> items
    ) {}
}
```

### Step 6: Configure Application

Create `src/main/resources/application.yaml`:

```yaml
server:
  port: 8080

tracing:
  service-name: order-service
  zipkin-url: http://localhost:9411

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus

logging:
  level:
    root: INFO
```

### Step 7: Run and Test

```bash
# Build project
cd observability-tracing
mvn clean package

# Start Jaeger
docker run -dit --name jaeger \
  -e COLLECTOR_OTLP_ENABLED=true \
  -p 16686:16686 \
  -p 4318:4318 \
  jaegertracing/all-in-one:latest

# Run application
java -jar target/observability-tracing-1.0.0.jar

# Test order processing
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "ORD-001",
    "customerId": "CUST-123",
    "amount": 150.00,
    "shippingAddress": "123 Main St",
    "items": ["ITEM-001", "ITEM-002"]
  }'

# Visualize in Jaeger UI
# Open http://localhost:16686 in browser
```

## Expected Output

- Distributed trace spans
- Service-to-service tracing
- Automatic context propagation
- Jaeger visualization

## Build Instructions Summary

| Step | Command | Time |
|------|---------|------|
| Setup project | Maven | 15 min |
| Configure tracing | OpenTelemetry | 1 hour |
| Create services | Business + tracing | 2 hours |
| Create API | REST endpoints | 1 hour |
| Setup Jaeger | Docker | 15 min |
| Testing | Integration | 3 hours |

Total estimated time: 8-10 hours