# Architecture Patterns - REFACTORING

## Monolith to Microservices

### Strangler Fig Pattern
Gradually replace monolithic components with microservices.

```java
// Step 1: Add a proxy that routes to new service for specific paths
// Step 2: Move one feature at a time
// Step 3: Remove old monolith code after all features migrated

// Router in API Gateway
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("products", r -> r
            .path("/api/products/**")
            .uri("http://new-product-service"))  // new microservice
        .route("legacy", r -> r
            .path("/api/**")
            .uri("http://legacy-monolith"))  // old system
        .build();
}
```

### Decompose by Business Capability
Identify bounded contexts. Each becomes a service.

## Layered to Hexagonal (Ports & Adapters)

### Before (Tight Coupling)
```java
public class OrderService {
    private JdbcOrderRepository repo;  // direct DB dependency
}
```

### After (Hexagonal)
```java
public class OrderService {
    private final OrderRepository repo;  // interface (port)
    // JdbcOrderRepository injected from outside (adapter)
}
```

## Event-Driven Adoption

### From Synchronous to Async
```java
// Before: synchronous call
public void placeOrder(Order order) {
    inventoryService.reserveStock(order);  // blocking call
    paymentService.processPayment(order);  // blocking call
}

// After: event-driven
public void placeOrder(Order order) {
    orderPlacedPublisher.publish(new OrderPlacedEvent(order));
    // inventory and payment handle events asynchronously
}
```

## Adding CQRS to Existing System

### Step 1: Add Event Store alongside write DB
### Step 2: Create initial projection for current state
### Step 3: Build query model using projections
### Step 4: Route reads to query model, writes to command model
### Step 5: Backfill events from existing data
