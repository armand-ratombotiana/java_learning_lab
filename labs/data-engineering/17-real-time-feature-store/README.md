# Real-Time Feature Store

## Overview
A feature store is a centralized repository for feature engineering, storage, and serving. It enables feature reuse, training-serving consistency, and point-in-time correct joins for ML models. This lab covers Feast, feature serving, online/offline stores, and point-in-time joins.

## Key Concepts
- **Feature Store**: Centralized platform for feature engineering, storage, discovery, and serving
- **Online Store**: Low-latency feature serving for real-time inference (Redis, Cassandra)
- **Offline Store**: Historical feature storage for training data generation (S3, BigQuery)
- **Point-in-Time Join**: Correctly join features to labels using temporal alignment
- **Feast**: Open-source feature store with online/offline stores and serving API
- **Feature Serving**: REST/gRPC API for real-time feature retrieval during model inference

## Learning Objectives
1. Understand feature store architecture (online, offline, registry)
2. Deploy Feast feature store with Redis online store and BigQuery offline store
3. Define feature views and feature services in Feast
4. Implement point-in-time correct join for training data
5. Serve features via REST API for real-time model inference
6. Monitor feature freshness and consistency between online and offline stores
