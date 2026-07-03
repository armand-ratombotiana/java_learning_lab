# 26 - Software Architecture (Hexagonal)

Hexagonal (Ports & Adapters) architecture in Java with Spring Boot. Covers domain core layers (entities, value objects), port interfaces (inbound/outbound), use case services, adapter implementations (persistence, notification, REST controllers), and dependency inversion principles.

## Prerequisites

- Java 11+
- Maven 3.x
- Spring Boot

## Key Concepts

- Hexagonal architecture: domain core isolated from infrastructure
- Ports: interfaces defining boundary contracts (repository, notification)
- Adapters: implementations connecting domain to external systems (in-memory repository, console notification, REST controller)
- Use cases: application services orchestrating domain logic
- Dependency inversion: domain depends on abstractions, not implementations
- Account domain: deposits, withdrawals, balance management, status tracking

## Module Structure

- `hexagonal-architecture/` - Hexagonal architecture Spring Boot application

## Learning Objectives

- Structure applications using hexagonal architecture
- Separate domain logic from infrastructure concerns
- Implement ports and adapters for maintainable code

## Estimated Time

- 2-3 hours

## How to Build

```bash
cd 26-architecture
mvn clean package
```

Run the Spring Boot application:

```bash
cd hexagonal-architecture
mvn spring-boot:run
```
