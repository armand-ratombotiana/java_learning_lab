package com.ml.lab10;

import java.util.*;

/**
 * Model Evaluation — confusion matrix, metrics, ROC/AUC, k-fold CV.
 * <p>
 * Demonstrates evaluation techniques on synthetic binary predictions.
 */
public class Main {

    // ──────────────────────────────────────────────
    // Confusion Matrix
    // ──────────────────────────────────────────────

    public static int[] confusionMatrix(int[] actual, int[] predicted) {
        int tp = 0, fp = 0, fn = 0, tn = 0;
        for (int i = 0; i < actual.length; i++) {
            if (actual[i] == 1 && predicted[i] == 1) tp++;
            else if (actual[i] == 0 && predicted[i] == 1) fp++;
            else if (actual[i] == 1 && predicted[i] == 0) fn++;
            else tn++;
        }
        return new int[]{tp, fp, fn, tn};
    }

    // ──────────────────────────────────────────────
    // Metrics
    // ──────────────────────────────────────────────

    public static double accuracy(int tp, int fp, int fn, int tn) {
        return (double) (tp + tn) / (tp + fp + fn + tn);
    }

    public static double precision(int tp, int fp) {
        return tp + fp == 0 ? 0 : (double) tp / (tp + fp);
    }

    public static double recall(int tp, int fn) {
        return tp + fn == 0 ? 0 : (double) tp / (tp + fn);
    }

    public static double f1(double prec, double rec) {
        return prec + rec == 0 ? 0 : 2 * prec * rec / (prec + rec);
    }

    // ──────────────────────────────────────────────
    // ROC points & AUC (trapezoidal)
    // ──────────────────────────────────────────────

    public static double auc(double[] scores, int[] actual) {
        int n = scores.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> Double.compare(scores[b], scores[a]));

        int pos = 0, neg = 0;
        for (int v : actual) { if (v == 1) pos++; else neg++; }

        double tpr = 0, fpr = 0, prevFpr = 0, prevTpr = 0, area = 0;
        for (int i = 0; i < n; i++) {
            int label = actual[idx[i]];
            if (label == 1) tpr += 1.0 / pos;
            else fpr += 1.0 / neg;
            if (i == n - 1 || scores[idx[i]] != scores[idx[i + 1]]) {
                area += (fpr - prevFpr) * (tpr + prevTpr) / 2;
                prevFpr = fpr;
                prevTpr = tpr;
            }
        }
        return area;
    }

    // ──────────────────────────────────────────────
    // K-Fold Cross-Validation
    // ──────────────────────────────────────────────

    public static double[] crossVal(double[][] X, int[] y, int k, Random rng) {
        int n = X.length;
        List<Integer> shuf = new ArrayList<>();
        for (int i = 0; i < n; i++) shuf.add(i);
        Collections.shuffle(shuf, rng);

        double[] accs = new double[k];
        int foldSize = n / k;
        for (int fold = 0; fold < k; fold++) {
            int start = fold * foldSize;
            int end = (fold == k - 1) ? n : start + foldSize;
            Set<Integer> testIdx = new HashSet<>(shuf.subList(start, end));

            List<double[]> trainXl = new ArrayList<>();
            List<Integer> trainYl = new ArrayList<>();
            List<double[]> testXl = new ArrayList<>();
            List<Integer> testYl = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                if (testIdx.contains(i)) {
                    testXl.add(X[i]);
                    testYl.add(y[i]);
                } else {
                    trainXl.add(X[i]);
                    trainYl.add(y[i]);
                }
            }

            double[][] trainX = trainXl.toArray(new double[0][]);
            int[] trainY = trainYl.stream().mapToInt(v -> v).toArray();
            double[][] testX = testXl.toArray(new double[0][]);
            int[] testY = testYl.stream().mapToInt(v -> v).toArray();

            // Dummy classifier: predict majority class from training
            int sum = 0;
            for (int v : trainY) sum += v;
            int majority = sum > trainY.length / 2 ? 1 : 0;

            int ok = 0;
            for (int i = 0; i < testY.length; i++) {
                if (testY[i] == majority) ok++;
            }
            accs[fold] = (double) ok / testY.length;
        }
        return accs;
    }

    // ──────────────────────────────────────────────
    // Main — test cases
    // ──────────────────────────────────────────────

    public static void main(String[] args) {
        System.out.println("=== Model Evaluation Lab ===");

        // Synthetic predictions
        int[] actual    = {1, 1, 0, 1, 0, 0, 1, 0, 1, 0};
        int[] predicted = {1, 0, 0, 1, 1, 0, 1, 0, 0, 0};
        double[] scores = {0.9, 0.7, 0.2, 0.8, 0.6, 0.1, 0.95, 0.3, 0.4, 0.05};

        int[] cm = confusionMatrix(actual, predicted);
        System.out.printf("Confusion: TP=%d FP=%d FN=%d TN=%d%n", cm[0], cm[1], cm[2], cm[3]);

        double acc = accuracy(cm[0], cm[1], cm[2], cm[3]);
        double prec = precision(cm[0], cm[1]);
        double rec = recall(cm[0], cm[2]);
        double f = f1(prec, rec);
        System.out.printf("Acc=%.2f Prec=%.2f Rec=%.2f F1=%.2f%n", acc, prec, rec, f);

        double area = auc(scores, actual);
        System.out.printf("AUC = %.4f%n", area);

        Random rng = new Random(42);
        double[][] X = new double[20][2];
        int[] y = new int[20];
        for (int i = 0; i < 20; i++) {
            X[i][0] = rng.nextDouble();
            X[i][1] = rng.nextDouble();
            y[i] = rng.nextDouble() > 0.5 ? 1 : 0;
        }
        double[] cvAcc = crossVal(X, y, 5, rng);
        double mean = 0;
        for (double v : cvAcc) mean += v;
        mean /= cvAcc.length;
        double std = 0;
        for (double v : cvAcc) std += (v - mean) * (v - mean);
        std = Math.sqrt(std / cvAcc.length);
        System.out.printf("5-Fold CV: %.2f ± %.3f%n", mean, std);
    }
}
