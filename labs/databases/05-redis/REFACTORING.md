# Refactoring: Redis

## Cache-Aside to Write-Through

```java
// Before: cache-aside (after DB write, cache is stale)
public void updateProduct(Product p) {
    productRepo.save(p);
    redisTemplate.delete("product:" + p.getId()); // lazy invalidation
}

// After: write-through (update cache on write)
public void updateProduct(Product p) {
    productRepo.save(p);
    redisTemplate.opsForValue().set("product:" + p.getId(), p, 1, TimeUnit.HOURS);
}
```

## Jedis to Lettuce Migration

```java
// Before: Jedis (blocking, no reactive support)
Jedis jedis = new Jedis("localhost", 6379);
jedis.set("key", "value");

// After: Lettuce (reactive, async, non-blocking)
RedisClient client = RedisClient.create("redis://localhost:6379");
StatefulRedisConnection<String, String> conn = client.connect();
RedisCommands<String, String> sync = conn.sync();       // sync
RedisAsyncCommands<String, String> async = conn.async(); // async
RedisReactiveCommands<String, String> reactive = conn.reactive(); // reactive
```

## Embedded Lua to Redis Functions

```lua
-- Before: EVAL with inline script
local script = "return redis.call('incr', KEYS[1])";
redis.eval(script, ...);

-- After: Redis Functions (7.0+)
-- redis-cli FUNCTION LOAD
#!lua name=mylib
redis.register_function('myincr',
    function(keys, args)
        return redis.call('incr', keys[1])
    end
)
-- Call: redis.fcall('myincr', 1, 'counter');
```

## Pub/Sub to Streams

```java
// Before: Pub/Sub (no persistence, no replay)
redisTemplate.convertAndSend("notifications", message);

// After: Streams (persistent, replayable, consumer groups)
redisTemplate.opsForStream().add(
    StreamRecords.newRecord()
        .inStream("notifications")
        .ofMap(Map.of("type", "order", "data", json)));
```

## Multi-key Operations with Hash Tags

```java
// Before: operations on different slots (cluster error)
redis.opsForValue().multiSet(Map.of("user:1:name", "A", "user:2:name", "B"));

// After: hash tags force same slot
redis.opsForValue().multiSet(Map.of(
    "{user}:1:name", "A",
    "{user}:2:name", "B"));
```

## Sentinel to Cluster

```yaml
# Before: Sentinel
spring:
  redis:
    sentinel:
      master: mymaster
      nodes: host1:26379,host2:26379,host3:26379

# After: Cluster
spring:
  redis:
    cluster:
      nodes: host1:6379,host2:6379,host3:6379,host4:6379,host5:6379,host6:6379
```

## Raw Jedis to Spring Cache Abstraction

```java
// Before: manual caching
String key = "product:" + id;
Product p = redis.opsForValue().get(key);
if (p == null) {
    p = repo.findById(id);
    redis.opsForValue().set(key, p, 1, TimeUnit.HOURS);
}

// After: Spring Cache annotation
@Cacheable(value = "products", key = "#id", unless = "#result == null")
public Product getProduct(Long id) {
    return repo.findById(id).orElseThrow();
}
```
