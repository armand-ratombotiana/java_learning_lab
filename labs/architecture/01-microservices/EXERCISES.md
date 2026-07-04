# Microservices Exercises

## Beginner Exercises

### Exercise 1: Service Discovery
Implement Eureka server and register two Spring Boot services.
```java
// Create Eureka server with @EnableEurekaServer
// Register order-service and payment-service
// Verify they appear in Eureka dashboard
```

### Exercise 2: API Gateway
Configure Spring Cloud Gateway to route requests to services.
```yaml
# Routes for order-service, payment-service, notification-service
```

## Intermediate Exercises

### Exercise 3: Circuit Breaker
Add Resilience4j circuit breaker to a Feign client call with fallback.

### Exercise 4: Distributed Tracing
Integrate Spring Cloud Sleuth with Zipkin for request tracing across 3 services.

### Exercise 5: Event-Driven Communication
Implement order creation event flow using Kafka:
- Order Service publishes OrderCreated event
- Notification Service consumes and sends email
- Inventory Service consumes and reserves stock

## Advanced Exercises

### Exercise 6: Saga Pattern
Implement distributed transaction with compensating actions:
- Orchestrate order > payment > inventory
- Implement compensation on failure

### Exercise 7: Blue/Green Deployment
Set up Kubernetes deployment with blue/green strategy for zero-downtime deployments.

### Exercise 8: Performance Benchmark
Use Gatling to benchmark your microservices system under 1000 concurrent users.
