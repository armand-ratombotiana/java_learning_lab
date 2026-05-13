# Prometheus Metrics Projects

This module covers Prometheus metrics collection, monitoring fundamentals, alerting rules, and service discovery for building observable Java applications with comprehensive metrics infrastructure.

## Mini-Projects by Concept

### 1. Metrics Collection (2 hours)
Implement metrics collection using Counter, Gauge, Histogram, and Summary types with Micrometer. Expose application metrics via /actuator/prometheus endpoint.

### 2. Query Language (2 hours)
Master PromQL for querying and aggregating metrics. Learn rate, sum, avg, histogram_quantile functions and label-based filtering.

### 3. Alerting (2 hours)
Configure alerting rules with Alertmanager integration. Define alert conditions, severity levels, notification routing, and alert state management.

### 4. Dashboards (2 hours)
Build Grafana dashboards with Prometheus data sources. Create graphs, panels, alerts, and templated dashboards for operational monitoring.

### Real-world: Monitoring System
Build a comprehensive monitoring system with metrics collection, alerting, and visualization for production applications.

---

## Mini-Project: Application Metrics Dashboard (2-4 Hours)

### Overview

Build a complete metrics monitoring solution demonstrating all Prometheus metric types (Counter, Gauge, Histogram), custom metrics with labels, and integration with Spring Boot Actuator for production-ready observability.

### Technology Stack

- Java 21 with Spring Boot 3.x
- Micrometer for metrics abstraction
- Prometheus client library
- Maven build system

### Project Structure

```
prometheus-metrics/
├── pom.xml
└── src/
    └── main/
        ├── java/com/learning/prometheus/
        │   ├── MetricsDashboardApplication.java
        │   ├── config/
        │   │   └── MetricsConfig.java
        │   ├── metrics/
        │   │   ├── BusinessMetrics.java
        │   │   └── InfrastructureMetrics.java
        │   ├── service/
        │   │   └── MetricsService.java
        │   └── controller/
        │       └── MetricsController.java
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
    
    <artifactId>prometheus-metrics</artifactId>
    
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
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-core</artifactId>
            <version>1.12.0</version>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
            <version>1.12.0</version>
        </dependency>
        <dependency>
            <groupId>io.prometheus</groupId>
            <artifactId>simpleclient</artifactId>
            <version>0.16.0</version>
        </dependency>
        <dependency>
            <groupId>io.prometheus</groupId>
            <artifactId>simpleclient_exporter</artifactId>
            <version>0.16.0</version>
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

**MetricsConfig.java**

```java
package com.learning.prometheus.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.*;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MetricsConfig {
    
    @Bean
    public PrometheusMeterRegistry prometheusMeterRegistry() {
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(
            PrometheusConfig.DEFAULT,
            new io.prometheus.client.Collector.Registry(),
            io.micrometer.core.instrument.Clock.SYSTEM
        );
        
        registerJvmMetrics(registry);
        
        return registry;
    }
    
    private void registerJvmMetrics(PrometheusMeterRegistry registry) {
        new JvmGcMetrics().bindTo(registry);
        new JvmMemoryMetrics().bindTo(registry);
        new JvmThreadMetrics().bindTo(registry);
        new ClassLoaderMetrics().bindTo(registry);
        new JvmCompilationMetrics().bindTo(registry);
        
        new ProcessorMetrics().bindTo(registry);
    }
}
```

**BusinessMetrics.java**

```java
package com.learning.prometheus.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.DoubleSupplier;

@Component
public class BusinessMetrics {
    
    private final MeterRegistry registry;
    
    private final Counter ordersCreated;
    private final Counter ordersFailed;
    private final Counter paymentsProcessed;
    private final Counter userLogins;
    private final Counter userLoginsFailed;
    
    private final Timer orderProcessingTime;
    private final Timer paymentProcessingTime;
    
    private final Gauge activeUsers;
    private final Gauge pendingOrders;
    private final Gauge inventoryItems;
    
    public BusinessMetrics(MeterRegistry registry) {
        this.registry = registry;
        
        this.ordersCreated = Counter.builder("orders_created_total")
            .description("Total number of orders created")
            .tag("type", "business")
            .register(registry);
        
        this.ordersFailed = Counter.builder("orders_failed_total")
            .description("Total number of failed orders")
            .tag("type", "business")
            .register(registry);
        
        this.paymentsProcessed = Counter.builder("payments_processed_total")
            .description("Total number of payments processed")
            .register(registry);
        
        this.userLogins = Counter.builder("user_logins_total")
            .description("Total number of successful user logins")
            .register(registry);
        
        this.userLoginsFailed = Counter.builder("user_logins_failed_total")
            .description("Total number of failed user logins")
            .register(registry);
        
        this.orderProcessingTime = Timer.builder("order_processing_duration_seconds")
            .description("Time taken to process orders")
            .register(registry);
        
        this.paymentProcessingTime = Timer.builder("payment_processing_duration_seconds")
            .description("Time taken to process payments")
            .register(registry);
        
        Gauge.builder("active_users", registry, (DoubleSupplier<Double>) () -> (double) getActiveUserCount())
            .description("Number of currently active users")
            .register(registry);
        
        Gauge.builder("pending_orders", registry, (DoubleSupplier<Double>) () -> (double) getPendingOrderCount())
            .description("Number of pending orders")
            .register(registry);
        
        Gauge.builder("inventory_items", registry, (DoubleSupplier<Double>) () -> (double) getInventoryItemCount())
            .description("Number of items in inventory")
            .register(registry);
    }
    
    public void recordOrderCreated(boolean success) {
        if (success) {
            ordersCreated.increment();
        } else {
            ordersFailed.increment();
        }
    }
    
    public void recordPaymentProcessed() {
        paymentsProcessed.increment();
    }
    
    public void recordUserLogin(boolean success) {
        if (success) {
            userLogins.increment();
        } else {
            userLoginsFailed.increment();
        }
    }
    
    public Timer.Sample startOrderTimer() {
        return Timer.start(registry);
    }
    
    public void recordOrderProcessingTime(Timer.Sample sample) {
        sample.stop(orderProcessingTime);
    }
    
    public Timer.Sample startPaymentTimer() {
        return Timer.start(registry);
    }
    
    public void recordPaymentProcessingTime(Timer.Sample sample) {
        sample.stop(paymentProcessingTime);
    }
    
    private int getActiveUserCount() {
        return (int) (Math.random() * 100) + 50;
    }
    
    private int getPendingOrderCount() {
        return (int) (Math.random() * 20);
    }
    
    private int getInventoryItemCount() {
        return (int) (Math.random() * 1000) + 500;
    }
}
```

**InfrastructureMetrics.java**

```java
package com.learning.prometheus.metrics;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class InfrastructureMetrics {
    
    private final MeterRegistry registry;
    
    private final Counter httpRequestsTotal;
    private final Counter httpRequestsErrors;
    private final Timer httpRequestDuration;
    private final Gauge databaseConnections;
    private final Gauge cacheHitRate;
    private final Counter cacheHits;
    private final Counter cacheMisses;
    
    public InfrastructureMetrics(MeterRegistry registry) {
        this.registry = registry;
        
        this.httpRequestsTotal = Counter.builder("http_requests_total")
            .description("Total HTTP requests")
            .tag("service", "api")
            .register(registry);
        
        this.httpRequestsErrors = Counter.builder("http_requests_errors_total")
            .description("Total HTTP request errors")
            .tag("service", "api")
            .register(registry);
        
        this.httpRequestDuration = Timer.builder("http_request_duration_seconds")
            .description("HTTP request processing time")
            .register(registry);
        
        this.databaseConnections = Gauge.builder("database_connections_active")
            .description("Active database connections")
            .register(registry);
        
        this.cacheHitRate = Gauge.builder("cache_hit_ratio")
            .description("Cache hit ratio")
            .register(registry);
        
        this.cacheHits = Counter.builder("cache_hits_total")
            .description("Total cache hits")
            .register(registry);
        
        this.cacheMisses = Counter.builder("cache_misses_total")
            .description("Total cache misses")
            .register(registry);
    }
    
    public void recordHttpRequest(String method, String path, int statusCode) {
        httpRequestsTotal.increment();
        
        if (statusCode >= 400) {
            httpRequestsErrors.increment();
        }
    }
    
    public Timer.Sample startHttpTimer() {
        return Timer.start(registry);
    }
    
    public void recordHttpRequestTime(Timer.Sample sample) {
        sample.stop(httpRequestDuration);
    }
    
    public void recordDatabaseConnection(int activeConnections) {
        databaseConnections.set(activeConnections);
    }
    
    public void recordCacheHit() {
        cacheHits.increment();
        updateCacheHitRate();
    }
    
    public void recordCacheMiss() {
        cacheMisses.increment();
        updateCacheHitRate();
    }
    
    private void updateCacheHitRate() {
        double total = cacheHits.count() + cacheMisses.count();
        if (total > 0) {
            cacheHitRate.set(cacheHits.count() / total);
        }
    }
}
```

**MetricsService.java**

```java
package com.learning.prometheus.service;

import com.learning.prometheus.metrics.BusinessMetrics;
import com.learning.prometheus.metrics.InfrastructureMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Meter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MetricsService {
    
    private final MeterRegistry registry;
    private final BusinessMetrics businessMetrics;
    private final InfrastructureMetrics infrastructureMetrics;
    
    public MetricsService(MeterRegistry registry, BusinessMetrics businessMetrics, 
            InfrastructureMetrics infrastructureMetrics) {
        this.registry = registry;
        this.businessMetrics = businessMetrics;
        this.infrastructureMetrics = infrastructureMetrics;
    }
    
    public void recordOrderCreated(boolean success) {
        businessMetrics.recordOrderCreated(success);
    }
    
    public void recordPaymentProcessed() {
        businessMetrics.recordPaymentProcessed();
    }
    
    public void recordUserLogin(boolean success) {
        businessMetrics.recordUserLogin(success);
    }
    
    public void recordHttpRequest(String method, String path, int statusCode) {
        infrastructureMetrics.recordHttpRequest(method, path, statusCode);
    }
    
    public void recordDatabaseConnection(int active) {
        infrastructureMetrics.recordDatabaseConnection(active);
    }
    
    public Map<String, Object> getAllMetrics() {
        Map<String, Object> result = new LinkedHashMap<>();
        
        registry.getMeters().forEach(meter -> {
            String name = meter.getId().getName();
            Map<String, String> tags = new HashMap<>();
            meter.getId().getTags().forEach(tag -> tags.put(tag.getKey(), tag.getValue()));
            
            Object value = getMeterValue(meter);
            
            result.put(name, Map.of(
                "value", value,
                "type", meter.getId().getType().toString(),
                "tags", tags
            ));
        });
        
        return result;
    }
    
    public List<Map<String, Object>> getMetricsByName(String namePattern) {
        return registry.getMeters().stream()
            .filter(m -> m.getId().getName().matches(namePattern))
            .map(this::mapMeter)
            .collect(Collectors.toList());
    }
    
    private Object getMeterValue(Meter meter) {
        if (meter instanceof io.micrometer.core.instrument.Counter counter) {
            return counter.count();
        } else if (meter instanceof io.micrometer.core.instrument.Gauge gauge) {
            return gauge.value();
        } else if (meter instanceof io.micrometer.core.instrument.Timer timer) {
            return Map.of(
                "count", timer.count(),
                "sum", timer.totalTime(TimeUnit.SECONDS),
                "max", timer.max(TimeUnit.SECONDS)
            );
        }
        
        return "unknown";
    }
    
    private Map<String, Object> mapMeter(Meter meter) {
        return Map.of(
            "name", meter.getId().getName(),
            "type", meter.getId().getType().toString(),
            "value", getMeterValue(meter)
        );
    }
}
```

**MetricsController.java**

```java
package com.learning.prometheus.controller;

import com.learning.prometheus.metrics.BusinessMetrics;
import com.learning.prometheus.metrics.InfrastructureMetrics;
import com.learning.prometheus.service.MetricsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class MetricsController {
    
    private final MetricsService metricsService;
    private final BusinessMetrics businessMetrics;
    private final InfrastructureMetrics infrastructureMetrics;
    
    public MetricsController(MetricsService metricsService, BusinessMetrics businessMetrics,
            InfrastructureMetrics infrastructureMetrics) {
        this.metricsService = metricsService;
        this.businessMetrics = businessMetrics;
        this.infrastructureMetrics = infrastructureMetrics;
    }
    
    @PostMapping("/orders")
    public Map<String, String> createOrder(@RequestParam(required = false) boolean simulateFailure) {
        metricsService.recordOrderCreated(!simulateFailure);
        
        return Map.of("status", simulateFailure ? "failed" : "created");
    }
    
    @PostMapping("/payments")
    public Map<String, String> processPayment() {
        metricsService.recordPaymentProcessed();
        
        return Map.of("status", "processed");
    }
    
    @PostMapping("/logins")
    public Map<String, String> login(@RequestParam(required = false) boolean simulateFailure) {
        metricsService.recordUserLogin(!simulateFailure);
        
        return Map.of("status", simulateFailure ? "failed" : "success");
    }
    
    @GetMapping("/metrics")
    public Map<String, Object> getAllMetrics() {
        return metricsService.getAllMetrics();
    }
    
    @GetMapping("/metrics/{pattern}")
    public Map<String, Object> getMetricsByPattern(@PathVariable String pattern) {
        return Map.of("meters", metricsService.getMetricsByName(pattern));
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
```

**MetricsDashboardApplication.java**

```java
package com.learning.prometheus;

import com.learning.prometheus.metrics.BusinessMetrics;
import com.learning.prometheus.metrics.InfrastructureMetrics;
import com.learning.prometheus.service.MetricsService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@SpringBootApplication
public class MetricsDashboardApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MetricsDashboardApplication.class, args);
    }
    
    @Bean
    CommandLineRunner demo(MetricsService service, BusinessMetrics businessMetrics) {
        return args -> {
            System.out.println("=== Prometheus Metrics Dashboard Demo ===\n");
            
            System.out.println("1. Recording business metrics:");
            service.recordOrderCreated(true);
            service.recordOrderCreated(true);
            service.recordOrderCreated(true);
            service.recordOrderCreated(false);
            
            service.recordPaymentProcessed();
            service.recordPaymentProcessed();
            
            service.recordUserLogin(true);
            service.recordUserLogin(true);
            service.recordUserLogin(false);
            
            System.out.println("   Orders created: 3 successful, 1 failed");
            System.out.println("   Payments processed: 2");
            System.out.println("   User logins: 2 successful, 1 failed");
            
            System.out.println("\n2. Recording infrastructure metrics:");
            service.recordHttpRequest("GET", "/api/orders", 200);
            service.recordHttpRequest("POST", "/api/orders", 201);
            service.recordHttpRequest("GET", "/api/users", 200);
            service.recordHttpRequest("POST", "/api/payments", 400);
            service.recordHttpRequest("GET", "/api/missing", 404);
            
            service.recordDatabaseConnection(42);
            
            System.out.println("   HTTP requests: 5 total");
            System.out.println("   Database connections: 42");
            
            System.out.println("\n3. Queried metrics:");
            Map<String, Object> metrics = service.getAllMetrics();
            System.out.println("   Total metrics registered: " + metrics.size());
            
            System.out.println("\n=== Demo Complete ===");
            System.exit(0);
        };
    }
}
```

**application.properties**

```properties
spring.application.name=prometheus-metrics
server.port=8080

management.endpoints.web.exposure.include=prometheus,health,info,metrics
management.endpoint.prometheus.enabled=true
management.metrics.export.prometheus.enabled=true
management.metrics.tags.application=${spring.application.name}
```

### Build and Run

```bash
# Start Prometheus
docker run -d --name prometheus -p 9090:9090 -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus

# Build and run
cd 38-prometheus/prometheus-metrics
mvn clean package -DskipTests
mvn spring-boot:run

# Access metrics endpoint
curl http://localhost:8080/actuator/prometheus
```

### Expected Metrics Output

```
# HELP orders_created_total Total number of orders created
# TYPE orders_created_total counter
orders_created_total{type="business"} 3.0

# HELP orders_failed_total Total number of failed orders
# TYPE orders_failed_total counter
orders_failed_total{type="business"} 1.0

# HELP payments_processed_total Total number of payments processed
# TYPE payments_processed_total counter
payments_processed_total 2.0

# HELP user_logins_total Total number of successful user logins
# TYPE user_logins_total counter
user_logins_total 2.0

# HELP http_request_duration_seconds HTTP request processing time
# TYPE http_request_duration_seconds summary
http_request_duration_seconds_count 5.0
http_request_duration_seconds_sum 1.234

# HELP database_connections_active Active database connections
# TYPE database_connections_active gauge
database_connections_active 42.0
```

---

## Real-World Project: Production Monitoring and Alerting System (8+ Hours)

### Overview

Build a comprehensive production monitoring system with Prometheus that demonstrates advanced querying with PromQL, alerting rules with Alertmanager, recording rules for pre-computed metrics, and service discovery integration for dynamic infrastructure monitoring.

### Key Features

1. **Advanced PromQL** - Complex queries and functions
2. **Recording Rules** - Pre-computed metric pipelines
3. **Alerting Rules** - Comprehensive alert definitions
4. **Alertmanager Integration** - Alert routing and notifications
5. **Service Discovery** - Dynamic target discovery
6. **Remote Write** - Long-term storage integration
7. ** federation** - Hierarchical Prometheus setup
8. **Custom Collectors** - Application-specific metrics

### Project Structure

```
prometheus-metrics/
├── pom.xml
├── prometheus/
│   ├── prometheus.yml
│   ├── alerts.yml
│   └── recording_rules.yml
└── src/
    └── main/
        ├── java/com/learning/prometheus/
        │   ├── ProductionMonitoringApplication.java
        │   ├── config/
        │   │   └── AlertConfig.java
        │   ├── collector/
        │   │   └── CustomCollector.java
        │   ├── alerting/
        │   │   └── AlertEvaluator.java
        │   └── service/
        │       └── MonitoringService.java
        └── resources/
            └── application.properties
```

### Implementation

**prometheus.yml**

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

alerting:
  alertmanagers:
    - static_configs:
        - targets: ['alertmanager:9093']

rule_files:
  - 'alerts.yml'
  - 'recording_rules.yml'

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  - job_name: 'application'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['application:8080']

  - job_name: 'redis'
    static_configs:
      - targets: ['redis-exporter:9121']

  - job_name: 'postgres'
    static_configs:
      - targets: ['postgres-exporter:9187']

  - job_name: 'kubernetes-nodes'
    kubernetes_sd_configs:
      - role: node

  - job_name: 'kubernetes-pods'
    kubernetes_sd_configs:
      - role: pod
```

**alerts.yml**

```yaml
groups:
  - name: application_alerts
    interval: 30s
    
    rules:
      - alert: HighErrorRate
        expr: |
          sum(rate(http_requests_errors_total[5m])) 
          / sum(rate(http_requests_total[5m])) > 0.05
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "High error rate detected"
          description: "Error rate is {{ $value | humanizePercentage }}"

      - alert: HighLatency
        expr: |
          histogram_quantile(0.95, sum(rate(http_request_duration_seconds_bucket[5m])) by (le) > 2
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High request latency"
          description: "P95 latency is {{ $value }}s"

      - alert: OutOfMemory
        expr: |
          (jvm_memory_used_bytes / jvm_memory_max_bytes) > 0.9
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "JVM memory usage critical"
          description: "Memory usage is {{ $value | humanizePercentage }}"

      - alert: HighCPUUsage
        expr: |
          process_cpu_seconds_total > 0.8
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "High CPU usage"
          description: "CPU usage is {{ $value | humanizePercentage }}"

      - alert: DatabaseConnectionPoolExhausted
        expr: |
          database_connections_active / database_connections_max > 0.9
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "Database connection pool exhausted"
          description: "{{ $value | humanizePercentage }} of connections in use"

      - alert: ServiceDown
        expr: |
          up{job="application"} == 0
        for: 3m
        labels:
          severity: critical
        annotations:
          summary: "Service is down"
          description: "Application has been down for 3 minutes"

      - alert:HighDiskUsage
        expr: |
          (node_filesystem_avail_bytes / node_filesystem_size_bytes) < 0.1
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "Disk space low"
          description: "Disk usage is {{ $value | humanizePercentage }}"
```

**recording_rules.yml**

```yaml
groups:
  - name: application_recording_rules
    interval: 15s
    
    rules:
      - record: job:http_requests_seconds_sum:rate5m
        expr: |
          sum(rate(http_request_duration_seconds_sum[5m])) by (job)
          / sum(rate(http_request_duration_seconds_count[5m])) by (job)

      - record: job:http_errors_seconds_sum:rate5m
        expr: |
          sum(rate(http_request_duration_seconds_sum[5m])) by (job, status)
          / sum(rate(http_request_duration_seconds_count[5m])) by (job, status)
          > 0.05

      - record: job:orders_per_minute:rate5m
        expr: |
          sum(rate(orders_created_total[5m])) by (job)

      - record: job:active_users:gauge
        expr: |
          sum(active_users) by (job)

      - record: instance:memory_usage_percent:avg
        expr: |
          100 * (1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes))

      - record: instance:cpu_usage_percent:avg5m
        expr: |
          100 * (1 - avg by (instance) (rate(node_cpu_seconds_total{mode="idle"}[5m])))
```

**CustomCollector.java**

```java
package com.learning.prometheus.collector;

import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;
import io.prometheus.client.HistogramMetricFamily;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomCollector extends Collector {
    
    private final List<ChildMetric> metrics = new ArrayList<>();
    
    public static class ChildMetric {
        String name;
        String help;
        double value;
        List<String> labelNames = new ArrayList<>();
        List<String> labelValues = new ArrayList<>();
        Type type = Type.GAUGE;
        
        enum Type { GAUGE, COUNTER, HISTOGRAM }
    }
    
    public void addGauge(String name, String help, double value, String... labels) {
        addMetric(name, help, value, Type.GAUGE, labels);
    }
    
    public void addCounter(String name, String help, double value, String... labels) {
        addMetric(name, help, value, Type.COUNTER, labels);
    }
    
    private void addMetric(String name, String help, double value, Type type, String... labels) {
        ChildMetric metric = new ChildMetric();
        metric.name = name;
        metric.help = help;
        metric.value = value;
        metric.type = type;
        
        for (int i = 0; i < labels.length; i += 2) {
            metric.labelNames.add(labels[i]);
            metric.labelValues.add(labels[i + 1]);
        }
        
        metrics.add(metric);
    }
    
    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> collection = new ArrayList<>();
        
        for (ChildMetric metric : metrics) {
            switch (metric.type) {
                case GAUGE -> {
                    GaugeMetricFamily family = new GaugeMetricFamily(
                        metric.name, metric.help);
                    
                    family.addMetric(metric.labelNames, metric.labelValues, metric.value);
                    collection.add(family);
                }
                
                case COUNTER -> {
                    CounterMetricFamily family = new CounterMetricFamily(
                        metric.name, metric.help);
                    
                    family.addMetric(metric.labelNames, metric.labelValues, metric.value);
                    collection.add(family);
                }
                
                case HISTOGRAM -> {
                    HistogramMetricFamily family = new HistogramMetricFamily(
                        metric.name, metric.help, List.of(0.1, 0.5, 1.0, 5.0));
                    
                    family.addMetric(metric.labelNames, metric.labelValues, 1.0, List.of(1L, 1L, 1L, 1L));
                    collection.add(family);
                }
            }
        }
        
        return collection;
    }
}
```

**AlertEvaluator.java**

```java
package com.learning.prometheus.alerting;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AlertEvaluator {
    
    private final Map<String, AlertState> alertStates = new ConcurrentHashMap<>();
    private final List<AlertCallback> callbacks = new ArrayList<>();
    
    public interface AlertCallback {
        void onAlertFiring(Alert alert);
        void onAlertResolved(Alert alert);
    }
    
    public static class Alert {
        private String name;
        private String expression;
        private String severity;
        private String message;
        private LocalDateTime firedAt;
        private Map<String, String> labels;
        
        public Alert(String name, String expression, String severity, String message) {
            this.name = name;
            this.expression = expression;
            this.severity = severity;
            this.message = message;
            this.labels = new HashMap<>();
        }
        
        public String getName() { return name; }
        public String getExpression() { return expression; }
        public String getSeverity() { return severity; }
        public String getMessage() { return message; }
        public LocalDateTime getFiredAt() { return firedAt; }
        public void setFiredAt(LocalDateTime firedAt) { this.firedAt = firedAt; }
        public Map<String, String> getLabels() { return labels; }
    }
    
    static class AlertState {
        Alert alert;
        boolean firing;
        int fireDuration;
        LocalDateTime lastEvaluation;
        
        AlertState(Alert alert) {
            this.alert = alert;
            this.lastEvaluation = LocalDateTime.now();
        }
    }
    
    public void evaluate(String alertName, boolean condition, String expression, 
            String severity, String message, int forMinutes) {
        
        AlertState state = alertStates.computeIfAbsent(alertName, 
            k -> new AlertState(new Alert(alertName, expression, severity, message)));
        
        if (condition) {
            state.fireDuration += 1;
            
            if (!state.firing && state.fireDuration >= forMinutes) {
                state.firing = true;
                state.alert.setFiredAt(LocalDateTime.now());
                
                notifyCallbacks(AlertCallback::onAlertFiring, state.alert);
                
                System.out.println("ALERT FIRED: " + alertName + " [" + severity + "]");
            }
        } else {
            if (state.firing) {
                state.firing = false;
                state.fireDuration = 0;
                
                notifyCallbacks(AlertCallback::onAlertResolved, state.alert);
                
                System.out.println("ALERT RESOLVED: " + alertName);
            }
            
            state.fireDuration = 0;
        }
        
        state.lastEvaluation = LocalDateTime.now();
    }
    
    public void registerCallback(AlertCallback callback) {
        callbacks.add(callback);
    }
    
    private void notifyCallbacks(java.util.function.Consumer<AlertCallback> action, Alert alert) {
        for (AlertCallback callback : callbacks) {
            try {
                action.accept(callback);
            } catch (Exception e) {
                System.err.println("Callback error: " + e.getMessage());
            }
        }
    }
    
    public List<Alert> getFiringAlerts() {
        return alertStates.values().stream()
            .filter(s -> s.firing)
            .map(s -> s.alert)
            .toList();
    }
    
    public Map<String, Boolean> getAlertStatus() {
        Map<String, Boolean> status = new HashMap<>();
        
        alertStates.forEach((name, state) -> status.put(name, state.firing));
        
        return status;
    }
}
```

### Build and Run

```bash
cd 38-prometheus/prometheus-metrics
mvn clean package -DskipTests

# Start Prometheus with config
docker run -d --name prometheus -p 9090:9090 \
  -v $(pwd)/prometheus:/etc/prometheus \
  prom/prometheus

# Start Alertmanager
docker run -d --name alertmanager -p 9093:9093 \
  -v $(pwd)/alertmanager.yml:/etc/alertmanager/alertmanager.yml \
  prom/alertmanager

# Run application
mvn spring-boot:run
```

### API Endpoints

```
# Metrics
GET /actuator/prometheus           # Prometheus format metrics
GET /actuator/metrics            # JSON metrics
GET /api/metrics                # Custom metrics

# Alerts
GET /api/alerts                 # Get firing alerts
GET /api/alerts/status          # Get alert status

# Application
POST /api/orders?simulateFailure=true/false
POST /api/payments
POST /api/logins?simulateFailure=true/false

GET /api/health
```

### Learning Outcomes

After completing these projects, you will understand:

1. **Metric Types** - Counters, Gauges, Histograms
2. **Micrometer** - Spring Boot metrics abstraction
3. **PromQL** - Advanced query language
4. **Recording Rules** - Pre-computed metrics
5. **Alerting** - Alert definitions and routing
6. **Service Discovery** - Dynamic target discovery
7. **Labels** - Multi-dimensional metrics
8. **Exporters** - Infrastructure monitoring

### References

- Prometheus Exposition Format: https://prometheus.io/docs/instrumenting/exposition_formats/
- PromQL: https://prometheus.io/docs/prometheus/latest/querying/basics/
- Alerting: https://prometheus.io/docs/prometheus/latest/configuration/alerting_rules/