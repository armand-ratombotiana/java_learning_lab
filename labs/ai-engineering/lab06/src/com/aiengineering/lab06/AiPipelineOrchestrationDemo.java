package com.aiengineering.lab06;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Demonstrates AI pipeline orchestration: data preprocessing,
 * feature extraction, model inference, and post-processing.
 * <p>
 * Implements a configurable pipeline where each stage transforms
 * data through a chain of operations, with timing and metrics.
 */
public class AiPipelineOrchestrationDemo {

    // ---------- Pipeline Stage ----------

    @FunctionalInterface
    interface Stage<I, O> {
        O process(I input);
    }

    static class TimedStage<I, O> implements Stage<I, O> {
        private final String name;
        private final Stage<I, O> delegate;
        private long totalTime = 0;
        private int invocations = 0;

        TimedStage(String name, Stage<I, O> delegate) {
            this.name = name;
            this.delegate = delegate;
        }

        @Override
        public O process(I input) {
            long start = System.nanoTime();
            O result = delegate.process(input);
            totalTime += System.nanoTime() - start;
            invocations++;
            return result;
        }

        String getName() { return name; }
        long getAvgTimeMicros() { return invocations == 0 ? 0 : totalTime / invocations / 1000; }
        int getInvocations() { return invocations; }
    }

    // ---------- Pipeline ----------

    static class Pipeline {
        private final List<TimedStage<?, ?>> stages = new ArrayList<>();
        private final String name;

        Pipeline(String name) { this.name = name; }

        @SuppressWarnings("unchecked")
        <I, O> Pipeline addStage(String stageName, Stage<I, O> stage) {
            stages.add(new TimedStage<>(stageName, (Stage<Object, Object>) stage));
            return this;
        }

        @SuppressWarnings("unchecked")
        <T> T execute(T input) {
            System.out.println("Pipeline \"" + name + "\" starting...");
            Object current = input;
            for (TimedStage<?, ?> s : stages) {
                TimedStage<Object, Object> stage = (TimedStage<Object, Object>) s;
                current = stage.process(current);
            }
            return (T) current;
        }

        void printMetrics() {
            System.out.println("Pipeline \"" + name + "\" metrics:");
            for (TimedStage<?, ?> s : stages) {
                System.out.printf("  %s: %d invocations, avg %.2f µs%n",
                    s.getName(), s.getInvocations(), s.getAvgTimeMicros());
            }
        }
    }

    // ---------- Stages Implementation ----------

    static class TextPreprocessor implements Stage<String, String> {
        @Override
        public String process(String input) {
            String cleaned = input.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
            return cleaned;
        }
    }

    static class Tokenizer implements Stage<String, List<String>> {
        @Override
        public List<String> process(String input) {
            return Arrays.asList(input.split("\\s+"));
        }
    }

    static class FeatureExtractor implements Stage<List<String>, Map<String, Double>> {
        @Override
        public Map<String, Double> process(List<String> tokens) {
            Map<String, Double> features = new HashMap<>();
            features.put("token_count", (double) tokens.size());
            features.put("avg_token_length", tokens.stream()
                .mapToInt(String::length).average().orElse(0));
            features.put("unique_ratio", (double) new HashSet<>(tokens).size() / tokens.size());
            // Count specific keywords
            long keywordCount = tokens.stream()
                .filter(t -> Set.of("ai", "ml", "model", "data", "train").contains(t))
                .count();
            features.put("keyword_density", (double) keywordCount / tokens.size());
            return features;
        }
    }

    static class ModelInference implements Stage<Map<String, Double>, Double> {
        @Override
        public Double process(Map<String, Double> features) {
            // Simple mock model: weighted sum + bias
            double score = features.getOrDefault("token_count", 0) * 0.1
                + features.getOrDefault("avg_token_length", 0) * 0.3
                + features.getOrDefault("unique_ratio", 0) * 0.4
                + features.getOrDefault("keyword_density", 0) * 0.2
                - 0.5;
            return Math.max(0, Math.min(1, score));
        }
    }

    static class ResultFormatter implements Stage<Double, String> {
        @Override
        public String process(Double input) {
            String label = input < 0.3 ? "Low relevance" :
                           input < 0.6 ? "Medium relevance" :
                           input < 0.8 ? "High relevance" : "Very high relevance";
            return String.format("Score: %.4f — %s", input, label);
        }
    }

    // ---------- Main Demo ----------

    public static void main(String[] args) {
        System.out.println("=== AI Engineering Academy — Lab 06: AI Pipeline Orchestration ===\n");

        // Build pipeline
        Pipeline pipeline = new Pipeline("AI Content Analyzer")
            .addStage("Text Preprocessing", new TextPreprocessor())
            .addStage("Tokenization", new Tokenizer())
            .addStage("Feature Extraction", new FeatureExtractor())
            .addStage("Model Inference", new ModelInference())
            .addStage("Result Formatting", new ResultFormatter());

        // Sample inputs
        List<String> samples = Arrays.asList(
            "AI and ML models are trained on large datasets!",
            "The weather today is sunny and warm.",
            "Data scientists train machine learning models using AI techniques."
        );

        System.out.println("--- Pipeline Execution ---");
        for (int i = 0; i < samples.size(); i++) {
            System.out.println("\nInput " + (i + 1) + ": \"" + samples.get(i) + "\"");
            String result = pipeline.execute(samples.get(i));
            System.out.println("Output: " + result);
        }

        System.out.println("\n--- Pipeline Metrics ---");
        pipeline.printMetrics();

        System.out.println("\nDemo complete. Pipeline has 5 stages, executed on 3 samples.");
    }
}
