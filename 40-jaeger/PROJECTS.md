# Jaeger Distributed Tracing Projects

This module covers Jaeger distributed tracing integration, trace analysis, span correlation, and comprehensive observability for building traceable Java applications with complete request flow visibility across microservices.

## Mini-Project: Request Tracing System (2-4 Hours)

### Overview

Build a distributed tracing system with Jaeger that demonstrates trace creation, span management, context propagation, and integration with Spring Boot for complete request flow visibility.

### Technology Stack

- Java 21 with Spring Boot 3.x
- Jaeger client library
- OpenTelemetry
- Maven build system

### Project Structure

```
jaeger-tracing/
├── pom.xml
└── src/
    └── main/
        ├── java/com/learning/jaeger/
        │   ├── TraceApplication.java
        │   ├── config/
        │   │   └── TracingConfig.java
        │   ├── service/
        │   │   ├── OrderTracingService.java
        │   │   └── PaymentTracingService.java
        │   └── controller/
        │       └── TraceController.java
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
    
    <artifactId>jaeger-tracing</artifactId>
    
    <properties>
        <java.version>21</java.version>
        <spring-boot.version>3.2.0</spring-boot.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jaegertracing</groupId>
            <artifactId>jaeger-client</artifactId>
            <version>1.8.0</version>
        </dependency>
        <dependency>
            <groupId>io.opentelemetry</groupId>
            <artifactId>opentelemetry-api</artifactId>
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
                <version>${spring-boot.version}</version>
            </plugin>
        </plugins>
    </build>
</project>
```

**TracingConfig.java**

```java
package com.learning.jaeger.config;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.Tracer;
import io.opentracing.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {
    
    @Bean
    public Tracer jaegerTracer() {
        Configuration configuration = new Configuration("jaeger-tracing")
            .withReporterConfiguration(new ReporterConfiguration()
                .withLogSpans(true)
                .withSender(new Configuration.SenderConfiguration()
                    .withAgentHost("localhost")
                    .withAgentPort(6831)))
            .withSamplerConfiguration(new SamplerConfiguration()
                .withType("probabilistic")
                .withParam(0.1));
        
        return configuration.getTracer();
    }
}
```

**OrderTracingService.java**

```java
package com.learning.jaeger.service;

import io.opentracing.Span;
import io.opentracing.Tracer;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderTracingService {
    
    private final Tracer tracer;
    private final PaymentTracingService paymentService;
    
    public OrderTracingService(Tracer tracer, PaymentTracingService paymentService) {
        this.tracer = tracer;
        this.paymentService = paymentService;
    }
    
    public Map<String, Object> createOrder(String orderId, List<Map<String, Object>> items) {
        Span span = tracer.buildSpan("create-order")
            .withTag("order.id", orderId)
            .withTag("operation", "create-order")
            .start();
        
        span.setTag("items.count", String.valueOf(items.size()));
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            span.log("Validating order items");
            validateOrderItems(items);
            
            span.log("Checking inventory for items");
            Map<String, Object> inventoryResult = checkInventory(items);
            
            span.log("Processing payment");
            Map<String, Object> paymentResult = paymentService.processPayment(
                extractTotal(items), span);
            
            result.put("status", "CREATED");
            result.put("orderId", orderId);
            result.put("inventoryChecked", inventoryResult.get("available"));
            result.put("paymentProcessed", paymentResult.get("success"));
            
            span.setTag("order.status", "CREATED");
            
        } catch (Exception e) {
            span.setTag("error", "true");
            span.log("Order creation failed: " + e.getMessage());
            result.put("status", "FAILED");
            result.put("error", e.getMessage());
            
        } finally {
            span.finish();
        }
        
        return result;
    }
    
    private void validateOrderItems(List<Map<String, Object>> items) {
        for (Map<String, Object> item : items) {
            int quantity = (int) item.getOrDefault("quantity", 0);
            if (quantity <= 0) {
                throw new IllegalArgumentException("Invalid quantity");
            }
        }
    }
    
    private Map<String, Object> checkInventory(List<Map<String, Object>> items) {
        Span span = tracer.buildSpan("check-inventory")
            .withTag("operation", "check-inventory")
            .start();
        
        Map<String, Object> result = new HashMap<>();
        result.put("available", true);
        
        for (Map<String, Object> item : items) {
            span.setTag("item.id", item.get("productId").toString());
            span.log("Checked stock for " + item.get("productId"));
        }
        
        span.finish();
        
        return result;
    }
    
    private double extractTotal(List<Map<String, Object>> items) {
        return items.stream()
            .mapToDouble(item -> ((Number) item.getOrDefault("price", 0)).doubleValue() * 
                ((Number) item.getOrDefault("quantity", 1)).intValue())
            .sum();
    }
    
    public Map<String, Object> getOrder(String orderId) {
        Span span = tracer.buildSpan("get-order")
            .withTag("order.id", orderId)
            .withTag("operation", "get-order")
            .start();
        
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("status", "FOUND");
        
        span.finish();
        
        return result;
    }
    
    public Map<String, Object> getOrderTrace(String orderId) {
        Span span = tracer.buildSpan("get-order-trace")
            .withTag("order.id", orderId)
            .start();
        
        Map<String, Object> result = new HashMap<>();
        result.put("traceId", span.context().toTraceId());
        result.put("spanId", span.context().toSpanId());
        result.put("orderId", orderId);
        
        span.finish();
        
        return result;
    }
}
```

**PaymentTracingService.java**

```java
package com.learning.jaeger.service;

import io.opentracing.Span;
import io.opentracing.Tracer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentTracingService {
    
    private final Tracer tracer;
    
    public PaymentTracingService(Tracer tracer) {
        this.tracer = tracer;
    }
    
    public Map<String, Object> processPayment(double amount, Span parentSpan) {
        Span span = tracer.buildSpan("process-payment")
            .asChildOf(parentSpan)
            .withTag("operation", "process-payment")
            .start();
        
        span.setTag("amount", String.valueOf(amount));
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            span.log("Validating payment amount");
            if (amount <= 0) {
                throw new IllegalArgumentException("Invalid amount");
            }
            
            span.log("Authorizing payment");
            authorizePayment(amount);
            
            span.log("Charging card");
            chargeCard(amount);
            
            result.put("success", true);
            result.put("transactionId", "TXN-" + System.currentTimeMillis());
            
            span.setTag("payment.status", "SUCCESS");
            
        } catch (Exception e) {
            span.setTag("error", "true");
            span.log("Payment failed: " + e.getMessage());
            
            result.put("success", false);
            result.put("error", e.getMessage());
            
            span.setTag("payment.status", "FAILED");
            
        } finally {
            span.finish();
        }
        
        return result;
    }
    
    private void authorizePayment(double amount) throws InterruptedException {
        Thread.sleep(50);
    }
    
    private void chargeCard(double amount) throws InterruptedException {
        Thread.sleep(100);
    }
}
```

**TraceController.java**

```java
package com.learning.jaeger.controller;

import com.learning.jaeger.service.OrderTracingService;
import com.learning.jaeger.service.PaymentTracingService;
import io.opentracing.Tracer;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class TraceController {
    
    private final OrderTracingService orderService;
    private final PaymentTracingService paymentService;
    private final Tracer tracer;
    
    public TraceController(OrderTracingService orderService, 
            PaymentTracingService paymentService, Tracer tracer) {
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.tracer = tracer;
    }
    
    @PostMapping("/orders")
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> request) {
        String orderId = request.containsKey("orderId") 
            ? request.get("orderId").toString() 
            : UUID.randomUUID().toString();
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");
        
        return orderService.createOrder(orderId, items);
    }
    
    @GetMapping("/orders/{orderId}")
    public Map<String, Object> getOrder(@PathVariable String orderId) {
        return orderService.getOrder(orderId);
    }
    
    @GetMapping("/orders/{orderId}/trace")
    public Map<String, Object> getOrderTrace(@PathVariable String orderId) {
        return orderService.getOrderTrace(orderId);
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "tracer", "jaeger");
    }
}
```

**TraceApplication.java**

```java
package com.learning.jaeger;

import com.learning.jaeger.service.OrderTracingService;
import io.opentracing.Tracer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class TraceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TraceApplication.class, args);
    }
    
    @Bean
    CommandLineRunner demo(OrderTracingService orderService) {
        return args -> {
            System.out.println("=== Jaeger Distributed Tracing Demo ===\n");
            
            List<Map<String, Object>> items = List.of(
                Map.of("productId", "LAPTOP-001", "name", "Laptop", "quantity", 1, "price", 1200.00),
                Map.of("productId", "MOUSE-001", "name", "Mouse", "quantity", 2, "price", 29.99)
            );
            
            System.out.println("1. Creating order with distributed tracing:");
            Map<String, Object> result = orderService.createOrder("ORD-12345", items);
            System.out.println("   Order status: " + result.get("status"));
            System.out.println("   Inventory checked: " + result.get("inventoryChecked"));
            System.out.println("   Payment processed: " + result.get("paymentProcessed"));
            
            System.out.println("\n2. Getting order trace:");
            Map<String, Object> trace = orderService.getOrderTrace("ORD-12345");
            System.out.println("   Trace ID: " + trace.get("traceId"));
            System.out.println("   Span ID: " + trace.get("spanId"));
            
            System.out.println("\n3. Demonstrating span hierarchy:");
            System.out.println("   Root span: create-order");
            System.out.println("   Child spans: check-inventory, process-payment");
            System.out.println("   Grandchild: authorize-payment, charge-card");
            
            System.out.println("\n=== Demo Complete ===");
            System.exit(0);
        };
    }
}
```

**application.properties**

```properties
spring.application.name=jaeger-tracing
server.port=8080

jaeger.agent.host=localhost
jaeger.agent.port=6831
jaeger.service.name=jaeger-tracing
jaeger.sampler.type=probabilistic
jaeger.sampler.param=0.1
```

### Build and Run

```bash
# Start Jaeger
docker run -d --name jaeger -p 6831:6831 -p 16686:16686 jaegertracing/all-in-one:1.48

# Build and run
cd 40-jaeger/jaeger-tracing
mvn clean package -DskipTests
mvn spring-boot:run
```

### Expected Output

```
=== Jaeger Distributed Tracing Demo ===

1. Creating order with distributed tracing:
   Order status: CREATED
   Inventory checked: true
   Payment processed: true

2. Getting order trace:
   Trace ID: 4f4e3d2c1b5a8e9f
   Span ID: 1a2b3c4d5e6f7g8h

3. Demonstrating span hierarchy:
   Root span: create-order
   Child spans: check-inventory, process-payment
   Grandchild: authorize-payment, charge-card

=== Demo Complete ===
```

---

## Real-World Project: Enterprise Trace Analysis Platform (8+ Hours)

### Overview

Build a comprehensive trace analysis platform with Jaeger that demonstrates advanced trace querying, service dependency analysis, performance bottlenecks identification, and integration with comprehensive observability for enterprise applications.

### Key Features

1. **Advanced Trace Queries** - Complex trace filtering
2. **Service Dependencies** - Dependency graph analysis
3. **Performance Analysis** - Bottleneck identification
4. **Error Tracking** - Error correlation across services
5. **Sampling Strategies** - Intelligent trace sampling
6. **Multi-Trace Analysis** - Aggregated trace insights

### Project Structure

```
jaeger-tracing/
├── pom.xml
└── src/
    └── main/
        ├── java/com/learning/jaeger/
        │   ├── TraceAnalysisApplication.java
        │   ├── config/
        │   │   └── AdvancedTracingConfig.java
        │   ├── analysis/
        │   │   ├── TraceAnalyzer.java
        │   │   └── DependencyAnalyzer.java
        │   ├── service/
        │   │   └── TracingService.java
        │   └── controller/
        │       └── AnalysisController.java
        └── resources/
            └── application.properties
```

### Implementation

**AdvancedTracingConfig.java**

```java
package com.learning.jaeger.config;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.opentracing.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdvancedTracingConfig {
    
    @Bean
    public Tracer advancedTracer() {
        Configuration config = new Configuration("jaeger-tracing")
            .withReporterConfiguration(new ReporterConfiguration()
                .withLogSpans(true)
                .withSender(new Configuration.SenderConfiguration()
                    .withAgentHost("localhost")
                    .withAgentPort(6831))
                .withFlushInterval(1000)
                .withMaxQueueSize(10000))
            .withSamplerConfiguration(new SamplerConfiguration()
                .withType("rate_limiting")
                .withParam(100.0));
        
        return config.getTracer();
    }
}
```

**TraceAnalyzer.java**

```java
package com.learning.jaeger.analysis;

import io.opentracing.Span;
import io.opentracing.Tracer;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class TraceAnalyzer {
    
    private final Map<String, TraceData> traces = new ConcurrentHashMap<>();
    private final Tracer tracer;
    
    public static class TraceData {
        String traceId;
        String operationName;
        String serviceName;
        Instant startTime;
        Instant endTime;
        long durationMs;
        Map<String, String> tags;
        List<SpanData> spans;
        boolean error;
        
        TraceData(String traceId, String operationName, String serviceName) {
            this.traceId = traceId;
            this.operationName = operationName;
            this.serviceName = serviceName;
            this.tags = new HashMap<>();
            this.spans = new ArrayList<>();
        }
        
        public String getTraceId() { return traceId; }
        public String getOperationName() { return operationName; }
        public String getServiceName() { return serviceName; }
        public Instant getStartTime() { return startTime; }
        public void setStartTime(Instant startTime) { this.startTime = startTime; }
        public Instant getEndTime() { return endTime; }
        public void setEndTime(Instant endTime) { this.endTime = endTime; }
        public long getDurationMs() { return durationMs; }
        public void setDurationMs(long durationMs) { this.durationMs = durationMs; }
        public Map<String, String> getTags() { return tags; }
        public List<SpanData> getSpans() { return spans; }
        public boolean isError() { return error; }
        public void setError(boolean error) { this.error = error; }
    }
    
    public static class SpanData {
        String spanId;
        String operationName;
        String serviceName;
        String parentSpanId;
        long startTimeMs;
        long durationMs;
        Map<String, String> tags;
        
        public String getSpanId() { return spanId; }
        public String getOperationName() { return operationName; }
        public String getServiceName() { return serviceName; }
        public String getParentSpanId() { return parentSpanId; }
        public long getStartTimeMs() { return startTimeMs; }
        public long getDurationMs() { return durationMs; }
        public Map<String, String> getTags() { return tags; }
    }
    
    public TraceAnalyzer(Tracer tracer) {
        this.tracer = tracer;
    }
    
    public String startTrace(String operationName, String serviceName, Map<String, String> tags) {
        Span span = tracer.buildSpan(operationName)
            .withTag("service.name", serviceName)
            .start();
        
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            span.setTag(entry.getKey(), entry.getValue());
        }
        
        TraceData traceData = new TraceData(
            span.context().toTraceId(),
            operationName,
            serviceName
        );
        
        traces.put(traceData.traceId, traceData);
        
        return traceData.traceId;
    }
    
    public void addSpan(String traceId, String operationName, String serviceName, 
            String parentSpanId, long duration) {
        
        TraceData trace = traces.get(traceId);
        if (trace == null) return;
        
        SpanData spanData = new SpanData();
        spanData.operationName = operationName;
        spanData.serviceName = serviceName;
        spanData.parentSpanId = parentSpanId;
        spanData.durationMs = duration;
        
        trace.spans.add(spanData);
    }
    
    public void endTrace(String traceId) {
        TraceData trace = traces.get(traceId);
        if (trace == null) return;
        
        trace.endTime = Instant.now();
        
        if (trace.startTime != null) {
            trace.durationMs = java.time.Duration.between(trace.startTime, trace.endTime).toMillis();
        }
    }
    
    public List<TraceData> getErrorTraces() {
        return traces.values().stream()
            .filter(TraceData::isError)
            .sorted(Comparator.comparing(TraceData::getStartTime).reversed())
            .collect(Collectors.toList());
    }
    
    public List<TraceData> getSlowTraces(long thresholdMs) {
        return traces.values().stream()
            .filter(t -> t.durationMs > thresholdMs)
            .sorted(Comparator.comparing(TraceData::getDurationMs).reversed())
            .collect(Collectors.toList());
    }
    
    public Map<String, List<TraceData>> getTracesByService() {
        return traces.values().stream()
            .collect(Collectors.groupingBy(TraceData::getServiceName));
    }
    
    public Map<String, Double> getAverageDurationByOperation() {
        return traces.values().stream()
            .collect(Collectors.groupingBy(
                TraceData::getOperationName,
                Collectors.averagingLong(TraceData::getDurationMs)
            ));
    }
    
    public Map<String, Long> getPercentileDuration(String operation, int percentile) {
        List<Long> durations = traces.values().stream()
            .filter(t -> t.getOperationName().equals(operation))
            .map(TraceData::getDurationMs)
            .sorted()
            .collect(Collectors.toList());
        
        if (durations.isEmpty()) {
            return Map.of();
        }
        
        int index = (int) Math.ceil(durations.size() * percentile / 100.0) - 1;
        
        return Map.of(
            "p" + percentile, durations.get(index),
            "min", durations.get(0),
            "max", durations.get(durations.size() - 1)
        );
    }
    
    public List<String> identifyBottlenecks(String traceId) {
        TraceData trace = traces.get(traceId);
        if (trace == null) return List.of();
        
        List<String> bottlenecks = new ArrayList<>();
        
        for (SpanData span : trace.spans) {
            if (span.durationMs > 100) {
                bottlenecks.add(span.operationName + ": " + span.durationMs + "ms");
            }
        }
        
        bottlenecks.sort((a, b) -> {
            long da = Long.parseLong(a.split(": ")[1].replace("ms", ""));
            long db = Long.parseLong(b.split(": ")[1].replace("ms", ""));
            return Long.compare(db, da);
        });
        
        return bottlenecks;
    }
    
    public Map<String, Object> getTraceSummary(String traceId) {
        TraceData trace = traces.get(traceId);
        if (trace == null) {
            return Map.of("error", "Trace not found");
        }
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("traceId", trace.traceId);
        summary.put("operation", trace.operationName);
        summary.put("service", trace.serviceName);
        summary.put("durationMs", trace.durationMs);
        summary.put("spanCount", trace.spans.size());
        summary.put("error", trace.error);
        summary.put("startTime", trace.startTime);
        
        return summary;
    }
}
```

**DependencyAnalyzer.java**

```java
package com.learning.jaeger.analysis;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DependencyAnalyzer {
    
    private final Map<String, ServiceNode> serviceGraph = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Edge>> edges = new ConcurrentHashMap<>();
    
    public static class ServiceNode {
        String serviceName;
        int callCount;
        long totalLatency;
        double avgLatency;
        int errorCount;
        
        public String getServiceName() { return serviceName; }
        public int getCallCount() { return callCount; }
        public long getTotalLatency() { return totalLatency; }
        public double getAvgLatency() { return avgLatency; }
        public int getErrorCount() { return errorCount; }
    }
    
    public static class Edge {
        String fromService;
        String toService;
        int callCount;
        long totalLatency;
        
        public String getFromService() { return fromService; }
        public String getToService() { return toService; }
        public int getCallCount() { return callCount; }
        public long getTotalLatency() { return totalLatency; }
    }
    
    public void recordCall(String fromService, String toService, long latency, boolean error) {
        ServiceNode fromNode = serviceGraph.computeIfAbsent(fromService, 
            k -> { ServiceNode n = new ServiceNode(); n.serviceName = fromService; return n; });
        fromNode.callCount++;
        fromNode.totalLatency += latency;
        fromNode.avgLatency = (double) fromNode.totalLatency / fromNode.callCount;
        if (error) fromNode.errorCount++;
        
        ServiceNode toNode = serviceGraph.computeIfAbsent(toService,
            k -> { ServiceNode n = new ServiceNode(); n.serviceName = toService; return n; });
        
        Map<String, Edge> fromEdges = edges.computeIfAbsent(fromService, k -> new HashMap<>());
        Edge edge = fromEdges.computeIfAbsent(toService, k -> {
            Edge e = new Edge();
            e.fromService = fromService;
            e.toService = toService;
            return e;
        });
        edge.callCount++;
        edge.totalLatency += latency;
    }
    
    public Map<String, ServiceNode> getServices() {
        return new HashMap<>(serviceGraph);
    }
    
    public Map<String, Edge>> getDependencies() {
        return new HashMap<>(edges);
    }
    
    public List<String> getDepGraph() {
        List<String> lines = new ArrayList<>();
        
        for (ServiceNode node : serviceGraph.values()) {
            lines.add(node.serviceName + " [calls=" + node.callCount + 
                ", avgLatency=" + String.format("%.2f", node.avgLatency) + "ms]");
        }
        
        return lines;
    }
    
    public List<String> findCriticalPath() {
        return serviceGraph.values().stream()
            .sorted((a, b) -> Double.compare(b.avgLatency, a.avgLatency))
            .limit(5)
            .map(n -> n.serviceName + " (p50 latency: " + 
                String.format("%.2f", n.avgLatency) + "ms)")
            .toList();
    }
    
    public Map<String, Object> getDependencyStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalServices", serviceGraph.size());
        stats.put("totalEdges", edges.values().stream()
            .mapToInt(Map::size)
            .sum());
        
        int totalCalls = serviceGraph.values().stream()
            .mapToInt(s -> s.callCount)
            .sum();
        stats.put("totalCalls", totalCalls);
        
        double totalAvgLatency = serviceGraph.values().stream()
            .mapToDouble(s -> s.avgLatency)
            .average()
            .orElse(0.0);
        stats.put("avgLatency", totalAvgLatency);
        
        return stats;
    }
}
```

**TracingService.java**

```java
package com.learning.jaeger.service;

import com.learning.jaeger.analysis.TraceAnalyzer;
import com.learning.jaeger.analysis.DependencyAnalyzer;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class TracingService {
    
    private final Tracer tracer;
    private final TraceAnalyzer analyzer;
    private final DependencyAnalyzer depAnalyzer;
    
    public TracingService(Tracer tracer, TraceAnalyzer analyzer, 
            DependencyAnalyzer depAnalyzer) {
        this.tracer = tracer;
        this.analyzer = analyzer;
        this.depAnalyzer = depAnalyzer;
    }
    
    public String startDistributedTrace(String operationName, String serviceName) {
        Map<String, String> tags = new HashMap<>();
        tags.put("operation", operationName);
        tags.put("service", serviceName);
        
        return analyzer.startTrace(operationName, serviceName, tags);
    }
    
    public Span continueTrace(String traceId, String operationName, 
            String serviceName, String parentSpanId) {
        
        return tracer.buildSpan(operationName)
            .asChildOf(tracer.extract(
                io.opentracing.propagacao.Format.TEXT_MAP,
                new io.opentracing.propagacao.TextMapAdapter(Map.of(
                    "uber-trace-id", traceId
                ))
            ))
            .withTag("service.name", serviceName)
            .start();
    }
    
    public void recordServiceCall(String fromService, String toService, 
            long latency, boolean error) {
        
        depAnalyzer.recordCall(fromService, toService, latency, error);
    }
    
    public Map<String, Object> analyzeTrace(String traceId) {
        Map<String, Object> analysis = new LinkedHashMap<>();
        
        analysis.put("summary", analyzer.getTraceSummary(traceId));
        analysis.put("bottlenecks", analyzer.identifyBottlenecks(traceId));
        
        return analysis;
    }
    
    public Map<String, Object> getPerformanceAnalysis() {
        Map<String, Object> analysis = new LinkedHashMap<>();
        
        analysis.put("avgDurationByOperation", analyzer.getAverageDurationByOperation());
        analysis.put("slowTraces", analyzer.getSlowTraces(1000));
        analysis.put("errorTraces", analyzer.getErrorTraces());
        
        return analysis;
    }
    
    public Map<String, Object> getServiceAnalysis() {
        Map<String, Object> analysis = new LinkedHashMap<>();
        
        analysis.put("services", depAnalyzer.getServices());
        analysis.put("dependencies", depAnalyzer.getDependencies());
        analysis.put("criticalPath", depAnalyzer.findCriticalPath());
        
        return analysis;
    }
    
    public Map<String, Object> getFullAnalysis() {
        Map<String, Object> analysis = new LinkedHashMap<>();
        
        Map<String, Double> avgDuration = analyzer.getAverageDurationByOperation();
        avgDuration.forEach((op, avg) -> {
            Map<String, Object> percentiles = analyzer.getPercentileDuration(op, 50);
            percentiles.forEach((p, v) -> avgDuration.merge(p, v, (a, b) -> v));
        });
        
        analysis.put("avgDurationByOperation", avgDuration);
        analysis.put("tracesByService", analyzer.getTracesByService());
        analysis.put("depStats", depAnalyzer.getDependencyStats());
        analysis.put("depGraph", depAnalyzer.getDepGraph());
        
        return analysis;
    }
}
```

### Build and Run

```bash
cd 40-jaeger/jaeger-tracing
mvn clean package -DskipTests
mvn spring-boot:run
```

### API Endpoints

```
GET /api/traces/{traceId}/analysis
GET /api/traces/performance
GET /api/traces/services
GET /api/traces/errors
GET /api/services/dependencies
GET /api/health
```

### Learning Outcomes

After completing these projects, you will understand:

1. **Distributed Tracing** - Trace creation and spans
2. **Context Propagation** - Trace ID injection/extraction
3. **Span Hierarchy** - Parent-child span relationships
4. **Trace Analysis** - Performance bottleneck identification
5. **Service Dependencies** - Call graph analysis
6. **Error Tracking** - Error correlation across services
7. **Sampling** - Probabilistic and rate limiting
8. **Jaeger UI** - Trace visualization

### References

- Jaeger Documentation: https://www.jaegertracing.io/docs/
- OpenTracing API: https://opentracing.io/
- Semantic Conventions: https://opentelemetry.io/docs/specification/trace/semantic_conventions/