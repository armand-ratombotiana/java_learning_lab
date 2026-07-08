package com.arch.sidecar;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class AmbassadorProxy {
    private final HttpClient httpClient;
    private final String targetHost;
    private final int targetPort;
    private final Duration timeout;

    public AmbassadorProxy(String targetHost, int targetPort, Duration timeout) {
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.timeout = timeout;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();
    }

    public String forward(String path, String body) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://" + targetHost + ":" + targetPort + path))
                    .timeout(timeout)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new ProxyException("Ambassador forwarding failed: " + e.getMessage());
        }
    }

    public String getHealth() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://" + targetHost + ":" + targetPort + "/health"))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 ? "healthy" : "unhealthy";
        } catch (Exception e) {
            return "unreachable";
        }
    }
}
