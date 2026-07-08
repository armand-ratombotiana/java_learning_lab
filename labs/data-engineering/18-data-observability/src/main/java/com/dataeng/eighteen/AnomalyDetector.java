package com.dataeng.eighteen;

import java.util.*;

public class AnomalyDetector {

    public record VolumeAnomaly(String metric, double currentValue, double expectedValue,
                                  double deviation, boolean isAnomaly, String severity) {}

    public record FreshnessAnomaly(String table, long currentLagMs, long expectedLagMs,
                                    boolean isAnomaly, String severity) {}

    public VolumeAnomaly detectVolumeAnomaly(double current, List<Double> history) {
        double mean = history.stream().mapToDouble(v -> v).average().orElse(current);
        double stddev = Math.sqrt(history.stream()
            .mapToDouble(v -> Math.pow(v - mean, 2)).average().orElse(0));
        double zScore = stddev > 0 ? Math.abs(current - mean) / stddev : 0;
        double deviation = mean > 0 ? (current - mean) / mean : 0;

        String severity;
        boolean isAnomaly;
        if (zScore > 4) { isAnomaly = true; severity = "CRITICAL"; }
        else if (zScore > 3) { isAnomaly = true; severity = "HIGH"; }
        else if (zScore > 2) { isAnomaly = true; severity = "WARNING"; }
        else { isAnomaly = false; severity = "OK"; }

        return new VolumeAnomaly("row_count", current, mean, deviation, isAnomaly, severity);
    }

    public FreshnessAnomaly detectFreshnessAnomaly(long currentLagMs, List<Long> history, long slaMs) {
        double avgLag = history.stream().mapToLong(v -> v).average().orElse(currentLagMs);
        boolean isAnomaly = currentLagMs > slaMs && currentLagMs > avgLag * 2;
        String severity = isAnomaly ? (currentLagMs > slaMs * 3 ? "CRITICAL" : "HIGH") : "OK";
        return new FreshnessAnomaly("table", currentLagMs, (long) avgLag, isAnomaly, severity);
    }

    public Map<String, Object> detectDistributionDrift(double[] currentHistogram, double[] expectedHistogram) {
        double ks = computeKsStatistic(currentHistogram, expectedHistogram);
        var result = new HashMap<String, Object>();
        result.put("ks_statistic", ks);
        result.put("drift_detected", ks > 0.1);
        result.put("severity", ks > 0.2 ? "HIGH" : ks > 0.1 ? "WARNING" : "OK");
        return result;
    }

    private double computeKsStatistic(double[] a, double[] b) {
        double[] cdfA = new double[a.length];
        double[] cdfB = new double[b.length];
        cdfA[0] = a[0]; cdfB[0] = b[0];
        for (int i = 1; i < a.length; i++) {
            cdfA[i] = cdfA[i-1] + a[i];
            cdfB[i] = cdfB[i-1] + b[i];
        }
        double maxDiff = 0;
        for (int i = 0; i < a.length; i++) {
            maxDiff = Math.max(maxDiff, Math.abs(cdfA[i] - cdfB[i]));
        }
        return maxDiff;
    }
}
