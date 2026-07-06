# Exercises: JVM Tuning

## Exercise 1: Heap Sensitivity Analysis
Write a program that allocates memory at a fixed rate and measures throughput with heap sizes of 256 MB, 512 MB, 1 GB, 2 GB, and 4 GB. Plot throughput vs heap size.

## Exercise 2: Young Generation Tuning
Write a benchmark that creates short-lived objects (90% die young) and measures GC frequency with different young sizes: 64 MB, 128 MB, 256 MB, 512 MB.

## Exercise 3: Code Cache Monitoring
Write a program that generates code (via dynamic proxies or lambda generation) and monitors code cache usage using `ManagementFactory.getMemoryPoolMXBeans()` for the "CodeHeap" pools.

## Exercise 4: Metaspace Class Generator
Write a program that loads classes at a configurable rate (classes/second) and measures the Metaspace growth. Test with and without `-XX:MaxMetaspaceSize`.

## Exercise 5: String Dedup Effectiveness
Write a program that creates strings with varying levels of duplication (10%, 50%, 90% duplicate). Run with and without `-XX:+UseStringDeduplication` and compare heap usage.

## Exercise 6: JVM Flag Comparison
Write a program that runs the same workload under different flag combinations:
- Default flags vs optimized (your choice of flags)
- With and without `-XX:+AlwaysPreTouch`
- With and without `-XX:+UseLargePages` (if available)

## Exercise 7: GC Overhead Calculator
Write a tool that reads GC log files and calculates: total GC time, GC overhead %, average pause, P99 pause, allocation rate, and promotion rate. Output a summary report.
