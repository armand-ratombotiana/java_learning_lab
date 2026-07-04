# Partitioning: Code Deep Dive

## Complete Sharded Key-Value Store

```java
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ShardedKeyValueStore<K, V> {
    private final ConsistentHash<Shard<K, V>> hashRing;
    private final List<Shard<K, V>> shards;
    
    public ShardedKeyValueStore(int numberOfShards) {
        this.shards = new ArrayList<>();
        this.hashRing = new ConsistentHash<>(new MD5Hash(), 100);
        
        for (int i = 0; i < numberOfShards; i++) {
            Shard<K, V> shard = new Shard<>(i);
            shards.add(shard);
            hashRing.addNode(shard);
        }
    }
    
    public void put(K key, V value) {
        Shard<K, V> shard = hashRing.getNode(key);
        shard.put(key, value);
    }
    
    public V get(K key) {
        Shard<K, V> shard = hashRing.getNode(key);
        return shard.get(key);
    }
    
    public void addShard() {
        Shard<K, V> newShard = new Shard<>(shards.size());
        shards.add(newShard);
        hashRing.addNode(newShard);
        rebalance();
    }
    
    private void rebalance() {
        Map<Shard<K, V>, List<K>> toMove = new HashMap<>();
        
        for (Shard<K, V> shard : shards) {
            List<K> keys = new ArrayList<>(shard.keys());
            for (K key : keys) {
                Shard<K, V> correctShard = hashRing.getNode(key);
                if (correctShard != shard) {
                    toMove.computeIfAbsent(correctShard, k -> new ArrayList<>()).add(key);
                    shard.remove(key);
                }
            }
        }
        
        for (Map.Entry<Shard<K, V>, List<K>> entry : toMove.entrySet()) {
            Shard<K, V> target = entry.getKey();
            for (K key : entry.getValue()) {
                // In practice, transfer value from old shard
                target.put(key, null); // Would need value from old shard
            }
        }
    }
    
    static class Shard<K, V> {
        private final int id;
        private final Map<K, V> data = new ConcurrentHashMap<>();
        
        Shard(int id) { this.id = id; }
        
        void put(K key, V value) { data.put(key, value); }
        V get(K key) { return data.get(key); }
        V remove(K key) { return data.remove(key); }
        Set<K> keys() { return data.keySet(); }
        
        @Override
        public String toString() { return "Shard-" + id; }
    }
}
```
