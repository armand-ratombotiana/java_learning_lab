# Edge Cases & Pitfalls: Java HTTP Client

The Java 11 `HttpClient` is powerful, but its asynchronous and reactive nature introduces new pitfalls regarding resource management, thread exhaustion, and memory leaks.

## 1. The `HttpClient` Instantiation Leak
*   **The Scenario**: You write a method that fetches data from an API. Inside the method, you instantiate a new `HttpClient`.
    ```java
    public String fetchData() {
        HttpClient client = HttpClient.newHttpClient(); // BUG!
        return client.send(request, BodyHandlers.ofString()).body();
    }
    ```
*   **The Pitfall**: `HttpClient` manages an internal connection pool, a thread pool for asynchronous tasks, and SSL contexts. Creating a new client for every request is incredibly expensive and will quickly exhaust system resources (threads and sockets), leading to an `OutOfMemoryError` or `BindException`.
*   **Mitigation**: `HttpClient` instances are immutable and strictly thread-safe. You MUST create a single `HttpClient` instance and share it globally across your application.

## 2. The `sendAsync` Thread Pool Exhaustion
*   **The Scenario**: You use `client.sendAsync()` to make 10,000 concurrent HTTP requests.
*   **The Pitfall**: By default, `HttpClient` uses a cached thread pool to manage asynchronous tasks. If you fire 10,000 requests to a slow server, the default executor will spawn 10,000 threads. This can crash the JVM.
*   **Mitigation**: Always provide a custom, bounded `ExecutorService` when building the `HttpClient` for production use.
    ```java
    HttpClient client = HttpClient.newBuilder()
        .executor(Executors.newFixedThreadPool(50)) // Limit concurrency
        .build();
    ```

## 3. The WebSocket "One Message" Trap
*   **The Scenario**: You connect a `WebSocket` and implement the `Listener.onText` method. You receive the first message, but the server sends a second message and your client never receives it.
*   **The Pitfall**: The `WebSocket.Listener` interface relies on Reactive Streams backpressure. By default, it requests 1 message. When `onText` is called, that request is consumed. If you don't explicitly request more messages, the WebSocket will silently pause, waiting for your permission to deliver the next message.
*   **Mitigation**: You must call `webSocket.request(1)` at the end of your `onText` method, OR simply return the result of `super.onText(webSocket, data, last)` (which automatically calls `request(1)` for you).

## 4. `BodyHandlers.ofString()` Memory Exhaustion
*   **The Scenario**: You use `BodyHandlers.ofString()` to download a 5GB log file or a massive JSON payload.
*   **The Pitfall**: `ofString()` attempts to read the entire HTTP response body into a single `String` object in the JVM heap. A 5GB string will immediately cause an `OutOfMemoryError`.
*   **Mitigation**: Use streaming body handlers for large payloads.
    *   `BodyHandlers.ofFile(Path)`: Streams the response directly to disk.
    *   `BodyHandlers.ofInputStream()`: Allows you to process the payload chunk-by-chunk.

## 5. HTTP/2 Protocol Downgrades
*   **The Scenario**: You configure the client to use `HttpClient.Version.HTTP_2`. You connect to a server.
*   **The Pitfall**: HTTP/2 requires ALPN (Application-Layer Protocol Negotiation) over TLS (HTTPS). If you connect to an `http://` endpoint, or if the server does not support HTTP/2, the `HttpClient` will silently downgrade the connection to HTTP/1.1. If your application relies on HTTP/2 multiplexing for performance, this silent downgrade can cause massive, unexpected latency bottlenecks.
*   **Mitigation**: Monitor the actual protocol used by calling `response.version()`. If you strictly require HTTP/2, you must enforce it and fail fast if the server negotiates HTTP/1.1.