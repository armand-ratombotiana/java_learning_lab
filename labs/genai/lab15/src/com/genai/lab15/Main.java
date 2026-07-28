package com.genai.lab15;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Building a GenAI Platform
 * 
 * Demonstrates API gateway, model registry, orchestrator,
 * A/B testing, and rate limiting in Java.
 */
public class Main {

    /** Model record. */
    record Model(String id, String name, String version, int costUnits) {}

    /** Model registry. */
    static class ModelRegistry {
        final Map<String, List<Model>> models = new ConcurrentHashMap<>();

        void register(Model model) {
            models.computeIfAbsent(model.name, k -> new ArrayList<>()).add(model);
        }

        Model getLatest(String name) {
            var list = models.get(name);
            return list == null || list.isEmpty() ? null : list.get(list.size() - 1);
        }

        Model getVersion(String name, String version) {
            var list = models.get(name);
            if (list == null) return null;
            return list.stream().filter(m -> m.version.equals(version)).findFirst().orElse(null);
        }
    }

    /** API Gateway with routing. */
    static class APIGateway {
        final ModelRegistry registry;
        double abVariantRatio = 0.5;

        APIGateway(ModelRegistry registry) { this.registry = registry; }

        String route(String modelName, String requestId, boolean useVariant) {
            Model model;
            if (useVariant && requestId.hashCode() % 100 < abVariantRatio * 100) {
                model = registry.getVersion(modelName, "v2");
            } else {
                model = registry.getLatest(modelName);
            }
            if (model == null) return "ERROR: Model not found";
            return "Routed to " + model.name + " v" + model.version + " (cost: " + model.costUnits + ")";
        }
    }

    /** Orchestrator with fallback. */
    static class Orchestrator {
        final List<String> modelChain;
        final Map<String, String> fallbacks;

        Orchestrator(List<String> modelChain, Map<String, String> fallbacks) {
            this.modelChain = modelChain;
            this.fallbacks = fallbacks;
        }

        String execute(String request, Map<String, String> modelEndpoints) {
            String result = null;
            for (String model : modelChain) {
                String endpoint = modelEndpoints.get(model);
                if (endpoint != null && !endpoint.equals("ERROR")) {
                    result = "[" + model + "] Processed: " + request;
                    break;
                }
                // Try fallback
                String fb = fallbacks.get(model);
                if (fb != null) {
                    String fbEndpoint = modelEndpoints.get(fb);
                    if (fbEndpoint != null && !fbEndpoint.equals("ERROR")) {
                        result = "[" + fb + " (fallback)] Processed: " + request;
                        break;
                    }
                }
            }
            return result != null ? result : "All models failed";
        }
    }

    /** Token bucket rate limiter. */
    static class RateLimiter {
        final int maxTokens;
        final double refillRate;
        double tokens;
        long lastRefill;

        RateLimiter(int maxTokens, double refillRate) {
            this.maxTokens = maxTokens;
            this.refillRate = refillRate;
            this.tokens = maxTokens;
            this.lastRefill = System.nanoTime();
        }

        synchronized boolean tryAcquire(int cost) {
            refill();
            if (tokens >= cost) { tokens -= cost; return true; }
            return false;
        }

        private void refill() {
            long now = System.nanoTime();
            double elapsed = (now - lastRefill) / 1e9;
            tokens = Math.min(maxTokens, tokens + elapsed * refillRate);
            lastRefill = now;
        }
    }

    /** A/B test tracker. */
    static class ABTestTracker {
        final AtomicLong variantA = new AtomicLong();
        final AtomicLong variantB = new AtomicLong();
        final AtomicLong errorsA = new AtomicLong();
        final AtomicLong errorsB = new AtomicLong();

        void record(String variant, boolean error) {
            if (variant.equals("A")) { variantA.incrementAndGet(); if (error) errorsA.incrementAndGet(); }
            else { variantB.incrementAndGet(); if (error) errorsB.incrementAndGet(); }
        }

        void report() {
            System.out.println("=== A/B Test Results ===");
            System.out.printf("Variant A: %d requests, %d errors (%.2f%%)%n",
                variantA.get(), errorsA.get(), variantA.get() > 0 ? errorsA.get() * 100.0 / variantA.get() : 0);
            System.out.printf("Variant B: %d requests, %d errors (%.2f%%)%n",
                variantB.get(), errorsB.get(), variantB.get() > 0 ? errorsB.get() * 100.0 / variantB.get() : 0);
        }
    }

    public static void main(String[] args) {
        ModelRegistry registry = new ModelRegistry();
        registry.register(new Model("m1", "gpt-small", "v1", 1));
        registry.register(new Model("m2", "gpt-small", "v2", 2));
        registry.register(new Model("m3", "gpt-large", "v1", 10));

        APIGateway gateway = new APIGateway(registry);
        System.out.println("=== API Gateway ===");
        System.out.println(gateway.route("gpt-small", "req-123", true));
        System.out.println(gateway.route("gpt-small", "req-456", true));

        Orchestrator orch = new Orchestrator(
            List.of("gpt-large", "gpt-small"),
            Map.of("gpt-large", "gpt-small"));
        Map<String, String> endpoints = Map.of("gpt-large", "ERROR", "gpt-small", "OK");
        System.out.println("\n=== Orchestrator with Fallback ===");
        System.out.println(orch.execute("Hello world", endpoints));

        RateLimiter rl = new RateLimiter(10, 5);
        System.out.println("\n=== Rate Limiter ===");
        int granted = 0;
        for (int i = 0; i < 15; i++) if (rl.tryAcquire(1)) granted++;
        System.out.println("Granted " + granted + "/15 requests");

        ABTestTracker ab = new ABTestTracker();
        Random rng = new Random(42);
        for (int i = 0; i < 100; i++) {
            ab.record(rng.nextBoolean() ? "A" : "B", rng.nextDouble() < 0.05);
        }
        ab.report();

        System.out.println("\nGenAI Platform concepts validated.");
    }
}
