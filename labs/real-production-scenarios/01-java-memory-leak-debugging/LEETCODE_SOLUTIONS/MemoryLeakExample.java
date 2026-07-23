package com.prod.solutions.memoryleak;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates a ThreadLocal memory leak in a thread-pooled environment.
 * Inspired by the Netflix Zuul Metaspace leak (INC-2024-0415-ZUUL-OOM).
 *
 * BUG: ThreadLocal values are set but never removed. Since threads are pooled,
 * stale entries accumulate in each thread's ThreadLocalMap, preventing GC
 * of the referenced objects and their ClassLoaders.
 */
public class MemoryLeakExample {

    private static final ThreadLocal<byte[]> threadLocalData = new ThreadLocal<>();
    private static final List<byte[]> leakAccumulator = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== ThreadLocal Memory Leak Demo ===");
        System.out.println("Simulating request processing with ThreadLocal leak...");

        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (int request = 0; request < 100; request++) {
            final int reqId = request;
            executor.submit(() -> {
                // BUG: ThreadLocal is set but never removed
                threadLocalData.set(new byte[1024 * 100]); // 100KB per request
                // Simulate processing
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                // BUG: No threadLocalData.remove() called!
                System.out.printf("[Request %d] Thread %s processed. Leak = %d bytes%n",
                        reqId, Thread.currentThread().getName(), countLeakedBytes());
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.printf("%nFinal leaked byte count per thread: %d%n", countLeakedBytes());
        System.out.println("Run FixedMemoryLeakExample to see the fix.");
        System.out.println("=== End of leak demo ===");
    }

    private static long countLeakedBytes() {
        long total = 0;
        for (byte[] data : leakAccumulator) {
            total += data.length;
        }
        return total;
    }
}
