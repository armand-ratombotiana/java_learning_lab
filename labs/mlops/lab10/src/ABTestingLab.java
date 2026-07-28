package com.mlops.lab10;

import java.util.*;
import java.util.stream.*;

/**
 * A/B Testing & Experimentation — Lab 10.
 * <p>
 * Demonstrates A/B testing framework for ML models including statistical
 * significance testing, confidence intervals, and multi-armed bandit algorithms
 * (epsilon-greedy, UCB, Thompson sampling).
 */
public class ABTestingLab {

    /** Z-test for two proportions. */
    static class StatisticalTest {

        /** Computes z-statistic for two proportions. */
        static double zTestTwoProportions(int nA, int successesA, int nB, int successesB) {
            double pA = (double) successesA / nA;
            double pB = (double) successesB / nB;
            double pPooled = (double) (successesA + successesB) / (nA + nB);
            double se = Math.sqrt(pPooled * (1 - pPooled) * (1.0 / nA + 1.0 / nB));
            return se == 0 ? 0 : (pA - pB) / se;
        }

        /** Computes p-value from z-statistic (normal approximation). */
        static double pValue(double z) {
            // Approximation of normal CDF
            double a = Math.abs(z);
            double p = 1.0 / (1.0 + a * 0.2316419);
            double d = 0.3989423 * Math.exp(-z * z / 2);
            double cdf = 1.0 - d * ((((1.330274 * p - 1.821256) * p + 1.781478) * p - 0.356538) * p + 0.319382) * p;
            return 2.0 * (1.0 - Math.min(cdf, 1.0)); // two-tailed
        }

        /** Computes confidence interval for a proportion. */
        static double[] confidenceInterval(double p, int n, double alpha) {
            double z = alpha == 0.05 ? 1.96 : 2.576;
            double se = Math.sqrt(p * (1 - p) / n);
            return new double[]{p - z * se, p + z * se};
        }

        /** Computes minimum detectable effect for given sample size. */
        static double minimumDetectableEffect(int n, double alpha, double beta, double variance) {
            double zAlpha = alpha == 0.05 ? 1.96 : 2.576;
            double zBeta = beta == 0.2 ? 0.84 : 1.28;
            return (zAlpha + zBeta) * Math.sqrt(2 * variance / n);
        }
    }

    /** A/B test experiment manager. */
    static class ABTest {
        final String name;
        final Map<String, Variant> variants = new LinkedHashMap<>();
        final Random rng = new Random();

        static class Variant {
            final String label;
            int impressions;
            int conversions;

            Variant(String label) { this.label = label; }

            double getRate() { return impressions == 0 ? 0 : (double) conversions / impressions; }

            void recordOutcome(boolean converted) {
                impressions++;
                if (converted) conversions++;
            }
        }

        ABTest(String name, String... variantLabels) {
            this.name = name;
            for (String label : variantLabels) {
                variants.put(label, new Variant(label));
            }
        }

        String assignVariant(long userId) {
            // Deterministic assignment based on userId hash
            int idx = Math.abs(Long.hashCode(userId)) % variants.size();
            return variants.keySet().stream().skip(idx).findFirst().orElseThrow();
        }

        void recordConversion(String variantLabel, long userId, boolean converted) {
            Variant v = variants.get(variantLabel);
            if (v != null) v.recordOutcome(converted);
        }

        void printResults() {
            System.out.printf("A/B Test: %s%n", name);
            System.out.println("---------------------");
            for (Variant v : variants.values()) {
                double[] ci = StatisticalTest.confidenceInterval(v.getRate(), v.impressions, 0.05);
                System.out.printf("  %s: rate=%.4f (%d/%d) 95%% CI=[%.4f, %.4f]%n",
                        v.label, v.getRate(), v.conversions, v.impressions, ci[0], ci[1]);
            }
            // Compare first two variants
            if (variants.size() >= 2) {
                List<Variant> list = new ArrayList<>(variants.values());
                Variant a = list.get(0), b = list.get(1);
                double z = StatisticalTest.zTestTwoProportions(
                        a.impressions, a.conversions, b.impressions, b.conversions);
                double p = StatisticalTest.pValue(z);
                System.out.printf("  z=%.3f, p=%.4f %s%n", z, p,
                        p < 0.05 ? "✓ Statistically significant" : "(not significant)");
            }
        }
    }

    /** Multi-Armed Bandit implementations. */
    static class MultiArmedBandit {
        final int nArms;
        final int[] counts;
        final double[] values;
        final Random rng = new Random();

        MultiArmedBandit(int nArms) {
            this.nArms = nArms;
            this.counts = new int[nArms];
            this.values = new double[nArms];
        }

        /** Epsilon-Greedy: explore with prob ε, exploit otherwise. */
        int selectArmEpsilonGreedy(double epsilon) {
            if (rng.nextDouble() < epsilon) {
                return rng.nextInt(nArms); // explore
            }
            return argmax(values); // exploit
        }

        /** UCB1: Upper Confidence Bound. */
        int selectArmUCB(int totalPlays) {
            int best = 0;
            double bestUcb = 0;
            for (int i = 0; i < nArms; i++) {
                if (counts[i] == 0) return i; // try each arm at least once
                double ucb = values[i] + Math.sqrt(2 * Math.log(totalPlays) / counts[i]);
                if (ucb > bestUcb) { bestUcb = ucb; best = i; }
            }
            return best;
        }

        /** Thompson Sampling: sample from Beta(α, β). */
        int selectArmThompsonSampling() {
            int best = 0;
            double bestSample = 0;
            for (int i = 0; i < nArms; i++) {
                double alpha = values[i] * counts[i] + 1; // successes + 1
                double beta = counts[i] - values[i] * counts[i] + 1; // failures + 1
                double sample = sampleBeta(alpha, beta);
                if (sample > bestSample) { bestSample = sample; best = i; }
            }
            return best;
        }

        void update(int arm, double reward) {
            counts[arm]++;
            double n = counts[arm];
            values[arm] = ((n - 1) * values[arm] + reward) / n;
        }

        private int argmax(double[] arr) {
            int idx = 0;
            for (int i = 1; i < arr.length; i++) {
                if (arr[i] > arr[idx]) idx = i;
            }
            return idx;
        }

        private double sampleBeta(double alpha, double beta) {
            // Generate Gamma samples using Marsaglia-Tsang method
            double ga = sampleGamma(alpha);
            double gb = sampleGamma(beta);
            return ga / (ga + gb);
        }

        private double sampleGamma(double shape) {
            if (shape < 1) {
                double u = rng.nextDouble();
                return sampleGamma(shape + 1) * Math.pow(u, 1.0 / shape);
            }
            double d = shape - 1.0 / 3.0;
            double c = 1.0 / Math.sqrt(9.0 * d);
            while (true) {
                double x, v;
                do {
                    x = rng.nextGaussian();
                    v = 1 + c * x;
                } while (v <= 0);
                double u = rng.nextDouble();
                double v3 = v * v * v;
                if (u < 1 - 0.0331 * x * x * x * x) return d * v3;
                if (Math.log(u) < 0.5 * x * x + d * (1 - v3 + Math.log(v3))) return d * v3;
            }
        }

        void printState() {
            for (int i = 0; i < nArms; i++) {
                System.out.printf("  Arm %d: %.4f (played %d times)%n", i, values[i], counts[i]);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== A/B Testing & Experimentation ===\n");

        // PART 1: A/B Test
        System.out.println("--- AB Test: Model A vs Model B ---");
        ABTest test = new ABTest("Model Comparison", "Champion (v1)", "Challenger (v2)");
        Random rng = new Random(42);

        // Simulate 5000 users per variant
        for (long userId = 0; userId < 10000; userId++) {
            String variant = test.assignVariant(userId);
            double conversionRate = variant.startsWith("Champion") ? 0.12 : 0.14;
            boolean converted = rng.nextDouble() < conversionRate;
            test.recordConversion(variant, userId, converted);
        }
        test.printResults();

        // PART 2: Multi-Armed Bandit
        System.out.println("\n--- Multi-Armed Bandit (Epsilon-Greedy) ---");
        double[] trueRates = {0.10, 0.12, 0.15, 0.13, 0.11};
        MultiArmedBandit mab = new MultiArmedBandit(5);

        for (int round = 0; round < 2000; round++) {
            int arm = mab.selectArmEpsilonGreedy(0.1);
            double reward = rng.nextDouble() < trueRates[arm] ? 1.0 : 0.0;
            mab.update(arm, reward);
        }
        System.out.println("True rates: " + Arrays.toString(trueRates));
        mab.printState();

        // PART 3: Sample Size Calculation
        System.out.println("\n--- Sample Size Calculation ---");
        double mde = StatisticalTest.minimumDetectableEffect(5000, 0.05, 0.2,
                0.12 * (1 - 0.12));
        System.out.printf("Minimum detectable effect with n=5000: %.4f (%.1f%%)%n", mde, mde * 100);
    }
}
