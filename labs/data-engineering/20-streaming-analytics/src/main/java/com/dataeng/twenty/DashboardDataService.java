package com.dataeng.twenty;

import java.net.http.*;
import java.net.URI;
import java.util.*;

public class DashboardDataService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String databaseUrl;

    public DashboardDataService(String databaseUrl) { this.databaseUrl = databaseUrl; }

    public record MetricSnapshot(String windowStart, String metric, long count, double avg) {}

    public List<MetricSnapshot> getLatestMetrics(int limit) {
        try {
            var request = HttpRequest.newBuilder()
                .uri(URI.create(databaseUrl + "/api/metrics?limit=" + limit))
                .GET()
                .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return parseMetrics(response.body());
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch metrics: " + e.getMessage());
        }
        return List.of();
    }

    private List<MetricSnapshot> parseMetrics(String json) {
        // Simplified JSON parsing for demonstration
        List<MetricSnapshot> metrics = new ArrayList<>();
        if (json != null && !json.isEmpty()) {
            metrics.add(new MetricSnapshot("now", "revenue", 100, 250.0));
            metrics.add(new MetricSnapshot("now", "users", 50, 1.5));
        }
        return metrics;
    }

    public boolean checkLatency(long maxAllowedLagMs) {
        try {
            var request = HttpRequest.newBuilder()
                .uri(URI.create(databaseUrl + "/api/health"))
                .GET()
                .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
