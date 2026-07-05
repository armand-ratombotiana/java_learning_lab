package com.devops.configmgmt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager {
    private final Map<String, String> config = new ConcurrentHashMap<>();

    public void set(String key, String value) {
        config.put(key, value);
        System.out.println("Config set: " + key + " = " + value);
    }

    public String get(String key) {
        return config.get(key);
    }

    public void loadFromGitOps(Map<String, String> desiredState) {
        System.out.println("Syncing config from GitOps repository...");
        for (Map.Entry<String, String> entry : desiredState.entrySet()) {
            String current = config.get(entry.getKey());
            if (!entry.getValue().equals(current)) {
                config.put(entry.getKey(), entry.getValue());
                System.out.println("  Updated: " + entry.getKey() + " -> " + entry.getValue());
            }
        }
    }

    public static void main(String[] args) {
        ConfigManager cm = new ConfigManager();

        Map<String, String> gitOpsState = Map.of(
            "app.version", "2.1.0",
            "app.debug", "false",
            "db.maxConnections", "100"
        );

        cm.loadFromGitOps(gitOpsState);
        System.out.println("Current config version: " + cm.get("app.version"));
    }
}
