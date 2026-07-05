package com.cloud.compute;

import java.util.*;
import java.util.concurrent.*;

public class LambdaFunctionSim {

    public interface LambdaHandler {
        Object handleRequest(Map<String, Object> event, Context context);
    }

    public static class Context {
        public final String functionName;
        public final String functionVersion;
        public final long memoryLimitInMB;
        public final String awsRequestId;
        public final long startTime;

        public Context(String functionName, long memoryMB) {
            this.functionName = functionName;
            this.functionVersion = "$LATEST";
            this.memoryLimitInMB = memoryMB;
            this.awsRequestId = UUID.randomUUID().toString().substring(0, 8);
            this.startTime = System.currentTimeMillis();
        }

        public long getRemainingTimeInMillis() {
            return 15_000 - (System.currentTimeMillis() - startTime);
        }
    }

    public static class LambdaRuntime {
        private final Map<String, LambdaHandler> handlers = new ConcurrentHashMap<>();
        private final ExecutorService executor = Executors.newCachedThreadPool();

        public void registerFunction(String name, LambdaHandler handler) {
            handlers.put(name, handler);
            System.out.println("Registered function: " + name);
        }

        public CompletableFuture<Object> invoke(String functionName, Map<String, Object> event) {
            LambdaHandler handler = handlers.get(functionName);
            if (handler == null) {
                return CompletableFuture.failedFuture(
                    new IllegalArgumentException("Function not found: " + functionName));
            }

            return CompletableFuture.supplyAsync(() -> {
                Context ctx = new Context(functionName, 512);
                System.out.println("\nInvoking " + functionName + " (reqId=" + ctx.awsRequestId + ")");
                long start = System.nanoTime();
                Object result = handler.handleRequest(event, ctx);
                long duration = (System.nanoTime() - start) / 1_000_000;
                System.out.println("  Completed in " + duration + "ms");
                return result;
            }, executor);
        }

        public void shutdown() { executor.shutdown(); }
    }

    public static void main(String[] args) throws Exception {
        LambdaRuntime runtime = new LambdaRuntime();

        runtime.registerFunction("hello-world", (event, ctx) -> {
            String name = (String) event.getOrDefault("name", "World");
            return Map.of("statusCode", 200, "body", "Hello, " + name + "!");
        });

        runtime.registerFunction("process-order", (event, ctx) -> {
            String orderId = (String) event.get("orderId");
            try { Thread.sleep(50); } catch (InterruptedException e) {}
            return Map.of("orderId", orderId, "status", "PROCESSED");
        });

        System.out.println("=== Lambda Functions ===");
        Object r1 = runtime.invoke("hello-world", Map.of("name", "Alice")).get();
        System.out.println("Result: " + r1);

        Object r2 = runtime.invoke("process-order", Map.of("orderId", "ORD-12345")).get();
        System.out.println("Result: " + r2);

        runtime.shutdown();
    }
}
