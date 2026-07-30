# Lab 01: LLM Serving Infrastructure

## Learning Objectives
- Understand model serving architectures for large language models
- Implement request batching to maximize GPU utilization
- Build response caching for repeated prompts
- Implement load balancing across model replicas

## Concepts Covered
- **Model Serving**: Deploying LLMs behind a REST/gRPC endpoint
- **Continuous Batching**: Dynamically batching requests as they arrive
- **Response Caching**: KV-cache and prompt-result caching
- **Load Balancing**: Round-robin, least-connections, and adaptive routing
- **Autoscaling**: Scaling replicas based on queue depth

## Setup
```bash
cd lab01
javac src/com/aiengineering/lab01/LlmServingInfrastructureDemo.java
java com.aiengineering.lab01.LlmServingInfrastructureDemo
```

## Key Takeaways
- Batching improves throughput at the cost of per-request latency
- Caching eliminates redundant computation for identical prompts
- Stateless replicas simplify horizontal scaling
