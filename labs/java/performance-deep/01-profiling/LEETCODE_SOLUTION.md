# LeetCode 146: LRU Cache

> **Difficulty**: Medium | **Company**: Amazon, Google, Meta, Microsoft, Apple | **Category**: Performance Deep (Profiling / Data Structures)

## Problem

Design a data structure that follows the constraints of an **LRU (Least Recently Used) Cache**:

- `LRUCache(int capacity)`: Initialize the cache with positive capacity.
- `int get(int key)`: Return the value of the key if it exists, otherwise return -1.
- `void put(int key, int value)`: Update the value if key exists; otherwise add the key-value pair. If the cache reaches capacity, evict the least recently used key.

Both operations must run in **O(1)** average time.

## Solution

HashMap for O(1) key lookup + doubly linked list for O(1) insertion/removal in LRU order.

```java
import java.util.*;

/**
 * LeetCode 146: LRU Cache
 *
 * Time: O(1) for both get() and put()
 * Space: O(capacity)
 */
public class LRUCache {

    private static class Node {
        int key, value;
        Node prev, next;
        Node(int key, int value) { this.key = key; this.value = value; }
    }

    private final int capacity;
    private final Map<Integer, Node> map;
    private final Node head;  // dummy head (most recent)
    private final Node tail;  // dummy tail (least recent)

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        Node node = map.get(key);
        if (node == null) return -1;
        moveToHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        Node node = map.get(key);
        if (node != null) {
            node.value = value;
            moveToHead(node);
        } else {
            node = new Node(key, value);
            map.put(key, node);
            addToHead(node);
            if (map.size() > capacity) {
                Node removed = removeTail();
                map.remove(removed.key);
            }
        }
    }

    private void addToHead(Node node) {
        node.next = head.next;
        node.prev = head;
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

    private Node removeTail() {
        Node node = tail.prev;
        removeNode(node);
        return node;
    }

    // ─────────────────────
    // Verification
    // ─────────────────────
    public static void main(String[] args) {
        LRUCache cache = new LRUCache(2);
        cache.put(1, 1);
        cache.put(2, 2);
        assert cache.get(1) == 1 : "get(1)";
        cache.put(3, 3);           // evicts key 2
        assert cache.get(2) == -1 : "key 2 should be evicted";
        cache.put(4, 4);           // evicts key 1
        assert cache.get(1) == -1 : "key 1 should be evicted";
        assert cache.get(3) == 3 : "get(3)";
        assert cache.get(4) == 4 : "get(4)";

        // Edge: capacity = 1
        LRUCache c2 = new LRUCache(1);
        c2.put(1, 10);
        assert c2.get(1) == 10;
        c2.put(2, 20);
        assert c2.get(1) == -1;
        assert c2.get(2) == 20;

        System.out.println("All tests passed.");
    }
}
```

## Complexity

| Operation | Time | Space |
|-----------|------|-------|
| get       | O(1) | O(1)  |
| put       | O(1) | O(1)  |

**Overall space**: O(capacity)

## Key Insights

1. **HashMap + Doubly Linked List**: The classic O(1) LRU design. HashMap provides key-to-node lookup; the linked list maintains access order.
2. **Dummy sentinel nodes**: `head` and `tail` dummy nodes eliminate null checks and simplify edge cases.
3. **Eviction policy**: When capacity is exceeded, remove the node before `tail` (least recently used).
4. **`LinkedHashMap` shortcut**: JDK provides `LinkedHashMap` with `accessOrder=true` and `removeEldestEntry()` — can solve LRU in 5 lines.
