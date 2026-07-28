# System Design Cheatsheet for MLOps

## ML Platform Architecture

```
                         ┌─────────────────┐
                         │ Feature Store    │
                         │ (Online/Offline) │
                         └────────┬────────┘
                                  │
┌──────────┐   ┌──────────┐   ┌──▼───────┐   ┌──────────┐   ┌──────────┐
│ Data     │──▶│ Feature  │──▶│ Training │──▶│ Model    │──▶│ Serving  │
│ Pipeline │   │ Pipeline │   │ Pipeline │   │ Registry │   │ Infra    │
└──────────┘   └──────────┘   └──────────┘   └──────────┘   └──────────┘
                                                              │
                                                              ▼
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│ Monitoring│   │ A/B      │   │ CI/CD    │   │ Governance│   │Feedback  │
│ System   │   │ Testing  │   │ Pipeline │   │ & Audit  │   │ Loop     │
└──────────┘   └──────────┘   └──────────┘   └──────────┘   └──────────┘
```

## Key Design Considerations

### 1. Feature Store
- **Offline**: Parquet, Hive, Iceberg — batch computation, large-scale
- **Online**: Redis, DynamoDB, Cassandra — low-latency (<10ms) serving
- **Consistency**: Point-in-time correctness, timestamp alignment

### 2. Model Serving
- **Batch**: Spark, scheduled jobs — high throughput, periodic
- **Online REST/gRPC**: Flask/FastAPI/Spring Boot, low latency (<100ms)
- **Streaming**: Kafka + Flink — real-time, event-driven

### 3. Training Infrastructure
- **Single Node**: Prototyping, small datasets
- **Distributed**: Horovod, PyTorch DDP, TF Distribution Strategy
- **AutoML**: Hyperparameter tuning (Optuna, Ray Tune), NAS

### 4. Monitoring Stack
- **Metrics**: Prometheus + Grafana
- **Drift**: Evidently AI, WhyLabs, custom statistical tests
- **Logging**: ELK stack, Loki
- **Alerting**: PagerDuty, OpsGenie, Slack webhooks

### 5. CI/CD Pipeline
- **Source**: Git (DVC for data/models)
- **Build**: Docker, GitHub Actions, Jenkins
- **Test**: Data validation (GE), model evaluation, integration tests
- **Deploy**: Blue-green, canary, rolling updates
- **Registry**: Docker Hub, ECR, GCR + MLflow Model Registry

## Scaling Patterns

| Pattern | Use Case | Tech |
|---------|----------|------|
| Queue-based | Async feature computation | Kafka, RabbitMQ |
| Sharding | Horizontal scaling | Consistent hashing |
| Replication | High availability | Leader-follower |
| Caching | Low-latency serving | Redis, Memcached |
| Batching | Throughput optimization | Micro-batching |

## Capacity Planning

- **Daily inference volume**: Predictions/sec × 86400
- **Feature storage**: Features × entities × bytes × retention
- **Model storage**: Model size × versions × replicas
