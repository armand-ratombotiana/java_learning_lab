package com.learning.javalin;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Lab {
    static class JavalinApp {
        record Route(String method, String path, BiFunction<Map<String,String>, Map<String,String>, String> handler) {}
        record HandlerContext(Map<String,String> pathParams, Map<String,String> queryParams, String body) {}
        final List<Route> routes = new ArrayList<>();
        final Map<String, List<Consumer<HandlerContext>>> beforeHandlers = new HashMap<>();
        final Map<String, List<Consumer<HandlerContext>>> afterHandlers = new HashMap<>();

        JavalinApp get(String path, BiFunction<Map<String,String>, Map<String,String>, String> handler) {
            routes.add(new Route("GET", path, handler)); return this;
        }

        JavalinApp post(String path, BiFunction<Map<String,String>, Map<String,String>, String> handler) {
            routes.add(new Route("POST", path, handler)); return this;
        }

        JavalinApp put(String path, BiFunction<Map<String,String>, Map<String,String>, String> handler) {
            routes.add(new Route("PUT", path, handler)); return this;
        }

        JavalinApp delete(String path, BiFunction<Map<String,String>, Map<String,String>, String> handler) {
            routes.add(new Route("DELETE", path, handler)); return this;
        }

        JavalinApp before(String path, Consumer<HandlerContext> middleware) {
            beforeHandlers.computeIfAbsent(path, p -> new ArrayList<>()).add(middleware);
            return this;
        }

        JavalinApp after(String path, Consumer<HandlerContext> middleware) {
            afterHandlers.computeIfAbsent(path, p -> new ArrayList<>()).add(middleware);
            return this;
        }

        String handle(String method, String path, Map<String,String> params, String body) {
            var ctx = new HandlerContext(extractParams(path), params, body);
            runHandlers(beforeHandlers, path, ctx);
            var result = routes.stream()
                .filter(r -> r.method.equals(method) && matches(r.path, path))
                .findFirst()
                .map(r -> r.handler.apply(ctx.pathParams, ctx.queryParams))
                .orElse("404 Not Found");
            runHandlers(afterHandlers, path, ctx);
            return result;
        }

        Map<String,String> extractParams(String path) {
            var params = new HashMap<String,String>();
            var segments = path.split("/");
            for (int i = 0; i < segments.length; i++) {
                if (segments[i].startsWith(":")) params.put(segments[i].substring(1), segments[i]);
            }
            return params;
        }

        boolean matches(String pattern, String path) {
            var pParts = pattern.split("/");
            var aParts = path.split("/");
            if (pParts.length != aParts.length) return false;
            for (int i = 0; i < pParts.length; i++) {
                if (!pParts[i].startsWith(":") && !pParts[i].equals(aParts[i])) return false;
            }
            return true;
        }

        void runHandlers(Map<String, List<Consumer<HandlerContext>>> map, String path, HandlerContext ctx) {
            map.getOrDefault(path, List.of()).forEach(h -> h.accept(ctx));
        }
    }

    static class InMemoryStore {
        final Map<String, String> data = new HashMap<>();
        String create(String k, String v) { data.put(k, v); return "Created: " + k; }
        String read(String k) { return Optional.ofNullable(data.get(k)).orElse("Not found"); }
        String update(String k, String v) { data.put(k, v); return "Updated: " + k; }
        String delete(String k) { data.remove(k); return "Deleted: " + k; }
    }

    public static void main(String[] args) {
        System.out.println("=== Javalin: Lightweight Web Framework ===");
        var app = new JavalinApp();
        var store = new InMemoryStore();

        // Routes
        app.get("/", (p, q) -> "Welcome to Javalin Lab");
        app.get("/hello/:name", (p, q) -> "Hello, " + p.get(":name") + "!");
        app.get("/items", (p, q) -> "Items: " + String.join(", ", store.data.keySet()));
        app.post("/items/:id", (p, q) -> store.create(p.get(":id"), q.getOrDefault("value", "unknown")));
        app.put("/items/:id", (p, q) -> store.update(p.get(":id"), q.getOrDefault("value", "unknown")));
        app.delete("/items/:id", (p, q) -> store.delete(p.get(":id")));

        // Middleware
        app.before("/", ctx -> System.out.println("[before] Request received: " + ctx.body));
        app.after("/", ctx -> System.out.println("[after] Response sent"));

        // Simulate requests
        var requests = List.of(
            Map.entry("GET /", Map.of()),
            Map.entry("GET /hello/Javalin", Map.of()),
            Map.entry("POST /items/book1", Map.of("value", "1984")),
            Map.entry("POST /items/book2", Map.of("value", "Brave New World")),
            Map.entry("GET /items", Map.of()),
            Map.entry("GET /items/:id", Map.of(":id", "book1")),
            Map.entry("PUT /items/book1", Map.of("value", "Nineteen Eighty-Four")),
            Map.entry("DELETE /items/book2", Map.of()),
            Map.entry("GET /items", Map.of())
        );

        for (var req : requests) {
            var parts = req.getKey().split(" ", 2);
            var method = parts[0];
            var path = parts[1];
            var pathParams = resolvePathParams(path);
            var result = app.handle(method, path, pathParams, req.getValue().toString());
            System.out.println("  " + req.getKey() + " -> " + result);
        }

        System.out.println("\n--- Static Files (Simulated) ---");
        System.out.println("  JavalinConfig config = new JavalinConfig();");
        System.out.println("  config.staticFiles.add(\"/public\");");
        System.out.println("  config.enableCorsForAllOrigins();");
        System.out.println("  Javalin.create(config).start(7070);");
    }

    static Map<String,String> resolvePathParams(String path) {
        var m = new HashMap<String,String>();
        var segments = path.split("/");
        for (int i = 0; i < segments.length; i++) {
            if (segments[i].startsWith(":")) m.put(segments[i], segments[i].substring(1));
        }
        return m;
    }
}
