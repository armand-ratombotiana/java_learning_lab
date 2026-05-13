# Time Series Lab

## Overview
Time series analysis involves methods for analyzing time series data to extract meaningful statistics and characteristics.

## 1. Time Series Components

### Trend
- Long-term movement in data
- Upward, downward, or stationary

### Seasonality
- Regular, periodic fluctuations
- Daily, weekly, monthly patterns

### Cyclical
- Irregular, longer-term patterns
- Business cycles

### Residual/Noise
- Random, unpredictable variations

## 2. Stationarity

### Definition
- Constant mean over time
- Constant variance over time
- Auto-correlation structure doesn't change

### Tests
- Augmented Dickey-Fuller (ADF)
- KPSS test

## 3. Transformations

### Differencing
- First difference: Δy_t = y_t - y_{t-1}
- Removes trend, stabilizes variance

### Log Transform
- Stabilizes variance
- Makes multiplicative seasonal patterns additive

## 4. Forecasting Methods

### Moving Average
- Simple: average of last n observations
- Weighted: different weights for observations
- Exponential: EWMA with decay

### ARIMA
- AR: Auto-regressive terms
- I: Integrated (differencing)
- MA: Moving average terms

### Exponential Smoothing
- Simple Exponential Smoothing (SES)
- Holt's Linear Trend
- Holt-Winters (additive/multiplicative seasonality)

## 5. Evaluation Metrics

- MAE: Mean Absolute Error
- MSE: Mean Squared Error
- RMSE: Root Mean Squared Error
- MAPE: Mean Absolute Percentage Error