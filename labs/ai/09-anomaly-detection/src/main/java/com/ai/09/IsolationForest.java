package com.ai09;

import java.util.*;

public class IsolationForest {
    private int numTrees;
    private int sampleSize;
    private List<IsolationTree> trees;
    private Random rng;

    static class IsolationTree {
        int featureIndex;
        double splitValue;
        IsolationTree left, right;
        int size;

        boolean isLeaf() { return left == null && right == null; }
    }

    public IsolationForest(int numTrees, int sampleSize) {
        this.numTrees = numTrees;
        this.sampleSize = sampleSize;
        this.rng = new Random(42);
    }

    public void fit(double[][] data) {
        trees = new ArrayList<>();
        int n = data.length;
        for (int t = 0; t < numTrees; t++) {
            List<Integer> sampleIndices = new ArrayList<>();
            for (int i = 0; i < sampleSize && i < n; i++)
                sampleIndices.add(rng.nextInt(n));
            trees.add(buildTree(data, sampleIndices, 0));
        }
    }

    private IsolationTree buildTree(double[][] data, List<Integer> indices, int depth) {
        IsolationTree node = new IsolationTree();
        node.size = indices.size();
        if (indices.size() <= 1 || depth >= 10) return node;
        int dims = data[0].length;
        double minVal = Double.MAX_VALUE, maxVal = -Double.MAX_VALUE;
        int feat = rng.nextInt(dims);
        for (int idx : indices) {
            minVal = Math.min(minVal, data[idx][feat]);
            maxVal = Math.max(maxVal, data[idx][feat]);
        }
        if (minVal == maxVal) return node;
        double split = minVal + rng.nextDouble() * (maxVal - minVal);
        node.featureIndex = feat;
        node.splitValue = split;
        List<Integer> leftIndices = new ArrayList<>();
        List<Integer> rightIndices = new ArrayList<>();
        for (int idx : indices) {
            if (data[idx][feat] < split) leftIndices.add(idx);
            else rightIndices.add(idx);
        }
        node.left = buildTree(data, leftIndices, depth + 1);
        node.right = buildTree(data, rightIndices, depth + 1);
        return node;
    }

    public double anomalyScore(double[] point) {
        double avgPathLength = 0;
        for (IsolationTree tree : trees)
            avgPathLength += pathLength(tree, point, 0);
        avgPathLength /= trees.size();
        double c = averagePathLength(sampleSize);
        return Math.pow(2, -avgPathLength / c);
    }

    private double pathLength(IsolationTree node, double[] point, int depth) {
        if (node.isLeaf()) return depth + c(node.size);
        if (point[node.featureIndex] < node.splitValue)
            return pathLength(node.left, point, depth + 1);
        else
            return pathLength(node.right, point, depth + 1);
    }

    private double c(int n) {
        if (n <= 1) return 0;
        return 2 * (Math.log(n - 1) + 0.5772156649) - (2 * (n - 1) / (double) n);
    }

    private static double averagePathLength(int n) {
        if (n <= 1) return 0;
        return 2 * (Math.log(n - 1) + 0.5772156649) - (2 * (n - 1) / (double) n);
    }

    public static void main(String[] args) {
        System.out.println("=== Isolation Forest Demo ===");
        double[][] data = {
            {1, 1}, {1.1, 1.1}, {1.2, 1.0}, {1.1, 0.9},
            {5, 5}, {5.1, 5.1}, {5.2, 5.0},
            {10, 1}
        };
        IsolationForest forest = new IsolationForest(50, 256);
        forest.fit(data);
        for (double[] point : data)
            System.out.println("Point " + Arrays.toString(point) + " anomaly score: " + forest.anomalyScore(point));
    }
}
