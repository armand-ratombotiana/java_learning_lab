# Common Mistakes: Connection Pooling

## Pool Size Too Large
```properties
# BAD: 100 connections for a 4-core database
spring.datasource.hikari.maximum-pool-size=100
```
**Problem**: Database context switching overhead. More connections ≠ more throughput.
**Fix**: Start with `CPU_cores × 2` and tune up.

## Pool Size Too Small
```properties
# BAD: 1 connection for a production app
spring.datasource.hikari.maximum-pool-size=1
```
**Problem**: Requests queue up, timeouts occur.
**Fix**: Monitor `connections.pending` and increase until < 1 at peak.

## Not Setting Max Lifetime
Some databases (MySQL, PG) drop idle connections after 8 hours. Without `maxLifetime`, the pool serves stale connections.
**Fix**: Set `max-lifetime` shorter than DB's idle timeout.

## Connection Leaks
```java
// BAD: connection not returned to pool
Connection conn = dataSource.getConnection();
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("...");
// conn.close() never called - LEAK!
```
**Fix**: Always use try-with-resources or enable leak detection.

## Using Connection Pool in Batch Jobs
Batch jobs should use connection pools sized for their workload, not share the OLTP pool.
**Fix**: Use separate `DataSource` for batch processing with larger pool.
