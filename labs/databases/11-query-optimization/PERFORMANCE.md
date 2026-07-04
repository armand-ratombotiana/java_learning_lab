# Performance: Query Optimization

## Index Strategy

### Single Column Index
```sql
CREATE INDEX idx_orders_status ON orders(status);
```
Best for: Equality conditions on a single column.

### Composite Index
```sql
CREATE INDEX idx_orders_status_date ON orders(status, created_at DESC);
```
Best for: Queries filtering on both columns. Order matters – put equality first, range later.

### Partial Index
```sql
CREATE INDEX idx_active_users ON users(email) WHERE active = true;
```
Best for: Queries that always filter on a common condition.

### Covering Index
```sql
CREATE INDEX idx_users_email ON users(email) INCLUDE (name, avatar_url);
```
Best for: Queries accessing only indexed + included columns (index-only scan).

## Query Rewriting

### Bad Pattern
```sql
SELECT * FROM orders WHERE EXTRACT(YEAR FROM created_at) = 2024;
```
### Good Pattern
```sql
SELECT * FROM orders WHERE created_at >= '2024-01-01' AND created_at < '2025-01-01';
-- Can use index on created_at
```

## JPA-Specific Performance
```java
// BAD: brings all entities to memory
List<Order> orders = orderRepository.findAll();
// GOOD: stream processing
Stream<Order> orders = orderRepository.streamAll();
orders.forEach(order -> processOrder(order));
```

## Work Memory Tuning
```sql
-- Increase sort memory for complex queries
SET work_mem = '64MB';
ALTER SYSTEM SET work_mem = '64MB';
```
