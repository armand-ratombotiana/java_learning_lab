# Math Foundation of Statistics

## Prerequisites

- Probability theory: distributions, expectation, variance
- Calculus: optimization (MLE requires derivatives)
- Linear algebra: regression uses matrix notation

## Key Theorems

### Law of Large Numbers
$$
\bar{X}_n \xrightarrow{p} \mu \quad \text{as } n \to \infty
$$

### Central Limit Theorem
$$
\sqrt{n}(\bar{X}_n - \mu) \xrightarrow{d} \mathcal{N}(0, \sigma^2)
$$

### Chebyshev's Inequality
$$
P(|X - \mu| \ge k\sigma) \le \frac{1}{k^2}
$$

## Important Distributions

| Distribution | Notation | Used For |
|-------------|----------|----------|
| Normal | $\mathcal{N}(\mu, \sigma^2)$ | Many natural phenomena |
| $t$-distribution | $t_k$ | Small sample inference |
| $\chi^2$ | $\chi^2_k$ | Goodness of fit |
| $F$-distribution | $F_{d_1,d_2}$ | ANOVA, comparing variances |

## Maximum Likelihood Estimation

$$
\hat{\theta}_{\text{MLE}} = \arg\max_\theta \prod_{i=1}^n f(x_i \mid \theta)
$$

Often solved by maximizing the log-likelihood:

$$
\ell(\theta) = \sum_{i=1}^n \log f(x_i \mid \theta)
$$
