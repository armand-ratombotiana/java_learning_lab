# Experimentation Lab

## Overview
Experimentation involves designing, conducting, and analyzing experiments to make data-driven decisions.

## 1. Hypothesis Testing

### Null Hypothesis (H0)
- No effect or no difference
- Status quo
- Burden of proof on alternative

### Alternative Hypothesis (H1)
- There is an effect or difference
- What we want to prove
- Research hypothesis

### Types of Errors
- Type I (α): False positive - reject H0 when true
- Type II (β): False negative - fail to reject H0 when false

## 2. Test Statistics

### Z-Test
- Known variance
- Large samples (n > 30)

### T-Test
- Unknown variance
- Small samples
- One-sample, two-sample, paired

### Chi-Square Test
- Categorical data
- Goodness of fit
- Independence

### ANOVA
- Compare 3+ group means
- F-distribution

## 3. P-Values

### Definition
- Probability of observing data if H0 is true
- Smaller p-value = stronger evidence against H0

### Interpretation
- p < 0.05: Significant at 5% level
- p < 0.01: Significant at 1% level
- p > 0.05: Not significant

## 4. Power Analysis

### Components
- Effect size (d)
- Significance level (α)
- Power (1 - β)
- Sample size (n)

### Trade-offs
- Larger effect → smaller n needed
- Higher power → larger n needed
- Lower α → larger n needed

## 5. Multiple Testing

### Problem
- More tests → more false positives
- Family-wise error rate (FWER)

### Solutions
- Bonferroni correction
- Benjamini-Hochberg (FDR)
- Tukey's HSD

## 6. Design of Experiments

### Completely Randomized
- Random assignment to treatments

### Randomized Block
- Homogeneous blocks
- Reduces within-block variation

### Factorial Design
- Multiple factors
- All combinations

### Split-Plot
- Whole plots and subplots
- Hierarchical treatments