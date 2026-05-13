package com.recsys.ml;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class MatrixFactorizationModel {

    private final int numFactors;
    private final double learningRate;
    private final double regularization;
    private final int maxIterations;

    private Map<String, double[]> userFactors;
    private Map<String, double[]> itemFactors;
    private Map<String, Double> userMeans;
    private double globalMean;

    public MatrixFactorizationModel() {
        this.numFactors = 50;
        this.learningRate = 0.005;
        this.regularization = 0.02;
        this.maxIterations = 100;
        this.userFactors = new HashMap<>();
        this.itemFactors = new HashMap<>();
        this.userMeans = new HashMap<>();
    }

    public void train(List<Rating> ratings) {
        log.info("Training matrix factorization on {} ratings", ratings.size());
        
        globalMean = ratings.stream()
            .mapToDouble(Rating::score)
            .average()
            .orElse(3.0);

        ratings.forEach(r -> {
            userMeans.computeIfAbsent(r.userId(), k -> 0.0);
            userMeans.merge(r.userId(), r.score(), (a, b) -> (a + b) / 2.0);
        });

        Set<String> users = ratings.stream().map(Rating::userId).collect(HashSet::new, HashSet::add, HashSet::addAll);
        Set<String> items = ratings.stream().map(Rating::itemId).collect(HashSet::new, HashSet::add, HashSet::addAll);

        users.forEach(u -> userFactors.put(u, randomVector(numFactors)));
        items.forEach(i -> itemFactors.put(i, randomVector(numFactors)));

        for (int iter = 0; iter < maxIterations; iter++) {
            double totalError = 0.0;
            Collections.shuffle(ratings, new Random(42));

            for (Rating rating : ratings) {
                double[] userVec = userFactors.get(rating.userId());
                double[] itemVec = itemFactors.get(rating.itemId());

                double prediction = predict(userVec, itemVec, userMeans.getOrDefault(rating.userId(), globalMean));
                double error = rating.score() - prediction;
                totalError += error * error;

                for (int f = 0; f < numFactors; f++) {
                    double userF = userVec[f];
                    double itemF = itemVec[f];

                    userVec[f] += learningRate * (error * itemF - regularization * userF);
                    itemVec[f] += learningRate * (error * userF - regularization * itemF);
                }
            }

            if (iter % 10 == 0) {
                log.info("Iteration {}: RMSE = {}", iter, Math.sqrt(totalError / ratings.size()));
            }
        }
        log.info("Training completed");
    }

    public double predict(String userId, String itemId) {
        double[] userVec = userFactors.get(userId);
        double[] itemVec = itemFactors.get(itemId);

        if (userVec == null || itemVec == null) {
            return userMeans.getOrDefault(userId, globalMean);
        }

        return predict(userVec, itemVec, userMeans.getOrDefault(userId, globalMean));
    }

    private double predict(double[] userVec, double[] itemVec, double userMean) {
        return userMean + dotProduct(userVec, itemVec);
    }

    private double dotProduct(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    private double[] randomVector(int size) {
        double[] v = new double[size];
        Random rand = new Random(42);
        for (int i = 0; i < size; i++) {
            v[i] = rand.nextDouble() * 0.1;
        }
        return v;
    }

    public List<String> recommendItems(String userId, int topN) {
        double[] userVec = userFactors.get(userId);
        if (userVec == null) {
            return List.of();
        }

        return itemFactors.entrySet().stream()
            .filter(e -> !hasRated(userId, e.getKey()))
            .map(e -> Map.entry(e.getKey(), dotProduct(userVec, e.getValue())))
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(topN)
            .map(Map.Entry::getKey)
            .toList();
    }

    private boolean hasRated(String userId, String itemId) {
        return false;
    }

    public record Rating(String userId, String itemId, double score, long timestamp) {}
}