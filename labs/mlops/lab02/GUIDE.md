# Lab 02: Experiment Tracking with MLflow — Guide

## Step 1: Start MLflow Server

```bash
pip install mlflow
mlflow server --host 0.0.0.0 --port 5000
```

## Step 2: Understand the TrackingClient

The `MlflowTrackingClient` wraps MLflow's REST API. Key endpoints:
- `POST /api/2.0/mlflow/experiments/create`
- `POST /api/2.0/mlflow/runs/create`
- `POST /api/2.0/mlflow/runs/log-parameter`
- `POST /api/2.0/mlflow/runs/log-metric`

## Step 3: Run the Lab

```bash
cd lab02/src
javac com/mlops/lab02/*.java
java com.mlops.lab02.ExperimentTrackingLab
```

## Step 4: View Results

Open http://localhost:5000 in your browser to see experiments and runs.

## Key Concepts

| MLflow Concept | Java Representation |
|---------------|-------------------|
| Experiment | createExperiment(name) |
| Run | createRun(experimentId) |
| Parameter | logParam(runId, key, value) |
| Metric | logMetric(runId, key, value, step) |
| Artifact | logArtifact(runId, localPath) |

## Best Practices
- Use consistent parameter naming across experiments
- Log all hyperparameters before training starts
- Log metrics at each epoch/step for full visibility
- Tag runs with meaningful metadata (dataset, environment, etc.)
