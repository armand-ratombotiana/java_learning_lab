# Lab 07 Interview Questions

## Q1: What is the difference between SMA and EMA?
SMA gives equal weight to all observations in the window. EMA gives more weight to recent observations, making it more responsive to changes.

## Q2: What is autocorrelation and why does it matter?
Autocorrelation measures correlation between a series and its lagged values. It matters because it violates independence assumptions in standard regression.

## Q3: How do you identify trend and seasonality?
Trend is the long-term direction; seasonality is periodic fluctuations. Decomposition separates these components from residuals.

## Q4: What is stationarity and why is it important?
A stationary series has constant mean, variance, and autocorrelation over time. Many forecasting methods require stationarity.

## Q5: What is the difference between additive and multiplicative decomposition?
Additive: y = T + S + R (constant seasonal amplitude). Multiplicative: y = T × S × R (seasonal amplitude grows with trend).
