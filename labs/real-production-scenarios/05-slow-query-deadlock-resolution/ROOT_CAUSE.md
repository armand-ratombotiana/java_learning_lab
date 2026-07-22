# Root Cause Analysis: Batch Job Slow Query — Missing Index

**Incident**: INC-2024-0810-BATCH
**Analyst**: DBA Team + Application Development Team
**Date of Analysis**: August 12, 2024
**Method**: AWR report analysis, execution plan analysis, schema change audit

## Executive Summary

The `END_OF_DAY_SETTLEMENT` batch job ran for 8 hours and failed, blocking all subsequent batch processing. The root cause was a missing composite index on the `SETTLEMENT_HOLD.transaction_ref` column that was added to a JOIN condition five days prior. Without an index, the Oracle optimizer chose a full table scan on `SETTLEMENT_HOLD` (5M rows) combined with a full table scan on `TRANSACTIONS` (52M rows), producing a 245-million-row intermediate result set that required 8.5GB of temporary tablespace and ran for over 8 hours. The query plan also showed a Cartesian product merge due to the optimizer's inability to estimate proper cardinality without index statistics on the new column.

## AWR Report Evidence

The AWR report for the 8-hour batch window showed:

```
Top 5 Timed Events:
────────────────────────────────────────────────────
Event                    Waits    Time(s)  Avg Wait
────────────────────────────────────────────────────
direct path read temp    2,847,293  28,942    10ms  ← Massive temp I/O
direct path write temp   1,824,906  18,514    10ms  ← Spilling to disk
CPU time                           1,203            ← Minimal CPU (I/O bound)
SQL*Net message to client  12,847      12     1ms
log file sync                 847       8    10ms

SQL ordered by Elapsed Time:
────────────────────────────────────────────────────
SQL ID        Elapsed (s)  Executions  %Total
────────────────────────────────────────────────────
6g8k2x3a1b4c     28,713          1    99.4%  ← Problem query

SQL ordered by I/O:
────────────────────────────────────────────────────
SQL ID        Disk Reads    Executions  %Total
────────────────────────────────────────────────────
6g8k2x3a1b4c  12,847,293          1    99.8%  ← 12.8M disk reads
```

## The 5 Whys Analysis

### Why 1: Why did the batch job take 8+ hours?

The execution plan showed:

```
INSERT INTO SETTLEMENT_SUMMARY (...)
SELECT ...
FROM TRANSACTIONS t
JOIN SETTLEMENT_AUDIT sa ON t.txn_id = sa.txn_id
LEFT JOIN SETTLEMENT_HOLD sh ON t.transaction_id = sh.transaction_ref
WHERE t.status = 'PENDING';

Plan:
┌─────────────────────────────────────────────────────────────┐
│ HASH JOIN RIGHT ANTI (245M rows, 8h 15min)                  │
│   ├── TABLE ACCESS FULL: SETTLEMENT_HOLD (5M rows, 45s)     │
│   └── HASH JOIN (124M rows, 7h 30min)                       │
│         ├── TABLE ACCESS FULL: TRANSACTIONS (52M, 45min)    │
│         └── TABLE ACCESS FULL: SETTLEMENT_AUDIT (22M, 20min)│
└─────────────────────────────────────────────────────────────┘
```

Three full table scans combined through hash joins. The intermediate result of joining TRANSACTIONS (52M) with SETTLEMENT_AUDIT (22M) produced 124M rows. This was then hash-anti-joined with SETTLEMENT_HOLD (5M). The optimizer estimated an anti-join result of 245M rows — larger than any of the input tables, indicating a cardinality estimation error caused by the missing statistics on the unindexed column.

The 8.5GB of temporary space usage confirmed that the intermediate results could not fit in memory (PGA) and spilled to disk, causing the massive direct path read/write temp events.

### Why 2: Why did the optimizer choose full table scans on all tables?

The `TRANSACTIONS` table had an index on `status` (used for the WHERE clause), but the query selected many columns that were not in the index, requiring the table access anyway. The `SETTLEMENT_AUDIT` table had no index on the JOIN column `txn_id`. The `SETTLEMENT_HOLD` table had no index on `transaction_ref`.

Without indexes on the join columns, the optimizer had two choices for each join:
1. Full table scan + HASH JOIN (chosen — bad for large tables)
2. Full table scan + NESTED LOOP (would be even worse without indexes)

The optimizer chose the least bad option by default. With proper indexes, it could have used:
- INDEX RANGE SCAN on `SETTLEMENT_HOLD.transaction_ref`
- NESTED LOOPS joining TRANSACTIONS → SETTLEMENT_HOLD via index

### Why 3: Why was there no index on SETTLEMENT_HOLD.transaction_ref?

Five days before the incident, a schema change added the `transaction_ref` column to `SETTLEMENT_HOLD` to support a new business requirement for tracking cross-reference transactions. The DDL was:

```sql
ALTER TABLE settlement_hold ADD (transaction_ref VARCHAR2(50));
```

This column was never indexed. The development team added the column and modified the batch query to use it in the JOIN, but did not request an index review. The DBA team was not notified about the schema change. The development team assumed the optimizer would handle it efficiently, not understanding that without an index on a 5M-row table, any JOIN would be a full table scan.

### Why 4: Why did the schema change process not include index review?

The organization's schema change process had the following gaps:

1. **No mandatory index review**: Development teams could add columns and modify queries without DBA review
2. **No execution plan check in CI/CD**: The batch query's EXPLAIN PLAN was not checked before deployment
3. **No performance baseline comparison**: Before/after execution plans were not compared
4. **No index creation checklist**: Adding a new JOIN column without an index was possible in the standard workflow

The process required DBA approval for DDL changes but the approval was focused on:
- Table structure correctness
- Data type compatibility
- Storage parameters
- NOT on index requirements for new JOIN columns

### Why 5: Why didn't the monitoring catch the performance regression during the 5-day window?

The batch job ran successfully for 4 days after the schema change before the critical failure on day 5. Why?

Day 1 after change: Job completed in 65 minutes (normal range)
Day 2 after change: Job completed in 92 minutes (elevated but not alarming)
Day 3 after change: Job completed in 135 minutes (slow but not failed)
Day 4 after change: Job completed in 210 minutes (approaching timeout)
Day 5 after change: Job FAILED after 8 hours (timeout)

The performance degraded gradually because:
- The `SETTLEMENT_HOLD` table grew by ~1M rows per day
- On day 1-2, the table was small enough that full table scan was acceptable
- On day 3-4, the table crossed a threshold where the full table scan became painful
- On day 5, with 5M rows, the full table scan became catastrophic

There was no alerting on:
- Batch job duration increase (P95 deviation from baseline)
- Temp space usage trends
- Full table scan detection

## Query Execution Plan (Before)

```
SQL> SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY_CURSOR('6g8k2x3a1b4c', 0, 'ALLSTATS LAST'));

Plan hash value: 3948572018

-------------------------------------------------------------------------------------------------------------
| Id | Operation                      | Name               | Rows   | Cost | Time      | Temp  | Starts |
-------------------------------------------------------------------------------------------------------------
|  0 | INSERT STATEMENT               | SETTLEMENT_SUMMARY |        | 847K |           |       |      1 |
|  1 |  LOAD TABLE CONVENTIONAL       | SETTLEMENT_SUMMARY |        | 847K |           |       |      1 |
|  2 |   HASH JOIN RIGHT ANTI         |                    |  245M  | 847K | 08:15:23 |       |      1 |
|  3 |    TABLE ACCESS FULL           | SETTLEMENT_HOLD    |  5.2M  | 4175 | 00:00:45 |       |      1 |
|  4 |    HASH JOIN                   |                    |  124M  | 840K | 07:30:00 | 8.5GB |      1 |
|  5 |     TABLE ACCESS FULL          | TRANSACTIONS       |   52M  | 410K | 00:45:00 |       |      1 |
|  6 |     TABLE ACCESS FULL          | SETTLEMENT_AUDIT   |   22M  | 183K | 00:20:00 |       |      1 |
-------------------------------------------------------------------------------------------------------------
```

## Detailed AWR Report Analysis

### Load Profile

```
Load Profile (8-hour batch window):
────────────────────────────────────
Total DB Time (s):        28,941
Total DB Wait Time (s):   27,738 (95.8% of DB time)
CPU Time (s):             1,203 (4.2% of DB time)

Top 5 Wait Events:
Event                            Waits     Time (s)  Avg Wait
────────────────────────────────────────────────────────────
direct path read temp           2,847,293    28,942    10ms
direct path write temp          1,824,906    18,514    10ms
CPU time                                   1,203
SQL*Net message from client        12,847        12     1ms
log file sync                         847         8    10ms
```

The "direct path read/write temp" events indicate the query spilled to disk because the hash join could not fit in memory. This is the definitive signature of a missing index causing temp space overflow.

### SQL Statistics

```
SQL ordered by Elapsed Time:
────────────────────────────────────
SQL ID        Elapsed (s)  CPU (s)   Executions  %Total  %CPU
─────────────────────────────────────────────────────────────
6g8k2x3a1b4c    28,713      1,198        1       99.4%    4.2%

SQL ordered by Disk Reads:
────────────────────────────────────
SQL ID        Disk Reads    Executions  %Total
─────────────────────────────────────────────
6g8k2x3a1b4c  12,847,293        1       99.8%

SQL ordered by Temp Space:
────────────────────────────────────
SQL ID        Temp (MB)     Executions
─────────────────────────────────────
6g8k2x3a1b4c    8,704           1
```

### Instance Efficiency

```
Buffer Nowait %:        99.98
Buffer Hit %:            98.32
Library Hit %:           99.87
Execute to Parse %:      85.21
Temp Space Used (GB):     8.5
```

The buffer hit ratio was excellent at 98.32%, confirming that the database was not under general memory pressure. The specific SQL had a unique issue that caused temp space overflow despite adequate SGA/PGA configuration.

## Performance Degradation Timeline

The missing index was not immediately catastrophic because `SETTLEMENT_HOLD` was empty when the column was added. Over 5 days, the table grew:

| Day | SETTLEMENT_HOLD Rows | TRANSACTIONS Rows | Query Duration | CPU Time | Temp Space |
|-----|---------------------|-------------------|----------------|----------|------------|
| 0 | 0 | 50M | 45 min | 400s | 0MB |
| 1 | 800K | 50M | 65 min | 520s | 120MB |
| 2 | 1.9M | 51M | 92 min | 690s | 890MB |
| 3 | 3.1M | 51M | 135 min | 810s | 2.1GB |
| 4 | 4.2M | 52M | 210 min | 950s | 4.8GB |
| 5 | 5.2M | 52M | 495 min* | 1,198s | 8.5GB |

*Timed out at 8 hours (480 min) but continued running until manually killed.

The query duration scaled non-linearly with SETTLEMENT_HOLD size because:
- At 0 rows: hash anti-join was trivial (no rows to anti-join)
- At 800K rows: small enough to fit in memory
- At 1.9M rows: started spilling to temp
- At 3.1M rows: significant temp spill, duration growing
- At 5.2M rows: catastrophic failure

## Related Oracle Support Cases

| Case ID | Issue | Resolution |
|---------|-------|------------|
| 3-123456789 | Missing index on JOIN column causes hash join temp overflow | Create index with ONLINE NOLOGGING |
| 3-987654321 | CBO chooses full table scan over index due to stale statistics | Gather table statistics |
| 3-456789123 | Partition exchange load fails due to referential integrity | Disable constraints during PEL |
| 3-321654987 | AWR shows direct path read temp for all top SQL | Increase PGA_AGGREGATE_TARGET |

## Root Cause Validation

The root cause was validated by:

1. **Execution plan analysis**: The plan clearly showed full table scans on all three tables with a hash join producing more rows than input tables

2. **Semantic analysis**: The new column `transaction_ref` was added to `SETTLEMENT_HOLD` without a corresponding index, and the batch query was modified to JOIN on this column

3. **Fix verification**: After creating the index and rewriting the query with hints, the execution plan showed INDEX RANGE SCAN instead of TABLE ACCESS FULL, and the query completed in 17 minutes instead of 8+ hours

4. **Reproducibility**: A local test database with the same schema and data volumes reproduced the slow query, which was fixed by the same index creation

## Root Cause Discussion with Management

Q: Why wasn't this caught before it reached production?

A: The schema change added a JOIN column (transaction_ref) to SETTLEMENT_HOLD without creating an index because the column was initially NULL and no review process required indexes for new columns. The query was tested on a development database with 1M rows, where the full table scan completed in an acceptable 5 minutes.

Q: Why did the query degrade over 5 days?

A: The table grew from 0 rows to 5.2M rows over 5 days (normal business data accumulation). The hash anti-join started in-memory at small row counts but spilled to temp as the table grew. Non-linear scaling meant performance collapsed once the working set exceeded available PGA memory.

Q: What prevented the gradual degradation from being detected?

A: The batch job duration was monitored only for failure (8-hour timeout), not for performance trending. A duration-trending alert (e.g., "job duration increased 50% over 24h") would have detected the regression on day 2 when the query took 65 minutes instead of 45.

## Technical Validation Summary

The root cause was validated through:
1. AWR report showing SQL ID 6g8k2x3a1b4c consuming 99.4% of elapsed time
2. Execution plan showing full table scans on all three joined tables
3. Semantic analysis showing missing index on SETTLEMENT_HOLD.transaction_ref
4. Local reproduction: same query without index produces temp spill
5. Fix verification: index creation reduces query from 8h+ to 17 minutes

## 5 Whys Summary

```
Why 1: Batch job took 8+ hours → Full table scans + temp space overflow
Why 2: Full table scans chosen → No index on JOIN column
Why 3: No index created → Schema change process did not require index review
Why 4: No index review required → Process gap in DDL change management
Why 5: Process gap existed → org did not prioritize index review for new JOIN columns
                           → OR: monitoring did not detect gradual performance regression
```

