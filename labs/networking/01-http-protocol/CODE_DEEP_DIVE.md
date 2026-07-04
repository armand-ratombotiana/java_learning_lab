# HTTP Protocol - Code Deep Dive

## Building a Complete HTTP Client

```java
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class AdvancedHttpClient {
    private final HttpClient client;
    private final String baseUrl;

    public AdvancedHttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }

    public <T> CompletableFuture<T> get(String path,
            HttpResponse.BodyHandler<T> handler) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .GET()
            .header("Accept", "application/json")
            .timeout(Duration.ofSeconds(30))
            .build();
        return client.sendAsync(request, handler)
            .thenApply(HttpResponse::body);
    }

    public <T> CompletableFuture<T> post(String path, Object body,
            HttpResponse.BodyHandler<T> handler) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .POST(BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .build();
            return client.sendAsync(request, handler)
                .thenApply(HttpResponse::body);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        AdvancedHttpClient client = new AdvancedHttpClient("https://api.example.com");
        client.get("/users/1", BodyHandlers.ofString())
            .thenAccept(body -> System.out.println("User: " + body));
    }
}
```

## Building an HTTP Server with Routing

```java
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class RouterHttpServer {
    private final HttpServer server;
    private final List<Route> routes = new ArrayList<>();

    record Route(Pattern pattern, String method,
        java.util.function.BiFunction<String, Map<String,String>, String> handler) {}

    public RouterHttpServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
    }

    public void get(String path,
            java.util.function.BiFunction<String, Map<String,String>, String> handler) {
        routes.add(new Route(Pattern.compile(path), "GET", handler));
    }

    public void start() {
        server.createContext("/", exchange -> {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String requestBody = new String(exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8);

            for (Route route : routes) {
                Matcher matcher = route.pattern.matcher(path);
                if (route.method.equals(method) && matcher.matches()) {
                    Map<String,String> groups = new HashMap<>();
                    for (int i = 1; i <= matcher.groupCount(); i++)
                        groups.put("param" + i, matcher.group(i));
                    String response = route.handler.apply(requestBody, groups);
                    byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, bytes.length);
                    exchange.getResponseBody().write(bytes);
                    exchange.close();
                    return;
                }
            }
            String notFound = "{\"error\":\"Not Found\"}";
            byte[] bytes = notFound.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(404, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
    }
}
```
