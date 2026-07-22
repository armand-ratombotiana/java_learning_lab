# Incident Report: RDS Connection Pool Exhaustion

**Incident ID**: INC-2024-0715-CONNPOOL
**Severity**: P0 (SEV-1)
**Date**: July 15, 2024
**Affected Service**: Order Management System
**Duration**: 47 minutes (total outage) + 3 hours (full resolution)
**Detection**: HikariCP pool exhaustion log + automated alert

## Executive Summary

On July 15, 2024, the order management system experienced a complete database connectivity outage lasting 47 minutes. A recent deployment introduced a code path where database connections were not returned to the HikariCP pool when exceptions occurred after connection acquisition. This caused connections to leak at a rate of approximately 3-5 per minute during peak traffic. Simultaneously, a pre-existing slow query (introduced two weeks earlier by an ORM mapping change) held connections for 30+ seconds each, further reducing pool capacity. At 15,000 requests per minute, the pool of 200 connections was exhausted in approximately 4 minutes. All subsequent requests failed with connection timeout errors. The fix involved adding try-with-resources to the leaking code path, optimizing the slow query with a new index, and reducing HikariCP's leak detection threshold from 30 minutes to 5 minutes.

## Timeline (All Times UTC)

### Day 1 — July 15

| Time | Event |
|------|-------|
| 13:45 | New deployment (v2.7.1) rolled out to 100% of instances |
| 13:48 | First connections leaked by new code path |
| 13:52 | Pool utilization rises from 50 to 120 active connections |
| 13:55 | Pool utilization reaches 180 active connections |
| 13:56 | First connection timeout errors: "Connection is not available, request timed out after 30000ms" |
| 13:57 | PagerDuty alert: "HikariCP Pool Exhaustion" — 0 idle connections, 87 pending threads |
| 13:58 | On-call acknowledges. Service completely unable to process orders |
| 14:00 | Engineer checks HikariCP metrics: 198 active, 0 idle, 87 pending |
| 14:02 | Check RDS: 200 connections active, CPU at 30%, no database-side issue |
| 14:05 | Check application logs: connection timeout errors on all database operations |
| 14:08 | Initial hypothesis: traffic spike. Check request rate: normal |
| 14:10 | Check HikariCP leak detection logs: "Connection leak detection triggered" |
| 14:12 | HikariCP log shows 3 connections marked as leaked (but 30-min threshold means recent leaks not yet detected) |
| 14:15 | Escalate to incident commander |

### Mitigation Phase

| Time | Event |
|------|-------|
| 14:20 | Decision: restart application instances to release all connections |
| 14:22 | Rolling restart of all 20 application instances |
| 14:25 | Pool resets to 0 active connections for each instance |
| 14:27 | Connections rapidly acquired again (same code path causing leaks) |
| 14:30 | Pool exhaustion returns within 3 minutes |
| 14:32 | Second restart attempt — same result |
| 14:35 | Decision: roll back deployment to v2.7.0 |
| 14:40 | Rollback initiated |
| 14:45 | Pool stabilizing after rollback |
| 14:52 | Pool returns to normal: 60 active connections |
| 15:00 | Service fully recovered. Incidents downgraded to P1 |

### Root Cause Analysis

| Time | Event |
|------|-------|
| 15:00 | Begin code review of changes between v2.7.0 and v2.7.1 |
| 15:15 | Identified new method: `OrderService.processBulkOrders()` |
| 15:20 | Code review shows: Connection obtained via `dataSource.getConnection()`, no try-finally |
| 15:25 | Slow query identified: `SELECT o.*, oi.*, p.* FROM orders o JOIN order_items oi ON o.id = oi.order_id JOIN products p ON oi.product_id = p.id WHERE o.status = 'PENDING' ORDER BY o.created_at DESC` |
| 15:30 | Missing index: `orders(status, created_at)` — causing full table scan on 5M rows |
| 15:35 | Combined effect: leaks consumed connections while slow query held remaining connections longer |
| 16:00 | Fix design: add try-with-resources to processBulkOrders |
| 16:15 | Fix design: add composite index on orders(status, created_at) |
| 16:30 | Fix design: reduce HikariCP leakDetectionThreshold from 30min to 5s |
| 16:45 | Fix implemented and tested in staging |
| 17:00 | Deploy v2.7.2 (fix only, on top of v2.7.1) to canary |
| 17:30 | Canary healthy. Full rollout |
| 17:45 | Incident declared resolved |

## Key Findings

1. **Root Cause**: Missing try-with-resources in `OrderService.processBulkOrders()` caused connection leak on exception
2. **Aggravating Factor**: Slow query held connections for 30+ seconds, depleting pool faster
3. **Missed in Code Review**: New method was not reviewed for resource cleanup
4. **Missed in Monitoring**: Leak detection threshold was 30 minutes — too slow to catch active leaks

## Action Items

| # | Action | Owner | Status |
|---|--------|-------|--------|
| 1 | Add try-with-resources to all Connection, Statement, ResultSet usage | Platform Team | Done |
| 2 | Add HikariCP leak detection metric alerts | SRE Team | Done |
| 3 | Reduce leakDetectionThreshold to 5 seconds | Platform Team | Done |
| 4 | Add slow query monitoring and alerting | DBA Team | Done |
| 5 | Add index on orders(status, created_at) | DBA Team | Done |
| 6 | Add connection leak tests to CI/CD pipeline | QA Team | Done |

## Detailed Impact Assessment

### Customer Impact
- Total outage duration: 47 minutes
- Affected customers: All users attempting to place orders, view order history, or process returns
- Failed transactions: Approximately 705,000 failed order attempts (15,000 TPM × 47 minutes)
- Revenue impact: All orders lost during the outage window; estimated at $235,000 based on average order value

### Engineering Impact
- 3 engineers involved in incident response
- 2 rollback attempts before root cause identified
- 1 database index created to fix slow query
- ~45 engineering hours spent on investigation, fix, and postmortem

### Timeline of Connection Pool Exhaustion

The pool exhaustion followed a predictable pattern:

```
Minute 0 (13:45): Deployment v2.7.1 rolled out. Pool: 50 active, 150 idle.
Minute 1 (13:46): First connections leaked. Pool: 55 active, 145 idle.
Minute 2 (13:47): More leaks + first slow query execution. Pool: 65 active, 135 idle.
Minute 3 (13:48): Leaks accelerating. Pool: 80 active, 120 idle.
Minute 4 (13:49): Slow query saturating. Pool: 100 active, 100 idle.
Minute 5 (13:50): Pool crossing threshold. Pool: 130 active, 70 idle.
Minute 6 (13:51): First pending threads appear. Pool: 155 active, 45 idle, 5 pending.
Minute 7 (13:52): Pool nearly exhausted. Pool: 180 active, 20 idle, 25 pending.
Minute 8 (13:53): First connection timeouts. Pool: 195 active, 5 idle, 50 pending.
Minute 9 (13:54): Pool completely exhausted. Pool: 198 active, 0 idle, 87 pending.
Minute 10+ (13:55+): All requests fail with timeout. Service unavailable.
```

### Detailed Code Analysis of the Leak

The problematic code was in `OrderService.java`, added in commit `a3b7c9d`:

```java
// Lines 142-168 — the leaking code path
public void processBulkOrders(List<String> orderIds) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
        conn = dataSource.getConnection(); // Line 147: acquire from pool
        stmt = conn.prepareStatement("UPDATE orders SET processed=1 WHERE id=?");
        for (String orderId : orderIds) {
            stmt.setString(1, orderId);
            stmt.executeUpdate(); // Line 152: may throw SQLException
        }
    } catch (SQLException e) {
        log.error("Failed to process order " + orderId, e);
        // Line 155: BUG — no conn.close() here!
        // Connection leaked from pool
    }
    // Line 157: BUG — no finally block
    // conn is never closed on exception path
}
```

The fix was a single-line change replacing the try-catch with try-with-resources:

```java
// Lines 142-152 — the fixed code
public void processBulkOrders(List<String> orderIds) {
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement("UPDATE orders SET processed=1 WHERE id=?")) {
        for (String orderId : orderIds) {
            stmt.setString(1, orderId);
            stmt.executeUpdate();
        }
    } catch (SQLException e) {
        log.error("Failed to process order", e);
        // Connection auto-closed by try-with-resources
    }
}
```

## Post-Incident Code Audit Results

The team audited all 127 files containing DataSource.getConnection() calls:

| Pattern | Count | Status |
|---------|-------|--------|
| Try-with-resources (correct) | 89 | Already correct |
| Manual try-finally (correct) | 12 | Already correct |
| Try-catch without finally (BUG) | 3 | Fixed immediately |
| getConnection() without try (BUG) | 1 | Fixed immediately |
| Chained operations without try | 22 | No fix needed (utility methods) |

## Incident Response Timeline

| Time | Action | Result |
|------|--------|--------|
| 14:08 | Monitoring alert: OrderService p99 latency > 30s | Investigation begins |
| 14:12 | HikariCP pool metrics show 0 available connections | Connection pool exhaustion confirmed |
| 14:15 | Thread dumps show 87 threads waiting for connection | Root cause direction: connection leak |
| 14:18 | HikariCP leak detection logs captured | Stack traces point to OrderService line 142 |
| 14:22 | Restart #1 attempted | Pool recovers for 3 minutes, then exhausts again |
| 14:30 | Restart #2 attempted | Same result: exhausted in 3 minutes |
| 14:40 | Rollback to v2.7.0 | Pool stabilizes immediately |
| 14:47 | Incident resolved | P0 duration: 39 minutes |
| 15:00 | Root cause identified: missing finally block | Fix deployed to v2.7.2 |
| 15:30 | Code audit of 105 files completed | 7 additional leaks found and fixed |

## SLA Impact Analysis

| Metric | Target | During Incident | Degradation |
|--------|--------|-----------------|-------------|
| Order Processing P99 | < 500ms | 30,000ms (timeout) | 60x |
| Service Availability | 99.95% | 96.7% | 3.25% below target |
| Error Rate | < 0.5% | 100% (during peak exhaustion) | 200x |
| Orders Lost | < 0.01% | ~0.5% estimated | 50x |
| MTTR | < 30 min | 47 min | 1.6x |

### Financial Impact
- Attempted orders during outage: ~705,000
- Confirmed lost orders: ~12,000
- Average order value: $33.50
- Direct revenue impact: ~$402,000
- Customer goodwill credits issued: ~$18,000
- Engineering time cost: ~$45,000
- Total estimated impact: ~$465,000

### Root Cause Classification
- **Type**: Resource Exhaustion / Connection Leak
- **Category**: JDBC Connection Management
- **CWE**: CWE-404 (Improper Resource Shutdown), CWE-772 (Missing Release of Resource)
- **Severity**: P0 / SEV-1

## Post-Incident Database Audit

After the incident, 105 files containing database interactions were audited:

| Component | Files Checked | Violations | Fixed |
|-----------|--------------|------------|-------|
| Repository classes | 42 | 3 | 3 |
| Service classes | 31 | 2 | 2 |
| Batch processors | 8 | 1 | 1 |
| Utility classes | 12 | 0 | 0 |
| SQL mappers | 8 | 0 | 0 |
| API endpoints | 4 | 1 | 1 |
| **Total** | **105** | **7** | **7** |

### Slow Query Audit Results

| Query ID | Table | Before | After | Fix |
|----------|-------|--------|-------|-----|
| Q1 | orders | 32s | 3ms | Added index (status, created_at) |
| Q2 | order_items | 18s | 5ms | Added index (order_id) |
| Q3 | products | 12s | 2ms | Added composite index |
| Q4 | customers | 28s | 8ms | Query rewrite with hints |

## Lessons Learned

1. **Try-with-resources is not optional**: All JDBC resource acquisition MUST use try-with-resources. Manual try-finally is acceptable only when try-with-resources is not possible (rare cases).

2. **Leak detection must be fast**: A 30-minute leak detection threshold makes the feature useless for catching active leaks. Set to 5 seconds or less.

3. **Pool utilization monitoring is critical**: Pool active %, pending threads, and connection timeout count should be the first metrics checked when investigating application slowdowns.

4. **Slow queries are deadly in combination**: A slow query alone might not cause an outage. A connection leak alone might not cause an outage. Together, they amplify each other.

5. **Rollback is not always the fastest fix**: The team attempted two restarts before realizing the root cause. Rolling back the deployment was more effective than restarting instances.

