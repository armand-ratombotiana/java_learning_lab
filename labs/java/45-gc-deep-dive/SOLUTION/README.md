# Solutions: GC Deep Dive

## GcComparisonDemo.java
Uses `ManagementFactory.getGarbageCollectorMXBeans()` to list active collectors. Allocates 100 MB of 1 KB byte arrays while monitoring collection counts. After allocation, reports total collections and time. The demo works with any collector and shows how GC behavior changes under allocation pressure.

## G1GcDemo.java
G1 divides the heap into 1 MB regions. This demo allocates objects between 128 bytes and 4 KB with random reference patterns to create cross-region references (requiring remembered sets). 10% of objects are retained as GC roots. The varying object sizes force G1 to categorize regions as Eden, Survivor, Old, or Humongous.

## ZGcDemo.java
ZGC uses colored pointers and load barriers for concurrent compaction. This workload creates 20 cycles of 50,000 short-lived objects (each 256 bytes) with 5% retained as survivors. ZGC excels here because concurrent processing allows allocation to continue during most GC phases, and colored pointers enable compact-to-forward without stop-the-world pauses.

## GcRootExample.java
Demonstrates the four root types:
- Stack roots: local variables in active methods
- Static roots: class static fields
- JNI roots: global references from native code
- Thread roots: running thread objects

The demo creates objects reachable via each root type, calls `System.gc()`, then clears references and calls `System.gc()` again.

## GcLoggingExample.java
Registers a `NotificationListener` on each `GarbageCollectorMXBean` that implements `NotificationEmitter`. The listener prints GC notification events. Allocation of 100 KB arrays triggers GC cycles. The final report shows collection counts and cumulative time, demonstrating how to monitor GC programmatically in production.
