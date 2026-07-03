# Quizzes: Reactive I/O

Test your knowledge of AsynchronousFileChannels, Reactive Streams integration, and Event Loop mechanics.

## Quiz 1: Asynchronous I/O

**Q1: What is the primary difference between `FileChannel` and `AsynchronousFileChannel`?**
- A) `AsynchronousFileChannel` can only read data, not write it.
- B) `FileChannel` blocks the calling thread during read/write operations. `AsynchronousFileChannel` returns immediately, executing the I/O operation in the background and notifying the application via a `Future` or a `CompletionHandler` when the operation finishes.
- C) `AsynchronousFileChannel` uses UDP instead of TCP.
- D) There is no difference; they are aliases.
*Answer: B*

**Q2: When using `AsynchronousFileChannel` with a `CompletionHandler`, which thread executes the `completed()` callback method?**
- A) The main thread.
- B) The thread that initiated the read operation.
- C) A thread from the channel's associated `ExecutorService` (which is a default system thread pool if not explicitly provided).
- D) A new thread is created specifically for the callback.
*Answer: C*

## Quiz 2: Reactive Integration and Backpressure

**Q1: How does the Java 11 `HttpClient` implement Reactive I/O when downloading a large file?**
- A) It loads the entire file into a `byte[]` and then wraps it in a reactive stream.
- B) It acts as a `Publisher`, pushing chunks of data (`ByteBuffer`s) to a `Subscriber`. It uses the `request(n)` backpressure mechanism to pause reading from the OS network socket if the subscriber cannot keep up.
- C) It uses a `while(true)` loop to poll the socket continuously.
- D) It uses the `java.io.InputStream` API.
*Answer: B*

**Q2: In a high-performance Reactive I/O framework like Netty, why is it dangerous to call `subscription.request(Long.MAX_VALUE)`?**
- A) It disables backpressure. The framework will read data from the network as fast as the OS allows and push it into the JVM heap. If the application processes this data slowly, the heap will fill up, causing an `OutOfMemoryError`.
- B) It causes a `StackOverflowError`.
- C) It instantly closes the connection.
- D) It throws an `IllegalArgumentException`.
*Answer: A*

## Quiz 3: The Event Loop

**Q1: You are building a Reactive web server using Spring WebFlux. Inside a reactive pipeline, you use standard JDBC to query a database (which takes 2 seconds). What is the consequence?**
- A) The database query automatically becomes non-blocking.
- B) You block one of the few Event Loop threads. If you block all Event Loop threads (e.g., 4 threads on a 4-core machine), the entire web server stops accepting new connections and freezes for 2 seconds.
- C) The compiler throws an error.
- D) The JVM spawns a new thread to handle the blockage.
*Answer: B*