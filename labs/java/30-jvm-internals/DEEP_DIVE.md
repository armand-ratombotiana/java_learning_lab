# Deep Dive: JVM Internals

## 1. JVM Start-Up Sequence

When a Java process starts (`java -jar MyApp.jar`), the following sequence unfolds:

### Phase 1: Native Bootstrap
1. The `java` launcher (C++ code in `src/java.base/share/native/launcher/`) parses arguments, sets up environment variables
2. Creates the JVM via `JNI_CreateJavaVM()` in HotSpot's `src/share/vm/runtime/init.cpp`
3. This calls `Threads::create_vm()` which initializes global data structures

### Phase 2: Memory Region Initialization
4. `init_globals()` initializes:
   - **Universe** (heap, permgen/metaspace)
   - **CollectedHeap** (GC-specific heap initialization)
   - **SymbolTable** and **StringTable** (interned strings)
   - **SystemDictionary** (class registry)
   - **CodeCache** (JIT compiled code storage)
5. `vm_init_globals()` → `os::init()` (OS-specific), `os::init_2()` (large page config, stack size)

### Phase 3: Java-Level Initialization
6. `JavaCalls::call_default_constructor()` on `java.lang.System` class initializes:
   - `System::initializeSystemClass()` → registers native methods, sets `in`, `out`, `err`
   - Loads `java.lang.Thread`, creates the main thread
7. `java.lang.Thread` init creates the JVM TI (Tool Interface) agent
8. Finalization and reference handler threads start

### Phase 4: Main Class Loading
9. The application's main class is loaded via the application class loader
10. `ClassLoader.loadClass()` delegates through parent-first delegation
11. Bytecode verification (pass 1-4 verification)
12. Class linking: verification → preparation → resolution (optional step)
13. Class initialization: `<clinit>` runs static initializers

```java
// To observe startup, run with:
// -XX:+TraceClassLoading -XX:+LogCompilation -XX:+PrintGC
public class StartupObserver {
    public static void main(String[] args) {
        // With -agentlib:hprof=file=startup.hprof
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Startup complete");
        }));
    }
}
```

## 2. Memory Regions Deep Dive

### Metaspace (replacing PermGen since Java 8)

Metaspace is **not** in the Java heap. It's native memory managed by the `Metaspace` object in HotSpot.

```java
// -XX:MaxMetaspaceSize=256m -XX:MetaspaceSize=128m
// -XX:CompressedClassSpaceSize=1g -XX:+PrintMetaspaceStatisticsAtExit
```

**Metaspace internals:**
- Composed of **chunks** (1KB-4MB) allocated from the "chunk freelist" 
- Chunks are carved from **virtual space nodes** (backed by `mmap` segments of 2MB each)
- A `ClassLoaderData` owns a list of chunks per class loader
- The `MetaspaceArena` allocates within a chunk using bump-pointer allocation
- When a class loader dies, all its class metadata becomes reclaimable (class unloading)
- Chunks are returned to the global freelist for reuse

**Chunk types:**
- Specialized chunks (1KB) — for small class loaders
- Small chunks (4KB) — for typical class loaders  
- Medium chunks (64KB) — for large class loaders
- Humongous chunks (4MB) — for huge metadata allocations

```java
// Programmatic Metaspace monitoring
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;

public class MetaspaceMonitor {
    public static void check() {
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            if ("Metaspace".equals(pool.getName())) {
                System.out.println("Metaspace used: " + pool.getUsage().getUsed());
                System.out.println("Metaspace max: " + pool.getUsage().getMax());
            }
            if ("Compressed Class Space".equals(pool.getName())) {
                System.out.println("CCS used: " + pool.getUsage().getUsed());
            }
        }
    }
}
```

### CodeHeap (JIT Code Cache)

The code cache stores compiled native code. Split into three regions (with tiered compilation):

```java
// -XX:+PrintCodeCache -XX:ReservedCodeCacheSize=256m
// -XX:NonMethodCodeHeapSize=120m -XX:ProfiledCodeHeapSize=140m
// -XX:NonProfiledCodeHeapSize=120m
```

**Code Heap Segments:**
1. **Non-method code heap** — JVM internal code (stubs, adapters, runtime calls)
2. **Profiled code heap** — C1 compiled code with profiling (tier 2, tier 3)
3. **Non-profiled code heap** — C2 compiled code (tier 4), C1 without profiling (tier 1)

Each code blob has a header with:
- `_type` (non-profiled, profiled, non-method)
- `_compiler` (C1, C2, interpreter)
- Immutable `_instructions_start` and `_instructions_end` pointers

```java
// Observe code cache behavior
public class CodeCacheObserver {
    public static void main(String[] args) {
        for (int i = 0; i < 100_000; i++) {
            Math.sin(i); // Triggers JIT compilation for hot method
        }
        // Run with -XX:+PrintCodeCache to see usage
    }
}
```

### Compressed Class Space

A contiguous region of native memory (default 1GB) where class metadata pointers live when `-XX:+UseCompressedClassPointers` is active.

- The base address is stored in `CompressedKlassPointers::_base`
- Encoding/decoding is a simple shift/add: `pointer = (uintptr_t)((intx)encoding << shift) + base`
- Requires `-XX:+UseCompressedOops` to also be active (default for heaps < 32GB)
- If class metadata exceeds the reserved space, Metaspace OOM occurs even if Metaspace overall has room

## 3. Object Header Internals

Every Java object in HotSpot has a header preceding its instance fields.

### 64-bit Object Layout

```
|----------------------------------------------------|
|         Mark Word (8 bytes)                        |
|----------------------------------------------------|
|   Klass Pointer (4 bytes with COOPs, else 8)       |
|----------------------------------------------------|
|   Instance fields (packed in declared order)        |
|----------------------------------------------------|
|   Padding (to 8-byte boundary)                     |
|----------------------------------------------------|
```

Total overhead: 12 bytes with compressed OOPs, 16 bytes without.

```java
// Use JOL (Java Object Layout) to view object headers
// Add dependency: org.openjdk.jol:jol-core:0.17
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
import org.openjdk.jol.vm.VM;

public class ObjectHeaderViewer {
    public static void main(String[] args) {
        System.out.println(VM.current().details());
        System.out.println(ClassLayout.parseInstance(new Object()).toPrintable());
        // Sample output shows:
        // OFFSET  SIZE   TYPE DESCRIPTION       VALUE
        //      0     8        (object header: mark)     0x0000000000000001 (biased:0)
        //      8     4        (object header: class)    0x00000000
        //     12     4        (loss due to the next object alignment)
    }
}
```

### Mark Word Bit Layout

The 8-byte mark word is a tagged union:

```
Biased Locking (biased_lock=1, age=0, pattern=10):
[0:2]   = biased_lock (1) + lock (0)  = "10" = biased
[3:7]   = age (5 bits, max 31)
[8:9]   = pattern (10 for biased)
[10:54] = thread ID (45 bits, JavaThread*)
[54:56] = epoch (2 bits)
[62:63] = unused

Thin Lock (parked, pattern=00):
[0:2]   = biased_lock (0) + lock (1)  = "01" = unlocked
[3:7]   = age (5 bits)
[8:63]  = identity_hashcode (25 bits on 64-bit with COOPs)

Inflated Lock (heavy, pattern=10):
[0:2]   = biased_lock (0) + lock (2)  = "10" = monitor
[3:63]  = pointer to ObjectMonitor (61 bits)

GC Forwarding (pattern=11):
[0:2]   = biased_lock (0) + lock (3)  = "11" = forwarding
[3:63]  = forwarding pointer to new location (used during compaction)
```

**Age bits:** Each GC cycle the object survives, its age increments (max 15 by default, controlled by `-XX:MaxTenuringThreshold`). At max age, it promotes to the old generation.

```java
public class MarkWordExplorer {
    public static void checkHashCode() {
        Object o = new Object();
        // Before hashCode(): mark word has no hash, thread ID present if biased
        System.out.println("Before: " + ClassLayout.parseInstance(o).toPrintable());
        
        int hash = System.identityHashCode(o);
        // After hashCode(): hash stored in mark word (lazy computation)
        System.out.println("After: " + ClassLayout.parseInstance(o).toPrintable());
        System.out.printf("Hash: 0x%08x%n", hash);
    }
}
```

### Klass Pointer

The klass pointer points to an internal structure (`InstanceKlass` or `ArrayKlass`) that describes the object's type.

```
InstanceKlass layout:
- _name (Symbol*) — e.g., "java/lang/String"
- _super (Klass*) — superclass
- _fields (Array<u2>) — field descriptors
- _methods (Array<Method*>) — method table
- _vtable_len — virtual method table length
- _itable_len — interface method table length
- _size — instance size in bytes
- _vtable[] — actual virtual method dispatch table
- _itable[] — interface method dispatch table
```

The klass pointer goes through one level of indirection: `this->klass()->*` followed by a possible adjustment through `klass->_klass` (the metaclass pointer). The actual `InstanceKlass` is reached after subtracting the `_offset`.

## 4. Oop Maps (Object Pointer Maps)

Oop maps are metadata that tell the GC where object references exist on the stack and in registers. They are critical for precise GC.

### Structure

Each safepoint-eligible instruction in compiled code has an associated `OopMap`:

```java
// Internal representation (C++ in HotSpot):
// class OopMap {
//     int _offset;        // instruction offset
//     int _num_locals;    // # of stack slots
//     int _num_expressions; // # of expression stack slots
//     OopMapSet* _set;    // compressed bit vector
// };
```

- Each `OopMap` contains a bitmask for every register and stack slot
- A '1' bit indicates an oop (object pointer) at that location
- `OopMapSet` contains all OopMaps for a compiled method, one per safepoint

```java
// Observe oop maps with -XX:+PrintOopMaps
public class OopMapExample {
    private String name;
    private Object data;
    
    public void process() {
        String local = name;
        Object temp = data;
        System.gc(); // Safepoint — oop maps enable GC to find local and temp
        System.out.println(local);
    }
}
```

The GC uses oop maps during **root scanning** — it knows exactly which stack slots and registers contain oops without conservative scanning. This means:
- Precise GC (no false positives)
- Objects are not pinned unnecessarily
- Compacting collectors can safely move objects

## 5. Safepoints

A safepoint is a point in execution where the thread's state is well-defined and the GC (or other VM operations) can inspect it.

### How Safepoints Work

1. **VM Operation Request**: A thread requests a GC, deoptimization, or other VM operation
2. **Safepoint Synchronization**: The requesting thread calls `SafepointSynchronize::begin()`
3. **Thread Polling**: All threads must reach a safepoint. At each back-edge and method entry, compiled code checks `_poll_page` (a memory page that gets protected)
4. **Signal-Based**: When the safepoint starts, the page is mprotect'd (SIGSEGV), which threads hit at the next poll, entering the safepoint handler
5. **VM Operation Runs**: All threads are blocked at the safepoint, the operation (e.g., GC) runs
6. **Resume**: `SafepointSynchronize::end()` unprotects the page, threads resume

```java
// -XX:+PrintSafepointStatistics -XX:PrintSafepointStatisticsCount=1
// -XX:+SafepointTimeout -XX:SafepointTimeoutDelay=1000
public class SafepointObserver {
    public static void main(String[] args) throws Exception {
        // A long running loop may cause safepoint stalls
        // because safepoint polls only occur at back-edges
        long start = System.nanoTime();
        for (int i = 0; i < 1_000_000; i++) {
            // tight loop — safepoint poll happens here
            Math.sin(i);
        }
        System.out.println("Elapsed: " + (System.nanoTime() - start) / 1e6 + "ms");
    }
}
```

### Safepoint Causes

Common reasons for safepoints (visible in `-XX:+PrintSafepointStatistics`):
- `G1CollectFull` — Full GC
- `G1CollectForAllocation` — Young GC
- `BulkRevokeBias` — Biased locking revocation
- `Deoptimize` — Deoptimization of compiled code
- `RevokeBias` — Per-thread bias revocation
- `ThreadDump` — Thread dumps (`jstack`, `jcmd`)
- `ClassLoaderLoadInstance` — Loading new classes

### Safepoint Metrics

Key metrics (from `-XX:+PrintSafepointStatistics`):
- **vmop** — the VM operation name
- **safepoint** — total time in safepoint
- **spin** — time waiting for threads to reach safepoint
- **page** — time to protect/unprotect polling page
- **operation** — time executing the actual operation

## 6. JMX MBeans for JVM Monitoring

The JVM exposes management and monitoring capabilities via JMX. The `java.lang.management` package provides MXBeans that wrap the internal JVM.

```java
import java.lang.management.*;
import javax.management.*;

public class JVMMXBeanDeepDive {
    public static void main(String[] args) throws Exception {
        // Thread MXBean — thread counts, deadlocks
        ThreadMXBean thread = ManagementFactory.getThreadMXBean();
        System.out.println("Peak threads: " + thread.getPeakThreadCount());
        System.out.println("Current threads: " + thread.getThreadCount());
        System.out.println("Total started: " + thread.getTotalStartedThreadCount());
        
        // Detect deadlocks
        long[] deadlocked = thread.findDeadlockedThreads();
        if (deadlocked != null) {
            for (long id : deadlocked) {
                ThreadInfo info = thread.getThreadInfo(id, Integer.MAX_VALUE);
                System.out.println("Deadlocked: " + info.getThreadName());
            }
        }
        
        // Memory MXBean — heap usage
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memory.getHeapMemoryUsage();
        System.out.printf("Heap: used=%dMB, committed=%dMB, max=%dMB%n",
            heapUsage.getUsed() / 1024 / 1024,
            heapUsage.getCommitted() / 1024 / 1024,
            heapUsage.getMax() / 1024 / 1024);
        
        // GC MXBeans
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.printf("GC %s: count=%d, time=%dms%n",
                gc.getName(), gc.getCollectionCount(), gc.getCollectionTime());
        }
        
        // Memory Pool details
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            MemoryUsage usage = pool.getUsage();
            System.out.printf("Pool %s: used=%d/%d%n",
                pool.getName(), usage.getUsed(), usage.getMax());
            // Type: HEAP or NON_HEAP
            System.out.println("  Type: " + pool.getType());
            // Memory managers that manage this pool
            for (String mgr : pool.getMemoryManagerNames()) {
                System.out.println("  Manager: " + mgr);
            }
        }
        
        // Platform MBean Server — custom monitoring
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName osName = new ObjectName("java.lang:type=OperatingSystem");
        System.out.println("Process CPU: " + 
            server.getAttribute(osName, "ProcessCpuLoad"));
        
        // Compilation MXBean
        CompilationMXBean comp = ManagementFactory.getCompilationMXBean();
        System.out.println("JIT compiler: " + comp.getName());
        System.out.println("Total compile time: " + comp.getTotalCompilationTime() + "ms");
    }
}
```

### HotSpot Diagnostic MBeans

Beyond the standard MXBeans, HotSpot provides diagnostic MBeans (with `-XX:+UnlockDiagnosticVMOptions`):

- `com.sun.management:type=HotSpotDiagnostic` — GC heap dump, VMOption get/set
- `com.sun.management:type=GarbageCollector,name=*` — Extended GC info (last GC cause, last GC duration)

```java
// Access HotSpot Diagnostic MBean
import com.sun.management.HotSpotDiagnosticMXBean;
import java.lang.management.ManagementFactory;

public class HotSpotDiagnostic {
    public static void main(String[] args) throws Exception {
        HotSpotDiagnosticMXBean diagnostic = ManagementFactory
            .getPlatformMXBean(HotSpotDiagnosticMXBean.class);
        
        // Generate heap dump programmatically
        diagnostic.dumpHeap("heap.hprof", true);
        
        // Check/set VM options at runtime
        System.out.println(diagnostic.getVMOption("PrintGC"));
    }
}
```

### JMS / MXBean Custom Events

You can emit custom JMX notifications:

```java
import javax.management.*;
import java.lang.management.ManagementFactory;

public class CustomJMXMonitor {
    private final MBeanServer mbs;
    private final NotificationBroadcasterSupport broadcaster;
    
    public CustomJMXMonitor() {
        mbs = ManagementFactory.getPlatformMBeanServer();
        broadcaster = new NotificationBroadcasterSupport();
    }
    
    public void emitMemoryPressure(long usedBytes) {
        Notification n = new Notification(
            "jvm.memory.pressure", this, 
            System.currentTimeMillis(), 
            "Memory pressure: " + usedBytes);
        n.setUserData(usedBytes);
        broadcaster.sendNotification(n);
    }
}
```

## 7. Benchmarking: Object Header Overhead

```java
// Comparing object sizes with JOL
// Expected output on 64-bit HotSpot with compressed OOPs:
// Integer: 16 bytes (12 header + 4 int value)
// Point: 24 bytes (12 header + 8 fields + 4 padding)
// Empty String: 24 bytes (12 header + 12 char[] ref + padding)
public class ObjectSizeBenchmark {
    // Simple object
    static class Point {
        int x;
        int y;
    }
    
    // Object with reference
    static class Node {
        Object value;
        Node next;
    }
    
    public static void main(String[] args) {
        // Use JOL from command line:
        // java -jar jol-cli.jar internals java.lang.Integer
        System.out.println("Run with: jol-cli.jar internals");
    }
}
```

## 8. Conclusion

Understanding JVM internals at this level enables:
- **Accurate performance tuning** — knowing the real cost of objects, locks, and GC
- **Diagnosing production issues** — using safepoint analysis, oop map inspection
- **Memory optimization** — leveraging compressed OOPs, TLABs, and escape analysis
- **Monitoring systems** — building robust JMX-based observability

Key takeaway: The JVM is not a black box. Every object header byte, every safepoint poll, every memory region has a purpose and a cost. Profiling and observation tools make these internals visible.
