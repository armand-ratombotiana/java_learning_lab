# Why Causal Inference Exists

Causal inference exists because correlation is not causation — and almost every important business decision requires knowing causation, not correlation. "Does this drug cure the disease?" "Will this ad campaign increase sales?" "Does this policy reduce crime?" These questions require causal answers that observational data alone cannot provide.

## The Gap It Bridges

- **Correlation**: $P(Y|X)$ — given that X happened, what do we observe about Y?
- **Causation**: $P(Y|do(X))$ — if we intervened and set X to a value, what would happen to Y?

The gap between these two is **confounding** — variables that influence both X and Y create non-causal correlations.

```java
// Correlation is easy: just compute Pearson's r
double r = Smile.correlation(adSpend, sales);
// r = 0.72 — but does ad spend cause sales? Or does high sales cause more ad spend?
// Or does a third factor (holiday season) cause both?
```
