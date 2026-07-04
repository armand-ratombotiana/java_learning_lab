# Refactoring Data Lakes

## From Ad-hoc to Medallion
**Before**: No structure, random paths
```java
spark.read().parquet("s3://data/sales_march.parquet");
spark.read().json("s3://data/export_20240301.json");
```

**After**: Structured medallion
```java
spark.table("bronze.sales");
spark.table("silver.events");
spark.table("gold.daily_metrics");
```

## From No ACID to Delta Lake
**Before**: Parquet with no transactions
**After**: Delta Lake with ACID, time travel, schema evolution
