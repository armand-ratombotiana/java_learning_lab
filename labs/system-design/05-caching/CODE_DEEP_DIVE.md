# Caching - CODE DEEP DIVE

## Table of Contents
1. [Local Caching with Caffeine](#caffeine)
2. [Distributed Caching with Redis](#redis)
3. [Multi-Layer Caching](#multi-layer)
4. [CDN Caching Simulation](#cdn)

---

## 1. Local Caching with Caffeine <a name="caffeine"></a>

### Setup
```xml
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

### Cache Configuration
```java
@Configuration
public class CaffeineConfig {

    @Bean
    public Cache<String, Product> productCache() {
        return Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .recordStats()
            .build();
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("products", "users");
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(100_000)
            .expireAfterAccess(10, TimeUnit.MINUTES));
        return cacheManager;
    }
}
```

### Cache Usage
```java
@Service
public class ProductService {
    private final Cache<String, Product> cache;
    private final ProductRepository repository;

    public ProductService(Cache<String, Product> cache, ProductRepository repository) {
        this.cache = cache;
        this.repository = repository;
    }

    public Product getProduct(String id) {
        return cache.get(id, k -> repository.findById(k)
            .orElseThrow(() -> new ProductNotFoundException(id)));
    }

    public Product updateProduct(Product product) {
        Product saved = repository.save(product);
        cache.put(saved.getId(), saved);
        return saved;
    }

    public void deleteProduct(String id) {
        repository.deleteById(id);
        cache.invalidate(id);
    }
}
```

### Cache Statistics
```java
@RestController
@RequestMapping("/admin/cache")
public class CacheMetricsController {

    @GetMapping("/stats")
    public Map<String, CacheStats> getCacheStats() {
        CacheStats stats = cache.stats();
        return Map.of(
            "hitRate", stats.hitRate(),
            "missRate", stats.missRate(),
            "loadCount", stats.loadCount(),
            "evictionCount", stats.evictionCount()
        );
    }
}
```

### Caffeine Cache Loading
```java
// Auto-loading cache
LoadingCache<String, Product> loadingCache = Caffeine.newBuilder()
    .maximumSize(10_000)
    .refreshAfterWrite(1, TimeUnit.MINUTES)  // async refresh before expiry
    .build(k -> repository.findById(k).orElseThrow());

// Async loading
AsyncLoadingCache<String, Product> asyncCache = Caffeine.newBuilder()
    .maximumSize(10_000)
    .buildAsync(k -> CompletableFuture.supplyAsync(() ->
        repository.findById(k).orElseThrow()));

Product product = asyncCache.get("p123").get();  // CompletableFuture
```

---

## 2. Distributed Caching with Redis <a name="redis"></a>

### Setup
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### Redis Configuration
```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .withCacheConfiguration("products",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
            .withCacheConfiguration("users",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)))
            .build();
    }
}
```

### Redis Operations
```java
@Service
public class RedisCacheService {
    private final RedisTemplate<String, Object> redis;

    public RedisCacheService(RedisTemplate<String, Object> redis) {
        this.redis = redis;
    }

    public void cacheProduct(Product product) {
        redis.opsForValue().set("product:" + product.getId(), product, 10, TimeUnit.MINUTES);
    }

    public Product getProduct(String id) {
        return (Product) redis.opsForValue().get("product:" + id);
    }

    public void invalidateProduct(String id) {
        redis.delete("product:" + id);
    }

    // Batch operations
    public Map<String, Product> getProducts(List<String> ids) {
        List<String> keys = ids.stream()
            .map(id -> "product:" + id)
            .collect(Collectors.toList());
        List<Object> values = redis.opsForValue().multiGet(keys);
        // Map keys back to products
        Map<String, Product> result = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            if (values.get(i) != null) {
                result.put(ids.get(i), (Product) values.get(i));
            }
        }
        return result;
    }
}
```

### Redis Rate Limiting
```java
@Component
public class RateLimiter {
    private final RedisTemplate<String, String> redis;

    public RateLimiter(RedisTemplate<String, String> redis) {
        this.redis = redis;
    }

    public boolean allowRequest(String clientId, int maxRequests, int windowSeconds) {
        String key = "ratelimit:" + clientId;
        Long count = redis.opsForValue().increment(key);
        if (count == 1) {
            redis.expire(key, windowSeconds, TimeUnit.SECONDS);
        }
        return count <= maxRequests;
    }
}
```

### Redis Distributed Lock
```java
@Component
public class DistributedLock {
    private final RedisTemplate<String, String> redis;

    public DistributedLock(RedisTemplate<String, String> redis) {
        this.redis = redis;
    }

    public boolean acquireLock(String lockKey, String requestId, int ttlSeconds) {
        return redis.opsForValue()
            .setIfAbsent(lockKey, requestId, Duration.ofSeconds(ttlSeconds));
    }

    public void releaseLock(String lockKey, String requestId) {
        // Lua script ensures atomic check-and-delete
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] " +
                        "then return redis.call('del', KEYS[1]) " +
                        "else return 0 end";
        redis.execute(new DefaultRedisScript<>(script, Long.class),
            List.of(lockKey), requestId);
    }
}
```

---

## 3. Multi-Layer Caching <a name="multi-layer"></a>

### L1 (Local) + L2 (Redis) Cache
```java
@Component
public class MultiLayerCache {
    private final Cache<String, Object> l1Cache;      // Caffeine - local
    private final RedisTemplate<String, Object> l2Cache; // Redis - distributed
    private static final String L2_PREFIX = "l2:";

    public MultiLayerCache(RedisTemplate<String, Object> redis) {
        this.l1Cache = Caffeine.newBuilder()
            .maximumSize(1_000)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .recordStats()
            .build();
        this.l2Cache = redis;
    }

    public Object get(String key) {
        // Try L1 first
        Object value = l1Cache.getIfPresent(key);
        if (value != null) return value;

        // Try L2
        value = l2Cache.opsForValue().get(L2_PREFIX + key);
        if (value != null) {
            l1Cache.put(key, value);  // populate L1 from L2
            return value;
        }

        return null;  // cache miss
    }

    public void put(String key, Object value, int ttlMinutes) {
        l1Cache.put(key, value);
        l2Cache.opsForValue().set(L2_PREFIX + key, value, ttlMinutes, TimeUnit.MINUTES);
    }

    public void evict(String key) {
        l1Cache.invalidate(key);
        l2Cache.delete(L2_PREFIX + key);
    }

    public Map<String, CacheStats> getStats() {
        return Map.of(
            "l1", l1Cache.stats(),
            "l2", null  // Redis stats available via INFO command
        );
    }
}
```

### Cache Stampede Prevention
```java
@Component
public class StampedePreventionCache {
    private final Cache<String, CompletableFuture<Product>> cache;
    private final ProductRepository repository;

    public StampedePreventionCache(ProductRepository repository) {
        this.cache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(5, TimeUnit.SECONDS)  // short TTL for demo
            .build();
        this.repository = repository;
    }

    public Product getProduct(String id) {
        CompletableFuture<Product> future = cache.get(id, k -> {
            // Only one thread per key loads from DB
            return CompletableFuture.supplyAsync(() ->
                repository.findById(k).orElseThrow());
        });

        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            cache.invalidate(id);  // remove failed future
            throw new RuntimeException("Failed to load product", e);
        }
    }
}
```

---

## 4. CDN Caching Simulation <a name="cdn"></a>

### Edge Cache Simulation
```java
@Component
public class EdgeCache {
    private final Cache<String, String> edgeCache;

    public EdgeCache() {
        this.edgeCache = Caffeine.newBuilder()
            .maximumSize(100_000)
            .expireAfterWrite(24, TimeUnit.HOURS)  // CDN-style long TTL
            .build();
    }

    public String serveContent(String path, String originUrl) {
        return edgeCache.get(path, k -> fetchFromOrigin(originUrl));
    }

    private String fetchFromOrigin(String url) {
        RestTemplate rest = new RestTemplate();
        return rest.getForObject(url, String.class);
    }

    public void invalidate(String path) {
        edgeCache.invalidate(path);
    }

    public void invalidateByPrefix(String prefix) {
        // CDNs support purge by path prefix
        edgeCache.asMap().keySet()
            .stream()
            .filter(k -> k.startsWith(prefix))
            .forEach(edgeCache::invalidate);
    }
}
```

### Cache-Controlled Endpoint
```java
@RestController
@RequestMapping("/api")
public class CachedEndpointController {

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        Product product = productService.getProduct(id);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS)
                .staleWhileRevalidate(60, TimeUnit.SECONDS))
            .eTag(computeEtag(product))
            .body(product);
    }

    @GetMapping("/products/highlighted")
    public ResponseEntity<List<Product>> getHighlightedProducts() {
        List<Product> products = productService.getHighlightedProducts();
        String hash = Integer.toHexString(products.hashCode());

        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
            .eTag(hash)
            .body(products);
    }

    private String computeEtag(Product product) {
        return Integer.toHexString(product.hashCode());
    }
}
```

---

## Summary

This deep dive covered:
1. **Caffeine**: High-performance local caching with automatic statistics
2. **Redis**: Distributed caching with TTL, rate limiting, and distributed locks
3. **Multi-Layer**: L1 (local) + L2 (distributed) cache with stampede protection
4. **CDN Simulation**: Edge caching with ETags and cache-control headers

Each caching strategy balances trade-offs between consistency, throughput, and operational complexity.
