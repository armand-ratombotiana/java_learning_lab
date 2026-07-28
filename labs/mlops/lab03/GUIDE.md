# Lab 03: Model Registry & Versioning — Guide

## Step 1: Understand Model Registry Concepts

A Model Registry provides:
- **Versioning**: Each model iteration gets a unique version
- **Stage Management**: Models move through None → Staging → Production → Archived
- **Lineage**: Track which data, code, and hyperparameters produced each model
- **Champion/Challenger**: Production (champion) vs candidate (challenger) models

## Step 2: Explore the ModelRegistry

The `ModelRegistry` class manages model versions and their lifecycle stages.

## Step 3: Compile and Run

```bash
cd lab03/src
javac com/mlops/lab03/*.java
java com.mlops.lab03.ModelRegistryLab
```

## Step 4: Model Lifecycle

```
Version 1 (None) → Version 1 (Staging) → Version 1 (Production) → Version 1 (Archived)
                                                 ↓
                                         Version 2 (Staging) → Version 2 (Production)
```

## MLflow Model Registry Mapping

| Java Concept | MLflow Equivalent |
|-------------|-------------------|
| ModelRegistry | MlflowClient ModelRegistry |
| ModelVersion | RegisteredModelVersion |
| Stage | Stage (None/Staging/Production/Archived) |
| promoteToProduction | transition_stage |
| getProductionModel | get_latest_versions(name, stages=["Production"]) |
| listVersions | search_model_versions |

## Best Practices
- Automatically archive previous production version when promoting
- Validate model metrics before promoting to production
- Tag versions with experiment run IDs for full traceability
- Use semantic versioning (major.minor.patch) for model versions
