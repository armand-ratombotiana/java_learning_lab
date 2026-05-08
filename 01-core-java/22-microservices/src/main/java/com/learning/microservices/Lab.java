package com.learning.microservices;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Microservices Architecture Lab ===\n");

        serviceDecomposition();
        serviceRegistry();
        circuitBreaker();
        apiGateway();
        eventDrivenCommunication();
    }

    static void serviceDecomposition() {
        System.out.println("--- Service Decomposition ---");

        record Service(String name, String domain, List<String> caps) {}
        var services = List.of(
            new Service("user-svc", "Identity", List.of("register","login","profile")),
            new Service("order-svc", "Orders", List.of("create","track","cancel")),
            new Service("payment-svc", "Billing", List.of("charge","refund")),
            new Service("inventory-svc", "Stock", List.of("reserve","release")),
            new Service("notification-svc", "Alerts", List.of("email","sms","push"))
        );
        System.out.printf("%-22s %-15s %s%n", "Service", "Domain", "Capabilities");
        System.out.println("-".repeat(60));
        services.forEach(s -> System.out.printf("%-22s %-15s %s%n", s.name(), s.domain(), String.join(", ", s.caps())));

        System.out.println("\n  DDD tactical patterns:");
        for (var d : List.of("Entity (User, Order)", "Value Object (Money, Address)",
                "Aggregate (consistency boundary)", "Repository (aggregate retrieval)",
                "Domain Event (something happened)"))
            System.out.println("  " + d);
    }

    static void serviceRegistry() {
        System.out.println("\n--- Service Registry & Discovery ---");

        class Registry {
            final ConcurrentHashMap<String, List<String>> store = new ConcurrentHashMap<>();

            void register(String id, String addr) {
                store.computeIfAbsent(id, k -> new CopyOnWriteArrayList<>()).add(addr);
                System.out.printf("  Registered %s at %s%n", id, addr);
            }
            List<String> discover(String id) { return store.getOrDefault(id, List.of()); }
            void healthCheck() { store.forEach((id, addrs) -> System.out.printf("  %s: %d instance(s)%n", id, addrs.size())); }
        }

        var reg = new Registry();
        reg.register("user-svc", "192.168.1.10:8081");
        reg.register("order-svc", "192.168.1.11:8082");
        reg.register("order-svc", "192.168.1.12:8082");
        reg.healthCheck();
        System.out.println("  order-svc instances: " + reg.discover("order-svc"));
    }

    static void circuitBreaker() {
        System.out.println("\n--- Circuit Breaker ---");

        enum State { CLOSED, OPEN, HALF_OPEN }

        class CB {
            State state = State.CLOSED;
            int failures = 0;
            final int threshold = 3;
            final long timeoutMs = 2000;
            long lastFail = 0;

            <T> T call(Supplier<T> op, Supplier<T> fallback) {
                if (state == State.OPEN) {
                    if (System.currentTimeMillis() - lastFail > timeoutMs) {
                        state = State.HALF_OPEN;
                        System.out.println("  HALF_OPEN: trying");
                    } else {
                        System.out.println("  OPEN: fallback used");
                        return fallback.get();
                    }
                }
                try {
                    T r = op.get();
                    if (state == State.HALF_OPEN) { state = State.CLOSED; failures = 0; }
                    return r;
                } catch (Exception e) {
                    failures++; lastFail = System.currentTimeMillis();
                    if (failures >= threshold) state = State.OPEN;
                    return fallback.get();
                }
            }
        }

        var cb = new CB();
        var fail = new Supplier<String>() { int c = 0;
            public String get() { if (++c <= 3) throw new RuntimeException("fail"); return "OK"; } };
        var fb = (Supplier<String>) () -> "fallback";

        for (int i = 1; i <= 5; i++) System.out.printf("  Call %d: %s%n", i, cb.call(fail, fb));
    }

    static void apiGateway() {
        System.out.println("\n--- API Gateway ---");

        record Route(String path, String target, String method) {}

        class Gateway {
            final List<Route> routes = new ArrayList<>();
            void addRoute(Route r) { routes.add(r); }
            void process(String path, String method) {
                System.out.printf("  %s %s -> ", method, path);
                var r = routes.stream().filter(x -> path.startsWith(x.path()) && x.method().equals(method)).findFirst();
                r.ifPresentOrElse(x -> System.out.println("route to " + x.target()),
                    () -> System.out.println("404 Not Found"));
            }
        }

        var gw = new Gateway();
        gw.addRoute(new Route("/api/users", "user-svc", "GET"));
        gw.addRoute(new Route("/api/orders", "order-svc", "POST"));
        gw.process("/api/users", "GET");
        gw.process("/api/unknown", "GET");
    }

    static void eventDrivenCommunication() {
        System.out.println("\n--- Event-Driven Communication ---");

        class EventBus {
            final Map<String, List<Consumer<String>>> subs = new ConcurrentHashMap<>();
            void sub(String type, Consumer<String> h) { subs.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(h); }
            void pub(String type, String data) {
                System.out.println("  Event: " + type + " -> " + data);
                var handlers = subs.get(type);
                if (handlers != null) handlers.forEach(h -> h.accept(data));
            }
        }

        var bus = new EventBus();
        bus.sub("OrderPlaced", d -> System.out.println("    Inventory: reserve " + d));
        bus.sub("OrderPlaced", d -> System.out.println("    Payment: charge " + d));
        bus.sub("OrderPlaced", d -> System.out.println("    Notification: confirm " + d));
        bus.pub("OrderPlaced", "order-12345");

        System.out.println("\n  Event-driven patterns:");
        for (var p : List.of("Event Sourcing", "CQRS", "Saga (distributed tx)", "Outbox Pattern"))
            System.out.println("  " + p);
    }
}
