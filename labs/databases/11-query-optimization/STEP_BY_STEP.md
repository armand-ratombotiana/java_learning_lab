# Step by Step: Optimizing a Slow Query

## Step 1: Identify the Slow Query
```sql
-- Database logs or pg_stat_statements
SELECT query, calls, total_time, mean_time, rows
FROM pg_stat_statements
ORDER BY total_time DESC
LIMIT 5;
```

## Step 2: Get Execution Plan
```sql
EXPLAIN ANALYZE SELECT o.*, u.name
FROM orders o
JOIN users u ON o.user_id = u.id
WHERE o.status = 'PENDING'
ORDER BY o.created_at DESC;
```

## Step 3: Analyze the Plan
Look for:
- `Seq Scan` on large tables
- `Sort` without index (disk sort)
- High `rows` vs actual mismatch (stale statistics)
- Nested Loop joins on large result sets

## Step 4: Add Missing Index
```sql
CREATE INDEX idx_orders_status_created ON orders(status, created_at DESC);
CREATE INDEX idx_orders_user_id ON orders(user_id);
```

## Step 5: Re-check Plan
```sql
EXPLAIN ANALYZE SELECT ...;  -- Should now show Index Scan
```

## Step 6: Verify in Application
```java
// Before: 500ms
// After: 2ms
long start = System.nanoTime();
List<Order> orders = orderRepository.findByStatus("PENDING");
long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
log.info("Query took {}ms", elapsed);
```
