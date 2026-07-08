package com.datasci.09;

import java.util.*;

public class Metrics {

    private Metrics() {}

    // Classification
    public static double accuracy(int tp, int tn, int fp, int fn) {
        int total = tp + tn + fp + fn;
        return total == 0 ? 0.0 : (double) (tp + tn) / total;
    }

    public static double precision(int tp, int fp) {
        return tp + fp == 0 ? 0.0 : (double) tp / (tp + fp);
    }

    public static double recall(int tp, int fn) {
        return tp + fn == 0 ? 0.0 : (double) tp / (tp + fn);
    }

    public static double f1Score(double precision, double recall) {
        return precision + recall == 0 ? 0.0 : 2 * precision * recall / (precision + recall);
    }

    public static ConfusionMatrix confusionMatrix(double[] predictions, double[] labels, double threshold) {
        int tp = 0, tn = 0, fp = 0, fn = 0;
        for (int i = 0; i < predictions.length; i++) {
            boolean pred = predictions[i] >= threshold;
            boolean actual = labels[i] >= threshold;
            if (pred && actual) tp++;
            else if (!pred && !actual) tn++;
            else if (pred && !actual) fp++;
            else fn++;
        }
        return new ConfusionMatrix(tp, tn, fp, fn);
    }

    // Regression
    public static double meanSquaredError(double[] predictions, double[] labels) {
        double sum = 0.0;
        for (int i = 0; i < predictions.length; i++) {
            double diff = predictions[i] - labels[i];
            sum += diff * diff;
        }
        return sum / predictions.length;
    }

    public static double rootMeanSquaredError(double[] predictions, double[] labels) {
        return Math.sqrt(meanSquaredError(predictions, labels));
    }

    public static double meanAbsoluteError(double[] predictions, double[] labels) {
        double sum = 0.0;
        for (int i = 0; i < predictions.length; i++) {
            sum += Math.abs(predictions[i] - labels[i]);
        }
        return sum / predictions.length;
    }

    public static double rSquared(double[] predictions, double[] labels) {
        double mean = Arrays.stream(labels).average().orElse(0.0);
        double ssRes = 0.0, ssTot = 0.0;
        for (int i = 0; i < predictions.length; i++) {
            ssRes += Math.pow(predictions[i] - labels[i], 2);
            ssTot += Math.pow(labels[i] - mean, 2);
        }
        return ssTot == 0 ? 0.0 : 1 - ssRes / ssTot;
    }

    // Cross-validation
    public static double[] crossValidate(double[][] features, double[] labels,
                                          int k, java.util.function.BiFunction<double[][], double[], Double> trainer) {
        int n = features.length;
        int foldSize = n / k;
        double[] scores = new double[k];
        for (int fold = 0; fold < k; fold++) {
            int valStart = fold * foldSize;
            int valEnd = (fold == k - 1) ? n : valStart + foldSize;
            int valCount = valEnd - valStart;
            int trainCount = n - valCount;
            double[][] trainFeatures = new double[trainCount][];
            double[] trainLabels = new double[trainCount];
            double[][] valFeatures = new double[valCount][];
            double[] valLabels = new double[valCount];
            int ti = 0, vi = 0;
            for (int i = 0; i < n; i++) {
                if (i >= valStart && i < valEnd) {
                    valFeatures[vi] = features[i];
                    valLabels[vi] = labels[i];
                    vi++;
                } else {
                    trainFeatures[ti] = features[i];
                    trainLabels[ti] = labels[i];
                    ti++;
                }
            }
            scores[fold] = trainer.apply(trainFeatures, trainLabels);
        }
        return scores;
    }

    // Value classes
    public record ConfusionMatrix(int tp, int tn, int fp, int fn) {
        public double accuracy() { return Metrics.accuracy(tp, tn, fp, fn); }
        public double precision() { return Metrics.precision(tp, fp); }
        public double recall() { return Metrics.recall(tp, fn); }
        public double f1() { return Metrics.f1Score(precision(), recall()); }
    }

    public record ROCPoint(double fpr, double tpr) {}

    public static List<ROCPoint> rocCurve(double[] scores, double[] labels) {
        int n = scores.length;
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;
        Arrays.sort(indices, (a, b) -> Double.compare(scores[b], scores[a]));
        List<ROCPoint> curve = new ArrayList<>();
        curve.add(new ROCPoint(0.0, 0.0));
        int posTotal = (int) Arrays.stream(labels).filter(l -> l > 0.5).count();
        int negTotal = n - posTotal;
        int tp = 0, fp = 0;
        for (int idx : indices) {
            if (labels[idx] > 0.5) tp++;
            else fp++;
            curve.add(new ROCPoint((double) fp / negTotal, (double) tp / posTotal));
        }
        return curve;
    }

    public static double auc(List<ROCPoint> curve) {
        double area = 0.0;
        for (int i = 1; i < curve.size(); i++) {
            var prev = curve.get(i - 1);
            var curr = curve.get(i);
            area += (curr.fpr() - prev.fpr()) * (curr.tpr() + prev.tpr()) / 2.0;
        }
        return area;
    }
}
