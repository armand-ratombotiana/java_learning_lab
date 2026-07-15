# Fork/Join Code Deep Dive

This lab provides a pure Java implementation of a parallel sum algorithm using the Fork/Join framework.

## 💻 Pure Java Implementation

```java file="labs/java/concurrency/fork-join/SOLUTION/ParallelSum.java"
package java.concurrency.forkjoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * A demonstration of the Fork/Join framework for parallel array summation.
 */
public class ParallelSum extends RecursiveTask<Long> {

    private static final int THRESHOLD = 10_000; // Sequential threshold
    private final int[] array;
    private final int start;
    private final int end;

    public ParallelSum(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        int length = end - start;

        // Base Case: If the task is small enough, compute sequentially
        if (length <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        }

        // Recursive Step: Divide the task into two halves
        int mid = start + length / 2;
        ParallelSum leftTask = new ParallelSum(array, start, mid);
        ParallelSum rightTask = new ParallelSum(array, mid, end);

        // Fork the left task (pushes it to the current thread's deque)
        leftTask.fork();

        // Compute the right task in the current thread
        long rightResult = rightTask.compute();

        // Join the left task (blocks until result is ready, or steals work while waiting)
        long leftResult = leftTask.join();

        return leftResult + rightResult;
    }

    public static void main(String[] args) {
        int n = 1_000_000;
        int[] data = new int[n];
        for (int i = 0; i < n; i++) data[i] = 1;

        // Use the common pool (default for Parallel Streams)
        ForkJoinPool pool = ForkJoinPool.commonPool();
        
        long start = System.currentTimeMillis();
        long totalSum = pool.invoke(new ParallelSum(data, 0, n));
        long end = System.currentTimeMillis();

        System.out.println("Total Sum: " + totalSum);
        System.out.println("Parallel processing took: " + (end - start) + " ms");
    }
}
```

## 🔍 Key Takeaways
1. **The `fork()` and `join()` Pattern**: Notice we call `leftTask.fork()` but then call `rightTask.compute()` immediately after. This is more efficient than calling `fork()` on both, as it keeps the current thread busy instead of creating another task object and pushing it to a queue.
2. **Work-Stealing in Action**: When `leftTask.join()` is called, the thread does not sit idle. If the result is not yet ready, the `ForkJoinPool` will automatically make that thread go and steal tasks from other threads' deques to keep the CPU cores saturated.
3. **The Common Pool**: `ForkJoinPool.commonPool()` is a static, shared pool used by all parallel streams in Java. Tuning this pool (via system properties) affects the performance of every `parallelStream()` in your entire JVM.