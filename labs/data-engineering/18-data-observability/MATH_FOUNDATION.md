# Math Foundation for Data Observability

## Anomaly Detection
```
ZScore = (value - mean) / stddev
Outlier = |Z| > 3 (strict) or |Z| > 2 (sensitive)
IQR = Q3 - Q1
Outlier: x < Q1 - 1.5*IQR or x > Q3 + 1.5*IQR
```

## Freshness
```
Freshness_Lag = CURRENT_TIMESTAMP - MAX(update_timestamp)
SLA_Breach = Freshness_Lag > SLA_Threshold
Historical_P50 = PERCENTILE(freshness_lags, 0.5)
Historical_P95 = PERCENTILE(freshness_lags, 0.95)
```

## Volume Monitoring
```
Expected = EMA(historical_volumes) or same_day_last_week
Deviation = (Actual - Expected) / Expected
Alert_Threshold = |Deviation| > 0.5 (50% change)
Seasonal = Expected * day_of_week_factor * hour_factor
```

## Distribution Comparison
```
KS_Stat = max(|CDF_Actual(x) - CDF_Expected(x)|)
Drift_Threshold = 0.1 (significant)
Jensen_Shannon = SQRT(mean divergence)
Earth_Movers = Wasserstein distance between distributions
```
