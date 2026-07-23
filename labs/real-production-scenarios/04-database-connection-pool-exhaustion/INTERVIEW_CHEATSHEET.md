# Interview Cheatsheet: Connection Pool Exhaustion

## Key Diagnostic Commands
- `SHOW STATUS LIKE 'Threads_connected'` — current DB connections
- `SHOW PROCESSLIST` — running queries
- `netstat -an | grep :3306` — connections from app to DB
- HikariCP JMX metrics: `ActiveConnections`, `PendingThreads`, `IdleConnections`
- Application logs: "Connection is not available, request timed out"
- `lsof -i :3306` — check who's connected

## Common Metrics to Check
- Active connections vs. max pool size
- Connection wait time (HikariCP: `pool.Wait` metric)
- Connection idle timeout (should be near zero in steady state)
- Requests pending for connection
- Database max_connections
- Slow query count (long-running queries hold connections)

## Typical Root Causes
- Connection not returned (missing close() in finally)
- Slow queries holding connections for seconds
- Transaction not committed/rolled back
- DB max_connections < pool size × app instances
- Network latency to DB (long hold time)
- Leak due to exception before connection close

## Interview Question Patterns
- "What happens when all connections in the pool are in use?"
- "How do you diagnose connection pool exhaustion?"
- "Design a connection pool health monitoring system"
- "How does try-with-resources prevent connection leaks?"
- "Compare HikariCP, Tomcat CP, DBCP2"

## STAR Story Template
**S**: 50% of API calls timing out during peak, "Connection not available" errors
**T**: Diagnose and fix connection pool exhaustion
**A**: Found connection leak in new feature — missing close() in exception path, added try-with-resources, set pool validation
**R**: Errors resolved, added pool monitoring dashboard
