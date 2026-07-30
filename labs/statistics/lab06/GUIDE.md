# Guide: Bayesian Statistics in Java

## Step 1: Understand the Beta Distribution
Beta(α, β) has mean = α/(α+β). It is a conjugate prior for the Binomial likelihood.

## Step 2: Compute Posterior
Posterior = Beta(α + successes, β + failures).

## Step 3: Compute Posterior Mean and Variance
Mean = α'/(α'+β'), Variance = α'β' / ((α'+β')²(α'+β'+1)).

## Step 4: Compute Credible Interval
Use numerical integration (or inverse Beta CDF approximation) to find the 95% highest density interval.

## Step 5: Bayesian A/B Testing
Compute P(θ_A > θ_B) by Monte Carlo sampling from both posteriors.

## Step 6: Java Implementation
```java
public static BetaPosterior betaBinomial(int priorAlpha, int priorBeta,
                                          int successes, int trials) {
    double postAlpha = priorAlpha + successes;
    double postBeta = priorBeta + (trials - successes);
    double mean = postAlpha / (postAlpha + postBeta);
    return new BetaPosterior(postAlpha, postBeta, mean);
}
```

## Test Cases
- Prior: Beta(1,1) (uniform), Data: 8 heads in 10 tosses → Posterior: Beta(9,3), Mean ≈ 0.75
- A/B test: Version A (100 conversions / 1000 visitors), Version B (120 / 1000) → P(B > A) ≈ 0.92
