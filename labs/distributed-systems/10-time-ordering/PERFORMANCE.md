# Time and Ordering: Performance

## Performance Comparison

| Clock Type    | Metadata Size | Comparison Cost | Update Cost |
|---------------|---------------|-----------------|-------------|
| Lamport       | 4-8 bytes     | O(1)            | O(1)        |
| Vector        | O(N) bytes    | O(N)            | O(N)        |
| HLC           | 16 bytes      | O(1)            | O(1)        |
| TrueTime      | 16 bytes      | O(1)            | O(1)        |

## Scaling Concerns
- Vector clocks: N=1000 → 4KB per message (with 4-byte counters)
- Lamport clocks: always 8 bytes regardless of N
- HLC: 2 × 64-bit values, constant size

## Optimization
- **Dot clocks**: Instead of full vector, send only process ID + counter
- **Version vectors**: For storage systems with known process set
- **Interval tree clocks**: O(log N) size for large N
- **Pruning**: Remove resolved entries from vector clocks
