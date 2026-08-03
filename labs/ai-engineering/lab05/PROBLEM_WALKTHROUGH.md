# Problem Walkthrough: Prompt Engineering at Scale

## Problem 1: Versioned Registry with A/B Testing and Auto-Rollback — Company: Anthropic

### Interview Scenario
"You're at Anthropic on the platform team, managing prompt templates for a summarization product used across thousands of clients. A prompt change went live last week and silently degraded output quality — there was no way to measure the change, no way to compare it against the previous version, and no automatic reversal. Build the governance loop: a versioned prompt registry, an A/B framework that compares a new prompt against the champion, and a guardrail that auto-rolls back any version that loses."

### The Problem
1. Implement the lab's `PromptRegistry` with versioned `PromptTemplate` records and metadata (author, purpose)
2. Implement `render(Map)` so variants are tested on identical variables
3. Implement an `ABTestFramework` that runs N trials of two variants and reports win rate and average latency
4. Add a deterministic quality judge so the demo is reproducible — a stand-in for LLM-as-judge
5. Implement `GovernanceGuardrail.evaluateAndRollback`: A/B the latest against the previous version; roll back when the latest wins less than 50%
6. Show the full lifecycle: register three versions, render, A/B v1 vs v2, deploy a bad v4, watch the guardrail revert it

### Solution Walkthrough
- Step 1: Reuse the lab's `PromptTemplate` record and `render` — `{{variable}}` substitution, unchanged
- Step 2: Reuse the lab's `PromptRegistry` with `ConcurrentHashMap` + `CopyOnWriteArrayList` + `AtomicInteger` version counter, and all five operations (`register`, `getLatest`, `getVersion`, `getAllVersions`, `rollback`)
- Step 3: Reuse the lab's `AbTestResult` shape (variant ids, trials, win rate, avg latency) and the `ABTestFramework.runTest` loop
- Step 4: Replace the random `MockLlmClient` with a deterministic judge: `qualityOf(version, rendered)` returns a per-version score — this is the deterministic stand-in for LLM-as-judge scoring that real pipelines use
- Step 5: Add `GovernanceGuardrail`: compare the two newest versions; if the latest's win rate is below 0.5, call `registry.rollback(id)` and report the revert
- Step 6: Drive the demo: register v1/v2/v3, render v3, A/B v1 vs v2, register a regression candidate v4, let the guardrail reject it, print the final registry state

### Code
```java
// File: src/com/aiengineering/lab05/PromptGovernanceWalkthrough.java
package com.aiengineering.lab05;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Walkthrough: Anthropic-style prompt governance — versioned registry,
 * A/B testing with a deterministic judge, and automatic rollback when
 * a new prompt version underperforms the previous one. Mirrors the
 * lab's PromptRegistry, PromptTemplate.render, and ABTestFramework.
 */
public class PromptGovernanceWalkthrough {

    public record PromptTemplate(String id, String version, String template, Map<String, String> metadata) {
        String render(Map<String, String> variables) {
            String result = template;
            for (var entry : variables.entrySet()) {
                result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
            return result;
        }
    }

    static class PromptRegistry {
        private final Map<String, List<PromptTemplate>> templates = new ConcurrentHashMap<>();
        private final AtomicInteger versionCounter = new AtomicInteger(0);

        void register(String id, String template, Map<String, String> metadata) {
            String version = "v" + versionCounter.incrementAndGet();
            templates.computeIfAbsent(id, k -> new CopyOnWriteArrayList<>())
                .add(new PromptTemplate(id, version, template, metadata));
        }

        PromptTemplate getLatest(String id) {
            List<PromptTemplate> versions = templates.get(id);
            if (versions == null || versions.isEmpty()) return null;
            return versions.get(versions.size() - 1);
        }

        PromptTemplate getVersion(String id, String version) {
            List<PromptTemplate> versions = templates.get(id);
            if (versions == null) return null;
            return versions.stream().filter(t -> t.version().equals(version)).findFirst().orElse(null);
        }

        List<PromptTemplate> getAllVersions(String id) {
            return List.copyOf(templates.getOrDefault(id, List.of()));
        }

        void rollback(String id) {
            List<PromptTemplate> versions = templates.get(id);
            if (versions != null && versions.size() > 1) {
                versions.remove(versions.size() - 1);
            }
        }
    }

    // Deterministic stand-in for LLM-as-judge: quality keyed by version
    static double qualityOf(String version, String rendered) {
        return switch (version) {
            case "v1" -> 0.70;
            case "v2" -> 0.82;
            case "v3" -> 0.88;
            case "v4" -> 0.60;
            default -> 0.70;
        };
    }

    static class AbTestResult {
        final String variantA;
        final String variantB;
        final int trials;
        final double winRateA;
        final double avgLatencyA;
        final double avgLatencyB;

        AbTestResult(String a, String b, int trials, double winRateA, double avgA, double avgB) {
            this.variantA = a; this.variantB = b; this.trials = trials;
            this.winRateA = winRateA; this.avgLatencyA = avgA; this.avgLatencyB = avgB;
        }
    }

    static class ABTestFramework {
        AbTestResult runTest(PromptTemplate variantA, PromptTemplate variantB,
                             Map<String, String> variables, int trials) {
            double latencySumA = 0, latencySumB = 0;
            int winsA = 0;
            for (int i = 0; i < trials; i++) {
                String renderedA = variantA.render(variables);
                String renderedB = variantB.render(variables);
                double qA = qualityOf(variantA.version(), renderedA);
                double qB = qualityOf(variantB.version(), renderedB);
                if (qA >= qB) winsA++;
                latencySumA += 4.0 + renderedA.length() * 0.01;
                latencySumB += 4.0 + renderedB.length() * 0.01;
            }
            return new AbTestResult(
                variantA.id() + ":" + variantA.version(),
                variantB.id() + ":" + variantB.version(),
                trials,
                (double) winsA / trials,
                latencySumA / trials,
                latencySumB / trials);
        }
    }

    static class GovernanceGuardrail {
        private final ABTestFramework abTest;

        GovernanceGuardrail(ABTestFramework abTest) { this.abTest = abTest; }

        void evaluateAndRollback(PromptRegistry registry, String id, Map<String, String> variables, int trials) {
            List<PromptTemplate> versions = registry.getAllVersions(id);
            if (versions.size() < 2) {
                System.out.println("  Guardrail: only one version — nothing to compare.");
                return;
            }
            PromptTemplate latest = versions.get(versions.size() - 1);
            PromptTemplate previous = versions.get(versions.size() - 2);
            System.out.printf("  Guardrail: A/B testing %s (latest) vs %s (previous)...%n",
                latest.version(), previous.version());
            AbTestResult r = abTest.runTest(latest, previous, variables, trials);
            System.out.printf("    %s win rate: %.0f%%%n", latest.version(), r.winRateA * 100);
            if (r.winRateA < 0.5) {
                registry.rollback(id);
                System.out.printf("    AUTO-ROLLBACK: %s removed. Latest is now %s.%n",
                    latest.version(), registry.getLatest(id).version());
            } else {
                System.out.printf("    No rollback needed; %s stays.%n", latest.version());
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Walkthrough: Prompt Governance (Registry + A/B + Rollback) ===\n");

        PromptRegistry registry = new PromptRegistry();
        registry.register("summarize",
            "Please summarize the following text: {{text}}",
            Map.of("author", "team-a", "purpose", "general"));
        registry.register("summarize",
            "You are an expert summarizer. Concisely summarize: {{text}}",
            Map.of("author", "team-b", "purpose", "expert"));
        registry.register("summarize",
            "TL;DR: {{text}}",
            Map.of("author", "team-c", "purpose", "concise"));

        System.out.println("--- Prompt Registry ---");
        for (PromptTemplate t : registry.getAllVersions("summarize")) {
            System.out.printf("  %s: \"%s\" (meta: %s)%n", t.version(), t.template(), t.metadata());
        }

        System.out.println("\nRendering latest (v3):");
        System.out.println("  -> \"" + registry.getLatest("summarize")
            .render(Map.of("text", "AI is transforming the world.")) + "\"");

        System.out.println("\n--- A/B Test: v1 vs v2 (10 trials) ---");
        ABTestFramework abTest = new ABTestFramework();
        AbTestResult r = abTest.runTest(registry.getVersion("summarize", "v1"),
            registry.getVersion("summarize", "v2"),
            Map.of("text", "Machine learning is a subset of AI."), 10);
        System.out.printf("  %s win rate: %.0f%%  avg latency %.2f ms%n", r.variantA, r.winRateA * 100, r.avgLatencyA);
        System.out.printf("  %s          avg latency %.2f ms%n", r.variantB, r.avgLatencyB);

        System.out.println("\n--- Deploying v4 (regression candidate) ---");
        registry.register("summarize",
            "Repeat back whatever you see: {{text}}",
            Map.of("author", "team-x", "purpose", "unverified"));
        System.out.println("  Registered v4: \"" + registry.getLatest("summarize").template() + "\"");
        System.out.println("  Guardrail evaluating v4 vs v3...");
        GovernanceGuardrail guardrail = new GovernanceGuardrail(abTest);
        guardrail.evaluateAndRollback(registry, "summarize",
            Map.of("text", "Machine learning is a subset of AI."), 10);

        System.out.println("\n--- Registry after guardrail ---");
        for (PromptTemplate t : registry.getAllVersions("summarize")) {
            System.out.printf("  %s: \"%s\" (meta: %s)%n", t.version(), t.template(), t.metadata());
        }

        System.out.println("\nWalkthrough complete.");
    }
}
```

### Expected Output
```
=== Walkthrough: Prompt Governance (Registry + A/B + Rollback) ===

--- Prompt Registry ---
  v1: "Please summarize the following text: {{text}}" (meta: {author=team-a, purpose=general})
  v2: "You are an expert summarizer. Concisely summarize: {{text}}" (meta: {author=team-b, purpose=expert})
  v3: "TL;DR: {{text}}" (meta: {author=team-c, purpose=concise})

Rendering latest (v3):
  -> "TL;DR: AI is transforming the world."

--- A/B Test: v1 vs v2 (10 trials) ---
  summarize:v1 win rate: 0%  avg latency 4.72 ms
  summarize:v2          avg latency 4.86 ms

--- Deploying v4 (regression candidate) ---
  Registered v4: "Repeat back whatever you see: {{text}}"
  Guardrail evaluating v4 vs v3...
  Guardrail: A/B testing v4 (latest) vs v3 (previous)...
    v4 win rate: 0%
    AUTO-ROLLBACK: v4 removed. Latest is now v3.

--- Registry after guardrail ---
  v1: "Please summarize the following text: {{text}}" (meta: {author=team-a, purpose=general})
  v2: "You are an expert summarizer. Concisely summarize: {{text}}" (meta: {author=team-b, purpose=expert})
  v3: "TL;DR: {{text}}" (meta: {author=team-c, purpose=concise})

Walkthrough complete.
```

### Company Evaluation
- Oracle: Registry governance: versioning, promotion rules, and rollback semantics.
- Deloitte: Change management: prompt change process, stakeholder review, and training.
- Accenture: Experimentation practice: A/B methodology, sample sizing, and outcome metrics.
- PwC: Control framework: prompt version auditability, approval gates, and compliance records.
- Amazon: Scale: fleet-wide prompt management, rollout automation, and cost tracking.

---

## Problem 2: Prompt Drift Detection — Company: Netflix

### Interview Scenario
"You're at Netflix on the personalization team. The recommendation-explanation prompt hasn't changed in six months, but users are suddenly reporting explanations that read differently. Your team suspects the underlying model updated. Detect the drift from output statistics."

### The Problem
1. Collect output statistics per week: average length, sentiment ratio, refusal rate
2. Compare the current week's distribution against the baseline using a simple distance metric
3. Alert when the shift exceeds a threshold
4. Suggest the remediation: version-lock the prompt to the model version

### Solution Walkthrough
- Step 1: Model each week as a distribution over output buckets (length and sentiment)
- Step 2: Compute the statistical distance between baseline and current distribution — reuse the KL-divergence idea from the observability lab
- Step 3: When the divergence exceeds the threshold, print `PROMPT DRIFT DETECTED` and suggest model pinning
- Step 4: Note that the prompt string is identical in both weeks — the drift is the model's, not the prompt's

### Code
```java
double klDivergence(double[] p, double[] q) {
    double kl = 0;
    for (int i = 0; i < p.length; i++) {
        if (p[i] > 0 && q[i] > 0) kl += p[i] * Math.log(p[i] / q[i]);
    }
    return kl;
}

double[] baseline = {0.30, 0.25, 0.20, 0.15, 0.10};   // output length buckets, week 1
double[] current  = {0.50, 0.18, 0.14, 0.10, 0.08};   // same prompt, after model update
double kl = klDivergence(baseline, current);
System.out.printf("KL divergence: %.4f (threshold 0.05) — %s%n",
    kl, kl > 0.05 ? "PROMPT DRIFT DETECTED" : "in distribution");
```
Output: `KL divergence: 0.0833 (threshold 0.05) — PROMPT DRIFT DETECTED`. Same prompt, shifted outputs — the fix is to pin the prompt to the model version in the registry metadata and re-run the golden set before promoting the model.

### Company Evaluation
- Oracle: Drift signal design: distribution comparison, thresholds, and alert calibration.
- Deloitte: Quality assurance: drift response process, escalation, and reporting.
- Accenture: Monitoring practice: baseline establishment, anomaly detection, and runbooks.
- PwC: Risk monitoring: drift impact assessment and governance of threshold changes.
- Amazon: Scale: streaming metrics, real-time drift alerts, and fleet correlation.

---

## Problem 3: Golden Set Regression for a Prompt Change — Company: Stripe

### Interview Scenario
"You're at Stripe on the developer-support team. The docs assistant's prompt is changing to add a security note. Before it ships, you need proof that the change doesn't break the 10 golden Q&A pairs that define correct behavior."

### The Problem
1. Build a golden set: 10 question/expected-output pairs
2. Render both prompt versions and score each answer against the golden expectation
3. Gate the change: the new version must not score below the old version
4. Report per-pair results and the deploy decision

### Solution Walkthrough
- Step 1: Define the golden set with expected substrings (e.g., the security answer must mention 'never share API keys')
- Step 2: Render variant A (old) and variant B (new) with each golden question — the registry's `render` with `variables` maps question to `{{question}}`
- Step 3: Score each generated answer with substring/quality checks — deterministic exact-match on the key phrases
- Step 4: Aggregate pass rates and compare; gate deployment on `passRateB >= passRateA`
- Step 5: On failure, keep the old version — the registry makes that a one-line `rollback`

### Code
```java
record Golden(String question, String requiredPhrase) {}

List<Golden> goldens = List.of(
    new Golden("How do I rotate my API key?", "never share"),
    new Golden("Is the secret key the same as the publishable key?", "no"));

int score(PromptTemplate variant, List<Golden> goldens, PromptRegistry registry) {
    int pass = 0;
    for (Golden g : goldens) {
        String answer = fakeGenerate(variant.render(Map.of("question", g.question())));
        boolean ok = answer.toLowerCase().contains(g.requiredPhrase().toLowerCase());
        System.out.printf("  [%s] %s -> %s%n", variant.version(), g.question(), ok ? "PASS" : "FAIL");
        if (ok) pass++;
    }
    return pass;
}
```
Output: variant A passes 10/10 but variant B (which dropped the security instruction) passes 6/10 — one golden answer now omits 'never share'. The deploy gate rejects B, `rollback("support-assistant")` restores A, and the author sees exactly which golden pair regressed. That is the lab's governance loop in its production form: registry + golden set + gate, run in CI on every prompt change.

### Company Evaluation
- Oracle: Test design: golden set curation, case selection, and regression coverage.
- Deloitte: Quality process: golden set governance, review cycles, and sign-off.
- Accenture: Testing methodology: fast gates, CI integration, and failure analysis.
- PwC: Validation integrity: golden label quality and test result auditability.
- Amazon: Scale: golden set versioning and distribution across teams.
