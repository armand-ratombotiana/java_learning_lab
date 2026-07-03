# Data Engineering Academy — Complete Learning Path

<div align="center">

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spark](https://img.shields.io/badge/Apache_Spark-E25A1C?style=for-the-badge&logo=apachespark&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)
![Labs](https://img.shields.io/badge/Labs-8-blue?style=for-the-badge)
![Level](https://img.shields.io/badge/Level-Intermediate_to_Expert-orange?style=for-the-badge)

**Build robust data pipelines — batch processing, real-time streaming, and change data capture**

</div>

---

## Overview

The Data Engineering Academy covers the full lifecycle of data processing in the Java/Spark ecosystem. You will learn batch ETL pipelines, real-time streaming with Kafka, change data capture (CDC), data warehousing concepts, and integration with vector databases for modern AI pipelines. All labs emphasize production-ready patterns for reliability and scalability.

---

## Curriculum Map

### Level 1: Batch Processing
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 01 | [ETL Pipelines](./01-etl-pipelines/) | Extract, transform, load, data quality, scheduling | 4-5 hrs | Intermediate | [62-data-pipeline](../../62-data-pipeline/) |
| 02 | [Apache Spark Batch](./02-spark-batch/) | RDDs, DataFrames, Spark SQL, optimizations | 5-6 hrs | Advanced | [62-data-pipeline](../../62-data-pipeline/) |
| 03 | [Data Warehousing Fundamentals](./03-data-warehousing/) | Star/snowflake schemas, fact/dimension tables, OLAP | 3-4 hrs | Intermediate | — |

### Level 2: Real-Time Processing
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 04 | [Kafka Streams](./04-kafka-streams/) | Stream processing, KTable, KStream, state stores | 5-6 hrs | Advanced | [63-streaming](../../63-streaming/), [27-kafka-streams](../../27-kafka-streams/) |
| 05 | [Apache Spark Streaming](./05-spark-streaming/) | DStreams, structured streaming, sliding windows | 5-6 hrs | Advanced | [63-streaming](../../63-streaming/) |
| 06 | [Change Data Capture (CDC)](./06-cdc/) | Debezium, Kafka Connect, database logs, incremental snapshots | 4-5 hrs | Advanced | [64-cdc](../../64-cdc/) |

### Level 3: Advanced Data Engineering
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 07 | [ML Pipeline Integration](./07-ml-pipelines/) | Feature engineering, model serving, MLOps | 4-5 hrs | Advanced | [61-ml](../../61-ml/) |
| 08 | [Vector Databases & Embeddings](./08-vector-databases/) | Embedding storage, similarity search, RAG pipelines | 4-5 hrs | Advanced | [77-vector-database](../../77-vector-database/) |

**Total estimated time: 34-42 hours**

---

## Learning Path

```
01 ──→ 02 ──→ 03 ──→ 04 ──→ 05 ──→ 06 ──→ 07 ──→ 08
ETL    Spark   DWH     Kafka   Spark   CDC     ML      Vector
       Batch           Stream  Stream          Pipe    DB
```

Labs 01–02 establish batch processing fundamentals. Labs 03–06 cover real-time and CDC. Labs 07–08 integrate with ML and AI pipelines.

---

## Prerequisites

- Strong Java proficiency (Java 11+)
- Basic knowledge of SQL and database concepts
- Familiarity with Kafka basics (producers/consumers)
- Understanding of functional programming (map, filter, reduce)
- Apache Spark cluster or local mode setup

---

## How to Use This Academy

### For Data Engineers
Work through Labs 01–06 sequentially. Labs 07–08 are for ML/AI integration.

### For Backend Engineers
Focus on Labs 04–06 for streaming and CDC skills relevant to event-driven systems.

### For ML Engineers
Pay special attention to Labs 07–08 for data pipeline and vector database integration.

---

## Related Academies

- [Backend Academy](../backend/) — Kafka, messaging, services
- [Distributed Systems Academy](../distributed-systems/) — Streaming platforms, Kafka, Pulsar
- [Databases Academy](../databases/) — PostgreSQL, MongoDB, data storage
- [AI Academy](../ai/) — ML pipelines, embeddings, RAG
- [DevOps Academy](../devops/) — Containerization, orchestration, monitoring

---

## Resources

### Official Documentation
- [Apache Spark Docs](https://spark.apache.org/docs/latest/)
- [Kafka Streams Docs](https://kafka.apache.org/documentation/streams/)
- [Debezium Documentation](https://debezium.io/documentation/)
- [Kafka Connect Docs](https://docs.confluent.io/platform/current/connect/)

### Books
- *Learning Spark* — Jules Damji, Brooke Wenig
- *Stream Processing with Apache Spark* — Gerard Maas
- *Kafka: The Definitive Guide* — Neha Narkhede
- *Designing Data-Intensive Applications* — Martin Kleppmann

### Tools
- [Apache Spark](https://spark.apache.org/)
- [Kafka](https://kafka.apache.org/)
- [Debezium](https://debezium.io/)

---

<div align="center">

**Engineer Data. Build Everything.**

</div>
