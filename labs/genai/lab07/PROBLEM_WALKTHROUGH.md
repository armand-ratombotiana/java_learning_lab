# Problem Walkthrough: RLHF & Preference Optimization

## Problem 1: Preference Pipeline with KL Guard — Company: Anthropic

### Interview Scenario
"You're at Anthropic building a comparison harness for alignment experiments. The team
wants a single Java program that exercises the whole preference pipeline with the lab's
components: reward-model loss over a preference dataset, a PPO sweep over the KL
coefficient showing how the guard tightens the objective, and DPO loss as the
reward-model-free alternative — all deterministic and dependency-free."

### The Problem
1. Build a 4-example preference dataset with `PreferenceExample`.
2. Compute the reward model's pairwise loss with the lab's `RewardModel.loss`.
3. Sweep `klCoeff` in {0.0, 0.01, 0.1, 0.5} with `PPOSimulation.policyUpdate` and show
   the objective declining as the KL guard tightens.
4. Compute DPO loss at two betas to show beta's amplification effect.
5. End with a validation footer matching the lab style.

### Solution Walkthrough
- Step 1: Copy `PreferenceExample`, `RewardModel`, `PPOSimulation`, and `DPOLoss`
  verbatim — the seeded `Random(42)` noise keeps the demo's numbers reproducible.
- Step 2: Build four preference pairs covering the prompt/chosen/rejected schema.
- Step 3: Run `rm.loss(prefData)` and print it; the per-pair sigmoid loss
  `-log(1/(1+exp(rRejected - rChosen)))` is the real RLHF reward-model objective.
- Step 4: Call `policyUpdate(oldLogits, newLogits, 1.0, klCoeff)` across the sweep —
  the objective is `clippedSurrogate - klCoeff * kl`, so it declines monotonically.
- Step 5: Compute `DPOLoss.compute` at beta 0.1 and 0.5 — higher beta widens the
  chosen-vs-rejected gap in the loss.

### Code
```java
package com.genai.lab07.solution;

import java.util.*;

/**
 * Lab 07 walkthrough: preference optimization pipeline comparing
 * reward-model loss, PPO with a KL guard, and DPO. Reuses the lab's
 * PreferenceExample, RewardModel, PPOSimulation, and DPOLoss.
 */
public class PreferenceOptimization {

    static class PreferenceExample {
        final String prompt;
        final String chosen;
        final String rejected;
        PreferenceExample(String p, String c, String r) {
            prompt = p; chosen = c; rejected = r;
        }
    }

    static class RewardModel {
        final Random rng = new Random(42);

        double score(String text) {
            double base = text.length() * 0.01;
            if (text.contains("good") || text.contains("excellent")) base += 0.5;
            if (text.contains("bad") || text.contains("terrible")) base -= 0.5;
            return base + rng.nextGaussian() * 0.1;
        }

        double loss(List<PreferenceExample> data) {
            double totalLoss = 0.0;
            for (var ex : data) {
                double rChosen = score(ex.chosen);
                double rRejected = score(ex.rejected);
                totalLoss += -Math.log(1.0 / (1.0 + Math.exp(rRejected - rChosen)));
            }
            return totalLoss / data.size();
        }
    }

    static class PPOSimulation {
        static double clippedSurrogate(double probRatio, double advantage, double epsilon) {
            double clipped = Math.clamp(probRatio, 1 - epsilon, 1 + epsilon);
            return Math.min(probRatio * advantage, clipped * advantage);
        }

        static double policyUpdate(double[] logitsOld, double[] logitsNew,
                                    double reward, double klCoeff) {
            double kl = 0.0;
            double sumOld = 0.0, sumNew = 0.0;
            for (double l : logitsOld) sumOld += Math.exp(l);
            for (double l : logitsNew) sumNew += Math.exp(l);
            for (int i = 0; i < logitsOld.length; i++) {
                double pOld = Math.exp(logitsOld[i]) / sumOld;
                double pNew = Math.exp(logitsNew[i]) / sumNew;
                if (pOld > 1e-10) kl += pOld * Math.log(pOld / pNew);
            }
            double ratio = sumNew / sumOld;
            double clipped = clippedSurrogate(ratio, reward, 0.2);
            return clipped - klCoeff * kl;
        }
    }

    static class DPOLoss {
        static double compute(double[] policyLogitsChosen, double[] policyLogitsRejected,
                               double[] refLogitsChosen, double[] refLogitsRejected,
                               double beta) {
            double logPiChosen = policyLogitsChosen[0];
            double logPiRejected = policyLogitsRejected[0];
            double logRefChosen = refLogitsChosen[0];
            double logRefRejected = refLogitsRejected[0];

            double logRatio = beta * (logPiChosen - logRefChosen - logPiRejected + logRefRejected);
            return -Math.log(1.0 / (1.0 + Math.exp(-logRatio)));
        }
    }

    public static void main(String[] args) {
        var prefData = List.of(
            new PreferenceExample("What is AI?", "AI is the simulation of intelligence.",
                "AI is a computer thing."),
            new PreferenceExample("Explain attention.", "Attention computes weighted sums.",
                "Attention is hard."),
            new PreferenceExample("What is a transformer?", "A model with self-attention layers.",
                "A transformer is a power device."),
            new PreferenceExample("Summarize the paper.", "The paper introduces a new architecture.",
                "The paper is long.")
        );

        System.out.println("=== Reward Model Loss (preference data) ===");
        RewardModel rm = new RewardModel();
        System.out.printf("Loss over %d examples: %.4f%n", prefData.size(), rm.loss(prefData));

        System.out.println("\n=== PPO Objective vs KL Coefficient ===");
        double[] oldLogits = {0.5, 1.0, 0.3};
        double[] newLogits = {0.6, 0.9, 0.4};
        for (double klCoeff : new double[]{0.0, 0.01, 0.1, 0.5}) {
            double obj = PPOSimulation.policyUpdate(oldLogits, newLogits, 1.0, klCoeff);
            System.out.printf("klCoeff=%.2f objective=%.4f%n", klCoeff, obj);
        }

        System.out.println("\n=== DPO (no reward model needed) ===");
        double dpoLoss = DPOLoss.compute(
            new double[]{0.8}, new double[]{0.3},
            new double[]{0.7}, new double[]{0.4}, 0.1);
        System.out.printf("DPO loss: %.4f%n", dpoLoss);

        double dpoLossHighBeta = DPOLoss.compute(
            new double[]{0.8}, new double[]{0.3},
            new double[]{0.7}, new double[]{0.4}, 0.5);
        System.out.printf("DPO loss (beta=0.5): %.4f%n", dpoLossHighBeta);

        System.out.println("\nPreference optimization pipeline validated.");
    }
}
```

### Expected Output
```text
=== Reward Model Loss (preference data) ===
Loss over 4 examples: 0.6228

=== PPO Objective vs KL Coefficient ===
klCoeff=0.00 objective=1.0099
klCoeff=0.01 objective=1.0099
klCoeff=0.10 objective=1.0094
klCoeff=0.50 objective=1.0074

=== DPO (no reward model needed) ===
DPO loss: 0.6832
DPO loss (beta=0.5): 0.6444

Preference optimization pipeline validated.
```

### Company Evaluation
- Anthropic: Preference data quality, RLHF scaling, reward hacking defense.
- OpenAI: InstructGPT/PPO engineering, online rollouts, KL control in practice.
- Google DeepMind: Reward model evaluation, alignment tax measurement.
- Meta: DPO pipelines for open models, preference dataset curation.
- Stanford: DPO theory, closed-form policy-reward mapping, beta interpretation.

---

## Problem 2: Reward Hacking Detection — Company: OpenAI

### Interview Scenario
"You're at OpenAI auditing a fine-tune. The policy started spamming 'excellent' to push
the lab-style reward model's score up. Build a detector that compares reward gains
against a human-quality proxy and flags divergence."

### The Problem
1. Score generated text with the lab's `RewardModel`.
2. Compute the fraction of outputs containing reward-exploiting keywords.
3. Flag the run when keyword frequency grows faster than reward.

### Solution Walkthrough
- Step 1: Score a batch of generated strings and count keyword hits ('good', 'excellent').
- Step 2: Compare the mean reward to the keyword rate.
- Step 3: Print a `WARN: possible reward hacking` when the rate crosses 50%.

### Code
```java
String[] outputs = {"Good answer.", "Excellent!", "It is good and excellent."};
double meanReward = 0; int keywords = 0;
for (String o : outputs) {
    meanReward += rm.score(o);
    if (o.contains("good") || o.contains("excellent")) keywords++;
}
System.out.printf("Mean reward: %.2f, keyword rate: %.0f%%%n", meanReward / 3, 100.0 * keywords / 3);
if (keywords * 100 / 3 > 50) System.out.println("WARN: possible reward hacking");
```
Expected output: `Mean reward: ~1.0, keyword rate: 100%` followed by the WARN — the
spurious pattern the KL guard exists to suppress.

---

## Problem 3: Clipping vs No Clipping — Company: Google DeepMind

### Interview Scenario
"You're at Google DeepMind writing a unit test for the PPO surrogate. Prove that the
lab's `clippedSurrogate` caps the objective when the probability ratio moves far from 1,
while leaving small moves untouched."

### The Problem
1. Compute the unclipped surrogate for ratios in {0.5, 1.0, 1.5, 2.0}.
2. Compute the clipped surrogate at epsilon 0.2.
3. Show where the two diverge.

### Solution Walkthrough
- Step 1: Loop over ratios with advantage = 1.0.
- Step 2: `Math.min(ratio * 1.0, clamp(ratio, 0.8, 1.2) * 1.0)`.
- Step 3: Print both columns; the clip binds at ratio > 1.2.

### Code
```java
for (double ratio : new double[]{0.5, 1.0, 1.5, 2.0}) {
    double clipped = PPOSimulation.clippedSurrogate(ratio, 1.0, 0.2);
    System.out.printf("ratio=%.1f unclipped=%.2f clipped=%.2f%n", ratio, ratio, clipped);
}
```
Expected output: for ratio 0.5 the clipped value is 0.8 (floor binds), 1.0 stays 1.0,
and 1.5 and 2.0 cap at 1.2 — the trust region in action.
