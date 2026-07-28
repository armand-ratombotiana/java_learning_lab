package com.mlops.lab02;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

/**
 * Experiment Tracking with MLflow — Lab 02.
 * <p>
 * Demonstrates how to track ML experiments by wrapping MLflow's REST API.
 * Logs parameters, metrics, and manages runs programmatically from Java.
 */
public class ExperimentTrackingLab {

    /** Lightweight HTTP client for MLflow REST API calls. */
    static class MlflowTrackingClient {
        private final String trackingUri;

        MlflowTrackingClient(String trackingUri) {
            this.trackingUri = trackingUri;
        }

        private String postJson(String path, String json) {
            try {
                URL url = URI.create(trackingUri + path).toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes(StandardCharsets.UTF_8));
                }
                int code = conn.getResponseCode();
                if (code < 200 || code >= 300) {
                    throw new RuntimeException("MLflow API error " + code + " for " + path);
                }
                return new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException("MLflow API call failed: " + e.getMessage(), e);
            }
        }

        String createExperiment(String name) {
            String json = "{\"name\":\"" + name + "\"}";
            String resp = postJson("/api/2.0/mlflow/experiments/create", json);
            // Extract experiment_id from response
            return resp.replaceAll(".*\"experiment_id\":\"(\\d+)\".*", "$1");
        }

        String createRun(String experimentId) {
            String json = "{\"experiment_id\":\"" + experimentId + "\"}";
            String resp = postJson("/api/2.0/mlflow/runs/create", json);
            return resp.replaceAll(".*\"run_id\":\"([^\"]+)\".*", "$1");
        }

        void logParam(String runId, String key, String value) {
            String json = "{\"run_id\":\"" + runId + "\",\"key\":\"" + key + "\",\"value\":\"" + value + "\"}";
            postJson("/api/2.0/mlflow/runs/log-parameter", json);
        }

        void logMetric(String runId, String key, double value, long step) {
            String json = String.format(
                "{\"run_id\":\"%s\",\"key\":\"%s\",\"value\":%f,\"step\":%d}",
                runId, key, value, step);
            postJson("/api/2.0/mlflow/runs/log-metric", json);
        }

        void setTerminated(String runId, String status) {
            String json = "{\"run_id\":\"" + runId + "\",\"status\":\"" + status + "\"}";
            postJson("/api/2.0/mlflow/runs/update", json);
        }
    }

    /** Simulates training a model and returns a final metric. */
    static double simulateTraining(double lr, int epochs, int batchSize) {
        double accuracy = 0.5;
        Random rng = new Random(42);
        for (int epoch = 0; epoch < epochs; epoch++) {
            double improvement = lr * (0.8 - accuracy) * 0.1 + (rng.nextDouble() - 0.5) * 0.02;
            accuracy = Math.min(1.0, accuracy + improvement);
        }
        return accuracy;
    }

    public static void main(String[] args) {
        String trackingUri = args.length > 0 ? args[0] : "http://localhost:5000";
        MlflowTrackingClient client = new MlflowTrackingClient(trackingUri);

        System.out.println("=== Experiment Tracking with MLflow ===\n");

        // Create experiment
        String expName = "Java_ML_Experiment_" + Instant.now().toEpochMilli();
        String expId = client.createExperiment(expName);
        System.out.println("Created experiment: " + expName + " (id=" + expId + ")\n");

        // Run multiple trials
        double[] learningRates = {0.001, 0.01, 0.1};
        int[] epochsList = {10, 20};
        int[] batchSizes = {16, 32};

        for (double lr : learningRates) {
            for (int epochs : epochsList) {
                for (int batchSize : batchSizes) {
                    String runId = client.createRun(expId);
                    System.out.printf("Run %s: lr=%.3f, epochs=%d, batch=%d%n",
                            runId.substring(0, 8), lr, epochs, batchSize);

                    // Log parameters
                    client.logParam(runId, "learning_rate", String.valueOf(lr));
                    client.logParam(runId, "epochs", String.valueOf(epochs));
                    client.logParam(runId, "batch_size", String.valueOf(batchSize));

                    // Simulate training and log metrics per epoch
                    Random rng = new Random((long) (lr * 10000 + epochs + batchSize));
                    double accuracy = 0.5;
                    for (int epoch = 0; epoch < epochs; epoch++) {
                        double improvement = lr * (0.85 - accuracy) * 0.1
                                + (rng.nextDouble() - 0.5) * 0.01;
                        accuracy = Math.min(1.0, Math.max(0.0, accuracy + improvement));
                        double loss = Math.pow(1.0 - accuracy, 2) + 0.1 * rng.nextDouble();
                        client.logMetric(runId, "accuracy", accuracy, epoch);
                        client.logMetric(runId, "loss", loss, epoch);
                    }

                    double finalAccuracy = simulateTraining(lr, epochs, batchSize);
                    client.logMetric(runId, "final_accuracy", finalAccuracy, epochs);
                    client.setTerminated(runId, "FINISHED");
                    System.out.printf("  Final accuracy: %.4f%n%n", finalAccuracy);
                }
            }
        }

        System.out.println("=== Tracking complete ===");
        System.out.println("View results at: " + trackingUri);
    }
}
