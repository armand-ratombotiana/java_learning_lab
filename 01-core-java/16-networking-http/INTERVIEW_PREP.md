# Module 16: Networking & HTTP - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the difference between TCP and UDP?
**Answer**:
- **TCP (Transmission Control Protocol)**: A connection-oriented protocol. It guarantees the reliable, ordered delivery of a stream of bytes. It handles packet loss via retransmissions and manages congestion (e.g., HTTP, HTTPS, SSH, FTP).
- **UDP (User Datagram Protocol)**: A connectionless protocol. It sends packets (datagrams) without verifying they reached the destination. It is much faster but unreliable, meaning packets can arrive out of order, be duplicated, or be lost entirely. Used for live streaming, gaming, and DNS.

### Q2: Explain the significance of the `SO_TIMEOUT` setting on a socket.
**Answer**:
By default, reading from a Java Socket `InputStream` is a blocking operation. If the remote server stops responding without closing the TCP connection (e.g., due to a network partition), the reading thread will block indefinitely, causing a thread leak. 
`setSoTimeout(int ms)` sets a timeout on blocking read operations. If no data arrives within the specified time, a `java.net.SocketTimeoutException` is thrown, allowing the thread to recover or close the connection.

### Q3: Why was the new `HttpClient` introduced in Java 11? What was wrong with `HttpURLConnection`?
**Answer**:
The legacy `HttpURLConnection` API was designed in the early days of Java. It is synchronous, blocking, hard to use, and only supports HTTP/1.1. 
The modern `java.net.http.HttpClient` API supports HTTP/2 (multiplexing), WebSockets, and offers a fluent builder API. Crucially, it provides native support for asynchronous requests using `CompletableFuture`, allowing highly concurrent network requests without exhausting thread pools.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Asynchronous HTTP Fetching
**Problem**: Write a method that takes a URL, sends an HTTP GET request asynchronously using the Java 11+ `HttpClient`, and prints the response body length when it completes, without blocking the main thread.

**Solution**:
```java
public void fetchUrlAsync(String targetUrl) {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(targetUrl))
            .GET()
            .build();
            
    client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
          .thenApply(HttpResponse::body)
          .thenAccept(body -> System.out.println("Length: " + body.length()))
          .exceptionally(ex -> {
              System.err.println("Request failed: " + ex.getMessage());
              return null;
          });
          
    System.out.println("Request submitted...");
}
```