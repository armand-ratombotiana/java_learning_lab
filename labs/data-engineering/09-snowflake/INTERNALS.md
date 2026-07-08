# Snowflake Data Cloud Internals

## Query Execution Flow
1. Client submits SQL to Services Layer. 2. Parser builds AST. 3. Optimizer generates plan using metadata statistics. 4. Planner prunes micro-partitions. 5. Scheduler assigns work. 6. Nodes scan partitions in columnar format. 7. Results shuffled/aggregated. 8. Final result returned.

## Micro-Partition Metadata
Each micro-partition stores: column min/max values (for pruning), column null counts, column distinct counts, compressed/uncompressed storage size, row count. This metadata is automatically maintained and updated.

## Clustering Algorithm
CLUSTER BY defines keys. Greedy algorithm minimizes overlap. Clustering depth and ratio monitored automatically. Background reclustering maintains optimal layout. Can be scheduled or triggered manually.

## Concurrency Model
Virtual warehouses are fully isolated — no resource contention between warehouses. Multi-cluster warehouses add clusters when queries queue up. Queued queries trigger cluster creation within 2-5 minutes.
