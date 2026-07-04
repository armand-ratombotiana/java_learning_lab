# Pipeline Architecture Interview Questions

## Junior Level

### Q: What is a pipeline architecture?
**A:** An architecture where data flows through a sequence of processing stages. Each stage transforms the data and passes it to the next stage.

### Q: How is it different from Chain of Responsibility?
**A:** In Chain of Responsibility, each handler decides whether to process or pass. In pipeline, all stages process the data in order.

## Mid Level

### Q: How do you handle errors in a pipeline?
**A:** Per-stage error handlers, retry with backoff for transient errors, dead letter queue for permanent failures. Consider circuit breaker for stages that consistently fail.

### Q: How do you test a pipeline?
**A:** Test each stage individually (unit tests), test stage composition (integration tests), test full pipeline with known inputs/outputs. Use in-memory test doubles for external dependencies.

## Senior Level

### Q: Design a document processing pipeline.
**A:**
Stages: Format detection -> Extraction -> Validation -> Enrichment -> Classification -> Storage
Parallel branches for different document types
Error handling per stage with retry and DLQ
Metrics: throughput, latency per stage, error rates
Configuration-driven stage ordering
Monitoring and alerting on pipeline health

### Q: How would you implement a pipeline that supports both serial and parallel execution?
**A:** Create a PipelineStage interface with both serial and parallel implementations. Use a CompositeStage that can contain ordered or unordered sub-stages. ExecutorService for parallel execution. ForkJoinPool for parallel split/aggregate patterns.
