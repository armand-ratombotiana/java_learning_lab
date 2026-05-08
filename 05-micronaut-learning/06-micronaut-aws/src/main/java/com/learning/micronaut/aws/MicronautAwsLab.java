package com.learning.micronaut.aws;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.io.*;

public class MicronautAwsLab {

    static class LambdaContext {
        final String functionName;
        final String awsRequestId;
        final int memoryLimitMB;
        final long deadlineMs;

        LambdaContext(String functionName, String awsRequestId, int memoryLimitMB, long deadlineMs) {
            this.functionName = functionName; this.awsRequestId = awsRequestId;
            this.memoryLimitMB = memoryLimitMB; this.deadlineMs = deadlineMs;
        }
    }

    interface LambdaFunction<I, O> {
        O handleRequest(I input, LambdaContext context);
    }

    static class ApiGatewayEvent {
        final String httpMethod;
        final String path;
        final Map<String, String> queryParams;
        final String body;
        final Map<String, String> headers;

        ApiGatewayEvent(String httpMethod, String path, Map<String, String> queryParams,
                        String body, Map<String, String> headers) {
            this.httpMethod = httpMethod; this.path = path;
            this.queryParams = queryParams; this.body = body; this.headers = headers;
        }

        static ApiGatewayEvent fromRequest(String method, String path, String body) {
            return new ApiGatewayEvent(method, path, new HashMap<>(), body, new HashMap<>());
        }
    }

    static class ApiGatewayResponse {
        final int statusCode;
        final Map<String, String> headers;
        final String body;

        ApiGatewayResponse(int statusCode, String body) {
            this.statusCode = statusCode;
            this.headers = Map.of("Content-Type", "application/json");
            this.body = body;
        }

        ApiGatewayResponse(int statusCode, Map<String, String> headers, String body) {
            this.statusCode = statusCode;
            this.headers = headers;
            this.body = body;
        }

        static ApiGatewayResponse ok(String body) { return new ApiGatewayResponse(200, body); }
        static ApiGatewayResponse notFound(String msg) { return new ApiGatewayResponse(404, "{\"error\":\"" + msg + "\"}"); }
        static ApiGatewayResponse error(int code, String msg) { return new ApiGatewayResponse(code, "{\"error\":\"" + msg + "\"}"); }
    }

    static class S3Event {
        final String bucketName;
        final String objectKey;
        final long objectSize;
        final String eventType;

        S3Event(String bucket, String key, long size, String type) {
            this.bucketName = bucket; this.objectKey = key; this.objectSize = size; this.eventType = type;
        }
    }

    static class DynamoDbEvent {
        final String tableName;
        final String operation;
        final Map<String, Object> keys;
        final Map<String, Object> oldImage;
        final Map<String, Object> newImage;

        DynamoDbEvent(String table, String op, Map<String, Object> keys,
                      Map<String, Object> oldImage, Map<String, Object> newImage) {
            this.tableName = table; this.operation = op;
            this.keys = keys; this.oldImage = oldImage;
            this.newImage = newImage;
        }
    }

    static class UserLambda implements LambdaFunction<ApiGatewayEvent, ApiGatewayResponse> {
        private final Map<String, Map<String, String>> users = new ConcurrentHashMap<>();

        UserLambda() {
            users.put("1", Map.of("id","1","name","Alice","email","alice@x.com"));
            users.put("2", Map.of("id","2","name","Bob","email","bob@x.com"));
        }

        @Override
        public ApiGatewayResponse handleRequest(ApiGatewayEvent event, LambdaContext ctx) {
            System.out.println("  [Lambda " + ctx.functionName + "] reqId=" + ctx.awsRequestId);

            return switch (event.httpMethod) {
                case "GET" -> handleGet(event);
                case "POST" -> handlePost(event);
                default -> ApiGatewayResponse.error(405, "Method not allowed");
            };
        }

        private ApiGatewayResponse handleGet(ApiGatewayEvent event) {
            String id = event.path.replace("/users/", "");
            if (id.isEmpty()) return ApiGatewayResponse.ok(users.values().toString());
            Map<String, String> user = users.get(id);
            return user != null ? ApiGatewayResponse.ok(user.toString()) : ApiGatewayResponse.notFound("User " + id);
        }

        private ApiGatewayResponse handlePost(ApiGatewayEvent event) {
            String newId = String.valueOf(users.size() + 1);
            users.put(newId, Map.of("id",newId,"name","New User","email","new@x.com"));
            return ApiGatewayResponse.ok("{\"created\":\"" + newId + "\"}");
        }
    }

    static class LambdaRuntime {
        private final Map<String, LambdaFunction<ApiGatewayEvent, ApiGatewayResponse>> handlers = new ConcurrentHashMap<>();
        private final ExecutorService executor = Executors.newFixedThreadPool(4);

        public void register(String functionName, LambdaFunction<ApiGatewayEvent, ApiGatewayResponse> handler) {
            handlers.put(functionName, handler);
        }

        public CompletableFuture<ApiGatewayResponse> invokeAsync(String functionName, ApiGatewayEvent event) {
            return CompletableFuture.supplyAsync(() -> {
                LambdaContext ctx = new LambdaContext(functionName, UUID.randomUUID().toString(), 512, System.currentTimeMillis() + 30000);
                LambdaFunction<ApiGatewayEvent, ApiGatewayResponse> fn = handlers.get(functionName);
                return fn != null ? fn.handleRequest(event, ctx) : ApiGatewayResponse.error(500, "Function not found");
            }, executor);
        }

        public ApiGatewayResponse invoke(String functionName, ApiGatewayEvent event) {
            try { return invokeAsync(functionName, event).get(10, TimeUnit.SECONDS); }
            catch (Exception e) { return ApiGatewayResponse.error(500, e.getMessage()); }
        }

        public void shutdown() { executor.shutdown(); }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Micronaut AWS Lambda Concepts Lab ===\n");

        lambdaFunctionDemo();
        apiGatewayIntegration();
        eventSourceDemo();
        coldStartWarmStart();
    }

    static void lambdaFunctionDemo() {
        System.out.println("--- Lambda Function ---");
        LambdaRuntime runtime = new LambdaRuntime();
        runtime.register("user-function", new UserLambda());

        ApiGatewayResponse r1 = runtime.invoke("user-function",
            ApiGatewayEvent.fromRequest("GET", "/users/1", ""));
        System.out.println("  Response: " + r1.statusCode + " body=" + r1.body);

        ApiGatewayResponse r2 = runtime.invoke("user-function",
            ApiGatewayEvent.fromRequest("GET", "/users/999", ""));
        System.out.println("  Response: " + r2.statusCode + " body=" + r2.body);
    }

    static void apiGatewayIntegration() throws Exception {
        System.out.println("\n--- API Gateway + Lambda Integration ---");
        LambdaRuntime runtime = new LambdaRuntime();
        runtime.register("api-handler", new UserLambda());

        var requests = List.of(
            ApiGatewayEvent.fromRequest("GET", "/users/", ""),
            ApiGatewayEvent.fromRequest("GET", "/users/2", ""),
            ApiGatewayEvent.fromRequest("POST", "/users/", "{\"name\":\"Charlie\"}"));

        var futures = requests.stream().map(r -> runtime.invokeAsync("api-handler", r)).toList();
        for (var f : futures) { ApiGatewayResponse resp = f.get(); System.out.println("  " + resp.statusCode + ": " + resp.body); }
    }

    static void eventSourceDemo() {
        System.out.println("\n--- Event Sources (S3, DynamoDB Streams) ---");
        S3Event s3Event = new S3Event("my-bucket", "images/photo.jpg", 2048576, "ObjectCreated:Put");
        System.out.println("  S3 Event: " + s3Event.bucketName + "/" + s3Event.objectKey
            + " (" + s3Event.objectSize + " bytes)");

        DynamoDbEvent dbEvent = new DynamoDbEvent("Orders", "INSERT",
            Map.of("orderId", "1001"), Map.of(),
            Map.of("orderId","1001","total","250.00","status","PENDING"));
        System.out.println("  DynamoDB Event: " + dbEvent.operation + " on " + dbEvent.tableName);

        System.out.println("  (Micronaut AWS triggers Lambdas from S3, DynamoDB Streams, SQS, etc.)");
    }

    static void coldStartWarmStart() throws Exception {
        System.out.println("\n--- Cold Start vs Warm Start ---");
        LambdaRuntime runtime = new LambdaRuntime();
        runtime.register("greeter", (event, ctx) -> { sleep(200); return ApiGatewayResponse.ok("Hello"); });

        long cold = measureInvocation(runtime, "greeter");
        long warm1 = measureInvocation(runtime, "greeter");
        long warm2 = measureInvocation(runtime, "greeter");
        System.out.println("  Cold: ~" + cold + "ms | Warm: ~" + warm1 + "ms, ~" + warm2 + "ms");
        System.out.println("  (GraalVM native-image minimizes cold starts)");
    }

    private static long measureInvocation(LambdaRuntime runtime, String fnName) throws Exception {
        long start = System.nanoTime();
        runtime.invoke(fnName, ApiGatewayEvent.fromRequest("GET", "/", ""));
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    }

    private static void sleep(int ms) { try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } }
}
