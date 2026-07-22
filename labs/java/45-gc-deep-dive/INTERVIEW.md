# Interview Questions: Garbage Collection Deep Dive

## Company-Specific Focus

### Google
- GC roots: what are they and how tracing works from roots
- Generational hypothesis: most objects die young
- G1 GC: region-based, concurrent, incremental, mixed collections

### Microsoft
- Java GC vs .NET GC: generational, workstation vs server mode
- G1 GC: how it works and how to tune it for low latency
- ZGC: low-latency garbage collection for large heaps

### Amazon
- GC tuning for high throughput microservices
- ZGC and Shenandoah: sub-millisecond pause times
- G1 GC tuning: -XX:G1HeapRegionSize, -XX:MaxGCPauseMillis

### Meta
- G1 GC: mixed GC, remembered sets, SATB algorithm
- GC logs: how to read and analyze GC logs for optimization
- Object allocation and TLAB (Thread Local Allocation Buffers)

### Apple
- GC on macOS with different heap sizes
- ZGC on ARM: how it differs
- GC for memory-constrained environments

### Oracle
- HotSpot GC architecture: generational, concurrent, parallel
- G1, ZGC, Shenandoah, Parallel, Serial GC
- GC ergonomics: auto-tuning based on hardware
- GC logging: -Xlog:gc* in Java 9+ unified logging

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — GC is a JVM runtime mechanism) |
| 146 LRU Cache | Medium | Google, Amazon | Understanding object lifecycle and GC roots |

## Real Production Scenarios
- **Amazon**: Full GC paused the entire fleet for 5 seconds — caused by a promotion failure in G1
- **Netflix**: ZGC with a 300GB heap provided <1ms pause times for the recommendation engine
- **LinkedIn**: CMS GC (legacy) caused long concurrent mode failures — migrated to G1

## Interview Patterns & Tips
- **GC tuning**: Choose GC based on requirements: throughput (Parallel), low-latency (G1/ZGC/Shenandoah)
- **GC logs**: Always enable GC logging for troubleshooting
- **TLAB**: Each thread gets a TLAB for allocation without synchronization
- **Heap analysis**: jmap, MAT, jhat for heap dump analysis

## Deep Dive Questions
- **Roots**: What are the GC roots? (stack, static, JNI, active monitors)
- **Generational**: Why are objects allocated in Eden and promoted to Survivor/Old?
- **G1**: How does G1 divide the heap? What are remembered sets and SATB?
- **ZGC**: How does ZGC achieve sub-millisecond pause times?
- **Shenandoah**: How does Shenandoah differ from ZGC?