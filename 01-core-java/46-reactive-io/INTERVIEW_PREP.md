# Interview Preparation: Reactive I/O

This document covers advanced questions related to Asynchronous I/O, Event Loops, backpressure in network streams, and memory management.

## Q1: What is the difference between Non-Blocking I/O (NIO) and Asynchronous I/O (AIO)?
**Answer:**
*   **Non-Blocking I/O (NIO)**: You ask the OS to read data. If no data is ready, the OS returns 0 immediately. Your thread must use a `Selector` to actively poll the OS to find out when the data is finally ready, and then your thread performs the actual read operation.
*   **Asynchronous I/O (AIO)**: You ask the OS to read data and provide a callback (a `CompletionHandler`). Your thread returns immediately. The OS performs the read operation entirely in the background. When the read is completely finished and the data is in the buffer, the OS invokes your callback on a background thread. You don't poll; you are simply notified when the job is done.

## Q2: Why is it a fatal error to execute a blocking database query inside a Reactive web framework like Spring WebFlux?
**Answer:**
Reactive frameworks use an Event Loop architecture, which typically relies on a very small number of threads (often equal to the number of CPU cores). These few threads are responsible for handling all incoming requests, routing events, and executing non-blocking callbacks.
If you execute a blocking query (like standard JDBC), you freeze one of these precious event-loop threads. If you have 4 cores, and 4 concurrent requests all execute a blocking query, the entire web server is frozen. It cannot accept new connections or process other events until the database responds. This completely destroys the scalability of the reactive model.

## Q3: How do you safely execute blocking legacy code inside a Reactive pipeline?
**Answer:**
You must offload the blocking execution to a dedicated thread pool that is separate from the reactive event loop.
In Project Reactor (Spring WebFlux), you use the `subscribeOn` or `publishOn` operators with a specific scheduler, typically `Schedulers.boundedElastic()`. This scheduler maintains a pool of threads specifically designed to handle blocking tasks, preventing the main event loop from freezing.

## Q4: How does the Java 11 `HttpClient` prevent `OutOfMemoryError`s when downloading a massive file?
**Answer:**
The Java 11 `HttpClient` natively implements the Java 9 Flow API (Reactive Streams).
When you use a streaming body handler (like `BodyHandlers.ofInputStream()` or a custom `BodySubscriber`), the HTTP client acts as a `Publisher` pushing chunks of data (`ByteBuffer`s).
Crucially, it respects **Backpressure**. The client will only read data from the underlying OS network socket if the `Subscriber` has explicitly requested it via `subscription.request(n)`. If the subscriber pauses requesting data, the HTTP client pauses reading. This causes the OS TCP buffer to fill up, which triggers TCP flow control (Windowing) to tell the sending server to slow down. This end-to-end backpressure prevents the data from buffering in the JVM heap and causing an OOM.

## Q5: When writing custom callbacks for `AsynchronousFileChannel`, why must you be careful about which `ExecutorService` you use?
**Answer:**
If you don't explicitly provide an `ExecutorService` when opening an `AsynchronousFileChannel`, it uses a default, system-wide thread pool.
If your `CompletionHandler` callbacks perform heavy CPU processing or accidentally block, you will exhaust this default system pool. This will cause all other asynchronous file I/O operations across your entire application to be delayed or rejected.
You should always instantiate the channel with a dedicated, custom thread pool to isolate your file I/O callbacks from the rest of the system.