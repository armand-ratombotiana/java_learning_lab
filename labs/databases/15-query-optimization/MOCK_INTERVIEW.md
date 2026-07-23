# Mock Interview: Query Optimization — Advanced (Lab 15)

**Role:** Senior Database Performance Engineer  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What are the key metrics in an AWR report that indicate performance problems?

**Candidate:** Key AWR metrics:
1. **DB Time:** Total time spent in database calls. If > CPU time, there's waiting.
2. **Top 5 Timed Events:** Where most wait time is spent (I/O, CPU, locks, network)
3. **SQL ordered by Elapsed Time:** Slowest individual queries
4. **Buffer Cache Hit Ratio:** < 90% indicates I/O bottleneck
5. **Redo Log Space Requests:** > 0 indicates redo log contention
6. **Log File Sync Wait:** High = slow COMMIT performance
7. **Enqueue Waits:** Contention for database resources
8. **Parse CPU to Total CPU Ratio:** High ratio = hard parsing overhead

**Interviewer:** How do you use ASH (Active Session History) for real-time performance diagnostics?

**Candidate:** ASH samples active sessions every second:
```sql
-- Find top wait events in last 15 minutes
SELECT event, COUNT(*)
FROM v$active_session_history
WHERE sample_time > SYSTIMESTAMP - INTERVAL '15' MINUTE
GROUP BY event
ORDER BY COUNT(*) DESC;

-- Find SQL currently consuming most time
SELECT sql_id, COUNT(*) as active_samples
FROM v$active_session_history
WHERE sample_time > SYSTIMESTAMP - INTERVAL '5' MINUTE
GROUP BY sql_id
ORDER BY COUNT(*) DESC
FETCH FIRST 10 ROWS ONLY;
```

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** What is SQL Plan Management and how does it prevent regression?

**Candidate:** SQL Plan Management (SPM) maintains a plan baseline for SQL statements:
1. **Plan capture:** New SQL generates a plan baseline (first plan)
2. **Plan evolution:** When a new plan is found (e.g., after statistics change), it's added as "accepted" or "not accepted"
3. **Plan selection:** Optimizer chooses the best plan from accepted baselines
4. **Plan verification:** Non-accepted plans are verified before acceptance

```sql
-- Load existing plans into baseline
DECLARE
    v_plans NUMBER;
BEGIN
    v_plans := DBMS_SPM.LOAD_PLANS_FROM_CURSOR_CACHE(
        attribute_name => 'SQL_TEXT',
        attribute_value => '%SELECT ... %'
    );
END;
```

Benefits: Prevents optimizer plan regression after upgrades, statistics changes, or index changes.

**Interviewer:** How do SQL Profiles and SQL Patches work?

**Candidate:** 
- **SQL Profile:** Additional statistics and optimizer directives for a specific SQL. Non-intrusive, no SQL change needed. Created by SQL Tuning Advisor.
- **SQL Patch:** Forces specific optimizer behavior (e.g., use certain join method). Creates a patch without modifying SQL text.

```sql
-- Create SQL Profile from Tuning Advisor
EXEC DBMS_SQLTUNE.CREATE_SQL_PROFILE(
    task_name => 'TUNE_TASK_001',
    name => 'PROFILE_001'
);

-- Force a specific plan using SQL Patch
EXEC DBMS_SQLDIAG.CREATE_SQL_PATCH(
    sql_id => 'abc123',
    hint_text => 'FULL(employees)',
    name => 'agent_patch'
);
```

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** A critical OLTP query that ran in 10ms suddenly takes 5 seconds. The execution plan shows an INDEX FAST FULL SCAN instead of the expected INDEX UNIQUE SCAN. Diagnose and fix.

**Candidate:** 

**Diagnosis:**
1. **Check plan history:** `SELECT * FROM dba_hist_sql_plan WHERE sql_id = ?` — when did the plan change?
2. **Check optimizer statistics:** `SELECT last_analyzed, num_rows FROM dba_tables WHERE table_name = ?` — stale stats may have caused plan change
3. **Check bind variable peeking:** `SELECT value_string FROM v$sql_bind_capture WHERE sql_id = ?` — first bind value may have skewed the plan
4. **Check parameter changes:** `optimizer_index_caching`, `optimizer_index_cost_adj` may have been altered

**Common causes:**
- Fresh statistics with different distribution
- Index became invisible/unusable
- Bind variable peeking captured an outlier value
- Database parameter changes affecting cost calculation

**Resolution:**
1. **Immediate fix:** Apply SQL Plan Baseline to pin the good plan
```sql
VARIABLE v_plan NUMBER;
EXEC :v_plan := DBMS_SPM.LOAD_PLANS_FROM_CURSOR_CACHE(
    sql_id => 'good_plan_sql_id',
    fixed => 'YES',
    enabled => 'YES'
);
```
2. **Short-term:** Create SQL Profile with hints to restore desired access path
3. **Long-term:** Fix the root cause (fresh statistics, parameter tuning)

**Interviewer:** How do you use Real-Time SQL Monitoring for long-running queries?

**Candidate:** Oracle Real-Time SQL Monitoring provides real-time execution metrics for long-running queries (> 5 seconds CPU or I/O):
```sql
-- Find monitored queries
SELECT sql_id, status, elapsed_time, cpu_time, disk_reads
FROM v$sql_monitor
WHERE status NOT IN ('DONE', 'DONE (ERROR)')
ORDER BY elapsed_time DESC;

-- Get detailed execution plan with actual rows/time
SELECT dbms_sqltune.report_sql_monitor(
    sql_id => 'abc123',
    type => 'TEXT',
    report_level => 'ALL'
) AS report FROM DUAL;
```

The report shows actual execution statistics (not estimated), making it invaluable for identifying where the query is actually spending time versus where the optimizer thinks it spends time.

---

## Interviewer Feedback

**Strengths:** Excellent performance tuning knowledge, practical AWR/ASH usage, deep SQL Plan Management understanding  
**Areas to Improve:** Could discuss Exadata-specific optimizations (Smart Scan, Storage Indexes)  
**Verdict:** Strong Hire

---

*Databases Lab 15 MOCK_INTERVIEW.md*
