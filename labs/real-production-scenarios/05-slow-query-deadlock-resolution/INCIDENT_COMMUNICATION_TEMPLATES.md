# Lab 05 — Slow Query / Deadlock: Incident Communication Templates

## Initial Alert

```
Title: [SEV1] End-of-Day Settlement Batch — 8+ Hours and Running
Service: Financial Batch Processing
Severity: SEV1

Metrics:
- Batch elapsed: 8+ hours (normal: 45-60 min)
- SQL elapsed: 99.4% of runtime in one query
- Temp space: 8.5GB used (normal: < 100MB)
- Disk reads: 12.8M (normal: < 100K)

Impact: All downstream batch jobs blocked, financial reporting delayed
```

## Status Updates

### Investigating

```
STATUS #1 — Batch Job Slow Investigation

What: END_OF_DAY_SETTLEMENT batch at 8+ hours (normal 45-60 min)
Impact: Daily reconciliation delayed, downstream jobs blocked
Severity: SEV1

Actions:
- AWR report generated — analyzing
- Execution plan captured via DBMS_XPLAN
- Checking for recent schema changes (new JOIN added this week)
- Checking optimizer statistics freshness

Next update: 30 minutes
```

### Identified

```
STATUS #2 — Batch Job Root Cause Identified

Root cause: New JOIN to SETTLEMENT_HOLD table without an index on
transaction_ref column. Query plan shows:
- Full table scan on TRANSACTIONS (50M rows)
- Full table scan on SETTLEMENT_HOLD (5M rows)
- Hash join causes temp space spill (8.5GB)

Fix: Creating composite index on SETTLEMENT_HOLD(transaction_ref, status)
and rewriting query with LEADING hint to optimize join order.

Estimated completion with fix: 45-60 minutes (normal).
```

### Resolved

```
STATUS #3 — Batch Job Resolved

Index created and query hints applied.
Batch completed in 47 minutes — within normal range.

Permanent actions:
1. Composite index created on SETTLEMENT_HOLD(transaction_ref, status)
2. Query rewritten with proper join conditions
3. AWR baseline captured for plan regression detection
4. Schema change process updated — index review required

Post-mortem: Thursday 14:00 UTC
```
