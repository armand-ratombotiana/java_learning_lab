# Caching - HISTORY

## Timeline

### 1960s-1980s: CPU Caching
- IBM System/360 introduces cache memory
- L1/L2/L3 cache hierarchy in microprocessors
- Virtual memory page cache

### 1990s: Web Caching
- Browser caching (Expires, Cache-Control headers)
- Squid proxy cache (1996) — reverse proxy caching
- Akamai CDN founded (1998) — content delivery network

### 2000s: Distributed Caching
- 2003: Memcached (LiveJournal) — distributed object cache
- 2007: Memcached used by Facebook, Wikipedia, YouTube
- 2009: Redis (Salvatore Sanfilippo) — richer data structures
- 2009: Varnish Cache — high-performance HTTP accelerator

### 2010s: Advanced Caching
- 2011: Redis Cluster — native distributed mode
- 2013: Hazelcast IMDG — in-memory data grid
- 2015: Apache Ignite — distributed database + caching
- 2017: Caffeine (Ben Manes) — modern Java local cache

### 2020s: Multi-Layer Caching
- L1 (local) + L2 (distributed) cache patterns
- CDN + edge computing (Cloudflare Workers, Deno Deploy)
- Cache-as-a-service (Redis Enterprise, ElastiCache)
- Predictive caching (ML-driven prefetch)

## Java Caching Evolution
- Ehcache (2003) — first popular Java cache
- Spring 3.1 (2011) — @Cacheable abstraction
- JCache (JSR-107, 2014) — standard caching API
- Caffeine (2017) — replacing Guava cache
- Redis Spring Boot Starter (2018) — auto-configured caching
