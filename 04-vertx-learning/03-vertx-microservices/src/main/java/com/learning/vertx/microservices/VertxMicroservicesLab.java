package com.learning.vertx.microservices;

import java.util.concurrent.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class VertxMicroservicesLab {

    static class ServiceEndpoint {
        final String name;
        final String host;
        final int port;
        final boolean healthy;

        ServiceEndpoint(String name, String host, int port, boolean healthy) {
            this.name = name;
            this.host = host;
            this.port = port;
            this.healthy = healthy;
        }

        public String call(String request) {
            if (!healthy) throw new RuntimeException(name + " is unhealthy");
            return name + " response to: " + request;
        }
    }

    static class ServiceRegistry {
        private final Map<String, List<ServiceEndpoint>> services = new ConcurrentHashMap<>();
        private final Random random = new Random();

        public void register(String serviceName, ServiceEndpoint endpoint) {
            services.computeIfAbsent(serviceName, k -> new CopyOnWriteArrayList<>()).add(endpoint);
        }

        public ServiceEndpoint discover(String serviceName) {
            List<ServiceEndpoint> instances = services.get(serviceName);
            if (instances == null || instances.isEmpty())
                throw new RuntimeException("No instances for " + serviceName);
            return instances.get(random.nextInt(instances.size()));
        }

        public List<ServiceEndpoint> getAll(String serviceName) {
            return services.getOrDefault(serviceName, List.of());
        }
    }

    static class ApiGateway {
        private final ServiceRegistry registry;
        private final Map<String, String> routeMap = new ConcurrentHashMap<>();

        ApiGateway(ServiceRegistry registry) {
            this.registry = registry;
        }

        public void addRoute(String path, String serviceName) {
            routeMap.put(path, serviceName);
        }

        public String route(String path, String request) {
            String serviceName = routeMap.get(path);
            if (serviceName == null) return "404: No route for " + path;
            ServiceEndpoint ep = registry.discover(serviceName);
            return ep.call(request);
        }
    }

    static class CircuitBreaker {
        enum State { CLOSED, OPEN, HALF_OPEN }

        private State state = State.CLOSED;
        private int failureCount = 0;
        private final int threshold;
        private final long timeoutMs;
        private long lastFailureTime;

        CircuitBreaker(int threshold, long timeoutMs) {
            this.threshold = threshold;
            this.timeoutMs = timeoutMs;
        }

        public synchronized <T> T call(Callable<T> operation) throws Exception {
            if (state == State.OPEN) {
                if (System.currentTimeMillis() - lastFailureTime > timeoutMs) {
                    state = State.HALF_OPEN;
                    System.out.println("  Circuit -> HALF_OPEN, probing...");
                } else {
                    throw new RuntimeException("Circuit OPEN, request rejected");
                }
            }
            try {
                T result = operation.call();
                if (state == State.HALF_OPEN) {
                    state = State.CLOSED;
                    failureCount = 0;
                    System.out.println("  Circuit -> CLOSED (probe succeeded)");
                }
                return result;
            } catch (Exception e) {
                failureCount++;
                lastFailureTime = System.currentTimeMillis();
                if (failureCount >= threshold) {
                    state = State.OPEN;
                    System.out.println("  Circuit -> OPEN (threshold reached)");
                }
                throw e;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Vert.x Microservices Concepts Lab ===\n");

        serviceRegistryAndDiscovery();
        apiGatewayDemo();
        circuitBreakerDemo();
        asyncServiceComposition();
    }

    static void serviceRegistryAndDiscovery() {
        System.out.println("--- Service Registry & Discovery ---");
        ServiceRegistry registry = new ServiceRegistry();
        registry.register("user-service", new ServiceEndpoint("user-svc-1", "10.0.0.1", 8081, true));
        registry.register("user-service", new ServiceEndpoint("user-svc-2", "10.0.0.2", 8081, true));
        registry.register("order-service", new ServiceEndpoint("order-svc-1", "10.0.0.3", 8082, true));

        for (int i = 0; i < 3; i++) {
            ServiceEndpoint ep = registry.discover("user-service");
            System.out.println("  Dispatched to: " + ep.name + " @ " + ep.host + ":" + ep.port);
        }
    }

    static void apiGatewayDemo() {
        System.out.println("\n--- API Gateway ---");
        ServiceRegistry registry = new ServiceRegistry();
        registry.register("users", new ServiceEndpoint("user-api", "10.0.0.1", 8081, true));
        registry.register("orders", new ServiceEndpoint("order-api", "10.0.0.2", 8082, true));

        ApiGateway gateway = new ApiGateway(registry);
        gateway.addRoute("/api/users", "users");
        gateway.addRoute("/api/orders", "orders");

        System.out.println("  " + gateway.route("/api/users", "GET /list"));
        System.out.println("  " + gateway.route("/api/orders", "GET /123"));
        System.out.println("  " + gateway.route("/api/unknown", "GET"));
    }

    static void circuitBreakerDemo() throws Exception {
        System.out.println("\n--- Circuit Breaker ---");
        CircuitBreaker cb = new CircuitBreaker(3, 1000);
        AtomicInteger callCount = new AtomicInteger(0);

        for (int i = 0; i < 7; i++) {
            try {
                String result = cb.call(() -> {
                    int n = callCount.incrementAndGet();
                    if (n <= 3) throw new RuntimeException("Service timeout");
                    return "Success on call " + n;
                });
                System.out.println("  Result: " + result);
            } catch (Exception e) {
                System.out.println("  Failed: " + e.getMessage());
            }
            Thread.sleep(100);
        }
    }

    static void asyncServiceComposition() throws Exception {
        System.out.println("\n--- Async Service Composition ---");
        ExecutorService exec = Executors.newFixedThreadPool(3);

        CompletableFuture<String> userFuture = CompletableFuture.supplyAsync(() -> {
            sleep(200);
            return "{id:1, name:'Alice'}";
        }, exec);

        CompletableFuture<String> orderFuture = CompletableFuture.supplyAsync(() -> {
            sleep(150);
            return "{id:100, total:250.00}";
        }, exec);

        String combined = userFuture.thenCombine(orderFuture,
            (user, order) -> "User: " + user + " | Order: " + order).get();
        System.out.println("  Composed: " + combined);

        exec.shutdown();
    }

    private static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
