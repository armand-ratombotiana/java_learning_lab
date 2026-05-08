package com.learning.serverless;

import java.util.*;
import java.util.function.*;
import java.util.concurrent.*;
import java.time.*;

public class Lab {

    @FunctionalInterface
    interface Function<T, R> {
        R handle(T input, Context ctx);
    }

    record Context(String requestId, Instant timestamp, String region, int memoryMB, int timeoutSec) {}

    record APIGatewayEvent(String httpMethod, String path, Map<String, String> queryParams,
                           String body, Map<String, String> headers) {}

    record APIGatewayResponse(int statusCode, String body, Map<String, String> headers) {}

    static class LambdaRuntime {
        private final Map<String, Function<APIGatewayEvent, APIGatewayResponse>> handlers = new ConcurrentHashMap<>();

        void register(String path, Function<APIGatewayEvent, APIGatewayResponse> handler) {
            handlers.put(path, handler);
        }

        APIGatewayResponse invoke(String path, APIGatewayEvent event) {
            var handler = handlers.get(path);
            if (handler == null) return new APIGatewayResponse(404, "Not found", Map.of());
            var ctx = new Context("req-" + System.nanoTime(), Instant.now(), "us-east-1", 512, 30);
            return handler.handle(event, ctx);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Serverless Lab ===\n");

        lambdaFunctions();
        eventDriven();
        coldStart();
        faasComparison();
        statelessDesign();
    }

    static void lambdaFunctions() {
        System.out.println("--- Lambda Functions ---");
        var runtime = new LambdaRuntime();

        runtime.register("/hello", (event, ctx) -> {
            System.out.println("  [Lambda] RequestId: " + ctx.requestId());
            var name = event.queryParams().getOrDefault("name", "World");
            return new APIGatewayResponse(200,
                "{\"message\":\"Hello, " + name + "!\",\"region\":\"" + ctx.region() + "\"}",
                Map.of("Content-Type", "application/json"));
        });

        runtime.register("/calc", (event, ctx) -> {
            try {
                int x = Integer.parseInt(event.queryParams().getOrDefault("x", "0"));
                int y = Integer.parseInt(event.queryParams().getOrDefault("y", "0"));
                return new APIGatewayResponse(200,
                    "{\"result\":" + (x + y) + "}",
                    Map.of("Content-Type", "application/json"));
            } catch (NumberFormatException e) {
                return new APIGatewayResponse(400, "{\"error\":\"invalid input\"}", Map.of());
            }
        });

        var helloEvent = new APIGatewayEvent("GET", "/hello",
            Map.of("name", "Alice"), "", Map.of());
        System.out.println("  GET /hello?name=Alice -> " + runtime.invoke("/hello", helloEvent).body());

        var calcEvent = new APIGatewayEvent("GET", "/calc",
            Map.of("x", "10", "y", "20"), "", Map.of());
        System.out.println("  GET /calc?x=10&y=20 -> " + runtime.invoke("/calc", calcEvent).body());
    }

    static void eventDriven() {
        System.out.println("\n--- Event-Driven Architecture ---");
        var events = new LinkedBlockingQueue<String>();
        var processed = new CopyOnWriteArrayList<String>();

        class EventProcessor implements Runnable {
            public void run() {
                try {
                    while (true) {
                        var event = events.poll(100, TimeUnit.MILLISECONDS);
                        if (event == null) break;
                        processed.add("Processed: " + event.toUpperCase());
                    }
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }

        new Thread(new EventProcessor()).start();
        for (var e : List.of("order.created", "payment.received", "email.sent")) {
            events.offer(e);
        }
        try { Thread.sleep(200); } catch (InterruptedException ex) {}
        processed.forEach(s -> System.out.println("  " + s));
        System.out.println("  Event sources: S3, SQS, DynamoDB Streams, Kinesis, API Gateway");
    }

    static void coldStart() {
        System.out.println("\n--- Cold Start & Warm Start ---");
        System.out.println("""
  Cold start: first invocation after idle/deploy
    ~500ms-3s depending on runtime & package size
    Steps:
      1. Download code from S3 (~100ms)
      2. Start runtime + JVM (~300ms)
      3. Load classpath (~200ms)
      4. Run static initializers (~100ms)
      5. Execute handler

  Warm start: subsequent invocations (reused container)
    ~1-10ms overhead

  Mitigations:
  - Provisioned Concurrency (keep N containers warm)
  - SnapStart (Lambda): VM snapshot after init phase
  - Minimize dependencies (GraalVM native image)
  - Lazy initialization of heavy resources
    """);
    }

    static void faasComparison() {
        System.out.println("--- FaaS Comparison ---");
        System.out.println("""
  AWS Lambda:
    Java: Java 21, SnapStart, 10GB max memory, 15min timeout
    SDK: aws-lambda-java-core

  Azure Functions:
    Java: Java 17, Premium plan for warm instances
    SDK: azure-functions-java-library

  Google Cloud Functions:
    Java: Java 17, 1st gen / 2nd gen (Cloud Run)
    SDK: functions-framework-java

  Spring Cloud Function:
    Write once, deploy on Lambda/Azure/Cloud Run/Knative
    Adapters for each cloud provider
    @Bean Function<String, String> uppercase() { ... }
    """);
    }

    static void statelessDesign() {
        System.out.println("\n--- Stateless Design ---");
        System.out.println("""
  Functions MUST be stateless:
  - No local file system state (use S3 / EFS)
  - No in-memory state beyond request lifetime
  - No thread affinity (container may be frozen)

  External state stores:
  - DynamoDB / Aurora Serverless for structured data
  - ElastiCache / DAX for caching
  - SQS / EventBridge for async workflows

  Best practices:
  - Single responsibility per function
  - Idempotent handlers (retry safe)
  - Dead-letter queues for failures
  - Structured logging (JSON) for CloudWatch
    """);
    }
}
