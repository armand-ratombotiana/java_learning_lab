package com.learning.micro.architecture;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class ArchitecturePatternsLab {

    static class Service {
        final String name;
        final String database;
        final List<String> dependencies;

        Service(String name, String database, String... dependencies) {
            this.name = name;
            this.database = database;
            this.dependencies = List.of(dependencies);
        }
    }

    static class Event {
        final String id;
        final String type;
        final Map<String, Object> data;
        final long timestamp;

        Event(String type, Map<String, Object> data) {
            this.id = UUID.randomUUID().toString();
            this.type = type;
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }
    }

    static class EventBus {
        private final Map<String, List<Consumer<Event>>> subscribers = new ConcurrentHashMap<>();

        public void publish(Event event) {
            List<Consumer<Event>> handlers = subscribers.get(event.type);
            if (handlers != null) {
                handlers.forEach(h -> h.accept(event));
            }
        }

        public void subscribe(String eventType, Consumer<Event> handler) {
            subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
        }
    }

    static class SagaOrchestrator {
        private final Map<String, List<CompensatingAction>> sagas = new ConcurrentHashMap<>();
        private final EventBus eventBus;

        SagaOrchestrator(EventBus eventBus) { this.eventBus = eventBus; }

        record CompensatingAction(String stepName, Runnable compensate) {}

        public void registerSaga(String sagaName, List<CompensatingAction> steps) {
            sagas.put(sagaName, steps);
        }

        public void execute(String sagaName) {
            List<CompensatingAction> steps = sagas.get(sagaName);
            if (steps == null) return;

            List<CompensatingAction> executed = new ArrayList<>();
            try {
                for (CompensatingAction step : steps) {
                    System.out.println("  Executing: " + step.stepName());
                    executed.add(step);
                }
                System.out.println("  Saga completed successfully");
            } catch (Exception e) {
                System.out.println("  Saga failed, compensating...");
                Collections.reverse(executed);
                executed.forEach(c -> {
                    System.out.println("  Compensating: " + c.stepName());
                    c.compensate().run();
                });
            }
        }
    }

    static class CircuitBreaker {
        enum State { CLOSED, OPEN, HALF_OPEN }

        private State state = State.CLOSED;
        private int failures = 0;
        private final int threshold;
        private long lastFailureTime;
        private final long timeoutMs;

        CircuitBreaker(int threshold, long timeoutMs) {
            this.threshold = threshold;
            this.timeoutMs = timeoutMs;
        }

        public synchronized <T> T call(Callable<T> operation, T fallback) {
            if (state == State.OPEN) {
                if (System.currentTimeMillis() - lastFailureTime > timeoutMs) {
                    state = State.HALF_OPEN;
                } else {
                    System.out.println("  [CB] Circuit OPEN, returning fallback");
                    return fallback;
                }
            }
            try {
                T result = operation.call();
                if (state == State.HALF_OPEN) {
                    state = State.CLOSED;
                    failures = 0;
                }
                return result;
            } catch (Exception e) {
                failures++;
                lastFailureTime = System.currentTimeMillis();
                if (failures >= threshold) state = State.OPEN;
                return fallback;
            }
        }
    }

    static class ApiGateway {
        private final Map<String, String> routes = new ConcurrentHashMap<>();
        private final Map<String, Long> rateLimits = new ConcurrentHashMap<>();

        public void addRoute(String path, String target) { routes.put(path, target); }

        public String route(String path) {
            String target = routes.get(path);
            if (target == null) return "404 Not Found";

            long now = System.currentTimeMillis() / 1000;
            Long lastCall = rateLimits.get(path);
            if (lastCall != null && (now - lastCall) < 1) {
                return "429 Too Many Requests";
            }
            rateLimits.put(path, now);
            return "Routed to " + target;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Microservices Architecture Patterns Lab ===\n");

        decompositionPatterns();
        databasePerService();
        eventDrivenArchitecture();
        sagaPattern();
        apiGatewayPattern();
    }

    static void decompositionPatterns() {
        System.out.println("--- Decomposition Strategies ---");
        List<Service> services = List.of(
            new Service("User Service", "users_db"),
            new Service("Order Service", "orders_db", "User Service", "Inventory Service"),
            new Service("Inventory Service", "inventory_db"),
            new Service("Payment Service", "payments_db", "Order Service"),
            new Service("Notification Service", "notifications_db", "User Service")
        );

        System.out.println("  Service boundaries:");
        services.forEach(s -> System.out.println("    " + s.name + " -> " + s.database
            + (s.dependencies.isEmpty() ? "" : " (depends on: " + s.dependencies + ")")));
        System.out.println("  Patterns: Domain-driven design, subdomain decomposition");
    }

    static void databasePerService() {
        System.out.println("\n--- Database per Service ---");
        Map<String, String> schemas = new LinkedHashMap<>();
        schemas.put("users", "users_db (users, profiles)");
        schemas.put("orders", "orders_db (orders, order_items)");
        schemas.put("inventory", "inventory_db (products, stock)");
        schemas.put("payments", "payments_db (transactions, invoices)");

        schemas.forEach((svc, db) -> System.out.println("  " + svc + ": " + db));
        System.out.println("  No direct joins across services; data is accessed via APIs");
    }

    static void eventDrivenArchitecture() {
        System.out.println("\n--- Event-Driven Architecture ---");
        EventBus eventBus = new EventBus();

        eventBus.subscribe("OrderPlaced", e -> {
            System.out.println("  [Inventory] Reserved stock for order " + e.data.get("orderId"));
        });
        eventBus.subscribe("OrderPlaced", e -> {
            System.out.println("  [Payment] Charged customer for order " + e.data.get("orderId"));
        });
        eventBus.subscribe("OrderPlaced", e -> {
            System.out.println("  [Notification] Emailed confirmation for order " + e.data.get("orderId"));
        });

        Event orderEvent = new Event("OrderPlaced", Map.of("orderId", "ORD-1001", "total", 250.00));
        eventBus.publish(orderEvent);
        System.out.println("  Services react asynchronously to the same event");
    }

    static void sagaPattern() {
        System.out.println("\n--- Saga Pattern (Orchestration) ---");
        EventBus eventBus = new EventBus();
        SagaOrchestrator orchestrator = new SagaOrchestrator(eventBus);

        List<SagaOrchestrator.CompensatingAction> orderSteps = List.of(
            new SagaOrchestrator.CompensatingAction("Reserve Inventory", () ->
                System.out.println("    Compensating: release inventory")),
            new SagaOrchestrator.CompensatingAction("Process Payment", () ->
                System.out.println("    Compensating: refund payment")),
            new SagaOrchestrator.CompensatingAction("Update Order Status", () ->
                System.out.println("    Compensating: cancel order"))
        );

        orchestrator.registerSaga("createOrder", orderSteps);

        System.out.println("  Successful saga:");
        orchestrator.execute("createOrder");

        System.out.println("\n  (Real sagas use choreography or orchestration to maintain consistency)");
    }

    static void apiGatewayPattern() {
        System.out.println("\n--- API Gateway Pattern ---");
        ApiGateway gateway = new ApiGateway();
        gateway.addRoute("/api/users", "user-service:8080");
        gateway.addRoute("/api/orders", "order-service:8081");
        gateway.addRoute("/api/products", "inventory-service:8082");

        System.out.println("  " + gateway.route("/api/users"));
        System.out.println("  " + gateway.route("/api/orders"));
        System.out.println("  " + gateway.route("/api/unknown"));

        System.out.println("  Gateway handles routing, rate limiting, auth aggregation");
    }
}
