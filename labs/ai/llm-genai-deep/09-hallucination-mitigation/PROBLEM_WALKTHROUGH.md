# PROBLEM WALKTHROUGH: Hallucination Detection System

## Problem Statement

**Difficulty: Hard | Category: Hallucination / Factuality**

Implement a factuality/hallucination detection system that determines whether a generated text is factually consistent with a given source document. The system should extract claims from generated text, verify each claim against the source, and provide a confidence score.

**Interview Context:** Hallucination is the #1 problem in production LLM deployments (2023-2026). Interviewers want to see understanding of factual consistency, the difference between intrinsic vs extrinsic hallucination, and the trade-offs between different detection approaches (NLI-based, QA-based, consistency-based).

### Requirements

1. **Claim Extraction:** Decompose generated text into atomic factual claims.
2. **Claim Verification:** Check each claim against the source document for factual support.
3. **Confidence Scoring:** Aggregate claim-level verdicts into an overall hallucination score.
4. **Self-Consistency Check:** Optionally verify by generating multiple responses and checking agreement.
5. **Explainability:** Provide evidence for why a claim is flagged as hallucinated.

### Input/Output Contract

```
Input:  Source document (context), generated text (response),
        verification_method = "entailment" | "consistency"
Output: Hallucination verdict {overall_score, is_hallucinated},
        per-claim breakdown {claim, supported, evidence, confidence}
```

---

## Step-by-Step Solution Walkthrough

### 1. Types of Hallucination

**Intrinsic hallucination:** The generated information contradicts the source document.
- Source: "Paris is the capital of France."
- Response: "Paris is the capital of Italy."
- → Hallucination (direct contradiction)

**Extrinsic hallucination:** The generated information is not present in the source at all.
- Source: "Paris is the capital of France."
- Response: "Paris has a population of 2 million."
- → Extrinsic (neither supported nor contradicted). May or may not be acceptable depending on the use case.

Our system detects both types.

### 2. Claim Extraction Strategy

Decompose generated text into atomic claims — self-contained factual statements:

**Heuristic method (sentence splitting + conjunction splitting):**
1. Split on sentence boundaries (period, exclamation, question mark).
2. Further split on coordinating conjunctions ("and", "but", "or").
3. Filter out non-factual clauses (opinions, hedges, questions).

**Example:**
```
"The Eiffel Tower is in Paris and was built in 1889 for the World's Fair."
→ Claims:
  1. "The Eiffel Tower is in Paris."
  2. "The Eiffel Tower was built in 1889."
  3. "The Eiffel Tower was built for the World's Fair."
```

### 3. Entailment-Based Verification

Using an NLI (Natural Language Inference) model:

```
For each claim:
  Input: Premise = source_document, Hypothesis = claim
  Output: {entailment: P(entailment), neutral: P(neutral), contradiction: P(contradiction)}

  If P(contradiction) > threshold (e.g., 0.5):
    → Claim is hallucinated
  Elif P(entailment) > threshold (e.g., 0.5):
    → Claim is supported
  Else:
    → Claim is unknown (extrinsic)
```

**Without an NLI model, use n-gram overlap as a proxy:**

```
overlap_ratio = |n-grams(claim) ∩ n-grams(source)| / |n-grams(claim)|
If overlap_ratio < threshold (e.g., 0.3):
  → Claim is potentially hallucinated
```

### 4. Self-Consistency Verification

Generate multiple responses to the same query and check for agreement:

```
Generate N responses (N=3-5):
  If key claims are present in most responses:
    → Low hallucination risk (consistent)
  If a claim appears in only 1/N responses:
    → High hallucination risk (invented on the fly)
```

This is based on the observation that LLMs tend to hallucinate inconsistently — a hallucinated fact in one generation may not appear in another.

### 5. Aggregation and Scoring

Overall hallucination score (0 = factual, 1 = fully hallucinated):

```
total_claims = claims.size()
hallucinated_claims = count(verdict == CONTRADICTION)
unknown_claims = count(verdict == UNKNOWN)

score = (hallucinated_claims + 0.5 * unknown_claims) / total_claims

is_hallucinated = score > threshold (e.g., 0.3)
```

### 6. Explainability

For each flagged claim, provide:
1. The specific claim text.
2. The verdict (supported / contradicted / unknown).
3. Evidence from the source (the most relevant sentence or n-grams).
4. Confidence in the verdict.

---

## Java Implementation

```java
package com.llm.genai.deep.hallucination;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implements a hallucination detection system that verifies generated text
 * against source documents using claim extraction and entailment/consistency
 * checking.
 * <p>
 * Supports both entailment-based verification (requires an NLI model) and
 * self-consistency verification (requires multiple generations).
 */
public class HallucinationDetector {

    private static final int NGRAM_SIZE = 3;
    private static final double OVERLAP_THRESHOLD = 0.3;
    private static final double CONTRADICTION_THRESHOLD = 0.5;
    private static final double ENTAILMENT_THRESHOLD = 0.5;
    private static final double HALLUCINATION_SCORE_THRESHOLD = 0.3;

    private final NLIModel nliModel;

    /**
     * Enumeration for claim verification verdict.
     */
    public enum Verdict {
        SUPPORTED,
        CONTRADICTION,
        UNKNOWN
    }

    /**
     * Holds the result of a single claim verification.
     */
    public static class ClaimResult {
        public final String claim;
        public final Verdict verdict;
        public final double confidence;
        public final String evidence;

        ClaimResult(String claim, Verdict verdict, double confidence, String evidence) {
            this.claim = claim;
            this.verdict = verdict;
            this.confidence = confidence;
            this.evidence = evidence;
        }
    }

    /**
     * Holds the overall hallucination detection result.
     */
    public static class DetectionResult {
        public final double hallucinationScore;
        public final boolean isHallucinated;
        public final List<ClaimResult> claimResults;
        public final int totalClaims;
        public final int supportedCount;
        public final int contradictedCount;
        public final int unknownCount;

        DetectionResult(double score, List<ClaimResult> claims) {
            this.hallucinationScore = score;
            this.isHallucinated = score > HALLUCINATION_SCORE_THRESHOLD;
            this.claimResults = Collections.unmodifiableList(claims);
            this.totalClaims = claims.size();
            this.supportedCount = (int) claims.stream()
                    .filter(c -> c.verdict == Verdict.SUPPORTED).count();
            this.contradictedCount = (int) claims.stream()
                    .filter(c -> c.verdict == Verdict.CONTRADICTION).count();
            this.unknownCount = (int) claims.stream()
                    .filter(c -> c.verdict == Verdict.UNKNOWN).count();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Hallucination Score: %.4f (threshold=%.2f)%n",
                    hallucinationScore, HALLUCINATION_SCORE_THRESHOLD));
            sb.append(String.format("Verdict: %s%n",
                    isHallucinated ? "HALLUCINATED" : "FACTUAL"));
            sb.append(String.format("Claims: %d total, %d supported, %d contradicted, %d unknown%n",
                    totalClaims, supportedCount, contradictedCount, unknownCount));
            sb.append("\nPer-Claim Breakdown:\n");
            for (int i = 0; i < claimResults.size(); i++) {
                ClaimResult cr = claimResults.get(i);
                sb.append(String.format("  [%d] Claim: \"%s\"%n", i + 1, cr.claim));
                sb.append(String.format("       Verdict: %s (conf=%.4f)%n",
                        cr.verdict, cr.confidence));
                if (!cr.evidence.isEmpty()) {
                    sb.append(String.format("       Evidence: \"%s\"%n", cr.evidence));
                }
            }
            return sb.toString();
        }
    }

    /**
     * Interface for an NLI (Natural Language Inference) model.
     */
    @FunctionalInterface
    public interface NLIModel {
        /**
         * Returns probabilities for [entailment, neutral, contradiction].
         *
         * @param premise   the source document
         * @param hypothesis the claim to verify
         * @return array of 3 probabilities summing to 1.0
         */
        double[] predict(String premise, String hypothesis);
    }

    /**
     * Constructs a hallucination detector with an NLI model.
     *
     * @param nliModel the NLI model for entailment-based verification
     */
    public HallucinationDetector(NLIModel nliModel) {
        this.nliModel = nliModel;
    }

    /**
     * Detects hallucination in generated text against a source document.
     *
     * @param source     the source document (ground truth context)
     * @param generated  the generated text to verify
     * @return detection result with per-claim breakdown
     */
    public DetectionResult detect(String source, String generated) {
        List<String> claims = extractClaims(generated);
        List<ClaimResult> results = new ArrayList<>();

        for (String claim : claims) {
            ClaimResult result = verifyClaim(source, claim);
            results.add(result);
        }

        double score = computeHallucinationScore(results);
        return new DetectionResult(score, results);
    }

    /**
     * Extracts atomic claims from generated text.
     * Heuristic approach: sentence splitting + conjunction splitting.
     *
     * @param text generated text
     * @return list of atomic claim strings
     */
    public List<String> extractClaims(String text) {
        List<String> claims = new ArrayList<>();

        // Split into sentences
        String[] sentences = text.split("(?<=[.!?])\\s+");

        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (sentence.isEmpty()) continue;

            // Remove non-factual sentence types
            if (isNonFactual(sentence)) continue;

            // Split on conjunctions for compound sentences
            List<String> parts = splitOnConjunctions(sentence);

            for (String part : parts) {
                part = part.trim();
                // Remove leading/trailing punctuation
                part = part.replaceAll("^[,\\s]+|[,\\s]+$", "");
                if (part.length() > 5) { // ignore very short fragments
                    claims.add(part);
                }
            }
        }

        return claims;
    }

    /**
     * Checks if a sentence is non-factual (opinion, question, command, hedge).
     */
    private boolean isNonFactual(String sentence) {
        String lower = sentence.toLowerCase();
        // Questions
        if (sentence.trim().endsWith("?")) return true;
        // Hedges
        if (lower.matches("^(i think|i believe|maybe|perhaps|probably).*")) return true;
        // Meta comments
        if (lower.matches(".*(overall|in summary|in conclusion|note that).*")) {
            return sentence.length() < 30; // short meta comments are not factual
        }
        return false;
    }

    /**
     * Splits a sentence on coordinating conjunctions at the clause level.
     */
    private List<String> splitOnConjunctions(String sentence) {
        List<String> parts = new ArrayList<>();
        // Split on " and ", " but ", " or " that are not part of enumerations
        String[] toks = sentence.split("\\s+(and|but|or)\\s+");
        Collections.addAll(parts, toks);
        // Further split on commas for serial enumerations
        if (parts.size() == 1 && parts.get(0).contains(",")) {
            String[] commaParts = parts.get(0).split(",");
            parts.clear();
            for (String cp : commaParts) {
                cp = cp.trim();
                if (!cp.isEmpty()) parts.add(cp);
            }
        }
        return parts;
    }

    /**
     * Verifies a single claim against the source document.
     */
    private ClaimResult verifyClaim(String source, String claim) {
        if (nliModel != null) {
            return verifyWithNLI(source, claim);
        } else {
            return verifyWithOverlap(source, claim);
        }
    }

    /**
     * NLI-based verification.
     */
    private ClaimResult verifyWithNLI(String source, String claim) {
        double[] probs = nliModel.predict(source, claim);
        double entailProb = probs[0];
        double neutralProb = probs[1];
        double contradictProb = probs[2];

        if (contradictProb > CONTRADICTION_THRESHOLD) {
            String evidence = extractEvidence(source, claim);
            return new ClaimResult(claim, Verdict.CONTRADICTION,
                    contradictProb, evidence);
        } else if (entailProb > ENTAILMENT_THRESHOLD) {
            String evidence = extractEvidence(source, claim);
            return new ClaimResult(claim, Verdict.SUPPORTED,
                    entailProb, evidence);
        } else {
            return new ClaimResult(claim, Verdict.UNKNOWN,
                    Math.max(neutralProb, 1.0 - entailProb - contradictProb), "");
        }
    }

    /**
     * Lexical overlap-based verification (fallback when no NLI model).
     */
    private ClaimResult verifyWithOverlap(String source, String claim) {
        Set<String> sourceNGrams = getNGrams(source.toLowerCase());
        Set<String> claimNGrams = getNGrams(claim.toLowerCase());

        if (sourceNGrams.isEmpty() || claimNGrams.isEmpty()) {
            return new ClaimResult(claim, Verdict.UNKNOWN, 0.0, "");
        }

        long overlap = claimNGrams.stream()
                .filter(sourceNGrams::contains)
                .count();
        double overlapRatio = (double) overlap / claimNGrams.size();

        if (overlapRatio < OVERLAP_THRESHOLD) {
            String evidence = extractEvidence(source, claim);
            return new ClaimResult(claim, Verdict.CONTRADICTION,
                    1.0 - overlapRatio, evidence);
        } else if (overlapRatio > 0.7) {
            String evidence = extractEvidence(source, claim);
            return new ClaimResult(claim, Verdict.SUPPORTED,
                    overlapRatio, evidence);
        } else {
            return new ClaimResult(claim, Verdict.UNKNOWN,
                    1.0 - overlapRatio, "");
        }
    }

    /**
     * Extracts the most relevant evidence from the source for a claim.
     * Returns the sentence in the source with the highest n-gram overlap.
     */
    private String extractEvidence(String source, String claim) {
        String[] sentences = source.split("(?<=[.!?])\\s+");
        String bestSentence = "";
        double bestOverlap = 0;

        Set<String> claimNGrams = getNGrams(claim.toLowerCase());

        for (String sentence : sentences) {
            Set<String> sentNGrams = getNGrams(sentence.toLowerCase());
            long overlap = claimNGrams.stream()
                    .filter(sentNGrams::contains)
                    .count();
            double ratio = claimNGrams.isEmpty() ? 0 :
                    (double) overlap / claimNGrams.size();
            if (ratio > bestOverlap) {
                bestOverlap = ratio;
                bestSentence = sentence;
            }
        }

        return bestOverlap > 0.1 ? bestSentence : "";
    }

    /**
     * Generates character-level n-grams from text.
     */
    private Set<String> getNGrams(String text) {
        String clean = text.replaceAll("\\s+", " ").toLowerCase();
        Set<String> ngrams = new HashSet<>();
        for (int i = 0; i <= clean.length() - NGRAM_SIZE; i++) {
            ngrams.add(clean.substring(i, i + NGRAM_SIZE));
        }
        return ngrams;
    }

    /**
     * Computes overall hallucination score from claim results.
     * Hallucinated claims get weight 1.0, unknown claims get weight 0.5.
     */
    private double computeHallucinationScore(List<ClaimResult> results) {
        if (results.isEmpty()) return 0.0;

        double weightedSum = 0;
        for (ClaimResult cr : results) {
            switch (cr.verdict) {
                case CONTRADICTION:
                    weightedSum += 1.0;
                    break;
                case UNKNOWN:
                    weightedSum += 0.5;
                    break;
                case SUPPORTED:
                    weightedSum += 0.0;
                    break;
            }
        }
        return weightedSum / results.size();
    }

    /**
     * Performs self-consistency check by comparing multiple responses.
     *
     * @param source     source document
     * @param responses  multiple generated responses for the same query
     * @return consistency score (0 = no agreement, 1 = perfect agreement)
     */
    public double selfConsistencyCheck(String source, List<String> responses) {
        if (responses == null || responses.size() < 2) return 1.0;

        // Extract claims from each response
        List<List<String>> allClaims = new ArrayList<>();
        for (String response : responses) {
            allClaims.add(extractClaims(response));
        }

        // For each claim in the first response, check how many other responses contain it
        List<String> baseClaims = allClaims.get(0);
        if (baseClaims.isEmpty()) return 1.0;

        double totalScore = 0;
        for (String claim : baseClaims) {
            int count = 0;
            for (int i = 1; i < allClaims.size(); i++) {
                boolean found = allClaims.get(i).stream()
                        .anyMatch(c -> jaccardSimilarity(c, claim) > 0.6);
                if (found) count++;
            }
            totalScore += (double) count / (responses.size() - 1);
        }

        return totalScore / baseClaims.size();
    }

    /**
     * Computes Jaccard similarity between two strings based on word sets.
     */
    private double jaccardSimilarity(String a, String b) {
        Set<String> wordsA = new HashSet<>(
                Arrays.asList(a.toLowerCase().split("\\W+")));
        Set<String> wordsB = new HashSet<>(
                Arrays.asList(b.toLowerCase().split("\\W+")));
        wordsA.remove("");
        wordsB.remove("");

        if (wordsA.isEmpty() && wordsB.isEmpty()) return 1.0;

        Set<String> intersection = new HashSet<>(wordsA);
        intersection.retainAll(wordsB);

        Set<String> union = new HashSet<>(wordsA);
        union.addAll(wordsB);

        return (double) intersection.size() / union.size();
    }

    /**
     * Main method demonstrating hallucination detection.
     */
    public static void main(String[] args) {
        // NLI model stub (in production, use a real NLI model)
        NLIModel stubNLI = (premise, hypothesis) -> {
            double[] probs = new double[3];
            double overlap = 0;
            String pH = premise.toLowerCase();
            String hH = hypothesis.toLowerCase();
            String[] hWords = hH.split("\\W+");
            for (String w : hWords) {
                if (pH.contains(w)) overlap++;
            }
            double ratio = overlap / Math.max(hWords.length, 1);
            probs[0] = ratio * 0.9;        // entailment
            probs[1] = (1 - ratio) * 0.3;  // neutral
            probs[2] = (1 - ratio) * 0.7;  // contradiction
            // Normalize
            double sum = probs[0] + probs[1] + probs[2];
            for (int i = 0; i < 3; i++) probs[i] /= sum;
            return probs;
        };

        HallucinationDetector detector = new HallucinationDetector(stubNLI);

        String source = "Paris is the capital of France. "
                + "The Eiffel Tower is a wrought-iron lattice tower located in Paris, France. "
                + "It was constructed from 1887 to 1889 and opened on March 31, 1889.";

        String factualResponse = "The capital of France is Paris. "
                + "The Eiffel Tower is located in Paris and was completed in 1889.";

        String hallucinatedResponse = "The capital of France is Lyon. "
                + "The Eiffel Tower is located in London and was built in 1900 for the Olympics.";

        System.out.println("=== Testing Factual Response ===");
        DetectionResult result1 = detector.detect(source, factualResponse);
        System.out.println(result1);

        System.out.println("\n=== Testing Hallucinated Response ===");
        DetectionResult result2 = detector.detect(source, hallucinatedResponse);
        System.out.println(result2);

        // Self-consistency check
        System.out.println("\n=== Self-Consistency Check ===");
        List<String> responses = List.of(
            "Paris is the capital of France.",
            "The capital city is Paris, France.",
            "France's capital is Lyon." // hallucinated
        );
        double consistency = detector.selfConsistencyCheck(source, responses);
        System.out.printf("Consistency score: %.4f (higher = more consistent)%n", consistency);
    }
}
```

---

## Complexity Analysis

### Time Complexity

- **Claim extraction:** O(N) where N = length of generated text. Sentence splitting and conjunction splitting are linear scans.
- **Per-claim NLI inference:** O(1) model call per claim. The NLI model itself is O(L²) in sequence length for transformers, but this is external.
- **Lexical overlap fallback:** O(M × C × N) where M = source length, C = number of claims, N = n-gram size.
- **Self-consistency:** O(R × C × L) for R responses, C claims each, L length.
- **Total (NLI):** O(C × T_model) — dominated by NLI model calls.
- **Total (overlap):** O(M × C × N) — fast for typical text lengths.

### Space Complexity

- **Source document:** O(M) characters.
- **Claims:** O(C × L) where C = number of claims (typically 3-20), L = average claim length.
- **Results storage:** O(C) per detection.
- **NLI model:** Variable (hundreds of MB for transformer models).

### Bottlenecks

1. **NLI model latency:** Each claim requires a forward pass through the NLI model. For 10 claims, this is 10x more expensive than a single hallucination check.
2. **Batch processing:** Claims from one response can be batched into a single NLI forward pass for efficiency.
3. **Long documents:** Very long source documents may exceed the NLI model's input length, requiring chunking and aggregation.

---

## Follow-Up Questions

### Q1: How do you distinguish between "acceptable augmentation" and "harmful hallucination"?

**Answer:** Not all extrinsic information is harmful. The distinction depends on the use case:
- **Acceptable augmentation:** The response adds common knowledge not in the source but known to be true. "Source: Paris is the capital." Response: "Paris has about 2 million people." Acceptable because population is a well-known fact.
- **Harmful hallucination:** The response invents false information that contradicts established knowledge or creates risks. "Source: Paris is the capital." Response: "Paris is in Germany." Clearly harmful.

**Detection strategies:**
1. **Verifiability check:** Can the claim be verified from a trusted knowledge base (Wikipedia, Wikidata)?
2. **Confidence threshold:** Only flag claims with very low source overlap as hallucinations.
3. **Domain-specific rules:** In medical/legal domains, ANY claim not in the source is treated as hallucinated.

### Q2: What is the state of the art in hallucination detection?

**Answer:** Current top approaches (2025-2026):
1. **FacTool (2023):** Uses tool calls (search, calculator, code execution) to verify claims against external knowledge.
2. **SelfCheckGPT (2023):** Consistency-based detection using multiple generations. Simple and effective.
3. **FactScore (2023):** Breaks down generation into atomic facts, verifies each against a knowledge base.
4. **AlignScore (2023):** Trains a unified model for factual consistency across diverse tasks.
5. **LLM-as-Judge:** Use a strong LLM (GPT-4, Claude 3.5) to evaluate factual consistency. Most accurate but most expensive.

**Best practice:** Combine multiple approaches. Use LLM-as-Judge for high-stakes claims, overlap-based detection for real-time filtering, and self-consistency as a cross-check.

### Q3: How do you handle "partially correct" claims?

**Answer:** Claims can be partially correct (some details right, some wrong):
- **Claim:** "The Eiffel Tower was built in 1889 for the World's Fair in London."
- **Correct parts:** Built in 1889, for the World's Fair.
- **Wrong part:** Location (Paris, not London).

**Decomposition strategy:** Atomic claim decomposition should separate each fact. The example above should be split into:
1. "The Eiffel Tower was built in 1889."
2. "The Eiffel Tower was built for the World's Fair."
3. "The World's Fair was in London." → contradiction with source.

**Scoring:** Contradicting any atomic claim makes the response partially hallucinated. The overall score reflects the fraction of contradicted atomic claims.

### Q4: How do calibration and thresholds affect detection performance?

**Answer:** The threshold determines precision-recall tradeoff:
- **Low threshold (0.1):** High recall (catches most hallucinations) but many false positives (flags good responses).
- **High threshold (0.7):** High precision (few false alarms) but low recall (misses subtle hallucinations).

**Optimal threshold depends on cost of errors:**
- Medical diagnosis: Low threshold (false positive is better than false negative).
- Creative writing: High threshold (hallucination is expected, don't over-flag).

**Calibration:** The NLI model's output probabilities may not be well-calibrated. Apply Platt scaling or temperature scaling to improve calibration before thresholding.

### Q5: How does hallucination detection integrate into a production RAG system?

**Answer:** Production integration follows a "guardrail" pattern:

```
User Query → Retriever → LLM Generator → Hallucination Detector → Output
                                              ↓
                                         If hallucinated:
                                         → Regenerate with stricter prompt
                                         → Add retrieved evidence
                                         → Or fallback to "I don't know"
```

**Tiered approach:**
1. **Real-time: Lexical overlap** (fast, catches obvious hallucinations).
2. **Near-real-time: Self-consistency** (medium, requires 2-3 generations).
3. **Batch: NLI-based or LLM-as-Judge** (slow but accurate, run every N requests).

Production systems typically use tier 1 for all traffic and tiers 2-3 for a random sample to monitor quality.

---

## Test Cases

### Test Case 1: Fully Factual Response

```
Source: "Paris is the capital of France. The Eiffel Tower is in Paris."
Response: "The capital of France is Paris. The Eiffel Tower is in Paris."
Expected: All claims SUPPORTED, hallucinationScore ≈ 0.0, isHallucinated = false.
```

### Test Case 2: Fully Hallucinated Response

```
Source: "Paris is the capital of France."
Response: "The capital of France is Berlin. Berlin is in Germany."
Expected: Both claims CONTRADICTION. hallucinationScore ≈ 1.0, isHallucinated = true.
```

### Test Case 3: Partially Hallucinated Response

```
Source: "The Eiffel Tower was built in 1889 in Paris."
Response: "The Eiffel Tower was built in 1889 in Rome."
Claims:
  [0] "The Eiffel Tower was built in 1889." → SUPPORTED
  [1] "The Eiffel Tower was built in Rome." → CONTRADICTION
Expected: hallucinationScore = 0.5. isHallucinated = true (0.5 > 0.3).
```

### Test Case 4: Unknown/Extrinsic Claims

```
Source: "Paris is the capital of France."
Response: "Paris has a population of 2 million."
Expected: Claim UNKNOWN (no evidence, no contradiction). 
hallucinationScore = 0.5 × 1/1 = 0.5. isHallucinated = true.
```

### Test Case 5: Self-Consistency Agreement

```
Response 1: "Paris is the capital of France."
Response 2: "The capital of France is Paris."
Response 3: "France's capital city is Paris."
Expected: selfConsistencyScore ≈ 1.0 (all agree).
```

### Test Case 6: Self-Consistency Disagreement

```
Response 1: "Paris is the capital of France."
Response 2: "The capital of France is Paris."
Response 3: "The capital of France is Lyon."
Expected: selfConsistencyScore ≈ 0.67 (only 2/3 agree on the capital fact).
```

### Test Case 7: Non-Factual Sentence Filtering

```
Generated: "I think the capital is Paris. What is the capital? Overall, it's Paris."
Expected: Only "it's Paris" might be extracted, or nothing if filtering is aggressive.
Non-factual markers (I think, question, Overall) should be removed.
```

---

## Summary

This walkthrough implemented a comprehensive hallucination detection system with:
1. **Atomic claim extraction** from generated text via sentence and conjunction splitting.
2. **NLI-based verification** with per-claim entailment, neutral, and contradiction scoring.
3. **Lexical overlap fallback** for environments without an NLI model.
4. **Self-consistency checking** across multiple generations for agreement-based detection.
5. **Explainable output** with evidence snippets for each flagged claim.

The key insight is that hallucination detection is fundamentally a claim verification problem: decompose the output into checkable units, verify each against the source, and aggregate. The approach allows for graceful degradation from sophisticated NLI models to simple overlap metrics.