# Recommendation Systems - Mini Project

## Mini Project: Implementing Matrix Factorization for Movie Recommendations

### Part 1: Matrix Factorization Implementation

```java
package com.ml.recsys;

import java.util.*;

public class MatrixFactorization {
    private int numUsers;
    private int numItems;
    private int numFactors;
    private double learningRate;
    private double regularization;
    private double[][] userFactors;
    private double[][] itemFactors;
    private double[] userBias;
    private double[] itemBias;
    private double globalMean;

    public MatrixFactorization(int numFactors, double learningRate, double regularization) {
        this.numFactors = numFactors;
        this.learningRate = learningRate;
        this.regularization = regularization;
    }

    public void fit(int[] userIds, int[] itemIds, double[] ratings) {
        // Find dimensions
        numUsers = Arrays.stream(userIds).max().orElse(0) + 1;
        numItems = Arrays.stream(itemIds).max().orElse(0) + 1;

        // Initialize factors randomly
        Random rand = new Random(42);
        userFactors = new double[numUsers][numFactors];
        itemFactors = new double[numItems][numFactors];
        userBias = new double[numUsers];
        itemBias = new double[numItems];

        for (int i = 0; i < numUsers; i++) {
            for (int f = 0; f < numFactors; f++) {
                userFactors[i][f] = rand.nextDouble() * 0.1;
            }
        }
        for (int i = 0; i < numItems; i++) {
            for (int f = 0; f < numFactors; f++) {
                itemFactors[i][f] = rand.nextDouble() * 0.1;
            }
        }

        // Compute global mean
        globalMean = Arrays.stream(ratings).average().orElse(0);

        // Stochastic gradient descent
        int epochs = 20;
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < ratings.length; i++) {
                int u = userIds[i];
                int it = itemIds[i];
                double r = ratings[i];

                // Predict
                double pred = predictSingle(u, it);

                // Error
                double error = r - pred;

                // Update biases
                userBias[u] += learningRate * (error - regularization * userBias[u]);
                itemBias[it] += learningRate * (error - regularization * itemBias[it]);

                // Update factors
                for (int f = 0; f < numFactors; f++) {
                    double uF = userFactors[u][f];
                    double iF = itemFactors[it][f];

                    userFactors[u][f] += learningRate *
                        (error * iF - regularization * uF);
                    itemFactors[it][f] += learningRate *
                        (error * uF - regularization * iF);
                }
            }
        }
    }

    private double predictSingle(int user, int item) {
        double pred = globalMean + userBias[user] + itemBias[item];
        for (int f = 0; f < numFactors; f++) {
            pred += userFactors[user][f] * itemFactors[item][f];
        }
        return pred;
    }

    public double predict(int user, int item) {
        if (user >= numUsers || item >= numItems) return globalMean;
        return predictSingle(user, item);
    }

    public int[] recommend(int user, int n, Set<Integer> excludeItems) {
        double[] scores = new double[numItems];
        for (int item = 0; item < numItems; item++) {
            if (!excludeItems.contains(item)) {
                scores[item] = predict(user, item);
            } else {
                scores[item] = Double.MIN_VALUE;
            }
        }

        // Get top N
        Integer[] indices = new Integer[numItems];
        for (int i = 0; i < numItems; i++) indices[i] = i;

        Arrays.sort(indices, (a, b) -> Double.compare(scores[b], scores[a]));

        int[] topItems = new int[n];
        for (int i = 0; i < n; i++) {
            topItems[i] = indices[i];
        }

        return topItems;
    }

    public double rmse(int[] userIds, int[] itemIds, double[] actual) {
        double sumSq = 0;
        for (int i = 0; i < actual.length; i++) {
            double pred = predict(userIds[i], itemIds[i]);
            double err = actual[i] - pred;
            sumSq += err * err;
        }
        return Math.sqrt(sumSq / actual.length);
    }
}
```

### Part 2: User-Based Collaborative Filtering

```java
package com.ml.recsys;

import java.util.*;

public class UserBasedCF {
    private double[][] userItemMatrix;
    private double[][] similarities;
    private int numUsers;
    private int numItems;

    public void fit(double[][] ratings) {
        this.userItemMatrix = ratings;
        this.numUsers = ratings.length;
        this.numItems = ratings[0].length;

        // Compute user similarities
        similarities = new double[numUsers][numUsers];
        for (int u1 = 0; u1 < numUsers; u1++) {
            for (int u2 = u1 + 1; u2 < numUsers; u2++) {
                double sim = cosineSimilarity(ratings[u1], ratings[u2]);
                similarities[u1][u2] = sim;
                similarities[u2][u1] = sim;
            }
        }
    }

    private double cosineSimilarity(double[] u1, double[] u2) {
        double dot = 0, norm1 = 0, norm2 = 0;
        for (int i = 0; i < u1.length; i++) {
            dot += u1[i] * u2[i];
            norm1 += u1[i] * u1[i];
            norm2 += u2[i] * u2[i];
        }
        if (norm1 == 0 || norm2 == 0) return 0;
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    public double predict(int user, int item) {
        // Weighted average of similar users' ratings
        double weightedSum = 0;
        double simSum = 0;

        for (int other = 0; other < numUsers; other++) {
            if (other != user && userItemMatrix[other][item] != 0) {
                double sim = similarities[user][other];
                weightedSum += sim * userItemMatrix[other][item];
                simSum += Math.abs(sim);
            }
        }

        return simSum > 0 ? weightedSum / simSum : 0;
    }
}
```

### Part 3: Main Example

```java
package com.ml.recsys;

public class Main {
    public static void main(String[] args) {
        // Create sample rating data (5 users, 10 items)
        int nRatings = 50;
        int[] users = new int[nRatings];
        int[] items = new int[nRatings];
        double[] ratings = new double[nRatings];

        java.util.Random rand = new java.util.Random(42);
        for (int i = 0; i < nRatings; i++) {
            users[i] = rand.nextInt(5);
            items[i] = rand.nextInt(10);
            ratings[i] = 1 + rand.nextDouble() * 4; // 1-5 rating
        }

        // Matrix Factorization
        MatrixFactorization mf = new MatrixFactorization(10, 0.01, 0.02);
        mf.fit(users, items, ratings);

        // Evaluate on test data
        double testRmse = mf.rmse(users, items, ratings);
        System.out.println("Training RMSE: " + testRmse);

        // Recommend for user 0
        int[] recommendations = mf.recommend(0, 3, new java.util.HashSet<>());
        System.out.println("Top 3 recommendations for user 0: " +
            java.util.Arrays.toString(recommendations));
    }
}
```

### Tasks

1. Add item-based collaborative filtering
2. Implement SVD++ with implicit feedback
3. Add evaluation metrics (precision, recall)
4. Create a simple movie dataset loader