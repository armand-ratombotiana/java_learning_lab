# Company Interview Guide for Data Engineering

## FAANG & Top Tech — Data Engineering Interview Patterns

### Amazon
- **Focus**: Leadership principles, system design (Kinesis, S3, Redshift), SQL-heavy
- **Typical loop**: 1x phone screen -> 4x on-site (2x LP/behavioral, 1x SQL, 1x system design)
- **DE-specific**: Design a real-time dashboard, design a data pipeline for personalization
- **Prep tip**: Practice STAR stories with 2-3 data-specific examples

### Google
- **Focus**: Algorithms, distributed systems, data modeling, Go/Python
- **Typical loop**: 1x phone -> 5x on-site (2x algorithms, 1x systems design, 1x data modeling, 1x Googleyness)
- **DE-specific**: Design a logging pipeline, design a columnar storage format
- **Prep tip**: Master MapReduce concepts, Spanner/Bigtable mental models

### Meta (Facebook)
- **Focus**: SQL, product analytics, pipeline efficiency, scale
- **Typical loop**: 1x recruiter -> 1x technical screen -> 3x virtual on-site (1x SQL, 1x data modeling, 1x behavioral)
- **DE-specific**: Build an A/B testing pipeline, optimize a slow Hive query
- **Prep tip**: Know Presto/Hive internals, star schema modeling

### Netflix
- **Focus**: Data platform engineering, stream processing, chaos engineering
- **Typical loop**: 1x phone -> 4x on-site (1x coding, 1x DE architecture, 1x system design, 1x culture)
- **DE-specific**: Design a real-time recommendation feature store
- **Prep tip**: Understand Keystone/Manhattan, Apache Kafka at scale

### Uber
- **Focus**: Real-time data, Apache Kafka, Apache Flink, data quality
- **Typical loop**: 1x screen -> 3-4x on-site (coding, system design, SQL, behavioral)
- **DE-specific**: Design a real-time ETA pipeline, fraud detection streaming job
- **Prep tip**: Study Uber's data platform blogs, Hive -> Spark migration patterns

### Apple
- **Focus**: Privacy, on-device intelligence, data pipelines
- **Typical loop**: 1x recruiter -> 1x tech screen -> on-site (coding, data engineering, domain, behavioral)
- **DE-specific**: Design a privacy-preserving analytics pipeline
- **Prep tip**: Differential privacy concepts, on-device vs server-side tradeoffs

### Stripe
- **Focus**: Event-driven architecture, idempotency, event sourcing
- **Typical loop**: 1x screen -> on-site (coding, system design, DE deep-dive)
- **DE-specific**: Design a payment reconciliation pipeline
- **Prep tip**: Exactly-once semantics, idempotency keys, outbox pattern

### Airbnb
- **Focus**: Data quality, experimentation, data discovery
- **Typical loop**: 1x screen -> on-site (SQL, DE coding, system design, behavioral)
- **DE-specific**: Design Minerva (metric platform), data quality monitoring
- **Prep tip**: Airflow DAG design, data discovery tooling patterns

## Common System Design Questions by Company

| Company | Classic DE Design Question |
|---------|---------------------------|
| Amazon  | Design a real-time inventory tracking system |
| Google  | Design a log aggregation service |
| Meta    | Design a real-time event processing pipeline |
| Netflix | Design a data platform for A/B testing |
| Uber    | Design a real-time data pipeline for trip pricing |
| Stripe  | Design a financial event processing system |

## Preparation Timeline

| Week | Focus Area                                            |
|------|-------------------------------------------------------|
| 1-2  | SQL mastery (window functions, joins, optimization)   |
| 3-4  | Java/Python coding (LeetCode medium)                  |
| 5-6  | System design fundamentals (consistency, partitioning)|
| 7-8  | DE-specific system design (pipelines, storage)        |
| 9-10 | Mock interviews + behavioral prep                     |
