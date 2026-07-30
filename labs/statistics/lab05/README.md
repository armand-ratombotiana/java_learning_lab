# Lab 05: Correlation & Regression

## Overview
Correlation measures the strength and direction of a linear relationship between two variables. Regression models the relationship between a dependent variable and one or more independent variables.

## Learning Objectives
- Compute Pearson correlation coefficient (r)
- Compute Spearman rank correlation coefficient (rho)
- Perform simple linear regression (OLS)
- Perform multiple linear regression
- Interpret R² and residuals

## Key Formulas

| Measure | Formula |
|---------|---------|
| Pearson r | r = Σ((xᵢ-x̄)(yᵢ-ȳ)) / √(Σ(xᵢ-x̄)² · Σ(yᵢ-ȳ)²) |
| Spearman rho | same as Pearson but on ranked data |
| Slope (b₁) | b₁ = Σ((xᵢ-x̄)(yᵢ-ȳ)) / Σ(xᵢ-x̄)² |
| Intercept (b₀) | b₀ = ȳ - b₁x̄ |
| R² | SSreg / SStot = 1 - SSres / SStot |

## Running the Code

```bash
javac -d out src/CorrelationAndRegression.java
java -cp out com.statistics.lab05.CorrelationAndRegression
```
