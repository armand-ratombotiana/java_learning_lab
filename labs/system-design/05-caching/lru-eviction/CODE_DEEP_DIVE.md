# LRU Cache Code Deep Dive

This lab provides a pure Java implementation of an LRU Cache from scratch, demonstrating the classic HashMap + Doubly-Linked List architecture.

## 💻 Pure Java Implementation

```java file="labs/system-design/05-caching/lru-eviction/SOLUTION/LRUCache.java"
package systemdesign.caching;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A thread-safe, O(1) time complexity LRU Cache.
 */
public class LRUCache<K, V> {

    // Internal Node for the Doubly-Linked List
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<K, Node<K, V>> cache;
    
    // Dummy head and tail to simplify boundary conditions
    private final Node<K, V> head;
    private final Node<K, V> tail;
    
    // ReadWriteLock for high-concurrency thread safety
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0");
        }
        this.capacity = capacity;
        this.cache = new HashMap<>();
        
        // Initialize dummy head and tail
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Retrieves a value from the cache. O(1)
     */
    public V get(K key) {
        lock.writeLock().lock(); // Write lock needed because get() modifies the list order
        try {
            Node<K, V> node = cache.get(key);
            if (node == null) {
                return null; // Cache miss
            }
            
            // Cache hit: Move to head (most recently used)
            moveToHead(node);
            return node.value;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Inserts or updates a value in the cache. O(1)
     */
    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            Node<K, V> node = cache.get(key);
            
            if (node != null) {
                // Update existing node and move to head
                node.value = value;
                moveToHead(node);
            } else {
                // Create new node
                Node<K, V> newNode = new Node<>(key, value);
                cache.put(key, newNode);
                addToHead(newNode);
                
                // Evict if capacity exceeded
                if (cache.size() > capacity) {
                    Node<K, V> lru = popTail();
                    cache.remove(lru.key);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    // --- Doubly-Linked List Helper Methods ---

    private void addToHead(Node<K, V> node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node<K, V> node) {
        Node<K, V> prevNode = node.prev;
        Node<K, V> nextNode = node.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    private void moveToHead(Node<K, V> node) {
        removeNode(node);
        addToHead(node);
    }

    private Node<K, V> popTail() {
        Node<K, V> res = tail.prev;
        removeNode(res);
        return res;
    }
}
```

## 🔍 Key Takeaways
1. **Dummy Head and Tail**: Notice the empty `head` and `tail` nodes created in the constructor. This is a crucial design pattern for doubly-linked lists. It eliminates the need for complex `if (head == null)` null-checks during insertions and deletions, making the code much cleaner and less error-prone.
2. **The Concurrency Trap**: Notice that `get()` uses a `writeLock()`, not a `readLock()`. This is because in an LRU cache, a read operation modifies the internal state (it moves the node to the head of the list). Using a `readLock()` would cause data corruption if multiple threads read simultaneously.
3. **Java's Built-in Solution**: In Java, you rarely build this from scratch in production. You can use `LinkedHashMap`, overriding its `removeEldestEntry` method, to achieve the exact same behavior in 5 lines of code. However, building it from scratch is a mandatory rite of passage for understanding system design.