# Collections — Internal Mechanics

## ArrayList Internal Growth

```java
private Object[] grow(int minCapacity) {
    int oldCapacity = elementData.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1);  // 1.5× growth
    if (newCapacity - minCapacity < 0) newCapacity = minCapacity;
    if (newCapacity - MAX_ARRAY_SIZE > 0) newCapacity = hugeCapacity(minCapacity);
    return elementData = Arrays.copyOf(elementData, newCapacity);
}
```

- Growth factor of 1.5 balances memory usage and frequency of resizing
- Amortized O(1) for add operations
- Specifying initial capacity avoids unnecessary resizing

## HashMap Capacity and Resize

```java
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;  // 16
static final float DEFAULT_LOAD_FACTOR = 0.75f;

final Node<K,V>[] resize() {
    Node<K,V>[] oldTab = table;
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    int newCap = oldCap << 1;  // Double
    // ... create new table, rehash entries
}
```

- Capacity is always a power of 2 (enables fast `&` for index computation)
- Load factor 0.75 is a trade-off between space and time
- Rehashing is expensive — specify capacity if size is known

## HashMap Treeification

When a bucket exceeds TREEIFY_THRESHOLD (8), the linked list converts to a `TreeNode`:

```java
static final int TREEIFY_THRESHOLD = 8;
static final int UNTREEIFY_THRESHOLD = 6;
static final int MIN_TREEIFY_CAPACITY = 64;
```

- Tree nodes are 2× the size of regular nodes
- Only treeifies if table size ≥ 64 (otherwise just resizes)
- Degrades back to list when count drops below 6
- This prevents hash-collision DoS attacks (malicious keys with identical hash)

## LinkedHashMap Access Order

```java
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    public LRUCache(int maxSize) {
        super(16, 0.75f, true);  // access-order=true
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}
```

- Access order: after each get/put, the entry moves to the end of the linked list
- `removeEldestEntry()` is called after each put — the eldest entry can be evicted
- This is the foundation for LRU caches in Java

## Collections.unmodifiableList() Wrapper

```java
static class UnmodifiableList<E> extends UnmodifiableCollection<E> implements List<E> {
    private final List<? extends E> list;

    public E get(int index) { return list.get(index); }

    public boolean add(E e) { throw new UnsupportedOperationException(); }
    // All mutators throw UnsupportedOperationException
}
```

- Not a true immutable — the backing list can still be modified
- Changes to the backing list are visible through the wrapper
- True immutability: `List.of()` (Java 9+) or `stream.toList()` (Java 16+)

## Collections.synchronizedList()

```java
static class SynchronizedList<E> extends SynchronizedCollection<E> implements List<E> {
    public boolean add(E e) { synchronized (mutex) { return list.add(e); } }
    public E get(int index) { synchronized (mutex) { return list.get(index); } }
}
```

- Every method is synchronized on a mutex object
- Compound operations (iteration) still need external synchronization
- `ConcurrentHashMap` and `CopyOnWriteArrayList` are better choices for concurrent access
