# Performance — Randomized Algorithms

## Algorithm Comparison

| Algorithm | Expected Time | Worst Case | Space |
|-----------|--------------|------------|-------|
| Quickselect | O(n) | O(n^2) | O(1) |
| Fisher-Yates | O(n) | O(n) | O(1) |
| Reservoir | O(n) | O(n) | O(k) |
| Freivalds | O(n^2) | O(n^2) | O(n) |
| Karger | O(n^4 log n) | O(n^4 log n) | O(n^2) |

## Benchmark Data

- Quickselect on 10^7 elements: ~50ms expected
- Fisher-Yates on 10^7 elements: ~80ms
- Reservoir sampling of 1000 from 10^7: ~30ms
- Freivalds on 1000x1000 matrix: ~5ms per test
- Karger on 1000-node graph: several seconds

## Optimization Tips

- Use ThreadLocalRandom for thread-local access
- Replace recursion with iteration for quickselect
- Use arrays of primitives instead of boxed types
- For Karger, use adjacency lists and union-find for efficient contraction
