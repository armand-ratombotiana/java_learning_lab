# Lab 08: Model Monitoring & Observability — Guide

## Step 1: Understand Drift Types

| Drift Type | Description | Detection Method |
|-----------|-------------|------------------|
| Data Drift | Input feature distribution changes | PSI, KL divergence, JS divergence |
| Concept Drift | Relationship features→target changes | Performance monitoring |
| Prediction Drift | Output distribution changes | PSI on predictions |

## Step 2: Implement Drift Detector

The `DriftDetector` computes PSI (Population Stability Index) and KL divergence between reference and current distributions.

## Step 3: Implement Performance Monitor

The `PerformanceMonitor` tracks prediction accuracy, latency, and error rates over sliding time windows.

## Step 4: Compile and Run

```bash
cd lab08/src
javac com/mlops/lab08/*.java
java com.mlops.lab08.ModelMonitoringLab
```

## Key Metrics

| Metric | Description | Alert Threshold |
|--------|-------------|-----------------|
| PSI | Population Stability Index | > 0.25 (major drift) |
| Accuracy | Prediction accuracy | < 0.85 |
| P99 Latency | 99th percentile response time | > 500ms |
| Error Rate | % of failed predictions | > 1% |
| Data Volume | Predictions per hour | < 50% baseline |

## Best Practices
- Monitor both data drift and concept drift
- Set up alerts at multiple severity levels (warning, critical)
- Store monitoring data in time-series DB (InfluxDB, TimescaleDB)
- Use sliding windows for metric computation
- Correlate drift events with retraining triggers
