package com.ai06;

import java.util.*;

public class DecisionTree {
    private DecisionNode root;
    private int maxDepth;
    private int minSamples;

    public DecisionTree(int maxDepth, int minSamples) {
        this.maxDepth = maxDepth;
        this.minSamples = minSamples;
    }

    static class DecisionNode {
        int featureIndex;
        double threshold;
        DecisionNode left, right;
        Integer prediction;
        int depth;

        boolean isLeaf() { return prediction != null; }
    }

    public void fit(double[][] X, int[] y) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < X.length; i++) indices.add(i);
        root = buildTree(X, y, indices, 0);
    }

    private DecisionNode buildTree(double[][] X, int[] y, List<Integer> indices, int depth) {
        DecisionNode node = new DecisionNode();
        node.depth = depth;
        Set<Integer> uniqueClasses = new HashSet<>();
        for (int idx : indices) uniqueClasses.add(y[idx]);
        if (uniqueClasses.size() == 1 || depth >= maxDepth || indices.size() <= minSamples) {
            int[] counts = new int[100];
            for (int idx : indices) counts[y[idx]]++;
            int best = 0;
            for (int i = 1; i < counts.length; i++)
                if (counts[i] > counts[best]) best = i;
            node.prediction = best;
            return node;
        }
        int bestFeature = -1;
        double bestThreshold = 0;
        double bestGini = Double.MAX_VALUE;
        for (int f = 0; f < X[0].length; f++) {
            List<Double> values = new ArrayList<>();
            for (int idx : indices) values.add(X[idx][f]);
            Collections.sort(values);
            for (double thresh : values) {
                List<Integer> leftIdxs = new ArrayList<>();
                List<Integer> rightIdxs = new ArrayList<>();
                for (int idx : indices) {
                    if (X[idx][f] <= thresh) leftIdxs.add(idx);
                    else rightIdxs.add(idx);
                }
                if (leftIdxs.isEmpty() || rightIdxs.isEmpty()) continue;
                double gini = weightedGini(y, leftIdxs, rightIdxs);
                if (gini < bestGini) {
                    bestGini = gini;
                    bestFeature = f;
                    bestThreshold = thresh;
                }
            }
        }
        if (bestFeature == -1) {
            int[] counts = new int[100];
            for (int idx : indices) counts[y[idx]]++;
            int best = 0;
            for (int i = 1; i < counts.length; i++)
                if (counts[i] > counts[best]) best = i;
            node.prediction = best;
            return node;
        }
        node.featureIndex = bestFeature;
        node.threshold = bestThreshold;
        List<Integer> leftIdxs = new ArrayList<>();
        List<Integer> rightIdxs = new ArrayList<>();
        for (int idx : indices) {
            if (X[idx][bestFeature] <= bestThreshold) leftIdxs.add(idx);
            else rightIdxs.add(idx);
        }
        node.left = buildTree(X, y, leftIdxs, depth + 1);
        node.right = buildTree(X, y, rightIdxs, depth + 1);
        return node;
    }

    private double giniImpurity(int[] y, List<Integer> indices) {
        if (indices.isEmpty()) return 0;
        Map<Integer, Integer> counts = new HashMap<>();
        for (int idx : indices)
            counts.merge(y[idx], 1, Integer::sum);
        double impurity = 1;
        double total = indices.size();
        for (int c : counts.values()) {
            double p = c / total;
            impurity -= p * p;
        }
        return impurity;
    }

    private double weightedGini(int[] y, List<Integer> left, List<Integer> right) {
        double total = left.size() + right.size();
        return (left.size() / total) * giniImpurity(y, left) + (right.size() / total) * giniImpurity(y, right);
    }

    public int predict(double[] x) {
        DecisionNode node = root;
        while (!node.isLeaf()) {
            if (x[node.featureIndex] <= node.threshold) node = node.left;
            else node = node.right;
        }
        return node.prediction;
    }

    public int[] predict(double[][] X) {
        int[] predictions = new int[X.length];
        for (int i = 0; i < X.length; i++) predictions[i] = predict(X[i]);
        return predictions;
    }

    public static void main(String[] args) {
        System.out.println("=== Decision Tree Demo ===");
        double[][] X = {
            {1.0, 2.0}, {2.0, 1.0}, {2.0, 3.0},
            {5.0, 4.0}, {6.0, 5.0}, {7.0, 6.0},
            {3.0, 2.5}, {4.0, 3.5}
        };
        int[] y = {0, 0, 0, 1, 1, 1, 0, 1};
        DecisionTree dt = new DecisionTree(5, 1);
        dt.fit(X, y);
        double[][] test = {{1.5, 2.0}, {5.5, 5.0}, {3.0, 3.0}};
        for (double[] t : test)
            System.out.println("Test " + java.util.Arrays.toString(t) + " -> " + dt.predict(t));
    }
}
