# How Distributed Caching Works

## Cache-Aside Pattern

```java
public class CacheAsideStore {
    private final Cache cache;
    private final Database database;
    
    public Object get(String key) {
        Object value = cache.get(key);
        if (value == null) {
            // Cache miss - load from database
            value = database.query(key);
            if (value != null) {
                cache.put(key, value);
            }
        }
        return value;
    }
    
    public void put(String key, Object value) {
        // Write to database first, then invalidate cache
        database.update(key, value);
        cache.invalidate(key);
    }
}
```

## Read-Through Pattern

```java
public class ReadThroughCache {
    private final CacheLoader loader;
    
    public Object get(String key) {
        return cache.computeIfAbsent(key, k -> loader.load(k));
    }
}
```

## Write-Behind Pattern

```java
public class WriteBehindCache {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    
    public void put(String key, Object value) {
        cache.put(key, value);
        executor.submit(() -> database.update(key, value));
    }
}
```
