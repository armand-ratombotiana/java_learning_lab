# How It Works: ML Platform

1. Raw data is ingested from source (Kafka, database, files) into the data lake
2. Feature engineering pipeline transforms raw data into features (scaling, encoding, aggregations)
3. Features are stored in the Feature Store (Redis for online, S3 for offline)
4. Training pipeline reads training dataset (features + labels) from feature store
5. Model is trained (scikit-learn, XGBoost, PyTorch) with hyperparameter tuning
6. Training metrics (accuracy, precision, recall, F1) are logged
7. Model artifact + metadata is registered in Model Registry
8. Model is evaluated against a holdout set; if metrics pass threshold, promoted to staging
9. Manual approval promotes to production
10. Online serving: model loaded into memory, exposes REST/gRPC prediction endpoint
11. Batch serving: model runs on scheduled basis, predictions written to database
12. Monitor: feature distributions, prediction distributions, accuracy (when labels arrive)
