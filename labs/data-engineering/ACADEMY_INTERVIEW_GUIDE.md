# Data Engineering Academy - Company Interview Preparation Guide

> Comprehensive per-company interview prep for Data Engineering roles at top-tier companies.

---

## Table of Contents
1. [Snowflake](#1-snowflake)
2. [Databricks](#2-databricks)
3. [Confluent / Kafka](#3-confluent--kafka)
4. [Google Cloud (Google Data)](#4-google-cloud-google-data)
5. [Amazon (Data Engineering)](#5-amazon-data-engineering)
6. [Meta (Data Infrastructure)](#6-meta-data-infrastructure)
7. [Microsoft Fabric](#7-microsoft-fabric)
8. [Fivetran](#8-fivetran)
9. [dbt Labs](#9-dbt-labs)
10. [Airflow / Astronomer](#10-airflow--astronomer)

---

## 1. Snowflake

### DE Roles at Snowflake
| Role | Focus | Typical Level |
|------|-------|--------------|
| Data Engineer (Customer-facing) | Build demos, POCs, migration patterns | IC3-IC5 |
| Solutions Architect / SA | Enterprise architecture, adoption | IC4-IC6 |
| Software Engineer (DE Platform) | Warehouse internals, query optimization | IC4-IC6 |
| Analytics Engineer | SQL modeling, transformations | IC3-IC5 |
| Partner Engineer | SDK/connector development | IC4-IC6 |

### Interview Rounds

#### Round 1: Recruiter Screen (30 min)
- Background: years of SQL, data warehousing experience
- Why Snowflake? Interest in cloud data platforms
- Current tech stack: what warehouse are you using now?
- Availability and level alignment

#### Round 2: Technical Screen (45-60 min)
- **SQL deep dive** (60% of the interview):
  - Window functions: `ROW_NUMBER()`, `RANK()`, `DENSE_RANK()`, `LAG()/LEAD()`
  - Hierarchical queries: `CONNECT BY` / recursive CTEs
  - JSON/半構造 data: `PARSE_JSON()`, `FLATTEN()`, `VARIANT` type
  - Time-series: `DATE_TRUNC()`, `DATEDIFF()`, conditional aggregation
  - MERGE/UPSERT patterns for incremental loads
- **Warehouse concepts** (40% of interview):
  - Virtual warehouse sizing: XS to 6XL
  - Auto-suspend, auto-resume, multi-cluster warehouses
  - Caching: results cache, metadata cache, data cache
  - Clustering keys and automatic clustering

**Sample SQL problem:** Find the top 5 products by revenue per month, showing MoM change:
```sql
WITH monthly_revenue AS (
  SELECT
    DATE_TRUNC('month', order_date) AS month,
    product_id,
    SUM(revenue) AS total_revenue
  FROM orders
  GROUP BY 1, 2
),
ranked AS (
  SELECT *,
    ROW_NUMBER() OVER (PARTITION BY month ORDER BY total_revenue DESC) AS rank,
    LAG(total_revenue) OVER (PARTITION BY product_id ORDER BY month) AS prev_month_revenue
  FROM monthly_revenue
)
SELECT *,
  ROUND((total_revenue - prev_month_revenue) / NULLIF(prev_month_revenue, 0) * 100, 2) AS mom_change_pct
FROM ranked
WHERE rank <= 5;
```

#### Round 3: Systems Design (60 min)
- **Design a data sharing platform** (Snowflake Data Sharing / Marketplace)
- **Design a multi-cloud data warehouse** with replication
- **Design a zero-copy cloning system** across environments
- **Migration patterns**: Teradata/Oracle/Redshift -> Snowflake
- **Key concepts**: separation of storage/compute, cloud agnosticism, data sharing without copying

**Common design question:** "Design a real-time data ingestion pipeline into Snowflake."
- Use Snowpipe for continuous ingestion
- S3/ADLS/GCS event notifications
- Stages, file formats, pipe objects
- Error handling with COPY history
- Transformation with dynamic tables or streams/tasks
- Cost optimization: warehouse sizing, auto-suspend, materialized views

#### Round 4: Hiring Manager (Behavioral, 45 min)
- **STAR framework** scenarios:
  - Tell me about a time you optimized a slow pipeline
  - Describe a data quality incident you resolved
  - How did you migrate a data warehouse? What went wrong?
- Team collaboration, stakeholder management
- Data modeling decisions and trade-offs

#### Round 5: Bar Raiser / Cross-functional (45 min)
- Leadership principles
- Conflict resolution with product teams
- Technical disagreement story
- Ambiguity and decision-making

### Key Topics to Master
- Time travel (AT/BEFORE, how it works, retention period)
- Zero-copy cloning (CREATE CLONE, cost implications)
- Tasks and streams for CDC
- Dynamic tables (declarative transformation)
- External tables and Iceberg tables
- Account/warehouse/role hierarchy
- Performance tuning: query profiling, access history
- Cost management: credits, warehouse usage, storage

### Resources
- Snowflake docs: Best Practices, Performance Optimization
- Snowflake blog: engineering deep dives
- Snowflake University: free training and certification

---

## 2. Databricks

### DE Roles at Databricks
| Role | Focus | Typical Level |
|------|-------|--------------|
| Data Engineer (Customer-facing) | Build production pipelines, demos | IC3-IC5 |
| Solutions Architect | Medallion architecture, Unity Catalog | IC4-IC6 |
| Software Engineer (Spark/Databricks) | Lakehouse engine internals | IC4-IC6 |
| Field Engineer | Customer onboarding, migration | IC3-IC5 |
| ML Engineer / ML DE | Feature engineering, ML pipelines | IC4-IC6 |

### Interview Rounds

#### Round 1: Recruiter Screen (30 min)
- Spark experience level
- Cloud platform experience (AWS, Azure, GCP)
- Why Databricks?
- Leveling based on years of PySpark/SQL experience

#### Round 2: Technical Screen (60 min)
- **PySpark / Scala Spark** (50%):
  - Transformations vs actions
  - Catalyst optimizer understanding
  - Shuffle: wide vs narrow dependencies
  - `groupBy` vs `reduceByKey` vs `agg`
  - `mapPartitions` vs `map`
  - Broadcast joins, bucketing
  - AQE (Adaptive Query Execution) internals
  - Delta Lake: ACID, time travel, OPTIMIZE, ZORDER
- **SQL / Data Modeling** (50%):
  - Medallion architecture: Bronze -> Silver -> Gold
  - Lakehouse design patterns
  - Schema evolution with Delta Lake
  - CDC patterns: MERGE, apply-changes API (DLT)

**Sample PySpark problem:**
```python
# Given stream of click events, compute 1-hour session windows
from pyspark.sql.functions import *
from pyspark.sql.window import Window

def compute_sessions(click_events_df):
    windowed = click_events_df \
        .groupBy(
            session_window("timestamp", "1 hour"),
            "user_id"
        ) \
        .agg(
            count("click_id").alias("click_count"),
            collect_list("page_url").alias("pages_visited")
        )
    return windowed
```

#### Round 3: Systems Design (60 min)
- **Design a real-time feature engineering platform**
- **Design a multi-hop medallion pipeline**
- **Design a data catalog with Unity Catalog principles**
- **Design serverless SQL analytics on a data lake**
- **Delta Sharing design and architecture**

**Common design question:** "Design a CDC pipeline from a relational database into the lakehouse."
1. Capture: Debezium / Kafka Connect for CDC
2. Landing: Raw JSON in Bronze layer (append-only)
3. Processing: Auto-loader / Structured Streaming
4. Merge: MERGE INTO target Delta table in Silver
5. Aggregation: DLT pipeline for Gold layer aggregations
6. Governance: Unity Catalog for lineage and access control

#### Round 4: Behavioral + System Design Deep Dive (45 min)
- Data pipeline failures and recovery
- Spark performance troubleshooting
- Migration from on-prem Hive/Spark to Databricks

#### Round 5: Hiring Committee (Leadership, 45 min)
- Communication: explaining Spark execution plans
- Technical mentoring experience
- Cross-team data initiatives
- Open-source contributions to Spark / Delta Lake

### Key Topics to Master
- Spark execution model: driver, executors, tasks, stages
- Memory management: on-heap vs off-heap, spark.memory
- Shuffle tuning: `spark.shuffle.partitions`, `spark.sql.shuffle.partitions`
- File formats: Parquet, Delta, Iceberg
- DLT: expectations, constraints, pipelines
- Unity Catalog: metastore, catalog, schema, table hierarchy
- Auto-loader: cloud files source, schema inference and evolution
- Photon engine, Delta Engine
- Partner Connect, Databricks SQL

### Resources
- Databricks Academy: free DE certification prep
- Spark: The Definitive Guide (Chambers & Zaharia)
- Databricks blog: engineering blog, performance series

---

## 3. Confluent / Kafka

### DE Roles at Confluent
| Role | Focus | Typical Level |
|------|-------|--------------|
| Data Engineer (Customer-facing) | Kafka migration, streaming pipelines | IC3-IC5 |
| Solutions Engineer | Enterprise streaming architecture | IC4-IC6 |
| Software Engineer (Kafka Core) | Kafka broker internals | IC4-IC7 |
| Field Engineer | Customer onboarding, PoCs | IC3-IC5 |
| Streaming Analytics Engineer | ksqlDB, Flink integration | IC4-IC6 |

### Interview Rounds

#### Round 1: Recruiter Screen (30 min)
- Streaming/messaging experience
- Kafka familiarity: producing, consuming, Kafka Connect
- Why Confluent? Interest in event streaming
- Specific Kafka version and feature knowledge

#### Round 2: Technical Phone Screen (45-60 min)
- **Kafka fundamentals**:
  - Topic, partition, offset, consumer group
  - At-least-once vs exactly-once semantics
  - acks=0, acks=1, acks=all
  - Leader and follower replicas, ISR
  - Retention: time-based, size-based, compacted topics
  - Partition assignment strategies: range, round-robin, sticky
- **Kafka Streams / ksqlDB**:
  - KStream vs KTable vs GlobalKTable
  - State stores: RocksDB, in-memory
  - Exactly-once semantics in Kafka Streams
  - Interactive queries (queryable state)
- **Kafka Connect**:
  - Source vs sink connectors
  - Single Message Transform (SMT)
  - Connector lifecycle: distributed mode, rebalancing

**Sample Kafka Streams problem:**
```java
// Count page views per URL in 1-minute windows
KStream<String, PageView> views = builder.stream("page-views");
KTable<Windowed<String>, Long> counts = views
    .groupBy((key, value) -> value.getUrl())
    .windowedBy(TimeWindows.of(Duration.ofMinutes(1)))
    .count();
counts.toStream().to("page-view-counts");
```

#### Round 3: System Design (60 min)
- **Design a real-time fraud detection system with Kafka**
- **Design a multi-region Kafka deployment with replication**
- **Design a CDC pipeline from database to Kafka**
- **Design an event-sourced microservices platform**
- **Design a log aggregation system with Kafka**

**Common design question:** "Design a global payment processing platform with Kafka."
- Multi-region cluster design (Confluent Cluster Linking)
- Exactly-once semantics for payment processing
- Dead letter queues for failed payments
- Schema Registry for schema evolution
- Partition key: transaction_id for ordering guarantee
- Consumer rebalancing handling

#### Round 4: Distributed Systems Deep Dive (60 min)
- **Kafka replication protocol**:
  - How leader election works (quorum controller)
  - How `min.insync.replicas` affects availability
  - Unclean leader election scenarios
- **Controller protocol**:
  - KRaft vs ZooKeeper mode
  - Controller quorum: voters, observers
- **Performance**:
  - Zero-copy optimization (`sendfile`)
  - Page cache vs disk I/O
  - Batching: `linger.ms`, `batch.size`
  - Compression: gzip, snappy, lz4, zstd

#### Round 5: Behavioral + Bar Raiser (45 min)
- Handling production outages
- Open-source contributions
- Mentoring and knowledge sharing
- Dealing with ambiguity in streaming requirements

### Key Topics to Master
- KRaft mode (KIP-500): no ZooKeeper dependency
- Tiered storage: moving older data to S3
- Confluent Cloud features: cluster linking, schema linking
- ksqlDB: persistent vs transient queries, pull vs push queries
- Flink on Confluent Cloud
- Exactly-once semantics: idempotent producer, transactions
- Consumer rebalancing: cooperative vs eager
- Monitoring: consumer lag, broker metrics, JMX

### Resources
- Confluent Developer: free courses and certification
- Kafka: The Definitive Guide (Narkhede, Shapira, Palino)
- Confluent blog: engineering blog, KIPs, performance benchmarks

---

## 4. Google Cloud (Google Data)

### DE Roles at Google / Google Cloud
| Role | Focus | Typical Level |
|------|-------|--------------|
| Data Engineer (Google Cloud) | Build pipelines on GCP, customer-facing | L3-L5 |
| Data Infrastructure Engineer | Build internal data systems | L4-L6 |
| Software Engineer (BigQuery) | BigQuery storage/query engine internals | L4-L6 |
| Data Solutions Architect | Enterprise architecture on GCP | L5-L7 |
| Analytics Engineer | Looker, Data Studio, modeling | L3-L5 |

### Interview Rounds

#### Round 1: Recruiter Screen (30 min)
- GCP vs AWS/Azure experience
- BigQuery, Dataflow, Pub/Sub experience
- Why Google?
- SWE generalist vs DE specialization

#### Round 2: Technical Phone Screen (45 min)
- **BigQuery** (40%):
  - Partitioning: ingestion-time vs column-based
  - Clustering: sort order and cardinality
  - Slots: reservation vs on-demand pricing
  - Query optimization: pruning, JOIN strategies
  - Materialized views vs logical views
- **Dataflow / Apache Beam** (40%):
  - Windowing: fixed, sliding, sessions
  - Triggers: early, late, accumulating vs discarding
  - Watermarks and late data handling
  - PTransform, PCollection, PCollectionView
  - The Beam portability framework
- **Pub/Sub** (20%):
  - Push vs pull subscriptions
  - Exactly-once vs at-least-once delivery
  - Dead letter topics, retry policies

**Sample Beam problem:**
```java
// Compute running average of sensor readings per device
PCollection<SensorReading> readings = pipeline.apply(
    "ReadFromPubSub", PubsubIO.readAvros(...));

PCollection<Double> averages = readings
    .apply("FixedWindows", Window.into(FixedWindows.of(Duration.ofMinutes(5))))
    .apply("PerDevice", PerElement.of())
    .apply("Mean", Mean.globally().withoutDefaults());
```

#### Round 3: Algorithm / Coding (45 min)
- LeetCode-style: medium to hard
- Focus on: arrays, strings, hash maps, recursion
- Must be efficient: O(n) or O(n log n)
- Language: Python, Java, or Go preferred
- Sample: "Find all duplicates in an array", "LRU Cache"

#### Round 4: System Design (60 min)
- **Design Google Analytics data pipeline**
- **Design a real-time anomaly detection system**
- **Design a global data warehouse** (multi-region BigQuery)
- **Design a streaming ETL pipeline with Dataflow**
- **Design a data lake on GCS with Dataproc**

**Common design question:** "Design a YouTube analytics pipeline."
1. Ingestion: Pub/Sub for click events, Cloud Storage for batch
2. Processing: Dataflow with session windows for user sessions
3. Storage: BigQuery partitioned by date, clustered by video_id
4. Serving: Looker dashboards + BigQuery BI Engine
5. Real-time: Dataflow streaming + BigQuery streaming inserts

#### Round 5: Googleyness + Leadership (45 min)
- **Googleyness** (cultural fit):
  - Ambiguity tolerance
  - Humility and collaboration
  - Passion for technology and impact
- **Leadership**:
  - Influence without authority
  - Cross-functional projects (TPM, UX, Eng)
  - Incident leadership

### Key Topics to Master
- BigQuery: slots reservation, BI Engine, Omni, query plans
- Dataflow: pipeline I/O, side inputs, unbounded sources
- Pub/Sub: ordering keys, message retention, flow control
- Data Fusion, Dataproc, Composer (Airflow managed)
- Looker: LookML, PDTs, symmetric aggregates
- Cloud Storage: object lifecycle, storage classes
- Cloud SQL, Spanner, Firestore
- Migration: Teradata/Oracle to BigQuery

### Resources
- Google Cloud Skills Boost: Data Engineer learning path
- Professional Data Engineer certification guide
- Google Cloud Blog: BigQuery, Dataflow engineering deep dives

---

## 5. Amazon (Data Engineering)

### DE Roles at Amazon
| Role | Focus | Typical Level |
|------|-------|--------------|
| Data Engineer | Build data pipelines, ETL/ELT | L4-L6 |
| Data Engineer II | Lead data platform initiatives | L5-L6 |
| Sr. Data Engineer | Architecture, ML data pipelines | L6-L7 |
| Data Engineering Manager | Team leadership, DE strategy | L6-L8 |
| Data Infrastructure Engineer | Build internal data tools | L5-L7 |
| Big Data Engineer | AWS-based data solutions | L4-L6 |

### Interview Rounds

#### Round 1: Recruiter Screen (30 min)
- Years of data engineering experience
- AWS services experience (Redshift, S3, EMR, Glue, Kinesis)
- Why Amazon? Interest in e-commerce/scale
- Leadership Principles awareness (16 LPs)

#### Round 2: Technical Phone Screen (60 min)
- **SQL** (40%):
  - Complex joins: self-joins, anti-joins, semi-joins
  - Window functions for deduplication and ranking
  - Recursive CTEs for hierarchical data
  - Pivot/unpivot queries
  - Query performance: EXPLAIN, distribution keys
- **Python/Scripting** (40%):
  - Data manipulation: pandas, NumPy
  - File processing: CSV, JSON, Parquet
  - Optimization: generators, iterators, batch processing
  - Error handling and logging
- **AWS DE Services** (20%):
  - Glue: ETL jobs, crawlers, Data Catalog
  - EMR: Spark/Hive on EC2, cluster configuration
  - Redshift: distribution keys, sort keys, WLM
  - Kinesis: Data Streams, Data Firehose, Data Analytics
  - Step Functions: orchestration, error handling

**Sample SQL problem:** "Customer retention cohorts":
```sql
WITH first_purchase AS (
  SELECT customer_id, MIN(order_date) AS first_order_date
  FROM orders GROUP BY 1
),
cohorts AS (
  SELECT customer_id,
    DATE_TRUNC('month', first_order_date) AS cohort_month,
    EXTRACT(MONTH FROM AGE(order_date, first_order_date)) AS month_offset
  FROM orders JOIN first_purchase USING (customer_id)
)
SELECT cohort_month, month_offset, COUNT(DISTINCT customer_id) AS customers
FROM cohorts
GROUP BY 1, 2 ORDER BY 1, 2;
```

#### Round 3: System Design (60 min)
- **Design the Amazon.com product catalog data pipeline**
- **Design a real-time inventory management system**
- **Design a customer 360 data platform (DMP)**
- **Design a multi-source CDC pipeline**
- **Design a data quality monitoring framework**

**Common design question:** "Design a customer purchase recommendations pipeline."
1. Sources: clickstream (Kinesis), orders (DynamoDB Streams), reviews
2. Processing: Spark on EMR for ML feature computation
3. Storage: S3 data lake (Parquet), Redshift for serving
4. ML output: personalization service via DynamoDB/ElastiCache
5. Orchestration: Step Functions with Lambda and Glue
6. Monitoring: CloudWatch, SNS alerts for failures

#### Round 4: Bar Raiser (Behavioral, 60 min)
- **16 Leadership Principles** probed deeply:
  - Customer Obsession: "Tell me about a time you went above and beyond"
  - Ownership: "Describe a situation where you took ownership of a failing project"
  - Deliver Results: "How did you deliver a complex data project on time"
  - Insist on Highest Standards: "Data quality incident resolution"
  - Dive Deep: "Debugging a slow Spark job"
  - Have Backbone; Disagree and Commit: "Technical disagreement"
  - Learn and Be Curious: "Learning a new data technology"
- Must prepare 2-3 stories per LP
- Use STAR format (Situation, Task, Action, Result) rigorously

#### Round 5: On-site Loop (4-5 rounds)
- **Coding** (LeetCode medium/hard, 45 min):
  - Arrays, strings, hash tables, trees, graphs
  - Focus on efficiency and clean code
- **Data Modeling** (45 min):
  - Design a star schema for e-commerce analytics
  - Fact and dimension design decisions
  - Slowly changing dimension handling (Type 1/2/3)
- **SQL Deep Dive** (45 min):
  - Complex analytical queries
  - Query optimization: analyzing EXPLAIN plans
  - Redshift-specific: DISTKEY, SORTKEY, COMPROWS
- **Manager Loop** (45 min):
  - Leadership, vision, team building
  - Conflict resolution
  - Data strategy
- **Bar Raiser** (60 min):
  - Deep behavioral probing
  - Each answer followed by "Tell me more", "What did you learn?"

### Key Topics to Master
- Redshift: architecture, workload management, concurrency scaling
- Glue: ETL jobs, Data Catalog, crawlers, Glue Studio
- EMR: cluster types, instance fleets, auto-scaling
- Kinesis: shard management, partitioning, KCL
- Step Functions: state machines, error handling, retry
- S3: storage classes, lifecycle policies, Athena queries
- DynamoDB: table design, streams, TTL
- Lambda: triggers, scaling, timeout management

### Resources
- AWS re:Invent videos: DE-focused sessions
- AWS Well-Architected Framework: Data Analytics pillar
- Amazon Leadership Principles: memorize and prepare stories

---

## 6. Meta (Data Infrastructure)

### DE Roles at Meta
| Role | Focus | Typical Level |
|------|-------|--------------|
| Data Engineer (Product) | Facebook/Instagram product pipelines | IC3-IC5 |
| Data Infrastructure Engineer | Build and scale internal data platforms | IC4-IC7 |
| Analytics Engineer | Self-serve analytics, experimentation | IC3-IC5 |
| Production Data Engineer | Real-time ML feature pipelines | IC4-IC6 |
| Data Platform Engineer | Presto, Spark, Hive cluster management | IC5-IC7 |

### Interview Rounds

#### Round 1: Recruiter Screen (30 min)
- Experience with large-scale data (petabytes+)
- SQL + Python proficiency
- Distributed systems understanding
- Why Meta? Interest in social media + scale

#### Round 2: Technical Screen (45 min)
- **SQL** (50%):
  - Complex window functions: `RANGE` vs `ROWS` vs `GROUPS`
  - Self-joins for graph analytics (friendships, connections)
  - Array/JSON functions: `unnest`, `lateral view explode`
  - UDAF creation patterns
  - Query optimization for Presto/Hive
- **Python** (50%):
  - Data processing: pandas, NumPy, Dask
  - Writing efficient Spark UDFs
  - Generator expressions for memory efficiency
  - Data serialization: Avro, Thrift, Parquet

**Sample query:** "Friends connection strength based on mutual friends":
```sql
WITH mutuals AS (
  SELECT a.user_id, b.friend_id, COUNT(*) AS mutual_count
  FROM friendships a
  JOIN friendships b ON a.friend_id = b.friend_id
  WHERE a.user_id != b.friend_id
  GROUP BY 1, 2
)
SELECT * FROM mutuals ORDER BY mutual_count DESC;
```

#### Round 3: System Design (45 min)
- **Design a real-time friend suggestion system**
- **Design a notifications delivery pipeline**
- **Design a social graph analytics platform**
- **Design a real-time ad performance measurement system**
- **Design a data quality framework at petabyte scale**

**Common design question:** "Design a news feed ranking pipeline."
1. Features: user engagement features from Hive/Presto
2. Real-time: user session features from Scribe (log-based)
3. Model inference: prediction service calls
4. Ranked feed: Spark job scoring top N items
5. Online serving: TAO (graph) + features cache
6. Monitoring: backfill comparison, A/A tests, data freshness

#### Round 4: Behavioral (45 min)
- **Meta Behaviors:**
  - Move Fast: "Describe a time you shipped a data pipeline quickly"
  - Focus on Impact: "Prioritize between multiple stakeholder requests"
  - Build Social Value: "Using data for social good"
  - Be Direct and Respect: "Giving constructive feedback on a data model"
  - Meta Leadership Principles: prepare specific stories

#### Round 5: Coding + System Design Deep Dive (60 min)
- **Coding** (LeetCode medium):
  - Graph algorithms: BFS, DFS for social connections
  - String processing: JSON parsing, log parsing
  - Recursion and backtracking
- **System design** deep dive:
  - Follow-up on earlier design with scale constraints
  - How would you handle 10x growth?
  - SLAs: p99 latency, data freshness, availability

### Key Topics to Master
- Presto/Trino: connector architecture, query federation
- Spark at Meta: shuffle improvements, external shuffle service
- Hive: ACID, LLAP, ORC format
- Scribe: streaming log aggregation
- TAO: distributed graph store
- Linter/Scuba: real-time analytics at Meta
- Apache Cassandra: social graph storage
- FB Thrift: serialization framework
- Data quality: row-level diff, data validation frameworks

### Resources
- Meta Engineering Blog: data infrastructure posts
- Presto/Trino documentation and internals
- Apache Cassandra: read/write path, compaction

---

## 7. Microsoft Fabric

### DE Roles at Microsoft (Fabric)
| Role | Focus | Typical Level |
|------|-------|--------------|
| Data Engineer (Fabric) | Build pipelines using Fabric | 59-62 |
| Data Infrastructure Engineer | Synapse, OneLake, Spark internals | 61-65 |
| Analytics Engineer | Power BI, DAX, data modeling | 59-62 |
| Data Platform Engineer | Fabric capacity planning, administration | 61-64 |
| Software Engineer (Fabric Team) | Fabric product development | 61-67 |

### Interview Rounds

#### Round 1: Recruiter Screen (30 min)
- Experience with Microsoft data stack
- Azure Synapse, Data Lake, Power BI skills
- Why Microsoft / Fabric?
- Current title and level mapping

#### Round 2: Technical Screen (60 min)
- **Azure Data Platform** (40%):
  - Synapse Analytics: dedicated vs serverless SQL pools
  - OneLake: shortcuts, data mesh principles
  - Fabric experience: Lakehouse, Warehouse, Notebooks
  - Pipelines: Copy Data, Dataflow Gen2
- **Spark** (30%):
  - Fabric Spark: cluster management, notebooks
  - Delta Lake on OneLake
  - Lakehouse architecture patterns
- **Power BI / DAX** (30%):
  - Measure vs calculated column
  - Row context vs filter context
  - Time intelligence: SAMEPERIODLASTYEAR, DATESYTD

**Sample question:** "Design a medallion architecture in Fabric with Direct Lake mode."

#### Round 3: System Design (60 min)
- **Design a data mesh on Microsoft Fabric**
- **Design a real-time dashboard using Fabric**
- **Design a multi-geo data platform with OneLake**
- **Design a migration from on-prem SSIS to Fabric**
- **Design a cost-optimized Fabric capacity deployment**

**Common design question:** "Design a global sales analytics platform on Fabric."
1. OneLake: single copy of data across regions
2. Shortcuts: connect to ADLS, S3, GCS
3. Lakehouse: bronze/silver/gold layers with Delta format
4. Warehouse: gold layer for serving via T-SQL
5. Direct Lake: Power BI semantic model without import
6. Orchestration: Fabric pipelines, Dataflow Gen2
7. Governance: Microsoft Purview integration

#### Round 4: Behavioral + Leadership (45 min)
- **Microsoft competencies:**
  - Customer Obsession
  - Growth Mindset
  - Diverse and Inclusive
  - Collaborative
  - Data-Driven
- Prepare stories on: cross-team collaboration, learning new tech, customer focus

#### Round 5: Final Loop (ASAP - Ask, Study, Answer, Problems)
- **Coding** (LeetCode medium):
  - Arrays, strings, trees
  - C#, Python, or TypeScript preferred
- **System Design** (45 min follow-up):
  - Scaling, disaster recovery, data consistency
  - Cost optimization strategies
- **Deep Expertise**:
  - Data modeling: star schema in Fabric Warehouse
  - Performance: query tuning, partition pruning, statistics

### Key Topics to Master
- OneLake: shortcuts, regions, data mesh, tenant-level storage
- Fabric Lakehouse: tables, partitions, V-order optimization
- Fabric Warehouse: T-SQL surface area, CTAS, statistics
- Direct Lake: semantic model, columnar storage
- Pipelines: activities, parameters, triggers
- Dataflow Gen2: Power Query in the cloud
- Notebooks: Spark, Python, SQL, R
- Capacity: SKUs, consumption meters, throttling
- Microsoft Purview: data catalog, lineage, sensitive data
- Real-Time Intelligence: KQL database, Eventstream

### Resources
- Microsoft Learn: Fabric DE learning path
- Fabric documentation (learn.microsoft.com/fabric)
- Power BI blog: DAX patterns, Direct Lake deep dives
- Azure Data Explorer (ADX) documentation

---

## 8. Fivetran

### DE Roles at Fivetran
| Role | Focus | Typical Level |
|------|-------|--------------|
| Data Engineer (Customer-facing) | Connector migration, pipeline setup | IC3-IC5 |
| Solutions Architect | Enterprise deployment, data modeling | IC4-IC6 |
| Partner Engineer | Connector SDK development | IC3-IC5 |
| Software Engineer (Connectors) | Build and maintain 200+ connectors | IC4-IC6 |
| Analytics Engineer | dbt transformations on Fivetran data | IC3-IC5 |

### Interview Rounds

#### Round 1: Recruiter Screen (30 min)
- ETL/ELT experience
- Tools: Fivetran, Airbyte, Stitch, Meltano
- Why Fivetran? Interest in automated data movement

#### Round 2: Technical Screen (60 min)
- **ELT concepts** (40%):
  - Historical sync vs incremental sync
  - CDC: Log-based vs timestamp-based vs full refresh
  - Schema drift handling
  - API connector pagination (cursor, offset, page)
  - Data type mapping across sources/destinations
- **SQL + Transformations** (40%):
  - dbt transformations on raw data
  - Staging models, intermediate models, fact/dimension
  - Incremental models: merge, delete+insert, insert-only
  - Custom SQL connectors (Fivetran HVR/MQL)
- **Error handling** (20%):
  - Checkpointing and resumability
  - Retry policies and backoff strategies
  - Dead letter queues

**Sample problem:** "Design a custom connector for a REST API with rate limiting."

#### Round 3: System Design (60 min)
- **Design a connector platform like Fivetran**
- **Design a schema drift handling system**
- **Design a multi-tenant data pipeline platform**
- **Design a sync orchestration engine**
- **Design API connector with oAuth and pagination**

**Common design question:** "Design a log-based CDC connector for PostgreSQL."
1. WAL reading: logical replication slot
2. Parsing: INSERT/UPDATE/DELETE from WAL events
3. Schema: tracking table schema changes
4. State: checkpointing LSN positions
5. Delivery: batching, ordering guarantees
6. Reliability: exactly-once vs at-least-once

#### Round 4: Behavioral (45 min)
- Customer empathy: handling failed syncs
- Ownership: connector reliability and bug fixes
- Engineering excellence: testing connectors
- Communication: explaining sync delays to customers

### Key Topics to Master
- Connector types: database, SaaS API, file, event
- Sync strategies: historical, incremental, CDC
- Schema drift: column addition, type changes
- Data types: JSON flattening, nested structure handling
- Destination support: Snowflake, BigQuery, Redshift, Databricks
- Security: encryption in transit/at rest, SSH tunnels, IP whitelist
- Monitoring: sync logs, alerts, usage metrics
- dbt integration: Fivetran dbt packages

### Resources
- Fivetran Docs: connector reference, best practices
- Fivetran Blog: engineering posts on connector internals
- dbt Fivetran packages: source definitions, staging models

---

## 9. dbt Labs

### DE Roles at dbt Labs
| Role | Focus | Typical Level |
|------|-------|--------------|
| Analytics Engineer (Customer-facing) | dbt implementation, data modeling | IC3-IC5 |
| Solutions Architect | Enterprise dbt adoption | IC4-IC6 |
| Software Engineer (dbt Core) | dbt Core, dbt Cloud development | IC4-IC7 |
| Field Engineer | Customer onboarding, migration | IC3-IC5 |
| Data Modeling Expert | dbt patterns, Kimball methodology | IC4-IC6 |

### Interview Rounds

#### Round 1: Recruiter Screen (30 min)
- dbt experience level
- Data modeling methodology (Kimball, Data Vault)
- SQL expertise
- Why dbt? Passion for analytics engineering

#### Round 2: Technical Screen (60 min)
- **dbt Core** (50%):
  - Models: view, table, incremental, ephemeral
  - Materializations: custom materializations
  - Tests: singular, generic (not_null, unique, relationships, accepted_values)
  - Sources: freshness, loaded_at_field, filters
  - Exposures: connecting dbt to BI tools
  - Jinja and macros: for loops, conditional logic
  - dbt_project.yml: config inheritance
  - Seeds, snapshots, analyses, operations
- **Data Modeling** (30%):
  - Kimball: star schema, conformed dimensions
  - Data Vault: hubs, links, satellites
  - One definition: CTE-based layered modeling
  - Naming conventions: stg_, int_, fct_, dim_
- **SQL** (20%):
  - Window functions for deduplication
  - Array aggregation: LISTAGG, ARRAY_AGG
  - Type 2 SCD snapshots

**Sample dbt model:**
```sql
-- Dim_Customer with SCD Type 2 using dbt snapshots
{% snapshot dim_customer_snapshot %}
{{
    config(
        target_schema='snapshots',
        unique_key='customer_id',
        strategy='check',
        check_cols=['email', 'address', 'phone', 'status']
    )
}}
SELECT * FROM {{ ref('stg_customers') }}
{% endsnapshot %}
```

#### Round 3: System Design (60 min)
- **Design a multi-tenant dbt Cloud platform**
- **Design a data quality framework with dbt tests**
- **Design an analytics engineering practice**
- **Design a CI/CD pipeline for dbt**
- **Design dbt Mesh for large organizations**

**Common design question:** "Design a CI/CD pipeline for dbt."
1. Branch: feature branch changes
2. CI: dbt build --select state:modified+ on PR
3. Artifacts: manifest.json, run_results.json
4. Docs: dbt docs generate on staging schema
5. Diff: dbt slim CI, column-level lineage
6. Deploy: dbt build to production via dbt Cloud
7. Observability: dbt exposures + freshness alerts

#### Round 4: Behavioral + Philosophy (45 min)
- **Analytics engineering philosophy:**
  - What is analytics engineering vs data engineering?
  - Why the transformation layer should be in SQL, not Python
  - Advantages of version-controlled data models
- **Collaboration:**
  - Working with data analysts, DEs, and stakeholders
  - Data modeling disagreements
  - Teaching SQL best practices

### Key Topics to Master
- dbt Core: CLI, profiles.yml, packages.yml
- dbt Cloud: jobs, environments, CI/CD, APIs
- Materializations: table, view, incremental, snapshot, ephemeral
- Jinja: macros, packages (dbt_utils, dbt_expectations)
- Testing: dbt test, generic tests, custom tests
- Documentation: dbt docs, descriptions, lineage
- Source freshness: SLAs for data arrival
- Exposures: BI sync, usage reporting
- dbt Mesh: cross-project ref, versioning, contracts
- dbt Semantic Layer: metrics, saved queries

### Resources
- dbt Learn: free courses, dbt Fundamentals
- dbt Docs: reference, best practices
- dbt Blog: engineering posts, analytics engineering guides
- dbt GitHub: open-source contributions

---

## 10. Airflow / Astronomer

### DE Roles at Astronomer (Airflow)
| Role | Focus | Typical Level |
|------|-------|--------------|
| Solutions Engineer | Airflow deployment, migration | IC3-IC5 |
| Software Engineer (Airflow) | Airflow core, scheduler, executor | IC4-IC7 |
| Data Engineer | Build DAGs, pipeline orchestration | IC3-IC5 |
| Field Engineer | Customer onboarding, training | IC3-IC5 |
| SRE (Airflow Platform) | Airflow infrastructure, scaling | IC4-IC6 |

### Interview Rounds

#### Round 1: Recruiter Screen (30 min)
- Workflow orchestration experience
- Airflow vs Dagster vs Prefect
- Python expertise (DAG authoring)
- Why Astronomer? Interest in open-source data infrastructure

#### Round 2: Technical Screen (60 min)
- **Airflow Core** (50%):
  - DAG lifecycle: parser, scheduler, executor, worker
  - Operators: PythonOperator, SQLExecuteQueryOperator, KubernetesPodOperator
  - Sensors: time, file, custom sensors
  - Hooks: service connections (Snowflake, GCP, AWS)
  - XComs: size limits, custom backends
  - TaskFlow API: @dag, @task decorators
  - Dynamic DAGs: dynamic task mapping, DAG factory
- **Executor types** (20%):
  - SequentialExecutor, LocalExecutor, CeleryExecutor
  - KubernetesExecutor: pod customization, namespace-per-DAG
  - CeleryKubernetesExecutor: hybrid for scale
- **Scheduler** (30%):
  - DAG processing: FileProcess, DAG Serialization
  - Smart sensor: replacing traditional sensors
  - min_file_process_interval, parsing_processes
  - With statement: DAG-level concurrency

**Sample DAG pattern:**
```python
@dag(
    schedule="@daily",
    start_date=pendulum.datetime(2024, 1, 1),
    catchup=False,
    tags=["etl", "customer"],
)
def customer_etl():
    @task
    def extract():
        return load_from_api()

    @task
    def transform(data):
        return transform_data(data)

    @task
    def load(data):
        write_to_warehouse(data)

    load(transform(extract()))

customer_etl_dag = customer_etl()
```

#### Round 3: System Design (60 min)
- **Design a multi-tenant Airflow platform**
- **Design a DAG authoring and CI/CD system**
- **Design a pipeline observability platform**
- **Design a dynamic pipeline generation system**
- **Design a data quality orchestration framework**

**Common design question:** "Design Airflow for 1000+ DAGs across 10 teams."
1. Team namespaces: project-level DAG folders
2. KubernetesExecutor: per-DAG pod, isolation
3. CI/CD: deploy DAGs via GitSync or Registry
4. Monitoring: statsd/Prometheus + custom health checks
5. Alerting: SLA misses, task failures, sensor timeouts
6. Cost: resource quotas, pod limits, cluster scaling

#### Round 4: Behavioral + Open Source (45 min)
- Open-source contributions to Airflow
- Community engagement: PRs, issues, AIPs
- Handling production incidents with Airflow
- Cross-team orchestration standardization
- Dealing with spaghetti DAGs

### Key Topics to Master
- Airflow architecture: DAG Directory, Metadata DB, Scheduler, Workers
- Executors: Sequential, Local, Celery, Kubernetes, CeleryK8s
- Task lifecycle: scheduled -> queued -> running -> success/fail
- Triggerer: deferrable operators, triggers, time-waiting
- Airflow 2.0+ features: TaskFlow, dynamic task mapping, deferrable
- Best DAG practices: idempotency, retries, backfills
- Monitoring: statsd metrics, logs, task-level SLAs
- Security: RBAC, LDAP, OpenID Connect
- Lineage: OpenLineage integration
- Providers: 200+ integrations (Snowflake, AWS, GCP, Databricks)

### Resources
- Airflow Docs: official documentation
- Astronomer Docs: Deployment, best practices
- Airflow GitHub: AIPs, RFCS
- Coursera: Data Pipeline Orchestration with Airflow
- Marquez / OpenLineage: lineage integration
