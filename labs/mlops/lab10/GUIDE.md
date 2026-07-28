# Lab 10: A/B Testing & Experimentation — Guide

## Step 1: Understand A/B Testing for ML

```
Champion (A) — current production model
Challenger (B) — candidate model

Random split: 50/50 or 90/10
Metrics: accuracy, CTR, conversion, latency

Statistical test: t-test or z-test for proportions
Decision: promote B if p < 0.05 and effect size > MDE
```

## Step 2: Implement ABTest

The `ABTest` class handles:
- Random assignment of users to variants
- Metric collection per variant
- Statistical significance computation (z-test, t-test)
- Confidence interval calculation

## Step 3: Implement MultiArmedBandit

The `MultiArmedBandit` class implements:
- Epsilon-Greedy: explore with probability ε, exploit otherwise
- UCB (Upper Confidence Bound): balance exploration/exploitation
- Thompson Sampling: Bayesian approach with Beta distributions

## Step 4: Compile and Run

```bash
cd lab10/src
javac com/mlops/lab10/*.java
java com.mlops.lab10.ABTestingLab
```

## Key Statistical Formulas

- **Sample size**: n = (Z_α/2 + Z_β)² × 2σ² / δ²
- **Z-test**: Z = (p̂_A - p̂_B) / √(p̂(1-p̂)(1/n_A + 1/n_B))
- **Confidence interval**: p̂ ± Z_α/2 × √(p̂(1-p̂)/n)
- **Minimum detectable effect**: δ = t_α/2 × √(2σ²/n)

## Best Practices
- Decide primary metric before experiment starts
- Calculate required sample size upfront
- Run experiments for sufficient duration (multiple business cycles)
- Monitor guardrail metrics (latency, error rate)
- Use sequential testing to stop early if results are clear
