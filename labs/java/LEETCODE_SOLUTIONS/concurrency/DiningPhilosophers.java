package concurrency;

import java.util.concurrent.locks.ReentrantLock;

/**
 * LeetCode 1226 - The Dining Philosophers
 * 
 * Five philosophers sit at a table. Each needs two forks to eat.
 * Goal: avoid deadlock and starvation.
 * 
 * Approach 1: Global lock (simplest) — only one eats at a time
 * Approach 2: Lock ordering — pick lower-numbered fork first
 * Approach 3: TryLock with timeout
 * 
 * Time: O(1) per eat cycle, Space: O(n) for fork locks
 */
public class DiningPhilosophers {

    // Approach: Lock ordering to prevent circular wait
    static class PhilosopherLockOrder {
        private final ReentrantLock[] forks = new ReentrantLock[5];
        
        PhilosopherLockOrder() {
            for (int i = 0; i < 5; i++) forks[i] = new ReentrantLock();
        }

        // Pick lower-numbered fork first to prevent circular wait
        public void dine(int philosopher, Runnable pickLeft, Runnable pickRight, 
                         Runnable eat, Runnable putLeft, Runnable putRight) {
            int left = philosopher;
            int right = (philosopher + 1) % 5;
            int first = Math.min(left, right);
            int second = Math.max(left, right);

            forks[first].lock();
            forks[second].lock();
            try {
                pickLeft.run();
                pickRight.run();
                eat.run();
                putLeft.run();
                putRight.run();
            } finally {
                forks[second].unlock();
                forks[first].unlock();
            }
        }
    }

    // Approach: TryLock with timeout (deadlock-free, allows concurrency)
    static class PhilosopherTryLock {
        private final ReentrantLock[] forks = new ReentrantLock[5];

        PhilosopherTryLock() {
            for (int i = 0; i < 5; i++) forks[i] = new ReentrantLock();
        }

        public void dine(int philosopher, Runnable pickLeft, Runnable pickRight,
                         Runnable eat, Runnable putLeft, Runnable putRight) throws InterruptedException {
            int left = philosopher;
            int right = (philosopher + 1) % 5;
            while (true) {
                if (forks[left].tryLock()) {
                    try {
                        if (forks[right].tryLock()) {
                            try {
                                pickLeft.run(); pickRight.run();
                                eat.run();
                                putLeft.run(); putRight.run();
                                return;
                            } finally { forks[right].unlock(); }
                        }
                    } finally { forks[left].unlock(); }
                }
                Thread.sleep(1); // Backoff before retry
            }
        }
    }

    public static void main(String[] args) throws Exception {
        PhilosopherLockOrder table = new PhilosopherLockOrder();
        Thread[] philosophers = new Thread[5];
        for (int i = 0; i < 5; i++) {
            int id = i;
            philosophers[i] = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    try {
                        table.dine(id,
                            () -> System.out.println("P" + id + " picked left"),
                            () -> System.out.println("P" + id + " picked right"),
                            () -> System.out.println("P" + id + " eating"),
                            () -> System.out.println("P" + id + " put left"),
                            () -> System.out.println("P" + id + " put right"));
                    } catch (Exception e) { }
                }
            });
            philosophers[i].start();
        }
        for (Thread t : philosophers) t.join();
        System.out.println("All philosophers finished dining — no deadlock.");
    }
}