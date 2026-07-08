# Performance Optimization for Snowflake Data Cloud

## Warehouse Tuning
Multi-cluster warehouses for high concurrency (MIN=1, MAX=10). Separate warehouses for ETL (Large) vs reporting (Medium). Scale down when not needed.

## Clustering
High-cardinality columns first. Composite keys like (date, customer_id). Monitor depth weekly. Avoid low-cardinality columns (booleans).

## Query Optimization
SELECT only needed columns. Use filters on clustered columns. Limit ORDER BY on large results. Avoid Cartesian joins.

## Data Loading
Parquet format for best compression. Batch files to 100-500 MB each. Snowpipe for continuous loading. PURGE=TRUE after successful copy.
