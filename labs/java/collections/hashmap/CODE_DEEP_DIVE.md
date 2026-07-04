# HashMap Code Deep Dive

This lab dissects a production-grade implementation of a custom Hash Map to understand the mechanics without the noise of the JDK's internal optimizations.

## 💻 Building a Simple HashMap from Scratch

To truly understand `HashMap`, we must build one. Below is a simplified, generic implementation using chaining for collision resolution.

```java file="labs/java/collections/hashmap/SOLUTION/SimpleHashMap.java"
package collections.hashmap;

import java.util.Objects;

/**
 * A simplified educational implementation of a Hash Map using chaining.
 * Demonstrates the core mechanics of hashing, indexing, collisions, and resizing.
 */
public class SimpleHashMap<K, V> {
    
    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    
    private Node<K, V>[] table;
    private int size;
    
    @SuppressWarnings("unchecked")
    public SimpleHashMap() {
        table = new Node[DEFAULT_CAPACITY];
    }
    
    // Internal Node structure for the linked list (chaining)
    private static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;
        
        Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
    
    /**
     * Calculates the bucket index for a given key.
     */
    private int getIndex(K key) {
        if (key == null) return 0;
        // Prevent negative indices with bitwise AND
        return (key.hashCode() & 0x7FFFFFFF) % table.length; 
    }
    
    /**
     * Inserts or updates a key-value pair.
     */
    public void put(K key, V value) {
        int index = getIndex(key);
        Node<K, V> head = table[index];
        
        // Traverse the chain to check for existing key
        Node<K, V> current = head;
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                current.value = value; // Update existing
                return;
            }
            current = current.next;
        }
        
        // Key not found, insert at the head of the chain
        table[index] = new Node<>(key, value, head);
        size++;
        
        // Check if resizing is needed
        if (size >= table.length * DEFAULT_LOAD_FACTOR) {
            resize();
        }
    }
    
    /**
     * Retrieves a value by key.
     */
    public V get(K key) {
        int index = getIndex(key);
        Node<K, V> current = table[index];
        
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }
    
    /**
     * Resizes the internal table and rehashes all elements.
     * This is an expensive O(n) operation.
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldTable = table;
        table = new Node[oldTable.length * 2];
        size = 0; // Reset size, put() will increment it
        
        // Rehash all existing elements into the new table
        for (Node<K, V> head : oldTable) {
            Node<K, V> current = head;
            while (current != null) {
                put(current.key, current.value);
                current = current.next;
            }
        }
    }
    
    public int size() {
        return size;
    }
}
```

### 🔍 Key Takeaways from the Code
1. **The `getIndex()` Method**: Notice how we mask the sign bit (`& 0x7FFFFFFF`) before applying the modulo operator. This prevents negative indices if `hashCode()` returns a negative integer.
2. **Insertion at Head**: When a collision occurs (and the key isn't already present), the new `Node` is inserted at the *head* of the linked list (`new Node<>(key, value, head)`). This is an O(1) insertion. (Note: Java 8 changed this to insert at the tail to prevent infinite loops in concurrent environments, but head insertion is simpler to implement).
3. **The Cost of `resize()`**: Look at the nested loops in `resize()`. It must iterate over every bucket, and then over every node in every chain, recalculating the hash index for the new table size. This illustrates why setting a good initial capacity is vital for performance-critical code.