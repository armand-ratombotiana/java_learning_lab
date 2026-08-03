# Problem Walkthrough: Correlation & Regression

## Problem 1: Listing Price Model — Company: Airbnb
### Interview Scenario
"You're at Airbnb on the pricing science team. You have 8 listings with nightly price (EUR), bedroom count, and distance to the city center (km). The pricing team wants a small interpretable model: price = f(bedrooms, distance). They also need to know how strongly bedrooms correlate with price, how an outlier listing distorts correlation, and which listing would be mispriced if you ignored distance entirely."

### The Problem
1. Fit a multiple regression of price on bedrooms and distance using the normal equations
2. Print per-listing predicted prices and residuals
3. Fit a simple regression of price on bedrooms only and report slope, intercept, R²
4. Compute Pearson and Spearman correlation between bedrooms and price
5. Show how a single outlier changes Pearson but not Spearman
6. Sum of squared residuals as the model-quality metric

### Solution Walkthrough
- Step 1: Reuse the lab's `CorrelationAndRegression` methods verbatim: `multipleRegression`, `solveLinearSystem`, `predict`, `residuals`, `simpleRegression`, `pearson`, `spearman`, `rank`
- Step 2: Build the design matrix with the all-ones intercept column; `multipleRegression` forms XᵀX and Xᵀy and solves via Gaussian elimination with partial pivoting
- Step 3: Coefficients come out as intercept 76.2323, bedrooms +14.9452 EUR/bedroom, distance -5.7456 EUR/km — every extra km from the center costs about 5.75 EUR
- Step 4: `predict(X, beta)` and `residuals(price, pred)` produce the table; the largest residual is -3.65 on the 4-bedroom/2.5 km listing — SSE = 47.055152
- Step 5: Simple regression on bedrooms alone: slope 18.45, intercept 53.25, R² = 0.889273 — the omitted-variable cost of ignoring distance
- Step 6: Pearson r = 0.943013; replacing the most expensive listing with a 250 EUR outlier drops Pearson to 0.742394 while Spearman stays 0.927105 — the rank-based robustness story from the lab's `rank` average-tie logic

### Code
```java
package com.statistics.lab05;

import java.util.Arrays;

/**
 * Mirrors the lab's CorrelationAndRegression class (Pearson, Spearman,
 * OLS simple and multiple regression, residuals) and applies it to an
 * Airbnb-style listing price model: price from bedrooms and distance
 * to the city center.
 */
public final class ListingPriceModel {

    private ListingPriceModel() {
    }

    public static double mean(double[] data) {
        double sum = 0;
        for (double v : data) {
            sum += v;
        }
        return sum / data.length;
    }

    public static double pearson(double[] x, double[] y) {
        double mx = mean(x);
        double my = mean(y);
        double sxy = 0, sxx = 0, syy = 0;
        for (int i = 0; i < x.length; i++) {
            double dx = x[i] - mx;
            double dy = y[i] - my;
            sxy += dx * dy;
            sxx += dx * dx;
            syy += dy * dy;
        }
        return sxy / Math.sqrt(sxx * syy);
    }

    public static double[] rank(double[] values) {
        int n = values.length;
        double[] sorted = values.clone();
        Arrays.sort(sorted);
        double[] ranks = new double[n];
        for (int i = 0; i < n; i++) {
            double v = values[i];
            int first = 0, last = 0;
            for (int j = 0; j < n; j++) {
                if (sorted[j] == v) {
                    first = j;
                    break;
                }
            }
            for (int j = n - 1; j >= 0; j--) {
                if (sorted[j] == v) {
                    last = j;
                    break;
                }
            }
            ranks[i] = 1.0 + (first + last) / 2.0;
        }
        return ranks;
    }

    public static double spearman(double[] x, double[] y) {
        return pearson(rank(x), rank(y));
    }

    public static double[] simpleRegression(double[] x, double[] y) {
        double mx = mean(x);
        double my = mean(y);
        double sxy = 0, sxx = 0;
        for (int i = 0; i < x.length; i++) {
            double dx = x[i] - mx;
            double dy = y[i] - my;
            sxy += dx * dy;
            sxx += dx * dx;
        }
        double slope = sxy / sxx;
        double intercept = my - slope * mx;

        double ssRes = 0, ssTot = 0;
        for (int i = 0; i < y.length; i++) {
            double pred = slope * x[i] + intercept;
            ssRes += (y[i] - pred) * (y[i] - pred);
            ssTot += (y[i] - my) * (y[i] - my);
        }
        double rSquared = 1.0 - ssRes / ssTot;
        return new double[]{slope, intercept, rSquared};
    }

    public static double[] solveLinearSystem(double[][] A, double[] b) {
        int n = b.length;
        double[][] aug = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, aug[i], 0, n);
            aug[i][n] = b[i];
        }
        for (int col = 0; col < n; col++) {
            int pivot = col;
            for (int row = col + 1; row < n; row++) {
                if (Math.abs(aug[row][col]) > Math.abs(aug[pivot][col])) {
                    pivot = row;
                }
            }
            double[] tmp = aug[col];
            aug[col] = aug[pivot];
            aug[pivot] = tmp;
            double pivVal = aug[col][col];
            for (int j = col; j <= n; j++) {
                aug[col][j] /= pivVal;
            }
            for (int row = 0; row < n; row++) {
                if (row != col) {
                    double factor = aug[row][col];
                    for (int j = col; j <= n; j++) {
                        aug[row][j] -= factor * aug[col][j];
                    }
                }
            }
        }
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = aug[i][n];
        }
        return x;
    }

    public static double[] multipleRegression(double[][] X, double[] y) {
        int n = X.length;
        int p = X[0].length;
        double[][] XtX = new double[p][p];
        double[] Xty = new double[p];
        for (int i = 0; i < p; i++) {
            for (int j = 0; j < p; j++) {
                double sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += X[k][i] * X[k][j];
                }
                XtX[i][j] = sum;
            }
            double sum = 0;
            for (int k = 0; k < n; k++) {
                sum += X[k][i] * y[k];
            }
            Xty[i] = sum;
        }
        return solveLinearSystem(XtX, Xty);
    }

    public static double[] residuals(double[] y, double[] predicted) {
        double[] res = new double[y.length];
        for (int i = 0; i < y.length; i++) {
            res[i] = y[i] - predicted[i];
        }
        return res;
    }

    public static double[] predict(double[][] X, double[] beta) {
        double[] pred = new double[X.length];
        for (int i = 0; i < X.length; i++) {
            double sum = 0;
            for (int j = 0; j < beta.length; j++) {
                sum += X[i][j] * beta[j];
            }
            pred[i] = sum;
        }
        return pred;
    }

    public static void main(String[] args) {
        System.out.println("=== Airbnb-style listing price model ===");
        double[][] X = {
            {1, 1, 5.0},
            {1, 1, 2.0},
            {1, 2, 4.0},
            {1, 2, 1.5},
            {1, 3, 3.0},
            {1, 3, 0.8},
            {1, 4, 2.5},
            {1, 4, 1.0}
        };
        double[] price = {65, 78, 82, 95, 105, 120, 118, 132};
        double[] bedrooms = {1, 1, 2, 2, 3, 3, 4, 4};

        double[] beta = multipleRegression(X, price);
        System.out.print("Multiple regression coefficients (intercept, bedrooms, distance): ");
        for (double c : beta) {
            System.out.printf("%.4f ", c);
        }
        System.out.println();

        double[] pred = predict(X, beta);
        double[] res = residuals(price, pred);
        double ssRes = 0;
        for (double r : res) {
            ssRes += r * r;
        }
        System.out.printf("Sum of squared residuals: %.6f%n", ssRes);
        for (int i = 0; i < price.length; i++) {
            System.out.printf("  beds=%.0f dist=%.1f obs=%6.1f pred=%7.2f res=%+7.2f%n",
                X[i][1], X[i][2], price[i], pred[i], res[i]);
        }

        System.out.println("\n=== Simple regression: price ~ bedrooms ===");
        double[] simple = simpleRegression(bedrooms, price);
        System.out.printf("Slope = %.4f, Intercept = %.4f, R2 = %.6f%n",
            simple[0], simple[1], simple[2]);

        System.out.println("\n=== Correlation: bedrooms vs price ===");
        System.out.printf("Pearson r  = %.6f%n", pearson(bedrooms, price));
        System.out.printf("Spearman   = %.6f%n", spearman(bedrooms, price));

        System.out.println("\n=== Outlier robustness: price has one 250 EUR outlier ===");
        double[] priceOutlier = {65, 78, 82, 95, 105, 120, 118, 250};
        System.out.printf("Pearson r  = %.6f (pulled by the outlier)%n", pearson(bedrooms, priceOutlier));
        System.out.printf("Spearman   = %.6f (rank-based, robust)%n", spearman(bedrooms, priceOutlier));
    }
}
```

### Expected Output
```
=== Airbnb-style listing price model ===
Multiple regression coefficients (intercept, bedrooms, distance): 76.2323 14.9452 -5.7456 
Sum of squared residuals: 47.055152
  beds=1 dist=5.0 obs=  65.0 pred=  62.45 res=  +2.55
  beds=1 dist=2.0 obs=  78.0 pred=  79.69 res=  -1.69
  beds=2 dist=4.0 obs=  82.0 pred=  83.14 res=  -1.14
  beds=2 dist=1.5 obs=  95.0 pred=  97.50 res=  -2.50
  beds=3 dist=3.0 obs= 105.0 pred= 103.83 res=  +1.17
  beds=3 dist=0.8 obs= 120.0 pred= 116.47 res=  +3.53
  beds=4 dist=2.5 obs= 118.0 pred= 121.65 res=  -3.65
  beds=4 dist=1.0 obs= 132.0 pred= 130.27 res=  +1.73

=== Simple regression: price ~ bedrooms ===
Slope = 18.4500, Intercept = 53.2500, R2 = 0.889273

=== Correlation: bedrooms vs price ===
Pearson r  = 0.943013
Spearman   = 0.927105

=== Outlier robustness: price has one 250 EUR outlier ===
Pearson r  = 0.742394 (pulled by the outlier)
Spearman   = 0.927105 (rank-based, robust)
```

### Company Evaluation
- Airbnb: price modeling with sparse listing features, outlier-robust correlation for market benchmarks, residual diagnostics before price-suggestion rollouts.
- Uber: fare vs trip-distance regression, surge elasticity, Spearman for skewed rider ratings.
- Google: query-latency vs fleet-size models, multiple regression on serving metrics, VIF checks before coefficient interpretation.
- Stripe: fee vs transaction-volume models, outlier-robust correlation on merchant health scores.

---

## Problem 2: Demand vs Weather Correlation — Company: Uber
### Interview Scenario
"You're at Uber. You have 7 city-days of ride counts and a weather index: x = {1,2,3,4,5,6,7}, y = {3,1,4,2,7,5,6}. The team wants to know how strongly weather and rides move together."

### The Problem
1. Compute Pearson correlation
2. Compute Spearman rank correlation with the lab's average-tie ranking
3. Explain the gap between the two numbers

### Solution Walkthrough
- Step 1: `pearson(x, y)` = 0.714286 — the raw linear association
- Step 2: `spearman(x, y)` ranks both series and re-runs Pearson on the ranks — the lab's demo output is rho = 0.714286, identical here because the pattern is monotonic with no ties
- Step 3: When the two differ, the relationship is monotonic but non-linear — that's the diagnosis signal the team should read before fitting any linear model

### Code
```java
double[] x = {1, 2, 3, 4, 5, 6, 7};
double[] y = {3, 1, 4, 2, 7, 5, 6};
System.out.printf("Pearson r = %.6f%n", pearson(x, y));
System.out.printf("Spearman rho = %.6f%n", spearman(x, y));
```

### Expected Output
```
Pearson r = 0.714286
Spearman rho = 0.714286
```

---

## Problem 3: Perfect-Fit Sanity Check — Company: Google
### Interview Scenario
"You're at Google verifying a new statistics library by regression: y = 1 + 2x₁ - 0.5x₂ generated exactly. If the solver recovers the coefficients and the residuals are zero, the normal equations and the Gaussian elimination are correct."

### The Problem
1. Fit the multiple regression on the exact synthetic data
2. Verify coefficients equal (1, 2, -0.5)
3. Confirm residuals are (numerically) zero
4. Print sum of squared residuals

### Solution Walkthrough
- Step 1: `multipleRegression(X, ym)` with the lab's demo design matrix; the normal equations XᵀXβ = Xᵀy are solved by `solveLinearSystem`
- Step 2: The printout shows 1.0000, 2.0000, -0.5000 — exact recovery
- Step 3: `residuals(ym, pred)` prints -0.0000 per row and the SSE is effectively zero — the oracle passes

### Code
```java
double[][] X = {
    {1, 1, 1}, {1, 2, 3}, {1, 3, 2}, {1, 4, 5}, {1, 5, 4}
};
double[] ym = {2.5, 3.5, 6.0, 6.5, 9.0};
double[] beta = multipleRegression(X, ym);
System.out.print("Coefficients (intercept, x1, x2): ");
for (double c : beta) {
    System.out.printf("%.4f ", c);
}
System.out.println();
double[] res = residuals(ym, predict(X, beta));
double ss = 0;
for (double r : res) {
    ss += r * r;
}
System.out.printf("Sum of squared residuals: %.6f%n", ss);
```

### Expected Output
```
Coefficients (intercept, x1, x2): 1.0000 2.0000 -0.5000 
Sum of squared residuals: 0.000000
```
