# Debugging PostgreSQL Issues

## Common Failure Scenarios

### Connection Issues

Applications fail to connect to PostgreSQL, throwing connection refused or timeout errors. This happens when PostgreSQL isn't running, network configuration is wrong, or authentication fails.

The "no pg_hba.conf entry for host" error means the client IP isn't authorized to connect. Check pg_hba.conf allows your IP or use trust/ md5 authentication for development. The "password authentication failed" error indicates wrong credentials—verify the username and password match the database.

Stack trace examples:

```
org.postgresql.util.PSQLException: Connection refused. Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections.
    at org.postgresql.core.ConnectionFactory.connect(ConnectionFactory.java:262)
```

```
org.postgresql.util.PSQLException: FATAL: no pg_hba.conf entry for host "192.168.1.100", user "admin", database "mydb", SSL off
    at org.postgresql.core.v3.ConnectionFactoryImpl.doAuthentication(ConnectionFactoryImpl.java:520)
```

### Query Performance Problems

Slow queries cause high latency and database load. Check for missing indexes, unoptimized query plans, or excessive data retrieval. The EXPLAIN ANALYZE command reveals the actual execution plan and timing.

Large table scans without indexes cause full table scans. If you see "Seq Scan" in EXPLAIN output where "Index Scan" is expected, add an index on the filtered column. When queries use functions or operations on columns, indexes can't be used—consider expression indexes.

Lock contention occurs when transactions hold locks too long or conflicting transactions run simultaneously. The "could not serialize access due to concurrent update" error happens when concurrent transactions modify the same row.

### Stack Trace Examples

**Serialization failure:**
```
org.postgresql.util.PSQLException: ERROR: could not serialize access due to concurrent update
    at org.postgresql.core.v3.QueryExecutorImpl.receiveErrorResponse(QueryExecutorImpl.java:2533)
```

**Deadlock detected:**
```
org.postgresql.util.PSQLException: ERROR: deadlock detected
    at org.postgresql.core.v3.QueryExecutorImpl.receiveErrorResponse(QueryExecutorImpl.java:2533)
    Detail: Process 12345 waits for ShareLock on transaction 67890; blocked by Process 11111.
```

**Connection pool exhausted:**
```
org.hibernate.exception.GenericJDBCException: Unable to acquire JDBC Connection
    at org.hibernate.resource.engine.internal.ScopedSessionImpl$TransactionImpl.begin(ScopedSessionImpl.java:401)
Caused by: org.apache.commons.dbcp2.SQLNestedException: Pool exhausted - cannot acquire connection
```

## Debugging Techniques

### Analyzing Query Performance

Run EXPLAIN ANALYZE on slow queries to see actual execution time. The output shows whether indexes are used, join types, and where time is spent. Look for "Seq Scan" on large tables as a sign of missing indexes.

```sql
EXPLAIN ANALYZE 
SELECT * FROM orders o
JOIN users u ON o.user_id = u.id
WHERE o.created_at > '2024-01-01';

-- Look for:
-- - Seq Scan (indicates no index)
-- - High actual time
-- - Excessive rows examined
```

Check for missing indexes by looking at slow query patterns. Create indexes on foreign keys and frequently filtered columns. Use composite indexes for queries filtering on multiple columns.

```sql
-- Find unused indexes
SELECT indexrelname, idx_scan, idx_tup_read, idx_tup_fetch
FROM pg_stat_user_indexes
WHERE idx_scan = 0
ORDER BY indexrelname;

-- Find slow queries
SELECT query, calls, mean_exec_time, total_exec_time
FROM pg_stat_statements
ORDER by mean_exec_time DESC
LIMIT 10;
```

### Monitoring Connection Pool

Check active connections and waiting threads. Configure pool size to match expected concurrency and PostgreSQL's max_connections limit. Use HikariCP or another connection pool with proper configuration.

```properties
# application.properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

Check PostgreSQL connection counts:

```sql
SELECT count(*), state 
FROM pg_stat_activity 
GROUP BY state;

SELECT datname, numbackends, count(*) as active_connections
FROM pg_stat_database
GROUP BY datname, numbackends;
```

### Lock Debugging

Find blocking queries and long-running transactions:

```sql
-- Find active locks
SELECT 
    pg_blocking_pid(pid) AS blocking_pid,
    pg_blocking_role(pid) AS blocking_role,
    pid, 
    usename, 
    query, 
    state, 
    wait_event_type, 
    wait_event
FROM pg_stat_activity
WHERE state != 'idle'
ORDER BY query_start;

-- Find locks on a table
SELECT l.locktype, l.relation::regclass, l.mode, l.granted, l.pid
FROM pg_locks l
JOIN pg_class c ON l.relation = c.oid
WHERE c.relname = 'orders';
```

Kill blocking processes when needed (emergency only):

```sql
SELECT pg_terminate_backend(pid) 
FROM pg_stat_activity 
WHERE query_start < NOW() - INTERVAL '5 minutes'
AND state = 'idle in transaction';
```

### Transaction Issues

Long-running transactions hold resources and cause locks. Set statement timeout:

```sql
-- Set timeout for current session
SET statement_timeout = '30s';

-- Check long transactions
SELECT pid, usename, state, query, 
       NOW() - query_start AS duration
FROM pg_stat_activity
WHERE state != 'idle'
AND query_start < NOW() - INTERVAL '5 minutes';
```

## Best Practices

Use connection pooling with appropriate sizing. Monitor slow queries and add indexes proactively. Set appropriate statement timeouts to prevent runaway queries. Use prepared statements for frequently executed queries. Implement proper error handling with retry logic for transient failures.