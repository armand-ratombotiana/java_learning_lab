# Mock Interview: Data Pipelines (01-data-pipelines)

## Scenario: E-commerce data pipeline failure
Your e-commerce company has a batch pipeline that loads 500M daily clickstream events from S3 into Snowflake. The pipeline started failing at 2 AM with schema mismatch errors. The CEO needs dashboards updated by 8 AM.

### Time: 45 minutes | Difficulty: Hard

---

## Part 1: Pipeline Architecture (15 min)

Design a batch + streaming data pipeline for clickstream events.

**Requirements:**
- 500M events/day, peak 50K events/sec
- SLA: 4-hour freshness for dashboards
- Sources: web (JS SDK), mobile (iOS/Android SDK)
- Destinations: Snowflake (analytics), S3 (ML training), Kafka (real-time features)

**Considerations:**
- Ingestion: Kinesis/Firehose vs Kafka vs direct S3 upload
- Batch: Spark vs Snowpipe vs native Snowflake COPY
- Streaming: Kafka Streams vs Flink vs Spark Structured Streaming
- Storage format: JSON (raw) → Parquet (cleaned)
- Schema management: Schema Registry, schema-on-read vs schema-on-write

**Sample architecture:**
```
Web Clickstream → Kinesis Data Streams
  ├── Firehose (raw JSON to S3, partitioned by hour)
  │     └── Snowpipe (auto-ingest raw into Snowflake staging)
  ├── Kinesis Analytics (Flink) → real-time aggregates → Redis
  └── S3 events → Lambda → schema validation → bad records to DLQ
Bronze (raw JSON) → Silver (cleaned Parquet, Spark batch every 30min) → Gold (aggregates)
```

**Guiding questions:**
- What's your partition strategy for S3? (dt=YYYY-MM-DD/hour=HH/)
- How do you handle 5-minute micro-batch vs 1-hour batch trade-off?
- What's the exactly-once strategy for the streaming path?

---

## Part 2: Debug the Failure (10 min)

The pipeline fails at 2 AM with schema mismatch. Walk through your debugging process.

**Debugging steps:**
1. **Check error log:** What's the exact error? (column type mismatch, missing column, extra column)
2. **Identify source change:** Did the web team deploy new SDK? Did a column get renamed?
3. **Isolate bad records:** Query the error table / dead letter queue for sample bad records
4. **Determine impact:** How many records affected? Since when?
5. **Hotfix:** Quick workaround (type cast, column exclusion, schema override)
6. **Root cause fix:** Schema drift detection, automated evolution, CI/CD checks

**Scenario details:**
The web team added a `user_agent` column as `STRUCT<browser STRING, os STRING, version INT>` without notifying data engineering. Your pipeline expects a flat `user_agent STRING`.

**Your response should cover:**
- How to detect the schema drift automatically
- Whether to accept the new column, transform it, or reject
- Adding schema validation stage before the main pipeline
- Communication process with source teams

---

## Part 3: Optimization (10 min)

The 6-hour pipeline needs to be < 2 hours to meet a new SLA.

**Diagnose first:**
- Profile in Snowflake: Which stages are slow? (COPY, transformation, MERGE)
- Profile in S3: Are too many small files causing slow listing? (use S3 Inventory)
- Profile in Spark (if used): Stages detail, shuffle spill, data skew

**Optimizations to discuss:**
1. **Parallelism:** Increase Snowflake warehouse size (2X-Large → 4X-Large)
2. **Incremental:** Switch from full refresh to incremental load
3. **File size:** Coalesce small files to 128MB+ before load
4. **Partition pruning:** Cluster by event_date, filter partitions
5. **Compression:** Switch to ZSTD for better compression ratio
6. **Streaming:** Add streaming path for time-sensitive data, keep batch for historical
7. **Materialized views:** Pre-compute common aggregations
8. **Load strategy:** Replace MERGE with INSERT-only for append data

**Cost consideration:**
- Larger warehouse costs more. Is the SLA worth the extra cost?
- Incremental processing saves compute but adds complexity

---

## Part 4: Scale + Monitoring (10 min)

**Data grows 10x (5B events/day). How does the architecture change?**

- Kafka partitioning: increase partitions from 16 to 64+
- S3: Add data lifecycle policies (standard → glacier after 90 days)
- Snowflake: Multi-cluster warehouse, automatic clustering, materialized views
- Spark: Increase shuffle partitions, add more executors, use spot instances
- Cost: 10x data ~= 5-8x cost (compression benefits at scale)

**Monitoring metrics to track:**
- Freshness: time since last successful load per table (SLA: < 4h)
- Volume: row count per load, % change vs expected
- Latency: time per stage (extract, transform, load)
- Errors: failure rate, dead letter queue size
- Schema drift: number of drift events per week
- Cost: credits per pipeline run, storage cost per table

---

## Follow-up Questions (Interviewer Notes)

**Late-arriving data:**
- How do you handle clicks that arrive 6 hours late? (mobile offline mode)
- Use `merge_date` (date pipeline processed) vs `event_date` (date event occurred)
- Backfill logic: re-run pipeline for late data partition

**Schema evolution strategy:**
- Add columns as nullable (never NOT NULL initially)
- Version schemas with Schema Registry (Avro/Protobuf)
- Use Snowflake VARIANT type for flexible fields
- Maintain schema_versions table tracking column history

**Backfill 3 months of corrupted data:**
- Isolate affected date range: `WHERE event_date BETWEEN '2024-01-01' AND '2024-03-31'`
- Re-process in parallel by partition (day-level granularity)
- Write to staging tables, validate row counts/profiling
- SWAP partitions: `ALTER TABLE target SWAP PARTITION` or rename tables
- Coordinate with downstream consumers

**Airflow vs Step Functions:**
| Feature | Airflow | Step Functions |
|---------|---------|----------------|
| DAG complexity | Complex dependencies, branching | Simple sequential/parallel |
| Backfill | Built-in (date range) | Manual |
| Monitoring | Rich (logs, metrics, SLA) | Limited (CloudWatch) |
| Cost | Server cost (EC2/K8s) | Per state transition |
| Learning curve | Medium | Low |

