# Performance Interview Questions

## Section 1: JVM Basics

### Q1: Explain the JVM memory layout
**Answer:** JVM has:
- **Heap:** Object allocation (Young Gen: Eden, S0, S1; Old Gen)
- **Metaspace:** Class metadata (replaced PermGen)
- **Stack:** Per-thread execution
- **Native Method Stack:** Native code
- **Program Counter Register:** Current instruction

---

### Q2: What is the difference between -Xms and -Xmx?
**Answer:**
- **-Xms:** Initial heap size
- **-Xmx:** Maximum heap size

Setting them equal (-Xms=-Xmx) prevents heap resizing overhead.

---

### Q3: What are the generations in the heap?
**Answer:**
- **Young Generation:** New objects, short-lived
  - Eden space
  - Survivor spaces (S0, S1)
- **Old Generation:** Long-lived objects, promoted from young
- **Metaspace:** Class metadata (separate from heap)

---

## Section 2: Garbage Collection

### Q4: What is garbage collection?
**Answer:** Automatic process of reclaiming memory from objects no longer in use. Marks live objects, removes unreachable.

---

### Q5: Explain the different GC algorithms
**Answer:**
- **Serial:** Single-threaded, stop-the-world
- **Parallel:** Multi-threaded, throughput-focused
- **CMS:** Concurrent Mark Sweep, low pause
- **G1:** Region-based, predictable pause times
- **ZGC:** Ultra-low latency, concurrent
- **Shenandoah:** Low pause, independent of heap size

---

### Q6: What is the difference between Minor and Major GC?
**Answer:**
- **Minor GC:** Young generation, frequent, fast
- **Major GC/Full GC:** Old generation or entire heap, less frequent, slower
- G1 uses "Mixed GC" combining young and old

---

### Q7: How do you tune G1GC?
**Answer:**
- **-XX:MaxGCPauseMillis:** Target pause time
- **-XX:G1HeapRegionSize:** Region size (1-32MB)
- **-XX:InitiatingHeapOccupancyPercent:** When to start mixed GC
- **-XX:G1ReservePercent:**预留 space for promotions

---

### Q8: What is the purpose of -XX:+UseG1GC?
**Answer:** Enables the G1 (Garbage First) collector - modern default GC that balances throughput and latency.

---

## Section 3: Profiling

### Q9: How do you profile a Java application?
**Answer:**
- **VisualVM:** GUI for CPU/memory profiling
- **JFR (Java Flight Recorder):** Production recording
- **async-profiler:** Low-overhead CPU profiling
- **jstat:** GC statistics
- **jstack:** Thread dumps
- **jmap:** Heap dumps, memory info

---

### Q10: What is a heap dump and how do you create one?
**Answer:** Snapshot of heap memory at a point in time. Use:
- `jmap -dump:format=b,file=heap.bin <pid>`
- `jcmd <pid> GC.heap_dump file=heap.bin`
- From JMX or VisualVM

---

### Q11: How do you analyze GC logs?
**Answer:**
- Enable logging: `-Xlog:gc*:file=gc.log`
- Use GCViewer, GCEasy, or charts
- Analyze: pause times, GC frequency, heap usage, promotion rates

---

### Q12: What is the N+1 problem in JPA?
**Answer:** Query for N entities causes N additional queries for related entities. Solution: JOIN FETCH, EntityGraph, batch fetching.

---

## Section 4: Benchmarking

### Q13: Why is JMH used for benchmarking?
**Answer:** JMH (Java Microbenchmark Harness) provides:
- Accurate timing
- Warmup phases (JIT compilation)
- Proper JVM forking
- Statistical analysis

---

### Q14: How do you write a JMH benchmark?
**Answer:**
```java
@Benchmark
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public void myMethod() {
    // code to benchmark
}
```

Run with: `java -jar target/benchmarks.jar MyBenchmark`

---

### Q15: What are important JMH annotations?
**Answer:**
- **@Benchmark:** Marks benchmark method
- **@BenchmarkMode:** Throughput, AverageTime, SampleTime
- **@Fork:** JVM instances to run
- **@Warmup:** Iterations before measurement
- **@Measurement:** Iterations to measure
- **@State:** Shared state between invocations

---

## Section 5: Memory Issues

### Q16: What causes OutOfMemoryError?
**Answer:**
- **Java heap space:** Heap exhausted, increase -Xmx or fix leak
- **GC overhead limit:** Too much time in GC
- **Metaspace:** Class metadata exhausted
- **Unable to create native thread:** Too many threads
- **Direct buffer memory:** NIO buffer exhaustion

---

### Q17: How do you find memory leaks?
**Answer:**
1. Enable heap dumps on OOM
2. Analyze heap dump with MAT (Memory Analyzer Tool)
3. Look for growing collections
4. Compare snapshots
5. Find GC roots keeping objects alive

---

### Q18: What is the difference between soft, weak, and phantom references?
**Answer:**
- **Soft:** Collected when memory is low (caches)
- **Weak:** Collected on next GC (like WeakHashMap)
- **Phantom:** Object finalized but not reclaimed (cleanup)

---

## Section 6: Performance Tuning

### Q19: How do you tune JVM for a web application?
**Answer:**
- Use G1GC: `-XX:+UseG1GC`
- Set heap: `-Xms2g -Xmx2g`
- Target pause: `-XX:MaxGCPauseMillis=200`
- Enable GC logging: `-Xlog:gc*:file=gc.log`
- Consider: `-XX:+UseStringDeduplication`

---

### Q20: What is JIT compilation?
**Answer:** Just-In-Time compilation converts bytecode to native machine code at runtime for better performance. JIT uses profiling data to optimize hot code paths.

---

### Q21: How does connection pooling improve performance?
**Answer:** Reuses database connections instead of creating new ones:
- Reduces connection overhead
- Limits concurrent connections
- Manages connection lifecycle
- Use HikariCP (default in Spring Boot)

---

### Q22: What is String deduplication?
**Answer:** JVM feature that identifies identical String objects in heap and makes them share the same char array, saving memory. Enabled with `-XX:+UseStringDeduplication`.

---

### Q23: How do you monitor JVM in production?
**Answer:**
- JMX + JConsole/VisualVM
- JFR for detailed recording
- Prometheus + exporters
- Cloud provider monitoring
- GC logging analysis

---

### Q24: What is escape analysis?
**Answer:** JIT optimization that determines if object allocation can be moved to stack (scalar replacement) instead of heap, improving performance.

---

### Q25: How do you optimize for low latency?
**Answer:**
- Use low-latency GC (ZGC, Shenandoah)
- Pre-allocate and pool objects
- Avoid allocations in hot path
- Use primitives over objects
- Enable -XX:+UseStringDeduplication
- Tune GC pause goals
- Consider off-heap storage