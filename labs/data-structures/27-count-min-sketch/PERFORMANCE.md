# Performance: Count-Min Sketch

## Time Complexity

| Operation | Average | Worst |
|-----------|---------|-------|
| Insert | O(log n) | O(n) |
| Lookup | O(log n) | O(n) |
| Delete | O(log n) | O(n) |
| Space | O(n) | O(n) |

## Performance Factors

Load factor, hash function quality, and memory hierarchy significantly impact performance.

## Comparison with Alternatives

Different trade-offs compared to standard library implementations.

## Optimization Strategies

Tune load factors, use efficient hash functions, minimize allocation, optimize memory layout, and profile first.
