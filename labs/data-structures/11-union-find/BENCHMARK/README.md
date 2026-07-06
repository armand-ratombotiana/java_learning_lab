# Benchmark: Union-Find Performance

## Benchmark Scenarios

1. **Sequential unions**: Union(0,1), union(1,2), ..., union(n-2, n-1)
2. **Random unions**: Union random pairs, 2n operations
3. **Mixed operations**: Alternate union and find
4. **Find-heavy**: 90% find, 10% union
5. **Worst-case**: Union in a pattern causing tall trees (no optimizations)

## Metrics

- Time per operation (ns)
- Operations per second
- Maximum tree depth
- Memory usage (bytes)
- Cache misses

## Run with

```bash
javac Benchmark.java && java Benchmark
```
