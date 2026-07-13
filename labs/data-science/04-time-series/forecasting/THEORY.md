# Time Series Theory & Intuition

## 💡 The Problem: Temporal Dependence
In standard Machine Learning (like predicting house prices), we assume that every data point is independent. The price of House A does not directly affect the price of House B.

In **Time Series** data (like predicting stock prices, server CPU load, or daily ice cream sales), this assumption is completely broken. 
The ice cream sales on Tuesday are heavily dependent on the sales from Monday. Furthermore, the sales on Tuesday are heavily dependent on the sales from *last* Tuesday. This is called **Temporal Dependence**. Standard ML algorithms (like a basic Random Forest) struggle with this because they don't understand the sequence of the rows.

## 🧱 Components of a Time Series
To forecast the future, we must decompose the past into three distinct signals:
1. **Trend**: The long-term progression of the series. (e.g., Ice cream sales are slowly growing year over year because the population is growing).
2. **Seasonality**: A repeating, predictable pattern at fixed intervals. (e.g., Ice cream sales spike every July, and drop every December. Or, server load spikes every morning at 9 AM).
3. **Residual (Noise)**: The random, unpredictable fluctuations left over after you subtract the Trend and Seasonality.

## 🛑 The Stationarity Requirement
Most statistical forecasting models (like ARIMA) cannot handle data with Trends or Seasonality. They require the data to be **Stationary**.
A stationary time series has statistical properties (mean, variance) that do *not* change over time. It looks like pure white noise.

**Why?** Because if the mean and variance are constantly shifting, the model cannot reliably project those rules into the future. 

### How to make data Stationary: Differencing
If your data has an upward trend, you can make it stationary by taking the **First Difference**.
Instead of modeling the actual value $Y_t$, you model the *change* between today and yesterday: $Y'_t = Y_t - Y_{t-1}$.
If the original data was a straight line going up, the differenced data is a flat horizontal line (stationary).