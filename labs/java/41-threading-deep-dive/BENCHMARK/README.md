# Benchmarks: Threading Constructs

## Benchmark 1: ThreadPoolExecutor vs ForkJoinPool
Compare throughput for CPU-bound and I/O-bound workloads.

| Pool Type | CPU-bound (100K tasks) | I/O-bound (simulated 10ms) |
|-----------|----------------------|---------------------------|
| ThreadPoolExecutor (4 threads) | TBD | TBD |
| ForkJoinPool (commonPool) | TBD | TBD |
| WorkStealingPool (custom) | TBD | TBD |

## Benchmark 2: CompletableFuture Stages
Measure overhead of chaining stages:
- thenApply chain of 10 stages
- thenApply chain of 100 stages
- thenCompose vs nested get()

## Benchmark 3: Structured Concurrency
Compare StructuredTaskScope.ShutdownOnSuccess vs CompletableFuture.anyOf:
- 5 concurrent tasks, 1 succeeds immediately
- 5 concurrent tasks, all succeed with varying latencies

## Running Benchmarks
Use Java Microbenchmark Harness (JMH) or simple `System.nanoTime()` measurements:
```bash
java -jar benchmarks.jar -wi 5 -i 10 -f 1
```

## Key Metrics
- Throughput (tasks/second)
- Latency p50, p99 (ms)
- Thread utilization (%)
- Context switch rate (per second)
