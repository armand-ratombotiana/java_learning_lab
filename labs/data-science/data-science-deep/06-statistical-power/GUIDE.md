# Statistical Power Guide

## Framework

Power = P(reject H₀ | H₁ true) = 1 - β

Four quantities determine power:
- **α**: Type I error rate (usually 0.05)
- **β**: Type II error rate (usually 0.20, so power = 0.80)
- **n**: Sample size
- **δ**: Effect size

Specify any three, solve for the fourth.

## 1. Two-Sample t-Test

### Cohen's d
```java
public record CohensD(double d) {
    public static CohensD fromMeans(double mean1, double mean2, double sd1, double sd2) {
        double pooled = Math.sqrt((sd1 * sd1 + sd2 * sd2) / 2.0);
        return new CohensD(Math.abs(mean1 - mean2) / pooled);
    }
    // Interpretation: 0.2 = small, 0.5 = medium, 0.8 = large
}
```

### Power Calculation
```java
public static double powerTwoSampleT(double d, int n1, int n2, double alpha) {
    double df = n1 + n2 - 2;
    double ncp = d / Math.sqrt(1.0 / n1 + 1.0 / n2); // non-centrality parameter
    double tCrit = studentTQuantile(1.0 - alpha / 2.0, df);
    return 1.0 - nctCdf(tCrit, df, ncp) + nctCdf(-tCrit, df, ncp);
}
```

### Sample Size Calculation
```java
public static int sampleSizeTwoSampleT(double d, double power, double alpha) {
    double zAlpha = normalQuantile(1.0 - alpha / 2.0);
    double zBeta = normalQuantile(power);
    double n = 2.0 * Math.pow(zAlpha + zBeta, 2) / (d * d);
    return (int) Math.ceil(n);
}
```

## 2. One-Way ANOVA

### Cohen's f
```java
public record CohensF(double f) {
    public static CohensF fromMeans(double[] groupMeans, double grandMean, double withinSD) {
        double ssBetween = 0;
        for (double m : groupMeans) ssBetween += Math.pow(m - grandMean, 2) / groupMeans.length;
        double msBetween = ssBetween / (groupMeans.length - 1);
        return new CohensF(Math.sqrt(msBetween / (withinSD * withinSD)));
    }
    // Interpretation: 0.10 = small, 0.25 = medium, 0.40 = large
}
```

### Power for ANOVA F-test
```java
public static double powerAnova(double f, int k, int nPerGroup, double alpha) {
    int df1 = k - 1;
    int df2 = k * (nPerGroup - 1);
    double ncp = f * f * (k * nPerGroup); // non-centrality parameter
    double fCrit = fQuantile(1.0 - alpha, df1, df2);
    return 1.0 - ncfCdf(fCrit, df1, df2, ncp);
}
```

## 3. Two-Proportion z-Test

### Cohen's h
```java
public record CohensH(double h) {
    // h = 2 * arcsin(sqrt(p1)) - 2 * arcsin(sqrt(p2))
    public static CohensH fromProportions(double p1, double p2) {
        double h = 2.0 * Math.asin(Math.sqrt(p1)) - 2.0 * Math.asin(Math.sqrt(p2));
        return new CohensH(Math.abs(h));
    }
    // Interpretation: 0.20 = small, 0.50 = medium, 0.80 = large
}
```

### Power and Sample Size
```java
public static double powerTwoProportionZ(double p1, double p2, int n, double alpha) {
    double h = CohensH.fromProportions(p1, p2).h();
    double zAlpha = normalQuantile(1.0 - alpha / 2.0);
    double zBeta = Math.abs(h) * Math.sqrt(n / 2.0) - zAlpha;
    return normalCDF(zBeta);
}
```

## 4. Equivalence Testing (TOST)

```java
public class TOST {
    public static boolean equivalenceTest(double mean1, double mean2, double sd, 
                                          int n1, int n2, double margin, double alpha) {
        double se = sd * Math.sqrt(1.0 / n1 + 1.0 / n2);
        double t1 = (mean1 - mean2 + margin) / se;
        double t2 = (mean1 - mean2 - margin) / se;
        double p1 = 1.0 - studentTCdf(t1, n1 + n2 - 2);
        double p2 = studentTCdf(t2, n1 + n2 - 2);
        return Math.max(p1, p2) < alpha; // both one-sided tests must be significant
    }
}
```

## Power Curves

```java
public record PowerCurve(List<PowerPoint> points) {
    public record PowerPoint(double sampleSize, double power) {}
    
    public static PowerCurve generate(double d, double alpha, int maxN, int step) {
        List<PowerPoint> pts = new ArrayList<>();
        for (int n = step; n <= maxN; n += step) {
            double power = powerTwoSampleT(d, n, n, alpha);
            pts.add(new PowerPoint(n, power));
        }
        return new PowerCurve(pts);
    }
    
    public int sampleSizeForPower(double targetPower) {
        return points.stream()
            .filter(p -> p.power() >= targetPower)
            .mapToInt(PowerPoint::sampleSize)
            .findFirst()
            .orElse(-1);
    }
}
```
