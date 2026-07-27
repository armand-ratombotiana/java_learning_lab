# Mock Interview: Bayesian Inference

**Topic:** Explain Bayesian vs Frequentist — MLE vs MAP — with examples

## Core Questions

### Q1: What is the fundamental difference between Bayesian and Frequentist statistics?

**Answer:**
| Aspect | Frequentist | Bayesian |
|--------|------------|----------|
| **Probability** | Long-run frequency of events | Degree of belief |
| **Parameters** | Fixed (unknown) constants | Random variables (have distributions) |
| **Data** | Random (repeatable) | Fixed (observed) |
| **Inference** | $P(\text{data} \mid \theta)$ | $P(\theta \mid \text{data})$ |
| **Prior** | Not allowed | Required |
| **Uncertainty** | Confidence intervals | Credible intervals |

**Key equation:** Bayes' theorem

$P(\theta \mid D) = \frac{P(D \mid \theta) P(\theta)}{P(D)} \propto \text{Likelihood} \times \text{Prior}$

### Q2: MLE vs. MAP.

**Answer:**
**MLE (Maximum Likelihood Estimation):**
$\hat{\theta}_{MLE} = \arg\max_\theta P(D \mid \theta)$

**MAP (Maximum A Posteriori):**
$\hat{\theta}_{MAP} = \arg\max_\theta P(\theta \mid D) = \arg\max_\theta [\log P(D \mid \theta) + \log P(\theta)]$

**Relationship:**
- MAP = MLE + prior regularization
- As $n \to \infty$, MAP → MLE (data dominates prior)
- MAP with uniform prior = MLE
- Gaussian prior on weights → L2 regularization (Ridge regression)
- Laplace prior on weights → L1 regularization (Lasso regression)

### Q3: Derive MAP for linear regression with Gaussian prior.

**Answer:**
Likelihood: $y \mid X, w, \sigma^2 \sim N(Xw, \sigma^2 I)$

Prior: $w \sim N(0, \tau^2 I)$

Posterior: $P(w \mid X, y) \propto \exp\left(-\frac{1}{2\sigma^2}\|y - Xw\|^2\right) \exp\left(-\frac{1}{2\tau^2}\|w\|^2\right)$

MAP: $\hat{w}_{MAP} = \arg\min_w \frac{1}{\sigma^2}\|y - Xw\|^2 + \frac{1}{\tau^2}\|w\|^2$

= Ridge regression with $\lambda = \sigma^2 / \tau^2$

### Q4: Bayesian inference example — coin flipping.

**Answer:**
**Data:** Observed $n$ flips, $k$ heads.

**Likelihood:** $k \sim \text{Binomial}(n, \theta)$

**Prior:** $\theta \sim \text{Beta}(\alpha, \beta)$

**Posterior:** $\theta \mid k \sim \text{Beta}(\alpha + k, \beta + n - k)$

**Predictive (next flip):**
$P(\text{heads} \mid k) = \frac{\alpha + k}{\alpha + \beta + n}$

For large $n$, this approaches $k/n$ (MLE). For small $n$, prior pulls toward $\alpha/(\alpha+\beta)$.

### Q5: When would you prefer Bayesian vs. Frequentist?

**Answer:**
**Prefer Bayesian:**
- Strong prior knowledge available
- Need full uncertainty distribution (not just point estimate)
- Small data / sparse settings (prior helps)
- Sequential decision making (posterior today = prior tomorrow)
- Hierarchical modeling (e.g., multi-task learning)

**Prefer Frequentist:**
- No reliable prior
- Need computational efficiency
- Large data (Bayesian computational complexity)
- Need frequentist guarantees (coverage of confidence intervals)
- Objectivity concerns (prior can be subjective)

## Advanced

- **Conjugate priors:** Prior and posterior in same family — closed-form updates
- **Bayesian model averaging:** $P(y \mid D) = \int P(y \mid \theta) P(\theta \mid D) d\theta$ — naturally avoids overfitting
- **MCMC sampling:** When posterior is intractable (e.g., complex NNs) — Gibbs, HMC, NUTS
- **Bayesian neural networks:** Priors on weights, predict via posterior. Variational inference (VI) for tractability.
