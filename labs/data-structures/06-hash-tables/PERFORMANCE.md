# Performance: Hash Tables

## Time Complexity

| Operation | Average | Worst Case |
|-----------|---------|------------|
| get | O(1) | O(n) |
| put | O(1)* | O(n) |
| remove | O(1) | O(n) |
| containsKey | O(1) | O(n) |
| containsValue | O(n) | O(n) |

\*Amortized (includes rehashing)

## Memory Comparison

| Structure | Memory (1M entries) | Notes |
|-----------|--------------------|-------|
| HashMap (default) | ~32 MB | Node overhead + arrays |
| LinkedHashMap | ~40 MB | Additional before/after references |
| TreeMap | ~40 MB | Red-Black node overhead |
| ConcurrentHashMap | ~32 MB | Slightly more due to segments |

## Load Factor Impact

| Load Factor | Memory | Performance | Use Case |
|------------|--------|------------|----------|
| 0.5 | High | Faster | Time-critical, many reads |
| 0.75 | Medium | Balanced | Default (Java) |
| 1.0 | Low | Slower | Memory-constrained |
| > 1.0 | Minimal | Very slow | Only for open addressing |

## Rehashing Cost

Resizing a HashMap with n entries:
- New array allocation: O(n) memory
- Rehashing all entries: O(n) time
- Occurs O(log n) times over n inserts
- Total rehashing cost: O(n) → amortized O(1) per insert

## Java HashMap Optimizations

### Hash Spreading
```java
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

### Power-of-2 Indexing
```java
// Instead of modulo (slow):
index = hash % length;
// Use bitwise AND (fast):
index = hash & (length - 1);  // requires length = power of 2
```

### Treeification Threshold
- Chain length < 8: linked list (simpler, faster for small chains)
- Chain length ≥ 8 AND capacity ≥ 64: Red-Black tree (O(log n) worst case)
- This prevents HashDoS attacks

## ConcurrentHashMap Performance

- **Java 7**: Segment-based locking (max 16 concurrent writers)
- **Java 8+**: CAS + synchronized on specific buckets (better concurrency)
- **Reads**: almost always lock-free
- **Writes**: lock only the affected bucket
