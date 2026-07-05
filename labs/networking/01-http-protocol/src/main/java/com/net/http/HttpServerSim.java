package com.net.http;

import java.util.*;
import java.util.concurrent.*;

public class HttpServerSim {

    public interface HttpHandler {
        HttpResponse handle(HttpRequest request);
    }

    public static class SimpleServer {
        private final int port;
        private final Map<String, Map<String, HttpHandler>> routes = new ConcurrentHashMap<>();
        private volatile boolean running = false;
        private final ExecutorService pool = Executors.newFixedThreadPool(4);

        public SimpleServer(int port) { this.port = port; }

        public void get(String path, HttpHandler handler) {
            routes.computeIfAbsent("GET", k -> new ConcurrentHashMap<>()).put(path, handler);
        }

        public void post(String path, HttpHandler handler) {
            routes.computeIfAbsent("POST", k -> new ConcurrentHashMap<>()).put(path, handler);
        }

        public void start() {
            running = true;
            System.out.println("Server starting on port " + port);
        }

        public void stop() { running = false; pool.shutdown(); }

        public HttpResponse handleRequest(HttpRequest req) {
            Map<String, HttpHandler> methodRoutes = routes.get(req.method);
            if (methodRoutes != null) {
                HttpHandler handler = methodRoutes.get(req.path);
                if (handler != null) {
                    return handler.handle(req);
                }
            }
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "text/plain");
            return new HttpResponse(404, "Not Found", headers, "404 Not Found");
        }
    }

    public static void main(String[] args) {
        SimpleServer server = new SimpleServer(8080);

        server.get("/", req -> {
            Map<String, String> h = new HashMap<>();
            h.put("Content-Type", "text/html");
            return new HttpResponse(200, "OK", h, "<h1>Welcome</h1>");
        });

        server.get("/api/health", req -> {
            Map<String, String> h = new HashMap<>();
            h.put("Content-Type", "application/json");
            return new HttpResponse(200, "OK", h, "{\"status\":\"UP\"}");
        });

        server.post("/api/data", req -> {
            Map<String, String> h = new HashMap<>();
            h.put("Content-Type", "application/json");
            return new HttpResponse(201, "Created", h,
                "{\"received\":\"" + req.body + "\"}");
        });

        server.start();

        HttpRequest req1 = new HttpRequest("GET", "/api/health", new HashMap<>(), null);
        System.out.println(server.handleRequest(req1).body);

        HttpRequest req2 = new HttpRequest("POST", "/api/data", new HashMap<>(), "sample payload");
        System.out.println(server.handleRequest(req2).body);

        HttpRequest req3 = new HttpRequest("GET", "/notfound", new HashMap<>(), null);
        System.out.println(server.handleRequest(req3).statusCode);

        server.stop();
    }
}
