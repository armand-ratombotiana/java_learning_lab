# Lab 03: Hypothesis Testing

## Overview
Hypothesis testing is a framework for making decisions under uncertainty. This lab covers t-tests, z-tests, chi-square tests, p-values, Type I/II errors, and significance levels.

## Learning Objectives
- Understand null and alternative hypotheses
- Perform one-sample and two-sample t-tests
- Perform z-tests for large samples
- Perform chi-square tests for categorical data
- Interpret p-values and confidence intervals
- Understand Type I and Type II errors

## Tests Covered

| Test | Use Case |
|------|----------|
| One-sample t-test | Compare sample mean to known value |
| Two-sample t-test | Compare means of two independent groups |
| Paired t-test | Compare paired observations |
| Z-test | Known variance, large sample |
| Chi-square goodness-of-fit | Test observed vs expected frequencies |
| Chi-square independence | Test association between categorical variables |

## Running the Code

```bash
javac -d out src/HypothesisTesting.java
java -cp out com.statistics.lab03.HypothesisTesting
```
