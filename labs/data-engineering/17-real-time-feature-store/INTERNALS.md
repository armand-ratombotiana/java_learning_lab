# Real-Time Feature Store Internals

## Feast Architecture
Feast Core (Registry): manages feature definitions, stores in PostgreSQL/MySQL. Feast Online Serving: FastAPI/gRPC server reading from online store. Feast Job Service: manages materialization jobs. Python SDK: user-facing API for defining features, generating training data, serving.

## Registry and Metadata
Feature Table: source + entities + features + batch source. Feature View: logical grouping of features with transformations. Feature Service: collection of feature views for serving. Data Source: batch or stream source for feature computation. Entity: join key (customer_id, user_id) with timestamp.

## Online Store Operations
Write path: materialization job computes features, writes to online store with upsert semantics. Read path: serving API receives entity keys, fetches feature values, returns in milliseconds. Backends: Redis (in-memory, fast), Cassandra (distributed, durable), DynamoDB (AWS managed). Redshift/BigQuery (analytical, slower).

## Offline Store Operations
Storage: Parquet files in S3/GCS, or tables in BigQuery/Redshift. Retrieval: Feast generates point-in-time correct SQL/queries. Returns training dataset as DataFrame. Supports time-partitioned retrieval for incremental training data generation.
