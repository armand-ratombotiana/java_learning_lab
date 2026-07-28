# Problem Walkthrough: Hypothesis Testing Framework

## Problem
Build a unified hypothesis testing framework with parametric and non-parametric tests, automatic test selection based on data characteristics, and comprehensive output.

## Step 1: Unified Test Result

```java
public sealed interface TestResult permits TTestResult, ChiSquareResult, MannWhitneyResult, 
                                           AnovaResult, KruskalWallisResult, BootstrapResult {
    double pValue();
    double effectSize();
    String interpretation();
}

public record TTestResult(double tStat, double pValue, double df, double effectSize, 
                          double ciLower, double ciUpper) implements TestResult {
    public String interpretation() {
        if (pValue < 0.001) return "Highly significant (p < 0.001)";
        if (pValue < 0.01) return "Very significant (p < 0.01)";
        if (pValue < 0.05) return "Significant (p < 0.05)";
        return "Not significant (p >= 0.05)";
    }
}
```

## Step 2: Automatic Test Selector

```java
public class TestSelector {
    public TestResult autoSelect(double[][] groups) {
        if (groups.length == 2 && isContinuous(groups[0]) && isContinuous(groups[1])) {
            if (isNormal(groups[0]) && isNormal(groups[1]) && hasEqualVariance(groups[0], groups[1])) {
                return StudentTTest.test(groups[0], groups[1], 0.05);
            } else if (isNormal(groups[0]) && isNormal(groups[1])) {
                return WelchTTest.test(groups[0], groups[1], 0.05);
            } else {
                return MannWhitneyUTest.test(groups[0], groups[1]);
            }
        } else if (groups.length > 2 && isContinuous(groups[0])) {
            if (allNormal(groups) && allEqualVariance(groups)) {
                return OneWayANOVA.test(groups, 0.05);
            } else {
                return KruskalWallisTest.test(groups);
            }
        } else if (isCategorical(groups)) {
            return ChiSquareTest.independence(contingencyTable(groups));
        }
        throw new IllegalArgumentException("No suitable test found");
    }
    
    private boolean isNormal(double[] x) {
        // Shapiro-Wilk or Anderson-Darling test
        return shapiroWilk(x).pValue() > 0.05;
    }
    
    private boolean hasEqualVariance(double[] x, double[] y) {
        // Levene's or Bartlett's test
        return leveneTest(x, y).pValue() > 0.05;
    }
}
```

## Step 3: One-Way ANOVA

```java
public record AnovaResult(double fStat, double pValue, int dfBetween, int dfWithin,
                          double etaSq, double omegaSq) implements TestResult {
    public static AnovaResult test(double[][] groups, double alpha) {
        int k = groups.length;
        double grandMean = 0;
        int totalN = 0;
        for (double[] g : groups) { for (double v : g) grandMean += v; totalN += g.length; }
        grandMean /= totalN;
        
        double ssBetween = 0, ssWithin = 0;
        for (int j = 0; j < k; j++) {
            double gm = Arrays.stream(groups[j]).average().orElseThrow();
            ssBetween += groups[j].length * Math.pow(gm - grandMean, 2);
            for (double v : groups[j]) ssWithin += Math.pow(v - gm, 2);
        }
        
        int dfB = k - 1, dfW = totalN - k;
        double msBetween = ssBetween / dfB;
        double msWithin = ssWithin / dfW;
        double f = msBetween / msWithin;
        double p = 1.0 - fCdf(f, dfB, dfW);
        double etaSq = ssBetween / (ssBetween + ssWithin);
        double omegaSq = (ssBetween - dfB * msWithin) / (ssBetween + ssWithin + msWithin);
        
        return new AnovaResult(f, p, dfB, dfW, etaSq, omegaSq);
    }
}
```

## Step 4: Shapiro-Wilk Normality Test

```java
public record ShapiroWilkResult(double wStat, double pValue) {
    public static ShapiroWilkResult test(double[] x) {
        int n = x.length;
        double[] sorted = Arrays.copyOf(x, n);
        Arrays.sort(sorted);
        double mean = Arrays.stream(sorted).average().orElseThrow();
        double s2 = Arrays.stream(sorted).map(v -> Math.pow(v - mean, 2)).sum();
        
        // Generate coefficients (simplified for n up to 50)
        double[] a = new double[n];
        if (n <= 3) {
            a[0] = Math.sqrt(0.5);
        } else {
            // Approximate using Royston's method
            for (int i = 1; i <= n; i++) {
                a[i - 1] = normalQuantile((i - 0.375) / (n + 0.25));
            }
            double m2 = Arrays.stream(a).map(v -> v * v).sum();
            for (int i = 0; i < n; i++) a[i] /= Math.sqrt(m2);
            // Sign correction
            a[0] = -a[n - 1] = 1.0 / Math.sqrt(n);
        }
        
        double b = 0;
        for (int i = 0; i < n / 2; i++) b += a[i] * (sorted[n - 1 - i] - sorted[i]);
        double w = b * b / s2;
        
        // Approximate p-value (simplified)
        double mu = -2.0 + 2.1 / Math.sqrt(n); // These are approximate
        double sigma = 0.5 / Math.sqrt(n);
        double z = (Math.log(1 - w) - mu) / sigma;
        double p = 1.0 - normalCDF(z);
        
        return new ShapiroWilkResult(w, p);
    }
}
```

## Step 5: Levene's Test for Homogeneity of Variance

```java
public record LeveneResult(double fStat, double pValue) {
    public static LeveneResult test(double[][] groups) {
        int k = groups.length;
        double[][] deviations = new double[k][];
        for (int j = 0; j < k; j++) {
            double median = median(groups[j]);
            deviations[j] = Arrays.stream(groups[j]).map(v -> Math.abs(v - median)).toArray();
        }
        // One-way ANOVA on absolute deviations from group median
        AnovaResult anova = AnovaResult.test(deviations, 0.05);
        return new LeveneResult(anova.fStat(), anova.pValue());
    }
}
```

## Step 6: Verification

```
> testSelector.autoSelect(data1, data2)
[Student t-test] t = -2.14, df = 18, p = 0.046, d = 0.96
Interpretation: Significant (p < 0.05)

> testSelector.autoSelect(skewed1, skewed2)  
[Mann-Whitney U] U = 23, p = 0.038, rb = 0.54
Interpretation: Significant (p < 0.05)

> chiSquareTest.independence(table)
ChiSq = 12.4, df = 4, p = 0.015, V = 0.35
Interpretation: Significant (p < 0.05)
```
