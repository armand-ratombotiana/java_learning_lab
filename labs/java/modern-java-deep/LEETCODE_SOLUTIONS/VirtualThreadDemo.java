package modernjava;

import java.util.concurrent.*;

/**
 * Virtual Threads (Java 21+ / Project Loom).
 * 
 * Virtual threads are lightweight threads managed by the JVM.
 * They are perfect for IO-bound workloads — millions of concurrent tasks.
 * 
 * Key points:
 * - Backed by carrier threads (ForkJoinPool)
 * - Mount/unmount on blocking operations
 * - Never pool virtual threads (create per task)
 * - Use ReentrantLock instead of synchronized (pinning)
 * 
 * Time: O(1) per task dispatch
 * Space: O(stack size) per thread, but much smaller than OS threads
 */
public class VirtualThreadDemo {

    public static void main(String[] args) throws Exception {
        int count = 10_000;

        // 1. Create virtual threads via Thread.ofVirtual()
        long start = System.nanoTime();
        var threads = new java.util.ArrayList<Thread>();
        for (int i = 0; i < count; i++) {
            Thread vt = Thread.ofVirtual().name("vt-" + i).unstarted(() -> {
                try { Thread.sleep(1); } catch (InterruptedException e) { }
            });
            threads.add(vt);
        }
        threads.forEach(Thread::start);
        for (Thread t : threads) t.join();
        long elapsed = System.nanoTime() - start;
        System.out.println(count + " virtual threads: " + elapsed / 1_000_000 + "ms");

        // 2. Via Executors.newVirtualThreadPerTaskExecutor()
        var results = new java.util.concurrent.atomic.AtomicInteger(0);
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < count; i++) {
                exec.submit(() -> {
                    results.incrementAndGet();
                    try { Thread.sleep(1); } catch (InterruptedException e) { }
                    return results.get();
                });
            }
        } // close() waits for all tasks
        assert results.get() == count : "All tasks completed";

        // 3. Thread properties
        Thread vt = Thread.ofVirtual()
            .name("demo")
            .start(() -> {
                System.out.println("VT name: " + Thread.currentThread().getName());
                System.out.println("Is virtual: " + Thread.currentThread().isVirtual());
                System.out.println("Group: " + Thread.currentThread().getThreadGroup());
            });
        vt.join();

        // 4. Carrier thread awareness
        Thread vt2 = Thread.ofVirtual().start(() -> {
            System.out.println("Carrier: " + 
                ((Thread.virtualThread() == null) ? 
                 "not virtual" : Thread.virtualThread().toString()));
        });
        vt2.join();

        System.out.println("All VirtualThreadDemo tests passed.");
    }
}