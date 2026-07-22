# Incident Response Runbook: Slow Query / Batch Job Failure

**Incident Type**: Database Slow Query / Batch Job Performance
**Severity**: P1-P2 (SEV-2/3)
**Response Time**: < 5 minutes for initial triage

## 1. DETECTION AND TRIAGE

### 1.1 Verify the Alert
- [ ] Confirm alert: "BatchJobFailed", "BatchJobDurationCritical", or scheduled job timeout
- [ ] Check batch scheduler (Control-M, TWS, Autosys) for job status
- [ ] Check application logs for the batch job
- [ ] Check database alert log for errors
- [ ] Check incident history: has this job failed before?

### 1.2 Assess Impact
- [ ] Which batch jobs are blocked downstream?
- [ ] What is the business impact? (financial reporting, data processing)
- [ ] Is there a manual workaround? (run a different job, skip to next step)
- [ ] What is the deadline for this batch processing?
- [ ] Notify batch operations team
- [ ] Declare incident severity

### 1.3 Initial Mitigation
- [ ] Kill the stuck query: identify spid from v$session, `ALTER SYSTEM KILL SESSION 'sid,serial#'`
- [ ] Free temp space: identify and kill sessions using temp
- [ ] Restart the batch job if it can be safely restarted from checkpoint
- [ ] If restart is not possible: skip to next batch step, process this step manually

## 2. DATA COLLECTION

### 2.1 Collect Execution Information
- [ ] Get SQL ID of the problematic query: from v$sql_monitor or v$session
- [ ] Get execution plan: `SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY_CURSOR('sql_id'))`
- [ ] Get AWR report for the batch window: `@?/rdbms/admin/awrrpt.sql`
- [ ] Get SQL Monitor report: `SELECT DBMS_SQLTUNE.REPORT_SQL_MONITOR(sql_id => '...') FROM dual`
- [ ] Get ASH data: check wait events during execution

### 2.2 Collect Performance Metrics
- [ ] Check full table scans in execution plan
- [ ] Check temp space used: `SELECT * FROM v$sql_workarea WHERE sql_id = '...'`
- [ ] Check disk reads / buffer gets: `SELECT * FROM v$sql WHERE sql_id = '...'`
- [ ] Check PGA and temp usage: `SELECT * FROM v$sgastat`

### 2.3 Check Table Statistics
- [ ] Are table statistics current? `SELECT last_analyzed FROM dba_tables WHERE table_name = '...'`
- [ ] Check table size: `SELECT num_rows, blocks FROM dba_tables WHERE table_name = '...'`
- [ ] Check existing indexes: `SELECT index_name, column_name, column_position FROM dba_ind_columns WHERE table_name = '...'`
- [ ] Check missing indexes: any columns in JOIN or WHERE without indexes?

### 2.4 Check Schema Changes
- [ ] Check recent DDL changes: `SELECT * FROM dba_ddl_actions WHERE timestamp > SYSDATE - 7`
- [ ] Check if any columns were added to tables in the query
- [ ] Check if any indexes were dropped
- [ ] Check if statistics were reset or changed

## 3. ROOT CAUSE ANALYSIS

### 3.1 Execution Plan Analysis
- [ ] Identify full table scans on large tables (> 1M rows)
- [ ] Identify hash joins on large tables without indexes
- [ ] Identify Cartesian products (no join condition)
- [ ] Identify temp space usage (spill to disk)
- [ ] Compare with known good execution plan (if available)

### 3.2 Index Analysis
- [ ] Are JOIN columns indexed on both sides?
- [ ] Are WHERE clause columns indexed?
- [ ] Are ORDER BY / GROUP BY columns indexed?
- [ ] Are composite indexes designed with correct column order?

### 3.3 Statistics Analysis
- [ ] Are table statistics up to date? (last_analyzed > SYSDATE-7)
- [ ] Are index statistics up to date?
- [ ] Is histogram data available for skewed columns?
- [ ] Was a statistics gathering job missed?

### 3.4 Query Structure
- [ ] Is there an appropriate LIMIT / ROWNUM clause?
- [ ] Are there unnecessary columns in SELECT?
- [ ] Are there unnecessary tables in JOIN?
- [ ] Can the query be rewritten more efficiently?

## 4. FIX AND VERIFICATION

### 4.1 Immediate Fix Options
#### Option A: Create Missing Index (Fastest)
- [ ] Identify the missing index column
- [ ] Create index ONLINE (production) with NOLOGGING
- [ ] Gather statistics on the new index

#### Option B: Query Rewrite with Hints
- [ ] Identify correct join order
- [ ] Add LEADING hint to force join order
- [ ] Add INDEX hint to force index usage
- [ ] Add USE_NL / USE_HASH hints for join method

#### Option C: Update Statistics
- [ ] Gather table statistics: `EXEC DBMS_STATS.GATHER_TABLE_STATS(...)`
- [ ] If statistics are stale, fresh statistics may fix the plan

### 4.2 Verify Fix
- [ ] Run EXPLAIN PLAN — verify index usage
- [ ] Run the query with a small LIMIT — verify correct results
- [ ] Run the full query — measure elapsed time
- [ ] Verify temp space usage is 0 or minimal
- [ ] Compare before/after execution plans

### 4.3 Deploy Fix
- [ ] Apply index change to production
- [ ] Apply query change to batch code
- [ ] Restart batch job at appropriate checkpoint
- [ ] Monitor job completion

## 5. PREVENTIVE MEASURES

### 5.1 Monitoring
- [ ] Add batch job duration alerts
- [ ] Add temp space monitoring and alerts
- [ ] Add full table scan detection for large tables
- [ ] Add execution plan change detection

### 5.2 Process
- [ ] Add index review to schema change process
- [ ] Add execution plan check to CI/CD pipeline
- [ ] Add performance baseline comparison to deployment

### 5.3 Database
- [ ] Set up regular statistics gathering job
- [ ] Set up index usage monitoring
- [ ] Set up automatic SQL Tuning Advisor execution
- [ ] Consider partitioning for large tables

## 6. POSTMORTEM

- [ ] Complete INCIDENT_REPORT.md
- [ ] Complete ROOT_CAUSE.md with 5 Whys
- [ ] Complete PREVENTION.md
- [ ] Review with DBA and development teams
- [ ] Update batch job runbook

## Key Metrics Reference

| Metric | Healthy | Warning | Critical | Action |
|--------|---------|---------|----------|--------|
| Job Duration | < 60 min | 2-4 hours | > 4 hours | Check execution plan |
| Temp Space | < 100MB | 100MB-1GB | > 1GB | Missing index likely |
| Full Table Scans | 0 (large tables) | 1-2 | 3+ | Missing index detected |
| Disk Reads | < 100K | 100K-1M | > 1M | Query not optimized |
| Plan Hash Change | Same | Different | Different | Compare plans |

## Tools Reference

| Tool | Command | Purpose |
|------|---------|---------|
| DBMS_XPLAN | `DBMS_XPLAN.DISPLAY_CURSOR('sql_id')` | Execution plan |
| AWR | `@?/rdbms/admin/awrrpt.sql` | Workload report |
| SQL Monitor | `DBMS_SQLTUNE.REPORT_SQL_MONITOR` | Real-time SQL monitoring |
| ASH | `SELECT * FROM v$active_session_history` | Session history |
| SQL Tuning Advisor | `DBMS_SQLTUNE.CREATE_TUNING_TASK` | Automated tuning |
| SQL Developer | GUI tool | Visual plan analysis |
| TOAD | GUI tool | DBA toolkit |

