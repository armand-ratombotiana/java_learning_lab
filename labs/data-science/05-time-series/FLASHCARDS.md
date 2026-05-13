# Time Series Flashcards

### Card 1: Time Series
**Q:** What is time series data?
**A:** Data points collected at successive time intervals (daily, monthly, yearly).

### Card 2: Trend
**Q:** What is trend component?
**A:** Long-term direction of data (increasing, decreasing, stationary).

### Card 3: Seasonality
**Q:** What is seasonal component?
**A:** Repeating patterns at fixed intervals (daily, weekly, yearly).

### Card 4: Stationarity
**Q:** When is a series stationary?
**A:** Mean, variance, and autocovariance are constant over time.

### Card 5: ADF Test
**Q:** What does ADF test check?
**A:** Whether a time series is stationary using null hypothesis of unit root.

### Card 6: Differencing
**Q:** Why difference a series?
**A:** To make it stationary by removing trend and stabilizing variance.

### Card 7: ACF
**Q:** What is autocorrelation function?
**A:** Correlation of series with itself at different lags.

### Card 8: PACF
**Q:** What is partial autocorrelation?
**A:** Direct correlation at a lag controlling for intermediate lags.

### Card 9: Moving Average
**Q:** What does MA(k) forecast use?
**A:** Average of last k observations.

### Card 10: Exponential Smoothing
**Q:** What makes EMA different from MA?
**A:** Gives more weight to recent observations with exponential decay.

### Card 11: ARIMA
**Q:** What does ARIMA(p,d,q) mean?
**A:** Auto-regressive (p), differencing (d), moving average (q).

### Card 12: Holt's Method
**Q:** When use Holt's method?
**A:** For series with trend but no seasonality.

### Card 13: Holt-Winters
**Q:** When use Holt-Winters?
**A:** For series with both trend and seasonality.

### Card 14: Decomposition
**Q:** What is additive decomposition?
**A:** Y(t) = Trend + Seasonal + Residual (components add up).

### Card 15: Multiplicative
**Q:** What is multiplicative decomposition?
**A:** Y(t) = Trend × Seasonal × Residual (components multiply).

### Card 16: MAE
**Q:** Formula for MAE?
**A:** Mean of absolute differences between actual and predicted.

### Card 17: RMSE
**Q:** Formula for RMSE?
**A:** Square root of mean squared differences.

### Card 18: MAPE
**Q:** Formula for MAPE?
**A:** Mean of absolute percentage errors.

### Card 19: Lag Feature
**Q:** What is a lag feature?
**A:** Value from previous time step(s) used as predictor.

### Card 20: Rolling Window
**Q:** What is rolling window feature?
**A:** Statistic (mean, std) computed over sliding window of observations.