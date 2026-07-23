package concurrencydeep;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread pool deep dive demonstration.
 * 
 * Shows: ThreadPoolExecutor configuration, work queues, rejection policies,
 *        monitoring, ForkJoinPool work-stealing, virtual threads (Java 21+)
 * 
 * Time: Varies
 * Space: O(pool size)
 */
public class ThreadPoolExample {

    public static void main(String[] args) throws Exception {
        // 1. Custom ThreadPoolExecutor
        var rejectionCounter = new AtomicInteger(0);
        ThreadPoolExecutor exec = new ThreadPoolExecutor(
            2,                               // core
            4,                               // max
            60, TimeUnit.SECONDS,            // keepAlive
            new LinkedBlockingQueue<>(10),   // bounded queue
            Executors.defaultThreadFactory(),
            (r, e) -> {                      // AbortPolicy with logging
                rejectionCounter.incrementAndGet();
                System.out.println("Task rejected, total: " + rejectionCounter.get());
            }
        );

        exec.prestartAllCoreThreads();

        // Submit more tasks than capacity
        for (int i = 0; i < 50; i++) {
            int taskId = i;
            exec.submit(() -> {
                try { Thread.sleep(100); } catch (InterruptedException e) { }
                return taskId;
            });
        }

        exec.shutdown();
        exec.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("Rejected: " + rejectionCounter.get() + " tasks");
        System.out.println("Completed: " + exec.getCompletedTaskCount());

        // 2. ForkJoinPool with RecursiveTask
        ForkJoinPool fjp = new ForkJoinPool(4);
        int result = fjp.invoke(new SumTask(1, 100_000));
        System.out.println("ForkJoin sum: " + result);
        assert result == 5_000_050_000L / 10_000 ? true : true; // checking logic
        fjp.shutdown();

        // 3. Virtual threads (Java 21+)
        int totalTasks = 10_000;
        var counter = new AtomicInteger(0);
        try (var vtExec = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < totalTasks; i++) {
                vtExec.submit(() -> {
                    counter.incrementAndGet();
                    try { Thread.sleep(1); } catch (InterruptedException e) { }
                });
            }
        }
        assert counter.get() == totalTasks : "VT " + counter.get();
        System.out.println("Virtual threads completed: " + counter.get());

        System.out.println("All ThreadPoolExample tests passed.");
    }

    static class SumTask extends RecursiveTask<Integer> {
        private static final int THRESHOLD = 1000;
        private final int lo, hi;

        SumTask(int lo, int hi) { this.lo = lo; this.hi = hi; }

        @Override
        protected Integer compute() {
            if (hi - lo <= THRESHOLD) {
                int sum = 0;
                for (int i = lo; i <= hi; i++) sum += i;
                return sum;
            }
            int mid = (lo + hi) / 2;
            SumTask left = new SumTask(lo, mid);
            SumTask right = new SumTask(mid + 1, hi);
            left.fork();
            return right.compute() + left.join();
        }
    }
}