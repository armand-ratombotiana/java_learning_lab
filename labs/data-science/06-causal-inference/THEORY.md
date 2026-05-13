# Causal Inference Lab

## Overview
Causal inference is the process of determining the effect of a cause (treatment) on an outcome.

## 1. Key Concepts

### Treatment Effect
- **ATE**: Average Treatment Effect = E[Y(1)] - E[Y(0)]
- **ATT**: Average Treatment Effect on Treated
- **ITE**: Individual Treatment Effect

### Potential Outcomes
- Y(1): Outcome if treated
- Y(0): Outcome if not treated
- Only one observed, other is counterfactual

### Causal Graph
Nodes: Treatment (T), Outcome (Y), Covariates (X), Confounders (C)

## 2. Identification Assumptions

### Ignorability
(T ⊥ Y(0), Y(1) | X) - Treatment assignment independent of potential outcomes given X

### Positivity
P(T=1|X) > 0 for all X with positive density

### SUTVA
- No interference (stable unit treatment value assumption)
- No hidden treatment versions

## 3. Methods

### Selection on Observables
- Stratification / Subclassification
- Matching (exact, propensity, nearest neighbor)
- Inverse Probability Weighting (IPW)
- G-computation

### Instrumental Variables
- Two-Stage Least Squares
- Local Average Treatment Effect (LATE)

### Difference in Differences
- Pre/post comparison with control group

### Regression Discontinuity
- Sharp RD
- Fuzzy RD

## 4. Estimation

### Propensity Score
P(T=1|X) - probability of treatment given covariates

### IPW Estimator
ATE = E[ (T*Y) / e(X) - ((1-T)*Y) / (1-e(X)) ]

### Matching Estimator
Match treated units to similar untreated units