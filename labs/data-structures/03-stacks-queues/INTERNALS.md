# Internals: Java's ArrayDeque and PriorityQueue

## ArrayDeque Internals (Java 17+)

`java.util.ArrayDeque<E>` is the recommended stack/queue implementation.

```java
public class ArrayDeque<E> extends AbstractCollection<E>
    implements Deque<E>, Cloneable, Serializable {

    transient Object[] elements;  // power of 2 size
    transient int head;           // index of first element
    transient int tail;           // index where next addLast would insert
}
```

### Key Design Points

- **Power-of-2 capacity**: always a power of 2 (smallest ≥ requested)
- **Circular buffer**: elements wrap around when tail reaches the end
- **No null elements**: explicitly disallowed (`NullPointerException` on insert)
- **Bitwise index math**: uses `elements.length - 1` as mask for fast modulo

```java
public void addLast(E e) {
    if (e == null) throw new NullPointerException();
    elements[tail] = e;
    tail = (tail + 1) & (elements.length - 1);  // fast modulo
    if (tail == head) doubleCapacity();  // full
}

public E pollFirst() {
    int h = head;
    E result = (E) elements[h];
    if (result != null) {
        elements[h] = null;
        head = (h + 1) & (elements.length - 1);
    }
    return result;
}
```

The `& (len - 1)` trick works because `len` is a power of 2: `x & (2^k - 1) ≡ x % 2^k`.

### Resize

```java
private void doubleCapacity() {
    int p = head;
    int n = elements.length;
    int r = n - p;  // elements to the right of head
    int newCapacity = n << 1;  // double
    Object[] a = new Object[newCapacity];
    System.arraycopy(elements, p, a, 0, r);      // head → end
    System.arraycopy(elements, 0, a, r, p);       // start → head
    elements = a;
    head = 0;
    tail = n;  // n elements now occupy [0, n-1]
}
```

## PriorityQueue Internals

```java
public class PriorityQueue<E> extends AbstractQueue<E>
    implements java.io.Serializable {

    transient Object[] queue;  // binary heap array
    int size;
    private final Comparator<? super E> comparator;
}
```

### Heap Operations

```java
// bubble-up (on add)
private void siftUp(int k, E x) {
    while (k > 0) {
        int parent = (k - 1) >>> 1;  // (k-1)/2
        Object e = queue[parent];
        if (comparator.compare(x, (E) e) >= 0) break;
        queue[k] = e;
        k = parent;
    }
    queue[k] = x;
}

// bubble-down (on poll)
private void siftDown(int k, E x) {
    int half = size >>> 1;  // last non-leaf
    while (k < half) {
        int child = (k << 1) + 1;  // left child
        Object c = queue[child];
        int right = child + 1;
        if (right < size && comparator.compare((E) c, (E) queue[right]) > 0)
            c = queue[child = right];  // take smaller child
        if (comparator.compare(x, (E) c) <= 0) break;
        queue[k] = c;
        k = child;
    }
    queue[k] = x;
}
```

- **Default capacity**: 11
- **Growth**: `oldCapacity < 64 ? oldCapacity + 2 : oldCapacity >> 1` (50% growth for large)
- **Ordering**: head is the smallest element (min-heap), or as defined by Comparator
- **Not stable**: elements with equal priority have no guaranteed order
