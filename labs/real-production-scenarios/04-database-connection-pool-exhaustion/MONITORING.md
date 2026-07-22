# Monitoring and Alerting: Connection Pool and Database Health

**Incident**: INC-2024-0715-CONNPOOL
**Category**: Database Connection Observability
**Author**: Platform Engineering + DBA Team

## Overview

This document specifies the monitoring, metrics, and alerting rules required to detect connection pool exhaustion, slow queries, and database connectivity issues before they cause production outages.

## Metrics Collection

### 1. HikariCP Pool Metrics (Critical)

```
HikariCP JMX MBeans (com.zaxxer.hikari:type=Pool,*):
├── pool.TotalConnections     — Total connections in pool (idle + active)
├── pool.ActiveConnections    — Connections currently checked out
├── pool.IdleConnections      — Connections available for use
├── pool.PendingThreads       — Threads waiting for a connection
├── pool.ConnectionTimeoutCount — Number of timeout events
├── pool.ConnectionCreationTime  — Time to create a new connection
├── pool.ConnectionAcquisitionTime — Time to acquire from pool
├── pool.MaxLifetimeTime      — Max connection lifetime
├── pool.LeakDetectionTime    — Time threshold for leak detection
└── pool.ThreadsAwaitingConnection — Threads blocked waiting
```

### 2. Database Server Metrics (Critical)

```
Amazon RDS CloudWatch Metrics:
├── DatabaseConnections       — Active connections to RDS
├── CPUUtilization            — RDS CPU usage
├── DatabaseConnectionsMax    — max_connections limit
├── ReadLatency / WriteLatency — Storage latency
├── ReadIOPS / WriteIOPS      — IO operations
├── QueriesPerSecond          — Query throughput
├── SlowQueries               — Queries exceeding slow_query_log_time
├── Deadlocks                 — Deadlock count
└── SwapUsage                 — Memory pressure
```

### 3. Application-Level Database Metrics (High)

```
Custom metrics from application:
├── db.query.time             — Query execution time (histogram)
├── db.query.count            — Queries per second
├── db.connection.acquire.time — Connection acquisition time
├── db.connection.leak.count  — Leak detection events
├── db.transaction.time       — Transaction duration
├── db.error.count            — SQLException count
└── db.retry.count            — Retry attempt count
```

## Alerting Rules

### P0/Critical Alarms (Page On-Call)

| Alert Name | Condition | Duration | Runbook |
|------------|-----------|----------|---------|
| PoolExhaustion | `pool.ActiveConnections >= pool.TotalConnections * 0.95` | 30s | Connection leak or slow query |
| PoolPendingHigh | `pool.PendingThreads >= 10` | 30s | Pool nearly exhausted |
| ConnectionTimeout | `rate(pool.ConnectionTimeoutCount[1m]) > 0` | 0s | Connections cannot be acquired |
| RDSConnectionHigh | `DatabaseConnections >= max_connections * 0.95` | 1m | Scale RDS or optimize pool |

### P1/Warning Alarms (Alert during business hours)

| Alert Name | Condition | Duration | Action |
|------------|-----------|----------|--------|
| PoolUtilizationHigh | `pool.ActiveConnections >= pool.TotalConnections * 0.80` | 2m | Investigate connection usage |
| SlowQueryDetected | `rate(SlowQueries[5m]) > 10` | 5m | Review slow query log |
| ConnectionAcquisitionSlow | `p99(db.connection.acquire.time) > 1000ms` | 5m | Check pool health |
| LeakDetectionFired | `rate(db.connection.leak.count[5m]) > 0` | 5m | Code review for leaked connections |

### P2/Info Alarms (Dashboard / Report)

| Alert Name | Condition | Action |
|------------|-----------|--------|
| PoolShrinking | `pool.TotalConnections < pool.MaximumPoolSize` | Check for connection creation failures |
| QueryTimeIncreasing | `avg(db.query.time) > 2x baseline` for 1 hour | Review query plans |
| ErrorRateElevated | `rate(db.error.count[1h]) > 0` | Investigate root cause |

## Dashboard: Database Connection Health

### Panel 1: Pool Utilization (Gauge)
```sql
SELECT
  pool.ActiveConnections{instance="$instance"}
  / pool.TotalConnections{instance="$instance"} * 100
AS "Pool Utilization %"
FROM hikaricp_metrics
```
Visual: Radial gauge with warning at 60%, critical at 80%

### Panel 2: Pool Activity (Time Series)
```sql
SELECT
  pool.ActiveConnections{instance="$instance"} AS "Active",
  pool.IdleConnections{instance="$instance"} AS "Idle",
  pool.PendingThreads{instance="$instance"} AS "Pending"
FROM hikaricp_metrics
WHERE $__timeFilter
```

### Panel 3: Connection Acquisition Time (Heatmap)
```sql
SELECT
  db.connection.acquire.time{instance="$instance"}
AS "Acquire Time (ms)"
FROM application_metrics
```

### Panel 4: RDS Connection Count (Time Series)
```sql
SELECT
  DatabaseConnections{dbinstance="$db"} AS "Active",
  max_connections{dbinstance="$db"} AS "Max"
FROM rds_metrics
```

## JFR Configuration for Connection Monitoring

```bash
# JFR recording with JDBC events
jcmd <pid> JFR.start name=jdbc_monitoring \
  settings=profile \
  dumponexit=true \
  filename=/data/jfr/jdbc_monitoring.jfr

# Key JFR events for connection analysis:
#   jdk.JDBCConnection — Connection acquire/close events
#   jdk.JDBCStatement — Statement execution events
#   jdk.JDBCResultSet — ResultSet events
#   jdk.SocketRead — Network read time for queries
#   jdk.SocketWrite — Network write time
#   jdk.JavaMonitorEnter — Lock contention on DataSource
```

## Prometheus JMX Exporter Configuration

```yaml
# /etc/jmx_exporter/hikaricp.yaml
rules:
  # HikariCP connection pool metrics
  - pattern: 'com.zaxxer.hikari<name=(\w+(?:-\w+)?)><>(.+): (\w+)'
    name: hikaricp_$2
    labels:
      pool: "$1"
    type: GAUGE
    attrNameSnakeCase: true

  # Specifically match ActiveConnections, IdleConnections, etc.
  - pattern: 'com.zaxxer.hikari<name=(\w+(?:-\w+)?)><>(\w+)'
    name: hikaricp_$2
    labels:
      pool: "$1"
    type: GAUGE
```

## Slow Query Monitoring

```sql
-- MySQL slow query log configuration
SET GLOBAL slow_query_log = ON;
SET GLOBAL slow_query_log_file = '/var/log/mysql/slow-queries.log';
SET GLOBAL long_query_time = 1;  -- Log queries taking > 1 second
SET GLOBAL log_queries_not_using_indexes = ON;

-- Analyze slow query log with pt-query-digest
-- pt-query-digest /var/log/mysql/slow-queries.log

-- Check currently running queries
SHOW FULL PROCESSLIST;

-- Check for long-running transactions
SELECT * FROM information_schema.innodb_trx
WHERE TIME_TO_SEC(TIMEDIFF(NOW(), trx_started)) > 10;
```

## Automated Response

When connection pool alerts fire:

1. **Identify the leak**: Check `pool.LeakDetectionTime` events for leaked connection stack traces
2. **Kill stuck threads**: If connections are held for > 30s, trace to the application thread
3. **Rollback deployment**: If recent deployment introduced the leak, roll back
4. **Kill slow queries**: Use `SHOW FULL PROCESSLIST` + `KILL QUERY <id>`
5. **Scale up**: Increase pool size temporarily (if RDS can handle more connections)
6. **Restart application**: Last resort to release all connections

## Implementation Guide

### Step 1: Enable HikariCP JMX Metrics

Add the following to your HikariCP configuration:

```yaml
# HikariCP JMX configuration
registerMbeans: true
```

This exposes all pool metrics via JMX under `com.zaxxer.hikari:type=Pool (name=...)`.

### Step 2: Configure Prometheus JMX Exporter

```yaml
# /etc/jmx_exporter/hikari.yaml
rules:
  - pattern: 'com.zaxxer.hikari<name=(\w+)><>(\w+)'
    name: hikaricp_$2
    labels:
      pool: "$1"
    type: GAUGE
```

### Step 3: Create Alerts

```yaml
groups:
  - name: hikaricp_alerts
    rules:
      - alert: HikariCPPoolExhaustion
        expr: hikaricp_ActiveConnections / hikaricp_TotalConnections > 0.95
        for: 30s
        labels: { severity: critical }
      - alert: HikariCPConnectionTimeout
        expr: rate(hikaricp_ConnectionTimeoutCount[5m]) > 0
        for: 0s
        labels: { severity: critical }
```

### Step 4: Set Up Database Health Dashboard

Create a Grafana dashboard with four rows:

1. Connection pool health (gauges and time series)
2. RDS instance metrics (connections, CPU, IOPS)
3. Slow query analysis (top queries by time)
4. Application error rates (timeouts, SQL exceptions)

## References

- Prometheus: "JMX Exporter for HikariCP" — https://github.com/prometheus/jmx_exporter
- Amazon RDS: "Monitoring RDS Performance" — AWS Documentation
- MySQL: "Slow Query Log" — MySQL Documentation
- Grafana: "HikariCP Dashboard" — Grafana Dashboard Repository
- HikariCP: "Metrics and Monitoring" — HikariCP GitHub Wiki
- Amazon CloudWatch: "RDS Enhanced Monitoring" — AWS Documentation

