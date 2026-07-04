# Common Mistakes: Redis

## 1. Not Setting TTL on Cached Data
```java
// WRONG: never expires — manual cleanup needed
redis.opsForValue().set("cache:key", data);

// RIGHT: always set expiration
redis.opsForValue().set("cache:key", data, 1, TimeUnit.HOURS);
```

## 2. O(N) Commands on Large Collections
```java
// WRONG: blocks Redis for millions of elements
Set<String> allKeys = redis.keys("*");
redis.opsForSet().members("huge:set");
redis.opsForHash().entries("large:hash");
redisTemplate.keys("*");

// RIGHT: use SCAN, SSCAN, HSCAN with cursor
Set<String> keys = new HashSet<>();
ScanOptions options = ScanOptions.scanOptions().match("*").count(100).build();
try (Cursor<String> cursor = redis.scan(options)) {
    cursor.forEachRemaining(keys::add);
}
```

## 3. Using Redis as Primary Database Without Persistence
```conf
# WRONG: no persistence configured
save ""

# RIGHT: configure persistence
appendonly yes
appendfsync everysec
```

## 4. Ignoring Memory Limits
```conf
# WRONG: no maxmemory — Redis uses all RAM, gets killed by OOM
# maxmemory 0

# RIGHT: set memory limit + eviction policy
maxmemory 2gb
maxmemory-policy allkeys-lru
```

## 5. Not Using Connection Pooling
```java
// WRONG: new connection per request
Jedis jedis = new Jedis("localhost", 6379);

// RIGHT: use connection pool (Lettuce manages this automatically)
// Spring Boot + Lettuce uses commons-pool2
```

## 6. Storing Large Objects (>10MB)
```java
// WRONG: large strings degrade performance
String largeData = loadLargeFile(); // >10MB
redis.opsForValue().set("big:key", largeData);

// RIGHT: store in chunks, use compression, or use another store
byte[] compressed = compress(largeData);
redis.opsForValue().set("big:key", compressed);
```

## 7. Cross-Slot Operations in Cluster Mode
```java
// WRONG: multi-key operation on different hash slots
redis.opsForValue().multiGet(List.of("key1", "key2")); // keys in different slots

// RIGHT: use hash tags { } to pin keys to same slot
redis.opsForValue().multiGet(List.of("{user:1}:profile", "{user:1}:settings"));
```

## 8. Not Handling Key Deletion from Replicas
```java
// WRONG: TTL-based keys on master may not expire on replica
// (replicas handle expiration via master sync)

// RIGHT: always set consistent TTL via master
redis.opsForValue().set("key", val, 60, TimeUnit.SECONDS);
```
