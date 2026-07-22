# Production Scenarios: MongoDB (Oracle Comparison)

## Scenario 1: Shard Key Hotspot Causing 10x Latency
**Context**: A MongoDB cluster with 5 shards processing IoT sensor data experienced uneven load.
**Problem**: One shard had 80% of the data and 90% of the queries. P95 latency on that shard was 2 seconds vs 50ms on others. The hot shard was causing overall system slowdown.
**Root Cause**: The shard key was `timestamp` (monotonically increasing). All new data went to the same shard because MongoDB's range-based sharding directed new timestamps to the maximum shard. The hot shard was also the one receiving all writes.
**Solution**: 1) Changed shard key to hashed `_id` to distribute writes evenly: `sh.shardCollection("db.collection", { "_id": "hashed" })`. 2) For range queries, added a compound shard key: `{ region: 1, _id: "hashed" }`. 3) Pre-split chunks to avoid auto-split overhead. 4) Monitored `sh.status()` and `db.collection.getShardDistribution()` to verify distribution.
**Lessons Learned**: Never use monotonically increasing values as shard keys. Use hashed shard keys for write-heavy workloads. Monitor shard distribution after rebalancing. Pre-split chunks for large data loads.

## Scenario 2: WiredTiger Cache Pressure
**Context**: A MongoDB 4.4 production cluster started experiencing periodic write stalls.
**Problem**: Every 5 minutes, write operations would pause for 2-3 seconds. Application timeouts occurred. `mongostat` showed high `dirty` and `queued` metrics.
**Root Cause**: WiredTiger's internal cache was filling up. The eviction thread could not keep up with writes. `wiredTiger cache maximum bytes configured` was at default 50% of RAM (6GB out of 12GB). The working set exceeded the cache, causing frequent eviction and page faults.
**Solution**: 1) Increased WiredTiger cache to 60% of RAM: `storage.wiredTiger.engineConfig.cacheSizeGB: 8`. 2) Increased `eviction_target` to 90 and `eviction_trigger` to 95. 3) Shrunk the working set by archiving old data to a separate collection. 4) Added indexes to reduce the amount of data scanned per query. 5) Monitored `serverStatus().wiredTiger.cache` for eviction rates.
**Lessons Learned**: Monitor WiredTiger cache pressure proactively. Size cache to accommodate working set. Use time-series collections for IoT data to manage growth. Implement data archiving strategy.

## Scenario 3: Replica Set Election Causes Write Gap
**Context**: A MongoDB replica set with 3 members experienced a primary election.
**Problem**: During the election (2 seconds), no writes were accepted. A critical order-processing application timed out. New orders were lost.
**Root Cause**: The primary node ran out of disk space for the oplog. MongoDB stepped down. The election took 2 seconds because `electionTimeoutMillis` was at default 10000ms. The application had `w: majority` write concern, which queued writes during the election.
**Solution**: 1) Freed disk space on all nodes. 2) Reduced `electionTimeoutMillis` to 5000ms for faster elections. 3) Set `members[n].priority` appropriately to avoid unnecessary elections. 4) Implemented client-side retry logic for write concern timeout. 5) Added monitoring for oplog size and disk usage.
**Lessons Learned**: Monitor disk space on all replica set members. Configure `electionTimeoutMillis` appropriately. Implement retry logic in applications. Set up alerts for oplog size and disk usage. Use `w: majority` with retryWrites.

## Scenario 4: Aggregation Pipeline Memory Limit
**Context**: A reporting application ran MongoDB aggregation on a 50M document collection.
**Problem**: The aggregation failed with "Exceeded memory limit for $group, but didn't allow external sort". The report could not be generated.
**Root Cause`: The `$group` stage was processing 50M documents and required more than 100MB of RAM. MongoDB aggregation stages have a 100MB memory limit by default. The `allowDiskUse` option was not set.
**Solution**: 1) Added `allowDiskUse: true` to the aggregation command. 2) Added an earlier `$match` stage to filter documents before the `$group`. 3) Broke the aggregation into two steps: pre-aggregate hourly and then aggregate daily. 4) Created a materialized view using `$merge` into a summary collection. 5) Increased the aggregation pipeline limit: `cursor.batchSize(5000)` for large result sets.
**Lessons Learned**: Always use `allowDiskUse` for large aggregations. Filter early in the pipeline. Pre-aggregate data for recurring reports. Monitor aggregation memory usage. Create summary collections for large datasets.
