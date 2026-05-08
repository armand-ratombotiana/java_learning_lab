# Spring Boot Microservices

Microservices architecture module with Spring Cloud.

## Overview

- Microservices architecture
- Service discovery
- Inter-service communication
- Distributed systems patterns

## Key Concepts

- **Service Decomposition** - Break monolith into services
- **Service Discovery** - Dynamic service registration
- **Circuit Breaker** - Fault tolerance
- **Load Balancing** - Distribute requests

## Running

```bash
mvn spring-boot:run
```

## Dependencies

- spring-boot-starter-web
- spring-cloud-starter (varies by config)

## Service Patterns

| Pattern | Description |
|--------|-------------|
| API Gateway | Single entry point |
| Service Discovery | Register/find services |
| Circuit Breaker | Handle failures |
| Config Server | Centralized config |

## Communication

- REST API calls
- Message queues (async)
- Feign client (declarative)

## Architecture

Each service:
- Owns its data
- Exposes REST API
- Can be scaled independently
- Communicates via network

## Version

Spring Boot 3.3.0