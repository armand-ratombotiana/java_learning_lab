package com.ai02;

import java.util.*;

public class NaiveBayesClassifier {
    private Map<Integer, List<double[]>> classSamples;
    private Map<Integer, double[]> classMeans;
    private Map<Integer, double[]> classVars;
    private Map<Integer, Double> classPriors;
    private int numFeatures;

    public void train(double[][] X, int[] y) {
        numFeatures = X[0].length;
        classSamples = new HashMap<>();
        for (int i = 0; i < X.length; i++) {
            classSamples.computeIfAbsent(y[i], k -> new ArrayList<>()).add(X[i]);
        }
        classMeans = new HashMap<>();
        classVars = new HashMap<>();
        classPriors = new HashMap<>();
        int total = X.length;
        for (Map.Entry<Integer, List<double[]>> entry : classSamples.entrySet()) {
            int cls = entry.getKey();
            List<double[]> samples = entry.getValue();
            classPriors.put(cls, (double) samples.size() / total);
            double[] mean = new double[numFeatures];
            for (double[] s : samples)
                for (int f = 0; f < numFeatures; f++)
                    mean[f] += s[f];
            for (int f = 0; f < numFeatures; f++)
                mean[f] /= samples.size();
            double[] var = new double[numFeatures];
            for (double[] s : samples)
                for (int f = 0; f < numFeatures; f++)
                    var[f] += (s[f] - mean[f]) * (s[f] - mean[f]);
            for (int f = 0; f < numFeatures; f++)
                var[f] = Math.max(var[f] / samples.size(), 1e-9);
            classMeans.put(cls, mean);
            classVars.put(cls, var);
        }
    }

    public int predict(double[] x) {
        int bestClass = -1;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (int cls : classMeans.keySet()) {
            double score = Math.log(classPriors.get(cls));
            double[] mean = classMeans.get(cls);
            double[] var = classVars.get(cls);
            for (int f = 0; f < numFeatures; f++)
                score += -0.5 * Math.log(2 * Math.PI * var[f])
                    - ((x[f] - mean[f]) * (x[f] - mean[f])) / (2 * var[f]);
            if (score > bestScore) {
                bestScore = score;
                bestClass = cls;
            }
        }
        return bestClass;
    }

    public int[] predict(double[][] X) {
        int[] predictions = new int[X.length];
        for (int i = 0; i < X.length; i++)
            predictions[i] = predict(X[i]);
        return predictions;
    }

    public static void main(String[] args) {
        System.out.println("=== Naive Bayes Classifier Demo ===");
        double[][] X = {
            {1.0, 2.0}, {2.0, 1.0}, {2.0, 3.0},
            {5.0, 4.0}, {6.0, 5.0}, {7.0, 6.0}
        };
        int[] y = {0, 0, 0, 1, 1, 1};
        NaiveBayesClassifier nb = new NaiveBayesClassifier();
        nb.train(X, y);
        double[][] test = {{1.5, 2.0}, {5.5, 4.5}};
        int[] predictions = nb.predict(test);
        for (int i = 0; i < test.length; i++)
            System.out.println("Test " + java.util.Arrays.toString(test[i]) + " -> Class " + predictions[i]);
    }
}
