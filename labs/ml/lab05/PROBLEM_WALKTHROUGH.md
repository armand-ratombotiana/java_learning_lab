# Problem Walkthrough: K-Nearest Neighbors

## Problem 1: Netflix Taste Matcher — Company: Netflix

### Interview Scenario
"You're at Netflix. The personalization team wants a v0 'because you watched'
feature: given a viewer's taste vector — how much they like action and drama, on
a 0–7 scale — find the taste tribe they belong to using the viewers whose taste
is closest. The model has to be explainable in a product review: 'these three
viewers are nearest to you, and they all watch drama-first.'"

### The Problem
Build a KNN classifier over viewer taste vectors. It must: (1) Compute Euclidean
distance from the new viewer to every stored viewer, (2) Sort and take the K
nearest, (3) Support both plain majority and distance-weighted voting,
(4) Report accuracy across K = 1, 3, 5 so the team sees the K sensitivity,
(5) Assign a new viewer to a taste tribe.

### Solution Walkthrough
- Step 1: Encode 6 viewers as `(action, drama)` taste vectors with tribe labels
  0 and 1 — the lab's two-cluster layout.
- Step 2: Reuse the lab's `euclidean` — √Σ(x−y)² — and `predict`, which sorts
  `List<Neighbor>` by distance and keeps the top K.
- Step 3: Run `accuracy(..., false)` and `accuracy(..., true)` for K = 1, 3, 5;
  weighted voting uses `1.0 / dist` per neighbor.
- Step 4: Predict the new viewer (action=5.2, drama=5.5) with K=3 under both
  voting schemes — all three nearest neighbors are tribe 1.
- Step 5: Note the normalization concern: the features are already on one scale,
  which is why the raw Euclidean distance is meaningful.

### Code
```java
package com.ml.lab05;

import java.util.*;

/**
 * Netflix-style taste matcher (KNN).
 * <p>
 * Classifies a viewer by the taste vectors of their K nearest
 * neighbors, with both majority and distance-weighted voting,
 * mirroring Lab 05's euclidean / predict / accuracy methods.
 */
public class TasteMatcher {

    public static double euclidean(double[] a, double[] b) {
        double s = 0;
        for (int i = 0; i < a.length; i++) s += (a[i] - b[i]) * (a[i] - b[i]);
        return Math.sqrt(s);
    }

    public static int predict(double[][] trainX, int[] trainY,
                              double[] testX, int k, boolean weighted) {
        int n = trainX.length;
        List<Neighbor> neighbors = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            neighbors.add(new Neighbor(trainY[i], euclidean(trainX[i], testX)));
        }
        neighbors.sort(Comparator.comparingDouble(a -> a.dist));
        List<Neighbor> topK = neighbors.subList(0, Math.min(k, n));

        Map<Integer, Double> votes = new HashMap<>();
        for (Neighbor nb : topK) {
            double w = weighted ? (nb.dist == 0 ? 1e6 : 1.0 / nb.dist) : 1.0;
            votes.merge(nb.label, w, Double::sum);
        }

        return Collections.max(votes.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    static class Neighbor {
        int label;
        double dist;
        Neighbor(int l, double d) { label = l; dist = d; }
    }

    public static double accuracy(double[][] trainX, int[] trainY,
                                  double[][] testX, int[] testY, int k, boolean weighted) {
        int ok = 0;
        for (int i = 0; i < testX.length; i++) {
            if (predict(trainX, trainY, testX[i], k, weighted) == testY[i]) ok++;
        }
        return (double) ok / testX.length;
    }

    public static void main(String[] args) {
        System.out.println("=== Netflix-style Taste Matcher ===");

        // Taste vectors (action, drama) on a 0..7 scale, cluster = taste tribe
        double[][] trainX = {
            {1.0, 1.0}, {1.5, 2.0}, {2.0, 1.5},
            {5.0, 5.0}, {5.5, 6.0}, {6.0, 5.0}
        };
        int[] trainY = {0, 0, 0, 1, 1, 1};

        double[][] testX = {
            {1.2, 1.8}, {5.2, 5.5}
        };
        int[] testY = {0, 1};

        for (int k : new int[]{1, 3, 5}) {
            double acc = accuracy(trainX, trainY, testX, testY, k, false);
            double wacc = accuracy(trainX, trainY, testX, testY, k, true);
            System.out.printf("K=%d  Accuracy=%.2f  Weighted=%.2f%n", k, acc, wacc);
        }

        // New viewer near the drama cluster
        double[] point = {5.2, 5.5};
        int k3 = predict(trainX, trainY, point, 3, false);
        int k3w = predict(trainX, trainY, point, 3, true);
        System.out.printf("New viewer (action=5.2, drama=5.5) -> K=3 vote=%d, weighted=%d%n",
                k3, k3w);
    }
}
```

### Expected Output
```
=== Netflix-style Taste Matcher ===
K=1  Accuracy=1.00  Weighted=1.00
K=3  Accuracy=1.00  Weighted=1.00
K=5  Accuracy=1.00  Weighted=1.00
New viewer (action=5.2, drama=5.5) -> K=3 vote=1, weighted=1
```

---

## Problem 2: Spotify Mood Classifier — Company: Spotify

### Interview Scenario
"You're at Spotify. A new 'chill vs upbeat' mood playlist needs a fast
classifier over two audio features — energy and valence, both 0–1. The dataset
is small and changes weekly as tracks are added, so retraining a parametric
model every week is overkill. Use neighbors."

### The Problem
Classify tracks by mood and: (1) Evaluate K = 1, 3, 5 with plain and weighted
voting, (2) Predict the mood of a new track at (energy=0.6, valence=0.5),
(3) Justify why KNN fits the weekly-update workflow.

### Solution Walkthrough
- Step 1: Track rows `(energy, valence)` on 0–1, labels 0 = chill, 1 = upbeat.
- Step 2: `accuracy` for K = 1, 3, 5 under both voting schemes.
- Step 3: `predict` the ambiguous new track with K=5 — its three nearest
  neighbors are all upbeat, so both votes return 1.
- Step 4: The insert-then-serve property: adding a labeled track costs no
  retraining, unlike the lab's gradient-descent models.

### Code
```java
// Track features: [energy 0..1, valence 0..1]; 0 = chill, 1 = upbeat
double[][] trainX = {
    {0.2, 0.2}, {0.3, 0.4}, {0.15, 0.3},
    {0.8, 0.8}, {0.9, 0.7}, {0.7, 0.9}
};
int[] trainY = {0, 0, 0, 1, 1, 1};

double[][] testX = {{0.25, 0.35}, {0.85, 0.75}};
int[] testY = {0, 1};

for (int k : new int[]{1, 3, 5}) {
    System.out.printf("K=%d  Accuracy=%.2f  Weighted=%.2f%n", k,
            TasteMatcher.accuracy(trainX, trainY, testX, testY, k, false),
            TasteMatcher.accuracy(trainX, trainY, testX, testY, k, true));
}

double[] track = {0.6, 0.5};
System.out.printf("Track (energy=0.6, valence=0.5) -> K=5 vote=%d, weighted=%d%n",
        TasteMatcher.predict(trainX, trainY, track, 5, false),
        TasteMatcher.predict(trainX, trainY, track, 5, true));
```

### Expected Output
```
K=1  Accuracy=1.00  Weighted=1.00
K=3  Accuracy=1.00  Weighted=1.00
K=5  Accuracy=1.00  Weighted=1.00
Track (energy=0.6, valence=0.5) -> K=5 vote=1, weighted=1
```

---

## Problem 3: Photo-Similarity Distance Metrics — Company: Instagram

### Interview Scenario
"You're at Instagram. The photo-similarity service compares 3-D feature vectors
of faces, and the team argues about which distance metric to ship. Show the three
candidates side by side on one pair of vectors so the decision is data, not
opinion."

### The Problem
Compare distance metrics and: (1) Implement Euclidean, Manhattan, and Minkowski
(p=3), (2) Compute all three on vectors a=(1,2,3), b=(4,5,6), (3) Explain why
the metric choice changes which neighbors KNN finds.

### Solution Walkthrough
- Step 1: Implement the three functions — Euclidean squares the deltas,
  Manhattan sums absolutes, Minkowski powers by p and roots by 1/p.
- Step 2: Each dimension differs by 3: Euclidean = √(27) = 5.1962, Manhattan =
  9, Minkowski p=3 = 81^(1/3) = 4.3267.
- Step 3: Note the ordering — different metrics rank the same pairs
  differently, so the neighbor set — and the vote — depends on the metric.

### Code
```java
public static double euclidean(double[] a, double[] b) {
    double s = 0;
    for (int i = 0; i < a.length; i++) s += (a[i] - b[i]) * (a[i] - b[i]);
    return Math.sqrt(s);
}

public static double manhattan(double[] a, double[] b) {
    double s = 0;
    for (int i = 0; i < a.length; i++) s += Math.abs(a[i] - b[i]);
    return s;
}

public static double minkowski(double[] a, double[] b, double p) {
    double s = 0;
    for (int i = 0; i < a.length; i++) s += Math.pow(Math.abs(a[i] - b[i]), p);
    return Math.pow(s, 1.0 / p);
}

public static void main(String[] args) {
    // Two photo feature vectors (3 dims): person A vs person B
    double[] a = {1.0, 2.0, 3.0};
    double[] b = {4.0, 5.0, 6.0};
    System.out.printf("Euclidean  = %.4f%n", euclidean(a, b));
    System.out.printf("Manhattan  = %.4f%n", manhattan(a, b));
    System.out.printf("Minkowski p=3: %.4f%n", minkowski(a, b, 3));
}
```

### Expected Output
```
Euclidean  = 5.1962
Manhattan  = 9.0000
Minkowski p=3: 4.3267
```
