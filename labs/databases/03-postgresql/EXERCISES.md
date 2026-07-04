# Exercises: PostgreSQL

## Exercise 1: CRUD with JDBC
```java
// Implement:
public class UserRepository {
    public User findById(long id);
    public List<User> findByEmailDomain(String domain);
    public User create(User user);
    public void updateEmail(long id, String email);
    public void delete(long id);
}
```

## Exercise 2: JSONB Operations
- Create a table `events` with a JSONB column `data`
- Insert events with nested JSON
- Query using `@>`, `->`, `->>`, `#>` operators
- Create a GIN index and compare query performance

## Exercise 3: Full-Text Search
```sql
-- Create a documents table with tsvector
-- Implement a search endpoint that returns ranked results
-- Test with different tsquery operators: &, |, !, <->

CREATE TABLE documents (
    id SERIAL PRIMARY KEY,
    title TEXT,
    body TEXT,
    search_vector tsvector
);
```

## Exercise 4: Index Optimization
1. Create a table with 1M rows
2. Create different index types (B-tree, GIN, BRIN, partial)
3. Run EXPLAIN ANALYZE for each
4. Compare: seq scan, index scan, bitmap index scan, index-only scan

## Exercise 5: Connection Pooling with HikariCP
```java
// Configure HikariCP with monitoring
// Implement connection leak detection
// Monitor pool metrics with Micrometer
```

## Exercise 6: WAL and Point-in-Time Recovery
1. Enable WAL archiving
2. Perform a base backup with `pg_basebackup`
3. Insert data, then simulate a failure
4. Recover to a specific point in time

## Exercise 7: Partitioning
```sql
-- Partition the orders table by month
-- Insert data across partitions
-- Query with partition pruning
-- Compare performance vs non-partitioned table
```

## Exercise 8: Row-Level Security
```sql
-- Create a multi-tenant application
-- Enable RLS on shared tables
-- Implement tenant isolation policies
-- Test cross-tenant access prevention
```

## Exercise 9: Replication Setup
1. Set up streaming replication (primary + standby)
2. Configure synchronous replication
3. Test failover and switchover
4. Configure read replicas for query offloading

## Exercise 10: Performance Tuning
1. Use `pg_stat_statements` to find top 5 slow queries
2. Create appropriate indexes
3. Compare before/after with EXPLAIN ANALYZE
4. Tune `shared_buffers`, `work_mem`, `effective_cache_size`
