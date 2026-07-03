# Module 48: Reactive Streams & Flow API - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-47 (especially Modules 30 Reactive Programming & 05 Concurrency)  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Introduction to Java Flow API](#intro)
2. [The Four Core Interfaces](#interfaces)
3. [Backpressure Explained](#backpressure)
4. [SubmissionPublisher](#submission-publisher)
5. [Integrating with Project Reactor / RxJava](#integration)

---

## 1. Introduction to Java Flow API <a name="intro"></a>
Java 9 introduced the `java.util.concurrent.Flow` API, which standardizes the Reactive Streams specification within the JDK. It provides a set of interfaces to establish a standard for asynchronous stream processing with non-blocking backpressure.

---

## 2. The Four Core Interfaces <a name="interfaces"></a>
The `Flow` class contains exactly four nested interfaces:
- **`Flow.Publisher<T>`**: Emits a sequence of items to subscribers.
- **`Flow.Subscriber<T>`**: Receives items from the Publisher. Must implement `onSubscribe`, `onNext`, `onError`, and `onComplete`.
- **`Flow.Subscription`**: Represents the 1-to-1 link between a Publisher and a Subscriber. It has two methods: `request(long n)` and `cancel()`.
- **`Flow.Processor<T, R>`**: Acts as both a Subscriber and a Publisher. Used for transforming elements in the pipeline.

---

## 3. Backpressure Explained <a name="backpressure"></a>
Backpressure is the mechanism that allows a slow Subscriber to tell a fast Publisher to slow down. Instead of the Publisher "pushing" data blindly (potentially causing OutOfMemoryErrors), the Subscriber "pulls" data by calling `Subscription.request(n)`. The Publisher is mathematically bound to emit no more than `n` items until more are requested.

---

## 4. SubmissionPublisher <a name="submission-publisher"></a>
The JDK provides `SubmissionPublisher<T>` as a standard implementation of `Flow.Publisher<T>`. It allows you to quickly create an asynchronous publisher backed by a `ForkJoinPool`.

```java
try (SubmissionPublisher<String> publisher = new SubmissionPublisher<>()) {
    
    // Register a subscriber
    publisher.subscribe(new MyCustomSubscriber());
    
    // Publish items
    publisher.submit("Item 1");
    publisher.submit("Item 2");
} // Closes the publisher and sends onComplete()
```

---

## 5. Integrating with Project Reactor / RxJava <a name="integration"></a>
Because the Java `Flow` API perfectly mirrors the `org.reactivestreams` specification, you can seamlessly convert JDK 9 Flow publishers into Reactor `Flux` or RxJava `Flowable` using adapter libraries (like `reactor-core`'s `JdkFlowAdapter`).