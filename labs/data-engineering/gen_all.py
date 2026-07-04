#!/usr/bin/env python3
import os

BASE = r"C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\data-engineering"

def w(path, content):
    if not os.path.exists(path):
        with open(path, "w", encoding="utf-8") as f:
            f.write(content.lstrip() + "\n")
        return True
    return False

gen = 0

# ============== LAB 01: DATA PIPELINES ==============
d = os.path.join(BASE, "01-data-pipelines")
c_readme = """# Data Pipelines

A data pipeline is a series of processing steps moving data from sources to destinations.

## Key Concepts
- **Batch Processing**: Bounded data on schedule
- **Stream Processing**: Unbounded data in real-time
- **Pipeline Patterns**: Fan-out, Fan-in, Lambda, Kappa

## Java/Spark Example
```java
import org.apache.spark.sql.SparkSession;
public class PipelineExample {
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder().appName("Pipeline").getOrCreate();
        spark.read().option("header",true).csv("input.csv")
            .filter("value IS NOT NULL").groupBy("category")
            .agg(org.apache.spark.sql.functions.avg("value"))
            .write().mode("overwrite").parquet("output/");
    }
}
```"""
if w(os.path.join(d, "README.md"), c_readme): gen+=1

c_theory = """# Data Pipeline Theory

## Batch vs Streaming
| Batch | Streaming |
|-------|-----------|
| Bounded data | Unbounded data |
| Scheduled | Continuous |
| Minutes latency | Ms latency |
| Lower complexity | Higher complexity |

## Architecture Patterns
- **ETL**: Extract, Transform, Load
- **ELT**: Extract, Load, Transform (in warehouse)
- **Lakehouse**: Data lake + warehouse"""
if w(os.path.join(d, "THEORY.md"), c_theory): gen+=1

c_why_exists = """# Why Data Pipelines Exist

Raw data is rarely queryable in native format. Pipelines exist to:
1. Centralize data from distributed sources
2. Standardize formats and schemas
3. Clean and validate data quality
4. Enrich with business context
5. Deliver to consumption tools

Analogy: Like a city water system - raw water collected, treated, distributed through pipes."""
if w(os.path.join(d, "WHY_IT_EXISTS.md"), c_why_exists): gen+=1

c_why_matters = """# Why Data Pipelines Matter

- **Data-Driven Decisions**: Clean data enables accurate analytics
- **Operational Efficiency**: Automation replaces manual work
- **Real-Time Insights**: Streaming enables instant response
- **Data Quality**: Enforced validation and schema enforcement

| Metric | Without | With |
|--------|---------|------|
| Time to insight | Days | Minutes |
| Error rate | High | <0.1% |
| Freshness | Stale | Real-time |"""
if w(os.path.join(d, "WHY_IT_MATTERS.md"), c_why_matters): gen+=1

c_hist = """# History of Data Pipelines

- **1970s**: Mainframe batch COBOL
- **1980s**: ETL tools (Informatica, DataStage)
- **1990s**: Data warehousing (Inmon, Kimball)
- **2000s**: Hadoop ecosystem
- **2010s**: Spark unifies batch and streaming; Kafka enables real-time
- **2020s**: Lakehouse, Data Mesh, DataOps

## Key Milestones
1. 2004: Google MapReduce paper
2. 2010: Apache Kafka at LinkedIn
3. 2014: Apache Spark top-level project
4. 2019: Delta Lake open-sourced"""
if w(os.path.join(d, "HISTORY.md"), c_hist): gen+=1

c_mental = """# Mental Models

## 1. Assembly Line
Each stage adds value: raw materials -> inspection -> assembly -> delivery

## 2. Water Flow
Source=reservoir, Pipeline=pipes, Transform=filtration, Sink=storage

## 3. DAG Model
Nodes=processing steps, Edges=data dependencies, No cycles allowed"""
if w(os.path.join(d, "MENTAL_MODELS.md"), c_mental): gen+=1

c_how = """# How Data Pipelines Work

## Core Mechanism
1. **Source Connectors**: JDBC, Kafka, REST APIs
2. **Transformation Layer**: Cleaning, enrichment, aggregation
3. **Sink Connectors**: Warehouse, data lake, search index

## Processing Semantics
- **At-most-once**: Fast but may lose data
- **At-least-once**: Reliable but may duplicate
- **Exactly-once**: Most reliable, highest overhead

```java
Dataset<Row> result = orders.join(customers, "id")
    .withColumn("total", functions.expr("qty * price"))
    .filter("total > 0");
result.write().mode("append").jdbc(url, "fact_orders", props);
```"""
if w(os.path.join(d, "HOW_IT_WORKS.md"), c_how): gen+=1

c_internals = """# Pipeline Internals

## Process Manager
Tasks are executed via thread pool with CompletableFuture composition. Each stage runs asynchronously.

## Checkpointing
Write-ahead log for state recovery. Offset tracking enables resume from failures.

## Backpressure
Bounded queues with rejection prevent unbounded memory growth."""
if w(os.path.join(d, "INTERNALS.md"), c_internals): gen+=1

c_math = """# Math Foundation

## Throughput
TPS = N / dt; Total = Sum(TP_i) for i workers

## Latency
Total = T_extract + T_transform + T_load + T_queue

## Amdahl's Law
Speedup = 1 / ((1-P) + P/N) where P = parallelizable fraction

## Capacity
```java
public static double calcNodes(long volGB, double rate, double sla) {
    return Math.ceil(volGB / 24.0 / rate * 24.0 / sla);
}
```"""
if w(os.path.join(d, "MATH_FOUNDATION.md"), c_math): gen+=1

c_visual = """# Visual Guide

## Pipeline Architecture
```
[Source] -> [Extract] -> [Transform] -> [Load] -> [Warehouse]
```

## State Machine
PENDING -> RUNNING -> STOPPING -> COMPLETED
                               -> FAILED -> TIMEOUT -> ABORTED

## Streaming Pipeline
```
[Producers] -> [Kafka] -> [Stream Processor] -> [Consumers]
```"""
if w(os.path.join(d, "VISUAL_GUIDE.md"), c_visual): gen+=1

c_code = """# Code Deep Dive: Spring Batch Pipeline

## Domain Model
```java
public class Transaction {
    private String id; private String userId; private BigDecimal amount;
    private LocalDateTime timestamp; private String status;
    // Getters and Setters
}
```

## Item Processor
```java
public class EnrichmentProcessor implements ItemProcessor<Transaction, Enriched> {
    public Enriched process(Transaction t) {
        Customer c = customerService.findById(t.getUserId());
        Enriched e = new Enriched();
        e.setId(t.getId()); e.setCustomerName(c.getFullName());
        e.setRiskScore(fraudClient.evaluate(t));
        return e;
    }
}
```

## Job Configuration
```java
@Bean
public Job pipelineJob(JobRepository repo, Step step) {
    return new JobBuilder("pipeline", repo).start(step)
        .listener(new MetricsListener()).build();
}
```"""
if w(os.path.join(d, "CODE_DEEP_DIVE.md"), c_code): gen+=1

c_steps = """# Step-by-Step Guide

1. **Requirements**: Identify sources, SLAs, consumers
2. **Architecture Design**: Choose batch/streaming
3. **Source Connection**: Configure connectors
4. **Transform Logic**: Clean, enrich, aggregate
5. **Sink Configuration**: Write to target
6. **Monitoring**: Health checks, metrics, alerts"""
if w(os.path.join(d, "STEP_BY_STEP.md"), c_steps): gen+=1

c_mistakes = """# Common Mistakes

1. **Schema Evolution**: Hard-coding column names instead of schema-aware access
2. **Late Data**: Missing watermarks in streaming pipelines
3. **No Idempotency**: Duplicate data on retry (use MERGE)
4. **No Backpressure**: Unbounded memory with infinite queues
5. **Hardcoded Credentials**: Secrets in code instead of environment variables"""
if w(os.path.join(d, "COMMON_MISTAKES.md"), c_mistakes): gen+=1

c_debug = """# Debugging

## Common Failures
- **OOM**: Collecting large datasets - use foreachPartition
- **Serialization**: Task not serializable - use mapPartitions
- **Data Skew**: Few tasks run longer - salt keys

## Commands
```bash
open http://localhost:4040  # Spark UI
kafka-consumer-groups --describe --group pipeline-group
kubectl logs -l app=pipeline -n data-platform --tail=100 -f
```"""
if w(os.path.join(d, "DEBUGGING.md"), c_debug): gen+=1

c_refactor = """# Refactoring

## Before: Monolithic
```java
public class Pipeline { public void run() { /* 500 lines mixed */ } }
```

## After: Modular
```java
@Component
public class PipelineOrchestrator {
    private final Extractor e; private final Transformer t; private final Loader l;
    public PipelineResult run(PipelineContext ctx) {
        return l.load(t.transform(e.extract(ctx)));
    }
}
```"""
if w(os.path.join(d, "REFACTORING.md"), c_refactor): gen+=1

c_perf = """# Performance

## Spark Tuning
```java
SparkSession.builder()
    .config("spark.sql.shuffle.partitions", "200")
    .config("spark.sql.adaptive.enabled", "true")
    .config("spark.sql.adaptive.skewJoin.enabled", "true")
    .getOrCreate();
```

## Broadcast Joins
```java
largeDF.join(smallDF.hint("broadcast"), "key");
```

## Partition Pruning
```java
dataset.write().partitionBy("year","month","day").parquet("s3://data/");
```"""
if w(os.path.join(d, "PERFORMANCE.md"), c_perf): gen+=1

c_sec = """# Security

## Authentication
- Use environment variables for credentials
- SSL for JDBC connections
- Vault for secret management

## Encryption
- Encrypt PII columns with AES-GCM
- Column-level masking for sensitive data

## Audit
- Log all pipeline executions
- Track data lineage through transformations"""
if w(os.path.join(d, "SECURITY.md"), c_sec): gen+=1

c_arch = """# Architecture

## High-Level
```
+----------------------------------------------------+
|                Data Pipeline Platform               |
+------------+-----------+-----------+---------------+
| Ingestion  | Processing | Storage   | Serving       |
| Kafka      | Spark      | Data Lake | APIs          |
| JDBC       | Flink      | Warehouse | BI Tools      |
+------------+-----------+-----------+---------------+
```"""
if w(os.path.join(d, "ARCHITECTURE.md"), c_arch): gen+=1

c_ex = """# Exercises

1. Build a batch pipeline with Spring Batch (CSV -> PostgreSQL)
2. Create a streaming pipeline with Kafka + Spark
3. Implement retry and dead letter queue pattern
4. Add metrics collection and health endpoints"""
if w(os.path.join(d, "EXERCISES.md"), c_ex): gen+=1

c_quiz = """# Quiz

1. Batch vs stream processing difference?
   C) Batch=bounded, Stream=unbounded
2. DAG stands for?
   B) Directed Acyclic Graph
3. Purpose of checkpointing?
   B) Fault recovery"""
if w(os.path.join(d, "QUIZ.md"), c_quiz): gen+=1

c_flash = """# Flashcards

1. **Data Pipeline**: Series of steps moving data from source to destination
2. **ETL**: Extract, Transform, Load
3. **Exactly-once**: Each record processed once even on failure
4. **Dead Letter Queue**: Storage for failed messages
5. **Watermark**: Threshold for late data in streaming"""
if w(os.path.join(d, "FLASHCARDS.md"), c_flash): gen+=1

c_interview = """# Interview Questions

## Beginner
Q: What is a data pipeline?
A: An automated process that moves data from collection to consumption, cleaning and transforming it along the way.

## Intermediate
Q: Compare ETL vs ELT.
A: ETL transforms before loading (good for complex transforms on smaller data). ELT loads raw first and transforms in warehouse (better for big data).

## Senior
Q: Design real-time fraud detection pipeline.
A: Kafka ingestion -> Flink/Spark real-time feature computation -> ML model serving -> Rules engine -> Feedback loop. Must handle 10K+ events/sec with sub-second latency."""
if w(os.path.join(d, "INTERVIEW.md"), c_interview): gen+=1

c_reflect = """# Reflection

## Key Takeaways
- Data pipelines are fundamental data engineering infrastructure
- Understanding batch vs streaming tradeoffs is critical
- Reliability comes from idempotency, checkpointing, and monitoring
- Java/Spring ecosystem provides robust tooling for enterprise pipelines"""
if w(os.path.join(d, "REFLECTION.md"), c_reflect): gen+=1

c_refs = """# References

- "Designing Data-Intensive Applications" by Martin Kleppmann
- "The Data Warehouse Toolkit" by Ralph Kimball
- "Streaming Systems" by Tyler Akidau et al.
- Apache Spark: https://spark.apache.org
- Spring Batch: https://spring.io/projects/spring-batch
- Apache Kafka: https://kafka.apache.org"""
if w(os.path.join(d, "REFERENCES.md"), c_refs): gen+=1

print(f"Lab 01 generated: {gen} files")
