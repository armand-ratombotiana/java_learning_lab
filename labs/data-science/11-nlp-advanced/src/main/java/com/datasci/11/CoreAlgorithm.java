package com.datasci.11;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Core implementation of Advanced NLP algorithms.
 */
public class CoreAlgorithm {

    private final AlgorithmConfig config;
    private double[] weights;
    private double bias;
    private boolean trained;
    private List<Double> trainingLossHistory;
    private List<Double> validationLossHistory;

    public CoreAlgorithm() {
        this(new AlgorithmConfig.Builder().build());
    }

    public CoreAlgorithm(AlgorithmConfig config) {
        this.config = Objects.requireNonNull(config, "config must not be null");
        this.weights = new double[0];
        this.bias = 0.0;
        this.trained = false;
        this.trainingLossHistory = new ArrayList<>();
        this.validationLossHistory = new ArrayList<>();
    }

    public List<Double> fit(double[][] features, double[] labels) {
        validateInputs(features, labels);
        int numSamples = features.length;
        int numFeatures = features[0].length;

        this.weights = new double[numFeatures];
        Random rng = new Random(config.getRandomSeed());
        for (int i = 0; i < numFeatures; i++) {
            weights[i] = rng.nextGaussian() * 0.01;
        }
        this.bias = 0.0;

        trainingLossHistory.clear();
        validationLossHistory.clear();

        double[][] trainFeatures;
        double[] trainLabels;
        double[][] valFeatures = new double[0][];
        double[] valLabels = new double[0];

        if (config.getValidationRatio() > 0) {
            int valSize = (int) (numSamples * config.getValidationRatio());
            trainFeatures = new double[numSamples - valSize][];
            trainLabels = new double[numSamples - valSize];
            valFeatures = new double[valSize][];
            valLabels = new double[valSize];
            System.arraycopy(features, 0, trainFeatures, 0, numSamples - valSize);
            System.arraycopy(labels, 0, trainLabels, 0, numSamples - valSize);
            System.arraycopy(features, numSamples - valSize, valFeatures, 0, valSize);
            System.arraycopy(labels, numSamples - valSize, valLabels, 0, valSize);
        } else {
            trainFeatures = features;
            trainLabels = labels;
        }

        for (int epoch = 0; epoch < config.getMaxIterations(); epoch++) {
            double[] predictions = forward(trainFeatures);
            double[] valPredictions = valFeatures.length > 0 ? forward(valFeatures) : new double[0];

            double trainLoss = computeLoss(predictions, trainLabels);
            double valLoss = valFeatures.length > 0 ? computeLoss(valPredictions, valLabels) : Double.NaN;
            trainingLossHistory.add(trainLoss);
            if (valFeatures.length > 0) validationLossHistory.add(valLoss);

            if (epoch > 0 && Math.abs(trainingLossHistory.get(epoch) - trainingLossHistory.get(epoch - 1)) < config.getTolerance()) {
                break;
            }

            double[] gradients = computeGradients(trainFeatures, trainLabels, predictions);
            updateParameters(gradients);
        }

        this.trained = true;
        return Collections.unmodifiableList(trainingLossHistory);
    }

    public double[] predict(double[][] features) {
        if (!trained) {
            throw new IllegalStateException("Model must be trained before making predictions");
        }
        return forward(features);
    }

    private double[] forward(double[][] features) {
        int numSamples = features.length;
        double[] predictions = new double[numSamples];
        for (int i = 0; i < numSamples; i++) {
            double sum = bias;
            for (int j = 0; j < weights.length; j++) {
                sum += weights[j] * features[i][j];
            }
            predictions[i] = sum;
        }
        return predictions;
    }

    private double computeLoss(double[] predictions, double[] labels) {
        double sum = 0.0;
        for (int i = 0; i < predictions.length; i++) {
            double diff = predictions[i] - labels[i];
            sum += diff * diff;
        }
        double dataLoss = sum / predictions.length;
        double regLoss = 0.0;
        if (config.getRegularization() > 0) {
            for (double w : weights) {
                regLoss += w * w;
            }
            regLoss *= 0.5 * config.getRegularization() / predictions.length;
        }
        return dataLoss + regLoss;
    }

    private double[] computeGradients(double[][] features, double[] labels, double[] predictions) {
        int numSamples = features.length;
        int numFeatures = weights.length;
        double[] gradients = new double[numFeatures];
        for (int i = 0; i < numSamples; i++) {
            double error = predictions[i] - labels[i];
            for (int j = 0; j < numFeatures; j++) {
                gradients[j] += error * features[i][j];
            }
        }
        for (int j = 0; j < numFeatures; j++) {
            gradients[j] = gradients[j] / numSamples + config.getRegularization() * weights[j];
        }
        return gradients;
    }

    private void updateParameters(double[] gradients) {
        double lr = config.getLearningRate();
        for (int j = 0; j < weights.length; j++) {
            weights[j] -= lr * gradients[j];
        }
        bias -= lr * gradients[0];
    }

    private void validateInputs(double[][] features, double[] labels) {
        if (features == null || labels == null) {
            throw new DataValidationException("Features and labels must not be null");
        }
        if (features.length == 0) {
            throw new DataValidationException("Features must contain at least one sample");
        }
        if (features.length != labels.length) {
            throw new DataValidationException("Number of samples must match");
        }
        for (int i = 0; i < features.length; i++) {
            if (features[i] == null || features[i].length == 0) {
                throw new DataValidationException("Feature row " + i + " is null or empty");
            }
        }
    }

    public double[] getWeights() { return weights.clone(); }
    public double getBias() { return bias; }
    public boolean isTrained() { return trained; }
    public AlgorithmConfig getConfig() { return config; }
    public List<Double> getTrainingLossHistory() { return Collections.unmodifiableList(trainingLossHistory); }
    public List<Double> getValidationLossHistory() { return Collections.unmodifiableList(validationLossHistory); }

    public static class AlgorithmConfig {
        private final double learningRate;
        private final int maxIterations;
        private final double tolerance;
        private final double regularization;
        private final double validationRatio;
        private final long randomSeed;

        private AlgorithmConfig(Builder builder) {
            this.learningRate = builder.learningRate;
            this.maxIterations = builder.maxIterations;
            this.tolerance = builder.tolerance;
            this.regularization = builder.regularization;
            this.validationRatio = builder.validationRatio;
            this.randomSeed = builder.randomSeed;
        }

        public double getLearningRate() { return learningRate; }
        public int getMaxIterations() { return maxIterations; }
        public double getTolerance() { return tolerance; }
        public double getRegularization() { return regularization; }
        public double getValidationRatio() { return validationRatio; }
        public long getRandomSeed() { return randomSeed; }

        public static class Builder {
            private double learningRate = 0.01;
            private int maxIterations = 1000;
            private double tolerance = 1e-6;
            private double regularization = 0.0;
            private double validationRatio = 0.2;
            private long randomSeed = 42L;

            public Builder learningRate(double val) { this.learningRate = val; return this; }
            public Builder maxIterations(int val) { this.maxIterations = val; return this; }
            public Builder tolerance(double val) { this.tolerance = val; return this; }
            public Builder regularization(double val) { this.regularization = val; return this; }
            public Builder validationRatio(double val) { this.validationRatio = val; return this; }
            public Builder randomSeed(long val) { this.randomSeed = val; return this; }

            public AlgorithmConfig build() {
                if (learningRate <= 0) throw new IllegalArgumentException("Learning rate must be positive");
                if (maxIterations <= 0) throw new IllegalArgumentException("Max iterations must be positive");
                if (tolerance <= 0) throw new IllegalArgumentException("Tolerance must be positive");
                if (regularization < 0) throw new IllegalArgumentException("Regularization must be non-negative");
                if (validationRatio < 0 || validationRatio >= 1)
                    throw new IllegalArgumentException("Validation ratio must be in [0, 1)");
                return new AlgorithmConfig(this);
            }
        }
    }

    public static class DataValidationException extends RuntimeException {
        public DataValidationException(String message) { super(message); }
    }

    public static class ConvergenceException extends RuntimeException {
        public DataValidationException(String message) { super(message); }
    }
}
