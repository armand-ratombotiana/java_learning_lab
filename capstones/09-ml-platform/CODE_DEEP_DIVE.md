# Code Deep Dive: ML Platform

## Feature Pipeline

`FeaturePipeline` uses Apache Spark for distributed feature computation. It reads raw data from Parquet/S3, applies transformations via Spark SQL or UDFs, and writes to two sinks: online (Redis via batch insert) and offline (Parquet partitioned by date). Features are computed in batch jobs scheduled by Airflow/Dagster.

## Training Orchestrator

`TrainingOrchestrator` coordinates training jobs. It reads training config (model type, hyperparameters, feature list), splits data from the offline feature store (train/test/validate), spawns a training process (local thread or Spark job), captures metrics, and registers the model.

## Model Serving

`ServingService` loads model artifacts from the model registry. Models are cached in a `ConcurrentHashMap<String, Model>` with version management. Prediction requests are routed based on model version headers. A/B testing: configurable % of traffic routed to candidate model version.

## Monitoring

`MonitorService` periodically computes PSI for feature distributions (comparing current window vs training window). It also tracks prediction confidence distribution. If drift exceeds threshold, it triggers an alert and optionally rolls back model version.
