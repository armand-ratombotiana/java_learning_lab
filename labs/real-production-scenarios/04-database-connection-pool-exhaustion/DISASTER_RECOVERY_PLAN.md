# Lab 04 — Connection Pool Exhaustion: Disaster Recovery Plan

## Recovery Objectives

| Metric | Target |
|--------|--------|
| RTO | 5 minutes (kill slow queries + rollback) |
| RPO | 0 (no data loss for committed transactions) |
| MTD | 15 minutes before SLA breach |

## Scenarios

### Scenario A: Connection Pool Exhaustion (Immediate)

**Trigger:** Pool active reaches 100%, pending threads > 0
**Impact:** All database-dependent operations fail
**Recovery:**
1. Kill slow queries on RDS: `CALL mysql.rds_kill_query(query_id)`
2. Restart HikariCP connection pool (Spring Actuator / JMX operation)
3. Identify leaked connections via leakDetectionThreshold logs
4. Fix the code path that is not returning connections
5. Deploy fix

### Scenario B: Database max_connections Limit Reached

**Trigger:** RDS max_connections = 200 and all in use
**Impact:** No new connections to database
**Recovery:**
1. Kill idle connections: `SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE state = 'idle'`
2. Reduce per-instance pool sizes
3. Add RDS Proxy between app and database
4. Scale up RDS instance (increase max_connections)

### Scenario C: Slow Query Flooding

**Trigger:** Multiple long-running queries holding connections
**Impact:** Pool active > 90% with all connections doing slow queries
**Recovery:**
1. Identify slow queries: SHOW FULL PROCESSLIST
2. Kill the slowest/queries with highest impact
3. Add missing index (temporary)
4. Deploy index permanently via migration
5. Monitor query performance

## Runbook

```yaml
symptoms:
  - "HikariPool timeout errors"
  - "Active connections = maxPoolSize"
  - "Pending threads > 0"

diagnosis:
  - "Check HikariCP metrics: spring.datasource.hikari.jmx-enabled=true"
  - "Check MySQL processlist: SHOW FULL PROCESSLIST"
  - "Check leak detection logs: 'Connection leak detection'"
  - "Check slow query log: /var/log/mysql/slow-queries.log"

immediate_mitigation:
  - "Kill long-running queries: CALL mysql.rds_kill_query(N)"
  - "Restart connection pool: JMX operation or restart service"
  - "Rollback recent deployment if correlated"

fix:
  - "Add try-with-resources for all Connection/Statement/ResultSet"
  - "Optimize slow queries with indexes"
  - "Set leakDetectionThreshold to 5000ms"
  - "Enable HikariCP JMX monitoring"
```
