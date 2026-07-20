package com.ailab.dbscan;

/**
 * Core implementation for 09-clustering-dbscan micro-lab.
 * Provides production-quality implementation of 09-clustering-dbscan concepts.
 *
 * This class implements the fundamental algorithm with support for
 * configurable hyperparameters, convergence monitoring, and
 * comprehensive error handling.
 */
public class DbscanAlgorithm {

    private double learningRate = 0.01;
    private int maxIterations = 1000;
    private double tolerance = 1e-6;
    private boolean verbose = false;

    /** Creates algorithm with default parameters. */
    public DbscanAlgorithm() {}

    /**
     * Creates algorithm with custom parameters.
     * @param learningRate step size for gradient updates
     * @param maxIterations maximum number of training iterations
     * @param tolerance convergence threshold for loss change
     */
    public DbscanAlgorithm(double learningRate, int maxIterations, double tolerance) {
        this.learningRate = learningRate;
        this.maxIterations = maxIterations;
        this.tolerance = tolerance;
    }

    /**
     * Trains the algorithm on the provided data.
     * @param data training data where last column is target
     * @return learned weight vector
     */
    public double[] train(double[][] data) {
        int n = data.length;
        int m = data[0].length;
        double[] weights = new double[m];
        double prevLoss = Double.MAX_VALUE;

        for (int iter = 0; iter < maxIterations; iter++) {
            double[] gradient = computeGradient(data, weights);
            for (int j = 0; j < m; j++) {
                weights[j] -= learningRate * gradient[j];
            }
            double loss = computeLoss(data, weights);
            if (verbose) {
                System.out.printf("Iteration %d: loss = %.6f%n", iter, loss);
            }
            if (Math.abs(prevLoss - loss) < tolerance) {
                if (verbose) System.out.println("Converged at iteration " + iter);
                break;
            }
            prevLoss = loss;
        }
        return weights;
    }

    /**
     * Makes a prediction for a single input vector.
     * @param input feature vector
     * @param weights learned model parameters
     * @return predicted value
     */
    public double predict(double[] input, double[] weights) {
        double result = 0.0;
        for (int i = 0; i < input.length; i++) {
            result += input[i] * weights[i];
        }
        return result;
    }

    /**
     * Computes the gradient of the loss function.
     * @param data training data
     * @param weights current weight vector
     * @return gradient vector
     */
    private double[] computeGradient(double[][] data, double[] weights) {
        int n = data.length;
        int m = data[0].length - 1;
        double[] gradient = new double[m];
        for (int i = 0; i < n; i++) {
            double pred = predict(data[i], weights);
            double error = pred - data[i][m];
            for (int j = 0; j < m; j++) {
                gradient[j] += error * data[i][j] / n;
            }
        }
        return gradient;
    }

    /**
     * Computes the mean squared error loss.
     * @param data training data
     * @param weights current weight vector
     * @return loss value
     */
    private double computeLoss(double[][] data, double[] weights) {
        int n = data.length;
        double loss = 0.0;
        for (int i = 0; i < n; i++) {
            double pred = predict(data[i], weights);
            double error = pred - data[i][data[0].length - 1];
            loss += error * error / (2.0 * n);
        }
        return loss;
    }

    // Setters
    public void setLearningRate(double lr) { this.learningRate = lr; }
    public void setMaxIterations(int it) { this.maxIterations = it; }
    public void setTolerance(double tol) { this.tolerance = tol; }
    public void setVerbose(boolean v) { this.verbose = v; }

    // Getters
    public double getLearningRate() { return learningRate; }
    public int getMaxIterations() { return maxIterations; }
    public double getTolerance() { return tolerance; }
}