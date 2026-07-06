# Why Garbage Collection Exists

Garbage collection exists because manual memory management is error-prone and productivity-killing. In C/C++, approximately 40% of bugs are memory-related (buffer overflows, use-after-free, double-free). GC eliminates entire categories of these bugs, making Java safer and more productive.

Generational collection exists because it exploits the generational hypothesis: most objects die young. By focusing collection effort on the young generation (where most garbage is), generational collectors achieve high throughput with low pause times.

G1 exists because large heaps with stop-the-world collectors cause multi-second pauses. Web servers and interactive applications cannot tolerate 10-second GC pauses. G1 divides the heap into regions and collects incrementally, aiming for a configurable pause time target (default 200ms).

ZGC exists because even G1's pause times (~50-200ms) are too high for ultra-low-latency applications (trading systems, real-time analytics). ZGC's pause times are less than 1ms regardless of heap size. It achieves this through colored pointers (storing GC state in object reference bits) and load barriers (intercepting reference reads to ensure consistency).

Shenandoah exists as an alternative to ZGC with a different approach: Brooks pointers (forwarding pointers stored in objects) instead of colored pointers. This avoids ZGC's 4TB heap limitation (from bit stealing) and works on architectures without 64-bit pointer manipulation support.

Concurrent collection exists because stop-the-world (STW) pauses don't scale with heap size. A full GC on a 128GB heap can take minutes. Concurrent collectors do most GC work while the application runs, requiring only brief STW pauses for root scanning and finalization.

GC logging exists because understanding GC behavior requires empirical data. Without logs, you can't know pause times, collection frequency, promotion rates, or allocation pressure. GC logs are the primary diagnostic tool for memory-related performance issues.
