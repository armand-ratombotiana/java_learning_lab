# A/B Testing Guide

## Overview

A/B testing (split testing) compares two versions of a treatment by randomly assigning subjects to control (A) and treatment (B) groups. The goal is to determine if the treatment produces a statistically significant difference.

## Statistical Framework

### 1. Hypothesis Formulation

- **Null hypothesis (H₀)**: No difference between groups (θ_B - θ_A = 0)
- **Alternative hypothesis (H₁)**: There is a difference (θ_B - θ_A ≠ 0)

### 2. Test Statistics

**For binary metrics (conversion rate):**
```java
// Two-proportion z-test
double z = (pB - pA) / Math.sqrt(pPooled * (1 - pPooled) * (1.0/nA + 1.0/nB));
```

**For continuous metrics (revenue, time spent):**
```java
// Two-sample t-test
double t = (meanB - meanA) / Math.sqrt(varA/nA + varB/nB);
```

### 3. Sample Size Calculation

Use the normal approximation for binary metrics:
```java
public static int sampleSizePerVariant(double baseline, double effect, double alpha, double power) {
    double zAlpha = normalQuantile(1.0 - alpha / 2.0);
    double zBeta = normalQuantile(power);
    double pPooled = (baseline + baseline + effect) / 2.0;
    return (int) Math.ceil(
        2.0 * pPooled * (1 - pPooled) * Math.pow(zAlpha + zBeta, 2) / Math.pow(effect, 2)
    );
}
```

## Java Implementation

### ABTestResult Record
```java
public record ABTestResult(
    double zScore, double pValue, double ciLower, double ciUpper,
    double lift, boolean significant, long requiredSampleSize
) {
    public ABTestResult {
        if (Double.isNaN(zScore) || Double.isNaN(pValue)) {
            throw new IllegalArgumentException("Invalid test statistics");
        }
    }
}
```

### Metrics
```java
public sealed interface Metric permits BinaryMetric, ContinuousMetric {
    ABTestResult analyze(double[] control, double[] treatment, double alpha);
}

public record BinaryMetric(int controlConversions, int controlTotal,
                           int treatmentConversions, int treatmentTotal) implements Metric {
    @Override
    public ABTestResult analyze(double[] control, double[] treatment, double alpha) {
        double pA = (double) controlConversions / controlTotal;
        double pB = (double) treatmentConversions / treatmentTotal;
        double pPooled = (double) (controlConversions + treatmentConversions)
                       / (controlTotal + treatmentTotal);
        double se = Math.sqrt(pPooled * (1 - pPooled) * (1.0/controlTotal + 1.0/treatmentTotal));
        double z = (pB - pA) / se;
        double pValue = 2.0 * (1.0 - normalCDF(Math.abs(z)));
        double me = 1.96 * se;
        return new ABTestResult(z, pValue, (pB - pA) - me, (pB - pA) + me,
                                (pB - pA) / pA, pValue < alpha,
                                sampleSizePerVariant(pA, 0.01, alpha, 0.8));
    }
}
```

### Multiple Testing Correction
```java
public sealed interface Correction permits BonferroniCorrection, BenjaminiHochberg {
    double[] adjust(double[] pValues);
}

public record BenjaminiHochberg() implements Correction {
    @Override
    public double[] adjust(double[] pValues) {
        int m = pValues.length;
        var indexed = IntStream.range(0, m)
            .mapToObj(i -> new double[]{pValues[i], i})
            .sorted(Comparator.comparingDouble(a -> a[0]))
            .toArray(double[][]::new);
        double[] adjusted = new double[m];
        double cumulative = 1.0;
        for (int i = m - 1; i >= 0; i--) {
            double rank = i + 1;
            double q = indexed[i][0] * m / rank;
            cumulative = Math.min(cumulative, q);
            adjusted[(int) indexed[i][1]] = cumulative;
        }
        return adjusted;
    }
}
```

## Advanced Topics

### CUPED (Controlled-experiment Using Pre-Experiment Data)
```java
public double cupedAdjustment(double[] preTreatment, double[] postTreatment,
                              double[] preControl, double[] postControl) {
    double covT = covariance(preTreatment, postTreatment);
    double varT = variance(preTreatment);
    double covC = covariance(preControl, postControl);
    double varC = variance(preControl);
    double theta = (covT / varT + covC / varC) / 2.0;
    double adjustedT = mean(postTreatment) - theta * (mean(preTreatment) - mean(preControl));
    double adjustedC = mean(postControl);
    return adjustedT - adjustedC;
}
```

### Sequential Testing (always-valid p-values)
```java
public class SequentialTest {
    private final double alpha;
    private final double[] treatment;
    private final double[] control;
    private final List<Double> pValueHistory = new ArrayList<>();
    
    public double mixtureSPRT() {
        // Uses mixing distribution over effect sizes for anytime-valid inference
        double zSum = 0.0;
        double infoSum = 0.0;
        for (int i = 0; i < Math.min(treatment.length, control.length); i++) {
            double diff = treatment[i] - control[i];
            zSum += diff;
            infoSum += 1.0;
            double zScore = zSum / Math.sqrt(infoSum);
            // Always-valid p-value from martingale theory
            double p = Math.exp(-0.5 * zScore * zScore) / (0.5 * alpha);
            pValueHistory.add(Math.min(1.0, p));
        }
        return pValueHistory.get(pValueHistory.size() - 1);
    }
}
```
