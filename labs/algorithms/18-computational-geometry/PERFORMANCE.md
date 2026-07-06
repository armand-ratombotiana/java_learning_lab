# Performance — Computational Geometry

## Algorithm Comparison

| Algorithm | Complexity | Notes |
|-----------|-----------|-------|
| Graham Scan | O(n log n) | Requires polar angle sorting |
| Monotone Chain | O(n log n) | More stable, avoids trig functions |
| Jarvis March | O(nh) | Fast when hull h is small |
| Quickhull | O(n log n) avg | Worst-case O(n^2) |
| Closest Pair | O(n log n) | Divide and conquer |
| Line Intersection (sweep) | O((n+k) log n) | k = number of intersections |

## Benchmark Data

For 1 million random points:
- Monotone chain: ~200ms
- Graham Scan: ~350ms (due to polar angle)
- Closest pair: ~500ms
- Naive O(n^2): > 1 hour

## Optimization Tips

- Use primitive double arrays instead of Point objects for tight loops
- Pre-allocate array lists with expected capacity
- Use iterative (non-recursive) divide and conquer for closest pair
