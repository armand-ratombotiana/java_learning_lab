# Debugging: Relational Databases

## Common Issues & Diagnosis

### 1. Slow Queries
```sql
-- Check query plan
EXPLAIN ANALYZE SELECT ... ;
```
**Symptoms**: Seq Scan on large table, missing indexes, bad join order.

### 2. Deadlocks
```sql
-- PostgreSQL: view blocked queries
SELECT pid, wait_event, query FROM pg_stat_activity WHERE wait_event IS NOT NULL;
```
**Solution**: Ensure consistent lock ordering across transactions.

### 3. Connection Leaks
```java
// Problem: connection not closed
Connection conn = dataSource.getConnection();
// ... work ...
// conn.close() never called → pool exhaustion

// Fix: try-with-resources
try (Connection conn = dataSource.getConnection()) {
    // ... work ...
}
```

### 4. JPA LazyInitializationException
```java
// Problem: accessing lazy association outside transaction
// Session/transaction already closed

// Fix 1: JOIN FETCH
// Fix 2: @Transactional on service method
// Fix 3: Open Session in View (anti-pattern, use cautiously)
```

### 5. Constraint Violations
```sql
-- PostgreSQL: disable triggers temporarily (DDL only)
SET session_replication_role = replica;
-- ... data fix ...
SET session_replication_role = origin;
```

### 6. MVCC Bloat
```sql
-- Check table bloat
SELECT schemaname, tablename, n_live_tup, n_dead_tup
FROM pg_stat_user_tables
WHERE n_dead_tup > 1000;
```
**Fix**: `VACUUM ANALYZE;`

## JDBC Debugging Flags
```java
// Enable driver logging
System.setProperty("org.jboss.logging.provider", "slf4j");
// Hibernate SQL logging
// spring.jpa.show-sql=true
// spring.jpa.properties.hibernate.format_sql=true
```
