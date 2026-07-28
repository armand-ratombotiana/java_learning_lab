# Time Series — Interview Questions

### Q1: Stationarity
**Q**: Why is stationarity important for time series modeling? How do you test for it?

**A**: Stationarity ensures constant mean, variance, and autocorrelation over time — required for ARIMA. Test via ADF (Augmented Dickey-Fuller) or KPSS. ADF: H₀ = unit root (non-stationary). If p < 0.05, reject H₀. Transform via differencing (d=1 for linear trend, d=2 for quadratic), log transform (variance stabilization), or Box-Cox.

### Q2: Model Selection
**Q**: How do you choose p, d, q for ARIMA?

**A**: d: ADF test until stationary. p: PACF cuts off after lag p. q: ACF cuts off after lag q. Alternatively, grid search (p,q,d) minimizing AIC or BIC. Auto-ARIMA approaches: Hyndman-Khandakar algorithm using unit root tests + AIC minimization.

### Q3: Seasonality
**Q**: You have daily data with weekly seasonality. How do you model it?

**A**: SARIMA(p,d,q)(P,D,Q)_s with s=7. If the seasonal pattern is stable, use seasonal differencing (D=1). For longer seasonal periods (hourly data with daily seasonality, s=24), consider STL decomposition + ARIMA on residuals, or Facebook Prophet which handles multiple seasonalities via Fourier terms.

### Q4: Forecasting at Scale
**Q**: How would you forecast 10,000 SKUs in a retail setting?

**A**: Use a two-tier approach: (1) Cluster SKUs into groups with similar patterns (k-means on features like trend strength, seasonal strength, variance). (2) Fit one model per cluster (ARIMA, ETS, or Prophet). (3) For individual SKU adjustments, use a simple scaling factor based on recent history. Monitor forecast accuracy via tracking signal and trigger retraining when accuracy degrades.

### Q5: Structural Breaks
**Q**: A new policy caused a sudden shift in your time series. How do you detect and handle this?

**A**: Detect via CUSUM (cumulative sum of residuals), Chow test, or Bai-Perron multiple breakpoint test. After detection: (1) Model pre- and post-break separately. (2) Include a step-change dummy variable. (3) Use adaptive methods (Damped Holt-Winters, dynamic linear models) that discount older observations.

## Coding

### Q6: Compute ACF
```java
public double[] autocorrelation(double[] series, int maxLag) {
    double mean = Arrays.stream(series).average().orElseThrow();
    double[] acf = new double[maxLag + 1];
    double var = 0;
    for (double v : series) var += Math.pow(v - mean, 2);
    for (int k = 0; k <= maxLag; k++) {
        double cov = 0;
        for (int t = k; t < series.length; t++) cov += (series[t] - mean) * (series[t - k] - mean);
        acf[k] = cov / var;
    }
    return acf;
}
```
