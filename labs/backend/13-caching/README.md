# Caching

Caching strategies with Spring Cache abstraction.

## Topics
- @Cacheable, @CachePut, @CacheEvict, @Caching
- Redis cache implementation
- Hazelcast distributed caching
- Caffeine for local caching
- Cache configuration and TTL
- Cache eviction strategies (LRU, LFU, TTL)
- Transactional caching
- Cache metrics and monitoring

## Example
```java
@Service
public class ProductService {
    @Cacheable(value = "products", key = "#id", unless = "#result == null")
    public Product getProduct(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @CacheEvict(value = "products", key = "#id")
    public void updateProduct(Long id, Product product) { ... }
}
```
