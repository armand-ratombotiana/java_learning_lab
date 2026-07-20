package com.capstone.mlplatform;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class FeatureStore {
    private final Map<String, FeatureGroup> groups = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> onlineStore = new ConcurrentHashMap<>();

    public record FeatureGroup(String name, List<FeatureDefinition> features, String source, String description) {
        public FeatureGroup { features = List.copyOf(features); }
    }

    public record FeatureDefinition(String name, FeatureType type, String description, boolean nullable) {}
    public enum FeatureType { STRING, DOUBLE, LONG, BOOLEAN, VECTOR }

    public record FeatureVector(String entityId, Map<String, Object> features, long timestamp) {
        public FeatureVector { features = Map.copyOf(features); }
    }

    public FeatureGroup registerFeatureGroup(String name, List<FeatureDefinition> features, String source) {
        FeatureGroup group = new FeatureGroup(name, features, source, "");
        groups.put(name, group);
        return group;
    }

    public void ingestOnline(String groupName, String entityId, Map<String, Object> values) {
        String key = groupName + ":" + entityId;
        onlineStore.computeIfAbsent(key, k -> new ConcurrentHashMap<>()).putAll(values);
    }

    public Optional<Map<String, Object>> getOnlineFeatures(String groupName, String entityId) {
        return Optional.ofNullable(onlineStore.get(groupName + ":" + entityId))
            .map(Map::copyOf);
    }

    public Map<String, Object> getOnlineFeaturesBatch(String entityId, List<String> groupNames) {
        Map<String, Object> combined = new HashMap<>();
        for (String group : groupNames) {
            Map<String, Object> features = onlineStore.get(group + ":" + entityId);
            if (features != null) combined.putAll(features);
        }
        return combined;
    }

    public List<FeatureVector> getHistoricalFeatures(String groupName) {
        return onlineStore.entrySet().stream()
            .filter(e -> e.getKey().startsWith(groupName + ":"))
            .map(e -> {
                String entityId = e.getKey().substring(groupName.length() + 1);
                return new FeatureVector(entityId, e.getValue(), System.currentTimeMillis());
            })
            .collect(Collectors.toList());
    }

    public FeatureGroup getFeatureGroup(String name) { return groups.get(name); }
    public Set<String> getGroupNames() { return groups.keySet(); }
    public int onlineSize() { return onlineStore.size(); }
    public void clear() { groups.clear(); onlineStore.clear(); }
}
