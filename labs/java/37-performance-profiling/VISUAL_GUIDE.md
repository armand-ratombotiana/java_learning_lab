# Visual Guide — Performance Profiling (Lab 37)

## Profiling Workflow Diagram

```
   ┌──────────────┐
   │ Identify     │  Symptom: slow response, high CPU, OOM, thread stuck
   │ Problem      │
   └──────┬───────┘
          │
          ▼
   ┌──────────────┐
   │ Choose       │  CPU Profiler (async-profiler, JMC)
   │ Profiler     │  Memory Profiler (JProfiler, Eclipse MAT)
   │ Type         │  Thread Profiler (jstack, JMC)
   └──────┬───────┘
          │
          ▼
   ┌──────────────┐
   │ Attach to    │  - Agent mode: -agentpath:... (low overhead)
   │ JVM          │  - Sampling vs Instrumentation
   └──────┬───────┘  - Wall clock vs CPU time
          │
          ▼
   ┌──────────────┐
   │ Collect &    │  Hot methods (CPU), allocation sites (heap),
   │ Analyze      │  lock contention (threads), GC pauses
   └──────┬───────┘
          │
          ▼
   ┌──────────────┐
   │ Identify     │  "95% of time spent in String.split()"
   │ Bottleneck   │  "10k unnecessary StringBuilder allocations/sec"
   └──────┬───────┘
          │
          ▼
   ┌──────────────┐
   │ Fix &        │  Change algorithm, add caching, reduce allocations
   │ Re-profile   │  Verify improvement; repeat if needed
   └──────────────┘
```

## Flame Graph Description

A flame graph (Brendan Gregg) visualizes stack traces over time:

```
    ┌─────────────────────────────────────────────────┐
    │  java.lang.Thread.run()                         │
    │  ┌─────────────────────────────────────┐        │
    │  │ org.apache.tomcat.util.threads...  │        │
    │  │ ┌──────────────────────────┐        │        │
    │  │ │ com.myapp.Controller     │        │        │
    │  │ │ ┌─────────────────┐      │        │        │
    │  │ │ │ Service.doWork()│      │        │        │
    │  │ │ │ ┌─────────┐     │      │        │        │
    │  │ │ │ │DB.query │     │      │        │        │
    │ _│_│_│_│_│________│_____│______│________│________│_
```

- **X-axis**: alphabetical stack sort, not time series. Width = sample count.
- **Y-axis**: stack depth (root at bottom). Each rectangle is a method.
- **Color**: typically random or by package; some tools color by CPU vs I/O.
- **Reading**: wider rectangles mean more samples = hotter. Look for tall thin towers (deep call chains) and wide flat plateaus (hot methods).
- **Top-down**: shows caller → callee relationships.
