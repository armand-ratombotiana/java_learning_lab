package com.aiengineering.lab05;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Demonstrates prompt engineering at scale: template management,
 * versioning, A/B testing of prompt variants, and performance tracking.
 * <p>
 * Includes a PromptRegistry for versioned templates, an A/B test
 * framework that compares prompt variants, and a metrics collector.
 */
public class PromptEngineeringAtScaleDemo {

    // ---------- Prompt Template ----------

    public record PromptTemplate(String id, String version, String template, Map<String, String> metadata) {
        String render(Map<String, String> variables) {
            String result = template;
            for (var entry : variables.entrySet()) {
                result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
            return result;
        }
    }

    // ---------- Prompt Registry (Versioned) ----------

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

    // ---------- LLM Simulator ----------

    @FunctionalInterface
    interface LlmClient {
        String generate(String prompt);
    }

    static class MockLlmClient implements LlmClient {
        private final Random rng = new Random(42);
        public String generate(String prompt) {
            sleep(5);
            String[] outputs = {
                "The answer is " + prompt.length() + ".",
                "Based on the prompt, the result is clear.",
                "I think the response should be concise."
            };
            return outputs[rng.nextInt(outputs.length)];
        }
    }

    // ---------- A/B Test Framework ----------

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
        private final LlmClient llm;

        ABTestFramework(LlmClient llm) { this.llm = llm; }

        AbTestResult runTest(PromptTemplate variantA, PromptTemplate variantB,
                             Map<String, String> variables, int trials) {
            long latencySumA = 0, latencySumB = 0;
            int winsA = 0;

            for (int i = 0; i < trials; i++) {
                // Variant A
                long start = System.nanoTime();
                String resultA = llm.generate(variantA.render(variables));
                long timeA = System.nanoTime() - start;

                // Variant B
                start = System.nanoTime();
                String resultB = llm.generate(variantB.render(variables));
                long timeB = System.nanoTime() - start;

                latencySumA += timeA;
                latencySumB += timeB;
                if (resultA.length() <= resultB.length()) winsA++; // shorter = better
            }

            return new AbTestResult(
                variantA.id() + ":" + variantA.version(),
                variantB.id() + ":" + variantB.version(),
                trials,
                (double) winsA / trials,
                (double) latencySumA / trials / 1_000_000,
                (double) latencySumB / trials / 1_000_000
            );
        }
    }

    // ---------- Main Demo ----------

    public static void main(String[] args) {
        System.out.println("=== AI Engineering Academy — Lab 05: Prompt Engineering at Scale ===\n");

        // Setup prompt registry
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
        System.out.println("All versions of 'summarize':");
        for (PromptTemplate t : registry.getAllVersions("summarize")) {
            System.out.printf("  %s: \"%s\" (meta: %s)%n",
                t.version(), t.template(), t.metadata());
        }

        System.out.println("\nRendering latest template:");
        PromptTemplate latest = registry.getLatest("summarize");
        String rendered = latest.render(Map.of("text", "AI is transforming the world."));
        System.out.println("  Input: AI is transforming the world.");
        System.out.println("  Rendered: \"" + rendered + "\"");

        System.out.println("\nRollback to previous version...");
        registry.rollback("summarize");
        System.out.println("  After rollback, latest: \"" + registry.getLatest("summarize").template() + "\"");

        // A/B Testing
        System.out.println("\n--- A/B Testing ---");
        MockLlmClient llm = new MockLlmClient();
        ABTestFramework abTest = new ABTestFramework(llm);

        PromptTemplate variantA = registry.getVersion("summarize", "v1");
        PromptTemplate variantB = registry.getVersion("summarize", "v2");
        AbTestResult result = abTest.runTest(variantA, variantB,
            Map.of("text", "Machine learning is a subset of AI."), 10);

        System.out.printf("A/B Test Results (%d trials):%n", result.trials());
        System.out.printf("  Variant A (%s) win rate: %.1f%%%n", result.variantA(), result.winRateA() * 100);
        System.out.printf("  Variant A avg latency: %.2f ms%n", result.avgLatencyA());
        System.out.printf("  Variant B (%s) avg latency: %.2f ms%n", result.variantB(), result.avgLatencyB());

        System.out.println("\nDemo complete. Registry contains " +
            registry.getAllVersions("summarize").size() + " active versions.");
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
