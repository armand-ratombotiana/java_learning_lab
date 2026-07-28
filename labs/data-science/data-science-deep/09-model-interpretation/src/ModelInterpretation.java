package com.datascience.deep.lab09;

import java.util.*;
import java.util.function.DoubleUnaryOperator;

public final class ModelInterpretation {

    @FunctionalInterface
    public interface Predictor {
        double predict(double[] features);
        default double[] predictBatch(double[][] X) {
            return Arrays.stream(X).mapToDouble(this::predict).toArray();
        }
    }

    // -- Kernel SHAP --

    public static class KernelSHAP {
        private final Predictor model;
        private final double[][] background;
        private final int nSamples;
        private final Random rng;

        public KernelSHAP(Predictor model, double[][] background, int nSamples) {
            this.model = model;
            this.background = background;
            this.nSamples = nSamples;
            this.rng = new Random(42L);
        }

        public double[] explain(double[] instance) {
            int m = instance.length;
            double baseline = Arrays.stream(model.predictBatch(background)).average().orElseThrow();
            double[][] coalitions = new double[nSamples][m];
            double[] preds = new double[nSamples];
            double[] weights = new double[nSamples];

            for (int s = 0; s < nSamples; s++) {
                int size = 0;
                double[] input = new double[m];
                int bg = rng.nextInt(background.length);
                for (int j = 0; j < m; j++) {
                    if (rng.nextDouble() < 0.5) {
                        coalitions[s][j] = 1.0;
                        input[j] = instance[j];
                        size++;
                    } else {
                        input[j] = background[bg][j];
                    }
                }
                preds[s] = model.predict(input) - baseline;
                weights[s] = shapKernelWeight(size, m);
            }

            double[][] Z = new double[nSamples][m + 1];
            for (int s = 0; s < nSamples; s++) {
                Z[s][0] = 1.0;
                System.arraycopy(coalitions[s], 0, Z[s], 1, m);
            }
            double[] phi = weightedOLS(Z, preds, weights);
            return Arrays.copyOfRange(phi, 1, phi.length);
        }

        private double shapKernelWeight(int size, int m) {
            if (size == 0 || size == m) return 1e6;
            return (double) (m - 1) / (size * (m - size) * binomial(m, size));
        }
    }

    // -- Permutation Importance --

    public record FeatureImportance(int index, double importance, double std) {}

    public static List<FeatureImportance> permutationImportance(Predictor model, double[][] X, double[] y, int repeats) {
        int p = X[0].length;
        double baseline = rmse(model.predictBatch(X), y);
        List<FeatureImportance> result = new ArrayList<>();
        Random rng = new Random(42L);

        for (int j = 0; j < p; j++) {
            double[] scores = new double[repeats];
            for (int r = 0; r < repeats; r++) {
                double[][] Xp = deepCopy(X);
                shuffleColumn(Xp, j, rng);
                scores[r] = rmse(model.predictBatch(Xp), y) - baseline;
            }
            double mean = Arrays.stream(scores).average().orElseThrow();
            double std = Math.sqrt(Arrays.stream(scores).map(s -> Math.pow(s - mean, 2)).sum() / (repeats - 1));
            result.add(new FeatureImportance(j, mean, std));
        }
        result.sort(Comparator.comparingDouble(FeatureImportance::importance).reversed());
        return result;
    }

    // -- Partial Dependence --

    public record PDPPoint(double featureValue, double averagePrediction) {}

    public static List<PDPPoint> partialDependence(Predictor model, double[][] X, int feature, double[] grid) {
        List<PDPPoint> points = new ArrayList<>();
        for (double g : grid) {
            double sum = 0;
            for (double[] row : X) {
                double[] modified = row.clone();
                modified[feature] = g;
                sum += model.predict(modified);
            }
            points.add(new PDPPoint(g, sum / X.length));
        }
        return points;
    }

    // -- ICE (Individual Conditional Expectation) --

    public record ICEPoint(double featureValue, int instanceIdx, double prediction) {}

    public static List<ICEPoint> individualConditionalExpectation(Predictor model, double[][] X, int feature, double[] grid) {
        List<ICEPoint> points = new ArrayList<>();
        for (int i = 0; i < Math.min(X.length, 50); i++) {
            for (double g : grid) {
                double[] modified = X[i].clone();
                modified[feature] = g;
                points.add(new ICEPoint(g, i, model.predict(modified)));
            }
        }
        return points;
    }

    // -- Friedman's H-Statistic --

    public record HStatistic(double h, int featureI, int featureJ) {
        public static HStatistic compute(Predictor model, double[][] X, int i, int j) {
            int n = X.length;
            double num = 0, den = 0;
            for (double[] row : X) {
                double pdJk = pdPair(model, X, i, j, row[i], row[j]);
                double pdJ = pdSingle(model, X, i, row[i]);
                double pdK = pdSingle(model, X, j, row[j]);
                double diff = pdJk - pdJ - pdK;
                num += diff * diff;
                den += pdJk * pdJk;
            }
            return new HStatistic(Math.sqrt(num / den), i, j);
        }

        private static double pdSingle(Predictor model, double[][] X, int feat, double val) {
            double sum = 0;
            for (double[] row : X) { double[] m = row.clone(); m[feat] = val; sum += model.predict(m); }
            return sum / X.length;
        }

        private static double pdPair(Predictor model, double[][] X, int i, int j, double vi, double vj) {
            double sum = 0;
            for (double[] row : X) { double[] m = row.clone(); m[i] = vi; m[j] = vj; sum += model.predict(m); }
            return sum / X.length;
        }
    }

    // -- Utilities --

    private static double rmse(double[] pred, double[] actual) {
        double sum = 0;
        for (int i = 0; i < pred.length; i++) sum += Math.pow(pred[i] - actual[i], 2);
        return Math.sqrt(sum / pred.length);
    }

    private static double[][] deepCopy(double[][] X) {
        double[][] c = new double[X.length][];
        for (int i = 0; i < X.length; i++) c[i] = X[i].clone();
        return c;
    }

    private static void shuffleColumn(double[][] X, int col, Random rng) {
        int n = X.length;
        for (int i = n - 1; i > 0; i--) {
            int k = rng.nextInt(i + 1);
            double tmp = X[i][col]; X[i][col] = X[k][col]; X[k][col] = tmp;
        }
    }

    private static double binomial(int n, int k) {
        if (k < 0 || k > n) return 0;
        double res = 1;
        for (int i = 1; i <= k; i++) res = res * (n - k + i) / i;
        return res;
    }

    private static double[] weightedOLS(double[][] Z, double[] y, double[] w) {
        int n = Z.length, p = Z[0].length;
        double[][] ZtWZ = new double[p][p];
        double[] ZtWy = new double[p];
        for (int i = 0; i < p; i++) {
            for (int j = 0; j < p; j++) {
                double s = 0;
                for (int k = 0; k < n; k++) s += Z[k][i] * w[k] * Z[k][j];
                ZtWZ[i][j] = s;
            }
            double s = 0;
            for (int k = 0; k < n; k++) s += Z[k][i] * w[k] * y[k];
            ZtWy[i] = s;
        }
        double[][] inv = invert(ZtWZ);
        double[] beta = new double[p];
        for (int i = 0; i < p; i++) {
            for (int j = 0; j < p; j++) beta[i] += inv[i][j] * ZtWy[j];
        }
        return beta;
    }

    private static double[][] invert(double[][] A) {
        int n = A.length;
        double[][] aug = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, aug[i], 0, n);
            aug[i][n + i] = 1.0;
        }
        for (int col = 0; col < n; col++) {
            int pivot = col;
            for (int row = col + 1; row < n; row++) if (Math.abs(aug[row][col]) > Math.abs(aug[pivot][col])) pivot = row;
            double[] tmp = aug[col]; aug[col] = aug[pivot]; aug[pivot] = tmp;
            double div = aug[col][col];
            for (int j = 0; j < 2 * n; j++) aug[col][j] /= div;
            for (int row = 0; row < n; row++) {
                if (row == col) continue;
                double mult = aug[row][col];
                for (int j = 0; j < 2 * n; j++) aug[row][j] -= mult * aug[col][j];
            }
        }
        double[][] inv = new double[n][n];
        for (int i = 0; i < n; i++) System.arraycopy(aug[i], n, inv[i], 0, n);
        return inv;
    }
}
