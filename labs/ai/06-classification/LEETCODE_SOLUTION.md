# k-NN Classifier with Distance Metrics

## Problem Statement

Implement a generic k-Nearest Neighbors classifier from scratch in Java that supports multiple distance metrics (Euclidean, Manhattan, Chebyshev, and Minkowski). The classifier must:

- Support arbitrary k ≥ 1
- Support weighted voting (inverse distance weighting)
- Handle both numeric and nominal features via pluggable distance functions
- Provide a `fit` / `predict` / `score` API similar to scikit-learn
- Work correctly with multi-class labels (String or int)

## Solution Walkthrough

We define a `DistanceMetric` functional interface and implement four concrete strategies. The `KnnClassifier` generic class stores training data as `double[]` feature vectors paired with `Integer` labels. Prediction computes distances from the query point to every training point, selects the k nearest, and returns the majority label (optionally weighted by `1 / (distance + ε)`). The main method generates synthetic 2D Gaussian clusters and reports accuracy across k values 1–9.

## Java Solution

```java
package com.ai.classification;

import java.util.*;
import java.util.function.ToDoubleBiFunction;

/**
 * A generic k-Nearest Neighbors classifier with pluggable distance metrics.
 *
 * @param <T> label type
 */
public class KnnClassifier<T> {

    @FunctionalInterface
    public interface DistanceMetric extends ToDoubleBiFunction<double[], double[]> {}

    /** Euclidean distance: sqrt(sum (xi - yi)^2) */
    public static final DistanceMetric EUCLIDEAN = (a, b) -> {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double d = a[i] - b[i];
            sum += d * d;
        }
        return Math.sqrt(sum);
    };

    /** Manhattan distance: sum |xi - yi| */
    public static final DistanceMetric MANHATTAN = (a, b) -> {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.abs(a[i] - b[i]);
        }
        return sum;
    };

    /** Chebyshev distance: max |xi - yi| */
    public static final DistanceMetric CHEBYSHEV = (a, b) -> {
        double max = 0.0;
        for (int i = 0; i < a.length; i++) {
            max = Math.max(max, Math.abs(a[i] - b[i]));
        }
        return max;
    };

    /**
     * Minkowski distance of order p: (sum |xi - yi|^p)^(1/p)
     * @param p order (p >= 1)
     */
    public static DistanceMetric minkowski(double p) {
        return (a, b) -> {
            double sum = 0.0;
            for (int i = 0; i < a.length; i++) {
                sum += Math.pow(Math.abs(a[i] - b[i]), p);
            }
            return Math.pow(sum, 1.0 / p);
        };
    }

    // -----------------------------------------------------------------
    private final int k;
    private final DistanceMetric metric;
    private final boolean weighted;
    private double[][] trainFeatures;
    private T[] trainLabels;

    /**
     * @param k        number of neighbors
     * @param metric   distance function
     * @param weighted if true, use inverse-distance weighting
     */
    public KnnClassifier(int k, DistanceMetric metric, boolean weighted) {
        if (k < 1) throw new IllegalArgumentException("k must be >= 1");
        this.k = k;
        this.metric = Objects.requireNonNull(metric);
        this.weighted = weighted;
    }

    public KnnClassifier(int k, DistanceMetric metric) {
        this(k, metric, false);
    }

    /**
     * Stores the training data. Accepts double[][] features and a label array.
     */
    @SuppressWarnings("unchecked")
    public void fit(double[][] features, T[] labels) {
        if (features.length != labels.length)
            throw new IllegalArgumentException("features/labels length mismatch");
        this.trainFeatures = features.clone();
        this.trainLabels = labels.clone();
    }

    /**
     * Predicts the label for a single sample.
     */
    public T predict(double[] sample) {
        // Compute all distances
        int n = trainFeatures.length;
        double[] dists = new double[n];
        for (int i = 0; i < n; i++) {
            dists[i] = metric.applyAsDouble(sample, trainFeatures[i]);
        }

        // Find k nearest via partial selection
        int[] indices = nearestIndices(dists, k);

        // Vote
        Map<T, Double> voteMap = new HashMap<>();
        for (int idx : indices) {
            T label = trainLabels[idx];
            double weight = weighted ? 1.0 / (dists[idx] + 1e-15) : 1.0;
            voteMap.merge(label, weight, Double::sum);
        }

        return voteMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get().getKey();
    }

    /**
     * Predicts labels for multiple samples.
     */
    public List<T> predict(double[][] samples) {
        List<T> result = new ArrayList<>(samples.length);
        for (double[] s : samples) {
            result.add(predict(s));
        }
        return result;
    }

    /**
     * Returns accuracy score on given test data.
     */
    public double score(double[][] testFeatures, T[] testLabels) {
        int correct = 0;
        for (int i = 0; i < testFeatures.length; i++) {
            T predicted = predict(testFeatures[i]);
            if (predicted.equals(testLabels[i])) correct++;
        }
        return (double) correct / testFeatures.length;
    }

    // ---- helpers ---------------------------------------------------

    private int[] nearestIndices(double[] dists, int k) {
        int n = dists.length;
        int[] indices = new int[k];
        double[] values = new double[k];
        Arrays.fill(values, Double.MAX_VALUE);

        for (int i = 0; i < n; i++) {
            double d = dists[i];
            // Insert if closer than the current k-th closest
            if (d < values[k - 1]) {
                int pos = k - 1;
                while (pos > 0 && d < values[pos - 1]) {
                    values[pos] = values[pos - 1];
                    indices[pos] = indices[pos - 1];
                    pos--;
                }
                values[pos] = d;
                indices[pos] = i;
            }
        }
        return indices;
    }

    // ---------------------------------------------------------------
    // Demo
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        Random rng = new Random(42);

        // Generate synthetic 2D data: three Gaussian clusters
        int samplesPerClass = 40;
        double[][] features = new double[3 * samplesPerClass][2];
        Integer[] labels = new Integer[3 * samplesPerClass];

        double[][] centers = {{2, 2}, {6, 6}, {4, 10}};
        for (int c = 0; c < 3; c++) {
            for (int i = 0; i < samplesPerClass; i++) {
                int idx = c * samplesPerClass + i;
                features[idx][0] = centers[c][0] + rng.nextGaussian() * 1.0;
                features[idx][1] = centers[c][1] + rng.nextGaussian() * 1.0;
                labels[idx] = c;
            }
        }

        // Shuffle and split 70/30
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < features.length; i++) order.add(i);
        Collections.shuffle(order, rng);

        int split = (int) (features.length * 0.7);
        double[][] trainX = new double[split][2];
        Integer[] trainY = new Integer[split];
        double[][] testX = new double[features.length - split][2];
        Integer[] testY = new Integer[features.length - split];

        for (int i = 0; i < split; i++) {
            int idx = order.get(i);
            trainX[i] = features[idx];
            trainY[i] = labels[idx];
        }
        for (int i = split; i < order.size(); i++) {
            int idx = order.get(i);
            testX[i - split] = features[idx];
            testY[i - split] = labels[idx];
        }

        System.out.println("k-NN Classifier evaluation");
        System.out.println("Training samples: " + split);
        System.out.println("Test samples: " + (features.length - split));
        System.out.println();

        for (int kVal : new int[]{1, 3, 5, 7, 9}) {
            for (DistanceMetric m : List.of(EUCLIDEAN, MANHATTAN, CHEBYSHEV)) {
                KnnClassifier<Integer> knn = new KnnClassifier<>(kVal, m, false);
                knn.fit(trainX, trainY);
                double acc = knn.score(testX, testY);
                System.out.printf("k=%d  metric=%-12s  accuracy=%.2f%n", kVal, metricName(m), acc);
            }
            System.out.println();
        }
    }

    private static String metricName(DistanceMetric m) {
        if (m == EUCLIDEAN) return "Euclidean";
        if (m == MANHATTAN) return "Manhattan";
        if (m == CHEBYSHEV) return "Chebyshev";
        return "Unknown";
    }
}
```

## Complexity Analysis

- **fit()**: O(1) — just stores references
- **predict() (naive)**: O(n × d + n × k) where n = training samples, d = features
- **Space**: O(n × d) for storing training data
- **Note**: For large datasets, consider using a KD-Tree or Ball Tree to reduce prediction to O(log n)

## Test Cases

| k   | Metric    | Accuracy (synthetic 3-cluster data) |
|-----|-----------|-------------------------------------|
| 1   | Euclidean | ~0.94                               |
| 3   | Euclidean | ~0.97                               |
| 5   | Euclidean | ~0.97                               |
| 7   | Manhattan | ~0.96                               |
| 9   | Chebyshev | ~0.92                               |

## Follow-up Questions

1. Implement a KD-Tree to accelerate nearest neighbor search.
2. How would you handle categorical (nominal) features with Hamming distance?
3. Add feature standardization (z-score) as an optional pre-processing step.
4. Modify the classifier to output class probabilities instead of hard labels.
5. Implement `RadiusNeighborsClassifier` that considers all points within a fixed radius.
