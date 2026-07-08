# Performance Optimization for Delta Lake

## File Sizing
Target 256MB-1GB per file after OPTIMIZE. Set spark.sql.files.maxPartitionBytes to 128MB. Repartition() before writes for uniform file sizes.

## Z-ordering
Choose high-cardinality, frequently filtered columns. Avoid low-cardinality (booleans). Composite Z-order: ZORDER BY (date, customer_id).

## Partition Pruning
Partition by date granularity (year/month/day). Ensure queries filter on partition columns. Use DELTA statistics for file skipping.

## Read Performance
Enable file listing caching. Set Delta session configs. Use Data Skipping statistics for metadata-based pruning.
