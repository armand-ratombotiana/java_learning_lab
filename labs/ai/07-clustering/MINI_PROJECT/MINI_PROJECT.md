# Clustering - Mini Project

## Mini Project: Implementing K-means, DBSCAN, and Hierarchical Clustering

### Project Overview
Implement three clustering algorithms from scratch in Java, compare their performance on different datasets, and visualize results.

### Duration
3-4 hours

---

## Project Structure

```
clustering/
├── src/main/java/com/ml/clustering/
│   ├── Clusterer.java
│   ├── kmeans/
│   │   ├── KMeansClusterer.java
│   │   └── ElbowMethod.java
│   ├── dbscan/
│   │   ├── DBSCANClusterer.java
│   │   └── DensityReachability.java
│   ├── hierarchical/
│   │   ├── HierarchicalClusterer.java
│   │   └── Dendrogram.java
│   ├── evaluation/
│   │   ├── SilhouetteScore.java
│   │   └── ClusterMetrics.java
│   └── Main.java
```

---

## Part 1: K-means Implementation

```java
package com.ml.clustering.kmeans;

import com.ml.clustering.Clusterer;

public class KMeansClusterer implements Clusterer {

    private int k;
    private int maxIterations;
    private double tolerance;
    private double[][] centroids;
    private int[] labels;

    public KMeansClusterer(int k) {
        this(k, 100, 1e-6);
    }

    public KMeansClusterer(int k, int maxIterations, double tolerance) {
        this.k = k;
        this.maxIterations = maxIterations;
        this.tolerance = tolerance;
    }

    @Override
    public void fit(double[][] X) {
        int n = X.length;
        int d = X[0].length;

        // Initialize centroids using K-means++
        centroids = initializeCentroids(X);
        labels = new int[n];

        for (int iter = 0; iter < maxIterations; iter++) {
            // Assignment step
            boolean changed = false;
            for (int i = 0; i < n; i++) {
                int nearest = findNearestCentroid(X[i]);
                if (labels[i] != nearest) {
                    labels[i] = nearest;
                    changed = true;
                }
            }

            if (!changed) break;

            // Update step
            double[][] newCentroids = new double[k][d];
            int[] counts = new int[k];

            for (int i = 0; i < n; i++) {
                int cluster = labels[i];
                for (int j = 0; j < d; j++) {
                    newCentroids[cluster][j] += X[i][j];
                }
                counts[cluster]++;
            }

            for (int c = 0; c < k; c++) {
                if (counts[c] > 0) {
                    for (int j = 0; j < d; j++) {
                        newCentroids[c][j] /= counts[c];
                    }
                } else {
                    // Reinitialize empty cluster
                    newCentroids[c] = X[(int)(Math.random() * n)].clone();
                }
            }

            // Check convergence
            double maxShift = 0;
            for (int c = 0; c < k; c++) {
                double shift = distance(centroids[c], newCentroids[c]);
                maxShift = Math.max(maxShift, shift);
            }

            centroids = newCentroids;

            if (maxShift < tolerance) break;
        }
    }

    private double[][] initializeCentroids(double[][] X) {
        double[][] centroids = new double[k][];
        Random rand = new Random(42);

        // First centroid: random point
        centroids[0] = X[rand.nextInt(X.length)].clone();

        // Remaining centroids: K-means++ selection
        for (int c = 1; c < k; c++) {
            double[] distances = new double[X.length];
            double totalDist = 0;

            for (int i = 0; i < X.length; i++) {
                double minDist = Double.MAX_VALUE;
                for (int j = 0; j < c; j++) {
                    double d = distance(X[i], centroids[j]);
                    minDist = Math.min(minDist, d);
                }
                distances[i] = minDist * minDist;
                totalDist += distances[i];
            }

            // Choose next centroid with probability proportional to distance²
            double r = rand.nextDouble() * totalDist;
            double cumSum = 0;
            for (int i = 0; i < X.length; i++) {
                cumSum += distances[i];
                if (cumSum >= r) {
                    centroids[c] = X[i].clone();
                    break;
                }
            }
        }

        return centroids;
    }

    private int findNearestCentroid(double[] point) {
        int nearest = 0;
        double minDist = distance(point, centroids[0]);

        for (int c = 1; c < k; c++) {
            double d = distance(point, centroids[c]);
            if (d < minDist) {
                minDist = d;
                nearest = c;
            }
        }
        return nearest;
    }

    private double distance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    @Override
    public int[] predict(double[][] X) {
        int[] predictions = new int[X.length];
        for (int i = 0; i < X.length; i++) {
            predictions[i] = findNearestCentroid(X[i]);
        }
        return predictions;
    }

    public double[][] getCentroids() {
        return centroids;
    }

    public double withinClusterVariance() {
        double sum = 0;
        int n = labels.length;

        for (int i = 0; i < n; i++) {
            sum += distanceSquared(labels[i]);
        }

        return sum / n;
    }

    private double distanceSquared(int cluster) {
        // For calculation - simplified
        return 0;
    }
}
```

---

## Part 2: DBSCAN Implementation

```java
package com.ml.clustering.dbscan;

import com.ml.clustering.Clusterer;

public class DBSCANClusterer implements Clusterer {

    private double eps;
    private int minPts;
    private int[] labels;
    private static final int NOISE = -1;
    private static final int UNVISITED = -2;

    public DBSCANClusterer(double eps, int minPts) {
        this.eps = eps;
        this.minPts = minPts;
    }

    @Override
    public void fit(double[][] X) {
        int n = X.length;
        labels = new int[n];

        for (int i = 0; i < n; i++) {
            labels[i] = UNVISITED;
        }

        int clusterId = 0;

        for (int i = 0; i < n; i++) {
            if (labels[i] != UNVISITED) continue;

            java.util.List<Integer> neighbors = getNeighbors(X, i);

            if (neighbors.size() < minPts) {
                labels[i] = NOISE;
            } else {
                expandCluster(X, i, neighbors, clusterId);
                clusterId++;
            }
        }
    }

    private void expandCluster(double[][] X, int pointIdx,
                               java.util.List<Integer> neighbors, int clusterId) {
        labels[pointIdx] = clusterId;

        java.util.Queue<Integer> queue = new java.util.ArrayDeque<>(neighbors);

        while (!queue.isEmpty()) {
            int currentIdx = queue.poll();

            if (labels[currentIdx] == NOISE) {
                labels[currentIdx] = clusterId;
            }

            if (labels[currentIdx] != UNVISITED) continue;

            labels[currentIdx] = clusterId;

            java.util.List<Integer> currentNeighbors = getNeighbors(X, currentIdx);

            if (currentNeighbors.size() >= minPts) {
                for (int neighbor : currentNeighbors) {
                    if (labels[neighbor] == UNVISITED || labels[neighbor] == NOISE) {
                        queue.add(neighbor);
                    }
                }
            }
        }
    }

    private java.util.List<Integer> getNeighbors(double[][] X, int pointIdx) {
        java.util.List<Integer> neighbors = new java.util.ArrayList<>();
        double[] point = X[pointIdx];

        for (int i = 0; i < X.length; i++) {
            if (distance(point, X[i]) <= eps) {
                neighbors.add(i);
            }
        }

        return neighbors;
    }

    private double distance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    @Override
    public int[] predict(double[][] X) {
        // For clustering, predict is the same as fit on new data
        // Simplified - would need refitting
        return labels.clone();
    }

    public int getNoiseCount() {
        int count = 0;
        for (int label : labels) {
            if (label == NOISE) count++;
        }
        return count;
    }
}
```

---

## Part 3: Hierarchical Clustering

```java
package com.ml.clustering.hierarchical;

import com.ml.clustering.Clusterer;

import java.util.*;

public class HierarchicalClusterer implements Clusterer {

    private LinkageType linkage;
    private int nClusters;
    private int[] labels;
    private List<List<double[]>> clusters;

    public enum LinkageType {
        SINGLE, COMPLETE, AVERAGE
    }

    public HierarchicalClusterer(int nClusters, LinkageType linkage) {
        this.nClusters = nClusters;
        this.linkage = linkage;
    }

    @Override
    public void fit(double[][] X) {
        // Initialize each point as its own cluster
        clusters = new ArrayList<>();
        for (double[] point : X) {
            List<double[]> cluster = new ArrayList<>();
            cluster.add(point);
            clusters.add(cluster);
        }

        // Merge clusters until we have nClusters
        while (clusters.size() > nClusters) {
            double minDist = Double.MAX_VALUE;
            int mergeI = 0, mergeJ = 1;

            // Find closest pair of clusters
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    double d = clusterDistance(clusters.get(i), clusters.get(j));
                    if (d < minDist) {
                        minDist = d;
                        mergeI = i;
                        mergeJ = j;
                    }
                }
            }

            // Merge clusters
            clusters.get(mergeI).addAll(clusters.get(mergeJ));
            clusters.remove(mergeJ);
        }

        // Assign labels
        labels = new int[X.length];
        for (int i = 0; i < clusters.size(); i++) {
            for (double[] point : clusters.get(i)) {
                // Find original index
                for (int j = 0; j < X.length; j++) {
                    if (Arrays.equals(point, X[j])) {
                        labels[j] = i;
                        break;
                    }
                }
            }
        }
    }

    private double clusterDistance(List<double[]> c1, List<double[]> c2) {
        return switch (linkage) {
            case SINGLE -> {
                double minDist = Double.MAX_VALUE;
                for (double[] p1 : c1) {
                    for (double[] p2 : c2) {
                        double d = distance(p1, p2);
                        if (d < minDist) minDist = d;
                    }
                }
                yield minDist;
            }
            case COMPLETE -> {
                double maxDist = 0;
                for (double[] p1 : c1) {
                    for (double[] p2 : c2) {
                        double d = distance(p1, p2);
                        if (d > maxDist) maxDist = d;
                    }
                }
                yield maxDist;
            }
            case AVERAGE -> {
                double totalDist = 0;
                int count = 0;
                for (double[] p1 : c1) {
                    for (double[] p2 : c2) {
                        totalDist += distance(p1, p2);
                        count++;
                    }
                }
                yield totalDist / count;
            }
        };
    }

    private double distance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    @Override
    public int[] predict(double[][] X) {
        // For Agglomerative, we assign new points to nearest existing cluster
        int[] predictions = new int[X.length];

        for (int i = 0; i < X.length; i++) {
            double minDist = Double.MAX_VALUE;
            int nearestCluster = 0;

            for (int c = 0; c < clusters.size(); c++) {
                // Use centroid
                double[] centroid = computeCentroid(clusters.get(c));
                double d = distance(X[i], centroid);
                if (d < minDist) {
                    minDist = d;
                    nearestCluster = c;
                }
            }

            predictions[i] = nearestCluster;
        }

        return predictions;
    }

    private double[] computeCentroid(List<double[]> cluster) {
        int d = cluster.get(0).length;
        double[] centroid = new double[d];

        for (double[] point : cluster) {
            for (int i = 0; i < d; i++) {
                centroid[i] += point[i];
            }
        }

        for (int i = 0; i < d; i++) {
            centroid[i] /= cluster.size();
        }

        return centroid;
    }
}
```

---

## Part 4: Evaluation Metrics

```java
package com.ml.clustering.evaluation;

import java.util.*;

public class SilhouetteScore {

    public static double compute(double[][] X, int[] labels) {
        int n = X.length;
        if (n == 0) return 0;

        int k = Arrays.stream(labels).max().orElse(0) + 1;

        // Compute average distance to points in same cluster
        double[] a = new double[n];
        int[] clusterSize = new int[k];

        for (int i = 0; i < n; i++) {
            int cluster = labels[i];
            clusterSize[cluster]++;

            double sumDist = 0;
            int count = 0;

            for (int j = 0; j < n; j++) {
                if (i != j && labels[j] == cluster) {
                    sumDist += distance(X[i], X[j]);
                    count++;
                }
            }

            a[i] = count > 0 ? sumDist / count : 0;
        }

        // Compute average distance to nearest other cluster
        double[] b = new double[n];

        for (int i = 0; i < n; i++) {
            int currentCluster = labels[i];
            double minAvgDist = Double.MAX_VALUE;

            for (int c = 0; c < k; c++) {
                if (c == currentCluster || clusterSize[c] == 0) continue;

                double sumDist = 0;
                for (int j = 0; j < n; j++) {
                    if (labels[j] == c) {
                        sumDist += distance(X[i], X[j]);
                    }
                }

                double avgDist = sumDist / clusterSize[c];
                if (avgDist < minAvgDist) {
                    minAvgDist = avgDist;
                }
            }

            b[i] = minAvgDist == Double.MAX_VALUE ? 0 : minAvgDist;
        }

        // Compute silhouette score
        double totalSilhouette = 0;
        for (int i = 0; i < n; i++) {
            double s = 0;
            if (clusterSize[labels[i]] > 1) {
                double maxAB = Math.max(a[i], b[i]);
                if (maxAB > 0) {
                    s = (b[i] - a[i]) / maxAB;
                }
            }
            totalSilhouette += s;
        }

        return totalSilhouette / n;
    }

    private static double distance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
}
```

---

## Part 5: Main Example

```java
package com.ml.clustering;

import com.ml.clustering.kmeans.KMeansClusterer;
import com.ml.clustering.dbscan.DBSCANClusterer;
import com.ml.clustering.hierarchical.HierarchicalClusterer;
import com.ml.clustering.evaluation.SilhouetteScore;

public class Main {

    public static void main(String[] args) {
        // Generate synthetic data - three clusters
        Random rand = new Random(42);
        int n = 300;
        List<double[]> points = new ArrayList<>();
        List<Integer> trueLabels = new ArrayList<>();

        // Cluster 1
        for (int i = 0; i < n/3; i++) {
            points.add(new double[]{rand.nextGaussian() + 0,
                                    rand.nextGaussian() + 0});
            trueLabels.add(0);
        }

        // Cluster 2
        for (int i = 0; i < n/3; i++) {
            points.add(new double[]{rand.nextGaussian() + 5,
                                    rand.nextGaussian() + 5});
            trueLabels.add(1);
        }

        // Cluster 3
        for (int i = 0; i < n/3; i++) {
            points.add(new double[]{rand.nextGaussian() - 3,
                                    rand.nextGaussian() + 4});
            trueLabels.add(2);
        }

        double[][] X = points.toArray(new double[0][]);
        int[] yTrue = new int[trueLabels.size()];
        for (int i = 0; i < trueLabels.size(); i++) yTrue[i] = trueLabels.get(i);

        System.out.println("=== Clustering Algorithm Comparison ===\n");

        // K-means
        KMeansClusterer kmeans = new KMeansClusterer(3);
        kmeans.fit(X);
        int[] kmeansLabels = kmeans.predict(X);
        double kmeansSilhouette = SilhouetteScore.compute(X, kmeansLabels);
        System.out.println("K-means Silhouette: " + kmeansSilhouette);

        // DBSCAN
        DBSCANClusterer dbscan = new DBSCANClusterer(1.5, 5);
        dbscan.fit(X);
        int[] dbscanLabels = dbscan.predict(X);
        double dbscanSilhouette = SilhouetteScore.compute(X, dbscanLabels);
        System.out.println("DBSCAN Silhouette: " + dbscanSilhouette);
        System.out.println("DBSCAN Noise points: " + dbscan.getNoiseCount());

        // Hierarchical
        HierarchicalClusterer hierarchical = new HierarchicalClusterer(
            3, HierarchicalClusterer.LinkageType.AVERAGE);
        hierarchical.fit(X);
        int[] hierLabels = hierarchical.predict(X);
        double hierSilhouette = SilhouetteScore.compute(X, hierLabels);
        System.out.println("Hierarchical Silhouette: " + hierSilhouette);
    }
}
```

---

## Tasks

1. Implement elbow method for K selection
2. Add support for different distance metrics
3. Create dendrogram visualization
4. Test on real datasets (iris, wine)