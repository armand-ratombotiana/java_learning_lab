package com.systemdesign.ratelimit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record RatelimitConfig(String serviceName, int maxConnections, int timeoutMs, boolean enableCaching, Map<String, String> properties) {
    public RatelimitConfig {
        properties = Map.copyOf(properties != null ? properties : new ConcurrentHashMap<>());
    }
    public static RatelimitConfig defaults(String name) {
        return new RatelimitConfig(name, 100, 5000, true, Map.of("region", "us-east-1", "env", "dev"));
    }
    public String getProperty(String key, String def) { return properties.getOrDefault(key, def); }
}