# CAP Theorem: Code Deep Dive

## CP System Implementation

```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

public class CPKeyValueStore<K, V> {
    private final List<CPNode<K, V>> nodes;
    private final int quorumSize;
    
    public CPKeyValueStore(List<CPNode<K, V>> nodes) {
        this.nodes = nodes;
        this.quorumSize = nodes.size() / 2 + 1;
    }
    
    public void put(K key, V value) throws SystemUnavailableException {
        int successes = 0;
        long version = System.currentTimeMillis();
        
        for (CPNode<K, V> node : nodes) {
            try {
                node.write(key, value, version);
                successes++;
            } catch (TimeoutException e) {
                // Node unreachable during partition
            }
        }
        
        if (successes < quorumSize) {
            throw new SystemUnavailableException(
                "Cannot achieve write quorum: " + successes + "/" + quorumSize);
        }
    }
    
    public V get(K key) throws SystemUnavailableException {
        V latestValue = null;
        long latestVersion = -1;
        int successes = 0;
        
        for (CPNode<K, V> node : nodes) {
            try {
                CPNode.ReadResult<V> result = node.read(key);
                successes++;
                if (result.version > latestVersion) {
                    latestVersion = result.version;
                    latestValue = result.value;
                }
            } catch (TimeoutException e) {
                // Node unreachable
            }
        }
        
        if (successes < quorumSize) {
            throw new SystemUnavailableException(
                "Cannot achieve read quorum: " + successes + "/" + quorumSize);
        }
        
        return latestValue;
    }
}
```

## AP System Implementation

```java
import java.util.concurrent.ConcurrentHashMap;

public class APKeyValueStore<K, V> {
    private final List<APNode<K, V>> nodes;
    
    public APKeyValueStore(List<APNode<K, V>> nodes) {
        this.nodes = nodes;
    }
    
    public void put(K key, V value) {
        for (APNode<K, V> node : nodes) {
            try {
                node.writeAsync(key, value);
            } catch (Exception e) {
                // Log and continue - availability is priority
                System.err.println("Write failed to node: " + node.id());
            }
        }
    }
    
    public V get(K key) {
        V value = null;
        for (APNode<K, V> node : nodes) {
            try {
                V result = node.read(key);
                if (result != null) value = result;
                // Return first successful response
                return value;
            } catch (Exception e) {
                continue; // Try next node
            }
        }
        return value; // May be null if all nodes fail
    }
}
```
