package com.capstone.mlplatform;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ABTestFramework {
    private final Map<String, ExperimentConfig> experiments = new ConcurrentHashMap<>();
    private final Map<String, List<TrialResult>> results = new ConcurrentHashMap<>();
    private final AtomicLong trialIdGen = new AtomicLong(0);

    public record ExperimentConfig(String experimentId, String name, List<Variant> variants,
                                    double trafficAllocation, String metricName) {
        public ExperimentConfig {
            variants = List.copyOf(variants);
        }
    }

    public record Variant(String id, String name, String modelId, double weight) {}
    public record TrialResult(long trialId, String experimentId, String variantId, String userId,
                               double metricValue, long timestamp) {}

    public ExperimentConfig createExperiment(String name, List<Variant> variants, double trafficAllocation, String metricName) {
        String id = "ab-" + UUID.randomUUID().toString().substring(0, 8);
        ExperimentConfig config = new ExperimentConfig(id, name, variants, trafficAllocation, metricName);
        experiments.put(id, config);
        return config;
    }

    public Variant assignVariant(String experimentId, String userId) {
        ExperimentConfig config = experiments.get(experimentId);
        if (config == null) throw new IllegalArgumentException("Experiment not found: " + experimentId);
        double r = ThreadLocalRandom.current().nextDouble();
        double cumulative = 0;
        for (Variant v : config.variants()) {
            cumulative += v.weight();
            if (r <= cumulative) return v;
        }
        return config.variants().get(config.variants().size() - 1);
    }

    public TrialResult recordResult(String experimentId, String variantId, String userId, double metricValue) {
        long id = trialIdGen.incrementAndGet();
        TrialResult result = new TrialResult(id, experimentId, variantId, userId, metricValue, System.currentTimeMillis());
        results.computeIfAbsent(experimentId, k -> new ArrayList<>()).add(result);
        return result;
    }

    public Map<String, Double> getVariantMetrics(String experimentId) {
        List<TrialResult> trials = results.get(experimentId);
        if (trials == null || trials.isEmpty()) return Map.of();
        return trials.stream()
            .collect(Collectors.groupingBy(TrialResult::variantId,
                Collectors.averagingDouble(TrialResult::metricValue)));
    }

    public Optional<Variant> getWinningVariant(String experimentId) {
        Map<String, Double> metrics = getVariantMetrics(experimentId);
        return metrics.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(e -> {
                ExperimentConfig config = experiments.get(experimentId);
                if (config == null) return null;
                return config.variants().stream()
                    .filter(v -> v.id().equals(e.getKey()))
                    .findFirst().orElse(null);
            });
    }

    public long getTrialCount(String experimentId) {
        return results.getOrDefault(experimentId, List.of()).size();
    }

    public List<TrialResult> getResults(String experimentId) {
        return List.copyOf(results.getOrDefault(experimentId, List.of()));
    }

    public ExperimentConfig getExperiment(String id) { return experiments.get(id); }
    public List<ExperimentConfig> getAllExperiments() { return List.copyOf(experiments.values()); }
    public void clear() { experiments.clear(); results.clear(); trialIdGen.set(0); }
}
