# Debugging: SQL Queries

## Tools & Techniques

### EXPLAIN ANALYZE
```sql
EXPLAIN (ANALYZE, BUFFERS, TIMING)
SELECT * FROM orders WHERE customer_id = 123;
```
**Look for**: Seq Scan on large tables, high buffers, large row estimates vs actual.

### Logging Slow Queries

#### PostgreSQL
```ini
# postgresql.conf
log_min_duration_statement = 1000  # log queries > 1 second
log_connections = on
log_disconnections = on
```

#### Spring Boot (JPA)
```yaml
logging.level.org.hibernate.SQL: DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder: TRACE
spring.jpa.properties.hibernate.format_sql: true
```

### Common Performance Issues

| Symptom | Likely Cause | Fix |
|---|---|---|
| Seq Scan on big table | Missing index | Add index |
| Nested Loop on big join | Bad plan / stats outdated | ANALYZE, or set join_collapse_limit |
| High buffer hits | Cache miss | Increase shared_buffers |
| Slow ORDER BY | Sort on disk | Increase work_mem, add index |
| Slow COUNT(*) | Full scan | Use estimated count via index |

### JDBC Debugging

```java
// Enable driver-level logging (PostgreSQL)
// Add to JVM args:
// -Dorg.postgresql.Driver.logLevel=DEBUG
// -Djava.util.logging.config.file=logging.properties
```

### SQL Injection Detection
```java
// Bad: string concat
String sql = "SELECT * FROM users WHERE name = '" + userName + "'";

// Better: parameterized
PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
ps.setString(1, userName);
```

### Deadlock Diagnosis
```sql
-- PostgreSQL: blocked queries
SELECT blocked.pid AS blocked_pid,
       blocked.query AS blocked_query,
       blocking.pid AS blocking_pid,
       blocking.query AS blocking_query
FROM pg_locks blocked
JOIN pg_locks blocking ON blocked.pid != blocking.pid
WHERE NOT blocked.granted;
```
