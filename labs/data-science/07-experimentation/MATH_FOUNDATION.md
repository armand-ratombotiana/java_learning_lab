# Math Foundation: Experimentation

## 1. Hypothesis Testing

### Type I Error (α)
False positive: rejecting H₀ when H₀ is true. Usually set to 0.05.

### Type II Error (β)
False negative: failing to reject H₀ when H₁ is true. Usually set to 0.20.

### Power ($1 - \beta$)
Probability of detecting a true effect. Minimum acceptable: 0.80.

## 2. Key Distributions

### Normal Distribution
$$ f(x) = \frac{1}{\sigma\sqrt{2\pi}} e^{-\frac{1}{2}\left(\frac{x-\mu}{\sigma}\right)^2} $$

### t-Distribution
Used when variance is unknown (the usual case). Approaches normal as n → ∞.

### Binomial Distribution
$$ P(X = k) = \binom{n}{k} p^k (1-p)^{n-k} $$

For conversion rates: X = number of conversions, n = sample size, p = conversion probability.

## 3. Delta Method for Ratios

For metrics that are ratios (e.g., revenue per user), the delta method approximates the variance:

$$ Var\left(\frac{Y}{X}\right) \approx \frac{Var(Y)}{E[X]^2} + \frac{E[Y]^2 Var(X)}{E[X]^4} - \frac{2E[Y]}{E[X]^3} Cov(X, Y) $$

## 4. Sequential Testing (Always Valid p-values)

Traditional p-values are invalid under continuous monitoring. Sequential testing uses a spending function to maintain α:

$$ \alpha(t) = \alpha \times t^\rho $$

Where t = information time (fraction of total planned sample), ρ > 0.

## 5. Multi-Armed Bandits

Instead of fixed sample allocation, Thompson Sampling dynamically allocates more users to promising variants:

$$ \text{Allocation} \propto P(\text{treatment} > \text{all others} | \text{data}) $$

```java
// Thompson Sampling: sample from posterior Beta distributions
double thetaA = new BetaDistribution(convA + 1, nonConvA + 1).sample();
double thetaB = new BetaDistribution(convB + 1, nonConvB + 1).sample();
// Allocate user to the variant with higher sampled value
```
