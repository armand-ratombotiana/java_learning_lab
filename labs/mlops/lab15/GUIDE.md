# Lab 15: Production ML Architecture — Guide

## Step 1: End-to-End Architecture

```
                          ┌──────────────────────────────────┐
                          │          ML Platform             │
                          │  ┌──────────────────────────┐    │
                   ┌──────▼──▼──────┐    ┌─────────────┐ │    │
                   │  Data Pipeline │───▶│ Feature     │ │    │
                   │  (Airflow/Dag) │    │ Store       │ │    │
                   └──────┬─────────┘    └──────┬──────┘ │    │
                          │                     │        │    │
                   ┌──────▼─────────────────────▼──────┐ │    │
                   │        Training Pipeline          │ │    │
                   │  (Validation → Featurize → Train) │ │    │
                   └──────────────┬────────────────────┘ │    │
                                  │                      │    │
                   ┌──────────────▼──────────────┐       │    │
                   │    Model Registry           │       │    │
                   │  (MLflow: versions, stages) │       │    │
                   └──────────────┬──────────────┘       │    │
                                  │                      │    │
                   ┌──────────────▼──────────────┐       │    │
                   │   Serving Infrastructure   │       │    │
                   │  (Docker → K8s → Istio)    │───────┘    │
                   └──────────────┬──────────────┘            │
                                  │                           │
                   ┌──────────────▼──────────────┐            │
                   │   Monitoring & Observability│            │
                   │   (Drift, Metrics, Alerts)  │            │
                   └─────────────────────────────┘            └──┘
```

## Step 2: Explore Architecture Components

The `ProductionMLArchitecture` class models each component as a Java class with responsibilities and interactions.

## Step 3: Run the Simulation

```bash
cd lab15/src
javac com/mlops/lab15/*.java
java com.mlops.lab15.ProductionMLArchitectureLab
```

## Step 4: Architecture Principles

| Principle | Description |
|-----------|-------------|
| Separation of Concerns | Each component has a single responsibility |
| API-Driven | All components communicate via well-defined APIs |
| Observability | Every component emits metrics, logs, traces |
| Idempotency | Pipelines can be safely retried |
| Immutability | Data and models are versioned and immutable |
| Automation | CI/CD automates testing, validation, deployment |

## Case Studies

### Case Study 1: Real-time Fraud Detection
- **Scale**: 10K transactions/sec, P99 < 50ms
- **Architecture**: Kafka → Feature Store (Redis) → Model Server (Spring Boot) → K8s → Alert
- **Key Design**: In-memory feature cache, horizontal scaling via HPA, shadow deployment for new models

### Case Study 2: Batch Recommendation Engine
- **Scale**: 100M users, daily batch refresh
- **Architecture**: Airflow → Spark (feature eng) → training → batch scoring → S3 → serving API
- **Key Design**: Offline pre-computation, incremental updates, A/B testing framework

### Case Study 3: Multi-tenant ML Platform
- **Scale**: 50 teams, 500 models
- **Architecture**: Namespace-per-team, shared feature store, model registry, monitoring
- **Key Design**: Resource quotas, RBAC, cost allocation, model governance
