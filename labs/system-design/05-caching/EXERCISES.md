# Caching Strategies - EXERCISES

## Exercise 1: Implement Cache-Aside
Create a cache-aside pattern with:
- Cache lookup
- DB fallback
- Cache population

## Exercise 2: Implement Write-Through
Create write-through with:
- DB write first
- Cache update second
- Error handling

## Exercise 3: Implement TTL-Based Expiration
Create TTL manager that:
- Sets expiration time
- Checks staleness
- Auto-evicts

## Exercise 4: Redis Cache Service
Create Redis-backed cache with:
- Connection pooling
- Serialization
- Error handling

## Exercise 5: Cache Warming
Create cache warming that:
- Loads hot data on startup
- Scheduled refresh
- Parallel loading

---

## Solutions

### Exercise 1: Cache-Aside

```java
public <T> T cacheAside(String key, Class<T> type, Supplier<T> loader, long ttl) {
    T cached = cache.get(key);
    if (cached != null) return cached;
    
    T value = loader.get();
    cache.put(key, value, ttl, TimeUnit.SECONDS);
    return value;
}
```

### Exercise 2: Write-Through

```java
public void writeThrough(String key, Object value) {
    try {
        db.save(key, value);
        cache.put(key, value);
    } catch (Exception e) {
        cache.invalidate(key);
        throw e;
    }
}
```

### Exercise 4: Redis Service

```java
@Service
public class RedisCacheService {
    private final StringRedisTemplate template;
    
    public <T> T get(String key, Class<T> type) {
        String json = template.opsForValue().get(key);
        return json != null ? objectMapper.readValue(json, type) : null;
    }
    
    public <T> void set(String key, T value, long ttl) {
        String json = objectMapper.writeValueAsString(value);
        template.opsForValue().set(key, json, ttl, TimeUnit.SECONDS);
    }
}
```