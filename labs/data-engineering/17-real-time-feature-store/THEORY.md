# Real-Time Feature Store Theory

## Feature Store Architecture
Registry: stores feature definitions, metadata, and lineage. Offline Store: historical feature values for training data generation (S3, BigQuery, Snowflake). Online Store: low-latency feature serving (Redis, Cassandra, DynamoDB). Serving Layer: REST/gRPC API for real-time retrieval. Transformation Layer: compute features from source data (Spark, Flink).

## Point-in-Time Correctness
When joining features to labels, each label timestamp must only use features computed from data available at or before that timestamp. Without point-in-time joins, training data leaks future information, causing models to appear more accurate than they will be in production. Implemented via AS OF join or temporal join (SQL: FOR SYSTEM_TIME AS OF).

## Feature Materialization
Process of computing features from source data and writing to online store. Batch materialization: scheduled Spark/Airflow jobs compute features and push to online store. Streaming materialization: Flink/Kafka pipelines continuously update online store. Materialization must be monitored for freshness and consistency.

## Training-Serving Skew
Features computed during training must exactly match features computed during serving. Differences (skew) cause model degradation. Prevention: use same transformation code in both paths, validate distributions match, monitor online vs offline feature values.
