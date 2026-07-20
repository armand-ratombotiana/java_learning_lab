package com.capstone.mlplatform;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DriftDetector {
    private final Map<String, Distribution> baseline = new ConcurrentHashMap<>();
    private final Map<String, List<DriftReport>> driftHistory = new ConcurrentHashMap<>();
    private double threshold = 0.1;

    public record Distribution(double mean, double stddev, long count, double[] percentiles) {
        public Distribution { percentiles = percentiles == null ? new double[0] : percentiles; }
    }

    public record DriftReport(String featureName, double driftScore, DriftLevel level,
                               long detectedAt, String message) {}

    public enum DriftLevel { NONE, WARNING, SEVERE }

    public void setBaseline(String featureName, Distribution dist) {
        baseline.put(featureName, dist);
    }

    public DriftReport detectDrift(String featureName, double[] currentValues) {
        Distribution base = baseline.get(featureName);
        if (base == null) return new DriftReport(featureName, 0, DriftLevel.NONE,
            System.currentTimeMillis(), "No baseline");

        double currentMean = Arrays.stream(currentValues).average().orElse(0);
        double ps = populationStabilityIndex(base, currentValues);
        DriftLevel level = ps > threshold * 2 ? DriftLevel.SEVERE :
            ps > threshold ? DriftLevel.WARNING : DriftLevel.NONE;

        DriftReport report = new DriftReport(featureName, ps, level, System.currentTimeMillis(),
            "PSI=" + String.format("%.4f", ps) + " mean=" + String.format("%.4f", currentMean));
        driftHistory.computeIfAbsent(featureName, k -> new ArrayList<>()).add(report);
        return report;
    }

    public List<DriftReport> detectBatch(Map<String, double[]> currentData) {
        return currentData.entrySet().stream()
            .map(e -> detectDrift(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
    }

    public List<DriftReport> getDriftHistory(String featureName) {
        return List.copyOf(driftHistory.getOrDefault(featureName, List.of()));
    }

    public List<DriftReport> getRecentAlerts(DriftLevel minLevel) {
        return driftHistory.values().stream()
            .flatMap(List::stream)
            .filter(r -> r.level().ordinal() >= minLevel.ordinal())
            .sorted(Comparator.comparingLong(DriftReport::detectedAt).reversed())
            .limit(100)
            .collect(Collectors.toList());
    }

    public void setThreshold(double threshold) { this.threshold = threshold; }
    public double getThreshold() { return threshold; }

    public void clear() { baseline.clear(); driftHistory.clear(); }

    private double populationStabilityIndex(Distribution expected, double[] actual) {
        double psi = 0;
        int bins = Math.min(expected.percentiles().length + 1, 10);
        double binSize = actual.length / (double) bins;
        for (int i = 0; i < bins; i++) {
            double expectedProp = 1.0 / bins;
            int start = (int)(i * binSize);
            int end = (int)((i + 1) * binSize);
            double actualProp = (end - start) / (double) actual.length;
            if (actualProp > 0 && expectedProp > 0) {
                psi += (actualProp - expectedProp) * Math.log(actualProp / expectedProp);
            }
        }
        return psi;
    }
}
