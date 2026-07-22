# Monitoring and Alerting: Oracle Batch Job Performance

**Incident**: INC-2024-0810-BATCH
**Category**: Database Query Observability
**Author**: DBA Team + Application Team

## Overview

This document specifies the monitoring, metrics, and alerting rules required to detect slow queries, missing indexes, and batch job performance regression before they cause batch failures.

## Metrics Collection

### 1. Batch Job Metrics (Critical)

```
For each batch job execution:
├── batch.job.duration           — Total elapsed time (seconds)
├── batch.job.rows.processed     — Number of rows affected
├── batch.job.rows.expected      — Expected row count (baseline)
├── batch.job.status             — SUCCESS / FAILED / TIMEOUT
├── batch.job.temp.used          — Temp tablespace usage (MB)
├── batch.job.disk.reads         — Physical reads
├── batch.job.buffer.gets        — Logical reads
└── batch.job.cpu.time           — CPU time (seconds)
```

### 2. Database Query Metrics (Critical)

```
Oracle AWR/ASH Metrics:
├── SQL elapsed time             — Total elapsed for each SQL ID
├── SQL CPU time                 — CPU time for each SQL ID
├── SQL disk reads               — Physical reads
├── SQL buffer gets              — Logical reads
├── SQL executions               — Execution count
├── SQL temp space               — Temp space per execution
└── SQL plan hash value          — Execution plan identifier

Per execution plan:
├── Full table scan count        — Tables accessed via full scan
├── Index range scan count       — Tables accessed via index
├── Cartesian product flag       — Join without condition
├── Temp space usage             — Intermediate result spill
└── Cost estimate                — Optimizer cost
```

### 3. Database Health Metrics (High)

```
Oracle Instance Metrics:
├── session.count                — Active sessions
├── session.active               — Currently executing SQL
├── temp.used / temp.total       — Temp tablespace utilization
├── pga.used / pga.total         — PGA memory usage
├── redo.size                    — Redo generation rate
├── db.block.changes             — Block change rate
├── enqueue.deadlocks            — Deadlock count
└── latch.miss.rate              — Latch contention rate
```

## Alerting Rules

### P0/Critical Alarms (Page On-Call)

| Alert Name | Condition | Duration | Runbook |
|------------|-----------|----------|---------|
| BatchJobFailed | `batch.job.status == 'FAILED'` | 0 min | Investigate and restart |
| BatchJobDurationCritical | `batch.job.duration > 4h` | 0 min | Check for performance issue |
| TempSpaceCritical | `temp.used / temp.total > 0.9` | 2 min | Identify top SQL by temp |
| FullTableScanCritical | Full table scan on table > 10M rows | 5 min | Missing index investigation |

### P1/Warning Alarms (Alert during business hours)

| Alert Name | Condition | Duration | Action |
|------------|-----------|----------|--------|
| BatchJobDurationWarning | `batch.job.duration > 2h` | 5 min | Check execution plan |
| TempSpaceWarning | `temp.used / temp.total > 0.7` | 5 min | Monitor temp growth |
| ExecutionPlanChanged | Plan hash value changed for batch SQL | 0 min | Compare old vs new plan |
| SQLDiskReadsHigh | `disk.reads > 1M` per execution | 10 min | Check for missing index |

### P2/Info Alarms (Dashboard / Report)

| Alert Name | Condition | Action |
|------------|-----------|--------|
| RowCountDeviation | `batch.job.rows.processed` deviates > 20% from `rows.expected` | Verify data volume |
| NewFullTableScan | New full table scan on large table detected | Review index strategy |
| IndexUsageMissing | Index created > 30 days ago and not used | Drop unused index |
| P95DurationIncrease | Job duration increased > 20% vs 30-day P95 | Investigate regression |

## Dashboard: Batch Job Performance

### Panel 1: Batch Job Duration (Time Series)
```sql
SELECT
  batch.job.duration{job_name="$job"} AS "Duration (s)"
FROM batch_metrics
WHERE $__timeFilter
```
Visual: Line chart with baseline line at P95

### Panel 2: Top SQL by Elapsed Time (Table)
```sql
SELECT
  sql_id,
  elapsed_time / 1000000 AS elapsed_seconds,
  disk_reads,
  temp_space_used_mb,
  plan_hash_value
FROM v$sql_monitor
WHERE status = 'EXECUTING'
ORDER BY elapsed_time DESC
```

### Panel 3: Temp Space Usage (Time Series)
```sql
SELECT
  temp.used{instance="$instance"} AS "Used (MB)",
  temp.total{instance="$instance"} AS "Total (MB)"
FROM oracle_metrics
WHERE $__timeFilter
```

### Panel 4: Full Table Scan Detections (Table)
```sql
SELECT
  sql_id,
  object_owner || '.' || object_name AS table_name,
  options AS operation,
  cost,
  cardinality
FROM v$sql_plan
WHERE operation = 'TABLE ACCESS'
  AND options = 'FULL'
  AND cardinality > 1000000
```

## AWR Configuration

```sql
-- Configure AWR snapshot interval (default 60 min)
BEGIN
    DBMS_WORKLOAD_REPOSITORY.MODIFY_SNAPSHOT_SETTINGS(
        retention  => 43200,  -- 30 days
        interval   => 30      -- 30 minutes (more granular)
    );
END;
/

-- Generate AWR report for batch window
@?/rdbms/admin/awrrpt.sql
-- Select: days, begin_snap, end_snap, output format

-- Generate AWR for specific SQL
@?/rdbms/admin/awrsqrpt.sql
-- Enter SQL ID when prompted
```

## ASH (Active Session History) Query

```sql
-- Find top wait events during batch window
SELECT
    TO_CHAR(sample_time, 'HH24:MI') AS time_slot,
    COUNT(*) AS session_count,
    event,
    sql_id
FROM v$active_session_history
WHERE sample_time BETWEEN TIMESTAMP '2024-08-10 22:00:00'
                      AND TIMESTAMP '2024-08-11 06:00:00'
GROUP BY
    TO_CHAR(sample_time, 'HH24:MI'),
    event,
    sql_id
ORDER BY time_slot, session_count DESC;

-- Find SQL by temp space usage
SELECT sql_id,
       SUM(temp_space_allocated) / 1048576 AS temp_mb,
       COUNT(*) AS executions
FROM v$active_session_history
WHERE temp_space_allocated > 0
GROUP BY sql_id
ORDER BY temp_mb DESC;
```

## SQL Monitor Reports

```sql
-- Real-time SQL monitoring (for currently running queries)
SELECT
    dbms_sqltune.report_sql_monitor(
        sql_id => '6g8k2x3a1b4c',
        type   => 'TEXT',
        report_level => 'ALL'
    ) AS report
FROM dual;

-- Check for SQLs using temp space
SELECT sql_id,
       ROUND(temp_space_allocated / 1048576, 2) AS temp_mb,
       ROUND(elapsed_time / 1000000, 2) AS elapsed_secs,
       cpu_time / 1000000 AS cpu_secs,
       disk_reads,
       buffer_gets
FROM v$sql_monitor
WHERE temp_space_allocated > 104857600  -- > 100MB
ORDER BY temp_space_allocated DESC;
```

## Automated Index Detection

```sql
-- Find tables with full table scans that could benefit from indexes
SELECT
    ROUND(s.elapsed_time_total / 1000000) AS total_elapsed_seconds,
    s.sql_id,
    o.owner || '.' || o.object_name AS table_name,
    o.cardinality AS rows_estimated,
    p.options
FROM dba_hist_sqlstat s
JOIN dba_hist_sql_plan p ON s.sql_id = p.sql_id
JOIN dba_hist_sqltext t ON s.sql_id = t.sql_id
JOIN dba_objects o ON p.object# = o.object_id
WHERE p.operation = 'TABLE ACCESS'
  AND p.options = 'FULL'
  AND o.cardinality > 1000000  -- > 1M rows
  AND s.elapsed_time_total > 60000000000  -- > 60 seconds total
ORDER BY s.elapsed_time_total DESC;
```

## References

- Oracle: "AWR Reports" — Oracle Database Performance Tuning Guide
- Oracle: "SQL Monitor" — Oracle Real-Time SQL Monitoring
- Oracle: "Active Session History (ASH)" — Oracle Documentation
- Oracle: "Automatic Indexing" — Oracle Database 19c New Features
- Grafana: "Oracle Database Dashboard" — Grafana Dashboard Repository
- Prometheus: "Oracle DB Exporter" — Oracle Prometheus Exporter

