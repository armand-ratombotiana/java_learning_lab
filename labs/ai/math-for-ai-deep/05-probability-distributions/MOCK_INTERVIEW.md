# Mock Interview: Probability Distributions

**Topic:** Explain key distributions and MLE for each

## Core Questions

### Q1: Bernoulli & Binomial distributions.

**Answer:**
**Bernoulli($p$):** Single trial, $x \in \{0, 1\}$
- PMF: $P(x) = p^x (1-p)^{1-x}$
- Mean: $p$, Variance: $p(1-p)$
- **MLE:** $\hat{p} = \frac{1}{n} \sum x_i$ (sample mean)

**Binomial($n, p$):** Sum of $n$ independent Bernoulli($p$)
- PMF: $P(k) = \binom{n}{k} p^k (1-p)^{n-k}$
- Mean: $np$, Variance: $np(1-p)$

### Q2: Gaussian (Normal) distribution.

**Answer:**
$N(\mu, \sigma^2)$: $f(x) = \frac{1}{\sqrt{2\pi\sigma^2}} \exp\left(-\frac{(x-\mu)^2}{2\sigma^2}\right)$

**MLE:**
- $\hat{\mu} = \frac{1}{n} \sum x_i$ (sample mean)
- $\hat{\sigma}^2 = \frac{1}{n} \sum (x_i - \hat{\mu})^2$ (biased sample variance; often use $n-1$ for unbiased)

**Why Gaussian?** Central Limit Theorem — sum of independent random variables converges to Gaussian. Used for noise models, error distributions, initialization.

### Q3: Categorical & Multinomial.

**Answer:**
**Categorical($\theta$):** $K$ outcomes with probabilities $\theta_1, \ldots, \theta_K$
- PMF: $P(x) = \prod_{k=1}^K \theta_k^{\mathbb{1}[x=k]}$
- **MLE:** $\hat{\theta}_k = \frac{n_k}{n}$ where $n_k$ = count of category $k$

**Multinomial($n, \theta$):** Counts over $K$ categories from $n$ trials
- PMF: $P(n_1, \ldots, n_K) = \frac{n!}{n_1! \cdots n_K!} \prod \theta_k^{n_k}$
- **MLE:** same: $\hat{\theta}_k = n_k / n$

Connection to cross-entropy loss: minimizing cross-entropy $\iff$ MLE of categorical distribution.

### Q4: Exponential & Poisson.

**Answer:**
**Exponential($\lambda$):** Time between events
- PDF: $f(x) = \lambda e^{-\lambda x}$, $x \ge 0$
- Mean: $1/\lambda$, Variance: $1/\lambda^2$
- **MLE:** $\hat{\lambda} = 1 / \bar{x}$

**Memoryless property:** $P(X > s + t \mid X > t) = P(X > s)$

**Poisson($\lambda$):** Count of events in fixed interval
- PMF: $P(k) = \frac{e^{-\lambda} \lambda^k}{k!}$
- Mean: $\lambda$, Variance: $\lambda$
- **MLE:** $\hat{\lambda} = \bar{x}$

### Q5: Beta & Dirichlet (conjugate priors).

**Answer:**
**Beta($\alpha, \beta$):** Prior for Bernoulli/Binomial
- PDF: $f(p) \propto p^{\alpha-1} (1-p)^{\beta-1}$
- Mean: $\alpha / (\alpha + \beta)$
- Posterior: Beta($\alpha + n_1, \beta + n_0$)

**Dirichlet($\alpha$):** Prior for Categorical/Multinomial
- PDF: $f(\theta) \propto \prod \theta_k^{\alpha_k - 1}$
- Posterior: Dirichlet($\alpha_1 + n_1, \ldots, \alpha_K + n_K$)

### Q6: MLE general principle.

**Answer:**
MLE: $\hat{\theta} = \arg\max_\theta \prod_{i=1}^n p(x_i \mid \theta)$

Equivalently: $\hat{\theta} = \arg\max_\theta \sum_{i=1}^n \log p(x_i \mid \theta)$

Properties:
- **Consistent:** Converges to true $\theta$ as $n \to \infty$
- **Asymptotically normal:** $\hat{\theta} \approx N(\theta, I^{-1}(\theta)/n)$
- **Efficient:** Achieves Cramér-Rao lower bound asymptotically
- **Invariant:** MLE of $g(\theta)$ is $g(\hat{\theta})$

## Advanced

- **Exponential family:** $p(x|\theta) = h(x) \exp(\eta(\theta)^T T(x) - A(\theta))$ — includes most common distributions
- **Mixture models:** $p(x) = \sum \pi_k p_k(x \mid \theta_k)$ — no closed-form MLE, use EM
- **Cramér-Rao bound:** $\text{Var}(\hat{\theta}) \ge 1 / I(\theta)$ where $I(\theta)$ is Fisher information
