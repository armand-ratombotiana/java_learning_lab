# Refactoring Delta Lake Pipelines

## File Compaction
Before: Thousands of small files from streaming writes
After: Scheduled OPTIMIZE with ZORDER BY

## Schema Evolution
Before: Schema change causes job failures
After: Enable mergeSchema=true for automatic evolution

## Batch to Streaming
Before: Hourly batch upserts
After: Continuous streaming MERGE with Change Data Feed consumer
