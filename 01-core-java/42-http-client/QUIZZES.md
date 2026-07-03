# Quizzes: Java HTTP Client

Test your knowledge of the Java 11 `HttpClient`, asynchronous requests, and WebSockets.

## Quiz 1: Core Mechanics

**Q1: Why is it a critical anti-pattern to call `HttpClient.newHttpClient()` inside a method that is executed frequently?**
- A) It causes a syntax error.
- B) `HttpClient` maintains its own connection pool, thread pool, and SSL context. Creating a new instance for every request is incredibly expensive and will quickly exhaust system resources (threads and sockets), leading to OOM or BindException. It must be created once and shared globally.
- C) It automatically downgrades the connection to HTTP/1.0.
- D) It prevents the use of asynchronous requests.
*Answer: B*

**Q2: Which class represents an immutable HTTP request, usually created via a Builder?**
- A) `HttpURLConnection`
- B) `HttpRequest`
- C) `HttpResponse`
- D) `BodyHandler`
*Answer: B*

## Quiz 2: Asynchronous Execution

**Q1: What does `client.sendAsync(request, bodyHandler)` return?**
- A) `String`
- B) `HttpResponse<T>`
- C) `CompletableFuture<HttpResponse<T>>`
- D) `Thread`
*Answer: C (This allows you to chain non-blocking operations using `.thenApply()` and `.thenAccept()`).*

**Q2: If you use `sendAsync()` to make 1,000 requests, what thread pool executes those requests by default?**
- A) The main thread.
- B) A dedicated pool of exactly 10 threads.
- C) A cached thread pool that will spawn a new thread for every request if none are available, which can potentially crash the JVM if the server is slow.
- D) The ForkJoinPool.commonPool().
*Answer: C (This is why providing a custom, bounded `Executor` in the `HttpClient.Builder` is essential for production).*

## Quiz 3: WebSockets and Body Handlers

**Q1: In the `WebSocket.Listener` interface, why might your client stop receiving messages after the first one?**
- A) Because WebSockets are inherently unidirectional.
- B) Because the listener uses reactive backpressure. If you do not explicitly call `webSocket.request(1)` (or return the `super` method) inside `onText`, the client will not ask the server for the next message.
- C) Because the connection times out after 1 second.
- D) Because you must instantiate a new WebSocket for each message.
*Answer: B*

**Q2: If you need to download a 10GB video file, which `BodyHandler` should you use?**
- A) `BodyHandlers.ofString()`
- B) `BodyHandlers.ofByteArray()`
- C) `BodyHandlers.ofFile(Path)`
- D) `BodyHandlers.discarding()`
*Answer: C (This streams the data directly to the disk, preventing the JVM from trying to load 10GB into the heap and throwing an OutOfMemoryError).*