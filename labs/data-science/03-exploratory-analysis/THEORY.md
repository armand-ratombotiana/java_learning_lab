# Exploratory Data Analysis Lab

## Overview
Exploratory Data Analysis (EDA) is the process of analyzing data to summarize main characteristics, often using statistical methods and visualizations. It helps discover patterns, spot anomalies, and form hypotheses.

## 1. Descriptive Statistics

### Measures of Central Tendency
- **Mean**: Average value - `sum / count`
- **Median**: Middle value (sorted) - separates upper/lower halves
- **Mode**: Most frequent value

### Measures of Dispersion
- **Variance**: Average squared deviation from mean
- **Standard Deviation**: Square root of variance
- **Range**: Max - Min
- **Interquartile Range (IQR)**: Q3 - Q1

### Shape Statistics
- **Skewness**: Symmetry of distribution
  - Negative: Left-skewed (long tail left)
  - Zero: Symmetric
  - Positive: Right-skewed (long tail right)
- **Kurtosis**: "Peakedness" of distribution
  - Mesokurtic (normal)
  - Leptokurtic (heavy tails)
  - Platykurtic (light tails)

## 2. Distributions

### Normal Distribution
```
Properties:
- Bell-shaped curve
- Mean = Median = Mode
- Symmetric around center
- 68% within 1 std dev
- 95% within 2 std dev
- 99.7% within 3 std dev
```

### Other Distributions
- **Uniform**: Equal probability all values
- **Exponential**: Long tail, common in waiting times
- **Binomial**: Discrete, fixed trials
- **Poisson**: Discrete events in interval

## 3. Statistical Tests

### Normality Tests
- Shapiro-Wilk test
- Kolmogorov-Smirnov test
- Anderson-Darling test

### Correlation Tests
- Pearson (linear relationship)
- Spearman (rank-based)
- Kendall (concordant pairs)

### Hypothesis Tests
- T-test (compare means)
- Chi-square (categorical)
- ANOVA (compare multiple groups)

## 4. Visualization for EDA

### Distribution Plots
- Histogram
- Density plot
- Box plot
- Violin plot

### Relationship Plots
- Scatter matrix
- Correlation heatmap
- Pair plot

### Comparison Plots
- Grouped box plots
- Strip plots
- Swarm plots