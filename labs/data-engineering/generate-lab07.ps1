$base = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\data-engineering\07-data-lake"

$files = @{}

$files["README.md"] = @"
# Data Lake

## Overview
A data lake is a centralized repository that stores all structured and unstructured data at any scale, supporting schema-on-read, diverse data types, and the lakehouse architecture.

## Key Concepts
- **Delta Lake**: ACID transactions on data lakes with time travel
- **Apache Iceberg**: Table format for managing large analytic datasets
- **Apache Hudi**: Incremental processing and record-level updates
- **Lakehouse**: Data lake + warehouse capabilities

## Java/Spark Example
```java
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class DataLakeExample {
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
            .appName("DataLakeExample")
            .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
            .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")
            .getOrCreate();

        // Write as Delta table (ACID compliant)
        Dataset<Row> data = spark.read().json("s3://raw-data/events/");
        data.write().format("delta").mode("append")
            .partitionBy("event_date")
            .save("s3://data-lake/bronze/events/");

        // Read historical version (time travel)
        spark.read().format("delta")
            .option("versionAsOf", 5)
            .load("s3://data-lake/bronze/events/")
            .show();
    }
}
```
"@

$files["THEORY.md"] = @"
# Data Lake Theory

## Architecture Layers
### Bronze (Raw)
- Immutable raw data ingestion
- Original format preserved
- Schema-on-read
- Partitioned by ingestion date

### Silver (Cleaned)
- Data cleaned and deduplicated
- Schema enforced
- Joins across datasets
- Quality filtered

### Gold (Aggregated)
- Business-level aggregations
- Denormalized for analytics
- Materialized views
- Ready for consumption

## Table Formats
| Feature | Delta Lake | Iceberg | Hudi |
|---------|-----------|---------|------|
| ACID | Yes | Yes | Yes |
| Time Travel | Yes | Yes | Yes |
| Schema Evolution | Yes | Yes | Yes |
| Partition Evolution | No | Yes | Yes |
| Merge/Upsert | Yes | Yes | Yes |
| Performance | Fast | Fast | Fast |
"@

$files["WHY_IT_EXISTS.md"] = @"
# Why Data Lakes Exist

## The Problem
Traditional data warehouses are expensive, rigid, and can't handle unstructured data. Organizations need a place to store all data types - structured, semi-structured, and unstructured - without upfront schema design.

## Root Cause
- Data volume growing faster than warehouse capacity
- Variety of data formats (JSON, Avro, Parquet, images, logs)
- Schema-on-write requires upfront design
- Need for raw data preservation for ML and data science

## Data Lake Solution
- Cheap object storage (S3, ADLS, GCS)
- Schema-on-read (flexibility)
- Store everything, transform later
- Supports ML, AI, analytics workloads
"@

$files["WHY_IT_MATTERS.md"] = @"
# Why Data Lakes Matter

## Business Impact
- **Cost**: 10-100x cheaper than data warehouses per TB
- **Flexibility**: Store any data type without schema design
- **ML/AI**: Raw data available for feature engineering
- **Data Science**: Self-service access to all data

## Key Metrics
| Metric | Traditional DW | Data Lake |
|--------|---------------|-----------|
| Cost/TB/month | $100-1000 | $10-30 |
| Data types | Structured | All types |
| Schema requirement | On write | On read |
| Scalability | Limited | Unlimited |

## Lakehouse Advantage
Delta Lake + Spark brings warehouse ACID to data lake economics.
"@

$files["HISTORY.md"] = @"
# History of Data Lakes

## Timeline
- **2010**: Hadoop HDFS becomes popular for storing raw data
- **2011**: "Data Lake" term coined by James Dixon (Pentaho)
- **2013**: AWS S3 becomes primary data lake storage
- **2016**: Apache Iceberg created at Netflix
- **2017**: Apache Hudi created at Uber
- **2019**: Delta Lake open-sourced by Databricks
- **2020**: Lakehouse architecture emerges
- **2023**: Apache XTable for interoperability between formats

## Key Insights
- Data warehouses were too expensive and rigid
- Object storage made cheap massive storage possible
- ACID on data lakes was the missing piece (Delta, Iceberg, Hudi)
- Lakehouse unifies data lake and warehouse
"@

$files["MENTAL_MODELS.md"] = @"
# Mental Models for Data Lakes

## 1. The Reservoir
- **Bronze** = Raw reservoir water (untreated)
- **Silver** = Filtered water (clean but basic)
- **Gold** = Bottled water (ready to consume)

## 2. The Library Archive
- **All books stored** regardless of format
- **Catalog** for finding books
- **Different rooms** for different types
- **No membership required** to browse

## 3. The Workshop
- Raw materials on shelves (bronze)
- Work-in-progress on benches (silver)
- Finished products ready for shipping (gold)
- Tools and blueprints available (schemas/metadata)
"@

$files["HOW_IT_WORKS.md"] = @"
# How Data Lakes Work

## Medallion Architecture Implementation
```java
public class MedallionPipeline {

    public void bronzeToSilver() {
        // Bronze: Read raw data
        Dataset<Row> bronze = spark.read().format("delta")
            .load("s3://lake/bronze/events/");

        // Silver: Clean and validate
        Dataset<Row> silver = bronze
            .filter(col("event_type").isNotNull())
            .withColumn("event_date", to_date(col("timestamp")))
            .dropDuplicates("event_id")
            .withColumn("etl_processed_at", current_timestamp());

        silver.write().format("delta")
            .mode("append")
            .partitionBy("event_date")
            .save("s3://lake/silver/events/");
    }

    public void silverToGold() {
        Dataset<Row> silver = spark.read().format("delta")
            .load("s3://lake/silver/events/");

        Dataset<Row> gold = silver
            .groupBy("event_date", "event_type")
            .agg(
                count("*").as("event_count"),
                countDistinct("user_id").as("unique_users")
            );

        gold.write().format("delta")
            .mode("overwrite")
            .save("s3://lake/gold/event_aggregates/");
    }
}
```

## Delta Lake Time Travel
```java
// Read as of specific version
spark.read().format("delta")
    .option("versionAsOf", 25)
    .load("/delta/table");

// Read as of timestamp
spark.read().format("delta")
    .option("timestampAsOf", "2024-01-15")
    .load("/delta/table");

// Vacuum old versions
spark.sql("VACUUM delta.`/delta/table` RETAIN 168 HOURS");
```

## Iceberg Tables
```java
spark.conf().set("spark.sql.catalog.my_catalog", "org.apache.iceberg.spark.SparkCatalog");
spark.conf().set("spark.sql.catalog.my_catalog.type", "hadoop");
spark.conf().set("spark.sql.catalog.my_catalog.warehouse", "s3://lake/iceberg/");

Dataset<Row> df = spark.read().json("input/");
df.writeTo("my_catalog.db.events")
    .partitionedBy(Expressions.days("timestamp"))
    .create();
```
"@

$files["INTERNALS.md"] = @"
# Data Lake Internals

## Delta Lake Transaction Log
```java
/*
Delta Lake uses a transaction log (JSON files in _delta_log/) to track:
- Add/remove file actions
- Schema changes
- Commit info
- Checkpoint files

_log/00000000000000000001.json
_log/00000000000000000002.json
...
_log/00000000000000000010.checkpoint.parquet
*/
```

## File Layout
```
s3://data-lake/
  bronze/
    events/
      event_date=2024-01-01/
        part-00001.snappy.parquet
        part-00002.snappy.parquet
      _delta_log/
        00000000000000000001.json
  silver/
    events/
      event_date=2024-01-01/
        part-00001.snappy.parquet
      _delta_log/
        00000000000000000001.json
```

## Iceberg Manifest Files
```java
/*
Iceberg metadata:
metadata/v1.metadata.json  - Table schema, partition spec, snapshots
metadata/snap-123.avro     - Manifest list (pointing to manifest files)
metadata/manifest-456.avro - Data file locations and stats
*/
```
"@

$files["MATH_FOUNDATION.md"] = @"
# Math Foundation for Data Lakes

## Storage Calculations
```
Raw Capacity = Sum(SourceDataSizes) * Replication
Compressed = RawCapacity / CompressionRatio (3-10x)
Effective = Compressed * (1 + MetadataOverhead)

Bronze: Raw (stays at Raw)
Silver: Compressed (clean, deduplicated)
Gold: Aggregated (much smaller)
```

## Cost Analysis
```
Storage Cost = DataSize * $/GB/month * Replication
Compute Cost = ScanCost * QueryCount + TransformCost * PipelineRuns
Total Cost = StorageCost + ComputeCost + MetadataCost + EGRESS

Data Lake Cost vs Warehouse:
- Storage: 10-100x cheaper
- Compute: Same or higher (more raw data scanned)
- Net: 2-5x cheaper overall
```

## File Size Optimization
```
OptimalFileSize = HDFSBlockSize * 1.5 = ~192MB
FileCount = TotalData / OptimalFileSize
SmallFileProblem = Files < BlockSize * 0.5

Compaction = Merge small files into optimal sizes
Target: Files between 64MB and 1GB
```
"@

$files["VISUAL_GUIDE.md"] = @"
# Visual Guide to Data Lakes

## Medallion Architecture
```
Source Data
   |
   v
+---------------------------------------+
|  BRONZE (Raw)                         |
|  S3://lake/bronze/                    |
|  events/ event_date=YYYY-MM-DD/       |
|  logs/   event_date=YYYY-MM-DD/       |
|  - Immutable, schema-on-read          |
|  - Original format preserved          |
+---------------------------------------+
   |
   | Cleaning, dedup, validation
   v
+---------------------------------------+
|  SILVER (Cleaned)                     |
|  S3://lake/silver/                    |
|  events/ event_date=YYYY-MM-DD/       |
|  - Deduplicated, schema enforced      |
|  - Joined across sources              |
|  - Standardized data types            |
+---------------------------------------+
   |
   | Aggregation, business logic
   v
+---------------------------------------+
|  GOLD (Aggregated)                    |
|  S3://lake/gold/                      |
|  daily_active_users/                  |
|  revenue_by_region/                   |
|  - Business-level metrics             |
|  - Denormalized for BI               |
|  - Read-optimized                     |
+---------------------------------------+
```

## Delta Lake Transaction Log
```
Time -->
v1: INSERT file1.parquet -> add file1
v2: INSERT file2.parquet -> add file2
v3: UPDATE -> remove file1, add file3.parquet
v4: DELETE -> remove file2
v5: COMPACTION -> remove files, add big-file.parquet

Read at v2: file1 + file2
Read at v4: file3 (after update and delete)
```
"@

$files["CODE_DEEP_DIVE.md"] = @"
# Code Deep Dive: Data Lake Operations

## Delta Lake Operations
```java
public class DeltaLakeOperations {

    // Upsert (Merge)
    public void upsertEvents(Dataset<Row> updates, String tablePath) {
        updates.createOrReplaceTempView("updates");
        spark.sql(String.format("""
            MERGE INTO delta.`%s` t
            USING updates s ON t.event_id = s.event_id
            WHEN MATCHED THEN UPDATE SET *
            WHEN NOT MATCHED THEN INSERT *
            """, tablePath));
    }

    // Time travel query
    public Dataset<Row> readAsOf(String tablePath, long version) {
        return spark.read().format("delta")
            .option("versionAsOf", version)
            .load(tablePath);
    }

    // Vacuum (clean up old files)
    public void vacuum(String tablePath, int retentionHours) {
        spark.sql(String.format("VACUUM delta.`%s` RETAIN %d HOURS",
            tablePath, retentionHours));
    }

    // Describe history
    public Dataset<Row> getHistory(String tablePath) {
        return spark.sql(String.format("DESCRIBE HISTORY delta.`%s`", tablePath));
    }

    // Optimize (compact small files)
    public void optimizeTable(String tablePath) {
        spark.sql(String.format("OPTIMIZE delta.`%s`", tablePath));
    }

    // Z-order clustering
    public void zorderBy(String tablePath, String... columns) {
        String cols = String.join(", ", columns);
        spark.sql(String.format("OPTIMIZE delta.`%s` ZORDER BY (%s)",
            tablePath, cols));
    }
}
```

## Schema Evolution
```java
public class SchemaEvolution {
    public void evolveSchema() {
        // Auto-merge schema
        spark.sql("SET spark.databricks.delta.schema.autoMerge.enabled = true");

        // Manual schema evolution
        spark.sql(String.format("""
            ALTER TABLE delta.`%s` ADD COLUMNS (new_column STRING AFTER existing_column)
            """, tablePath));
    }
}
```

## Iceberg API
```java
public class IcebergOperations {
    public void createIcebergTable() {
        spark.sql("CREATE TABLE prod.db.events (" +
            "event_id BIGINT, event_type STRING, ts TIMESTAMP) " +
            "USING iceberg " +
            "PARTITIONED BY (days(ts))");
    }

    public void incrementalRead() {
        Dataset<Row> increment = spark.read()
            .format("iceberg")
            .option("snapshot-id", 123456789L)
            .load("prod.db.events");
    }
}
```
"@

$files["STEP_BY_STEP.md"] = @"
# Step-by-Step Data Lake Setup

## Step 1: Choose Storage
- AWS S3, Azure ADLS, Google GCS, or HDFS
- Set up bucket/container structure
- Implement lifecycle policies

## Step 2: Set Up Table Format
```java
SparkSession.builder()
    .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
    .config("spark.sql.catalog.spark_catalog",
        "org.apache.spark.sql.delta.catalog.DeltaCatalog")
    .getOrCreate();
```

## Step 3: Bronze Layer Ingestion
```java
Dataset<Row> raw = spark.read().json("s3://source/");
raw.write().format("delta").mode("append")
    .partitionBy("ingestion_date")
    .save("s3://lake/bronze/data/");
```

## Step 4: Silver Layer Processing
```java
Dataset<Row> bronze = spark.read().format("delta")
    .load("s3://lake/bronze/data/");
Dataset<Row> silver = bronze
    .dropDuplicates("id")
    .filter(col("status").isNotNull())
    .withColumn("etl_ts", current_timestamp());
silver.write().format("delta").mode("append")
    .save("s3://lake/silver/data/");
```

## Step 5: Gold Layer Aggregation
```java
Dataset<Row> gold = spark.read().format("delta")
    .load("s3://lake/silver/data/")
    .groupBy("dimension")
    .agg(sum("metric").as("total"));
gold.write().format("delta").mode("overwrite")
    .save("s3://lake/gold/aggregates/");
```

## Step 6: Set Up Catalog
```sql
CREATE DATABASE IF NOT EXISTS my_catalog.bronze;
CREATE DATABASE IF NOT EXISTS my_catalog.silver;
CREATE DATABASE IF NOT EXISTS my_catalog.gold;
```
"@

$files["COMMON_MISTAKES.md"] = @"
# Common Data Lake Mistakes

## 1. Data Lake = Data Swamp
```java
// WRONG - no organization, no catalog
spark.read().json("s3://data/some-random-path/");

// RIGHT - organized medallion architecture
spark.table("bronze.events");
spark.table("silver.events");
spark.table("gold.daily_metrics");
```

## 2. No Partitioning
```java
// WRONG - no partitioning
data.write().format("delta").save("s3://lake/data/");

// RIGHT - partitioned
data.write().format("delta")
    .partitionBy("year", "month", "day")
    .save("s3://lake/data/");
```

## 3. Too Many Small Files
```java
// WRONG - thousands of tiny files
spark.conf().set("spark.sql.files.maxPartitionBytes", "1MB");

// RIGHT - optimal file sizes
spark.conf().set("spark.sql.files.maxPartitionBytes", "128MB");
// Run compaction
spark.sql("OPTIMIZE delta.`/path`");
```

## 4. No Data Lifecycle
```java
// WRONG - keep everything forever
// Costs grow unbounded

// RIGHT - implement lifecycle
spark.sql("VACUUM delta.`/bronze` RETAIN 30 DAYS");
```
"@

$files["DEBUGGING.md"] = @"
# Debugging Data Lakes

## Common Issues

### Delta Lake Transaction Log Conflicts
```java
// Conflicting writes to same Delta table
// Multiple writers without proper isolation

// Fix: Use separate partition directories
data.write().format("delta").mode("append")
    .partitionBy("event_date")
    .save("s3://lake/events/");
```

### Iceberg Metadata Issues
```java
// Large metadata overhead
// Fix: Regular metadata compaction
spark.sql("CALL iceberg.system.rewrite_manifests('db.table')");
```

### Partition Pruning Not Working
```java
// Check if predicates are pushed down
spark.read().format("delta")
    .load("s3://lake/bronze/events/")
    .filter("event_date = '2024-01-01'")
    .explain("formatted");
// Look for: PushedFilters, PartitionFilters
```
"@

$files["REFACTORING.md"] = @"
# Refactoring Data Lakes

## From Ad-hoc to Medallion
```java
// BEFORE: Random paths, no structure
spark.read().parquet("s3://data/sales_march.parquet");
spark.read().json("s3://data/export_20240301.json");

// AFTER: Structured medallion
spark.table("bronze.sales");
spark.table("bronze.export_logs");
```

## From No ACID to Delta Lake
```java
// BEFORE: Parquet files with no transactions
data.write().mode("overwrite").parquet("s3://data/");

// AFTER: Delta Lake with ACID
data.write().format("delta").mode("append")
    .save("s3://lake/bronze/sales/");
```
"@

$files["PERFORMANCE.md"] = @"
# Data Lake Performance

## Optimization Techniques

### Compaction
```java
// Merge small files
spark.sql("OPTIMIZE delta.`/path`");
spark.sql("OPTIMIZE delta.`/path` WHERE date >= '2024-01-01'");

// Z-order clustering
spark.sql("OPTIMIZE delta.`/path` ZORDER BY (customer_id, product_id)");
```

### Partition Pruning
```java
// Filter on partition columns for performance
Dataset<Row> result = spark.read().format("delta")
    .load("s3://lake/bronze/events/")
    .filter("event_date >= '2024-01-01' AND event_date < '2024-02-01'");
// Only scans January 2024 partitions
```

### File Sizing
```java
// Target: 64MB - 1GB files
spark.conf().set("spark.sql.files.maxPartitionBytes", "134217728"); // 128MB
spark.conf().set("spark.sql.files.openCostInBytes", "134217728");

// Optimal file count
int fileCount = totalDataSize / targetFileSize;
spark.conf().set("spark.sql.shuffle.partitions", fileCount);
```
"@

$files["SECURITY.md"] = @"
# Data Lake Security

## Access Control
```java
// Bucket policies (AWS S3 example)
{
    "Effect": "Allow",
    "Principal": {"AWS": "arn:aws:iam::account:role/DataEngineer"},
    "Action": ["s3:GetObject", "s3:ListBucket"],
    "Resource": ["arn:aws:s3:::lake/bronze/*", "arn:aws:s3:::lake"]
}
```

## Column-Level Security
```java
// Create views with masked columns
spark.sql("CREATE VIEW silver.employees_safe AS " +
    "SELECT id, name, department, " +
    "  CONCAT(LEFT(ssn, 4), '***-****') as ssn_masked, " +
    "  CONCAT(LEFT(email, 3), '***') as email_masked " +
    "FROM silver.employees");
```

## Encryption
```java
// Server-side encryption (S3 SSE-S3 or SSE-KMS)
// Client-side encryption before writing
data.write().format("delta")
    .option("delta.autoOptimize.optimizeWrite", "true")
    .save("s3://lake/silver/pii/");
```

## Audit
```java
// Delta Lake table history
spark.sql("DESCRIBE HISTORY delta.`/lake/silver/employees`");
// Shows who, what, when for all changes
```
"@

$files["ARCHITECTURE.md"] = @"
# Data Lake Architecture

## Lakehouse Architecture
```
+------------------------------------------+
|       BI Tools & Analytics               |
|  Tableau | Power BI | Databricks SQL     |
+------------------------------------------+
|        Catalog & Governance               |
|  Unity Catalog | AWS Glue | Hive Meta    |
+------------------------------------------+
|          Gold Layer (Aggregated)          |
|  Sales Summary | Customer 360 | KPIs      |
+------------------------------------------+
|          Silver Layer (Cleaned)           |
|  Events | Users | Transactions            |
+------------------------------------------+
|          Bronze Layer (Raw)               |
|  Logs | API Events | DB Snapshots         |
+------------------------------------------+
|         Storage (S3 / ADLS / GCS)         |
|  Parquet / Delta / Iceberg / Hudi        |
+------------------------------------------+
```
"@

$files["EXERCISES.md"] = @"
# Data Lake Exercises

## Exercise 1: Bronze Layer Ingestion
Set up a Delta Lake table that ingests JSON events from S3 with proper partitioning.

## Exercise 2: Silver Layer Processing
Clean and deduplicate the bronze data to create a silver layer.

## Exercise 3: Time Travel
Implement a point-in-time query using Delta Lake time travel.

## Exercise 4: Merge/Upsert
Implement upsert logic to handle late-arriving data in Delta Lake.

## Exercise 5: Iceberg Tables
Create and query an Iceberg table with partition evolution.
"@

$files["QUIZ.md"] = @"
# Data Lake Quiz

## Question 1
What are the three layers in the medallion architecture?
- A) Raw, Processed, Final
- B) Bronze, Silver, Gold
- C) Input, Transform, Output
- D) Stage, Clean, Aggregate

## Question 2
Which feature distinguishes Delta Lake from plain Parquet?
- A) Columnar storage
- B) ACID transactions
- C) Compression
- D) Schema support

## Question 3
What is a lakehouse?
- A) A data lake without structure
- B) Data lake + warehouse capabilities
- C) Only structured data
- D) A type of database

## Answer Key
1: B, 2: B, 3: B
"@

$files["FLASHCARDS.md"] = @"
# Data Lake Flashcards

## Card 1
**Front**: What is a data lake?
**Back**: A centralized repository storing all data (structured and unstructured) at any scale with schema-on-read.

## Card 2
**Front**: What is the medallion architecture?
**Back**: Bronze (raw), Silver (cleaned), Gold (aggregated) - a multi-hop data organization pattern.

## Card 3
**Front**: What is Delta Lake?
**Back**: An open-source storage layer bringing ACID transactions to data lakes with time travel and schema evolution.

## Card 4
**Front**: What is time travel in Delta Lake?
**Back**: The ability to query previous versions of a table using version numbers or timestamps.

## Card 5
**Front**: What is a lakehouse?
**Back**: A data architecture combining data lake flexibility with warehouse ACID compliance and performance.
"@

$files["INTERVIEW.md"] = @"
# Data Lake Interview Questions

## Beginner
**Q**: What is the difference between a data lake and a data warehouse?
**A**: Data lakes store all data types (structured, semi-structured, raw) with schema-on-read. Data warehouses store processed, structured data with schema-on-write. Lakes are cheaper but require more processing.

## Intermediate
**Q**: Explain the medallion architecture.
**A**: Bronze (raw ingest), Silver (cleaned/validated), Gold (aggregated/business-ready). Each layer adds value and structure.

## Advanced
**Q**: Compare Delta Lake, Iceberg, and Hudi.
**A**: Delta Lake (Databricks) - best Spark integration, simple transaction log. Iceberg (Netflix) - best for partition evolution, multi-engine support. Hudi (Uber) - best for incremental processing and upserts at record level.
"@

$files["REFLECTION.md"] = @"
# Data Lake Reflection

## Key Learnings
- Data lakes enable storing all data types cheaply
- Medallion architecture brings structure to data lakes
- Delta/Iceberg/Hudi add ACID to object storage
- Lakehouse is the convergence of data lake and warehouse

## Questions to Explore
1. Which table format is best for your use case?
2. How do you handle schema drift in production?
3. What governance practices prevent data swamps?
"@

$files["REFERENCES.md"] = @"
# Data Lake References

## Books
- "The Enterprise Big Data Lake" by Alex Gorelik
- "Delta Lake: The Definitive Guide" by various
- "Data Lakehouse in Action" by Pradeep Menon

## Documentation
- Delta Lake: https://delta.io
- Apache Iceberg: https://iceberg.apache.org
- Apache Hudi: https://hudi.apache.org

## Papers
- "Delta Lake: High-Performance ACID Table Storage over Cloud Object Stores"
- "Iceberg: A Modern Table Format for Big Data"
- "Hudi: Incremental Processing on Hadoop"
"@

foreach ($file in $files.Keys) {
    $content = $files[$file]
    $path = Join-Path $base $file
    $content | Out-File -FilePath $path -Encoding utf8
    Write-Host "Created: $path"
}
Write-Host "Lab 07 complete"
