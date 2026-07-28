package com.genai.lab09;

import java.util.*;
import java.util.stream.*;

/**
 * LLM Evaluation & Benchmarks
 * 
 * Demonstrates BLEU/ROUGE scoring, toxicity/bias detection,
 * and factual consistency checking in Java.
 */
public class Main {

    /** BLEU score (simplified: unigram precision + brevity penalty). */
    static double bleu(String reference, String candidate) {
        String[] refTokens = reference.toLowerCase().split("\\s+");
        String[] candTokens = candidate.toLowerCase().split("\\s+");
        Map<String, Integer> refCounts = new HashMap<>();
        for (String t : refTokens) refCounts.merge(t, 1, Integer::sum);

        int matches = 0;
        Map<String, Integer> candCounts = new HashMap<>();
        for (String t : candTokens) {
            Integer rc = refCounts.get(t);
            if (rc != null) {
                int cc = candCounts.getOrDefault(t, 0);
                if (cc < rc) { matches++; }
                candCounts.merge(t, 1, Integer::sum);
            }
        }
        double precision = (double) matches / candTokens.length;
        double bp = (candTokens.length < refTokens.length)
            ? Math.exp(1 - (double) refTokens.length / candTokens.length) : 1.0;
        return bp * precision;
    }

    /** ROUGE-1 (recall-oriented). */
    static double rouge1(String reference, String candidate) {
        String[] refTokens = reference.toLowerCase().split("\\s+");
        String[] candTokens = candidate.toLowerCase().split("\\s+");
        Set<String> refSet = new HashSet<>(Arrays.asList(refTokens));
        Set<String> candSet = new HashSet<>(Arrays.asList(candTokens));
        long overlap = refSet.stream().filter(candSet::contains).count();
        return (double) overlap / refSet.size();
    }

    /** Toxicity classifier using keyword + pattern matching. */
    static class ToxicityClassifier {
        final Set<String> toxicWords = Set.of("hate", "kill", "stupid", "idiot", "ugly");

        double toxicityScore(String text) {
            String lower = text.toLowerCase();
            long count = toxicWords.stream().filter(lower::contains).count();
            return (double) count / (text.split("\\s+").length + 1) * 10;
        }

        boolean isToxic(String text, double threshold) {
            return toxicityScore(text) > threshold;
        }
    }

    /** Bias evaluator: measures demographic term association. */
    static class BiasEvaluator {
        final Set<String> maleTerms = Set.of("he", "him", "his", "man", "men", "boy");
        final Set<String> femaleTerms = Set.of("she", "her", "hers", "woman", "women", "girl");

        double computeBiasRatio(String text) {
            String lower = text.toLowerCase();
            long maleCount = maleTerms.stream().filter(t -> lower.contains(t)).count();
            long femaleCount = femaleTerms.stream().filter(t -> lower.contains(t)).count();
            if (femaleCount == 0) return maleCount > 0 ? Double.POSITIVE_INFINITY : 1.0;
            return (double) maleCount / femaleCount;
        }
    }

    /** Factual consistency: simple claim overlap check. */
    static class FactualConsistency {
        static double consistencyScore(String generated, String source) {
            Set<String> genTokens = new HashSet<>(Arrays.asList(
                generated.toLowerCase().split("\\s+")));
            Set<String> srcTokens = new HashSet<>(Arrays.asList(
                source.toLowerCase().split("\\s+")));
            long overlap = genTokens.stream().filter(srcTokens::contains).count();
            return (double) overlap / genTokens.size();
        }
    }

    public static void main(String[] args) {
        String ref = "The cat sat on the mat";
        String cand = "The cat sat on mat";

        System.out.println("=== BLEU ===");
        System.out.printf("BLEU: %.4f%n", bleu(ref, cand));
        System.out.println("=== ROUGE-1 ===");
        System.out.printf("ROUGE-1: %.4f%n", rouge1(ref, cand));

        ToxicityClassifier tc = new ToxicityClassifier();
        System.out.println("\n=== Toxicity ===");
        System.out.println("Toxicity('You are stupid'): " + tc.toxicityScore("You are stupid"));
        System.out.println("Toxicity('Hello world'): " + tc.toxicityScore("Hello world"));

        BiasEvaluator be = new BiasEvaluator();
        System.out.println("\n=== Bias ===");
        System.out.println("Bias ratio: " + be.computeBiasRatio("The man and the woman worked hard. He and she both succeeded."));

        System.out.println("\n=== Factual Consistency ===");
        double cs = FactualConsistency.consistencyScore(
            "The cat sat on the mat.", "Experts agree the cat sat on the mat yesterday.");
        System.out.printf("Consistency: %.4f%n", cs);

        System.out.println("\nEvaluation framework validated.");
    }
}
