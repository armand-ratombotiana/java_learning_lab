# Production Scenarios: Connection Pooling (Oracle Focus)

## Scenario 1: HikariCP Pool Exhausted — Connection Leak
**Context**: A Spring Boot application with HikariCP connecting to Oracle under production load.
**Problem**: After 4 hours of operation, all HTTP requests timed out. HikariCP metrics showed `Active: 50, Idle: 0, Pending: 200`. No connections were being returned to the pool.
**Root Cause**: A code path in the service layer created a JDBC `Connection` using `DataSource.getConnection()` but did not close it in a `finally` block or try-with-resources. When a specific error occurred in that code path, the `Connection.close()` was skipped. Connections leaked at a rate of 5 per minute.
**Solution**: 1) Enabled HikariCP leak detection: `leakDetectionThreshold=30000` (30 seconds). 2) Found the leak in logs: "Connection leak detection: connection has been active for 30000ms". 3) Fixed the code: wrapped all connections in try-with-resources. 4) Reduced `maximumPoolSize` to 30 to fail fast when leaks occur. 5) Set up alerting on `hikaricp_connections_active` when > 80% of max.
**Lessons Learned**: Always use try-with-resources for JDBC connections. Enable leak detection in development and staging. Monitor connection pool metrics in production. Fail fast rather than queue indefinitely.

## Scenario 2: DRCP + HikariCP Double Pooling
**Context**: Oracle DRCP (Database Resident Connection Pooling) was enabled on the database side.
**Problem**: Application performance degraded over time. AWR showed high `latch: shared pool` and `cursor: pin S wait on X` waits. Pooled connections were slow.
**Root Cause**: HikariCP was pooling connections on the application side, and DRCP was pooling them again on the database side. This double pooling caused unnecessary context switching and latch contention. Each "acquired" connection went through two pool management layers.
**Solution**: 1) Disabled DRCP for the application's connection pool: altered the service to use dedicated server processes instead. 2) Or disabled HikariCP pooling and used DRCP directly with a minimal connection pool. 3) Used `oracle.jdbc.DRCPConnectionPool` directly for DRCP-aware pooling. 4) Monitored `V$CPOOL` to verify DRCP usage. 5) For high-throughput OLTP, chose one pooling layer and disabled the other.
**Lessons Learned**: Avoid double pooling — use either application-side (HikariCP) or database-side (DRCP) pooling. HikariCP is preferred for most Java applications. DRCP is useful for PHP/Python apps that cannot pool at the application tier.

## Scenario 3: RAC Failover — Connections Point to Failed Node
**Context**: An Oracle RAC cluster with 2 nodes. Node 1 failed unexpectedly.
**Problem**: After Node 1 failure, all new connection attempts failed with "ORA-12505: TNS:listener does not currently know of SID given in connect descriptor". The application could not connect to the surviving Node 2.
**Root Cause**: The JDBC URL was configured with `jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=node1)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=ORCL)))`. This connected only to Node 1. There was no failover configuration — the URL did not include Node 2.
**Solution**: 1) Changed JDBC URL to use Oracle RAC addressing with failover: `jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=node1)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=node2)(PORT=1521))(LOAD_BALANCE=ON)(FAILOVER=ON)(CONNECT_DATA=(SERVICE_NAME=ORCL)))`. 2) Set `oracle.jdbc.ReadTimeout` to 5000ms to detect node failure faster. 3) Configured HikariCP `connectionTestQuery=SELECT 1 FROM DUAL` for validation. 4) Added ONS (Oracle Notification Service) for fast RAC event detection. 5) Tested failover scenarios in lower environments.
**Lessons Learned**: Always configure JDBC URLs with all RAC nodes. Use FAILOVER=ON and LOAD_BALANCE=ON. Configure short connection timeout. Test RAC failover scenarios regularly.

## Scenario 4: Connection Validation Causing 1M Queries/Day
**Context**: HikariCP was configured with `connectionTestQuery=SELECT 1 FROM DUAL`.
**Problem**: The database AWR report showed "SELECT 1 FROM DUAL" as the #1 SQL by executions — 1M times in 24 hours. The validation queries were consuming 5% of total database CPU.
**Root Cause**: HikariCP was validating connections on every borrow (`connectionTestQuery` runs before each `getConnection()` call). With 100 TPS and pool size 50, every connection was validated before each use. The validation ran even for connections that were just returned to the pool.
**Solution**: 1) Changed to `validationTimeout=1000` and relied on HikariCP's `keepaliveTime=60000` (validate every 60 seconds). 2) Disabled per-borrow validation: `connectionTestQuery` removed, rely on idle timeout and keepalive. 3) Used `leakDetectionThreshold` instead of per-borrow validation. 4) For Oracle, used `connectionInitSql=SELECT 1 FROM DUAL` (runs once per new connection). 5) Monitored pool validation rate to confirm reduction.
**Lessons Learned**: Avoid per-borrow connection validation for high-throughput apps. Use `keepaliveTime` for periodic validation instead. Use `connectionInitSql` for single-shot validation at connection creation. Monitor database impact of validation queries.
