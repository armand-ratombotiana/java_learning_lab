# Quick Reference: Redis

<div align="center">

![Module](https://img.shields.io/badge/Module-24-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-Redis-green?style=for-the-badge)

**Quick lookup guide for Redis commands**

</div>

---

## 📋 Data Types

| Type | Commands |
|------|----------|
| **String** | SET, GET, INCR, DECR |
| **Hash** | HSET, HGET, HGETALL |
| **List** | LPUSH, RPUSH, LRANGE |
| **Set** | SADD, SMEMBERS, SINTER |
| **Sorted Set** | ZADD, ZRANGE, ZRANK |
| **HyperLogLog** | PFADD, PFCOUNT |
| **Geospatial** | GEOADD, GEORADIUS |
| **Streams** | XADD, XRANGE, XREAD |

---

## 🔑 Key Commands

### Strings
```bash
SET key value [EX seconds] [PX milliseconds] [NX|XX]
GET key
INCR key
DECR key
APPEND key value
SETEX key seconds value
SETNX key value
```

### Hashes
```bash
HSET key field value
HGET key field
HGETALL key
HDEL key field [field ...]
HKEYS key
HVALS key
HLEN key
HMSET key field value [field value ...]
```

### Lists
```bash
LPUSH key value [value ...]
RPUSH key value [value ...]
LPOP key
RPOP key
LRANGE key start stop
LLEN key
LTRIM key start stop
```

### Sets
```bash
SADD key member [member ...]
SREM key member [member ...]
SMEMBERS key
SISMEMBER key member
SCARD key
SINTER key [key ...]
SUNION key [key ...]
SDIFF key [key ...]
```

### Sorted Sets
```bash
ZADD key score member [score member ...]
ZRANGE key start stop [WITHSCORES]
ZREVRANGE key start stop [WITHSCORES]
ZRANK key member
ZSCORE key member
ZRANGEBYSCORE key min max
ZINCRBY key increment member
```

---

## 💻 Java Clients

### Jedis
```java
Jedis jedis = new Jedis("localhost", 6379);
jedis.set("key", "value");
String value = jedis.get("key");
jedis.close();
```

### Lettuce
```java
RedisClient client = RedisClient.create("redis://localhost");
StatefulRedisConnection<String, String> connection = client.connect();
RedisCommands<String, String> commands = connection.sync();
commands.set("key", "value");
connection.close();
```

### Spring Data Redis
```java
@Autowired
private StringRedisTemplate template;

template.opsForValue().set("key", "value");
String value = template.opsForValue().get("key");
```

---

## 📊 Operations Examples

### Cache Pattern
```java
// Get or load
public String getCached(String key) {
    String cached = template.opsForValue().get(key);
    if (cached != null) return cached;
    
    String value = loadFromDB(key);
    template.opsForValue().set(key, value, Duration.ofMinutes(10));
    return value;
}
```

### Distributed Lock
```java
Boolean locked = template.opsForValue()
    .setIfAbsent("lock", "owner", Duration.ofSeconds(10));
if (locked) {
    try {
        // Critical section
    } finally {
        template.delete("lock");
    }
}
```

### Rate Limiter
```java
String count = template.opsForValue().get("ratelimit:" + userId);
if (count != null && Integer.parseInt(count) >= limit) {
    throw new RateLimitExceededException();
}
template.opsForValue().increment("ratelimit:" + userId);
template.expire("ratelimit:" + userId, Duration.ofMinutes(1));
```

### Pub/Sub
```java
// Subscribe
template.subscribe(new MessageListener() {
    public void onMessage(Message msg, byte[] pattern) {
        System.out.println(new String(msg.getBody()));
    }
}, "channel");

// Publish
template.convertAndSend("channel", "message");
```

### Session Store
```java
template.opsForHash().put("session:" + sessionId, "userId", userId);
template.opsForHash().put("session:" + sessionId, "loginTime", now());
template.expire("session:" + sessionId, Duration.ofHours(24));
```

---

## ✅ Best Practices

- Use appropriate data structures
- Set TTL for temporary data
- Use connection pooling
- Handle serialization
- Monitor memory usage

### ❌ DON'T
- Don't store large objects as strings
- Don't forget TTL on cache keys
- Don't use KEYS in production

---

<div align="center">

[Back to Module →](./IMPLEMENTATION.md)

[Take Quizzes →](./PROJECTS.md)

</div>