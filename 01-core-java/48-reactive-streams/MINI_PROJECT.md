# Module 48: Reactive Streams & Flow API - Mini Project

**Project Name**: Custom JDK 9 Flow Data Pipeline  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Understand the underlying mechanics of Reactive Streams by building a complete data processing pipeline entirely from scratch using only the `java.util.concurrent.Flow` API, without any third-party libraries like Project Reactor or RxJava.

## 📝 Requirements

### Core Features

1. **The Publisher (`DataGenerator`)**:
   - Use the built-in `SubmissionPublisher<String>`.
   - In a background thread or a simple loop, submit 20 strings representing user names (some valid, some null or empty).

2. **The Processor (`NameFilterProcessor`)**:
   - Create a class implementing `Flow.Processor<String, String>`.
   - It must maintain its own `SubmissionPublisher` internally to pass data downstream.
   - When it receives a name in `onNext()`, it should discard nulls/blanks, convert valid names to UPPERCASE, and `submit()` them to the downstream publisher.
   - Ensure you properly manage the `Subscription` to request more items from upstream after processing.

3. **The Subscriber (`ConsoleSubscriber`)**:
   - Create a class implementing `Flow.Subscriber<String>`.
   - In `onSubscribe`, request a batch of 5 items: `subscription.request(5)`.
   - Maintain a counter. In `onNext`, print the item and increment the counter. When the counter reaches 5, request 5 more items (simulating controlled backpressure).
   - In `onComplete`, print "Pipeline finished".

4. **Execution (`Main`)**:
   - Chain them together: `generator -> processor -> subscriber`.
   - Submit the data and use `Thread.sleep()` or a `CountDownLatch` at the end of the `main` method to prevent the JVM from exiting before the async processing finishes.

---

## 💡 Solution Blueprint

**The Custom Processor**:
```java
public class NameFilterProcessor implements Flow.Processor<String, String> {
    private Flow.Subscription subscription;
    private final SubmissionPublisher<String> publisher = new SubmissionPublisher<>();

    @Override
    public void subscribe(Flow.Subscriber<? super String> subscriber) {
        publisher.subscribe(subscriber);
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1); // Request first item
    }

    @Override
    public void onNext(String item) {
        if (item != null && !item.trim().isEmpty()) {
            publisher.submit(item.toUpperCase());
        }
        subscription.request(1); // Ask for next
    }

    @Override
    public void onError(Throwable throwable) {
        publisher.closeExceptionally(throwable);
    }

    @Override
    public void onComplete() {
        publisher.close();
    }
}
```