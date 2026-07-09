# Deep Dive: Garbage Collection

## 1. G1 (Garbage-First) Collector Deep Dive

G1 is the default GC since Java 9. It divides the heap into ~2048 regions (each 1MB-32MB) and uses a concurrent, incremental approach.

### G1 Heap Structure

```
Heap Regions (example: 4GB heap, 2048 regions, 2MB each):
+-----+-----+-----+-----+-----+-----+-----+-----+
|  E  |  E  |  E  |  S  |  S  |  O  |  O  |  H  |
|     |     |     |     |     |     |     |     |
| Eden| Eden| Eden| Surv| Surv| Old | Old | Hum |
+-----+-----+-----+-----+-----+-----+-----+-----+
|  O  |  O  |  O  |  E  |  E  |  E  |  E  |  E  |
| Old | Old | Old | Eden| Eden| Eden| Eden| Eden|
+-----+-----+-----+-----+-----+-----+-----+-----+

E = Eden, S = Survivor, O = Old, H = Humongous
```

Each region has:
- **RSet (Remembered Set)** — tracks incoming references from other regions
- **Live bitmaps** — marks live objects
- **Prev/Next marking bitmaps** — for concurrent marking

```java
// G1 heap parameters
// -XX:G1HeapRegionSize=4m    // Region size (1MB-32MB, auto-calculated)
// -XX:+G1UseAdaptiveIHOP     // Adaptive Initiating Heap Occupancy
// -XX:G1NewSizePercent=5     // Initial young gen size (% of heap)
// -XX:G1MaxNewSizePercent=60 // Max young gen size (% of heap)
// -XX:G1HeapWastePercent=5   // Allowed waste in heap
// -XX:G1MixedGCLiveThresholdPercent=85 // Only collect old regions below 85% live
// -XX:G1MixedGCCountTarget=8 // Target number of mixed GCs per cycle

public class G1HeapLayout {
    public static void main(String[] args) {
        // Run with: -XX:+PrintRegionDetails
        // Regions are visible in GC logs:
        // [0.042s][info][gc,heap] Heap region size: 2M
        // [0.042s][info][gc,heap]   region 0: free
        // [0.042s][info][gc,heap]   region 1: free
        // ...
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Max heap: " + runtime.maxMemory() / 1024 / 1024 + "MB");
    }
}
```

### Remembered Sets (RSet)

RSets track cross-region references. Without RSets, a GC pause would need to scan the entire heap to find references into collected regions.

```java
// RSet is a hash table:
// Key: region index (2 bytes)
// Value: set of cards (512-byte blocks) containing pointers
//
// Card table: byte array tracking dirty cards
// Each heap byte maps to a card table entry
// When a reference is written, the card is marked dirty
// (via post-write barrier in compiled code)
//
// RSet refinement:
// - Concurrent refinement threads process dirty cards
// - -XX:G1ConcRefinementThreads=8
// - -XX:G1RSetUpdatingPauseTimePercent=5
// - Cards are batched for efficiency
// - If refinement falls behind, mutator threads help
// - (G1EnteringDirtyCardQueue into G1SATBBufferQueue)

public class RSetCosts {
    // High RSet cardinality (many incoming references) means:
    // - More work during young GC (scanning RSets)
    // - Dangerous: an object referenced from every region has RSet entries everywhere
    // - Hash table collisions increase memory overhead
    
    // -XX:G1RSetRegionEntries=1024 (entries per region)
    // -XX:+G1TraceRSetRefinement (logs RSet activity)
    // -XX:G1RSetLogEntries=32 (card entries per RSet bucket)
}
```

### Concurrent Marking (G1)

G1 uses SATB (Snapshot-At-The-Beginning) concurrent marking:

```java
// Marking phases:
// 1. Initial Mark (STW) — marks roots, usually piggybacked on young GC
// 2. Root Region Scan — scan survivor regions for references to old gen
// 3. Concurrent Mark — traverse object graph from roots
// 4. Remark (STW) — finalize marking, SATB processing
// 5. Cleanup (STW) — compute region live data, free empty regions

public class G1MarkingPhases {
    // -XX:ConcGCThreads=8 (concurrent threads)
    // -XX:+UnlockDiagnosticVMOptions -XX:+G1PrintRegionLivenessInfo
    // -XX:+PrintGCDetails -XX:+PrintGCTimeStamps
    
    public static void triggerMarking() {
        // IHOP (Initiating Heap Occupancy Percent) triggers marking
        // -XX:InitiatingHeapOccupancyPercent=45 (default)
        // When old gen occupancy > 45% + some margin, marking starts
        // -XX:G1HeapReservePercent=10
        
        // To observe marking phases:
        // -XX:+PrintGC -XX:+PrintGCDetails
        // [GC pause (G1 Evacuation Pause) (young) 123M->45M(512M)]
        // [GC concurrent-root-region-scan-start]
        // [GC concurrent-mark-start]
        // [GC concurrent-mark-end, 0.123 secs]
        // [GC remark [Finalize Marking, 0.002 secs]
        // [GC cleanup 345M->345M(512M), 0.001 secs]
    }
}
```

### SATB (Snapshot-At-The-Beginning)

SATB ensures concurrent marking sees a consistent snapshot:

```java
// SATB algorithm:
// - At marking start, the entire object graph is logically frozen
// - New allocations are marked live immediately
// - When a reference is overwritten, the old value is recorded in SATB buffer
// - During remark, the SATB buffers are processed (objects that were live at start are retained)
// - This means some garbage may be retained until the next cycle
// - But no live object is missed (conservative correctness)

// Pre-write barrier (in compiled code):
// if (SATB marking is active) {
//     enqueue previous reference to SATB buffer
// }
// Then the regular store happens

// -XX:+PrintSATBQueueLengths (diagnostic)
// -XX:ConcRefinementThreads — processes SATB buffers
public class SATBExample {
    private Object field;
    
    public void updateField(Object newValue) {
        // Concurrent marking might be in progress here:
        // Pre-write barrier records the old value of this.field
        // The old value will be traced during concurrent marking
        // even if it becomes unreachable after this assignment
        this.field = newValue;
    }
}
```

### G1 Evacuation Pause

The evacuation pause (young/mixed GC) is a stop-the-world event:

```java
// Young collection phases:
// 1. Select collection set (CSet) — young regions + optional old regions
// 2. Scan RSets — find roots into CSet from non-CSet regions
// 3. Update RSets — process remaining dirty card queue entries 
// 4. Evacuate — copy live objects from CSet to survivor/old regions
// 5. Fix-up — update references to moved objects
// 6. Post-evacuation — prepare for next phase

// -XX:+G1GC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps
// Sample log:
// [GC pause (G1 Evacuation Pause) (young) 218M->64M(512M), 0.014 secs]
//   [Parallel Time: 8.5 ms, GC Workers: 8]
//      [GC Worker Start (ms): 124.0]
//      [Ext Root Scanning (ms): 1.2]
//      [Update RS (ms): 0.8]
//      [Scan RS (ms): 2.1]
//      [Code Root Scanning (ms): 0.3]
//      [Object Copy (ms): 3.5]
//      [Termination (ms): 0.1]
//      [GC Worker Other (ms): 0.2]
//      [GC Worker Total (ms): 8.2]
//      [GC Worker End (ms): 132.2]
//   [Code Root Fixup: 0.001 ms]
//   [Code Root Purge: 0.001 ms]
//   [Clear CT: 0.2 ms]
//   [Other: 0.6 ms]
//   [Choose CSet: 0.001 ms]
//   [Ref Proc: 0.3 ms]
//   [Ref Enq: 0.01 ms]
//   [Redirty Cards: 0.1 ms]
//   [Humongous Register: 0.01 ms]
//   [Humongous Reclaim: 0.01 ms]
//   [Free CSet: 0.2 ms]

public class G1PauseAnalysis {
    // Key metrics from GC logs:
    // - Pause duration: < 100ms typically (target: -XX:MaxGCPauseMillis=200)
    // - Evacuation failure: when regions fill during copy, very bad
    // - Humongous allocation: >50% region size, allocated directly in old gen
    // - Collection set: too large → long pauses
    // - RSet scanning: high → many cross-region references
    
    // Optimization:
    // - Increase heap: -Xms8g -Xmx8g
    // - Increase region count: smaller regions via -XX:G1HeapRegionSize
    // - Increase concurrent threads: -XX:ConcGCThreads
    // - Tweak IHOP: -XX:InitiatingHeapOccupancyPercent
}
```

## 2. ZGC Deep Dive

ZGC (Z Garbage Collector) is a low-latency GC (typically <10ms pause) available since Java 11 (experimental), production since Java 15.

### ZGC Colored Pointers

ZGC stores metadata about object state in the pointer itself:

```java
// ZGC uses 64-bit pointers (requires heaps up to 16TB)
// Colored pointer layout (x86-64):
//  N | M | R | R | R | R | R | 0 | Address (42 bits)
//  63  62  61  60  59  58  57  56  55..14  13..0
//
// - Bit 63-62: Finalizable bit  
// - Bit 61-60: Remapped bit (per-page view)
// - Bit 59-58: Marked0 bit
// - Bit 57-56: Marked1 bit
// - Bit 55-14: Object address (up to 16TB)
// - Bit 13-0:  Page offset (aligned to 8 bytes)
//
// On ARM64 (48-bit VA): similar layout with fewer address bits
//
// Color bits cycle between marking phases:
// - Phase 0: Marked0=1, Marked1=0  (marking)
// - Phase 1: Marked0=0, Marked1=1  (next marking)
// - Remapped bit: indicates if object was relocated since last mark

public class ZGCColorBits {
    // To view pointer colors, use -XX:+UnlockDiagnosticVMOptions -XX:+PrintZHeapLayout
    // ZGC requires: -XX:+UseZGC
    // Heap must be aligned to 2MB (ZPage size)
    // Min heap: 8MB, Max heap: 16TB (if OS supports 5-level paging)
}
```

### ZGC Load Barriers

Unlike G1's write barriers, ZGC uses **load barriers** — code injected before every object reference load:

```java
// Load barrier (generated by JIT):
// Object load(Object ref) {
//   if (ref is bad (color bits indicate GC action needed)) {
//     ref = slow_path(ref);  // Called on "bad" pointer
//   }
//   return ref;
// }
//
// The fast path is extremely cheap:
// - Test 2 bits in the pointer (AND, CMP)
// - Statistically, most loads see "good" pointers
// - Fast path: ~2-3 cycles on modern hardware
// - Slow path: ~100+ cycles (GC metadata update, possible relocation)
//
// The load barrier ensures:
// 1. Self-healing: once a pointer is fixed, all subsequent loads see the fixed pointer
// 2. The GC can relocate objects concurrently without stopping the mutator
// 3. No need for write barriers for correctness (marking uses different mechanism)

// Load barrier cost benchmark:
public class ZGCLoadBarrierCost {
    private static final int SIZE = 10_000_000;
    private Object[] array = new Object[SIZE];
    
    public long iterateThroughLoadBarrier() {
        long sum = 0;
        // Each array[i] load goes through the load barrier
        // On ZGC: 2 extra instructions (test + conditional branch)
        for (int i = 0; i < SIZE; i++) {
            if (array[i] != null) sum++;
        }
        return sum;
    }
}
```

### Generational ZGC (Java 21+)

ZGC gained generational capabilities in Java 21 (`-XX:+UseZGC -XX:+ZGenerational`):

```java
// Generational ZGC design:
// - Young generation: small, frequent collections (every few seconds)
// - Old generation: large, infrequent collections (every few minutes)
// - Both use colored pointers and load barriers
// - Young GC uses a "mark and relocate" cycle
// - Old GC uses concurrent mark + concurrent relocation
//
// Benefits:
// - Lower CPU overhead (most objects die young)
// - Lower memory overhead (no full heap marking)
// - Equivalent pause times (<1ms typically)

// -XX:ZCollectionInterval=30  (seconds between GC cycles)
// -XX:ZAllocationSpikeTolerance=2.0
// -XX:ZUncommitDelay=300 (seconds before returning memory to OS)
// -XX:SoftMaxHeapSize=8g (target soft max, GC tries to stay below)

public class ZGenerationalConfig {
    public static void main(String[] args) {
        // java -XX:+UseZGC -XX:+ZGenerational -Xms4g -Xmx16g
        // -Xlog:gc*:file=gc.log
        System.out.println("ZGC Generational active");
    }
}
```

### ZGC Phases

```
ZGC Cycle:
  1. Pause Mark Start (STW, <1ms)
     - Mark roots, identify live set
  2. Concurrent Mark
     - Trace object graph (load barriers handle concurrent updates)
  3. Pause Mark End (STW, <1ms)  
     - Finalize marking state
  4. Concurrent Relocation
     - Compact live objects
  5. Pause Relocation End (STW, <1ms)
     - Remap remaining references
```

## 3. Shenandoah GC

Shenandoah (available since Java 12, production since Java 21 in mainline OpenJDK) is another low-pause GC:

```java
// Shenandoah vs ZGC:
// - Shenandoah: uses Brooks forwarding pointers (extra word in object header)
// - ZGC: uses colored pointers (no header overhead)
// - Shenandoah: higher CPU overhead per object access
// - ZGC: lower CPU overhead, but requires 64-bit (can't use compressed OOPs)
// - Shenandoah: works with compressed OOPs
// - ZGC: no compressed OOPs (or partial support)

// -XX:+UseShenandoahGC
// -XX:ShenandoahGarbageThreshold=15  (% garbage in region to be collected)
// -XX:ShenandoahAllocationThreshold=0
// -XX:ShenandoahGCHeuristics=adaptive|static|compact|passive|aggressive

public class ShenandoahConfig {
    public static void main(String[] args) {
        // Shenandoah cycle phases:
        // 1. Init Mark (STW)
        // 2. Concurrent Mark
        // 3. Final Mark (STW) - process SATB queues
        // 4. Concurrent Evacuation - copy objects
        // 5. Init Update Refs (STW) - new GC cycle ready
        // 6. Concurrent Update Refs
        // 7. Final Update Refs (STW)
        
        // -Xlog:gc+shenandoah*=debug
        // View region state: -Xlog:gc+regions=debug
    }
}
```

## 4. GC Logs Analysis

### Enabling GC Logging

```java
// Java 11+ unified logging:
// -Xlog:gc*:file=gc.log:time,uptime,level,tags
// -Xlog:gc+age=trace:file=gc-age.log
// -Xlog:gc+heap=trace:file=gc-heap.log
// -Xlog:gc+region=trace:file=gc-region.log  (G1/ZGC)
// -Xlog:gc+safepoint=debug:file=gc-safepoint.log
// -Xlog:gc+ref=debug:file=gc-ref.log
//
// Log rotation:
// -Xlog:gc*:file=gc.log:time,uptime:filecount=10,filesize=10M

public class GCLogAnalysis {
    // Key metrics to extract from logs:
    // 1. Pause frequency — how often STW occurs
    // 2. Pause duration — min, max, average, P99
    // 3. Promotion rate — bytes moved from young to old
    // 4. Allocation rate — bytes allocated per second
    // 5. Heap occupancy — after GC, before GC
    // 6. GC CPU time — user+sys vs wall clock (parallelism)
    // 7. Concurrent marking duration
    // 8. Humongous allocation count/size (G1)
    
    public static void main(String[] args) {
        // Use GCViewer or gceasy.io for visualization
        // Parse logs with Regex:
        String gcLogPattern = 
            "\\[([^\\]]+)\\]\\[gc.*?\\] GC pause \\(([^)]+)\\) (\\S+?)->(\\S+?)\\((\\S+?)\\)";
    }
}
```

### Analyzing GC Pauses

```java
// Young GC pause breakdown (G1 example):
// [Parallel Time: 15.2 ms]
//   - Ext Root Scanning: 2.1 ms  (roots: threads, JNI, etc.)
//   - Update RS: 1.8 ms          (processing dirty cards)
//   - Scan RS: 4.5 ms            (scanning remembered sets)
//   - Object Copy: 5.8 ms        (copying live objects)
//   - Termination: 0.4 ms        (worker synchronization)
//   - Other: 0.6 ms
//
// Full GC pause:
// [Full GC (Allocation Failure)  1024M->512M(2048M), 5.234 secs]
// - Full GC is a sequential mark-sweep-compact
// - Usually indicates G1 failed to keep up with allocation
// - Causes: too many humongous allocations, evacuation failure
// - Fix: increase heap, tune IHOP, reduce allocation rate

public class GCPauseAnalysis {
    // -XX:+PrintGCDetails -XX:+PrintGCTimeStamps
    // Key warning signs:
    // 1. Increasing concurrent marking duration → heap too full
    // 2. Frequent To-space Exhausted → survivor space too small
    // 3. Evacuation Failure → old regions too full, promote earlier
    // 4. Humongous Allocation → objects >50% of region size
    // 5. Continuous concurrent cycles → IHOP too aggressive
    // 6. Remark long → too many SATB entries (high mutation rate)
}
```

## 5. GC Pause Time Optimization

```java
// Strategy 1: Heap Sizing
// -Xms8g -Xmx8g (fixed heap avoids resize)
// Larger heap → fewer GCs but longer pauses
//
// Strategy 2: G1-Specific
// -XX:MaxGCPauseMillis=100 (target)
// -XX:G1NewSizePercent=10 (more young space → fewer YGCs)
// -XX:G1HeapRegionSize=4m (more regions → finer granularity)
// -XX:ConcGCThreads=4 (parallel marking)
// -XX:G1RSetUpdatingPauseTimePercent=10 (more concurrent RSet work)
//
// Strategy 3: ZGC-Specific
// -XX:ConcGCThreads=4
// -XX:ZCollectionInterval=60
// -XX:ZAllocationSpikeTolerance=2.0
//
// Strategy 4: JVM Tuning
// -XX:+UseStringDeduplication (deduplicate String[] in old gen)
// -XX:+UseTLAB (thread-local allocation buffers)
// -XX:ReservedCodeCacheSize=256m (avoid code cache GC)

public class GCOptimization {
    // GC optimization workflow:
    // 1. Measure baseline (GC logs, JFR)
    // 2. Identify bottleneck
    //    a. High pause time → check roots, RSet, object copy
    //    b. High frequency → allocation rate too high
    //    c. High CPU → concurrent marking overhead
    // 3. Apply targeted fix
    // 4. Measure again
    
    // Common GC antipatterns:
    // a. System.gc() calls — disable with -XX:+DisableExplicitGC
    // b. Large young gen → longer young GC pauses
    // c. Too many threads → GC root scanning time increases
    // d. Weak/soft references → GC processing time
    // e. DirectByteBuffer allocation → full GCs for Cleaner
}
```

## 6. GC Selection Guide

```java
// GC selection criteria:
//
// Serial (-XX:+UseSerialGC):
// - Single thread, single CPU
// - Heap < 100MB
// - Client apps, low memory footprint
// - Pause times: 10-100ms per GB
//
// Parallel (-XX:+UseParallelGC):
// - Multi-threaded young and old GC
// - Throughput-focused
// - Heap < 8GB, batch processing
// - Pause times: 100ms-1s per GB
//
// G1 (-XX:+UseG1GC):
// - Default since Java 9
// - Balanced throughput and latency
// - Heap 1GB-100GB
// - Pause times: 50-200ms target
//
// ZGC (-XX:+UseZGC):
// - Ultra-low latency
// - Heap 8MB-16TB
// - Pause times: <10ms, typically <1ms
// - Higher CPU overhead (~15%)
//
// Shenandoah (-XX:+UseShenandoahGC):
// - Low latency, works with compressed OOPs
// - Heap 100MB-2TB
// - Pause times: <10ms
// - Region-based, similar to G1

public class GCSelector {
    public static String suggestGC(long maxHeapMB, boolean lowLatency) {
        if (maxHeapMB < 256) return "-XX:+UseSerialGC";
        if (!lowLatency && maxHeapMB < 8192) return "-XX:+UseParallelGC";
        if (maxHeapMB < 100 * 1024) return "-XX:+UseG1GC";
        if (lowLatency) return "-XX:+UseZGC";
        return "-XX:+UseG1GC";
    }
}
```

## 7. Memory Leak Detection

```java
// GC-based memory leak detection:
// 1. Enable GC logging: -Xlog:gc*:file=gc.log
// 2. Monitor heap growth: used after GC increasing over time
// 3. Take heap dumps:
//    jcmd <pid> GC.heap_dump dump.hprof
//    jmap -dump:live,file=dump.hprof <pid>
// 4. Analyze with Eclipse MAT or JProfiler
//
// Common memory leak patterns detected via GC:
// a. ClassLoader leak (PermGen/Metaspace growth)
// b. ThreadLocal leak (thread not cleaned up)
// c. Listener/Callback registration not removed
// d. String.intern() overuse
// e. DirectByteBuffer not released

public class MemoryLeakDetector {
    // -XX:+PrintTenuringDistribution
    // Shows object age distribution
    // If many objects survive to old gen → potential leak
    
    // -XX:+PrintHeapAtGC
    // Detailed heap state before/after GC
    
    // Monitor with jstat:
    // jstat -gc <pid> 1s
    // jstat -gccause <pid> 1s  (GC cause breakdown)
}
```

## 8. Practical GC Benchmarking

```java
public class GCBenchmark {
    // Measure allocation rate and GC impact
    private static final int OBJECTS = 10_000_000;
    
    public static void main(String[] args) {
        // Warmup
        for (int i = 0; i < 5; i++) {
            allocateAndDiscard();
        }
        
        long t0 = System.nanoTime();
        long allocated = allocateAndDiscard();
        long elapsed = System.nanoTime() - t0;
        
        System.out.printf("Allocated %d objects in %dms (%.1f MB/s)%n",
            allocated, elapsed / 1_000_000,
            (allocated * 24.0) / (elapsed / 1_000_000_000.0) / 1024 / 1024);
        // 24 bytes = approximate object size (header + int field)
    }
    
    private static long allocateAndDiscard() {
        long count = 0;
        for (int i = 0; i < OBJECTS; i++) {
            Object o = new Object(); // TLAB allocation
            count++;
        }
        return count;
    }
}
```

GC optimization is an empirical science. Always measure before and after changes. The GC logs are the single most important diagnostic tool.
