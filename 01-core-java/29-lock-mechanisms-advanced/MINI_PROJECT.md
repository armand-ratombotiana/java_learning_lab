# Mini Project: High-Performance Bounded Buffer

## Objective
Build a thread-safe Bounded Buffer (a core component of producer-consumer patterns) using explicit `ReentrantLock` and multiple `Condition` variables. Then, implement a high-performance coordinate tracker using `StampedLock` optimistic reading.

## Prerequisites
*   Java 17+

## Part 1: The Bounded Buffer (ReentrantLock & Conditions)
We will build a buffer that blocks producers if full, and blocks consumers if empty.

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBuffer<T> {
    private final Object[] items;
    private int putIndex = 0;
    private int takeIndex = 0;
    private int count = 0;

    // The explicit lock
    private final Lock lock = new ReentrantLock();
    
    // Two conditions associated with the same lock
    private final Condition notFull  = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public BoundedBuffer(int capacity) {
        items = new Object[capacity];
    }

    public void put(T x) throws InterruptedException {
        lock.lock();
        try {
            // ALWAYS wait in a while loop to prevent spurious wakeups
            while (count == items.length) {
                System.out.println(Thread.currentThread().getName() + " waiting (Buffer FULL)");
                notFull.await(); // Releases lock, waits for signal
            }
            
            items[putIndex] = x;
            if (++putIndex == items.length) putIndex = 0;
            count++;
            
            System.out.println(Thread.currentThread().getName() + " produced: " + x);
            
            // Signal a waiting consumer that the buffer is no longer empty
            notEmpty.signal();
        } finally {
            lock.unlock(); // CRITICAL: Always in finally block
        }
    }

    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                System.out.println(Thread.currentThread().getName() + " waiting (Buffer EMPTY)");
                notEmpty.await();
            }
            
            @SuppressWarnings("unchecked")
            T x = (T) items[takeIndex];
            items[takeIndex] = null; // Let GC do its work
            if (++takeIndex == items.length) takeIndex = 0;
            count--;
            
            System.out.println(Thread.currentThread().getName() + " consumed: " + x);
            
            // Signal a waiting producer that the buffer is no longer full
            notFull.signal();
            return x;
        } finally {
            lock.unlock();
        }
    }
}
```

## Part 2: The Coordinate Tracker (StampedLock)
Demonstrate optimistic reading for a highly concurrent data structure.

```java
import java.util.concurrent.locks.StampedLock;

public class Point {
    private double x, y;
    private final StampedLock sl = new StampedLock();

    // Exclusive Write
    public void move(double deltaX, double deltaY) {
        long stamp = sl.writeLock();
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            sl.unlockWrite(stamp);
        }
    }

    // Optimistic Read
    public double distanceFromOrigin() {
        // 1. Get optimistic stamp (no locking overhead!)
        long stamp = sl.tryOptimisticRead();
        
        // 2. Read data into local variables
        double currentX = x;
        double currentY = y;
        
        // 3. Validate stamp. If a writer acquired the lock since step 1, this returns false.
        if (!sl.validate(stamp)) {
            // 4. Fallback to pessimistic read lock
            System.out.println("Optimistic read failed, falling back to pessimistic lock.");
            stamp = sl.readLock();
            try {
                currentX = x;
                currentY = y;
            } finally {
                sl.unlockRead(stamp);
            }
        }
        
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }
}
```

## Part 3: Test the Implementations
```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- Testing Bounded Buffer ---");
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(2);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 4; i++) {
                    buffer.put(i);
                    Thread.sleep(100); // Simulate work
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 4; i++) {
                    buffer.take();
                    Thread.sleep(300); // Slower consumer, will force producer to wait
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "Consumer");

        producer.start();
        consumer.start();
        
        producer.join();
        consumer.join();

        System.out.println("\n--- Testing StampedLock ---");
        Point p = new Point();
        p.move(3.0, 4.0);
        System.out.println("Distance: " + p.distanceFromOrigin()); // Expected 5.0
    }
}
```