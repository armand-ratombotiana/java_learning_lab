package com.genai.lab07;

import java.util.*;

/**
 * RLHF & Preference Optimization
 * 
 * Demonstrates reward modeling, PPO simulation, KL divergence,
 * and DPO concepts in Java.
 */
public class Main {

    static class PreferenceExample {
        final String prompt;
        final String chosen;
        final String rejected;
        PreferenceExample(String p, String c, String r) {
            prompt = p; chosen = c; rejected = r;
        }
    }

    /** Simple reward model (simulated with scores). */
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

    /** PPO simulation with clipped surrogate. */
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

    /** DPO loss implementation. */
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
                "Attention is hard.")
        );

        RewardModel rm = new RewardModel();
        System.out.println("=== Reward Model Loss ===");
        System.out.printf("Loss: %.4f%n", rm.loss(prefData));

        System.out.println("\n=== PPO Simulation ===");
        double[] oldLogits = {0.5, 1.0, 0.3};
        double[] newLogits = {0.6, 0.9, 0.4};
        double ppoObj = PPOSimulation.policyUpdate(oldLogits, newLogits, 1.0, 0.01);
        System.out.printf("PPO objective: %.4f%n", ppoObj);

        System.out.println("\n=== DPO Loss ===");
        double dpoLoss = DPOLoss.compute(
            new double[]{0.8}, new double[]{0.3},
            new double[]{0.7}, new double[]{0.4}, 0.1);
        System.out.printf("DPO loss: %.4f%n", dpoLoss);

        System.out.println("\nRLHF/DPO concepts validated.");
    }
}
