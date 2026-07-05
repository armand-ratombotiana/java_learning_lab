package com.ai10;

public class CollaborativeFiltering {
    private double[][] ratings;
    private int numUsers, numItems;

    public void fit(double[][] ratings) {
        this.ratings = ratings;
        this.numUsers = ratings.length;
        this.numItems = ratings[0].length;
    }

    public double predictUserBased(int userId, int itemId, int k) {
        double[] userRatings = ratings[userId];
        double userMean = 0;
        int userCount = 0;
        for (int i = 0; i < numItems; i++)
            if (userRatings[i] > 0) { userMean += userRatings[i]; userCount++; }
        userMean /= Math.max(userCount, 1);

        double[] similarities = new double[numUsers];
        for (int u = 0; u < numUsers; u++) {
            if (u == userId) continue;
            similarities[u] = pearsonCorrelation(ratings[userId], ratings[u]);
        }

        double weightedSum = 0;
        double weightSum = 0;
        for (int u = 0; u < numUsers && k > 0; u++) {
            int best = -1;
            double bestSim = -Double.MAX_VALUE;
            for (int v = 0; v < numUsers; v++)
                if (v != userId && similarities[v] > bestSim && ratings[v][itemId] > 0) {
                    bestSim = similarities[v];
                    best = v;
                }
            if (best >= 0 && bestSim > 0) {
                weightedSum += bestSim * ratings[best][itemId];
                weightSum += Math.abs(bestSim);
                similarities[best] = -Double.MAX_VALUE;
                k--;
            } else break;
        }
        return weightSum > 0 ? weightedSum / weightSum : userMean;
    }

    private double pearsonCorrelation(double[] a, double[] b) {
        double meanA = 0, meanB = 0;
        int count = 0;
        for (int i = 0; i < a.length; i++)
            if (a[i] > 0 && b[i] > 0) { meanA += a[i]; meanB += b[i]; count++; }
        if (count == 0) return 0;
        meanA /= count; meanB /= count;
        double num = 0, denA = 0, denB = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] > 0 && b[i] > 0) {
                num += (a[i] - meanA) * (b[i] - meanB);
                denA += (a[i] - meanA) * (a[i] - meanA);
                denB += (b[i] - meanB) * (b[i] - meanB);
            }
        }
        return (denA == 0 || denB == 0) ? 0 : num / (Math.sqrt(denA) * Math.sqrt(denB));
    }

    public static void main(String[] args) {
        System.out.println("=== Collaborative Filtering Demo ===");
        double[][] ratings = {
            {5, 3, 0, 1, 4},
            {4, 0, 0, 1, 5},
            {1, 1, 0, 5, 0},
            {1, 0, 0, 4, 0},
            {0, 1, 5, 4, 0}
        };
        CollaborativeFiltering cf = new CollaborativeFiltering();
        cf.fit(ratings);
        double prediction = cf.predictUserBased(0, 2, 2);
        System.out.println("Predicted rating for user 0, item 2: " + prediction);
    }
}
