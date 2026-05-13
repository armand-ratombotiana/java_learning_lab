# Fraud Detection System - Portfolio Capstone

## Overview
Real-time ML-based fraud detection system with streaming data processing, anomaly detection, and automated risk assessment.

## Architecture
```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Incoming   │────▶│   Apache     │────▶│   Fraud      │
│  Transactions│     │   Kafka      │     │  Detection   │
└──────────────┘     └──────────────┘     └──────────────┘
                                                │
                        ┌───────────────────────┼───────────────────────┐
                        │                       │                       │
                        ▼                       ▼                       ▼
               ┌──────────────┐        ┌──────────────┐        ┌──────────────┐
               │   ML Model   │        │   Rule       │        │   Alert      │
               │   Pipeline   │        │   Engine     │        │   Manager    │
               └──────────────┘        └──────────────┘        └──────────────┘
```

## Tech Stack
- **Framework**: Spring Boot 3.2.x, Spring ML
- **ML**: TensorFlow, Deeplearning4j
- **Streaming**: Kafka Streams, Apache Flink
- **Storage**: PostgreSQL, Redis, MongoDB
- **Deployment**: Docker, Kubernetes

## Features
- Real-time transaction analysis
- ML-based anomaly detection
- Rule engine for fraud patterns
- Risk scoring system
- Automated alerts
- Case management
- Reporting & analytics

## Quick Start
```bash
cd 02-fraud-detection
docker-compose up -d
```

## ML Models
1. **Isolation Forest** - Anomaly detection
2. **Random Forest** - Binary classification
3. **Neural Network** - Pattern recognition
4. **Ensemble** - Combined scoring