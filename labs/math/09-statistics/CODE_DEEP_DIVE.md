# Code Deep Dive: Statistics

```java
package com.mathacademy.statistics;

import java.util.*;

public class StatisticsCalculator {
    
    public static double mean(double[] data) {
        return Arrays.stream(data).sum() / data.length;
    }
    
    public static double median(double[] data) {
        double[] sorted = data.clone();
        Arrays.sort(sorted);
        int n = sorted.length;
        return n % 2 == 0 ? (sorted[n/2-1] + sorted[n/2]) / 2 : sorted[n/2];
    }
    
    public static double mode(double[] data) {
        Map<Double, Integer> counts = new HashMap<>();
        for (double x : data) counts.put(x, counts.getOrDefault(x, 0) + 1);
        return Collections.max(counts.entrySet(), Map.Entry.comparingByValue()).getKey();
    }
    
    public static double variance(double[] data, boolean sample) {
        double m = mean(data);
        double sumSq = 0;
        for (double x : data) sumSq += (x - m) * (x - m);
        return sumSq / (sample ? data.length - 1 : data.length);
    }
    
    public static double stdDev(double[] data, boolean sample) {
        return Math.sqrt(variance(data, sample));
    }
    
    public static double[] fiveNumberSummary(double[] data) {
        double[] sorted = data.clone();
        Arrays.sort(sorted);
        return new double[]{sorted[0], percentile(sorted, 25), median(sorted), percentile(sorted, 75), sorted[sorted.length-1]};
    }
    
    public static double percentile(double[] sorted, double p) {
        double idx = (p / 100) * (sorted.length - 1);
        int lower = (int) Math.floor(idx);
        int upper = (int) Math.ceil(idx);
        if (lower == upper) return sorted[lower];
        return sorted[lower] + (idx - lower) * (sorted[upper] - sorted[lower]);
    }
    
    public static double covariance(double[] x, double[] y) {
        double meanX = mean(x), meanY = mean(y);
        double sum = 0;
        for (int i = 0; i < x.length; i++) sum += (x[i] - meanX) * (y[i] - meanY);
        return sum / (x.length - 1);
    }
    
    public static double correlation(double[] x, double[] y) {
        return covariance(x, y) / (stdDev(x, true) * stdDev(y, true));
    }
    
    public static double[] linearRegression(double[] x, double[] y) {
        double meanX = mean(x), meanY = mean(y);
        double slope = covariance(x, y) / variance(x, true);
        double intercept = meanY - slope * meanX;
        return new double[]{intercept, slope};
    }
    
    public static double[] confidenceInterval(double[] data, double confidence) {
        double m = mean(data);
        double se = stdDev(data, true) / Math.sqrt(data.length);
        double z = 1.96; // for 95% CI
        return new double[]{m - z * se, m + z * se};
    }
    
    public static double tStatistic(double sampleMean, double popMean, double stdDev, int n) {
        return (sampleMean - popMean) / (stdDev / Math.sqrt(n));
    }
    
    public static double chiSquareStatistic(double[] observed, double[] expected) {
        double sum = 0;
        for (int i = 0; i < observed.length; i++) {
            sum += Math.pow(observed[i] - expected[i], 2) / expected[i];
        }
        return sum;
    }
}
```