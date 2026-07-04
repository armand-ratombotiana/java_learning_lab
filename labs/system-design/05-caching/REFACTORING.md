# Caching - REFACTORING

## From No Cache to Cache-Aside

### Before (Direct DB)
```java
@Service
public class ProductService {
    public Product getProduct(String id) {
        return repository.findById(id)
            .orElseThrow();  // Always hits DB
    }
}
```

### After (With Cache)
```java
@Service
public class ProductService {
    @Cacheable("products")
    public Product getProduct(String id) {
        return repository.findById(id)
            .orElseThrow();  // Hits DB only on cache miss
    }
}
```

## From Local Cache to Distributed Cache

### Before (Caffeine)
```java
@Bean
public CacheManager cacheManager() {
    return new CaffeineCacheManager();  // Per-instance, inconsistent across JVMs
}
```

### After (Redis)
```java
@Bean
public CacheManager cacheManager(RedisConnectionFactory factory) {
    return RedisCacheManager.builder(factory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30)))
        .build();  // Shared across all instances
}
```

## From Write-Through to Write-Behind

### Before (Write-Through)
```java
public void saveProduct(Product p) {
    cache.put(p.getId(), p);    // synchronous
    repository.save(p);         // synchronous
}
```

### After (Write-Behind)
```java
public void saveProduct(Product p) {
    cache.put(p.getId(), p);    // immediate
    queue.add(p.getId());        // async DB write
}

@Scheduled(fixedDelay = 5000)
public void flushBatch() {
    List<String> ids = new ArrayList<>();
    queue.drainTo(ids);
    repository.saveAll(ids);  // batch DB write
}
```

## Adding Cache Invalidation

### Before: No invalidation
```java
public Product updateProduct(Product p) {
    return repository.save(p);  // cache now has stale data
}
```

### After: Proper invalidation
```java
@CachePut(value = "products", key = "#p.id")  // update cache with new value
@CacheEvict(value = "productLists", allEntries = true)  // invalidate list caches
public Product updateProduct(Product p) {
    return repository.save(p);
}
```

## Performance Impact of Refactoring

| Refactoring | Before | After |
|------------|--------|-------|
| Add local cache | 100ms latency | 1ms latency (hit) |
| Add distributed cache | Inconsistent across instances | Consistent across all instances |
| Write-behind | 100ms write latency | 1ms write latency |
| Proper invalidation | Stale data bugs | Always consistent |
