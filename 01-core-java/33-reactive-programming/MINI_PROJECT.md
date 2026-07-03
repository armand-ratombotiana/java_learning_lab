# Mini Project: Custom Flow API Publisher & Subscriber

## Objective
Build a complete end-to-end Reactive Stream using the raw Java 9 `java.util.concurrent.Flow` interfaces. You will implement a Publisher that generates data, a Subscriber that consumes it, and properly implement the Backpressure contract.

## Prerequisites
*   Java 9+

## Step 1: Implement the Subscriber
The Subscriber requests items one by one to demonstrate backpressure.

```java
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public class PrintSubscriber implements Subscriber<String> {
    private Subscription subscription;
    private final String name;
    private int itemsProcessed = 0;

    public PrintSubscriber(String name) {
        this.name = name;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        System.out.println("[" + name + "] Subscribed!");
        this.subscription = subscription;
        
        // Request the first item to start the flow
        System.out.println("[" + name + "] Requesting 1 item...");
        this.subscription.request(1);
    }

    @Override
    public void onNext(String item) {
        System.out.println("[" + name + "] Received: " + item);
        itemsProcessed++;
        
        // Simulate processing time
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // Request the next item (Backpressure in action!)
        System.out.println("[" + name + "] Requesting 1 more item...");
        this.subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        System.err.println("[" + name + "] ERROR: " + throwable.getMessage());
    }

    @Override
    public void onComplete() {
        System.out.println("[" + name + "] COMPLETE! Processed " + itemsProcessed + " items.");
    }
}
```

## Step 2: Implement the Publisher
The Publisher must respect the requests from the Subscriber. It cannot send data unless `request(n)` has been called.

```java
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DataPublisher implements Publisher<String> {
    private final List<String> data;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public DataPublisher(List<String> data) {
        this.data = data;
    }

    @Override
    public void subscribe(Subscriber<? super String> subscriber) {
        // Create a new Subscription for this specific subscriber
        DataSubscription subscription = new DataSubscription(subscriber, data, executor);
        
        // Notify the subscriber
        subscriber.onSubscribe(subscription);
    }

    // Inner class to handle the state of the subscription
    private static class DataSubscription implements Subscription {
        private final Subscriber<? super String> subscriber;
        private final List<String> data;
        private final ExecutorService executor;
        
        private final AtomicInteger currentIndex = new AtomicInteger(0);
        private final AtomicBoolean isCanceled = new AtomicBoolean(false);

        public DataSubscription(Subscriber<? super String> subscriber, List<String> data, ExecutorService executor) {
            this.subscriber = subscriber;
            this.data = data;
            this.executor = executor;
        }

        @Override
        public void request(long n) {
            if (isCanceled.get()) return;

            if (n <= 0) {
                executor.execute(() -> subscriber.onError(new IllegalArgumentException("Request must be > 0")));
                return;
            }

            // Asynchronously process the request to avoid blocking the subscriber's thread
            executor.execute(() -> {
                long itemsSent = 0;
                while (itemsSent < n && currentIndex.get() < data.size() && !isCanceled.get()) {
                    String item = data.get(currentIndex.getAndIncrement());
                    subscriber.onNext(item);
                    itemsSent++;
                }

                if (currentIndex.get() >= data.size() && !isCanceled.get()) {
                    isCanceled.set(true);
                    subscriber.onComplete();
                }
            });
        }

        @Override
        public void cancel() {
            isCanceled.set(true);
        }
    }
}
```

## Step 3: Test the Reactive Flow
```java
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<String> items = List.of("Apple", "Banana", "Cherry", "Date");
        
        DataPublisher publisher = new DataPublisher(items);
        PrintSubscriber subscriber = new PrintSubscriber("Sub-1");

        System.out.println("Starting flow...");
        // This triggers onSubscribe, which triggers request(1), which triggers onNext...
        publisher.subscribe(subscriber);

        // Keep main thread alive while async processing happens
        Thread.sleep(3000);
        System.out.println("Main thread exiting.");
    }
}
```

## Expected Output
Notice the "ping-pong" effect. The Publisher waits for the Subscriber to ask for data before sending the next item. This is true backpressure.

```text
Starting flow...
[Sub-1] Subscribed!
[Sub-1] Requesting 1 item...
[Sub-1] Received: Apple
[Sub-1] Requesting 1 more item...
[Sub-1] Received: Banana
[Sub-1] Requesting 1 more item...
[Sub-1] Received: Cherry
[Sub-1] Requesting 1 more item...
[Sub-1] Received: Date
[Sub-1] Requesting 1 more item...
[Sub-1] COMPLETE! Processed 4 items.
Main thread exiting.
```