package com.arch.bff;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MobileBff implements BffGateway {
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private final String userId;
    private static final int MAX_PAYLOAD_SIZE = 1024;

    public MobileBff(String userId) {
        this.userId = userId;
    }

    @Override
    public String getUserId() { return userId; }

    @Override
    public Map<String, Object> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("summary", getCompactSummary());
        dashboard.put("notifications", getRecentNotifications(5));
        dashboard.put("quick_actions", getQuickActions());
        return dashboard;
    }

    @Override
    public Map<String, Object> getUserProfile(String uid) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", uid);
        profile.put("name", "User_" + uid);
        profile.put("avatar_url", "/avatars/" + uid + ".jpg");
        profile.put("member_since", "2024-01-15");
        return profile;
    }

    @Override
    public boolean submitOrder(String productId, int quantity) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product ID required");
        }
        if (quantity <= 0 || quantity > 10) {
            throw new IllegalArgumentException("Invalid quantity");
        }
        return true;
    }

    @Override
    public String getClientType() { return "mobile"; }

    private Map<String, Object> getCompactSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("orders", 12);
        summary.put("cart_items", 3);
        summary.put("unread_messages", 2);
        return summary;
    }

    private java.util.List<String> getRecentNotifications(int count) {
        return java.util.List.of("Order shipped", "Payment received", "New offer available");
    }

    private java.util.List<String> getQuickActions() {
        return java.util.List.of("Track Order", "Reorder", "Support");
    }
}
