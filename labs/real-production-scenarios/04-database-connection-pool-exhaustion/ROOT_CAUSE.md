# Root Cause Analysis: RDS Connection Pool Exhaustion

**Incident**: INC-2024-0715-CONNPOOL
**Analyst**: Platform Engineering Team + DBA Team
**Date of Analysis**: July 16, 2024
**Method**: HikariCP metrics analysis, code review, AWR report, slow query log analysis

## Executive Summary

The order management system became completely unavailable for 47 minutes due to HikariCP connection pool exhaustion. The root cause was a missing finally block in a newly added method (`OrderService.processBulkOrders()`) that acquired a JDBC Connection but did not return it when an exception occurred. This leaked approximately 3-5 connections per minute under peak load. A secondary contributing factor was a pre-existing slow query (full table scan on 5M rows) that held connections for 30+ seconds during execution. The combination of leaked connections (never returned) and slow queries (held too long) exhausted the 200-connection pool within 4 minutes of deployment.

## HikariCP Metrics Evidence

Pool metrics at the time of exhaustion:

```
HikariCP Pool Metrics (node-7 at 13:57):
─────────────────────────────────────────
Total Connections          : 200 (max reached)
Active Connections         : 198
  ├── Normal queries       : ~120 (executing queries, 50-500ms each)
  ├── Slow query           : ~30  (30s each, from pre-existing slow query)
  └── Leaked connections   : ~48  (from new code path, never returned)
Idle Connections           : 0
Pending Threads            : 87
ConnectionTimeout          : 30s
Leak Detection Threshold   : 1800s (30min — way too slow)
```

## The 5 Whys Analysis

### Why 1: Why did the connection pool exhaust?

All 200 connections in the HikariCP pool were active and in use. The pool had 0 idle connections and 87 threads waiting for a connection with 30-second timeouts. Three categories of connection usage were identified:

1. **Normal connections** (~120): Held for 50-500ms for regular query execution
2. **Slow query connections** (~30): Held for 30+ seconds by a slow full-table-scan query
3. **Leaked connections** (~48): Acquired from pool but never returned (exception occurred before close)

The normal connections released quickly (returned to pool). The slow query connections held their connections 60x longer than normal. The leaked connections were never returned at all. The combination prevented the pool from servicing new requests.

### Why 2: Why were connections leaked?

Code inspection revealed a new method in `OrderService.java` that was added in deployment v2.7.1:

```java
// BUGGY CODE — Connection not returned on exception
public void processBulkOrders(List<String> orderIds) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
        conn = dataSource.getConnection();  // Acquired from pool
        stmt = conn.prepareStatement("UPDATE orders SET processed = ? WHERE id = ?");
        for (String orderId : orderIds) {
            stmt.setBoolean(1, true);
            stmt.setString(2, orderId);
            stmt.executeUpdate();
        }
        // If an exception occurs in the loop (e.g., SQLException on a bad orderId),
        // the catch block logs but does NOT close the connection
    } catch (SQLException e) {
        log.error("Error processing order batch", e);
        // BUG: No conn.close() here! The connection is leaked
        // BUG: No finally block at all!
    }
}
```

When `stmt.executeUpdate()` threw a `SQLException` (e.g., from constraint violation on a specific order), the catch block logged the error but did not close the connection. The `conn` variable went out of scope without close() being called, and the connection was never returned to the pool. Each such exception leaked one connection.

### Why 3: Why was there no finally block or try-with-resources?

The developer who wrote the method was a junior engineer who:
- Was not aware of the try-with-resources pattern (introduced in Java 7)
- Was not aware that Connection.close() returns the connection to the pool
- Followed an older code pattern in the codebase that also lacked proper cleanup
- The code review did not catch the missing cleanup because:
  - The reviewer focused on business logic correctness, not resource management
  - The codebase had inconsistent patterns — some methods used try-finally, some didn't
  - There was no static analysis check for Connection leak detection

### Why 4: Why did the slow query aggravate the situation?

Two weeks before the incident, an ORM mapping change in the Order entity changed the fetch strategy from LAZY to EAGER on the `orderItems` relationship. This caused Hibernate to generate a multi-table JOIN query without an appropriate index:

```sql
-- Slow query (30+ seconds)
SELECT o.*, oi.*, p.*
FROM orders o
JOIN order_items oi ON o.id = oi.order_id
JOIN products p ON oi.product_id = p.id
WHERE o.status = 'PENDING'
ORDER BY o.created_at DESC
```

The `orders` table had 5 million rows. Without an index on `(status, created_at)`, the database performed a full table scan, reading all 5M rows, sorting them by `created_at` (using filesort), and joining with `order_items` and `products`. This query held a connection for 30+ seconds each time it was executed.

Under normal pool conditions (50-80 active connections), the slow query caused elevated but manageable connection wait times (100-500ms P99). But when the pool was already stressed by leaked connections, even a small number of slow queries consumed the remaining available connections.

### Why 5: Why didn't monitoring catch the pool exhaustion trend?

1. **HikariCP leak detection threshold was 30 minutes**: The default leakDetectionThreshold was set to 30 minutes (1,800,000ms). A leaked connection was only logged as "leaked" after 30 minutes of being checked out. By that time, the pool would be completely exhausted. The threshold should have been set to 5 seconds for production.

2. **No pool utilization alert**: There was no alert for "active connections > 80% of max". The team relied on application-level alerts (5xx errors) which only fired after the pool was already exhausted.

3. **Slow query monitoring was passive**: The DBA team reviewed slow query logs weekly. The slow query was in the log for two weeks but was not escalated until it caused an outage.

4. **No connection pool trend dashboard**: Pool utilization metrics (active, idle, pending) were collected but not visualized. A simple dashboard showing pool utilization over time would have shown the upward trend during the 4-minute window between deployment and exhaustion.

## Contributing Factors

| Factor | Description |
|--------|-------------|
| Missing finally block | Connection acquired but not returned on exception path |
| No try-with-resources | Method used manual Connection management instead of ARM |
| Generous leak detection | 30-minute leak detection threshold was far too slow |
| Slow query | Missing index caused 30s full table scan in hot path |
| Lazy → Eager change | ORM fetch strategy change two weeks prior introduced slow query |
| No pool utilization alert | Active connection % was not monitored |
| Inconsistent code patterns | Some methods used proper cleanup, some didn't |
| Code review gap | Reviewer missed missing resource cleanup |
| No static analysis | No Checkstyle/ErrorProne rule for Connection leak detection |

## Connection Lifecycle Diagram

```
NORMAL FLOW (works correctly):
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│ getConn()│───▶│ execute()│───▶│ close()  │───▶│ Return   │
│ from pool│    │ query    │    │ results  │    │ to pool  │
└──────────┘    └──────────┘    └──────────┘    └──────────┘

LEAK FLOW (the bug):
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│ getConn()│───▶│ execute()│───▶│ Exception│───▶│ CONN     │
│ from pool│    │ query    │    │ thrown   │    │ LEAKED!  │
└──────────┘    └──────────┘    └──────────┘    └──────────┘
                                                     │
                                                     ▼
                                              Never returned
                                              to pool. Pool
                                              shrinks by 1.

SLOW QUERY FLOW (aggravator):
┌──────────┐    ┌───────────────┐    ┌──────────┐
│ getConn()│───▶│ Full table    │───▶│ close()  │
│ from pool│    │ scan (30s)    │    │ to pool  │
└──────────┘    └───────────────┘    └──────────┘
                     │
                     ▼
               Connection held
               for 30s instead
               of 50ms normal

COMBINED EFFECT:
┌─────────────────────────────────────────────────────────────┐
│ Pool: 200 connections                                       │
│   ├─ Normal queries: ~120 (released quickly, 50-500ms)      │
│   ├─ Slow queries:   ~30  (held 30s each, accumulating)     │
│   ├─ Leaked conns:   ~48  (never released, accumulating)    │
│   └─ Available:      ~2   (rapidly consumed)                │
└─────────────────────────────────────────────────────────────┘
│                                                             │
▼                                                             │
┌─────────────────────────────────────────────────────────────┐
│ Result: Pool exhausted in 9 minutes                         │
│ 87 threads pending → all timeout after 30s → service down   │
└─────────────────────────────────────────────────────────────┘
```

## Why Two Restarts Failed

The first two mitigation attempts (restarting application instances) failed because:

1. **Restart #1 (14:22)**: All instances restarted, pool reset to 0 active. But within 3 minutes, the same code path leaked connections again, and the slow query consumed the remaining pool.

2. **Restart #2 (14:30)**: Same result — the root cause (the leaking code) was still deployed. Restarting only provided a temporary 3-minute window before exhaustion reoccurred.

3. **Rollback (14:40)**: The only effective mitigation was rolling back to the previous deployment version that did not have the leaking code path. This immediately stabilized the pool.

## Why Two Restarts Failed

The first two mitigation attempts (restarting application instances) failed because:
1. Restart #1 (14:22): Pool reset to 0 active, but leaked connections again within 3 minutes
2. Restart #2 (14:30): Same result — root cause still deployed
3. Rollback (14:40): Only effective mitigation — v2.7.0 did not have the leaking code

## Root Cause Confirmation Summary

1. **Code inspection**: v2.7.1 introduced a try-catch block without finally clause in OrderService.processBulkOrders()
2. **HikariCP leak detection**: Stack traces logged pointing to OrderService.java:142 every 5 seconds
3. **Local reproduction**: Unit test confirmed 3-5 connections leaked per second
4. **Fix verification**: After try-with-resources, zero leaks over 24 hours
5. **Production verification**: Rollback to v2.7.0 stabilized pool within 2 minutes

## Database Schema Change History

The slow query was introduced by a database schema change 14 days before the incident:

| Date | Change | Author | Reviewed By |
|------|--------|--------|-------------|
| Jul 1 | Added EAGER fetch to Order.orderItems | Junior Dev | Senior Dev (code only, no SQL) |
| Jul 1 | No index on orders(status, created_at) | N/A | Not flagged |
| Jul 15 | Incident occurred | N/A | N/A |

The schema change added a Hibernate annotation `@ManyToOne(fetch = FetchType.EAGER)` on the `orderItems` relationship. This caused Hibernate to generate a multi-table JOIN query that was never reviewed for performance. The resulting query did a full table scan on 5 million rows without any supporting index.

## Root Cause Validation

The root cause was validated through:
1. Code inspection of the v2.7.0 → v2.7.1 diff
2. HikariCP leak detection stack traces matching the exact line
3. Local reproduction with a unit test confirming 3-5 connections/second leak rate
4. Fix verification showing zero leaks after try-with-resources fix
5. Production rollback confirming pool stabilization within 2 minutes

## FAQ

### Q: How can I detect connection leaks in development?
Enable HikariCP leak detection with `leakDetectionThreshold=5000` in your dev profile. The log will show the exact stack trace of where the connection was acquired. For static analysis, use ErrorProne's `CheckReturnValue` or SpotBugs `OBL_UNSATISFIED_OBLIGATION`.

### Q: What is the difference between connection leak and pool exhaustion?
A connection leak is one connection that is never returned to the pool (root cause). Pool exhaustion is the symptom where all connections are in use, causing new requests to time out. Fixing the leak resolves the exhaustion, but adding more connections (maxPoolSize) only delays the failure.

### Q: Could this have been prevented by a larger pool size?
No. The pool was leaking 3-5 connections per second. At 200 max connections, exhaustion in ~60 seconds was inevitable regardless of pool size. A larger pool would have extended the time-to-exhaustion but not prevented it.

### Q: Should I use try-with-resources for all JDBC resources?
Yes for Connection, Statement, and ResultSet. These all implement AutoCloseable. Using try-with-resources ensures that even if an exception is thrown during close(), the resource is still cleaned up.

## Lessons Summary

| Lesson | Action Item | Owner | Deadline |
|--------|-------------|-------|----------|
| Try-with-resources is not optional | Enforce in code review and static analysis | Platform Team | Immediate |
| Leak detection must be fast | Set leakDetectionThreshold to 5s | SRE | Week 1 |
| Pool monitoring is critical | Add pool metrics to dashboards | SRE | Week 1 |
| Slow queries amplify leaks | Add query performance gates | DBA | Week 2 |
| Rollback is effective | Prioritize rollback over restart | SRE | Training complete |

## Incident Classification

| Field | Value |
|-------|-------|
| Incident ID | INC-2024-0715-DB-POOL |
| Severity | P0 (SEV-1) |
| Type | Resource Exhaustion / Connection Leak |
| CWE | CWE-404, CWE-772 |
| MTTR | 39 minutes |
| Total impact | ~$465,000 (estimated) |
| Root cause | Missing finally clause in try-catch block |

## Glossary

| Term | Definition |
|------|------------|
| Connection Leak | A JDBC Connection that is opened but never closed/returned to pool |
| Pool Exhaustion | All connections in pool are in use, no available connections |
| HikariCP | High-performance JDBC connection pool for Java |
| Leak Detection | HikariCP feature that logs stack traces of connections not returned within threshold |
| try-with-resources | Java 7+ idiom that auto-closes AutoCloseable resources |
| Connection Timeout | Maximum time to wait for a connection from the pool |
| Idle Connection | Connection not currently in use but available in pool |
| Active Connection | Connection currently in use by an application thread |
| Pending Thread | Thread waiting for a connection to become available |
| maxPoolSize | Maximum number of connections the pool will create |

