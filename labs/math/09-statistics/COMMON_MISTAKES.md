# Common Mistakes in Statistics

## Correlation ≠ Causation

```java
// Ice cream sales ↑, drownings ↑ (both caused by summer heat)
// r > 0 does NOT mean one causes the other
```

## Confusing Sample vs Population Variance

```java
// Population variance: divide by n
// Sample variance: divide by n-1 (Bessel's correction)
double popVariance = sumSq / n;
double sampleVariance = sumSq / (n - 1);
```

## P-Hacking / Multiple Comparisons

Running many tests until one is significant ($p < 0.05$) inflates false positive rate. Use Bonferroni correction.

## Outliers Without Investigation

Don't remove outliers without understanding why they exist. They may indicate measurement errors or important phenomena.

## Ignoring Assumptions

Tests have assumptions (normality, equal variance, independence). Check them before applying the test.

## Simpson's Paradox

A trend appears in several groups but disappears or reverses when groups are combined. Always check for confounding variables.
