# Performance Deep Dive — Module Interview Guide

## Company-Specific Questions

### Google
- "How would you profile a Java service that has intermittent latency spikes? Walk through tool selection."
- "Design a JMH benchmark to compare stream vs loop performance. What pitfalls must you avoid?"
- "Explain how JFR's low overhead works. How are events buffered without allocation?"

### Amazon
- "How would you reduce P99 latency of a Java service on AWS from 200ms to 50ms? Show your process."
- "You have an EC2 instance with 32 vCPUs running a Java service. The CPU utilization is 15%. How do you diagnose?"
- "How does JFR streaming help with production monitoring? What events would you subscribe to?"

### Meta
- "Your application OOMs after 6 hours of running. Heap dump shows byte[] occupying 70% of heap. What do you do?"
- "How would you set up a continuous profiling pipeline for a fleet of Java microservices?"
- "Compare async-profiler + JFR + JMH. When is each tool appropriate?"

### Apple
- "How does JMH minimize measurement noise? Explain forks, warmup iterations, and blackholes."
- "Your app uses 2x more memory on ARM Mac than Intel Mac. Where do you start investigating?"

### Oracle
- "How does JFR's event streaming work? What is the ring buffer architecture?"
- "Explain the JFR stacktrace mechanism. How are stack traces sampled without safepoint bias?"
- "How would you integrate JFR with OpenTelemetry for distributed tracing?"

## LeetCode Problems

| Problem | Performance Concept |
|---------|-------------------|
| 53 Maximum Subarray | Kadane's algorithm — O(n) vs brute force O(n²) — JMH benchmark both |
| 215 Kth Largest Element | QuickSelect O(n) vs sort O(n log n) — measure constant factors |
| 347 Top K Frequent Elements | Bucket sort O(n) vs heap O(n log k) — compare with JMH |
| 15 3Sum | Three-pointer O(n²) vs HashMap O(n²) — real-world allocation analysis |
| 56 Merge Intervals | Sort + merge — what's faster: Arrays.sort() or parallelSort()? |

## FAANG Interview Stories

**Story 1: Google — JMH Benchmarking Pitfall**
> *"I was asked to benchmark two sorting algorithms. My first JMH benchmark showed absurd results — the second sort was 10x faster because the JIT had compiled the code path from the first fork. The fix: JMH forks by default, but I had forgotten to warm up properly. Lesson: 5 warmup iterations minimum, 5 measurement iterations."* — SWE, Google

**Story 2: Amazon — P99 Latency Investigation**
> *"A service had P99 latency of 1 second but P50 of 10ms. We used JFR to capture stack traces. The culprit: a synchronized block that was uncontended 99% of the time but blew up under load. We changed to StampedLock's optimistic read. P99 dropped to 50ms."* — Principal Engineer, Amazon

**Story 3: Netflix — Memory Leak Detection**
> *"Service OOM'd every 48 hours. Heap dump showed 60% of heap was char[] held by HashMap$Node objects. This was a session cache with no eviction policy. Adding Caffeine cache with TTL fixed it. Lesson: always set bounded data structures in Java."* — Senior Performance Engineer, Netflix

## Senior vs Staff Deep Dive

### Senior-Level
- "Explain JMH's @State(Scope.Thread) vs Scope.Benchmark. How does each affect results?"
- "How does async-profiler work? How does it get stack traces without safepoint bias?"
- "What is the difference between CPU profiling and wall-clock profiling? When do you use each?"

### Staff-Level
- "Design a continuous profiling system for 10,000 JVM instances. How do you handle data volume?"
- "How does JFR's ring buffer work? How are events written and read concurrently without allocation?"
- "Explain how to use eBPF to profile JVM applications. What information can you get that JFR can't?"
- "Design a benchmarking framework for comparing JVM configurations across a CI/CD pipeline."

## System Design Connections

| System | Performance Tool |
|--------|----------------|
| Monitoring dashboard | JFR streaming + OpenTelemetry |
| CI/CD pipeline | JMH for regression detection, startup time tests |
| Production profiling | async-profiler in continuous mode, JFR recording |
| Load testing | Custom harness with JFR events for latency breakdown |
| Capacity planning | GC log analysis for heap sizing, JFR events for allocation rates |

## Code Review Scenarios

**Scenario 1**: Unnecessary object allocation in hot path.
```java
// Bad: Creates new object on every call
return new BigDecimal(value).toPlainString();
// Better: Cache or use StringBuilder
```

**Scenario 2**: Thread.sleep() for timing.
```java
// Bad: Thread.sleep(1000) — imprecise, blocks thread
Thread.sleep(1000);
// Better: ScheduledExecutorService.schedule()
```

**Scenario 3**: String.format() in loop.
```java
// Bad: String.format() parses format string each time
for (int i = 0; i < 1_000_000; i++) {
    String.format("Value: %d", i);
}
// Better: Manually build with StringBuilder or pre-parse format
```

## Debugging Scenarios

**Scenario 1**: Latency spike every ~10 minutes.
- GC log analysis: Show regular Full GC or Concurrent Mark cycles
- Fix: Tune GC to spread marking more evenly, increase heap, change GC algorithm

**Scenario 2**: Application slow but CPU and memory look normal.
- async-profiler (wall-clock mode): Shows threads waiting on locks
- JFR: Look for jdk.JavaMonitorEnter and jdk.ThreadPark events
- Common: Database connection pool exhaustion, external service latency

**Scenario 3**: OutOfMemoryError but heap is only 30% used.
- Check: Metaspace, direct buffer memory, thread stacks, mapped files
- jcmd <pid> VM.native_memory summary — shows native allocations
- Possible: DirectByteBuffer leak, thread leak, off-heap allocation (Netty)
