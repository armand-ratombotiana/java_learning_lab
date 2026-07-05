# Theory: ML Platform

## ML Pipeline Architecture
End-to-end ML system: data ingestion -> feature engineering -> model training -> model evaluation -> model deployment -> prediction serving -> monitoring. Each stage is a separate, composable component.

## Feature Engineering
Transforming raw data into model-ready features. Includes: scaling (standardization, min-max), encoding (one-hot, label, target), text features (TF-IDF, embeddings), datetime features, and aggregation features.

## Model Registry
Centralized repository for model artifacts, metadata (version, author, hyperparameters, metrics), and lifecycle management (staging -> production -> archived). Enables reproducibility and governance.

## A/B Testing
Route traffic between model versions to compare performance. Metrics tracked: accuracy, latency, business KPIs. Statistical significance testing determines winner.

## Model Serving
- **Online**: Real-time inference via REST/gRPC API. Low latency (< 100ms). Single request at a time.
- **Batch**: Offline inference on large datasets. High throughput. Periodic (daily/weekly) schedules.

## Monitoring
Model performance degrades over time (concept drift, data drift). Monitoring tracks: prediction distribution, feature distribution, accuracy metrics (when labels arrive), latency, throughput.
