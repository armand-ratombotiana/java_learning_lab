# Theory: Caching

## Cache Abstraction
Spring's Cache abstraction provides a consistent API across providers.

### Core Annotations
- **@Cacheable**: Cache method result
- **@CachePut**: Update cache with method result
- **@CacheEvict**: Remove entries from cache
- **@Caching**: Group multiple cache operations
- **@CacheConfig**: Class-level cache defaults

### Cache Providers
| Provider | Type | Use Case |
|----------|------|----------|
| Caffeine | Local, in-memory | Single instance, low latency |
| Redis | Distributed | Multi-instance, sharing |
| Hazelcast | Distributed | Data grid, near-cache |
| JCache (JSR-107) | Standard | Vendor-neutral |

### Eviction Policies
- **LRU** (Least Recently Used): Evict oldest accessed
- **LFU** (Least Frequently Used): Evict least popular
- **TTL** (Time To Live): Evict after fixed time
- **FIFO** (First In First Out): Evict oldest inserted

### Cache Penetration
Protect against cache miss storms with:
- null value caching
- Bloom filters
- Mutex locks on cache miss
