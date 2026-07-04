# Six-Port Architecture Exercises

## Beginner Exercises

### Exercise 1: Identify Port Types
Given a Spring Boot service, identify and categorize all its external dependencies into the 6 port types.

### Exercise 2: Create Port Interfaces
Create the 6 port interfaces for a simple payment processing service.

## Intermediate Exercises

### Exercise 3: Six-Port Payment Service
Implement a complete payment service with all 6 port types:
- Inbound REST port
- Outbound persistence port
- Outbound external gateway port
- Event publisher port
- Event subscriber port
- Notification port

### Exercise 4: In-Memory Test Adapters
Create in-memory test adapters for each port type and write unit tests.

### Exercise 5: Multiple Adapter Implementations
Implement two versions of PaymentGatewayPort (Stripe and PayPal) and switch with configuration.

## Advanced Exercises

### Exercise 6: Port Monitoring Decorator
Create a decorator around each port type that measures and logs performance:
```java
@Component
public class MonitoredPaymentPort implements PaymentGatewayPort {
    private final PaymentGatewayPort delegate;
    // Add metrics to each method
}
```

### Exercise 7: Complete Six-Port Microservice
Build a complete order microservice:
- All 6 port types implemented
- Real adapters (JPA, Kafka, REST, Email)
- In-memory adapters for testing
- ArchUnit tests verifying port/adapter separation
- Performance monitoring on each port
