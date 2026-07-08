package com.arch.stranglerfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class NewSystemModule implements MigrationTarget {
    private final Map<String, String> database = new ConcurrentHashMap<>();

    public NewSystemModule() {
        database.put("user_1", "{\"name\":\"Alice\",\"email\":\"alice@new.com\"}");
        database.put("user_3", "{\"name\":\"Charlie\",\"email\":\"charlie@new.com\"}");
    }

    @Override
    public String getUser(String userId) {
        return database.getOrDefault(userId, "{}");
    }

    @Override
    public String createUser(String userId, String userData) {
        database.put(userId, userData);
        return userData;
    }

    @Override
    public String updateUser(String userId, String userData) {
        if (!database.containsKey(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        database.put(userId, userData);
        return userData;
    }

    @Override
    public boolean deleteUser(String userId) {
        return database.remove(userId) != null;
    }

    @Override
    public boolean isHealthy() {
        return true;
    }

    public int getUserCount() {
        return database.size();
    }
}
