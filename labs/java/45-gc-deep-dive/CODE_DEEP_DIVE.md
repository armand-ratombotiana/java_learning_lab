# Code Deep Dive: Garbage Collection

## GcComparisonDemo.java Analysis
This demo allocates 100 MB of 1 KB byte arrays while monitoring GC statistics. The `GarbageCollectorMXBean` list comes from `ManagementFactory.getGarbageCollectorMXBeans()`. Each bean reports collection count and cumulative time. The allocation workload triggers GC based on the current heap size and allocation rate.

The `List<byte[]>` prevents premature GC of the allocated arrays. Each element is strongly referenced by the list, which is strongly referenced by the static `heap` field (a GC root). The GC cannot collect these until `heap.clear()` is called.

## G1GcDemo.java Analysis
This allocates objects of varying sizes (128 bytes to 4 KB) with random reference patterns. The key simulation aspect is that 10% of objects are retained as GC roots (added to `roots`), while the remaining 90% form chains through `data.next`. Objects at the end of these chains are eligible for GC (unreachable) while root objects are live.

The random size distribution simulates real-world allocation patterns. G1's region-based design handles mixed-size allocations better than the Parallel collector's generational layout.

## ZGcDemo.java Analysis
The allocation pattern is specifically designed for ZGC: 20 cycles of 50,000 short-lived objects with 5% survivors. ZGC's concurrent processing means the 20 cycles can overlap with GC marking and relocation. The short-lived objects (256-byte payload) are efficiently handled by ZGC's load barrier, which primarily triggers on reads of "bad" colored pointers.

## GcRootExample.java Analysis
The demo explicitly shows three GC root types:
- `stackReference()`: the `local` variable is a stack root while the method executes
- `staticReference()`: the `staticRoot` field is a static root (live until set to null)
- `list` in `main()`: a stack root referencing 1000 objects

After `clear()` and `staticRoot = null`, the objects become eligible for GC. The `System.gc()` calls demonstrate that `System.gc()` is just a hint — the JVM may ignore it depending on `-XX:+DisableExplicitGC`.

## GcLoggingExample.java Analysis
The `NotificationListener` captures GC events from `GarbageCollectorMXBean` implementations that extend `NotificationEmitter`. The `getMessage()` from the notification contains human-readable GC event descriptions. This is the programmatic equivalent of parsing GC log files.
