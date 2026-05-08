package com.learning.micronaut.grpc;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class MicronautGrpcLab {

    static class ProtoMessage {
        final Map<String, Object> fields = new LinkedHashMap<>();

        ProtoMessage set(String key, Object value) { fields.put(key, value); return this; }
        <T> T get(String key) { return (T) fields.get(key); }
        public String toString() { return "Message" + fields; }
    }

    static class ProtoBuilder {
        public static ProtoMessage of(String... kv) {
            ProtoMessage msg = new ProtoMessage();
            for (int i = 0; i < kv.length; i += 2) msg.set(kv[i], kv[i + 1]);
            return msg;
        }
    }

    interface GrpcService {
        String serviceName();
        ProtoMessage handle(String method, ProtoMessage request);
    }

    static class UserService implements GrpcService {
        private final Map<String, ProtoMessage> users = new ConcurrentHashMap<>();

        UserService() {
            users.put("1", ProtoBuilder.of("id","1","name","Alice","email","alice@x.com"));
            users.put("2", ProtoBuilder.of("id","2","name","Bob","email","bob@x.com"));
        }

        public String serviceName() { return "UserService"; }

        public ProtoMessage handle(String method, ProtoMessage request) {
            return switch (method) {
                case "getUser" -> getUser(request);
                case "listUsers" -> listUsers();
                case "createUser" -> createUser(request);
                default -> ProtoBuilder.of("error", "Unknown method: " + method);
            };
        }

        private ProtoMessage getUser(ProtoMessage req) {
            String id = req.get("id");
            ProtoMessage user = users.get(id);
            return user != null ? user : ProtoBuilder.of("error", "User not found");
        }

        private ProtoMessage listUsers() {
            return ProtoBuilder.of("count", String.valueOf(users.size()), "users", users.values().toString());
        }

        private ProtoMessage createUser(ProtoMessage req) {
            String id = String.valueOf(users.size() + 1);
            ProtoMessage newUser = ProtoBuilder.of("id",id,"name",req.get("name"),"email",req.get("email"));
            users.put(id, newUser);
            return newUser;
        }
    }

    static class ProductService implements GrpcService {
        public String serviceName() { return "ProductService"; }
        public ProtoMessage handle(String method, ProtoMessage request) {
            return switch (method) {
                case "getProduct" -> ProtoBuilder.of("id",request.get("id"),"name","Laptop","price","1299.99");
                default -> ProtoBuilder.of("error","Unknown method");
            };
        }
    }

    static class GrpcServer {
        private final Map<String, GrpcService> services = new ConcurrentHashMap<>();
        private final ExecutorService executor = Executors.newFixedThreadPool(4);
        private volatile boolean running = false;

        public void registerService(GrpcService service) {
            services.put(service.serviceName(), service);
        }

        public void start() {
            running = true;
            System.out.println("  gRPC server started on port 50051");
        }

        public CompletableFuture<ProtoMessage> callAsync(String serviceName, String method, ProtoMessage request) {
            return CompletableFuture.supplyAsync(() -> {
                GrpcService svc = services.get(serviceName);
                if (svc == null) return ProtoBuilder.of("error", "Service not found: " + serviceName);
                return svc.handle(method, request);
            }, executor);
        }

        public void shutdown() { executor.shutdown(); running = false; }
    }

    static class GrpcClient {
        private final GrpcServer server;
        private final Map<String, String> headers = new ConcurrentHashMap<>();

        GrpcClient(GrpcServer server) { this.server = server; }

        public void setHeader(String key, String value) { headers.put(key, value); }

        public ProtoMessage call(String service, String method, ProtoMessage request) {
            System.out.println("  Client -> " + service + "/" + method + " req=" + request);
            ProtoMessage response = server.services.get(service).handle(method, request);
            System.out.println("  Client <- " + response);
            return response;
        }

        public CompletableFuture<ProtoMessage> callAsync(String service, String method, ProtoMessage request) {
            return server.callAsync(service, method, request);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Micronaut gRPC Concepts Lab ===\n");

        unaryRpc();
        serverStreaming();
        clientStreaming();
        bidirectionalStreaming();
        interceptorDemo();
    }

    static void unaryRpc() throws Exception {
        System.out.println("--- Unary RPC (Request-Response) ---");
        GrpcServer server = new GrpcServer();
        server.registerService(new UserService());
        server.start();

        GrpcClient client = new GrpcClient(server);
        client.call("UserService", "getUser", ProtoBuilder.of("id", "1"));
        client.call("UserService", "getUser", ProtoBuilder.of("id", "999"));

        server.shutdown();
    }

    static void serverStreaming() throws Exception {
        System.out.println("\n--- Server-Side Streaming ---");
        GrpcServer server = new GrpcServer();
        server.registerService(new UserService());

        ProtoMessage allUsers = server.services.get("UserService").handle("listUsers", new ProtoMessage());
        System.out.println("  Server streaming response: " + allUsers);
        System.out.println("  (gRPC streams multiple responses from a single request)");
    }

    static void clientStreaming() throws Exception {
        System.out.println("\n--- Client-Side Streaming ---");
        GrpcServer server = new GrpcServer();
        server.registerService(new UserService());
        GrpcClient client = new GrpcClient(server);

        client.call("UserService", "createUser", ProtoBuilder.of("name","Charlie","email","charlie@x.com"));
        ProtoMessage result = client.call("UserService", "getUser", ProtoBuilder.of("id","3"));
        System.out.println("  (gRPC streams multiple client requests to a single response)");
    }

    static void bidirectionalStreaming() throws Exception {
        System.out.println("\n--- Bidirectional Streaming ---");
        GrpcServer server = new GrpcServer();
        server.registerService(new UserService());
        GrpcClient client = new GrpcClient(server);

        List<ProtoMessage> requests = List.of(
            ProtoBuilder.of("id","1"), ProtoBuilder.of("id","2"));
        System.out.println("  BiDi: sending " + requests.size() + " requests, receiving streamed responses");
        for (ProtoMessage req : requests) {
            client.call("UserService", "getUser", req);
        }
    }

    static void interceptorDemo() throws Exception {
        System.out.println("\n--- Interceptors / Middleware ---");
        GrpcServer server = new GrpcServer();
        server.registerService(new UserService());
        GrpcClient client = new GrpcClient(server);

        client.setHeader("authorization", "Bearer token-xyz");
        client.setHeader("x-trace-id", "trace-001");

        long start = System.nanoTime();
        client.call("UserService", "getUser", ProtoBuilder.of("id","1"));
        long elapsed = System.nanoTime() - start;
        System.out.println("  Request completed in " + TimeUnit.NANOSECONDS.toMillis(elapsed) + "ms");
        System.out.println("  (Micronaut gRPC supports interceptors for auth, logging, tracing)");
    }
}
