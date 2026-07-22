# Interview Questions: JVM Internals

## Company-Specific Focus

### Google
- JVM architecture: class loader subsystem, runtime data areas, execution engine
- HotSpot internals: details about the JIT, garbage collection, profiling
- JVM tuning: applying Xms/Xmx, GC choice, metaspace, and code cache

### Microsoft
- JVM vs CLR: architecture and memory management comparison
- Metadata in the JVM: class file parsing vs CLI metadata
- JVM performance tuning for enterprise Java applications on Azure

### Amazon
- JVM warm up: why microservices need warmup strategies on AWS
- JDK Flight Recorder: event-based monitoring and production troubleshooting
- JVM crash analysis: hs_err log and core dumps

### Meta
- Garbage collection: how different generations/collectors work
- Escape analysis: allocating on stack where possible
- JIT compilation: C1 vs C2, tiered compilation and inlining decisions

### Apple
- JVM on macOS: always ARM native (since JDK 17)
- Object layout: mark word, klass pointer, field alignment
- Page size: macOS uses 16KB pages vs Linux 4KB

### Oracle
- JVM specification: the bible for JVM implementations
- HotSpot JVM: the reference implementation, open source
- The JVM execution model: interpretation -> tiered -> native code
- Flight Recorder and Mission Control

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LeetCode problems map to JVM internals — but the following test JVM knowledge) |
| Implement a simple class loader | N/A | Google, Microsoft | Understand delegation model |
| Explain GC log output | N/A | Amazon, Google | JVM problem diagnosis |
| Interpret hs_err log | N/A | Meta, Apple | Analyzing JVM crashes |

## Real Production Scenarios
- **Netflix**: JVM crash due to a large thread stack caused two hours of downtime — forced to set Xss explicitly
- **Uber**: Code cache full warning degraded performance of the JIT — added -XX:ReservedCodeCacheSize=256m
- **Twitter**: A JVM was paused for 50 seconds due to full GC cause of metaspace growing endlessly

## Interview Patterns & Tips
- **HotSpot**: Most production JVM is OpenJDK HotSpot
- **-Xms == -Xmx**: Avoid resizing pauses during execution
- **Choose GC based on the workload**: 
  - Throughput: ParallelGC
  - Low latency: G1GC, ZGC, Shenandoah
  - Large heap (>100GB): ZGC or Shenandoah

## Deep Dive Questions
- **JVM memory**: Explain the runtime data areas: heap, stack, method area, PC Register, native method stack
- **Class file format**: The magic number (0xCAFEBABE), version, constant pool, methods, and attributes
- **Class loading**: Delegation hierarchy, loading, linking (verification, preparation, resolution), initialization
- **JIT**: What triggers C1 vs C2 compilation? Tiered compilation phases
- **GC**: What are GC roots? How does tracing work from roots to live objects?
