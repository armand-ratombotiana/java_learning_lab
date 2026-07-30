# Lab 06: Bayesian Statistics

## Overview
Bayesian statistics interprets probability as a degree of belief. Prior beliefs are updated with observed data via Bayes' theorem to produce a posterior distribution.

## Learning Objectives
- Understand Bayes' theorem: P(θ|D) = P(D|θ)P(θ) / P(D)
- Work with conjugate priors (Beta-Binomial)
- Compute posterior distributions
- Perform Bayesian A/B testing
- Compute credible intervals

## Key Formulas

| Concept | Formula |
|---------|---------|
| Bayes Theorem | P(θ|D) = P(D|θ)P(θ) / ∫P(D|θ)P(θ)dθ |
| Beta-Binomial Prior | Beta(α, β) |
| Beta-Binomial Posterior | Beta(α + k, β + n - k) |
| Credible Interval | interval containing 95% posterior probability |

## Running the Code

```bash
javac -d out src/BayesianStatistics.java
java -cp out com.statistics.lab06.BayesianStatistics
```
