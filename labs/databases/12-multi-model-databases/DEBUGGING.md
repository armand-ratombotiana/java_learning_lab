# Debugging: Multi-Model & Polyglot

## Common Issues

| Symptom | Cause | Fix |
|---|---|---|
| Stale data in search | Elasticsearch indexing lag | Check indexing pipeline, use index refresh API |
| Cache miss storm | Redis eviction + many concurrent requests | Warm cache, increase maxmemory |
| Cross-database inconsistency | Partial write failure | Implement outbox pattern or saga |
| Connection pool exhausted on one DB | Traffic spike on one model | Separate connection pools, scale that database |
| Query timeout on wrong DB | Running graph query on document DB | Route queries to correct database |

## Debugging Tools

| Database | Tool | Command |
|---|---|---|
| PostgreSQL | pg_stat_activity | `SELECT * FROM pg_stat_activity` |
| MongoDB | mongostat, mongotop | Monitor document operations |
| Redis | redis-cli MONITOR | Watch all commands |
| Neo4j | PROFILE | Query profiling |
| Elasticsearch | /_nodes/hot_threads | Slow query detection |

## Consistency Verification
```java
// Write to primary, then verify secondary
public void createOrder(Order order) {
    orderRepo.save(order);  // Write to PostgreSQL

    // Verify eventually consistent replica
    RetryUtil.retry(() -> {
        SearchDoc doc = searchRepo.findById(order.getId());
        if (doc == null) throw new RetryableException("Not indexed yet");
    }, 5, Duration.ofMillis(200));
}
```
