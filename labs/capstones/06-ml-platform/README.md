# ML Platform

A simplified ML platform in Java covering the full ML lifecycle: feature store (online/offline), training pipeline management, experiment tracking, model registry, REST model serving, drift detection, and A/B testing framework.

## Architecture Overview

```
┌──────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────┐
│ Feature  │  │ Training     │  │ Experiment   │  │ Model    │
│ Store    │─►│ Pipeline     │─►│ Tracker      │─►│ Registry │
│ (online/ │  │ (config/     │  │ (metrics/    │  │ (staging/ │
│  offline)│  │  run/monitor)│  │  params/log) │  │ production)
└──────────┘  └──────────────┘  └──────────────┘  └─────┬────┘
                                                         │
┌──────────┐  ┌──────────────┐  ┌──────────────┐        │
│ A/B Test │  │ Drift        │  │ Model Server │◄───────┘
│ Framework│  │ Detector     │  │ (REST/serve) │
│          │  │ (PSI/stat)   │  │              │
└──────────┘  └──────────────┘  └──────────────┘
```

## Features

- **FeatureStore**: Register feature groups, online ingestion, batch retrieval, historical export
- **TrainingPipeline**: Configurable runs with hyperparameters, train-split, status tracking
- **ExperimentTracker**: Experiment creation, metric logging, best-run comparison
- **ModelRegistry**: Versioned model storage, promotion to production, archival
- **ModelServer**: Deploy/undeploy production models, predict with latency tracking
- **DriftDetector**: PSI-based drift detection with configurable thresholds, alert history
- **ABTestFramework**: Multi-variant experiments, weighted assignment, result aggregation, winner selection

## Usage

```java
var store = new FeatureStore();
store.registerFeatureGroup("user_features", 
    List.of(new FeatureDefinition("age", FeatureType.DOUBLE, "User age", false)), "clickhouse");
store.ingestOnline("user_features", "u1", Map.of("age", 30.0));

var registry = new ModelRegistry();
var mv = registry.register("classifier", "/models/clf/v1", Map.of("accuracy", 0.95));
registry.promoteToProduction(mv.modelId());

var server = new ModelServer(registry);
server.deploy("classifier");
var prediction = server.predict("classifier", Map.of("age", 30.0, "income", 75000.0));
```
