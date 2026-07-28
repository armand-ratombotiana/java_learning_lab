# Mock Interview: Time Series Analysis

**Interviewer**: We need to forecast daily active users (DAU) for the next 90 days. Walk me through your approach.

**Candidate**: First, I'd explore the data: plot the series, check for trend, day-of-week seasonality, and holiday effects. I'd test stationarity with an ADF test. If non-stationary, I'd difference. I'd examine ACF/PACF to identify ARIMA order, likely SARIMA with weekly seasonality (s=7). I'd validate via time series cross-validation (rolling origin), measuring forecast accuracy with RMSE and MASE. If the series has complex seasonality (e.g., monthly + weekly), I'd use Prophet or TBATS.

**Interviewer**: Why MASE over MAPE?

**Candidate**: MAPE is undefined/infinite when actual values are near zero (common in DAU dips overnight). MASE divides by the in-sample MAE of a naive forecast (random walk), so it's scale-invariant and always defined. MASE < 1 means our forecast beats the naive baseline.

**Interviewer**: Our DAU shows a sharp drop during a server outage. How do you handle this outlier?

**Candidate**: I'd detect it via the series' IQR or by comparing against the forecast's prediction interval. For modeling, I have three options: (1) Replace the outlier with forecasted values (imputation), (2) Include a dummy variable marking the outage day, (3) Use robust methods (Huber loss instead of MSE) if there could be other subtle anomalies. I'd prefer option 2 — the dummy preserves the information that something unusual happened without distorting the underlying pattern.

**Interviewer**: What if there's a persistent level shift after a product launch?

**Candidate**: That's a structural break. I'd use a Chow test or CUSUM to identify the breakpoint. Then I'd either: (a) fit separate models for pre- and post-launch periods, (b) include a level-shift dummy from the launch date onward, or (c) use an adaptive model like Damped Holt-Winters or a dynamic linear model that automatically adjusts to regime changes via a forgetting factor.

**Interviewer**: Let's code. Implement ACF up to lag k.

**Candidate**:
```java
public double[] acf(double[] series, int maxLag) {
    int n = series.length;
    double mean = Arrays.stream(series).average().orElseThrow();
    double var = 0;
    for (double v : series) var += Math.pow(v - mean, 2);
    double[] r = new double[maxLag + 1];
    for (int k = 0; k <= maxLag; k++) {
        double cov = 0;
        for (int t = k; t < n; t++) cov += (series[t] - mean) * (series[t - k] - mean);
        r[k] = cov / var;
    }
    return r;
}
```

**Interviewer**: How do you interpret the ACF plot for ARIMA model selection?

**Candidate**: For AR(p): ACF decays exponentially or oscillates, PACF cuts off after lag p. For MA(q): ACF cuts off after lag q, PACF decays. For ARMA(p,q): both decay. These are guidelines; I always supplement with AIC/BIC-based grid search across candidate (p,d,q) orders.
