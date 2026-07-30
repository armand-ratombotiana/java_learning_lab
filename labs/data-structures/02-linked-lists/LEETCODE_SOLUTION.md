# LRU Cache (LeetCode 146)

**Problem:** Design a data structure that follows the constraints of a **Least Recently Used (LRU) cache**.

Implement `LRUCache`:

- `LRUCache(int capacity)` — Initialize the LRU cache with positive size capacity.
- `int get(int key)` — Return the value of the key if the key exists, otherwise return -1.
- `void put(int key, int value)` — Update the value of the key if the key exists. Otherwise, add the key-value pair to the cache. If the number of keys exceeds the capacity from this operation, **evict the least recently used key**.

**Constraints:** `O(1)` average time complexity for both `get` and `put`.

## Java Solution

```java
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * LRU Cache implementation using a doubly linked list + HashMap.
 *
 * <p>The doubly linked list maintains the order of usage: the head is the
 * <b>most recently used</b> node and the tail is the <b>least recently used</b>
 * node. The HashMap provides O(1) key-to-node lookup.</p>
 *
 * <h2>Complexity Analysis</h2>
 * <ul>
 *   <li><b>get(key)</b> — O(1) average</li>
 *   <li><b>put(key, value)</b> — O(1) average</li>
 * </ul>
 *
 * <b>Space:</b> O(capacity)
 */
public class LRUCache {

    private static class Node {
        int key;
        int value;
        Node prev;
        Node next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> map;
    private final Node head; // dummy head
    private final Node tail; // dummy tail

    /**
     * Constructs an LRU cache with the given capacity.
     *
     * @param capacity the maximum number of entries
     * @throws IllegalArgumentException if capacity is not positive
     */
    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.map = new HashMap<>(capacity);
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Returns the value for the given key, or -1 if not present.
     * Marks the key as most recently used.
     *
     * @param key the lookup key
     * @return the associated value, or -1
     */
    public int get(int key) {
        Node node = map.get(key);
        if (node == null) {
            return -1;
        }
        moveToHead(node);
        return node.value;
    }

    /**
     * Inserts or updates a key-value pair. Evicts the least recently used
     * entry if the cache is at capacity.
     *
     * @param key   the key
     * @param value the associated value
     */
    public void put(int key, int value) {
        Node node = map.get(key);
        if (node != null) {
            node.value = value;
            moveToHead(node);
            return;
        }
        if (map.size() == capacity) {
            Node lru = tail.prev;
            removeNode(lru);
            map.remove(lru.key);
        }
        Node newNode = new Node(key, value);
        map.put(key, newNode);
        addToHead(newNode);
    }

    // ---- Doubly linked list helpers ----

    private void addToHead(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }

    /**
     * Returns the current number of entries in the cache.
     * Package-private for testing.
     */
    int size() {
        return map.size();
    }
}
```

## Test Cases

```java
/**
 * Unit tests for LRUCache.
 */
public class LRUCacheTest {

    public static void main(String[] args) {
        // --- Test 1: Basic put/get ---
        LRUCache cache = new LRUCache(2);
        cache.put(1, 10);
        cache.put(2, 20);
        assert cache.get(1) == 10 : "get(1) should be 10";
        assert cache.get(2) == 20 : "get(2) should be 20";

        // --- Test 2: Eviction (LRU) ---
        // cache: [1,2], access 1 -> [2,1], put 3 evicts 2 -> [1,3]
        cache.get(1);
        cache.put(3, 30);
        assert cache.get(2) == -1 : "key 2 should have been evicted";
        assert cache.get(1) == 10 : "key 1 should still be present";
        assert cache.get(3) == 30 : "key 3 should be present";

        // --- Test 3: Update existing key ---
        LRUCache cache2 = new LRUCache(2);
        cache2.put(1, 100);
        cache2.put(1, 200);
        assert cache2.get(1) == 200 : "value should be updated to 200";

        // --- Test 4: Evicts correct key after update ---
        LRUCache cache3 = new LRUCache(2);
        cache3.put(1, 1);
        cache3.put(2, 2);
        // update key 1, so key 2 becomes LRU
        cache3.put(1, 10);
        cache3.put(3, 3);
        assert cache3.get(2) == -1 : "key 2 should be evicted";
        assert cache3.get(1) == 10 : "key 1 should be present with updated value";
        assert cache3.get(3) == 3 : "key 3 should be present";

        // --- Test 5: Capacity 1 ---
        LRUCache cache4 = new LRUCache(1);
        cache4.put(1, 1);
        assert cache4.get(1) == 1 : "get(1) should be 1";
        cache4.put(2, 2); // evicts 1
        assert cache4.get(1) == -1 : "key 1 should be evicted";
        assert cache4.get(2) == 2 : "key 2 should be present";

        // --- Test 6: get non-existent key ---
        LRUCache cache5 = new LRUCache(3);
        assert cache5.get(999) == -1 : "non-existent key should return -1";

        // --- Test 7: Eviction order ---
        LRUCache cache6 = new LRUCache(3);
        cache6.put(1, 1);
        cache6.put(2, 2);
        cache6.put(3, 3);
        cache6.get(1);
        cache6.get(2);
        // order of recency: 3 (least) -> 1 -> 2 (most)
        cache6.put(4, 4); // evicts 3
        assert cache6.get(3) == -1 : "key 3 should be evicted";
        assert cache6.get(1) == 1 : "key 1 should be present";
        assert cache6.get(2) == 2 : "key 2 should be present";
        assert cache6.get(4) == 4 : "key 4 should be present";

        // --- Test 8: Size after operations ---
        LRUCache cache7 = new LRUCache(5);
        for (int i = 1; i <= 5; i++) cache7.put(i, i * 10);
        assert cache7.size() == 5 : "size should be 5";
        cache7.put(6, 60); // evicts key 1
        assert cache7.size() == 5 : "size should remain 5 after eviction";

        System.out.println("All LRUCache tests passed!");
    }
}
```
