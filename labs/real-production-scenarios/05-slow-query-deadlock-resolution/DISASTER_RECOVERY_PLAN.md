# Lab 05 — Slow Query / Deadlock: Disaster Recovery Plan

## Recovery Objectives

| Metric | Target |
|--------|--------|
| RTO | 30 minutes (add index + query hint) |
| RPO | 0 (batch is replayable — no data loss) |
| MTD | 8 hours (overlaps next processing window) |

## Scenarios

### Scenario A: Missing Index — Batch Slow

**Trigger:** Batch job exceeds 2x normal duration
**Recovery:**
1. Kill the current batch job
2. Analyze AWR to identify the problematic SQL
3. Identify missing index from execution plan
4. Create the index (online if possible)
5. Restart the batch job with query hint as backup

### Scenario B: Plan Regression

**Trigger:** Batch suddenly slow after optimizer stats update
**Recovery:**
1. Identify the plan hash value change
2. Create SQL Plan Baseline with the known-good plan
3. Restart batch job — it will use the baseline plan
4. Investigate why optimizer chose the bad plan

### Scenario C: Deadlock During Batch

**Trigger:** Batch job deadlocks with concurrent session
**Recovery:**
1. Identify the blocked session via `SELECT * FROM v$session WHERE blocking_session IS NOT NULL`
2. Kill the blocking or blocked session (whichever is less impactful)
3. Analyze the deadlock graph in alert log
4. Determine root cause (usually lock ordering in application)
5. Restart the killed job from last checkpoint

### Scenario D: Temp Space Full

**Trigger:** Query spills to disk, temp tablespace fills
**Recovery:**
1. Add tempfile to temp tablespace: `ALTER TABLESPACE temp ADD TEMPFILE`
2. Kill the offending query
3. Fix the cause (missing index, Cartesian product)
4. Restart the batch

## Runbook

```yaml
symptoms:
  - "Batch job duration > 2x normal"
  - "Temp space > 1GB"
  - "Disk reads > 1M per query"

diagnosis:
  - "Generate AWR: @$ORACLE_HOME/rdbms/admin/awrrpt.sql"
  - "Find top SQL: v$sqlstats ORDER BY elapsed_time"
  - "Check plan: SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY_CURSOR('sql_id'))"
  - "Check stats: SELECT last_analyzed FROM dba_tables WHERE table_name=?"
  - "Check indexes: SELECT index_name, column_name FROM dba_ind_columns"

mitigation:
  - "Create missing index: CREATE INDEX ... ONLINE;"
  - "Force plan with hint: /*+ LEADING(...) USE_NL(...) */"
  - "Kill and restart bad query"
  - "Capture baseline: DBMS_SQLTUNE.CREATE_SQL_PLAN_BASELINE"

fix:
  - "Add composite index on JOIN columns"
  - "Rewrite query for optimal join order"
  - "Set up AWR baselines for critical queries"
  - "Add optimizer stats refresh schedule"
```
