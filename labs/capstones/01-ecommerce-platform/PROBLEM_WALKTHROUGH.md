# Problem Walkthrough: High-Traffic E-Commerce Platform

## Problem Statement

**Design a high-traffic e-commerce platform supporting 1M daily active users, 100K concurrent shoppers during peak events (Black Friday, Cyber Monday), with 99.99% availability, sub-200ms page loads, and zero data loss for orders.**

The platform must handle product catalog browsing, shopping cart management, order processing, payment processing, inventory management, personalized recommendations, and admin analytics. The system must gracefully handle traffic spikes of 10x normal load during flash sales while maintaining data consistency for orders and payments.

### Business Requirements
- 1M DAU, 100K concurrent peak users
- 10,000 orders/minute at peak
- 99.99% uptime (52 minutes downtime/year max)
- Sub-200ms page load times (P95)
- Zero order loss — every submitted order must be processed
- Real-time inventory accuracy within 1 second
- Personalized recommendations served in < 50ms

### Technical Constraints
- Java 21+ runtime
- Microservices architecture
- Event-driven communication between services
- At-least-once delivery guarantees for order processing
- Horizontal scaling for all service tiers
- Multi-region deployment for disaster recovery

---

## Solution Architecture

### Step 1: Identify Core Services

Decompose the platform into independently deployable services:

1. **Product Catalog Service** — Product browsing, search, filtering (read-heavy, cacheable)
2. **Shopping Cart Service** — Cart CRUD, merge anonymous carts on login (session affinity)
3. **Order Service** — Order creation, status management, state machine (transactional)
4. **Payment Service** — Payment processing, fraud detection, refunds (idempotent)
5. **Inventory Service** — Stock reservation, release on timeout/ cancellation (high consistency)
6. **User Service** — Authentication, profiles, address book
7. **Recommendation Service** — Collaborative filtering, personalized suggestions (read-heavy)
8. **Admin Analytics** — Sales reports, inventory insights, user metrics (async, eventual consistency)

### Step 2: Data Flow Design

```
[Client] --> [CDN] --> [Load Balancer] --> [API Gateway]
                                                |
                 +------------------------------+------------------------------+
                 |              |               |              |               |
          [Catalog Svc]   [Cart Svc]     [Order Svc]    [Payment Svc]   [User Svc]
                 |              |               |              |               |
                 +--------------+-------+-------+----+---------+               |
                                        |              |                       |
                                   [Message Queue]  [Cache Layer]              |
                                        |              |                       |
                                   [Inventory]  [Redis Cluster]               |
                                        |              |                       |
                                   [Database]    [Session Store]              |
```

### Step 3: Caching Strategy

Implement multi-layer caching:

```java
public class CacheManager<K, V> {
    private final Cache<K, V> localCache;  // Caffeine (in-process)
    private final RedisCluster redis;       // Distributed cache
    private final Database database;        // Source of truth

    public V get(CacheKey key, CacheLevel level) {
        if (level == CacheLevel.LOCAL) {
            V val = localCache.getIfPresent(key);
            if (val != null) return val;
        }
        if (level == CacheLevel.REDIS || level == CacheLevel.ALL) {
            V val = redis.get(key.toString());
            if (val != null) {
                localCache.put(key, val);  // Populate local
                return val;
            }
        }
        // Cache miss — load from database
        V val = database.load(key);
        redis.set(key.toString(), val, Duration.ofMinutes(5));
        localCache.put(key, val);
        return val;
    }
}

// Cache invalidation via event bus
public class CacheInvalidator {
    private final MessageBus bus;
    private final RedisCluster redis;

    public void onProductUpdated(Long productId) {
        // Broadcast invalidation to all service instances
        bus.publish("cache.invalidate", new InvalidateEvent("product", productId));
    }

    @EventListener
    public void handleInvalidation(InvalidateEvent event) {
        redis.del(event.getCacheKey());
        // Local cache will expire via TTL or on next read
    }
}
```

### Step 4: Message Queue for Order Processing

Use a Kafka-like message queue for reliable order processing:

```java
public class OrderProcessingPipeline {

    private final Producer<String, OrderEvent> producer;
    private final Consumer<String, OrderEvent> consumer;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;

    public void submitOrder(Order order) {
        // Step 1: Validate order
        OrderValidated validated = validateOrder(order);

        // Step 2: Publish to "order-created" topic
        producer.send("order-created", order.getOrderId(), validated);
    }

    @KafkaListener(topics = "order-created")
    public void handleOrderCreated(OrderEvent event) {
        // Step 3: Reserve inventory (with timeout)
        boolean reserved = inventoryService.reserveStock(
            event.getOrderId(),
            event.getItems(),
            Duration.ofMinutes(15)  // Release if not completed in 15 min
        );

        if (reserved) {
            producer.send("inventory-reserved", event.getOrderId(), event);
        } else {
            producer.send("order-failed", event.getOrderId(),
                new OrderFailed(event.getOrderId(), "Insufficient inventory"));
        }
    }

    @KafkaListener(topics = "inventory-reserved")
    public void handleInventoryReserved(OrderEvent event) {
        // Step 4: Process payment (idempotent — retry safe)
        PaymentResult payment = paymentService.processPayment(
            event.getPaymentInfo(),
            event.getTotal()
        );

        if (payment.isSuccess()) {
            producer.send("payment-completed", event.getOrderId(), event);
        } else if (payment.isRetryable()) {
            // Retry with exponential backoff
            producer.send("payment-retry", event.getOrderId(), event);
        } else {
            // Release inventory, fail order
            inventoryService.releaseStock(event.getOrderId());
            producer.send("order-failed", event.getOrderId(),
                new OrderFailed(event.getOrderId(), payment.getError()));
        }
    }

    @KafkaListener(topics = "payment-completed")
    public void handlePaymentCompleted(OrderEvent event) {
        // Step 5: Confirm order
        orderService.confirmOrder(event.getOrderId());
        producer.send("order-confirmed", event.getOrderId(), event);
    }
}
```

### Step 5: Failover and Resilience Patterns

```java
// Circuit Breaker for downstream service calls
public class CircuitBreaker {
    private final int failureThreshold;
    private final Duration timeout;
    private AtomicInteger failureCount = new AtomicInteger(0);
    private volatile CircuitState state = CircuitState.CLOSED;
    private volatile Instant lastFailureTime;

    public <T> T call(Supplier<T> operation, T fallback) {
        if (state == CircuitState.OPEN) {
            if (Duration.between(lastFailureTime, Instant.now()).compareTo(timeout) > 0) {
                state = CircuitState.HALF_OPEN;
            } else {
                return fallback;  // Fast fail
            }
        }

        try {
            T result = operation.get();
            if (state == CircuitState.HALF_OPEN) {
                state = CircuitState.CLOSED;
                failureCount.set(0);
            }
            return result;
        } catch (Exception e) {
            lastFailureTime = Instant.now();
            int failures = failureCount.incrementAndGet();
            if (failures >= failureThreshold) {
                state = CircuitState.OPEN;
            }
            return fallback;
        }
    }
}

// Bulkhead pattern — isolate thread pools per dependency
public class BulkheadRegistry {
    private final Map<String, ThreadPoolExecutor> executors = new ConcurrentHashMap<>();

    public BulkheadRegistry() {
        // Dedicated thread pools per external dependency
        executors.put("payment-gateway", createPool(10, 20, "payment"));
        executors.put("inventory-service", createPool(20, 40, "inventory"));
        executors.put("recommendation-engine", createPool(5, 10, "recs"));
    }

    public <T> CompletableFuture<T> submit(String service, Callable<T> task) {
        ThreadPoolExecutor executor = executors.get(service);
        if (executor == null) {
            throw new IllegalArgumentException("Unknown service: " + service);
        }
        return CompletableFuture.supplyAsync(() -> {
            try { return task.call(); }
            catch (Exception e) { throw new RuntimeException(e); }
        }, executor);
    }

    private ThreadPoolExecutor createPool(int core, int max, String name) {
        return new ThreadPoolExecutor(core, max, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            new ThreadFactoryBuilder().setNameFormat(name + "-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
```

### Step 6: Database Sharding for Orders

```java
public class OrderShardManager {
    private static final int SHARD_COUNT = 16;
    private final List<DataSource> shards;

    public OrderShardManager(List<DataSource> shards) {
        if (shards.size() != SHARD_COUNT) {
            throw new IllegalArgumentException("Need " + SHARD_COUNT + " shards");
        }
        this.shards = shards;
    }

    public DataSource getShard(String orderId) {
        int shardId = Math.abs(orderId.hashCode() % SHARD_COUNT);
        return shards.get(shardId);
    }

    public DataSource getShard(Long customerId, Long orderId) {
        // Composite sharding: customer-based for queries, order-based for writes
        int shardId = Math.abs((customerId.hashCode() * 31 + orderId.hashCode()) % SHARD_COUNT);
        return shards.get(shardId);
    }

    // Read from all shards in parallel for admin queries
    public <T> List<T> queryAllShards(Function<DataSource, List<T>> query) {
        return shards.parallelStream()
            .flatMap(ds -> query.apply(ds).stream())
            .collect(Collectors.toList());
    }
}
```

### Step 7: Rate Limiting and Throttling

```java
@Component
public class RateLimiter {
    private final Cache<String, TokenBucket> buckets = Caffeine.newBuilder()
        .expireAfterAccess(Duration.ofMinutes(5))
        .build();

    public boolean allowRequest(String userId, String endpoint, int permits) {
        String key = userId + ":" + endpoint;
        TokenBucket bucket = buckets.get(key, k -> new TokenBucket(100, 10));
        // 100 tokens max, refill 10 per second
        return bucket.tryConsume(permits);
    }

    static class TokenBucket {
        private final int maxTokens;
        private final int refillRate;
        private double tokens;
        private Instant lastRefill;

        TokenBucket(int maxTokens, int refillRate) {
            this.maxTokens = maxTokens;
            this.refillRate = refillRate;
            this.tokens = maxTokens;
            this.lastRefill = Instant.now();
        }

        synchronized boolean tryConsume(int count) {
            refill();
            if (tokens >= count) {
                tokens -= count;
                return true;
            }
            return false;
        }

        private void refill() {
            long elapsed = Duration.between(lastRefill, Instant.now()).toMillis();
            double newTokens = (elapsed / 1000.0) * refillRate;
            tokens = Math.min(maxTokens, tokens + newTokens);
            lastRefill = Instant.now();
        }
    }
}
```

### Step 8: Deployment Architecture

```yaml
# Kubernetes deployment topology
services:
  - name: api-gateway
    replicas: 10
    hpa:
      min: 5
      max: 50
      cpuThreshold: 70
    resources:
      requests: { cpu: "1", memory: "2Gi" }
      limits: { cpu: "2", memory: "4Gi" }

  - name: product-catalog
    replicas: 20
    hpa:
      min: 10
      max: 100
      cpuThreshold: 60
    cache:
      local: Caffeine (10K entries, 5 min TTL)
      distributed: Redis Cluster (6 nodes, 3 replicas)

  - name: order-service
    replicas: 15
    hpa:
      min: 5
      max: 30
      cpuThreshold: 70
    database:
      type: PostgreSQL (16 shards)
      read-replicas: 2 per shard

  - name: inventory-service
    replicas: 10
    database:
      type: PostgreSQL with optimistic locking
      isolation: SERIALIZABLE

  - name: payment-service
    replicas: 8
    idempotency: Redis-based idempotency keys (24h TTL)
```

---

## Best Practices

### Caching
1. **Multi-layer with TTL stratification**: L1 cache (local, 5s TTL) → L2 (Redis, 5min TTL) → DB; shorter TTLs for frequently updated data
2. **Cache-aside pattern**: Load on miss, cache before returning; use write-through for inventory counts
3. **Invalidation via events**: Never set absolute TTL for critical data; invalidate explicitly via message bus events
4. **Cache warming**: Pre-warm product catalog cache on deployment; use historical traffic patterns to determine which products to cache

### Queue Design
1. **At-least-once delivery**: Idempotent consumers with deduplication (orderId as unique key)
2. **Dead letter queues**: Route failed messages to DLQ after 3 retries; monitor DLQ for operational alerts
3. **Backpressure**: Implement consumer lag monitoring; auto-scale consumers when lag exceeds threshold (1000 messages)
4. **Exactly-once semantics for payments**: Use idempotency keys with TTL; reject duplicate payment requests within idempotency window

### Database
1. **Sharding key**: Use customerId as primary shard for orders; orderId as secondary — ensures all customer orders co-located for queries
2. **Read replicas**: Route analytical queries to read replicas; keep write traffic on primary
3. **Optimistic locking**: Use version columns for concurrent inventory updates; retry on conflict
4. **Connection pooling**: HikariCP with max 20 connections per service instance; monitor for connection leaks

### Resilience
1. **Timeouts**: Set connect timeout (500ms), read timeout (2s), write timeout (2s) for all inter-service calls
2. **Retry with jitter**: Exponential backoff (100ms, 200ms, 400ms, 800ms, 1.6s) + random jitter (+/- 50%)
3. **Bulkhead**: Isolate thread pools per downstream dependency; one failing service should not exhaust all threads
4. **Health checks**: /health endpoint with dependency checks; readiness probe fails if downstream dependencies are unhealthy

### Deployment
1. **Blue-green deployment**: Maintain two identical environments; switch traffic after health checks pass
2. **Canary releases**: Route 5% traffic to new version; monitor error rates and latencies for 10 minutes before full rollout
3. **Feature flags**: Use LaunchDarkly or similar for gradual feature rollout; kill switch for problematic features
4. **Chaos engineering**: Weekly chaos experiments (kill a node, inject latency, partition network) to validate resilience

## Performance Targets

| Metric | Target | Measurement |
|--------|--------|-------------|
| P95 page load time | < 200ms | Real user monitoring (RUM) |
| P99 API response | < 500ms | APM instrumentation |
| Order throughput | 10,000/min | Load test |
| Order confirmation latency | < 5s | End-to-end tracing |
| Inventory accuracy | 99.99% | Reconciliation job |
| Cache hit ratio | > 95% | Redis metrics |
| Payment success rate | > 99.5% | Payment provider logs |
| Search response | < 100ms (P95) | Elasticsearch metrics |
| Recommendation latency | < 50ms | Service metrics |
| System availability | 99.99% | Uptime monitoring |
