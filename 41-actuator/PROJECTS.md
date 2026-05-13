# Spring Boot Actuator - Projects

This document contains two complete projects demonstrating Spring Boot Actuator: a mini-project for learning core concepts and a real-world project implementing production-grade observability.

## Mini-Projects by Concept

### 1. Endpoint Configuration (2 hours)
Configure actuator endpoints including health, info, metrics, env, beans, and custom endpoints. Learn endpoint exposure and security.

### 2. Health Indicators (2 hours)
Implement custom health indicators for database, cache, external services. Configure health groups and aggregate health views.

### 3. Metrics Exposure (2 hours)
Expose application metrics via actuator. Configure metric exporters, use micrometer for custom metrics, and access metrics endpoint.

### 4. Info & Build Details (2 hours)
Configure info endpoint with build information, git details, and custom info contributors. Expose environment details.

### Real-world: Observability Platform
Build production-grade observability with custom health checks, metrics collection, and endpoint monitoring.

---

## Project 1: Actuator Basics Mini-Project

### Overview

This mini-project demonstrates fundamental Spring Boot Actuator concepts including health endpoints, metrics exposure, and info endpoint configuration. It serves as a learning starting point for understanding actuator endpoints.

### Project Structure

```
actuator-basics/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── learning/
        │           └── actuator/
        │               └── BasicsLab.java
        └── resources/
            └── application.yml
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <groupId>com.learning</groupId>
    <artifactId>actuator-basics</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <java.version>21</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
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

### application.yml

```yaml
server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env,beans
  endpoint:
    health:
      show-details: always
      show-components: always
  info:
    env:
      enabled: true
  metrics:
    export:
      simple:
        enabled: true
info:
  app:
    name: Actuator Basics
    version: 1.0.0
    description: Learning Spring Boot Actuator
```

### BasicsLab.java

```java
package com.learning.actuator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.time.Instant;
import java.util.Random;

@SpringBootApplication
@RestController
public class BasicsLab {
    
    public static void main(String[] args) {
        SpringApplication.run(BasicsLab.class, args);
    }
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello from Actuator Basics!";
    }
    
    @Bean
    public HealthIndicator customHealthIndicator() {
        return () -> Health.up()
            .withDetail("timestamp", Instant.now().toString())
            .withDetail("service", "custom-health-check")
            .build();
    }
    
    @Bean
    public HealthIndicator databaseHealthIndicator() {
        return () -> {
            boolean dbAvailable = checkDatabase();
            if (dbAvailable) {
                return Health.up()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("responseTime", "15ms")
                    .build();
            } else {
                return Health.down()
                    .withDetail("error", "Connection refused")
                    .build();
            }
        };
    }
    
    private boolean checkDatabase() {
        return new Random().nextBoolean();
    }
    
    @GetMapping("/metrics/custom")
    public String customMetrics() {
        var rt = Runtime.getRuntime();
        var mem = ManagementFactory.getMemoryMXBean();
        var os = ManagementFactory.getOperatingSystemMXBean();
        
        long totalMemory = mem.getHeapMemoryUsage().getUsed();
        long maxMemory = mem.getHeapMemoryUsage().getMax();
        int threads = rt.availableProcessors();
        
        return String.format(
            "Memory Used: %d MB, Max: %d MB, Available CPUs: %d",
            totalMemory / 1048576, maxMemory / 1048576, threads
        );
    }
}
```

### Build and Run Instructions

```bash
cd actuator-basics
mvn clean package
java -jar target/actuator-basics-1.0.0.jar
```

### Accessing Actuator Endpoints

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/health/db
curl http://localhost:8080/actuator/info
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.used
curl http://localhost:8080/actuator/env
curl http://localhost:8080/actuator/beans
```

## Project 2: Production Observability Dashboard

### Overview

This real-world project implements a complete production-grade observability solution using Spring Boot Actuator combined with Prometheus and Grafana for metrics visualization. It includes custom health checks, detailed metrics, and integration with monitoring infrastructure.

### Project Structure

```
actuator-production/
├── pom.xml
├── config/
│   └── prometheus.yml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── learning/
        │           └── actuator/
        │               ├── ProductionApplication.java
        │               ├── health/
        │               │   ├── DatabaseHealthIndicator.java
        │               │   ├── RedisHealthIndicator.java
        │               │   └── ExternalServiceHealthIndicator.java
        │               ├── metrics/
        │               │   ├── BusinessMetrics.java
        │               │   └── SystemMetrics.java
        │               └── controller/
        │                   └── OrderController.java
        └── resources/
            └── application.yml
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <groupId>com.learning</groupId>
    <artifactId>actuator-production</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <java.version>21</java.version>
        <micrometer.version>1.12.0</micrometer.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
            <version>${micrometer.version}</version>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-core</artifactId>
            <version>${micrometer.version}</version>
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

### application.yml

```yaml
server:
  port: 8080

spring:
  application:
    name: production-actuator
  datasource:
    url: jdbc:postgresql://localhost:5432/orders
    username: postgres
    password: ${DB_PASSWORD:postgres}
  data-redis:
    host: localhost
    port: 6379
  jpa:
    hibernate:
      ddl-auto: update

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,loggers,env,beans
      base-path: /actuator
  endpoint:
    health:
      show-details: always
      show-components: always
      probes:
        enabled: true
  health:
    livenessState:
      enabled: true
    readinessState:
      enabled: true
    redis:
      enabled: true
    db:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: ${spring.application.name}
    distribution:
      percentiles-histogram:
        http.server.requests: true
      percentiles:
        http.server.requests: 0.5, 0.95, 0.99
info:
  app:
    name: Production Actuator
    version: 1.0.0
  build:
    version: ${BUILD_VERSION:unknown}
  git:
    commit: ${GIT_COMMIT:unknown}
```

### ProductionApplication.java

```java
package com.learning.actuator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.actuate.annotation.EnableEndpointPresentation;

@SpringBootApplication
@EnableEndpointPresentation
public class ProductionApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ProductionApplication.class, args);
    }
}
```

### DatabaseHealthIndicator.java

```java
package com.learning.actuator.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    private final DataSource dataSource;
    
    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                return Health.up()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("timestamp", Instant.now().toString())
                    .withDetail("status", "connected")
                    .build();
            } else {
                return Health.down()
                    .withDetail("error", "Connection invalid")
                    .build();
            }
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .withDetail("timestamp", Instant.now().toString())
                .build();
        }
    }
}
```

### RedisHealthIndicator.java

```java
package com.learning.actuator.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RedisHealthIndicator implements HealthIndicator {
    
    private final StringRedisTemplate redisTemplate;
    
    public RedisHealthIndicator(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    public Health health() {
        try {
            String result = redisTemplate.opsForValue().get("health:ping");
            if (result == null) {
                redisTemplate.opsForValue().set("health:ping", "ok");
            }
            return Health.up()
                .withDetail("cache", "Redis")
                .withDetail("timestamp", Instant.now().toString())
                .withDetail("status", "available")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .withDetail("timestamp", Instant.now().toString())
                .build();
        }
    }
}
```

### ExternalServiceHealthIndicator.java

```java
package com.learning.actuator.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Component
public class ExternalServiceHealthIndicator implements HealthIndicator {
    
    private final RestTemplate restTemplate;
    private static final String SERVICE_URL = "http://external-api:8080/health";
    
    public ExternalServiceHealthIndicator() {
        this.restTemplate = new RestTemplate();
    }
    
    @Override
    public Health health() {
        long startTime = System.currentTimeMillis();
        try {
            Map response = restTemplate.getForObject(SERVICE_URL, Map.class);
            long responseTime = System.currentTimeMillis() - startTime;
            
            return Health.up()
                .withDetail("service", "external-api")
                .withDetail("responseTime", responseTime + "ms")
                .withDetail("timestamp", Instant.now().toString())
                .build();
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            return Health.down()
                .withDetail("service", "external-api")
                .withDetail("error", e.getMessage())
                .withDetail("responseTime", responseTime + "ms")
                .withDetail("timestamp", Instant.now().toString())
                .build();
        }
    }
}
```

### BusinessMetrics.java

```java
package com.learning.actuator.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class BusinessMetrics {
    
    private final Counter orderCreatedCounter;
    private final Counter orderFailedCounter;
    private final Timer orderProcessingTimer;
    private final AtomicInteger activeOrders;
    private final AtomicLong totalRevenue;
    
    public BusinessMetrics(MeterRegistry registry) {
        this.orderCreatedCounter = Counter.builder("orders.created")
            .description("Total number of orders created")
            .register(registry);
        
        this.orderFailedCounter = Counter.builder("orders.failed")
            .description("Total number of failed orders")
            .register(registry);
        
        this.orderProcessingTimer = Timer.builder("orders.processing.time")
            .description("Time to process orders")
            .register(registry);
        
        this.activeOrders = new AtomicInteger(0);
        Gauge.builder("orders.active", activeOrders, AtomicInteger::get)
            .description("Number of active orders")
            .register(registry);
        
        this.totalRevenue = new AtomicLong(0);
        Gauge.builder("orders.revenue.total", totalRevenue, AtomicLong::get)
            .description("Total revenue from orders")
            .register(registry);
    }
    
    public void recordOrderCreated(double amount) {
        orderCreatedCounter.increment();
        activeOrders.incrementAndGet();
        totalRevenue.addAndGet((long) amount);
    }
    
    public void recordOrderCompleted() {
        activeOrders.decrementAndGet();
    }
    
    public void recordOrderFailed() {
        orderFailedCounter.increment();
        activeOrders.decrementAndGet();
    }
    
    public Timer.Sample startTimer() {
        return Timer.start();
    }
    
    public void recordProcessingTime(Timer.Sample sample) {
        sample.stop(orderProcessingTimer);
    }
}
```

### OrderController.java

```java
package com.learning.actuator.controller;

import com.learning.actuator.metrics.BusinessMetrics;
import io.micrometer.core.instrument.Timer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final BusinessMetrics metrics;
    
    public OrderController(BusinessMetrics metrics) {
        this.metrics = metrics;
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> order) {
        Timer.Sample sample = metrics.startTimer();
        
        try {
            String orderId = UUID.randomUUID().toString();
            double amount = ((Number) order.getOrDefault("amount", 0.0)).doubleValue();
            
            metrics.recordOrderCreated(amount);
            metrics.recordProcessingTime(sample);
            
            return ResponseEntity.ok(Map.of(
                "orderId", orderId,
                "status", "created",
                "timestamp", Instant.now().toString(),
                "amount", amount
            ));
        } catch (Exception e) {
            metrics.recordOrderFailed();
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable String id) {
        return ResponseEntity.ok(Map.of(
            "orderId", id,
            "status", "processing",
            "timestamp", Instant.now().toString()
        ));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable String id) {
        metrics.recordOrderFailed();
        return ResponseEntity.ok(Map.of(
            "orderId", id,
            "status", "cancelled",
            "timestamp", Instant.now().toString()
        ));
    }
}
```

### Prometheus Configuration (config/prometheus.yml)

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'production-actuator'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

### Build and Run Instructions

```bash
cd actuator-production
mvn clean package

docker run -d --name prometheus -p 9090:9090 \
    -v $(pwd)/config/prometheus.yml:/etc/prometheus/prometheus.yml \
    prom/prometheus

java -jar target/actuator-production-1.0.0.jar
```

### Accessing Observability Endpoints

```bash
# Health endpoint with all components
curl http://localhost:8080/actuator/health/json

# Prometheus metrics endpoint
curl http://localhost:8080/actuator/prometheus

# Custom business metrics
curl http://localhost:8080/actuator/metrics/orders.created
curl http://localhost:8080/actuator/metrics/orders.active
curl http://localhost:8080/actuator/metrics/orders.revenue.total
```

### Testing with curl

```bash
# Create test orders
curl -X POST http://localhost:8080/api/orders \
    -H "Content-Type: application/json" \
    -d '{"amount": 99.99}'

curl -X POST http://localhost:8080/api/orders \
    -H "Content-Type: application/json" \
    -d '{"amount": 149.99}'

# Check metrics after creating orders
curl http://localhost:8080/actuator/metrics/orders.created
curl http://localhost:8080/actuator/metrics/orders.active
curl http://localhost:8080/actuator/metrics/orders.revenue.total
```

## Summary

These two projects demonstrate:

1. **Mini-Project**: Basic actuator setup with simple health indicators and metrics exposure
2. **Production Project**: Complete observability solution with custom health checks, business metrics, and Prometheus integration

The production project can be extended with Grafana dashboards for visualization and alerting rules for proactive monitoring.