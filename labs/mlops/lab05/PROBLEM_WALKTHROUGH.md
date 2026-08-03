# Problem Walkthrough: Model Serving with Docker

## Problem 1: Self-Testing Fraud Score Server — Company: Uber
### Interview Scenario
"You're at Uber. The fraud team's scoring model — weights `{0.5, -0.2, 0.8, 0.1}`, bias `0.3` — is about to ship as a Docker container, and you need to prove the endpoint contract before the image is built: correct prediction output, health endpoints that return the documented bodies, an explicit 400 on feature-count mismatch, and a 405 on non-POST. Build the server exactly as the lab does with `com.sun.net.httpserver`, then self-test it over real HTTP — start on an ephemeral port, issue requests with the JDK's `HttpClient`, print every response, and shut down."

### The Problem
1. Load the model with the lab's weights and bias and serve `POST /predict` with `{"prediction": ...}` responses.
2. Serve `GET /healthz` → `{"status":"ok"}` and `GET /readyz` → `{"status":"ready"}`.
3. Return `400 {"error":"Expected 4 features, got N"}` for wrong feature counts and `405` for non-POST on `/predict`.
4. Prove the contract end-to-end: bind on port 0, run real HTTP requests against the live server, print status + body per request.
5. Shut the server down cleanly so the program exits — a container-friendly demo.

### Solution Walkthrough
- Step 1: Reuse the lab's `Model` class verbatim — `predict` dot product with the length guard throwing `IllegalArgumentException("Expected 4 features, got 2")`.
- Step 2: Reuse the lab's `parseFeatures`, `sendResponse` (JSON content type, byte-length headers), and `/healthz`/`/readyz` handlers unchanged.
- Step 3: Bind `HttpServer.create(new InetSocketAddress(0), 0)` — port 0 makes the OS pick a free port, read it back via `server.getAddress().getPort()`, so the demo needs no fixed port and can't collide.
- Step 4: Start the server, then use the JDK's `java.net.http.HttpClient` (no external deps) to send GET `/healthz`, GET `/readyz`, POST `/predict` with the guide's payload `{"features": [5.1, 3.5, 1.4, 0.2]}`, a 2-feature POST, and a GET on `/predict`.
- Step 5: Print each response as `METHOD path -> status body` so the contract is human-checkable against this document.
- Step 6: `server.stop(0)` after the tests and shut down the executor pool — in the real container the server would block forever; the self-test needs the clean exit, and an un-shutdown `newFixedThreadPool` would keep non-daemon threads alive and hang the demo.

### Code
```java
package com.mlops.lab05;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ModelServerWalkthrough {

    static class Model {
        private final double[] weights;
        private final double bias;

        Model(double[] weights, double bias) {
            this.weights = weights;
            this.bias = bias;
        }

        double predict(double[] features) {
            if (features.length != weights.length) {
                throw new IllegalArgumentException(
                        "Expected " + weights.length + " features, got " + features.length);
            }
            double result = bias;
            for (int i = 0; i < features.length; i++) {
                result += weights[i] * features[i];
            }
            return result;
        }
    }

    static double[] parseFeatures(String json) {
        String numsPart = json.replaceAll(".*\\[", "").replaceAll("\\].*", "");
        String[] parts = numsPart.split(",");
        double[] features = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            features[i] = Double.parseDouble(parts[i].trim());
        }
        return features;
    }

    static void sendResponse(HttpExchange exchange, int code, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static void main(String[] args) throws Exception {
        Model model = new Model(new double[]{0.5, -0.2, 0.8, 0.1}, 0.3);

        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        ExecutorService pool = Executors.newFixedThreadPool(4);
        server.setExecutor(pool);

        server.createContext("/healthz", exchange ->
                sendResponse(exchange, 200, "{\"status\":\"ok\"}"));
        server.createContext("/readyz", exchange ->
                sendResponse(exchange, 200, "{\"status\":\"ready\"}"));
        server.createContext("/predict", exchange -> {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }
            try {
                String body;
                try (InputStream is = exchange.getRequestBody();
                     BufferedReader br = new BufferedReader(
                             new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    body = br.lines().reduce("", (a, b) -> a + b);
                }
                double[] features = parseFeatures(body);
                double prediction = model.predict(features);
                sendResponse(exchange, 200, "{\"prediction\":" + prediction + "}");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        });

        server.start();
        int port = server.getAddress().getPort();
        System.out.println("Model server started on port " + port);
        System.out.println("Endpoints: POST /predict, GET /healthz, GET /readyz\n");

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> health = client.send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/healthz"))
                        .GET().build(), HttpResponse.BodyHandlers.ofString());
        System.out.println("GET  /healthz  -> " + health.statusCode() + " " + health.body());

        HttpResponse<String> ready = client.send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/readyz"))
                        .GET().build(), HttpResponse.BodyHandlers.ofString());
        System.out.println("GET  /readyz   -> " + ready.statusCode() + " " + ready.body());

        HttpResponse<String> pred = client.send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/predict"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{\"features\": [5.1, 3.5, 1.4, 0.2]}"))
                        .build(), HttpResponse.BodyHandlers.ofString());
        System.out.println("POST /predict  -> " + pred.statusCode() + " " + pred.body());

        HttpResponse<String> bad = client.send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/predict"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{\"features\": [1.0, 2.0]}"))
                        .build(), HttpResponse.BodyHandlers.ofString());
        System.out.println("POST /predict (2 features) -> " + bad.statusCode() + " " + bad.body());

        HttpResponse<String> wrongMethod = client.send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/predict"))
                        .GET().build(), HttpResponse.BodyHandlers.ofString());
        System.out.println("GET  /predict  -> " + wrongMethod.statusCode() + " " + wrongMethod.body());

        server.stop(0);
        pool.shutdown();
        System.out.println("\nServer stopped cleanly.");
    }
}
```

### Expected Output
```
Model server started on port 44229
Endpoints: POST /predict, GET /healthz, GET /readyz

GET  /healthz  -> 200 {"status":"ok"}
GET  /readyz   -> 200 {"status":"ready"}
POST /predict  -> 200 {"prediction":3.2899999999999996}
POST /predict (2 features) -> 400 {"error":"Expected 4 features, got 2"}
GET  /predict  -> 405 {"error":"Method not allowed"}

Server stopped cleanly.
```
*(The ephemeral port number varies per run; everything else is exact. `0.3 + 0.5×5.1 − 0.2×3.5 + 0.8×1.4 + 0.1×0.2` equals `3.29` in decimal, but IEEE-754 double arithmetic produces `3.2899999999999996`, which is what the raw `toJson`-style concatenation prints — a good argument for formatting predictions before serializing.)*

---

## Problem 2: Multi-Stage Docker Build for the Server — Company: Stripe
### Interview Scenario
"You're at Stripe. The `ModelServingLab` compiles fine locally, but the image your team ships is 500MB and the container needs nothing except the JVM. Containerize it with a multi-stage build and prove the endpoint with curl."

### The Problem
1. Build with a JDK image, run with a JRE image.
2. Copy only the compiled classes into the runtime stage.
3. Document the port, the startup command, and the smoke-test curl.

### Solution Walkthrough
- Step 1: Builder stage from `eclipse-temurin:21-jdk`, `WORKDIR /app`, copy `src/com/mlops/lab05/*.java`, run `javac`.
- Step 2: Runtime stage from `eclipse-temurin:21-jre` — no compiler shipped to production.
- Step 3: `COPY --from=builder /app .`, `EXPOSE 8080`, `CMD ["java", "com.mlops.lab05.ModelServingLab"]`.
- Step 4: Build, run, and smoke-test with the guide's curl; `3.29` matches the server's dot product.

### Code
```dockerfile
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY src/com/mlops/lab05/*.java ./com/mlops/lab05/
RUN javac com/mlops/lab05/*.java

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app .
EXPOSE 8080
CMD ["java", "com.mlops.lab05.ModelServingLab"]
```
### Expected Output
```
$ docker build -t mlops-model-server .
$ docker run -p 8080:8080 mlops-model-server
$ curl -X POST http://localhost:8080/predict \
    -H "Content-Type: application/json" \
    -d '{"features": [5.1, 3.5, 1.4, 0.2]}'
{"prediction":3.2899999999999996}
```

---

## Problem 3: Sliding-Window Rate Limiter on /predict — Company: Netflix
### Interview Scenario
"You're at Netflix. A partner integration is hammering `/predict` at 200 req/s, tripling your GPU bill. Add a per-client sliding-window limiter in front of prediction — the LeetCode 359 pattern — and prove it in the demo."

### The Problem
1. Allow at most 3 prediction requests per client per 10-second window.
2. Return `429 {"error":"Rate limit exceeded"}` beyond the window.
3. Print the decision for each simulated request, deterministically.

### Solution Walkthrough
- Step 1: Keep a `Map<String, Deque<Long>>` of request timestamps per client, pruning entries older than the window before counting.
- Step 2: In the `/predict` handler, call `allow(clientId)` before parsing; if denied, respond 429.
- Step 3: Deterministic demo: simulate 5 requests from `partner_a` — first 3 pass, the next 2 get 429 until the window slides.

### Code
```java
static class RateLimiter {
    private final Map<String, Deque<Long>> hits = new HashMap<>();
    private final int maxRequests;
    private final long windowMs;

    RateLimiter(int maxRequests, long windowMs) {
        this.maxRequests = maxRequests;
        this.windowMs = windowMs;
    }

    boolean allow(String clientId) {
        long now = System.currentTimeMillis();
        Deque<Long> deque = hits.computeIfAbsent(clientId, k -> new ArrayDeque<>());
        while (!deque.isEmpty() && now - deque.peekFirst() >= windowMs) {
            deque.pollFirst();
        }
        if (deque.size() >= maxRequests) return false;
        deque.addLast(now);
        return true;
    }
}

RateLimiter limiter = new RateLimiter(3, 10_000);   // 3 req / 10s per client
for (int i = 1; i <= 5; i++) {
    boolean allowed = limiter.allow("partner_a");
    System.out.printf("Request %d: %s%n", i, allowed ? "200 (predicted)" : "429 (rate limited)");
}
```
### Expected Output
```
Request 1: 200 (predicted)
Request 2: 200 (predicted)
Request 3: 200 (predicted)
Request 4: 429 (rate limited)
Request 5: 429 (rate limited)
```
