package com.datascience.deep.lab10;

import java.util.*;
import java.util.function.DoubleUnaryOperator;

public final class MLPipelineAutomation {

    @FunctionalInterface
    public interface Predictor {
        double predict(double[] features);
        default double[] predictBatch(double[][] X) {
            return Arrays.stream(X).mapToDouble(this::predict).toArray();
        }
    }

    // -- Feature Selection --

    public static int[] varianceThreshold(double[][] X, double threshold) {
        int p = X[0].length;
        List<Integer> selected = new ArrayList<>();
        for (int j = 0; j < p; j++) {
            double sum = 0, sumSq = 0;
            for (double[] row : X) { sum += row[j]; sumSq += row[j] * row[j]; }
            double mean = sum / X.length;
            double var = sumSq / X.length - mean * mean;
            if (var >= threshold) selected.add(j);
        }
        return selected.stream().mapToInt(Integer::intValue).toArray();
    }

    public static double mutualInformation(double[] x, double[] y, int bins) {
        double minX = Arrays.stream(x).min().orElseThrow(), maxX = Arrays.stream(x).max().orElseThrow();
        double minY = Arrays.stream(y).min().orElseThrow(), maxY = Arrays.stream(y).max().orElseThrow();
        int[] xi = new int[x.length], yi = new int[y.length];
        for (int i = 0; i < x.length; i++) {
            xi[i] = (int) ((x[i] - minX) / (maxX - minX + 1e-10) * bins);
            yi[i] = (int) ((y[i] - minY) / (maxY - minY + 1e-10) * bins);
            xi[i] = Math.min(bins - 1, xi[i]); yi[i] = Math.min(bins - 1, yi[i]);
        }
        double[][] joint = new double[bins][bins];
        double[] mx = new double[bins], my = new double[bins];
        for (int i = 0; i < x.length; i++) {
            joint[xi[i]][yi[i]]++; mx[xi[i]]++; my[yi[i]]++;
        }
        double mi = 0;
        for (int i = 0; i < bins; i++) {
            for (int j = 0; j < bins; j++) {
                if (joint[i][j] > 0) {
                    mi += (joint[i][j] / x.length) * Math.log((joint[i][j] * x.length) / (mx[i] * my[j]));
                }
            }
        }
        return mi;
    }

    // -- Cross-Validation --

    public static class CrossValidation {
        private final int k;
        private final Random rng;

        public CrossValidation(int k) {
            this.k = k;
            this.rng = new Random(42L);
        }

        public record FoldResult(double[][] trainX, double[] trainY, double[][] testX, double[] testY) {}

        public List<FoldResult> split(double[][] X, double[] y) {
            int n = X.length;
            int[] idx = IntStream.range(0, n).toArray();
            for (int i = n - 1; i > 0; i--) { int j = rng.nextInt(i + 1); int t = idx[i]; idx[i] = idx[j]; idx[j] = t; }
            List<FoldResult> folds = new ArrayList<>();
            int foldSize = n / k;
            for (int f = 0; f < k; f++) {
                int start = f * foldSize;
                int end = (f == k - 1) ? n : start + foldSize;
                Set<Integer> testSet = new HashSet<>();
                for (int i = start; i < end; i++) testSet.add(idx[i]);
                List<double[]> tX = new ArrayList<>(); List<Double> tY = new ArrayList<>();
                List<double[]> vX = new ArrayList<>(); List<Double> vY = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                    if (testSet.contains(i)) { vX.add(X[i]); vY.add(y[i]); }
                    else { tX.add(X[i]); tY.add(y[i]); }
                }
                folds.add(new FoldResult(
                    tX.toArray(double[][]::new), tY.stream().mapToDouble(Double::doubleValue).toArray(),
                    vX.toArray(double[][]::new), vY.stream().mapToDouble(Double::doubleValue).toArray()
                ));
            }
            return folds;
        }
    }

    // -- Recursive Feature Elimination --

    public static int[] recursiveFeatureElimination(double[][] X, double[] y, int minFeatures) {
        int p = X[0].length;
        boolean[] selected = new boolean[p];
        Arrays.fill(selected, true);
        int currentCount = p;
        CrossValidation cv = new CrossValidation(5);

        while (currentCount > minFeatures) {
            // Use a simple predictor (average baseline) for speed — in practice use a real model
            double baselineScore = meanCrossValScore(X, y, selected, cv);
            int worst = -1;
            for (int j = 0; j < p; j++) {
                if (!selected[j]) continue;
                selected[j] = false;
                double score = meanCrossValScore(X, y, selected, cv);
                if (score >= baselineScore) { worst = j; baselineScore = score; }
                selected[j] = true;
            }
            if (worst >= 0) { selected[worst] = false; currentCount--; }
            else break;
        }
        List<Integer> result = new ArrayList<>();
        for (int j = 0; j < p; j++) if (selected[j]) result.add(j);
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    private static double meanCrossValScore(double[][] X, double[] y, boolean[] selected, CrossValidation cv) {
        double[][] subX = selectCols(X, selected);
        List<CrossValidation.FoldResult> folds = cv.split(subX, y);
        double sum = 0;
        for (CrossValidation.FoldResult fold : folds) {
            double meanY = Arrays.stream(fold.trainY()).average().orElseThrow();
            double mse = Arrays.stream(fold.testY()).map(v -> Math.pow(v - meanY, 2)).average().orElseThrow();
            sum += -mse;
        }
        return sum / folds.size();
    }

    // -- Gradient Boosting (simplified) --

    public static class GradientBoosting implements Predictor {
        private final int nEstimators;
        private final double learningRate;
        private final int maxDepth;
        private List<SimpleTree> trees;
        private double basePrediction;

        public GradientBoosting(int nEstimators, double learningRate, int maxDepth) {
            this.nEstimators = nEstimators;
            this.learningRate = learningRate;
            this.maxDepth = maxDepth;
        }

        public Predictor fit(double[][] X, double[] y) {
            int n = X.length;
            trees = new ArrayList<>();
            basePrediction = Arrays.stream(y).average().orElseThrow();
            double[] residuals = Arrays.stream(y).map(v -> v - basePrediction).toArray();
            for (int m = 0; m < nEstimators; m++) {
                SimpleTree tree = new SimpleTree(maxDepth);
                tree.fit(X, residuals);
                double[] preds = tree.predictBatch(X);
                for (int i = 0; i < n; i++) residuals[i] -= learningRate * preds[i];
                trees.add(tree);
            }
            return this;
        }

        @Override
        public double predict(double[] features) {
            double pred = basePrediction;
            for (SimpleTree tree : trees) pred += learningRate * tree.predict(features);
            return pred;
        }

        private static class SimpleTree {
            private final int maxDepth;
            private Node root;

            SimpleTree(int maxDepth) { this.maxDepth = maxDepth; }

            void fit(double[][] X, double[] y) { root = build(X, y, 0); }
            double predict(double[] x) { return root.predict(x); }
            double[] predictBatch(double[][] X) { return Arrays.stream(X).mapToDouble(this::predict).toArray(); }

            private Node build(double[][] X, double[] y, int depth) {
                if (depth >= maxDepth || X.length < 2) return new Leaf(Arrays.stream(y).average().orElseThrow());
                int bestFeat = 0; double bestThresh = 0, bestMse = Double.MAX_VALUE;
                int n = X.length, p = X[0].length;
                double meanY = Arrays.stream(y).average().orElseThrow();
                for (int j = 0; j < p; j++) {
                    double min = Arrays.stream(X).mapToDouble(r -> r[j]).min().orElseThrow();
                    double max = Arrays.stream(X).mapToDouble(r -> r[j]).max().orElseThrow();
                    for (double thresh = min; thresh <= max; thresh += (max - min) / 10 + 1e-10) {
                        double sumL = 0, sumR = 0; int cntL = 0, cntR = 0;
                        for (int i = 0; i < n; i++) {
                            if (X[i][j] <= thresh) { sumL += y[i]; cntL++; }
                            else { sumR += y[i]; cntR++; }
                        }
                        if (cntL < 1 || cntR < 1) continue;
                        double mL = sumL / cntL, mR = sumR / cntR;
                        double mse = 0;
                        for (int i = 0; i < n; i++) {
                            double pred = X[i][j] <= thresh ? mL : mR;
                            mse += Math.pow(y[i] - pred, 2);
                        }
                        if (mse < bestMse) { bestMse = mse; bestFeat = j; bestThresh = thresh; }
                    }
                }
                if (bestMse >= Double.MAX_VALUE) return new Leaf(meanY);
                List<double[]> leftX = new ArrayList<>(); List<Double> leftY = new ArrayList<>();
                List<double[]> rightX = new ArrayList<>(); List<Double> rightY = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                    if (X[i][bestFeat] <= bestThresh) { leftX.add(X[i]); leftY.add(y[i]); }
                    else { rightX.add(X[i]); rightY.add(y[i]); }
                }
                return new SplitNode(bestFeat, bestThresh,
                    build(leftX.toArray(double[][]::new), leftY.stream().mapToDouble(d -> d).toArray(), depth + 1),
                    build(rightX.toArray(double[][]::new), rightY.stream().mapToDouble(d -> d).toArray(), depth + 1));
            }

            private sealed interface Node permits Leaf, SplitNode {
                double predict(double[] x);
            }
            private record Leaf(double value) implements Node {
                public double predict(double[] x) { return value; }
            }
            private record SplitNode(int feature, double threshold, Node left, Node right) implements Node {
                public double predict(double[] x) { return x[feature] <= threshold ? left.predict(x) : right.predict(x); }
            }
        }
    }

    // -- Utility --

    private static double[][] selectCols(double[][] X, boolean[] mask) {
        int p = 0;
        for (boolean b : mask) if (b) p++;
        double[][] out = new double[X.length][p];
        for (int i = 0; i < X.length; i++) {
            int idx = 0;
            for (int j = 0; j < mask.length; j++) if (mask[j]) out[i][idx++] = X[i][j];
        }
        return out;
    }
}
