# Probability - Code Deep Dive

## Using SciPy for Probability Distributions

```python
import numpy as np
from scipy import stats
from scipy.special import comb

# === DISCRETE DISTRIBUTIONS ===

# Bernoulli
p = 0.7
dist = stats.bernoulli(p)
samples = dist.rvs(1000)  # Random samples
pmf = dist.pmf([0, 1])  # Probability mass function

# Binomial
n, p = 10, 0.5
dist = stats.binom(n, p)
prob = dist.pmf(5)  # P(X=5)
cdf = dist.cdf(3)  # P(X≤3)
samples = dist.rvs(1000)

# Poisson
lambda_param = 3
dist = stats.poisson(lambda_param)
prob = dist.pmf(2)  # P(X=2)
samples = dist.rvs(1000)

# === CONTINUOUS DISTRIBUTIONS ===

# Uniform
a, b = 0, 1
dist = stats.uniform(a, b-a)
pdf = dist.pdf(0.5)  # Density at 0.5
cdf = dist.cdf(0.5)  # P(X≤0.5)
samples = dist.rvs(1000)

# Exponential
lambda_param = 1
dist = stats.expon(scale=1/lambda_param)
pdf = dist.pdf(1)
samples = dist.rvs(1000)

# Gaussian (Normal)
mu, sigma = 0, 1
dist = stats.norm(mu, sigma)
pdf = dist.pdf(0)  # 0.3989...
cdf = dist.cdf(0)  # 0.5
samples = dist.rvs(1000)
quantile = dist.ppf(0.95)  # 1.645

# Multivariate Normal
mu = np.array([0, 0])
cov = np.array([[1, 0.5], [0.5, 1]])
dist = stats.multivariate_normal(mu, cov)
samples = dist.rvs(1000)
pdf = dist.pdf([0, 0])

# Beta (for Bayesian inference)
alpha, beta = 2, 5
dist = stats.beta(alpha, beta)
samples = dist.rvs(1000)

# Dirichlet (multivariate Beta)
alpha = np.array([1, 2, 3])
dist = stats.dirichlet(alpha)
samples = dist.rvs(10)

# === STATISTICAL TESTS ===

# Kolmogorov-Smirnov test
data = stats.norm.rvs(100)
ks_stat, p_value = stats.kstest(data, 'norm')

# Chi-squared test for categorical data
observed = np.array([10, 20, 30])
chi2, p_value = stats.chisquare(observed)

# T-test
group1 = stats.norm.rvs(100)
group2 = stats.norm.rvs(100, loc=1)
t_stat, p_value = stats.ttest_ind(group1, group2)

# === BAYESIAN INFERENCE ===

# Simple Bayesian update for Bernoulli
prior_alpha, prior_beta = 1, 1  # Beta(1,1) = Uniform
data = [1, 1, 0, 1, 1]  # Observed data

# Update with conjugate prior
posterior_alpha = prior_alpha + sum(data)
posterior_beta = prior_beta + len(data) - sum(data)

# Posterior distribution
posterior = stats.beta(posterior_alpha, posterior_beta)
mean = posterior.mean()  # Posterior mean
credible_interval = posterior.ppf([0.025, 0.975])

# === MONTE CARLO SIMULATION ===

# Estimate pi
n_samples = 1000000
points = np.random.rand(n_samples, 2)
in_circle = (points[:,0]**2 + points[:,1]**2) <= 1
pi_estimate = 4 * sum(in_circle) / n_samples

# Importance sampling
def target(x):
    return stats.norm.pdf(x, 0, 1)

def proposal(x):
    return stats.norm.pdf(x, 2, 2)

samples = stats.norm.rvs(2, 2, 10000)
weights = target(samples) / proposal(samples)
mean_estimate = np.sum(weights * samples) / np.sum(weights)
```

## Custom Distributions

```python
from scipy.stats import rv_continuous

class CustomDistribution(rv_continuous):
    def _pdf(self, x):
        return np.exp(-x**2 / 2)  # Unnormalized

custom = CustomDistribution()
samples = custom.rvs(1000)
```

## Numerical Stability

```python
# Use log-probabilities for numerical stability
log_prob = sum([np.log(p) for p in probabilities])

# Softmax with numerical stability
def stable_softmax(x):
    x_shifted = x - np.max(x)
    return np.exp(x_shifted) / np.sum(np.exp(x_shifted))
```