# Distributed Caching: Step by Step

## Building a Client-Side Cache

### Step 1: Define cache interface
```java
interface Cache<K, V> {
    V get(K key);
    void put(K key, V value);
    void invalidate(K key);
}
```

### Step 2: Implement local cache with LRU
```java
class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxCapacity;
    
    LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.maxCapacity = capacity;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > maxCapacity;
    }
}
```

### Step 3: Add TTL support
```java
class TTLWrapper {
    private final Object value;
    private final long expiry;
}
```

### Step 4: Implement distributed coordination
```java
class DistributedCacheCoordinator {
    void onNodeJoin(String nodeId) {
        ring.addNode(nodeId);
        redistributeData();
    }
    
    void onNodeLeave(String nodeId) {
        ring.removeNode(nodeId);
        // Move data to new owners
    }
}
```
