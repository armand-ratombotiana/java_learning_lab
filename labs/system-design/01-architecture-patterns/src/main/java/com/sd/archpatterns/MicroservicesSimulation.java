package com.sd.archpatterns;

import java.util.*;
import java.util.concurrent.*;

public class MicroservicesSimulation {

    public interface Microservice {
        String getName();
        Object handleRequest(String action, Map<String, Object> params);
    }

    public static class UserService implements Microservice {
        private final Map<String, Object> db = new ConcurrentHashMap<>();

        @Override
        public String getName() { return "user-service"; }

        @Override
        public Object handleRequest(String action, Map<String, Object> params) {
            return switch (action) {
                case "create" -> {
                    String id = UUID.randomUUID().toString();
                    db.put(id, params);
                    yield Map.of("id", id, "status", "created");
                }
                case "get" -> db.get(params.get("id"));
                default -> Map.of("error", "Unknown action");
            };
        }
    }

    public static class OrderService implements Microservice {
        private final Map<String, Object> db = new ConcurrentHashMap<>();

        @Override
        public String getName() { return "order-service"; }

        @Override
        public Object handleRequest(String action, Map<String, Object> params) {
            return switch (action) {
                case "create" -> {
                    String id = UUID.randomUUID().toString();
                    db.put(id, params);
                    yield Map.of("id", id, "status", "created");
                }
                case "get" -> db.get(params.get("id"));
                default -> Map.of("error", "Unknown action");
            };
        }
    }

    public static class ApiGateway {
        private final Map<String, Microservice> services = new HashMap<>();

        public void register(Microservice svc) {
            services.put(svc.getName(), svc);
            System.out.println("Registered: " + svc.getName());
        }

        public Object route(String serviceName, String action, Map<String, Object> params) {
            Microservice svc = services.get(serviceName);
            if (svc == null) return Map.of("error", "Service not found: " + serviceName);
            System.out.println("API Gateway routing to " + serviceName + "/" + action);
            return svc.handleRequest(action, params);
        }
    }

    public static void main(String[] args) {
        ApiGateway gateway = new ApiGateway();
        gateway.register(new UserService());
        gateway.register(new OrderService());

        Map<String, Object> user = (Map<String, Object>) gateway.route("user-service", "create",
            Map.of("name", "Alice", "email", "alice@test.com"));
        System.out.println("Created: " + user);

        Map<String, Object> order = (Map<String, Object>) gateway.route("order-service", "create",
            Map.of("userId", user.get("id"), "product", "Laptop"));
        System.out.println("Order: " + order);

        Map<String, Object> error = (Map<String, Object>) gateway.route("unknown-svc", "get", Map.of());
        System.out.println("Error: " + error);
    }
}
