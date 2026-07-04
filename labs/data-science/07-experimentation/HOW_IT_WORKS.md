# How Experimentation Works

## The A/B Test Lifecycle

```
1. Hypothesis   2. Design        3. Run           4. Analyze      5. Decide
                                    
"What if we     Power analysis   Randomize       Compute p-value  Ship / Kill /
change the      → sample size    → users to      and confidence   Iterate
button color?"                   A or B          interval
```

## Hypothesis Testing Framework

| Component | Definition | Example |
|---|---|---|
| Null hypothesis (H₀) | No effect | Button color doesn't change click rate |
| Alternative (H₁) | There is an effect | New color increases click rate |
| Test statistic | Metric that measures the effect | Difference in click rates |
| p-value | P(observed or more extreme | H₀ true) | 0.03 |
| Significance level (α) | Threshold for rejecting H₀ | 0.05 |
| Power (1-β) | Probability of detecting a true effect | 0.80 |

## Computing the p-value (Two-Sample Proportion Test)

```java
public class TwoProportionZTest {
    public static double test(double n1, double p1, double n2, double p2) {
        // Pooled proportion
        double pPool = (p1 * n1 + p2 * n2) / (n1 + n2);
        // Standard error
        double se = Math.sqrt(pPool * (1 - pPool) * (1/n1 + 1/n2));
        // Z-score
        double z = (p1 - p2) / se;
        // Two-sided p-value
        return 2 * (1 - normalCDF(Math.abs(z)));
    }
}
```

## Confidence Interval

```java
public static double[] confidenceInterval(double p1, double p2, double n1, double n2, double alpha) {
    double diff = p1 - p2;
    double se = Math.sqrt(p1 * (1-p1)/n1 + p2 * (1-p2)/n2);
    double z = 1 - alpha / 2;  // 1.96 for 95% CI
    return new double[]{diff - z * se, diff + z * se};
}
```

## Bayesian Alternative

```java
// Beta-Binomial model: Beta(α₀ + conversions, β₀ + non-conversions)
BetaDistribution control = new BetaDistribution(1 + controlConversions, 1 + controlNonConversions);
BetaDistribution treatment = new BetaDistribution(1 + treatmentConversions, 1 + treatmentNonConversions);
// P(treatment > control)
double probTreatmentWins = simulateComparison(control, treatment, 100000);
```
