# Lab 09: Non-parametric Statistics

## Overview
Non-parametric tests make no assumptions about the underlying probability distribution. They work with ranks rather than raw values and are robust to outliers.

## Learning Objectives
- Perform Mann-Whitney U test (two independent samples)
- Perform Wilcoxon signed-rank test (paired samples)
- Perform Kruskal-Wallis H test (k independent samples)
- Perform Friedman test (block design)
- Understand ranking procedures and tie handling

## Key Formulas

| Test | Statistic | Use |
|------|-----------|-----|
| Mann-Whitney U | U = R₁ - n₁(n₁+1)/2 | Two independent groups |
| Wilcoxon signed-rank | W = Σ sign(d) · rank(|d|) | Paired differences |
| Kruskal-Wallis H | H = 12/(N(N+1)) · Σ Rᵢ²/nᵢ - 3(N+1) | k independent groups |
| Friedman | Q = 12/(bk(k+1)) · Σ Rⱼ² - 3b(k+1) | Randomized block design |

## Running the Code

```bash
javac -d out src/NonParametricTests.java
java -cp out com.statistics.lab09.NonParametricTests
```
