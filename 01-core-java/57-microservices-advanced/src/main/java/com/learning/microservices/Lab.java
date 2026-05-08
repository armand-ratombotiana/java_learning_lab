package com.learning.microservices;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public class Lab {

    record ServiceInstance(String id, String host, int port, boolean healthy) {}

    static class ServiceRegistry {
        private final ConcurrentHashMap<String, List<ServiceInstance>> services = new ConcurrentHashMap<>();

        void register(String name, ServiceInstance instance) {
            services.computeIfAbsent(name, k -> new CopyOnWriteArrayList<>()).add(instance);
        }

        List<ServiceInstance> discover(String name) {
            return services.getOrDefault(name, List.of()).stream()
                .filter(ServiceInstance::healthy).toList();
        }

        void report() {
            services.forEach((name, instances) -> {
                System.out.println("    " + name + ": " + instances.stream()
                    .map(i -> i.id() + "@" + i.host() + ":" + i.port())
                    .toList());
            });
        }
    }

    static class CircuitBreaker {
        private final AtomicInteger failures = new AtomicInteger(0);
        private final int threshold;
        private volatile boolean open = false;
        private volatile long openSince;

        CircuitBreaker(int threshold) { this.threshold = threshold; }

        <T> T call(Supplier<T> operation, Supplier<T> fallback) {
            if (open) {
                if (System.currentTimeMillis() - openSince > 5000) {
                    open = false;
                    failures.set(0);
                } else {
                    return fallback.get();
                }
            }
            try {
                var result = operation.get();
                failures.set(0);
                return result;
            } catch (Exception e) {
                if (failures.incrementAndGet() >= threshold) {
                    open = true;
                    openSince = System.currentTimeMillis();
                }
                return fallback.get();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Microservices Advanced Lab ===\n");

        serviceDiscovery();
        apiComposition();
        sagaPattern();
        circuitBreakerPattern();
        eventDrivenComms();
        observability();
    }

    static void serviceDiscovery() {
        System.out.println("--- Service Discovery ---");
        var registry = new ServiceRegistry();

        registry.register("user-service", new ServiceInstance("u1", "10.0.0.1", 8081, true));
        registry.register("user-service", new ServiceInstance("u2", "10.0.0.2", 8081, true));
        registry.register("order-service", new ServiceInstance("o1", "10.0.0.3", 8082, true));

        System.out.println("  Registered:");
        registry.report();

        var users = registry.discover("user-service");
        System.out.println("  Discovered user-service: " + users.size() + " instances");

        System.out.println("""
  Client-side: Ribbon, Spring Cloud LoadBalancer
  Server-side: Kubernetes Service, Consul, Eureka
  Health checks: /actuator/health (Spring) or /health (K8s)
    """);
    }

    static void apiComposition() {
        System.out.println("\n--- API Composition ---");
        record Order(int id, String userId, double total) {}
        record User(String id, String name) {}
        record OrderDetail(Order order, User user) {}

        var users = Map.of("u1", new User("u1", "Alice"), "u2", new User("u2", "Bob"));
        var orders = List.of(
            new Order(1, "u1", 250.00), new Order(2, "u2", 150.00), new Order(3, "u1", 99.99));

        var details = orders.stream()
            .map(o -> new OrderDetail(o, users.get(o.userId())))
            .toList();

        System.out.println("  Composed order details:");
        details.forEach(d -> System.out.printf("    Order %d: %s $%.2f (by %s)%n",
            d.order().id(), d.user().name(), d.order().total(), d.user().id()));
        System.out.println("  API Gateway fan-out to multiple services");
    }

    static void sagaPattern() {
        System.out.println("\n--- Saga Pattern (Choreography) ---");
        System.out.println("""
  Choreography saga:
    Order Service: Create order -> emit OrderCreated
    Payment Svc:  Consume OrderCreated -> process payment -> emit PaymentProcessed
    Inventory Svc: Consume PaymentProcessed -> reserve stock -> emit InventoryReserved

  Compensation (rollback on failure):
    Payment failed -> emit PaymentFailed
    Order Svc: consume PaymentFailed -> cancel order
    Inventory Svc: consume OrderCancelled -> release stock

  Orchestration saga:
    OrderSagaOrchestrator coordinates:
    1. Call Payment Svc -> if fails, call compensate
    2. Call Inventory Svc -> if fails, call compensate
    Each service only does its job + compensate
    """);
    }

    static void circuitBreakerPattern() {
        System.out.println("\n--- Circuit Breaker ---");
        var cb = new CircuitBreaker(2);
        var failCount = new AtomicInteger(0);

        for (int i = 0; i < 8; i++) {
            var result = cb.call(() -> {
                if (failCount.incrementAndGet() <= 3) throw new RuntimeException("Timeout");
                return "Success";
            }, () -> "Fallback response");
            System.out.println("  [Request " + i + "] Result: " + result);
        }

        System.out.println("""
  States: CLOSED -> OPEN -> HALF_OPEN -> CLOSED
  Resilience4j: @CircuitBreaker, @Retry, @Bulkhead, @RateLimiter
  Hystrix: Netflix's circuit breaker (maintenance mode)
    """);
    }

    static void eventDrivenComms() {
        System.out.println("\n--- Event-Driven Communication ---");
        class EventBus {
            private final Map<String, List<Consumer<String>>> listeners = new ConcurrentHashMap<>();

            void on(String event, Consumer<String> handler) {
                listeners.computeIfAbsent(event, k -> new CopyOnWriteArrayList<>()).add(handler);
            }

            void emit(String event, String data) {
                var handlers = listeners.get(event);
                if (handlers != null) handlers.forEach(h -> h.accept(data));
            }
        }

        var bus = new EventBus();
        bus.on("UserRegistered", data -> System.out.println("  Email Svc: send welcome to " + data));
        bus.on("UserRegistered", data -> System.out.println("  Analytics: track signup " + data));
        bus.on("UserRegistered", data -> System.out.println("  CRM: create lead for " + data));

        System.out.println("  Event 'UserRegistered' emitted for 'alice@test.com':");
        bus.emit("UserRegistered", "alice@test.com");
        System.out.println("""
  Async communication via: Kafka, RabbitMQ, NATS, EventBridge
  Event schema: CloudEvents, Avro, Protobuf
  Eventual consistency in event-driven systems
    """);
    }

    static void observability() {
        System.out.println("\n--- Observability ---");
        System.out.println("""
  Three pillars:

  Logging:
  - Structured JSON logs
  - Correlation ID (traceId) in MDC
  - Log aggregation: Elasticsearch + Kibana / Loki + Grafana

  Metrics:
  - RED metrics: Rate, Errors, Duration (per service)
  - USE metrics: Utilization, Saturation, Errors (per resource)
  - Prometheus + Grafana dashboards

  Tracing:
  - Distributed trace across service boundaries
  - OpenTelemetry SDK + collector
  - Sampling strategy: head-based, tail-based

  Health endpoints:
  /health, /info, /metrics, /prometheus (Spring Actuator)
    """);
    }
}
