# Mini Project: JVM Flag Optimizer

## Objective
Build a tool that systematically tests JVM flag combinations, runs a benchmark, and reports the optimal configuration for a given workload.

## Requirements
1. Define a `Workload` interface with `void run()`
2. Create 3 workloads:
   - `CpuBoundWorkload` — pure computation (matrix multiply)
   - `MemoryBoundWorkload` — heavy allocation with survivor retention
   - `MixedWorkload` — combination of CPU + memory + I/O simulation
3. Define a set of JVM flags to test:
   - Heap: `-Xms`, `-Xmx`, `-Xmn`, `-XX:NewRatio`
   - GC: `-XX:+UseG1GC`, `-XX:+UseZGC`, `-XX:MaxGCPauseMillis`
   - Compiler: `-XX:CompileThreshold`, `-XX:-TieredCompilation`
4. Run each workload with each flag combination
5. Collect metrics: throughput, max pause time, total GC time, CPU usage
6. Report the Pareto-optimal configurations for each workload

## Architecture
```
JvmFlagOptimizer
  ├── WorkloadRunner (spawns JVM subprocess with flags)
  ├── MetricsCollector (parses stdout, GC logs)
  ├── ConfigGenerator (cartesian product of flag values)
  └── ReportGenerator (Pareto frontier, ranked configs)
```

## Example Output
```
=== Best Configurations for MemoryBoundWorkload ===
Rank | Config | Throughput | P99 Pause | GC Time
1 | -Xms4g -Xmx4g -XX:+UseZGC | 1420 ops/s | 2ms | 3.2%
2 | -Xms2g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=10 | 985 ops/s | 8ms | 5.1%
3 | -Xms2g -Xmx2g -XX:+UseParallelGC | 2100 ops/s | 45ms | 12.0%
```

## Deliverables
- `JvmFlagOptimizer.java` — main framework
- 3 workload implementations
- `OptimizerTest.java` — runs on a subset of flags
- Report with Pareto frontier analysis
