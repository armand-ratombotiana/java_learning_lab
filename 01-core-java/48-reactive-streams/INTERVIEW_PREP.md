# Module 48: Reactive Streams & Flow API - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: Why did Java 9 introduce the `java.util.concurrent.Flow` API instead of providing a full reactive framework like Project Reactor?
**Answer**:
The goal of the JDK was not to build a massive, fully-featured reactive library with hundreds of operators (like `map`, `flatMap`, `retry`, etc.). Instead, the goal was **interoperability**.
Before Java 9, libraries like RxJava, Project Reactor, and Akka Streams all implemented reactive paradigms but used different core interfaces. By standardizing the four core interfaces (`Publisher`, `Subscriber`, `Subscription`, `Processor`) into the JDK itself, the JDK provided a universal common ground. Now, a database driver returning a JDK `Publisher` can seamlessly stream data into an RxJava `Flowable` or a Reactor `Flux` without complex bridging logic.

### Q2: Explain the Push-Pull model (Backpressure) in Reactive Streams.
**Answer**:
- **Push**: The Publisher pushes data to the Subscriber as fast as it can. This is dangerous if the Subscriber is slow (e.g., writing to a slow disk), causing memory buffers to overflow.
- **Pull**: The Subscriber pulls data from the Publisher synchronously (like `Iterator.next()`). This blocks threads while waiting for data.
- **Push-Pull (Reactive)**: The Subscriber asynchronously calls `request(n)` (the pull). The Publisher is then allowed to asynchronously push exactly `n` items to the Subscriber. If the Subscriber finishes processing and wants more, it requests more. This dynamic flow control prevents resource exhaustion without blocking threads.

### Q3: What is the `SubmissionPublisher` class?
**Answer**:
It is the standard implementation of `Flow.Publisher` provided in the JDK. It uses an `Executor` (by default, the common `ForkJoinPool`) to asynchronously deliver submitted items to registered subscribers. It handles the complex logic of buffering items and managing backpressure per subscriber, allowing developers to easily create custom asynchronous data producers.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Implementing a Safe Subscriber
**Problem**: An interviewer asks you to write a custom `Flow.Subscriber` that prints items. They want to see how you handle the initial request and ongoing requests safely.

**Solution**:
The key is to save the `Subscription` object in `onSubscribe` and use it to ask for the first item. Then, in `onNext`, ask for the subsequent items.

```java
public class PrintSubscriber implements Flow.Subscriber<String> {
    private Flow.Subscription subscription;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        System.out.println("Subscribed!");
        // Request the first item to start the flow
        subscription.request(1);
    }

    @Override
    public void onNext(String item) {
        System.out.println("Received: " + item);
        // Request the next item after processing this one
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        System.err.println("Error: " + throwable.getMessage());
    }

    @Override
    public void onComplete() {
        System.out.println("Stream completed.");
    }
}
```