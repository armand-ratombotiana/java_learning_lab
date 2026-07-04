# Performance of Arrays

## Time Complexity

| Operation | Static Array | Dynamic Array (amortized) |
|-----------|-------------|--------------------------|
| Read by index | O(1) | O(1) |
| Write by index | O(1) | O(1) |
| Insert at end | N/A | O(1) |
| Insert at beginning | N/A | O(n) |
| Insert at middle | N/A | O(n) |
| Delete at end | N/A | O(1) |
| Delete at beginning | N/A | O(n) |
| Delete at middle | N/A | O(n) |
| Linear search | O(n) | O(n) |
| Binary search (sorted) | O(log n) | O(log n) |

## Space Complexity

- Static: O(n) — exactly n × element_size
- Dynamic: O(n) — between n and 2n (typical growth factor)

## JVM Optimizations

### Intrinsics
- `System.arraycopy` → `rep movs` (x86) or SIMD
- `Arrays.copyOf`, `Arrays.fill` → inline intrinsics
- `Arrays.sort` → Dual-Pivot Quicksort (primitives), TimSort (objects)

### Escape Analysis
Small arrays may be allocated on the stack if they never escape the method.

### Cache Performance
- Sequential read: ~1 CPU cycle per element (L1 hit)
- Random access in large array: many cache misses
- Stride-1 access automatically prefetched

## ArrayList vs LinkedList Performance

```java
// ArrayList — contiguous memory, CPU cache friendly
List<Integer> arrayList = new ArrayList<>();

// LinkedList — scattered nodes, cache misses
List<Integer> linkedList = new LinkedList<>();
```

For 100,000 random accesses:
- ArrayList: ~2 ms
- LinkedList: ~5,000 ms (O(n) traversal to find each element)

## Profiling Tips

- Use `-XX:+PrintCompilation` to verify intrinsics are used
- Use JMH for microbenchmarks
- Watch for boxing overhead: `int[]` vs `Integer[]`
