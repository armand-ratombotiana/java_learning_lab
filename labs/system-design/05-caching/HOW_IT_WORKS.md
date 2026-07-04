# Caching - HOW IT WORKS

## Cache-Aside (Lazy Loading)

### Flow
```java
public Product getProduct(String id) {
    // 1. Check cache
    Product cached = cache.get(id);
    if (cached != null) return cached;  // cache hit

    // 2. Cache miss — load from DB
    Product db = repository.findById(id);

    // 3. Populate cache
    cache.put(id, db);

    // 4. Return
    return db;
}
```

### Spring Implementation
```java
@Cacheable(value = "products", key = "#id")
public Product getProduct(String id) {
    return repository.findById(id).orElseThrow();
}

@CacheEvict(value = "products", key = "#product.id")
public Product updateProduct(Product product) {
    return repository.save(product);
}
```

## Read-Through

### Flow
```java
// Cache is responsible for loading from DB
public class ReadThroughCache {
    public V get(K key) {
        V value = internalStore.get(key);
        if (value == null) {
            value = loadFromDb(key);
            internalStore.put(key, value);
        }
        return value;
    }
}
```

## Write-Through

### Flow
```java
// Every write goes through cache → DB
// Cache is always consistent with DB
public void write(K key, V value) {
    cache.put(key, value);   // write to cache
    db.save(key, value);     // write to DB (in same transaction)
}
```

## Write-Behind (Write-Back)

### Flow
```java
public void write(K key, V value) {
    cache.put(key, value);
    queue.add(new WriteTask(key, value));  // async DB write
}

// Background worker processes queue
@Scheduled(fixedDelay = 1000)
public void flushWrites() {
    while (!queue.isEmpty()) {
        WriteTask task = queue.poll();
        db.save(task.key, task.value);
    }
}
```

## Cache Eviction Policies

```java
// LRU — Least Recently Used (Caffeine default)
Caffeine.newBuilder()
    .maximumSize(10000)
    .expireAfterAccess(10, TimeUnit.MINUTES)
    .build();

// TTL — Time To Live
.caffeineSpec("maximumSize=10000,expireAfterWrite=5m")

// LFU — Least Frequently Used (combines recency + frequency)
Caffeine.newBuilder()
    .maximumSize(10000)
    .expireAfterAccess(10, TimeUnit.MINUTES)
    .recordStats()  // enables frequency tracking
    .build();
```

## Distributed Caching with Redis

```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues()
            .serializeValuesWith(
                SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
            );

        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .build();
    }
}
```
