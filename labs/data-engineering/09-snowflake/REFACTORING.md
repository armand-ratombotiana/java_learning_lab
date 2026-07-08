# Refactoring Snowflake Data Cloud Pipelines

## From Batch to Continuous
Before: Daily COPY INTO jobs with cron scheduling
After: Snowpipe AUTO_INGEST=TRUE for real-time ingestion

## Query Optimization
Before: SELECT * FROM orders WHERE YEAR(order_date) = 2024 (full scan)
After: WHERE order_date >= '2024-01-01' AND order_date < '2025-01-01' (pruning)

## Materialized Views
Before: Repeated aggregations scanning source table
After: CREATE MATERIALIZED VIEW with automatic incremental refresh
