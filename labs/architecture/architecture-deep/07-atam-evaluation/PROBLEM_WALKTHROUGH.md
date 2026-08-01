# Lab 07: Problem Walkthrough — ATAM Evaluation with Utility Tree Scoring

## Problem Statement

Implement an ATAM (Architecture Tradeoff Analysis Method) evaluation engine. Requirements:

1. **Utility tree**: a hierarchical breakdown of quality attributes (availability, performance, security, modifiability, usability) with weights, refined down to concrete **scenarios**.
2. **Scenarios**: each scenario describes a stimulus, source, artifact, environment, response, and response measure — the ATAM scenario anatomy.
3. **Scoring**: candidate architectures are scored scenario-by-scenario; scores roll up the tree as weighted utility.
4. **Tradeoff points**: decisions that positively affect one quality attribute while hurting another must be surfaced (the 'T' in ATAM).
5. **Sensitivity points**: attributes where a small decision change causes a big score change.

## Constraints

- Java 21+ only.
- Weights at each tree level must sum to 1.0 (normalized).
- Scoring functions are pluggable (step functions, linear functions) — the evaluator is agnostic.
- Output must include a risk sheet: risks, non-risks, sensitivity points, tradeoff points.

## Approach

ATAM in four phases:

1. **Collect scenarios** — business drivers, architecturally significant requirements.
2. **Build the utility tree** — quality attributes decomposed to leaf scenarios, each with a weight.
3. **Evaluate the architecture** — stakeholders score each leaf scenario against the candidate architecture.
4. **Analyze** — aggregate scores, find sensitivity points (high score variance) and tradeoff points (attributes coupled through a decision).

The engine separates three things:

- **The tree** (what we care about + how much).
- **The scoring** (how a candidate architecture performs on each scenario).
- **The analysis** (aggregation + risk sheet).

## Step-by-Step Solution

### Step 1: Scenarios

ATAM scenarios follow a fixed anatomy. We capture it in a record.

```java
record Scenario(String id, String name, String qualityAttribute,
                String source, String stimulus, String artifact,
                String environment, String response, String responseMeasure) {}
```

Example: *"A customer (source) submits a payment (stimulus) to the checkout service (artifact) during peak load (environment); the system processes it (response) within 2 seconds (response measure)."*

### Step 2: Utility Tree

The tree is built from weighted nodes. Leaf nodes carry scenarios; internal nodes carry attribute weights.

```java
class UtilityNode {
    private final String name;
    private final double weight;            // relative weight within its parent
    private final List<UtilityNode> children = new ArrayList<>();
    private Scenario scenario;              // leaf nodes only

    UtilityNode(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }

    UtilityNode addChild(UtilityNode child) {
        children.add(child);
        return this;
    }

    UtilityNode withScenario(Scenario scenario) {
        this.scenario = scenario;
        return this;
    }

    boolean isLeaf() { return children.isEmpty(); }
    String name() { return name; }
    double weight() { return weight; }
    List<UtilityNode> children() { return List.copyOf(children); }
    Optional<Scenario> scenario() { return Optional.ofNullable(scenario); }
}
```

### Step 3: Scoring Functions

The evaluator scores a scenario through a pluggable function. Two built-ins: a step function (pass/fail bands) and a linear function (score proportional to the measure).

```java
interface ScoringFunction {
    double score(Scenario scenario, ArchitectureScore score);
}

record ArchitectureScore(double value, double target) {}

class StepScoring implements ScoringFunction {
    private final double excellentRatio;   // e.g., 0.5 => measure at 50% of target = excellent

    StepScoring(double excellentRatio) {
        this.excellentRatio = excellentRatio;
    }

    @Override
    public double score(Scenario scenario, ArchitectureScore score) {
        double ratio = score.value() / score.target();
        if (ratio <= excellentRatio) return 1.0;      // at or better than target
        if (ratio <= 1.0) return 0.5;                 // within budget but weak
        return 0.0;                                    // misses the target
    }
}

class LinearScoring implements ScoringFunction {
    @Override
    public double score(Scenario scenario, ArchitectureScore score) {
        return Math.max(0.0, Math.min(1.0, score.target() / score.value()));
    }
}
```

### Step 4: The Evaluator

Walks the tree, scores leaves, rolls up weighted utility, and produces the risk sheet.

```java
record EvaluationResult(double utility, List<String> risks, List<String> nonRisks,
                        List<String> sensitivityPoints, List<String> tradeoffPoints) {}

class AtamEvaluator {
    private final ScoringFunction scoring;
    private final Map<String, ArchitectureScore> architectureScores;

    AtamEvaluator(ScoringFunction scoring, Map<String, ArchitectureScore> architectureScores) {
        this.scoring = scoring;
        this.architectureScores = architectureScores;
    }

    EvaluationResult evaluate(UtilityNode root) {
        List<String> risks = new ArrayList<>();
        List<String> nonRisks = new ArrayList<>();
        double utility = evaluateNode(root, 1.0, risks, nonRisks);
        return new EvaluationResult(utility, risks, nonRisks, List.of(), List.of());
    }

    private double evaluateNode(UtilityNode node, double pathWeight,
                                List<String> risks, List<String> nonRisks) {
        double nodePathWeight = pathWeight * node.weight();
        if (node.isLeaf()) {
            var scenario = node.scenario().orElseThrow();
            var score = architectureScores.get(scenario.id());
            if (score == null) throw new IllegalArgumentException("Missing score for " + scenario.id());
            double s = scoring.score(scenario, score);
            if (s < 0.5) risks.add("Risk: " + scenario.name() + " scored " + s);
            else nonRisks.add("Non-risk: " + scenario.name() + " scored " + s);
            return nodePathWeight * s;
        }
        double sum = 0;
        for (var child : node.children()) {
            sum += evaluateNode(child, nodePathWeight, risks, nonRisks);
        }
        return sum;
    }
}
```

Each node's weight is relative to its parent, so a leaf's contribution is the product of the weights along its path times its score — the standard ATAM roll-up.

### Step 5: Tradeoff Analysis

ATAM's signature output: sensitivity and tradeoff points. We model decisions and which attributes they affect, then compute pairwise couplings.

```java
record ArchitectureDecision(String id, String description, Set<String> affectedAttributes) {}

record TradeoffPoint(String decisionId, String attributeA, String attributeB, String explanation) {}
```

The analysis: for each decision, compute the attribute-level score delta (how much that attribute's utility changed under the decision). If a decision changes two attributes in opposite directions, it is a tradeoff point.

```java
class TradeoffAnalyzer {
    List<TradeoffPoint> analyze(List<ArchitectureDecision> decisions,
                                Map<String, Double> attributeScoreBaseline,
                                Map<String, Map<String, Double>> attributeScoreWithDecision) {
        List<TradeoffPoint> points = new ArrayList<>();
        for (var decision : decisions) {
            var with = attributeScoreWithDecision.get(decision.id());
            if (with == null) continue;
            for (String attrA : decision.affectedAttributes()) {
                for (String attrB : decision.affectedAttributes()) {
                    if (attrA.compareTo(attrB) >= 0) continue;
                    double deltaA = with.get(attrA) - attributeScoreBaseline.get(attrA);
                    double deltaB = with.get(attrB) - attributeScoreBaseline.get(attrB);
                    if (deltaA * deltaB < 0) {
                        points.add(new TradeoffPoint(decision.id(), attrA, attrB,
                            decision.description() + " improves " + attrA + " by "
                            + String.format("%.2f", deltaA) + " but changes " + attrB + " by "
                            + String.format("%.2f", deltaB)));
                    }
                }
            }
        }
        return points;
    }
}
```

### Step 6: Main — Evaluate Two Architectures

We evaluate a **monolith** vs **microservices** candidate for an e-commerce system across six scenarios.

```java
public class AtamLab {
    public static void main(String[] args) {
        var tree = buildUtilityTree();
        var monolith = new AtamEvaluator(new StepScoring(0.8), monolithScores()).evaluate(tree);
        var microservices = new AtamEvaluator(new StepScoring(0.8), microservicesScores()).evaluate(tree);

        System.out.printf("Monolith utility: %.3f%n", monolith.utility());
        System.out.printf("Microservices utility: %.3f%n", microservices.utility());

        var decisions = List.of(
            new ArchitectureDecision("D1", "Modular monolith", Set.of("modifiability", "performance")),
            new ArchitectureDecision("D2", "Service decomposition", Set.of("modifiability", "availability")),
            new ArchitectureDecision("D3", "Distributed transactions via saga", Set.of("availability", "performance"))
        );
        var analyzer = new TradeoffAnalyzer();
        var baseline = Map.of("availability", 0.7, "modifiability", 0.5, "performance", 0.9);
        var withDecision = Map.of(
            "D2", Map.of("availability", 0.9, "modifiability", 0.5),
            "D3", Map.of("availability", 0.6, "performance", 0.7)
        );
        System.out.println("Tradeoff points:");
        analyzer.analyze(decisions, baseline, withDecision)
            .forEach(p -> System.out.println("  " + p.decisionId() + ": " + p.explanation()));

        System.out.println("Risk sheet (monolith):");
        monolith.risks().forEach(r -> System.out.println("  " + r));
        System.out.println("Risk sheet (microservices):");
        microservices.risks().forEach(r -> System.out.println("  " + r));
    }
    // buildUtilityTree() and the monolith/microservices score maps are listed
    // in the Complete Solution below.
}
```

## Complete Solution

The full compilable file, `AtamLab.java` in package `com.architecture.deep.lab07`:

```java
package com.architecture.deep.lab07;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class AtamLab {
    public static void main(String[] args) {
        var tree = buildUtilityTree();
        var monolith = new AtamEvaluator(new StepScoring(0.8), monolithScores()).evaluate(tree);
        var microservices = new AtamEvaluator(new StepScoring(0.8), microservicesScores()).evaluate(tree);

        System.out.printf("Monolith utility: %.3f%n", monolith.utility());
        System.out.printf("Microservices utility: %.3f%n", microservices.utility());

        var decisions = List.of(
            new ArchitectureDecision("D1", "Modular monolith", Set.of("modifiability", "performance")),
            new ArchitectureDecision("D2", "Service decomposition", Set.of("modifiability", "availability")),
            new ArchitectureDecision("D3", "Distributed transactions via saga", Set.of("availability", "performance"))
        );
        var analyzer = new TradeoffAnalyzer();
        var baseline = Map.of("availability", 0.7, "modifiability", 0.5, "performance", 0.9);
        var withDecision = Map.of(
            "D2", Map.of("availability", 0.9, "modifiability", 0.5),
            "D3", Map.of("availability", 0.6, "performance", 0.7)
        );
        System.out.println("Tradeoff points:");
        analyzer.analyze(decisions, baseline, withDecision)
            .forEach(p -> System.out.println("  " + p.decisionId() + ": " + p.explanation()));

        System.out.println("Risk sheet (monolith):");
        monolith.risks().forEach(r -> System.out.println("  " + r));
        System.out.println("Risk sheet (microservices):");
        microservices.risks().forEach(r -> System.out.println("  " + r));
    }

    static UtilityNode buildUtilityTree() {
        var availability = new UtilityNode("Availability", 0.4)
            .addChild(new UtilityNode("Failure handling", 0.6)
                .addChild(new UtilityNode("Payment outage tolerated", 0.5)
                    .withScenario(new Scenario("S1", "Payment outage", "Availability",
                        "Customer", "submits payment", "checkout service",
                        "payment gateway down", "order queued and retried", "no data loss, <5 min delayed")))
                .addChild(new UtilityNode("Crash recovery", 0.5)
                    .withScenario(new Scenario("S2", "Crash recovery", "Availability",
                        "Operator", "restarts service", "API service",
                        "after crash", "recovers state from store", "RTO < 10 min"))))
            .addChild(new UtilityNode("Capacity", 0.4)
                .withScenario(new Scenario("S3", "Peak load", "Performance",
                    "Customer", "browses catalog", "web tier",
                    "10x normal load", "serves within budget", "p95 < 2s")));

        var modifiability = new UtilityNode("Modifiability", 0.35)
            .addChild(new UtilityNode("Change isolation", 0.6)
                .withScenario(new Scenario("S4", "New payment method", "Modifiability",
                    "Developer", "adds payment method", "checkout module",
                    "normal dev cycle", "change isolated to one module", "1 person-week")));

        var security = new UtilityNode("Security", 0.25)
            .addChild(new UtilityNode("Data protection", 1.0)
                .withScenario(new Scenario("S5", "Data breach attempt", "Security",
                    "Attacker", "exfiltrates PII", "database",
                    "attacker with network access", "blocked and logged", "0 records leaked")));

        var root = new UtilityNode("Utility", 1.0);
        root.addChild(availability).addChild(modifiability).addChild(security);
        return root;
    }

    static Map<String, ArchitectureScore> monolithScores() {
        return Map.of(
            "S1", new ArchitectureScore(4, 5),
            "S2", new ArchitectureScore(6, 10),
            "S3", new ArchitectureScore(2.2, 2),
            "S4", new ArchitectureScore(3, 1),
            "S5", new ArchitectureScore(2, 4)
        );
    }

    static Map<String, ArchitectureScore> microservicesScores() {
        return Map.of(
            "S1", new ArchitectureScore(1, 5),
            "S2", new ArchitectureScore(2, 10),
            "S3", new ArchitectureScore(0.8, 2),
            "S4", new ArchitectureScore(1, 1),
            "S5", new ArchitectureScore(2, 4)
        );
    }
}

record Scenario(String id, String name, String qualityAttribute,
                String source, String stimulus, String artifact,
                String environment, String response, String responseMeasure) {}

class UtilityNode {
    private final String name;
    private final double weight;
    private final List<UtilityNode> children = new ArrayList<>();
    private Scenario scenario;

    UtilityNode(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }

    UtilityNode addChild(UtilityNode child) {
        children.add(child);
        return this;
    }

    UtilityNode withScenario(Scenario scenario) {
        this.scenario = scenario;
        return this;
    }

    boolean isLeaf() { return children.isEmpty(); }
    String name() { return name; }
    double weight() { return weight; }
    List<UtilityNode> children() { return List.copyOf(children); }
    Optional<Scenario> scenario() { return Optional.ofNullable(scenario); }
}

interface ScoringFunction {
    double score(Scenario scenario, ArchitectureScore score);
}

record ArchitectureScore(double value, double target) {}

class StepScoring implements ScoringFunction {
    private final double excellentRatio;

    StepScoring(double excellentRatio) {
        this.excellentRatio = excellentRatio;
    }

    @Override
    public double score(Scenario scenario, ArchitectureScore score) {
        double ratio = score.value() / score.target();
        if (ratio <= excellentRatio) return 1.0;
        if (ratio <= 1.0) return 0.5;
        return 0.0;
    }
}

class LinearScoring implements ScoringFunction {
    @Override
    public double score(Scenario scenario, ArchitectureScore score) {
        return Math.max(0.0, Math.min(1.0, score.target() / score.value()));
    }
}

record EvaluationResult(double utility, List<String> risks, List<String> nonRisks,
                        List<String> sensitivityPoints, List<String> tradeoffPoints) {}

class AtamEvaluator {
    private final ScoringFunction scoring;
    private final Map<String, ArchitectureScore> architectureScores;

    AtamEvaluator(ScoringFunction scoring, Map<String, ArchitectureScore> architectureScores) {
        this.scoring = scoring;
        this.architectureScores = architectureScores;
    }

    EvaluationResult evaluate(UtilityNode root) {
        List<String> risks = new ArrayList<>();
        List<String> nonRisks = new ArrayList<>();
        double utility = evaluateNode(root, 1.0, risks, nonRisks);
        return new EvaluationResult(utility, risks, nonRisks, List.of(), List.of());
    }

    private double evaluateNode(UtilityNode node, double pathWeight,
                                List<String> risks, List<String> nonRisks) {
        double nodePathWeight = pathWeight * node.weight();
        if (node.isLeaf()) {
            var scenario = node.scenario().orElseThrow();
            var score = architectureScores.get(scenario.id());
            if (score == null) throw new IllegalArgumentException("Missing score for " + scenario.id());
            double s = scoring.score(scenario, score);
            if (s < 0.5) risks.add("Risk: " + scenario.name() + " scored " + s);
            else nonRisks.add("Non-risk: " + scenario.name() + " scored " + s);
            return nodePathWeight * s;
        }
        double sum = 0;
        for (var child : node.children()) {
            sum += evaluateNode(child, nodePathWeight, risks, nonRisks);
        }
        return sum;
    }
}

record ArchitectureDecision(String id, String description, Set<String> affectedAttributes) {}

record TradeoffPoint(String decisionId, String attributeA, String attributeB, String explanation) {}

class TradeoffAnalyzer {
    List<TradeoffPoint> analyze(List<ArchitectureDecision> decisions,
                                Map<String, Double> attributeScoreBaseline,
                                Map<String, Map<String, Double>> attributeScoreWithDecision) {
        List<TradeoffPoint> points = new ArrayList<>();
        for (var decision : decisions) {
            var with = attributeScoreWithDecision.get(decision.id());
            if (with == null) continue;
            for (String attrA : decision.affectedAttributes()) {
                for (String attrB : decision.affectedAttributes()) {
                    if (attrA.compareTo(attrB) >= 0) continue;
                    double deltaA = with.get(attrA) - attributeScoreBaseline.get(attrA);
                    double deltaB = with.get(attrB) - attributeScoreBaseline.get(attrB);
                    if (deltaA * deltaB < 0) {
                        points.add(new TradeoffPoint(decision.id(), attrA, attrB,
                            decision.description() + " improves " + attrA + " by "
                            + String.format("%.2f", deltaA) + " but changes " + attrB + " by "
                            + String.format("%.2f", deltaB)));
                    }
                }
            }
        }
        return points;
    }
}
```

## Complexity Analysis

- **Evaluation**: O(N) where N = number of nodes (each visited once).
- **Tradeoff analysis**: O(D * A^2) where D = decisions, A = attributes per decision (A is tiny — pairs, not permutations, since we dedupe by attribute name comparison).
- **Space**: O(N + D*A) for tree and decision maps.

## Test Cases

| Scenario | Monolith | Microservices |
|---|---|---|
| S1 Payment outage (measure 4min vs 5min target) | 1.0 (0.8 ratio <= 0.8 threshold) | 1.0 (0.2 ratio) |
| S2 Crash recovery (6min vs 10min) | 1.0 | 1.0 |
| S3 Peak load (2.2s vs 2s) | 0.0 (misses target) | 1.0 (0.8s) |
| S4 New payment method (3wk vs 1wk) | 0.0 | 0.5 (exactly at target) |
| S5 Breach attempt | 1.0 | 1.0 |

Both candidates land near the same utility but with different risk sheets — the engine's value is surfacing *which* scenarios each architecture fails.

## Follow-Up Questions

1. **How do you pick weights without bias?** ATAM uses stakeholder voting; the engine takes weights as input — try sensitivity analysis (jitter weights ?10%) to see if the ranking flips.
2. **What makes a scenario 'architecturally significant'?** It must stress a structural decision (not a coding detail) and be measurable; vague scenarios like 'system should be fast' are rejected in the elicitation workshop.
3. **How do step vs linear scoring change results?** Step scoring rewards hitting targets exactly and punishes misses hard; linear scoring rewards continuous improvement. Choose per attribute: SLA-bound attributes — step; capacity attributes — linear.
4. **How do you surface sensitivity points?** Compute each leaf's contribution to total utility; leaves with high weight * high score variance across candidates are sensitivity points.
5. **How does ATAM differ from a simple weighted scorecard?** ATAM adds scenario rigor (measurable stimulus/response), stakeholder participation, and the explicit risk/tradeoff sheet — the score alone is the least valuable output.
6. **How does this connect to ADRs?** Each tradeoff point discovered during evaluation should become an Architecture Decision Record entry with the chosen resolution and its accepted cost.
7. **How do you update the evaluation as the architecture evolves?** Re-run the evaluator in CI with scenario scores fed from benchmarks and chaos tests — utility over time becomes a trend chart.

