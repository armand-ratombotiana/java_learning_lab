# LeetCode 706: Design HashMap

> **Difficulty**: Easy | **Company**: Amazon, Google, Microsoft, Meta | **Category**: Collections Deep

## Problem

Design a HashMap without using any built-in hash table libraries. Implement `MyHashMap` with:

- `put(key, value)`: Insert a (key, value) pair. Update value if key already exists.
- `get(key)`: Return the value mapped to the key, or -1 if no mapping exists.
- `remove(key)`: Remove the key and its value if present.

## Solution

Implements separate chaining with an array of buckets. Each bucket is a linked list of nodes.

```java
import java.util.Arrays;
import java.util.Objects;

/**
 * LeetCode 706: Design HashMap
 * 
 * Time: O(1) average per operation, O(n) worst-case (all keys collide)
 * Space: O(capacity + keys)
 */
public class MyHashMap {

    private static final int INITIAL_CAP = 1 << 4;  // 16
    private static final double LOAD_FACTOR = 0.75;

    private Node[] buckets;
    private int size;

    private static class Node {
        final int key;
        int value;
        Node next;
        Node(int key, int value) { this.key = key; this.value = value; }
    }

    public MyHashMap() {
        buckets = new Node[INITIAL_CAP];
        size = 0;
    }

    private int hash(int key) {
        return key ^ (key >>> 16);
    }

    private int index(int key) {
        return hash(key) & (buckets.length - 1);
    }

    public void put(int key, int value) {
        int idx = index(key);
        Node head = buckets[idx];
        if (head == null) {
            buckets[idx] = new Node(key, value);
            size++;
        } else {
            Node cur = head;
            while (true) {
                if (cur.key == key) { cur.value = value; return; }
                if (cur.next == null) break;
                cur = cur.next;
            }
            cur.next = new Node(key, value);
            size++;
        }
        if ((double) size / buckets.length > LOAD_FACTOR) resize();
    }

    public int get(int key) {
        Node cur = buckets[index(key)];
        while (cur != null) {
            if (cur.key == key) return cur.value;
            cur = cur.next;
        }
        return -1;
    }

    public void remove(int key) {
        int idx = index(key);
        Node cur = buckets[idx];
        if (cur == null) return;
        if (cur.key == key) {
            buckets[idx] = cur.next;
            size--;
            return;
        }
        while (cur.next != null) {
            if (cur.next.key == key) {
                cur.next = cur.next.next;
                size--;
                return;
            }
            cur = cur.next;
        }
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node[] old = buckets;
        buckets = new Node[old.length << 1];
        size = 0;
        for (Node head : old) {
            Node cur = head;
            while (cur != null) {
                put(cur.key, cur.value);
                cur = cur.next;
            }
        }
    }

    // ─────────────────────
    // Verification
    // ─────────────────────
    public static void main(String[] args) {
        MyHashMap map = new MyHashMap();
        map.put(1, 1);
        map.put(2, 2);
        assert map.get(1) == 1 : "get(1) should be 1";
        assert map.get(3) == -1 : "get(3) should be -1";
        map.put(2, 1);
        assert map.get(2) == 1 : "get(2) should be 1 (updated)";
        map.remove(2);
        assert map.get(2) == -1 : "get(2) should be -1 after remove";

        // Collision test (same bucket)
        map.put(0, 100);
        map.put(16, 200);  // same bucket as 0 when cap = 16
        assert map.get(0) == 100 : "get(0)";
        assert map.get(16) == 200 : "get(16)";

        // Resize test
        for (int i = 0; i < 20; i++) map.put(i, i * 10);
        for (int i = 0; i < 20; i++) assert map.get(i) == i * 10 : "get(" + i + ") after resize";

        System.out.println("All tests passed.");
    }
}
```

## Complexity

| Operation | Average | Worst Case |
|-----------|---------|------------|
| put       | O(1)    | O(n)       |
| get       | O(1)    | O(n)       |
| remove    | O(1)    | O(n)       |

**Space**: O(capacity + entries) — dominated by the bucket array and stored key-value pairs.

## Key Insights

1. **Hash function**: `key ^ (key >>> 16)` XORs high bits into low bits for better distribution.
2. **Index calculation**: `hash & (capacity - 1)` is a fast modulo when capacity is a power of two.
3. **Separate chaining**: Each bucket holds a linked list; handles collisions gracefully.
4. **Load factor & resize**: When size exceeds `capacity * loadFactor`, double capacity and rehash all entries.
