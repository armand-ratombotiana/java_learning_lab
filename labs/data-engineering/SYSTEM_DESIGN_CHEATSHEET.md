# Data Engineering System Design Cheatsheet

> Comprehensive system design guide for data engineering interviews covering batch/streaming, warehouse, lake, lakehouse, orchestration, and data modeling.

---

## 1. Batch vs Streaming Architecture

### Lambda Architecture

```
Batch Layer             Speed Layer             Serving Layer
+----------------+      +----------------+      +-------------+
| Historical     |      | Real-time      |      | Merged      |
| Data (S3/HDFS) |      | Stream (Kafka) |      | View        |
|                |      |                |      |             |
| Spark Batch    |      | Flink/Spark    |      | Presto/     |
| Job (Hourly)   |      | Stream (Min)   |      | Trino       |
+-------+--------+      +-------+--------+      +------+------+
        |                        |                      |
        +------------------------+----------------------+
                 Merge: Batch View + Real-Time View
```

**Key concepts:**
- Two code paths: batch recomputation + stream incremental
- Merge by key: `COALESCE(batch_value, stream_value)` or `batch + stream_delta`
- Trade-off: accuracy vs latency
- Re-processing: drop and recompute batch, replay stream

**Pros:** Accurate historical data, proven technology
**Cons:** Two code paths to maintain, merge complexity

### Kappa Architecture

```
Kafka (Immutable Log)
   |
   v
Stream Processor (Kafka Streams / Flink)
   |
   +--> Real-time View (Low latency)
   +--> Materialized View (High throughput)
   |
   v
Serving Layer (State Store / Database)
```

**Key concepts:**
- Single code path: all data processed as streams
- Kafka as source of truth (replayable)
- Re-processing: reset consumer offsets to beginning
- State stores for aggregation (RocksDB, RDBMS)

**Pros:** Simpler codebase, unified processing, easy replay
**Cons:** Harder to handle large state, state store management

### When to Use Which

| Scenario | Architecture | Reason |
|----------|-------------|--------|
| Real-time fraud detection | Kappa | Low latency requirement |
| Daily financial reporting | Lambda | Accuracy + auditability |
| Real-time dashboard with historical | Kappa | Unified streaming |
| Machine learning features | Kappa | Continuous feature computation |
| Regulatory compliance | Lambda | Reprocessable, auditable |

---

## 2. Data Warehouse Architecture

### Snowflake Architecture

```
+-------------------------------------------------------+
|                  Cloud Services Layer                  |
| Authentication | Query Optimization | Metadata |       |
| Transaction Manager | Infrastructure Manager          |
+---------------------------+---------------------------+
                            |
+--------+------------------+------------------+--------+
| Virtual |    Virtual       |    Virtual       | Virtual |
| Warehouse|  Warehouse      |   Warehouse      |Warehouse|
| (XS-6XL) | (Auto-Suspend) | (Multi-Cluster)  | (SQL)   |
+--------+------------------+------------------+--------+
                            |
+---------------------------+---------------------------+
|               Storage Layer (S3/Blob/GCS)             |
| Compressed | Columnar | Micro-partitioned | Encrypted |
+-------------------------------------------------------+
```

**Key components:**
- **Micro-partitions:** 50-500 MB, automatically managed, column metadata
- **Pruning:** Automatic elimination of micro-partitions based on query filters
- **Clustering keys:** Co-locate related rows; automatic clustering maintenance
- **Caching:** Results cache (24h), metadata cache, warehouse data cache
- **Time travel:** `AT(TIMESTAMP => ...)` or `BEFORE(STATEMENT => ...)`
- **Zero-copy cloning:** `CREATE TABLE t CLONE source_table`

**Performance optimization:**
- Set clustering keys on high-cardinality filter columns
- Use materialized views for pre-aggregation
- Right-size warehouse: start with XS, scale up for complex queries
- Multi-cluster warehouse for concurrent users
- Search optimization service for point lookups

### Amazon Redshift Architecture

```
+---------------------------------------------+
|       Elastic Network                        |
|   +---------+   +---------+   +---------+   |
|   | Compute |   | Compute |   | Compute |   |
|   | Node 1  |   | Node 2  |   | Node N  |   |
|   |  +----+ |   |  +----+ |   |  +----+ |   |
|   |  |Slice| |   |  |Slice| |   |  |Slice| |
|   |  +----+ |   |  +----+ |   |  +----+ |   |
|   +----+----+   +----+----+   +----+----+   |
|        |             |             |         |
+--------+-------------+-------------+---------+
         |             |             |
    +----+-------------+-------------+--------+
    |           S3 Data Lake                   |
    +-----------------------------------------+
```

**Distribution styles:**
- **KEY:** Rows distributed by one column (join key) - best for fact-dim joins
- **ALL:** Full copy on all nodes - for small dimension tables
- **EVEN:** Round-robin - when no clear distribution key

**Sort keys:**
- **COMPOUND:** Multi-column, leading column most selective
- **INTERLEAVED:** All columns equally important, but higher maintenance

### Google BigQuery Architecture

```
+--------------------------------------------------+
|                 Client/BI Tools                   |
+--------------------------------------------------+
                    |
+--------------------------------------------------+
|              Dremel Query Engine                   |
|  Root Server -> Mixer -> Leaf Workers (Columnar)   |
+--------------------------------------------------+
                    |
+------------------------------------------+-------+
|   Colossus (Distributed Storage)         |  BI   |
|   Capacitor (Columnar Format)            | Engine|
|   Clustering | Partitioning              | (Mem) |
+------------------------------------------+-------+
```

**Optimization strategies:**
- Partition by date/timestamp for large tables
- Cluster by high-cardinality columns (sorted after partition)
- Use materialized views for repetitive aggregations
- Avoid `SELECT *` - only select needed columns
- Use approximate functions (`APPROX_COUNT_DISTINCT`) when acceptable
- Slot reservations for predictable performance

---

## 3. Data Lake Architecture

### AWS S3 Data Lake

```
+---------------------------------------------------+
|                 Data Sources                        |
|  OLTP (RDS) | SaaS (Salesforce) | IoT | Webhooks   |
+-----------------------+---------------------------+
                        |
+-----------------------+---------------------------+
|             Ingestion Layer                         |
|  Kinesis Firehose | Glue ETL | DMS | AppFlow       |
+-----------------------+---------------------------+
                        |
+-----------------------+---------------------------+
|              Storage Layer (S3)                     |
|  Raw Bucket: data/raw/{source}/{date}/             |
|  Staging: data/staging/{pipeline_id}/              |
|  Curated: data/curated/{domain}/{table}/            |
+-----------------------+---------------------------+
                        |
+-----------------------+---------------------------+
|              Processing Layer                        |
|  Glue Jobs | EMR (Spark) | Athena | Redshift       |
+-----------------------+---------------------------+
                        |
+-----------------------+---------------------------+
|             Catalog & Governance                     |
|  Glue Data Catalog | Lake Formation |             |
|  Parquet/ORC/Iceberg Format                         |
+---------------------------------------------------+
```

**Best practices:**
- Use Hive-style partitioning: `dt=2024-01-01/hour=12/`
- Choose file size ~128MB-1GB per partition
- Use columnar formats (Parquet/ORC) for analytical queries
- Implement lifecycle policies for data retention
- Enable S3 Inventory for large bucket management

### Azure Data Lake Storage (ADLS)

```
+---------------------------------------------------+
|             Azure Data Factory                      |
|   Copy Data | Mapping Data Flow | SSIS Integration  |
+-----------------------+---------------------------+
                        |
+-----------------------+---------------------------+
|    ADLS Gen2 (Hierarchical Namespace)               |
|   /raw/ /stage/ /curated/ /gold/                    |
|   +-- Delta Lake format (Spark)                     |
|   +-- OneLake shortcuts (Fabric)                    |
+-----------------------+---------------------------+
                        |
+-----------------------+---------------------------+
|   Synapse | Databricks | HDInsight | Power BI       |
+---------------------------------------------------+
```

**Best practices:**
- Hierarchical namespace for directory-level permissions
- POSIX ACLs for fine-grained access control
- Soft delete for accidental deletion protection
- Version-level immutability for compliance

### Google Cloud Storage (GCS)

```
+---------------------------------------------------+
|           Storage Classes                           |
| Standard | Nearline | Coldline | Archive           |
| (30 days) | (90 days) | (365 days) | (365+ days)   |
+---------------------------------------------------+
```

**Best practices:**
- Object lifecycle management for automatic tiering
- Uniform vs fine-grained access control
- Object versioning for backup and recovery
- Requester pays for shared datasets

---

## 4. Lakehouse Architecture

### What is a Lakehouse?

**Definition:** Combines data lake flexibility with warehouse ACID properties.

**Core components:**
1. **ACID transactions:** Atomic commits, isolation for concurrent readers/writers
2. **Schema enforcement:** Schema on write (vs schema on read in pure lakes)
3. **Time travel:** Query historical versions of data
4. **Unified batch/streaming:** Single table format for both

### Delta Lake (Databricks)

```
+---------------------------------------------+
|         Delta Table on S3/ADLS/GCS           |
|  /data/table_name/                           |
|   +-- _delta_log/                            |
|   |   +-- 00000000000000000001.json          |
|   |   +-- 00000000000000000002.json          |
|   |   +-- ...                                |
|   +-- part-00001-xxx.snappy.parquet          |
|   +-- part-00002-xxx.snappy.parquet          |
|   +-- ...                                    |
+---------------------------------------------+
```

**Transaction log:** JSON files recording every commit
- Protocol version
- Metadata updates
- Add/remove file actions
- Commit info (timestamp, user, operation)

**Key features:**
- **ACID:** Concurrent writes via optimistic concurrency control
- **Schema evolution:** Auto-merge schema changes
- **Time travel:** `VERSION AS OF` or `TIMESTAMP AS OF`
- **ZORDER:** Multi-dimensional clustering (co-locates related column values)
- **OPTIMIZE:** Bin-packing small files into larger ones
- **VACUUM:** Remove old files beyond retention period
- **Generated columns:** Auto-populated columns for partitioning

### Apache Iceberg (Open)

```
+---------------------------------------------+
|           Iceberg Table Structure             |
|  /warehouse/db/table/                        |
|   +-- metadata/                              |
|   |   +-- v1.metadata.json                   |
|   |   +-- v2.metadata.json                   |
|   |   +-- snap-12345-xxx.avro                |
|   +-- data/                                  |
|       +-- 00000-0-xxx.parquet                |
|       +-- 00001-0-xxx.parquet                |
+---------------------------------------------+
```

**Metadata layer:**
1. **Metadata file:** Table schema, partitioning, snapshot references
2. **Manifest list:** List of manifest files per snapshot
3. **Manifest files:** List of data files with min/max statistics
4. **Data files:** Actual columnar data (Parquet/ORC/Avro)

**Key features:**
- **Hidden partitioning:** Partition by derived values (e.g., `month(ts)`)
- **Evolution:** Schema and partition evolution without rewrites
- **Time travel:** Snapshot-based querying
- **File pruning:** Column stats in manifests for partition elimination
- **Catalog integration:** Hive, Nessie, JDBC, REST, Glue, DynamoDB
- **Data compaction:** Rewriting small files into optimal size

### Apache Hudi

```
+---------------------------------------------+
|              Hudi Table Types                 |
|                                               |
|  Copy-on-Write (COW):                        |
|   - Parquet files only                       |
|   - Write: rewrite entire file               |
|   - Read: read parquet files                  |
|                                               |
|  Merge-on-Read (MOR):                        |
|   - Parquet base files + log files           |
|   - Write: append to delta log               |
|   - Read: merge base + log on read           |
+---------------------------------------------+
```

**Key features:**
- **Incremental queries:** Pull only changed records since last commit
- **UPSERT:** Native merge with index tracking
- **Clustering:** Reorganize data for better file sizing
- **Compaction:** Merge log files into base files (MOR)
- **Table services:** Automatic cleaning, clustering, compaction

### Delta Lake vs Iceberg vs Hudi

| Feature | Delta Lake | Apache Iceberg | Apache Hudi |
|---------|-----------|----------------|-------------|
| Open format | Yes (Parquet + JSON log) | Yes (Parquet/ORC + Avro) | Yes (Parquet + Avro log) |
| ACID | Optimistic concurrency | Optimistic concurrency | Optimistic concurrency |
| Schema evolution | Yes (explicit) | Yes (explicit) | Yes (explicit) |
| Time travel | Yes (version/timestamp) | Yes (snapshot) | Yes (timestamp) |
| Partition evolution | No (rewrite required) | Yes (evolve without rewrite) | Yes (auto) |
| Incremental queries | No (requires Delta Sharing) | No (requires consistency wrapper) | Yes (native) |
| Ecosystem | Databricks, Spark | Spark, Flink, Trino, Hive | Spark, Flink, Presto |
| Best for | Databricks lakehouse | Open multi-engine lakehouse | High-volume UPSERT/CDC |

---

## 5. Pipeline Orchestration

### Airflow DAG Design

```
+-------------------------------------------------------+
|                    DAG Structure                        |
|                                                         |
|  start >> extract >> validate >> transform >> load      |
|                            |                            |
|                          quality_check                  |
|                            |                            |
|                         (fail or continue)              |
|                            |                            |
|                  [if fail] send_alert                    |
|                  [if pass] >> done                      |
+-------------------------------------------------------+
```

**Best practices:**
- **Idempotency:** Running a DAG twice should produce same result
- **Retries:** Transient failures: retry 2-3 times with exponential backoff
- **Backfills:** DAGs should support date-range backfilling
- **Dependency management:** `depends_on_past`, `wait_for_downstream`
- **Task boundaries:** One task = one atomic operation
- **XComs size:** < 48KB (store in S3/GCS for larger data)
- **Dynamic DAGs:** Use DAG factory pattern for repeated patterns

**Executor choices:**
| Executor | Scale | Isolation | Cost |
|----------|-------|-----------|------|
| Sequential | Dev only | None | Free |
| Local | Single node | Process-based | Free |
| Celery | Multi-node | Pod-level | Moderate |
| Kubernetes | Multi-node | Container-level | Variable |
| CeleryK8s | Hybrid | Both | High |

**Common patterns:**
- **Data quality gate:** Fail pipeline if row count drops > 20%
- **Timeout handling:** `execution_timeout=timedelta(hours=2)` per task
- **SLAs:** `sla=timedelta(hours=12)` for DAG-level SLA monitoring
- **Dynamic tasks:** `expand()` for map-style task generation
- **Deferrable operators:** Long-running tasks without holding worker slots

### Dagster vs Prefect vs Airflow

| Feature | Airflow | Dagster | Prefect |
|---------|---------|---------|---------|
| DAG definition | Python DAG file | Python with @ops/@jobs | Python with @task/@flow |
| Asset management | External | First-class (Software Defined Assets) | External |
| Testing | Unit tests with trigger | Asset materializations | CLI test, Orion testing |
| Observability | StatsD, logs | Dagit UI, asset lineage | Prefect UI, notifications |
| Execution | Executor-based | Executor-based | Agent/worker-based |
| Scaling | Difficult at 1000+ DAGs | Better with partitioning | Good with auto-scaling |
| CI/CD | GitSync | Customizable | Work pools |
| Best for | Traditional scheduling | Data platform teams | ML pipelines, experimentation |

---

## 6. Streaming Architecture

### Kafka Streams

```
+---------------------------------------------+
|              Kafka Streams Topology           |
|                                               |
|  source_processor                             |
|     |                                         |
|  map_values (transform)                       |
|     |                                         |
|  group_by (repartition)                       |
|     |                                         |
|  aggregate (state store)                      |
|     |                                         |
|  to (output topic)                           |
+---------------------------------------------+
```

**Key concepts:**
- **KStream:** Record stream (INSERT only)
- **KTable:** Changelog stream (UPDATE/DELETE - latest value per key)
- **GlobalKTable:** Full replica of a KTable on every node
- **State stores:** RocksDB (disk-based) or in-memory
- **Interactive queries:** Query state stores directly from applications
- **Exactly-once:** `processing.guarantee=exactly_once_v2`

### Apache Flink

```
+---------------------------------------------+
|              Flink Streaming Pipeline         |
|                                               |
|  Source (Kafka)                               |
|     |                                         |
|  Map/FlatMap (Stateless)                      |
|     |                                         |
|  KeyBy (Partition)                            |
|     |                                         |
|  Window (Tumbling/Sliding/Session)            |
|     |                                         |
|  Aggregate (Stateful)                         |
|     |                                         |
|  Sink (Kafka/S3/Database)                    |
+---------------------------------------------+
```

**Key concepts:**
- **Checkpointing:** Periodic state snapshots for fault tolerance
- **Savepoints:** Manual snapshots for upgrade/migration
- **Watermarks:** Track event time progress; handle late data
- **State:** Keyed state (ValueState, ListState, MapState)
- **Timers:** Event-time and processing-time callbacks
- **Operator chaining:** Optimizing pipeline by co-locating operators

**Windowing strategies:**
- **Tumbling:** Fixed-size, non-overlapping windows
- **Sliding:** Fixed-size, overlapping windows
- **Session:** Windows defined by gap of inactivity
- **Global:** Single window; custom trigger

### Spark Structured Streaming

```
+---------------------------------------------+
|         Spark Structured Streaming            |
|                                               |
|  Input Table (Kafka/Socket/File)              |
|     |                                         |
|  Batch-like queries (Select, Filter, Agg)    |
|     |                                         |
|  Output Table (Memory/Parquet/Kafka)          |
|     |                                         |
|  Trigger: ProcessingTime / EventTime / Once   |
+---------------------------------------------+
```

**Key concepts:**
- **Micro-batch:** Default processing mode; continuous processing (experimental)
- **Output modes:** Append, Update, Complete
- **Watermark:** `withWatermark("eventTime", "10 minutes")`
- **State store:** RocksDB (default), HDFS-based state
- **Stream-Stream joins:** Join two streaming sources with state retention
- **Stream-Static joins:** Stream + lookup table (broadcast)

---

## 7. Data Modeling Patterns

### Star Schema

```
+---------------------------+       +---------------------------+
|    dim_customer           |       |    dim_date               |
|---------------------------|       |---------------------------|
| customer_id (PK)          |       | date_id (PK)              |
| customer_name             |<-+    | full_date                 |
| email                     |  |    | year                      |
| segment                   |  |    | quarter                   |
| first_order_date          |  +--->| month                     |
+---------------------------+  |    | day                       |
                               |    | day_of_week               |
+---------------------------+  |    | is_holiday                |
|    fact_sales              |  |    +---------------------------+
|---------------------------|  |
| order_id (PK)             |  |    +---------------------------+
| customer_id (FK) ---------+--+    |    dim_product             |
| product_id (FK) ----------+----->|---------------------------|
| date_id (FK) -------------+--->| product_id (PK)            |
| store_id (FK) ------------+-+ | product_name                |
| quantity                   | | | category                    |
| unit_price                | | | brand                       |
| discount                  | | | list_price                  |
| revenue                   | | +---------------------------+
+---------------------------+ |
                               |
+---------------------------+  |
|    dim_store               |  |
|---------------------------|  |
| store_id (PK) ------------+--+
| store_name                 |
| address                    |
| region                     |
| district                   |
+---------------------------+
```

### Snowflake Schema

```
Same as star but dimensions are normalized:
dim_product -> dim_category (category_id FK)
dim_store -> dim_region -> dim_country
```

### Third Normal Form (3NF)

```
+------------------+    +------------------+
| orders           |    | customers        |
|------------------|    |------------------|
| order_id (PK)    |--->| customer_id (PK) |
| customer_id (FK) |    | name             |
| order_date       |    | email            |
| total_amount     |    +------------------+
+--------+---------+
         |
         |          +------------------+
         +--------->| order_items      |
         |          |------------------|
         |          | order_id (FK)    |
         |          | product_id (FK)  |
         |          | quantity         |
         |          | unit_price       |
         |          +------------------+
         |
         v
    +----------+
    | products |
    |----------|
    |pid (PK)  |
    |name      |
    |category  |
    |price     |
    +----------+
```

### Data Vault 2.0

```
+---------------------------+       +---------------------------+
|    hub_customer           |       |    hub_product            |
|---------------------------|       |---------------------------|
| customer_hk (hash key PK)|       | product_hk (hash key PK)  |
| customer_id (business key)|       | product_id (business key) |
| load_date                 |       | load_date                 |
| record_source             |       | record_source             |
+---------------------------+       +---------------------------+
         |                                      |
         +---+------------------------------+---+
             |                              |
+------------+----------+     +-------------+----------+
|  link_customer_order   |     |  link_order_product     |
|-------------------------|     |-------------------------|
| customer_order_hk (HK) |     | order_product_hk (HK)   |
| customer_hk (FK)       |     | order_hk (FK)           |
| order_hk (FK)          |     | product_hk (FK)         |
| load_date              |     | load_date               |
| record_source          |     | record_source           |
+--------------------------+     +--------------------------+
         |
         v
+------------------------------------------+
| sat_order_detail (satellite)             |
|------------------------------------------|
| order_hk (FK PK)                         |
| load_date (PK)                           |
| order_date                               |
| status                                   |
| total_amount                             |
| hash_diff (for SCD detection)            |
+------------------------------------------+
```

### One Big Table (OBT)

```
+--------------------------------------------------+
|    sales_obt (denormalized)                        |
|--------------------------------------------------|
| order_id | customer_name | product_name | category |
| quantity | revenue | order_date | year | quarter |
| month | store_name | region                      |
+--------------------------------------------------+

- All dimensions flattened into one wide table
- No joins needed for analysis
- Trade-off: storage redundancy, update anomalies
- Best for: fixed analytics, high-performance dashboards
- Worst for: flexible ad-hoc queries, high-latency sources
```

---

## 8. Schema Evolution

### Backward Compatible (Adding columns)
```sql
-- Old readers can read new data (missing columns get NULL/default)
ALTER TABLE users ADD COLUMN phone_number STRING;
```

### Forward Compatible (Dropping columns)
```sql
-- New readers can read old data (dropped columns are ignored)
-- Typically requires format support (Avro, Parquet, ORC)
-- For Delta/Iceberg, this is explicit
ALTER TABLE users DROP COLUMN legacy_field;
```

### Safe evolution strategies:
1. Add nullable columns only (no NOT NULL initial)
2. Set defaults for new columns
3. Use mapping layer (dbt, views) for bridging
4. Test backward compatibility before deploy
5. Monitor reader/writer schema compatibility

---

## 9. Partitioning & Clustering

### Partitioning Strategies

| Warehouse | Partition Type | Granularity | Syntax |
|-----------|---------------|-------------|--------|
| Snowflake | No native partition (micro-partition auto-managed) | N/A | `CLUSTER BY (col)` |
| Redshift | Distribution + sort key | User-defined | `DISTKEY(col) SORTKEY(col)` |
| BigQuery | Column / ingestion-time | Daily / hourly / monthly | `PARTITION BY DATE(ts)` |
| Hive/Partition | Hive-style folder | Custom | `PARTITIONED BY (dt STRING)` |
| Delta | Generated columns | Custom | `PARTITIONED BY (month(col))` |
| Iceberg | Hidden partitions | Derived expression | `PARTITION BY month(ts)` |

### Clustering Keys

- Snowflake: Up to 4 columns, automatic clustering
- BigQuery: Up to 4 columns, sorted after partition
- Redshift: SORTKEY (compound or interleaved)
- Delta: ZORDER (multi-dimensional)
- Iceberg: Sort order per partition

### File Formats

| Format | Compression | Splittable | Schema | Best For |
|--------|------------|------------|--------|----------|
| Parquet | Snappy/ZSTD/Gzip | Yes | Native | Analytics, columnar queries |
| ORC | Zlib/Snappy/ZSTD | Yes | Native | Hive/Spark, columnar |
| Avro | Snappy/Deflate | Yes | Native | Streaming, Kafka, row-level |
| JSON | - | No | Embedded | Raw ingestion, flexibility |
| CSV | Gzip | Yes (sorted) | None | Simple exchange |

---

## 10. Key Design Questions

### How to design a real-time dashboard pipeline?
1. Source: Kafka with clickstream events
2. Stream: Flink/Spark Streaming with tumbling windows
3. Aggregation: per-minute counts, top-N items
4. Serving: Redis/ElastiCache for sub-second reads
5. Dashboard: WebSocket push from API server
6. Fallback: batch job recomputes hourly for drift correction

### How to design a CDC from Oracle to Snowflake?
1. Capture: Oracle LogMiner or GoldenGate
2. Stream: Kafka Connect (Debezium for non-Oracle, GoldenGate for Oracle)
3. Transform: Flink for dedup and type mapping
4. Stage: Snowflake staging tables with MERGE
5. Apply: Streams + Tasks for continuous loading
6. Monitor: LSN/SCN tracking, lag alerts

### How to design cross-region replication?
1. Active-active: Multi-region writes to Kafka (Confluent Cluster Linking)
2. Async: Kafka MirrorMaker 2 (topic-level replication)
3. Consistency: Last-writer-wins or CRDT (conflict resolution)
4. Data lake: S3 cross-region replication + Iceberg catalog sync
5. Warehouse: BigQuery cross-region replication, Snowflake replication

### How to design a data quality monitoring system?
1. Metrics: Row count, null ratio, uniqueness, freshness, distribution statistics
2. Storage: Time-series database (InfluxDB, Prometheus) for metrics
3. Alerts: Threshold-based (anomaly detection for advanced)
4. Actions: PagerDuty, Jira ticket, Slack notification, pipeline halt
5. Framework: Great Expectations, dbt tests, Deequ (Spark)
6. Observability: Monte Carlo, Sifflet, manual data profiling
