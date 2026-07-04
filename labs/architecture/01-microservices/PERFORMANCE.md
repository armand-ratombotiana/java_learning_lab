# Microservices Performance

## Latency Budgets
```
Total request latency: 500ms (target)
Internal service breakdown:
  - API Gateway: 10ms
  - Order Service: 100ms
  - Payment Service: 200ms
  - Inventory Service: 50ms
  - Database: 100ms
  - Network overhead: 40ms
```

## Performance Optimization

### 1. Connection Pooling
```java
@Configuration
public class HttpClientConfig {
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
            .connectionPoolSize(200)
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    }
}
```

### 2. Response Caching
```java
@Cacheable(value = "productCache", key = "#productId")
public Product getProduct(String productId) {
    return inventoryClient.getProduct(productId);
}
```

### 3. Asynchronous Processing
```java
@Async
public CompletableFuture<OrderResponse> processOrder(OrderRequest request) {
    // Process asynchronously, return immediately
    return CompletableFuture.completedFuture(orderService.createOrder(request));
}
```

### 4. Performance Metrics
```java
@Timed(value = "order.create", description = "Order creation time")
public OrderResponse createOrder(OrderRequest request) {
    // MeterRegistry automatically collects metrics
}
```

## Benchmarking
- Use JMH for service-level benchmarks
- Use Gatling for system-level load tests
- Monitor p50, p90, p99 latency percentiles
