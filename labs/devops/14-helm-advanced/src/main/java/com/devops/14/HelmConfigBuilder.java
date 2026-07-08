package com.devops.fourteen;

import java.util.LinkedHashMap;
import java.util.Map;

public class HelmConfigBuilder {
    private final Map<String, Object> config = new LinkedHashMap<>();

    public HelmConfigBuilder set(String key, Object value) {
        config.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public HelmConfigBuilder setNested(String keyPath, Object value) {
        String[] parts = keyPath.split("\\.");
        Map<String, Object> current = config;
        for (int i = 0; i < parts.length - 1; i++) {
            current.computeIfAbsent(parts[i], k -> new LinkedHashMap<>());
            current = (Map<String, Object>) current.get(parts[i]);
        }
        current.put(parts[parts.length - 1], value);
        return this;
    }

    public String render() {
        StringBuilder sb = new StringBuilder();
        renderMap(sb, config, 0);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private void renderMap(StringBuilder sb, Map<String, Object> map, int indent) {
        String prefix = "  ".repeat(indent);
        for (var entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                sb.append(prefix).append(entry.getKey()).append(":\n");
                renderMap(sb, (Map<String, Object>) entry.getValue(), indent + 1);
            } else if (entry.getValue() instanceof String) {
                sb.append(prefix).append(entry.getKey()).append(": \"").append(entry.getValue()).append("\"\n");
            } else {
                sb.append(prefix).append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }
    }

    public static void main(String[] args) {
        HelmConfigBuilder builder = new HelmConfigBuilder()
            .set("version", "1.0.0")
            .set("enabled", true)
            .setNested("service.name", "HelmService")
            .setNested("service.port", 8080);
        System.out.println(builder.render());
    }
}
