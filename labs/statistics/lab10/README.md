# Lab 10: Statistical Power & Effect Size

## Overview
Statistical power is the probability of correctly rejecting a false null hypothesis (1 - β). Effect size measures the magnitude of a phenomenon independent of sample size.

## Learning Objectives
- Compute Cohen's d for mean differences
- Calculate statistical power for various tests
- Determine required sample size for desired power
- Compute minimum detectable effect (MDE)
- Generate power curves

## Key Formulas

| Measure | Formula |
|---------|---------|
| Cohen's d | (μ₁ - μ₂) / σ_pooled |
| Power (means) | Φ(Z_α/2 - Δ/(σ√(2/n))) |
| MDE (means) | Δ = (Z_α/2 + Z_β) · σ · √(2/n) |
| Sample Size | n = (Z_α/2 + Z_β)² · 2σ² / Δ² |

## Running the Code

```bash
javac -d out src/StatisticalPower.java
java -cp out com.statistics.lab10.StatisticalPower
```
