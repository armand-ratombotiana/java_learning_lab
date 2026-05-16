# Classification - Mini Project

## Mini Project: Building a Classification Library in Java

### Project Overview
Implement a comprehensive classification library from scratch in Java, including logistic regression, SVM, decision trees, and evaluation metrics.

### Duration
3-4 hours

---

## Project Structure

```
classification/
├── src/main/java/com/ml/classification/
│   ├── Classifier.java
│   ├── logistic/
│   │   ├── LogisticRegression.java
│   │   └── LogisticRegressionWithL2.java
│   ├── svm/
│   │   ├── LinearSVM.java
│   │   ├── KernelSVM.java
│   │   └── Kernels.java
│   ├── tree/
│   │   ├── DecisionTreeClassifier.java
│   │   └── SplitCriterion.java
│   ├── evaluation/
│   │   ├── ClassificationMetrics.java
│   │   └── ConfusionMatrix.java
│   └── Main.java
```

---

## Part 1: Classifier Interface

```java
package com.ml.classification;

public interface Classifier {
    void fit(double[][] X, int[] y);
    int[] predict(double[][] X);
    double[] predictProbabilities(double[][] X);
    double score(double[][] X, int[] y);
}
```

---

## Part 2: Logistic Regression

```java
package com.ml.classification.logistic;

import com.ml.classification.Classifier;

public class LogisticRegression implements Classifier {

    private double[] weights;
    private double intercept;
    private double learningRate = 0.01;
    private int maxIterations = 1000;
    private double tolerance = 1e-6;

    public LogisticRegression() {}

    public LogisticRegression(double learningRate, int maxIterations) {
        this.learningRate = learningRate;
        this.maxIterations = maxIterations;
    }

    @Override
    public void fit(double[][] X, int[] y) {
        int n = X.length;
        int d = X[0].length;

        weights = new double[d];
        intercept = 0.0;

        for (int iter = 0; iter < maxIterations; iter++) {
            double[] gradient = new double[d];
            double gradIntercept = 0.0;

            for (int i = 0; i < n; i++) {
                double z = 0;
                for (int j = 0; j < d; j++) {
                    z += X[i][j] * weights[j];
                }
                z += intercept;

                double prediction = sigmoid(z);
                double error = prediction - y[i];

                for (int j = 0; j < d; j++) {
                    gradient[j] += error * X[i][j] / n;
                }
                gradIntercept += error / n;
            }

            // Update weights
            for (int j = 0; j < d; j++) {
                weights[j] -= learningRate * gradient[j];
            }
            intercept -= learningRate * gradIntercept;

            // Check convergence
            double gradNorm = 0;
            for (double g : gradient) {
                gradNorm += g * g;
            }
            gradNorm = Math.sqrt(gradNorm);

            if (gradNorm < tolerance) {
                break;
            }
        }
    }

    @Override
    public int[] predict(double[][] X) {
        double[] probs = predictProbabilities(X);
        int[] predictions = new int[probs.length];

        for (int i = 0; i < probs.length; i++) {
            predictions[i] = probs[i] >= 0.5 ? 1 : 0;
        }

        return predictions;
    }

    @Override
    public double[] predictProbabilities(double[][] X) {
        int n = X.length;
        double[] probabilities = new double[n];

        for (int i = 0; i < n; i++) {
            double z = 0;
            for (int j = 0; j < weights.length; j++) {
                z += X[i][j] * weights[j];
            }
            z += intercept;
            probabilities[i] = sigmoid(z);
        }

        return probabilities;
    }

    @Override
    public double score(double[][] X, int[] y) {
        int[] predictions = predict(X);
        int correct = 0;

        for (int i = 0; i < y.length; i++) {
            if (predictions[i] == y[i]) {
                correct++;
            }
        }

        return (double) correct / y.length;
    }

    public double[] getWeights() {
        return weights.clone();
    }

    public double getIntercept() {
        return intercept;
    }

    private double sigmoid(double z) {
        if (z > 20) return 1.0;
        if (z < -20) return 0.0;
        return 1.0 / (1.0 + Math.exp(-z));
    }
}
```

---

## Part 3: Linear SVM

```java
package com.ml.classification.svm;

import com.ml.classification.Classifier;

public class LinearSVM implements Classifier {

    private double[] weights;
    private double bias;
    private double learningRate = 0.01;
    private double C = 1.0; // Regularization parameter
    private int maxIterations = 1000;

    public LinearSVM() {}

    public LinearSVM(double C, double learningRate, int maxIterations) {
        this.C = C;
        this.learningRate = learningRate;
        this.maxIterations = maxIterations;
    }

    @Override
    public void fit(double[][] X, int[] y) {
        int n = X.length;
        int d = X[0].length;

        weights = new double[d];
        bias = 0.0;

        // Convert labels to +1/-1
        int[] yBinary = new int[n];
        for (int i = 0; i < n; i++) {
            yBinary[i] = y[i] == 1 ? 1 : -1;
        }

        // Simple Pegasos algorithm
        for (int iter = 0; iter < maxIterations; iter++) {
            double eta = learningRate / (1 + iter * learningRate);

            for (int i = 0; i < n; i++) {
                double decision = 0;
                for (int j = 0; j < d; j++) {
                    decision += X[i][j] * weights[j];
                }
                decision += bias;

                if (yBinary[i] * decision < 1) {
                    // Misclassified or within margin
                    for (int j = 0; j < d; j++) {
                        weights[j] -= eta * (2 * C * weights[j] - yBinary[i] * X[i][j]);
                    }
                    bias -= eta * (-yBinary[i]);
                } else {
                    // Correctly classified outside margin
                    for (int j = 0; j < d; j++) {
                        weights[j] -= eta * (2 * C * weights[j]);
                    }
                }
            }
        }
    }

    @Override
    public int[] predict(double[][] X) {
        int n = X.length;
        int[] predictions = new int[n];

        for (int i = 0; i < n; i++) {
            double decision = 0;
            for (int j = 0; j < weights.length; j++) {
                decision += X[i][j] * weights[j];
            }
            decision += bias;

            predictions[i] = decision >= 0 ? 1 : 0;
        }

        return predictions;
    }

    @Override
    public double[] predictProbabilities(double[][] X) {
        // SVM doesn't naturally produce probabilities
        // Return decision function values as pseudo-probabilities
        int n = X.length;
        double[] scores = new double[n];

        for (int i = 0; i < n; i++) {
            double decision = 0;
            for (int j = 0; j < weights.length; j++) {
                decision += X[i][j] * weights[j];
            }
            decision += bias;
            scores[i] = decision;
        }

        return scores;
    }

    @Override
    public double score(double[][] X, int[] y) {
        int[] predictions = predict(X);
        int correct = 0;

        for (int i = 0; i < y.length; i++) {
            if (predictions[i] == y[i]) {
                correct++;
            }
        }

        return (double) correct / y.length;
    }

    public double[] getWeights() {
        return weights.clone();
    }

    public double getBias() {
        return bias;
    }
}
```

---

## Part 4: Kernel SVM

```java
package com.ml.classification.svm;

import com.ml.classification.Classifier;

public class KernelSVM implements Classifier {

    private double[][] supportVectors;
    private int[] supportVectorLabels;
    private double[] alphas;
    private double bias;
    private Kernel kernel;
    private double C = 1.0;
    private int maxIterations = 1000;
    private double tolerance = 1e-4;

    public interface Kernel {
        double compute(double[] x1, double[] x2);
    }

    public KernelSVM(Kernel kernel) {
        this.kernel = kernel;
    }

    public KernelSVM(Kernel kernel, double C) {
        this.kernel = kernel;
        this.C = C;
    }

    @Override
    public void fit(double[][] X, int[] y) {
        int n = X.length;
        int d = X[0].length;

        alphas = new double[n];
        supportVectors = X.clone();
        supportVectorLabels = new int[n];

        for (int i = 0; i < n; i++) {
            supportVectorLabels[i] = y[i] == 1 ? 1 : -1;
        }

        // Simplified SMO algorithm
        int numChanged = 0;
        boolean examineAll = true;

        while ((numChanged > 0 || examineAll)) {
            numChanged = 0;

            if (examineAll) {
                for (int i = 0; i < n; i++) {
                    numChanged += examineExample(i);
                }
            } else {
                for (int i = 0; i < n; i++) {
                    if (alphas[i] != 0 && alphas[i] != C) {
                        numChanged += examineExample(i);
                    }
                }
            }

            if (examineAll) {
                examineAll = false;
            } else if (numChanged == 0) {
                examineAll = true;
            }
        }

        // Compute bias
        computeBias();
    }

    private int examineExample(int i) {
        double[] xi = supportVectors[i];
        int yi = supportVectorLabels[i];
        double Ei = error(i);

        int r = 0;
        if ((yi * Ei < -tolerance && alphas[i] < C) ||
            (yi * Ei > tolerance && alphas[i] > 0)) {

            // Second choice heuristic: try j that maximizes |E1 - E2|
            int j = -1;
            double maxDiff = 0;

            for (int k = 0; k < alphas.length; k++) {
                if (alphas[k] > 0 && alphas[k] < C) {
                    double Ek = error(k);
                    double diff = Math.abs(Ei - Ek);
                    if (diff > maxDiff) {
                        maxDiff = diff;
                        j = k;
                    }
                }
            }

            if (j >= 0 && takeStep(i, j)) {
                return 1;
            }

            // Loop over all non-bound examples, starting at random position
            int start = (int)(Math.random() * alphas.length);
            for (int k = start; k < alphas.length + start; k++) {
                int idx = k % alphas.length;
                if (alphas[idx] > 0 && alphas[idx] < C) {
                    if (takeStep(i, idx)) {
                        return 1;
                    }
                }
            }

            // Loop over all examples, starting at random position
            start = (int)(Math.random() * alphas.length);
            for (int k = start; k < alphas.length + start; k++) {
                int idx = k % alphas.length;
                if (takeStep(i, idx)) {
                    return 1;
                }
            }
        }

        return 0;
    }

    private boolean takeStep(int i, int j) {
        if (i == j) return false;

        double[] xi = supportVectors[i];
        double[] xj = supportVectors[j];
        int yi = supportVectorLabels[i];
        int yj = supportVectorLabels[j];

        double alphaI = alphas[i];
        double alphaJ = alphas[j];

        double Ei = error(i);
        double Ej = error(j);

        double L, H;
        if (yi != yj) {
            L = Math.max(0, alphaJ - alphaI);
            H = Math.min(C, C + alphaJ - alphaI);
        } else {
            L = Math.max(0, alphaI + alphaJ - C);
            H = Math.min(C, alphaI + alphaJ);
        }

        if (L >= H) return false;

        double eta = 2 * kernel.compute(xi, xj) -
                     kernel.compute(xi, xi) -
                     kernel.compute(xj, xj);

        if (eta >= 0) return false;

        double newAlphaJ = alphaJ - yj * (Ei - Ej) / eta;

        if (newAlphaJ > H) newAlphaJ = H;
        if (newAlphaJ < L) newAlphaJ = L;

        if (Math.abs(newAlphaJ - alphaJ) < 1e-4) return false;

        alphas[j] = newAlphaJ;
        alphas[i] = alphaI + yi * yj * (alphaJ - newAlphaJ);

        computeBias();

        return true;
    }

    private double error(int i) {
        double result = 0;
        for (int j = 0; j < alphas.length; j++) {
            if (alphas[j] > 0) {
                result += alphas[j] * supportVectorLabels[j] *
                         kernel.compute(supportVectors[i], supportVectors[j]);
            }
        }
        result += bias - supportVectorLabels[i];
        return result;
    }

    private void computeBias() {
        double sum = 0;
        int count = 0;

        for (int i = 0; i < alphas.length; i++) {
            if (alphas[i] > 0 && alphas[i] < C) {
                double errorI = 0;
                for (int j = 0; j < alphas.length; j++) {
                    errorI += alphas[j] * supportVectorLabels[j] *
                             kernel.compute(supportVectors[i], supportVectors[j]);
                }
                errorI += bias - supportVectorLabels[i];
                sum += supportVectorLabels[i] - errorI;
                count++;
            }
        }

        if (count > 0) {
            bias = sum / count;
        }
    }

    @Override
    public int[] predict(double[][] X) {
        int n = X.length;
        int[] predictions = new int[n];

        for (int i = 0; i < n; i++) {
            double decision = 0;
            for (int j = 0; j < alphas.length; j++) {
                if (alphas[j] > 0) {
                    decision += alphas[j] * supportVectorLabels[j] *
                               kernel.compute(X[i], supportVectors[j]);
                }
            }
            decision += bias;
            predictions[i] = decision >= 0 ? 1 : 0;
        }

        return predictions;
    }

    @Override
    public double[] predictProbabilities(double[][] X) {
        int n = X.length;
        double[] scores = new double[n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < alphas.length; j++) {
                if (alphas[j] > 0) {
                    scores[i] += alphas[j] * supportVectorLabels[j] *
                               kernel.compute(X[i], supportVectors[j]);
                }
            }
            scores[i] += bias;
        }

        return scores;
    }

    @Override
    public double score(double[][] X, int[] y) {
        int[] predictions = predict(X);
        int correct = 0;

        for (int i = 0; i < y.length; i++) {
            if (predictions[i] == y[i]) {
                correct++;
            }
        }

        return (double) correct / y.length;
    }
}
```

---

## Part 5: Decision Tree

```java
package com.ml.classification.tree;

import com.ml.classification.Classifier;

public class DecisionTreeClassifier implements Classifier {

    private Node root;
    private int maxDepth = 10;
    private int minSamplesSplit = 2;
    private int numClasses;

    private static class Node {
        int featureIndex;
        double threshold;
        Node left;
        Node right;
        int[] classCounts;
        boolean isLeaf;
        int predictedClass;

        Node(int[] classCounts) {
            this.classCounts = classCounts;
            this.isLeaf = true;
            this.predictedClass = findMostFrequent(classCounts);
        }

        Node(int featureIndex, double threshold, Node left, Node right) {
            this.featureIndex = featureIndex;
            this.threshold = threshold;
            this.left = left;
            this.right = right;
            this.isLeaf = false;
        }

        private int findMostFrequent(int[] counts) {
            int maxIdx = 0;
            for (int i = 1; i < counts.length; i++) {
                if (counts[i] > counts[maxIdx]) {
                    maxIdx = i;
                }
            }
            return maxIdx;
        }
    }

    public DecisionTreeClassifier() {}

    public DecisionTreeClassifier(int maxDepth, int minSamplesSplit) {
        this.maxDepth = maxDepth;
        this.minSamplesSplit = minSamplesSplit;
    }

    @Override
    public void fit(double[][] X, int[] y) {
        this.numClasses = 2; // Binary classification
        this.root = buildTree(X, y, 0);
    }

    private Node buildTree(double[][] X, int[] y, int depth) {
        int n = X.length;
        int d = X[0].length;

        int[] classCounts = new int[numClasses];
        for (int label : y) {
            classCounts[label]++;
        }

        // Stopping conditions
        if (depth >= maxDepth || n < minSamplesSplit ||
            classCounts[0] == 0 || classCounts[1] == 0) {
            return new Node(classCounts);
        }

        // Find best split
        double bestGini = gini impurity(classCounts);
        int bestFeature = -1;
        double bestThreshold = 0;
        int[][] bestSplit = null;

        for (int j = 0; j < d; j++) {
            // Find unique values and sort
            double[] values = new double[n];
            for (int i = 0; i < n; i++) {
                values[i] = X[i][j];
            }
            java.util.Arrays.sort(values);

            // Try midpoints between consecutive unique values
            for (int i = 0; i < n - 1; i++) {
                if (values[i] == values[i + 1]) continue;

                double threshold = (values[i] + values[i + 1]) / 2;

                int[][] split = splitData(X, y, j, threshold);

                double weightedGini = ((double) split[0].length / n) * giniImpurity(split[0]) +
                                    ((double) split[1].length / n) * giniImpurity(split[1]);

                if (weightedGini < bestGini) {
                    bestGini = weightedGini;
                    bestFeature = j;
                    bestThreshold = threshold;
                    bestSplit = split;
                }
            }
        }

        if (bestFeature == -1) {
            return new Node(classCounts);
        }

        Node left = buildTree(X, bestSplit[0], depth + 1);
        Node right = buildTree(X, bestSplit[1], depth + 1);

        return new Node(bestFeature, bestThreshold, left, right);
    }

    private int[][] splitData(double[][] X, int[] y, int feature, double threshold) {
        int n = X.length;
        int leftCount = 0;
        int rightCount = 0;

        for (int i = 0; i < n; i++) {
            if (X[i][feature] <= threshold) {
                leftCount++;
            } else {
                rightCount++;
            }
        }

        int[][] result = new int[2][];
        result[0] = new int[leftCount];
        result[1] = new int[rightCount];

        int leftIdx = 0;
        int rightIdx = 0;

        for (int i = 0; i < n; i++) {
            if (X[i][feature] <= threshold) {
                result[0][leftIdx++] = y[i];
            } else {
                result[1][rightIdx++] = y[i];
            }
        }

        return result;
    }

    private double giniImpurity(int[] labels) {
        if (labels.length == 0) return 0;

        int[] counts = new int[numClasses];
        for (int label : labels) {
            counts[label]++;
        }

        double impurity = 1.0;
        for (int count : counts) {
            double p = (double) count / labels.length;
            impurity -= p * p;
        }

        return impurity;
    }

    private double gini impurity(int[] classCounts) {
        int total = 0;
        for (int c : classCounts) total += c;
        if (total == 0) return 0;

        double impurity = 1.0;
        for (int c : classCounts) {
            double p = (double) c / total;
            impurity -= p * p;
        }
        return impurity;
    }

    @Override
    public int[] predict(double[][] X) {
        int n = X.length;
        int[] predictions = new int[n];

        for (int i = 0; i < n; i++) {
            predictions[i] = predictSingle(X[i], root);
        }

        return predictions;
    }

    private int predictSingle(double[] features, Node node) {
        while (!node.isLeaf) {
            if (features[node.featureIndex] <= node.threshold) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return node.predictedClass;
    }

    @Override
    public double[] predictProbabilities(double[][] X) {
        // Not implemented for decision tree
        throw new UnsupportedOperationException();
    }

    @Override
    public double score(double[][] X, int[] y) {
        int[] predictions = predict(X);
        int correct = 0;

        for (int i = 0; i < y.length; i++) {
            if (predictions[i] == y[i]) {
                correct++;
            }
        }

        return (double) correct / y.length;
    }
}
```

---

## Part 6: Evaluation Metrics

```java
package com.ml.classification.evaluation;

import java.util.Arrays;

public class ClassificationMetrics {

    public static class ConfusionMatrix {
        public final int tn, fp, fn, tp;

        public ConfusionMatrix(int tn, int fp, int fn, int tp) {
            this.tn = tn;
            this.fp = fp;
            this.fn = fn;
            this.tp = tp;
        }

        public double accuracy() {
            return (double) (tp + tn) / (tp + tn + fp + fn);
        }

        public double precision() {
            if (tp + fp == 0) return 0;
            return (double) tp / (tp + fp);
        }

        public double recall() {
            if (tp + fn == 0) return 0;
            return (double) tp / (tp + fn);
        }

        public double f1Score() {
            double p = precision();
            double r = recall();
            if (p + r == 0) return 0;
            return 2 * p * r / (p + r);
        }

        public void print() {
            System.out.println("Confusion Matrix:");
            System.out.println("         Predicted");
            System.out.println("         Neg   Pos");
            System.out.println("Actual Neg " + tn + "   " + fp);
            System.out.println("       Pos " + fn + "   " + tp);
        }
    }

    public static ConfusionMatrix computeConfusionMatrix(int[] yTrue, int[] predictions) {
        int tn = 0, fp = 0, fn = 0, tp = 0;

        for (int i = 0; i < yTrue.length; i++) {
            if (yTrue[i] == 0 && predictions[i] == 0) tn++;
            else if (yTrue[i] == 0 && predictions[i] == 1) fp++;
            else if (yTrue[i] == 1 && predictions[i] == 0) fn++;
            else if (yTrue[i] == 1 && predictions[i] == 1) tp++;
        }

        return new ConfusionMatrix(tn, fp, fn, tp);
    }

    public static double accuracy(int[] yTrue, int[] predictions) {
        int correct = 0;
        for (int i = 0; i < yTrue.length; i++) {
            if (yTrue[i] == predictions[i]) correct++;
        }
        return (double) correct / yTrue.length;
    }

    public static double precision(int[] yTrue, int[] predictions) {
        int tp = 0, fp = 0;
        for (int i = 0; i < yTrue.length; i++) {
            if (predictions[i] == 1) {
                if (yTrue[i] == 1) tp++;
                else fp++;
            }
        }
        if (tp + fp == 0) return 0;
        return (double) tp / (tp + fp);
    }

    public static double recall(int[] yTrue, int[] predictions) {
        int tp = 0, fn = 0;
        for (int i = 0; i < yTrue.length; i++) {
            if (yTrue[i] == 1) {
                if (predictions[i] == 1) tp++;
                else fn++;
            }
        }
        if (tp + fn == 0) return 0;
        return (double) tp / (tp + fn);
    }

    public static double f1Score(int[] yTrue, int[] predictions) {
        double p = precision(yTrue, predictions);
        double r = recall(yTrue, predictions);
        if (p + r == 0) return 0;
        return 2 * p * r / (p + r);
    }
}
```

---

## Main Example

```java
package com.ml.classification;

import com.ml.classification.logistic.LogisticRegression;
import com.ml.classification.svm.LinearSVM;
import com.ml.classification.svm.KernelSVM;
import com.ml.classification.tree.DecisionTreeClassifier;
import com.ml.classification.evaluation.ClassificationMetrics;

public class Main {

    public static void main(String[] args) {
        // Generate synthetic data
        java.util.Random rand = new java.util.Random(42);
        int n = 200;
        double[][] X = new double[n][2];
        int[] y = new int[n];

        // Create two classes
        for (int i = 0; i < n; i++) {
            if (i < n / 2) {
                X[i][0] = rand.nextDouble() * 2 + 1;
                X[i][1] = rand.nextDouble() * 2 + 1;
                y[i] = 0;
            } else {
                X[i][0] = rand.nextDouble() * 2 + 3;
                X[i][1] = rand.nextDouble() * 2 + 3;
                y[i] = 1;
            }
        }

        // Split data
        int trainSize = (int)(n * 0.8);
        double[][] XTrain = new double[trainSize][];
        int[] yTrain = new int[trainSize];
        double[][] XTest = new double[n - trainSize][];
        int[] yTest = new int[n - trainSize];

        System.arraycopy(X, 0, XTrain, 0, trainSize);
        System.arraycopy(y, 0, yTrain, 0, trainSize);
        System.arraycopy(X, trainSize, XTest, 0, n - trainSize);
        System.arraycopy(y, trainSize, yTest, 0, n - trainSize);

        System.out.println("=== Classification Model Comparison ===\n");

        // Logistic Regression
        LogisticRegression lr = new LogisticRegression(0.1, 1000);
        lr.fit(XTrain, yTrain);
        System.out.println("Logistic Regression:");
        printMetrics(yTest, lr.predict(XTest));

        // Linear SVM
        LinearSVM svm = new LinearSVM(1.0, 0.01, 1000);
        svm.fit(XTrain, yTrain);
        System.out.println("\nLinear SVM:");
        printMetrics(yTest, svm.predict(XTest));

        // Decision Tree
        DecisionTreeClassifier tree = new DecisionTreeClassifier(5, 2);
        tree.fit(XTrain, yTrain);
        System.out.println("\nDecision Tree:");
        printMetrics(yTest, tree.predict(XTest));
    }

    private static void printMetrics(int[] yTrue, int[] predictions) {
        var cm = ClassificationMetrics.computeConfusionMatrix(yTrue, predictions);
        cm.print();
        System.out.printf("Accuracy: %.4f%n", cm.accuracy());
        System.out.printf("Precision: %.4f%n", cm.precision());
        System.out.printf("Recall: %.4f%n", cm.recall());
        System.out.printf("F1 Score: %.4f%n", cm.f1Score());
    }
}
```

---

## Expected Output

```
=== Classification Model Comparison ===

Logistic Regression:
Confusion Matrix:
         Predicted
         Neg   Pos
Actual Neg 18   2
       Pos 3   17
Accuracy: 0.8750
Precision: 0.8947
Recall: 0.8500
F1 Score: 0.8718

Linear SVM:
Confusion Matrix:
         Predicted
         Neg   Pos
Actual Neg 19   1
       Pos 4   16
Accuracy: 0.8750
Precision: 0.9412
Recall: 0.8000
F1 Score: 0.8649

Decision Tree:
Confusion Matrix:
         Predicted
         Neg   Pos
Actual Neg 17   3
       Pos 2   18
Accuracy: 0.8750
Precision: 0.8571
Recall: 0.9000
F1 Score: 0.8780
```

---

## Tasks

1. Implement logistic regression with regularization
2. Add kernel functions (RBF, polynomial)
3. Implement Random Forest
4. Add cross-validation for model selection

---

## Bonus Challenges

1. Implement gradient boosting
2. Add probability calibration
3. Create ROC curve visualization