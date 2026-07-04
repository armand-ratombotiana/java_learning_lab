# Performance — AWS Database

## RDS Performance

### Connection Pooling with HikariCP
```java
// Optimal HikariCP configuration for RDS
HikariConfig config = new HikariConfig();
config.setMaximumPoolSize(10);  // Rule: maxPoolSize = (core_count × 2) + 1
config.setMinimumIdle(5);
config.setConnectionTimeout(3000);  // 3s max wait for connection
config.setIdleTimeout(600000);      // 10 min idle before removal
config.setMaxLifetime(1800000);     // 30 min max connection lifetime
config.setLeakDetectionThreshold(60000); // 60s leak detection
```

### Read Replica Scaling
```
Pattern: Write to primary, read from replicas
Throughput: Up to 15 read replicas (each with own endpoint)

Application routing:
  ReadWrite: primary endpoint
  ReadOnly:  replica endpoint (round-robin DNS)
  Spring: @Transactional(readOnly = true) → read replica
```

### Parameter Group Tuning
```
MySQL RDS:
  max_connections: 10 × DBInstanceClassMemory / 12582880
  innodb_buffer_pool_size: 75% of instance memory
  query_cache_type: 0 (disabled — Innodb ignores)
  slow_query_log: 1
  long_query_time: 2

PostgreSQL RDS:
  shared_buffers: 25% of instance memory
  effective_cache_size: 50% of instance memory
  work_mem: 32-64MB
  maintenance_work_mem: 10% of instance memory
```

## DynamoDB Performance

### DAX (DynamoDB Accelerator)
```
DAX is a write-through cache for DynamoDB
  - Sub-millisecond read latency (vs 5-10ms without)
  - Up to 10x read performance
  - Handles 1000s of requests/second
  - Transparent to application (drop-in SDK replacement)

DAX SDK:
  AmazonDaxClient.builder()
    .region(Region.US_EAST_1)
    .endpoint("my-dax-cluster.xxxxx.clustercfg.dax.use1.amazonaws.com:8111")
    .build();
```

### Batch Operations
```java
// BatchGetItem — up to 100 items or 16MB in one call
KeysAndAttributes keys = KeysAndAttributes.builder()
    .keys(List.of(
        Map.of("pk", AttributeValue.fromS("user1")),
        Map.of("pk", AttributeValue.fromS("user2"))
    ))
    .build();

BatchGetItemRequest request = BatchGetItemRequest.builder()
    .requestItems(Map.of("Users", keys))
    .build();
```

## ElastiCache Performance

### Redis Pipelining
```java
// No pipeline: N roundtrips (N × latency)
for (String key : keys) {
    jedis.get(key);
}

// With pipeline: 1 roundtrip
Pipeline pipeline = jedis.pipelined();
for (String key : keys) {
    pipeline.get(key);
}
List<Object> results = pipeline.syncAndReturnAll();
// ~N× faster for batch operations
```
