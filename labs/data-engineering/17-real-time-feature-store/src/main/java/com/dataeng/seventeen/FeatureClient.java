package com.dataeng.seventeen;

import java.net.http.*;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;

public class FeatureClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String feastUrl;
    private final Map<String, CachedValue> cache = new ConcurrentHashMap<>();

    public FeatureClient(String feastUrl) { this.feastUrl = feastUrl; }

    public record FeatureValue(String name, Object value, long timestamp) {}
    public record CachedValue(FeatureValue value, long expiry) {}

    public Map<String, FeatureValue> getOnlineFeatures(String featureView, Map<String, String> entities, List<String> features) {
        Map<String, FeatureValue> result = new HashMap<>();
        List<String> uncached = new ArrayList<>();

        for (String feature : features) {
            String cacheKey = featureView + ":" + entities + ":" + feature;
            CachedValue cached = cache.get(cacheKey);
            if (cached != null && cached.expiry() > System.currentTimeMillis()) {
                result.put(feature, cached.value());
            } else {
                uncached.add(feature);
            }
        }

        if (!uncached.isEmpty()) {
            try {
                String json = buildRequestJson(featureView, entities, uncached);
                var request = HttpRequest.newBuilder()
                    .uri(URI.create(feastUrl + "/get-online-features"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
                var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    // Parse response and update cache (simplified)
                    for (String f : uncached) {
                        FeatureValue fv = new FeatureValue(f, response.body(), System.currentTimeMillis());
                        cache.put(featureView + ":" + entities + ":" + f, new CachedValue(fv, System.currentTimeMillis() + 5000));
                        result.put(f, fv);
                    }
                }
            } catch (Exception e) {
                System.err.println("Feature serving failed: " + e.getMessage());
            }
        }

        return result;
    }

    private String buildRequestJson(String featureView, Map<String, String> entities, List<String> features) {
        var sb = new StringBuilder();
        sb.append("{"feature_service":"").append(featureView).append("",");
        sb.append(""entities":{");
        var entityParts = entities.entrySet().stream()
            .map(e -> "\"" + e.getKey() + "\":\"" + e.getValue() + "\"")
            .toList();
        sb.append(String.join(",", entityParts));
        sb.append("},"features":[");
        sb.append(String.join(",", features.stream().map(f -> "\"" + f + "\"").toList()));
        sb.append("]}");
        return sb.toString();
    }

    public void clearCache() { cache.clear(); }
    public int cacheSize() { return cache.size(); }
}
