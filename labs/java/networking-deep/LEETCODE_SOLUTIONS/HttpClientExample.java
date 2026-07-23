package networking;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.*;

/**
 * Java HTTP Client (Java 11+) — modern, non-blocking HTTP client.
 * 
 * Features: HTTP/2, WebSocket, synchronous and asynchronous modes,
 *            request/response body handlers, custom executors.
 * 
 * Time: O(network) for synchronous, O(1) dispatch for async
 * Space: O(response body)
 */
public class HttpClientExample {

    public static void main(String[] args) throws Exception {
        var client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(java.time.Duration.ofSeconds(10))
            .build();

        // 1. Synchronous GET
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://httpbin.org/get"))
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        System.out.println("GET status: " + response.statusCode());
        System.out.println("GET body length: " + response.body().length());

        // 2. Asynchronous GET
        CompletableFuture<HttpResponse<String>> future = client.sendAsync(
            HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/delay/1"))
                .GET()
                .build(),
            BodyHandlers.ofString()
        );

        HttpResponse<String> asyncResponse = future.orTimeout(5, TimeUnit.SECONDS).join();
        System.out.println("Async GET status: " + asyncResponse.statusCode());

        // 3. POST with JSON body
        String json = "{\"name\":\"Java\",\"year\":2026}";
        HttpRequest postRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://httpbin.org/post"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> postResponse = client.send(postRequest, BodyHandlers.ofString());
        System.out.println("POST status: " + postResponse.statusCode());
        assert postResponse.body().contains("Java");

        // 4. Custom executor for async requests
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var customClient = HttpClient.newBuilder()
                .executor(executor)
                .build();

            var futures = new java.util.ArrayList<CompletableFuture<HttpResponse<String>>>();
            for (int i = 0; i < 5; i++) {
                futures.add(customClient.sendAsync(
                    HttpRequest.newBuilder().uri(URI.create("https://httpbin.org/get")).GET().build(),
                    BodyHandlers.ofString()
                ));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            System.out.println("All async requests completed.");
        }

        System.out.println("All HttpClientExample tests passed.");
    }
}