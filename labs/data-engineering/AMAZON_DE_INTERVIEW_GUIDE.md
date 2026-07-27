# Amazon Data Engineer Interview Guide

## Interview Structure
- Recruiter Screen (30 min): DE experience, AWS services, Leadership Principles
- Technical Phone (60 min): SQL + Python + AWS services
- System Design (60 min): Data pipelines, CDC, data quality
- Bar Raiser (60 min): In-depth behavioral probing on 16 LPs
- On-site Loop (4-5 rounds): Coding, Data Modeling, SQL Deep Dive, Manager, Bar Raiser

## Key Topics

### SQL Deep Dive
- Window functions: dedup, ranking, running totals, lag/lead
- Advanced joins: self-joins, anti-joins, semi-joins, cross-joins
- ETL patterns: MERGE, UPSERT, SCD Type 1/2
- Query optimization: EXPLAIN, distribution keys, sort keys
- Redshift-specific: DISTKEY (KEY/ALL/EVEN), SORTKEY (COMPOUND/INTERLEAVED)

### AWS Data Services

**S3:** Storage classes (Standard, Glacier), lifecycle policies, partition layout
**Redshift:** Leader + compute nodes, slices, distribution styles, WLM, columnar storage
**Glue:** Crawlers, Data Catalog, ETL jobs (Python/Spark), Glue Studio
**EMR:** Cluster types (transient/long-running), instance fleets, auto-scaling, Spot
**Kinesis:** Data Streams (shards, retention), Firehose (S3/Redshift delivery), Analytics (Flink)
**Step Functions:** State machines, error handling, retry, parallel branches
**Lambda:** Event triggers (S3, DynamoDB, Kinesis), timeout, cold start

### System Design for Amazon

**E-commerce pipeline design:**
1. Clickstream from website/app via Kinesis
2. Batch processing on EMR (Spark) for product recommendations
3. Storage in S3 data lake (Parquet, partitioned by date)
4. Redshift for BI dashboards (conformed dimensions)
5. Step Functions orchestration with error handling
6. CloudWatch monitoring + SNS alerts

**Data modeling:**
- Star schema for sales analytics
- SCD Type 2 for product/customer changes
- Bridge tables for many-to-many relationships

### Leadership Principles

**Focus on these 6 (most relevant to DE):**
- Customer Obsession: "Data quality fix that improved customer experience"
- Ownership: "Taking charge of a failing pipeline"
- Deliver Results: "Delivering complex data project on time"
- Insist on Highest Standards: "Data quality incident resolution"
- Dive Deep: "Debugging Spark performance issue"
- Have Backbone; Disagree and Commit: "Technical disagreement on architecture"

### Coding (LeetCode Medium)
- Arrays: two pointers, sliding window
- Strings: parsing, pattern matching
- Hash tables: counting, frequency
- Trees: BFS, DFS (less frequent but possible)

## Sample Questions
1. "Design the Amazon product catalog data pipeline"
2. "How would you handle duplicate orders in a pipeline?"
3. "Optimize a slow Redshift query - walk through your process"
4. "Design a real-time inventory management system"
5. "Tell me about a time you disagreed with a technical decision"

## Resources
- Amazon Leadership Principles (memorize all 16)
- AWS re:Invent videos on data analytics
- AWS Well-Architected Framework: Data Analytics pillar
