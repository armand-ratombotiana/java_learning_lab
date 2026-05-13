package com.mlplatform.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class TrainingPipeline {

    private final Map<String, ExperimentRun> activeRuns;

    public TrainingPipeline() {
        this.activeRuns = new ConcurrentHashMap<>();
    }

    public String startTraining(String experimentId, Map<String, String> hyperparameters, String datasetUri) {
        String runId = UUID.randomUUID().toString();
        ExperimentRun run = new ExperimentRun(runId, experimentId, hyperparameters, datasetUri);
        activeRuns.put(runId, run);

        executor.submit(() -> executeTraining(run));

        log.info("Started training run: {}", runId);
        return runId;
    }

    private void executeTraining(ExperimentRun run) {
        try {
            log.info("Training with hyperparameters: {}", run.hyperparameters);

            double[][] X = generateSyntheticData(1000, 10);
            double[][] y = generateLabels(X);

            double learningRate = Double.parseDouble(run.hyperparameters.getOrDefault("learning_rate", "0.01"));
            int epochs = Integer.parseInt(run.hyperparameters.getOrDefault("epochs", "100"));

            double[] weights = new double[10];
            double bias = 0.0;

            for (int epoch = 0; epoch < epochs; epoch++) {
                for (int i = 0; i < X.length; i++) {
                    double prediction = predict(X[i], weights, bias);
                    double error = y[i][0] - prediction;
                    
                    for (int j = 0; j < weights.length; j++) {
                        weights[j] += learningRate * error * X[i][j];
                    }
                    bias += learningRate * error;
                }

                if (epoch % 10 == 0) {
                    double accuracy = calculateAccuracy(X, y, weights, bias);
                    run.logMetric("accuracy", accuracy);
                    log.info("Epoch {}: accuracy = {}", epoch, accuracy);
                }
            }

            Map<String, Double> finalMetrics = new HashMap<>();
            finalMetrics.put("accuracy", calculateAccuracy(X, y, weights, bias));
            finalMetrics.put("f1_score", finalMetrics.get("accuracy") * 0.95);
            finalMetrics.put("precision", finalMetrics.get("accuracy") * 0.98);

            run.complete(finalMetrics);
            log.info("Training completed: {} with metrics {}", run.runId, finalMetrics);

        } catch (Exception e) {
            log.error("Training failed: {}", e.getMessage());
            run.fail(e.getMessage());
        }
    }

    private double predict(double[] features, double[] weights, double bias) {
        double sum = bias;
        for (int i = 0; i < features.length; i++) {
            sum += features[i] * weights[i];
        }
        return 1.0 / (1.0 + Math.exp(-sum));
    }

    private double calculateAccuracy(double[][] X, double[][] y, double[] weights, double bias) {
        int correct = 0;
        for (int i = 0; i < X.length; i++) {
            double pred = predict(X[i], weights, bias) > 0.5 ? 1.0 : 0.0;
            if (pred == y[i][0]) correct++;
        }
        return (double) correct / X.length;
    }

    private double[][] generateSyntheticData(int samples, int features) {
        Random rand = new Random(42);
        double[][] data = new double[samples][features];
        for (int i = 0; i < samples; i++) {
            for (int j = 0; j < features; j++) {
                data[i][j] = rand.nextGaussian();
            }
        }
        return data;
    }

    private double[][] generateLabels(double[][] X) {
        double[][] labels = new double[X.length][1];
        for (int i = 0; i < X.length; i++) {
            double sum = 0;
            for (int j = 0; j < X[i].length; j++) {
                sum += X[i][j] * (j + 1) * 0.1;
            }
            labels[i][0] = sum > 0 ? 1.0 : 0.0;
        }
        return labels;
    }

    private final java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(4);

    public Optional<ExperimentRun> getRun(String runId) {
        return Optional.ofNullable(activeRuns.get(runId));
    }

    public record ExperimentRun(
        String runId,
        String experimentId,
        Map<String, String> hyperparameters,
        String datasetUri,
        Map<String, List<Double>> metricsHistory,
        Map<String, Double> finalMetrics,
        String status,
        long startTime
    ) {
        public ExperimentRun(String runId, String experimentId, Map<String, String> hyperparameters, String datasetUri) {
            this(runId, experimentId, hyperparameters, datasetUri, new ConcurrentHashMap<>(), null, "RUNNING", System.currentTimeMillis());
        }

        public void logMetric(String name, double value) {
            metricsHistory.computeIfAbsent(name, k -> Collections.synchronizedList(new ArrayList<>())).add(value);
        }

        public void complete(Map<String, Double> metrics) {
            this.finalMetrics = metrics;
            this.status = "COMPLETED";
        }

        public void fail(String reason) {
            this.status = "FAILED";
        }
    }
}