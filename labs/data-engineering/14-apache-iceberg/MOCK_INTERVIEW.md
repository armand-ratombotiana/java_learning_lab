# Mock Interview: Apache Iceberg (14-apache-iceberg)

## Scenario: Adopt Apache Iceberg for open lakehouse format
Your company wants to adopt an open table format to avoid vendor lock-in and enable multi-engine queries (Spark, Trino, Flink). You recommend Apache Iceberg.

### Time: 45 minutes | Difficulty: Hard

---

## Part 1: Iceberg Architecture (15 min)

**Iceberg table structure (three-layer metadata):**

```
Table: sales_db.fact_orders
Location: s3://warehouse/sales_db/fact_orders/

s3://warehouse/sales_db/fact_orders/
├── metadata/
│   ├── v1.metadata.json           # Current table metadata (schema, partition spec, snapshots)
│   ├── v2.metadata.json           # Updated metadata (new snapshot)
│   ├── snap-123456789-1.avro      # Snapshot: list of manifest files
│   └── snap-123456790-2.avro      # New snapshot (append)
├── data/
│   ├── order_date=2024-01-01/
│   │   ├── 00000-0-xxx.parquet    # Data file 1
│   │   └── 00001-0-xxx.parquet    # Data file 2
│   ├── order_date=2024-01-02/
│   │   └── 00000-0-xxx.parquet
│   └── ...
└── metadata/
    └── 00000-0-xxx.metadata.json  # Manifest file (references data files)
```

**Layer responsibilities:**

| Layer | File | Purpose | Content |
|-------|------|---------|---------|
| **Metadata file** | `.metadata.json` | Table definition | Schema, partition spec, sort order, snapshot references, table properties |
| **Manifest list** | `snap-*.avro` | Snapshot boundary | List of manifest files with partition stats (min/max), file count, added/deleted files |
| **Manifest file** | `*.avro` | File listing | List of data files with column-level stats (min/max, null counts), file path, format, partition data |
| **Data file** | `.parquet/.orc/.avro` | Actual data | Columnar data, with min/max/null statistics in footer metadata |

**Query planning with Iceberg:**
1. Load current metadata file (v2.metadata.json)
2. Find current snapshot (v2) → read manifest list
3. For each manifest in list, check partition stats against WHERE clause
4. Prune manifests that don't match (partition elimination)
5. For matching manifests, read data file metadata (column stats)
6. Further prune data files using column-level min/max stats
7. Only scan remaining data files
8. Result: instead of listing 10K files, Iceberg scans 100 manifests → 20 data files

---

## Part 2: Partition Evolution (10 min)

**Iceberg supports partition evolution without rewriting data:**

```sql
-- Step 1: Create table with monthly partition
CREATE TABLE sales_db.fact_orders (
    order_id BIGINT,
    customer_id INT,
    order_date DATE,
    amount DECIMAL(18,2)
)
USING ICEBERG
PARTITIONED BY (month(order_date));

-- Step 2: After 6 months, we need daily partitions
-- Iceberg allows partition spec evolution
ALTER TABLE sales_db.fact_orders
ADD PARTITION FIELD day(order_date);

-- Step 3: Old data is still partitioned by month
-- New data is partitioned by day
-- Query engine reads old manifests with month spec, new manifests with day spec
-- No data rewrite needed for historical data!
```

**How it works internally:**
- Each partition spec gets a unique ID
- Old data files reference spec ID 0 (monthly)
- New data files reference spec ID 1 (daily)
- Metadata tracks both specs
- Query planning checks both manifest groups and applies appropriate partition pruning

**Other evolution operations:**
```sql
-- Add partition field
ALTER TABLE t ADD PARTITION FIELD bucket(16, customer_id);

-- Drop partition field (data files remain, but no longer partitioned)
ALTER TABLE t DROP PARTITION FIELD month(order_date);

-- Evolve sort order
ALTER TABLE t WRITE ORDERED BY (order_date DESC, customer_id ASC);
```

**Comparison with other formats:**
| Format | Partition Evolution | Requires Rewrite |
|--------|-------------------|-----------------|
| Iceberg | Yes (new+old specs) | No |
| Delta Lake | No (must rewrite) | Yes |
| Hudi | Partial (clustering) | Yes |

---

## Part 3: Performance & Catalogs (10 min)

**File pruning with Iceberg:**
```sql
-- Without partition pruning:
SELECT COUNT(*) FROM fact_orders;  -- Scans all files

-- With partition pruning:
SELECT COUNT(*) FROM fact_orders
WHERE order_date >= '2024-01-01'  -- Prunes by partition
  AND customer_id = 12345;         -- Prunes by column stats (min/max in manifest)
```

**Iceberg catalogs comparison:**

| Catalog | Description | Best For | Multi-engine |
|---------|-------------|----------|------------|
| Hive | Traditional Hive Metastore integration | Existing Hive users | Spark, Trino, Flink, Hive |
| Nessie | Git-like versioning (branches, tags) | Data engineering with version control | Spark, Flink, Trino |
| JDBC | Relational database catalog (Postgres, MySQL) | Lightweight, no external deps | Spark, Flink, Trino |
| REST | REST API catalog (standards-based) | Multi-engine in cloud environments | Spark, Flink, Trino, Dremio |
| AWS Glue | Glue Data Catalog native | AWS-native environments | Spark, Trino (via EMR) |
| DynamoDB | AWS DynamoDB-backed | Serverless, highly available | Spark (AWS EMR) |

**Recommendation: REST Catalog**
```python
# Spark configuration for REST catalog
spark.conf.set("spark.sql.catalog.iceberg_catalog", "org.apache.iceberg.spark.SparkCatalog")
spark.conf.set("spark.sql.catalog.iceberg_catalog.type", "rest")
spark.conf.set("spark.sql.catalog.iceberg_catalog.uri", "https://iceberg-catalog.company.com/api/v1")
spark.conf.set("spark.sql.catalog.iceberg_catalog.warehouse", "s3://warehouse/")
spark.conf.set("spark.sql.catalog.iceberg_catalog.io-impl", "org.apache.iceberg.aws.s3.S3FileIO")

# Trino configuration
# catalog/iceberg.properties:
# connector.name=iceberg
# iceberg.catalog.type=rest
# iceberg.rest-catalog.uri=https://iceberg-catalog.company.com/api/v1
# iceberg.rest-catalog.warehouse=s3://warehouse/
```

---

## Part 4: Compaction & Time Travel (10 min)

**Compaction strategy:**

```python
# Iceberg's rewrite_data_files for compaction
from pyspark.sql import SparkSession

spark = SparkSession.builder \
    .config("spark.sql.extensions", "org.apache.iceberg.spark.extensions.IcebergSparkSessionExtensions") \
    .config("spark.sql.catalog.local", "org.apache.iceberg.spark.SparkCatalog") \
    .config("spark.sql.catalog.local.type", "hadoop") \
    .config("spark.sql.catalog.local.warehouse", "s3://warehouse/") \
    .getOrCreate()

# Rewrite small files into larger ones (default target: 512MB)
spark.sql("""
    CALL local.system.rewrite_data_files(
        table => 'sales_db.fact_orders',
        options => map(
            'target-file-size-bytes', '536870912',  -- 512MB
            'min-file-count', '10',
            'rewrite-all', 'false'
        )
    )
""")

# Expire old snapshots to reclaim storage
spark.sql("""
    CALL local.system.expire_snapshots(
        table => 'sales_db.fact_orders',
        older_than => TIMESTAMP '2024-01-01 00:00:00',
        retain_last => 10
    )
""")

# Remove orphan files (files not referenced by any snapshot)
spark.sql("""
    CALL local.system.remove_orphan_files(
        table => 'sales_db.fact_orders',
        older_than => TIMESTAMP '2024-01-01 00:00:00'
    )
""")
```

**Compaction schedule:**
| Table | Frequency | Target File Size | Trigger |
|-------|-----------|-----------------|---------|
| fact_orders (high volume) | Hourly | 512MB | After each streaming micro-batch |
| dim_customer (low volume) | Daily | 128MB | After daily full refresh |
| raw_events (append-only) | Every 6 hours | 256MB | Cumulative after 6 hours |

**Time travel (snapshot isolation):**
```sql
-- Read table as of a specific snapshot
SELECT * FROM sales_db.fact_orders
  FOR SYSTEM_VERSION AS OF 123456789;

-- Read table as of a specific timestamp
SELECT * FROM sales_db.fact_orders
  FOR SYSTEM_TIME AS OF '2024-01-15 10:00:00';

-- Rollback to previous version
CALL local.system.rollback_to_snapshot('sales_db.fact_orders', 123456789);

-- List all snapshots
SELECT * FROM sales_db.fact_orders.snapshots;
```

---

## Follow-up Questions

**Iceberg vs Delta Lake vs Hudi:**

| Feature | Iceberg | Delta Lake | Hudi |
|---------|---------|-----------|------|
| Open format | Yes (Apache project) | Yes (Linux Foundation) | Yes (Apache project) |
| Partition evolution | Yes | No (rewrite required) | No (clustering alternative) |
| Multi-engine | Spark, Flink, Trino, Hive, Dremio | Primarily Spark | Primarily Spark, Flink |
| Incremental queries | No (requires custom) | Via Change Data Feed | Native |
| Schema evolution | Yes | Yes | Yes |
| Hidden partitioning | Yes (partition by expression) | No | No |
| File format | Parquet, ORC, Avro | Parquet | Parquet, Avro |
| Best for | Open multi-engine lakehouse | Databricks ecosystem | High-volume UPSERT |

**MERGE INTO with Iceberg:**
```sql
MERGE INTO sales_db.fact_orders AS t
USING (SELECT * FROM staging.source_orders) AS s
ON t.order_id = s.order_id
WHEN MATCHED THEN UPDATE SET
    t.amount = s.amount,
    t.status = s.status
WHEN NOT MATCHED THEN INSERT *
```

**Iceberg with Nessie (version control for data):**
```sql
-- Create branch for experimental changes
CREATE BRANCH dev_test IN nessie_catalog;

-- Write to branch
INSERT INTO nessie_catalog.sales_db.fact_orders
  VALUES (1, 100, '2024-01-20', 99.99);

-- Compare branches
SELECT * FROM nessie_catalog.sales_db.fact_orders AT BRANCH dev_test
EXCEPT
SELECT * FROM nessie_catalog.sales_db.fact_orders AT BRANCH main;

-- Merge to main
MERGE BRANCH dev_test INTO main IN nessie_catalog;
```

