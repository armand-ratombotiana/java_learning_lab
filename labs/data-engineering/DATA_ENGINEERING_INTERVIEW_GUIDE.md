# Data Engineering Interview Guide

> Complete preparation guide covering data modeling, SQL, pipeline design, distributed processing (Spark), streaming, orchestration, and cloud platforms.

---

## 1. Data Modeling (15-20% of DE interviews)

### Star Schema
- **Fact tables:** Event-centric, numeric measures, foreign keys to dimensions
- **Dimension tables:** Descriptive attributes, denormalized, surrogate keys
- **Grain:** Declare the atomic level of a fact row before designing

**Key interview points:**
- Why star over 3NF for analytics? (query simplicity, BI tool optimization)
- Additive vs semi-additive vs non-additive facts
- Degenerate dimensions (e.g., order_number in fact table)
- Conformed dimensions across fact tables

### Slowly Changing Dimensions (SCD)
| Type | Behavior | Use Case |
|------|----------|----------|
| 0 | Never change | Birth date, SKU |
| 1 | Overwrite | Email, phone, status |
| 2 | Add new row (history) | Address, customer tier |
| 3 | Previous value column | Territory change |
| 6 | Hybrid Type 2 + 3 | Full history + current value |

**Common question:** "How would you track customer address changes over time?"
- SCD Type 2 with effective_date, end_date, is_current flag
- Surrogate key for joining, natural key for identifying entity
- Snapshot table for fast current-value lookup

### Data Vault
- **Hubs:** Business keys (no descriptive attributes)
- **Links:** Many-to-many relationships
- **Satellites:** Descriptive data with temporal tracking
- **Point-in-time (PIT):** Pre-joined snapshots for performance

**When to use Data Vault:**
- Large enterprise with multiple source systems
- Need full auditability (who changed what, when)
- Source systems change frequently (schema agility)
- Drawback: complex queries, many joins

### Medallion Architecture (Bronze/Silver/Gold)
- **Bronze:** Raw ingested data, append-only, schema-on-read
- **Silver:** Cleaned, validated, deduplicated, business keys resolved
- **Gold:** Aggregated, business-ready, star schema or OBT

---

## 2. SQL Skills (25-30% of DE interviews)

### Core Patterns

**Window Functions (must master):**
- `ROW_NUMBER()`, `RANK()`, `DENSE_RANK()` - ranking and dedup
- `LAG()`, `LEAD()` - time-series comparisons, change detection
- `FIRST_VALUE()`, `LAST_VALUE()` - boundary values per group
- `SUM() OVER`, `AVG() OVER` - running totals, moving averages
- `NTILE()` - quantile distribution
- `ROWS`, `RANGE`, `GROUPS` frame specifications

**Complex Joins:**
- Self-joins for graph/hierarchy data
- Anti-joins (`LEFT JOIN ... IS NULL` or `NOT EXISTS`)
- Semi-joins (`EXISTS` or `IN`)
- Lateral joins with `LATERAL VIEW EXPLODE` / `UNNEST`

**Common aggregations:**
- Pivot using conditional aggregation (`SUM(CASE WHEN ...)`)
- Cohort analysis with date_trunc
- Funnel analysis with step-by-step conversion
- Retention and churn calculations

**Presto/Trino specific:**
- `UNNEST` for array exploration
- `approx_distinct` for hyperloglog-based counting
- `approx_percentile` for quantile approximations
- `map_agg`, `map_concat` for key-value patterns

### Optimization Techniques
- Partition pruning: filter on partition columns
- Cluster pruning: filter on cluster/sort keys
- Predicate pushdown: filter early in the query
- Avoiding `SELECT *` (costly for columnar databases)
- Using materialized views for pre-computed aggregates
- Query profiling: EXPLAIN ANALYZE to find bottlenecks

---

## 3. Pipeline Design (20-25% of DE interviews)

### Batch Pipelines

**Components:**
1. **Extract:** API, database, file system, SaaS connector
2. **Transform:** Clean, validate, join, aggregate
3. **Load:** Full refresh vs incremental vs upsert

**Scheduling patterns:**
- Daily/hourly: fixed schedule
- Trigger-based: source completion event
- Backfill: re-run historical dates
- Incremental: only new/changed data

**Error handling:**
- Retries with exponential backoff
- Dead letter queue for failed records
- Data quality gates before load
- Circuit breaker for source system overload

### Streaming Pipelines

**Components:**
1. **Source:** Kafka, Kinesis, Pub/Sub, file watcher
2. **Processing:** Stateless (filter, map) vs stateful (aggregation, join)
3. **Sink:** Database, warehouse, lake, message queue

**Windowing strategies:**
- Tumbling: fixed non-overlapping windows
- Sliding: fixed overlapping windows (every N seconds, window of M seconds)
- Session: windows defined by inactivity gap
- Global: single window; manual trigger

**Delivery semantics:**
- At-most-once: fast but may miss data
- At-least-once: no loss but possible duplicates
- Exactly-once: idempotent sinks + offset tracking

### Orchestration

**Airflow (most common in interviews):**
- DAG structure: start >> task1 >> task2 >> end
- Operators: Python, SQL, Bash, Kubernetes, Sensors
- Executors: Sequential, Local, Celery, Kubernetes
- DAG best practices: idempotency, retries, backfills, SLAs

**Questions to expect:**
- How do you handle task dependencies and retries?
- How do you backfill historical data with a DAG?
- How do you monitor DAG performance and reliability?
- What's the difference between Airflow, Dagster, and Prefect?

---

## 4. Distributed Processing - Apache Spark (15-20% of DE interviews)

### Core Concepts

**RDD (Resilient Distributed Dataset):**
- Immutable, partitioned, lazily evaluated
- Transformations: map, filter, flatMap, reduceByKey, groupByKey
- Actions: count, collect, save, reduce, foreach

**DataFrame (most common in DE interviews):**
- Columnar, higher-level abstraction
- SQL queries directly on DataFrames
- Optimized by Catalyst Optimizer + Tungsten

**Spark Execution:**
1. Driver creates SparkContext and DAG
2. DAG Scheduler creates stages (shuffle boundary)
3. Task Scheduler assigns tasks to executors
4. Executors run tasks on partitions
5. Results sent back to driver (for actions)

### Performance Optimization

**Shuffle optimization:**
- Avoid wide dependencies when possible
- Use `reduceByKey` instead of `groupByKey` (map-side combine)
- Set `spark.sql.shuffle.partitions` appropriately (rule of thumb: N-1 cores)
- Bucketing eliminates shuffle for subsequent joins

**Join strategies:**
- **Broadcast join:** For small tables (< ~10MB default)
- **Sort merge join:** Default for large tables; sorts both sides
- **Hash join:** For equi-joins; builds hash table
- **Bucketed join:** Pre-shuffled data; eliminates shuffle

**Data skew:**
- Detect: some tasks take much longer than others
- Fix: salting (add random key suffix), range partitioning
- AQE (Adaptive Query Execution) auto-coalesces partitions

### Spark vs MapReduce
- Spark: in-memory, DAG-based, faster for iterative algorithms
- MapReduce: disk-based, simpler, more stable for large batch
- Spark SQL: unified batch/streaming, SQL interface

### Spark Structured Streaming
- Micro-batch engine (continuous processing experimental)
- Exactly-once with checkpointing + WAL
- Stateful operations: aggregation, stream-stream joins
- Watermarks for late data handling

---

## 5. Streaming (10-15% of DE interviews)

### Kafka

**Architecture:**
- Topics → Partitions → Brokers
- Producers write to partitions (key-based or round-robin)
- Consumer groups coordinate partition consumption
- ZooKeeper/KRaft for cluster management

**Durability and availability:**
- Replication factor: N copies across brokers
- ISR (In-Sync Replicas): replicas caught up with leader
- acks=0: fire and forget
- acks=1: leader acknowledged (default)
- acks=all: all ISRs acknowledged (safest)

**Performance tuning:**
- `batch.size`: producer batch size (16KB default)
- `linger.ms`: time to wait for batch (0ms default)
- `compression.type`: gzip, snappy, lz4, zstd
- `buffer.memory`: total memory for producer buffering

**Kafka Streams:**
- KStream vs KTable vs GlobalKTable
- Exactly-once via transactions and idempotent producer
- State stores (RocksDB, in-memory)
- Interactive queries (query state store directly)

### Apache Flink

**Key differentiating points from Spark Streaming:**
- True streaming (event-by-event, not micro-batches)
- Lower latency (sub-second vs seconds)
- More sophisticated state management
- Better event-time handling with watermarks

**Flink concepts:**
- **Checkpointing:** distributed snapshots of state
- **Savepoints:** manual snapshots for upgrades
- **Watermarks:** track event-time progress
- **Timers:** event-time and processing-time callbacks
- **State:** keyed state (ValueState, ListState, MapState, AggregatingState)

### ksqlDB
- SQL interface to Kafka Streams
- Streaming queries (continuous) vs pull queries (point-in-time)
- Materialized views for real-time dashboards

---

## 6. Orchestration (5-10% of DE interviews)

### Apache Airflow

**Architecture:**
- **Webserver:** UI for DAG management
- **Scheduler:** Parses DAGs, schedules tasks
- **Executor:** Runs tasks (Local, Celery, Kubernetes)
- **Worker:** Executes task instances
- **Metadata DB:** Stores DAG runs, task instances, variables

**DAG design patterns:**
```python
# Branching
@task.branch
def choose_branch():
    if condition: return 'task_a'
    return 'task_b'

# Dynamic task mapping
results = expand_task.expand(data=items)

# SubDAG (deprecated) vs TaskGroup
with TaskGroup('data_processing'):
    task1 >> task2 >> task3
```

**Best practices:**
- `depends_on_past=True` for sequential pipelines
- `max_active_runs=1` to prevent overlapping
- `catchup=False` for non-backfill runs
- `retries=2, retry_delay=timedelta(minutes=5)` for fault tolerance
- Idempotent tasks (safe to re-run)
- Use TaskFlow API (Python decorators) over classic operators

### Dagster vs Prefect vs Airflow

| Aspect | Airflow | Dagster | Prefect |
|--------|---------|---------|---------|
| DAG definition | Python DAG file | Software-defined assets | Flows/Tasks |
| Testing | unit test with trigger | Asset decorators, CLI test | prefect.testing |
| Observability | logs, statsd, graphite | Dagit UI, run history | Prefect UI, webhooks |
| CI/CD | git-sync, image build | dagster-cloud | prefect deploy |
| Learning curve | Medium | Medium-High | Low |

---

## 7. Cloud Platforms (5-10% of DE interviews)

### AWS Data Stack

| Service | Role | Interview Focus |
|---------|------|----------------|
| S3 | Data lake storage | Partitioning, lifecycle, Athena |
| Glue | ETL, Data Catalog | Crawlers, ETL jobs, schema inference |
| EMR | Spark cluster | Cluster types, auto-scaling, Spot instances |
| Redshift | Data warehouse | Distribution/sort keys, WLM, concurrency |
| Kinesis | Streaming | Shards, partitioning, KCL consumer |
| Lambda | Serverless compute | Triggers, scaling, timeouts |
| Step Functions | Orchestration | State machines, error handling |

**Design pattern for AWS:**
```
Kinesis Data Streams → Kinesis Data Analytics (Flink) → S3 (Parquet)
     +→ Lambda (real-time alerts)
     +→ Redshift (via Firehose)
S3 → Glue ETL (Spark) → Redshift / S3 (curated)
Step Functions orchestrates ETL jobs
```

### GCP Data Stack

| Service | Role | Interview Focus |
|---------|------|----------------|
| GCS | Data lake storage | Object lifecycle, requester pays |
| BigQuery | Data warehouse | Slots, partitioning, clustering, BI Engine |
| Dataflow | Stream/batch processing | Beam pipeline, I/O, windowing |
| Pub/Sub | Messaging | Push vs pull, ordering, exactly-once |
| Dataproc | Managed Spark/Hadoop | Cluster scaling, initialization actions |
| Composer | Managed Airflow | Airflow on GCP, GKE integration |

### Azure Data Stack

| Service | Role | Interview Focus |
|---------|------|----------------|
| ADLS Gen2 | Data lake | Hierarchical namespace, ACLs |
| Synapse | Analytics | Dedicated vs serverless SQL, pipelines |
| Data Factory | ETL orchestration | Copy Data, Data Flow, SSIS integration |
| Databricks | Spark | Unity Catalog, Delta Lake |
| Event Hubs | Streaming | Throughput units, consumer groups |
| Power BI | Visualization | Direct Lake, DAX, semantic models |

---

## 8. Interview-by-Interview Timeline

### Week 1-2: Foundation
- Review data modeling (star, snowflake, data vault, medallion)
- Master SQL window functions (daily practice on LeetCode)
- Review Spark core concepts (RDD, DataFrame, optimization)

### Week 3-4: System Design
- Practice one system design per day (see SYSTEM_DESIGN_CHEATSHEET.md)
- Draw architectures on paper or whiteboard
- Practice trade-off discussions (batch vs stream, Lambda vs Kappa)

### Week 5: Company Research
- Read company engineering blogs
- Understand their data stack (Snowflake vs Databricks vs BigQuery)
- Prepare 3-5 STAR behavioral stories

### Week 6: Mock Interviews
- Do 3-5 mock interviews (friends, peers, professional services)
- Time-box each section
- Record and review your responses

### Week 7+: Apply and Interview
- Target 3-4 companies at a time
- Rejections are learning experiences
- Revise weak areas between interviews

---

## 9. Common DE Interview Mistakes

| Mistake | Correct Approach |
|---------|-----------------|
| Jumping to solution without clarifying requirements | Ask about scale, latency, data volume, stakeholders |
| Ignoring trade-offs | Always discuss pros/cons of your design decisions |
| Not using the STAR format | Structure behaviorals: S-ituation, T-ask, A-ction, R-esult |
| Writing code without talking | Think aloud, explain your thought process |
| Forgetting monitoring/alerting | Every pipeline needs observability |
| Over-engineering the solution | Start simple, then discuss scaling |
| Not asking questions | Ask about the team, tech stack, challenges |

---

## 9. File Formats & Storage

### Columnar Formats (Parquet vs ORC)

| Feature | Parquet | ORC |
|---------|---------|-----|
| Compression | Snappy, ZSTD, Gzip, LZ4 | Zlib, Snappy, ZSTD |
| Nested types | Native (repeated/optional) | Native (STRUCT, LIST, MAP) |
| ACID support | Via Delta/Iceberg | Native (Hive ACID) |
| Predicate pushdown | Column stats (min/max) | Column stats + indexes |
| Write performance | Good | Better (strip-level indexes) |
| Read performance | Excellent (columnar) | Excellent |
| Ecosystem | Spark, Trino, Hive, Flink | Hive, Spark, Trino |
| Best for | Multi-engine lakehouses | Hive-heavy environments |

**Key interview topic:** Why Parquet is the de facto standard for Spark/Databricks (native support, predicate pushdown, efficient compression).

### Row Formats (Avro)

| Feature | Avro |
|---------|------|
| Schema | Embedded JSON schema |
| Compression | Snappy, Deflate, ZSTD |
| Splittable | Yes (with container file) |
| Best for | Kafka, streaming, WAL logs |
| Schema evolution | Full support (reader/writer schemas) |

**When to use Avro:**
- Kafka messages (schema registry integration)
- CDC events (Debezium outputs Avro)
- Write-heavy workloads (row format is faster for writes)

### Compression Comparison

| Codec | Speed (compress) | Speed (decompress) | Ratio | Splittable |
|-------|-----------------|-------------------|-------|------------|
| Snappy | Very fast | Very fast | 2:1 | Yes (container) |
| ZSTD | Fast | Fast | 3:1 | Yes (container) |
| Gzip | Slow | Fast | 4:1 | Yes (text), No (binary) |
| LZ4 | Very fast | Very fast | 2:1 | Yes (container) |
| Zlib | Slow | Slow | 4:1 | Yes (container) |

**Rule of thumb:** Snappy for speed, ZSTD for balance, Gzip for archival.

---

## 10. Data Security & Compliance

### Encryption
- **In transit:** TLS 1.2+ for all data movement
- **At rest:** Server-side encryption (SSE-S3, SSE-KMS, CSE)
- **Column-level:** Tokenization/masking for PII (Snowflake Dynamic Masking, BigQuery Column ACL)

### Access Control
- **RBAC:** Role-based access (analyst, engineer, admin, consumer)
- **ABAC:** Attribute-based (time-based, location-based)
- **Row-level security:** Filter rows based on user attributes
- **Column-level security:** Mask or restrict sensitive columns

### Compliance Frameworks
- **GDPR:** Right to deletion, data portability, consent management
- **CCPA:** Right to know, right to delete, opt-out of sale
- **HIPAA:** PHI protection, BAAs, audit logging, access controls
- **SOC2:** Security, availability, processing integrity, confidentiality, privacy
- **SOX:** Financial data integrity, audit trails, segregation of duties

### Data Retention
| Tier | Storage | Retention | Cost |
|------|---------|-----------|------|
| Hot | SSD/NVMe | 7-30 days | Expensive |
| Warm | HDD/Standard | 30-90 days | Moderate |
| Cold | Archive (Glacier/GCS Archive) | 90-365 days | Cheap |
| Frozen | Tape/Deep Archive | 1-10 years | Cheapest |

---

## 11. Cost Optimization

### Cloud Storage Costs
- **S3:** Standard > Infrequent Access > Glacier > Deep Archive
- **Lifecycle policies:** Auto-transition objects between tiers
- **Object size:** Larger files (128MB+) are cheaper per GB (fewer API calls)

### Compute Costs

**Snowflake:**
- Auto-suspend after N minutes of inactivity
- Multi-cluster warehouse only for concurrency needs
- Use materialized views instead of frequent query recomputation
- Right-sizing: start with XS, monitor, scale up

**BigQuery:**
- On-demand vs slot reservations
- Flat-rate slots for predictable workloads
- BI Engine for dashboard acceleration (cached)

**Databricks:**
- Autoscaling clusters (min/max workers)
- Spot instances for non-critical workloads
- Delta cache for reused data
- Job clusters (auto-terminate) vs all-purpose

### Data Storage Tiers
1. **Raw bronze:** 90-day retention, compressed Parquet
2. **Silver cleansed:** 30-day retention, deduplicated
3. **Gold aggregates:** 7-day retention, pre-computed
4. **Long-term archive:** 7-year retention, Glacier

---

## 12. Interview-by-Interview Timeline

### Week 1-2: Foundation
- Review data modeling (star, snowflake, data vault, medallion)
- Master SQL window functions (daily practice on LeetCode)
- Review Spark core concepts (RDD, DataFrame, optimization)

### Week 3-4: System Design
- Practice one system design per day (see SYSTEM_DESIGN_CHEATSHEET.md)
- Draw architectures on paper or whiteboard
- Practice trade-off discussions (batch vs stream, Lambda vs Kappa)

### Week 5: Company Research
- Read company engineering blogs
- Understand their data stack (Snowflake vs Databricks vs BigQuery)
- Prepare 3-5 STAR behavioral stories

### Week 6: Mock Interviews
- Do 3-5 mock interviews (friends, peers, professional services)
- Time-box each section
- Record and review your responses

### Week 7+: Apply and Interview
- Target 3-4 companies at a time
- Rejections are learning experiences
- Revise weak areas between interviews

---

## 13. Common DE Interview Mistakes

| Mistake | Correct Approach |
|---------|-----------------|
| Jumping to solution without clarifying requirements | Ask about scale, latency, data volume, stakeholders |
| Ignoring trade-offs | Always discuss pros/cons of your design decisions |
| Not using the STAR format | Structure behaviorals: S-ituation, T-ask, A-ction, R-esult |
| Writing code without talking | Think aloud, explain your thought process |
| Forgetting monitoring/alerting | Every pipeline needs observability |
| Over-engineering the solution | Start simple, then discuss scaling |
| Not asking questions | Ask about the team, tech stack, challenges |

---

## 14. Final Checklist

**Before each interview:**
- [ ] Research company's data stack
- [ ] Prepare 3 STAR stories (pipeline failure, data quality, technical disagreement)
- [ ] Review SQL window functions (write them from memory)
- [ ] Know Spark optimization techniques (broadcast, bucketing, AQE)
- [ ] Practice one system design (whiteboard or paper)
- [ ] Prepare 2-3 questions to ask the interviewer
- [ ] Charge your laptop, check internet, find quiet space

**Technical topics to review week of interview:**
- SQL: window functions, MERGE, SCD Type 2, CTEs
- Spark: Catalyst, Tungsten, AQE, shuffle optimization
- Streaming: Kafka architecture, exactly-once, watermarking
- Cloud: The specific cloud(s) the company uses
- Data modeling: Star schema, medallion architecture
- Orchestration: Airflow best practices, DAG design

**Questions to ask the interviewer:**
1. "What does a typical data pipeline look like on your team?"
2. "How does the team handle data quality and monitoring?"
3. "What's the current data stack and what are the biggest challenges?"
4. "How does the data team collaborate with product and engineering?"
5. "What's the most impactful project the data team has shipped recently?"

---

## 15. Glossary of Key Terms

| Term | Definition |
|------|------------|
| ACID | Atomicity, Consistency, Isolation, Durability |
| AQE | Adaptive Query Execution - Spark optimization |
| CDC | Change Data Capture |
| CTE | Common Table Expression (WITH clause) |
| DAG | Directed Acyclic Graph |
| DWH | Data Warehouse |
| ETL | Extract, Transform, Load |
| ELT | Extract, Load, Transform |
| ISR | In-Sync Replicas (Kafka) |
| OLAP | Online Analytical Processing |
| OLTP | Online Transaction Processing |
| SCD | Slowly Changing Dimension |
| WAL | Write-Ahead Log (PostgreSQL CDC) |
| WLM | Workload Management (Redshift) |
| ZSTD | Zstandard compression algorithm |
