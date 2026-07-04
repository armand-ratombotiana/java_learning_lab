# Common Mistakes in Time Series Analysis

## 1. Random Train/Test Split

Using random k-fold cross-validation on time series leaks future information into training folds.

```java
// WRONG: random split leaks future into past
KFold cv = new KFold(n, 5);  // each fold contains random rows

// RIGHT: temporal split
double[] train = Arrays.copyOfRange(series, 0, (int)(n * 0.8));
double[] test = Arrays.copyOfRange(series, (int)(n * 0.8), n);
```

## 2. Forgetting to Difference

Fitting ARIMA without ensuring stationarity produces spurious forecasts.

**Fix**: Always check ADF test before fitting.

## 3. Forecasting Too Far Ahead

Forecasting 24 months from 12 months of data is extrapolation, not prediction. Forecast horizon should not exceed ~20% of the training data length.

## 4. Ignoring Seasonality

Monthly data with yearly patterns but using ARIMA(p,d,q) instead of SARIMA(p,d,q)(P,D,Q)₁₂.

**Fix**: Always check for seasonal ACF peaks at lag 12, 24, 36.

## 5. Using Point Forecasts Without Intervals

A single number creates false precision. Always provide prediction intervals (80% and 95%).

```java
// Provide intervals
double[] forecast = model.forecast(12);
double[] se = model.standardErrors();
double z = 1.96;  // 95% CI
for (int i = 0; i < forecast.length; i++) {
    double lower = forecast[i] - z * se[i];
    double upper = forecast[i] + z * se[i];
    System.out.printf("t+%d: %.2f [%.2f, %.2f]%n", i+1, forecast[i], lower, upper);
}
```

## 6. Overfitting with Too Many Parameters

ARIMA(5,2,5) with 12 months of data has more parameters than data points.

**Fix**: Use AIC/BIC to penalize complexity. Simpler models (small p,q) often forecast better.
