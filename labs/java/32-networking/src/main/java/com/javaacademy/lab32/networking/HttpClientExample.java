package com.javaacademy.lab32.networking;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class HttpClientExample {

    private static final HttpClient client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();

    public String syncGet(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .timeout(Duration.ofSeconds(10))
            .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        return response.body();
    }

    public String syncPost(String url, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString(jsonBody))
            .timeout(Duration.ofSeconds(10))
            .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        return response.body();
    }

    public CompletableFuture<String> asyncGet(String url) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
        return client.sendAsync(request, BodyHandlers.ofString())
            .thenApply(HttpResponse::body);
    }

    public CompletableFuture<String> asyncPost(String url, String jsonBody) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString(jsonBody))
            .build();
        return client.sendAsync(request, BodyHandlers.ofString())
            .thenApply(HttpResponse::body);
    }

    public int getStatusCode(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        return client.send(request, BodyHandlers.discarding()).statusCode();
    }

    public static HttpClient getClient() { return client; }
}
