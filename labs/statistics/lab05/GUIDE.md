# Guide: Correlation & Regression in Java

## Step 1: Compute Pearson Correlation
Calculate means, then sum of cross-products divided by product of standard deviations.

## Step 2: Compute Spearman Correlation
Rank both datasets, then compute Pearson on the ranks. Handle ties by assigning average ranks.

## Step 3: Simple Linear Regression (OLS)
- Slope = covariance(x,y) / variance(x)
- Intercept = ȳ - slope × x̄
- R² = squared correlation between actual and predicted

## Step 4: Multiple Regression
Solve β = (XᵀX)⁻¹Xᵀy using matrix operations. Use Gaussian elimination for matrix inversion.

## Step 5: Residuals
Residual = observed - predicted. Examine for patterns suggesting non-linearity.

## Step 6: Java Implementation
```java
public static double pearson(double[] x, double[] y) {
    double mx = mean(x), my = mean(y);
    double sxy = 0, sxx = 0, syy = 0;
    for (int i = 0; i < x.length; i++) {
        double dx = x[i] - mx, dy = y[i] - my;
        sxy += dx * dy; sxx += dx * dx; syy += dy * dy;
    }
    return sxy / Math.sqrt(sxx * syy);
}
```

## Test Cases
- x = {1,2,3,4,5}, y = {2,4,6,8,10} → r = 1.0 (perfect linear)
- x = {1,2,3,4,5}, y = {5,4,3,2,1} → r = -1.0 (perfect negative)
- Heights vs Weights dataset → r ≈ 0.97, Regression: Weight = -143.0 + 5.0 × Height
