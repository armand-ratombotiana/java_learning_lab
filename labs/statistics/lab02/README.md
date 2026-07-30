# Lab 02: Probability Distributions

## Overview
Probability distributions describe how probabilities are assigned to different outcomes. This lab covers Normal, Binomial, Poisson, and Exponential distributions.

## Learning Objectives
- Understand PDF (probability density function) and CDF (cumulative distribution function)
- Implement common distributions in Java
- Generate random samples from each distribution
- Understand when to apply each distribution

## Distributions Covered

| Distribution | Parameters | Type | Use Case |
|-------------|------------|------|----------|
| Normal | μ, σ | Continuous | Natural phenomena, CLT |
| Binomial | n, p | Discrete | Number of successes |
| Poisson | λ | Discrete | Rare events count |
| Exponential | λ | Continuous | Time between events |

## Running the Code

```bash
javac -d out src/ProbabilityDistributions.java
java -cp out com.statistics.lab02.ProbabilityDistributions
```
