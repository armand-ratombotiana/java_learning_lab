# Interview: Redis

## Beginner

**Q**: What is Redis and what is it used for?
**A**: Redis is an in-memory data structure store. It's used for caching, session management, real-time analytics, message queues, rate limiting, and as a primary database for low-latency workloads.

**Q**: What data structures does Redis support?
**A**: Strings, Hashes, Lists, Sets, Sorted Sets, Streams, Bitmaps, HyperLogLogs, Geospatial indexes (since 3.2).

**Q**: How do you connect to Redis from Java?
**A**: Using Jedis (sync), Lettuce (sync/async/reactive), or Spring Data Redis abstraction. The connection string is `redis://host:6379`.

## Intermediate

**Q**: Explain Redis persistence. When would you use RDB vs AOF?
**A**: RDB creates point-in-time snapshots (default every 5 min). Good for backups and fast restarts. AOF logs every write operation. Good for crash safety (appendfsync everysec). Use RDB + AOF for production — RDB for fast restarts, AOF for minimal data loss.

**Q**: How does Redis handle memory management and eviction?
**A**: Redis stores data in RAM. When `maxmemory` is reached, eviction policy kicks in: noeviction, allkeys-lru, volatile-lru, allkeys-random, volatile-ttl, allkeys-lfu. Use `allkeys-lru` for caching workloads.

**Q**: Explain the Redis Cluster architecture.
**A**: 16384 hash slots distributed across multiple nodes. Each node owns a subset of slots. Client connects to any node, gets MOVED redirect if wrong slot. No central coordinator — gossip protocol for node discovery. Automatic failover with replica promotion. No multi-key operations across slots without hash tags.

## Advanced

**Q**: How does the Redlock algorithm work for distributed locking?
**A**: Redlock acquires the lock on a majority (N/2 + 1) of independent Redis instances. Steps: 1) Get current time. 2) Acquire lock on each instance sequentially with short timeout. 3) Check if acquired majority and elapsed time < lock validity. 4) If failed, release all locks. Provides safety (mutual exclusion) and liveness (deadlock-free) under the Redis distributed model, though it has known debates about correctness under certain clock drift scenarios.

**Q**: Design a real-time analytics system using Redis.
**A**: Use Sorted Sets for time-based metrics per minute/hour. Use HyperLogLog for unique counts (DAU/MAU). Use Bitmaps for daily active tracking. Use Streams for raw event ingestion. Use RedisTimeSeries module for time-series data with retention policies. Cache aggregated results with TTL. Pipeline writes from application for throughput.

**Q**: How would you migrate from Redis Sentinel to Redis Cluster with zero downtime?
**A**: 1) Set up Cluster nodes alongside Sentinel topology. 2) Configure application with dual-write (write to both Sentinel master and Cluster). 3) Backfill Cluster from Sentinel data. 4) Switch reads to Cluster once caught up. 5) Remove dual-write. 6) Decommission Sentinel. Use a proxy like RedisLabs' Migrator or custom dual-write library.
