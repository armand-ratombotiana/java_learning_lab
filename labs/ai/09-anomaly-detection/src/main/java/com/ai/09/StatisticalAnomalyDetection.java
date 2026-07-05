package com.ai09;

public class StatisticalAnomalyDetection {

    public static double zScore(double value, double mean, double stddev) {
        return (value - mean) / stddev;
    }

    public static boolean isAnomalyZScore(double value, double mean, double stddev, double threshold) {
        return Math.abs(zScore(value, mean, stddev)) > threshold;
    }

    public static double[] computeStats(double[] data) {
        double mean = 0;
        for (double v : data) mean += v;
        mean /= data.length;
        double variance = 0;
        for (double v : data) variance += (v - mean) * (v - mean);
        variance /= data.length;
        double stddev = Math.sqrt(variance);
        return new double[]{mean, stddev};
    }

    public static boolean[] detectZScore(double[] data, double threshold) {
        double[] stats = computeStats(data);
        int n = data.length;
        boolean[] anomalies = new boolean[n];
        for (int i = 0; i < n; i++)
            anomalies[i] = isAnomalyZScore(data[i], stats[0], stats[1], threshold);
        return anomalies;
    }

    public static boolean[] detectIQR(double[] data) {
        int n = data.length;
        double[] sorted = data.clone();
        java.util.Arrays.sort(sorted);
        double q1 = sorted[n / 4];
        double q3 = sorted[3 * n / 4];
        double iqr = q3 - q1;
        double lower = q1 - 1.5 * iqr;
        double upper = q3 + 1.5 * iqr;
        boolean[] anomalies = new boolean[n];
        for (int i = 0; i < n; i++)
            anomalies[i] = data[i] < lower || data[i] > upper;
        return anomalies;
    }

    public static double[] detectMAD(double[] data) {
        int n = data.length;
        double median = computeMedian(data);
        double[] absDeviations = new double[n];
        for (int i = 0; i < n; i++)
            absDeviations[i] = Math.abs(data[i] - median);
        double mad = computeMedian(absDeviations);
        double[] modifiedZScores = new double[n];
        for (int i = 0; i < n; i++)
            modifiedZScores[i] = 0.6745 * (data[i] - median) / Math.max(mad, 1e-10);
        return modifiedZScores;
    }

    private static double computeMedian(double[] sorted) {
        int n = sorted.length;
        if (n % 2 == 0)
            return (sorted[n / 2 - 1] + sorted[n / 2]) / 2;
        return sorted[n / 2];
    }

    public static void main(String[] args) {
        System.out.println("=== Statistical Anomaly Detection Demo ===");
        double[] data = {10, 12, 11, 13, 12, 11, 10, 100, 12, 11, 13, 12, -50, 11, 12};
        System.out.println("Data: " + java.util.Arrays.toString(data));
        double[] stats = computeStats(data);
        System.out.println("Mean: " + stats[0] + " StdDev: " + stats[1]);
        System.out.println("Z-Score anomalies (>3): " + java.util.Arrays.toString(detectZScore(data, 3)));
        System.out.println("IQR anomalies: " + java.util.Arrays.toString(detectIQR(data)));
        double[] madScores = detectMAD(data);
        System.out.println("MAD z-scores: " + java.util.Arrays.toString(madScores));
    }
}
