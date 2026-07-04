# Distributed Caching: Code Deep Dive

## Building a Distributed Cache with Consistent Hashing

```java
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DistributedCache<K, V> {
    private final ConsistentHashRing<K> ring;
    private final Map<String, CacheNode<K, V>> nodes;
    
    public DistributedCache(List<CacheNode<K, V>> nodeList) {
        this.nodes = new ConcurrentHashMap<>();
        this.ring = new ConsistentHashRing<>(150); // 150 virtual nodes
        
        for (CacheNode<K, V> node : nodeList) {
            nodes.put(node.getId(), node);
            ring.addNode(node.getId());
        }
    }
    
    public V get(K key) {
        String nodeId = ring.getNode(key);
        CacheNode<K, V> node = nodes.get(nodeId);
        return node.get(key);
    }
    
    public void put(K key, V value) {
        String nodeId = ring.getNode(key);
        CacheNode<K, V> node = nodes.get(nodeId);
        node.put(key, value);
    }
    
    static class ConsistentHashRing<K> {
        private final SortedMap<Integer, String> circle = new TreeMap<>();
        private final int virtualNodes;
        
        ConsistentHashRing(int virtualNodes) {
            this.virtualNodes = virtualNodes;
        }
        
        void addNode(String nodeId) {
            for (int i = 0; i < virtualNodes; i++) {
                int hash = hash(nodeId + ":" + i);
                circle.put(hash, nodeId);
            }
        }
        
        void removeNode(String nodeId) {
            for (int i = 0; i < virtualNodes; i++) {
                int hash = hash(nodeId + ":" + i);
                circle.remove(hash);
            }
        }
        
        String getNode(K key) {
            if (circle.isEmpty()) return null;
            int hash = hash(key.toString());
            SortedMap<Integer, String> tail = circle.tailMap(hash);
            Integer nodeHash = tail.isEmpty() ? circle.firstKey() : tail.firstKey();
            return circle.get(nodeHash);
        }
        
        private int hash(String s) {
            int h = 0;
            for (char c : s.toCharArray()) {
                h = 31 * h + c;
            }
            return h & 0x7FFFFFFF;
        }
    }
    
    static class CacheNode<K, V> {
        private final String id;
        private final Map<K, V> store = new ConcurrentHashMap<>();
        
        CacheNode(String id) { this.id = id; }
        String getId() { return id; }
        V get(K key) { return store.get(key); }
        void put(K key, V value) { store.put(key, value); }
    }
}
```
