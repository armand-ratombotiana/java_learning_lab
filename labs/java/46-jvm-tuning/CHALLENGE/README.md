# Challenge: Tune a JVM for Minimum Latency

## Problem
Given a latency-sensitive Java application, tune the JVM flags to achieve minimum possible P99 latency. The application is a high-frequency trading (HFT) simulation that processes market data ticks.

## Application Profile
```java
// Each tick must be processed in < 1 microsecond (not including GC)
class MarketTick {
    long timestamp;
    String symbol;
    double price;
    int volume;
}
// 1 million ticks/second, processed in batches of 10,000
// 90% of ticks discarded after processing (short-lived)
// 10% retained in an order book (medium-lived)
```

## Tuning Constraints
- Heap: max 4 GB
- CPUs: 2 cores (shared with other processes)
- Target: P99 latency < 500 microseconds including GC
- Must sustain at least 5 minutes of peak load

## Flags to Consider

### GC Flags
- `-XX:+UseZGC` or `-XX:+UseShenandoahGC` (low-pause concurrent collectors)
- `-XX:ConcGCThreads`, `-XX:ParallelGCThreads`
- `-XX:+UnlockExperimentalVMOptions -XX:G1NewSizePercent`
- `-XX:ZAllocationSpikeTolerance`

### Compiler Flags
- `-XX:-TieredCompilation` (pure C2, longer warmup but better steady-state)
- `-XX:CICompilerCount=1` (limit compiler threads)
- `-XX:+PrintCompilation` (verify hot methods are compiled)

### Memory Flags
- `-XX:+AlwaysPreTouch` (force OS to commit pages at startup)
- `-XX:-UseBiasedLocking` (Java 21 removes biased locking anyway)
- `-XX:+UseLargePages` (reduce TLB misses)

## Measurement
- Use `-XX:+PrintGCApplicationStoppedTime` to measure all safepoint pauses
- Use `-Xlog:gc*:file=gc.log` for detailed GC analysis
- Use `perf` or `async-profiler` to identify safepoint causes

## Deliverables
- `HftSimulator.java` — the application
- Batch script with the optimal JVM flags found
- GC log analysis showing the achieved latency
- Explanation of why each flag was chosen
