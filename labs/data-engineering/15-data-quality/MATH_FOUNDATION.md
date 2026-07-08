# Math Foundation for Data Quality Engineering

## Quality Score
```
Completeness = NonNullRows / TotalRows * 100
Uniqueness = DistinctKeys / TotalRows * 100
OverallScore = WeightedAvg(dimension_scores)
```

## Freshness SLA
```
Freshness = CURRENT_TIMESTAMP - MAX(updated_at)
WithinSLA = Freshness <= SLAThreshold
LagPercentile = PERCENTILE(lags, 0.95)
```

## Anomaly Detection
```
ZScore = (value - mean) / stddev
Outlier = |Z| > 3 (99.7% confidence)
MovingAvg = AVG(values) OVER (last N periods)
```

## Volume Monitoring
```
ExpectedVolume = MovingAvg(30d)
VolumeDeviation = (Actual - Expected) / Expected
AlertThreshold = VolumeDeviation > 0.5 (50% change)
```
