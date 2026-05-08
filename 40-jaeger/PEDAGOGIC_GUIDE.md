# Pedagogic Guide - Jaeger

## Learning Path

### Phase 1: Tracing Fundamentals
1. What is distributed tracing?
2. Trace, span, context concepts
3. OpenTelemetry standard
4. Jaeger architecture

### Phase 2: Instrumentation
1. Auto-instrumentation with agent
2. Manual span creation
3. Annotation and attributes
4. Exception recording

### Phase 3: Context Propagation
1. HTTP header propagation
2. W3C Trace Context standard
3. Baggage for cross-cutting data
4. Multi-service tracing

### Phase 4: Analysis
1. Trace visualization
2. Service dependency graphs
3. Performance bottleneck identification
4. Error tracking and analysis

### Phase 5: Advanced
1. Sampling strategies
2. Tail-based sampling
3. Custom collectors
4. Integration with logs and metrics

## Terminology

| Term | Description |
|------|-------------|
| Trace | End-to-end request flow |
| Span | Single operation unit |
| Parent | Previous span in hierarchy |
| Baggage | Cross-service key-value data |
| Sampling |选择性记录 traces |

## Interview Topics

| Topic | Question |
|-------|----------|
| Why Tracing? | When to use tracing vs. logging? |
| Overhead | How to minimize tracing impact? |
| Sampling | How to sample without losing data? |
| Context | How to handle context in async code? |
| Scale | How does Jaeger scale? |

## Best Practices
- Add meaningful span names
- Include relevant attributes
- Don't trace everything (use sampling)
- Use baggage sparingly (propagated to all services)
- Correlate with logs

## Next Steps
- Explore OpenTelemetry Collector
- Learn about metric-correlated traces
- Study continuous profiling integration