# How Real-Time Feature Store Works

1. Users define feature views in Python using Feast SDK
2. Registry stores feature definitions and metadata
3. Feature engineering jobs compute features from source data (Spark, Flink)
4. Batch features stored in offline store (S3, BigQuery) for training
5. Materialization jobs push latest feature values to online store
6. Model training: get_historical_features() generates point-in-time correct training data
7. Model inference: get_online_features() retrieves features by entity key in real-time
8. Consistency validation jobs compare online vs offline feature values
