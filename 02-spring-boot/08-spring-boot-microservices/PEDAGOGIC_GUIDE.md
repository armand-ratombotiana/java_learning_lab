# Spring Boot Microservices - Pedagogic Guide

## Learning Path

### Phase 1: Microservices Fundamentals (Day 1)
1. **Microservices Architecture** - Decomposition
2. **Service Boundaries** - Domain-driven design
3. **Independent Deployment** - Separate artifacts
4. **Database per Service** - Data isolation

### Phase 2: Communication (Day 2)
1. **Synchronous** - REST, gRPC
2. **Asynchronous** - Message queues
3. **Service Discovery** - Eureka, Consul
4. **Load Balancing** - Client-side, server-side

### Phase 3: Resiliency (Day 3)
1. **Circuit Breaker** - Resilience4j
2. **Retry Patterns** - Automatic retries
3. **Timeout Handling** - Prevent hanging
4. **Bulkheads** - Isolate failures

## Key Concepts

### Patterns
- **API Gateway** - Entry point
- **Service Registry** - Discovery
- ** Circuit Breaker** - Fault tolerance
- **Config Server** - Centralized config

### Communication Types
- **REST** - Synchronous, simple
- **Message Queues** - Async, decoupled
- **gRPC** - High performance

## Common Patterns

### Service Discovery
1. Service registers with discovery server
2. Client looks up service by name
3. Load balancer distributes requests

### Circuit Breaker
```java
@CircuitBreaker(name = "service", fallbackMethod = "fallback")
public String callService() { ... }
```

## Best Practices
- Loose coupling between services
- Independent scaling
- Graceful degradation
- Centralized logging
- Distributed tracing