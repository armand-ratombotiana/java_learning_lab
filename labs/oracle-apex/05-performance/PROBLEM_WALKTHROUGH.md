# Problem Walkthrough: Optimize APEX Page with Caching, Collections, and Bulk Operations

## Problem Statement

A large enterprise APEX application with 10,000+ concurrent users is experiencing performance degradation. The most critical page — an Executive Dashboard — takes 30+ seconds to load and frequently times out. The dashboard includes:

1. **10 interactive reports** pulling from tables with 5M+ rows each
2. **6 chart regions** with complex aggregated queries
3. **3 classic reports** showing year-over-year comparisons
4. **Multiple cascading filters** (region, branch, product line, date range)
5. **CSV export** functionality for each report (currently using APEX built-in export)

The client needs optimization to achieve:
- Page load under 3 seconds (p95)
- Dashboard filter changes under 1 second
- CSV export of 100K rows under 30 seconds
- Minimal database impact during peak hours (9-10 AM, 2-3 PM)

### Success Criteria
- Query execution time reduced by 80%
- Page rendering time under 3 seconds
- No timeout errors during peak load
- All charts render within 2 seconds of filter change
- Export completes without ORA-01555 (snapshot too old) errors

---

## Step-by-Step Walkthrough

### Step 1: Diagnose Performance Bottlenecks

Before optimizing, identify the actual bottlenecks:

```sql
-- 1. Monitor page performance
SELECT page_id, page_name, avg_elapsed, avg_db_time,
       avg_render_time, page_views
FROM apex_workspace_activity_log
WHERE application_id = :APP_ID
  AND page_id = 10
  AND access_date > SYSDATE - 7
ORDER BY access_date DESC;

-- 2. Find slow queries (from AWR/ASH)
SELECT sql_id, sql_text, elapsed_time_total / executions AS avg_elapsed,
       buffer_gets_total / executions AS avg_buffer_gets
FROM dba_hist_sqlstat NATURAL JOIN dba_hist_sqltext
WHERE executions > 0
  AND elapsed_time_total / executions > 1000000 -- > 1 second
  AND UPPER(sql_text) LIKE '%FROM SALES%'
ORDER BY avg_elapsed DESC;

-- 3. Check APEX cache hit rates
SELECT cache_name, cache_hits, cache_misses,
       ROUND(cache_hits * 100.0 / (cache_hits + cache_misses), 2) AS hit_ratio
FROM apex_application_cache_stats
WHERE application_id = :APP_ID;
```

### Step 2: Database Optimization

**2.1 Add Missing Indexes**

```sql
-- Analyze query patterns and add targeted indexes
CREATE INDEX idx_sales_region_date ON sales(region_id, sale_date);
CREATE INDEX idx_sales_product_date ON sales(product_id, sale_date);
CREATE INDEX idx_sales_status_date ON sales(status, sale_date);
CREATE INDEX idx_inventory_warehouse ON inventory(warehouse_id, product_id);

-- Composite index for the most common filter combination
CREATE INDEX idx_sales_region_branch_date
    ON sales(region_id, branch_id, sale_date)
    LOCAL; -- Use partitioning if table is partitioned

-- Function-based index for year-over-year comparison
CREATE INDEX idx_sales_year_month ON sales(
    EXTRACT(YEAR FROM sale_date),
    EXTRACT(MONTH FROM sale_date)
);
```

**2.2 Create Materialized Views**

```sql
-- Pre-aggregated sales by region, branch, and day
CREATE MATERIALIZED VIEW mv_sales_daily
REFRESH COMPLETE ON DEMAND
ENABLE QUERY REWRITE
AS
SELECT
    region_id,
    branch_id,
    TRUNC(sale_date) AS sale_day,
    product_category,
    COUNT(*) AS transaction_count,
    SUM(quantity) AS total_quantity,
    SUM(amount) AS total_amount,
    SUM(amount * cost_ratio) AS total_cost,
    COUNT(DISTINCT customer_id) AS unique_customers
FROM sales s
JOIN products p ON p.product_id = s.product_id
GROUP BY region_id, branch_id, TRUNC(sale_date), product_category;

-- Create dimensions for star query optimization
CREATE MATERIALIZED VIEW mv_product_totals_ytd
REFRESH COMPLETE ON DEMAND
AS
SELECT
    product_category,
    SUM(amount) AS ytd_sales,
    SUM(quantity) AS ytd_units,
    COUNT(*) AS ytd_transactions
FROM sales
WHERE sale_date >= TRUNC(SYSDATE, 'YEAR')
GROUP BY product_category;

-- Monthly summary for year-over-year
CREATE MATERIALIZED VIEW LOG ON sales WITH ROWID, SEQUENCE
    (amount, quantity, region_id, branch_id, product_id)
    INCLUDING NEW VALUES;

CREATE MATERIALIZED VIEW mv_monthly_summary
REFRESH FAST ON DEMAND
AS
SELECT
    TRUNC(sale_date, 'MM') AS month,
    region_id,
    branch_id,
    COUNT(*) AS transactions,
    SUM(amount) AS revenue,
    SUM(quantity) AS units
FROM sales
GROUP BY TRUNC(sale_date, 'MM'), region_id, branch_id;
```

**2.3 Partition Large Tables**

```sql
-- Partition sales table by month for partition pruning
CREATE TABLE sales_partitioned (
    sale_id      NUMBER,
    sale_date    DATE NOT NULL,
    region_id    NUMBER,
    branch_id    NUMBER,
    product_id   NUMBER,
    amount       NUMBER(12,2),
    quantity     NUMBER,
    customer_id  NUMBER
) PARTITION BY RANGE (sale_date) INTERVAL (INTERVAL '1' MONTH) (
    PARTITION p_before_2025 VALUES LESS THAN (DATE '2025-01-01')
);

-- Create indexes as LOCAL (partition-aware)
CREATE INDEX idx_sales_region_date_local
    ON sales_partitioned(region_id, sale_date) LOCAL;
```

### Step 3: APEX Region Caching

**3.1 Enable Caching on Slow Regions**

For each report region on the dashboard, enable **Caching**:

1. Edit region → **Advanced** → **Caching**
2. **Cache**: **Enabled**
3. **Cache Type**: **Session Cache** (filtered by user) or **Application Cache** (shared across users)

   For charts that are the same for all users:
   - Cache Type: **Application Cache**
   - Cache Lifetime: **600 seconds** (10 minutes)
   - Cache Key: `DASHBOARD_CHART_1`

   For user-specific reports:
   - Cache Type: **Session Cache**
   - Cache Lifetime: **Until Session End** (or 300 seconds)

4. **Cache on Client**: Enable (use `apex.message.hideLoading` on cached regions for instant display)

**3.2 Manual Cache Invalidation**

Create a process to invalidate cache when underlying data changes:

```sql
-- Application Process: INVALIDATE_DASHBOARD_CACHE
BEGIN
    -- Clear application-level cache for dashboard regions
    APEX_CACHE.REMOVE_REGION_CACHE(
        p_application_id => :APP_ID,
        p_page_id        => 10,
        p_region_id      => 100  -- Chart region 1
    );
    APEX_CACHE.REMOVE_REGION_CACHE(
        p_application_id => :APP_ID,
        p_page_id        => 10,
        p_region_id      => 101  -- Chart region 2
    );
    -- Clear session cache for all users (for shared regions)
    APEX_UTIL.CLEAR_APP_CACHE(p_application_id => :APP_ID);
END;
```

**3.3 Server-Side Region Caching with Function Body**

For complex computations, use a Function Body as the region source with caching:

```sql
-- Region Source (PL/SQL Function Body returning SQL Query)
DECLARE
    l_cache_key VARCHAR2(200);
    l_result    CLOB;
BEGIN
    l_cache_key := 'KPI_SUMMARY_' || :P10_REGION || '_' || :P10_DATE_RANGE;

    -- Check application cache first
    l_result := APEX_CACHE.GET(
        p_cache_id => l_cache_key,
        p_cache_type => APEX_CACHE.C_CACHE_TYPE_APPLICATION
    );

    IF l_result IS NULL THEN
        -- Compute the query
        l_result := 'SELECT ' ||
            '(SELECT SUM(amount) FROM sales WHERE ' ||
            get_date_filter(:P10_DATE_RANGE) || ') AS total_revenue, ' ||
            '(SELECT COUNT(*) FROM customers) AS total_customers ' ||
            'FROM DUAL';

        -- Store in cache for 5 minutes
        APEX_CACHE.SET(
            p_cache_id => l_cache_key,
            p_content  => l_result,
            p_cache_type => APEX_CACHE.C_CACHE_TYPE_APPLICATION,
            p_expire_after => 300
        );
    END IF;

    RETURN l_result;
END;
```

### Step 4: APEX Collections for Temporary Data

Use collections to avoid repeated expensive queries and to implement batch processing.

**4.1 Create Collection for Filtered Dataset**

Instead of running the same query for each chart, run it once and store in a collection:

```sql
-- Before page render: build filtered dataset
DECLARE
    l_filtered_total NUMBER;
BEGIN
    -- Clear existing collection for this session
    APEX_COLLECTION.DELETE_COLLECTION('DASHBOARD_DATA');

    -- Create collection structure
    APEX_COLLECTION.CREATE_COLLECTION(
        p_collection_name => 'DASHBOARD_DATA'
    );

    -- Populate with filtered data (single query, multiple uses)
    INSERT INTO APEX_COLLECTIONS (
        collection_name,
        seq_id,
        c001, -- region
        c002, -- branch
        n001, -- revenue
        n002, -- transactions
        d001  -- sale_date
    )
    SELECT
        'DASHBOARD_DATA',
        ROWNUM,
        r.region_name,
        b.branch_name,
        SUM(s.amount),
        COUNT(*),
        TRUNC(s.sale_date)
    FROM sales s
    JOIN regions r ON r.region_id = s.region_id
    JOIN branches b ON b.branch_id = s.branch_id
    WHERE s.sale_date BETWEEN :P10_START_DATE AND :P10_END_DATE
      AND (:P10_REGION IS NULL OR s.region_id = :P10_REGION)
    GROUP BY r.region_name, b.branch_name, TRUNC(s.sale_date);
END;
```

**4.2 Use Collection in Reports**

Instead of querying the base tables, report regions can reference the collection:

```sql
-- Report 1: Revenue by Region
SELECT c001 AS region,
       SUM(n001) AS revenue,
       SUM(n002) AS transactions
FROM apex_collections
WHERE collection_name = 'DASHBOARD_DATA'
GROUP BY c001
ORDER BY revenue DESC;

-- Report 2: Daily Trend
SELECT d001 AS sale_date,
       SUM(n001) AS revenue
FROM apex_collections
WHERE collection_name = 'DASHBOARD_DATA'
GROUP BY d001
ORDER BY d001;

-- Report 3: Top Branches
SELECT c001 AS region,
       c002 AS branch,
       SUM(n001) AS revenue
FROM apex_collections
WHERE collection_name = 'DASHBOARD_DATA'
GROUP BY c001, c002
ORDER BY revenue DESC;
```

**4.3 Collection-Based Report Template**

Create a **Function Body** region source that references the collection:

```sql
BEGIN
    -- Return appropriate SQL for each region
    CASE :APP_REGION_ID
        WHEN 100 THEN -- Revenue by Region
            RETURN 'SELECT c001 AS region, SUM(n001) AS revenue
                    FROM apex_collections
                    WHERE collection_name = ''DASHBOARD_DATA''
                    GROUP BY c001 ORDER BY revenue DESC';
        WHEN 101 THEN -- Daily Trend
            RETURN 'SELECT d001 AS day, SUM(n001) AS revenue
                    FROM apex_collections
                    WHERE collection_name = ''DASHBOARD_DATA''
                    GROUP BY d001 ORDER BY d001';
        ELSE
            RETURN 'SELECT 1 AS col FROM DUAL WHERE 1=0';
    END CASE;
END;
```

### Step 5: Bulk Operations Using Collections

**5.1 Batch Process for CSV Export**

Replace the built-in Interactive Report export with a bulk collection-based export:

```sql
CREATE OR REPLACE PROCEDURE export_dashboard_csv(
    p_start_date    IN DATE,
    p_end_date      IN DATE,
    p_region_id     IN NUMBER DEFAULT NULL
) IS
    l_csv CLOB;
    l_collection_name VARCHAR2(100) := 'EXPORT_DATA';
BEGIN
    -- Create collection from query
    APEX_COLLECTION.CREATE_COLLECTION_FROM_QUERY_B(
        p_collection_name => l_collection_name,
        p_query => 'SELECT
                        r.region_name,
                        b.branch_name,
                        s.sale_date,
                        s.amount,
                        s.quantity,
                        p.product_name,
                        c.customer_name
                    FROM sales s
                    JOIN regions r ON r.region_id = s.region_id
                    JOIN branches b ON b.branch_id = s.branch_id
                    JOIN products p ON p.product_id = s.product_id
                    JOIN customers c ON c.customer_id = s.customer_id
                    WHERE s.sale_date BETWEEN :start AND :end
                      AND (:region IS NULL OR s.region_id = :region)',
        p_query_params => json_object(
            'start' VALUE p_start_date,
            'end' VALUE p_end_date,
            'region' VALUE p_region_id
        )
    );

    -- Build CSV from collection
    l_csv := 'Region,Branch,Date,Amount,Quantity,Product,Customer' || UTL_CHR(10);
    FOR rec IN (
        SELECT c001 AS region, c002 AS branch, d001 AS sale_date,
               n001 AS amount, n002 AS quantity,
               c005 AS product, c006 AS customer
        FROM apex_collections
        WHERE collection_name = l_collection_name
        ORDER BY d001
    ) LOOP
        l_csv := l_csv || '"' || rec.region || '",' ||
                           '"' || rec.branch || '",' ||
                           TO_CHAR(rec.sale_date, 'YYYY-MM-DD') || ',' ||
                           rec.amount || ',' ||
                           rec.quantity || ',' ||
                           '"' || rec.product || '",' ||
                           '"' || rec.customer || '"' || UTL_CHR(10);
    END LOOP;

    -- Send as file download
    APEX_UTIL.DOWNLOAD_PRINT_DOCUMENT(
        p_content  => l_csv,
        p_filename => 'dashboard_export_' ||
                      TO_CHAR(SYSDATE, 'YYYYMMDD_HH24MISS') || '.csv',
        p_mime_type => 'text/csv'
    );

    -- Clean up collection
    APEX_COLLECTION.DELETE_COLLECTION(l_collection_name);
END export_dashboard_csv;
/
```

**5.2 Batch Update Process**

```sql
CREATE OR REPLACE PROCEDURE batch_update_shipments(
    p_tracking_list IN APEX_T_VARCHAR2,
    p_new_status    IN VARCHAR2
) IS
    l_collection_name VARCHAR2(100) := 'BATCH_UPDATE';
BEGIN
    -- Load tracking numbers into collection
    APEX_COLLECTION.CREATE_COLLECTION(l_collection_name);
    FOR i IN 1..p_tracking_list.COUNT LOOP
        APEX_COLLECTION.ADD_MEMBER(
            p_collection_name => l_collection_name,
            p_c001 => p_tracking_list(i)
        );
    END LOOP;

    -- Bulk update using collection join (single SQL statement)
    UPDATE shipments s
    SET s.status = p_new_status,
        s.updated_date = SYSDATE
    WHERE s.tracking_number IN (
        SELECT c001 FROM apex_collections
        WHERE collection_name = l_collection_name
    );

    -- Log the batch operation
    INSERT INTO batch_log VALUES (
        batch_log_seq.NEXTVAL,
        'STATUS_UPDATE',
        p_new_status,
        SQL%ROWCOUNT || ' rows updated',
        :APP_USER,
        SYSDATE
    );

    APEX_COLLECTION.DELETE_COLLECTION(l_collection_name);
    COMMIT;
END batch_update_shipments;
/
```

### Step 6: Query Optimization Techniques

**6.1 Use WITH Clause for Reusable Subqueries**

```sql
WITH filtered_sales AS (
    SELECT /*+ MATERIALIZE */
        s.sale_id, s.region_id, s.branch_id,
        s.product_id, s.amount, s.quantity, s.sale_date
    FROM sales s
    WHERE s.sale_date BETWEEN :P10_START_DATE AND :P10_END_DATE
      AND (:P10_REGION IS NULL OR s.region_id = :P10_REGION)
      AND (:P10_BRANCH IS NULL OR s.branch_id = :P10_BRANCH)
),
region_agg AS (
    SELECT region_id, SUM(amount) AS revenue, COUNT(*) AS transactions
    FROM filtered_sales
    GROUP BY region_id
),
product_agg AS (
    SELECT product_id, SUM(quantity) AS units, SUM(amount) AS revenue
    FROM filtered_sales
    GROUP BY product_id
)
-- Main query references the materialized CTEs
SELECT r.region_name, ra.revenue, ra.transactions,
       pa.product_name, pa.units
FROM region_agg ra
JOIN regions r ON r.region_id = ra.region_id
CROSS JOIN (SELECT p.product_name, pa.units, pa.revenue
            FROM product_agg pa
            JOIN products p ON p.product_id = pa.product_id
            ORDER BY pa.revenue DESC
            FETCH FIRST 5 ROWS ONLY) pa
ORDER BY ra.revenue DESC;
```

**6.2 Use APPROX_COUNT_DISTINCT for Approximate Cardinality**

```sql
-- Exact count (slow on large tables)
SELECT COUNT(DISTINCT customer_id) FROM sales WHERE ...

-- Approximate count (fast, ~1-2% error)
SELECT APPROX_COUNT_DISTINCT(customer_id) FROM sales WHERE ...
```

**6.3 Use FIRST_ROWS(n) Hint for Interactive Reports**

For Interactive Reports that only display the first page:

```sql
SELECT /*+ FIRST_ROWS(25) */
    s.sale_id, s.sale_date, s.amount, ...
FROM sales s
WHERE ...
ORDER BY s.sale_date DESC;
```

### Step 7: Page-Level Optimization

**7.1 Lazy Loading of Regions**

Configure regions with **Lazy Loading** (APEX 23.2+):
1. Region → Advanced → **Lazy Loading**: **On**
2. Regions load in order of priority
3. Critical KPIs load first, charts second, bottom reports last

**7.2 Static Region IDs for Chart Refresh**

Give each chart region a **Static ID** for precise JavaScript refresh:

```
Region: Revenue Chart → Static ID: chart-revenue
Region: Trend Chart  → Static ID: chart-trend
```

**7.3 JavaScript for Efficient Page Interactions**

```javascript
// Debounce filter changes to avoid rapid AJAX calls
var filterTimer;
function onFilterChange() {
    clearTimeout(filterTimer);
    filterTimer = setTimeout(function() {
        // Reload the collection and refresh all regions
        apex.server.process('REBUILD_DASHBOARD', {}, {
            success: function() {
                // Refresh all regions at once
                apex.region('chart-revenue').refresh();
                apex.region('chart-trend').refresh();
                apex.region('region-report').refresh();
                apex.region('summary-kpi').refresh();
            }
        });
    }, 300); // Debounce 300ms
}

// Attach to filter page items
$('#P10_START_DATE, #P10_END_DATE, #P10_REGION, #P10_BRANCH')
    .on('change', onFilterChange);
```

**7.4 Application-Level Performance Settings**

1. **Application Attributes → Performance**:
   - **Parallel Processing**: Enabled (for concurrent region rendering)
   - **Browser Cache**: Enabled (leverage browser caching for static resources)
   - **Server Cache**: Enabled

2. **Application Attributes → Security**:
   - **Session State Protection**: Use with caution — checksum computation adds overhead
   - Consider disabling for performance-critical public pages

### Step 8: Monitoring and Profiling

**8.1 Enable APEX Debug for Profiling**

```sql
BEGIN
    APEX_DEBUG.ENABLE(
        p_level => APEX_DEBUG.C_LOG_TIMING
    );
END;
```

**8.2 Create Performance Monitoring Dashboard**

```sql
-- Query to monitor dashboard performance over time
SELECT
    TRUNC(access_date) AS day,
    COUNT(*) AS total_loads,
    ROUND(AVG(elapsed), 2) AS avg_elapsed_sec,
    ROUND(MEDIAN(elapsed), 2) AS median_elapsed_sec,
    ROUND(PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY elapsed), 2) AS p95_elapsed,
    COUNT(CASE WHEN elapsed > 3 THEN 1 END) AS slow_loads,
    ROUND(COUNT(CASE WHEN elapsed > 3 THEN 1 END) * 100.0 / COUNT(*), 2) AS slow_pct
FROM (
    SELECT
        access_date,
        (apex_activity_log_get_elapsed(
            p_application_id, p_page_id, p_view_date,
            p_session_id, p_elapsed
        ) / 1000) AS elapsed
    FROM apex_workspace_activity_log
    WHERE application_id = :APP_ID
      AND page_id = 10
      AND access_date > SYSDATE - 30
)
GROUP BY TRUNC(access_date)
ORDER BY day DESC;
```

**8.3 Custom Performance Logging**

Add instrumentation to critical page processes:

```sql
CREATE OR REPLACE PACKAGE perf_log AS
    PROCEDURE start_timer(p_timer_name IN VARCHAR2);
    PROCEDURE stop_timer(p_timer_name IN VARCHAR2);
    PROCEDURE log_perf(p_page_id IN NUMBER, p_region_id IN NUMBER,
                       p_query_name IN VARCHAR2, p_elapsed_ms IN NUMBER);
END perf_log;
/

CREATE OR REPLACE PACKAGE BODY perf_log AS
    g_timers APEX_T_VARCHAR2;

    PROCEDURE start_timer(p_timer_name IN VARCHAR2) IS
    BEGIN
        g_timers(p_timer_name) := TO_CHAR(SYSTIMESTAMP, 'FF9');
    END start_timer;

    PROCEDURE stop_timer(p_timer_name IN VARCHAR2) IS
        l_start NUMBER;
        l_end   NUMBER;
    BEGIN
        l_start := TO_NUMBER(g_timers(p_timer_name));
        l_end := TO_CHAR(SYSTIMESTAMP, 'FF9');
        -- Log to custom performance table
        INSERT INTO perf_metrics VALUES (
            perf_seq.NEXTVAL,
            :APP_PAGE_ID,
            :APP_REGION_ID,
            p_timer_name,
            (l_end - l_start) / 1000,
            :APP_USER,
            SYSDATE
        );
        COMMIT;
    END stop_timer;

    PROCEDURE log_perf(p_page_id IN NUMBER, p_region_id IN NUMBER,
                       p_query_name IN VARCHAR2, p_elapsed_ms IN NUMBER) IS
    BEGIN
        INSERT INTO perf_metrics VALUES (
            perf_seq.NEXTVAL, p_page_id, p_region_id,
            p_query_name, p_elapsed_ms,
            :APP_USER, SYSDATE
        );
        COMMIT;
    END log_perf;
END perf_log;
/
```

### Step 9: Implementation Checklist

```
□ Step 1: Run diagnostic queries to identify bottlenecks
□ Step 2: Add missing indexes and analyze execution plans
□ Step 3: Create materialized views for aggregated data
□ Step 4: Partition large tables (if applicable)
□ Step 5: Enable region caching with appropriate lifetimes
□ Step 6: Implement collection-based data sharing across regions
□ Step 7: Replace individual exports with batch collection export
□ Step 8: Deploy lazy loading for below-the-fold regions
□ Step 9: Add debounced filter handling
□ Step 10: Enable APEX-level performance settings
□ Step 11: Deploy performance monitoring dashboard
```

---

## Best Practices Applied

1. **Cache Strategy**: Multi-level caching (APEX region cache, collection cache, materialized views)
2. **Single Source of Truth**: Collection used by all regions — single query, multiple consumers
3. **Lazy Loading**: Prioritize visible content, defer below-fold regions
4. **Batch Operations**: Collections enable set-based processing instead of row-by-row
5. **Debounced User Input**: Prevent AJAX storms from rapid filter changes
6. **Monitoring**: Custom performance logging for ongoing optimization

## Common Pitfalls to Avoid

1. **Over-caching**: Stale data displayed to users — set appropriate TTLs and manual invalidation
2. **Collection Memory**: Collections consume session memory — delete when done
3. **Missing Index Maintenance**: Rebuild indexes during off-peak hours
4. **N+1 Query Pattern**: Loading one row at a time in a loop — use set-based operations
5. **Over-Partitioning**: Too many partitions cause overhead — balance size and count
6. **Caching Without Invalidation**: Always provide cache invalidation hooks

## Extensions for Future Iterations

1. Redis caching layer for cross-session query result caching
2. Asynchronous report generation with email notification
3. WebSocket-based real-time updates for dashboard metrics
4. Query result pagination for export (streaming 100K rows in chunks)
5. Automated index recommendations based on workload analysis
6. Read replica routing for reporting queries to separate from OLTP traffic
