package com.mlops.lab05;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

/**
 * Model Serving with Docker — Lab 05.
 * <p>
 * Demonstrates a lightweight REST API model server using Java's built-in HTTP server.
 * Serves predictions via POST /predict and health checks via GET /healthz.
 * Designed to be containerized with Docker.
 */
public class ModelServingLab {

    private static final Logger LOG = Logger.getLogger(ModelServingLab.class.getName());

    /** Simple linear regression model (simulated). */
    static class Model {
        private final double[] weights;
        private final double bias;

        Model(double[] weights, double bias) {
            this.weights = weights;
            this.bias = bias;
        }

        double predict(double[] features) {
            if (features.length != weights.length) {
                throw new IllegalArgumentException(
                        "Expected " + weights.length + " features, got " + features.length);
            }
            double result = bias;
            for (int i = 0; i < features.length; i++) {
                result += weights[i] * features[i];
            }
            return result;
        }
    }

    /** Parses JSON body for prediction request. */
    static double[] parseFeatures(String json) {
        // Simplistic JSON parser: {"features":[1.0,2.0,3.0]}
        String numsPart = json.replaceAll(".*\\[", "").replaceAll("\\].*", "");
        String[] parts = numsPart.split(",");
        double[] features = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            features[i] = Double.parseDouble(parts[i].trim());
        }
        return features;
    }

    static String toJson(double prediction) {
        return "{\"prediction\":" + prediction + "}";
    }

    static void sendResponse(HttpExchange exchange, int code, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        Model model = new Model(new double[]{0.5, -0.2, 0.8, 0.1}, 0.3);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(4));

        // Health check endpoint
        server.createContext("/healthz", exchange -> {
            String resp = "{\"status\":\"ok\"}";
            sendResponse(exchange, 200, resp);
        });

        // Readiness check endpoint
        server.createContext("/readyz", exchange -> {
            String resp = "{\"status\":\"ready\"}";
            sendResponse(exchange, 200, resp);
        });

        // Prediction endpoint
        server.createContext("/predict", exchange -> {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }
            try {
                String body;
                try (InputStream is = exchange.getRequestBody();
                     BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    body = br.lines().reduce("", (a, b) -> a + b);
                }
                double[] features = parseFeatures(body);
                double prediction = model.predict(features);
                sendResponse(exchange, 200, toJson(prediction));
            } catch (Exception e) {
                LOG.warning("Prediction error: " + e.getMessage());
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        });

        server.start();
        LOG.info("Model server started on port " + port);
        LOG.info("Endpoints: POST /predict, GET /healthz, GET /readyz");
    }
}
