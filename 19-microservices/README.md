# Microservices Module

<div align="center">

![Java](https://img.shields.io/badge/Java_17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-Microservices-6DB33F?style=for-the-badge)
![Kubernetes](https://img.shields.io/badge/Kubernetes-Orchestration-326CE5?style=for-the-badge)

**Master Microservices Architecture and Service Communication**

</div>

---

## Overview

This module covers microservices architecture patterns, service communication, and orchestration. You'll learn how to build, deploy, and manage distributed systems using Spring Cloud and Kubernetes.

---

## Topics Covered

### 1. Service Discovery
- Eureka Server
- Consul
- Kubernetes service discovery
- Service registration and health checks

### 2. API Gateway
- Spring Cloud Gateway
- Route configuration
- Rate limiting
- Authentication/Authorization filters

### 3. Service Communication
- REST clients (RestTemplate, WebClient)
- OpenFeign
- gRPC
- Message-based communication

### 4. Resilience Patterns
- Circuit Breaker (Resilience4j)
- Retry mechanisms
- Bulkhead pattern
- Timeout handling

### 5. Service Orchestration
- Kubernetes deployments
- Service mesh basics
- Config maps and secrets
- Horizontal pod autoscaling

---

## Module Structure

```
19-microservices/
├── README.md                      # This file
├── PROJECTS.md                    # Hands-on projects
├── PEDAGOGIC_GUIDE.md            # Teaching guide
├── EXERCISES.md                  # Practice exercises
├── service-registry/             # Eureka server
├── product-service/              # Product microservice
├── order-service/               # Order microservice
├── customer-service/             # Customer microservice
├── payment-service/              # Payment microservice
└── api-gateway/                  # API Gateway
```

---

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker Desktop
- Kubernetes (optional)

### Run Services
```bash
cd 19-microservices

# Build all services
mvn clean install -DskipTests

# Start Service Registry
cd service-registry && mvn spring-boot:run &

# Start Microservices
cd ../product-service && mvn spring-boot:run &
cd ../order-service && mvn spring-boot:run &

# Start API Gateway
cd ../api-gateway && mvn spring-boot:run &
```

### Test Endpoints
- Eureka Dashboard: http://localhost:8761
- Product API: http://localhost:8080/products
- Order API: http://localhost:8080/orders

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway (8080)                      │
└─────────────────────────┬───────────────────────────────────┘
                          │
         ┌────────────────┼────────────────┐
         │                │                │
         ▼                ▼                ▼
┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│   Product   │  │   Order    │  │  Customer  │
│   Service   │  │   Service   │  │   Service   │
│   (8081)    │  │   (8082)   │  │   (8083)    │
└──────┬──────┘  └──────┬──────┘  └─────────────┘
       │               │
       │               │
       ▼               ▼
┌─────────────┐  ┌─────────────┐
│   Payment  │  │   Product   │
│   Service  │  │   Service   │
│   (8084)   │  │   (8081)    │
└─────────────┘  └─────────────┘
       │
       ▼
┌─────────────────────────────────────────────────┐
│              Service Registry (8761)             │
└─────────────────────────────────────────────────┘
```

---

## Key Patterns

### Circuit Breaker with Resilience4j

```java
@CircuitBreaker(name = "paymentService", fallbackMethod = "fallback")
public PaymentResult processPayment(Order order) {
    // Call payment gateway
}

public PaymentResult fallback(Order order, Throwable t) {
    return PaymentResult.pending(order.getId());
}
```

### OpenFeign Client

```java
@FeignClient(name = "product-service")
public interface ProductClient {
    
    @GetMapping("/api/products/{id}")
    Product getProduct(@PathVariable("id") Long id);
    
    @GetMapping("/api/products")
    List<Product> getAllProducts();
}
```

---

## Production Patterns

1. **Service Mesh**: Istio/Linkerd for traffic management
2. **Distributed Tracing**: Zipkin/Jaeger integration
3. **Centralized Logging**: ELK stack
4. **Metrics**: Prometheus + Grafana
5. **Health Checks**: Liveness and readiness probes

---

## Next Steps

After completing this module, proceed to:
- [20-axon-framework](../20-axon-framework) - CQRS and Event Sourcing
- [30-event-sourcing](../30-event-sourcing) - Event-driven architecture

---

## Resources

- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Microservices.io Patterns](https://microservices.io/patterns/)

