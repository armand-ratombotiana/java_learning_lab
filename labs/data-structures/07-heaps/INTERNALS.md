# Internals: Java PriorityQueue

## PriorityQueue (Java 17+)

```java
public class PriorityQueue<E> extends AbstractQueue<E>
    implements java.io.Serializable {

    private static final int DEFAULT_INITIAL_CAPACITY = 11;
    transient Object[] queue;  // binary heap array
    private int size = 0;
    private final Comparator<? super E> comparator;

    // Min-heap by default (natural ordering)
    // Max-heap via Comparator.reverseOrder()
}
```

### SiftUp (bubble up)

```java
private void siftUpComparable(int k, E x) {
    Comparable<? super E> key = (Comparable<? super E>) x;
    while (k > 0) {
        int parent = (k - 1) >>> 1;  // (k-1)/2
        Object e = queue[parent];
        if (key.compareTo((E) e) >= 0) break;
        queue[k] = e;
        k = parent;
    }
    queue[k] = key;
}
```

### SiftDown (bubble down)

```java
private void siftDownComparable(int k, E x) {
    Comparable<? super E> key = (Comparable<? super E>) x;
    int half = size >>> 1;  // loop while non-leaf
    while (k < half) {
        int child = (k << 1) + 1;  // left child
        Object c = queue[child];
        int right = child + 1;
        if (right < size && ((Comparable<? super E>) c).compareTo(
                (E) queue[right]) > 0)
            c = queue[child = right];  // smaller child
        if (key.compareTo((E) c) <= 0) break;
        queue[k] = c;
        k = child;
    }
    queue[k] = key;
}
```

### Grow

```java
private void grow(int minCapacity) {
    int oldCapacity = queue.length;
    int newCapacity = oldCapacity + (
        oldCapacity < 64 ? oldCapacity + 2 : oldCapacity >> 1
    );
    if (newCapacity > MAX_ARRAY_SIZE) newCapacity = hugeCapacity(minCapacity);
    queue = Arrays.copyOf(queue, newCapacity);
}
```

### Key Design Decisions

- **Array-backed**: cache-friendly, contiguous memory
- **Default comparator**: natural order (min-heap)
- **Max-heap**: `new PriorityQueue<>(Comparator.reverseOrder())`
- **Not stable**: equal-priority elements have no guaranteed order
- **O(n) remove(Object)**: linear scan + siftUp/siftDown
- **Not thread-safe**: use `PriorityBlockingQueue` for concurrency

### Iterator

Returns elements in no particular order. To traverse in priority order, repeatedly call `poll()`.

### Serialization

Writes array, size, and comparator. Deserialization rebuilds the heap.
