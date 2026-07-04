# Performance: Redis

## Tuning Configuration

```conf
# Memory
maxmemory 4gb
maxmemory-policy allkeys-lru

# Persistence
save ""                            # Disable RDB if using AOF
appendonly yes
appendfsync no                     # OS controls flush (fastest)
no-appendfsync-on-rewrite yes

# Networking
tcp-backlog 511
timeout 0
tcp-keepalive 300

# Latency
latency-monitor-threshold 100      # monitor > 100ms events
slowlog-log-slower-than 1000       # log queries > 1ms
```

## Connection Pool Tuning (Lettuce)

```yaml
spring:
  redis:
    lettuce:
      pool:
        max-active: 20          # max connections
        max-idle: 10            # max idle connections
        min-idle: 5             # maintain at least 5 idle
        max-wait: 3000ms        # timeout waiting for connection
      shutdown-timeout: 100ms
```

## Pipeline Operations

```java
// WRONG: multiple round-trips
for (String key : keys) {
    String val = redis.opsForValue().get(key);
}

// RIGHT: pipeline (single round-trip)
List<Object> results = redis.executePipelined(
    (RedisOperations<String, String> ops) -> {
        keys.forEach(ops::opsForValue);
        return null;
    });
```

## Batch Writes

```java
// WRONG: individual writes
for (User user : users) {
    redis.opsForValue().set("user:" + user.getId(), user);
}

// RIGHT: multi-set (single command)
Map<String, User> userMap = users.stream()
    .collect(Collectors.toMap(u -> "user:" + u.getId(), u -> u));
redis.opsForValue().multiSet(userMap);
```

## Use Appropriate Data Structures

```java
// WRONG: storing serialized objects in strings
redis.opsForValue().set("user:1:profile", jsonString);

// RIGHT: use hash for field-level access
Map<String, String> fields = Map.of(
    "name", "Alice", "email", "alice@example.com");
redis.opsForHash().putAll("user:1:profile", fields);
// Now read single field without deserializing entire object
String email = (String) redis.opsForHash().get("user:1:profile", "email");
```

## Avoid KEYS in Production

```java
// WRONG: SCAN instead of KEYS
Set<String> bad = redis.keys("pattern:*");

// RIGHT: incremental scan
Set<String> keys = new HashSet<>();
ScanOptions opts = ScanOptions.scanOptions().match("pattern:*").count(100).build();
try (Cursor<String> cursor = redis.scan(opts)) {
    cursor.forEachRemaining(keys::add);
}
```

## Memory Optimization

```conf
# Use smaller key names (but readable)
# WRONG: long key names waste memory
product:catalog:2024:electronics:laptops:12345

# RIGHT: shorter but still meaningful
prod:12345

# Use integers where possible (INT encoding, 8 bytes)
# WRONG: "count:1" string
# RIGHT: integer type where applicable
```

## Persistence Performance

```conf
# Best performance: RDB only
save 900 1

# Best durability: AOF with appendfsync always (slowest writes)
# Balanced: AOF with appendfsync everysec (recommended)
appendfsync everysec
```
