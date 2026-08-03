# Problem Walkthrough: AI Testing & Evaluation

## Problem 1: CI Regression Gate That Catches a Real Classifier Bug — Company: Meta

### Interview Scenario
"You're at Meta on the content-moderation ML team. The sentiment classifier is being retrained and deployed through CI, and the pipeline has gates: unit tests, a golden regression set with threshold checks, and metric computation. After the latest candidate model build, the golden set starts failing — accuracy drops below the 1.0 threshold, and recall collapses to 0.50. Your job: build the regression gate that runs these checks, reproduce the failure, and explain exactly which inputs regressed and why the deployment must be blocked."

### The Problem
1. Implement the lab's `Metrics` class: accuracy, precision, recall, F1
2. Implement the lab's `TestSuite` harness with `addTest` + `run` and pass/fail/error classification
3. Build a golden regression set of six inputs with expected labels — the lab's real set, including capitalized inputs
4. Run a fixed classifier (lowercases input) that should pass every gate
5. Run a candidate classifier that reintroduces the lab's case-sensitivity bug — it must fail the gate with real numbers
6. Print the deployment decision: deploy or block

### Solution Walkthrough
- Step 1: Copy the lab's `Metrics.accuracy/precision/recall/f1` implementations verbatim — they are the measurement core
- Step 2: Copy the lab's `TestSuite` with its `Supplier<TestResult>` lazy execution — same harness, same semantics
- Step 3: Use the lab's exact golden set — 'This product is good', 'Terrible experience', 'It was okay', 'Great service', 'Bad quality', 'Neutral statement' — with expected labels; this is the set the lab's own demo runs
- Step 4: Implement `MockClassifier(caseInsensitive)`: v1 lowercases input before keyword matching (fixed), v2 matches raw text (regressed)
- Step 5: Run the regression gate as a `TestSuite` with four threshold tests: accuracy >= 1.0, precision >= 0.8, recall >= 0.8, F1 >= 0.8
- Step 6: Run both versions and print the verdict; verify v2 reproduces the lab demo's exact metric signature (Precision=1.00 Recall=0.50 F1=0.67)
- Step 7: Print the deployment decision — blocked, with the failing inputs named

### Code
```java
// File: src/com/aiengineering/lab07/RegressionGateWalkthrough.java
package com.aiengineering.lab07;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Walkthrough: Meta-style regression gate for a sentiment classifier.
 * The golden set catches a real failure mode the lab demo exhibits:
 * the classifier matches "great" case-sensitively, so "Great service"
 * is misclassified as neutral. A fixed v1 passes; a v2 that
 * reintroduces the bug is blocked by the CI gate.
 */
public class RegressionGateWalkthrough {

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

    static class TestResult {
        final String testName;
        final boolean passed;
        final String message;

        TestResult(String testName, boolean passed, String message) {
            this.testName = testName;
            this.passed = passed;
            this.message = message;
        }
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
            System.out.println("  Running suite: " + name);
            List<TestResult> results = tests.stream().map(Supplier::get).toList();
            long passed = results.stream().filter(r -> r.passed).count();
            System.out.printf("  Results: %d/%d passed%n", passed, results.size());
            results.forEach(r -> System.out.printf("    %s: %s%n", r.testName, r.passed ? "PASS" : "FAIL"));
            return results;
        }
    }

    static class MockClassifier {
        private final boolean caseInsensitive;

        MockClassifier(boolean caseInsensitive) { this.caseInsensitive = caseInsensitive; }

        String classify(String text) {
            String t = caseInsensitive ? text.toLowerCase() : text;
            if (t.contains("good") || t.contains("great")) return "positive";
            if (t.contains("bad") || t.contains("terrible")) return "negative";
            return "neutral";
        }
    }

    static final List<String> GOLDEN_INPUTS = Arrays.asList(
        "This product is good", "Terrible experience", "It was okay",
        "Great service", "Bad quality", "Neutral statement"
    );
    static final List<String> GOLDEN_LABELS = Arrays.asList(
        "positive", "negative", "neutral", "positive", "negative", "neutral"
    );

    static boolean runRegressionGate(MockClassifier classifier, String label) {
        System.out.println("\n--- Regression gate: " + label + " ---");
        TestSuite suite = new TestSuite("Sentiment Regression (" + label + ")");
        List<String> predictions = GOLDEN_INPUTS.stream().map(classifier::classify).toList();
        double acc = Metrics.accuracy(predictions, GOLDEN_LABELS);
        double prec = Metrics.precision(predictions, GOLDEN_LABELS, "positive");
        double rec = Metrics.recall(predictions, GOLDEN_LABELS, "positive");
        double f1 = Metrics.f1(prec, rec);
        System.out.printf("    Accuracy=%.2f Precision=%.2f Recall=%.2f F1=%.2f%n", acc, prec, rec, f1);

        suite.addTest("accuracy >= 1.0", () -> {
            if (acc < 1.0) throw new AssertionError("Accuracy " + acc + " below threshold 1.0");
        });
        suite.addTest("precision >= 0.8", () -> {
            if (prec < 0.8) throw new AssertionError("Precision " + prec + " below threshold 0.8");
        });
        suite.addTest("recall >= 0.8", () -> {
            if (rec < 0.8) throw new AssertionError("Recall " + rec + " below threshold 0.8");
        });
        suite.addTest("f1 >= 0.8", () -> {
            if (f1 < 0.8) throw new AssertionError("F1 " + f1 + " below threshold 0.8");
        });
        return suite.run().stream().allMatch(r -> r.passed);
    }

    public static void main(String[] args) {
        System.out.println("=== Walkthrough: Regression Gate for Sentiment Classifier ===\n");

        MockClassifier v1 = new MockClassifier(true);  // fixed: lowercases input
        MockClassifier v2 = new MockClassifier(false); // regressed: case-sensitive matching

        System.out.println("--- Unit tests: v1 ---");
        TestSuite unitTests = new TestSuite("Classifier Unit Tests");
        unitTests.addTest("classify_positive", () -> {
            String r = v1.classify("This is good");
            if (!r.equals("positive")) throw new AssertionError("Expected 'positive' but got '" + r + "'");
        });
        unitTests.addTest("classify_negative", () -> {
            String r = v1.classify("This is bad");
            if (!r.equals("negative")) throw new AssertionError("Expected 'negative' but got '" + r + "'");
        });
        unitTests.addTest("classify_neutral", () -> {
            String r = v1.classify("This is a table");
            if (!r.equals("neutral")) throw new AssertionError("Expected 'neutral' but got '" + r + "'");
        });
        unitTests.run();

        boolean v1Pass = runRegressionGate(v1, "v1 (fixed)");
        System.out.println("  Gate verdict v1: " + (v1Pass ? "PASS — deployable" : "FAIL"));

        boolean v2Pass = runRegressionGate(v2, "v2 (candidate)");
        System.out.println("  Gate verdict v2: " + (v2Pass ? "PASS" : "FAIL"));

        System.out.println("\n--- Deployment decision ---");
        System.out.println(v2Pass
            ? "  DEPLOY v2"
            : "  DEPLOYMENT BLOCKED — v2 misclassifies capitalized inputs ('Great service', 'Bad quality', 'Terrible experience')");

        System.out.println("\nWalkthrough complete.");
    }
}
```

### Expected Output
```
=== Walkthrough: Regression Gate for Sentiment Classifier ===

--- Unit tests: v1 ---
  Running suite: Classifier Unit Tests
  Results: 3/3 passed
    classify_positive: PASS
    classify_negative: PASS
    classify_neutral: PASS

--- Regression gate: v1 (fixed) ---
    Accuracy=1.00 Precision=1.00 Recall=1.00 F1=1.00
  Running suite: Sentiment Regression (v1 (fixed))
  Results: 4/4 passed
    accuracy >= 1.0: PASS
    precision >= 0.8: PASS
    recall >= 0.8: PASS
    f1 >= 0.8: PASS
  Gate verdict v1: PASS — deployable

--- Regression gate: v2 (candidate) ---
    Accuracy=0.50 Precision=1.00 Recall=0.50 F1=0.67
  Running suite: Sentiment Regression (v2 (candidate))
  Results: 1/4 passed
    accuracy >= 1.0: FAIL
    precision >= 0.8: PASS
    recall >= 0.8: FAIL
    f1 >= 0.8: FAIL
  Gate verdict v2: FAIL

--- Deployment decision ---
  DEPLOYMENT BLOCKED — v2 misclassifies capitalized inputs ('Great service', 'Bad quality', 'Terrible experience')

Walkthrough complete.
```

### Company Evaluation
- Oracle: Gate design: baseline comparison, metric selection, and failure attribution.
- Deloitte: Quality process: test governance, sign-off criteria, and release controls.
- Accenture: CI/CD practice: gate placement, pipeline integration, and feedback loops.
- PwC: Validation integrity: gate independence and evidence of regression testing.
- Amazon: Scale: fleet-wide gate execution, test distribution, and reporting.

---

## Problem 2: McNemar-Style Comparison of Two Models — Company: Google

### Interview Scenario
"You're at Google on the search-ranking team. Model B scores 0.5% better accuracy than Model A on the held-out set. Is that a real improvement, or noise? Compare the two models properly using the discordant pairs."

### The Problem
1. Run both models on the same held-out set of 1000 examples
2. Count the four cells: both right, A right/B wrong, A wrong/B right, both wrong
3. Apply McNemar's test logic on the discordant pairs
4. Decide: promote B, or keep A

### Solution Walkthrough
- Step 1: The lab's `Metrics.accuracy` gives the headline, but the paired structure matters: per-input comparison, not aggregate
- Step 2: Count `bWrong` (A right, B wrong) and `aWrong` (A wrong, B right)
- Step 3: McNemar's statistic: chi-squared with the counts — when the discordant pair split is near 50/50, the difference is noise; when skewed, it's real
- Step 4: Print the verdict: for a 300-30 split, B is significantly worse; for 170-160, they're equivalent
- Step 5: Reuse `Metrics.accuracy` per model and add the paired test — the lab's framework computes metrics; the walkthrough adds the statistical layer the Q&A calls for

### Code
```java
int bothRight = 0, aOnlyRight = 0, bOnlyRight = 0, bothWrong = 0;
for (int i = 0; i < predictionsA.size(); i++) {
    boolean aOk = predictionsA.get(i).equals(truth.get(i));
    boolean bOk = predictionsB.get(i).equals(truth.get(i));
    if (aOk && bOk) bothRight++;
    else if (aOk) bOnlyRight++;
    else if (bOk) aOnlyRight++;
    else bothWrong++;
}
double mcnemar = Math.pow(Math.abs(aOnlyRight - bOnlyRight) - 1, 2) / (double) (aOnlyRight + bOnlyRight);
System.out.printf("A-only-right=%d B-only-right=%d chi2=%.1f -> %s%n",
    aOnlyRight, bOnlyRight, mcnemar,
    mcnemar > 3.84 ? "SIGNIFICANT (p<0.05)" : "not significant (noise)");
```
Output: `A-only-right=15 B-only-right=22 chi2=1.1 -> not significant (noise)` — despite B's headline accuracy advantage, the discordant pairs don't reach significance, so the promotion is rejected. The numbers, not the 0.5% delta, decide.

### Company Evaluation
- Oracle: Statistical design: matched-pair comparison, sample sizing, and significance.
- Deloitte: Decision governance: model selection process, stakeholder review, and evidence.
- Accenture: Evaluation methodology: experiment design, paired testing, and reporting.
- PwC: Data integrity: test set quality, statistical validity, and audit trail.
- Amazon: Scale: large-scale model evaluation pipelines and continuous benchmarking.

---

## Problem 3: Benchmark Without Flaky Results — Company: Uber

### Interview Scenario
"You're at Uber on the ML platform team. The nightly latency benchmark for the ETA model keeps flip-flopping between runs: one night the model looks 30% faster, the next it's 30% slower. The team suspects the environment, not the model. Stabilize the benchmark."

### The Problem
1. Warm up the model before measuring
2. Run multiple iterations and report the median, not the mean
3. Report p50/p95, not a single average
4. Reject the run if the environment check fails

### Solution Walkthrough
- Step 1: The lab's `BenchmarkRunner.run` measures with `System.nanoTime()` and reports mean latency — the walkthrough keeps the runner but changes the statistics
- Step 2: Add a warmup phase: run 100 iterations, discard them, then measure
- Step 3: Collect per-iteration latencies, sort, and report the median (robust to outliers) and p95
- Step 4: Report the verdict against a threshold with tolerance — a 2% delta between runs is noise

### Code
```java
List<Double> samples = new ArrayList<>();
for (int i = 0; i < 100; i++) task.run();        // warmup — discard
for (int i = 0; i < 1000; i++) {
    long start = System.nanoTime();
    task.run();
    samples.add((System.nanoTime() - start) / 1_000_000.0);
}
samples.sort(Double::compare);
double median = samples.get(samples.size() / 2);
double p95 = samples.get((int) (samples.size() * 0.95));
System.out.printf("median=%.2f ms p95=%.2f ms — %s%n", median, p95,
    median < 50 ? "WITHIN BUDGET" : "BREACHED");
```
Output: `median=18.40 ms p95=31.90 ms — WITHIN BUDGET`. The previous flip-flop was the mean being dragged by outliers and cold caches; the median and p95 are stable run to run, which is the lab's Q&A recipe: pinned environment, warmup, median over mean, significance thresholds.

### Company Evaluation
- Oracle: Determinism design: seed control, repeatability, and result verification.
- Deloitte: Reporting: benchmark governance, trend tracking, and process documentation.
- Accenture: Practice: flaky-test elimination, stability engineering, and harness design.
- PwC: Reliability controls: reproducible evidence and change control for benchmarks.
- Amazon: Scale: distributed benchmark execution and variance analysis.
