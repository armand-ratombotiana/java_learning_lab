# Lab 16: Spring Cloud - Microservice Infrastructure

## Overview
Spring Cloud provides tools for building distributed systems: service discovery, configuration management, load balancing, circuit breakers, and API gateways. This lab covers the core Spring Cloud components needed for production microservices.

## Topics Covered
- Service Discovery with Netflix Eureka
- Distributed Configuration with Spring Cloud Config
- Client-Side Load Balancing with Spring Cloud LoadBalancer
- Circuit Breakers with Resilience4J
- API Gateway with Spring Cloud Gateway
- Distributed Tracing with Micrometer

## Prerequisites
- Java 21+
- Spring Boot 3.3+
- Basic microservices concepts
- Docker for running infrastructure services

## Labs Structure
```
16-spring-cloud/
├── src/main/java/com/learning/backend16/
│   ├── Backend16Application.java
│   ├── discovery/          # Eureka server/client
│   ├── config/             # Config server/client
│   ├── gateway/            # API Gateway
│   └── resilience/         # Circuit breaker demos
├── src/test/java/com/learning/backend16/
├── BENCHMARK/
├── CHALLENGE/
├── DIAGRAMS/
├── MINI_PROJECT/
├── REAL_WORLD_PROJECT/
├── SOLUTION/
├── TESTS/
└── *.md (24 documentation files)
```

## Getting Started
```bash
# Start Eureka server
mvn spring-boot:run -Dspring-boot.run.profiles=eureka

# Start Config server
mvn spring-boot:run -Dspring-boot.run.profiles=config-server

# Start a client application
mvn spring-boot:run -Dspring-boot.run.profiles=client
```

## Key Dependencies
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

## Learning Objectives
1. Understand service discovery patterns and Eureka architecture
2. Implement distributed configuration with Spring Cloud Config
3. Apply circuit breaker patterns with Resilience4J
4. Configure API Gateway for routing and filtering
5. Implement client-side load balancing
6. Set up distributed tracing

## Project Structure
The lab contains multiple modules representing a microservices ecosystem:
- **eureka-server**: Service registry (Eureka)
- **config-server**: Centralized configuration
- **gateway-service**: API Gateway entry point
- **product-service**: Sample microservice (with circuit breaker)
- **order-service**: Sample microservice (with circuit breaker)

## Running the Full Stack
```bash
# Terminal 1: Eureka Server
mvn spring-boot:run -pl eureka-server

# Terminal 2: Config Server
mvn spring-boot:run -pl config-server

# Terminal 3: Gateway
mvn spring-boot:run -pl gateway-service

# Terminals 4-5: Microservices
mvn spring-boot:run -pl product-service
mvn spring-boot:run -pl order-service
```

## Verification
- Eureka Dashboard: http://localhost:8761
- Config Server: http://localhost:8888/config/default
- Gateway: http://localhost:8080
- Product Service: http://localhost:8081/products
- Order Service: http://localhost:8082/orders
