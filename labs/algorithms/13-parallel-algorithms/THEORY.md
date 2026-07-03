# Parallel Algorithms — Theoretical Foundation

## Parallelism Models

### Data Parallelism
Same operation on different data elements (SIMD)

### Task Parallelism
Different operations on same or different data (MIMD)

## Fork/Join Model
- Fork: Split task into subtasks
- Join: Wait for subtasks and combine results
- Work stealing: Idle threads steal work from busy threads

## Key Metrics
- **Speedup**: S(p) = T(1) / T(p)
- **Efficiency**: E(p) = S(p) / p
- **Amdahl's Law**: Maximum speedup = 1 / (1 - P + P/p)
  where P = parallelizable fraction

## Granularity
- Too coarse: Poor load balancing
- Too fine: Overhead dominates computation
- Sweet spot: Task size 10μs-100μs
