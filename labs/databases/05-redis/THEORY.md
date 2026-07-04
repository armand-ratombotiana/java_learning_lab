# Theory: Redis Data Structures

## Core Data Types

### Strings
```java
redisTemplate.opsForValue().set("key", "value");
// Operations: GET, SET, INCR, DECR, APPEND, GETSET, MGET, MSET
```

### Hashes (Maps)
```java
redisTemplate.opsForHash().put("user:1", "name", "Alice");
redisTemplate.opsForHash().put("user:1", "email", "alice@example.com");
// Operations: HGET, HSET, HGETALL, HINCRBY, HDEL
```

### Lists (Linked Lists)
```java
redisTemplate.opsForList().leftPush("logs", "error: connection failed");
redisTemplate.opsForList().rightPop("logs");
// Operations: LPUSH, RPUSH, LPOP, RPOP, LRANGE, LLEN
```

### Sets (Unordered, Unique)
```java
redisTemplate.opsForSet().add("tags:java", "spring", "hibernate", "jpa");
// Operations: SADD, SREM, SMEMBERS, SINTER, SUNION, SDIFF
```

### Sorted Sets (Score-Ordered)
```java
redisTemplate.opsForZSet().add("leaderboard", "alice", 100);
redisTemplate.opsForZSet().add("leaderboard", "bob", 85);
// Operations: ZADD, ZRANGE, ZRANK, ZINCRBY, ZREVRANGE
```

### Streams (Append-Only Log)
```java
redisTemplate.opsForStream().add(
    StreamRecords.newRecord()
        .inStream("events")
        .ofMap(Map.of("type", "login", "user", "alice")));
// Operations: XADD, XREAD, XREADGROUP, XRANGE
```

## Expiration / TTL
```java
redisTemplate.opsForValue().set("session:123", userId, 1, TimeUnit.HOURS);
redisTemplate.expire("temp:data", 30, TimeUnit.MINUTES);
```

## Persistence Modes
- **RDB**: Point-in-time snapshots (default every 5 min if 100+ writes)
- **AOF**: Append-only log of every write operation
- **None**: Pure cache, no persistence
- **RDB + AOF**: Hybrid approach (recommended)
