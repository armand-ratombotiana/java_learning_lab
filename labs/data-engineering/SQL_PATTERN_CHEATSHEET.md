# Data Engineering SQL Pattern Cheatsheet

> Comprehensive SQL patterns for data engineering interviews: window functions, MERGE/UPSERT, incremental loading, SCD, deduplication, CDC, backfill strategies, and partitioning.

---

## 1. Window Functions for Pipelines

### ROW_NUMBER - Deduplication
```sql
-- Keep the latest record per entity
WITH ranked AS (
  SELECT *,
    ROW_NUMBER() OVER (
      PARTITION BY entity_id
      ORDER BY event_timestamp DESC
    ) AS rn
  FROM events
)
SELECT * FROM ranked WHERE rn = 1;
```

### RANK vs DENSE_RANK - Top N per Group
```sql
-- Top 3 products by revenue per region
WITH ranked AS (
  SELECT region, product_id, revenue,
    RANK() OVER (PARTITION BY region ORDER BY revenue DESC) AS rank
  FROM product_revenue
)
SELECT * FROM ranked WHERE rank <= 3;
-- RANK: 1,2,2,4 (gaps)
-- DENSE_RANK: 1,2,2,3 (no gaps)
```

### LAG/LEAD - Time-series Changes
```sql
-- Month-over-month revenue change
SELECT month, product_id, revenue,
  LAG(revenue) OVER (
    PARTITION BY product_id
    ORDER BY month
  ) AS prev_month_revenue,
  revenue - LAG(revenue) OVER (
    PARTITION BY product_id
    ORDER BY month
  ) AS change
FROM monthly_revenue;
```

### Moving Averages
```sql
-- 7-day moving average of web traffic
SELECT date, daily_visits,
  AVG(daily_visits) OVER (
    ORDER BY date
    ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
  ) AS moving_avg_7d
FROM daily_traffic;
```

### Running Totals
```sql
-- Cumulative revenue per customer
SELECT customer_id, order_date, order_amount,
  SUM(order_amount) OVER (
    PARTITION BY customer_id
    ORDER BY order_date
    ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
  ) AS running_total
FROM orders;
```

### NTILE - Quantile Distribution
```sql
-- Divide customers into 4 spending quartiles
SELECT customer_id, total_spend,
  NTILE(4) OVER (ORDER BY total_spend DESC) AS spend_quartile
FROM customer_spend;
```

### FIRST_VALUE / LAST_VALUE - Boundary Values
```sql
-- First order date per customer
SELECT customer_id, order_id, order_date,
  FIRST_VALUE(order_date) OVER (
    PARTITION BY customer_id
    ORDER BY order_date
  ) AS first_order_date
FROM orders;
```

### PERCENT_RANK / CUME_DIST - Distribution Stats
```sql
-- Product price percentile
SELECT product_id, price,
  PERCENT_RANK() OVER (ORDER BY price) AS percentile,
  CUME_DIST() OVER (ORDER BY price) AS cumulative_dist
FROM products;
```

### ROWS vs RANGE vs GROUPS
```sql
-- ROWS: physical rows (most common)
SUM(amount) OVER (ORDER BY dt ROWS BETWEEN 2 PRECEDING AND CURRENT ROW)
-- RANGE: rows with same ORDER BY value
SUM(amount) OVER (ORDER BY dt RANGE BETWEEN INTERVAL '7' DAY PRECEDING AND CURRENT ROW)
-- GROUPS: groups of equal ORDER BY values
SUM(amount) OVER (ORDER BY dt GROUPS BETWEEN 1 PRECEDING AND 1 FOLLOWING)
```

### Cohort Analysis
```sql
WITH user_cohorts AS (
  SELECT
    user_id,
    DATE_TRUNC('week', signup_date) AS cohort_week
  FROM users
),
weekly_activity AS (
  SELECT
    u.user_id,
    uc.cohort_week,
    DATE_TRUNC('week', s.session_date) AS activity_week,
    COUNT(DISTINCT s.session_id) AS sessions
  FROM sessions s
  JOIN users u ON s.user_id = u.user_id
  JOIN user_cohorts uc ON u.user_id = uc.user_id
  GROUP BY 1, 2, 3
)
SELECT
  cohort_week,
  EXTRACT(WEEK FROM activity_week) - EXTRACT(WEEK FROM cohort_week) AS week_number,
  COUNT(DISTINCT user_id) AS active_users
FROM weekly_activity
GROUP BY 1, 2
ORDER BY 1, 2;
```

---

## 2. MERGE / UPSERT Patterns

### Standard MERGE (Snowflake / Databricks)
```sql
MERGE INTO target AS t
USING source AS s
  ON t.key = s.key
WHEN MATCHED AND s.update_flag = TRUE THEN
  UPDATE SET
    t.value = s.value,
    t.updated_at = CURRENT_TIMESTAMP()
WHEN NOT MATCHED THEN
  INSERT (key, value, created_at)
  VALUES (s.key, s.value, CURRENT_TIMESTAMP());
```

### MERGE with Conditions (SQL Server / Azure Synapse)
```sql
MERGE INTO dwh.fact_sales AS t
USING staging.sales AS s
  ON t.order_id = s.order_id
WHEN MATCHED AND t.last_modified < s.last_modified THEN
  UPDATE SET
    t.quantity = s.quantity,
    t.amount = s.amount,
    t.last_modified = s.last_modified
WHEN NOT MATCHED BY TARGET THEN
  INSERT (order_id, product_id, quantity, amount, last_modified)
  VALUES (s.order_id, s.product_id, s.quantity, s.amount, s.last_modified)
WHEN NOT MATCHED BY SOURCE AND t.is_active = 1 THEN
  UPDATE SET t.is_active = 0, t.ended_at = GETDATE();
```

### BigQuery MERGE
```sql
MERGE INTO `project.dataset.target` AS T
USING `project.dataset.source` AS S
  ON T.key = S.key
WHEN MATCHED THEN
  UPDATE SET value = S.value
WHEN NOT MATCHED THEN
  INSERT (key, value) VALUES (S.key, S.value);
```

### Postgres INSERT ON CONFLICT (UPSERT)
```sql
INSERT INTO target (key, value, updated_at)
VALUES ($1, $2, NOW())
ON CONFLICT (key)
DO UPDATE SET value = EXCLUDED.value, updated_at = NOW();
```

### MySQL INSERT ON DUPLICATE
```sql
INSERT INTO target (key, value, updated_at)
VALUES (?, ?, NOW())
ON DUPLICATE KEY UPDATE
  value = VALUES(value),
  updated_at = NOW();
```

---

## 3. Incremental Loading Patterns

### Timestamp-based Incremental
```sql
-- Extract records updated since last run
INSERT INTO warehouse.table
SELECT * FROM source.table
WHERE updated_at > (SELECT last_run_ts FROM watermark_table WHERE table_name = 'source.table');
```

### High-Watermark Tracking
```sql
-- Maintain watermark table
CREATE TABLE pipeline_watermarks (
  table_name STRING PRIMARY KEY,
  last_run_ts TIMESTAMP,
  last_processed_id INT,
  row_count INT
);

-- Update after each run
UPDATE pipeline_watermarks
SET last_run_ts = CURRENT_TIMESTAMP(),
    last_processed_id = (SELECT MAX(id) FROM source.table),
    row_count = (SELECT COUNT(*) FROM warehouse.table)
WHERE table_name = 'source.table';
```

### Delta Tables Incremental (Databricks)
```sql
-- Read only changed records since last version
SELECT * FROM source_table
WHERE _commit_version > (SELECT last_version FROM watermark_table);
```

### Iceberg Incremental Read
```sql
-- Read changes between snapshots
SELECT * FROM db.table
FOR SYSTEM_TIME AS OF '2024-01-01 00:00:00';
```

---

## 4. Slowly Changing Dimension (SCD) Type 2

### SCD Type 2 with dbt Snapshot
```sql
{% snapshot dim_customer_snapshot %}
{{
  config(
    target_schema='snapshots',
    unique_key='customer_id',
    strategy='timestamp',
    updated_at='updated_at',
    invalidate_hard_deletes=True
  )
}}
SELECT * FROM {{ ref('stg_customers') }}
{% endsnapshot %}
```

### SCD Type 2 with MERGE
```sql
MERGE INTO dim_customer AS target
USING (
  SELECT * FROM stg_customers
) AS source
ON target.customer_id = source.customer_id
  AND target.is_current = TRUE

WHEN MATCHED AND (
  target.email != source.email OR
  target.address != source.address
) THEN
  UPDATE SET
    is_current = FALSE,
    end_date = CURRENT_DATE()

WHEN NOT MATCHED THEN
  INSERT (customer_id, name, email, address, start_date, end_date, is_current)
  VALUES (
    source.customer_id, source.name, source.email, source.address,
    CURRENT_DATE(), NULL, TRUE
  );

-- Insert new current records for updated customers
INSERT INTO dim_customer (customer_id, name, email, address, start_date, end_date, is_current)
SELECT
  source.customer_id, source.name, source.email, source.address,
  CURRENT_DATE(), NULL, TRUE
FROM stg_customers source
JOIN dim_customer existing
  ON source.customer_id = existing.customer_id
  AND existing.is_current = FALSE
  AND existing.end_date = CURRENT_DATE();
```

### SCD Type 1 (Overwrite)
```sql
MERGE INTO dim_customer AS t
USING stg_customers AS s
  ON t.customer_id = s.customer_id
WHEN MATCHED THEN
  UPDATE SET
    email = s.email,
    phone = s.phone,
    updated_at = CURRENT_TIMESTAMP()
WHEN NOT MATCHED THEN
  INSERT (customer_id, email, phone, created_at, updated_at)
  VALUES (s.customer_id, s.email, s.phone, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
```

### SCD Type 3 (Previous Value)
```sql
-- Add previous_value column
ALTER TABLE dim_customer
ADD COLUMN prev_territory STRING;

-- On change, shift values
MERGE INTO dim_customer AS t
USING stg_customers AS s
  ON t.customer_id = s.customer_id
WHEN MATCHED AND t.territory != s.territory THEN
  UPDATE SET
    prev_territory = t.territory,
    territory = s.territory;
```

---

## 5. Deduplication Patterns

### Row Number Dedup (Keep Latest)
```sql
DELETE FROM target WHERE (id, seq) IN (
  SELECT id, seq FROM (
    SELECT id, seq,
      ROW_NUMBER() OVER (PARTITION BY id ORDER BY event_time DESC) AS rn
    FROM target
  ) WHERE rn > 1
);
```

### Group By Dedup (Aggregate)
```sql
-- Dedup by keeping max value
SELECT id, MAX(value) AS value, MAX(updated_at) AS updated_at
FROM events
GROUP BY id;
```

### Distinct Dedup (Full Row Match)
```sql
INSERT OVERWRITE table_name
SELECT DISTINCT * FROM table_name;
```

### Merge-based Dedup (Incremental)
```sql
MERGE INTO target AS t
USING (
  SELECT * FROM (
    SELECT *,
      ROW_NUMBER() OVER (PARTITION BY id ORDER BY version DESC) AS rn
    FROM incoming_data
  ) WHERE rn = 1
) AS s
  ON t.id = s.id
WHEN MATCHED AND s.version > t.version THEN
  UPDATE SET value = s.value, version = s.version
WHEN NOT MATCHED THEN
  INSERT (id, value, version) VALUES (s.id, s.value, s.version);
```

---

## 6. Change Data Capture (CDC) Patterns

### Kafka + Debezium CDC to Snowflake
```sql
-- Staging table for CDC events
CREATE TABLE cdc_events (
  source_table STRING,
  operation STRING, -- 'c' create, 'u' update, 'd' delete
  before VARIANT,
  after VARIANT,
  ts_ms TIMESTAMP
);

-- Apply CDC using MERGE
MERGE INTO target AS t
USING (
  SELECT
    after:customer_id::INT AS customer_id,
    after:name::STRING AS name,
    after:email::STRING AS email,
    operation
  FROM cdc_events
  WHERE source_table = 'customers'
    AND ts_ms > (SELECT last_processed_ts FROM cdc_watermark)
) AS s
ON t.customer_id = s.customer_id
WHEN MATCHED AND s.operation = 'd' THEN
  UPDATE SET is_deleted = TRUE, deleted_at = CURRENT_TIMESTAMP()
WHEN MATCHED AND s.operation IN ('u', 'c') THEN
  UPDATE SET name = s.name, email = s.email
WHEN NOT MATCHED AND s.operation IN ('u', 'c') THEN
  INSERT (customer_id, name, email)
  VALUES (s.customer_id, s.name, s.email);
```

### Debezium WAL-based CDC Query
```sql
-- Read from replica slot
SELECT * FROM pg_logical_slot_get_changes('debezium_slot', NULL, NULL);
```

### Oracle GoldenGate CDC
```sql
-- Create trail file format
GGSCI > ADD EXTRACT ext_cust, TRANLOG, BEGIN NOW
GGSCI > ADD REPLICAT rep_cust, TARGETDB orcl, ASSUMETARGETDEFS
```

### Batch CDC Comparison
```sql
-- Full table comparison for batch CDC
SELECT
  COALESCE(s.id, t.id) AS id,
  CASE
    WHEN t.id IS NULL THEN 'INSERT'
    WHEN s.id IS NULL THEN 'DELETE'
    WHEN t.hash_value != s.hash_value THEN 'UPDATE'
    ELSE NULL
  END AS cdc_operation
FROM source s
FULL OUTER JOIN target t ON s.id = t.id
WHERE t.id IS NULL OR s.id IS NULL OR t.hash_value != s.hash_value;
```

---

## 7. Backfill Strategies

### Partition-based Backfill
```sql
-- Backfill a specific date range
INSERT INTO target (date_key, metric)
SELECT date_key, metric
FROM source
WHERE date_key BETWEEN '2024-01-01' AND '2024-03-31'
  AND date_key NOT IN (SELECT date_key FROM target WHERE is_backfilled = TRUE);

-- Mark as backfilled
UPDATE target SET is_backfilled = TRUE
WHERE date_key BETWEEN '2024-01-01' AND '2024-03-31';
```

### Idempotent Backfill
```sql
-- Always produces same result; safe to re-run
TRUNCATE TABLE target PARTITION (date_key = '2024-01-15');

INSERT INTO target PARTITION (date_key = '2024-01-15')
SELECT * FROM source WHERE date_key = '2024-01-15';
```

### Incremental Backfill with Batch Ranges
```sql
-- Backfill in batches to avoid long transactions
WITH date_range AS (
  SELECT MIN(date_key) AS start_date, MAX(date_key) AS end_date
  FROM source WHERE date_key BETWEEN '2024-01-01' AND '2024-03-31'
    AND NOT EXISTS (SELECT 1 FROM target WHERE target.date_key = source.date_key)
)
SELECT start_date, end_date;
-- Then loop 1 week at a time
```

### Snapshot Backfill
```sql
-- Replace entire table snapshot
CREATE TABLE target_new AS
SELECT * FROM source WHERE data_date = '2024-06-01';

ALTER TABLE target RENAME TO target_old;
ALTER TABLE target_new RENAME TO target;
DROP TABLE target_old;
```

---

## 8. Table Partitioning

### Snowflake - Clustering Key (No Native Partition)
```sql
ALTER TABLE sales CLUSTER BY (order_date, customer_id);

-- Automatic clustering maintenance
ALTER TABLE sales RESUME RECLUSTER;
```

### BigQuery - Partition + Cluster
```sql
CREATE TABLE `project.dataset.sales` (
  order_id INT64,
  order_date DATE,
  customer_id INT64,
  amount FLOAT64
)
PARTITION BY order_date
CLUSTER BY customer_id;

-- Query against partition
SELECT * FROM `project.dataset.sales`
WHERE order_date >= '2024-01-01' AND customer_id = 12345;
```

### Redshift - DISTKEY + SORTKEY
```sql
CREATE TABLE sales (
  order_id INT DISTKEY,
  order_date DATE SORTKEY,
  customer_id INT,
  amount DECIMAL(10,2)
);

-- Compound sort key
CREATE TABLE sales (
  ...
) SORTKEY (order_date, customer_id);

-- Interleaved sort key
CREATE TABLE sales (
  ...
) SORTKEY INTERLEAVED (order_date, customer_id);
```

### Hive-style Partitioning
```sql
-- Create partitions as directories
INSERT INTO sales PARTITION (year=2024, month=1, day=15)
SELECT * FROM source WHERE order_date = '2024-01-15';

-- Add partition metadata
ALTER TABLE sales ADD PARTITION (year=2024, month=1, day=15);
```

---

## 9. Common Interview SQL Patterns

### Histogram / Distribution
```sql
-- Price distribution buckets
SELECT
  CASE
    WHEN price < 10 THEN '0-10'
    WHEN price < 50 THEN '10-50'
    WHEN price < 100 THEN '50-100'
    ELSE '100+'
  END AS price_bucket,
  COUNT(*) AS product_count
FROM products
GROUP BY 1
ORDER BY MIN(price);
```

### Pivoting (Rows to Columns)
```sql
SELECT
  date,
  SUM(CASE WHEN event_type = 'view' THEN 1 ELSE 0 END) AS views,
  SUM(CASE WHEN event_type = 'click' THEN 1 ELSE 0 END) AS clicks,
  SUM(CASE WHEN event_type = 'purchase' THEN 1 ELSE 0 END) AS purchases
FROM events
GROUP BY date;

-- Cross-tab with string aggregation
SELECT date,
  STRING_AGG(event_type || ':' || cnt, ', ') AS event_summary
FROM (
  SELECT date, event_type, COUNT(*) AS cnt
  FROM events
  GROUP BY 1, 2
) GROUP BY 1;
```

### Unpivoting (Columns to Rows)
```sql
SELECT id, 'metric_a' AS metric_name, metric_a AS value FROM table
UNION ALL
SELECT id, 'metric_b', metric_b FROM table
UNION ALL
SELECT id, 'metric_c', metric_c FROM table;
```

### Recursive CTE - Hierarchical Data
```sql
WITH RECURSIVE org_tree AS (
  SELECT employee_id, manager_id, employee_name, 1 AS level
  FROM employees
  WHERE manager_id IS NULL

  UNION ALL

  SELECT e.employee_id, e.manager_id, e.employee_name, ot.level + 1
  FROM employees e
  JOIN org_tree ot ON e.manager_id = ot.employee_id
)
SELECT * FROM org_tree ORDER BY level, employee_name;
```

### Date Series Generation
```sql
-- Generate all dates in a range (BigQuery)
SELECT DATE_ADD('2024-01-01', INTERVAL day_offset DAY) AS date
FROM UNNEST(GENERATE_ARRAY(0, 364)) AS day_offset;

-- Snowflake
SELECT DATEADD('day', seq, '2024-01-01') AS date
FROM TABLE(GENERATOR(ROWCOUNT => 365));
```

### Sampling
```sql
-- Random sampling
SELECT * FROM table
WHERE RANDOM() < 0.01; -- 1% sample

-- Stratified sampling
SELECT * FROM (
  SELECT *,
    ROW_NUMBER() OVER (PARTITION BY category ORDER BY RANDOM()) AS rn
  FROM products
) WHERE rn <= 100; -- 100 per category
```

### Gap Detection
```sql
-- Find missing dates in a series
WITH date_range AS (
  SELECT GENERATE_DATE_ARRAY('2024-01-01', '2024-01-31') AS dates
)
SELECT date
FROM date_range, UNNEST(dates) AS date
WHERE date NOT IN (SELECT DISTINCT order_date FROM orders);
```

### Most Frequent Value (Mode)
```sql
-- Mode per group
WITH counted AS (
  SELECT category, value, COUNT(*) AS cnt
  FROM table
  GROUP BY category, value
),
ranked AS (
  SELECT category, value, cnt,
    ROW_NUMBER() OVER (PARTITION BY category ORDER BY cnt DESC) AS rn
  FROM counted
)
SELECT category, value AS mode_value
FROM ranked WHERE rn = 1;
```

### First/Last non-NULL
```sql
-- Fill NULLs with last non-NULL (last observation carried forward)
SELECT
  date,
  value,
  LAST_VALUE(value IGNORE NULLS) OVER (ORDER BY date) AS filled_value
FROM time_series;
```

### CONSECUTIVE ROWS Detection
```sql
-- Find consecutive days a user was active
WITH active_dates AS (
  SELECT DISTINCT user_id, activity_date
  FROM activity
),
groups AS (
  SELECT user_id, activity_date,
    DATE_SUB(activity_date, ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY activity_date)) AS grp
  FROM active_dates
)
SELECT user_id,
  MIN(activity_date) AS streak_start,
  MAX(activity_date) AS streak_end,
  COUNT(*) AS streak_days
FROM groups
GROUP BY user_id, grp
HAVING COUNT(*) >= 5; -- streaks of 5+ days
```

---

## 10. Performance Optimization Patterns

### Index Strategy
```sql
-- Redshift: Sort key for range queries, dist key for joins
CREATE TABLE sales (
  order_id INT DISTKEY,
  order_date DATE SORTKEY
);

-- BigQuery: Cluster on high-cardinality filter columns
-- Snowflake: Clustering key on frequently filtered columns
```

### Table Stats Refresh
```sql
-- Redshift
ANALYZE sales;

-- BigQuery (automatic, but can force)
CALL BQ.REFRESH_MATERIALIZED_VIEW('dataset.sales_summary');
```

### EXPLAIN Plan Analysis
```sql
-- Check query plan
EXPLAIN SELECT * FROM sales WHERE order_date = '2024-01-15';

-- Look for:
-- Full table scans (should use partition pruning)
-- Broadcast joins (small table should be on right)
-- Hash joins vs merge joins
-- Sort operations (avoid if data already sorted)
```

### Avoiding Common Anti-Patterns
```sql
-- BAD: Non-sargable predicate
SELECT * FROM sales WHERE DATE(order_date) = '2024-01-15';
-- GOOD: Sargable
SELECT * FROM sales WHERE order_date = '2024-01-15';

-- BAD: Implicit type conversion
SELECT * FROM sales WHERE customer_id = '12345';
-- GOOD: Explicit type
SELECT * FROM sales WHERE customer_id = 12345;

-- BAD: SELECT * in production
SELECT * FROM sales;
-- GOOD: Explicit column list
SELECT order_id, order_date, customer_id, amount FROM sales;
```
