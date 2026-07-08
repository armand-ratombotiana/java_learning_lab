package com.devops.fourteen;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class HelmManager {
    private final String name;
    private final Map<String, String> config = new ConcurrentHashMap<>();
    private volatile boolean initialized = false;

    public HelmManager(String name) {
        this.name = name;
    }

    public void setConfig(String key, String value) {
        config.put(key, value);
    }

    public String getConfig(String key) {
        return config.get(key);
    }

    public boolean initialize() {
        System.out.printf("Initializing HelmManager '%s' with %d config entries%n", name, config.size());
        initialized = true;
        return true;
    }

    public boolean validate() {
        if (!initialized) {
            System.err.println("Not initialized");
            return false;
        }
        System.out.printf("Validating HelmManager '%s': all checks passed%n", name);
        return true;
    }

    public void shutdown() {
        initialized = false;
        System.out.printf("Shutting down HelmManager '%s'%n", name);
    }

    public boolean isInitialized() { return initialized; }
    public String getName() { return name; }
    public Map<String, String> getConfig() { return Map.copyOf(config); }

    public static void main(String[] args) {
        HelmManager manager = new HelmManager("HelmDemo");
        manager.setConfig("version", "1.0.0");
        manager.setConfig("environment", "production");
        manager.initialize();
        System.out.println("Validation: " + manager.validate());
        manager.shutdown();
    }
}
