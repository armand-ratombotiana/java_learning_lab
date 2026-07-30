# Lab 08: AI Observability

## Learning Objectives
- Implement token tracking for cost attribution
- Build latency monitoring and alerting
- Calculate per-model and per-user costs
- Detect data drift using statistical methods

## Concepts Covered
- **Token Tracking**: Counting input/output tokens per request
- **Latency Monitoring**: Measuring and aggregating response times
- **Cost Attribution**: Assigning costs to models, users, and features
- **Drift Detection**: KL divergence and PSI for distribution shifts
- **Metrics Collection**: Structured logging and time-series data

## Setup
```bash
cd lab08
javac src/com/aiengineering/lab08/AiObservabilityDemo.java
java com.aiengineering.lab08.AiObservabilityDemo
```

## Key Takeaways
- Observability is essential for production AI systems
- Token tracking directly ties usage to cost
- Drift detection catches model degradation early
