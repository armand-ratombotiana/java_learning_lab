# Interview Preparation: Java HTTP Client

This document covers advanced questions related to the Java 11 `HttpClient`, resource management, asynchronous programming, and WebSockets.

## Q1: Why is it considered a critical bug to instantiate a new `HttpClient` for every request?
**Answer:**
`HttpClient` is a heavy object. It maintains its own internal connection pool (to reuse TCP connections), its own thread pool (for asynchronous operations), and its own SSL context.
If you create a new `HttpClient` inside a method that is called frequently, you are creating new thread pools and connection pools that are never reused. This will rapidly exhaust the operating system's ephemeral ports (leading to `BindException`) and exhaust JVM memory (leading to `OutOfMemoryError` from thread explosion).
`HttpClient` instances are immutable and thread-safe. You must create one instance and share it globally.

## Q2: How does `HttpClient.sendAsync()` handle concurrency, and what is the danger of relying on its default behavior?
**Answer:**
`sendAsync()` returns a `CompletableFuture` and executes the HTTP request on a background thread.
By default, the `HttpClient` uses a cached thread pool. A cached thread pool creates a new thread for every submitted task if no idle threads are available.
**The Danger**: If your application receives a spike in traffic and makes 10,000 concurrent async requests to a slow downstream API, the cached thread pool will spawn 10,000 threads. This will crash the JVM.
**Mitigation**: You must provide a custom, bounded `ExecutorService` (e.g., `Executors.newFixedThreadPool(50)`) to the `HttpClient.Builder` to control concurrency and provide backpressure.

## Q3: Explain the difference between `BodyHandlers.ofString()` and `BodyHandlers.ofInputStream()`. When would you use each?
**Answer:**
*   `BodyHandlers.ofString()` buffers the entire HTTP response body into a single `byte[]` in memory, converts it to a `String`, and returns it. It is convenient but dangerous for large payloads. If the server sends a 2GB response, it will cause an `OutOfMemoryError`.
*   `BodyHandlers.ofInputStream()` returns an `InputStream` connected to the active network socket. It allows you to read the response chunk-by-chunk. You use this when downloading large files, processing massive JSON arrays lazily, or proxying data to another service without buffering it in memory.

## Q4: How does the `WebSocket.Listener` interface implement backpressure?
**Answer:**
The `WebSocket.Listener` is deeply integrated with Reactive Streams concepts. It does not push messages to your application as fast as they arrive over the network.
Instead, it waits for your application to explicitly request messages. When you call `webSocket.request(n)`, the client will deliver up to `n` messages to your `onText` or `onBinary` methods.
If you do not call `request(n)` (or return the `super.onText(...)` method, which automatically requests 1 message), the client will stop reading from the network socket. This causes the TCP buffer to fill up, which eventually signals the server to stop sending data, effectively preventing your application from being overwhelmed.

## Q5: What happens if an `HttpClient` configured for HTTP/2 connects to a server that only supports HTTP/1.1?
**Answer:**
The `HttpClient` handles this gracefully via protocol negotiation (ALPN during the TLS handshake).
If the server does not support HTTP/2, the client will silently downgrade the connection and use HTTP/1.1.
While this prevents the request from failing, it can cause performance issues if the application architecture heavily relies on HTTP/2 multiplexing (sending multiple concurrent requests over a single TCP connection). In HTTP/1.1, the client will have to open multiple TCP connections or suffer from Head-of-Line blocking. Developers can verify the negotiated protocol by checking `response.version()`.