# Refactoring to Microservices

## Monolith to Microservices Migration Strategy

### Strangler Fig Pattern
```java
// Step 1: Add proxy layer
@Bean
public RouterFunction<ServerResponse> routingFunction() {
    return RouterFunctions.route()
        .GET("/api/v2/orders", new OrderHandler())
        .GET("/api/v1/orders", oldOrderHandler)  // old code
        .build();
}

// Step 2: Route new traffic to microservice
// Step 3: Verify both systems work correctly
// Step 4: Decommission old code path
```

### Decomposition Patterns

#### 1. Extract Service Boundary
```java
// BEFORE: Monolithic OrderService
@Service
public class MonolithicOrderService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private OrderRepository orderRepository;
    // Everything coupled together
}

// AFTER: Separate services
// 1. Extract PaymentService as standalone service
// 2. Extract InventoryService as standalone service
// 3. OrderService communicates via Feign clients
```

#### 2. Database Separation
```sql
-- Phase 1: Logical separation (same DB, different schemas)
CREATE SCHEMA orders;
CREATE SCHEMA payments;

-- Phase 2: Physical separation (different databases)
-- Extract to separate DB instances
```

#### 3. Incremental Migration
```java
// Use feature flags for gradual rollout
@Configuration
public class ServiceRouter {
    @Value("${feature.use.microservice.order:false}")
    private boolean useMicroservice;

    @Bean
    public OrderService orderService() {
        if (useMicroservice) {
            return new MicroserviceOrderService();
        }
        return new MonolithicOrderService();
    }
}
```
