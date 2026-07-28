# MLOps Interview Cheatsheet (One-Page Reference)

## ML Pipeline Components

```
Raw Data → Validation → Feature Engineering → Training → Evaluation → Registry → Deployment → Monitoring
```

## Key Terms & Definitions

| Term | Definition |
|------|------------|
| **Data Drift** | Input distribution changes over time |
| **Concept Drift** | Relationship between features and target changes |
| **Model Staleness** | Model trained on outdated data distribution |
| **Feature Store** | Centralized repository for consistent feature computation |
| **Model Registry** | Versioned storage for trained models with metadata |
| **Champion/Challenger** | Production model vs candidate models for comparison |
| **Canary Deployment** | Gradual rollout to subset of traffic |
| **Blue-Green** | Two identical environments, switch traffic atomically |
| **shadow deployment** | New model runs in parallel without serving traffic |

## Common Metrics

### Classification
- Accuracy = (TP+TN)/(TP+TN+FP+FN)
- Precision = TP/(TP+FP)
- Recall = TP/(TP+FN)
- F1 = 2×P×R/(P+R)
- AUC-ROC = Area under TPR vs FPR curve

### Regression
- MSE = Σ(y_i - ŷ_i)² / n
- MAE = Σ|y_i - ŷ_i| / n
- R² = 1 - SS_res / SS_tot

### Serving
- Latency: P50, P95, P99 (ms)
- Throughput: requests/second
- Error rate: % of failed predictions
- Availability: uptime %

## MLOps Tools Reference

| Category | Tools |
|----------|-------|
| Orchestration | Airflow, Prefect, Dagster |
| Experiment Tracking | MLflow, Weights & Biases, Neptune |
| Model Registry | MLflow Model Registry, DVC |
| Feature Store | Feast, Tecton, SageMaker Feature Store |
| Containerization | Docker, Podman, Buildah |
| Orchestration (K8s) | Kubernetes, Helm, Kustomize |
| CI/CD | GitHub Actions, Jenkins, GitLab CI |
| Monitoring | Prometheus, Grafana, Evidently AI |
| Data Validation | Great Expectations, Deequ, Pandera |
| IaC | Terraform, Pulumi, CloudFormation |
| Distributed Training | Horovod, DeepSpeed, Ray |
| AutoML | Optuna, Ray Tune, H2O AutoML |

## Quick Formulas

```
PSI = Σ(Actual% - Expected%) × ln(Actual% / Expected%)
KL(P||Q) = Σ P(i) × log(P(i)/Q(i))
JS(P||Q) = 0.5×KL(P||M) + 0.5×KL(Q||M)  where M = 0.5×(P+Q)

Feature Store Latency:
  Online: P99 < 10ms
  Batch: Throughput > 100MB/s

A/B Test:
  n = (Z_α/2 + Z_β)² × 2σ² / δ²
  Minimum detectable effect = δ
```

## Behavioral STAR Template

```
S: [Brief context — project, team, timeline]
T: [Your responsibility — specific and measurable]
A: [Actions you took — technical details, decisions, collaboration]
R: [Quantified outcome — % improvement, time saved, revenue impact]
```

## Red Flags & Green Flags

| Red Flag | Green Flag |
|----------|------------|
| No model versioning | Immutable model registry with lineage |
| Manual deployment | Automated CI/CD with validation gates |
| No monitoring | Real-time drift detection + alerting |
| Data quality issues ignored | Automated data validation in pipeline |
| Single environment | Dev/Staging/Production with parity |
