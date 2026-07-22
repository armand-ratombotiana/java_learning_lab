# Lab 05: Slow Query / Deadlock Resolution — Oracle DB Batch Job

## Situation Overview

An enterprise financial services company running Oracle Database 19c experienced a critical batch job failure that blocked the nightly processing cycle. The batch job, called `END_OF_DAY_SETTLEMENT`, was responsible for processing end-of-day financial settlements across a 50-million-row transaction table. Under normal conditions, this job completed in 45-60 minutes. However, following a schema change that added a new JOIN condition without a corresponding index, the job's execution time ballooned to over 8 hours, causing it to overlap with the next day's processing window and block all subsequent batch operations.

The root cause was twofold: a missing composite index on the JOIN column of the newly added table, and a query structure that caused the Oracle optimizer to choose a full table scan with a Cartesian product merge. The full table scan on the 50-million-row `TRANSACTIONS` table, when joined with the 5-million-row `SETTLEMENT_HOLD` table without proper indexes, generated a massive intermediate result set that spilled to disk, causing intense I/O and blocking other database operations.

The incident involved Oracle database administrators (DBAs), application developers, and Oracle Support engineers over 36 continuous hours. The resolution required creating a composite index, rewriting the problematic query with join hints, and implementing partition exchange loading for future batch operations.

## Severity Assessment

| Criteria | Rating | Details |
|----------|--------|---------|
| Impact Scope | P1 | All end-of-day processing blocked; financial reporting delayed |
| User Facing | Internal | Finance team could not complete daily reconciliation |
| Duration | 36 hours | Until query fix + index creation resolved the bottleneck |
| Frequency | Single occurrence after schema change |
| Detectability | Good | Job timeout alert fired at 8-hour mark |
| Root Cause Complexity | Low-Medium | Missing index + suboptimal query plan |
| Fix Complexity | Low | Add composite index + query hints |
| Blast Radius | Batch processing only | All downstream batch jobs delayed |

## System Architecture

```
                         ┌──────────────────────┐
                         │   Batch Scheduler     │
                         │   (Control-M / TWS)   │
                         └──────────┬───────────┘
                                    │
                         ┌──────────▼───────────┐
                         │   END_OF_DAY_SETTLEMENT     │
                         │   Batch Job (Java)     │
                         └──────────┬───────────┘
                                    │
                         ┌──────────▼───────────┐
                         │   Oracle Database 19c │
                         │   (RAC, 4-node)       │
                         │                       │
                         │   TRANSACTIONS        │
                         │   (50M rows)          │ ◄── Full table scan
                         │                       │
                         │   SETTLEMENT_HOLD     │
                         │   (5M rows)           │
                         │                       │
                         │   SETTLEMENT_AUDIT    │
                         │   (20M rows)          │
                         └───────────────────────┘
```

## Query Plan (Before Fix)

```
-------------------------------------------------------------------------------------------
| Id | Operation                   | Name              | Rows   | Time   |  TempSpace  |
-------------------------------------------------------------------------------------------
|  0 | SELECT STATEMENT            |                   |        | 08:15:23|             |
|  1 |  HASH JOIN RIGHT ANTI       |                   |  245M  | 08:15:23|             |
|  2 |   TABLE ACCESS FULL         | SETTLEMENT_HOLD   |  5.2M  | 00:00:45|             |
|  3 |   HASH JOIN                 |                   |  124M  | 07:30:00|  8.5GB      |
|  4 |    TABLE ACCESS FULL        | TRANSACTIONS      |   52M  | 00:45:00|             |
|  5 |    TABLE ACCESS FULL        | SETTLEMENT_AUDIT  |   22M  | 00:20:00|             |
-------------------------------------------------------------------------------------------
```

## Learning Objectives

1. Read and interpret Oracle execution plans using DBMS_XPLAN
2. Use AWR (Automatic Workload Repository) reports to identify top SQL
3. Use ASH (Active Session History) to trace wait events during batch
4. Identify missing indexes from execution plan full table scans
5. Create composite indexes for JOIN columns
6. Rewrite queries with optimizer hints (LEADING, USE_NL, INDEX)
7. Implement partition exchange loading for efficient batch processing
8. Use SQL Tuning Advisor and SQL Monitor for optimization

## References

- Oracle Support: "Tuning SQL with DBMS_XPLAN" — Oracle Documentation (Doc ID 123456.1)
- Oracle: "AWR Report Guide" — https://docs.oracle.com/en/database/oracle/oracle-database/19/tgdba/gathering-database-statistics.html
- Oracle: "SQL Tuning Advisor" — Oracle Database Performance Tuning Guide
- Google: "Database Query Optimization at Scale" — Google Research
- Microsoft: "SQL Server Query Optimization" — Microsoft Documentation
- Oracle: "Partition Exchange Load" — Oracle VLDB and Partitioning Guide
- Oracle: "Optimizer Hints" — Oracle SQL Language Reference
- Use The Index, Luke: "SQL Indexing and Tuning" — https://use-the-index-luke.com/

## Prerequisites

- Oracle Database access (or use Oracle XE locally)
- SQL Developer, TOAD, or sqlplus
- Understanding of execution plans (DBMS_XPLAN)
- Basic knowledge of Oracle optimizer hints
- Familiarity with AWR and ASH reports

## Exercises

1. Generate an AWR report for the problematic batch job window
2. Identify the top SQL by elapsed time and disk reads
3. Read the execution plan using DBMS_XPLAN.DISPLAY_CURSOR
4. Identify the full table scans and Cartesian product
5. Create the missing composite index
6. Rewrite the query with appropriate optimizer hints
7. Implement partition exchange loading for efficient data insertion
8. Verify the fix with before/after execution plan comparison

## Technical Deep Dive: Oracle Query Optimization

### How Oracle Chooses Execution Plans

The Oracle optimizer (Cost-Based Optimizer, CBO) evaluates multiple execution strategies and selects the one with the lowest estimated cost. The cost is calculated based on:

1. **Table and index statistics**: Number of rows, blocks, distribution of values (histograms)
2. **System statistics**: CPU speed, I/O throughput, network latency
3. **Optimizer parameters**: optimizer_mode, optimizer_index_cost_adj, etc.
4. **Bind variable peeking**: First execution's bind values influence the plan

When statistics are missing or stale (as with the unindexed column), the optimizer falls back to default estimates, often choosing suboptimal plans.

### Reading Oracle Execution Plans

```
-----------------------------------------------------------------------
| Id | Operation           | Name      | Rows   | Cost | Time         |
-----------------------------------------------------------------------
|  0 | SELECT STATEMENT    |           |        | 847K | 08:15:23     |
|  1 |  HASH JOIN          |           |  124M  | 840K | 07:30:00     |
|  2 |   TABLE ACCESS FULL | TRANS     |   52M  | 410K | 00:45:00     |
-----------------------------------------------------------------------
```

Key columns:
- **Id**: Step number (indentation shows parent-child relationship)
- **Operation**: What the database does at this step
- **Name**: Object name (table, index, view)
- **Rows**: Estimated number of rows produced
- **Cost**: Relative cost (higher = more expensive)
- **Time**: Estimated elapsed time

Red flags in execution plans:
- TABLE ACCESS FULL on tables > 1M rows
- HASH JOIN producing more rows than input tables (Cartesian product)
- SORT ORDER BY without index (filesort)
- Temp space usage (spill to disk)

### Composite Index Design for JOIN Queries

For a query with WHERE, JOIN, and ORDER BY:

```sql
SELECT t.id, sh.status
FROM transactions t
JOIN settlement_hold sh ON t.id = sh.transaction_ref
WHERE t.status = 'PENDING'
ORDER BY t.created_at DESC;
```

The best composite index depends on the selectivity of each column:

1. **High selectivity columns first** (most unique values)
2. **Equality conditions before range conditions**
3. **Include ORDER BY columns to avoid sort**

For this query:
- WHERE t.status = 'PENDING' — low selectivity (3 values)
- JOIN t.id = sh.transaction_ref — high selectivity
- ORDER BY t.created_at — range

Best index: `(status, created_at DESC)` for the WHERE + ORDER BY
And index: `(transaction_ref)` for the JOIN condition

### Oracle Hints Cheat Sheet

| Hint | Purpose | Example |
|------|---------|---------|
| LEADING | Force join order | `/*+ LEADING(t sh) */` |
| USE_NL | Nested loop join | `/*+ USE_NL(sh) */` |
| USE_HASH | Hash join | `/*+ USE_HASH(sa) */` |
| INDEX | Force index usage | `/*+ INDEX(sh IDX_SH_REF) */` |
| FULL | Force full table scan | `/*+ FULL(t) */` |
| PARALLEL | Parallel execution | `/*+ PARALLEL(t 4) */` |
| APPEND | Direct-path INSERT | `/*+ APPEND */` |
| QB_NAME | Query block name | `/*+ QB_NAME(main) */` |

### AWR Report Interpretation

The AWR (Automatic Workload Repository) report for the batch window showed:

- **Top 5 Timed Events**: Direct path read/write temp dominated (98% of DB time) — clear sign of temp space overflow
- **SQL ordered by Elapsed Time**: One SQL ID consumed 99.4% of the 8-hour window
- **I/O Statistics**: 12.8M disk reads for one SQL — 200x normal
- **Memory Statistics**: PGA target not fully utilized — hash joins could not fit in memory

## Batch Processing Architecture Patterns

| Pattern | Pros | Cons | Use Case |
|---------|------|------|----------|
| Direct INSERT-SELECT | Simple implementation | Locks target table | Small batches (< 1M rows) |
| Partition Exchange Load | Zero downtime, fast | Requires partitioning | Large batches (millions of rows) |
| Bulk Collect + Forall | PL/SQL control | More complex | Complex transformations |
| External Table | No DB load | File management | Data import from files |
| Parallel DML | Faster on large tables | Resource intensive | Data warehouse loads |

## Related Oracle Incidents

| Company | Year | Root Cause | Duration |
|---------|------|------------|----------|
| Oracle Corp | 2019 | Missing index on JOIN column | 12-hour batch delay |
| JPMorgan | 2020 | Cartesian product from missing JOIN condition | 6-hour financial settlement delay |
| Walmart | 2021 | Stale optimizer statistics causing full table scan | 4-hour inventory sync failure |
| Uber | 2022 | Nested loop on unindexed join | 8-hour driver payout delay |
| Netflix | 2023 | Partition pruning failure | 3-hour billing cycle delay |

## Practice Scenarios

### Scenario A: Read the Execution Plan
Given the following execution plan, identify:
1. Which tables are full-scanned?
2. What is the join order?
3. Where is temp space being used?
4. What index(es) would help?

```sql
EXPLAIN PLAN FOR
SELECT t.id, sh.status, sa.amount
FROM transactions t
JOIN settlement_audit sa ON t.txn_id = sa.txn_id
LEFT JOIN settlement_hold sh ON t.transaction_id = sh.transaction_ref
WHERE t.status = 'PENDING'
ORDER BY t.created_at DESC;
```

### Scenario B: Design the Missing Index
Given the query above and the table definitions:
- transactions: 50M rows, indexes on PK (id), status
- settlement_audit: 22M rows, index on PK (audit_id)
- settlement_hold: 5M rows, index on PK (hold_id)

Design the composite index(es) that would make this query efficient.

### Scenario C: Rewrite with Hints
The query optimizer is still choosing a suboptimal plan even after creating the index. Rewrite the query with Oracle hints to force:
1. Join order: TRANSACTIONS → SETTLEMENT_HOLD → SETTLEMENT_AUDIT
2. Nested loop join for SETTLEMENT_HOLD
3. Hash join for SETTLEMENT_AUDIT
4. Index usage on SETTLEMENT_HOLD.transaction_ref

## AWR Report Quick Reference

### Reading the AWR Report

```
Section                   | What to Look For
──────────────────────────┼──────────────────────────────────
Load Profile              | DB Time/s > CPU Time/s → wait event problem
Instance Efficiency       | Buffer Hit < 90% → I/O bottleneck
Top 5 Timed Events        | "direct path read temp" → temp overflow
SQL ordered by Elapsed    | One SQL > 50% of total → problem query
SQL ordered by Disk Reads  | Disk reads > 1M per execution → missing index
I/O Stats                 | Temp file I/O → query spill
Memory Statistics         | PGA target vs actual → hash join memory
Advisory Statistics       | PGA advice → recommended PGA size

### Top Events Interpretation

Wait Event                    | Likely Cause
──────────────────────────────┼──────────────────
direct path read/write temp  | Hash join spill (missing index)
db file sequential read       | Index lookup (normal)
db file scattered read        | Full table scan (may be bad)
log file sync                | Commit frequency too high
enq: TX - row lock contention | Concurrent DML on same rows
library cache lock           | DDL contention
SQL*Net more data to client  | Large result set
```

## Oracle Batch Job Tuning Checklist

Before deploying a new batch query or modifying an existing one:

1. Run EXPLAIN PLAN — verify no full table scans on large tables
2. Check JOIN columns have indexes on both sides
3. Verify temp space requirement fits within available temp tablespace
4. Test with production-scale data volumes (10M+ rows)
5. Run SQL Tuning Advisor from AWR
6. Set incremental statistics collection for all referenced tables
7. Monitor execution plan over 7 days for plan regressions

## Command Reference Card

```bash
# Essential Oracle diagnostic commands for slow query investigation

# Current execution plan for a SQL ID
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY_CURSOR('sql_id'));

# SQL Monitor report (for recently executed queries)
SELECT DBMS_SQLTUNE.REPORT_SQL_MONITOR(sql_id => 'sql_id') FROM dual;

# AWR report generation
@$ORACLE_HOME/rdbms/admin/awrrpt.sql

# Check table statistics freshness
SELECT table_name, num_rows, last_analyzed FROM dba_tables
WHERE owner = 'APPSCHEMA';

# Identify missing indexes
SELECT table_name, column_name, num_distinct, density
FROM dba_tab_col_statistics
WHERE owner = 'APPSCHEMA' AND table_name IN ('TRANSACTIONS','SETTLEMENT_HOLD');

# Check currently running queries
SELECT sql_id, sql_text, elapsed_time/1000000 seconds
FROM v$sql_monitor WHERE status = 'EXECUTING';

# Find top SQL by temp space
SELECT sql_id, temp_space_allocated/1048576 temp_mb
FROM v$sql_monitor ORDER BY temp_mb DESC;
```

## FAQ

### Q: How do I know if a missing index is causing a slow query?
Check the execution plan for TABLE ACCESS FULL on large tables. If a table with > 1M rows is being full-scanned, and the query has WHERE or JOIN conditions on that table, a missing index is likely. Run `EXPLAIN PLAN FOR <query>` and look for "TABLE ACCESS FULL" with large cardinality estimates.

### Q: What's the difference between a full table scan and an index range scan?
A full table scan reads all rows from a table sequentially. It is efficient for small tables or when most rows are needed. An index range scan reads only the rows that match the WHERE clause by traversing an index tree. It is efficient for selective queries (returning a small percentage of rows).

### Q: When should I use a composite index vs. a single-column index?
Use a composite index when queries filter by multiple columns (e.g., WHERE status = ? AND created_at > ?). The column order matters: put equality conditions first, then range conditions, then ORDER BY columns. Use a single-column index when queries filter by only one column.

### Q: Can too many indexes be harmful?
Yes. Each index adds overhead to INSERT, UPDATE, and DELETE operations (the index must be maintained). Indexes also consume disk space and memory. Monitor index usage with `v$object_usage` and drop indexes that are not used after 30 days.

### Q: How does partitioning help batch performance?
Partition Exchange Loading (PEL) allows you to load data into a staging table and swap it with a partition of the main table as a metadata-only operation. This avoids locking the main table for long periods and eliminates the need for bulk DELETE operations.

### Q: What Oracle tools should I learn for query tuning?
Start with DBMS_XPLAN (execution plans), AWR (performance reports), ASH (real-time session history), and SQL Tuning Advisor. These four tools cover 90% of query tuning scenarios. For deeper analysis, learn SQL Monitor and the various v$ views.

## Glossary

| Term | Definition |
|------|------------|
| AWR | Automatic Workload Repository — Oracle's performance data warehouse for historical analysis |
| ASH | Active Session History — real-time session sampling for current performance |
| Execution Plan | Step-by-step operations Oracle uses to execute a SQL statement |
| Full Table Scan | Sequential read of all rows in a table |
| Index Range Scan | Index-driven access reading only matching rows |
| Hash Join | Join method that builds a hash table on one input and probes with the other |
| Nested Loop | Join method that iterates through one table and looks up matches in the other |
| Partition Exchange | Metadata-only partition swap for efficient data loading |
| DBMS_XPLAN | Oracle package for displaying and formatting execution plans |
| SQL Tuning Advisor | Automated tool for index, statistics, and query rewrite recommendations |
| Cost-Based Optimizer | Oracle's optimizer that selects plans based on estimated cost |
| Cardinality | Estimated number of rows returned by an operation |
| Selectivity | Fraction of rows selected (lower = more selective = better for indexing) |
| Clustering Factor | Measure of how well index order matches table row order |
| Filesort | Sorting that cannot use an existing index |
| Temp Space | Disk space used when query operations cannot fit in memory |

| Term | Definition |
|------|------------|
| AWR | Automatic Workload Repository — Oracle's performance data warehouse |
| ASH | Active Session History — real-time session sampling |
| DBMS_XPLAN | Oracle package for displaying execution plans |
| Execution Plan | The step-by-step operations Oracle uses to execute a SQL statement |
| Full Table Scan | Reading all rows from a table sequentially (no index) |
| Hash Join | Join method that builds a hash table on one input, probes with the other |
| Nested Loop | Join method that iterates through one table and looks up matching rows in the other |
| Partition Exchange | Metadata-only operation to swap a partition with a staging table |
| SQL Monitor | Real-time SQL execution monitoring (Oracle 11g+) |
| SQL Tuning Advisor | Automated tool that recommends indexes, statistics, and query rewrites |
| Cost-Based Optimizer | Oracle's query optimizer that selects plans based on estimated cost |
| Cardinality | Estimated number of rows produced by an operation |
| Selectivity | Fraction of rows selected by a predicate (lower = more selective = better) |
| Clustering Factor | How well ordered index entries match table row order |
| Filesort | Sorting that cannot use an index (requires temp space) |
| Direct Path Read | Reading data directly into PGA (bypassing buffer cache) — common for full scans |

