# Reflection: Redis

## Key Takeaways

- Redis's single-threaded model simplifies application code — no race conditions on single keys
- Rich data structures eliminate the need for separate tools (cache + queue + session store)
- TTL-based expiration makes cache management automatic
- Pipelining is essential for high-throughput applications
- AOF + RDB hybrid gives the best balance of durability and performance
- Redis Cluster enables linear scalability but requires careful key design (hash tags)

## Common Pitfalls

1. O(N) commands like KEYS, SMEMBERS, HGETALL on large collections
2. Missing eviction policy — Redis crashes with OOM
3. Not using connection pools — creating connections per request
4. Cross-slot operations in cluster mode without hash tags
5. Over-reliance on Pub/Sub without considering Streams for persistent messaging

## Questions for Further Study

- How does Redis's LFU eviction compare to LRU for different workloads?
- What are the practical limitations of Redlock in production?
- When should you use Redis Streams vs Kafka vs RabbitMQ?
- How does Redis on Flash (with SSD tiering change performance characteristics)?

## Application to Java Projects

```yaml
# Recommended Spring Boot Redis configuration
spring:
  cache:
    type: redis
    redis:
      time-to-live: 3600000  # 1 hour default
      cache-null-values: false
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
        max-wait: 3000ms
      shutdown-timeout: 200ms
```
