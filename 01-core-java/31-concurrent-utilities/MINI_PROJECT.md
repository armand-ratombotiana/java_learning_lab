# Mini Project: Multi-Stage Data Processing Pipeline

## Objective
Build a parallel data processing pipeline that demonstrates the use of `CountDownLatch` (for a starting gun), `Semaphore` (for rate limiting API calls), and `CyclicBarrier` (for merging multi-stage results).

## Prerequisites
*   Java 17+

## Step 1: The Rate Limited API (Semaphore)
Create a mock API that simulates fetching data. It uses a `Semaphore` to ensure no more than 2 threads can access it simultaneously.

```java
import java.util.concurrent.Semaphore;

public class RateLimitedApi {
    // Only 2 concurrent connections allowed!
    private final Semaphore semaphore = new Semaphore(2);

    public String fetchData(int id) {
        try {
            semaphore.acquire(); // Blocks if 2 threads are already fetching
            System.out.println("Thread " + Thread.currentThread().getName() + " fetching data for ID " + id);
            Thread.sleep(1000); // Simulate network latency
            return "Data-" + id;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            semaphore.release(); // CRITICAL: Always release in finally
        }
    }
}
```

## Step 2: The Worker Thread
The worker waits for the starting gun, fetches data (via the rate-limited API), processes it, and then waits at the barrier for other threads to finish the stage.

```java
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Worker implements Runnable {
    private final int id;
    private final RateLimitedApi api;
    private final CountDownLatch startGun;
    private final CyclicBarrier stageBarrier;

    public Worker(int id, RateLimitedApi api, CountDownLatch startGun, CyclicBarrier stageBarrier) {
        this.id = id;
        this.api = api;
        this.startGun = startGun;
        this.stageBarrier = stageBarrier;
    }

    @Override
    public void run() {
        try {
            // 1. Wait for the master thread to say "GO!"
            startGun.await();

            // 2. Stage 1: Fetch Data (Subject to Semaphore limits)
            String rawData = api.fetchData(id);
            
            // 3. Wait for all workers to finish Stage 1
            System.out.println("Worker " + id + " waiting at Stage 1 barrier...");
            stageBarrier.await();

            // 4. Stage 2: Process Data
            System.out.println("Worker " + id + " processing " + rawData);
            Thread.sleep(500); // Simulate processing

            // 5. Wait for all workers to finish Stage 2
            System.out.println("Worker " + id + " waiting at Stage 2 barrier...");
            stageBarrier.await();

        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("Worker " + id + " failed: " + e.getMessage());
        }
    }
}
```

## Step 3: The Master Orchestrator
Set up the synchronizers, spawn the threads, and fire the starting gun.

```java
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int numWorkers = 4;
        RateLimitedApi api = new RateLimitedApi();
        
        // Latch: 1 count for the master thread's "starting gun"
        CountDownLatch startGun = new CountDownLatch(1);
        
        // Barrier: 4 workers must arrive. When they do, print a message.
        CyclicBarrier stageBarrier = new CyclicBarrier(numWorkers, () -> {
            System.out.println(">>> BARRIER TRIPPED: All workers completed the stage! Proceeding... <<<");
        });

        System.out.println("Initializing workers...");
        for (int i = 1; i <= numWorkers; i++) {
            new Thread(new Worker(i, api, startGun, stageBarrier), "W-" + i).start();
        }

        System.out.println("Workers initialized and waiting. Firing starting gun in 2 seconds...");
        Thread.sleep(2000);
        
        // FIRE! All workers will wake up simultaneously
        startGun.countDown();
    }
}
```

## Expected Output
Notice how the Semaphore restricts fetching to 2 threads at a time. The barrier ensures no one processes data until everyone has fetched it.

```text
Initializing workers...
Workers initialized and waiting. Firing starting gun in 2 seconds...
Thread W-1 fetching data for ID 1
Thread W-2 fetching data for ID 2
(1 second pause)
Worker 1 waiting at Stage 1 barrier...
Worker 2 waiting at Stage 1 barrier...
Thread W-3 fetching data for ID 3
Thread W-4 fetching data for ID 4
(1 second pause)
Worker 4 waiting at Stage 1 barrier...
Worker 3 waiting at Stage 1 barrier...
>>> BARRIER TRIPPED: All workers completed the stage! Proceeding... <<<
Worker 3 processing Data-3
Worker 4 processing Data-4
Worker 1 processing Data-1
Worker 2 processing Data-2
(0.5 second pause)
Worker 2 waiting at Stage 2 barrier...
Worker 1 waiting at Stage 2 barrier...
Worker 4 waiting at Stage 2 barrier...
Worker 3 waiting at Stage 2 barrier...
>>> BARRIER TRIPPED: All workers completed the stage! Proceeding... <<<
```