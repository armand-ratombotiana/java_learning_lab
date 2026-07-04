# Internals

## JVM Array Representation

In HotSpot JVM, every array object has:

| Offset | Size | Field |
|--------|------|-------|
| 0      | 8    | Mark word (identity hash, lock info) |
| 8      | 4/8  | Klass* pointer (type metadata) |
| 12/16  | 4    | `length` (int) |
| 16/24  | varies | Element data |

Alignment is 8 bytes on 64-bit with compressed OOPs (default), 16 bytes without.

## System.arraycopy

This is a **native method** — the JVM replaces it with an intrinsic:

```java
public static native void arraycopy(
    Object src,  int srcPos,
    Object dest, int destPos,
    int length
);
```

Behind the scenes, HotSpot generates inline `rep movs` (x86) or equivalent SIMD instructions. It handles overlapping regions correctly (like `memmove`).

## Arrays Utility Class

`java.util.Arrays` provides:

- **Sorting**: `sort()` — dual-pivot quicksort (primitives), TimSort (objects)
- **Search**: `binarySearch()` — O(log n), requires sorted input
- **Copy**: `copyOf()`, `copyOfRange()` — wraps `arraycopy`
- **Fill**: `fill()` — sets all elements to a value
- **Comparison**: `equals()`, `deepEquals()`, `compare()`, `mismatch()`
- **Stream**: `stream()` — creates an IntStream, LongStream, DoubleStream
- **Parallel**: `parallelSort()`, `parallelPrefix()` — uses ForkJoinPool

## ArrayList Internals (Java 17+)

```java
public class ArrayList<E> extends AbstractList<E>
    implements List<E>, RandomAccess, Cloneable, java.io.Serializable {

    private static final int DEFAULT_CAPACITY = 10;
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    transient Object[] elementData;  // not serialized
    private int size;
}
```

- **Growth**: `oldCapacity + (oldCapacity >> 1)` — 1.5x (Java 6+)
- **Lazy initialization**: empty ArrayList uses `DEFAULTCAPACITY_EMPTY_ELEMENTDATA`; first `add()` allocates to 10
- **Fail-fast iterators**: `modCount` tracked; `ConcurrentModificationException` on concurrent modification
- **SubList**: returns a view backed by the original array; structural changes invalidate both
