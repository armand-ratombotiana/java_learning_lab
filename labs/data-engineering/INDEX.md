# Data Engineering Academy — Complete Learning Path

<div align="center">

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spark](https://img.shields.io/badge/Apache_Spark-E25A1C?style=for-the-badge&logo=apachespark&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)
![Labs](https://img.shields.io/badge/Labs-20-blue?style=for-the-badge)
![Level](https://img.shields.io/badge/Level-Beginner_to_Expert-orange?style=for-the-badge)

**Build robust data pipelines — batch processing, real-time streaming, and the modern data stack**

</div>

---

## Overview

The Data Engineering Academy covers the full lifecycle of data processing from batch ETL to real-time streaming analytics. You will learn foundational batch pipelines, stream processing with Kafka and Flink, data warehousing, modern table formats (Delta Lake, Iceberg), workflow orchestration (Airflow), data quality and observability, feature stores for ML, and data governance. All labs emphasize production-ready patterns for reliability, scalability, and maintainability.

---

## Curriculum Map

### Level 1: Batch Processing
| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 01 | [ETL Pipelines](./01-etl-pipelines/) | Extract, transform, load, data quality, scheduling | 4-5 hrs | Intermediate |
| 02 | [Apache Spark Batch](./02-spark-batch/) | RDDs, DataFrames, Spark SQL, optimizations | 5-6 hrs | Advanced |
| 03 | [Data Warehousing Fundamentals](./03-data-warehousing/) | Star/snowflake schemas, fact/dimension tables, OLAP | 3-4 hrs | Intermediate |

### Level 2: Real-Time Processing
| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 04 | [Kafka Streams](./04-kafka-streams/) | Stream processing, KTable, KStream, state stores | 5-6 hrs | Advanced |
| 05 | [Apache Spark Streaming](./05-spark-streaming/) | DStreams, structured streaming, sliding windows | 5-6 hrs | Advanced |
| 06 | [Change Data Capture (CDC)](./06-cdc/) | Debezium, Kafka Connect, database logs, incremental snapshots | 4-5 hrs | Advanced |

### Level 3: Advanced Data Engineering
| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 07 | [ML Pipeline Integration](./07-ml-pipelines/) | Feature engineering, model serving, MLOps | 4-5 hrs | Advanced |
| 08 | [Vector Databases & Embeddings](./08-vector-databases/) | Embedding storage, similarity search, RAG pipelines | 4-5 hrs | Advanced |
| 09 | [Snowflake Data Cloud](./09-snowflake/) | Architecture, virtual warehouses, clustering, time travel, zero-copy cloning | 4-5 hrs | Intermediate |
| 10 | [Apache Flink](./10-apache-flink/) | Stream processing, event time, watermarks, checkpointing, Flink SQL | 5-6 hrs | Advanced |
| 11 | [Apache Airflow](./11-apache-airflow/) | DAGs, operators, sensors, TaskFlow API, executors, XComs | 4-5 hrs | Intermediate |
| 12 | [Apache Beam](./12-apache-beam/) | Unified batch/streaming, PCollections, windowing, triggers, portable runners | 4-5 hrs | Advanced |

### Level 4: Modern Data Stack
| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 13 | [Delta Lake](./13-delta-lake/) | ACID on lake, time travel, schema enforcement, merge/optimize | 4-5 hrs | Advanced |
| 14 | [Apache Iceberg](./14-apache-iceberg/) | Table format, partitioning evolution, hidden partitioning, compaction | 4-5 hrs | Advanced |
| 15 | [Data Quality Engineering](./15-data-quality/) | Great Expectations, dbt tests, data contracts, SLAs, schema drift | 4-5 hrs | Intermediate |
| 16 | [Data Catalog](./16-data-catalog/) | Metadata management, discovery, lineage, Apache Atlas, DataHub, OpenMetadata | 4-5 hrs | Intermediate |

### Level 5: Data Governance & Streaming Analytics
| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 17 | [Real-Time Feature Store](./17-real-time-feature-store/) | Feast, feature serving, online/offline store, point-in-time joins | 4-5 hrs | Advanced |
| 18 | [Data Observability](./18-data-observability/) | Data lineage, profiling, anomaly detection, Monte Carlo, Soda | 4-5 hrs | Intermediate |
| 19 | [Data Governance](./19-data-governance/) | RBAC, PII detection, data masking, GDPR/compliance, audit logging | 4-5 hrs | Intermediate |
| 20 | [Streaming Analytics](./20-streaming-analytics/) | Real-time dashboards, streaming SQL, Kinesis Analytics, Flink SQL | 4-5 hrs | Advanced |

**Total estimated time: 85-110 hours**

---

## Learning Path

```
01 ──→ 02 ──→ 03 ──→ 04 ──→ 05 ──→ 06 ──→ 07 ──→ 08
ETL    Spark   DWH     Kafka   Spark   CDC     ML      Vector
       Batch           Stream  Stream          Pipe    DB

09 ──→ 10 ──→ 11 ──→ 12 ──→ 13 ──→ 14 ──→ 15 ──→ 16
Snow   Flink   Airflow Beam   Delta   Iceberg Quality Catalog
flake                          Lake

17 ──→ 18 ──→ 19 ──→ 20
Feat.  Observ-  Govern-  Stream
Store  ability  ance     Analytics
```

Labs 01–03 establish batch processing fundamentals. Labs 04–06 cover real-time and CDC. Labs 07–08 integrate with ML and AI pipelines. Labs 09–12 introduce the modern data stack (Snowflake, Flink, Airflow, Beam). Labs 13–14 cover modern table formats (Delta Lake, Iceberg). Labs 15–16 cover data quality and cataloging. Labs 17–18 cover feature stores and observability. Labs 19–20 cover governance and streaming analytics.

---

## Prerequisites

- Strong Java proficiency (Java 11+)
- Basic knowledge of SQL and database concepts
- Familiarity with Kafka basics (producers/consumers)
- Understanding of functional programming (map, filter, reduce)
- Basic Docker knowledge for running infrastructure services
- Apache Spark cluster or local mode setup

---

## How to Use This Academy

### For Data Engineers
Work through Labs 01–20 sequentially for comprehensive data engineering coverage.

### For Backend Engineers
Focus on Labs 04–06, 09–12 for streaming, CDC, and workflow skills.

### For ML Engineers
Pay special attention to Labs 07–08, 13–15, 17 for feature engineering, data quality, and feature stores.

### For Data Architects
Focus on Labs 09–16 for modern data stack, table formats, and data governance.

---

## Related Academies

- [Backend Academy](../backend/) — Kafka, messaging, services
- [Distributed Systems Academy](../distributed-systems/) — Streaming platforms, Kafka, Pulsar
- [Databases Academy](../databases/) — PostgreSQL, MongoDB, data storage
- [AI Academy](../ai/) — ML pipelines, embeddings, RAG
- [DevOps Academy](../devops/) — Containerization, orchestration, monitoring
- [Cloud Academy](../cloud/) — AWS, GCP, Azure data services

---

## Resources

### Official Documentation
- [Apache Spark Docs](https://spark.apache.org/docs/latest/)
- [Kafka Streams Docs](https://kafka.apache.org/documentation/streams/)
- [Debezium Documentation](https://debezium.io/documentation/)
- [Snowflake Docs](https://docs.snowflake.com/)
- [Apache Flink Docs](https://flink.apache.org/)
- [Apache Airflow Docs](https://airflow.apache.org/docs/)
- [Apache Beam Docs](https://beam.apache.org/)
- [Delta Lake Docs](https://docs.delta.io/latest/)
- [Apache Iceberg Docs](https://iceberg.apache.org/)
- [Great Expectations](https://greatexpectations.io/)
- [Feast](https://feast.dev/)
- [Soda](https://www.soda.io/)
- [OpenMetadata](https://open-metadata.org/)

### Books
- *Learning Spark* — Jules Damji, Brooke Wenig
- *Stream Processing with Apache Flink* — Fabian Hueske, Vasiliki Kalavri
- *Data Pipelines with Apache Airflow* — Bas Harenslak, Julian de Ruiter
- *Kafka: The Definitive Guide* — Neha Narkhede
- *Designing Data-Intensive Applications* — Martin Kleppmann
- *The Data Warehouse Toolkit* — Ralph Kimball

### Tools
- [Apache Spark](https://spark.apache.org/)
- [Kafka](https://kafka.apache.org/)
- [Debezium](https://debezium.io/)
- [Snowflake](https://www.snowflake.com/)
- [Apache Flink](https://flink.apache.org/)
- [Apache Airflow](https://airflow.apache.org/)
- [Apache Beam](https://beam.apache.org/)
- [Delta Lake](https://delta.io/)
- [Apache Iceberg](https://iceberg.apache.org/)
- [Great Expectations](https://greatexpectations.io/)
- [Feast](https://feast.dev/)
- [dbt](https://www.getdbt.com/)
- [OpenMetadata](https://open-metadata.org/)

---

<div align="center">

**Engineer Data. Build Everything.**

</div>
