# Collections — Debugging Strategies

## Common Runtime Exceptions

### ConcurrentModificationException

**Cause**: Modifying a collection while iterating over it (except via Iterator.remove()).

**Debug**: The exception is thrown on the *next* access to the iterator after modification, not at the modification point. Use `-ea` (enable assertions) and print thread stack traces to identify the modifier.

**Fix**: Use `removeIf()`, `Iterator.remove()`, or `Collectors.toList()` for modification during iteration.

### NullPointerException in TreeSet/TreeMap

**Cause**: Adding null to a sorted collection when the comparator doesn't support null.

**Debug**: Examine the stack trace; typically at the comparison site.

**Fix**: Use `Comparator.nullsFirst()` or `Comparator.nullsLast()` wrappers.

### UnsupportedOperationException

**Cause**: Calling a mutating method on an unmodifiable collection.

**Debug**: Check if the collection was created with `List.of()`, `Set.of()`, `Collections.unmodifiableList()`, or `stream.toList()`.

**Fix**: Copy to a mutable collection first: `new ArrayList<>(immutable)`.

## HashSet/ HashMap Debugging

### Mystery: Element in Set but contains() returns false

**Cause**: The element's hashCode changed after being added to the set.

**Debug**:
```java
// Show hash codes:
System.out.println("hash at add time: " + element.hashCode());
// Compare current hash code:
System.out.println("hash now: " + element.hashCode());
```

**Fix**: Use immutable keys/elements or never mutate fields used in hashCode()/equals().

### Poor HashMap Performance

**Cause**: Many hash collisions (bad hashCode() distribution).

**Debug**:
```java
// Check bucket distribution (Java 8+):
// Not directly visible, but count collisions:
int collisions = 0;
for (Node<?,?> n : map) {
    int bucketCount = countNodesInBucket(n);
    if (bucketCount > 1) collisions += bucketCount - 1;
}
```

**Fix**: Improve hashCode() implementation or use a better key type.

## IntelliJ Debug Tips

1. **Collection View**: Debugger shows collections as expandable trees with toString representations
2. **Evaluate Expression**: `list.stream().filter(...).collect(toList())` in debugger
3. **Custom renderers**: Configure collection renderers for custom collection types
4. **Breakpoints on Modification**: Set field watchpoints on the backing array

## Profiling Collection Usage

```bash
# Heap dump analysis (find collection issues):
jmap -dump:live,format=b,file=heap.bin <pid>
# Use Eclipse MAT or VisualVM to analyze

# GC overhead from excessive boxing in collections:
-XX:+PrintGCDetails -XX:+PrintGCTimeStamps
```

## Collection Memory Leaks

```java
// Memory leak: Map used as cache with no eviction
Map<String, Data> cache = new HashMap<>();
// Data is never removed — eventually OutOfMemoryError

// Fix: Use WeakHashMap or Guava Cache
Map<String, Data> cache = new WeakHashMap<>();
// OR LinkedHashMap with removeEldestEntry
```
