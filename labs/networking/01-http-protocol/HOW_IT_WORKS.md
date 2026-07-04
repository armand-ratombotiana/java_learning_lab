# HTTP Protocol - How It Works

## The Request-Response Cycle

```
Client                    Server
   |                         |
   |--- DNS Lookup -------->|
   |<-- IP Address ---------|
   |--- TCP SYN ----------->|
   |<-- TCP SYN-ACK --------|
   |--- TCP ACK ----------->|
   |--- TLS Handshake ----->|
   |<-- TLS Cert + Key -----|
   |--- HTTP Request ------>|
   |<-- HTTP Response ------|
```

## Java HTTP Server

```java
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class SimpleHttpServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/hello", exchange -> {
            String method = exchange.getRequestMethod();
            URI uri = exchange.getRequestURI();
            String response = "{\"message\":\"Hello, World!\"}";
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        System.out.println("Server started on port 8080");
    }
}
```

## Java HTTP Client

```java
import java.net.URI;
import java.net.http.*;
import java.util.concurrent.CompletableFuture;

public class HttpClientExample {
    private static final HttpClient client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .connectTimeout(java.time.Duration.ofSeconds(10))
        .build();

    public static HttpResponse<String> get(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url)).GET()
            .header("Accept", "application/json").build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static CompletableFuture<HttpResponse<String>> postAsync(String url, String json) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .header("Content-Type", "application/json").build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
```
