# Debugging: PostgreSQL

## Enable Query Logging
```conf
# postgresql.conf
log_statement = 'all'
log_min_duration_statement = 1000  # log queries > 1 second
log_line_prefix = '%t [%p]: [%l] %u@%d '
log_lock_waits = on
```

## Read Lock Wait Events
```sql
SELECT blocked_locks.pid AS blocked_pid,
       blocking_locks.pid AS blocking_pid,
       blocked_activity.query AS blocked_query,
       blocking_activity.query AS blocking_query
FROM pg_locks blocked_locks
JOIN pg_locks blocking_locks ON blocked_locks.locktype = blocking_locks.locktype
JOIN pg_stat_activity blocked_activity ON blocked_locks.pid = blocked_activity.pid
JOIN pg_stat_activity blocking_activity ON blocking_locks.pid = blocking_activity.pid
WHERE NOT blocked_locks.granted;
```

## Analyze Slow Queries
```sql
EXPLAIN (ANALYZE, BUFFERS, TIMING)
SELECT u.*, count(o.id) AS order_count
FROM users u
LEFT JOIN orders o ON o.user_id = u.id
WHERE u.created_at > '2024-01-01'
GROUP BY u.id;
```

## Using pg_stat_statements
```sql
CREATE EXTENSION IF NOT EXISTS pg_stat_statements;

-- Top 10 queries by total time
SELECT queryid, query, calls,
       total_exec_time / 1000 AS total_seconds,
       mean_exec_time AS avg_ms,
       rows
FROM pg_stat_statements
ORDER BY total_exec_time DESC
LIMIT 10;
```

## Java JDBC Debugging
```java
// Enable PostgreSQL JDBC driver logging
System.setProperty("org.postgresql.Driver", "DEBUG");
// Or via log4j
// log4j.logger.org.postgresql=DEBUG
```

```yaml
# application.yml - SQL logging
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql: TRACE
    com.zaxxer.hikari: DEBUG
```

## Checking Index Usage
```sql
SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read
FROM pg_stat_user_indexes
WHERE idx_scan = 0  -- unused indexes
ORDER BY tablename;
```

## Identifying Bloat
```sql
SELECT schemaname, tablename, n_dead_tup, n_live_tup,
       round(n_dead_tup * 100.0 / GREATEST(n_live_tup, 1), 2) AS dead_pct
FROM pg_stat_user_tables
ORDER BY n_dead_tup DESC;
```

## Connection Pool Leak Detection
```yaml
spring:
  datasource:
    hikari:
      leak-detection-threshold: 60000  # 60 seconds
```
