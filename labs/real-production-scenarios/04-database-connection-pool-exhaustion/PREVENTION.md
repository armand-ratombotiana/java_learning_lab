# Prevention: Avoiding Connection Pool Exhaustion

**Incident**: INC-2024-0715-CONNPOOL
**Category**: Database Connection Management
**Applies To**: All Java services using JDBC connection pools

## Prevention Strategies

### 1. Mandatory Try-With-Resources

All JDBC resource usage MUST use try-with-resources (Java 7+). No manual Connection management is allowed.

```java
// REQUIRED: Try-with-resources for all JDBC operations
public Order findById(String id) {
    String sql = "SELECT * FROM orders WHERE id = ?";
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        // ... safe even if exception occurs
    } catch (SQLException e) {
        throw new DatabaseException("Query failed", e);
    }
}
```

### 2. Code Review Checklist for Connection Management

Every code review MUST check:
- [ ] Is try-with-resources used for Connection?
- [ ] Is try-with-resources used for Statement/PreparedStatement?
- [ ] Is try-with-resources used for ResultSet?
- [ ] If try-with-resources cannot be used (rare), is there a finally block with close()?
- [ ] Is close() called for both success AND exception paths?
- [ ] Are there any `getConnection()` calls without immediate try?

### 3. Static Analysis Rules

Add ErrorProne or Checkstyle rules to detect:
- `DataSource.getConnection()` without corresponding `close()` in finally
- `Connection` variables that are not closed in all code paths
- `Statement`/`PreparedStatement` variables that are not closed
- `ResultSet` variables that are not closed

### 4. HikariCP Configuration Standards

| Parameter | Value | Rationale |
|-----------|-------|-----------|
| `leakDetectionThreshold` | 5000ms (5s) | Detect leaked connections within seconds, not minutes |
| `connectionTimeout` | 5000ms | Fail fast when pool is exhausted |
| `maxLifetime` | 1800000ms (30min) | Slightly less than MySQL wait_timeout |
| `validationTimeout` | 3000ms | Fast connection validation |
| `minimumIdle` | 2 | Keep minimum connections ready |
| `maximumPoolSize` | Calculated per formula | Not arbitrarily large |
| `registerMbeans` | true | Enable JMX monitoring |

### 5. Pool Sizing Formula

```java
/**
 * CONNECTION POOL SIZING GUIDE
 * =============================
 *
 * The formula: pool_size = (T * (C - 1)) + 1
 * where:
 *   T = number of threads (application threads doing DB work)
 *   C = number of database nodes (usually 1 for primary)
 *
 * Simplified rule of thumb:
 *   pool_size = (core_count * 2) + effective_spindle_count
 *
 * For SSD:   spindle_count = 1
 * For HDD:   spindle_count = number of physical drives
 *
 * EXAMPLES:
 *   2-core instance, SSD:    (2*2) + 1 = 5
 *   4-core instance, SSD:    (4*2) + 1 = 9
 *   8-core instance, SSD:    (8*2) + 1 = 17
 *   16-core instance, SSD:   (16*2) + 1 = 33
 *
 * IMPORTANT: More connections ≠ more throughput.
 * Actually, each connection requires a thread on the database server.
 * If you have 600 connections, the DB spends most time context switching.
 *
 * CALCULATION FOR THIS SERVICE:
 *   Instance: m5.large (2 vCPU)
 *   Formula: (2*2) + 1 = 5
 *   With burst buffer: 10
 *   Application instances: 20
 *   Total: 10 * 20 = 200 connections to RDS
 *   RDS max_connections: 200 (exactly right)
 *
 * MONITOR AND ADJUST:
 *   - If pool never goes below minimumIdle → TOO MANY
 *   - If pool frequently hits maximumPoolSize → TOO FEW
 *   - Monitor pool.active, pool.idle, pool.pending
 */
```

### 6. Slow Query Prevention

| Practice | Description |
|----------|-------------|
| Regular index review | Monthly review of slow query log and missing indexes |
| ORM fetch strategy review | Lazy loading should be default; eager only when proven necessary |
| Query analysis in CI/CD | Run EXPLAIN on all queries in test suite |
| Pagination enforcement | All queries returning lists MUST have LIMIT |
| Column selection | Never use SELECT * in production code |

### 7. Monitoring and Alerting

| Metric | Warning | Critical | Action |
|--------|---------|----------|--------|
| Pool Active % | > 60% | > 80% | Investigate connection usage |
| Pool Pending Threads | > 0 for 1 min | > 10 for 30s | Potential pool exhaustion |
| Connection Acquisition Time | > 100ms avg | > 1s avg | Pool nearly exhausted |
| Leak Detection Events | > 0 in 1 hour | > 0 in 5 min | Connection leak in code |
| Slow Queries | > 5/hour | > 10/5min | Missing index or bad query |
| Connection Timeout Count | > 0 in 1 hour | > 10 in 5min | Immediate investigation |

### 8. Education and Training

| Topic | Audience | Frequency |
|-------|----------|-----------|
| Try-with-resources pattern | All Java developers | Onboarding |
| HikariCP configuration workshop | Platform/SRE teams | Annually |
| JDBC resource management | All Java developers | Onboarding |
| Slow query analysis | All developers | Quarterly |
| Connection pool sizing | Platform team | Annually |

### 9. Deployment Verification Checklist

Before every deployment, verify:

- [ ] All new DataSource.getConnection() calls use try-with-resources
- [ ] No new database queries added without index review
- [ ] Connection pool configuration includes leakDetectionThreshold ≤ 5s
- [ ] Connection pool metrics are being collected
- [ ] Slow query log is enabled
- [ ] Load test includes pool exhaustion scenarios

### 10. HikariCP Configuration Template

```yaml
# Standard HikariCP configuration for production services
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
      connection-timeout: 5000
      idle-timeout: 300000
      max-lifetime: 1800000
      leak-detection-threshold: 5000
      validation-timeout: 3000
      connection-test-query: SELECT 1
      auto-commit: true
      transaction-isolation: TRANSACTION_READ_COMMITTED
      register-mbeans: true
```

### 11. Monitoring Dashboard Template

Create a Grafana dashboard with these panels:

- **Pool Utilization**: Gauge showing active/total
- **Connection Acquisition Time**: Heatmap
- **Leak Detection Events**: Counter
- **Connection Timeouts**: Alert threshold
- **Slow Queries**: Top 5 by duration

### 11. Connection Management Training

All developers must complete:
1. **JDBC Connection Lifecycle**: Understand acquire → use → close pattern
2. **Try-with-resources**: Why it is preferred over try-finally
3. **HikariCP configuration**: leakDetectionThreshold, connectionTimeout, maxPoolSize
4. **Pool metrics interpretation**: active, idle, pending, timeout counts
5. **Slow query detection**: How to identify queries that hold connections too long

Training is repeated annually and included in onboarding for new developers.

### 12. Performance Testing Requirements

All services using connection pools must have these load test scenarios:

```java
@Test
void poolShouldNotExhaustUnderPeakLoad() throws Exception {
    ExecutorService exec = Executors.newFixedThreadPool(50);
    List<Future<?>> futures = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
        futures.add(exec.submit(() -> {
            try (Connection conn = dataSource.getConnection()) {
                // Simulate typical query
                Thread.sleep(50);
            }
        }));
    }
    for (Future<?> f : futures) {
        f.get(30, TimeUnit.SECONDS);
    }
    // Pool should return to normal
    assertTrue(dataSource.getConnection().isValid(1));
}
```

### 13. Developer Checklist

Before merging any code that acquires a JDBC connection:

1. ✅ Connection acquired with try-with-resources (not try-finally)
2. ✅ All paths return connection to pool (no early returns before close)
3. ✅ catch block does not swallow exception without closing connection
4. ✅ Connection is not stored in a static/instance field for reuse
5. ✅ Query completes in < 1 second or uses pagination
6. ✅ ORM queries reviewed for N+1 and full table scan

### 14. Connection Health Check Automation

```java
@Scheduled(fixedDelay = 30000)
public void checkConnectionPoolHealth() {
    HikariPoolMXBean poolBean = dataSource.getHikariPoolMXBean();
    int active = poolBean.getActiveConnections();
    int idle = poolBean.getIdleConnections();
    int pending = poolBean.getPendingThreads();
    int total = active + idle;
    
    if (active > total * 0.8) {
        alertService.sendWarning(
            "Connection pool at " + active + "/" + total + " active connections"
        );
    }
    if (pending > 0) {
        alertService.sendCritical(
            pending + " threads waiting for connection from pool"
        );
    }
}
```

### 15. Incident Response Integration

When connection pool alerts fire:

1. Check pool active/idle/pending metrics
2. Check leak detection logs for stack traces
3. Check slow query log for recent entries
4. Kill long-running queries on database
5. If leak detected: roll back recent deployment
6. If slow query: add missing index or kill query
7. If pool exhausted: restart instances (temporary)

## References

- HikariCP Wiki: "About Pool Sizing" — https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing
- Amazon RDS: "Best Practices for Connection Pooling" — AWS Documentation
- Oracle: "JDBC Connection Management" — Oracle Java Tutorials
- PostgreSQL Wiki: "Connection Pooling" — PostgreSQL Documentation
- MySQL: "Connection Handling" — MySQL Documentation
- HikariCP: "Leak Detection" — HikariCP GitHub
- JDBC Specification: "Connection.close() contract" — Oracle JDBC Specification

