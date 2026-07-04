# Mental Models for Experimentation

## 1. The Two Groups Model

```
Population → Randomize → Treatment Group (new feature, n=5000)
                       → Control Group (status quo, n=5000)
```

Randomization ensures that any difference in outcomes between groups is caused by the treatment, not by pre-existing differences.

## 2. The Signal-to-Noise Ratio

$$
\text{Signal-to-Noise} = \frac{\text{Treatment Effect}}{\text{Variance}}
$$

- **Signal**: the true effect of the treatment (unknown)
- **Noise**: natural variance in the metric across users
- **Increasing sample size** reduces noise (standard error ∝ 1/√n)
- **CUPED** reduces noise by controlling for pre-experiment metrics

## 3. The Multiple Testing Penalty

If you test 20 metrics on the same experiment, you expect 1 to be "significant" at p < 0.05 by chance. Bonferroni correction divides α by the number of tests. False Discovery Rate (FDR) control is less conservative.

## 4. The Peeking Problem

Checking results every day and stopping when p < 0.05 inflates the false-positive rate to ~30% (not 5%). This is because each look is another chance to hit the significance threshold.

## 5. The Sample Size Triad

```
Sample Size × Effect Size × Power = Significance
```

For a given significance level (α = 0.05), you need:
- **Large effect** → small sample
- **Small effect** → large sample
- **High variance** → larger sample
- **More power** (1-β) → larger sample
