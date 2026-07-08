package com.arch.bff;

import java.util.HashMap;
import java.util.Map;

public final class WebBff implements BffGateway {
    private final String userId;

    public WebBff(String userId) {
        this.userId = userId;
    }

    @Override
    public String getUserId() { return userId; }

    @Override
    public Map<String, Object> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("full_summary", getFullSummary());
        dashboard.put("recent_orders", getRecentOrders(20));
        dashboard.put("recommendations", getRecommendations());
        dashboard.put("analytics", getAnalytics());
        dashboard.put("notifications", getAllNotifications());
        return dashboard;
    }

    @Override
    public Map<String, Object> getUserProfile(String uid) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", uid);
        profile.put("name", "User_" + uid);
        profile.put("email", uid + "@example.com");
        profile.put("phone", "+1-555-0100");
        profile.put("address", getAddress());
        profile.put("preferences", getPreferences());
        profile.put("payment_methods", getPaymentMethods());
        profile.put("order_history_url", "/api/users/" + uid + "/orders");
        return profile;
    }

    @Override
    public boolean submitOrder(String productId, int quantity) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product ID required");
        }
        if (quantity <= 0 || quantity > 100) {
            throw new IllegalArgumentException("Quantity must be 1-100");
        }
        return true;
    }

    @Override
    public String getClientType() { return "web"; }

    private Map<String, Object> getFullSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("total_orders", 47);
        summary.put("total_spent", 1250.00);
        summary.put("loyalty_points", 2340);
        summary.put("membership_tier", "Gold");
        return summary;
    }

    private java.util.List<Map<String, Object>> getRecentOrders(int limit) {
        return java.util.List.of(Map.of("id", "ORD-001", "status", "delivered"));
    }

    private java.util.List<String> getRecommendations() {
        return java.util.List.of("Product A", "Product B", "Product C");
    }

    private Map<String, Object> getAnalytics() {
        return Map.of("visits_this_week", 15, "page_views", 142);
    }

    private java.util.List<String> getAllNotifications() {
        return java.util.List.of("Order shipped", "Payment received", "Price drop alert", "Review reminder");
    }

    private Map<String, String> getAddress() {
        return Map.of("street", "123 Main St", "city", "Springfield");
    }

    private Map<String, Object> getPreferences() {
        return Map.of("theme", "dark", "language", "en", "newsletter", true);
    }

    private java.util.List<String> getPaymentMethods() {
        return java.util.List.of("Visa ending in 1234", "PayPal");
    }
}
