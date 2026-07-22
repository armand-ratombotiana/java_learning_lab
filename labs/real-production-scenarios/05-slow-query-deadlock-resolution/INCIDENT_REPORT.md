# Incident Report: Batch Job Slow Query / Deadlock

**Incident ID**: INC-2024-0810-BATCH
**Severity**: P1 (SEV-2 equivalent)
**Date**: August 10-11, 2024
**Affected Service**: End-of-Day Settlement Batch Job
**Duration**: 36 hours (active incident)
**Detection**: Control-M job timeout after 8 hours

## Executive Summary

On August 10, 2024, the `END_OF_DAY_SETTLEMENT` Oracle batch job failed to complete within its 8-hour execution window, blocking all downstream batch processes. The job, which normally completed in 45-60 minutes, was still running after 8 hours when the scheduler's timeout fired. Investigation revealed that a schema change deployed five days earlier had added a new JOIN between the `TRANSACTIONS` table (50M rows) and `SETTLEMENT_HOLD` table (5M rows) without a corresponding index on the JOIN column. The Oracle optimizer chose a full table scan on all three tables involved, generating 8.5GB of temporary space and running for over 8 hours. The fix involved creating a composite index on the JOIN columns, rewriting the query with LEADING and INDEX hints to force an optimized join order, and implementing partition exchange loading to prevent future issues.

## Timeline (All Times UTC)

### Day 1 — August 10

| Time | Event |
|------|-------|
| 22:00 | END_OF_DAY_SETTLEMENT batch job starts (scheduled start time) |
| 22:05 | Job begins querying TRANSACTIONS table — full table scan starts |
| 22:50 | Full table scan on TRANSACTIONS completes (52M rows, 45 min) |
| 22:50 | HASH JOIN with SETTLEMENT_AUDIT begins |
| 23:10 | HASH JOIN with SETTLEMENT_AUDIT completes |
| 23:10 | HASH JOIN with SETTLEMENT_HOLD begins — massive row explosion |
| 23:10 | Temp space starts growing: 2GB, 4GB, 6GB, 8.5GB |
| 23:45 | Temp space maxes out at 8.5GB (tablespace limit) |
| 23:45 | Job begins swapping to disk, performance collapses |

### Day 2 — August 11

| Time | Event |
|------|-------|
| 02:00 | Scheduled timeout at 4 hours NOT triggered (timeout set incorrectly) |
| 06:00 | Control-M timeout fires (configured for 8 hours) |
| 06:00 | Job marked as FAILED. All downstream jobs BLOCKED |
| 06:05 | Batch operator pages DBA on-call |
| 06:10 | DBA acknowledges. Checks Control-M — sees job failed after 8 hours |
| 06:15 | DBA checks alert log: no errors, no ORA- errors |
| 06:20 | DBA checks AWR report for the 8-hour window |
| 06:30 | AWR shows top SQL: `INSERT INTO SETTLEMENT_SUMMARY (...)` — elapsed time 7h58m |
| 06:35 | AWR shows 8.5GB temp space used, 100% direct path read/write waits |
| 06:40 | DBA extracts execution plan for the INSERT statement |
| 06:45 | Execution plan shows FULL TABLE SCAN on TRANSACTIONS (52M rows) |
| 06:55 | Execution plan shows HASH JOIN producing 245M row intermediate result |

### Root Cause Analysis

| Time | Event |
|------|-------|
| 07:00 | DBA checks table/index definitions |
| 07:10 | TRANSACTIONS table: 52M rows, indexes exist on PK and STATUS |
| 07:15 | SETTLEMENT_HOLD table: 5M rows, index on PK only |
| 07:20 | New column added to SETTLEMENT_HOLD 5 days ago: `transaction_ref` |
| 07:25 | JOIN in batch query: `ON t.transaction_id = sh.transaction_ref` |
| 07:30 | NO INDEX on `SETTLEMENT_HOLD.transaction_ref`! |
| 07:35 | Without index: full table scan on SETTLEMENT_HOLD for every join |
| 07:40 | DBA identifies missing composite index: `CREATE INDEX idx_sh_transaction_ref ...` |
| 07:45 | Decision: create index and force query plan with hints |

### Fix Phase

| Time | Event |
|------|-------|
| 08:00 | Create index on SETTLEMENT_HOLD(transaction_ref) — online, in production |
| 08:02 | Index creation starts (online, NOLOGGING) |
| 08:15 | Index created successfully |
| 08:20 | DBA runs SQL Tuning Advisor on the problematic query |
| 08:30 | SQL Tuning Advisor recommends: create index, use LEADING hint |
| 08:45 | DBA rewrites query with LEADING(t sh) and INDEX(sh idx_sh_transaction_ref) hints |
| 09:00 | Test the rewritten query — EXPLAIN PLAN shows INDEX RANGE SCAN |
| 09:05 | Execution plan now: 52M rows → 5M rows → 22M rows (nested loops) |
| 09:10 | Estimated time: from 8+ hours to ~15 minutes |
| 09:15 | DBA manually runs the fixed INSERT query |
| 09:32 | Query completes in 17 minutes |
| 09:35 | Remaining batch steps continue normally |

### Recovery Phase

| Time | Event |
|------|-------|
| 10:00 | All downstream jobs resume |
| 12:00 | End-of-day processing completed |
| 12:30 | Financial reconciliation begins (delayed but possible) |
| 14:00 | All batch processing caught up |
| 16:00 | Postmortem meeting begins |

## Key Findings

1. **Root Cause**: Missing index on `SETTLEMENT_HOLD.transaction_ref` caused full table scan + Cartesian product
2. **Trigger**: Schema change 5 days prior added new column and JOIN without corresponding index
3. **Amplification**: 52M x 5M intermediate result (245M rows) required 8.5GB temp space
4. **Detection Gap**: Job timeout was set to 8 hours (same as expected max time)
5. **Schema Change Gap**: No index review was performed for the new JOIN column

## Action Items

| # | Action | Owner | Status |
|---|--------|-------|--------|
| 1 | Add index on SETTLEMENT_HOLD(transaction_ref) | DBA Team | Done |
| 2 | Rewrite batch query with optimizer hints | Developer Team | Done |
| 3 | Add index review to all schema change requests | DBA Team | Done |
| 4 | Implement partition exchange loading for batch | DBA Team | In Progress |
| 5 | Add execution plan comparison to CI/CD pipeline | Developer Team | In Progress |
| 6 | Reduce job timeout from 8h to 2h (for earlier alert) | Batch Ops Team | Done |

## Detailed Timeline Analysis

### Pre-Incident Conditions

The schema change that introduced the vulnerability occurred on August 5:

| Date | Event | Impact |
|------|-------|--------|
| Aug 5 | ALTER TABLE settlement_hold ADD (transaction_ref VARCHAR2(50)) | Schema change deployed |
| Aug 5 | Batch job completes in 65 minutes | Normal range (baseline: 45-60 min) |
| Aug 6 | SETTLEMENT_HOLD table grows by 1M rows | Batch completes in 92 min |
| Aug 7 | SETTLEMENT_HOLD table grows to 3M rows | Batch completes in 135 min |
| Aug 8 | SETTLEMENT_HOLD table grows to 4M rows | Batch completes in 210 min |
| Aug 9 | SETTLEMENT_HOLD table grows to 5M rows | Batch completes in 310 min |
| Aug 10 | SETTLEMENT_HOLD at 5.2M rows | Batch FAILS after 8 hours (timeout) |

The performance degraded gradually over 5 days, but no alert was triggered because the job timeout was set to 8 hours — it only alerted on failure, not on duration increase.

### Detailed Execution Plan Analysis

The execution plan before the fix showed three critical problems:

1. **Full table scan on TRANSACTIONS**: 52M rows read sequentially. Each row was 2KB, so the scan read approximately 100GB of data from disk. This took 45 minutes.

2. **Full table scan on SETTLEMENT_AUDIT**: 22M rows read sequentially. This joined with the 52M TRANSACTIONS rows to produce 124M intermediate rows.

3. **Hash join with SETTLEMENT_HOLD**: The 124M intermediate rows were anti-joined with 5.2M SETTLEMENT_HOLD rows. Without an index on `transaction_ref`, this required a full scan of SETTLEMENT_HOLD and a hash join that produced 245M rows — more than any input table.

4. **Temp space overflow**: The hash join required 8.5GB of temporary space, which exceeded the available PGA memory. The database spilled to disk, causing the massive "direct path read temp" and "direct path write temp" wait events.

### Communication Log

| Time | From | To | Message |
|------|------|----|---------|
| 06:10 | Batch Operator | DBA | "EOD_SETTLEMENT job failed after 8 hours. Downstream jobs blocked." |
| 06:30 | DBA | Bridge | "AWR shows one SQL consumed 99% of the 8-hour window. Temp space exhausted." |
| 07:00 | DBA | Developer | "Missing index on SETTLEMENT_HOLD.transaction_ref. No index review was done." |
| 08:00 | DBA | Bridge | "Creating index online. Will rewrite query with hints after index completes." |
| 09:00 | DBA | Bridge | "Index created. Query rewritten. Estimated time down from 8h to 15min." |
| 09:32 | DBA | Bridge | "Query completed in 17 minutes. Proceeding with remaining batch steps." |
| 12:00 | Incident Commander | All | "Batch processing caught up. Incident resolved." |

## Incident Response Timeline

| Time | Action | Result |
|------|--------|--------|
| Day 1, 06:10 | Batch operator reports: EOD_SETTLEMENT job failed | Downstream jobs blocked |
| Day 1, 06:15 | DBA checks AWR report | One SQL consumed 99% of 8-hour window |
| Day 1, 06:30 | AWR shows 8.5GB temp space usage | Confirmed temp overflow |
| Day 1, 07:00 | Missing index identified | transaction_ref has no index despite JOIN |
| Day 1, 07:30 | Online index creation initiated | 15 minutes to complete on 5.2M rows |
| Day 1, 08:00 | Index created, query rewritten with hints | Job restarted |
| Day 1, 09:32 | Query completed in 17 minutes (before: 8h+) | Incident resolved |
| Day 1, 14:00 | All downstream jobs caught up | Financial reporting completed |

## SLA Impact Analysis

| Metric | Target | During Incident | Degradation |
|--------|--------|-----------------|-------------|
| Batch Job Duration | < 60 min | 8h 15min (timed out) | 8.25x target |
| Batch Window Close | 06:00 daily | 14:00 (14h delayed) | N/A |
| Downstream Jobs Blocked | 0 | 12 | Complete cascade |
| Financial Reporting Availability | 08:00 | 16:00 | 8h delay |
| Temp Space Usage | < 100MB | 8,704MB | 87x |

### Business Impact
- End-of-day financial reconciliation delayed by 8 hours
- 12 downstream batch jobs had to be manually rescheduled
- Regulatory reporting deadline was nearly missed
- ~36 hours of DBA and engineering time spent
- Estimated operational cost: $72,000

### Root Cause Classification
- **Type**: Database Performance / Missing Index
- **Category**: Query Plan Regression / Temp Space Overflow
- **CWE**: CWE-400 (Uncontrolled Resource Consumption)
- **Severity**: P1 / SEV-2

## Post-Incident Index Audit

All tables in the schema were audited for missing indexes:

| Table | Rows | Column | Missing Index? | Created |
|-------|------|--------|---------------|---------|
| SETTLEMENT_HOLD | 5.2M | transaction_ref | YES | ✅ Created |
| SETTLEMENT_AUDIT | 22M | txn_id | YES | ✅ Created |
| TRANSACTIONS | 52M | customer_id | NO | Already exists |
| TRANSACTIONS | 52M | created_at, status | Partial | Composite created |
| SETTLEMENT_SUMMARY | 15M | business_date | NO | Already partitioned |
| AUDIT_LOG | 40M | created_at | YES | ✅ Created |

Total missing indexes found: 7
Total indexes created: 7
Queries improved: 15

## Post-Incident Statistics

- Total incident duration: 36 hours (22:00 Aug 10 - 14:00 Aug 11)
- Batch job delay: 14 hours (from scheduled completion to actual completion)
- Downstream jobs affected: 12
- Financial reconciliation delayed: 14 hours
- Index creation time: 15 minutes (5.2M rows, NOLOGGING, ONLINE)
- Query improvement: 8h 15min → 17 minutes (29x faster)
- Temp space eliminated: 8.5GB → 0MB
- Disk reads eliminated: 12.8M → 248K (51x fewer)

## Detection Gap Analysis

| Detection Method | Status | Gap |
|-----------------|--------|-----|
| Batch job failure alert | Enabled | Only alerted on timeout (8h), not on degradation |
| Duration trending | Not implemented | Would have detected regression on day 2 |
| AWR weekly review | Manual (DBA reviews) | Slow degradation not noticed in weekly reports |
| Execution plan comparison | Not implemented | Plan regression would have been caught immediately |
| Schema change review | Not enforced | No index requirement for new JOIN columns |
| Load testing with prod-scale data | Not required | Dev test with 1M rows hid the issue |

## Recovery Steps

1. Kill the runaway batch job if still running
2. Identify missing index: check AWR for full table scans on large tables
3. Create index ONLINE NOLOGGING to minimize production impact
4. Rewrite query with optimizer hints if plan is suboptimal
5. Restart the batch job with updated query
6. Monitor subsequent job runs for performance regression

## Detection Gap Analysis

| Detection Method | Status | Gap |
|-----------------|--------|-----|
| Batch job failure alert | Enabled | Only alerted on timeout (8h), not on gradual degradation |
| Duration trending | Not implemented | Would have caught regression on day 2 |
| AWR weekly review | Manual | Slow degradation was not obvious in weekly reports |
| Execution plan comparison | Not implemented | Plan regression would have been caught immediately |
| Schema change review | Not enforced | No index requirement for new JOIN columns |
| Load testing with prod-scale data | Not required | Dev test with 1M rows hid the issue |

## Lessons Learned

1. **Every JOIN column needs an index**: Adding a column that will be used in a JOIN without creating a corresponding index is a production incident waiting to happen. The schema change process must enforce this.

2. **Batch job duration must be monitored, not just failure**: The job ran for 310 minutes on day 5 without alerting because only the 8-hour timeout was monitored. Duration trending alerts would have caught the regression on day 2.

3. **Execution plan regression must be gated in CI/CD**: Before this incident, no process checked whether a deployment changed the execution plan. Adding EXPLAIN PLAN comparison to CI/CD would have caught the regressed plan before production.

4. **Table size matters**: The same query performed adequately on a 1M-row table but catastrophically on a 5M-row table. All query performance tests should use production-scale data volumes.

5. **Oracle Support documentation is critical**: The team relied on Oracle Support Doc ID 123456.1 for DBMS_XPLAN syntax and Doc ID 789012.1 for optimizer hints. These references are essential for DBA workflows.

## Root Cause Classification
- **Type**: Database Performance / Missing Index
- **Category**: Query Plan Regression / Temp Space Overflow
- **CWE**: CWE-400 (Uncontrolled Resource Consumption)
- **Severity**: P1 / SEV-2

