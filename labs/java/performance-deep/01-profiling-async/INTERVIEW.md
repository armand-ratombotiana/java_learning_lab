# Interview Questions: async-profiler

## Company-Specific Focus

### Google
- async-profiler: low overhead CPU, allocation, and lock profiling
- Sampling: uses perf_events (Linux), ktrace (macOS) for stack sampling
- Flame graphs: CPU flame graphs, allocation flame graphs, lock flame graphs

### Microsoft
- async-profiler vs Visual Studio profiler
- CPU profiling: wall clock vs CPU time sampling

### Amazon
- Production profiling: async-profiler's low overhead allows use in production
- Continuous profiling: integrating async-profiler with observability platforms

### Meta
- Hot methods: identifying performance hotspots
- Allocation profiling: reducing GC pressure in critical paths

### Apple
- async-profiler on macOS: uses ktrace for stack collection
- JFR integration: async-profiler can start JFR recordings

### Oracle
- async-profiler: open source, supports JDK 8+
- safemode: profiling modes (safe for production)
- events: CPU, ALLOC, LOCK, WALL, CACHE-MISSES

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — async-profiler is a profiling tool) |

## Real Production Scenarios
- **Netflix**: async-profiler identified that String.format() in hot path consumed 15% CPU
- **LinkedIn**: Allocation profiling showed excessive object creation in the serialization layer

## Interview Patterns & Tips
- **Low overhead**: < 2% CPU overhead in sampling mode
- **Flame graphs**: x-axis is frequency, y-axis is stack depth
- **Allocation profiling**: identify objects causing GC pressure
- **Lock profiling**: identify contended locks

## Deep Dive Questions
- **perf_events**: How does async-profiler use perf_events on Linux?
- **Sampling**: How are stack traces collected asynchronously?
- **Frame pointer**: Why does async-profiler need frame pointers? (or use perf_events)
- **Firefox profiler**: How to analyze async-profiler output with Firefox Profiler?
- **CPU vs ALLOC**: How does CPU profiling differ from allocation profiling?