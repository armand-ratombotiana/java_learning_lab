# How Partitioning Works

## Consistent Hashing

```java
import java.util.*;

public class ConsistentHash<T> {
    private final HashFunction hashFunction;
    private final int numberOfReplicas;
    private final SortedMap<Integer, T> circle = new TreeMap<>();
    
    public ConsistentHash(HashFunction hashFunction, int numberOfReplicas) {
        this.hashFunction = hashFunction;
        this.numberOfReplicas = numberOfReplicas;
    }
    
    public void addNode(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            int hash = hashFunction.hash(node.toString() + i);
            circle.put(hash, node);
        }
    }
    
    public void removeNode(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            int hash = hashFunction.hash(node.toString() + i);
            circle.remove(hash);
        }
    }
    
    public T getNode(Object key) {
        if (circle.isEmpty()) return null;
        int hash = hashFunction.hash(key.toString());
        if (!circle.containsKey(hash)) {
            SortedMap<Integer, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }
    
    public interface HashFunction {
        int hash(String input);
    }
}
```

## Range Sharding

```java
public class RangeShardManager {
    private final NavigableMap<String, Shard> shards = new TreeMap<>();
    
    public RangeShardManager() {
        // Define shard boundaries
        shards.put("A", new Shard("shard1", "A", "M"));
        shards.put("N", new Shard("shard2", "N", "Z"));
    }
    
    public Shard getShard(String key) {
        // Find the first entry whose key is <= the search key
        Map.Entry<String, Shard> entry = shards.floorEntry(key.toUpperCase());
        if (entry == null) {
            // Handle keys before the first boundary
            return shards.firstEntry().getValue();
        }
        Shard shard = entry.getValue();
        if (key.toUpperCase().compareTo(shard.endRange) > 0) {
            return shards.higherEntry(key.toUpperCase()).getValue();
        }
        return shard;
    }
    
    record Shard(String name, String startRange, String endRange) {}
}
```
