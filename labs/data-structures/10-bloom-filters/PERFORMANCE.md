# Performance: Bloom Filters

## Time Complexity

| Operation | Time | Notes |
|-----------|------|-------|
| Put | O(k) | k = number of hash functions |
| MightContain | O(k) | Worst-case: always checks all k bits |
| Space | O(m) | m bits (typically 8-16 per element) |

## Hash Function Performance

| Hash | Quality | Speed | Use Case |
|------|---------|-------|----------|
| MurmurHash3 | Excellent | Fast | General purpose |
| FNV-1a | Good | Very fast | Streaming |
| SHA-256 | Excellent | Very slow | Not recommended |
| CRC32 | Good | Fast | Hardware-accelerated |
| Java hashCode() | Poor | Fast | Not suitable alone |

## Memory Comparison

For 1M elements with 1% false positive rate:

| Structure | Memory | Notes |
|-----------|--------|-------|
| Bloom filter | ~12 MB | Fixed, independent of element size |
| HashSet<Integer> | ~64 MB | Object overhead |
| HashSet<String> | ~160 MB | 20-char strings + object overhead |
| TreeSet<Integer> | ~80 MB | Node overhead |
| BitSet (dense) | ~125 KB | If elements are dense integers |

## Optimal Parameters Guide

| Elements (n) | FPP | m (KB) | k |
|-------------|-----|--------|---|
| 1,000 | 1% | 1.2 | 7 |
| 10,000 | 1% | 12 | 7 |
| 100,000 | 1% | 120 | 7 |
| 1,000,000 | 1% | 1,200 | 7 |
| 10,000,000 | 1% | 12,000 | 7 |
| 1,000,000 | 0.1% | 1,800 | 10 |
| 1,000,000 | 0.01% | 2,400 | 14 |

## FPP vs Fill Ratio

```
Bits per element (m/n) | k_optimal | FPP
------------------------|-----------|-----
       4                |     3     | 15.3%
       6                |     4     | 5.6%
       8                |     6     | 2.1%
       10               |     7     | 0.8%
       12               |     8     | 0.3%
       14               |     10    | 0.1%
       16               |     11    | 0.04%
```

## Java Guava Performance

```java
// Creating and querying 1M elements
BloomFilter<String> filter = BloomFilter.create(funnel, 1_000_000, 0.01);
// Creation: ~100ms
// 1M puts: ~200ms
// 1M queries: ~150ms
```

## Profiling Notes

- Hash computation dominates runtime (not bit operations)
- MurmurHash3_128 is the default in Guava — fast and high-quality
- For extremely performance-critical code, implement custom hash using XOR-shift or FNV-1a
- `BitSet` operations are fast (single word operations at hardware level)
