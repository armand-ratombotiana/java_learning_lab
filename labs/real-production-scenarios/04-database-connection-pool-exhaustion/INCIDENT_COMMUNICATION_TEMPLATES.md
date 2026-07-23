# Lab 04 — Connection Pool Exhaustion: Incident Communication Templates

## Initial Alert

```
Title: [P0] Database Connection Pool Exhaustion — Order Service
Service: Order Management System
Severity: P0

Metrics:
- Active connections: 198/200 (99%)
- Pending threads: 87
- Connection timeouts: 100% of requests
- Error rate: 100% on checkout operations

Impact: All order processing halted — users cannot place orders
Duration: 2 minutes since first timeout

Action: Acknowledge immediately
```

## Status Updates

### Investigating

```
STATUS #1 — INC-2024-004 — INVESTIGATING

What: Database connection pool exhausted — 198/200 active
Impact: All order processing halted
Severity: P0

Actions:
- Checking HikariCP leak detection logs
- Running MySQL SHOW FULL PROCESSLIST
- Checking for recent deployments (new order handler deployed 10 min ago)
- Running heartbeat query to validate DB connectivity

Next update: 10 minutes
```

### Identified

```
STATUS #2 — INC-2024-004 — IDENTIFIED

Root cause:
1. PRIMARY: New transaction handler missing try-with-resources causes connection leak
2. CONTRIBUTING: Pre-existing slow query (full table scan on orders table)
is holding connections for 30+ seconds

Actions:
- Rolling back deployment to restore normal operation
- Killing slow queries on DB to release connections quickly
- Adding temporary index for slow query

Next update: 15 minutes
```

### Resolved

```
STATUS #3 — INC-2024-004 — RESOLVED

Deployment rolled back at 14:30 UTC.
Slow queries killed at 14:32 UTC.
Index added at 14:45 UTC.
Connection pool back to 45/200 active.

Fix deployed:
1. Added try-with-resources to transaction handler
2. Added composite index on orders(user_id, status, created_at)
3. Reduced leakDetectionThreshold from 30min to 5s
4. Added HikariCP metrics dashboard

Post-mortem: Friday 10:00 UTC
```
