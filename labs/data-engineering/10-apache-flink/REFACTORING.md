# Refactoring Apache Flink Pipelines

## State Migration with Savepoints
```bash
flink savepoint <jobId> /savepoints/
# Update job with new state schema
flink run -s /savepoints/savepoint-<id> job.jar
```

## From Batch to Streaming
Before: DataSet API (batch processing)
After: DataStream API with event-time windows for near-real-time results

## Operator Chain Optimization
Before: Multiple separate map operations
After: Combine into single map or chain explicitly via .startNewChain() / .disableChaining()
