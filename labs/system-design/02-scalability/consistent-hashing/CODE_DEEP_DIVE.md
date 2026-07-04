# Consistent Hashing Code Deep Dive

This lab provides a pure Java implementation of a Consistent Hash Ring using a `TreeMap` to represent the circular topology.

## 💻 Pure Java Implementation

```java file="labs/system-design/02-scalability/consistent-hashing/SOLUTION/ConsistentHashRing.java"
package systemdesign.scalability;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A production-like implementation of a Consistent Hash Ring with Virtual Nodes.
 */
public class ConsistentHashRing<T> {

    // A cryptographic hash function is preferred for uniform distribution
    private final MessageDigest md;
    
    // The number of virtual nodes per physical node
    private final int numberOfReplicas;
    
    // The Hash Ring represented as a SortedMap (Red-Black tree)
    private final SortedMap<Long, T> ring = new TreeMap<>();

    public ConsistentHashRing(int numberOfReplicas, Collection<T> nodes) {
        this.numberOfReplicas = numberOfReplicas;
        try {
            this.md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
        for (T node : nodes) {
            addNode(node);
        }
    }

    /**
     * Adds a physical node to the ring by creating multiple virtual nodes.
     */
    public void addNode(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            // Label the virtual node (e.g., "ServerA-0", "ServerA-1")
            String vNodeName = node.toString() + "-" + i;
            long hash = hash(vNodeName);
            ring.put(hash, node);
        }
    }

    /**
     * Removes a physical node and all its virtual nodes from the ring.
     */
    public void removeNode(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            String vNodeName = node.toString() + "-" + i;
            long hash = hash(vNodeName);
            ring.remove(hash);
        }
    }

    /**
     * Routes a key to the correct physical node.
     */
    public T getNode(String key) {
        if (ring.isEmpty()) {
            return null;
        }
        
        long hash = hash(key);
        
        // Find the sub-map of the ring starting from the key's hash
        SortedMap<Long, T> tailMap = ring.tailMap(hash);
        
        // If the tailMap is empty, the key is past the last node, so wrap around to the first node
        long nodeHash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        
        return ring.get(nodeHash);
    }

    /**
     * Generates a 64-bit hash using MD5.
     */
    private long hash(String key) {
        md.reset();
        md.update(key.getBytes());
        byte[] digest = md.digest();
        
        // Convert first 8 bytes of MD5 to a long
        long h = 0;
        for (int i = 0; i < 8; i++) {
            h <<= 8;
            h |= ((long) digest[i]) & 0xFF;
        }
        return h;
    }
}
```

## 🔍 Key Takeaways
1. **The `TreeMap`**: We use a `TreeMap` because it keeps keys sorted and provides the `tailMap(hash)` method. This perfectly simulates moving "clockwise" around a ring to find the next available node.
2. **Wrap Around**: Notice the logic `tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey()`. If our key hashes to a value larger than the largest node hash on the ring, it wraps around to the very beginning of the ring.
3. **Cryptographic Hashing**: We use MD5 (or SHA-1) instead of Java's built-in `hashCode()`. Java's `String.hashCode()` is not uniformly distributed enough for consistent hashing and can cluster nodes together, defeating the purpose of the ring.