# LEETCODE_SOLUTIONS — Redis

## Caching / In-Memory Data Structure Solutions

| LeetCode Problem | Redis Data Structure | Approach |
|-----------------|---------------------|----------|
| 146 LRU Cache | Redis sorted set + hash | ZADD for order tracking, HMSET for data |
| 362 Hit Counter | Sorted set with timestamps | ZREMRANGEBYSCORE for window cleanup |
| 380 Randomized Set | Redis set + random member | SADD, SRANDMEMBER, SREM |
| 706 Design HashMap | Redis hash | HSET, HGET, HDEL |
| 155 Min Stack | Redis sorted set | ZADD for ordering, ZRANGE for min |

### Caching Pattern Example: LRU Cache
```python
# Using Redis sorted set for LRU tracking
redis.zadd('cache:lru', {key: time.time()})
redis.hset('cache:data', key, value)

# Eviction
cache_size = redis.zcard('cache:lru')
if cache_size > MAX_SIZE:
    oldest = redis.zpopmin('cache:lru')
    redis.hdel('cache:data', oldest[0][0])
```
