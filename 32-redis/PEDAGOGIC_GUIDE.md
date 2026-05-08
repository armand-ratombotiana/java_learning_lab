# Pedagogic Guide - Redis

## Learning Path

### Phase 1: Data Structures
1. Strings - simple key-value storage
2. Hashes - field-value pairs (like Maps)
3. Lists - ordered collections (LPUSH/RPOP pattern)
4. Sets - unique unordered collections
5. Sorted Sets - score-ordered collections

### Phase 2: Java Integration
1. Spring Data Redis setup
2. RedisTemplate operations
3. Serialization strategies (JSON, JDK)
4. Connection pooling

### Phase 3: Caching Patterns
1. Cache-aside (lazy loading)
2. Write-through caching
3. Cache eviction strategies
4. Cache penetration, avalanche, storm

### Phase 4: Advanced Patterns
1. Distributed locks (Redisson)
2. Rate limiting algorithms
3. Pub/Sub for real-time messaging
4. HyperLogLog for cardinality

### Phase 5: Operations & Monitoring
1. Redis persistence (RDB/AOF)
2. Memory management
3. Key expiration strategies
4. Latency monitoring

## Key Commands Reference

| Command | Description |
|---------|-------------|
| `GET/SET` | String operations |
| `HGET/HSET` | Hash operations |
| `LPUSH/LPOP` | List operations |
| `SADD/SMEMBERS` | Set operations |
| `ZADD/ZRANK` | Sorted set ops |
| `EXPIRE` | Set TTL |
| `SCAN` | Iterate keys |

## Interview Topics
- Cache vs. direct database trade-offs
- Redis vs. Memcached comparison
- When not to use Redis (persistence needs)
- Memory management and eviction policies
- Cluster vs. Sentinel architecture

## Next Steps
- Explore Redis Cluster for sharding
- Learn Redis Streams for event sourcing
- Study Lua scripting for atomic operations
- Explore Redis Modules (RediSearch, RedisJSON)