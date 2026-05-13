# Microservices Resources

Architecture patterns and concepts for microservices.

## Contents

- [Microservices Patterns](./microservices-patterns.md) - Design patterns and patterns

---

## Official Documentation

| Topic | Link |
|-------|------|
| Microservices.io | https://microservices.io/ |
| Spring Cloud | https://spring.io/projects/spring-cloud |
| Kubernetes Docs | https://kubernetes.io/docs/ |

---

## Key Concepts

### Core Patterns
- **Service decomposition** - Split monolith into services
- **API Gateway** - Single entry point for clients
- **Service discovery** - Dynamic service registration
- **Circuit breaker** - Handle failures gracefully
- **Event-driven** - Async communication via messages

### Communication Styles
- **Synchronous** - REST, gRPC
- **Asynchronous** - Message brokers (Kafka, RabbitMQ)

### Data Management
- **Database per service** - Own data store
- **Saga pattern** - Distributed transactions
- **CQRS** - Separate read/write models