# Problem Walkthrough: Bayesian Statistics

## Problem 1: Checkout Button Bayesian A/B Test — Company: Booking.com
### Interview Scenario
"You're at Booking.com on the conversion optimization team. A redesigned checkout button ran on 500 sessions and converted 62; the control converted 46 of 500. Product needs a decision metric that doesn't require a p-value lecture: 'what is the probability the new button is actually better, and what are the plausible rates for each version?' You must also show how the answer would shift under a skeptical prior, since finance is wary of over-claiming."

### The Problem
1. Compute the Beta posterior for each version under the uniform Beta(1,1) prior
2. Compute the 95% equal-tailed credible interval for each posterior
3. Estimate P(variant > control) by Monte Carlo sampling
4. Show how a strong prior Beta(100,900) moves the control posterior mean
5. Spot-check the Beta density machinery with known PDF/CDF values

### Solution Walkthrough
- Step 1: Reuse the lab's `BayesianStatistics` methods verbatim: `betaBinomialPosterior`, `credibleInterval95`, `abTestProbability`, `betaPdf`, `betaCdf`, `sampleBeta`, `sampleGamma`, `regularizedIncompleteBeta`, `continuedFractionBeta`, `logGamma`, `logBeta`
- Step 2: Control: Beta(47, 455), mean 0.0936; variant: Beta(63, 439), mean 0.1255 — the conjugacy update `prior + successes / trials - successes`
- Step 3: `credibleInterval95` bisects the posterior CDF: control [0.0697, 0.1206], variant [0.0980, 0.1558]
- Step 4: `abTestProbability(46, 500, 62, 500, 100000)` — 100k Gamma-transformed Beta draws per arm, seed 42 — yields P(variant > control) = 0.9464
- Step 5: Strong-prior check: Beta(100,900) prior pushes the control posterior mean to 0.0973, quantifying prior influence at n = 500
- Step 6: PDF/CDF spot checks (2.457600, 0.890625) validate the distribution helpers

### Code
```java
package com.statistics.lab06;

import java.util.Random;

/**
 * Mirrors the lab's BayesianStatistics class (Beta-Binomial conjugate
 * model, credible intervals, Monte Carlo A/B testing) and applies it to
 * a Booking.com-style checkout button experiment.
 */
public final class BayesianExperiment {

    private BayesianExperiment() {
    }

    public record BetaPosterior(double alpha, double beta, double mean) {
        @Override
        public String toString() {
            return String.format("Beta(%.1f, %.1f), mean = %.4f", alpha, beta, mean);
        }
    }

    public static double logBeta(double a, double b) {
        return logGamma(a) + logGamma(b) - logGamma(a + b);
    }

    public static double logGamma(double x) {
        double[] coef = {
            76.18009172947146, -86.50532032941677,
            24.01409824083091, -1.231739572450155,
            0.1208650973866179e-2, -0.5395239384953e-5
        };
        double y = x;
        double tmp = x + 5.5;
        tmp -= (x + 0.5) * Math.log(tmp);
        double ser = 1.000000000190015;
        for (int j = 0; j < 6; j++) {
            y += 1;
            ser += coef[j] / y;
        }
        return -tmp + Math.log(2.5066282746310005 * ser / x);
    }

    public static double betaPdf(double x, double a, double b) {
        if (x < 0 || x > 1) return 0;
        return Math.exp((a - 1) * Math.log(x) + (b - 1) * Math.log(1 - x) - logBeta(a, b));
    }

    public static double betaCdf(double x, double a, double b) {
        if (x < 0) return 0;
        if (x > 1) return 1;
        return regularizedIncompleteBeta(x, a, b);
    }

    public static double regularizedIncompleteBeta(double x, double a, double b) {
        if (x == 0 || x == 1) return x;
        double bt = Math.exp(logGamma(a + b) - logGamma(a) - logGamma(b)
            + a * Math.log(x) + b * Math.log(1 - x));
        if (x < (a + 1) / (a + b + 2)) {
            return bt * continuedFractionBeta(x, a, b) / a;
        } else {
            return 1 - bt * continuedFractionBeta(1 - x, b, a) / b;
        }
    }

    private static double continuedFractionBeta(double x, double a, double b) {
        int maxIter = 200;
        double eps = 3e-12;
        double qab = a + b;
        double qap = a + 1;
        double qam = a - 1;
        double c = 1.0;
        double d = 1.0 - qab * x / qap;
        if (Math.abs(d) < eps) d = eps;
        d = 1.0 / d;
        double h = d;
        for (int m = 1; m <= maxIter; m++) {
            int m2 = 2 * m;
            double aa = m * (b - m) * x / ((qam + m2) * (a + m2));
            d = 1.0 + aa * d;
            if (Math.abs(d) < eps) d = eps;
            c = 1.0 + aa / c;
            if (Math.abs(c) < eps) c = eps;
            d = 1.0 / d;
            h *= d * c;
            aa = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2));
            d = 1.0 + aa * d;
            if (Math.abs(d) < eps) d = eps;
            c = 1.0 + aa / c;
            if (Math.abs(c) < eps) c = eps;
            d = 1.0 / d;
            double del = d * c;
            h *= del;
            if (Math.abs(del - 1.0) < eps) break;
        }
        return h;
    }

    public static BetaPosterior betaBinomialPosterior(int priorAlpha, int priorBeta,
                                                       int successes, int trials) {
        double postAlpha = priorAlpha + successes;
        double postBeta = priorBeta + (trials - successes);
        double mean = postAlpha / (postAlpha + postBeta);
        return new BetaPosterior(postAlpha, postBeta, mean);
    }

    public static double[] credibleInterval95(double postAlpha, double postBeta) {
        double lo = 0.0, hi = 1.0;
        for (int i = 0; i < 50; i++) {
            double mid = (lo + hi) / 2;
            if (betaCdf(mid, postAlpha, postBeta) < 0.025) {
                lo = mid;
            } else {
                hi = mid;
            }
        }
        double lower = (lo + hi) / 2;
        lo = 0.0;
        hi = 1.0;
        for (int i = 0; i < 50; i++) {
            double mid = (lo + hi) / 2;
            if (betaCdf(mid, postAlpha, postBeta) < 0.975) {
                lo = mid;
            } else {
                hi = mid;
            }
        }
        double upper = (lo + hi) / 2;
        return new double[]{lower, upper};
    }

    public static double abTestProbability(int aSuccesses, int aTrials,
                                            int bSuccesses, int bTrials,
                                            int samples) {
        BetaPosterior postA = betaBinomialPosterior(1, 1, aSuccesses, aTrials);
        BetaPosterior postB = betaBinomialPosterior(1, 1, bSuccesses, bTrials);
        int count = 0;
        Random rng = new Random(42);
        for (int i = 0; i < samples; i++) {
            double thetaA = sampleBeta(postA.alpha(), postA.beta(), rng);
            double thetaB = sampleBeta(postB.alpha(), postB.beta(), rng);
            if (thetaB > thetaA) count++;
        }
        return (double) count / samples;
    }

    public static double sampleBeta(double a, double b, Random rng) {
        double ga = sampleGamma(a, rng);
        double gb = sampleGamma(b, rng);
        return ga / (ga + gb);
    }

    public static double sampleGamma(double shape, Random rng) {
        if (shape < 1) {
            double u = rng.nextDouble();
            return sampleGamma(1.0 + shape, rng) * Math.pow(u, 1.0 / shape);
        }
        double d = shape - 1.0 / 3.0;
        double c = 1.0 / Math.sqrt(9.0 * d);
        while (true) {
            double x, v;
            do {
                x = rng.nextGaussian();
                v = 1.0 + c * x;
            } while (v <= 0);
            v = v * v * v;
            double u = rng.nextDouble();
            double x2 = x * x;
            if (u < 1.0 - 0.0331 * x2 * x2) {
                return d * v;
            }
            if (Math.log(u) < 0.5 * x2 + d * (1.0 - v + Math.log(v))) {
                return d * v;
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Booking.com checkout button test (Beta(1,1) prior) ===");
        int ctrlSuc = 46, ctrlTri = 500;
        int varSuc = 62, varTri = 500;

        BetaPosterior ctrl = betaBinomialPosterior(1, 1, ctrlSuc, ctrlTri);
        BetaPosterior var = betaBinomialPosterior(1, 1, varSuc, varTri);
        System.out.println("Control: " + ctrl);
        System.out.println("Variant: " + var);

        double[] ciA = credibleInterval95(ctrl.alpha(), ctrl.beta());
        double[] ciB = credibleInterval95(var.alpha(), var.beta());
        System.out.printf("Control 95%% CI: [%.4f, %.4f]%n", ciA[0], ciA[1]);
        System.out.printf("Variant 95%% CI: [%.4f, %.4f]%n", ciB[0], ciB[1]);
        System.out.printf("CIs overlap: %b%n", !(ciB[0] > ciA[1] || ciA[0] > ciB[1]));

        double prob = abTestProbability(ctrlSuc, ctrlTri, varSuc, varTri, 100000);
        System.out.printf("P(variant > control) = %.4f%n", prob);

        System.out.println("\n=== Prior influence fades with more data ===");
        BetaPosterior strongPrior = betaBinomialPosterior(100, 900, ctrlSuc, ctrlTri);
        System.out.println("Posterior with strong prior Beta(100,900): " + strongPrior);
        System.out.printf("Posterior mean with strong prior: %.4f (vs %.4f uninformative)%n",
            strongPrior.mean(), ctrl.mean());

        System.out.println("\n=== Beta PDF/CDF spot checks ===");
        System.out.printf("Beta(2,5) PDF at 0.2: %.6f%n", betaPdf(0.2, 2, 5));
        System.out.printf("Beta(2,5) CDF at 0.5: %.6f%n", betaCdf(0.5, 2, 5));
    }
}
```

### Expected Output
```
=== Booking.com checkout button test (Beta(1,1) prior) ===
Control: Beta(47.0, 455.0), mean = 0.0936
Variant: Beta(63.0, 439.0), mean = 0.1255
Control 95% CI: [0.0697, 0.1206]
Variant 95% CI: [0.0980, 0.1558]
CIs overlap: true
P(variant > control) = 0.9464

=== Prior influence fades with more data ===
Posterior with strong prior Beta(100,900): Beta(146.0, 1354.0), mean = 0.0973
Posterior mean with strong prior: 0.0973 (vs 0.0936 uninformative)

=== Beta PDF/CDF spot checks ===
Beta(2,5) PDF at 0.2: 2.457600
Beta(2,5) CDF at 0.5: 0.890625
```

### Company Evaluation
- Booking.com: Bayesian A/B platform with P(B > A) decision metrics, credible intervals on conversion, prior calibration for low-traffic funnels.
- Airbnb: Bayesian CVR for search ranking, posterior-based adaptive traffic allocation.
- Uber: driver-side conversion tests, small-sample Bayesian inference on surge adoption.
- Netflix: Bayesian watch-time tests, posterior probability of improvement for recommendation changes.

---

## Problem 2: Coin Fairness — Company: Google
### Interview Scenario
"You're at Google testing an RNG for a fairness-sensitive game. 8 of 10 tosses came up heads. Give a posterior summary for the true heads rate under a uniform prior."

### The Problem
1. Compute the Beta posterior
2. Report the posterior mean
3. Report the 95% credible interval

### Solution Walkthrough
- Step 1: `betaBinomialPosterior(1, 1, 8, 10)` → Beta(9, 3), mean 0.75
- Step 2: `credibleInterval95(9, 3)` → [0.4822, 0.9398] via the bisection on `betaCdf`
- Step 3: The interval still includes 0.5 — with 10 tosses we cannot conclude bias, which is the honest small-sample answer

### Code
```java
BetaPosterior post = betaBinomialPosterior(1, 1, 8, 10);
System.out.println("Posterior: " + post);
double[] ci = credibleInterval95(post.alpha(), post.beta());
System.out.printf("95%% Credible Interval: [%.4f, %.4f]%n", ci[0], ci[1]);
```

### Expected Output
```
Posterior: Beta(9.0, 3.0), mean = 0.7500
95% Credible Interval: [0.4822, 0.9398]
```

---

## Problem 3: Traffic Allocation Between Two Versions — Company: Airbnb
### Interview Scenario
"You're at Airbnb deciding how to split traffic between two search layouts. Historical data: A converted 100 of 1000, B converted 120 of 1000. Compute the posterior summary and the probability B beats A."

### The Problem
1. Compute both posteriors
2. Compute both 95% credible intervals
3. Estimate P(B > A) with 100k Monte Carlo samples
4. Use the result to justify shifting traffic to B

### Solution Walkthrough
- Step 1: `betaBinomialPosterior(1, 1, 100, 1000)` → Beta(101, 901) mean 0.1008; B → Beta(121, 881) mean 0.1208
- Step 2: Intervals [0.0829, 0.1202] and [0.1013, 0.1416]
- Step 3: `abTestProbability(100, 1000, 120, 1000, 100000)` = 0.9238 — a 92% posterior probability B is better
- Step 4: Allocate the next traffic tranche toward B while the intervals narrow further

### Code
```java
double prob = abTestProbability(100, 1000, 120, 1000, 100000);
System.out.printf("P(B > A) = %.4f%n", prob);
BetaPosterior postA = betaBinomialPosterior(1, 1, 100, 1000);
BetaPosterior postB = betaBinomialPosterior(1, 1, 120, 1000);
double[] ciA = credibleInterval95(postA.alpha(), postA.beta());
double[] ciB = credibleInterval95(postB.alpha(), postB.beta());
System.out.println("A posterior: " + postA);
System.out.println("B posterior: " + postB);
System.out.printf("A 95%% CI: [%.4f, %.4f]%n", ciA[0], ciA[1]);
System.out.printf("B 95%% CI: [%.4f, %.4f]%n", ciB[0], ciB[1]);
```

### Expected Output
```
P(B > A) = 0.9238
A posterior: Beta(101.0, 901.0), mean = 0.1008
B posterior: Beta(121.0, 881.0), mean = 0.1208
A 95% CI: [0.0829, 0.1202]
B 95% CI: [0.1013, 0.1416]
```
