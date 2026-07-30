# Statistics Academy — Index

A hands-on, code-first curriculum covering foundational to advanced statistical concepts with Java 21+ implementations.

## Lab Overview

| Lab | Topic | Key Concepts |
|-----|-------|--------------|
| 01 | Descriptive Statistics | Mean, median, mode, variance, stddev, quartiles, IQR |
| 02 | Probability Distributions | Normal, Binomial, Poisson, Exponential — PDF, CDF, sampling |
| 03 | Hypothesis Testing | t-test, z-test, chi-square, p-values, Type I/II errors, significance |
| 04 | ANOVA | One-way, two-way, F-statistic, post-hoc tests, assumptions |
| 05 | Correlation & Regression | Pearson, Spearman, simple/multiple regression, R², residuals |
| 06 | Bayesian Statistics | Bayes theorem, prior/posterior, conjugate priors, credible intervals |
| 07 | Time Series Analysis | Trend, seasonality, autocorrelation, moving averages, smoothing |
| 08 | Experimental Design | Randomization, blocking, factorial designs, sample size |
| 09 | Non-parametric Statistics | Mann-Whitney, Wilcoxon, Kruskal-Wallis, Friedman test |
| 10 | Statistical Power & Effect Size | Power analysis, Cohen's d, sample size, MDE |

## How to Use

Each lab contains:
- **README.md** — Overview, learning objectives, and theory
- **GUIDE.md** — Step-by-step walkthrough with code snippets
- **INTERVIEW.md** — Common interview questions and answers
- **src/***.java** — Compilable Java 21+ source with test cases

## Prerequisites

- Java 21+ SDK
- Basic probability and algebra
- Familiarity with `javac` and `java` command-line tools

## Compilation & Execution

```bash
cd labs/statistics/lab01
javac -d out src/DescriptiveStatistics.java
java -cp out com.statistics.lab01.DescriptiveStatistics
```
