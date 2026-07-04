# Caching - COMMON MISTAKES

## 1. Cache Stampede
Many requests miss cache simultaneously, all hitting the database.
```java
// WRONG: No protection
public Product getProduct(String id) {
    Product p = cache.get(id);
    if (p == null) p = repository.findById(id);  // All threads hit DB
    return p;
}

// RIGHT: Double-checked locking
public Product getProduct(String id) {
    Product p = cache.get(id);
    if (p == null) {
        synchronized (lock) {
            p = cache.get(id);  // double check
            if (p == null) {
                p = repository.findById(id);
                cache.put(id, p);
            }
        }
    }
    return p;
}
```

## 2. Stale Data (Stale Cache)
Cache returns old data after the source is updated.
```java
// WRONG: Update DB but don't invalidate cache
public Product updateProduct(Product p) {
    return repository.save(p);  // cache still has old value
}

// RIGHT: Invalidate cache after update
public Product updateProduct(Product p) {
    Product saved = repository.save(p);
    cache.evict(p.getId());  // or cache.put(p.getId(), saved);
    return saved;
}
```

## 3. Caching Non-Serializable Objects
Must implement Serializable or use proper serialization for distributed caches.

## 4. Too Large Cache Values
Storing large blobs (>1MB) in cache reduces efficiency. Consider alternative storage for large objects.

## 5. No Cache Warming
Cold cache after deployment → all requests hit DB → slow startup.

## 6. Ignoring Cache Size
Unbounded caches cause OOM. Always set max size.
```java
// WRONG: Unbounded
Caffeine.newBuilder().build();

// RIGHT: Limited
Caffeine.newBuilder().maximumSize(10000).build();
```

## 7. Too Long TTL
Long TTLs serve stale data. Short TTLs reduce hit rate. Find the balance.

## 8. Caching Everything
Not all data benefits from caching. Rarely accessed data wastes memory.
- Cache only: hot data, computed results, repeated queries
- Don't cache: mutable data needing immediate consistency, large files

## 9. Missing Cache Metrics
Without hit/miss metrics, you don't know if caching is working. Always monitor.
