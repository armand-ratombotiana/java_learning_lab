# Production Scenarios: Redis (Oracle Integration)

## Scenario 1: Maxmemory Evicting Hot Keys — Cache Storm
**Context**: An e-commerce site used Redis to cache product catalog data sourced from Oracle.
**Problem**: During a flash sale, site response time increased from 200ms to 5 seconds. The database CPU went to 100%. Redis was evicting keys aggressively.
**Root Cause**: Redis `maxmemory` was set to 1GB. The flash sale loaded 2GB of new products into cache. The eviction policy `allkeys-lru` evicted the most recently used keys (which were the hot flash-sale keys) because the eviction approximated LRU. Cache misses caused all traffic to hit Oracle.
**Solution**: 1) Increased `maxmemory` to 4GB. 2) Changed eviction policy to `volatile-ttl` to evict keys with shortest TTL first. 3) Set TTL on product cache keys (3600 seconds) so old products expire before hot ones. 4) Implemented cache warming for flash sale items before the event. 5) Added circuit breaker: if cache miss rate > 50%, serve stale cached data.
**Lessons Learned**: Size Redis maxmemory for peak load, not average. Choose eviction policy based on access pattern. Set TTL on all cache keys. Pre-warm cache for known traffic spikes. Monitor cache hit ratio and eviction rate.

## Scenario 2: Replication Lag After Failover
**Context**: Redis Sentinel-based HA setup with one master and two replicas.
**Problem**: After a Sentinel failover, the new master had stale data. Orders processed after failover used old pricing data from the stale cache.
**Root Cause**: The old master was promoted to new master, but it was 5 seconds behind in replication when the old master failed. Redis replication is asynchronous. The `min-slaves-max-lag` was not configured to prevent reads from stale replicas.
**Solution**: 1) Configured `min-replicas-max-lag 5` and `min-replicas-to-write 2` to prevent writes during replication lag. 2) Changed application to read from master (not replica) for critical pricing data. 3) Implemented cache versioning: cache keys include a version that increments on price changes. 4) Added health check: verify replication lag before promoting replica. 5) Monitored `INFO replication` for lag measurement.
**Lessons Learned**: Redis replication is async — plan for data staleness. Use `min-replicas-to-write` for consistency. Read critical data from master. Implement cache versioning for invalidation. Monitor replication lag proactively.

## Scenario 3: Cluster Resharding Causes MOVED Storm
**Context**: A Redis Cluster with 6 nodes was being resharded to add 3 more nodes.
**Problem**: During resharding, the application received 50% `MOVED` redirects, causing 3-second latency. Connection pool exhaustion occurred.
**Root Cause**: The application's Redis client (Jedis) did not update its slot-to-node mapping during resharding. When a slot moved, Jedis received a MOVED redirect, connected to the new node, but then got another MOVED for the next request. The slot map was only refreshed on startup.
**Solution**: 1) Used a Redis client that supports automatic slot refresh (Lettuce instead of Jedis). 2) Configured `cluster.max-redirects` to 16 for automatic retry. 3) Ran resharding during low traffic. 4) Used the `redis-cli --cluster rebalance` with `--cluster-use-empty-migrants` to minimize data movement. 5) Implemented connection pooling per node to reduce MOVED overhead.
**Lessons Learned**: Use Redis clients with automatic cluster topology refresh. Reshard during maintenance windows. Monitor MOVED redirect count during resharding. Prefer Lettuce over Jedis for cluster mode.

## Scenario 4: AOF Corruption After Power Failure
**Context**: A Redis instance suffered an unexpected power outage.
**Problem**: After restart, Redis failed to load the AOF file: "Bad file format reading append only file". Data was lost for the last 15 minutes.
**Root Cause**: AOF was configured with `appendfsync everysec`. The power failure corrupted the last AOF block. Redis was configured to abort on AOF errors instead of recovering.
**Solution**: 1) Ran `redis-check-aof --fix appendonly.aof` to repair the corrupted AOF. 2) Changed `appendfsync` to `always` for critical data (trading off write throughput). 3) Changed `aof-load-truncated` to `yes` to allow loading truncated AOF. 4) Implemented dual persistence: AOF + RDB for recovery flexibility. 5) Added UPS backup for Redis servers to prevent unclean shutdowns.
**Lessons Learned**: Use AOF + RDB for production Redis. Set `aof-load-truncated yes`. Test AOF recovery procedures. Maintain UPS for database servers. Have `redis-check-aof` in recovery runbook.
