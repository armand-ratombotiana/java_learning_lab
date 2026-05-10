package com.learning.lab.module19.solution;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Solution {

    // Service Discovery
    public interface ServiceRegistry {
        void register(String serviceId, String url);
        void deregister(String serviceId);
        String getServiceUrl(String serviceId);
        List<String> getAllServices();
    }

    public static class InMemoryServiceRegistry implements ServiceRegistry {
        private final Map<String, String> services = new HashMap<>();

        @Override
        public void register(String serviceId, String url) {
            services.put(serviceId, url);
            System.out.println("Registered: " + serviceId + " -> " + url);
        }

        @Override
        public void deregister(String serviceId) {
            services.remove(serviceId);
            System.out.println("Deregistered: " + serviceId);
        }

        @Override
        public String getServiceUrl(String serviceId) {
            return services.get(serviceId);
        }

        @Override
        public List<String> getAllServices() {
            return new ArrayList<>(services.keySet());
        }
    }

    // Service Discovery: Eureka-style
    public static class EurekaClient {
        private final ServiceRegistry registry;

        public EurekaClient(ServiceRegistry registry) {
            this.registry = registry;
        }

        public void registerWithEureka(String serviceId, String host, int port) {
            String url = "http://" + host + ":" + port;
            registry.register(serviceId, url);
        }

        public String discover(String serviceId) {
            return registry.getServiceUrl(serviceId);
        }
    }

    // Feign Client
    public interface FeignClient {
        String get(String url);
        String post(String url, Object body);
    }

    public static class FeignClientImpl implements FeignClient {
        @Override
        public String get(String url) {
            System.out.println("GET: " + url);
            return "Response from " + url;
        }

        @Override
        public String post(String url, Object body) {
            System.out.println("POST: " + url + " body: " + body);
            return "Created at " + url;
        }
    }

    @FeignClient(name = "user-service")
    public interface UserFeignClient {
        @GetMapping("/users/{id}")
        User getUser(@PathVariable("id") Long id);

        @PostMapping("/users")
        User createUser(@Body User user);
    }

    // Ribbon Load Balancer
    public static class RibbonLoadBalancer {
        private final List<String> servers = new ArrayList<>();
        private int currentIndex = 0;

        public void addServer(String server) {
            servers.add(server);
        }

        public String getServer() {
            if (servers.isEmpty()) return null;
            String server = servers.get(currentIndex);
            currentIndex = (currentIndex + 1) % servers.size();
            return server;
        }

        public List<String> getAllServers() {
            return new ArrayList<>(servers);
        }
    }

    // Circuit Breaker
    public interface CircuitBreaker {
        void recordSuccess();
        void recordFailure();
        boolean isOpen();
        void reset();
    }

    public static class CircuitBreakerImpl implements CircuitBreaker {
        private int failureCount = 0;
        private int successCount = 0;
        private final int threshold;
        private final long resetTimeoutMs;
        private long lastFailureTime = 0;
        private boolean open = false;

        public CircuitBreakerImpl(int threshold, long resetTimeoutMs) {
            this.threshold = threshold;
            this.resetTimeoutMs = resetTimeoutMs;
        }

        @Override
        public synchronized void recordSuccess() {
            successCount++;
            if (open && successCount >= threshold / 2) {
                close();
            }
        }

        @Override
        public synchronized void recordFailure() {
            failureCount++;
            lastFailureTime = System.currentTimeMillis();
            if (failureCount >= threshold) {
                open = true;
            }
        }

        @Override
        public synchronized boolean isOpen() {
            if (open && System.currentTimeMillis() - lastFailureTime > resetTimeoutMs) {
                close();
            }
            return open;
        }

        @Override
        public synchronized void reset() {
            close();
        }

        private void close() {
            open = false;
            failureCount = 0;
            successCount = 0;
        }
    }

    // Circuit Breaker: Resilience4j style
    public static class ResilienceCircuitBreaker implements CircuitBreaker {
        public enum State { CLOSED, OPEN, HALF_OPEN }
        private State state = State.CLOSED;
        private int failureCount = 0;
        private int successCount = 0;

        public State getState() { return state; }

        @Override
        public void recordSuccess() {
            successCount++;
            if (state == State.HALF_OPEN && successCount > 2) {
                state = State.CLOSED;
            }
        }

        @Override
        public void recordFailure() {
            failureCount++;
            if (failureCount > 3 && state == State.CLOSED) {
                state = State.OPEN;
            }
        }

        @Override
        public boolean isOpen() {
            return state == State.OPEN;
        }

        @Override
        public void reset() {
            state = State.CLOSED;
            failureCount = 0;
            successCount = 0;
        }
    }

    // Service Gateway
    public static class ApiGateway {
        private final Map<String, String> routes = new HashMap<>();
        private final CircuitBreaker circuitBreaker;

        public ApiGateway(CircuitBreaker circuitBreaker) {
            this.circuitBreaker = circuitBreaker;
        }

        public void addRoute(String path, String serviceUrl) {
            routes.put(path, serviceUrl);
        }

        public String route(String path) {
            if (circuitBreaker.isOpen()) {
                return "Circuit breaker open";
            }
            return routes.get(path);
        }
    }

    // Service Mesh
    public static class ServiceMesh {
        private final Map<String, String> serviceInstances = new HashMap<>();

        public void registerInstance(String serviceId, String instanceId, String host, int port) {
            String key = serviceId + ":" + instanceId;
            serviceInstances.put(key, host + ":" + port);
        }

        public String getInstance(String serviceId) {
            for (Map.Entry<String, String> e : serviceInstances.entrySet()) {
                if (e.getKey().startsWith(serviceId + ":")) {
                    return e.getValue();
                }
            }
            return null;
        }
    }

    // Domain Objects
    public static class User {
        private Long id;
        private String name;
        private String email;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class Product {
        private Long id;
        private String name;
        private double price;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }

    // Annotations
    public @interface GetMapping { String value() default ""; }
    public @interface PostMapping { String value() default ""; }
    public @interface PathVariable { String value() default ""; }
    public @interface Body {}

    // Distributed Tracing
    public static class TraceContext {
        private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

        public static void setTraceId(String id) { TRACE_ID.set(id); }
        public static String getTraceId() { return TRACE_ID.get(); }
        public static void clear() { TRACE_ID.remove(); }
    }

    // Service Communication
    public static class ServiceCommunication {
        public CompletableFuture<String> callAsync(String url) {
            return CompletableFuture.supplyAsync(() -> {
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                return "Response from " + url;
            });
        }
    }

    // Config Server
    public static class ConfigServer {
        private final Map<String, Properties> configs = new HashMap<>();

        public void addConfig(String service, Properties props) {
            configs.put(service, props);
        }

        public Properties getConfig(String service) {
            return configs.get(service);
        }
    }

    public static void demonstrateMicroservices() {
        System.out.println("=== Service Discovery ===");
        ServiceRegistry registry = new InMemoryServiceRegistry();
        registry.register("user-service", "http://localhost:8081");
        registry.register("order-service", "http://localhost:8082");
        System.out.println("Services: " + registry.getAllServices());

        System.out.println("\n=== Feign Client ===");
        FeignClient feign = new FeignClientImpl();
        String result = feign.get("http://user-service/users/1");
        System.out.println(result);

        System.out.println("\n=== Load Balancer ===");
        RibbonLoadBalancer lb = new RibbonLoadBalancer();
        lb.addServer("server1:8080");
        lb.addServer("server2:8080");
        lb.addServer("server3:8080");
        System.out.println("Server 1: " + lb.getServer());
        System.out.println("Server 2: " + lb.getServer());

        System.out.println("\n=== Circuit Breaker ===");
        CircuitBreaker cb = new CircuitBreakerImpl(3, 5000);
        for (int i = 0; i < 3; i++) cb.recordFailure();
        System.out.println("Circuit open: " + cb.isOpen());

        System.out.println("\n=== API Gateway ===");
        CircuitBreaker circuit = new CircuitBreakerImpl(5, 60000);
        ApiGateway gateway = new ApiGateway(circuit);
        gateway.addRoute("/users/**", "http://user-service");
        System.out.println("Route: " + gateway.route("/users/1"));
    }

    public static void main(String[] args) {
        demonstrateMicroservices();
    }
}