# Mock Interview: Delta Lake (13-delta-lake)

## Scenario: Implement Delta Lake for a lakehouse architecture
Your company is moving from a raw data lake on S3 (1000+ Parquet tables) to a lakehouse using Delta Lake on Databricks.

### Time: 45 minutes | Difficulty: Hard

---

## Part 1: Migration Strategy (15 min)

**Converting existing Parquet tables to Delta:**

**Approach 1: In-place conversion (fastest, no data copy)**
```python
# Convert entire Parquet table to Delta in place
spark.sql("CONVERT TO DELTA parquet.`s3://data-lake/curated/dim_customer`")

# With options
spark.sql("""
  CONVERT TO DELTA parquet.`s3://data-lake/curated/fact_orders`
  PARTITIONED BY (order_date DATE)
""")
```

**Approach 2: CTAS (for re-partitioning or re-formatting)**
```python
# Create Delta table from Parquet (with optimization)
fact_orders = spark.read.parquet("s3://data-lake/curated/fact_orders")
fact_orders.write \
    .format("delta") \
    .mode("overwrite") \
    .partitionBy("order_date") \
    .option("delta.autoOptimize.optimizeWrite", "true") \
    .saveAsTable("gold.fact_orders")
```

**Approach 3: Incremental migration (for large tables, zero downtime)**
```python
# Step 1: Create Delta table with same schema
spark.sql("""
  CREATE TABLE gold.fact_orders
  USING DELTA
  LOCATION 's3://data-lake/delta/gold/fact_orders'
  AS SELECT * FROM parquet.`s3://data-lake/curated/fact_orders`
  WHERE order_date < '2024-01-01'
""")

# Step 2: Incrementally add remaining partitions
for date in remaining_dates:
    spark.sql(f"""
      INSERT INTO gold.fact_orders
      SELECT * FROM parquet.`s3://data-lake/curated/fact_orders`
      WHERE order_date = '{date}'
    """)

# Step 3: Point downstream consumers to new Delta location
# Step 4: Archive old Parquet location
```

**Migration checklist:**
- [ ] Test conversion on non-critical tables first
- [ ] Validate row counts before/after conversion
- [ ] Verify downstream queries work with Delta format
- [ ] Update table locations in catalog (Hive/Unity)
- [ ] Set up Delta optimization jobs (OPTIMIZE, VACUUM)
- [ ] Update ETL pipelines to write Delta format directly

---

## Part 2: Medallion Architecture (10 min)

**Bronze → Silver → Gold with Delta Lake:**

```python
# Bronze: Raw ingestion (append-only, schema-on-read)
df_raw = spark.readStream.format("cloudFiles") \
    .option("cloudFiles.format", "json") \
    .option("cloudFiles.schemaLocation", "s3://data-lake/bronze/schema/orders") \
    .load("s3://data-lake/raw/orders/")

df_raw.writeStream \
    .format("delta") \
    .option("checkpointLocation", "s3://data-lake/checkpoints/bronze_orders") \
    .trigger(once=True) \
    .table("bronze.orders")

# Silver: Cleaned, deduplicated, validated
spark.sql("""
  CREATE OR REPLACE TABLE silver.orders
  USING DELTA
  AS
  SELECT DISTINCT
    order_id,
    customer_id,
    CAST(order_date AS DATE) AS order_date,
    CAST(amount AS DECIMAL(18,2)) AS amount,
    status,
    CURRENT_TIMESTAMP() AS processed_at
  FROM bronze.orders
  WHERE order_id IS NOT NULL
    AND amount > 0
""")

# Gold: Business-ready aggregates
spark.sql("""
  CREATE OR REPLACE TABLE gold.daily_sales
  USING DELTA
  PARTITIONED BY (order_date)
  AS
  SELECT order_date,
    COUNT(DISTINCT order_id) AS order_count,
    COUNT(DISTINCT customer_id) AS customer_count,
    SUM(amount) AS total_revenue,
    AVG(amount) AS avg_order_value
  FROM silver.orders
  GROUP BY order_date
""")
```

**Delta Live Tables (DLT) for medallion:**
```python
import dlt
from pyspark.sql.functions import *

@dlt.table
def orders_bronze():
    return (
        spark.readStream.format("cloudFiles")
            .option("cloudFiles.format", "json")
            .load("s3://data-lake/raw/orders/")
    )

@dlt.table
@dlt.expect("valid_order_id", "order_id IS NOT NULL")
@dlt.expect_or_drop("positive_amount", "amount > 0")
def orders_silver():
    return (
        dlt.read_stream("orders_bronze")
            .dropDuplicates(["order_id"])
            .select(
                col("order_id"),
                col("customer_id"),
                col("order_date").cast("date"),
                col("amount").cast("decimal(18,2)")
            )
    )

@dlt.table
def daily_sales_gold():
    return (
        dlt.read_stream("orders_silver")
            .groupBy("order_date")
            .agg(
                countDistinct("order_id").alias("order_count"),
                sum("amount").alias("total_revenue")
            )
    )
```

---

## Part 3: Time Travel & Performance (10 min)

**Time travel use cases:**
```python
# Debug: what did the table look like yesterday?
df_yesterday = spark.sql("""
  SELECT * FROM gold.daily_sales
  TIMESTAMP AS OF '2024-01-15'
""")

# Rollback: revert accidental data deletion
spark.sql("""
  RESTORE TABLE gold.daily_sales
  TO VERSION AS OF 124
""")

# Audit: compare before and after
df_before = spark.sql("""
  SELECT SUM(total_revenue) FROM gold.daily_sales
  VERSION AS OF 123
""")
df_after = spark.sql("""
  SELECT SUM(total_revenue) FROM gold.daily_sales
  VERSION AS OF 124
""")
```

**OPTIMIZE, ZORDER, VACUUM:**
```python
# OPTIMIZE: bin-pack small files into larger ones
spark.sql("OPTIMIZE silver.orders")

# ZORDER: multi-dimensional clustering
spark.sql("OPTIMIZE gold.daily_sales ZORDER BY (customer_count, order_date)")

# Auto-optimize writing
spark.conf.set("spark.databricks.delta.autoCompact.enabled", "true")
spark.conf.set("spark.databricks.delta.optimizeWrite.enabled", "true")

# VACUUM: remove old files (run after OPTIMIZE, before retention)
spark.sql("VACUUM silver.orders RETAIN 168 HOURS")  # Keep 7 days
```

**When to use each:**
| Operation | When | Frequency | Duration |
|-----------|------|-----------|----------|
| OPTIMIZE | After many small writes, before big query workloads | Daily-weekly | Min-hours |
| ZORDER | On high-cardinality filter columns | Weekly | Min-hours |
| VACUUM | After OPTIMIZE, to reclaim storage | Weekly | Min |
| ANALYZE STATS | Before important queries, after large changes | Before queries | Sec-min |

---

## Part 4: Concurrent Writes & CDC (10 min)

**Concurrent write handling:**
```python
# Delta Lake uses optimistic concurrency control
# Two concurrent writes:
# Writer 1: INSERT INTO gold.daily_sales WHERE order_date = '2024-01-20'
# Writer 2: INSERT INTO gold.daily_sales WHERE order_date = '2024-01-20'

# If they try to modify same files:
# - First writer succeeds (commits version N+1)
# - Second writer gets ConcurrentAppendException
# - Second writer retries: reads latest version, reapplies change
# - Retry logic is built into Delta (Databricks auto-retries)

# Isolation levels:
spark.conf.set("spark.databricks.delta.isolationLevel", "WRITE_SERIALIZABLE")
# Serializable: strongest (no phantom reads)
# WriteSerializable: good balance (prevents write conflicts)
```

**CDC pipeline with Delta change data feed:**
```python
# Enable change data feed on source table
spark.sql("""
  ALTER TABLE silver.orders
  SET TBLPROPERTIES (delta.enableChangeDataFeed = true)
""")

# Read changes between versions
df_changes = spark.read \
    .format("delta") \
    .option("readChangeFeed", "true") \
    .option("startingVersion", "123") \
    .option("endingVersion", "125") \
    .table("silver.orders")

# Change types: insert, update_preimage, update_postimage, delete
df_changes.select("_change_type", "order_id", "order_date", "amount").show()

# Stream changes to downstream
df_streaming_changes = spark.readStream \
    .format("delta") \
    .option("readChangeFeed", "true") \
    .option("startingVersion", "latest") \
    .table("silver.orders")

df_streaming_changes.writeStream \
    .format("delta") \
    .option("checkpointLocation", "s3://checkpoints/cdc_out") \
    .table("analytics.order_changes")
```

---

## Follow-up Questions

**Transaction log walkthrough:**
```python
# Delta transaction log is at: /table/_delta_log/
# Each commit is a JSON file: 00000000000000000001.json

# Contents of a commit log:
# {
#   "add": {
#     "path": "part-00001-xxx.snappy.parquet",
#     "partitionValues": {"order_date": "2024-01-20"},
#     "size": 12345678,
#     "modificationTime": 1705766400000,
#     "dataChange": true,
#     "stats": "{\"numRecords\":1000,\"minValues\":...,\"maxValues\":...}"
#   },
#   "commitInfo": {
#     "timestamp": 1705766400000,
#     "operation": "WRITE",
#     "operationParameters": {"mode": "Append"},
#     "readVersion": 122
#   }
# }
```

**Schema evolution vs enforcement:**
```python
# Schema enforcement (default): reject writes with new columns
# Schema evolution: automatically add new columns

# Enable evolution on write:
df_new.write \
    .format("delta") \
    .mode("append") \
    .option("mergeSchema", "true") \
    .save("/path/delta_table")

# Or set table property:
spark.sql("""
  ALTER TABLE gold.daily_sales
  SET TBLPROPERTIES (delta.autoMerge.enabled = true)
""")
```

**Delta Sharing for external consumers:**
```python
# Share data with external partner (no need to copy)
spark.sql("""
  CREATE SHARE sales_share
  COMMENT = 'Share daily sales with analytics partner'
""")
spark.sql("GRANT SELECT ON TABLE gold.daily_sales TO SHARE sales_share")
spark.sql("ALTER SHARE sales_share ADD PARTNER partner_analytics")
# Partner accesses with Delta Sharing client
# Recipient token + share URL
```

