# Lab 02 — Thread Deadlock Analysis: Code Examples

## Reproducing the Bug

```java
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class DeadlockDemo {

    private final Object lockA = new Object();
    private final Object lockB = new Object();

    // Code path that acquires A then B
    public void methodAtoB() {
        synchronized (lockA) {
            System.out.println(Thread.currentThread().getName() + " acquired lockA");
            sleep(100); // Simulate work
            synchronized (lockB) {
                System.out.println(Thread.currentThread().getName() + " acquired lockB");
            }
        }
    }

    // Code path that acquires B then A — INCONSISTENT ORDERING!
    public void methodBtoA() {
        synchronized (lockB) {
            System.out.println(Thread.currentThread().getName() + " acquired lockB");
            sleep(100); // Simulate work
            synchronized (lockA) {
                System.out.println(Thread.currentThread().getName() + " acquired lockA");
            }
        }
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DeadlockDemo demo = new DeadlockDemo();
        ExecutorService executor = Executors.newFixedThreadPool(2);

        System.out.println("Starting deadlock demo...");
        System.out.println("Thread 1: lockA -> lockB");
        System.out.println("Thread 2: lockB -> lockA");
        System.out.println("Deadlock imminent!\n");

        // Submit both methods simultaneously
        Future<?> f1 = executor.submit(demo::methodAtoB);
        Future<?> f2 = executor.submit(demo::methodBtoA);

        // Wait for completion with timeout
        try {
            f1.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            System.out.println("\nDEADLOCK DETECTED: Thread 1 did not complete in 5 seconds!");
        }
        try {
            f2.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            System.out.println("DEADLOCK DETECTED: Thread 2 did not complete in 5 seconds!");
        }

        executor.shutdownNow();
        System.out.println("\nCheck with: jstack -l <pid> | grep -A 30 \"deadlock\"");
    }
}
```

## Fixing the Bug

### With ReentrantLock.tryLock()

```java
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class DeadlockFreeDemo {

    private final ReentrantLock lockA = new ReentrantLock();
    private final ReentrantLock lockB = new ReentrantLock();

    public void methodAtoB() {
        try {
            if (lockA.tryLock(100, TimeUnit.MILLISECONDS)) {
                try {
                    System.out.println(Thread.currentThread().getName() + " acquired lockA via tryLock");
                    Thread.sleep(50);
                    if (lockB.tryLock(100, TimeUnit.MILLISECONDS)) {
                        try {
                            System.out.println(Thread.currentThread().getName() + " acquired lockB via tryLock");
                        } finally {
                            lockB.unlock();
                        }
                    } else {
                        System.out.println("Could not acquire lockB, releasing lockA and retrying...");
                        // Release and retry — prevents deadlock!
                    }
                } finally {
                    lockA.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void methodBtoA() {
        try {
            if (lockB.tryLock(100, TimeUnit.MILLISECONDS)) {
                try {
                    System.out.println(Thread.currentThread().getName() + " acquired lockB via tryLock");
                    Thread.sleep(50);
                    if (lockA.tryLock(100, TimeUnit.MILLISECONDS)) {
                        try {
                            System.out.println(Thread.currentThread().getName() + " acquired lockA via tryLock");
                        } finally {
                            lockA.unlock();
                        }
                    } else {
                        System.out.println("Could not acquire lockA, releasing lockB and retrying...");
                    }
                } finally {
                    lockB.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DeadlockFreeDemo demo = new DeadlockFreeDemo();
        ExecutorService executor = Executors.newFixedThreadPool(2);

        System.out.println("Starting DEADLOCK-FREE demo using tryLock...\n");

        for (int i = 0; i < 5; i++) {
            System.out.println("--- Iteration " + (i+1) + " ---");
            Future<?> f1 = executor.submit(demo::methodAtoB);
            Future<?> f2 = executor.submit(demo::methodBtoA);
            Thread.sleep(1000);
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("\nNo deadlocks! tryLock allowed threads to back off and retry.");
    }
}
```

### With Consistent Lock Ordering

```java
public class ConsistentOrderingDemo {
    private final Object lockGroup1 = new Object(); // Always acquired first
    private final Object lockGroup2 = new Object(); // Always acquired second

    // Both code paths acquire locks in the SAME order: Group1 -> Group2
    public void methodOne() {
        synchronized (lockGroup1) {
            synchronized (lockGroup2) {
                System.out.println(Thread.currentThread().getName() + " — methodOne done");
            }
        }
    }

    public void methodTwo() {
        synchronized (lockGroup1) {  // Same order!
            synchronized (lockGroup2) { // Same order!
                System.out.println(Thread.currentThread().getName() + " — methodTwo done");
            }
        }
    }
}
```

### Deadlock Detection Service

```java
import java.lang.management.*;
import java.util.concurrent.*;

public class DeadlockDetector implements Runnable {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void start() {
        scheduler.scheduleAtFixedRate(this, 0, 10, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdown();
    }

    @Override
    public void run() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long[] deadlockedIds = threadBean.findDeadlockedThreads();

        if (deadlockedIds != null && deadlockedIds.length > 0) {
            System.err.println("DEADLOCK DETECTED! Thread IDs: " + java.util.Arrays.toString(deadlockedIds));
            for (long id : deadlockedIds) {
                ThreadInfo info = threadBean.getThreadInfo(id, Integer.MAX_VALUE);
                System.err.println("Thread " + info.getThreadName() + " (" + id + "):");
                System.err.println("  State: " + info.getThreadState());
                System.err.println("  Lock: " + info.getLockName() + " held by " + info.getLockOwnerName());
                for (StackTraceElement ste : info.getStackTrace()) {
                    System.err.println("    at " + ste);
                }
            }
            // Attempt recovery: interrupt the first deadlocked thread
            for (long id : deadlockedIds) {
                Thread.getAllStackTraces().keySet().stream()
                    .filter(t -> t.getId() == id)
                    .findFirst()
                    .ifPresent(t -> {
                        System.err.println("Interrupting thread " + t.getName() + " to break deadlock");
                        t.interrupt();
                    });
            }
        }
    }
}
```

### Unit Test for Deadlock Detection

```java
import org.junit.jupiter.api.Test;
import java.lang.management.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class DeadlockDetectionTest {

    @Test
    @Timeout(10)
    void testDeadlockIsDetected() throws Exception {
        Object lock1 = new Object();
        Object lock2 = new Object();
        ExecutorService exec = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);

        exec.submit(() -> {
            synchronized (lock1) {
                latch.countDown();
                try { Thread.sleep(500); } catch (InterruptedException e) {}
                synchronized (lock2) { Thread.sleep(100); }
            }
            return null;
        });

        exec.submit(() -> {
            try { latch.await(); } catch (InterruptedException e) {}
            synchronized (lock2) {
                try { Thread.sleep(500); } catch (InterruptedException e) {}
                synchronized (lock1) { Thread.sleep(100); }
            }
            return null;
        });

        Thread.sleep(2000);
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long[] deadlocked = bean.findDeadlockedThreads();

        assertNotNull(deadlocked, "Deadlock should be detected!");
        exec.shutdownNow();
    }
}
```
