# Mini Project: Custom Thread Pool & Work Stealing Simulator

## Objective
Build a robust, production-ready `ThreadPoolExecutor` with a custom rejection policy and proper exception handling. Then, implement a recursive divide-and-conquer algorithm using `ForkJoinPool` to demonstrate performance gains.

## Prerequisites
*   Java 17+

## Part 1: The Production-Ready Thread Pool
Never use `Executors.newFixedThreadPool()` in production. We will build a pool with strict boundaries and a `CallerRunsPolicy` to provide backpressure.

```java
import java.util.concurrent.*;

public class ProductionThreadPool {

    public static ExecutorService createPool() {
        int corePoolSize = 2;
        int maxPoolSize = 4;
        long keepAliveTime = 60L;
        
        // A strictly bounded queue
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(5);

        // Custom Thread Factory to name threads (crucial for debugging)
        ThreadFactory threadFactory = new ThreadFactory() {
            private int count = 1;
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "ProdWorker-" + count++);
                t.setDaemon(false);
                return t;
            }
        };

        // Rejection Policy: If queue is full and max threads reached, 
        // the thread submitting the task will execute it itself.
        RejectedExecutionHandler rejectionHandler = new ThreadPoolExecutor.CallerRunsPolicy();

        return new ThreadPoolExecutor(
            corePoolSize, 
            maxPoolSize, 
            keepAliveTime, TimeUnit.SECONDS, 
            workQueue, 
            threadFactory, 
            rejectionHandler
        );
    }
    
    public static void testPool() {
        ExecutorService pool = createPool();
        System.out.println("--- Submitting 15 tasks to bounded pool (Size 4, Queue 5) ---");
        
        for (int i = 1; i <= 15; i++) {
            final int taskId = i;
            System.out.println("Submitting Task " + taskId);
            
            pool.execute(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " executing Task " + taskId);
                    Thread.sleep(1000); // Simulate heavy work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        pool.shutdown();
    }
}
```

## Part 2: Divide and Conquer with ForkJoinPool
Calculate the sum of a massive array. If the array is larger than a threshold, split it in half and fork new tasks.

```java
import java.util.concurrent.RecursiveTask;

public class SumTask extends RecursiveTask<Long> {
    private static final int THRESHOLD = 10_000;
    private final long[] array;
    private final int start;
    private final int end;

    public SumTask(long[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        int length = end - start;
        
        // Base case: If the chunk is small enough, compute sequentially
        if (length <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        } 
        
        // Recursive case: Split the work in half
        int mid = start + (length / 2);
        SumTask leftTask = new SumTask(array, start, mid);
        SumTask rightTask = new SumTask(array, mid, end);

        // Fork the left task (pushes it to the deque, another thread might steal it)
        leftTask.fork();
        
        // Compute the right task directly in the current thread
        long rightResult = rightTask.compute();
        
        // Wait for the left task to finish and get its result
        long leftResult = leftTask.join();

        return leftResult + rightResult;
    }
}
```

## Part 3: Test and Execute
```java
import java.util.concurrent.ForkJoinPool;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // 1. Test the Production Thread Pool (Observe CallerRunsPolicy)
        ProductionThreadPool.testPool();
        
        // Wait for pool to finish
        try { Thread.sleep(3000); } catch (InterruptedException e) {}

        // 2. Test ForkJoinPool
        System.out.println("\n--- Testing ForkJoinPool ---");
        int size = 100_000_000;
        long[] array = new long[size];
        Random rand = new Random();
        for (int i = 0; i < size; i++) array[i] = rand.nextInt(100);

        ForkJoinPool fjPool = new ForkJoinPool();
        SumTask rootTask = new SumTask(array, 0, array.length);

        long startTime = System.currentTimeMillis();
        long totalSum = fjPool.invoke(rootTask);
        long endTime = System.currentTimeMillis();

        System.out.println("Total Sum: " + totalSum);
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
    }
}
```

## Expected Output
Notice how the "main" thread is forced to execute tasks when the pool is saturated, acting as a natural backpressure mechanism.
```text
--- Submitting 15 tasks to bounded pool (Size 4, Queue 5) ---
...
ProdWorker-1 executing Task 1
ProdWorker-2 executing Task 2
ProdWorker-3 executing Task 8
ProdWorker-4 executing Task 9
Submitting Task 10
main executing Task 10  <-- CallerRunsPolicy in action! Main thread is blocked.
...

--- Testing ForkJoinPool ---
Total Sum: 4950123456
Time taken: 32 ms
```