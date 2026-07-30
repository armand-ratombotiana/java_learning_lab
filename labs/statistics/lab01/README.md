# Lab 01: Descriptive Statistics

## Overview
Descriptive statistics summarize and describe the main features of a dataset. They provide simple summaries about the sample and measures.

## Learning Objectives
- Calculate measures of central tendency (mean, median, mode)
- Calculate measures of dispersion (variance, standard deviation)
- Compute quartiles and interquartile range (IQR)
- Understand the distribution shape through summary statistics
- Implement all computations in Java 21+

## Key Formulas

| Measure | Formula |
|---------|---------|
| Mean | μ = Σxᵢ / N |
| Variance (population) | σ² = Σ(xᵢ - μ)² / N |
| Variance (sample) | s² = Σ(xᵢ - x̄)² / (n-1) |
| Standard Deviation | σ = √σ² |
| IQR | Q3 - Q1 |

## Running the Code

```bash
javac -d out src/DescriptiveStatistics.java
java -cp out com.statistics.lab01.DescriptiveStatistics
```
