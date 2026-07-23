# Mock Interview: Redis (Lab 05)

**Role:** Backend Engineer / Database Engineer  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is Redis and what data structures does it support?

**Candidate:** Redis is an in-memory data structure store used as a cache, message broker, and database. Key data structures:
- **String:** Simple key-value (caching, counters)
- **List:** Ordered collection (message queues, activity feed)
- **Set:** Unordered, unique values (tags, followers)
- **Sorted Set:** Ordered by score (leaderboards, rate limiter)
- **Hash:** Field-value pairs (user profiles, session data)
- **Bitmap:** Bit-level operations (analytics, feature flags)
- **HyperLogLog:** Cardinality estimation (unique visitors)
- **Stream:** Append-only log (event sourcing, message queue)
- **GeoSpatial:** Location-based queries

**Interviewer:** How does Redis achieve high performance?

**Candidate:** Redis achieves high performance through:
- **In-memory storage:** All data in RAM (sub-millisecond latency)
- **Single-threaded event loop:** No locking overhead for most commands
- **I/O multiplexing:** epoll/kqueue for handling many connections
- **Pipelining:** Batch commands to reduce round-trips
- **Lua scripting:** Execute complex operations atomically on server

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** Explain Redis persistence options.

**Candidate:** 
1. **RDB (Redis Database):** Point-in-time snapshots at configurable intervals. Compact files, good for backups. Trade-off: data loss since last snapshot.
2. **AOF (Append-Only File):** Logs every write operation. Append-only, can replay on restart. Configurable fsync policy (every second, always, OS-default). Trade-off: larger files, slower writes.
3. **Mixed (RDB + AOF):** Recommended. RDB base file + incremental AOF changes.

**Interviewer:** How do you implement a distributed Redis cache with high availability?

**Candidate:** Redis Cluster provides:
- **Automatic sharding:** 16384 hash slots distributed across nodes
- **Master-slave replication:** Each master has 1+ replicas
- **Automatic failover:** If master is unreachable, replica is promoted
- **No single point of failure:** Clients connect to any node, redirected to correct shard

For caching, typical setup: Redis Cluster (3 masters, 3 replicas) with eviction policy `allkeys-lru`. Keys set with TTL. Application uses Lettuce or Jedis client with cluster-aware routing.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a real-time leaderboard for a gaming platform with 10M+ users using Redis.

**Candidate:** 

**Data model:**
```redis
# Global leaderboard (by score)
ZADD leaderboard:global {score} {player_id}

# Weekly leaderboard
ZADD leaderboard:weekly:2024-W30 {score} {player_id}

# Daily leaderboard
ZADD leaderboard:daily:2024-07-23 {score} {player_id}
```

**Operations:**
```redis
# Get top 100 players
ZREVRANGE leaderboard:global 0 99 WITHSCORES

# Get player rank
ZREVRANK leaderboard:global player_12345

# Get player score
ZSCORE leaderboard:global player_12345

# Get players around me (10 above, 10 below)
ZREVRANK leaderboard:global player_12345 → rank 1500
ZREVRANGE leaderboard:global 1490 1510 WITHSCORES
```

**Performance for 10M users:**
- Sorted sets use skip list + hash table — O(log N) for most operations
- ZREVRANGE with 100 results: ~10 microseconds
- Memory: ~100 bytes per entry × 10M = ~1GB

**Scale-out:** For very large leaderboards, shard by region or game mode. Aggregate top scores from each shard for global view.

---

## Interviewer Feedback

**Strengths:** Good Redis fundamentals, practical persistence knowledge, strong leaderboard design  
**Areas to Improve:** Could discuss Redis Stack (RedisJSON, RediSearch, RedisGraph)  
**Verdict:** Hire

---

*Databases Lab 05 MOCK_INTERVIEW.md*
