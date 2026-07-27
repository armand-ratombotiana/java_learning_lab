# Databricks Data Engineer Interview Guide

## Interview Structure
- Recruiter Screen (30 min): Spark experience, cloud platform, leveling
- Technical Phone (60 min): PySpark/Scala + SQL + data modeling
- Systems Design (60 min): Medallion architecture, Delta Lake, Unity Catalog
- Behavioral + Design Deep Dive (45 min): Spark troubleshooting, migration
- Hiring Committee (45 min): Leadership, open-source contributions

## Key Topics

### Spark Core
- RDD vs DataFrame vs Dataset: when to use each
- Transformations (lazy) vs Actions (eager)
- Narrow vs wide dependencies (shuffle boundaries)
- Catalyst optimizer and Tungsten execution engine
- AQE (Adaptive Query Execution): coalescing, join strategy switching, skew
- Memory management: on-heap vs off-heap, spark.memory.fraction

### PySpark Patterns
```python
# Broadcast join for small dimensions
df_large.join(broadcast(df_small), "key")
# ReduceByKey vs GroupByKey (map-side combine)
rdd.reduceByKey(lambda a, b: a + b)
# Bucketing for pre-shuffled data
df.write.bucketBy(16, "key").sortBy("key").saveAsTable("bucketed")
# MapPartitions for heavy initialization
df.rdd.mapPartitions(process_partition)
```

### Delta Lake
- ACID transactions via transaction log
- Time travel: VERSION AS OF, TIMESTAMP AS OF
- Schema evolution: mergeSchema option
- OPTIMIZE: bin-packing small files
- ZORDER: multi-dimensional clustering
- VACUUM: remove old files beyond retention
- CHANGE DATA FEED for CDC
- Delta Live Tables (DLT): declarative pipelines with expectations

### Unity Catalog
- Three-level namespace: catalog.schema.table
- Fine-grained access control (RBAC)
- Data lineage tracking
- Auto-flagging sensitive data (classification)
- Delta Sharing for cross-org data sharing

### Medallion Architecture
- Bronze: raw ingested data, append-only
- Silver: deduplicated, validated, enriched
- Gold: business-ready aggregates, star schema

## Sample Questions
1. "Design a CDC pipeline using Delta Live Tables"
2. "How would you optimize a slow Spark join with data skew?"
3. "Design a multi-hop Medallion pipeline for real-time analytics"
4. "Explain how Spark's Catalyst optimizer works"
5. "Design a data governance framework with Unity Catalog"

## Resources
- Databricks Academy: DE certification
- Spark: The Definitive Guide (Chambers & Zaharia)
- Databricks blog: engineering, performance series
