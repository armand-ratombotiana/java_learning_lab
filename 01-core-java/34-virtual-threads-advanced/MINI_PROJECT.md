# Mini Project: Virtual Thread Weather Aggregator

## Objective
Build a high-throughput data aggregator using Java 21 features. You will use `Executors.newVirtualThreadPerTaskExecutor()` to simulate thousands of concurrent network calls, and `StructuredTaskScope` to aggregate data from multiple services with a fail-fast mechanism.

## Prerequisites
*   Java 21+ (Enable preview features if necessary for your JDK version)

## Step 1: The Mock Slow Service
Create a service that simulates a slow, blocking network call. Because we will use Virtual Threads, this `Thread.sleep` will NOT block an OS thread; it will simply unmount the Virtual Thread.

```java
import java.util.Random;

public class WeatherService {
    private final Random random = new Random();

    public String fetchWeather(String city) throws InterruptedException {
        // Simulate network latency (500ms to 1.5s)
        int latency = 500 + random.nextInt(1000);
        Thread.sleep(latency); 
        
        // Simulate occasional failure (10% chance)
        if (random.nextInt(10) == 0) {
            throw new RuntimeException("API Gateway Timeout for " + city);
        }
        
        int temp = 10 + random.nextInt(25);
        return city + ": " + temp + "C";
    }
}
```

## Step 2: High Throughput with Virtual Threads
Demonstrate how easily Virtual Threads handle 10,000 concurrent blocking tasks. Doing this with standard threads would likely crash the JVM or OS.

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class HighThroughputDemo {
    
    public static void run(WeatherService service) {
        System.out.println("--- Starting 10,000 Virtual Threads ---");
        long start = System.currentTimeMillis();
        AtomicInteger successCount = new AtomicInteger(0);

        // DO NOT POOL. Create a new virtual thread per task.
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10000; i++) {
                final int id = i;
                executor.submit(() -> {
                    try {
                        service.fetchWeather("City-" + id);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        // Ignore for this demo
                    }
                });
            }
        } // The try-with-resources block implicitly waits for all tasks to finish (executor.close())

        long end = System.currentTimeMillis();
        System.out.println("Finished in " + (end - start) + " ms");
        System.out.println("Successful calls: " + successCount.get());
    }
}
```

## Step 3: Structured Concurrency
Use `StructuredTaskScope` to fetch data from 3 different APIs for a single user request. If *any* API fails, cancel the others immediately to save resources.

```java
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ExecutionException;

public class StructuredAggregator {
    
    private final WeatherService service = new WeatherService();

    public void fetchAllOrNothing() {
        System.out.println("\n--- Starting Structured Concurrency (Fail-Fast) ---");
        
        // ShutdownOnFailure will cancel all other tasks if one throws an exception
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            
            System.out.println("Forking tasks...");
            StructuredTaskScope.Subtask<String> tokyo = scope.fork(() -> service.fetchWeather("Tokyo"));
            StructuredTaskScope.Subtask<String> london = scope.fork(() -> service.fetchWeather("London"));
            StructuredTaskScope.Subtask<String> newYork = scope.fork(() -> service.fetchWeather("New York"));

            System.out.println("Waiting for tasks to complete...");
            scope.join();           // Block until all succeed OR one fails
            scope.throwIfFailed();  // Propagate the exception if a failure occurred

            // If we get here, all tasks succeeded. It is safe to call .get()
            System.out.println("All succeeded!");
            System.out.println(tokyo.get());
            System.out.println(london.get());
            System.out.println(newYork.get());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.err.println("Aggregation Failed: " + e.getCause().getMessage());
        }
    }
}
```

## Step 4: Execute
```java
public class Main {
    public static void main(String[] args) {
        WeatherService service = new WeatherService();
        
        // 1. Run 10,000 concurrent blocking tasks
        HighThroughputDemo.run(service);
        
        // 2. Run Structured Concurrency (Run a few times to see both success and fail-fast behavior)
        StructuredAggregator aggregator = new StructuredAggregator();
        aggregator.fetchAllOrNothing();
        aggregator.fetchAllOrNothing();
    }
}
```

## Expected Output
Notice that 10,000 tasks that each take ~1 second to run finish in roughly 1.5 seconds total, proving true massive concurrency.
```text
--- Starting 10,000 Virtual Threads ---
Finished in 1650 ms
Successful calls: 9012

--- Starting Structured Concurrency (Fail-Fast) ---
Forking tasks...
Waiting for tasks to complete...
All succeeded!
Tokyo: 22C
London: 15C
New York: 18C

--- Starting Structured Concurrency (Fail-Fast) ---
Forking tasks...
Waiting for tasks to complete...
Aggregation Failed: API Gateway Timeout for London
```