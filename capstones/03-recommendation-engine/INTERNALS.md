# Internals: Recommendation Engine

## Core Components
- **DataIngestor**: Consumes user interaction events (ratings, clicks, purchases) from Kafka
- **ALSModelTrainer**: Spark/Java implementation of ALS for matrix factorization
- **FeatureService**: Computes content-based features (TF-IDF on item descriptions, category embeddings)
- **ANNSearcher**: Approximate nearest neighbor search using HNSW (Hierarchical Navigable Small World) index
- **HybridRanker**: Combines collaborative score + content score + popularity boost
- **RecService**: REST API for serving recommendations (GET /recommendations/{userId})
- **FeedbackCollector**: Logs recommendation impressions and user interactions for retraining

## Model Training
- ALS hyperparameters: rank=50, alpha=40 (implicit feedback), iterations=20, lambda=0.01
- Training frequency: daily incremental + weekly full retrain
- Cold start fallback: popularity-based for new users, content-based for new items

## Storage
- Redis: User and item factor matrices, popularity scores, candidate sets
- PostgreSQL: Interaction history, model metadata, evaluation metrics
- S3/MinIO: Model artifacts, training datasets
