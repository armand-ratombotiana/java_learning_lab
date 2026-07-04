# Performance in Time Series

## 1. ARIMA Estimation Complexity

ARIMA MLE estimation uses iteration that may take O(n × p × q) per step.

**Optimization**: Use conditional least squares (faster, slightly less accurate) for initial candidate selection, then MLE for the final model.

## 2. Large-Scale Forecasting

Forecasting millions of SKUs (retail demand forecasting) requires distributed computation.

**Strategy**: Use a global forecasting model (one model per SKU is too expensive).

```java
// Global model: one model for all SKUs with SKU as a feature
// Uses much less compute than one model per SKU
public class GlobalForecaster {
    private Map<String, FeatureTransformer> skuFeatures;
    private RegressionModel model;  // shared model
    
    public double[] forecast(String sku, int horizon) {
        double[][] features = skuFeatures.get(sku).extract(sku, horizon);
        return model.predict(features);
    }
}
```

## 3. Real-Time Anomaly Detection

Streaming time series (e.g., server metrics) must detect anomalies in O(1) per point.

**Algorithm**: Rolling statistics with decay.

```java
public class StreamingDetector {
    private double mean;
    private double std;
    private double alpha = 0.01;  // decay factor
    private long count = 0;
    
    public boolean isAnomaly(double value) {
        if (count < 100) {
            updateStats(value);
            return false;
        }
        double z = Math.abs(value - mean) / std;
        updateStats(value);
        return z > 3.0;  // 3-sigma threshold
    }
    
    private void updateStats(double value) {
        count++;
        double delta = value - mean;
        mean += alpha * delta;
        std = Math.sqrt((1 - alpha) * (std * std + alpha * delta * delta));
    }
}
```

## 4. Memory for Long Histories

Storing 5 years of minutely data = ~2.6M points per series.

**Optimization**: Downsample historical data (daily aggregates for old data, full resolution for recent).
