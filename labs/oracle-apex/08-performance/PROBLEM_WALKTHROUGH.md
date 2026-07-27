# Problem Walkthrough: Performance

## Problem 1: Application Performance Tuning — Oracle
### APEX Interview Scenario
"Oracle's customer reports that their APEX app's dashboard page loads in 12 seconds. Optimize."

### Problem
Dashboard with 8 Interactive Reports, 3 charts, and 10 dynamic actions takes 12+ seconds to load.

### Solution Walkthrough
1. **Identify Slow Components** — Enable APEX Debug, analyze timings:
   ```
   Page Processing Time: 8.2s
   SQL Queries: 7.1s
   - IR #1 (orders): 3.2s
   - IR #2 (customers): 1.8s
   - Charts: 2.1s
   ```
2. **Optimize SQL** — Add indexes, rewrite queries:
   ```sql
   CREATE INDEX idx_orders_date ON orders(order_date);
   CREATE INDEX idx_customers_region ON customers(region_id);
   ```
3. **Lazy Loading** — Set chart regions to "Deferred Loading" (load after page render)
4. **Reduce Regions** — Combine related reports or use tab containers
5. **Cache Regions** — Set "Cache" → "Cached" with 300-second timeout for static data
6. **Optimize Dynamic Actions** — Remove unnecessary DA "onLoad" events
7. **Application Settings** — Reduce "Maximum Rows" to 100; enable pagination

### Code
```sql
-- Find slow queries from shared pool
SELECT sql_id, sql_text, elapsed_time, executions,
       ROUND(elapsed_time / DECODE(executions,0,1,executions) / 1000000, 2) AS avg_secs
FROM v$sql
WHERE sql_text LIKE '%orders%'
  AND command_type = 3
ORDER BY elapsed_time DESC
FETCH FIRST 10 ROWS ONLY;
```

### Company Evaluation
- **Oracle**: APEX debug analysis, SQL tuning, caching strategies
- **Accenture**: Performance SLA monitoring, load testing methodology
- **Deloitte**: Communicating performance improvements to stakeholders

---

## Problem 2: SQL Query Optimization — Deloitte
### APEX Interview Scenario
"Deloitte's APEX app has an Interactive Report that times out after 30 seconds on large datasets."

### Problem
An Interactive Report on `transactions` table (15M rows) with complex joins and filters times out.

### Solution Walkthrough
1. **EXPLAIN PLAN** — Analyze the current query:
   ```sql
   EXPLAIN PLAN FOR
   SELECT * FROM transactions t
   JOIN accounts a ON a.account_id = t.account_id
   JOIN customers c ON c.customer_id = a.customer_id
   WHERE t.transaction_date BETWEEN :P1_START AND :P1_END;
   ```
2. **Identify Full Table Scans** — Look for `TABLE ACCESS FULL`
3. **Add Indexes**:
   ```sql
   CREATE INDEX idx_trans_date ON transactions(transaction_date);
   CREATE INDEX idx_trans_account ON transactions(account_id);
   ```
4. **Rewrite Query** — Use materialized view for pre-joined data:
   ```sql
   CREATE MATERIALIZED VIEW mv_transactions
   REFRESH COMPLETE ON DEMAND
   AS SELECT t.*, a.account_name, c.customer_name
      FROM transactions t, accounts a, customers c
      WHERE a.account_id = t.account_id
        AND c.customer_id = a.customer_id;
   ```
5. **Use Bind Variables** — Ensure IR uses bind variables, not literals
6. **Set Pagination** — Reduce default page size to 50, enable server-side pagination

### Code
```sql
-- Before: 30-second execution
SELECT t.*, a.account_name, c.customer_name
FROM transactions t, accounts a, customers c
WHERE a.account_id = t.account_id
  AND c.customer_id = a.customer_id
  AND t.transaction_date BETWEEN '01-JAN-2026' AND '31-JAN-2026';

-- After: <1 second with MV + bind variables
SELECT * FROM mv_transactions
WHERE transaction_date BETWEEN :P1_START AND :P1_END;
```

### Company Evaluation
- **Deloitte**: Client-facing performance reporting, SLA documentation
- **Oracle**: Advanced SQL tuning, query optimizer internals
- **Accenture**: Index strategy, materialized view maintenance plans

---

## Problem 3: APEX Session & Memory Tuning — Accenture
### APEX Interview Scenario
"Accenture's client has 5,000 concurrent APEX users. The application becomes unresponsive during peak hours."

### Problem
APEX application experiences high memory usage and slow response under concurrent load.

### Solution Walkthrough
1. **Check Connection Pool** — Verify ORDS connection pool settings:
   ```
   db.connectionPool.initial: 10
   db.connectionPool.max: 50
   ```
2. **Increase Connection Pool** — `db.connectionPool.max: 200`
3. **Session Timeout** — Reduce idle session timeout:
   ```sql
   BEGIN
       APEX_INSTANCE_ADMIN.SET_PARAMETER('SESSION_TIMEOUT', 30);
   END;
   /
   ```
4. **Limit Concurrent Sessions per Schema** — Set `MAX_SESSIONS_PER_SCHEMA = 100`
5. **Enable APEX Caching** — Page and region caching:
   ```sql
   BEGIN
       APEX_CACHE.ENABLE_CACHE;
   END;
   /
   ```
6. **Tune ORDS** — Increase JVM heap:
   ```bash
   java -Xms2g -Xmx8g -jar ords.war
   ```
7. **Use CDN** — Enable APEX static files CDN to reduce server load

### Code
```sql
-- Monitor active sessions
SELECT workspace, schema, COUNT(*) AS active_sessions
FROM APEX_WORKSPACE_SESSIONS
WHERE session_state = 'ACTIVE'
GROUP BY workspace, schema
ORDER BY active_sessions DESC;

-- Check connection pool waits
SELECT name, current_count, max_count, in_use_count,
       (current_count - in_use_count) AS available
FROM v$resource_limit
WHERE name LIKE '%processes%';
```

### Company Evaluation
- **Accenture**: Capacity planning, performance testing, production monitoring
- **Oracle**: APEX cache architecture, session management internals
- **Deloitte**: Performance reporting dashboards, capacity forecasting

---

## Problem 4: APEX Debug Mode & Diagnostics — Oracle
### APEX Interview Scenario
"Oracle support receives a ticket: a specific APEX page returns inconsistent results. Debug it."

### Problem
Page 50 (Order Entry) works for some users but returns wrong totals for others. No error messages.

### Solution Walkthrough
1. **Enable APEX Debug** — Add `?p_debug=YES` to URL or use Developer Toolbar
2. **Analyze Debug Log** — Check SQL bind values, branch logic, computation order
3. **Common Findings**:
   - Wrong session state (item not cleared between users)
   - Cached region returning stale data for different users
   - Branch conditions using wrong app item
4. **Check Authorization** — Verify authorization scheme doesn't skip computations
5. **Isolate with Test Case** — Create two test users, run identical transactions
6. **Review Page Processing Order** — Computations → Validations → Processes → Branches
7. **Fix** — Clear region cache or use `NO_CACHE` for user-specific data

### Code
```sql
-- View debug log for specific session
SELECT elap, msg_text
FROM APEX_DEBUG_MESSAGES
WHERE flow_step = 50
  AND application_id = 100
  AND session_id = :APP_SESSION
ORDER BY seq_id;

-- Clear cached regions for a page
BEGIN
    APEX_CACHE.PURGE_CACHE(
        p_application_id => 100,
        p_page_id        => 50
    );
END;
/

-- Check computation order
SELECT computation_name, computation_item, computation_type,
       computation_point, static_assignment
FROM APEX_APPLICATION_PAGE_COMP
WHERE application_id = 100
  AND page_id = 50
ORDER BY computation_id;
```

### Company Evaluation
- **Oracle**: Debug log interpretation, systematic problem isolation
- **Accenture**: Root cause analysis documentation, reproducible test cases
- **Deloitte**: User impact assessment, prioritization of fixes
