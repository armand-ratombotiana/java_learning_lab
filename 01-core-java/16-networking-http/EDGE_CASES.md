# Module 16: Networking & HTTP - Edge Cases & Pitfalls

---

## Pitfall 1: Resource Leaks with Sockets

### ❌ Wrong
Forgetting to close a socket or its input/output streams can exhaust available file descriptors on the OS.
```java
Socket socket = new Socket("localhost", 8080);
socket.getOutputStream().write("Hello".getBytes());
// Never closed!
```

### ✅ Correct
Always use try-with-resources to ensure sockets and streams are closed automatically.
```java
try (Socket socket = new Socket("localhost", 8080)) {
    socket.getOutputStream().write("Hello".getBytes());
}
```

---

## Pitfall 2: Blocking Operations

### ❌ Wrong
Traditional Socket `read()` and `write()` operations block the thread indefinitely if the remote peer doesn't respond, leading to hung threads.

### ✅ Correct
Always set timeouts on your sockets or use asynchronous non-blocking IO (NIO) or the async capabilities of the new `HttpClient`.
```java
Socket socket = new Socket();
socket.connect(new InetSocketAddress("localhost", 8080), 5000); // 5 sec connection timeout
socket.setSoTimeout(5000); // 5 sec read timeout
```

---

## Pitfall 3: Legacy HttpURLConnection vs HttpClient

### ❌ Wrong
Using `HttpURLConnection` for complex operations (like multiplexing or async requests) leads to convoluted, error-prone code.

### ✅ Correct
For modern applications running on Java 11+, prefer the `java.net.http.HttpClient` API, which natively supports HTTP/2, WebSockets, and asynchronous requests via `CompletableFuture`.