package com.capstone.mlplatform;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ExperimentTracker {
    private final Map<String, Experiment> experiments = new ConcurrentHashMap<>();
    private final Map<String, List<RunLog>> runLogs = new ConcurrentHashMap<>();
    private final AtomicLong expIdGen = new AtomicLong(0);

    public record Experiment(String id, String name, String description,
                              Map<String, Object> config, Instant createdAt) {
        public Experiment { config = config == null ? Map.of() : Map.copyOf(config); }
    }

    public record RunLog(String experimentId, String runId, Map<String, Double> metrics,
                          Map<String, Object> params, Instant timestamp) {
        public RunLog {
            metrics = metrics == null ? Map.of() : Map.copyOf(metrics);
            params = params == null ? Map.of() : Map.copyOf(params);
        }
    }

    public Experiment createExperiment(String name, String description, Map<String, Object> config) {
        String id = "exp-" + expIdGen.incrementAndGet();
        Experiment exp = new Experiment(id, name, description, config, Instant.now());
        experiments.put(id, exp);
        return exp;
    }

    public void logMetrics(String experimentId, String runId, Map<String, Double> metrics) {
        logMetrics(experimentId, runId, metrics, Map.of());
    }

    public void logMetrics(String experimentId, String runId, Map<String, Double> metrics, Map<String, Object> params) {
        RunLog log = new RunLog(experimentId, runId, metrics, params, Instant.now());
        runLogs.computeIfAbsent(experimentId, k -> new ArrayList<>()).add(log);
    }

    public List<RunLog> getRunLogs(String experimentId) {
        return List.copyOf(runLogs.getOrDefault(experimentId, List.of()));
    }

    public Optional<Map<String, Double>> getBestMetrics(String experimentId, String metricName) {
        List<RunLog> logs = runLogs.get(experimentId);
        if (logs == null || logs.isEmpty()) return Optional.empty();
        return logs.stream()
            .map(RunLog::metrics)
            .filter(m -> m.containsKey(metricName))
            .max(Comparator.comparingDouble(m -> m.get(metricName)));
    }

    public Experiment getExperiment(String id) { return experiments.get(id); }
    public List<Experiment> getAllExperiments() { return List.copyOf(experiments.values()); }
    public int experimentCount() { return experiments.size(); }
    public void clear() { experiments.clear(); runLogs.clear(); expIdGen.set(0); }
}
