package com.aiengineering.lab07;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Demonstrates AI testing and evaluation: unit tests for AI
 * components, regression testing suites, and benchmark automation.
 * <p>
 * Includes a test runner framework, metric computation (accuracy,
 * precision, recall, F1), and regression test suites that detect
 * performance degradation.
 */
public class AiTestingAndEvaluationDemo {

    // ---------- Evaluation Metrics ----------

    static class Metrics {
        static double accuracy(List<String> predictions, List<String> groundTruth) {
            long correct = IntStream.range(0, predictions.size())
                .filter(i -> predictions.get(i).equals(groundTruth.get(i)))
                .count();
            return (double) correct / predictions.size();
        }

        static double precision(List<String> predictions, List<String> groundTruth, String positiveClass) {
            long tp = IntStream.range(0, predictions.size())
                .filter(i -> predictions.get(i).equals(positiveClass) && groundTruth.get(i).equals(positiveClass))
                .count();
            long fp = IntStream.range(0, predictions.size())
                .filter(i -> predictions.get(i).equals(positiveClass) && !groundTruth.get(i).equals(positiveClass))
                .count();
            return tp + fp == 0 ? 0 : (double) tp / (tp + fp);
        }

        static double recall(List<String> predictions, List<String> groundTruth, String positiveClass) {
            long tp = IntStream.range(0, predictions.size())
                .filter(i -> predictions.get(i).equals(positiveClass) && groundTruth.get(i).equals(positiveClass))
                .count();
            long fn = IntStream.range(0, predictions.size())
                .filter(i -> !predictions.get(i).equals(positiveClass) && groundTruth.get(i).equals(positiveClass))
                .count();
            return tp + fn == 0 ? 0 : (double) tp / (tp + fn);
        }

        static double f1(double precision, double recall) {
            return precision + recall == 0 ? 0 : 2 * precision * recall / (precision + recall);
        }
    }

    // ---------- Unit Test Framework ----------

    static class TestResult {
        final String testName;
        final boolean passed;
        final String message;

        TestResult(String testName, boolean passed, String message) {
            this.testName = testName;
            this.passed = passed;
            this.message = message;
        }

        boolean passed() { return passed; }
    }

    static class TestSuite {
        private final String name;
        private final List<Supplier<TestResult>> tests = new ArrayList<>();

        TestSuite(String name) { this.name = name; }

        void addTest(String testName, Runnable assertion) {
            tests.add(() -> {
                try {
                    assertion.run();
                    return new TestResult(testName, true, "PASSED");
                } catch (AssertionError e) {
                    return new TestResult(testName, false, "FAILED: " + e.getMessage());
                } catch (Exception e) {
                    return new TestResult(testName, false, "ERROR: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                }
            });
        }

        List<TestResult> run() {
            System.out.println("\n  Running suite: " + name);
            List<TestResult> results = tests.stream()
                .map(Supplier::get)
                .toList();
            long passed = results.stream().filter(TestResult::passed).count();
            System.out.printf("  Results: %d/%d passed%n", passed, results.size());
            results.forEach(r -> System.out.printf("    %s: %s%n", r.testName, r.passed ? "PASS" : "FAIL"));
            return results;
        }
    }

    // ---------- Benchmark Runner ----------

    static class BenchmarkResult {
        final String benchmarkName;
        final long opsPerSecond;
        final double avgLatencyMs;

        BenchmarkResult(String name, long ops, double lat) {
            this.benchmarkName = name; this.opsPerSecond = ops; this.avgLatencyMs = lat;
        }
    }

    static class BenchmarkRunner {
        static BenchmarkResult run(String name, Runnable task, int iterations) {
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) task.run();
            long totalNs = System.nanoTime() - start;
            double avgMs = (double) totalNs / iterations / 1_000_000;
            long opsPerSec = (long) (iterations / (totalNs / 1_000_000_000.0));
            return new BenchmarkResult(name, opsPerSec, avgMs);
        }
    }

    // ---------- Mock Classifier Under Test ----------

    static class MockClassifier {
        String classify(String text) {
            if (text.contains("good") || text.contains("great")) return "positive";
            if (text.contains("bad") || text.contains("terrible")) return "negative";
            return "neutral";
        }
    }

    // ---------- Main Demo ----------

    public static void main(String[] args) {
        System.out.println("=== AI Engineering Academy — Lab 07: AI Testing & Evaluation ===\n");

        // --- Unit Tests ---
        System.out.println("--- Unit Tests ---");
        TestSuite unitTests = new TestSuite("Classifier Tests");
        MockClassifier classifier = new MockClassifier();

        unitTests.addTest("classify_positive", () -> {
            String result = classifier.classify("This is good");
            if (!result.equals("positive")) throw new AssertionError("Expected 'positive' but got '" + result + "'");
        });
        unitTests.addTest("classify_negative", () -> {
            String result = classifier.classify("This is bad");
            if (!result.equals("negative")) throw new AssertionError("Expected 'negative' but got '" + result + "'");
        });
        unitTests.addTest("classify_neutral", () -> {
            String result = classifier.classify("This is a table");
            if (!result.equals("neutral")) throw new AssertionError("Expected 'neutral' but got '" + result + "'");
        });
        unitTests.addTest("classify_empty", () -> {
            String result = classifier.classify("");
            if (!result.equals("neutral")) throw new AssertionError("Expected 'neutral' for empty input");
        });

        var unitResults = unitTests.run();

        // --- Regression Tests ---
        System.out.println("\n--- Regression Tests ---");
        TestSuite regressionTests = new TestSuite("Sentiment Regression");
        List<String> testInputs = Arrays.asList(
            "This product is good", "Terrible experience", "It was okay",
            "Great service", "Bad quality", "Neutral statement"
        );
        List<String> expectedLabels = Arrays.asList(
            "positive", "negative", "neutral", "positive", "negative", "neutral"
        );

        regressionTests.addTest("regression_accuracy_threshold", () -> {
            List<String> predictions = testInputs.stream()
                .map(classifier::classify).toList();
            double acc = Metrics.accuracy(predictions, expectedLabels);
            if (acc < 1.0) throw new AssertionError(
                "Accuracy " + acc + " below threshold 1.0");
        });

        regressionTests.addTest("regression_metrics", () -> {
            List<String> predictions = testInputs.stream()
                .map(classifier::classify).toList();
            double prec = Metrics.precision(predictions, expectedLabels, "positive");
            double rec = Metrics.recall(predictions, expectedLabels, "positive");
            double f1 = Metrics.f1(prec, rec);
            System.out.printf("    Precision=%.2f Recall=%.2f F1=%.2f%n", prec, rec, f1);
            if (f1 < 0.5) throw new AssertionError("F1 score " + f1 + " below threshold");
        });

        regressionTests.run();

        // --- Benchmarks ---
        System.out.println("\n--- Benchmarks ---");
        BenchmarkResult br = BenchmarkRunner.run("classification",
            () -> classifier.classify("Benchmark test input for performance measurement"), 10000);
        System.out.printf("  %s: %.2f ms avg latency, %d ops/sec%n",
            br.benchmarkName, br.avgLatencyMs, br.opsPerSecond);

        // --- Metrics Demo ---
        System.out.println("\n--- Metric Computation Demo ---");
        List<String> preds = List.of("positive", "negative", "positive", "neutral", "positive");
        List<String> truth = List.of("positive", "negative", "neutral", "neutral", "negative");
        double acc = Metrics.accuracy(preds, truth);
        double prec = Metrics.precision(preds, truth, "positive");
        double rec = Metrics.recall(preds, truth, "positive");
        double f1 = Metrics.f1(prec, rec);
        System.out.printf("  Accuracy: %.2f%n", acc);
        System.out.printf("  Precision (positive): %.2f%n", prec);
        System.out.printf("  Recall (positive): %.2f%n", rec);
        System.out.printf("  F1 (positive): %.2f%n", f1);

        System.out.println("\nDemo complete. " + unitResults.size() + " unit tests, "
            + "2 regression suites, 1 benchmark run.");
    }
}
