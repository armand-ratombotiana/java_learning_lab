package com.capstone.agent;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class AgentMonitor {
    private final Map<String, AgentMetrics> metrics = new ConcurrentHashMap<>();
    private final List<MonitorEvent> events = new CopyOnWriteArrayList<>();
    private final Map<String, List<Double>> latencyHistory = new ConcurrentHashMap<>();

    public record AgentMetrics(String agentId, long totalSteps, long successfulSteps, long failedSteps,
                                double avgLatencyMs, double lastActiveTime, Map<String, Long> toolUsage) {
        public AgentMetrics { toolUsage = toolUsage == null ? Map.of() : Map.copyOf(toolUsage); }
    }

    public record MonitorEvent(String agentId, String eventType, String message, long timestamp) {}

    public void recordStep(String agentId, boolean success, long latencyMs, String toolName) {
        metrics.compute(agentId, (k, v) -> {
            if (v == null) return new AgentMetrics(agentId, 1, success ? 1 : 0, success ? 0 : 1,
                latencyMs, System.currentTimeMillis() / 1000.0,
                toolName != null ? Map.of(toolName, 1L) : Map.of());
            Map<String, Long> toolUsage = new HashMap<>(v.toolUsage());
            toolUsage.merge(toolName != null ? toolName : "unknown", 1L, Long::sum);
            return new AgentMetrics(agentId, v.totalSteps() + 1,
                v.successfulSteps() + (success ? 1 : 0),
                v.failedSteps() + (success ? 0 : 1),
                (v.avgLatencyMs() * v.totalSteps() + latencyMs) / (v.totalSteps() + 1),
                System.currentTimeMillis() / 1000.0, toolUsage);
        });
        latencyHistory.computeIfAbsent(agentId, k -> new CopyOnWriteArrayList<>()).add((double) latencyMs);
    }

    public void recordEvent(String agentId, String eventType, String message) {
        events.add(new MonitorEvent(agentId, eventType, message, System.currentTimeMillis()));
    }

    public Optional<AgentMetrics> getMetrics(String agentId) {
        return Optional.ofNullable(metrics.get(agentId));
    }

    public List<MonitorEvent> getEvents(String agentId) {
        return events.stream().filter(e -> e.agentId().equals(agentId)).collect(Collectors.toList());
    }

    public List<MonitorEvent> getRecentEvents(int limit) {
        return events.stream()
            .sorted(Comparator.comparingLong(MonitorEvent::timestamp).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    public List<AgentMetrics> getAllMetrics() { return List.copyOf(metrics.values()); }

    public List<AgentMetrics> getTopPerformingAgents(int limit) {
        return metrics.values().stream()
            .sorted(Comparator.comparingDouble(AgentMetrics::avgLatencyMs))
            .limit(limit)
            .collect(Collectors.toList());
    }

    public List<AgentMetrics> getStrugglingAgents(int limit) {
        return metrics.values().stream()
            .filter(m -> m.failedSteps() > m.totalSteps() * 0.3)
            .sorted(Comparator.comparingLong(AgentMetrics::failedSteps).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    public Map<String, List<Double>> getLatencyHistory() {
        Map<String, List<Double>> copy = new HashMap<>();
        for (Map.Entry<String, List<Double>> e : latencyHistory.entrySet()) {
            copy.put(e.getKey(), List.copyOf(e.getValue()));
        }
        return Map.copyOf(copy);
    }

    public void clear() { metrics.clear(); events.clear(); latencyHistory.clear(); }
}
