package concurrencydeep;

import java.util.concurrent.Phaser;

/**
 * Phaser deep dive — a flexible reusable barrier.
 * 
 * Phaser is like CyclicBarrier + CountDownLatch combined:
 * - Dynamic party registration (parties can register/deregister)
 * - Reusable across phases
 * - Can be used as a CountDownLatch (arriveAndAwaitAdvance)
 * 
 * Use cases: multi-phase computations, dynamic thread pools
 * 
 * Time: O(1) per arrive/await
 * Space: O(phases + parties)
 */
public class PhaserExample {

    public static void main(String[] args) {
        // Example 1: Reusable barrier for multi-phase computation
        int numWorkers = 3;
        Phaser phaser = new Phaser(1); // +1 for main thread
        var results = new java.util.concurrent.atomic.AtomicInteger[numWorkers];
        for (int i = 0; i < numWorkers; i++) {
            int id = i;
            results[i] = new java.util.concurrent.atomic.AtomicInteger(0);
            phaser.register();
            new Thread(() -> {
                for (int phase = 0; phase < 3; phase++) {
                    // Phase work
                    results[id].addAndGet(id + 1);
                    System.out.println("Worker " + id + " done with phase " + phase);
                    phaser.arriveAndAwaitAdvance(); // barrier
                }
                phaser.arriveAndDeregister();
            }).start();
        }

        // Main thread participates in barrier
        for (int phase = 0; phase < 3; phase++) {
            phaser.arriveAndAwaitAdvance();
            System.out.println("All workers completed phase " + phase);
        }

        // Deregister main
        phaser.arriveAndDeregister();

        // Verify results
        assert results[0].get() == 3 : "Worker 0 total";
        assert results[1].get() == 6 : "Worker 1 total";
        assert results[2].get() == 9 : "Worker 2 total";

        // Example 2: Dynamic party registration
        Phaser dynamic = new Phaser();
        dynamic.register(); // main
        int taskCount = 5;
        for (int i = 0; i < taskCount; i++) {
            dynamic.register();
            int id = i;
            new Thread(() -> {
                System.out.println("Dynamic task " + id + " starting");
                dynamic.arriveAndAwaitAdvance();
                System.out.println("Dynamic task " + id + " after barrier");
                dynamic.arriveAndDeregister();
            }).start();
        }
        // Main thread arrives
        dynamic.arriveAndAwaitAdvance();
        System.out.println("All dynamic tasks reached barrier");

        System.out.println("All PhaserExample tests passed.");
    }
}