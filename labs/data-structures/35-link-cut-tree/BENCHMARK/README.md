# Benchmark: Link-Cut Tree Performance Analysis

## Benchmark Scenarios
1. Basic operations: Time individual insert, delete, search, and update operations
2. Batch operations: Process 10K, 100K, and 1M items to measure throughput
3. Memory usage: Measure heap consumption with various load factors
4. Comparison: Against standard library equivalents and alternative implementations
5. Worst-case: Deliberately construct adversarial inputs to test degradation behavior

## Metrics
- Average, median, and p99 latency for each operation type
- Throughput in operations per second
- Memory overhead per element (structural metadata vs data)
- Cache misses and memory access patterns (if profiling tools available)
- Scalability with respect to input size (linear, log-linear, quadratic behavior)
