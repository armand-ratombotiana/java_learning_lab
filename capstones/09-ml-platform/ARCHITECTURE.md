# Architecture: ML Platform

## High-Level Architecture
```
[Data Sources] --> [Data Pipeline (Spark)] --> [Feature Store (Redis/S3)]
                                                      |
                      +-------------------------------+------------------+
                      |                               |                  |
              [Training Pipeline]           [Online Serving]    [Batch Serving]
                      |                               |                  |
              [Model Registry]              [Prediction API]   [Scheduled Jobs]
                      |                               |
              [A/B Test Controller]       [Monitor Service]
```

## Technology Stack
- **Language**: Java 17 + Python (for training scripts)
- **Framework**: Spring Boot 3.x
- **Data Processing**: Apache Spark (feature engineering)
- **Model Training**: XGBoost4J, scikit-learn (via Py4J), PyTorch (via JNI)
- **Model Serving**: ONNX Runtime Java
- **Feature Store**: Redis (online), Parquet on S3/MinIO (offline)
- **Registry**: PostgreSQL + S3 (metadata + artifacts)
- **Orchestration**: Custom scheduler (or Airflow/Dagster compatible)
- **Monitoring**: Prometheus + Grafana
- **Containerization**: Docker + docker-compose

## API Endpoints
- `POST /api/v1/predict` — Online prediction
- `POST /api/v1/train` — Submit training job
- `GET /api/v1/models` — List models
- `GET /api/v1/monitoring/drift` — Drift metrics
- `POST /api/v1/features/ingest` — Compute and store features
