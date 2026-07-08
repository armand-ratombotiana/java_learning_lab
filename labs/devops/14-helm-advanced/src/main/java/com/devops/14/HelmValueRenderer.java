package com.devops.fourteen;

import java.util.*;

public class HelmValueRenderer {
    private final Map<String, Object> globalValues = new LinkedHashMap<>();

    public void setGlobalValue(String key, Object value) { globalValues.put(key, value); }

    public String renderValues(Map<String, Object> overrides) {
        Map<String, Object> merged = mergeValues(globalValues, overrides);
        return renderYaml(merged, 0);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> mergeValues(Map<String, Object> base, Map<String, Object> overrides) {
        Map<String, Object> result = new LinkedHashMap<>(base);
        for (var entry : overrides.entrySet()) {
            if (result.containsKey(entry.getKey()) && entry.getValue() instanceof Map && result.get(entry.getKey()) instanceof Map) {
                result.put(entry.getKey(), mergeValues((Map<String, Object>) result.get(entry.getKey()), (Map<String, Object>) entry.getValue()));
            } else {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    private String renderYaml(Map<String, Object> map, int indent) {
        StringBuilder sb = new StringBuilder();
        String prefix = "  ".repeat(indent);
        for (var entry : map.entrySet()) {
            if (entry.getValue() instanceof Map submap) {
                sb.append(prefix).append(entry.getKey()).append(":\n");
                sb.append(renderYaml(submap, indent + 1));
            } else if (entry.getValue() instanceof List list) {
                sb.append(prefix).append(entry.getKey()).append(":\n");
                for (Object item : list) {
                    sb.append(prefix).append("  - ").append(item).append("\n");
                }
            } else {
                sb.append(prefix).append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        HelmValueRenderer renderer = new HelmValueRenderer();
        renderer.setGlobalValue("global", Map.of("environment", "production", "region", "us-east-1"));
        renderer.setGlobalValue("replicaCount", 3);
        renderer.setGlobalValue("image", Map.of("repository", "myapp", "tag", "1.0.0"));
        Map<String, Object> overrides = Map.of("replicaCount", 5, "image", Map.of("tag", "1.0.1"));
        System.out.println(renderer.renderValues(overrides));
    }
}
