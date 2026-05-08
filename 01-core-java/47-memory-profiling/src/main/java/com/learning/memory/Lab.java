package com.learning.memory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.lang.ref.*;

public class Lab {

    static class BigObject {
        private final byte[] data = new byte[1024 * 1024]; // 1MB
        private static final AtomicInteger counter = new AtomicInteger(0);
        final int id = counter.incrementAndGet();

        protected void finalize() {
            counter.decrementAndGet();
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Memory Profiling Lab ===\n");

        heapStructure();
        objectAllocation();
        gcBasics();
        memoryLeakDemo();
        weakReferences();
        profilingTools();
        jvmFlags();
    }

    static void heapStructure() {
        System.out.println("--- JVM Heap Structure ---");
        System.out.println("""
  Young Gen (Eden + S0 + S1)          - new objects
    Eden: most objects allocated here
    Survivor spaces: objects surviving minor GC
  Old Gen (Tenured)                   - long-lived objects
  Metaspace (native, not heap)        - class metadata

  Default ratio (G1): young:old ~ 1:1
  -Xmn=2g sets young gen size
  -XX:NewRatio=3 -> young:old = 1:3
    """);
    }

    static void objectAllocation() {
        System.out.println("--- Object Allocation & Escape Analysis ---");

        long before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        var list = new ArrayList<BigObject>();
        for (int i = 0; i < 5; i++) list.add(new BigObject());
        long after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        System.out.printf("  Allocated 5x 1MB objects: ~%d MB heap delta%n", (after - before) / (1024 * 1024));
        System.out.println("  Stack allocation: small objects may be allocated on stack");
        System.out.println("  TLAB (Thread-Local Allocation Buffer): thread-local Eden region");
        System.out.println("  Bump-the-pointer: fast sequential Eden allocation");
    }

    static void gcBasics() {
        System.out.println("\n--- GC Basics ---");
        System.out.println("""
  Minor GC: cleans Young Gen (STW, <~10ms)
  Major GC: cleans Old Gen (STW, longer)
  Full GC: whole heap + metaspace (STW, slowest)

  GC Algorithms:
  Serial     - single thread, STW (client, <100MB heap)
  Parallel   - multi-thread, STW (throughput-oriented)
  G1GC       - region-based, low-pause (default since JDK 9)
  ZGC        - concurrent, <10ms pause (<~1TB heap)
  Shenandoah - concurrent, low-pause (like ZGC)
    """);
    }

    static void memoryLeakDemo() {
        System.out.println("--- Memory Leak Scenarios ---");
        var cache = new HashMap<String, byte[]>();

        for (int i = 0; i < 100; i++) {
            cache.put("key-" + System.nanoTime(), new byte[10_000]);
        }
        System.out.println("  Leaky cache size: " + cache.size() + " entries (never removed)");

        var leakyList = new ArrayList<BigObject>();
        try { for (int i = 0; i < 3; i++) leakyList.add(new BigObject()); }
        catch (OutOfMemoryError e) { System.out.println("  OOM: " + e.getMessage()); }

        System.out.println("""
  Common leaks:
  1. HashMap key without hashCode/equals -> accumulates duplicates
  2. ThreadLocal not removed -> classloader leak
  3. static collections growing unbounded
  4. Unclosed streams/connections
  5. Inner class holding outer class reference
    """);
    }

    static void weakReferences() {
        System.out.println("\n--- Reference Types ---");
        var strong = new Object();
        var soft = new SoftReference<>(new Object());
        var weak = new WeakReference<>(new Object());
        var phantom = new PhantomReference<>(new Object(), new ReferenceQueue<>());

        System.out.println("  Strong: " + strong + " (not GC'd until nulled)");
        System.out.println("  Soft:   " + soft.get() + " (GC'd before OOM)");
        System.out.println("  Weak:   " + weak.get() + " (GC'd on next cycle)");
        System.out.println("  Phantom:" + phantom.get() + " (never get(), notified after finalize)");

        System.gc();
        System.out.println("\n  After System.gc():");
        System.out.println("  Strong: " + strong);
        System.out.println("  Soft:   " + soft.get());
        System.out.println("  Weak:   " + weak.get());
        System.out.println("  Phantom:" + phantom.get());
    }

    static void profilingTools() {
        System.out.println("\n--- Profiling Tools ---");
        System.out.println("""
  jcmd <pid> GC.heap_dump dump.hprof - heap dump
  jmap -dump:live,format=b,file=heap.hprof <pid>
  jstat -gcutil <pid> 1s - live GC stats
  jconsole                - JMX monitoring
  VisualVM / JMC          - heap analysis
  Eclipse MAT / JProfiler - leak detection

  Key metrics:
  GC pause time & frequency
  Allocation rate (MB/s)
  Heap usage after Full GC
  Metaspace growth
    """);
    }

    static void jvmFlags() {
        System.out.println("--- Memory-Related JVM Flags ---");
        System.out.println("""
  -Xms4g -Xmx4g                  heap size
  -Xmn2g                         young gen size
  -XX:+UseG1GC                   G1GC
  -XX:MaxGCPauseMillis=100       target pause
  -XX:InitiatingHeapOccupancyPercent=45  G1 starts concurrent cycle at 45%
  -XX:+HeapDumpOnOutOfMemoryError
  -XX:HeapDumpPath=/tmp/dump.hprof
  -Xlog:gc*:file=gc.log          detailed GC logging
    """);
    }
}
