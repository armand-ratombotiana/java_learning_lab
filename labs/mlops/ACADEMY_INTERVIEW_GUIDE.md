# MLOps Interview Preparation Guide

## Core Topics

### 1. ML Pipeline Architecture
- **Orchestration**: Airflow, Dagster, Prefect — DAG design, task dependencies, retries, backfills
- **Feature Engineering**: Online vs offline stores, feature consistency, point-in-time joins
- **Training Pipelines**: Data ingestion, validation, transformation, training, evaluation
- **Inference Pipelines**: Batch scoring, real-time serving, model ensembles

### 2. Experiment Tracking & Model Registry
- **MLflow**: Tracking URI, experiments, runs, parameters, metrics, artifacts
- **Model Registry**: Versioning, aliases (staging/production/champion), lineage
- **Java Client**: MlflowClient, CreateExperiment, LogParam, LogMetric, LogModel

### 3. Containerization & Orchestration
- **Docker**: Multi-stage builds, slim images, layer caching, entrypoint patterns
- **Kubernetes**: Pods, deployments, services, HPA, rolling updates, resource quotas
- **Helm**: Charts, values, templates for ML workloads

### 4. CI/CD for ML
- **GitHub Actions**: Workflow triggers, matrix builds, model validation gates
- **Jenkins**: Pipeline as code, shared libraries, artifact promotion
- **ML Pipeline CI**: Data validation, model training, evaluation, registry push

### 5. Monitoring & Observability
- **Drift Detection**: Data drift (distribution), concept drift (prediction quality)
- **Metrics**: Accuracy, precision, recall, latency, throughput, error rates
- **Tools**: Prometheus, Grafana, Evidently AI, WhyLabs

### 6. Data Quality & Validation
- **Great Expectations**: Expectations, suites, data docs, validation results
- **Schema Validation**: Avro, Protobuf, JSON Schema for feature data
- **Java Validation**: JSR 380 Bean Validation, custom validators

### 7. A/B Testing & Experimentation
- **Statistical Significance**: p-values, confidence intervals, power analysis
- **Multi-Armed Bandit**: Epsilon-greedy, UCB, Thompson sampling
- **Metric Design**: Guardrail metrics, proxy metrics, delta analysis

### 8. Model Governance
- **Model Cards**: Model details, intended use, fairness, limitations
- **Bias Detection**: Demographic parity, equal opportunity, disparate impact
- **Audit Trails**: Immutable logs, version control for data and models

### 9. Infrastructure as Code
- **Terraform**: Providers, state, modules, workspaces for ML infra
- **Pulumi**: Infrastructure as real code (Java, Python, Go)

### 10. Distributed Training
- **Data Parallelism**: Sharded data, all-reduce, sync/async SGD
- **Model Parallelism**: Pipeline parallelism, tensor parallelism
- **Horovod / DeepSpeed**: Ring-allreduce, ZeRO optimization

## Behavioral Framework (STAR)
- **Situation**: Context of the challenge
- **Task**: Your responsibility
- **Action**: Steps you took
- **Result**: Measurable outcome

## Common System Design Questions
- Design an end-to-end ML platform
- Design a real-time feature store
- Design a model monitoring system
- Design a distributed training infrastructure
- Design a model serving platform at scale

## Key ML System Design Tradeoffs
- Batch vs real-time inference
- Online vs offline feature computation
- Synchronous vs asynchronous serving
- Pull vs push feature retrieval
- Monolithic vs microservice ML architecture
