package com.learning.jvm;

import java.lang.management.*;
import java.util.*;

public class JVMInternalsLab {

    public static void main(String[] args) {
        System.out.println("=== JVM Internals Lab ===\n");

        System.out.println("1. Memory Regions:");
        System.out.println("   - Heap: Objects, arrays (shared across threads)");
        System.out.println("   - Stack: Primitives, references (per-thread)");
        System.out.println("   - Metaspace: Class metadata (replaces PermGen)");
        System.out.println("   - Code Cache: JIT-compiled native code");

        System.out.println("\n2. Runtime Info:");
        Runtime rt = Runtime.getRuntime();
        System.out.println("   Available processors: " + rt.availableProcessors());
        System.out.println("   Max memory: " + (rt.maxMemory() / 1024 / 1024) + " MB");
        System.out.println("   Total memory: " + (rt.totalMemory() / 1024 / 1024) + " MB");
        System.out.println("   Free memory: " + (rt.freeMemory() / 1024 / 1024) + " MB");

        System.out.println("\n3. Class Loading:");
        System.out.println("   ClassLoader: " + JVMInternalsLab.class.getClassLoader());
        System.out.println("   Parent: " + JVMInternalsLab.class.getClassLoader().getParent());
        System.out.println("   Bootstrap: " + String.class.getClassLoader());

        System.out.println("\n4. Garbage Collection:");
        System.out.println("   GC Algorithms:");
        System.out.println("   - Serial GC: Single-threaded, for small heaps");
        System.out.println("   - Parallel GC: Multi-threaded, throughput-focused");
        System.out.println("   - G1 GC: Low-pause, default since Java 9");
        System.out.println("   - ZGC: Ultra-low latency, <1ms pauses (Java 15+)");
        System.out.println("   - Shenandoah: Concurrent compaction (Java 12+)");
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gc : gcBeans) {
            System.out.println("   Active GC: " + gc.getName());
        }

        System.out.println("\n5. Thread Info:");
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long[] threadIds = threadBean.getAllThreadIds();
        System.out.println("   Active threads: " + threadIds.length);
        for (ThreadInfo info : threadBean.getThreadInfo(threadIds, 2)) {
            if (info != null)
                System.out.println("   - " + info.getThreadName() + " (" + info.getThreadState() + ")");
        }

        System.out.println("\n6. JMX MBeans:");
        System.out.println("   JVM uptime: " + ManagementFactory.getRuntimeMXBean().getUptime() + "ms");
        System.out.println("   JVM name: " + ManagementFactory.getRuntimeMXBean().getName());
        System.out.println("   Spec version: " + System.getProperty("java.specification.version"));
        System.out.println("   VM version: " + System.getProperty("java.vm.version"));

        System.out.println("\n7. JIT Compilation:");
        System.out.println("   - C1 (Client): Quick startup, less optimization");
        System.out.println("   - C2 (Server): Slower startup, aggressive optimization");
        System.out.println("   - Tiered compilation: C1 -> C2 (default)");
        System.out.println("   - -XX:+PrintCompilation shows JIT activity");

        System.out.println("\n8. Heap Dump & Monitoring Flags:");
        System.out.println("   - -Xms512m -Xmx2g: Heap sizing");
        System.out.println("   - -XX:+UseG1GC: Select G1 collector");
        System.out.println("   - -XX:+HeapDumpOnOutOfMemoryError");
        System.out.println("   - -XX:HeapDumpPath=/path/to/dump");
        System.out.println("   - -XX:+PrintGCDetails -Xlog:gc");

        System.out.println("\n9. Object Memory Layout:");
        System.out.println("   - Object header: Mark word (8 bytes) + Klass pointer (4-8 bytes)");
        System.out.println("   - Instance data: Fields aligned to word boundaries");
        System.out.println("   - Padding: Align to 8-byte boundary");
        System.out.println("   - Array: Header + length (4 bytes) + element data");

        System.out.println("\n=== JVM Internals Lab Complete ===");
    }
}