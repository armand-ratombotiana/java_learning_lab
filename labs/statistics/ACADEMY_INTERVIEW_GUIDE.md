# Statistics Academy — Interview Guide

## Preparation Strategy

1. **Master fundamentals** — Lab 01–03 cover the core concepts tested in 80% of interviews
2. **Understand assumptions** — Every statistical test has assumptions (normality, independence, homoscedasticity)
3. **Know when to use which test** — The single most common interview topic
4. **Practice interpretation** — Be ready to read output from Java/statistical software

## Test Selection Flowchart

```
Goal: Compare groups?
  → 2 groups, independent → t-test (parametric) or Mann-Whitney (non-parametric)
  → 2 groups, paired → paired t-test or Wilcoxon signed-rank
  → 3+ groups → ANOVA or Kruskal-Wallis

Goal: Association between variables?
  → Both continuous → Pearson (linear) or Spearman (monotonic)
  → Categorical → Chi-square test of independence

Goal: Predict an outcome?
  → Continuous outcome → Linear regression
  → Binary outcome → Logistic regression

Goal: Test a distribution?
  → Goodness-of-fit → Chi-square or Kolmogorov-Smirnov
```

## Key Formulas to Memorize

| Concept | Formula |
|---------|---------|
| Mean | μ = Σxᵢ / N |
| Variance | σ² = Σ(xᵢ - μ)² / N |
| Z-score | z = (x - μ) / σ |
| t-statistic | t = (x̄ - μ₀) / (s / √n) |
| Correlation | r = Σ((xᵢ-x̄)(yᵢ-ȳ)) / √(Σ(xᵢ-x̄)² Σ(yᵢ-ȳ)²) |
| Bayes Theorem | P(A\|B) = P(B\|A)P(A) / P(B) |
| Cohen's d | d = (x̄₁ - x̄₂) / s_pooled |

## Common Pitfalls

- Confusing p-value with effect size
- Ignoring assumptions before running a test
- Multiple comparisons without correction
- Overfitting regression models
- Using parametric tests on non-normal data
- Misinterpreting confidence intervals

## Behavioral Questions

- "Tell me about a time you used statistics to solve a problem"
- "How do you validate your statistical models?"
- "Describe a situation where your analysis was wrong"
