package com.ml.lab03;

import java.util.*;

/**
 * Decision Tree — ID3 algorithm with entropy / information gain.
 * <p>
 * Builds a tree for categorical features and a binary target.
 * Demonstrates impurity calculation, splitting, and prediction.
 */
public class Main {

    static class TreeNode {
        String label;          // feature name for split, or class for leaf
        Map<String, TreeNode> children;
        String leafClass;
        boolean isLeaf;

        TreeNode(String label) {
            this.label = label;
            this.children = new HashMap<>();
            this.isLeaf = false;
        }
    }

    // ──────────────────────────────────────────────
    // Entropy
    // ──────────────────────────────────────────────

    public static double entropy(String[] labels) {
        Map<String, Integer> counts = new HashMap<>();
        for (String l : labels) counts.merge(l, 1, Integer::sum);
        double e = 0.0;
        int n = labels.length;
        for (int c : counts.values()) {
            double p = (double) c / n;
            if (p > 0) e -= p * (Math.log(p) / Math.log(2));
        }
        return e;
    }

    // ──────────────────────────────────────────────
    // Information Gain
    // ──────────────────────────────────────────────

    public static double infoGain(String[][] data, String[] labels, int feat) {
        Map<String, Integer> valCount = new HashMap<>();
        for (String[] row : data) valCount.merge(row[feat], 1, Integer::sum);

        double parentE = entropy(labels);
        double childE = 0.0;
        int n = labels.length;

        for (String val : valCount.keySet()) {
            List<String> subLabels = new ArrayList<>();
            for (int i = 0; i < data.length; i++) {
                if (data[i][feat].equals(val)) subLabels.add(labels[i]);
            }
            String[] subArr = subLabels.toArray(new String[0]);
            childE += (double) subArr.length / n * entropy(subArr);
        }
        return parentE - childE;
    }

    // ──────────────────────────────────────────────
    // Build Tree (ID3)
    // ──────────────────────────────────────────────

    public static TreeNode buildTree(String[][] data, String[] labels,
                                     String[] featNames, boolean[] used) {
        Set<String> uniqueLabels = new HashSet<>(Arrays.asList(labels));
        if (uniqueLabels.size() == 1) {
            TreeNode leaf = new TreeNode("leaf");
            leaf.isLeaf = true;
            leaf.leafClass = labels[0];
            return leaf;
        }

        int bestFeat = -1;
        double bestGain = -1;
        for (int f = 0; f < featNames.length; f++) {
            if (used[f]) continue;
            double g = infoGain(data, labels, f);
            if (g > bestGain) { bestGain = g; bestFeat = f; }
        }

        if (bestFeat == -1) {
            TreeNode leaf = new TreeNode("leaf");
            leaf.isLeaf = true;
            leaf.leafClass = majority(labels);
            return leaf;
        }

        TreeNode node = new TreeNode(featNames[bestFeat]);
        used[bestFeat] = true;

        Map<String, List<Integer>> partitions = new HashMap<>();
        for (int i = 0; i < data.length; i++) {
            partitions.computeIfAbsent(data[i][bestFeat], k -> new ArrayList<>()).add(i);
        }

        for (String val : partitions.keySet()) {
            List<Integer> idx = partitions.get(val);
            String[][] subData = idx.stream().map(i -> data[i]).toArray(String[][]::new);
            String[] subLabs = idx.stream().map(i -> labels[i]).toArray(String[]::new);
            node.children.put(val, buildTree(subData, subLabs, featNames, used.clone()));
        }
        return node;
    }

    public static String majority(String[] labels) {
        Map<String, Integer> cnt = new HashMap<>();
        for (String l : labels) cnt.merge(l, 1, Integer::sum);
        return Collections.max(cnt.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    // ──────────────────────────────────────────────
    // Predict
    // ──────────────────────────────────────────────

    public static String predict(TreeNode node, String[] sample) {
        if (node.isLeaf) return node.leafClass;
        String val = sample[0];  // simplified: assume feature index from node.label
        TreeNode child = node.children.get(val);
        if (child == null) return "unknown";
        return predict(child, sample);
    }

    // ──────────────────────────────────────────────
    // Main — test cases
    // ──────────────────────────────────────────────

    public static void main(String[] args) {
        System.out.println("=== Decision Tree Lab ===");

        // Play Tennis dataset
        String[] featNames = {"outlook", "temp", "humidity", "wind"};
        String[][] data = {
            {"sunny",   "hot",  "high",  "weak"},
            {"sunny",   "hot",  "high",  "strong"},
            {"overcast","hot",  "high",  "weak"},
            {"rain",    "mild", "high",  "weak"},
            {"rain",    "cool", "normal","weak"},
            {"rain",    "cool", "normal","strong"},
            {"overcast","cool", "normal","strong"},
            {"sunny",   "mild", "high",  "weak"},
            {"sunny",   "cool", "normal","weak"},
            {"rain",    "mild", "normal","weak"},
            {"sunny",   "mild", "normal","strong"},
            {"overcast","mild", "high",  "strong"},
            {"overcast","hot",  "normal","weak"},
            {"rain",    "mild", "high",  "strong"}
        };
        String[] labels = {
            "no", "no", "yes", "yes", "yes", "no", "yes",
            "no", "yes", "yes", "yes", "yes", "yes", "no"
        };

        TreeNode root = buildTree(data, labels, featNames, new boolean[featNames.length]);
        System.out.println("Tree built. Root splits on: " + root.label);
        System.out.println("Children keys: " + root.children.keySet());

        int correct = 0;
        for (int i = 0; i < data.length; i++) {
            String pred = predict(root, data[i]);
            if (pred.equals(labels[i])) correct++;
            System.out.printf("True=%-4s Pred=%-4s%n", labels[i], pred);
        }
        System.out.printf("Accuracy = %d/%d = %.2f%n", correct, data.length,
                (double) correct / data.length);
    }
}
