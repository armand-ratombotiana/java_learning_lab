# Design HashMap (LeetCode 706)

**Problem:** Design a HashMap without using any built-in hash table libraries. Support the following operations:

- `MyHashMap()` — Initializes the object.
- `void put(int key, int value)` — Inserts a key-value pair. If the key already exists, update the value.
- `int get(int key)` — Returns the value for the key, or -1 if not found.
- `void remove(int key)` — Removes the key and its value if present.

**Requirements:** Use separate chaining for collision resolution and implement a load-factor-based resize mechanism.

## Java Solution

```java
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * HashMap implementation using separate chaining with linked lists.
 *
 * <p>Each bucket is a linked list of Entry nodes. When the load factor exceeds
 * 0.75, the table is resized (doubled) and all entries are rehashed.</p>
 *
 * <h2>Complexity Analysis</h2>
 * <ul>
 *   <li><b>put(key, value)</b> — O(1) average, O(n) worst-case</li>
 *   <li><b>get(key)</b> — O(1) average, O(n) worst-case</li>
 *   <li><b>remove(key)</b> — O(1) average, O(n) worst-case</li>
 * </ul>
 *
 * <b>Space:</b> O(n) where n is the number of key-value mappings
 */
public class MyHashMap {

    private static final int INITIAL_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private static class Entry {
        int key;
        int value;
        Entry next;

        Entry(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private Entry[] buckets;
    private int size;

    /** Constructs an empty HashMap. */
    public MyHashMap() {
        this.buckets = new Entry[INITIAL_CAPACITY];
        this.size = 0;
    }

    /**
     * Associates the specified value with the specified key.
     * If the key already exists, the old value is replaced.
     *
     * @param key   the key
     * @param value the value to associate
     */
    public void put(int key, int value) {
        int index = hash(key);
        Entry head = buckets[index];

        // Search for existing key in chain
        Entry curr = head;
        while (curr != null) {
            if (curr.key == key) {
                curr.value = value;
                return;
            }
            curr = curr.next;
        }

        // Key not found - insert at head
        Entry newEntry = new Entry(key, value);
        newEntry.next = head;
        buckes[index] = newEntry;
        size++;

        // Resize if load factor exceeded
        if ((double) size / buckets.length >= LOAD_FACTOR) {
            resize();
        }
    }

    /**
     * Returns the value for the given key, or -1 if not found.
     *
     * @param key the key to look up
     * @return the associated value, or -1
     */
    public int get(int key) {
        int index = hash(key);
        Entry curr = buckets[index];
        while (curr != null) {
            if (curr.key == key) {
                return curr.value;
            }
            curr = curr.next;
        }
        return -1;
    }

    /**
     * Removes the mapping for the given key if present.
     *
     * @param key the key to remove
     */
    public void remove(int key) {
        int index = hash(key);
        Entry curr = buckets[index];
        Entry prev = null;

        while (curr != null) {
            if (curr.key == key) {
                if (prev == null) {
                    buckets[index] = curr.next;
                } else {
                    prev.next = curr.next;
                }
                size--;
                return;
            }
            prev = curr;
            curr = curr.next;
        }
    }

    /**
     * Returns the number of key-value mappings.
     *
     * @return current size
     */
    public int size() {
        return size;
    }

    // ---- Internal helpers ----

    private int hash(int key) {
        // Use key's hashCode (the int itself) and ensure non-negative
        int h = Integer.hashCode(key);
        return (h ^ (h >>> 16)) & (buckets.length - 1);
    }

    private void resize() {
        Entry[] oldBuckets = buckets;
        buckets = new Entry[oldBuckets.length * 2];
        size = 0;

        for (Entry head : oldBuckets) {
            Entry curr = head;
            while (curr != null) {
                put(curr.key, curr.value);
                curr = curr.next;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Entry bucket : buckets) {
            Entry curr = bucket;
            while (curr != null) {
                if (!first) sb.append(", ");
                sb.append(curr.key).append("=").append(curr.value);
                first = false;
                curr = curr.next;
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
```

## Test Cases

```java
/**
 * Unit tests for MyHashMap.
 */
public class MyHashMapTest {

    public static void main(String[] args) {
        // --- Test 1: Basic put/get ---
        MyHashMap map = new MyHashMap();
        map.put(1, 10);
        map.put(2, 20);
        assert map.get(1) == 10 : "get(1) should be 10";
        assert map.get(2) == 20 : "get(2) should be 20";
        assert map.get(3) == -1 : "get(3) should be -1";
        assert map.size() == 2 : "size should be 2";

        // --- Test 2: Update existing key ---
        map.put(1, 100);
        assert map.get(1) == 100 : "get(1) should be 100 after update";
        assert map.size() == 2 : "size should still be 2";

        // --- Test 3: Remove ---
        map.remove(1);
        assert map.get(1) == -1 : "get(1) should be -1 after remove";
        assert map.size() == 1 : "size should be 1";

        // --- Test 4: Remove non-existent key ---
        map.remove(999); // should not throw
        assert map.size() == 1 : "size should still be 1";

        // --- Test 5: Collision handling ---
        // Force keys that hash to same bucket (use integer hash distribution)
        MyHashMap map2 = new MyHashMap();
        for (int i = 0; i < 100; i++) {
            map2.put(i, i * 10);
        }
        assert map2.size() == 100 : "size should be 100";
        for (int i = 0; i < 100; i++) {
            assert map2.get(i) == i * 10 : "value should match for key " + i;
        }

        // --- Test 6: Remove from chain ---
        map2.remove(50);
        assert map2.get(50) == -1 : "key 50 should be removed";
        assert map2.size() == 99 : "size should be 99";

        // --- Test 7: Large number of operations (resize trigger) ---
        MyHashMap map3 = new MyHashMap();
        for (int i = 1; i <= 1000; i++) {
            map3.put(i, i);
        }
        assert map3.size() == 1000 : "size should be 1000";
        for (int i = 1; i <= 1000; i++) {
            assert map3.get(i) == i : "value should match for key " + i;
        }
        // Check that resizing didn't break anything
        for (int i = 1; i <= 500; i++) {
            map3.remove(i);
        }
        assert map3.size() == 500 : "size should be 500 after removals";

        // --- Test 8: Negative keys ---
        MyHashMap map4 = new MyHashMap();
        map4.put(-1, 100);
        map4.put(-2, 200);
        assert map4.get(-1) == 100 : "negative key -1 should work";
        assert map4.get(-2) == 200 : "negative key -2 should work";
        assert map4.size() == 2 : "size should be 2";

        // --- Test 9: Key 0 ---
        MyHashMap map5 = new MyHashMap();
        map5.put(0, 999);
        assert map5.get(0) == 999 : "key 0 should work";

        // --- Test 10: Overwrite after partial remove ---
        MyHashMap map6 = new MyHashMap();
        map6.put(1, 1); map6.put(2, 2); map6.put(3, 3);
        map6.remove(2);
        map6.put(2, 22);
        assert map6.get(2) == 22 : "value should be updated after remove + put";

        System.out.println("All MyHashMap tests passed!");
    }
}
```
