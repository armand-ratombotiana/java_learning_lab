# Statistics Interview Cheatsheet — One-Page Reference

## Distributions
| Distribution | Parameters | Mean | Variance | Use |
|-------------|------------|------|----------|-----|
| Normal | μ, σ | μ | σ² | Continuous data, CLT |
| Binomial | n, p | np | np(1-p) | Count of successes |
| Poisson | λ | λ | λ | Rare events count |
| Exponential | λ | 1/λ | 1/λ² | Time between events |

## Hypothesis Tests
| Test | Data Type | Assumptions | Null Hypothesis |
|------|-----------|-------------|-----------------|
| One-sample t-test | Continuous | Normality | μ = μ₀ |
| Two-sample t-test | Continuous | Normality, equal var | μ₁ = μ₂ |
| Paired t-test | Continuous (paired) | Normality of diff | μ_d = 0 |
| Z-test | Continuous | Known σ, large n | μ = μ₀ |
| Chi-square | Categorical | Expected ≥5 | Independence |
| ANOVA | Continuous | Normality, equal var | All μ equal |
| Mann-Whitney | Ordinal/Continuous | Independence | Distributions equal |
| Wilcoxon | Ordinal/Continuous (paired) | Symmetry | Distributions equal |

## Effect Sizes
| Measure | Formula | Interpretation |
|---------|---------|----------------|
| Cohen's d | (x̄₁−x̄₂)/s_pooled | 0.2 small, 0.5 medium, 0.8 large |
| Pearson's r | Cov/σₓσᵧ | 0.1 small, 0.3 medium, 0.5 large |
| η² | SS_between/SS_total | Variance explained |
| Odds ratio | (a/b)/(c/d) | 1 = no effect |

## Common Pitfalls
- p-hacking (multiple testing without correction)
- Base rate fallacy (ignoring prior probability)
- Confusing practical vs statistical significance
- Ignoring assumptions of the test
- Simpson's Paradox (aggregated vs stratified results)
- Survivorship bias in observational data

## Quick Power Formula
```
n ≈ (Z_α/2 + Z_β)² * 2σ² / d²
Where:
  Z_α/2 = critical value (1.96 for α=0.05)
  Z_β = power (0.84 for 80% power)
  d = minimum detectable effect
  σ = population standard deviation
```
