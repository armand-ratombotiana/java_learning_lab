# Mock Interview Transcript: JVM Tuning & Optimization

## Interviewer: Staff Engineer, Amazon
## Candidate: Senior Java developer
## Time: 40 minutes
## Focus: Performance tuning, flags, container-awareness, CDS

---

**Q1: Your Java microservice on AWS is using 8GB heap but has 10-second GC pauses every hour. Walk through the diagnostic process.**

**Candidate**: Step 1: Enable GC logging: `-Xlog:gc*:file=gc.log:time,level,tags`. Step 2: Check what GC we're using. Step 3: Analyze logs — are these Full GC pauses? If so, change GC. Step 4: Check heap sizing — 8GB with G1 should target 200ms pauses. If using Parallel GC, switch to G1. Step 5: Check allocation rate with `-XX:+PrintAdaptiveSizePolicy`. Step 6: If G1: check `-XX:InitiatingHeapOccupancyPercent` — default 45%, may need lowering. Step 7: Consider ZGC or Shenandoah if <10ms SLA needed.

**Interviewer**: What JVM flags would you set for a container with 2 CPU cores and 4GB memory limit?

**Candidate**: 
```bash
-XX:+UseContainerSupport           # Auto-detect container limits (Java 10+)
-Xmx4g -Xms4g                      # Max heap = container limit
-XX:MaxRAMPercentage=75.0           # Use 75% of container memory for heap (3GB)
-XX:+UseG1GC                       # Balanced collector
-XX:ConcGCThreads=1                 # 1 concurrent thread (avoid CPU starvation)
-XX:ParallelGCThreads=2             # 2 parallel threads (matching CPU)
-XX:ActiveProcessorCount=2          # Explicitly set for some libraries
-XX:+ExitOnOutOfMemoryError         # Exit on OOM for container restart
-XX:+HeapDumpOnOutOfMemoryError     # Capture dump before exit
```

**Interviewer**: How does CDS (Class Data Sharing) improve startup time?

**Candidate**: CDS pre-processes class metadata into an archive file. Steps: (1) `java -Xshare:dump -XX:SharedArchiveFile=app.jsa -jar app.jar` — creates archive. (2) `java -Xshare:auto -XX:SharedArchiveFile=app.jsa -jar app.jar` — loads from archive. Benefits: (1) Faster startup (50%+ improvement for microservices). (2) Reduced memory (shared pages across JVM instances). (3) Consistent performance (avoids class loading costs). AppCDS (Java 10+) archives application classes, not just JDK classes.

**Interviewer**: What is `-XX:+AlwaysPreTouch`? When should you use it?

**Candidate**: `-XX:+AlwaysPreTouch` pre-commits and zeroes all heap pages at startup instead of on-demand. Benefits: (1) Guarantees physical memory at startup (no page fault delays during GC). (2) Predictable performance from the start. (3) Useful for latency-sensitive apps. Drawbacks: (1) Slower startup (touches all pages). (2) Locks memory (may cause OOM killer if memory is overcommitted). (3) Long startup with large heaps.

**Interviewer**: How do you diagnose high CPU usage in a Java application?

**Candidate**: (1) `top -H -p <pid>` — find hot threads. (2) `jstack <pid>` or `jcmd <pid> Thread.print` — get thread dumps, look for the hot thread. (3) `async-profiler` — `./profiler.sh -e cpu -d 30 -f cpu.html <pid>` — flame graph of CPU hot spots. (4) Check for: infinite loops, GC overhead (>20% CPU in GC threads), lock contention, inefficient algorithms (boxing, string concat in loops), JIT compilation (code cache full slows down).

**Interviewer**: How do you reduce memory usage in a high-throughput service?

**Candidate**: (1) Enable compact strings (Java 9+ default). (2) Use `-XX:+UseStringDeduplication` (G1). (3) Avoid unnecessary object creation (reuse, pool, flyweight). (4) Use primitives (int[] over Integer[]). (5) `-XX:+UseCompressedOops` (enabled by default for heaps < 32GB). (6) Off-heap for large caches (DirectByteBuffer, MapDB). (7) Use primitive collections (fastutil, Eclipse Collections). (8) Set appropriate max heap (don't overallocate).

**Interviewer**: How does `-XX:+UseCompressedOops` work?

**Candidate**: Compressed OOPs (Ordinary Object Pointers) allow the JVM to use 32-bit offsets instead of 64-bit pointers for objects in the heap. By default, the heap is mapped at a base address, and pointers are stored as 32-bit offsets from that base. This works for heaps < 32GB (or < 64GB with zero-based compression). Benefits: (1) 4 bytes per reference instead of 8 — saves ~40% for pointer-heavy data structures. (2) Better CPU cache utilization (more references fit in cache line). Disabled for heaps > ~32GB.

**Interviewer**: Final: What's the first thing you check when a Java process shows high memory usage but heap dump shows only 20% heap used?

**Candidate**: Native memory! Check: (1) Metaspace — `jstat -gcmetacapacity`. (2) Direct buffers — `jcmd <pid> VM.native_memory summary`. (3) Thread stacks — each thread uses ~1MB (platform threads). (4) Mapped files — `MappedByteBuffer` (off-heap). (5) Code cache — `jstat -printcompilation`. (6) GC metadata — remembered sets, card tables. (7) Netty or other off-heap allocators. Use NMT (Native Memory Tracking): `-XX:NativeMemoryTracking=summary` then `jcmd <pid> VM.native_memory`.

---

## Feedback

**Strengths**:
- Systematic diagnostic process
- Container tuning flags (cgroup awareness)
- CDS for startup optimization
- AlwaysPreTouch trade-off analysis
- Native memory diagnostic approach

**Areas for Improvement**:
- Could discuss JITWatch for compilation analysis
- Mention `-XX:MaxRAMPercentage` vs `-Xmx` for containers

**Score**: 4.5/5 — Expert JVM tuning
