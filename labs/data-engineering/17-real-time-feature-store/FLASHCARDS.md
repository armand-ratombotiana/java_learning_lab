# Flashcards: Real-Time Feature Store

## Card 1
**Front**: What is a feature store?
**Back**: Centralized repository for feature engineering, storage, discovery, and serving for ML

## Card 2
**Front**: What is the difference between online and offline store?
**Back**: Online: low-latency for inference (Redis). Offline: historical for training (S3, BigQuery).

## Card 3
**Front**: What is a point-in-time join?
**Back**: Join that uses timestamps to ensure features don't use future data relative to labels

## Card 4
**Front**: What is Feast?
**Back**: Open-source feature store by Tecton with Python SDK, online/offline stores, and serving API

## Card 5
**Front**: What is feature materialization?
**Back**: Computing features from source data and writing to online store for low-latency serving
