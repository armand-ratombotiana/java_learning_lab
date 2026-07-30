# Guide: Statistical Power & Effect Size in Java

## Step 1: Compute Cohen's d
d = (mean1 - mean2) / pooledStdDev. Interpretation: |d| ≈ 0.2 (small), 0.5 (medium), 0.8 (large).

## Step 2: Compute Statistical Power
Power = 1 - β = Φ(|Δ|/(σ√(2/n)) - Z_α/2). Use normal CDF.

## Step 3: Compute Minimum Detectable Effect (MDE)
MDE = (Z_α/2 + Z_β) × σ × √(2/n). The smallest effect detectable at given power.

## Step 4: Sample Size for Power
Inverse of the power formula: n = 2(Z_α/2 + Z_β)²σ² / Δ².

## Step 5: Generate Power Curves
Vary sample size or effect size, compute power at each point.

## Step 6: Java Implementation
```java
public static double cohensD(double mean1, double mean2, double pooledStdDev) {
    return (mean1 - mean2) / pooledStdDev;
}
public static double powerTwoSampleMeans(double delta, double sigma,
                                          int nPerGroup, double alpha) {
    double zAlpha2 = normInv(1 - alpha / 2);
    double se = sigma * Math.sqrt(2.0 / nPerGroup);
    return normCdf(Math.abs(delta) / se - zAlpha2);
}
```

## Test Cases
- d = 0.5, n = 64 per group, α = 0.05 → power ≈ 0.80
- d = 0.8, n = 26 per group, α = 0.05 → power ≈ 0.80
- σ = 10, n = 100, α = 0.05, β = 0.20 → MDE ≈ 3.96
