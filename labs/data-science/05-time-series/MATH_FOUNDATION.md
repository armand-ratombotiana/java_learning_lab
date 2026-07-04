# Math Foundation: Time Series

## 1. Autoregressive Model (AR(p))

$$ y_t = c + \phi_1 y_{t-1} + \phi_2 y_{t-2} + \ldots + \phi_p y_{t-p} + \varepsilon_t $$

The next value is a linear combination of p previous values plus noise.

## 2. Moving Average Model (MA(q))

$$ y_t = \mu + \varepsilon_t + \theta_1 \varepsilon_{t-1} + \ldots + \theta_q \varepsilon_{t-q} $$

The next value depends on previous forecast errors.

## 3. ARIMA(p,d,q)

AR + differencing + MA: the workhorse of time series forecasting.

## 4. Seasonality (SARIMA)

$$ SARIMA(p,d,q)(P,D,Q)_m $$

Adds seasonal terms at lag m (e.g., m=12 for monthly data with yearly seasonality).

## 5. Evaluation Metrics

### MAE (Mean Absolute Error)
$$ MAE = \frac{1}{n} \sum_{t=1}^{n} |y_t - \hat{y}_t| $$

### RMSE (Root Mean Squared Error)
$$ RMSE = \sqrt{\frac{1}{n} \sum_{t=1}^{n} (y_t - \hat{y}_t)^2} $$

### MAPE (Mean Absolute Percentage Error)
$$ MAPE = \frac{100\%}{n} \sum_{t=1}^{n} \left|\frac{y_t - \hat{y}_t}{y_t}\right| $$

```java
public class ForecastMetrics {
    public static double mae(double[] actual, double[] forecast) {
        double sum = 0;
        for (int i = 0; i < actual.length; i++) {
            sum += Math.abs(actual[i] - forecast[i]);
        }
        return sum / actual.length;
    }
    
    public static double rmse(double[] actual, double[] forecast) {
        double sum = 0;
        for (int i = 0; i < actual.length; i++) {
            sum += Math.pow(actual[i] - forecast[i], 2);
        }
        return Math.sqrt(sum / actual.length);
    }
    
    public static double mape(double[] actual, double[] forecast) {
        double sum = 0;
        for (int i = 0; i < actual.length; i++) {
            sum += Math.abs((actual[i] - forecast[i]) / actual[i]);
        }
        return 100.0 * sum / actual.length;
    }
}
```
