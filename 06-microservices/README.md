# 06 - Microservices Architecture Patterns

Architectural patterns for microservices systems. Covers service decomposition strategies, database-per-service patterns, event-driven architecture (event bus, pub/sub), saga pattern (orchestration), circuit breaker pattern, and API gateway pattern with rate limiting.

## Prerequisites

- Java 11+
- Maven 3.x

## Key Concepts

- Service decomposition (domain-driven design, subdomain decomposition)
- Database per service with private data stores
- Event-driven architecture with event bus and asynchronous consumers
- Saga pattern for distributed transactions (orchestration & choreography)
- Circuit breaker pattern (closed, open, half-open states)
- API gateway pattern (routing, rate limiting, aggregation)

## Module Structure

- `01-architecture-patterns/` - Decomposition, event-driven, saga, circuit breaker, API gateway
- `02-service-discovery/` - Service registry and discovery
- `03-api-gateway/` - Gateway routing and filtering

## Learning Objectives

- Understand microservices decomposition strategies
- Implement event-driven communication between services
- Apply saga and circuit breaker patterns for resilience

## Estimated Time

- 3-5 hours across submodules

## How to Build

```bash
cd 06-microservices
mvn clean package
```

Run the architecture patterns lab:

```bash
cd 01-architecture-patterns
mvn compile exec:java -Dexec.mainClass="com.learning.micro.architecture.ArchitecturePatternsLab"
```
