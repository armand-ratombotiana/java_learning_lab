# How Time Series Analysis Works

## Core Concepts

### Autocorrelation (ACF)

The correlation of a series with its own lag:

$$ ACF(k) = \frac{\sum_{t=k+1}^{n} (y_t - \bar{y})(y_{t-k} - \bar{y})}{\sum_{t=1}^{n} (y_t - \bar{y})^2} $$

```java
public double[] autocorrelation(double[] series, int maxLag) {
    int n = series.length;
    double mean = Arrays.stream(series).average().orElse(0);
    double var = 0;
    for (double v : series) var += Math.pow(v - mean, 2);
    
    double[] acf = new double[maxLag + 1];
    for (int k = 0; k <= maxLag; k++) {
        double cov = 0;
        for (int t = k; t < n; t++) {
            cov += (series[t] - mean) * (series[t - k] - mean);
        }
        acf[k] = cov / var;
    }
    return acf;
}
```

### Stationarity — Augmented Dickey-Fuller Test

Tests the null hypothesis that a unit root is present (non-stationary).

```java
ADFTest adf = new ADFTest();
double pValue = adf.test(series);
boolean isStationary = pValue < 0.05;  // reject null → stationary
if (!isStationary) {
    series = difference(series);       // apply first difference
}
```

### ARIMA(p, d, q)

- **p**: number of autoregressive lags ($y_t$ depends on $y_{t-1}...y_{t-p}$)
- **d**: degree of differencing (number of times differenced to achieve stationarity)
- **q**: number of moving average lags ($y_t$ depends on past forecast errors)

```java
Arima arima = Arima.fit(series, p, d, q);
double[] forecast = arima.forecast(horizon);
```

### Exponential Smoothing (Holt-Winters)

Weighted average of past values where weights decay exponentially. Additive and multiplicative seasonality variants.

```java
// Holt-Winters exponential smoothing
double alpha = 0.2;  // level smoothing
double beta = 0.1;   // trend smoothing
double gamma = 0.3;  // seasonal smoothing (period=12)
```
