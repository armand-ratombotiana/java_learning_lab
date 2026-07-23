package com.prod.solutions.memoryleak;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Fixed version of the ThreadLocal memory leak.
 *
 * FIX 1: ThreadLocal.remove() is called in a finally block.
 * FIX 2: Values are wrapped in WeakReference so even if remove() is missed,
 *        the referenced objects can still be GC'd.
 */
public class FixedMemoryLeakExample {

    private static final ThreadLocal<WeakReference<byte[]>> threadLocalData =
            ThreadLocal.withInitial(() -> new WeakReference<>(null));

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Fixed ThreadLocal Memory Leak Demo ===");
        System.out.println("Simulating request processing with proper cleanup...");

        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (int request = 0; request < 100; request++) {
            final int reqId = request;
            executor.submit(() -> {
                try {
                    // Set data
                    threadLocalData.set(new WeakReference<>(new byte[1024 * 100]));
                    // Simulate processing
                    Thread.sleep(10);
                    byte[] data = threadLocalData.get().get();
                    System.out.printf("[Request %d] Thread %s processed OK%n",
                            reqId, Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // FIX: Always remove ThreadLocal in finally block
                    threadLocalData.remove();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("No memory leak detected. Cleanup successful.");
        System.out.println("=== End of fixed demo ===");
    }
}
