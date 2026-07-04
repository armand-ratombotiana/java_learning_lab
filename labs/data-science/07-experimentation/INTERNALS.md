# Internals: Power Analysis and Sample Size Calculation

## How Sample Size Is Computed

For a two-sample proportion test:

$$ n = \frac{(Z_{\alpha/2} + Z_\beta)^2 \times [p_1(1-p_1) + p_2(1-p_2)]}{(p_2 - p_1)^2} $$

Where:
- $Z_{\alpha/2}$ = critical value for significance (1.96 for α=0.05)
- $Z_\beta$ = critical value for power (0.84 for β=0.20, power=0.80)
- $p_1$ = baseline conversion rate
- $p_2$ = expected conversion rate (baseline + minimum detectable effect)

```java
public class SampleSizeCalculator {
    public static long requiredSamplePerGroup(double baseline, double mde, 
                                               double alpha, double power) {
        double zAlpha = normalQuantile(1 - alpha / 2);
        double zBeta = normalQuantile(power);
        
        double p1 = baseline;
        double p2 = baseline + mde;
        double pAvg = (p1 + p2) / 2;
        
        double numerator = Math.pow(zAlpha * Math.sqrt(2 * pAvg * (1 - pAvg)) 
                           + zBeta * Math.sqrt(p1 * (1 - p1) + p2 * (1 - p2)), 2);
        double denominator = Math.pow(p2 - p1, 2);
        
        return (long) Math.ceil(numerator / denominator);
    }
    
    public static void main(String[] args) {
        // Baseline conversion = 10%, MDE = 1%, α = 0.05, power = 0.80
        long n = requiredSamplePerGroup(0.10, 0.01, 0.05, 0.80);
        System.out.println("Need " + n + " users per group");  // ~14,000
    }
}
```

## Multiple Testing Correction

### Bonferroni
$$ \alpha_{adjusted} = \frac{\alpha}{m} $$

Where m = number of tests. Very conservative, assumes tests are independent.

### Benjamini-Hochberg (FDR)

Controls the expected proportion of false discoveries among rejected hypotheses.

```java
public class BHCorrection {
    public static double[] adjust(double[] pValues) {
        int m = pValues.length;
        double[] sorted = pValues.clone();
        Arrays.sort(sorted);
        double[] adjusted = new double[m];
        for (int i = 0; i < m; i++) {
            adjusted[i] = sorted[i] * m / (i + 1);
        }
        // Sort back to original order (not shown for brevity)
        return adjusted;
    }
}
```

## CUPED (Variance Reduction)

Uses pre-experiment data to explain variance in the metric:

$$ Y_{cv} = Y - \theta \times (X - \mu_X) $$

Where X is the pre-experiment value of the metric, and θ = Cov(Y, X) / Var(X).
