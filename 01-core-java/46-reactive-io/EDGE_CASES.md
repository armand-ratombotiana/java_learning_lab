# Edge Cases & Pitfalls: Reactive I/O

Combining asynchronous programming, non-blocking I/O, and reactive streams creates a highly efficient but extremely fragile ecosystem. A single mistake can bring down the entire application.

## 1. The "Blocking the Event Loop" Catastrophe
*   **The Scenario**: You are using a reactive framework (like Spring WebFlux/Netty). Inside your reactive pipeline (e.g., inside a `map()` or `flatMap()` operator), you make a synchronous JDBC database call or use `java.io.FileInputStream`.
*   **The Pitfall**: Reactive I/O relies on a tiny number of Event Loop threads (usually 1 per CPU core). If you perform a blocking I/O operation on one of these threads, you freeze it. If you have 4 cores and 4 concurrent requests block, the entire server stops accepting new connections. The application appears completely dead, even though CPU and Memory usage are near zero.
*   **Mitigation**: **NEVER block an event loop thread.** You must use fully reactive drivers (like R2DBC for databases, or the Java 11 `HttpClient`). If you absolutely *must* use a blocking legacy API, you must offload it to a dedicated, bounded thread pool using operators like `subscribeOn(Schedulers.boundedElastic())`.

## 2. The `AsynchronousFileChannel` Thread Pool Exhaustion
*   **The Scenario**: You open an `AsynchronousFileChannel` and start reading hundreds of files concurrently, passing `CompletionHandler`s to process the data.
*   **The Pitfall**: By default, `AsynchronousFileChannel` uses a default system thread pool to execute the `CompletionHandler` callbacks. If your callback does heavy processing, or worse, blocks, you will exhaust this default thread pool. Subsequent asynchronous file operations will be delayed or rejected.
*   **Mitigation**: Always provide a custom `ExecutorService` when opening the channel using the `open(Path file, Set<? extends OpenOption> options, ExecutorService executor, FileAttribute<?>... attrs)` method. This isolates your file I/O callbacks from the rest of the JVM.

## 3. Byte Buffer Memory Leaks in Reactive Streams
*   **The Scenario**: You are writing a custom `Subscriber` to process a stream of `ByteBuffer`s coming from a reactive network connection (like the Java 11 `HttpClient`).
*   **The Pitfall**: In high-performance frameworks like Netty, `ByteBuffer`s are often pooled and allocated off-heap (Direct Buffers) to maximize I/O speed. If you receive a buffer, read the data, but forget to explicitly release/free the buffer back to the pool, you will cause a massive native memory leak that the Garbage Collector cannot fix.
*   **Mitigation**: You must understand the buffer lifecycle of the specific framework you are using. In Netty/Reactor, you must use `ReferenceCountUtil.release(buffer)` in a `finally` block to ensure off-heap memory is returned to the pool.

## 4. Backpressure Failure (The `request(Long.MAX_VALUE)` Trap)
*   **The Scenario**: You connect a reactive HTTP client to a massive firehose API (like a Twitter firehose). In your subscriber, you call `subscription.request(Long.MAX_VALUE)` because you "want all the data."
*   **The Pitfall**: You have disabled backpressure. The underlying NIO socket will read data from the OS buffer as fast as physically possible and push it into your application. If your application processes the data slower than it arrives, the data will buffer in the JVM heap until it crashes with an `OutOfMemoryError`.
*   **Mitigation**: Always request data in small, manageable batches (e.g., `request(10)`). Only request more when you have finished processing the current batch.

## 5. Context Loss (ThreadLocals)
*   **The Scenario**: You use `ThreadLocal` to store a user's security token. You start a reactive I/O pipeline.
*   **The Pitfall**: A single reactive request might be processed by Thread A, then paused for network I/O, and resumed by Thread B. `ThreadLocal` variables are tied to specific threads. When Thread B resumes the work, the security token is gone, leading to `NullPointerException` or security authorization failures.
*   **Mitigation**: Do not use `ThreadLocal` in reactive programming. Use the framework's native context propagation mechanisms (e.g., Reactor's `Context` API) or migrate to Java 21 `ScopedValue`s.