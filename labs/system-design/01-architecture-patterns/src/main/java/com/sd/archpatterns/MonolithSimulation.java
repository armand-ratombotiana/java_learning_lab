package com.sd.archpatterns;

import java.util.*;
import java.util.concurrent.*;

public class MonolithSimulation {

    public static class MonolithicApp {
        private final Map<String, Object> userDb = new ConcurrentHashMap<>();
        private final Map<String, Object> orderDb = new ConcurrentHashMap<>();
        private final Map<String, Object> productDb = new ConcurrentHashMap<>();

        public String createUser(String name, String email) {
            String id = UUID.randomUUID().toString();
            userDb.put(id, Map.of("id", id, "name", name, "email", email));
            System.out.println("[Monolith] Created user: " + name);
            return id;
        }

        public String createOrder(String userId, String productId) {
            String id = UUID.randomUUID().toString();
            orderDb.put(id, Map.of("id", id, "userId", userId, "productId", productId));
            System.out.println("[Monolith] Created order: " + id);
            return id;
        }

        public String addProduct(String name, double price) {
            String id = UUID.randomUUID().toString();
            productDb.put(id, Map.of("id", id, "name", name, "price", price));
            System.out.println("[Monolith] Added product: " + name);
            return id;
        }

        public Map<String, Object> getDashboard(String userId) {
            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("user", userDb.values());
            dashboard.put("products", productDb.values());
            dashboard.put("orders", orderDb.values());
            return dashboard;
        }

        public int getTotalDeploySize() {
            return userDb.size() + orderDb.size() + productDb.size();
        }
    }

    public static void main(String[] args) {
        MonolithicApp app = new MonolithicApp();

        app.addProduct("Laptop", 999.99);
        app.addProduct("Mouse", 29.99);

        String userId = app.createUser("Alice", "alice@test.com");
        app.createOrder(userId, "prod-1");

        System.out.println("\nDashboard: " + app.getDashboard(userId));
        System.out.println("Deployment: single deployable unit");
    }
}
