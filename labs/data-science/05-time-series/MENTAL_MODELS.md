# Mental Models for Time Series

## 1. The Decomposition Triad

Every time series is the sum (or product) of three components:

$$ y_t = Trend_t + Seasonality_t + Residual_t $$

- **Trend**: long-term direction (up, down, flat)
- **Seasonality**: repeating patterns (daily, weekly, yearly)
- **Residual**: random noise + irregular events

When you see a time series, mentally decompose it into these three. Each requires different treatment.

## 2. The Stationarity Constraint

Most TS models require stationarity (constant mean, variance, autocorrelation over time). Non-stationary series must be differenced or transformed first.

```
Non-stationary:    Stationary (differenced):
    ╱╲               ╱╲╱  ╲╱╲╱╲╱
   ╱  ╲             ╱
  ╱    ╲           ╱
 ╱      ╲
```

## 3. The 80/20 Forecast Rule

The first 20% of your time series effort should go to feature engineering (lags, rolling windows, calendar effects), which will give you 80% of the accuracy improvement. The choice between ARIMA and exponential smoothing matters far less than having good predictors.

## 4. The Random Walk Baseline

The simplest time series forecast is: "tomorrow equals today." This is the random walk model. Any model you build must beat this baseline. If it doesn't, the series may be unpredictable with available information.

```java
// Random walk baseline
double[] naiveForecast = new double[steps];
naiveForecast[0] = lastObservedValue;
for (int i = 1; i < steps; i++) {
    naiveForecast[i] = naiveForecast[i-1];  // flat line
}
```
