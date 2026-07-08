package com.systemdesign.chatsystem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record ChatsystemConfig(String serviceName, int maxConnections, int timeoutMs, boolean enableCaching, Map<String, String> properties) {
    public ChatsystemConfig {
        properties = Map.copyOf(properties != null ? properties : new ConcurrentHashMap<>());
    }
    public static ChatsystemConfig defaults(String name) {
        return new ChatsystemConfig(name, 100, 5000, true, Map.of("region", "us-east-1", "env", "dev"));
    }
    public String getProperty(String key, String def) { return properties.getOrDefault(key, def); }
}