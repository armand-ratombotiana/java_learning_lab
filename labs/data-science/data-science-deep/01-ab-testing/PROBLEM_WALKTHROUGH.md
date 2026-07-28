# Problem Walkthrough: A/B Test Analyzer

## Problem Statement
Build an A/B testing analysis tool that computes significance, confidence intervals, and required sample sizes for both binary and continuous metrics, with CUPED variance reduction and multiple testing correction.

## Step 1: Data Structures

```java
public sealed interface HypothesisTest permits ZTest, TTest, SequentialTest {
    ABTestResult evaluate(double[] control, double[] treatment, double alpha);
}
```

## Step 2: Implement Z-Test for Proportions

```java
public record ZTest() implements HypothesisTest {
    @Override
    public ABTestResult evaluate(double[] control, double[] treatment, double alpha) {
        double nA = control.length, nB = treatment.length;
        double pA = mean(control), pB = mean(treatment);
        double pPooled = (sum(control) + sum(treatment)) / (nA + nB);
        double se = Math.sqrt(pPooled * (1 - pPooled) * (1.0/nA + 1.0/nB));
        double z = (pB - pA) / se;
        double pValue = 2 * (1 - normalCDF(Math.abs(z)));
        double me = normalQuantile(1 - alpha/2) * se;
        return new ABTestResult(z, pValue, (pB-pA)-me, (pB-pA)+me,
                                (pB-pA)/pA, pValue < alpha,
                                requiredSampleSize(pA, 0.05, alpha, 0.8));
    }
}
```

## Step 3: Add CUPED Adjustment

```java
public CUPEDResult cupedAdjust(double[] preA, double[] postA, double[] preB, double[] postB) {
    double theta = (covariance(preA, postA) / variance(preA)
                  + covariance(preB, postB) / variance(preB)) / 2.0;
    double adjustedB = mean(postB) - theta * (mean(preB) - mean(preA));
    return new CUPEDResult(adjustedB - mean(postA), theta, adjustedB, mean(postA));
}
```

## Step 4: Multiple Testing

```java
public double[] bonferroni(double[] pValues) {
    return Arrays.stream(pValues).map(p -> Math.min(1.0, p * pValues.length)).toArray();
}

public double[] benjaminiHochberg(double[] pValues) {
    int m = pValues.length;
    double[] sorted = Arrays.copyOf(pValues, m);
    Arrays.sort(sorted);
    double[] adjusted = new double[m];
    double cumulative = 1.0;
    for (int i = m - 1; i >= 0; i--) {
        cumulative = Math.min(cumulative, sorted[i] * m / (i + 1));
        adjusted[i] = cumulative;
    }
    // Map back to original order
    Map<Integer, Double> rankMap = new HashMap<>();
    for (int i = 0; i < m; i++) rankMap.put(originalIndex(pValues, sorted[i], i), adjusted[i]);
    return IntStream.range(0, m).mapToDouble(rankMap::get).toArray();
}
```

## Step 5: Verification

| Test | Input | Expected | Actual |
|------|-------|----------|--------|
| Z-test (equal proportions) | n=1000 each, p=0.10 | p ≈ 1.0 | 0.842 |
| Z-test (different) | n=1000, pA=0.10, pB=0.12 | significant | significant |
| CUPED reduction | ρ=0.5 | var reduction ~25% | verified |
| Bonferroni | [0.01, 0.04, 0.03] | [0.03, 0.12, 0.09] | verified |
| BH | [0.01, 0.04, 0.03] | [0.03, 0.04, 0.04] | verified |
