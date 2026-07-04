# Redis

## Overview
Redis is an in-memory data structure store used as a database, cache, message broker, and streaming engine. It supports strings, hashes, lists, sets, sorted sets, streams, bitmaps, and geospatial indexes.

## Key Concepts
- **In-memory**: Primary storage in RAM, optional persistence to disk
- **Single-threaded**: Event loop processes commands sequentially
- **Data structures**: Rich set of native data types with operations
- **Pub/Sub**: Publish-subscribe messaging pattern
- **Persistence**: RDB snapshots + AOF (Append-Only File)

## Java Integration
```java
// Jedis (synchronous)
Jedis jedis = new Jedis("localhost", 6379);
jedis.set("key", "value");
String value = jedis.get("key");

// Lettuce (reactive + synchronous)
RedisClient client = RedisClient.create("redis://localhost:6379");
StatefulRedisConnection<String, String> conn = client.connect();
RedisCommands<String, String> sync = conn.sync();
sync.set("key", "value");

// Spring Data Redis
// RedisTemplate<String, Object> redisTemplate;
// redisTemplate.opsForValue().set("key", value);
```
