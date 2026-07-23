package concurrency;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * LeetCode 1117 - Building H2O
 * 
 * Two hydrogen atoms and one oxygen atom form a water molecule.
 * H threads call hydrogen(), O threads call oxygen(). They must be grouped as HHO/HOH/OHH.
 * 
 * Approach: Semaphore with counters
 * Time: O(1) per atom, Space: O(1)
 */
public class BuildingH2O {

    static class H2O {
        private final Semaphore oxygenSem = new Semaphore(0);
        private final Semaphore hydrogenSem = new Semaphore(2);
        private final AtomicInteger hCount = new AtomicInteger(0);

        public void hydrogen(Runnable releaseHydrogen) {
            try { hydrogenSem.acquire(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            releaseHydrogen.run();
            if (hCount.incrementAndGet() == 2) {
                oxygenSem.release();
            }
        }

        public void oxygen(Runnable releaseOxygen) {
            try { oxygenSem.acquire(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            releaseOxygen.run();
            hCount.set(0);
            hydrogenSem.release(2);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        H2O h2o = new H2O();
        StringBuilder sb = new StringBuilder();
        java.util.List<Thread> threads = new java.util.ArrayList<>();

        // Create 2 oxygen and 4 hydrogen threads
        for (int i = 0; i < 2; i++) {
            threads.add(new Thread(() -> h2o.oxygen(() -> sb.append("O"))));
        }
        for (int i = 0; i < 4; i++) {
            threads.add(new Thread(() -> h2o.hydrogen(() -> sb.append("H"))));
        }
        java.util.Collections.shuffle(threads);
        threads.forEach(Thread::start);
        for (Thread t : threads) t.join();

        System.out.println("Output: " + sb.toString());
        // Verify 2 H per O
        String s = sb.toString();
        int h = (int) s.chars().filter(c -> c == 'H').count();
        int o = (int) s.chars().filter(c -> c == 'O').count();
        assert h == 4 && o == 2 : "Expected 4H, 2O but got " + h + "H, " + o + "O";
        System.out.println("All tests passed.");
    }
}