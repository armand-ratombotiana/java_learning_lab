# Internals: ARIMA Estimation

## How ARIMA Parameters Are Estimated

The Box-Jenkins methodology has three steps:

### 1. Identification (determine p, d, q)

- d: number of differences until ACF decays quickly
- p: number of significant lags in PACF (partial autocorrelation)
- q: number of significant lags in ACF

### 2. Estimation (fit the coefficients)

ARIMA coefficients are estimated via **Maximum Likelihood Estimation (MLE)** or **Conditional Least Squares**.

The ARIMA(p,d,q) model after differencing (d times) becomes ARMA(p,q):

$$ y_t = c + \phi_1 y_{t-1} + \ldots + \phi_p y_{t-p} + \varepsilon_t + \theta_1 \varepsilon_{t-1} + \ldots + \theta_q \varepsilon_{t-q} $$

MLE finds $\phi$ and $\theta$ that maximize the probability of observing the data:

```java
// Simplified MLE for AR(1): y_t = c + phi * y_{t-1} + e_t
public double estimateAR1(double[] series) {
    int n = series.length;
    double sumYY = 0, sumYprev = 0, sumYprev2 = 0;
    for (int t = 1; t < n; t++) {
        sumYY += series[t] * series[t-1];
        sumYprev += series[t-1];
        sumYprev2 += series[t-1] * series[t-1];
    }
    return (sumYY - sumYprev * Arrays.stream(series).sum() / n) /
           (sumYprev2 - sumYprev * sumYprev / n);
}
```

### 3. Diagnostic Checking

Residuals should be white noise (no autocorrelation):

```java
boolean validModel = ljungBoxTest(residuals, 20) > 0.05;
```

Ljung-Box test: null hypothesis = residuals are independently distributed. p > 0.05 → fail to reject → residuals are white noise → model is adequate.

## Kalman Filter (State Space)

Many time series models can be represented in state space form and estimated with the Kalman filter, which is computationally efficient (O(n)) and handles missing values naturally.
