package com.arch.stranglerfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LegacySystem {
    private final Map<String, String> database = new ConcurrentHashMap<>();

    public LegacySystem() {
        database.put("user_1", "{\"name\":\"Alice\",\"email\":\"alice@legacy.com\"}");
        database.put("user_2", "{\"name\":\"Bob\",\"email\":\"bob@legacy.com\"}");
    }

    public String getUser(String userId) {
        simulateLatency(50);
        return database.getOrDefault(userId, "{}");
    }

    public String createUser(String userId, String userData) {
        simulateLatency(100);
        database.put(userId, userData);
        return userData;
    }

    public String updateUser(String userId, String userData) {
        simulateLatency(75);
        if (!database.containsKey(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        database.put(userId, userData);
        return userData;
    }

    public boolean deleteUser(String userId) {
        simulateLatency(50);
        return database.remove(userId) != null;
    }

    public int getUserCount() {
        return database.size();
    }

    public boolean isHealthy() {
        return true;
    }

    private void simulateLatency(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
