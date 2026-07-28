# Lab 07: CI/CD for ML Pipelines — Guide

## Step 1: Understand ML CI/CD Pipeline Stages

```
┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐
│ Code │ │ Data │ │Feature│ │Train │ │Eval  │ │Deploy│
│ Push │→│ Val  │→│ Eng  │→│Model │→│Model │→│Model │
└──────┘ └──────┘ └──────┘ └──────┘ └──────┘ └──────┘
```

## Step 2: Explore the Pipeline DSL

The `CiCdPipeline` class models pipeline stages, their dependencies, and execution logic.

## Step 3: Compile and Run

```bash
cd lab07/src
javac com/mlops/lab07/*.java
java com.mlops.lab07.CiCdForMLPipelineLab
```

## Step 4: The generated GitHub Actions workflow

The lab generates a `.github/workflows/ml-pipeline.yml` file.

## Key CI/CD Concepts for ML

| Concept | Description |
|---------|-------------|
| Data Validation | Check schema, distributions, quality before training |
| Model Training | Triggered on code/data changes |
| Model Evaluation | Compare against champion metrics |
| Registry Gate | Only push if metrics exceed threshold |
| Deployment Gate | Manual approval or auto-promote |
| Retrain Trigger | Scheduled or event-driven (data drift) |

## Best Practices
- Keep pipeline stages idempotent
- Cache dependencies (Maven/Gradle) and datasets
- Parallelize independent stages (data validation + feature engineering)
- Add model evaluation as a quality gate before deployment
- Use matrix builds for multi-model workflows
