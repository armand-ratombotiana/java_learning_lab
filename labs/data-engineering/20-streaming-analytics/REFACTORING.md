# Refactoring Streaming Analytics Pipelines

## Batch to Streaming
Before: Hourly batch aggregations
After: Streaming aggregations with Flink SQL

## Pull to Push
Before: Dashboard polls database
After: WebSocket push from stream processor to dashboard

## Monolithic to Microservices
Before: Single analytics pipeline
After: Independent streaming pipelines per business metric with materialized views
