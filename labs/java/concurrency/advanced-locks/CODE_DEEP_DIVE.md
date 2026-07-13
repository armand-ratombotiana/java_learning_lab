# Advanced Locks Code Deep Dive

This lab provides pure Java implementations comparing `ReentrantReadWriteLock` and `StampedLock` for a high-performance, read-heavy data structure.

## 💻 Pure Java Implementation

```java file="labs/java/concurrency/advanced-locks/SOLUTION/AdvancedLocksDemo.java"
package java.concurrency.advancedlocks;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

/**
 * A demonstration of ReentrantReadWriteLock vs StampedLock.
 * Simulates a high-performance 2D Point class.
 */
public class AdvancedLocksDemo {

    // --- 1. ReentrantReadWriteLock Implementation ---
    static class PointRWL {
        private double x, y;
        private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

        void move(double deltaX, double deltaY) {
            rwl.writeLock().lock(); // Blocks all readers and writers
            try {
                x += deltaX;
                y += deltaY;
            } finally {
                rwl.writeLock().unlock();
            }
        }

        double distanceFromOrigin() {
            rwl.readLock().lock(); // Blocks writers, but allows infinite readers
            try {
                return Math.sqrt(x * x + y * y);
            } finally {
                rwl.readLock().unlock();
            }
        }
    }

    // --- 2. StampedLock Implementation (Optimistic Reading) ---
    static class PointStamped {
        private double x, y;
        private final StampedLock sl = new StampedLock();

        void move(double deltaX, double deltaY) {
            long stamp = sl.writeLock(); // Blocks all readers and writers
            try {
                x += deltaX;
                y += deltaY;
            } finally {
                sl.unlockWrite(stamp);
            }
        }

        double distanceFromOrigin() {
            // 1. Try Optimistic Read (NO LOCK ACQUIRED!)
            long stamp = sl.tryOptimisticRead();
            
            // 2. Read the variables into local thread memory
            double currentX = x;
            double currentY = y;
            
            // 3. Validate the stamp. Did a writer mutate the data while we were reading?
            if (!sl.validate(stamp)) {
                // Yes! The data is corrupted. Fallback to a heavy, blocking read lock.
                stamp = sl.readLock();
                try {
                    currentX = x;
                    currentY = y;
                } finally {
                    sl.unlockRead(stamp);
                }
            }
            
            // 4. Compute using the safe local variables
            return Math.sqrt(currentX * currentX + currentY * currentY);
        }
    }

    public static void main(String[] args) {
        PointStamped point = new PointStamped();
        
        // Simulate a Writer Thread
        new Thread(() -> {
            point.move(3.0, 4.0);
            System.out.println("Writer moved point to (3,4)");
        }).start();

        // Simulate a Reader Thread
        new Thread(() -> {
            double distance = point.distanceFromOrigin();
            System.out.println("Reader calculated distance: " + distance);
        }).start();
    }
}
```

## 🔍 Key Takeaways
1. **The Read-Heavy Advantage**: If you have 10,000 threads calling `distanceFromOrigin()`, a standard `ReentrantLock` would force them into a single-file line, destroying performance. `ReentrantReadWriteLock` allows all 10,000 to execute simultaneously.
2. **The StampedLock Magic**: Look closely at `distanceFromOrigin` in `PointStamped`. It reads `x` and `y` without acquiring any lock whatsoever. If `validate(stamp)` returns true, it completely bypassed the overhead of updating the AQS state variables. In benchmarks with massive read contention, `StampedLock` can be 10x faster than `ReentrantReadWriteLock`.
3. **The `finally` Block**: Notice that every single lock acquisition (`lock()`, `writeLock()`) is immediately followed by a `try-finally` block containing the `unlock()` method. This is absolute mandatory boilerplate. If an exception is thrown inside the `try` block and you fail to `unlock()`, the lock is held forever, permanently deadlocking your entire application.