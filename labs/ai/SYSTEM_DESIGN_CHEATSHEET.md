# ML System Design Cheatsheet

Comprehensive guide for ML system design interviews. Covers pipeline design, training infrastructure, serving, feature stores, MLOps, and common ML system designs.

---

## 1. ML Pipeline Design

### End-to-End ML Pipeline

```
Data Source → Ingestion → Validation → Feature Engineering → Training → Evaluation → Deployment → Monitoring
                                                                                          ↓
                                                                                    Feedback Loop
```

### Phase 1: Data Ingestion

**Batch Ingestion**
- Tools: Apache Spark, Apache Beam, Airflow, Dataflow
- Schedule: Daily, hourly, or on-demand
- Storage: Data Lake (S3, ADLS, GCS) or Data Warehouse (BigQuery, Redshift, Snowflake)
- Considerations:
  - Data freshness requirements (latency vs completeness)
  - Incremental vs full loads
  - Schema evolution handling

**Stream Ingestion**
- Tools: Kafka, Kinesis, Pub/Sub, Pulsar
- Processing: Spark Streaming, Flink, Beam
- Storage: Time-series DB (InfluxDB, TimescaleDB), key-value store (DynamoDB, Cassandra)
- Considerations:
  - Exactly-once vs at-least-once semantics
  - Data ordering and deduplication
  - Backpressure handling

### Phase 2: Data Validation

```python
# Schema validation pattern
{
    "feature_1": {"type": "float", "range": [0, 1], "nullable": False},
    "feature_2": {"type": "int", "range": [0, 100], "nullable": True},
    "feature_3": {"type": "categorical", "values": ["A", "B", "C"]}
}

# Statistics validation
- Distribution drift (KL divergence, JS divergence)
- Missing value rate threshold
- Value range compliance
- Data type verification
```

**Tools**: Great Expectations, Deequ, TensorFlow Data Validation (TFDV)

### Phase 3: Feature Engineering

**Feature Pipeline Architecture**

```
Raw Data → Transformation → Feature Store (Online + Offline)
                                         ↓
                               Training (Offline Features)
                               Serving (Online Features)
```

**Feature Types**:
| Type | Storage | Access Pattern | Freshness |
|------|---------|----------------|-----------|
| Static (user age) | Offline store | Low latency not critical | Days |
| Dynamic (user activity count) | Online store | Sub-millisecond | Seconds |
| Real-time (session features) | Online store | Sub-millisecond | Real-time |
| Computed (embedding similarity) | Hybrid | Depends on use case | Minutes |

### Phase 4: Training

**Training Pipeline Components**

```yaml
pipeline:
  data_split:
    train: 0.7
    val: 0.15
    test: 0.15
    method: temporal  # or random, stratified, group

  preprocessing:
    - categorical_encoding: target_encoding
    - numerical_scaling: standard
    - missing_value_imputation: median

  model:
    type: gradient_boosted_tree
    params:
      n_estimators: 1000
      learning_rate: 0.1
      max_depth: 6
    hyperparameter_tuning: bayesian_optimization

  evaluation:
    metrics: [auc, precision@k, recall@k, ndcg@10]
    validation: temporal_cross_validation
    baselines: [heuristic, previous_production_model]
```

### Phase 5: Model Evaluation

**Offline Evaluation**
- Hold-out test set (time-based split for temporal data)
- Cross-validation (k-fold, stratified, group, temporal)
- Metrics selection (business-aligned not just ML metrics)
- Statistical significance tests (paired t-test, McNemar's test)
- Calibration analysis (reliability diagrams)

**Online Evaluation**
- A/B testing setup
- Interleaving experiments (for ranking)
- Multi-armed bandit exploration
- Shadow deployment (log-and-compare)
- Canary deployment (gradual rollout)

### Phase 6: Deployment

**Deployment Strategies**

| Strategy | Description | Risk Level | Rollback Time |
|----------|-------------|------------|---------------|
| Shadow | Deploy alongside existing, log predictions | Low | Immediate |
| Canary | Route small % of traffic | Low-Medium | Minutes |
| Blue/Green | Switch between two complete environments | Medium | Seconds |
| Rolling | Gradual replacement of instances | Low | Minutes |
| A/B Test | Serve different models to different users | Medium | Immediate |

**Deployment Infrastructure**
- Model server: TensorFlow Serving, TorchServe, Triton, ONNX Runtime
- Containerization: Docker, Kubernetes
- API Gateway: Nginx, Kong, Envoy
- Load balancing: Round-robin, least connections, consistent hashing

### Phase 7: Monitoring

**What to Monitor**

```yaml
monitoring:
  data_quality:
    - feature_distribution_drift
    - missing_value_rate
    - schema_compliance
    - data_freshness

  model_performance:
    - prediction_distribution
    - accuracy_metrics (when labels available)
    - latency_p99
    - throughput
    - error_rate

  infrastructure:
    - CPU/GPU utilization
    - memory usage
    - disk I/O
    - network traffic
    - request queue depth

  business_metrics:
    - user_engagement
    - revenue_impact
    - conversion_rate
    - user_satisfaction (surveys)
```

**Drift Detection**:
```python
# Distribution drift detection
def detect_drift(reference_distribution, current_distribution, threshold=0.1):
    kl_div = kl_divergence(reference_distribution, current_distribution)
    js_div = js_divergence(reference_distribution, current_distribution)
    return {
        'kl_divergence': kl_div,
        'js_divergence': js_div,
        'drift_detected': kl_div > threshold
    }
```

---

## 2. Training Infrastructure

### Distributed Training Paradigms

#### Data Parallelism

```
                    Model Replica 1  Model Replica 2  ...  Model Replica N
                    /       \         /       \              /       \
               GPU 1     GPU 2     GPU 1     GPU 2     ... GPU 1     GPU 2
                 |          |         |          |            |          |
               Batch 1    Batch 1   Batch 2    Batch 2    Batch N    Batch N
                    \          |         |          |            |     /
                     \         |         |          |            |    /
                         All-Reduce Gradient Synchronization
                                      |
                                    Update weights
```

**All-Reduce Algorithms**:
- Ring All-Reduce (NCCL): O(n) communication, scales well
- Tree All-Reduce: O(log n) depth, good for small clusters
- Butterfly All-Reduce: O(log n), optimal but complex

**Synchronization Strategies**:
| Strategy | Description | Pros | Cons |
|----------|-------------|------|------|
| Sync SGD | All workers sync each step | Clean convergence | Straggler problem |
| Async SGD | Workers update independently | No stragglers | Stale gradients |
| Gradient Accumulation | Accumulate N batches before sync | Larger effective batch | More memory |
| Local SGD | Multiple local steps before sync | Less communication | Convergence analysis |

#### Model Parallelism

```
Layer 0 → Layer 1 → Layer 2 → ... → Layer N
    |          |         |              |
  GPU 0      GPU 1     GPU 2         GPU N
```

**Types**:
1. **Tensor Parallelism**: Split individual operations across GPUs
   - Column-wise split: `W = [W1, W2]`
   - Row-wise split: `W = [W1; W2]`
   - Used in: Megatron-LM, DeepSpeed

2. **Pipeline Parallelism**: Split layers across GPUs
   - GPipe: Micro-batches for better utilization
   - PipeDream: 1F1B scheduling reduces memory

3. **Sequence Parallelism**: Split sequence dimension
   - Ring attention for long contexts
   - Used in: LongNet, Ring Attention

#### ZeRO Optimization (DeepSpeed)

| Stage | Memory Reduction | What is Partitioned |
|-------|------------------|---------------------|
| ZeRO-1 | 4x | Optimizer states |
| ZeRO-2 | 8x | Optimizer + gradients |
| ZeRO-3 | 64x+ | Optimizer + gradients + parameters |

### GPU Cluster Architecture

```
GPU Cluster Design:
  Node 1: {GPU 0, GPU 1, GPU 2, GPU 3, GPU 4, GPU 5, GPU 6, GPU 7}
          |    |    |    |    |    |    |    |
          NVLink/NVSwitch (600 GB/s intra-node)
          |
          InfiniBand (400 Gb/s inter-node)
          |
  Node 2: {GPU 0, GPU 1, GPU 2, GPU 3, GPU 4, GPU 5, GPU 6, GPU 7}
```

**Key Components**:
- **NVLink**: High-bandwidth GPU-to-GPU interconnect (600 GB/s on H100)
- **NVSwitch**: Full-bandwidth switch connecting all GPUs in a node
- **InfiniBand**: Inter-node connection (400-800 Gb/s HDR/NDR)
- **PCIe Gen5**: 64 GB/s per lane, alternative to NVLink

### Training Optimization Techniques

**Mixed Precision Training**:
- FP32 master weights, FP16/BF16 forward/backward
- Loss scaling to prevent underflow
- 2x speedup, half memory usage

**Gradient Checkpointing**:
- Trade compute for memory
- Recompute activations during backward pass
- ~20% compute overhead for 50-80% memory savings

**Activation Offloading**:
- Move activations from GPU to CPU memory
- CPU-GPU transfer latency trade-off
- Useful for very large models

**Flash Attention**:
- Fused attention kernel avoiding materialization of NxN attention matrix
- 2-4x speedup for attention computation
- Linear memory complexity instead of quadratic

---

## 3. Serving Infrastructure

### Inference Serving

#### Batch Inference

```yaml
batch_inference:
  trigger:
    - scheduled (cron)
    - event (new data available)
    - manual

  pipeline:
    - read_batch_from_feature_store
    - load_model_from_registry
    - run_predictions
    - write_results_to_database

  scaling:
    - horizontal: multiple workers on Spark/MapReduce
    - parallelism: partition data by key

  optimization:
    - model_batching: combine multiple requests
    - dynamic_batching: adaptive batch sizes
    - model_compilation: XLA, TorchScript
```

**Use Cases**:
- Recommendation candidate generation (nightly)
- Content moderation pipeline
- Batch scoring for credit risk
- Offline evaluation and monitoring

#### Real-time Inference

```yaml
real_time_inference:
  architecture:
    - api_gateway → load_balancer → model_server → feature_store → prediction

  model_servers:
    - TensorFlow Serving: TF models, versioned
    - TorchServe: PyTorch, custom handlers
    - Triton Inference Server: Multi-framework, GPU optimized
    - ONNX Runtime: Cross-platform optimization

  optimizations:
    - model_quantization: INT8, FP16
    - model_pruning: remove redundant weights
    - kernel_fusion: combine operations
    - batching: dynamic batching on server side
    - caching: prediction cache for repeated queries

  slo_requirements:
    latency_p99: < 100ms
    throughput: 10000 QPS
    availability: 99.9%
```

### Model Caching Strategies

| Strategy | Cache Key | Cache Value | Hit Rate | Complexity |
|----------|-----------|-------------|----------|------------|
| Result caching | Input hash | Prediction | Medium | Low |
| Feature caching | Feature vector | Computed features | High | Low |
| Embedding cache | Entity ID | Embedding vector | Very High | Medium |
| Model caching | Model ID | Loaded model weights | N/A | Low |
| Attention KV cache | Sequence ID | K,V tensors | High (autoregressive) | High |

### A/B Testing Infrastructure

```python
class ABTestPlatform:
    def __init__(self, experiment_config):
        self.experiments = experiment_config
        self.allocator = ConsistentHashAllocator()

    def get_experiment(self, user_id, experiment_name):
        # Consistent hashing ensures user sees same model
        bucket = self.allocator.assign(user_id, nu_m_buckets=100)

        for variant in self.experiments[experiment_name]['variants']:
            if variant['start'] <= bucket < variant['end']:
                return variant['model_id']

        return self.experiments[experiment_name]['default']

    def log_result(self, user_id, experiment_name, model_id, outcome):
        # Log to experiment tracking system
        self.logger.log({
            'user_id': user_id,
            'experiment': experiment_name,
            'model': model_id,
            'timestamp': time.now(),
            'outcome': outcome,
            'features': self.get_user_features(user_id)
        })
```

---

## 4. Feature Stores

### Architecture

```
                         Feature Store
                    /                    \
            Offline Store              Online Store
          (Apache Parquet)          (Redis/DynamoDB)
                |                        |
        Training Pipeline          Serving Pipeline
        (batch features)          (real-time features)
                |                        |
         Model Training           Model Inference
```

### Key Components

| Component | Offline Store | Online Store |
|-----------|---------------|--------------|
| Storage | S3/ADLS/GCS (Parquet) | Redis, DynamoDB, Cassandra |
| Access Pattern | Batch reads (Spark) | Point lookups (KV) |
| Freshness | Daily/Hourly | Real-time |
| Data Volume | Petabytes | Gigabytes (hot data) |
| Consistency | Eventual | Strong |
| Historical | Yes (time travel) | No (current value) |

### Feature Store Tools

**Feast** (Open Source):
- Point-in-time correct joins
- Feature serving with Redis/dynamoDB
- Feast SDK for feature definitions
- Batch + streaming support

**Tecton** (Enterprise):
- Declarative feature engineering
- Auto-computed feature freshness
- Zero-ETL feature creation
- Built-in monitoring and drift detection

```python
# Feast feature definition example
from feast import Entity, FeatureView, Field, FileSource
from feast.types import Float32, Int32, String

user = Entity(name="user_id", description="User identifier")

user_stats = FeatureView(
    name="user_purchase_statistics",
    entities=["user_id"],
    ttl=timedelta(days=7),
    schema=[
        Field(name="avg_purchase_amount", dtype=Float32),
        Field(name="purchase_frequency", dtype=Float32),
        Field(name="total_purchases", dtype=Int32),
        Field(name="top_category", dtype=String),
    ],
    source=FileSource(path="s3://data/user_stats.parquet"),
)
```

---

## 5. Model Registries

### MLflow Architecture

```yaml
mlflow_architecture:
  tracking_server:
    - experiment_tracking: parameters, metrics, artifacts
    - runs: each training run logged
    - artifacts: models, plots, data samples

  model_registry:
    - model_versioning: semantic versioning
    - stage_transitions: staging -> production -> archived
    - model_lineage: source run, dataset, code version

  model_serving:
    - mlflow_models: generic model format (flavor-based)
    - deployment: Docker container, SageMaker, Azure ML
    - serving: REST API, gRPC
```

### Weights & Biases (W&B)

**Capabilities**:
- Experiment tracking and visualization
- Hyperparameter sweeps (Bayesian, grid, random)
- Dataset versioning and artifact management
- Model registry with lineage tracking
- Collaborative dashboards and reports
- Integrations: PyTorch, TensorFlow, JAX, Hugging Face

---

## 6. MLOps

### CI/CD for ML

```yaml
ci_cd_pipeline:
  ci_stage:
    - code_linting (flake8, mypy, black)
    - unit_tests (pytest)
    - data_validation (Great Expectations)
    - model_training_smoke_test (small dataset)
    - notebook_validation (Papermill)

  cd_stage:
    - model_training (full dataset)
    - model_evaluation (against production metrics)
    - model_validation (bias, fairness, explainability)
    - model_promotion (manual or automatic gating)
    - canary_deployment (5% traffic -> 25% -> 100%)
    - monitoring_dashboards (Grafana, Datadog)

  rollback_triggers:
    - accuracy_drop > 5%
    - latency_p99 > 200ms
    - error_rate > 1%
    - data_drift_detected
    - concept_drift_detected
```

### Model Monitoring

**Drift Types**:
| Drift Type | Description | Detection Method | Response |
|------------|-------------|------------------|----------|
| Data Drift | Feature distribution changes | KL divergence, JS divergence | Retrain on new data |
| Concept Drift | Relationship P(Y|X) changes | Performance monitoring | Retrain with recent data |
| Label Drift | Label distribution changes | Confusion matrix diff | Rebalance training data |
| Upstream Drift | Source data changes | Schema validation | Fix data pipeline |

**Alerting Rules**:
```python
# Data drift alert
if feature_drift['kl_divergence']['feature_importance_weighted'] > 0.2:
    alert.send(
        severity='HIGH',
        message=f'Data drift detected: {feature_drift}',
        channel='#ml-monitoring',
        pagerduty=True
    )

# Performance degradation alert
if online_metrics['accuracy_7d_sma'] < historical_baseline['accuracy'] - 0.03:
    alert.send(
        severity='CRITICAL',
        message=f'Accuracy degraded by {degradation:.2%}',
        channel='#ml-oncall',
        pagerduty=True
    )
```

**Monitoring Infrastructure**:
- **Metrics**: Prometheus, Grafana, Datadog, New Relic
- **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **Tracing**: Jaeger, Zipkin for request-level latency breakdown
- **Alerts**: PagerDuty, OpsGenie, Slack

---

## 7. LLM Serving

### vLLM Architecture

```
Request → Tokenizer → Scheduler → Block Manager → GPU Workers → Output
                             ↓
                        KV Cache (PagedAttention)
```

**Key Features**:
- **PagedAttention**: Manage KV cache in pages, reducing fragmentation
- **Continuous Batching**: Add/remove sequences from batch dynamically
- **Speculative Decoding**: Use draft model for faster generation
- **Prefix Caching**: Cache shared prefixes across requests
- **Tensor Parallelism**: Split model across GPUs

### TensorRT-LLM

**Optimization Flow**:
```
PyTorch Model → ONNX → TensorRT Engine (FP16/INT4/INT8)
                          ↓
                 In-flight Batching
                          ↓
                 KV Cache Optimization
                          ↓
                 Plugin Kernels (FlashAttention, etc.)
```

**Key Features**:
- In-flight batching with KV cache reuse
- Weight quantization (FP16, INT8, INT4, AWQ, GPTQ)
- Multi-GPU inference (TP+PP)
- Streaming and chunked prefill

### Quantization Techniques

| Method | Precision | Compression | Quality Loss | Inference Speedup |
|--------|-----------|-------------|--------------|-------------------|
| FP32 baseline | 32-bit | 1x | 0% | 1x |
| FP16 / BF16 | 16-bit | 2x | ~0% | 1.5-2x |
| INT8 (W8A8) | 8-bit | 4x | <1% | 2-3x |
| INT4 (GPTQ/AWQ) | 4-bit | 8x | 1-3% | 3-4x |
| NF4 (QLoRA) | 4-bit | 8x | 2-5% | N/A (training) |
| INT4 + Sparsity | 4-bit + 50% | 16x | 3-8% | 4-5x |

### KV Cache Optimization

| Technique | Memory Reduction | Complexity | Use Case |
|-----------|-----------------|------------|----------|
| KV Cache | 1x | None | Baseline |
| Multi-Query Attention (MQA) | 4-8x | Low | Fast inference |
| Grouped Query Attention (GQA) | 2-4x | Low | Good quality/speed |
| Sliding Window Attention | Variable | Medium | Long contexts |
| KV Cache Quantization (INT8) | 2x | Medium | Memory bound |
| PagedAttention | Variable | High | Dynamic batching |
| Prefix Sharing | Variable | Medium | Shared prefixes |

---

## 8. RAG Architecture

### RAG Pipeline Components

```
User Query → Query Processing → Retrieval → Augmentation → Generation → Response
               ↓                  ↓             ↓              ↓
          Query Rewriting   Vector Search   Prompt Builder   LLM Inference
          Query Expansion   BM25 Search    Context Truncation  Safety Filter
          Query Translation Hybrid Search  Instruction Merge  Output Guard
```

### Embedding Models

| Model | Dimensions | Context Length | Quality | Use Case |
|-------|-----------|----------------|---------|----------|
| OpenAI Ada-002 | 1536 | 8191 | High | General purpose |
| Cohere Embed v3 | 1024 | 512 | High | RAG, classification |
| BAAI BGE-M3 | 1024 | 8192 | High | Multi-lingual |
| Snowflake Arctic | 768 | 512 | High | Enterprise |
| E5-mistral-7b | 4096 | 8192 | Very High | High accuracy |
| Instructor-XL | 768 | 512 | High | Task-specific |

### Vector Databases

| Database | Index Type | Performance (10M vectors) | Features |
|----------|------------|--------------------------|----------|
| Pinecone | SPTAG, HNSW | <10ms p99 | Managed, serverless |
| Weaviate | HNSW, SQ | <20ms p99 | Hybrid search, GraphQL |
| Qdrant | HNSW | <15ms p99 | Rust-based, filtered search |
| Milvus | IVF, HNSW, DiskANN | <20ms p99 | GPU acceleration, distributed |
| Chroma | HNSW | <30ms p99 | Lightweight, embedded |
| FAISS | IVF, HNSW, LSH | <10ms p99 | Library (not DB) |
| pgvector | IVFFlat, HNSW | <50ms p99 | PostgreSQL extension |

### Retrieval Strategies

| Strategy | Description | Precision | Recall | Latency |
|----------|-------------|-----------|--------|---------|
| Dense Retrieval | Embedding + ANN search | High | Medium | Fast |
| Sparse Retrieval | BM25, TF-IDF | Medium | Medium | Very Fast |
| Hybrid Retrieval | Dense + Sparse ensemble | High | High | Fast |
| Multi-vector Retrieval | ColBERT, late interaction | Very High | High | Medium |
| Hierarchical Retrieval | Coarse + fine search | High | High | Medium |
| Cross-encoder Reranking | Re-rank top-k with cross-encoder | Very High | High | Slow |
| Iterative Retrieval | Multi-turn retrieval | High | Very High | Slow |

### RAG Evaluation

| Metric | Description | Target |
|--------|-------------|--------|
| Context Precision | % of retrieved docs relevant | >0.8 |
| Context Recall | % of relevant docs retrieved | >0.7 |
| Answer Relevancy | Answer addresses query | >0.9 |
| Faithfulness | Answer supported by context | >0.95 |
| MRR | Mean reciprocal rank | >0.7 |
| NDCG@10 | Ranking quality | >0.6 |
| Latency | End-to-end (p99) | <2s |

---

## 9. Design ML Systems

### Design a Recommendation System

**Problem**: Design a recommendation system for a video sharing platform

**Requirements**:
- Scale: 1B users, 100M videos
- Latency: <200ms p99
- Freshness: New content within 5 minutes
- Metrics: Watch time, CTR, user satisfaction

**Architecture**:

```
User ID + Context → Candidate Generation → Ranking → Re-ranking → Recommendations
                         ↓                    ↓           ↓
                    Two-Tower Model    Deep Neural Net    Business Rules
                     (Retrieval)        (Scoring)         (Diversity)
```

**Candidate Generation Strategies**:
1. **Collaborative Filtering**: Matrix factorization, item-to-item CF
2. **Content-based**: Embedding similarity via two-tower model
3. **Popularity**: Trending, viral potential model
4. **Social**: Friends liked, group activity
5. **Contextual**: Time of day, device, location

**Ranking Model**:
- Features: User embeddings, item embeddings, cross features, context
- Model: Deep neural network with cross layers (DCN-V2)
- Optimization: Weighted pairwise loss (watch time)
- Architecture: Multi-task learning (engagement, satisfaction, diversity)

**Re-ranking**:
- Diversity constraints (MMR algorithm)
- Business rules (promote new content)
- Fatigue management (over-exposure penalty)
- Position bias correction (IPW, PAL)

### Design a Fraud Detection System

**Problem**: Design a real-time fraud detection system for a payment platform

**Requirements**:
- Scale: 10M transactions/day, 100K QPS peak
- Latency: <50ms per transaction decision
- Accuracy: >99% detection rate, <0.1% false positive
- Real-time: Block transaction before completion

**Architecture**:

```
Transaction Event → Feature Engine → Rule Engine → ML Model → Decision Engine
                       ↓               ↓             ↓            ↓
                Real-time Features   Rule Match    GBDT Model     Score + Action
                       ↓                             ↓
                Feature Store                   Model Server
```

**Feature Engineering**:
- User features: Transaction history, velocity, device fingerprint
- Merchant features: Historical fraud rate, location
- Transaction features: Amount, currency, time, IP geolocation
- Network features: Connection graph, device sharing

**Model Strategy**:
- Real-time: LightGBM or XGBoost (fast inference, high AUC)
- Batch: Graph neural network for fraud rings
- Ensemble: Stacking of multiple models
- Online learning: FTRL-Proximal for concept drift

### Design a Search Ranking System

**Problem**: Design a search ranking system for an e-commerce platform

**Requirements**:
- Scale: 100M products, 1M queries/second
- Latency: <100ms p99
- Relevance: NDCG@10 > 0.7
- Business: Maximize GMV (Gross Merchandise Value)

**Architecture**:

```
Query → Query Understanding → Retrieval → Ranking → Personalized Ranking → Results
           ↓                      ↓           ↓               ↓
     Spelling Correction    BM25 + Dense   LightGBM          DNN
     Query Classification   Semantic Search (Cross Features) (User Embedding)
     Query Embedding        Hybrid Search
```

**Ranking Features**:
- Query-document: TF-IDF, BM25, semantic similarity
- Document: Price, rating, reviews, freshness, availability
- User: History, preferences, location, device
- Context: Time, season, trending signals
- Business: Margin, sponsorship, inventory

**Model**:
- Two-stage ranking (light model on many docs, heavy on top K)
- LambdaMART or ListNet for listwise ranking
- Position bias correction (inverse propensity weighting)
- Calibration for probability estimates

### Design a Content Moderation System

**Problem**: Design an ML system to detect and moderate harmful content

**Requirements**:
- Scale: 1B+ items/day across text, image, video
- Latency: <500ms for real-time, batch for backlog
- Accuracy: High precision (avoid false positives), high recall (catch violations)
- Coverage: Toxicity, hate speech, violence, adult content, spam

**Multi-modal Architecture**:

```
Input → Text Model → Image Model → Video Model → Audio Model → Fusion → Decision
         (BERT)       (CNN/ViT)     (3D ConvNet)   (Wav2Vec)   (Transformer)
            ↓            ↓              ↓              ↓            ↓
        Text Embed    Visual Emb     Temporal       Audio        Multi-modal
                     + Object Det    Features       Features     Classifier
```

**Tiered Moderation Pipeline**:
1. **Fast Path**: Hash-based matching with known-violation database
2. **ML Classifier**: Multi-label classification model
3. **Action**: Auto-remove, flag for review, allow
4. **Human Review**: Edge cases, appeals, new patterns
5. **Feedback Loop**: Human decisions → retrain model

### Design a Personalization Engine

**Problem**: Design a real-time personalization engine for a news website

**Requirements**:
- Scale: 10M daily active users
- Latency: <50ms for personalization
- Freshness: New articles within 1 minute
- Privacy: No raw user data stored

**Architecture**:

```
User Event → Event Processor → Session Builder → Interest Model → Personalization
    ↓              ↓                ↓               ↓                ↓
Click/View     Kafka Topic     User Session    Real-time        Article Ranking
Read/Share                    Feature Vector   Interest         User-Specific
                                                                   ↓
                                                            Personalized Feed
```

**Personalization Techniques**:
1. **Collaborative**: User-based CF, item-based CF, matrix factorization
2. **Content-based**: TF-IDF user profile, category affinity
3. **Hybrid**: Combine CF + content scores
4. **Deep Learning**: Two-tower model, DIN (Deep Interest Network)
5. **Contextual Bandit**: Explore/exploit for new users/items
6. **Session-based**: GRU4Rec, SR-GNN for short-term interest

**Privacy-preserving Techniques**:
- Differential privacy for aggregate statistics
- On-device personalization (Apple federated learning)
- Federated learning across devices
- Ephemeral sessions (no persistent profiles)
