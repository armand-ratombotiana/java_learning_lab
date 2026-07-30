# Lab 06: AI Pipeline Orchestration — Interview Q&A

## FAANG-Level Questions

### Q1: Design a real-time AI pipeline that processes streaming data.

**A:** Use a message broker (Kafka/PubSub) for ingestion. Micro-batch the stream every 100ms or 1000 events. Each micro-batch goes through: validation → preprocessing → feature extraction → model inference → post-processing. Results are written back to a sink topic. Use Flink or Kafka Streams for stateful processing. Monitor lag, throughput, and per-stage latency.

### Q2: How would you handle a slow stage that blocks the entire pipeline?

**A:** Three strategies: (1) add a bounded queue between stages with backpressure to let fast stages throttle gracefully; (2) move the slow stage to an async worker pool with configurable parallelism; (3) introduce circuit breakers — if latency exceeds a threshold for N consecutive invocations, fail fast instead of accumulating backlog.

### Q3: How do you test an AI pipeline with multiple interdependent stages?

**A:** Unit test each stage in isolation with mock inputs/outputs. Integration test the full pipeline with golden datasets. Property-based tests to verify invariants (e.g., output score always in [0,1]). Chaos testing by injecting latency spikes or failures in individual stages to verify system resilience.

### Q4: Compare batch vs. streaming pipelines for AI workloads.

**A:** Batch: higher throughput, simpler to implement, good for offline/periodic tasks (training data processing, nightly reporting). Streaming: lower latency, handles real-time inference, requires state management and exactly-once semantics. Many systems use a Lambda architecture with both paths.

### Q5: How do you version and deploy pipeline configurations?

**A:** Store pipeline configs (stage order, parameters, model versions) in a registry with versioning. Each config maps to a pipeline hash. Deploy new configs via the same CI/CD system as code changes. Canary test new pipeline configs by routing a percentage of traffic through the new pipeline while monitoring quality metrics.