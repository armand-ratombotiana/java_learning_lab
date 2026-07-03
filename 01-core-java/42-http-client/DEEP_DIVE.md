# Deep Dive: Java HTTP Client (Java 11+)

## 1. The Legacy Problem (`HttpURLConnection`)
For years, Java developers relied on `java.net.HttpURLConnection` to make HTTP requests. It was notoriously difficult to use:
*   It only supported HTTP/1.1.
*   It was strictly synchronous and blocking.
*   The API was clunky, requiring manual stream management and boilerplate code to handle simple tasks like reading a response body into a String.
Because of this, developers almost exclusively used third-party libraries like Apache HttpClient or OkHttp.

## 2. The Modern Solution: `java.net.http.HttpClient`
Introduced as an incubator in Java 9 and standardized in Java 11, the new `HttpClient` API is a massive leap forward.
*   **Modern Protocols**: Native support for HTTP/1.1, HTTP/2, and WebSockets.
*   **Asynchronous**: Full support for non-blocking requests using `CompletableFuture`.
*   **Reactive**: Integrates with the Java 9 Flow API (Reactive Streams) for handling request and response bodies.
*   **Fluent API**: Uses the Builder pattern for clean, readable code.

## 3. The Core Components
The API is built around three core classes:

### 1. `HttpClient`
The engine that sends requests. It manages connection pools, HTTP/2 multiplexing, and routing. You should create one instance and reuse it across your application.
```java
HttpClient client = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_2)
    .connectTimeout(Duration.ofSeconds(10))
    .build();
```

### 2. `HttpRequest`
Represents the request to be sent. It is immutable and created via a builder.
```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/data"))
    .timeout(Duration.ofSeconds(5))
    .header("Accept", "application/json")
    .GET()
    .build();
```

### 3. `HttpResponse`
Represents the result of the request. It contains the status code, headers, and the body.
To handle the body, you must provide a `BodyHandler` when sending the request. The JDK provides common handlers in `HttpResponse.BodyHandlers`.
```java
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
System.out.println(response.statusCode());
System.out.println(response.body());
```

## 4. Asynchronous Requests
To avoid blocking the calling thread, you can send requests asynchronously. `client.sendAsync()` returns a `CompletableFuture<HttpResponse<T>>`.

```java
client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
    .thenApply(HttpResponse::body)
    .thenAccept(body -> System.out.println("Async Response: " + body))
    .exceptionally(e -> {
        System.err.println("Request failed: " + e.getMessage());
        return null;
    });
// Thread continues executing immediately without waiting for the network
```

## 5. WebSockets
The `HttpClient` also acts as a WebSocket client. WebSockets provide a persistent, bi-directional, full-duplex TCP connection, ideal for real-time applications like chat or live trading feeds.

To use it, you build a `WebSocket` and provide a `WebSocket.Listener` to handle incoming events asynchronously.

```java
WebSocket ws = client.newWebSocketBuilder()
    .buildAsync(URI.create("wss://echo.websocket.events"), new WebSocket.Listener() {
        @Override
        public void onOpen(WebSocket webSocket) {
            System.out.println("CONNECTED");
            webSocket.sendText("Hello Server", true);
            WebSocket.Listener.super.onOpen(webSocket); // MUST call to request more messages
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            System.out.println("RECEIVED: " + data);
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }
    }).join();
```
*Note: The WebSocket listener uses Reactive backpressure mechanics. You must explicitly request more messages by calling `webSocket.request(1)` or calling the `super` methods.*