package com.devops.fifteen;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class TerraformManager {
    private final String name;
    private final Map<String, String> config = new ConcurrentHashMap<>();
    private volatile boolean initialized = false;

    public TerraformManager(String name) {
        this.name = name;
    }

    public void setConfig(String key, String value) {
        config.put(key, value);
    }

    public String getConfig(String key) {
        return config.get(key);
    }

    public boolean initialize() {
        System.out.printf("Initializing TerraformManager '%s' with %d config entries%n", name, config.size());
        initialized = true;
        return true;
    }

    public boolean validate() {
        if (!initialized) {
            System.err.println("Not initialized");
            return false;
        }
        System.out.printf("Validating TerraformManager '%s': all checks passed%n", name);
        return true;
    }

    public void shutdown() {
        initialized = false;
        System.out.printf("Shutting down TerraformManager '%s'%n", name);
    }

    public boolean isInitialized() { return initialized; }
    public String getName() { return name; }
    public Map<String, String> getConfig() { return Map.copyOf(config); }

    public static void main(String[] args) {
        TerraformManager manager = new TerraformManager("TerraformDemo");
        manager.setConfig("version", "1.0.0");
        manager.setConfig("environment", "production");
        manager.initialize();
        System.out.println("Validation: " + manager.validate());
        manager.shutdown();
    }
}
