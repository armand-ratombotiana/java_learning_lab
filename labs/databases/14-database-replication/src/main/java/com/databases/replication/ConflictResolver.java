package com.databases.replication;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConflictResolver {
    private final Map<String, List<VersionedValue>> conflictLog = new ConcurrentHashMap<>();
    private final ConflictStrategy strategy;

    public enum ConflictStrategy { LAST_WRITE_WINS, TIMESTAMP_BASED, CRDT_MERGE, MANUAL }

    public record VersionedValue(String value, long timestamp, String nodeId, long version) {}

    public ConflictResolver(ConflictStrategy strategy) { this.strategy = strategy; }

    public String resolve(String key, List<VersionedValue> conflicting) {
        if (conflicting == null || conflicting.size() <= 1)
            return (conflicting != null && !conflicting.isEmpty()) ? conflicting.get(0).value() : null;

        conflictLog.put(key, new ArrayList<>(conflicting));

        return switch (strategy) {
            case LAST_WRITE_WINS -> conflicting.stream()
                .max(Comparator.comparingLong(VersionedValue::version))
                .map(VersionedValue::value).orElse(null);
            case TIMESTAMP_BASED -> conflicting.stream()
                .max(Comparator.comparingLong(VersionedValue::timestamp))
                .map(VersionedValue::value).orElse(null);
            case CRDT_MERGE -> mergeValues(conflicting);
            case MANUAL -> null;
        };
    }

    private String mergeValues(List<VersionedValue> values) {
        Set<String> unique = new LinkedHashSet<>();
        for (var v : values) Collections.addAll(unique, v.value().split(","));
        return String.join(",", unique);
    }

    public List<VersionedValue> getConflicts(String key) { return conflictLog.getOrDefault(key, List.of()); }
    public Map<String, List<VersionedValue>> getAllConflicts() { return Collections.unmodifiableMap(conflictLog); }
    public void resolveManually(String key) { conflictLog.remove(key); }
}
