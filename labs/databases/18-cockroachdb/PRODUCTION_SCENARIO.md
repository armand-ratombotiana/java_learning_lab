# Production Scenarios: CockroachDB (Oracle Comparison)

## Scenario 1: Hot Range Causing Single-Node Bottleneck
**Context**: A CockroachDB cluster with 9 nodes across 3 regions processing IoT sensor data.
**Problem**: One node had 80% CPU while others were at 20%. Write latency on that node was 50ms vs 5ms elsewhere. Throughput was limited by the hot node.
**Root Cause**: The primary key was `(sensor_id, timestamp)` where sensor_id was monotonically increasing. All writes for new sensors went to a single range. CockroachDB's range splitting was not keeping up with the write rate to that hot range.
**Solution**: 1) Changed primary key to use `(timestamp, sensor_id)` to distribute writes across ranges. 2) Or used a hash-sharded index: `CREATE TABLE ... WITH (hash_sharded = true)`. 3) Monitored hot ranges via `SHOW RANGES FROM TABLE`. 4) Used `ALTER TABLE SPLIT AT` to pre-split ranges. 5) Verified distribution: `SHOW EXPERIMENTAL_RANGES FROM TABLE`.
**Lessons Learned**: Use hash-sharded indexes for write-heavy workloads. Monitor range distribution. Pre-split ranges for known write patterns. Use composite keys with high-cardinality prefix.

## Scenario 2: Distributed Transaction Contention
**Context**: A financial application using CockroachDB for account transfers.
**Problem**: During peak hours, 30% of transactions failed with "restart transaction: TransactionRetryError". Users received errors and had to retry.
**Root Cause**: Two concurrent transfers involving the same account caused write contention. The account row was a hot spot. CockroachDB uses optimistic concurrency — conflicting transactions must retry. The application retry logic only retried once.
**Solution**: 1) Implemented exponential backoff retry in the application (up to 3 retries). 2) Reduced contention by batching transfers in a single transaction where possible. 3) Used `SELECT ... FOR UPDATE` to take locks preemptively and reduce conflicts. 4) Increased `kv.transaction.max_refresh_spans` for better concurrency. 5) Monitored `cockroach.sql.retry.count` metric.
**Lessons Learned**: Implement robust retry logic for CockroachDB transactions. Use `SELECT FOR UPDATE` to reduce contention. Batch operations into fewer transactions. Monitor transaction retry rates. Understand CockroachDB's optimistic concurrency model.

## Scenario 3: Node Decommission Taking Too Long
**Context**: A CockroachDB node was being decommissioned for hardware replacement.
**Problem**: `cockroach node decommission` command reported "decommissioning" but never completed. After 6 hours, only 30% of ranges had moved. The node could not be shut down.
**Root Cause**: The decommission process moves ranges one at a time. Some ranges had replicas on the decommissioning node that were the only up-to-date replicas. Other replicas were behind and needed to catch up before the range could be moved. A network issue between two nodes was slowing replication.
**Solution**: 1) Identified slow range movement: `cockroach node status --decommission` and `SHOW RANGES`. 2) Repaired the network issue between the two nodes. 3) Increased `kv.snapshot_rebalance.max_rate` to 64MB/s. 4) Used `cockroach node recommission` and re-decommissioned after network fix. 5) Monitored decommission progress per range.
**Lessons Learned**: Check cluster health before decommissioning. Resolve network and replication issues first. Tune rebalance rate for faster decommission. Monitor range movement progress. Plan for decommission time based on data size.

## Scenario 4: Locality-Constrained Access Not Optimal
**Context**: A CockroachDB cluster with geo-partitioned data — each region stores its own customer data.
**Problem**: Queries in the EU region were reading from US replicas, causing 100ms latency instead of 5ms. The `AS OF SYSTEM TIME` queries ignored locality.
**Root Cause**: The `SET locality` was configured but the application used `AS OF SYSTEM TIME '-5s'` (follower reads). Follower reads may read from any replica, not necessarily the local one. The `kv.close_queues.interval` setting caused stale leaseholders to be in different regions.
**Solution**: 1) Used `ALTER TABLE t SET LOCALITY REGIONAL BY ROW` to pin rows to specific regions. 2) Used `SET default_transaction_use_follower_reads = 'on'` with `nearby()` function for local reads. 3) Configured `kv.allocator.lease_reb_threshold` to ensure leaseholders are local. 4) Verified locality: `SHOW LOCALITY`. 5) Monitored `node_id` in query plans for cross-region access.
**Lessons Learned**: Configure table locality for regional data. Use nearby() for follower reads to prefer local replicas. Monitor query plan locations. Test read locality with `EXPLAIN ANALYZE`.
