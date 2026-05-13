package com.fraud.ml;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class IsolationForestModel {

    private static final int DEFAULT_NUM_TREES = 100;
    private static final int DEFAULT_SAMPLE_SIZE = 256;
    private static final double DEFAULT_CONTAMINATION = 0.1;

    private final int numTrees;
    private final int sampleSize;
    private final List<TreeNode> trees;
    private final Random random;

    public IsolationForestModel() {
        this(DEFAULT_NUM_TREES, DEFAULT_SAMPLE_SIZE);
    }

    public IsolationForestModel(int numTrees, int sampleSize) {
        this.numTrees = numTrees;
        this.sampleSize = sampleSize;
        this.trees = new ArrayList<>();
        this.random = new Random(42);
        initializeTrees();
    }

    private void initializeTrees() {
        for (int i = 0; i < numTrees; i++) {
            trees.add(new TreeNode(0, null));
        }
        log.info("Initialized Isolation Forest with {} trees", numTrees);
    }

    public double predict(double[] features) {
        if (features == null || features.length == 0) {
            return 0.5;
        }

        double avgPathLength = 0.0;
        for (TreeNode tree : trees) {
            avgPathLength += computePathLength(tree, features, 0);
        }

        avgPathLength /= numTrees;

        double c = computeNormalizationFactor(features.length);
        double score = Math.pow(2, -avgPathLength / c);

        return score;
    }

    private double computePathLength(TreeNode node, double[] features, int depth) {
        if (node.isLeaf() || depth >= DEFAULT_SAMPLE_SIZE) {
            return depth == 0 ? 1 : depth + computeLeafAdjustment(features.length);
        }

        int featureIndex = node.splitFeature;
        double splitValue = node.splitValue;

        if (features[featureIndex] < splitValue) {
            return computePathLength(node.left, features, depth + 1);
        } else {
            return computePathLength(node.right, features, depth + 1);
        }
    }

    private double computeNormalizationFactor(int featureCount) {
        if (featureCount <= 1) return 1.0;
        return 2.0 * (Math.log(featureCount - 1) + 0.5772156649) - 2.0 * (featureCount - 1) / (featureCount + 1);
    }

    private double computeLeafAdjustment(int featureCount) {
        return (featureCount > 2) ? 2.0 * (Math.log(featureCount - 1) + 0.5772156649) - 2.0 * (featureCount - 1) / (featureCount + 1) : 1.0;
    }

    public void train(double[][] trainingData) {
        log.info("Training Isolation Forest on {} samples", trainingData.length);
        
        for (int i = 0; i < numTrees; i++) {
            double[][] sample = sampleWithReplacement(trainingData);
            trees.set(i, buildTree(sample, 0));
        }
        
        log.info("Training completed");
    }

    private double[][] sampleWithReplacement(double[][] data) {
        double[][] sample = new double[sampleSize][];
        for (int i = 0; i < sampleSize; i++) {
            sample[i] = data[random.nextInt(data.length)];
        }
        return sample;
    }

    private TreeNode buildTree(double[][] data, int depth) {
        if (data.length <= 1 || depth > 20) {
            return new TreeNode(depth, null);
        }

        int featureIndex = random.nextInt(data[0].length);
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (double[] row : data) {
            min = Math.min(min, row[featureIndex]);
            max = Math.max(max, row[featureIndex]);
        }

        if (min == max) {
            return new TreeNode(depth, null);
        }

        double splitValue = min + random.nextDouble() * (max - min);

        List<double[]>> leftData = new ArrayList<>();
        List<double[]>> rightData = new ArrayList<>();
        for (double[] row : data) {
            if (row[featureIndex] < splitValue) {
                leftData.add(row);
            } else {
                rightData.add(row);
            }
        }

        TreeNode node = new TreeNode(depth, null);
        node.splitFeature = featureIndex;
        node.splitValue = splitValue;
        node.left = buildTree(leftData.toArray(new double[0][]), depth + 1);
        node.right = buildTree(rightData.toArray(new double[0][]), depth + 1);

        return node;
    }

    private static class TreeNode {
        int depth;
        Integer splitFeature;
        Double splitValue;
        TreeNode left;
        TreeNode right;

        TreeNode(int depth, Double splitValue) {
            this.depth = depth;
            this.splitValue = splitValue;
        }

        boolean isLeaf() {
            return splitFeature == null;
        }
    }
}