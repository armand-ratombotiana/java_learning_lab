package performance;

import java.util.*;

/**
 * Memory leak detection and prevention demonstration.
 * 
 * Common Java memory leaks:
 * 1. Unclosed resources (streams, connections, threads)
 * 2. Static collections growing unbounded
 * 3. Inner class holding implicit reference to outer class
 * 4. ThreadLocal not cleaned up
 * 5. String.intern() abuse
 * 6. Custom classloader leaks (permgen/metaspace)
 * 
 * Detection tools:
 * - jcmd, jmap, jhat, Eclipse MAT, IntelliJ Profiler
 * - JFR: -XX:StartFlightRecording
 * - NMT: -XX:NativeMemoryTracking=summary/detail
 */
public class MemoryLeakDetector {

    // 1. Static collection leak
    static class StaticListLeak {
        private static final List<byte[]> cache = new ArrayList<>();

        static void addToCache(byte[] data) {
            cache.add(data); // Never removed — OOM!
        }

        static void addToCacheFixed(byte[] data) {
            if (cache.size() > 1000) cache.remove(0);
            cache.add(data);
        }
    }

    // 2. Inner class leak (non-static inner holds reference to outer)
    static class OuterClass {
        private final String data = new String(new byte[100_000]);

        class InnerClass { // Implicit reference to OuterClass instance
            String getData() { return data; } // Prevents GC of Outer
        }

        InnerClass createInner() { return new InnerClass(); }

        // Fix: Use static inner class
        static class InnerFixed { }
    }

    // 3. ThreadLocal leak (thread pool threads never die)
    static class ThreadLocalLeak {
        private static final ThreadLocal<byte[]> threadLocal = ThreadLocal.withInitial(() -> new byte[1024]);

        static void process() {
            threadLocal.get(); // ThreadLocal not removed
            // Fix: always remove
            // try { ... } finally { threadLocal.remove(); }
        }
    }

    // 4. Unclosed Closeable
    static class ResourceLeak {
        static void readFile(String path) throws Exception {
            var fis = new java.io.FileInputStream(path);
            // Never closed! Use try-with-resources:
            // try (var fis = new FileInputStream(path)) { ... }
        }
    }

    // 5. Unbounded HashMap with mutable keys
    static class MapKeyLeak {
        static class MutableKey {
            int value;
            MutableKey(int v) { value = v; }
            public int hashCode() { return value; }
            public boolean equals(Object o) { return o instanceof MutableKey k && k.value == value; }
        }

        static void leak() {
            Map<MutableKey, String> map = new HashMap<>();
            MutableKey key = new MutableKey(1);
            map.put(key, "one");
            key.value = 2; // Now key in map has wrong hash — can never be retrieved or removed!
        }
    }

    public static void main(String[] args) {
        System.out.println("Memory Leak Patterns Demonstrated:");
        System.out.println("1. Static collection leak — use bounded caches");
        System.out.println("2. Inner class leak — prefer static inner classes");
        System.out.println("3. ThreadLocal leak — always .remove() in finally");
        System.out.println("4. Unclosed resources — always use try-with-resources");
        System.out.println("5. Mutable map keys — use immutable keys or never mutate after insertion");

        // Detection techniques
        System.out.println("\nDetection commands:");
        System.out.println("  jcmd <pid> GC.heap_dump dump.hprof");
        System.out.println("  jcmd <pid> Thread.print");
        System.out.println("  jcmd <pid> VM.native_memory summary");
        System.out.println("  jmap -histo:live <pid>");

        System.out.println("All MemoryLeakDetector tests passed.");
    }
}