# Caching - STEP BY STEP

## Setting Up Spring Cache with Caffeine

### Step 1: Add Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

### Step 2: Enable Caching
```java
@EnableCaching
@SpringBootApplication
public class Application { /* ... */ }
```

### Step 3: Configure Cache Manager
```java
@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cm = new CaffeineCacheManager();
        cm.setCaffeine(Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(10, TimeUnit.MINUTES));
        return cm;
    }
}
```

### Step 4: Add Cache Annotations
```java
@Cacheable(value = "products", key = "#id")
public Product getProduct(String id) { /* ... */ }

@CacheEvict(value = "products", key = "#product.id")
public Product updateProduct(Product product) { /* ... */ }
```

## Setting Up Redis Cache

### Step 1: Add Dependency
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### Step 2: Configure Redis
```yaml
spring.redis:
  host: localhost
  port: 6379
  timeout: 2000ms
  lettuce:
    pool:
      max-active: 16
      max-idle: 8
      min-idle: 4
```

### Step 3: Create Redis Configuration
```java
@Configuration
public class RedisConfig {
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        return RedisCacheManager.builder(factory)
            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)))
            .build();
    }
}
```

## Implementing Cache-Aside Pattern Manually

### Step 1: Define Cache Interface
```java
public interface CacheStore<K, V> {
    V get(K key);
    void put(K key, V value, long ttlSeconds);
    void evict(K key);
    void clear();
}
```

### Step 2: Implement with Redis
```java
@Component
public class RedisCacheStore implements CacheStore<String, Product> {
    private final RedisTemplate<String, Product> redis;

    public Product get(String key) {
        return redis.opsForValue().get(key);
    }

    public void put(String key, Product value, long ttlSeconds) {
        redis.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    public void evict(String key) {
        redis.delete(key);
    }
}
```

### Step 3: Use in Service
```java
@Service
public class ProductService {
    private final CacheStore<String, Product> cache;
    private final ProductRepository repository;

    public Product getProduct(String id) {
        Product cached = cache.get(id);
        if (cached != null) return cached;

        Product db = repository.findById(id).orElseThrow();
        cache.put(id, db, 300);  // 5 min TTL
        return db;
    }
}
```
