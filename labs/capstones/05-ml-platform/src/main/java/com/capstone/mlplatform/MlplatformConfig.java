package com.capstone.mlplatform;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record MlplatformConfig(String name, int maxWorkers, int queueSize, boolean enableMetrics, Map<String, String> settings) {
    public MlplatformConfig {
        settings = Map.copyOf(settings != null ? settings : new ConcurrentHashMap<>());
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
        if (maxWorkers < 1) throw new IllegalArgumentException("maxWorkers >= 1");
        if (queueSize < 1) throw new IllegalArgumentException("queueSize >= 1");
    }
    public static MlplatformConfig defaults(String name) {
        return new MlplatformConfig(name, 4, 1000, true, Map.of("timeout","30000","retries","3"));
    }
    public String getSetting(String key, String def) { return settings.getOrDefault(key, def); }
}