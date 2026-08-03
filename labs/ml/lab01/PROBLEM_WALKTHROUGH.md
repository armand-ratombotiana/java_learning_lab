# Problem Walkthrough: Linear Regression

## Problem 1: Zillow-Style Median Home Price Estimator — Company: Zillow

### Interview Scenario
"You're at Zillow. A new city market was just launched, and your team needs a
first-version home price model that can be explained to product and defended in front
of regulators. You only have one reliable feature so far — square footage of sold
listings over the last quarter. Build the model, measure how good it is, and price
three pending listings."

### The Problem
Build a univariate linear regression price model. The model must: (1) Fit a line
using the OLS closed form, (2) Cross-check the fit with gradient descent, (3) Report
MSE, MAE, and R² so the team can judge fit quality, (4) Price new listings at
predict-time with the fitted line, (5) Avoid a classic pitfall — unnormalized feature
scales that break gradient descent convergence.

### Solution Walkthrough
- Step 1: Encode the 10 sold listings as `sqftK` (square footage in thousands) and
  `priceK` (median sale price in $k). Scaling the feature keeps the loss surface
  well-conditioned.
- Step 2: Call `fitOLS(sqftK, priceK)` — the lab's closed form accumulates
  `sx, sy, sxx, sxy` and returns `[slope, intercept]`.
- Step 3: Call `fitGD(sqftK, priceK, 1e-5, 100000)` — the lab's batch gradient
  descent — and print both solutions side by side so they can be compared.
- Step 4: Compute predictions `yHat` with `predict(slope, intercept, x)` for every
  training point, once per fitter.
- Step 5: Evaluate with the lab's `mse`, `mae`, and `r2` on both fits. OLS should
  beat GD slightly because GD stops short of the optimum.
- Step 6: Price the three new listings with the OLS coefficients and print them in
  dollars.

### Code
```java
package com.ml.lab01;

/**
 * Zillow-style median home price model.
 * <p>
 * Fits a univariate linear regression on historical square-footage
 * listings with OLS and gradient descent, then prices new listings
 * and reports MSE / MAE / R². Mirrors Lab 01's fitOLS, fitGD,
 * predict, mse, mae and r2 methods.
 */
public class ZillowPriceModel {

    public static double[] fitOLS(double[] x, double[] y) {
        int n = x.length;
        double sx = 0, sy = 0, sxx = 0, sxy = 0;
        for (int i = 0; i < n; i++) {
            sx += x[i];
            sy += y[i];
            sxx += x[i] * x[i];
            sxy += x[i] * y[i];
        }
        double slope = (n * sxy - sx * sy) / (n * sxx - sx * sx);
        double intercept = (sy - slope * sx) / n;
        return new double[]{slope, intercept};
    }

    public static double[] fitGD(double[] x, double[] y, double lr, int epochs) {
        int n = x.length;
        double m = 0.0, b = 0.0;
        for (int ep = 0; ep < epochs; ep++) {
            double dm = 0.0, db = 0.0;
            for (int i = 0; i < n; i++) {
                double pred = m * x[i] + b;
                double err = pred - y[i];
                dm += err * x[i];
                db += err;
            }
            m -= lr * dm / n;
            b -= lr * db / n;
        }
        return new double[]{m, b};
    }

    public static double predict(double slope, double intercept, double x) {
        return slope * x + intercept;
    }

    public static double mse(double[] y, double[] yHat) {
        double s = 0;
        for (int i = 0; i < y.length; i++) {
            double d = y[i] - yHat[i];
            s += d * d;
        }
        return s / y.length;
    }

    public static double mae(double[] y, double[] yHat) {
        double s = 0;
        for (int i = 0; i < y.length; i++) {
            s += Math.abs(y[i] - yHat[i]);
        }
        return s / y.length;
    }

    public static double r2(double[] y, double[] yHat) {
        double yBar = 0;
        for (double v : y) yBar += v;
        yBar /= y.length;
        double ssRes = 0, ssTot = 0;
        for (int i = 0; i < y.length; i++) {
            ssRes += (y[i] - yHat[i]) * (y[i] - yHat[i]);
            ssTot += (y[i] - yBar) * (y[i] - yBar);
        }
        return 1 - ssRes / ssTot;
    }

    public static void main(String[] args) {
        System.out.println("=== Zillow-style Home Price Model ===");

        // Historical sold listings: sqft in thousands vs median sale price ($k)
        double[] sqftK = {0.8, 1.0, 1.2, 1.5, 1.8, 2.1, 2.4, 2.8, 3.2, 3.6};
        double[] priceK = {145, 168, 192, 228, 262, 301, 335, 385, 428, 470};

        double[] ols = fitOLS(sqftK, priceK);
        System.out.printf("OLS     -> slope = %.4f $k per 1k sqft, intercept = %.4f $k%n",
                ols[0], ols[1]);

        double[] gd = fitGD(sqftK, priceK, 1e-5, 100000);
        System.out.printf("GD      -> slope = %.4f $k per 1k sqft, intercept = %.4f $k%n",
                gd[0], gd[1]);

        double[] yHat = new double[priceK.length];
        double[] yHatGd = new double[priceK.length];
        for (int i = 0; i < priceK.length; i++) {
            yHat[i] = predict(ols[0], ols[1], sqftK[i]);
            yHatGd[i] = predict(gd[0], gd[1], sqftK[i]);
        }

        System.out.printf("MSE(OLS)= %.4f  MAE(OLS)= %.4f  R²(OLS)= %.4f%n",
                mse(priceK, yHat), mae(priceK, yHat), r2(priceK, yHat));
        System.out.printf("MSE(GD) = %.4f  MAE(GD) = %.4f  R²(GD) = %.4f%n",
                mse(priceK, yHatGd), mae(priceK, yHatGd), r2(priceK, yHatGd));

        double[] newListingsK = {0.95, 2.6, 4.0};
        for (double f : newListingsK) {
            System.out.printf("Listing %.2f sqft -> estimated $%.1fk%n",
                    f * 1000, predict(ols[0], ols[1], f));
        }
    }
}
```

### Expected Output
```
=== Zillow-style Home Price Model ===
OLS     -> slope = 117.4841 $k per 1k sqft, intercept = 51.7325 $k
GD      -> slope = 117.8229 $k per 1k sqft, intercept = 49.9663 $k
MSE(OLS)= 5.2723  MAE(OLS)= 1.7057  R²(OLS)= 0.9995
MSE(GD) = 6.5216  MAE(GD) = 1.9102  R²(GD) = 0.9994
Listing 950.00 sqft -> estimated $163.3k
Listing 2600.00 sqft -> estimated $357.2k
Listing 4000.00 sqft -> estimated $521.7k
```

---

## Problem 2: Airbnb Nightly Price Baseline — Company: Airbnb

### Interview Scenario
"You're at Airbnb. The listing team wants a one-feature baseline nightly price model
to sanity-check the ranking system: predict price from the number of guests a listing
can host. Ship the simplest defensible model with error numbers on the table."

### The Problem
Fit a linear model of nightly price vs guest capacity and: (1) Fit with OLS,
(2) Report MAE in dollars so pricing can understand it, (3) Show R²,
(4) Predict a new 6-guest listing's price.

### Solution Walkthrough
- Step 1: Data — guest capacity `g` and nightly price `p` for 8 recent bookings.
- Step 2: `fitOLS(g, p)` from Lab 01; no scaling needed at this magnitude.
- Step 3: Score with `mae` and `r2` — MAE is the business-facing number here.
- Step 4: `predict(slope, intercept, 6)` for the new listing and print it.

### Code
```java
public static void main(String[] args) {
    double[] guests = {1, 2, 2, 3, 3, 4, 5, 5};
    double[] price  = {85, 110, 118, 140, 135, 165, 190, 205};

    double[] w = ZillowPriceModel.fitOLS(guests, price);
    double[] yHat = new double[guests.length];
    for (int i = 0; i < guests.length; i++)
        yHat[i] = ZillowPriceModel.predict(w[0], w[1], guests[i]);

    System.out.printf("slope=%.2f $/guest  intercept=%.2f$%n", w[0], w[1]);
    System.out.printf("MAE=%.2f$  R²=%.3f%n",
            ZillowPriceModel.mae(price, yHat), ZillowPriceModel.r2(price, yHat));
    System.out.printf("6-guest listing -> $%.0f/night%n",
            ZillowPriceModel.predict(w[0], w[1], 6));
}
```

### Expected Output
```
slope=27.80 $/guest  intercept=56.63$
MAE=3.93$  R²=0.984
6-guest listing -> $223/night
```

---

## Problem 3: Uber Pickup Demand by Hour — Company: Uber

### Interview Scenario
"You're at Uber. Operations wants a quick model of nightly ride demand as a function
of hour-of-day so dispatch can pre-position drivers. The data is small and the
analyst will read the coefficients aloud in a review — keep it simple and exact."

### The Problem
Fit hourly demand with the OLS closed form and: (1) Fit the line, (2) Confirm the
gradient-descent path converges to the same answer, (3) Compare MSE of both fits,
(4) Forecast demand at hour 22.

### Solution Walkthrough
- Step 1: Encode hours 8–20 as tenths of a day (x in [0.8, 2.0]) and ride counts as y
  from the dispatch log. Scaling matters: with raw hour values the intercept
  gradient is tiny relative to the slope gradient and GD crawls for thousands of
  extra epochs.
- Step 2: Fit with `fitOLS` and `fitGD(hour, rides, 1e-3, 200000)`.
- Step 3: Predict both fits, compare MSE to show convergence — both land at 217.7.
- Step 4: Forecast hour 22 (x = 2.2); both models print 636 rides.

### Code
```java
double[] hour = {0.8, 0.9, 1.0, 1.1, 1.2, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0};
double[] rides = {310, 340, 355, 370, 402, 430, 448, 470, 505, 540, 580, 615};

double[] ols = ZillowPriceModel.fitOLS(hour, rides);
double[] gd = ZillowPriceModel.fitGD(hour, rides, 1e-3, 200000);
double mseOls = 0, mseGd = 0;
for (int i = 0; i < hour.length; i++) {
    mseOls += Math.pow(ZillowPriceModel.predict(ols[0], ols[1], hour[i]) - rides[i], 2);
    mseGd += Math.pow(ZillowPriceModel.predict(gd[0], gd[1], hour[i]) - rides[i], 2);
}
System.out.printf("OLS: %.3f rides/(0.1h) + %.1f | GD: %.3f + %.1f%n",
        ols[0], ols[1], gd[0], gd[1]);
System.out.printf("MSE OLS=%.1f  MSE GD=%.1f%n", mseOls / hour.length, mseGd / hour.length);
System.out.printf("Hour 22 forecast: OLS=%.0f rides, GD=%.0f rides%n",
        ZillowPriceModel.predict(ols[0], ols[1], 2.2),
        ZillowPriceModel.predict(gd[0], gd[1], 2.2));
```

### Expected Output
```
OLS: 238.337 rides/(0.1h) + 111.4 | GD: 238.336 + 111.4
MSE OLS=217.7  MSE GD=217.7
Hour 22 forecast: OLS=636 rides, GD=636 rides
```
