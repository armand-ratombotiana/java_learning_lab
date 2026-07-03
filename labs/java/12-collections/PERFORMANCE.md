# Collections — Performance Characteristics

## ArrayList Performance

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| add(E) | O(1) amortized | O(n) when resize needed |
| add(index, E) | O(n) | Shifts elements |
| get(index) | O(1) | Direct array access |
| remove(index) | O(n) | Shifts elements |
| contains(E) | O(n) | Linear search |
| iterator() | O(1) | Internal index |

**Optimization**: Specify initial capacity when size is known. Growth is 50% each resize.

## HashMap Performance

| Operation | Best | Worst | Average |
|-----------|------|-------|---------|
| put(K,V) | O(1) | O(n) | O(1) |
| get(K) | O(1) | O(n) | O(1) |
| containsKey(K) | O(1) | O(n) | O(1) |

**Load Factor**: 0.75 default. Trade-off between space and time.
- Lower (0.5): More memory, fewer collisions
- Higher (1.0): Less memory, more collisions

**Capacity**: Power of 2. Initial default 16. Specify for known sizes.

## TreeMap/TreeSet Performance

All operations: O(log n).

- Uses Red-Black tree: guarantees log n height
- 2× node memory overhead vs HashMap
- Maintains sorted order at all times

## LinkedList Performance

| Operation | Complexity | Notes |
|-----------|------------|-------|
| addFirst/addLast | O(1) | Pointer adjustment |
| get(index) | O(n) | Must traverse |
| remove(index) | O(n) | Must traverse + adjust |

**Memory**: ~24 bytes per element (prev/next pointers + object overhead). ~3× ArrayList overhead.

## ArrayDeque Performance

| Operation | Complexity | Notes |
|-----------|------------|-------|
| addFirst/addLast | O(1) amortized | Circular array |
| removeFirst/removeLast | O(1) | Index adjustment |
| get(index) | O(n) | Linear scan |

**Preferred** over LinkedList for stack/queue. Less memory, less GC pressure.

## HashSet Performance

Delegates to HashMap — same O(1) average, O(n) worst.

## ConcurrentHashMap Performance

- **Reads**: Fully concurrent (no locking on get)
- **Writes**: Striped locking (bucket-level)
- **Iteration**: Weakly consistent (no ConcurrentModificationException)
- **Performance**: ~10× better than synchronized HashMap in high-contention scenarios

## CopyOnWriteArrayList Performance

| Operation | Complexity | Notes |
|-----------|------------|-------|
| get | O(1) | No lock |
| add | O(n) | Copies entire array |
| iteration | O(n) | Snapshot — no CME |

**Only suitable** for read-heavy workloads with infrequent writes (e.g., listener lists).

## Optimization Tips

1. **Set initial capacity**: `new HashMap<>(expectedSize / 0.75f + 1)`
2. **Use EnumMap for enum keys**: O(1), no hashing, array-backed
3. **Prefer ArrayList over LinkedList**: Unless doing many addFirst/removeFirst
4. **Use ArrayDeque over Stack**: Stack synchronizes on every operation
5. **Batch operations**: `addAll()` single resize vs many individual adds
6. **Stream parallelism**: Only beneficial for large collections (>10K elements)
7. **Avoid boxing in collections**: Use `IntArrayList` from Eclipse Collections or Trove
