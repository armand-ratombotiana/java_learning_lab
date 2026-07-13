# Mathematical Foundation of Time Series

## 📏 Autocorrelation (ACF)
In standard statistics, correlation measures how two different variables (X and Y) move together.
In Time Series, **Autocorrelation** measures how a variable moves with *itself*, shifted by a specific time delay (a **Lag**).

Let $Y$ be our time series. The autocorrelation at lag $k$ (denoted $r_k$) is:
$$ r_k = \frac{\sum_{t=k+1}^{T} (Y_t - \bar{Y})(Y_{t-k} - \bar{Y})}{\sum_{t=1}^{T} (Y_t - \bar{Y})^2} $$

- If $r_1$ is high, it means today's value is highly correlated with yesterday's value.
- If $r_7$ is high, it indicates a strong weekly seasonality.

## 📈 The ARIMA Model
ARIMA stands for **Auto-Regressive Integrated Moving Average**. It is the most classic statistical forecasting model. It combines three separate mathematical concepts:

### 1. AR (Auto-Regressive) - The "P" parameter
This part predicts the future value using a linear combination of its *past values*.
$$ Y_t = c + \phi_1 Y_{t-1} + \phi_2 Y_{t-2} + \dots + \phi_p Y_{t-p} + e_t $$
Where $\phi$ are the learned weights, and $p$ is the number of past days to look at.

### 2. I (Integrated) - The "D" parameter
This represents the number of **Differences** needed to make the data stationary before modeling.
If $d=1$, the model operates on $Y'_t = Y_t - Y_{t-1}$.

### 3. MA (Moving Average) - The "Q" parameter
This part predicts the future value using a linear combination of past *forecast errors* (residuals).
$$ Y_t = c + e_t + \theta_1 e_{t-1} + \theta_2 e_{t-2} + \dots + \theta_q e_{t-q} $$
Where $\theta$ are the learned weights, and $e_t$ is white noise (the error).

An ARIMA(p, d, q) model combines all three equations.

## 🌊 Simple Exponential Smoothing (SES)
ARIMA is mathematically complex to implement from scratch. A simpler, highly effective alternative for data without trend or seasonality is **Exponential Smoothing**.

Instead of treating all past observations equally, SES gives exponentially decreasing weight to older observations.
The forecast for the next time step $\hat{Y}_{t+1}$ is:
$$ \hat{Y}_{t+1} = \alpha Y_t + (1 - \alpha) \hat{Y}_t $$
Where:
- $\alpha$ is the smoothing parameter ($0 < \alpha \le 1$).
- If $\alpha$ is close to 1, the model heavily trusts the most recent observation.
- If $\alpha$ is close to 0, the model heavily trusts the historical average.