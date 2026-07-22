# Production Scenarios: Database Sharding (Oracle Focus)

## Scenario 1: Hot Shard — Uneven Data Distribution
**Context**: An Oracle Sharded database with 4 shards for a customer 360 application.
**Problem**: Shard 1 had 60% of the data and 80% of query traffic. Response time on Shard 1 was 10x slower than other shards. The shard key was `customer_id` by region prefix.
**Root Cause**: The shard key was a range-based key based on customer ID prefix (A-M = Shard 1, N-Z = Shard 2-4). Most customers were in the A-M range (60% of total). The shard key did not distribute data evenly because the prefix range was not balanced.
**Solution**: 1) Changed shard key from range-based to hash-based: `SORT BY HASH(customer_id)`. 2) Used Oracle's `CONSOLIDATE SHARD` to redistribute data. 3) Pre-split chunks before rebalancing to control migration. 4) Verified distribution: `SELECT shard_id, COUNT(*) FROM sharded_table GROUP BY shard_id`. 5) Monitored query latency per shard using `V$SHARD_QUEUE`.
**Lessons Learned**: Use hash-based shard keys for even distribution. Monitor per-shard data size regularly. Avoid range-based sharding without careful workload analysis. Pre-verify distribution with representative data volumes.

## Scenario 2: Cross-Shard Query Performance — Scatter-Gather Timeout
**Context**: A reporting application ran queries that did not include the shard key in predicates.
**Problem**: Queries timed out after 60 seconds because they performed scatter-gather across all 16 shards. The reporting dashboard was unusable.
**Root Cause**: The query did not include `WHERE customer_id = ?`. Without the shard key, Oracle SDB dispatched the query to all shards in parallel. One shard was slow due to local contention, holding up the entire result set.
**Solution**: 1) Added shard key to the query: changed dashboard to always filter by customer. 2) For global reports, created a global table (replicated to all shards) with summary data. 3) Used `DBMS_SHARD.ENABLE_SCATTER_GATHER_TIMEOUT(30)` to set a maximum scatter-gather timeout. 4) Implemented aggregated reporting from a separate Oracle RAC warehouse instead of sharded database. 5) Optimized the slow shard with better indexing.
**Lessons Learned**: Design queries to include shard key for single-shard access. Use global replicated tables for reference data. Set scatter-gather timeouts. Consider separate reporting database for cross-shard analytics.

## Scenario 3: Shard Rebalancing Downtime
**Context**: A new shard was added to the Oracle SDB to handle growth.
**Problem**: During rebalancing, the chunk migration caused 30 minutes of read/write errors for 10% of customers. The migration was done during business hours.
**Root Cause**: Oracle Sharding's `ALTER SHARD ... REBALANCE` moved chunks with `MOVEABLE=YES`, locking the chunk during migration. Reads and writes to the chunk failed while it was being moved. The migration did not use `ONLINE` mode.
**Solution**: 1) Re-ran rebalancing with `ONLINE` keyword: `ALTER SHARD ... REBALANCE ONLINE`. 2) Scheduled rebalancing during maintenance window. 3) Reduced chunk size to minimize migration time per chunk. 4) Increased parallelism: `SET PARALLEL 4`. 5) Monitored rebalancing progress: `SELECT * FROM V$SHARD_CHUNK_MIGRATION`.
**Lessons Learned**: Use ONLINE mode for shard rebalancing. Schedule rebalancing during low traffic. Reduce chunk size for faster per-chunk migration. Monitor migration progress and impact.

## Scenario 4: Adding New Shard — Data Redistribution Too Long
**Context**: A new shard server was added to distribute 2TB of data across 3 existing shards.
**Problem**: The rebalancing was estimated to take 72 hours to move 500GB of data. The business could not tolerate 3 days of migration impact.
**Root Cause**: The chunk size was too large (10GB per chunk). The network bandwidth between shards was 1Gbps, but the migration was not using parallel transfer. The migration was configured with `PARALLEL 1`.
**Solution**: 1) Increased parallelism to 4: `ALTER SHARD ... REBALANCE ONLINE PARALLEL 4`. 2) Reduced chunk target size to 1GB for finer-grained migration. 3) Used dedicated network (10Gbps interconnect) for chunk migration. 4) Prioritized migrating smaller, less active chunks first. 5) Estimated migration time correctly using `V$SHARD_CHUNK_MIGRATION` projections.
**Lessons Learned**: Use parallel migration for large rebalancing operations. Choose appropriate chunk sizes. Use dedicated, high-bandwidth network for chunk migration. Plan for longer migration than estimated. Schedule additive shard capacity before storage reaches critical levels.
