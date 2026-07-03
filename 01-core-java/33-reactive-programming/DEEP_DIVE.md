# Deep Dive: Reactive Programming & The Flow API

## 1. The Problem with Imperative Concurrency
Traditional Java concurrency relies on the thread-per-request model. When a thread needs data from a database or a network call, it blocks (waits) until the data arrives. 
*   **The Flaw**: Threads are expensive (consuming ~1MB of RAM each and requiring OS context switches). If you have 10,000 concurrent users and your database takes 2 seconds to respond, you need 10,000 blocked threads sitting idle, doing nothing but consuming 10GB of RAM. This model does not scale to modern, high-throughput microservices.

## 2. The Reactive Paradigm
Reactive programming is an asynchronous programming paradigm concerned with data streams and the propagation of change. 
Instead of requesting data and blocking, you **subscribe** to a stream of data. When data arrives, a callback is triggered. The thread is never blocked; it handles the event and immediately moves on to other work.

### The Four Reactive Principles (The Reactive Manifesto):
1.  **Responsive**: The system responds in a timely manner.
2.  **Resilient**: The system stays responsive in the face of failure.
3.  **Elastic**: The system stays responsive under varying workload.
4.  **Message Driven**: The system relies on asynchronous message-passing.

## 3. Backpressure: The Core of Reactive Streams
In a push-based asynchronous system, a fast Producer can easily overwhelm a slow Consumer, leading to an `OutOfMemoryError` as the consumer's buffer fills up.
**Backpressure** is a feedback mechanism. The Consumer tells the Producer: *"I can only handle 10 items right now. Send me 10, and then wait until I ask for more."* This prevents the consumer from drowning in data.

## 4. The Java 9 Flow API
Prior to Java 9, reactive programming was handled entirely by third-party libraries (RxJava, Project Reactor, Akka). Java 9 introduced the `java.util.concurrent.Flow` API to provide a standard set of interfaces that all these libraries could implement, allowing them to interoperate seamlessly.

The Flow API consists of four core interfaces:

### 1. `Publisher<T>`
Produces items of type `T` and pushes them to subscribers.
```java
public interface Publisher<T> {
    void subscribe(Subscriber<? super T> subscriber);
}
```

### 2. `Subscriber<T>`
Consumes items of type `T`. It has four lifecycle methods:
```java
public interface Subscriber<T> {
    void onSubscribe(Subscription subscription); // Called first. Gives the subscriber the Subscription object.
    void onNext(T item);                         // Called when a new item is pushed.
    void onError(Throwable throwable);           // Called if an error occurs. Terminal state.
    void onComplete();                           // Called when the stream ends successfully. Terminal state.
}
```

### 3. `Subscription`
The link between the Publisher and the Subscriber. This is where **Backpressure** is controlled.
```java
public interface Subscription {
    void request(long n); // The Subscriber requests 'n' items from the Publisher.
    void cancel();        // The Subscriber tells the Publisher to stop sending items.
}
```

### 4. `Processor<T, R>`
Acts as both a Subscriber (receives type `T`) and a Publisher (emits type `R`). Used for transforming data in the middle of the stream.
```java
public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {}
```

## 5. The Execution Lifecycle
1.  A `Subscriber` calls `publisher.subscribe(this)`.
2.  The `Publisher` creates a `Subscription` and calls `subscriber.onSubscribe(subscription)`.
3.  The `Subscriber` saves the subscription and calls `subscription.request(n)` (e.g., `request(1)`).
4.  The `Publisher` pushes up to `n` items by calling `subscriber.onNext(item)`.
5.  After processing the item, the `Subscriber` calls `subscription.request(n)` again to ask for more (Backpressure in action!).
6.  Eventually, the `Publisher` calls `onComplete()` or `onError()`.