# Microservices Architecture

## Overview
Microservices architecture structures an application as a collection of loosely coupled, independently deployable services. Each service owns its own domain logic and data store, communicating via lightweight protocols.

## Topics Covered
- Service boundaries and decomposition strategies
- Inter-service communication (REST, gRPC, messaging)
- API Gateway pattern
- Service discovery and registration
- Configuration management with Spring Cloud Config
- Resilience patterns (circuit breaker, retry, bulkhead)
- Distributed tracing with Micrometer and Zipkin

## Java/Spring Stack
- Spring Boot for service implementation
- Spring Cloud Gateway for API Gateway
- Eureka/Consul for service discovery
- Spring Cloud Config for externalized configuration
- Resilience4j for fault tolerance
- Spring Cloud Sleuth for distributed tracing

## Prerequisites
- Java 17+
- Spring Boot 3.x
- Docker and Docker Compose
- Understanding of REST APIs

## Getting Started
Each service is a standalone Spring Boot application. Use `docker-compose up` to start all infrastructure dependencies.

## Lab Structure
- `MINI_PROJECT/` - Build a simple order management system with 3 services
- `REAL_WORLD_PROJECT/` - Implement a full e-commerce platform
- `CHALLENGE/` - Advanced microservices challenges
- `TESTS/` - Integration and contract tests
- `BENCHMARK/` - Performance benchmarking harness
- `DIAGRAMS/` - Architecture diagrams and sequence flows
- `SOLUTION/` - Reference implementations
