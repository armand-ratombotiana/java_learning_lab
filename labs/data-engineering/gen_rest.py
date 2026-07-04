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

# ===== LAB 02: ETL =====
d = os.path.join(BASE, "02-etl-processes")
for fname, c in {
    "THEORY.md": """# ETL Theory

## ELT vs ETL
| ETL | ELT |
|-----|-----|
| Transform before load | Load then transform |
| Staging area | Warehouse compute |
| Lower volume | Higher volume |
| Spark/Spring Batch | dbt/Snowflake |

## Extraction Strategies
- **Full**: Entire source each run
- **Incremental**: Changed data only
- **CDC**: Capture inserts, updates, deletes""",
    "WHY_IT_MATTERS.md": """# Why ETL Matters

- **Data Quality**: Clean, reliable data for analysis
- **Performance**: Optimized for query performance
- **Governance**: Audit trails and lineage
- **Automation**: Scheduled jobs eliminate manual work""",
    "HISTORY.md": """# History of ETL

- **1970s**: COBOL for data movement
- **1990s**: Commercial ETL (Informatica PowerCenter)
- **2000s**: Open-source (Pentaho, Talend)
- **2010s**: Cloud ETL (Fivetran, Stitch, Airbyte)
- **2020s**: ELT paradigm with dbt""",
    "MENTAL_MODELS.md": """# Mental Models

## Assembly Line
Raw materials -> each station adds value -> quality checks -> finished product

## Water Filtration
Intake (extract) -> Sedimentation (clean) -> Filtration (validate) -> Distribution (load)

## Kitchen Model
Shopping=Extract, Prepping=Clean, Cooking=Transform, Plating=Load""",
    "HOW_IT_WORKS.md": """# How ETL Works

## Extraction
```java
@Component
public class JdbcExtractor {
    public List<Order> extractIncremental(LocalDateTime lastRun) {
        return jdbc.query("SELECT * FROM orders WHERE updated_at > ?",
            new Object[]{Timestamp.valueOf(lastRun)}, new OrderRowMapper());
    }
}
```

## Transformation
```java
public Dataset<Row> applyBusinessRules(Dataset<Row> data) {
    return data.withColumn("segment",
        when(col("total").$greater(10000), "VIP").otherwise("STANDARD"));
}
```

## Load (Upsert)
```sql
MERGE INTO target t USING staging s ON t.id = s.id
WHEN MATCHED THEN UPDATE SET *
WHEN NOT MATCHED THEN INSERT *
```""",
    "INTERNALS.md": """# ETL Internals

## Metadata Management
Track job runs, status, record counts in audit tables.

## Error Handling
Retry logic (3 attempts with exponential backoff), dead letter queue for failures.

## Staging Area
Temporary storage for raw data before transformation, enables recovery.""",
    "MATH_FOUNDATION.md": """# Math Foundation

## Data Volume
Raw = Sum(source sizes), Intermediate = Raw + enrichment, Final = After aggregation
Compression ratio: 3-10x with Parquet

## Complexity
O(n): row-wise ops, O(n log n): sorts, O(n*m): joins

## Parallelism
Optimal = min(SourcePartitions, TargetPartitions, Cores*2)""",
    "VISUAL_GUIDE.md": """# Visual Guide

## ETL Flow
```
[Source DB] -> [Extract] -> [Staging] -> [Transform] -> [Load] -> [Warehouse]
```

## Transform Details
```
Raw Stage      Clean Stage       Enriched Stage
id:string      id:string         id:string
name:string -> name:string    -> name:string
amount:string  amount:decimal    amount:decimal
raw_data:json                   category:string
```""",
    "CODE_DEEP_DIVE.md": """# Code Deep Dive: Spring Batch ETL

## Job Configuration
```java
@Configuration
public class EtlJobConfig {
    @Bean
    public Job etlJob(JobRepository repo, Step extract, Step transform, Step load) {
        return new JobBuilder("customerETL", repo)
            .start(extract).next(transform).next(load)
            .listener(new EtlJobListener()).build();
    }
}
```

## Item Processor
```java
public class CustomerProcessor implements ItemProcessor<Customer, EnrichedCustomer> {
    public EnrichedCustomer process(Customer c) {
        EnrichedCustomer e = new EnrichedCustomer();
        e.setCustomerId(c.getId());
        e.setFullName(c.getFirstName() + " " + c.getLastName());
        e.setSegment(c.getTotalOrders() > 50 ? "LOYAL" : "REGULAR");
        return e;
    }
}
```""",
    "STEP_BY_STEP.md": """# Step-by-Step

1. **Design**: Map sources, define target schema, identify transforms
2. **Setup**: Add Spring Batch and Spark dependencies
3. **Configure**: DataSource beans for source and target
4. **Transform**: Spark DataFrame operations for business rules
5. **Load**: JdbcBatchItemWriter for target table
6. **Schedule**: @Scheduled cron or Airflow DAG""",
    "COMMON_MISTAKES.md": """# Common Mistakes

1. **No Incremental Logic**: Always full refresh (expensive for large data)
2. **Ignoring Data Types**: Everything as string (loses precision)
3. **No Error Handling**: Missing retry/checkpoint mechanisms
4. **No Quality Checks**: Trusting source data without validation""",
    "DEBUGGING.md": """# Debugging

## Type Mismatches
```java
sourceData.printSchema();
spark.conf().set("spark.sql.ansi.enabled", "false");
```

## Null Analysis
```java
long nullCount = sourceData.filter(col("key").isNull()).count();
```

## Connection Testing
```java
try { jdbcTemplate.queryForObject("SELECT 1", Integer.class); }
catch (Exception e) { log.error("DB unavailable"); }""",
    "REFACTORING.md": """# Refactoring ETL

## Before: Duplicated Logic
```java
public void runEtl1() { /* 200 lines */ }
public void runEtl2() { /* 190 similar lines */ }
```

## After: Abstract Base Class
```java
@Component
public abstract class BaseEtlJob {
    protected abstract Dataset<Row> extract();
    protected abstract Dataset<Row> transform(Dataset<Row>);
    protected abstract void load(Dataset<Row>);
    public void execute() { load(transform(extract())); }
}
```""",
    "PERFORMANCE.md": """# Performance

## Partitioning
```java
data.repartition(col("country")).sortWithinPartitions("order_date");
```

## Caching
```java
spark.sqlContext().cacheTable("dim_country");
```

## Broadcast Joins
```java
largeDF.join(smallDF.hint("broadcast"), "key");
```

## Adaptive Query
```
spark.sql.adaptive.enabled=true
spark.sql.adaptive.coalescePartitions.enabled=true
```""",
    "SECURITY.md": """# Security

## Column Encryption
```java
public Dataset<Row> encryptPII(Dataset<Row> data) {
    return data.withColumn("ssn", encryptColumn(col("ssn"), key));
}
```

## Access Control
```java
@PreAuthorize("hasRole('ETL_ADMIN')")
public void runSensitiveEtl() {}
```

## Audit Logging
Log all ETL executions with user, job, records, status, timestamp.""",
    "ARCHITECTURE.md": """# Architecture

## Layers
```
[Source Systems] -> [Staging Layer] -> [Transform Engine] -> [Target Layer]
```

## Spring Batch Flow
```
Job Launcher -> Job -> Step -> ItemReader -> ItemProcessor -> ItemWriter
```""",
    "EXERCISES.md": """# Exercises

1. File-based ETL (CSV to PostgreSQL)
2. Incremental load using timestamps
3. Data quality validation rules
4. Multi-source ETL (DB + API + file)
5. Error recovery with retry and DLQ""",
    "QUIZ.md": """# Quiz

1. ETL stands for? B) Extract, Transform, Load
2. Best load for history? B) SCD Type 2
3. ELT advantage? B) Warehouse compute power""",
    "FLASHCARDS.md": """# Flashcards

1. **ETL**: Extract, Transform, Load
2. **ELT**: Load raw, transform in warehouse
3. **SCD**: Slowly Changing Dimension for history
4. **Incremental Load**: Only changed data
5. **Staging Area**: Buffer zone for raw data""",
    "INTERVIEW.md": """# Interview Questions

## Beginner
Q: Purpose of staging area?
A: Buffer zone for raw data before transformation, enables error recovery.

## Senior
Q: Design ETL for 50TB daily data?
A: Partitioned ingestion, Spark distributed transform, Delta Lake ACID, incremental CDC, multi-hop architecture.""",
    "REFLECTION.md": """# Reflection

- ETL is the foundation of data warehousing
- Incremental processing is critical for scaling
- Quality checks should be built into every stage
- Monitoring is as important as ETL logic""",
}.items():
    if w(os.path.join(d, fname), c): gen += 1

print(f"Lab 02: {gen} files")

# ===== LAB 03: DATA WAREHOUSING =====
d = os.path.join(BASE, "03-data-warehousing")
for fname, c in {
    "THEORY.md": """# Data Warehousing Theory

## Dimensional Modeling
- **Fact Tables**: Measures, metrics, foreign keys
- **Dimension Tables**: Descriptive attributes, hierarchies
- **Grain**: Level of detail in fact table

## Schema Types
- **Star**: Denormalized dimensions around fact
- **Snowflake**: Normalized dimensions

## OLAP Operations
Slice, Dice, Drill-down, Roll-up, Pivot""",
    "WHY_IT_MATTERS.md": """# Why Data Warehousing Matters

- **Single Source of Truth**: Consistent definitions
- **Query Performance**: Sub-second analytical queries
- **Historical Analysis**: Years of trend data
- **Data Governance**: Centralized security and lineage""",
    "HISTORY.md": """# History

- **1960s**: Decision Support Systems concept
- **1988**: "Data Warehouse" term coined by Inmon
- **1996**: Kimball's dimensional modeling
- **2000s**: Enterprise DW (Teradata, Netezza)
- **2012**: Cloud DW (Redshift, BigQuery, Snowflake)
- **2019**: Lakehouse paradigm""",
    "INTERNALS.md": """# Warehouse Internals

## Columnar Storage
Data organized by column for better compression and faster aggregation.

## Query Optimization
- Predicate pushdown
- Partition pruning
- Materialized views
- Join order optimization""",
    "MATH_FOUNDATION.md": """# Math Foundation

## Star Join Cost
Query = FactScan + DimLookups
FactScan = RowCount * RowSize / IORate

## Storage
Raw = sum(row sizes) * count * repl
Columnar compression = 3-10x reduction
Star schema: Fact ~60-80% of total""",
    "VISUAL_GUIDE.md": """# Visual Guide

## Star Schema
```
[dim_customer] <-- [fact_sales] --> [dim_product]
                        |
                   [dim_date]    [dim_store]
```""",
    "CODE_DEEP_DIVE.md": """# Code Deep Dive: Star Schema Build

## Dimension Table
```java
spark.sql("CREATE TABLE dim_customer (customer_key BIGINT IDENTITY, "
    + "customer_id VARCHAR(50), name VARCHAR(200), email VARCHAR(200), "
    + "effective_date DATE, is_current BOOLEAN) USING DELTA");
```

## SCD Type 2
```java
spark.sql("MERGE INTO dim_customer t USING src s ON t.customer_id = s.customer_id "
    + "WHEN MATCHED AND t.name != s.name THEN UPDATE SET "
    + "t.end_date = CURRENT_DATE, t.is_current = FALSE "
    + "WHEN NOT MATCHED THEN INSERT *");
```""",
    "STEP_BY_STEP.md": """# Step-by-Step

1. **Source Analysis**: Identify business processes, define grain
2. **Schema Design**: Create dimension and fact tables
3. **Build ETL**: Load dimensions, establish FK, load facts
4. **Aggregations**: Pre-compute common metrics
5. **Access Control**: Grant permissions to roles""",
    "COMMON_MISTAKES.md": """# Common Mistakes

1. **Wrong Grain**: Too coarse or too fine
2. **No Conformed Dimensions**: Same dimension different definitions across marts
3. **Over-Normalization**: Too many joins in snowflake
4. **Ignoring History**: Overwriting instead of SCD Type 2""",
    "DEBUGGING.md": """# Debugging

## Slow Queries
```sql
EXPLAIN ANALYZE SELECT region, SUM(amount) FROM fact_sales GROUP BY region;
```

## Orphan Facts
```sql
SELECT COUNT(*) FROM fact_sales f LEFT JOIN dim_customer c
ON f.customer_key = c.customer_key WHERE c.customer_key IS NULL;
```""",
    "REFACTORING.md": """# Refactoring

## Before: Flat Table
```sql
CREATE TABLE sales_wide (transaction_id, customer_name, product_name, ...);
```
Problems: Redundancy, update anomalies, high storage.

## After: Star Schema
```sql
CREATE TABLE dim_customer (customer_key, name, ...);
CREATE TABLE fact_sales (customer_key, product_key, ..., amount);
```""",
    "PERFORMANCE.md": """# Performance

## Materialized Views
```java
spark.sql("CREATE MATERIALIZED VIEW mv_monthly AS "
    + "SELECT product_key, DATE_TRUNC('month', date) as month, "
    + "SUM(amount) as revenue FROM fact_sales GROUP BY product_key, month");
```

## Partition Pruning
```sql
SELECT ... FROM fact_sales WHERE year=2024 AND month=6;
```""",
    "SECURITY.md": """# Security

- Role-based access (analyst, etl_admin, reporting)
- Column-level security (mask sensitive fields)
- Audit logging for all queries
- Encryption at rest""",
    "ARCHITECTURE.md": """# Architecture

## Modern Warehouse
```
[Consumption Layer] -> [Access Layer] -> [Compute Layer] -> [Storage Layer] -> [Ingestion Layer]
```""",
    "EXERCISES.md": """# Exercises

1. Design star schema for e-commerce
2. Implement SCD Type 2 for customer addresses
3. Create materialized views for monthly sales
4. Optimize slow query using explain plans
5. Build OLAP API for drill-down operations""",
}.items():
    if w(os.path.join(d, fname), c): gen += 1

print(f"Lab 03: {gen} files")

print(f"\nTotal generated in this run: {gen}")
