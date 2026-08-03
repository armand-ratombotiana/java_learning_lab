# Problem Walkthrough: K-Means & Hierarchical Clustering

## Problem 1: Spotify Playlist Discovery — Company: Spotify

### Interview Scenario
"You're at Spotify. The editorial team wants to auto-suggest new playlist genres
by clustering tracks on two audio features — energy and danceability, both on a
0–10 scale. You don't know how many genres exist, and the product review needs
both a number and a story: 'these three clusters are the genres, and here are
their centers.'"

### The Problem
Cluster tracks with K-Means. It must: (1) Run Lloyd's algorithm with
assignment-then-update iterations, (2) Report inertia (WCSS) for K = 2..5 so the
team can apply the elbow method, (3) Identify the elbow K, (4) Print the final
centroids of the chosen K as the genre definitions, (5) Handle empty clusters
without dividing by zero.

### Solution Walkthrough
- Step 1: Encode 12 tracks as `(energy, danceability)` — three compact blobs of
  four tracks each, the lab's synthetic layout.
- Step 2: `kMeans(data, K, 100)` — the lab's Lloyd's loop with seeded random
  initialization and nearest-centroid assignment.
- Step 3: `inertia(data, labels, K)` — recompute centroids from labels and sum
  squared distances — for K = 2..5.
- Step 4: Read the elbow: 65.66 at K=2 collapses to 0.45 at K=3, then plateaus
  (0.39, 0.34) — three real clusters.
- Step 5: `centroids(data, labels, 3)` — an extension of the lab that
  materializes the cluster centers — and print each genre's center.

### Code
```java
package com.ml.lab07;

import java.util.*;

/**
 * Spotify-style playlist discovery via K-Means.
 * <p>
 * Clusters tracks by (energy, danceability) with the Lab 07 Lloyd's
 * algorithm, computes inertia per K for the elbow method, and prints
 * the final centroids for the chosen K.
 */
public class GenreClusters {

    public static double dist(double[] a, double[] b) {
        double s = 0;
        for (int i = 0; i < a.length; i++) s += (a[i] - b[i]) * (a[i] - b[i]);
        return Math.sqrt(s);
    }

    public static int[] kMeans(double[][] data, int K, int maxIter) {
        int n = data.length, dim = data[0].length;
        double[][] centroids = new double[K][dim];
        Random rng = new Random(42);
        for (int k = 0; k < K; k++) centroids[k] = data[rng.nextInt(n)].clone();

        int[] labels = new int[n];
        for (int iter = 0; iter < maxIter; iter++) {
            for (int i = 0; i < n; i++) {
                int best = 0;
                double bestDist = dist(data[i], centroids[0]);
                for (int k = 1; k < K; k++) {
                    double d = dist(data[i], centroids[k]);
                    if (d < bestDist) { bestDist = d; best = k; }
                }
                labels[i] = best;
            }
            double[][] sums = new double[K][dim];
            int[] counts = new int[K];
            for (int i = 0; i < n; i++) {
                int c = labels[i];
                for (int j = 0; j < dim; j++) sums[c][j] += data[i][j];
                counts[c]++;
            }
            for (int k = 0; k < K; k++) {
                if (counts[k] == 0) continue;
                for (int j = 0; j < dim; j++) centroids[k][j] = sums[k][j] / counts[k];
            }
        }
        return labels;
    }

    public static double inertia(double[][] data, int[] labels, int K) {
        int dim = data[0].length;
        double[][] centroids = new double[K][dim];
        int[] counts = new int[K];
        for (int i = 0; i < data.length; i++) {
            int c = labels[i];
            for (int j = 0; j < dim; j++) centroids[c][j] += data[i][j];
            counts[c]++;
        }
        for (int k = 0; k < K; k++) {
            if (counts[k] > 0)
                for (int j = 0; j < dim; j++) centroids[k][j] /= counts[k];
        }
        double inert = 0;
        for (int i = 0; i < data.length; i++) {
            int c = labels[i];
            inert += dist(data[i], centroids[c]) * dist(data[i], centroids[c]);
        }
        return inert;
    }

    // Extended from the lab: materialize the final centroids for a label set.
    public static double[][] centroids(double[][] data, int[] labels, int K) {
        int dim = data[0].length;
        double[][] centroids = new double[K][dim];
        int[] counts = new int[K];
        for (int i = 0; i < data.length; i++) {
            int c = labels[i];
            for (int j = 0; j < dim; j++) centroids[c][j] += data[i][j];
            counts[c]++;
        }
        for (int k = 0; k < K; k++) {
            if (counts[k] > 0)
                for (int j = 0; j < dim; j++) centroids[k][j] /= counts[k];
        }
        return centroids;
    }

    public static void main(String[] args) {
        System.out.println("=== Spotify-style Playlist Discovery ===");

        // Tracks: [energy, danceability] on a 0..10 scale, three genres
        double[][] data = {
            {1.0, 1.0}, {1.2, 1.1}, {0.8, 0.9}, {1.1, 0.8},
            {5.0, 5.0}, {5.2, 5.1}, {4.8, 4.9}, {5.1, 5.2},
            {9.0, 1.0}, {9.2, 0.8}, {8.8, 1.2}, {9.1, 0.9}
        };

        int elbowK = 3;
        for (int K = 2; K <= 5; K++) {
            int[] labels = kMeans(data, K, 100);
            double inert = inertia(data, labels, K);
            System.out.printf("K=%d  Inertia=%.4f  Labels=%s%n",
                    K, inert, Arrays.toString(labels));
        }

        int[] best = kMeans(data, elbowK, 100);
        double[][] c = centroids(data, best, elbowK);
        System.out.println("K=3 centroids:");
        for (int k = 0; k < c.length; k++) {
            System.out.printf("  cluster %d: energy=%.2f, danceability=%.2f%n",
                    k, c[k][0], c[k][1]);
        }
    }
}
```

### Expected Output
```
=== Spotify-style Playlist Discovery ===
K=2  Inertia=65.6613  Labels=[0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1]
K=3  Inertia=0.4500  Labels=[0, 0, 0, 0, 2, 2, 2, 2, 1, 1, 1, 1]
K=4  Inertia=0.3875  Labels=[0, 1, 0, 1, 2, 2, 2, 2, 3, 3, 3, 3]
K=5  Inertia=0.3375  Labels=[2, 2, 0, 1, 4, 4, 4, 4, 3, 3, 3, 3]
K=3 centroids:
  cluster 0: energy=1.03, danceability=0.95
  cluster 1: energy=9.03, danceability=0.98
  cluster 2: energy=5.03, danceability=5.05
```

---

## Problem 2: Uber Pickup-Zone Discovery — Company: Uber

### Interview Scenario
"You're at Uber. The dispatch team wants to identify driver pickup hotspots from
GPS coordinates so surge zones can be defined. Two clear hotspots exist in the
pilot data — run K-Means and confirm the split."

### The Problem
Cluster pickup coordinates and: (1) Run K-Means for K=2 and K=3, (2) Compare
inertia to justify K=2, (3) Read the assignment labels, (4) Note what K=3 does
to a two-hotspot city.

### Solution Walkthrough
- Step 1: 8 pickups as `(x, y)` grid miles — two tight clusters of four.
- Step 2: `kMeans` + `inertia` for K=2 (0.79) and K=3 (0.48) — the small gap
  shows K=3 is just carving one zone in half.
- Step 3: The K=3 labels `[1,1,1,1,0,2,0,2]` show the second hotspot being split
  — a classic over-splitting tell.

### Code
```java
// Pickup hotspots: (x, y) city-grid miles from downtown
double[][] data = {
    {2.0, 3.0}, {2.5, 2.8}, {1.8, 3.2}, {2.2, 2.9},
    {8.0, 7.0}, {8.4, 6.8}, {7.6, 7.2}, {8.2, 6.9}
};

for (int K = 2; K <= 3; K++) {
    int[] labels = GenreClusters.kMeans(data, K, 100);
    System.out.printf("K=%d  Inertia=%.4f  Labels=%s%n", K,
            GenreClusters.inertia(data, labels, K), Arrays.toString(labels));
}
```

### Expected Output
```
K=2  Inertia=0.7925  Labels=[1, 1, 1, 1, 0, 0, 0, 0]
K=3  Inertia=0.4800  Labels=[1, 1, 1, 1, 0, 2, 0, 2]
```

---

## Problem 3: Amazon Customer Segmentation — Company: Amazon

### Interview Scenario
"You're at Amazon. Marketing wants customer segments from two features — monthly
spend (in $k) and visits per month — so campaigns can be targeted per segment.
After clustering, a new customer must be assigned to a segment at request time
without re-running the algorithm."

### The Problem
Segment customers and serve assignments: (1) Cluster 9 customers with K=3,
(2) Report labels and inertia, (3) Materialize centroids, (4) Assign a new
customer by nearest centroid.

### Solution Walkthrough
- Step 1: 9 customers in three natural spend/visit tiers.
- Step 2: `kMeans(data, 3, 100)` — labels `[2,2,2,1,1,1,0,0,0]`, clean tiers.
- Step 3: `centroids()` to get the three segment centers.
- Step 4: Assign the new customer (3.0k, 5.0 visits) — nearest centroid is
  cluster 1 at distance 1.4395; the O(K·d) query is the model at serving time.

### Code
```java
// Customers: [monthly spend $k, visits per month]
double[][] data = {
    {0.2, 1.0}, {0.3, 1.2}, {0.15, 0.8},
    {2.0, 4.0}, {2.3, 3.6}, {1.8, 4.2},
    {5.0, 8.0}, {5.5, 7.5}, {4.7, 8.3}
};
int K = 3;
int[] labels = GenreClusters.kMeans(data, K, 100);
System.out.println("Labels: " + Arrays.toString(labels));
System.out.printf("Inertia(K=3) = %.4f%n", GenreClusters.inertia(data, labels, K));

double[][] c = GenreClusters.centroids(data, labels, K);
double[] newCust = {3.0, 5.0};
int best = 0;
double bestD = GenreClusters.dist(newCust, c[0]);
for (int k = 1; k < K; k++) {
    double d = GenreClusters.dist(newCust, c[k]);
    if (d < bestD) { bestD = d; best = k; }
}
System.out.printf("New customer (spend=3.0k, visits=5.0) -> cluster %d (dist=%.4f)%n",
        best, bestD);
```

### Expected Output
```
Labels: [2, 2, 2, 1, 1, 1, 0, 0, 0]
Inertia(K=3) = 1.0583
New customer (spend=3.0k, visits=5.0) -> cluster 1 (dist=1.4395)
```
