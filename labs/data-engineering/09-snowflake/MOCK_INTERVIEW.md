# Mock Interview: Snowflake (09-snowflake)

## Scenario: Optimize Snowflake costs and performance
Your company's Snowflake bill doubled from $20K to $40K/month. The CFO wants analysis. You have access to ACCOUNT_USAGE schema and QUERY_HISTORY.

### Time: 45 minutes | Difficulty: Medium-Hard

---

## Part 1: Cost Analysis (15 min)

**Break down the bill using ACCOUNT_USAGE:**

```sql
-- Warehouse costs by month
SELECT warehouse_name,
  ROUND(SUM(credits_used), 2) AS total_credits,
  ROUND(SUM(credits_used) * 4, 2) AS estimated_cost_dollars -- $4/credit standard
FROM snowflake.account_usage.warehouse_metering_history
WHERE start_time >= DATEADD('month', -3, CURRENT_TIMESTAMP)
GROUP BY warehouse_name
ORDER BY total_credits DESC;

-- Storage costs
SELECT usage_date,
  ROUND(AVG(storage_bytes) / POWER(1024, 4), 2) AS avg_tb,
  ROUND(AVG(stage_bytes) / POWER(1024, 4), 2) AS avg_stage_tb,
  ROUND(AVG(failsafe_bytes) / POWER(1024, 4), 2) AS avg_failsafe_tb
FROM snowflake.account_usage.storage_usage
WHERE usage_date >= DATEADD('month', -3, CURRENT_DATE)
GROUP BY usage_date
ORDER BY usage_date;

-- Query costs (most expensive queries)
SELECT query_id, query_text,
  warehouse_size, credits_used_cloud_services,
  ROUND(execution_time / 1000, 2) AS execution_seconds,
  ROUND(bytes_scanned / POWER(1024, 3), 2) AS gb_scanned,
  partitions_scanned, partitions_total
FROM snowflake.account_usage.query_history
WHERE start_time >= DATEADD('day', -7, CURRENT_TIMESTAMP)
ORDER BY credits_used_cloud_services DESC
LIMIT 20;
```

**Top cost drivers to investigate:**
1. Is a warehouse running 24/7 without auto-suspend?
2. Are users running expensive queries against large tables?
3. Is storage growing fast (time travel retention too long)?
4. Are there multiple warehouses that could be consolidated?
5. Is cloud services cost unusually high? (frequent metadata operations)

**Common issues found:**
- Development warehouse left running overnight (auto-suspend not set)
- BI team running SELECT * on 10TB table instead of specific columns
- Time travel retention set to 90 days for all tables (only some need it)
- 10 separate X-Small warehouses instead of 1 Medium with multi-cluster

---

## Part 2: Warehouse Optimization (10 min)

**Rightsizing strategy:**

| Current Size | Issue | Recommendation |
|-------------|-------|----------------|
| 2X-Large for all workloads | Over-provisioned for simple queries | Use X-Small for development, Medium for reporting, Large for batch ETL |
| Multiple X-Small | Inefficient for concurrent queries | 1 Medium with max_cluster_count=3 |
| No auto-suspend | Running 24/7 | Set auto_suspend = 60 seconds for dev, 300 for production |
| Static warehouse | Bursty workloads | Use multi-cluster (min=1, max=3) for auto-scaling |

**Example: Consolidate and optimize:**
```sql
-- Before: 5 X-Small warehouses, no auto-suspend
-- Monthly: 5 warehouses * 744 hours * 1 credit/hour = 3,720 credits = $14,880

-- After: 1 Medium warehouse, multi-cluster (min=1, max=3), auto-suspend=300s
-- Batch window: 8 hours/day * 2 credits/hour * 30 = 480 credits
-- Peak auto-scaling: 2 extra clusters * 4 hours * 30 * 2 credits = 480 credits
-- Total: ~960 credits = $3,840 (74% savings)
CREATE OR REPLACE WAREHOUSE analytics_wh
  WAREHOUSE_SIZE = 'MEDIUM'
  MAX_CLUSTER_COUNT = 3
  MIN_CLUSTER_COUNT = 1
  SCALING_POLICY = 'STANDARD'
  AUTO_SUSPEND = 300
  AUTO_RESUME = TRUE
  INITIALLY_SUSPENDED = TRUE;
```

**Materialized views for common queries:**
```sql
-- Frequently run aggregation (runs every 15 minutes by BI)
CREATE MATERIALIZED VIEW mv_daily_sales AS
SELECT DATE_TRUNC('day', order_date) AS day,
  product_id,
  SUM(revenue) AS daily_revenue,
  COUNT(DISTINCT customer_id) AS unique_customers
FROM fact_orders
GROUP BY 1, 2;

-- Instead of scanning 10TB fact table, query scans 10GB materialized view
-- Materialized view automatically refreshes on changes
```

---

## Part 3: Performance Tuning (10 min)

**Query optimization checklist:**

```sql
-- Step 1: Profile the slow query
-- Check: bytes_scanned, partitions_scanned vs partitions_total
-- Goal: partitions_scanned << partitions_total (pruning)

-- Step 2: Add clustering key
ALTER TABLE fact_orders CLUSTER BY (order_date, customer_id);

-- Step 3: Check automatic clustering efficiency
SELECT table_name, clustering_depth, number_of_partitions
FROM INFORMATION_SCHEMA.TABLES
WHERE table_name = 'FACT_ORDERS';
-- Goal: clustering_depth < 4, high number_of_partitions

-- Step 4: Use search optimization for point lookups
ALTER TABLE dim_customer ADD SEARCH OPTIMIZATION;

-- Step 5: Avoid common anti-patterns
-- BAD: Non-sargable filter
SELECT * FROM fact_orders WHERE DATE(order_date) = '2024-01-15';
-- GOOD: Sargable filter
SELECT * FROM fact_orders WHERE order_date = '2024-01-15'::DATE;

-- BAD: SELECT * in production (scans all columns)
SELECT * FROM fact_orders WHERE order_date = '2024-01-15';
-- GOOD: Select needed columns only
SELECT order_id, customer_id, revenue FROM fact_orders WHERE order_date = '2024-01-15';
```

**Caching layers:**
| Cache Type | Duration | Scope | Best For |
|-----------|----------|-------|----------|
| Results cache | 24 hours (or until data changes) | Account | Repeated identical queries |
| Metadata cache | Until metadata changes | Warehouse | Schema queries, table info |
| Warehouse data cache | Until warehouse suspend | Specific warehouse | Frequently accessed data |

**When each cache helps:**
- Result cache: BI dashboard with auto-refresh every 5 minutes (same queries)
- Data cache: ETL running multiple queries on same table in one session
- Metadata cache: Schema discovery queries

---

## Part 4: Governance & Storage (10 min)

**Resource monitor configuration:**
```sql
-- Weekly resource monitor with actions
CREATE OR REPLACE RESOURCE MONITOR weekly_budget
  WITH
    CREDIT_QUOTA = 1000
    FREQUENCY = 'WEEKLY'
    START_TIMESTAMP = '2024-01-01 00:00:00'
    TRIGGERS
      ON 75 PERCENT DO NOTIFY
      ON 90 PERCENT DO NOTIFY
      ON 100 PERCENT DO SUSPEND
      ON 110 PERCENT DO SUSPEND_IMMEDIATE
  INITIALLY_SUCCEEDED = TRUE;

-- Assign to warehouse
ALTER WAREHOUSE analytics_wh SET RESOURCE_MONITOR = weekly_budget;
```

**Storage optimization:**
```sql
-- Set time travel retention per table (not all tables need 90 days)
ALTER TABLE fact_orders SET DATA_RETENTION_TIME_IN_DAYS = 7;
ALTER TABLE dim_customer SET DATA_RETENTION_TIME_IN_DAYS = 30;
ALTER TABLE audit_log SET DATA_RETENTION_TIME_IN_DAYS = 90;

-- Automatic clustering maintenance
ALTER TABLE fact_orders RESUME RECLUSTER;

-- Zero-copy clone strategy for dev/test
CREATE DATABASE dev_sandbox CLONE production_db;
-- Clones are instantaneous, zero storage cost initially
-- Changes to dev clone create new micro-partitions (only changed data)
```

**Data sharing for external partners:**
```sql
-- Create reader account for analytics partner
CREATE MANAGED ACCOUNT partner_analytics
  ADMIN_NAME = partner_admin
  ADMIN_PASSWORD = 'secure_password'
  TYPE = READER;

-- Share specific schema
CREATE SHARE sales_share;
GRANT USAGE ON DATABASE analytics_db TO SHARE sales_share;
GRANT USAGE ON SCHEMA analytics_db.public TO SHARE sales_share;
GRANT SELECT ON TABLE analytics_db.public.fact_orders TO SHARE sales_share;
ALTER SHARE sales_share SET ACCOUNTS = [partner_account_locator];
```

---

## Follow-up Questions

**Materialized views vs streams + tasks vs dynamic tables:**
| Feature | Materialized View | Streams + Tasks | Dynamic Table |
|---------|------------------|-----------------|---------------|
| Refresh | Automatic | Scheduled (tasks) | Declarative (automatic) |
| Complexity | Simple aggregations | Complex transformations | Moderate transformations |
| Latency | Near-real-time | As scheduled | Configured target lag |
| Cost | Storage for pre-computed data | Compute for task execution | Compute for refresh |
| Best for | Frequently used aggregations | CDC pipelines, conditional logic | Declarative pipeline with SLA |

**Zero-copy clone use cases:**
- Dev/test: Create isolated environment instantly
- Data science: Experiment on production data snapshot
- Backup: Point-in-time snapshot before schema change
- CI/CD: Test migration scripts on production-like data
- Cost: Only pay for changed data (most data shared)

**Dynamic table example:**
```sql
CREATE DYNAMIC TABLE customer_order_summary
  TARGET_LAG = '1 hour'
  WAREHOUSE = analytics_wh
AS
SELECT c.customer_id, c.name,
  COUNT(o.order_id) AS order_count,
  SUM(o.revenue) AS total_revenue,
  MAX(o.order_date) AS last_order_date
FROM dim_customer c
LEFT JOIN fact_orders o ON c.customer_id = o.customer_id
GROUP BY 1, 2;
```

