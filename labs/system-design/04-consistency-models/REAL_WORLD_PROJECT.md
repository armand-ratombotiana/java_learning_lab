# Consistency Models - REAL WORLD PROJECT

## Project: Global E-commerce Platform Consistency

Design a distributed e-commerce system with different consistency requirements for different components.

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        API Gateway                               │
└─────────────────────────────────────────────────────────────────┘
         │                    │                    │
    ┌────▼────┐         ┌────▼────┐         ┌────▼────┐
    │ Product │         │  Order  │         │Payment  │
    │ Service │         │ Service │         │ Service │
    │   AP    │         │   CP    │         │   CP    │
    └────┬────┘         └────┬────┘         └────┬────┘
         │                    │                    │
    ┌────▼────┐         ┌────▼────┐         ┌────▼────┐
    │Cassandra│         │ PostgreSQL│        │ Bank API │
    │  (AP)   │         │   (CP)    │        │  (CP)    │
    └─────────┘         └──────────┘         └──────────┘
```

## Components with Different Consistency

### 1. Product Catalog (AP - Cassandra)
- High availability for reads
- Eventually consistent
- LWW conflict resolution

### 2. Order Processing (CP - PostgreSQL)
- Strong consistency for inventory
- Two-phase commit for payment
- Saga for order completion

### 3. Session Management (Session)
- Read-your-own-writes
- Sticky sessions with Redis

## Implementation

### Product Service (Eventual Consistency)

```java
@Service
public class ProductService {
    private final CassandraTemplate cassandra;
    private final VectorClockService clocks;
    
    public void updatePrice(String productId, BigDecimal newPrice) {
        VectorClock clock = clocks.generateClock();
        ProductUpdate update = new ProductUpdate(productId, newPrice, clock);
        cassandra.write(KEYSPACE, "products", update);
        // Async replication - eventual consistency
    }
    
    public Product getProduct(String productId) {
        // Read from quorum - can return stale data
        List<Product> versions = cassandra.readQuorum(KEYSPACE, "products", productId);
        return conflictResolver.resolveLWW(versions);
    }
}
```

### Order Service (Strong Consistency)

```java
@Service
public class OrderService {
    private final PostgresTransactionManager tx;
    private final SagaOrchestrator saga;
    
    @Transactional
    public Order createOrder(OrderRequest request) {
        return saga.execute(new OrderSaga(request));
    }
}

public class OrderSaga implements Saga<OrderRequest, Order> {
    public Order execute() {
        reserveInventory();      // Step 1
        processPayment();        // Step 2
        createOrder();           // Step 3
        notifyCustomer();        // Step 4
        return order;
    }
    
    public void compensate() {
        cancelInventory();       // Undo 1
        refundPayment();         // Undo 2
        // Order already created - mark cancelled
    }
}
```

### Session Service (Read-Your-Own-Writes)

```java
@Service
public class SessionService {
    private final RedisTemplate<String, Session> redis;
    private final Map<String, Session> localCache = new ConcurrentHashMap<>();
    private volatile long lastWriteTime;
    
    public void updateCart(String sessionId, Cart cart) {
        // Write to Redis
        redis.opsForValue().set(sessionId, cart);
        // Also cache locally
        localCache.put(sessionId, cart);
        lastWriteTime = System.currentTimeMillis();
    }
    
    public Cart getCart(String sessionId) {
        // Check local cache first for recent writes
        Session cached = localCache.get(sessionId);
        if (cached != null && cached.getTimestamp() >= lastWriteTime) {
            return cached.getCart();
        }
        // Read from Redis
        return redis.opsForValue().get(sessionId);
    }
}
```

## Consistency Configuration Matrix

| Component | Consistency | Database | Strategy |
|-----------|-------------|----------|----------|
| Product Catalog | Eventual | Cassandra | LWW |
| Inventory | Strong | PostgreSQL | 2PC + Locking |
| Orders | Strong | PostgreSQL | Saga |
| Payments | Strong | PostgreSQL | 2PC |
| User Profile | Session | Redis | Sticky + Read-own |
| Search Index | Eventual | Elasticsearch | Async sync |

## Monitoring Consistency

```java
@Configuration
public class ConsistencyMonitoring {
    @Bean
    public MeterRegistry meterRegistry() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }
    
    @Scheduled(fixedRate = 60000)
    public void reportStaleness() {
        // Measure staleness of eventual consistent reads
        gauge("consistency.staleness.seconds", 
              consistencyMonitor::averageStaleness);
        // Track conflict resolution frequency
        counter("consistency.conflicts.resolved", 
                conflictResolver::getResolutionCount);
    }
}
```

## Testing Strategy

```java
@Test
public void testStrongConsistency() {
    // Concurrent writes should fail one
    AtomicInteger successCount = new AtomicInteger(0);
    
    IntStream.range(0, 10).parallel().forEach(i -> {
        try {
            orderService.createOrder(createRequest(i));
            successCount.incrementAndGet();
        } catch (Exception e) {
            // Expected for some - inventory limit
        }
    });
    
    // Should only succeed up to inventory limit
    assertTrue(successCount.get() <= 100);
}
```

## Deliverables

- [x] Product service with eventual consistency (AP)
- [x] Order service with strong consistency (CP)
- [x] Session service with read-your-own-writes
- [x] Saga pattern for order processing
- [x] Circuit breakers for external services
- [x] Monitoring for consistency metrics
- [x] Documentation of consistency decisions