# Anomaly Detection - Mini Project

## Mini Project: Implementing Anomaly Detection Algorithms

### Part 1: Z-Score Based Detection

```java
package com.ml.anomaly;

public class ZScoreDetector {
    private double threshold;
    private double mean;
    private double std;

    public ZScoreDetector(double threshold) {
        this.threshold = threshold;
    }

    public void fit(double[][] X) {
        int d = X[0].length;
        mean = new double[d];
        std = new double[d];

        for (int j = 0; j < d; j++) {
            double sum = 0;
            for (int i = 0; i < X.length; i++) sum += X[i][j];
            mean[j] = sum / X.length;

            double sqSum = 0;
            for (int i = 0; i < X.length; i++) {
                sqSum += (X[i][j] - mean[j]) * (X[i][j] - mean[j]);
            }
            std[j] = Math.sqrt(sqSum / X.length);
            if (std[j] < 1e-10) std[j] = 1;
        }
    }

    public int[] predict(double[][] X) {
        int[] predictions = new int[X.length];

        for (int i = 0; i < X.length; i++) {
            double maxZScore = 0;
            for (int j = 0; j < X[0].length; j++) {
                double z = Math.abs((X[i][j] - mean[j]) / std[j]);
                maxZScore = Math.max(maxZScore, z);
            }
            predictions[i] = maxZScore > threshold ? 1 : 0;
        }

        return predictions;
    }

    public double[] getAnomalyScores(double[][] X) {
        double[] scores = new double[X.length];

        for (int i = 0; i < X.length; i++) {
            double sumSq = 0;
            for (int j = 0; j < X[0].length; j++) {
                double z = (X[i][j] - mean[j]) / std[j];
                sumSq += z * z;
            }
            scores[i] = Math.sqrt(sumSq);
        }

        return scores;
    }
}
```

### Part 2: Isolation Forest

```java
package com.ml.anomaly;

import java.util.*;

public class IsolationForest {
    private int numTrees;
    private int sampleSize;
    private TreeNode[] trees;

    private static class TreeNode {
        int splitFeature;
        double splitValue;
        TreeNode left;
        TreeNode right;
        boolean isLeaf;

        TreeNode(int feature, double value, TreeNode left, TreeNode right) {
            this.splitFeature = feature;
            this.splitValue = value;
            this.left = left;
            this.right = right;
            this.isLeaf = false;
        }

        TreeNode() {
            this.isLeaf = true;
        }
    }

    public IsolationForest(int numTrees, int sampleSize) {
        this.numTrees = numTrees;
        this.sampleSize = sampleSize;
    }

    public void fit(double[][] X) {
        trees = new TreeNode[numTrees];
        Random rand = new Random(42);

        for (int t = 0; t < numTrees; t++) {
            // Sample subset
            double[][] sample = sampleFromData(X, sampleSize, rand);
            trees[t] = buildTree(sample, 0, getMaxDepth(sampleSize), rand);
        }
    }

    private double[][] sampleFromData(double[][] X, int size, Random rand) {
        int n = X.length;
        double[][] sample = new double[size][];
        for (int i = 0; i < size; i++) {
            sample[i] = X[rand.nextInt(n)].clone();
        }
        return sample;
    }

    private TreeNode buildTree(double[][] X, int depth, int maxDepth, Random rand) {
        if (depth >= maxDepth || X.length <= 1) {
            return new TreeNode();
        }

        int d = X[0].length;
        int feature = rand.nextInt(d);

        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (double[] row : X) {
            min = Math.min(min, row[feature]);
            max = Math.max(max, row[feature]);
        }

        if (min == max) return new TreeNode();

        double split = min + rand.nextDouble() * (max - min);

        List<double[]> leftList = new ArrayList<>();
        List<double[]> rightList = new ArrayList<>();

        for (double[] row : X) {
            if (row[feature] < split) leftList.add(row);
            else rightList.add(row);
        }

        if (leftList.isEmpty() || rightList.isEmpty()) return new TreeNode();

        double[][] left = leftList.toArray(new double[0][]);
        double[][] right = rightList.toArray(new double[0][]);

        return new TreeNode(feature, split,
            buildTree(left, depth + 1, maxDepth, rand),
            buildTree(right, depth + 1, maxDepth, rand));
    }

    private int getMaxDepth(int n) {
        return (int) Math.ceil(Math.log(n) / Math.log(2));
    }

    public double[] predict(double[][] X) {
        double[] scores = new double[X.length];

        for (int i = 0; i < X.length; i++) {
            double avgPathLength = 0;
            for (TreeNode tree : trees) {
                avgPathLength += pathLength(X[i], tree, 0);
            }
            avgPathLength /= numTrees;

            double c = averagePathLength(sampleSize);
            scores[i] = Math.pow(2, -avgPathLength / c);
        }

        return scores;
    }

    private double pathLength(double[] point, TreeNode node, int depth) {
        if (node.isLeaf) {
            return depth + cFactor(node);
        }

        if (point[node.splitFeature] < node.splitValue) {
            return pathLength(point, node.left, depth + 1);
        } else {
            return pathLength(point, node.right, depth + 1);
        }
    }

    private double cFactor(int n) {
        if (n <= 1) return 0;
        return 2 * (Math.log(n - 1) + 0.57721566) - 2 * (n - 1) / n;
    }

    private double averagePathLength(int n) {
        return cFactor(n);
    }
}
```

### Part 3: Main Example

```java
package com.ml.anomaly;

public class Main {
    public static void main(String[] args) {
        // Generate data with anomalies
        java.util.Random rand = new java.util.Random(42);
        int n = 500;
        double[][] normal = new double[n][2];

        for (int i = 0; i < n; i++) {
            normal[i][0] = rand.nextGaussian() * 2 + 5;
            normal[i][1] = rand.nextGaussian() * 2 + 5;
        }

        // Add anomalies
        double[][] data = new double[n + 10][2];
        for (int i = 0; i < n; i++) {
            data[i] = normal[i];
        }
        for (int i = 0; i < 10; i++) {
            data[n + i][0] = rand.nextDouble() * 20;
            data[n + i][1] = rand.nextDouble() * 20;
        }

        // Z-Score Detection
        ZScoreDetector zscore = new ZScoreDetector(3.0);
        zscore.fit(normal);
        int[] zPred = zscore.predict(data);
        System.out.println("Z-Score detected: " + countAnomalies(zPred) + " anomalies");

        // Isolation Forest
        IsolationForest iforest = new IsolationForest(100, 256);
        iforest.fit(normal);
        double[] scores = iforest.predict(data);

        int[] ifPred = new int[scores.length];
        for (int i = 0; i < scores.length; i++) {
            ifPred[i] = scores[i] > 0.6 ? 1 : 0;
        }
        System.out.println("Isolation Forest detected: " + countAnomalies(ifPred) + " anomalies");
    }

    private static int countAnomalies(int[] predictions) {
        int count = 0;
        for (int p : predictions) if (p == 1) count++;
        return count;
    }
}
```

### Tasks

1. Implement One-Class SVM
2. Add Mahalanobis distance detection
3. Implement LOF algorithm
4. Add autoencoder-based detection