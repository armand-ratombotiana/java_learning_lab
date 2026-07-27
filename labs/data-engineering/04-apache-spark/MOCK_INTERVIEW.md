# Mock Interview: Apache Spark (04-apache-spark)

## Scenario: Optimize a slow Spark job
Your Spark batch job processes 2TB of clickstream data and takes 8 hours. The job does joins, aggregations, and writes to Parquet. The SLA is 4 hours. The DataFrame has schema: (click_id, user_id, session_id, page_url, event_type, timestamp, device_type, geo).

### Time: 45 minutes | Difficulty: Hard

---

## Part 1: Diagnose Performance (15 min)

**Spark UI screens to check first:**

1. **Jobs tab:** How many jobs? Which ones took longest?
2. **Stages tab:** Look for stages with high Shuffle Read/Write or long task times
3. **SQL tab:** Physical plan - which join strategy? Are there SortMergeJoins (expensive)?
4. **Executors tab:** Is there data skew? (some tasks take 10x longer than others)
5. **Storage tab:** Cache/RDD persistence - is data being spilled to disk?

**Common bottlenecks to identify:**

| Symptom | Root Cause | Fix |
|---------|------------|-----|
| Few long-running tasks | Data skew on join/group key | Salting, range partition |
| High shuffle spill | Insufficient memory | Increase shuffle partitions, executor memory |
| Too many small output files | Too many partitions | Coalesce before write |
| Full table scan | Missing partition filter | Add WHERE clause on partition column |
| Slow broadcast join | Table > broadcast threshold (10MB default) | Increase spark.sql.autoBroadcastJoinThreshold |

**Walk-through:**
- Open Spark UI → Stages → Stage 3 (shuffle stage) has 200 tasks
- 190 tasks finish in 30 seconds, 10 tasks take 30 minutes
- This is classic data skew on user_id (power users have 1000x more events)
- Fix: Salting the skewed key before aggregation

---

## Part 2: Join Optimization (10 min)

**Handling data skew in customer_id joins:**

**Problem:** `df_events.join(df_customers, "customer_id")` - some customers have 1M rows, others have 10

**Solution 1: Salted join**
```python
from pyspark.sql.functions import col, concat, rand, lit, floor

# Add salt to skewed side
skewed_events = df_events.filter(col("customer_id").isin(high_volume_ids))
skewed_events = skewed_events.withColumn(
    "salted_key",
    concat(col("customer_id"), lit("_"), floor(rand() * 10))
)

# Replicate dimension by salt factor
salted_customers = (df_customers
    .filter(col("customer_id").isin(high_volume_ids))
    .crossJoin(spark.range(10).toDF("salt"))
    .withColumn("salted_key", concat(col("customer_id"), lit("_"), col("salt")))
    .drop("salt")
)

# Normal join for non-skewed data
normal_events = df_events.filter(~col("customer_id").isin(high_volume_ids))
result = normal_events.join(df_customers, "customer_id") \
    .union(skewed_events.join(salted_customers, "salted_key"))
```

**Solution 2: AQE (Automatic skew join)**
```python
# Enable AQE - Spark 3.x auto-handles moderate skew
spark.conf.set("spark.sql.adaptive.enabled", "true")
spark.conf.set("spark.sql.adaptive.skewJoin.enabled", "true")
spark.conf.set("spark.sql.adaptive.skewJoin.skewedPartitionFactor", "5")
spark.conf.set("spark.sql.adaptive.skewJoin.skewedPartitionThresholdInBytes", "256MB")
```

**Broadcast join vs Sort merge join:**
- Broadcast join: Small table (< 10MB default) sent to all executors, no shuffle
- Sort merge join: Both sides sorted and merged (shuffle required)
- Force broadcast: `df_small.join(broadcast(df_tiny), "key")`
- If dimension table > 10MB, increase `autoBroadcastJoinThreshold`

---

## Part 3: Shuffle Tuning (10 min)

**Key parameters:**
```python
# Number of partitions for shuffles (default 200)
spark.conf.set("spark.sql.shuffle.partitions", "500")

# Shuffle memory buffer (default 32KB)
spark.conf.set("spark.shuffle.file.buffer", "64KB")

# Shuffle spill compression
spark.conf.set("spark.shuffle.compress", "true")
spark.conf.set("spark.shuffle.spill.compress", "true")

# Shuffle merge (Spark 3.x)
spark.conf.set("spark.sql.adaptive.coalescePartitions.enabled", "true")
spark.conf.set("spark.sql.adaptive.coalescePartitions.minPartitionSize", "64MB")
```

**File sizing:**
```python
# Target: 128MB-1GB per file for optimal reading
# Repartition (full shuffle) vs Coalesce (no shuffle if decreasing)
df_result.coalesce(20).write.parquet("output_path")

# For better file sizing control:
df_result.repartition(20).write.option("maxRecordsPerFile", 1000000).parquet("output_path")

# Delta Lake auto-optimize
df_result.write.format("delta").option("optimizeWrite", "true").save("delta_path")
```

**Resource allocation (for 2TB on a 10-node cluster):**
```python
# Per executor: 4 cores, 16GB memory
# Total: 10 nodes × 4 executors = 40 executors
# Memory: 40 × 16GB = 640GB total
spark.conf.set("spark.executor.cores", "4")
spark.conf.set("spark.executor.memory", "16g")
spark.conf.set("spark.executor.memoryOverhead", "4g")
spark.conf.set("spark.driver.memory", "8g")
spark.conf.set("spark.sql.shuffle.partitions", "200")  # 40 executors × 5 partitions each
```

---

## Part 4: Optimization Techniques (10 min)

**Bucketing:**
```python
# Write bucketed by customer_id (pre-shuffle for future joins)
df_events.write \
    .bucketBy(32, "customer_id") \
    .sortBy("event_timestamp") \
    .saveAsTable("events_bucketed")

# Future join eliminates shuffle
df_bucketed.join(df_customers, "customer_id")  # No shuffle needed
```

**Coalesce vs Repartition:**
```python
# Coalesce: No shuffle (narrow transformation), reduces partitions only
df.coalesce(10)  # From 200 to 10 partitions, no shuffle (if decreasing)

# Repartition: Full shuffle (wide transformation), can increase or decrease
df.repartition(10)         # Full shuffle, even distribution
df.repartition(100, "key") # Full shuffle by key, for grouped processing
```

**Key optimizations checklist:**
```python
# Enable all Spark 3.x optimizations
spark.conf.set("spark.sql.adaptive.enabled", "true")
spark.conf.set("spark.sql.adaptive.coalescePartitions.enabled", "true")
spark.conf.set("spark.sql.adaptive.skewJoin.enabled", "true")
spark.conf.set("spark.sql.adaptive.broadcastHashJoin.enabled", "true")

# Avoid UDFs when built-in functions work
# Use DataFrame API instead of RDD
# Filter early, select needed columns, use predicate pushdown
# Cache only when data is reused multiple times

# Write-side optimization (Delta Lake)
df.write.format("delta") \
    .option("delta.autoOptimize.optimizeWrite", "true") \
    .mode("append") \
    .save("/path/delta_table")
```

---

## Follow-up Questions

**AQE (Adaptive Query Execution) - Spark 3.x:**
- Dynamically coalesces shuffle partitions (reduces from 200 to 20 for 2GB data)
- Switches join strategy (SortMergeJoin → BroadcastHashJoin if one side is small)
- Handles data skew by splitting skewed partitions
- Optimizes join ordering based on statistics

**When to use Delta over Parquet:**
- Need ACID transactions
- Need schema evolution
- Need time travel
- Need concurrent reads/writes
- Otherwise, Parquet is simpler and sufficient

**Memory tuning:**
- `spark.memory.offHeap.enabled`: Use off-heap for large data
- `spark.memory.fraction`: Fraction for execution+storage (0.6 default)
- `spark.memory.storageFraction`: Fraction of region for storage (0.5 default)
- If shuffle spills to disk, increase memory or partitions

