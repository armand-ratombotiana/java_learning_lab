# Real World Project: Bayesian A/B Testing

## Project Overview
Implement a Bayesian approach to A/B testing for web experiments.

## Background
Bayesian A/B testing provides probabilistic interpretation of experiment results and allows for early stopping.

## Implementation

### 1. Define Prior Distributions
```python
from scipy import stats
import numpy as np

# Beta prior for conversion rate (Bernoulli likelihood)
prior_alpha = 1  # Uninformative prior
prior_beta = 1
```

### 2. Update with Observed Data
```python
def posterior_beta(alpha_prior, beta_prior, successes, trials):
    """Update Beta prior with observed data"""
    return alpha_prior + successes, beta_prior + (trials - successes)

# Example: A/B test results
# Variant A: 1000 visitors, 50 conversions
# Variant B: 1000 visitors, 65 conversions

posterior_A = posterior_beta(1, 1, 50, 1000)
posterior_B = posterior_beta(1, 1, 65, 1000)
```

### 3. Calculate Probabilities
```python
def calculate_winner_probs(posterior_A, posterior_B, n_samples=100000):
    """Monte Carlo estimation of winning probability"""
    samples_A = stats.beta.rvs(posterior_A[0], posterior_A[1], n_samples)
    samples_B = stats.beta.rvs(posterior_B[0], posterior_B[1], n_samples)

    prob_A_wins = np.mean(samples_A > samples_B)
    prob_B_wins = np.mean(samples_B > samples_A)

    return prob_A_wins, prob_B_wins
```

### 4. Expected Loss
```python
def expected_loss(posterior_A, posterior_B, n_samples=100000):
    """Calculate expected loss of choosing wrong variant"""
    samples_A = stats.beta.rvs(posterior_A[0], posterior_A[1], n_samples)
    samples_B = stats.beta.rvs(posterior_B[0], posterior_B[1], n_samples)

    loss_A = np.mean(np.maximum(samples_B - samples_A, 0))
    loss_B = np.mean(np.maximum(samples_A - samples_B, 0))

    return loss_A, loss_B
```

### 5. Sequential Testing Implementation
```python
class BayesianABTest:
    def __init__(self, prior_alpha=1, prior_beta=1):
        self.prior = (prior_alpha, prior_beta)
        self.posterior_A = [prior_alpha, prior_beta]
        self.posterior_B = [prior_alpha, prior_beta]

    def update(self, conversions_A, visitors_A, conversions_B, visitors_B):
        self.posterior_A = posterior_beta(*self.prior, conversions_A, visitors_A)
        self.posterior_B = posterior_beta(*self.prior, conversions_B, visitors_B)

    def get_results(self):
        prob_A, prob_B = calculate_winner_probs(self.posterior_A, self.posterior_B)
        loss_A, loss_B = expected_loss(self.posterior_A, self.posterior_B)

        return {
            'prob_A_better': prob_A,
            'prob_B_better': prob_B,
            'expected_loss_A': loss_A,
            'expected_loss_B': loss_B,
            'expected_improvement': prob_B - 0.5
        }
```

### 6. Visualization
```python
import matplotlib.pyplot as plt

def plot_posteriors(posterior_A, posterior_B):
    x = np.linspace(0, 0.2, 1000)
    plt.plot(x, stats.beta.pdf(x, *posterior_A), label='Variant A')
    plt.plot(x, stats.beta.pdf(x, *posterior_B), label='Variant B')
    plt.xlabel('Conversion Rate')
    plt.ylabel('Density')
    plt.legend()
    plt.show()
```

## Deliverables
- Complete Bayesian A/B testing framework
- Interactive dashboard for experiment monitoring
- Comparison with frequentist approach