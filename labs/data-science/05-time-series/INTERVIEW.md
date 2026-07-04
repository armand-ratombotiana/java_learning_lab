# Interview Questions: Time Series

## Conceptual

1. **What is stationarity and why does it matter for time series?**
   - A series is stationary if its mean, variance, and autocorrelation are constant over time. Most TS models require it because they assume the same statistical properties continue into the future.

2. **Explain the difference between ACF and PACF. How do you use them for ARIMA identification?**
   - ACF: correlation with lagged values (includes indirect correlations via intermediate lags)
   - PACF: correlation with lagged values after removing intermediate correlations
   - For AR(p): PACF cuts off after lag p, ACF decays. For MA(q): ACF cuts off after lag q, PACF decays.

3. **How would you forecast a series with both trend and seasonality?**
   - Use SARIMA (seasonal ARIMA), Holt-Winters exponential smoothing, or Prophet.
   - Decompose first, model trend and seasonality separately, then combine.

## Coding

4. **Implement simple exponential smoothing in Java.**

```java
public static double[] simpleExponentialSmoothing(double[] series, double alpha, int horizon) {
    int n = series.length;
    double[] forecast = new double[n + horizon];
    forecast[0] = series[0];
    for (int i = 1; i < n; i++) {
        forecast[i] = alpha * series[i - 1] + (1 - alpha) * forecast[i - 1];
    }
    for (int i = n; i < n + horizon; i++) {
        forecast[i] = forecast[n - 1];  // flat line for future
    }
    return forecast;
}
```

5. **How do you detect seasonal patterns in a time series?**
   - Plot the series and look for repeating patterns
   - ACF plot: significant peaks at lags 12, 24, 36 (monthly data)
   - Spectral analysis: find dominant frequencies via FFT
   - Seasonal decomposition (STL): extract seasonal component

6. **What metrics would you use to evaluate a forecast, and what are their trade-offs?**
   - MAE: interpretable, not sensitive to outliers
   - RMSE: penalizes large errors more (good when large errors are costly)
   - MAPE: scale-independent but undefined when actual = 0
   - sMAPE (symmetric MAPE): bounded, avoids MAPE's asymmetry
   - MASE: compares to naive forecast; >1 means worse than naive
