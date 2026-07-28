# Cracking the MLOps Interview

## 8-Week Study Plan

### Week 1-2: Foundations
- Complete Arrays & Hashing (NeetCode stage 1)
- Read labs 01-04
- Practice: 2 LeetCode problems/day + 1 system design whiteboard

### Week 3-4: Core MLOps
- Complete Sliding Window, Binary Search (NeetCode stages 3-4)
- Read labs 05-09
- Implement: Docker + MLflow + simple serving app
- Practice: ML system design sketches (feature store, serving infra)

### Week 5-6: Advanced Topics
- Complete Trees, Heaps, DP (NeetCode stages 5-7)
- Read labs 10-15
- Implement: K8s deployment + monitoring dashboard
- Practice: Distributed design patterns, capacity planning

### Week 7-8: Interview Simulation
- Mock coding interviews (45 min each)
- Mock ML design sessions (60 min each)
- Behavioral question practice (STAR format)
- Review company-specific guides

## Interview Day Tips

### Coding Round
1. **Clarify** — Restate problem, ask about constraints, edge cases
2. **Plan** — Discuss approach, time/space complexity before coding
3. **Implement** — Clean, readable code with meaningful variable names
4. **Test** — Walk through example, edge cases, suggest improvements
5. **MLOps Angle** — Relate solution to ML engineering (e.g., "This hashmap pattern is similar to how we store feature vectors")

### ML System Design Round
1. **Scope** — Clarify requirements, constraints, scale
2. **Data** — Data sources, volume, schema, quality
3. **Architecture** — High-level components, data flow
4. **ML Specifics** — Features, model selection, training, evaluation
5. **Infrastructure** — Serving, monitoring, CI/CD, scaling
6. **Tradeoffs** — Batch vs real-time, cost vs latency, accuracy vs speed

### Behavioral Round
- Prepare 5-7 STAR stories covering:
  - Pipeline improvement
  - Incident resolution
  - Cross-functional collaboration
  - Technical decision-making
  - Failure/learning experience
  - Mentoring/leadership
- Quantify results with metrics

## Must-Know Formulas

### Drift Detection
- PSI = Σ(P_i - Q_i) × ln(P_i / Q_i)
- KL Divergence = ΣP(x) × log(P(x) / Q(x))

### A/B Testing
- Sample size: n = (Z_α/2 + Z_β)² × 2σ² / δ²
- Confidence interval: p̂ ± Z × √(p̂(1-p̂)/n)

### Feature Store Latency Budget
- P99 < 10ms for online serving
- Throughput > 1000 QPS per node

### Kubernetes Resource Planning
- Request = baseline; Limit = burst
- HPA target = 60-70% CPU/memory
- Rolling update max surge = 25%, max unavailable = 25%

## Last-Minute Review Cards

```
┌─────────────────────────────────────────────┐
│ ML System Design Checklist                  │
├─────────────────────────────────────────────┤
│ ☐ Requirements & constraints                │
│ ☐ Data pipeline & quality                   │
│ ☐ Feature engineering & store               │
│ ☐ Model training & evaluation               │
│ ☐ Model serving & scaling                   │
│ ☐ Monitoring & observability                │
│ ☐ CI/CD & automation                        │
│ ☐ Governance & compliance                   │
│ ☐ Tradeoffs & alternatives                  │
└─────────────────────────────────────────────┘
```

## Resources
- **NeetCode.io** — 150 curated problems
- **MLflow Docs** — mlflow.org/docs/latest
- **Kubeflow** — kubeflow.org/docs
- **Great Expectations** — docs.greatexpectations.io
- **Terraform Learn** — learn.hashicorp.com/terraform
- **Cracking the Coding Interview** — Gayle Laakmann McDowell
- **Designing Data-Intensive Applications** — Martin Kleppmann
- **Machine Learning Design Interview** — Alex Xu
