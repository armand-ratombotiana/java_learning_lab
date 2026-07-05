# Internals: ML Platform

## Core Components
- **DataIngestor**: Connects to data sources (Kafka, JDBC, S3), validates schema, partitions data
- **FeaturePipeline**: Spark/Java pipeline for feature computation; supports online (Redis) and offline (Parquet) sinks
- **TrainingOrchestrator**: Manages training jobs; supports local and distributed execution (Spark)
- **ModelRegistry**: Versioned model store with metadata (MLflow-compatible API)
- **ServingService**: Online prediction server (Netty-based, model loaded in memory)
- **BatchPredictor**: Scheduled batch inference; reads from feature store, writes predictions
- **MonitorService**: Computes drift metrics, tracks prediction distribution, alerts on anomalies

## API
- `POST /api/v1/models/register` — Register model
- `GET /api/v1/models/{name}/versions` — List model versions
- `POST /api/v1/models/{name}/versions/{version}/deploy` — Deploy to serving
- `POST /api/v1/predict` — Online prediction
- `POST /api/v1/batch-predict` — Submit batch prediction job
- `GET /api/v1/monitoring/drift` — Get drift metrics
