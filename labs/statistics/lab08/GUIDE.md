# Guide: Experimental Design in Java

## Step 1: Sample Size for Means
n = 2(Z_α/2 + Z_β)²σ² / Δ². Uses normal approximation; Z values from standard normal CDF inverse.

## Step 2: Sample Size for Proportions
Use pooled variance formula with Z-scores for the desired alpha and beta.

## Step 3: Factorial Design (2²)
Two factors A and B, each at two levels (-1, +1). Compute main effects:
- Effect of A = (ȳ when A=+1) - (ȳ when A=-1)
- Effect of B = (ȳ when B=+1) - (ȳ when B=-1)
- Interaction AB = (ȳ at A=+1,B=+1 - ȳ at A=+1,B=-1) - (ȳ at A=-1,B=+1 - ȳ at A=-1,B=-1)

## Step 4: Java Implementation
```java
public static double sampleSizeMeans(double delta, double sigma,
                                      double alpha, double beta) {
    double zAlpha2 = normInv(1 - alpha / 2);
    double zBeta = normInv(1 - beta);
    return 2 * Math.pow(zAlpha2 + zBeta, 2) * sigma * sigma / (delta * delta);
}
```

## Test Cases
- Means: δ=5, σ=10, α=0.05, β=0.20 → n ≈ 63 per group
- Proportions: p1=0.1, p2=0.3, α=0.05, β=0.20 → n ≈ 62 per group
- 2² factorial: A + B interaction clearly visible in response means
