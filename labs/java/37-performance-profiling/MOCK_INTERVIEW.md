# Mock Interview Transcript: Performance Profiling

## Interviewer: Staff Engineer, Google
## Candidate: Senior Java developer
## Time: 40 minutes
## Focus: JFR, async-profiler, JMH, GC logs, analysis

---

**Q1: Your service has intermittent latency spikes. Walk through your diagnostic process.**

**Candidate**: Step 1: Check GC logs — are pauses correlated with latency spikes? Use `-Xlog:gc*:file=gc.log:time,level,tags`. Step 2: Use JFR with `jcmd <pid> JFR.start name=profile duration=60s filename=profile.jfr` to capture thread states, lock contention, allocation rates. Step 3: Analyze JFR in JDK Mission Control — check safepoint pauses, lock contention, compilation events. Step 4: Use async-profiler for wall-clock profiling: capture what the threads are actually doing during the spike. Step 5: Check external dependencies (database, network).

**Interviewer**: How would you benchmark two approaches (stream vs loop) to confirm which is faster?

**Candidate**: Use JMH:
```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(value = 2, warmups = 1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class SumBenchmark {
    
    @Param({"10", "100", "1000"})
    int size;
    List<Integer> list;
    
    @Setup
    public void init() {
        list = IntStream.range(0, size).boxed().collect(Collectors.toList());
    }
    
    @Benchmark
    public int stream() {
        return list.stream().mapToInt(Integer::intValue).sum();
    }
    
    @Benchmark
    public int loop() {
        int sum = 0;
        for (int v : list) sum += v;
        return sum;
    }
}
```
Run with: `java -jar benchmarks.jar -prof gc` to also measure allocation rates.

**Interviewer**: What does JFR's low overhead mean? How does it collect data?

**Candidate**: JFR overhead is <1% in production. It achieves this through: (1) Ring buffers — events are written to pre-allocated, lock-free ring buffers with no allocation. (2) Sampling — stack traces are sampled, not captured on every event. (3) Thread-local storage — events are written to thread-local buffers, merged globally. (4) No safepoint bias — JFR uses its own profiling mechanism, not safepoint-based sampling.

**Interviewer**: How do you analyze a GC log to identify a problem?

**Candidate**: 
```bash
[2024-03-15T10:30:00.123+0000][info][gc] GC(1) Pause Young (G1 Evacuation Pause) 4096M->1024M(8192M) 150.2ms
```
Key metrics: (1) Pause duration (150ms) — is it within target? (2) Reclaimed (4096→1024 = 75% garbage). (3) Heap usage after GC (1024M out of 8192M = 12.5%). (4) Promotion rate and allocation rate. Tools like `gcviewer`, `gceasy.io`, or custom grep/awk for averages. Red flags: increasing pause times, growing heap after GC (memory leak), Full GC events.

**Interviewer**: What is async-profiler and how does it work?

**Candidate**: async-profiler is a low-overhead sampling profiler. On Linux, it uses `perf_events` for CPU profiling (sampling program counter via hardware CPU counters). For wall-clock profiling, it uses `AsyncGetCallTrace` — a JVM API that samples stack traces without safepoint bias. It can profile: CPU, allocations, locks, wall clock. Output is a flame graph HTML.

**Interviewer**: How do you detect a memory leak from a heap dump?

**Candidate**: (1) Capture heap dump with `jcmd <pid> GC.heap_dump /path/dump.hprof` or `jmap -dump:live,file=path.hprof <pid>`. (2) Open in Eclipse MAT. (3) Run "Leak Suspects Report" — MAT identifies suspiciously large retained sets. (4) Check the "Dominator Tree" for largest objects. (5) Look for: ThreadLocal accumulations, HashMap that never shrinks, cached session objects, ClassLoader leaks (classes from redeploy not unloaded). (6) For suspected byte[] issues: check what holds char[] (implies String data).

**Interviewer**: What are the first JVM flags you'd check for a performance issue?

**Candidate**: `-Xms` and `-Xmx` (heap sizing), `-XX:+UseG1GC` (are we using the right GC?), GC log flags, `-XX:MaxMetaspaceSize`, `-XX:+HeapDumpOnOutOfMemoryError`, `-XX:ReservedCodeCacheSize`. Increase `-Xmx` if GC is too frequent. Cap metaspace to prevent unbounded growth. Set `-Xms = -Xmx` to avoid resizing pauses.

**Interviewer**: Final: How would you tune G1 GC for a 64GB heap with 100ms pause target?

**Candidate**: 
```bash
-Xms64g -Xmx64g 
-XX:+UseG1GC 
-XX:MaxGCPauseMillis=100 
-XX:G1HeapRegionSize=4m       # 4MB regions for 64GB heap
-XX:G1NewSizePercent=5        # 5% young gen (3.2GB)
-XX:MaxGCPauseMillis=100      # Soft target
-XX:+UnlockExperimentalVMOptions -XX:G1MixedGCLiveThresholdPercent=85 
-XX:G1MixedGCCountTarget=8    # Spread mixed GC over 8 pauses
-XX:ConcGCThreads=4           # Concurrent threads
-XX:ParallelGCThreads=8       # Parallel threads
-Xlog:gc*:file=gc.log:time,uptime,level,tags
```
After applying, monitor GC logs and adjust based on actual pause times.

---

## Feedback

**Strengths**:
- Systematic diagnostic process
- JMH benchmark with correct setup
- JFR internals and low-overhead explanation
- G1 tuning with specific flags

**Areas for Improvement**:
- Could discuss `PerfData` counters
- Mention JFR event streaming for real-time monitoring

**Score**: 4.5/5 — Strong performance analysis skills
