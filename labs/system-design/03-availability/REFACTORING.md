# Availability - REFACTORING

## Adding Circuit Breakers to Existing Code

### Before
```java
@Service
public class ProductService {
    public List<Product> getProducts() {
        return externalApi.fetchProducts();  // direct, unprotected call
    }
}
```

### After
```java
@Service
public class ProductService {
    private final CircuitBreaker circuitBreaker;

    public ProductService(CircuitBreakerRegistry registry) {
        this.circuitBreaker = registry.circuitBreaker("productService");
    }

    public List<Product> getProducts() {
        return Try.ofSupplier(
            CircuitBreaker.decorateSupplier(circuitBreaker,
                () -> externalApi.fetchProducts())
        ).recover(e -> cachedProducts()).get();
    }
}
```

## From Single DB to Read Replicas

### Before: Single database
```java
@Bean
public DataSource dataSource() {
    return new HikariDataSource(singleDbConfig());
}
```

### After: Master + Replica
```java
@Bean
@Primary
public DataSource routingDataSource() {
    return new ReadWriteDataSource(masterDs, replicaDs);  // routes by @Transactional
}
```

## Adding Retry Logic

### Before: Direct call
```java
public Product getProduct(String id) {
    return client.call(id);  // fails immediately on timeout
}
```

### After: Retry with backoff
```java
@Retryable(
    retryFor = TransientException.class,
    maxAttempts = 3,
    backoff = @Backoff(delay = 500, multiplier = 2)
)
public Product getProduct(String id) {
    return client.call(id);
}
```

## From Monolithic to Microservices with Resilience

### Strategy
1. Extract one service at a time
2. Add circuit breaker on each extraction
3. Keep original service as fallback
4. Remove old code when confidence is high

### Graceful Degradation
```java
public OrderPage getOrderPage(String id) {
    Order order = orderService.getOrder(id);  // primary
    User user = null;
    Recommendation rec = null;

    try {
        user = userService.getUser(order.getUserId());  // optional
    } catch (Exception e) {
        log.warn("User service unavailable, showing partial page");
    }

    try {
        rec = recommendationService.getRecommendations(id);  // optional
    } catch (Exception e) {
        // skip recommendations gracefully
    }

    return new OrderPage(order, user, rec);
}
```
