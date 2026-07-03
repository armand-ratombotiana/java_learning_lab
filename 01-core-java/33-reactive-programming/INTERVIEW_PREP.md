# Interview Preparation: Reactive Programming & Flow API

This document covers advanced questions related to reactive streams, backpressure, and the differences between reactive and imperative concurrency.

## Q1: Explain the concept of "Backpressure" in Reactive Streams. Why is it necessary?
**Answer:**
In a push-based asynchronous system, a fast Producer can emit data much faster than a slow Consumer can process it. If this happens, the data buffers in memory until the application crashes with an `OutOfMemoryError`.
**Backpressure** is a feedback mechanism that solves this. It allows the Consumer to dictate the pace of the data flow. The Consumer explicitly tells the Producer how many items it is ready to receive (via `Subscription.request(n)`). The Producer is bound by contract to never send more than `n` items until the Consumer asks for more.

## Q2: Why must you NEVER block the thread inside a reactive `onNext()` method?
**Answer:**
Reactive frameworks (like Reactor, RxJava, or Netty) operate on an Event Loop architecture. They use a very small pool of threads (typically one per CPU core) to handle thousands of concurrent connections asynchronously.
If you execute a blocking operation (like `Thread.sleep()`, a synchronous database query, or a blocking HTTP call) inside `onNext()`, you freeze one of those precious event-loop threads. If you freeze all of them, the entire application stops responding to all users. Blocking I/O must be offloaded to a separate, dedicated thread pool.

## Q3: What is the difference between Java Streams (`java.util.stream`) and Reactive Streams (`java.util.concurrent.Flow`)?
**Answer:**
*   **Java Streams**: Designed for synchronous, pull-based processing of finite datasets (like Collections). The thread pulling the data blocks until the processing is complete.
*   **Reactive Streams**: Designed for asynchronous, push-based processing of potentially infinite datasets (like live sensor data or WebSocket streams). They are non-blocking and rely heavily on the Backpressure mechanism to coordinate speed between producers and consumers.

## Q4: What are the four core interfaces of the Java 9 Flow API, and how do they interact?
**Answer:**
1.  `Publisher<T>`: Emits data.
2.  `Subscriber<T>`: Receives data.
3.  `Subscription`: The link between Publisher and Subscriber; controls backpressure (`request()`) and cancellation (`cancel()`).
4.  `Processor<T, R>`: Acts as both a Subscriber (receives `T`) and Publisher (emits `R`), used for transforming data in the middle of a pipeline.
**Interaction**: The Subscriber calls `publisher.subscribe(this)`. The Publisher calls `subscriber.onSubscribe(subscription)`. The Subscriber calls `subscription.request(n)`. The Publisher calls `subscriber.onNext(item)` up to `n` times. Finally, the Publisher calls `onComplete()` or `onError()`.

## Q5: Why did Java introduce the `Flow` API if libraries like RxJava and Project Reactor already existed?
**Answer:**
Before Java 9, various libraries implemented reactive streams, but their APIs were incompatible. You couldn't easily pass an RxJava `Observable` to an Akka `Stream`.
The creators of these libraries collaborated to create the Reactive Streams Specification. Java 9 incorporated this specification into the JDK as the `java.util.concurrent.Flow` interfaces. 
The JDK does not provide heavy implementations of these interfaces; it merely provides the standard contracts. Now, RxJava, Reactor, and standard JDK classes (like `HttpClient`) all implement the `Flow` interfaces, allowing them to interoperate seamlessly.