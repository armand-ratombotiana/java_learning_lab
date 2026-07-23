package concurrencydeep;

import java.util.concurrent.locks.StampedLock;

/**
 * StampedLock deep dive demonstrating optimistic vs pessimistic locking.
 * 
 * StampedLock supports three modes:
 * 1. Writing (exclusive)
 * 2. Reading (shared, like ReentrantReadWriteLock)
 * 3. Optimistic Reading (no lock, validated later)
 * 
 * Optimistic reads are lock-free — great for read-heavy, write-rare scenarios.
 * 
 * Time: O(1) per lock operation
 * Space: O(1)
 */
public class StampedLockExample {

    static class Point {
        private double x, y;
        private final StampedLock lock = new StampedLock();

        void move(double dx, double dy) {
            long stamp = lock.writeLock();
            try {
                x += dx;
                y += dy;
            } finally {
                lock.unlockWrite(stamp);
            }
        }

        double distanceFromOrigin() {
            // Optimistic read — no lock
            long stamp = lock.tryOptimisticRead();
            double curX = x, curY = y;
            if (!lock.validate(stamp)) {
                // Optimistic read failed — fall back to read lock
                stamp = lock.readLock();
                try {
                    curX = x;
                    curY = y;
                } finally {
                    lock.unlockRead(stamp);
                }
            }
            return Math.sqrt(curX * curX + curY * curY);
        }

        // Convert read lock to write lock
        void moveIfAt(double oldX, double oldY, double newX, double newY) {
            long stamp = lock.readLock();
            try {
                while (x == oldX && y == oldY) {
                    long ws = lock.tryConvertToWriteLock(stamp);
                    if (ws != 0L) {
                        stamp = ws;
                        x = newX;
                        y = newY;
                        break;
                    } else {
                        lock.unlockRead(stamp);
                        stamp = lock.writeLock();
                    }
                }
            } finally {
                lock.unlock(stamp);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Point p = new Point();
        p.move(3, 4);
        assert p.distanceFromOrigin() == 5.0;

        p.move(-3, -4);
        assert p.distanceFromOrigin() == 0.0;

        // Test with concurrent access
        var threads = new java.util.ArrayList<Thread>();
        for (int i = 0; i < 4; i++) {
            threads.add(new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    p.move(1, 1);
                    p.distanceFromOrigin();
                }
            }));
        }
        threads.forEach(Thread::start);
        for (Thread t : threads) t.join();

        System.out.println("All StampedLockExample tests passed.");
    }
}