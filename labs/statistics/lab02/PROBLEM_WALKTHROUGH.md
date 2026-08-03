# Problem Walkthrough: Probability Distributions

## Problem 1: Rider Support Queue Staffing — Company: Uber
### Interview Scenario
"You're at Uber on the rider-support data science team. Support tickets arrive at about 4 per hour, and management wants to know: what is the probability of a 6+-ticket hour? How many hours should you simulate to sanity-check the model? What's the chance a rider waits longer than 4 minutes for an agent whose response time is Normal(2.5, 0.8) minutes? And for a batch of 10 riders, what's the probability at least 7 use the app, given 60% do? Build a single capacity model from the four distributions."

### The Problem
1. Compute the Poisson PMF/CDF for ticket arrivals with λ = 4 per hour and the probability of more than 6 arrivals in an hour
2. Simulate 24 one-hour windows with the lab's exponential inter-arrival sampler and compare the total to the expectation
3. Model inter-arrival gaps as Exponential(4) and compute the probability a gap is ≤ 15 minutes
4. Model agent response time as Normal(2.5 min, 0.8 min) and compute the probability it exceeds 4 minutes
5. Model channel mix as Binomial(n=10, p=0.6) and compute P(at least 7 of 10 use the app)

### Solution Walkthrough
- Step 1: Reuse the lab's `ProbabilityDistributions` methods verbatim: `poissonPmf`, `poissonCdf`, `poissonSample`, `exponentialCdf`, `exponentialSample`, `normalPdf`, `normalCdf`, `normalSample`, `binomialPmf`, `binomialCdf`, `binomialSample`
- Step 2: P(X > 6) = 1 − P(X ≤ 6) = 1 − `poissonCdf(6, 4)`; from the PMF rows, P(X≤6) = 0.889326 so the upper tail is 0.110674
- Step 3: Simulate 24 hours via `poissonSample(4)`; the demo total (107) lands near 24λ = 96, exercising the sampler
- Step 4: Inter-arrival gaps come from `exponentialSample(4)`; P(gap ≤ 0.25 h) = `exponentialCdf(0.25, 4)` = 1 − e^(-1) ≈ 0.6321
- Step 5: Response time tail: 1 − `normalCdf(4, 2.5, 0.8)` ≈ 0.0304 — about 3% of riders wait past 4 minutes
- Step 6: Channel mix tail: 1 − `binomialCdf(6, 10, 0.6)` = 0.3823, computed by summing the lab's PMF rows

### Code
```java
package com.statistics.lab02;

import java.util.Random;

/**
 * Mirrors the lab's ProbabilityDistributions class (Normal, Binomial,
 * Poisson, Exponential) and applies it to an Uber-style rider-support
 * queue: ticket arrivals are a Poisson process, inter-arrival times are
 * exponential, agent response time is normal.
 */
public final class SupportQueueModel {

    private static final Random RNG = new Random(7);

    private SupportQueueModel() {
    }

    public static double normalPdf(double x, double mu, double sigma) {
        double z = (x - mu) / sigma;
        return Math.exp(-0.5 * z * z) / (sigma * Math.sqrt(2 * Math.PI));
    }

    public static double normalCdf(double x, double mu, double sigma) {
        double z = (x - mu) / sigma;
        return 0.5 * (1 + erf(z / Math.sqrt(2)));
    }

    private static double erf(double x) {
        double a1 = 0.254829592, a2 = -0.284496736, a3 = 1.421413741;
        double a4 = -1.453152027, a5 = 1.061405429, p = 0.3275911;
        int sign = (x < 0) ? -1 : 1;
        x = Math.abs(x);
        double t = 1.0 / (1.0 + p * x);
        double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);
        return sign * y;
    }

    public static double normalSample(double mu, double sigma) {
        double u1 = RNG.nextDouble();
        double u2 = RNG.nextDouble();
        double z = Math.sqrt(-2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
        return mu + sigma * z;
    }

    public static double binomialPmf(int k, int n, double p) {
        if (k < 0 || k > n) {
            return 0;
        }
        return binomialCoefficient(n, k) * Math.pow(p, k) * Math.pow(1 - p, n - k);
    }

    public static double binomialCdf(int k, int n, double p) {
        double sum = 0;
        for (int i = 0; i <= k; i++) {
            sum += binomialPmf(i, n, p);
        }
        return sum;
    }

    private static double binomialCoefficient(int n, int k) {
        if (k < 0 || k > n) {
            return 0;
        }
        if (k > n - k) {
            k = n - k;
        }
        double result = 1;
        for (int i = 1; i <= k; i++) {
            result = result * (n - k + i) / i;
        }
        return result;
    }

    public static int binomialSample(int n, double p) {
        int count = 0;
        for (int i = 0; i < n; i++) {
            if (RNG.nextDouble() < p) {
                count++;
            }
        }
        return count;
    }

    public static double poissonPmf(int k, double lambda) {
        if (k < 0) {
            return 0;
        }
        return Math.exp(-lambda) * Math.pow(lambda, k) / factorial(k);
    }

    public static double poissonCdf(int k, double lambda) {
        double sum = 0;
        for (int i = 0; i <= k; i++) {
            sum += poissonPmf(i, lambda);
        }
        return sum;
    }

    private static double factorial(int n) {
        if (n <= 1) {
            return 1;
        }
        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static int poissonSample(double lambda) {
        double el = Math.exp(-lambda);
        double product = 1;
        int k = 0;
        do {
            product *= RNG.nextDouble();
            k++;
        } while (product > el);
        return k - 1;
    }

    public static double exponentialPdf(double x, double lambda) {
        if (x < 0) {
            return 0;
        }
        return lambda * Math.exp(-lambda * x);
    }

    public static double exponentialCdf(double x, double lambda) {
        if (x < 0) {
            return 0;
        }
        return 1 - Math.exp(-lambda * x);
    }

    public static double exponentialSample(double lambda) {
        double u = RNG.nextDouble();
        return -Math.log(1 - u) / lambda;
    }

    public static void main(String[] args) {
        System.out.println("=== Rider support: tickets arrive as Poisson(lambda=4)/hour ===");
        int lambda = 4;
        for (int k = 0; k <= 8; k++) {
            System.out.printf("k=%d  P(X=k)=%.6f  P(X<=k)=%.6f%n",
                k, poissonPmf(k, lambda), poissonCdf(k, lambda));
        }
        System.out.printf("E[X] = lambda = %d tickets/hour%n", lambda);
        System.out.printf("P(X > 6) = %.6f%n", 1 - poissonCdf(6, lambda));

        System.out.println("\n=== Simulated arrivals, 24 one-hour windows ===");
        int total = 0;
        for (int h = 0; h < 24; h++) {
            int arrivals = poissonSample(lambda);
            total += arrivals;
        }
        System.out.println("Total simulated arrivals: " + total);

        System.out.println("\n=== Inter-arrival times are Exponential(lambda=4) ===");
        for (int i = 0; i < 8; i++) {
            System.out.printf("  gap %.3f h%n", exponentialSample(4));
        }
        System.out.printf("P(gap <= 15 min) = %.4f%n", exponentialCdf(0.25, 4));

        System.out.println("\n=== Agent response time: Normal(mu=2.5 min, sigma=0.8) ===");
        for (int i = 0; i < 5; i++) {
            System.out.printf("  sample %.2f min%n", normalSample(2.5, 0.8));
        }
        System.out.printf("P(response > 4 min) = %.4f%n", 1 - normalCdf(4, 2.5, 0.8));

        System.out.println("\n=== Channel mix: Binomial(n=10 riders, p=0.6 use the app) ===");
        System.out.println("App users in one batch of 10: " + binomialSample(10, 0.6));
        System.out.printf("P(at least 7 of 10 use app) = %.4f%n", 1 - binomialCdf(6, 10, 0.6));
    }
}
```

### Expected Output
```
=== Rider support: tickets arrive as Poisson(lambda=4)/hour ===
k=0  P(X=k)=0.018316  P(X<=k)=0.018316
k=1  P(X=k)=0.073263  P(X<=k)=0.091578
k=2  P(X=k)=0.146525  P(X<=k)=0.238103
k=3  P(X=k)=0.195367  P(X<=k)=0.433470
k=4  P(X=k)=0.195367  P(X<=k)=0.628837
k=5  P(X=k)=0.156293  P(X<=k)=0.785130
k=6  P(X=k)=0.104196  P(X<=k)=0.889326
k=7  P(X=k)=0.059540  P(X<=k)=0.948866
k=8  P(X=k)=0.029770  P(X<=k)=0.978637
E[X] = lambda = 4 tickets/hour
P(X > 6) = 0.110674

=== Simulated arrivals, 24 one-hour windows ===
Total simulated arrivals: 107

=== Inter-arrival times are Exponential(lambda=4) ===
  gap 0.206 h
  gap 0.093 h
  gap 0.224 h
  gap 0.024 h
  gap 0.162 h
  gap 0.554 h
  gap 0.041 h
  gap 0.382 h
P(gap <= 15 min) = 0.6321

=== Agent response time: Normal(mu=2.5 min, sigma=0.8) ===
  sample 2.07 min
  sample 2.13 min
  sample 3.17 min
  sample 2.63 min
  sample 3.50 min
P(response > 4 min) = 0.0304

=== Channel mix: Binomial(n=10 riders, p=0.6 use the app) ===
App users in one batch of 10: 2
P(at least 7 of 10 use app) = 0.3823
```

### Company Evaluation
- Uber: queueing and capacity modeling, driver supply as a Poisson process, ETA distributions, surge detection from inter-arrival gaps.
- Netflix: streaming session durations as heavy-tailed variables, buffering event counts per hour, A/B latency percentiles under the normal model.
- Google: ad click counts per impression (Binomial), query arrival rates (Poisson), service response-time normality checks before z-tests.
- Amazon: checkout completion counts, warehouse pick events per hour, request inter-arrival modeling for autoscaling.

---

## Problem 2: Ad Click Probability — Company: Google
### Interview Scenario
"You're at Google Ads. An ad gets 10 impressions per slot page; the true click-through rate is 0.5. The team wants the exact probability of 7+ clicks on one page, and a quick way to sample click counts for simulations."

### The Problem
1. Compute the Binomial PMF and CDF for n=10, p=0.5
2. Report P(X ≥ 7) as a tail probability
3. Generate click-count samples by simulating Bernoulli trials

### Solution Walkthrough
- Step 1: `binomialPmf(k, 10, 0.5)` sums to 1.000000 across k=0..10 in the lab demo, with the peak 0.246094 at k=5
- Step 2: P(X ≥ 7) = 1 − `binomialCdf(6, 10, 0.5)` = 1 − 0.828125 = 0.171875
- Step 3: `binomialSample(10, 0.5)` counts `RNG.nextDouble() < p` over 10 trials — the demo produced 7, 5, 6, 3, 3

### Code
```java
public static void main(String[] args) {
    int n = 10;
    double p = 0.5;
    System.out.printf("P(X = 5) = %.6f%n", binomialPmf(5, n, p));
    System.out.printf("P(X >= 7) = %.6f%n", 1 - binomialCdf(6, n, p));
    System.out.print("Samples: ");
    for (int i = 0; i < 5; i++) {
        System.out.print(binomialSample(n, p) + " ");
    }
    System.out.println();
}
```

### Expected Output
```
P(X = 5) = 0.246094
P(X >= 7) = 0.171875
Samples: 7 5 6 3 3
```

---

## Problem 3: Streaming Session Gaps — Company: Netflix
### Interview Scenario
"You're at Netflix analyzing playback sessions. Pauses between playbacks behave like a Poisson process with λ = 3 pauses per hour, and session lengths are roughly Exponential(λ=2) hours. The team wants exact probabilities without loading Python."

### The Problem
1. Compute Poisson PMF/CDF for λ = 3 and the probability of at most 4 pauses in an hour
2. Compute the Exponential CDF at 0.5 and 1.5 hours for λ = 2
3. Sanity-check that the exponential PDF integrates correctly at x = 0

### Solution Walkthrough
- Step 1: `poissonCdf(4, 3)` = 0.815263 from the lab demo — sum of `poissonPmf` rows k=0..4
- Step 2: `exponentialCdf(0.5, 2)` = 0.632121, `exponentialCdf(1.5, 2)` = 0.950213 — two-thirds of sessions break within half an hour, 95% within 1.5 hours
- Step 3: `exponentialPdf(0, 2)` = 2.000000 = λ, the boundary value that makes the density integrate to 1

### Code
```java
public static void main(String[] args) {
    System.out.printf("P(<= 4 pauses/h) = %.6f%n", poissonCdf(4, 3));
    System.out.printf("Exponential CDF at 0.5h: %.6f%n", exponentialCdf(0.5, 2));
    System.out.printf("Exponential CDF at 1.5h: %.6f%n", exponentialCdf(1.5, 2));
    System.out.printf("Exponential PDF at 0:    %.6f%n", exponentialPdf(0, 2));
}
```

### Expected Output
```
P(<= 4 pauses/h) = 0.815263
Exponential CDF at 0.5h: 0.632121
Exponential CDF at 1.5h: 0.950213
Exponential PDF at 0:    2.000000
```
