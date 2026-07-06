# Exercises: Garbage Collection

## Exercise 1: Allocation Rate Profiler
Write a program that allocates at different rates (10 MB/s, 100 MB/s, 1000 MB/s) and measures GC pause times for each rate with different collectors.

## Exercise 2: Memory Leak Detection
Write a program that intentionally leaks memory (e.g., growing a static HashMap). Run with different heap sizes and GC logs. Identify the leak pattern in the GC logs.

## Exercise 3: GC Collector Comparison
Write a benchmark that runs the same workload (mix of short-lived, medium-lived, and long-lived objects) with Serial, Parallel, G1, and ZGC. Compare throughput, pause times, and footprint.

## Exercise 4: G1 Region Sizing
Experiment with `-XX:G1HeapRegionSize=1m,2m,4m,8m`. Create humongous objects (>50% region size) and observe their impact on GC behavior.

## Exercise 5: GC Log Parser
Write a parser that reads a GC log file and produces summary statistics: average pause time, P99 pause time, allocation rate, promotion rate, and GC overhead percentage.

## Exercise 6: Soft/Weak/Phantom Reference GC
Write a program that creates soft, weak, and phantom references. Verify when each is cleared by the GC under memory pressure.

## Exercise 7: TLAB Analysis
Write a program that prints TLAB statistics using `ManagementFactory.getThreadMXBean()` and monitoring TLAB allocations. Measure TLAB waste and retlabs (retired TLABs) under different allocation patterns.
