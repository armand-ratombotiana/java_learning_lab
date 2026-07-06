package com.javaacademy.lab50.objectlayout;

/**
 * Simulates Thread-Local Allocation Buffer (TLAB) behavior.
 * Each thread gets a dedicated region of Eden space for allocations,
 * avoiding contention on shared allocation pointers. This demo creates
 * many thread-local allocations to observe TLAB effects.
 */
public class TlabSimulationExample {

    private static final int THREADS = 8;
    private static final int ALLOCS_PER_THREAD = 5_000_000;

    public static void main(String[] args) throws Exception {
        System.out.println("=== TLAB Simulation ===");
        System.out.println("Allocating " + (THREADS * ALLOCS_PER_THREAD)
            + " objects across " + THREADS + " threads");
        System.out.println("Run with -XX:+PrintTLAB to see TLAB stats");

        Thread[] threads = new Thread[THREADS];
        for (int t = 0; t < THREADS; t++) {
            final int threadId = t;
            threads[t] = new Thread(() -> {
                long threadTotal = 0;
                for (int i = 0; i < ALLOCS_PER_THREAD; i++) {
                    // Simulate TLAB-allocated objects
                    var obj = new TlabObject(threadId, i);
                    threadTotal += obj.getValue();
                }
                System.out.println("Thread " + threadId + " finished, total=" + threadTotal);
            }, "TLAB-Allocator-" + t);
            threads[t].start();
        }
        for (Thread t : threads) t.join();
        System.out.println("TLAB simulation complete.");
    }

    record TlabObject(int threadId, int sequence) {
        public int getValue() { return threadId + sequence; }
    }
}
