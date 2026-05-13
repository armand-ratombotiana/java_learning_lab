# Probability - Exercises

## Exercise 1: Conditional Probability
```python
# Task: Implement Bayesian inference from scratch

# Given:
# P(Disease) = 0.01 (1% prevalence)
# P(Positive|Disease) = 0.99 (sensitivity)
# P(Positive|No Disease) = 0.05 (false positive rate)

# Calculate: P(Disease|Positive) = ?

def bayes_inference():
    p_disease = 0.01
    p_positive_given_disease = 0.99
    p_positive_given_no_disease = 0.05
    p_no_disease = 1 - p_disease

    # P(Positive) = P(Positive|Disease)*P(Disease) + P(Positive|No Disease)*P(No Disease)
    p_positive = (p_positive_given_disease * p_disease +
                  p_positive_given_no_disease * p_no_disease)

    # P(Disease|Positive) = P(Positive|Disease)*P(Disease) / P(Positive)
    p_disease_given_positive = (p_positive_given_disease * p_disease) / p_positive

    return p_disease_given_positive
```

## Exercise 2: Distribution Fitting
```python
# Task: Fit distributions to data and perform goodness-of-fit test
import numpy as np
from scipy import stats

def fit_distributions(data):
    results = {}

    # Fit normal distribution
    mu, sigma = stats.norm.fit(data)
    results['normal'] = {'mu': mu, 'sigma': sigma}

    # Fit exponential
    loc, scale = stats.expon.fit(data)
    results['exponential'] = {'loc': loc, 'scale': scale}

    # KS test for normality
    ks_stat, p_value = stats.kstest(data, 'norm', args=(mu, sigma))
    results['normal_ks_test'] = {'statistic': ks_stat, 'p_value': p_value}

    return results
```

## Exercise 3: Entropy Calculation
```python
def calculate_entropy():
    # Binary entropy for different probabilities
    p_vals = [0.01, 0.1, 0.3, 0.5, 0.7, 0.9, 0.99]

    entropies = []
    for p in p_vals:
        h = -p * np.log2(p) - (1-p) * np.log2(1-p)
        entropies.append(h)

    # Maximum entropy is 1 bit at p=0.5
    return p_vals, entropies
```

## Exercise 4: MLE Implementation
```python
# Task: Implement MLE for different distributions

def mle_gaussian(data):
    """MLE for Gaussian: μ = mean, σ² = variance"""
    n = len(data)
    mu_hat = sum(data) / n
    sigma_sq_hat = sum((x - mu_hat)**2 for x in data) / n
    return mu_hat, np.sqrt(sigma_sq_hat)

def mle_bernoulli(data):
    """MLE for Bernoulli: p = successes / trials"""
    return sum(data) / len(data)

def mle_poisson(data):
    """MLE for Poisson: λ = mean"""
    return sum(data) / len(data)
```

## Exercise 5: Monte Carlo Integration
```python
def monte_carlo_integration():
    # Estimate ∫₀¹ x² dx = 1/3
    n = 100000
    x = np.random.rand(n)
    y = x**2
    estimate = np.mean(y)

    # With importance sampling
    # Use exponential proposal: x = -log(u) where u~Uniform(0,1)
    u = np.random.rand(n)
    x = -np.log(u)
    y = x**2 * np.exp(-x)  # f(x)/g(x)
    estimate_is = np.mean(y)

    return estimate, estimate_is
```

## Exercise 6: Gaussian Distribution
```python
def multivariate_gaussian():
    # 2D Gaussian parameters
    mu = np.array([0, 0])
    sigma = np.array([[1, 0.5], [0.5, 1]])
    rho = 0.5

    # Generate samples
    samples = np.random.multivariate_normal(mu, sigma, 1000)

    # Compute correlation from samples
    computed_corr = np.corrcoef(samples[:, 0], samples[:, 1])[0, 1]

    # PDF at specific point
    from scipy.stats import multivariate_normal
    pdf_val = multivariate_normal.pdf([0, 0], mu, sigma)

    return samples, computed_corr, pdf_val
```

## Advanced Challenges
1. Implement EM algorithm for Gaussian Mixture Models
2. Build a simple Bayesian classifier
3. Implement Metropolis-Hastings sampler
4. Calculate KL divergence between two distributions