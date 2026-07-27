# Company Interview Guide - Data Engineering

> Detailed breakdown of data engineering interview processes across top companies, focusing on SQL design, pipeline design, data modeling, and distributed systems concepts.

---

## 1. SQL Design Round

### What Companies Ask

| Company | Round Name | Duration | Difficulty | Focus Areas |
|---------|-----------|----------|------------|-------------|
| Snowflake | SQL Deep Dive | 45-60 min | Hard | Window functions, snowflake-specific SQL, semi-structured data |
| Amazon | SQL + Data Modeling | 45 min | Hard | Complex joins, ETL patterns, Redshift-specific optimizations |
| Google | SQL + Query Design | 45 min | Medium-Hard | BigQuery-specific SQL, partitioning, clustering |
| Meta | SQL Technical Screen | 45 min | Hard | Graph queries, array/JSON, Presto/Hive SQL |
| Microsoft | SQL + Performance Tuning | 45 min | Medium-Hard | T-SQL, indexing, statistics, partition switching |
| Databricks | SQL + Spark SQL | 45 min | Hard | Spark SQL, Delta SQL, MERGE, window functions |
| Confluent | SQL + Stream Query | 30-45 min | Medium | ksqlDB, pull vs push queries, streams vs tables |

### Common SQL Design Problems

#### Problem 1: User Retention Cohort Analysis
**Prompt:** Given tables `users(user_id, signup_date)` and `sessions(session_id, user_id, session_date)`, calculate weekly retention cohorts showing what percentage of users who signed up in week W return in subsequent weeks.

```sql
WITH weekly_cohorts AS (
  SELECT
    DATE_TRUNC('week', u.signup_date) AS cohort_week,
    DATE_TRUNC('week', s.session_date) AS activity_week,
    COUNT(DISTINCT u.user_id) AS retained_users
  FROM users u
  LEFT JOIN sessions s ON u.user_id = s.user_id
  GROUP BY 1, 2
),
cohort_sizes AS (
  SELECT cohort_week, SUM(retained_users) AS total_users
  FROM weekly_cohorts
  GROUP BY 1
)
SELECT
  c.cohort_week,
  EXTRACT(WEEK FROM c.activity_week) - EXTRACT(WEEK FROM c.cohort_week) AS week_offset,
  100.0 * c.retained_users / cs.total_users AS retention_rate
FROM weekly_cohorts c
JOIN cohort_sizes cs ON c.cohort_week = cs.cohort_week
ORDER BY 1, 2;
```

**Interviewer probes:**
- What if we need daily cohorts instead of weekly?
- How do you handle future weeks with incomplete data?
- What indexes would you create for performance?

#### Problem 2: Deduplication with Window Functions
**Prompt:** Table `events(event_id, user_id, event_type, event_timestamp, payload)` has duplicate events (same event_id appearing multiple times). Keep the latest occurrence per event_id.

```sql
SELECT event_id, user_id, event_type, event_timestamp, payload
FROM (
  SELECT *,
    ROW_NUMBER() OVER (PARTITION BY event_id ORDER BY event_timestamp DESC) AS rn
  FROM events
) ranked
WHERE rn = 1;
```

**Follow-ups:**
- What if duplicates have different payloads? How to merge?
- What if `event_id` is NULL? How to handle?
- Write a MERGE statement to deduplicate into a target table

#### Problem 3: Sessionization from Clickstream
**Prompt:** Given `clicks(user_id, click_time, page_url)`, create user sessions where a session ends after 30 minutes of inactivity. Assign a session_id and compute session metrics.

```sql
WITH user_lag AS (
  SELECT *,
    LAG(click_time) OVER (PARTITION BY user_id ORDER BY click_time) AS prev_click_time,
    CASE
      WHEN DATEDIFF('minute', LAG(click_time) OVER (PARTITION BY user_id ORDER BY click_time), click_time) > 30
        THEN 1 ELSE 0
    END AS is_new_session
  FROM clicks
),
sessions AS (
  SELECT *,
    SUM(is_new_session) OVER (PARTITION BY user_id ORDER BY click_time ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS session_id
  FROM user_lag
)
SELECT
  user_id, session_id,
  MIN(click_time) AS session_start,
  MAX(click_time) AS session_end,
  COUNT(*) AS page_views,
  DATEDIFF('second', MIN(click_time), MAX(click_time)) AS session_duration_seconds
FROM sessions
GROUP BY 1, 2;
```

#### Problem 4: Funnel Analysis
**Prompt:** Given `user_events(user_id, event_name, event_timestamp)`, compute a conversion funnel: view_product -> add_to_cart -> checkout -> purchase. Show unique users at each stage.

```sql
WITH funnel AS (
  SELECT
    user_id,
    MAX(CASE WHEN event_name = 'view_product' THEN 1 ELSE 0 END) AS funnel_1,
    MAX(CASE WHEN event_name = 'add_to_cart' THEN 1 ELSE 0 END) AS funnel_2,
    MAX(CASE WHEN event_name = 'checkout' THEN 1 ELSE 0 END) AS funnel_3,
    MAX(CASE WHEN event_name = 'purchase' THEN 1 ELSE 0 END) AS funnel_4
  FROM user_events
  GROUP BY user_id
)
SELECT
  'view_product' AS step, SUM(funnel_1) AS users, 100.0 AS pct_of_total
  FROM funnel
UNION ALL
SELECT
  'add_to_cart', SUM(funnel_2),
  100.0 * SUM(funnel_2) / SUM(funnel_1) FROM funnel
UNION ALL
SELECT
  'checkout', SUM(funnel_3),
  100.0 * SUM(funnel_3) / SUM(funnel_1) FROM funnel
UNION ALL
SELECT
  'purchase', SUM(funnel_4),
  100.0 * SUM(funnel_4) / SUM(funnel_1) FROM funnel;
```

### Performance Tuning Questions

#### Redshift-specific
- Explain distribution styles (KEY, ALL, EVEN) and when to use each
- How does sort key ordering affect query performance?
- What is the difference between COMPOUND and INTERLEAVED sort keys?
- How does WLM queue assignment work?

#### BigQuery-specific
- Explain slot reservations vs on-demand pricing
- How does BI Engine accelerate queries?
- When should you use clustering vs partitioning?
- How does BigQuery's columnar storage work with pruning?

#### Snowflake-specific
- Explain micro-partitioning and pruning
- How do clustering keys differ from traditional partitioning?
- What caching layers exist and how to warm them?
- How do materialized views differ from regular views?

### SQL Anti-Patterns to Avoid
- `SELECT *` in production queries
- Implicit cross joins
- Non-sargable WHERE clauses (e.g., `WHERE DATE(column) = ...`)
- Overusing scalar subqueries
- Missing DISTINCT when deduplication is needed
- Ignoring NULL handling in aggregations

---

## 2. Pipeline Design Round

### Common Pipeline Design Questions

#### Lambda Architecture
**Prompt:** Design a pipeline that provides both real-time and batch views.

**Solution components:**
- **Batch layer:** Spark batch job on S3 data, recomputes full view periodically
- **Speed layer:** Kafka + Flink/Spark Streaming for low-latency deltas
- **Serving layer:** Presto/Trino on Iceberg for unification
- **Merge logic:** batch view + streaming deltas, join on same key
- **Reconciliation:** nightly batch corrects stream approximations

#### Kappa Architecture
**Prompt:** Design a pipeline that uses only streaming, no batch layer.

**Solution components:**
- **Single source:** Kafka as the immutable log
- **Processing:** Kafka Streams / Flink re-processing from beginning
- **State:** State stores for aggregation; RocksDB-backed for large state
- **Output:** Materialized views updated continuously
- **Re-processing:** Replay Kafka from earliest offset when logic changes
- **Checkpointing:** Periodic state snapshots for recovery

#### CDC Pipeline Design
**Prompt:** Design a pipeline to capture changes from a PostgreSQL database and sync to a Snowflake data warehouse in near real-time.

**Architecture:**
```
PostgreSQL -> Debezium -> Kafka -> Kafka Connect -> Snowflake
                |                          |
           WAL Reader              Snowpipe / Connector
```

1. **Capture:** Debezium PostgreSQL connector reads WAL via logical replication slots
2. **Serialize:** AVRO in Kafka with Schema Registry
3. **Transform:** SMT (Single Message Transform) for type mapping
4. **Load:** Snowflake Kafka Connector ingests into staging tables
5. **Merge:** Snowflake streams + tasks apply CDC to main tables
6. **Monitor:** Consumer lag, WAL lag, sync latency

**Handling failures:**
- Schema drift: new columns auto-added via schema evolution
- WAL retention: PostgreSQL WAL must be kept for lagging consumers
- Out-of-order events: use event-time ordering + dedup
- Exactly-once: Kafka transactions + idempotent sink

#### Incremental Load Pipeline
**Prompt:** Design a pipeline for nightly incremental loads from source to warehouse.

**Extract patterns:**
- **Timestamp-based:** Filter `WHERE updated_at > last_run` - simple, but needs audit
- **Watermark table:** Track high-water marks per source table
- **CDC capture:** Log-based for minimal impact on source
- **Full comparison:** Compare hash of entire table - expensive but exhaustive

**Load patterns:**
- **Incremental append:** `INSERT INTO target SELECT * FROM source WHERE updated_at > last_run`
- **Merge/UPSERT:** `MERGE INTO target USING source ON key WHEN MATCHED THEN UPDATE WHEN NOT MATCHED THEN INSERT`
- **Delete+Insert:** For tables that need full refresh (small dimension tables)

#### Backfill Design
**Prompt:** Your pipeline has been running for 6 months but you discovered a logic bug that affects the first 3 months of data. Design a backfill strategy.

**Strategy:**
1. **Parallel re-processing:** Run backfill job on historical date ranges in parallel
2. **Timestamp partitioning:** Backfill by day/week/month partitions
3. **Staging environment:** Write to staging tables, validate before swapping
4. **Validation:** Row-count comparison, data quality checks, sample comparison
5. **Swap tables:** Rename/swap staging with production (or drop+reload partitions)
6. **Restart streams:** After backfill, continue from latest offset

**Key considerations:**
- Idempotency: re-running should produce same result
- Isolation: backfill shouldn't impact current production pipeline
- SLA: backfill window must not conflict with business hours
- Cost: backfill across petabytes can be expensive

### Pipeline Observability

**What interviewers look for:**
- Data quality monitoring: row count, null ratio, freshness
- Pipeline metrics: throughput, latency, error rate
- Alerting: PagerDuty/Slack on failures, SLA misses
- Debugging: access to logs, task retry history, lineage
- Lineage: end-to-end tracking from source to dashboard

---

## 3. Data Modeling Round

### Core Concepts

#### Star Schema Design
**Prompt:** Design an e-commerce star schema.

**Facts:**
- `fact_orders`: order_id, customer_id, product_id, date_id, store_id, quantity, unit_price, discount, revenue
- `fact_inventory`: product_id, date_id, warehouse_id, quantity_on_hand
- `fact_shipments`: shipment_id, order_id, carrier_id, date_id, delivery_date_id, cost

**Dimensions:**
- `dim_customer`: customer_id, name, email, segment, created_date, first_order_date
- `dim_product`: product_id, sku, name, category, subcategory, brand, list_price
- `dim_date`: date_id, date, year, quarter, month, week, day_of_week, is_holiday
- `dim_store`: store_id, store_name, address, region, district
- `dim_carrier`: carrier_id, carrier_name, service_type

**Why star schema?**
- Denormalized (fewer joins)
- Optimized for BI tools (Power BI, Looker, Tableau)
- Fact tables are tall and thin
- Dimension tables are short and wide

#### Slowly Changing Dimensions (SCD)

**Type 0:** Retain original (never change attributes)
- Use case: date of birth, original SKU

**Type 1:** Overwrite (no history)
- Use case: customer status (active/inactive), email
- SQL: `UPDATE dim_customer SET email = new_email WHERE customer_id = x`

**Type 2:** Add new row (full history)
- Use case: customer address, product price
- SQL: `UPDATE current SET end_date = today; INSERT new row with start_date = today`

**Type 3:** Add previous value column
- Use case: limited history (e.g., previous territory)
- SQL: `UPDATE dim SET prev_territory = territory, territory = new_territory`

**Type 6:** Hybrid (Type 2 + Type 3)
- Use case: need both full history and current value in one row

#### Data Vault Modeling
- **Hubs:** Business keys (e.g., `hub_customer`, `hub_product`)
- **Links:** Relationships (e.g., `link_customer_order`)
- **Satellites:** Descriptors (e.g., `sat_customer_details`)
- **Point-in-time tables:** Pre-built joins for performance

#### One Big Table (OBT)
- Denormalized single table with all fields
- Pros: zero joins, simple queries, fast read
- Cons: storage bloat, schema rigidity, update anomalies
- Best for: analytics that always need the same columns

#### Medallion Architecture (Databricks)
| Layer | Format | Purpose | Update Frequency |
|-------|--------|---------|-----------------|
| Bronze | Raw (JSON, AVRO, Parquet) | Append-only, raw data | Streaming / micro-batch |
| Silver | Cleaned Parquet/Delta | Deduplicated, validated | Per run |
| Gold | Aggregated Delta | Business-ready aggregates | Per run / on-demand |

### Modeling Interview Questions

**Q:** What normalization form should a data warehouse use?
**A:** 3NF for OLTP, but dimensional (star/snowflake) for OLAP. 3NF avoids redundancy but requires many joins. Star schema balances query performance with flexibility.

**Q:** When would you use a snowflake schema over a star schema?
**A:** When dimensions have natural hierarchies and you need to:
- Enforce referential integrity on dimension attributes
- Reduce storage redundancy
- Support OLAP drill-across queries
Examples: geography hierarchy (region -> country -> state -> city)

**Q:** How do you handle many-to-many relationships in a star schema?
**A:** Bridge tables. E.g., a product can belong to multiple categories. Create `dim_product_category_bridge(product_id, category_id)`.

**Q:** Design a data model for an A/B testing platform.
**A:**
- `fact_experiment_results`: user_id, experiment_id, variant_id, date_id, metric_value
- `dim_experiment`: experiment_id, name, start_date, end_date, hypothesis
- `dim_variant`: variant_id, experiment_id, variant_name, is_control

---

## 4. Distributed Systems Concepts

### Spark Architecture

**Execution model:**
1. Driver program creates SparkContext
2. Cluster Manager allocates executors
3. Tasks run in parallel on partitions
4. Shuffle sorts/aggregates across partitions

**Concepts to know:**
- **Lineage:** DAG of transformations that can rebuild RDDs
- **Lazy evaluation:** Transformations build plan; actions execute
- **Narrow dependency:** Each partition of parent used by at most one child partition (no shuffle)
- **Wide dependency:** Multiple child partitions depend on one parent partition (shuffle)
- **Shuffle:** Data redistribution across partitions; expensive operation
- **Catalyst Optimizer:** Rule-based and cost-based optimization
- **Tungsten:** Off-heap memory management, code generation
- **AQE (Adaptive Query Execution):** Dynamically coalesce partitions, switch join strategies

### Kafka Architecture

**Core components:**
- **Broker:** Server storing topic partitions
- **Producer:** Writes to partitions; can choose key-based partitioning
- **Consumer:** Reads from topics within a consumer group
- **ZooKeeper/KRaft:** Cluster coordination, leader election

**Replication:**
- **Leader:** Handles all reads/writes for a partition
- **Follower (ISR):** In-sync replicas; replicate from leader
- **acks=all:** Leader waits for all ISRs to acknowledge
- **min.insync.replicas:** Minimum ISRs for availability
- **Unclean leader election:** Allowing out-of-sync replica to become leader (may lose data)

**Concepts to know:**
- **Exactly-once semantics:** Idempotent producer + transactions + consumer isolation
- **Consumer rebalancing:** Partition redistribution among consumers
- **Sticky partitioner:** Batching by partition for efficiency
- **Tiered storage:** Moving old segment data to cheaper storage (S3)
- **Raft-based controller:** KRaft mode removes ZooKeeper dependency

### BigQuery Architecture

**Storage:**
- **Colossus:** Distributed file system
- **Capacitor:** Columnar storage format
- **Partitioning:** Ingestion-time or column (integer, date, timestamp)
- **Clustering:** Sort within partitions; improves pruning

**Compute:**
- **Dremel:** Query execution engine; tree architecture
- **Slots:** Compute units; reserved or on-demand
- **BI Engine:** In-memory acceleration for dashboards

**Network:**
- **Jupiter:** Google's data center network
- **Agni:** WAN interconnect between regions

### Snowflake Architecture

**Three layers:**
1. **Database Storage:** Compressed, columnar, micro-partitioned on S3/Blob/GCS
2. **Compute:** Virtual warehouses (elastic clusters), multi-cluster scaling
3. **Services:** Authentication, query optimization, metadata, transaction manager

**Unique features:**
- **Micro-partitions:** Auto-sized (50-500 MB), all columns stored together
- **Clustering keys:** Co-locate related rows within micro-partitions
- **Time travel:** AT/BEFORE queries on historical data (1-90 day retention)
- **Zero-copy cloning:** Instant table/database clones without data copy
- **Data sharing:** Reader accounts, listings, marketplace

### Distributed Systems Interview Questions

**Q:** How does Spark's shuffle work and how do you optimize it?
**A:** Shuffle is triggered by operations like `groupBy`, `join`, `distinct`. It involves:
1. Map phase: Write shuffle data to disk (sorted by partition)
2. Reduce phase: Fetch shuffle data from map partitions
3. Optimization:
   - `spark.sql.shuffle.partitions` tuning (start at N-1 cores)
   - AQE coalescing partitions after shuffle
   - Bucketing for pre-shuffled data
   - Salting for skew

**Q:** How does Kafka achieve high throughput?
**A:** 
- Sequential disk I/O (append-only log)
- Zero-copy with `sendfile` (transfer from page cache to socket)
- Batching: producer batches records, consumer fetches batches
- Partition parallelism: each partition maps to one consumer thread
- Efficiency: O(1) reads/writes per partition

**Q:** Explain the CAP theorem in the context of data systems.
**A:** 
- **Consistency:** Every read receives the most recent write
- **Availability:** Every request receives a non-error response
- **Partition Tolerance:** System continues despite network partition
- Data systems trade-offs:
  - Kafka: C+A (sacrifices P at partition boundary with ISR)
  - BigQuery: C+P (strong consistency)
  - Snowflake: C+A (consistency via transaction manager)
  - HDFS: C+P (NameNode is single point of failure)

**Q:** How would you design for exactly-once processing in a streaming pipeline?
**A:**
1. **Source:** Idempotent source reads (Kafka offsets committed only after processing)
2. **Processing:** Idempotent transformations (same input always produces same output)
3. **State:** Checkpointed state stores (RocksDB in Flink, state stores in Kafka Streams)
4. **Sink:** Idempotent writes (Kafka idempotent producer, transactional writes)
5. **Transaction coordination:** Two-phase commit or Kafka transactions
6. **Failure recovery:** Replay from last committed offset

**Q:** Compare Lambda vs Kappa architecture.
**A:**
| Aspect | Lambda | Kappa |
|--------|--------|-------|
| Codebase | Two code paths (batch + stream) | Single code path |
| Complexity | Higher (merge logic) | Lower |
| Data freshness | Batch could lag hours | Sub-second |
| Re-processing | Rewind batch layer + replay stream | Replay from beginning |
| State size | Can rebuild anytime | Must keep state in stream |
| Which to choose | Need both accuracy and speed | Can tolerate small inaccuracies |
