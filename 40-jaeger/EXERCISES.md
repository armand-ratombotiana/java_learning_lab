# Exercises - Jaeger

## Exercise 1: Automatic Instrumentation
Set up auto-tracing:

1. Configure OpenTelemetry Java agent
2. Enable instrumentation for HTTP, JDBC
3. Run application and generate traffic
4. View traces in Jaeger UI

## Exercise 2: Custom Spans
Add manual instrumentation:

1. Create custom spans with OpenTelemetry API
2. Add span attributes (business context)
3. Record exceptions and errors
4. Implement nested spans for complex operations

## Exercise 3: Context Propagation
Trace across services:

1. Propagate trace context via HTTP headers
2. Implement baggage for cross-service data
3. Trace async operations (CompletableFuture)
4. Correlate logs with trace IDs

## Exercise 4: Performance Analysis
Use traces for optimization:

1. Identify slow spans in trace viewer
2. Measure database query duration
3. Find network latency bottlenecks
4. Analyze service dependencies

## Exercise 5: Sampling Strategies
Configure intelligent sampling:

1. Implement head-based sampling (agent)
2. Configure tail-based sampling (collector)
3. Set up different sampling rates by environment
4. Analyze sampling impact on data volume

## Bonus Challenge
Build a circuit breaker that uses trace data to determine if a downstream service is healthy. If error rate > 10% in last 60 seconds, open circuit.