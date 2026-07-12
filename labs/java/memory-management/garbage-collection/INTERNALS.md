# Garbage Collection Internals

## ⚙️ The Evolution of GC Algorithms

### 1. Serial GC (`-XX:+UseSerialGC`)
The simplest GC. It uses a single thread for both Young and Old generation collections.
- **Pros**: Zero overhead from thread communication. Excellent for small heaps (< 100MB) or single-core machines.
- **Cons**: Massive STW pauses on multi-core machines because it only uses one core to clean up.

### 2. Parallel GC (`-XX:+UseParallelGC`)
The default in Java 8. It uses multiple threads to perform the GC work.
- **Pros**: Maximizes throughput. Great for batch processing jobs where you don't care about occasional long pauses.
- **Cons**: Still has significant STW pauses during Full GCs.

### 3. G1 GC (Garbage First) (`-XX:+UseG1GC`)
The default since Java 9. It abandons the strict physical separation of Young and Old generations. Instead, it divides the entire heap into thousands of small, equal-sized **Regions** (e.g., 2MB each).
- Some regions are logically designated as Young, others as Old.
- **Garbage First**: It tracks which regions contain the most garbage and prioritizes collecting them first.
- **Predictable Pauses**: You can set a target pause time (`-XX:MaxGCPauseMillis=200`), and G1 will try its best to collect only as many regions as it can within that time limit.

### 4. ZGC (Z Garbage Collector) (`-XX:+UseZGC`)
Introduced as production-ready in Java 15, and made Generational in Java 21. This is an ultra-low latency collector.
- **Goal**: Sub-millisecond STW pauses, regardless of heap size (works the same on a 1GB heap or a 16TB heap).
- **How?**: It performs almost all of its work (marking, relocating, and compacting objects) *concurrently* while the application threads are still running.
- **Colored Pointers & Load Barriers**: It uses bits in the object pointer itself to track the object's state. When an application thread tries to read a reference, a "Load Barrier" intercepts it. If the GC is currently moving that object, the Load Barrier pauses just that one thread for a microsecond, updates the pointer to the new location, and lets it continue.

## 📊 GC Tuning Trade-offs
You cannot have perfect throughput, perfect latency, and zero memory overhead. You must pick two.
- **Throughput**: Percentage of total time the CPU spends running application code vs GC code. (Use Parallel GC).
- **Latency**: The maximum time the application is unresponsive. (Use ZGC or Shenandoah).
- **Footprint**: The amount of extra memory the GC needs to do its job. (ZGC requires more memory overhead than Serial GC).