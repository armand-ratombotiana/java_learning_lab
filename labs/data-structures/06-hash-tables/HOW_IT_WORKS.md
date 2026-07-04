# How Hash Tables Work

## Separate Chaining Implementation

```java
public class ChainingHashMap<K, V> {
    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;
        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Entry<K, V>[] buckets;
    private int size;
    private static final double LOAD_FACTOR = 0.75;

    @SuppressWarnings("unchecked")
    public ChainingHashMap(int capacity) {
        buckets = new Entry[capacity];
        size = 0;
    }

    public void put(K key, V value) {
        int index = index(key);
        Entry<K, V> current = buckets[index];
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                current.value = value;  // update existing
                return;
            }
            current = current.next;
        }
        // Not found — insert at head
        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next = buckets[index];
        buckets[index] = newEntry;
        size++;
        if ((double) size / buckets.length > LOAD_FACTOR) {
            resize(buckets.length * 2);
        }
    }

    public V get(K key) {
        int index = index(key);
        Entry<K, V> current = buckets[index];
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    private int index(K key) {
        int hash = key.hashCode();
        // Spread bits to reduce collisions
        hash ^= (hash >>> 16);
        return hash & (buckets.length - 1);
    }

    private void resize(int newCapacity) {
        // Rehash all entries into new bucket array
        ChainingHashMap<K, V> newMap = new ChainingHashMap<>(newCapacity);
        for (Entry<K, V> bucket : buckets) {
            Entry<K, V> current = bucket;
            while (current != null) {
                newMap.put(current.key, current.value);
                current = current.next;
            }
        }
        this.buckets = newMap.buckets;
    }
}
```

## Key Design Points

- **Index computation**: `hash & (length - 1)` requires power-of-2 length
- **Hash spreading**: `hash ^= (hash >>> 16)` mixes high bits into low bits
- **Load factor threshold**: triggers resize when exceeded
- **Head insertion**: O(1) insert at bucket head (in Java 8, tail insertion)
- **Rehashing**: all entries must be re-inserted; O(n) operation
