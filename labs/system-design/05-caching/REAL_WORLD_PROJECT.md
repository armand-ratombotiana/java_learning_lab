# Caching - REAL WORLD PROJECT

Production caching layer with:
- Multi-level cache (L1/L2)
- Redis cluster
- Circuit breaker
- Metrics

```java
@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        return new CaffeineCacheManager();
    }
    
    @Bean
    public RedisCacheManager redisCacheManager() {
        return RedisCacheManager.builder(connectionFactory())
            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                .ttl(Duration.ofMinutes(5)))
            .build();
    }
}
```