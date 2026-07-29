# Design a Thread Pool with Work-Stealing

> **Category**: Concurrency Deep (CompletableFuture / Fork-Join)

## Problem

Design a thread pool that supports work-stealing: idle threads can steal tasks from busy threads' queues. This is similar to `ForkJoinPool` in the JDK.

## Solution

A simplified work-stealing thread pool. Each worker has a double-ended queue (deque). Workers push/pop from their own deque locally; idle workers steal from the tail of other workers' deques.

```java
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

/**
 * Work-Stealing Thread Pool
 *
 * Each worker maintains a local deque of tasks.
 * Idle workers steal tasks from the tail of other workers' deques.
 *
 * Time: O(1) for task submission, O(1) for task execution (amortized)
 * Space: O(N) where N = total queued tasks
 */
public class WorkStealingThreadPool {

    private final WorkerThread[] workers;
    private final Deque<Runnable>[] deques;
    private volatile boolean running = true;

    @SuppressWarnings("unchecked")
    public WorkStealingThreadPool(int numThreads) {
        workers = new WorkerThread[numThreads];
        deques = new Deque[numThreads];
        for (int i = 0; i < numThreads; i++) {
            deques[i] = new ArrayDeque<>();
            workers[i] = new WorkerThread(i);
            workers[i].start();
        }
    }

    public void submit(Runnable task) {
        int idx = ThreadLocalRandom.current().nextInt(workers.length);
        synchronized (deques[idx]) {
            deques[idx].addLast(task);
            deques[idx].notify();
        }
    }

    private class WorkerThread extends Thread {
        private final int id;

        WorkerThread(int id) { this.id = id; }

        @Override
        public void run() {
            while (running) {
                Runnable task = poll();
                if (task != null) {
                    task.run();
                } else {
                    // Steal from another worker
                    task = steal();
                    if (task != null) {
                        task.run();
                    } else {
                        synchronized (deques[id]) {
                            try { deques[id].wait(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                        }
                    }
                }
            }
        }

        private Runnable poll() {
            synchronized (deques[id]) {
                return deques[id].pollFirst();
            }
        }

        private Runnable steal() {
            int start = ThreadLocalRandom.current().nextInt(workers.length);
            for (int i = 0; i < workers.length; i++) {
                int victimId = (start + i) % workers.length;
                if (victimId == id) continue;
                synchronized (deques[victimId]) {
                    Runnable task = deques[victimId].pollLast();
                    if (task != null) return task;
                }
            }
            return null;
        }
    }

    public void shutdown() {
        running = false;
        for (WorkerThread w : workers) w.interrupt();
    }

    // ─────────────────────
    // Verification
    // ─────────────────────
    public static void main(String[] args) throws InterruptedException {
        int numTasks = 100;
        Counter counter = new Counter();

        WorkStealingThreadPool pool = new WorkStealingThreadPool(4);
        for (int i = 0; i < numTasks; i++) {
            pool.submit(counter::increment);
        }

        Thread.sleep(2000);  // wait for all tasks
        pool.shutdown();

        System.out.println("Expected: " + numTasks + ", Actual: " + counter.get());
        assert counter.get() == numTasks : "Counter mismatch: " + counter.get();
        System.out.println("All tests passed.");
    }

    static class Counter {
        private final AtomicInteger count = new AtomicInteger();
        void increment() { count.incrementAndGet(); }
        int get() { return count.get(); }
    }
}
```

## Complexity

| Metric          | Value                     |
|-----------------|---------------------------|
| Submit          | O(1)                      |
| Execute         | O(1) amortized            |
| Steal           | O(P) where P = threads    |
| Space           | O(N) queued tasks         |

## Key Insights

1. **Local deque**: Workers push/pop from the front of their own deque — avoids contention.
2. **Work stealing**: Idle workers steal from the tail of a victim's deque — minimizes contention with the victim's local operations.
3. **Random victim selection**: Avoids systematic bias and reduces contention hotspots.
4. **Compared to ForkJoinPool**: JDK's `ForkJoinPool` uses similar work-stealing but with optimizations: adaptive spinning, continuation stealing, and `WorkQueue` arrays with CAS operations.
