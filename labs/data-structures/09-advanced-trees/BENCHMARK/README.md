# Benchmark: Advanced Trees Performance

## Benchmark Scenarios

1. **Correctness baseline**: Verify operations against known results
2. **Throughput**: Operations per second under varying load
3. **Memory footprint**: Heap usage and object allocation
4. **Scalability**: Performance across input sizes (10² to 10⁶)

## Metrics

- Time per operation (ns)
- Operations per second
- Memory usage (bytes)
- Object allocations

## Run with

```bash
javac Benchmark.java && java Benchmark
```
