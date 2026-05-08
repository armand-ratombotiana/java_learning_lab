package com.learning.helidon;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class Lab {
    // Helidon SE: functional routing
    static class SERouting {
        final Map<String, Map<String, Function<Map<String,String>, String>>> routes = new HashMap<>();

        SERouting get(String path, Function<Map<String,String>, String> handler) {
            routes.computeIfAbsent(path, p -> new HashMap<>()).put("GET", handler);
            return this;
        }

        SERouting post(String path, Function<Map<String,String>, String> handler) {
            routes.computeIfAbsent(path, p -> new HashMap<>()).put("POST", handler);
            return this;
        }

        void dispatch(String method, String path, Map<String,String> params) {
            var handlers = routes.get(path);
            if (handlers != null && handlers.containsKey(method)) {
                System.out.println("[SE] " + method + " " + path + " -> " + handlers.get(method).apply(params));
            } else {
                System.out.println("[SE] 404 Not Found: " + method + " " + path);
            }
        }
    }

    // Helidon MP: annotated-style (simulated)
    @interface GET {}
    @interface POST {}
    @interface Path {
        String value();
    }

    static class MPResource {
        private final String basePath;
        private final Map<String, Map<String, Function<Map<String,String>, String>>> endpoints = new HashMap<>();

        MPResource(String basePath) { this.basePath = basePath; }

        void endpoint(String method, String subPath, Function<Map<String,String>, String> handler) {
            endpoints.computeIfAbsent(method, m -> new HashMap<>()).put(basePath + subPath, handler);
        }

        void dispatch(String method, String fullPath, Map<String,String> params) {
            var handlers = endpoints.get(method);
            if (handlers != null && handlers.containsKey(fullPath)) {
                System.out.println("[MP] " + method + " " + fullPath + " -> " + handlers.get(fullPath).apply(params));
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Helidon SE & MP Concepts ===\n");

        // Helidon SE
        System.out.println("--- Helidon SE (Functional) ---");
        var se = new SERouting();
        se.get("/hello", p -> "Hello " + p.getOrDefault("name", "World") + "!");
        se.get("/health", p -> "{\"status\":\"UP\"}");
        se.post("/data", p -> "Received: " + p);

        se.dispatch("GET", "/hello", Map.of("name", "Helidon"));
        se.dispatch("GET", "/health", Map.of());
        se.dispatch("POST", "/data", Map.of("key", "value"));

        // Helidon MP
        System.out.println("\n--- Helidon MP (MicroProfile Annotations) ---");
        var mp = new MPResource("/api");
        mp.endpoint("GET", "/greet", p -> "Hello from MP! name=" + p.getOrDefault("name", "anon"));
        mp.endpoint("GET", "/config", p -> "{\"app.port\":8080,\"app.host\":\"localhost\"}");
        mp.endpoint("POST", "/items", p -> "Created item: " + p);

        mp.dispatch("GET", "/api/greet", Map.of("name", "MP-User"));
        mp.dispatch("GET", "/api/config", Map.of());

        // Reactive concepts
        System.out.println("\n--- Helidon Reactive (Simulated) ---");
        var executor = Executors.newSingleThreadExecutor();
        var futures = new ArrayList<CompletableFuture<String>>();
        for (int i = 0; i < 3; i++) {
            int id = i;
            var f = CompletableFuture.supplyAsync(() -> {
                try { Thread.sleep(100); } catch (InterruptedException e) {}
                return "Processed request #" + id;
            }, executor);
            futures.add(f);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenRun(() -> System.out.println("[Reactive] All " + futures.size() + " requests completed"));
        futures.forEach(f -> System.out.println("[Reactive] " + f.join()));
        executor.shutdown();

        // Health checks
        System.out.println("\n--- Health Checks ---");
        var healthChecks = Map.of(
            "diskSpace", true, "database", true, "cache", false
        );
        healthChecks.forEach((name, healthy) ->
            System.out.println("  " + name + ": " + (healthy ? "UP" : "DOWN")));
        System.out.println("  Overall: " + (healthChecks.values().stream().allMatch(b -> b) ? "UP" : "DEGRADED"));
    }
}
