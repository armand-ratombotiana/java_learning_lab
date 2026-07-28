# Lab 04: Feature Store Architecture — Guide

## Step 1: Understand Feature Store Concepts

```
┌─────────────────────────────────────────────┐
│              Feature Store                   │
│  ┌────────────────┐  ┌────────────────┐     │
│  │   Offline Store │  │   Online Store │     │
│  │  (Parquet/Hive) │  │   (Redis/DDB)  │     │
│  │  Historical     │  │   Low-latency  │     │
│  │  Batch compute  │  │   Real-time    │     │
│  └────────────────┘  └────────────────┘     │
└─────────────────────────────────────────────┘
```

## Step 2: Implement FeatureGroup

A `FeatureGroup` defines a logical grouping of features with a transformation function.

## Step 3: Implement FeatureStore

The store manages both offline (simulated file-based) and online (in-memory HashMap) storage.

## Step 4: Compile and Run

```bash
cd lab04/src
javac com/mlops/lab04/*.java
java com.mlops.lab04.FeatureStoreLab
```

## Key Concepts

| Concept | Description |
|---------|-------------|
| Offline Store | Batch-computed features for training; stored as Parquet/Delta |
| Online Store | Low-latency (ms) feature retrieval for inference; Redis/DynamoDB |
| Point-in-Time Join | Historical feature values exactly as they were at prediction time |
| Feature Serving | REST/gRPC endpoint returning feature vectors |

## Best Practices
- Always use point-in-time joins to prevent data leakage
- Pre-compute offline features in scheduled pipelines
- Use TTL-based eviction for online features
- Monitor feature value distributions for drift
