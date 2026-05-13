# ML Platform - Portfolio Capstone

## Overview
End-to-end MLOps platform with training pipelines, model serving, monitoring, and experiment tracking.

## Architecture
```
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│   Data       │───▶│  Training    │───▶│   Model      │
│   Pipeline   │    │  Pipeline    │    │   Registry   │
└──────────────┘    └──────────────┘    └──────────────┘
                          │                    │
       ┌──────────────────┤                    │
       │                  ▼                    ▼
┌──────────────┐  ┌──────────────┐      ┌──────────────┐
│   Feature    │  │   Metric     │      │   Model      │
│   Store     │  │   Logging    │      │   Serving    │
└──────────────┘  └──────────────┘      └──────────────┘
```

## Features
- Experiment tracking (MLflow-like)
- Hyperparameter tuning
- Model versioning
- Feature store
- A/B testing
- Drift detection
- Model monitoring

## Quick Start
```bash
cd 09-ml-platform
docker-compose up -d
```