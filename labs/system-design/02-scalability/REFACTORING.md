# Scalability - REFACTORING

## From Monolith to Horizontally Scalable

### Step 1: Extract Session State
```java
// Before: In-memory HttpSession
// After: Redis-backed session
@Configuration
@EnableRedisHttpSession
public class SessionConfig { /* ... */ }
```

### Step 2: Stateless Authentication
```java
// Before: Session-based auth
SecurityContextHolder.getContext().setAuthentication(auth);

// After: JWT token
String token = "Bearer " + jwtService.createToken(user);
```

### Step 3: Extract Background Jobs
```java
// Before: Synchronous processing in request thread
public Order placeOrder(Order order) {
    emailService.sendConfirmation(order);  // blocking
    return orderRepository.save(order);
}

// After: Async via message queue
public Order placeOrder(Order order) {
    Order saved = orderRepository.save(order);
    kafkaTemplate.send("order-events", saved);
    return saved;
}
```

## From Vertical to Horizontal Database

### Add Read Replicas
```java
// Before: Single datasource
@Bean
public DataSource dataSource() {
    return new HikariDataSource(masterConfig);
}

// After: Read/write routing
@Bean
@Primary
public DataSource routingDataSource() {
    Map<Object, Object> sources = new HashMap<>();
    sources.put("master", masterDS);
    sources.put("replica", replicaDS);
    return new TransactionAwareDataSourceProxy(
        new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return TransactionSynchronizationManager
                    .isCurrentTransactionReadOnly() ? "replica" : "master";
            }
        }
    );
}
```

## From Single DB to Sharded

### Client-Side Sharding
```java
// Before: Single database
// After: ShardingSphere or custom router
// 1. Choose shard key (e.g., customer_id)
// 2. Map customer_id → shard
// 3. Route each query to correct shard
```

## Performance Gains Expected

| Refactoring | Improvement |
|------------|------------|
| Stateless services | 10x horizontal scale |
| Read replicas | 5x read throughput |
| Caching | 10-50x query latency |
| Async processing | 3x request throughput |
| Sharding | 10x data capacity |
