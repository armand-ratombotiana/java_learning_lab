# Why GC Deep Dive Matters

Understanding garbage collection determines whether your application has 10ms or 10-second latency spikes. The wrong GC configuration can turn a well-tuned application into a latency disaster. The right GC tuning can reduce p99 latency by 10x or more.

G1 heap region mechanics matter because humongous allocations (objects > 50% of a region) bypass region optimizations. A single large array can cause G1 to stop-the-world for a humongous allocation. Knowing this helps you break large arrays into smaller chunks or increase region size.

ZGC colored pointers matter because they impose a heap size limit. ZGC steals 4 bits from object pointers for GC metadata (currently uses 4 bits out of 42 available with 16TB address space — effective max heap is ~16TB). On 32-bit systems or with compressed OOPs, ZGC may have reduced capacity.

GC root scanning matters because it's a stop-the-world pause that scales with thread count. Applications with 1000+ threads have higher root scanning pause times. Virtual threads (Project Loom) may reduce thread count but increase the complexity of root scanning (stack walking through continuation frames).

GC log analysis matters because it's the primary diagnostic tool for memory issues. A GC log can reveal:
- Allocation rate (MB/sec) — determines collector suitability
- Promotion rate — indicates survivor space sizing
- Pause frequency — detecting GC thrashing
- Concurrent cycle duration — measuring collector efficiency

GC tuning matters because default parameters are designed for throughput, not latency. A 200ms pause target works for most web applications but fails for real-time systems. GC tuning is about trading off three dimensions: throughput, latency, and memory footprint. Understanding this tradeoff prevents over-tuning and under-tuning.

GC selection matters because different collectors optimize for different workloads:
- Serial: single-thread, small heaps (< 200MB)
- Parallel: throughput-oriented, large heaps, batch processing
- G1: balanced, large heaps, latency-sensitive
- ZGC/Shenandoah: ultra-low latency, very large heaps
