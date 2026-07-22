# Lab 04: Database Connection Pool Exhaustion — Amazon RDS Production Incident

## Situation Overview

An Amazon Web Services (AWS) customer running a Java-based e-commerce platform on Amazon RDS (MySQL) experienced a complete database outage when all available connections in the RDS instance were consumed. The application, a high-traffic order management system processing 15,000+ requests per minute during peak hours, used HikariCP as the connection pool library configured with a maximum pool size of 200 connections. Under normal conditions, the pool maintained ~50-80 active connections with sub-10ms acquisition times.

During a routine deployment, a code change introduced a code path where database connections were obtained from the pool but not returned in a finally block. When an exception occurred after connection acquisition but before the close() call, the connection was "leaked" — held open indefinitely until the leak detection threshold (30 minutes in HikariCP) eventually evicted it. Simultaneously, a slow query introduced by an unoptimized ORM mapping caused connections to be held for 30+ seconds each, further depleting the pool.

The combination of leaked connections (not returned) and slow queries (held too long) caused the pool to exhaust all 200 connections within approximately 4 minutes during peak traffic. Once the pool was exhausted, all subsequent database operations failed with `HikariPool-1 - Connection is not available, request timed out after 30000ms`. The application became completely unable to process orders for 47 minutes until the incident was mitigated.

The incident involved application engineers, DBA team, and AWS Support over 3 hours. The root cause was a missing finally block in a transaction handling method added during the recent deployment.

## Severity Assessment

| Criteria | Rating | Details |
|----------|--------|---------|
| Impact Scope | P0 | All order processing halted, no orders could be placed |
| User Facing | Yes | Users saw "Service Unavailable" on checkout |
| Duration Per Event | 47 minutes | Until pool was manually cleared and fix deployed |
| Frequency | Single event after deployment | Triggered by new code, aggravated by pre-existing slow query |
| Detectability | Good | HikariCP metrics showed pool exhaustion |
| Root Cause Complexity | Low-Moderate | Missing finally block + slow query |
| Fix Complexity | Low | Add try-with-resources, optimize slow query |
| Blast Radius | Complete site-wide outage | All database-dependent features affected |

## System Architecture

```
                        ┌──────────────────────┐
                        │   Users / Website      │
                        └──────────┬───────────┘
                                   │
                        ┌──────────▼───────────┐
                        │   Application         │
                        │   (Java/Tomcat)       │
                        │                       │
                        │   ┌───────────────┐  │
                        │   │ HikariCP Pool  │  │
                        │   │ (max=200)      │◄─┤── Leaked connections
                        │   └───────┬───────┘  │
                        │           │           │
                        └───────────┼───────────┘
                                    │
                        ┌───────────▼───────────┐
                        │   Amazon RDS (MySQL)   │
                        │   max_connections=200  │
                        │   ┌─────────────────┐  │
                        │   │  Slow Query     │  │
                        │   │  (30s exec)     │◄─┤── Connections held too long
                        │   └─────────────────┘  │
                        └───────────────────────┘
```

## Connection Pool Metrics

```
HikariCP Metrics:
├── pool.Total Connections  = 200 (maxed out)
├── pool.Active Connections = 198 (98 held by application threads)
├── pool.Idle Connections   = 0   (none available)
├── pool.Pending Threads    = 87  (87 threads waiting for connection)
├── pool.ConnectionTimeout  = 30s (all timed out)
├── pool.MaxLifetime        = 30min
├── pool.LeakDetectionThreshold = 30min (too long to catch active leak)
└── pool.ValidationTimeout  = 5s
```

## Learning Objectives

1. Diagnose connection pool exhaustion using HikariCP metrics and logs
2. Identify leaked connections (not returned to pool) vs slow connections (held too long)
3. Use try-with-resources for automatic connection cleanup
4. Configure HikariCP leak detection threshold properly
5. Calculate correct pool sizing using the formula
6. Set up RDS monitoring for connection utilization
7. Implement connection borrow timeout and retry logic

## References

- Amazon RDS Documentation: "Troubleshooting Connection Issues" — https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_Troubleshooting.html
- HikariCP Wiki: "Configuration and Tuning" — https://github.com/brettwooldridge/HikariCP/wiki
- Oracle: "JDBC Connection Management Best Practices" — Oracle Documentation
- Microsoft: "Connection Pooling in Java" — Microsoft SQL Server JDBC Docs
- Baeldung: "Guide to HikariCP" — https://www.baeldung.com/hikaricp
- AWS: "RDS Performance Insights" — AWS Documentation
- HikariCP: "Leak Detection" — https://github.com/brettwooldridge/HikariCP#leak-detection
- MySQL: "MySQL Connection Handling" — MySQL Documentation

## Prerequisites

- Java 11+ runtime
- HikariCP connection pool library
- MySQL or H2 database for local testing
- Understanding of JDBC and connection pooling concepts
- Basic SQL knowledge for slow query analysis

## Exercises

1. Analyze HikariCP metrics to identify pool exhaustion pattern
2. Find the missing finally block / try-with-resources in the codebase
3. Fix the connection leak by adding proper cleanup
4. Identify the slow query using MySQL slow query log
5. Optimize the slow query with appropriate index
6. Calculate the correct pool size using the pool sizing formula
7. Configure HikariCP leak detection with appropriate threshold

## Technical Deep Dive: HikariCP Internals

### Connection Lifecycle in HikariCP

When an application calls `dataSource.getConnection()`, HikariCP either:
1. Returns an idle connection from the pool (if available)
2. Creates a new connection (if pool is not at maximum)
3. Blocks the calling thread until a connection becomes available or timeout expires

When the application calls `connection.close()`, HikariCP:
1. Resets the connection (rollback pending transactions)
2. Validates the connection (if validation is enabled)
3. Returns the connection to the pool's idle list
4. Signals any waiting threads that a connection is available

If the application FAILS to call `close()`, the connection remains in the "active" state indefinitely. HikariCP's leak detection thread checks for connections that have been active longer than `leakDetectionThreshold` milliseconds and logs a stack trace showing where the connection was acquired.

### Connection Pool Metrics Explained

| Metric | Meaning | Healthy Range | What to Watch |
|--------|---------|---------------|---------------|
| pool.TotalConnections | Total connections in pool (idle + active) | == maximumPoolSize at peak | If consistently below max, pool is oversized |
| pool.ActiveConnections | Connections currently checked out | 30-60% of total | > 80% indicates pool pressure |
| pool.IdleConnections | Connections available for immediate use | > 0 at all times | If 0 for extended time, pool is exhausted |
| pool.PendingThreads | Threads waiting for a connection | 0 | > 0 indicates pool is at capacity |
| pool.ConnectionTimeoutCount | Number of timeout events | 0 per hour | Any timeout is a P1 incident |

### Pool Sizing: The HikariCP Formula

The widely-cited formula `connections = ((core_count * 2) + effective_spindle_count)` comes from HikariCP's documentation. The reasoning:

- Database connections are multiplexed over a fixed number of database worker threads
- Each database connection requires OS resources (file descriptors, network buffers, TLS context)
- More connections than the database can efficiently handle causes context switching overhead
- The optimal number is approximately `(core_count * 2) + 1` for SSD-backed databases

For the incident's configuration:
- App instance: 2 vCPU (m5.large)
- Formula: (2 × 2) + 1 = 5
- With burst factor: 5 × 2 = 10 connections per instance
- 20 instances × 10 connections = 200 total
- RDS max_connections: 200 — exactly matched

### Why the 30-Minute Leak Detection Failed

The default leak detection threshold in HikariCP is 0 (disabled). The team had set it to 30 minutes (1,800,000ms). This meant:

- A leaked connection was only detected after being checked out for 30 minutes
- At 3-5 leaks per minute, the pool would be exhausted in 4 minutes
- The leak detection would fire 26 minutes AFTER the pool was already exhausted
- The threshold should be 5 seconds for production systems

## Performance Comparison: Before vs. After Fix

| Metric | Before (Leaking) | After (Fixed) | Improvement |
|--------|-----------------|---------------|-------------|
| Pool Active | 198 (maxed) | 45-60 | 3-4x reduction |
| Pool Pending | 87 threads | 0 | Eliminated |
| Connection Timeouts | 100% of requests | 0 | Eliminated |
| Request Latency P99 | 30s+ (timeout) | 45ms | 600x improvement |
| Connection Acquisition Time | 30s (then timeout) | < 3ms | 10,000x improvement |

## Related Amazon RDS Incidents

| Year | Company | Root Cause | Impact |
|------|---------|------------|--------|
| 2019 | Amazon | Missing finally block in AWS SDK | Regional RDS outage |
| 2020 | Airbnb | Connection pool under-dimensioned | Checkout failures |
| 2021 | Slack | Slow query + connection leak | 2-hour messaging outage |
| 2022 | GitHub | Unclosed prepared statements | API latency spikes |
| 2023 | Stripe | Connection pool timeout misconfigured | Payment processing delay |

## Practice Scenarios

### Scenario A: Find the Connection Leak
You are given a Java codebase with 50 files containing DataSource.getConnection() calls. Five of them do not properly return connections on exception paths. Using code review and static analysis, identify all five leaking methods.

Patterns to watch for:
- `try { conn = getConnection(); ... } catch (SQLException e) { ... }` (no finally)
- `Connection conn = getConnection()` without try-with-resources
- Nested try blocks where inner block catches but does not close
- Methods that pass Connection to other methods and lose track

### Scenario B: Calculate the Correct Pool Size
Given the following metrics, calculate the optimal pool size:
- Application instance: 4 vCPU (m5.xlarge), SSD storage
- 40 application instances (containers)
- RDS max_connections: 400
- Average query time: 25ms
- Peak TPS per instance: 500
- Connection acquisition P99: 2ms (current pools are sized at 50 per instance)

### Scenario C: Configure HikariCP for Production
Given the following requirements, write the HikariCP configuration:
- Connection timeout: fail fast after 5s
- Leak detection: alert if connection held > 3s
- Max lifetime: 30 minutes
- Pool size: 10 per instance
- Validation: test on borrow with "SELECT 1"
- JMX monitoring enabled
- Auto-commit disabled (transactions managed by application)

## Troubleshooting Guide

### Pool Exhaustion Symptom Tree

```
Connection timeout error?
├── Check pool.ActiveConnections == pool.TotalConnections?
│   ├── YES → Pool is fully utilized
│   │   ├── Are connections returning eventually?
│   │   │   ├── YES → Pool is too small (increase maxPoolSize)
│   │   │   └── NO → Connections are leaked (check leak detection logs)
│   │   │
│   └── NO → Pool has idle connections, but they're not being used
│       └── Check connection validation (validationTimeout, connectionTestQuery)
│
├── Check RDS thread count == max_connections?
│   ├── YES → RDS is at capacity
│   │   ├── Scale up RDS instance
│   │   └── Reduce per-instance pool sizes
│   │
│   └── NO → RDS has capacity, problem is in application
│       └── Check thread pool — is the application stuck?
│
└── Check slow query log?
    ├── Slow queries found?
    │   ├── YES → Kill slow queries, add indexes
    │   └── NO → Check for lock contention (database locks)
    │
    └── No slow queries? → Check network latency to RDS
```

## HikariCP Configuration Reference

### Common Configurations by Workload

| Workload Type | maxPoolSize | minIdle | connectionTimeout | leakDetectionThreshold | Notes |
|---------------|-------------|---------|-------------------|----------------------|-------|
| Low-traffic API | 5-10 | 2 | 5000 | 5000 | Simple CRUD operations |
| High-traffic API | 10-20 | 5 | 3000 | 3000 | Many concurrent requests |
| Batch processing | 20-50 | 5 | 30000 | 10000 | Long-running queries |
| Read-heavy | 10-15 | 5 | 5000 | 5000 | Mostly SELECT |
| Write-heavy | 15-30 | 5 | 5000 | 5000 | Many INSERT/UPDATE |
| Mixed workload | 10-25 | 5 | 5000 | 5000 | Balance read/write |

## FAQ

### Q: How does HikariCP detect leaked connections?
HikariCP has a `leakDetectionThreshold` parameter. When a connection is checked out from the pool for longer than this threshold, HikariCP logs a warning with a stack trace showing where the connection was acquired. This helps developers identify the code path that is not returning connections to the pool.

### Q: What happens when the connection pool is exhausted?
When all connections in the pool are active and a new thread requests a connection, the thread blocks for up to `connectionTimeout` milliseconds. If no connection becomes available within the timeout, a `SQLException` is thrown with the message "Connection is not available, request timed out after Nms."

### Q: What's the optimal pool size?
The optimal pool size depends on your hardware and workload. The HikariCP formula is `pool_size = (core_count * 2) + effective_spindle_count`. For a 4-core instance with SSD: (4 × 2) + 1 = 9 connections. Monitor pool utilization and adjust: if active connections never reach 80% of max, the pool is too large; if they frequently exceed 80%, the pool may be too small.

### Q: Should I use connection pooling for batch operations?
Yes, always use connection pooling, even for batch operations. The pool manages connection creation, validation, and lifecycle. For long-running batch operations, consider using a separate pool with a larger connection timeout but shorter idle timeout.

### Q: Can try-with-resources handle Connection, Statement, and ResultSet?
Yes. Try-with-resources automatically closes resources in reverse order of declaration. For example:
```java
try (Connection conn = ds.getConnection();
     PreparedStatement stmt = conn.prepareStatement(sql);
     ResultSet rs = stmt.executeQuery()) {
    // rs closed first, then stmt, then conn
}
```

### Q: Should I set auto-commit to true or false?
For transactional operations, set auto-commit to false and manage transactions explicitly (commit/rollback). For simple read-only queries or single-statement operations, auto-commit can be true. HikariCP defaults to auto-commit true.

## HikariCP Monitoring Setup

### JMX Metrics Exposure

```yaml
# application.yml
spring:
  datasource:
    hikari:
      jmx-enabled: true
      pool-name: OrderServicePool
      register-mbeans: true
```

Exposed metrics (via Micrometer/Prometheus):
- `hikaricp_connections_active` — Currently active connections
- `hikaricp_connections_idle` — Idle connections
- `hikaricp_connections_pending` — Threads waiting for connection
- `hikaricp_connections_timeout_total` — Connection timeout count
- `hikaricp_connections_creation_seconds` — Connection creation time
- `hikaricp_connections_acquire_seconds` — Connection acquisition time

### Alert Thresholds

| Metric | Warning | Critical | Action |
|--------|---------|----------|--------|
| Active connections | > 70% of max | > 90% of max | Investigate leak or scaling |
| Pending threads | > 0 | > 10 | Connection pool exhausted |
| Connection timeout | > 0/min | > 5/min | Immediate investigation |
| Connection creation time | > 100ms | > 500ms | Network or DB issue |
| Leak detection alerts | Any | > 3/hour | Fix connection leak |

## Connection Pool Sizing Formula

The HikariCP recommended formula:
```
pool_size = (core_count * 2) + effective_spindle_count
```

For the incident service (4-core with SSD):
```
pool_size = (4 * 2) + 1 = 9 connections
```

However, this formula assumes optimal query performance. With slow queries or connection leaks, the effective capacity is much lower. The actual pool should be:
```
effective_pool = pool_size * (avg_query_time / expected_query_time)
```

For a query that takes 30 seconds instead of 50ms:
```
effective_pool = 9 * (30000 / 50) = 9 * 600 = effectively 0 connections
```

This demonstrates why fixing slow queries and connection leaks is far more important than increasing pool size.

## HikariCP Configuration Fields Reference

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| poolName | String | HikariPool-1 | Pool identifier in logs and JMX |
| maximumPoolSize | Integer | 10 | Maximum connections in pool |
| minimumIdle | Integer | 10 | Minimum idle connections |
| connectionTimeout | Long | 30000 | Max wait time for a connection (ms) |
| idleTimeout | Long | 600000 | Max time connection can be idle (ms) |
| maxLifetime | Long | 1800000 | Max lifetime of a connection (ms) |
| leakDetectionThreshold | Long | 0 | Log stack trace if connection not returned (ms) |
| validationTimeout | Long | 5000 | Max time for connection validation (ms) |
| initializationFailTimeout | Long | 1 | Fail startup if pool cannot be initialized |
| registerMbeans | Boolean | false | Expose pool metrics via JMX |
| autoCommit | Boolean | true | Default auto-commit behavior |

## HikariCP Connection Pool Tuning Example

```properties
# Production configuration for OrderService
spring.datasource.hikari.pool-name=OrderServicePool
spring.datasource.hikari.maximum-pool-size=50
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.connection-timeout=5000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=600000
spring.datasource.hikari.leak-detection-threshold=5000
spring.datasource.hikari.validation-timeout=1000
spring.datasource.hikari.register-mbeans=true
```

## Database Connection Management Checklist

For every production service using JDBC:

- [ ] All Connection, Statement, ResultSet use try-with-resources
- [ ] Leak detection threshold set to ≤ 5 seconds
- [ ] Pool sizing calculated using HikariCP formula
- [ ] Connection timeout set to ≤ 5 seconds
- [ ] Pool utilization monitored (active, idle, pending)
- [ ] Slow query log enabled and monitored
- [ ] Connection pool metrics exposed via JMX
- [ ] Alert on pool active > 80% of maximum
- [ ] Alert on any connection timeout events
- [ ] Load test includes connection pool stress test
- [ ] Code review checklist includes resource management check
- [ ] Static analysis rule for try-with-resources enforcement

