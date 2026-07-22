# Production Scenarios: PostgreSQL (Oracle Comparison)

## Scenario 1: Transaction ID Wraparound Approaching
**Context**: A PostgreSQL database that had been running for 2 years without maintenance.
**Problem**: Alert: "Transaction ID wraparound: 100 million transactions remaining before shutdown". The database was at risk of shutting down to prevent data loss.
**Root Cause**: Autovacuum was not configured properly. The `autovacuum_naptime` was set too high. The `vacuum_freeze_table_age` was at default 150M. Some tables had not been vacuumed in 12 months. The transaction ID counter was approaching the wraparound limit (2^31).
**Solution**: 1) Immediately ran `VACUUM FREEZE` on the most critical tables to mark old transaction IDs as frozen. 2) Lowered `autovacuum_freeze_max_age` to 500M for early activation. 3) Decreased `autovacuum_naptime` to 30 seconds. 4) Increased `autovacuum_vacuum_cost_limit` to speed up vacuum. 5) Set up monitoring for `SELECT datname, age(datfrozenxid) FROM pg_database`.
**Lessons Learned**: Monitor transaction ID age on all databases. Configure autovacuum proactively for wraparound prevention. Set up alerts at 500M remaining transactions. Schedule `VACUUM FREEZE` during maintenance windows for high-update tables.

## Scenario 2: PgBouncer Connection Pool Exhausted
**Context**: A Java application using PgBouncer for connection pooling experienced connection timeouts.
**Problem**: The application threw `org.postgresql.util.PSQLException: Connection refused to server`. PgBouncer logs showed "pool_size exhausted".
**Root Cause**: A code defect caused connection leaks: SQL connections were opened but not closed in a `finally` block. PgBouncer's `default_pool_size` was 20. After a traffic spike, all 20 connections were held by long-running queries and never returned.
**Solution**: 1) Restarted PgBouncer to clear all connections temporarily. 2) Fixed the Java code: wrapped JDBC calls in try-with-resources. 3) Increased `default_pool_size` to 100. 4) Set `server_idle_timeout` to 300 seconds to close idle connections. 5) Added connection leak detection: `pgbouncer SHOW STATS` monitoring and `V$SESSIONS` alerting.
**Lessons Learned**: Monitor connection pool utilization. Fix connection leaks in application code. Use connection timeout and idle timeout settings. Implement automated pool exhaustion alerts.

## Scenario 3: Streaming Replication Lag
**Context**: A reporting application reads from PostgreSQL read replicas for real-time dashboards.
**Problem**: Reports showed data that was 30 minutes stale. The application read from a replica that was lagging behind the primary by 30 minutes.
**Root Cause**: The replica was applying WAL at 5MB/s but the primary was generating WAL at 8MB/s. A long-running ANALYZE on the replica was consuming I/O. The `max_wal_senders` was at default 10 but only 5 replicas were configured.
**Solution**: 1) Queried `SELECT * FROM pg_stat_replication` to identify lag. 2) Stopped the ANALYZE job on the replica. 3) Increased `max_wal_senders` to 20. 4) Upgraded replica to same CPU/memory as primary. 5) Implemented `pg_stat_replication` monitoring with alerting at 60-second lag.
**Lessons Learned**: Monitor replication lag in real-time. Ensure replicas have sufficient I/O capacity. Avoid heavy maintenance on replicas during peak hours. Alert on lag exceeding acceptable threshold.

## Scenario 4: PostgreSQL vs Oracle Query Plan Regression
**Context**: A query migrated from Oracle to PostgreSQL ran 100x slower.
**Problem**: `EXPLAIN ANALYZE` showed PostgreSQL was using a nested loop join with 10M iterations instead of a hash join. The same query in Oracle used hash join and completed in 2 seconds.
**Root Cause**: PostgreSQL's statistics were not updated after data load. The `default_statistics_target` was 100 (default). The join column had a skewed distribution. PostgreSQL chose a nested loop because it underestimated the number of matching rows.
**Solution**: 1) Ran `ANALYZE` on all tables involved in the query. 2) Increased `default_statistics_target` to 1000 for the skewed column. 3) Set `enable_nestloop = off` temporarily to force hash join. 4) Rewrote the query with explicit join hints: `/*+ HashJoin(t1 t2) */`. 5) Enabled `auto_explain` module to log slow queries with plans.
**Lessons Learned**: Update statistics after data loads. Increase statistics target for skewed columns. Use `auto_explain` for slow query logging. Test migrated queries with representative data volumes.
