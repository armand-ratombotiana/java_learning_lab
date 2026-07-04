# Mini Project: Thread-Safe LRU Cache with Stampede Protection

## Objective
Build a production-grade cache from scratch. You will implement an LRU (Least Recently Used) eviction policy to prevent memory leaks, and use `ConcurrentHashMap` combined with `FutureTask` to prevent Cache Stampedes (Thundering Herd).

## Prerequisites
*   Java 17+

## Step 1: The LRU Core
We will use Java's built-in `LinkedHashMap`, which has a protected method specifically designed for building LRU caches.

```java
import java.util.LinkedHashMap;
import java.util.Map;

// We extend LinkedHashMap and configure it for Access-Order (true)
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LRUCache(int capacity) {
        // initialCapacity, loadFactor, accessOrder (true moves accessed items to the end)
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    // This is the magic hook. If this returns true, the oldest entry is deleted.
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
```

## Step 2: The Stampede-Proof Wrapper
If 100 threads ask for the same missing key, we only want ONE thread to execute the expensive calculation. The others must wait for the first thread to finish. We achieve this using `FutureTask`.

```java
import java.util.concurrent.*;
import java.util.function.Function;

public class StampedeProofCache<K, V> {
    // The underlying cache holds Futures, not raw values!
    private final Map<K, Future<V>> cache;

    public StampedeProofCache(int capacity) {
        // We wrap our LRU cache in a synchronized map for thread safety
        this.cache = Collections.synchronizedMap(new LRUCache<>(capacity));
    }

    public V computeIfAbsent(K key, Function<K, V> expensiveComputation) throws Exception {
        while (true) {
            Future<V> future = cache.get(key);
            
            if (future == null) {
                // The key is missing. We create a task to compute it.
                Callable<V> task = () -> expensiveComputation.apply(key);
                FutureTask<V> futureTask = new FutureTask<>(task);
                
                // putIfAbsent is atomic. Only ONE thread will successfully put its task in the map.
                future = cache.putIfAbsent(key, futureTask);
                
                if (future == null) {
                    // We won the race! We are the one thread allowed to execute the task.
                    future = futureTask;
                    futureTask.run(); // Execute the expensive computation
                }
            }
            
            try {
                // ALL threads (the winner and the losers) call get().
                // The losers will safely block here until the winner finishes the computation.
                return future.get(); 
            } catch (CancellationException e) {
                cache.remove(key, future); // Cleanup if cancelled
            } catch (ExecutionException e) {
                throw new Exception(e.getCause());
            }
        }
    }
}
```

## Step 3: Test the System
We will simulate 10 threads hitting the cache simultaneously for the exact same key.

```java
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws Exception {
        StampedeProofCache<String, String> cache = new StampedeProofCache<>(5);
        AtomicInteger databaseCalls = new AtomicInteger(0);

        // The expensive operation
        java.util.function.Function<String, String> dbQuery = key -> {
            System.out.println(">>> Executing EXPENSIVE database query for: " + key + " <<<");
            databaseCalls.incrementAndGet();
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            return "Data_For_" + key;
        };

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGun = new CountDownLatch(1);
        CountDownLatch finishLine = new CountDownLatch(threadCount);

        System.out.println("--- Starting Cache Stampede Test ---");
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startGun.await(); // Wait for all threads to be ready
                    
                    long start = System.currentTimeMillis();
                    // ALL 10 THREADS ASK FOR THE SAME KEY AT THE SAME TIME
                    String result = cache.computeIfAbsent("USER_123", dbQuery);
                    long duration = System.currentTimeMillis() - start;
                    
                    System.out.println("Thread " + threadId + " got result: " + result + " in " + duration + "ms");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    finishLine.countDown();
                }
            });
        }

        // Fire the starting gun!
        startGun.countDown();
        finishLine.await();
        executor.shutdown();

        System.out.println("\nTotal Database Calls Made: " + databaseCalls.get() + " (Should be exactly 1)");
    }
}
```

## Expected Output
Notice that even though 10 threads requested the data at the exact same millisecond, the expensive database query was executed exactly **once**. All other 9 threads waited safely for the first thread to finish, completely preventing a cache stampede.

```text
--- Starting Cache Stampede Test ---
>>> Executing EXPENSIVE database query for: USER_123 <<<
Thread 0 got result: Data_For_USER_123 in 1015ms
Thread 3 got result: Data_For_USER_123 in 1015ms
Thread 1 got result: Data_For_USER_123 in 1015ms
...
Thread 9 got result: Data_For_USER_123 in 1015ms

Total Database Calls Made: 1 (Should be exactly 1)
```