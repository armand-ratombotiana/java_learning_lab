# Module 41: Caching Strategies - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-40 (especially Databases, Spring Boot, and System Design)  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Introduction to Caching](#intro)
2. [Caching Topologies](#topologies)
3. [Cache Invalidation Strategies](#invalidation)
4. [Cache Eviction Policies](#eviction)
5. [Distributed Caching (Redis/Memcached)](#distributed)

---

## 1. Introduction to Caching <a name="intro"></a>
Caching is the process of storing copies of frequently accessed data in a temporary, high-speed storage layer (usually RAM) so that future requests for that data are served much faster than accessing the primary storage location (like a database or a remote API).

---

## 2. Caching Topologies <a name="topologies"></a>
- **Local/In-Memory Cache**: Data is cached in the heap memory of the application node (e.g., `ConcurrentHashMap`, Caffeine, Guava). Extremely fast, but cache state isn't shared if you have multiple instances of the application running.
- **Distributed Cache**: Data is cached in an external, standalone service (e.g., Redis, Hazelcast). Slower than local cache due to network hops, but state is synchronized across all application nodes.

---

## 3. Cache Invalidation Strategies <a name="invalidation"></a>
"There are only two hard things in Computer Science: cache invalidation and naming things."
- **Cache-Aside (Lazy Loading)**: The application checks the cache first. If a "cache miss" occurs, it queries the DB, puts the result in the cache, and returns it. If the DB is updated, the application explicitly invalidates the cache.
- **Write-Through**: The application writes data to the cache, and the cache synchronously writes it to the DB. Both are always in sync, but writes are slower.
- **Write-Behind (Write-Back)**: The application writes to the cache, and the cache asynchronously writes to the DB in the background. Extremely fast writes, but high risk of data loss if the cache node crashes before syncing.

---

## 4. Cache Eviction Policies <a name="eviction"></a>
Since RAM is finite, caches must evict old data when they become full.
- **LRU (Least Recently Used)**: Evicts the data that hasn't been accessed for the longest time. (Most common).
- **LFU (Least Frequently Used)**: Evicts the data that is accessed the least often.
- **TTL (Time To Live)**: Data automatically expires and is evicted after a configured time period (e.g., 5 minutes), regardless of how often it's accessed.

---

## 5. Distributed Caching (Redis/Memcached) <a name="distributed"></a>
Redis is the most popular distributed cache. It is a single-threaded, in-memory key-value data store. Spring Boot integrates seamlessly with Redis via Spring Data Redis.

```java
@Service
public class UserService {
    
    // Spring Boot automatically checks Redis before executing the method
    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }
    
    // Updates the database and explicitly evicts the stale cache entry
    @CacheEvict(value = "users", key = "#user.id")
    public void updateUser(User user) {
        userRepository.save(user);
    }
}
```