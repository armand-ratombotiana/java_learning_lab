# Interview Questions: Profiling & Observability

## Company-Specific Focus

### Google
- Continuous profiling: async-profiler, JFR streaming
- Observability: distributed tracing, metrics, logs
- Flame graphs and icicle graphs for CPU and allocation analysis

### Microsoft
- Application Insights for Java: distributed tracing, metric collection
- Thread dump analysis: jstack, thread state analysis

### Amazon
- Profiling in production: JFR, async-profiler, JMC
- AWS X-Ray: distributed tracing integration
- CloudWatch metrics: JVM metrics monitoring

### Meta
- Memory profiling: heap dump analysis, MAT, JProfiler
- Latency analysis: P99, P999 latency and percentiles
- GC log analysis: gceasy.io, GCViewer

### Apple
- Low-overhead profiling: JFR with minimal performance impact
- Metrics collection for macOS deployments

### Oracle
- JDK Mission Control (JMC): UI for JFR analysis
- JDK Flight Recorder (JFR): event based, low overhead recording
- jcmd: diagnostic command tool
- jhsdb: postmortem analysis tool

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — profiling/observability is operational) |

## Real Production Scenarios
- **Netflix**: JFR streaming to detect memory leaks in production without restart
- **Uber**: async-profiler used to identify CPU hot spot in the trip matching algorithm
- **LinkedIn**: Thread dump analysis identified 200 threads in BLOCKED state on a single lock

## Interview Patterns & Tips
- **async-profiler**: Open source, low overhead, CPU/ALLOC/LOCK profiling
- **JFR**: Built into the JDK, near zero overhead
- **Flame graphs**: X-axis is sampling count, Y-axis is stack depth
- **Three pillars of observability**: Metrics, logs, traces

## Deep Dive Questions
- **JFR**: How does JFR achieve low overhead? Uses ring buffers, thread-local events
- **async-profiler**: How does it collect stack traces? Uses perf_events (Linux), ktrace (macOS)
- **Sampling**: How does async-profiler handle sampling frequency vs accuracy?
- **Heap dump**: What is in a heap dump? How to analyze with MAT?
- **Distributed tracing**: How does context propagation work across services?