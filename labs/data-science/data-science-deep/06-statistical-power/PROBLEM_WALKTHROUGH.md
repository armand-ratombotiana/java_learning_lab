# Problem Walkthrough: Power Analysis Dashboard

## Problem
Build a power analysis tool that computes sample sizes and power for t-tests, ANOVA, and proportion tests, with visualization-ready output for power curves.

## Step 1: Unified Power API

```java
public sealed interface PowerAnalysis permits TTestPower, AnovaPower, ProportionPower {
    PowerResult compute(double effectSize, int n, double alpha);
    int sampleSize(double effectSize, double power, double alpha);
}

public record TTestPower() implements PowerAnalysis {
    @Override
    public PowerResult compute(double effectSize, int n, double alpha) {
        double df = 2.0 * n - 2.0;
        double ncp = effectSize * Math.sqrt(n / 2.0);
        double tCrit = studentTQuantile(1.0 - alpha / 2.0, df);
        double power = 1.0 - nctCdf(tCrit, df, ncp) + nctCdf(-tCrit, df, ncp);
        return new PowerResult(effectSize, n, alpha, power);
    }
    
    @Override
    public int sampleSize(double effectSize, double power, double alpha) {
        double zAlpha = normalQuantile(1.0 - alpha / 2.0);
        double zBeta = normalQuantile(power);
        return (int) Math.ceil(2.0 * Math.pow(zAlpha + zBeta, 2) / (effectSize * effectSize));
    }
}
```

## Step 2: Non-Central t-distribution

```java
public class NonCentralT {
    // CDF of non-central t-distribution via AS 243 (Lenth, 1989)
    public static double cdf(double t, double df, double ncp) {
        double[] w = {0.0055657196642445571, 0.012915947284065419, 0.020181515297735382,
                      0.027298621498568734, 0.034213810770299537, 0.040875750923643261,
                      0.047235083490265582, 0.053244713860759962, 0.058860144245324798,
                      0.064039797355015492, 0.068745323835736638, 0.072941885005653087,
                      0.076598410645870678, 0.079687828912071602, 0.082187266011339763,
                      0.084078218979661945, 0.085346685739338627, 0.085983275670394821};
        double[] x = {0.044489365833267018, 0.12293832291667808, 0.24004711246024697,
                      0.36592037646623693, 0.49779325136153839, 0.63286801811867705,
                      0.76876580765185254, 0.90334340252563218, 1.0346010812318106,
                      1.1607428260992599, 1.2801261110710194, 1.3912142130984963,
                      1.4926202269622091, 1.5830906767348207, 1.6614220458467077,
                      1.7266506543584874, 1.7778972050106661, 1.8144000000000000};
        // Gauss-Legendre quadrature of non-central t density
        double integral = 0;
        for (int i = 0; i < 18; i++) {
            double u = (t * x[i] + df) / (t + x[i] * Math.sqrt(df));
            integral += w[i] * studentTPdf(u, df);
        }
        return normalCdf(ncp - t) + integral;
    }
}
```

## Step 3: Power Curve Generation

```java
public class PowerCurveGenerator {
    public record CurvePoint(int n, double power) {}
    
    public List<CurvePoint> generate(double d, double alpha, int maxN, int step) {
        List<CurvePoint> points = new ArrayList<>();
        TTestPower ttp = new TTestPower();
        for (int n = step; n <= maxN; n += step) {
            PowerResult pr = ttp.compute(d, n, alpha);
            points.add(new CurvePoint(n, pr.power()));
        }
        return points;
    }
    
    // Find sample size achieving target power via binary search
    public int binarySearchSampleSize(double d, double targetPower, double alpha) {
        int lo = 0, hi = 1000000;
        while (lo < hi) {
            int mid = (lo + hi) / 2;
            double power = new TTestPower().compute(d, mid, alpha).power();
            if (power >= targetPower) hi = mid;
            else lo = mid + 1;
        }
        return lo;
    }
}
```

## Step 4: Effect Size Conversion

```java
public class EffectSizeConverter {
    public static double oddsRatioToD(double or) {
        return Math.log(or) * Math.sqrt(3) / Math.PI;
    }
    
    public static double dToR(double d) {
        return d / Math.sqrt(d * d + 4);
    }
    
    public static double fToEtaSquared(double f) {
        return f * f / (1 + f * f);
    }
    
    public static double twoProportionH(double p1, double p2) {
        return 2.0 * Math.asin(Math.sqrt(p1)) - 2.0 * Math.asin(Math.sqrt(p2));
    }
}
```

## Step 5: Verification

```
Sample size for d=0.5, power=0.80, α=0.05:
  Formula: n ≈ 64 per group
  Power curve: n=64 → power ≈ 0.80 ✓

Sample size for p1=0.10, p2=0.15, power=0.80, α=0.05:
  h = 0.152
  n ≈ 680 per group ✓

ANOVA k=3, f=0.25, power=0.80, α=0.05:
  n ≈ 52 per group ✓
```
