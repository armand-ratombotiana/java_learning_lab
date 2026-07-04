# Refactoring Time Series Code

## Smell: Model Selection Hardcoded

ARIMA(p,d,q) values are hardcoded in the script.

**Refactor**: Auto-ARIMA that searches the parameter space.

```java
public class AutoArima {
    public static Arima fit(double[] series, int maxP, int maxQ, int maxD) {
        double bestAIC = Double.MAX_VALUE;
        Arima bestModel = null;
        for (int d = 0; d <= maxD; d++) {
            double[] diffed = difference(series, d);
            for (int p = 0; p <= maxP; p++) {
                for (int q = 0; q <= maxQ; q++) {
                    if (p == 0 && q == 0) continue;
                    try {
                        Arima model = Arima.fit(diffed, p, 0, q);
                        if (model.AIC() < bestAIC) {
                            bestAIC = model.AIC();
                            bestModel = model;
                        }
                    } catch (Exception e) { /* skip unstable models */ }
                }
            }
        }
        return bestModel;
    }
}
```

## Smell: Forecasting Logic Mixed with Business Logic

The same class computes forecasts and generates business recommendations.

**Refactor**: Separate concerns.

```java
// ForecastingService: pure time series modeling
public class ForecastingService {
    public ForecastResult forecast(double[] history, int horizon) { /* ... */ }
}

// InventoryPlanner: business logic using forecasts
public class InventoryPlanner {
    private final ForecastingService forecaster;
    
    public ReorderPlan generatePlan(double[] salesHistory) {
        ForecastResult forecast = forecaster.forecast(salesHistory, 3);
        return ReorderPlan.fromForecast(forecast);
    }
}
```

## Smell: Manual Feature Engineering for Lag Features

Lags created ad-hoc with inline code.

**Refactor**: Create lag feature transformer.

```java
public class LagFeatureGenerator {
    public static double[][] createLagFeatures(double[] series, int[] lags) {
        int n = series.length;
        double[][] features = new double[n][lags.length];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < lags.length; j++) {
                int lag = lags[j];
                features[i][j] = (i >= lag) ? series[i - lag] : 0;
            }
        }
        return features;
    }
}
```
