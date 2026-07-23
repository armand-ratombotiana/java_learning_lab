# Mock Interview Transcript: Networking

## Interviewer: Staff Engineer, Amazon
## Candidate: Senior Java developer
## Time: 40 minutes
## Focus: HTTP clients, WebSocket, gRPC, network performance

---

**Q1: Compare the Java 11 HttpClient with Apache HttpClient.**

**Candidate**: Java 11 HttpClient is built-in (no external dependency), supports HTTP/2 (and HTTP/1.1 fallback), provides both synchronous and asynchronous (CompletableFuture) APIs, and has a cleaner builder-based API. Apache HttpClient is more feature-rich (connection pooling customization, proxy handling, cookie stores). For new projects, prefer Java 11 HttpClient unless you need Apache's specific features.

**Interviewer**: Write code to make a POST request with JSON body using Java 11 HttpClient.

**Candidate**: 
```java
HttpClient client = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(5))
    .build();

String json = "{\"name\":\"Alice\",\"email\":\"alice@example.com\"}";

HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/users"))
    .header("Content-Type", "application/json")
    .timeout(Duration.ofSeconds(10))
    .POST(HttpRequest.BodyPublishers.ofString(json))
    .build();

HttpResponse<String> response = client.send(request, 
    HttpResponse.BodyHandlers.ofString());
System.out.println(response.statusCode());  // 201
```

**Interviewer**: What about async?

**Candidate**: 
```java
CompletableFuture<HttpResponse<String>> future = 
    client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

future.thenAccept(response -> {
    if (response.statusCode() == 201) {
        System.out.println("Created: " + response.body());
    }
}).exceptionally(err -> {
    System.err.println("Request failed: " + err.getMessage());
    return null;
});
```

**Interviewer**: How does HTTP/2 multiplexing work at the Java level?

**Candidate**: HTTP/2 allows multiple concurrent requests over a single TCP connection. The Java HttpClient automatically uses HTTP/2 if the server supports it (and Java's implementation supports it). Multiplexing happens transparently — multiple `sendAsync()` calls share the same connection. This reduces connection overhead and head-of-line blocking compared to HTTP/1.1.

**Interviewer**: How do you implement a WebSocket client in Java?

**Candidate**: 
```java
WebSocket ws = HttpClient.newHttpClient()
    .newWebSocketBuilder()
    .buildAsync(URI.create("wss://echo.example.com"), 
        new WebSocket.Listener() {
            public CompletionStage<?> onText(WebSocket ws, 
                    CharSequence data, boolean last) {
                System.out.println("Received: " + data);
                return ws.request(1);  // Request next message
            }
            public void onError(WebSocket ws, Throwable e) {
                System.err.println("Error: " + e);
            }
        })
    .join();

ws.sendText("Hello, WebSocket!", true);
```

**Interviewer**: How does `ws.request(1)` work for backpressure?

**Candidate**: WebSocket messages are flow-controlled. The listener must explicitly request messages via `request(n)`. If you don't call `request()`, no more messages arrive. This provides backpressure — the application controls the message flow rate. Each `onText` calls `request(1)` to receive the next message.

**Interviewer**: Compare REST with gRPC for microservices.

**Candidate**: gRPC advantages: (1) Strongly typed (protobuf). (2) HTTP/2 streaming (server, client, bidirectional). (3) Smaller, faster serialization. (4) Code generation (client stubs, server skeletons). (5) Streaming with backpressure. REST advantages: (1) Universally understood. (2) Works with browser clients. (3) No code generation needed. (4) Easier debugging. (5) Better for public APIs.

**Interviewer**: Final: How would you debug a network timeout issue?

**Candidate**: (1) Check if the timeout is set at all — many defaults are infinite. (2) Check DNS resolution — is it fast or slow? (3) Check TCP connect timing — is the server reachable? (4) Use `curl` to isolate network path issues. (5) Check server-side — is the server overloaded? (6) Check firewalls and load balancers. (7) Use Java Flight Recorder to capture socket events.

---

## Feedback

**Strengths**:
- Proficient with Java 11 HttpClient
- Correct WebSocket with backpressure
- Understands HTTP/2 multiplexing
- Practical debugging approach

**Areas for Improvement**:
- Could mention `BodyPublishers.ofFile()` for efficient file uploads
- Should discuss `keepAlive` and connection pooling

**Score**: 4/5 — Strong networking knowledge
