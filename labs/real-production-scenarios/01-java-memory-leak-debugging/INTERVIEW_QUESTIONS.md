# Lab 01 — Java Memory Leak Debugging: Interview Questions

## Technical Questions

### Junior Level

**Q1: What is the difference between a heap memory leak and a Metaspace memory leak?**

**Answer:** A heap memory leak occurs when objects are created but never garbage collected because they remain reachable from GC roots. This fills the Java heap (-Xmx). A Metaspace memory leak occurs when class metadata accumulates because ClassLoaders cannot be garbage collected. Metaspace is native memory outside the heap. Heap leaks are diagnosed with heap dumps and MAT; Metaspace leaks require ClassLoader analysis and GC log examination.

**Q2: What is a ThreadLocal and how can it cause a memory leak?**

**Answer:** ThreadLocal provides per-thread storage. Each Thread has a ThreadLocalMap containing Entry objects. The Entry key (the ThreadLocal) is a WeakReference, but the value is a strong reference. If the ThreadLocal is set but never removed, and the thread lives a long time (e.g., pool thread), the value object is strongly reachable even after the request completes, preventing GC. In web applications, if the value holds a reference to a ClassLoader, it prevents the entire ClassLoader and all its loaded classes from being GC'd, causing a Metaspace leak.

**Q3: What JVM flags would you use to investigate an OutOfMemoryError: Metaspace?**

**Answer:** Key flags:
- `-XX:+HeapDumpOnOutOfMemoryError` — capture heap dump on OOM
- `-XX:HeapDumpPath=/path/` — where to save the dump
- `-XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.log` — GC logging
- `-XX:StartFlightRecording=settings=profile,dumponexit=true` — JFR for diagnostic events
- `-XX:MaxMetaspaceSize=256m` — set limit to fail fast
- `-XX:+PrintClassHistogram` — class statistics

**Q4: What does jcmd <pid> VM.classloader_stats show and how do you interpret it?**

**Answer:** It shows the number of ClassLoader instances, the number of classes loaded per ClassLoader, and the bytes consumed. Key indicators of a leak: 1) ClassLoader count continuously increasing, 2) Many ClassLoaders with a high number of loaded classes, 3) The same classes appearing under multiple ClassLoaders (repeated loading), 4) No class unloading events in GC logs.

### Mid-Level

**Q5: You see Metaspace growing at 100MB/hour in production. No OOM has occurred yet. Walk through your investigation.**

**Answer:** 1) Check ClassLoader count via jcmd VM.classloader_stats — if increasing, we have a ClassLoader leak. 2) Examine GC logs for class unloading events — if unloading is 0, confirmed leak. 3) Capture thread dump — look for ThreadLocal references to ClassLoaders. 4) Capture heap dump and analyze in MAT: look for URLClassLoader instances with large retained heap, trace "Path to GC Roots" excluding weak references. 5) Identify the retaining reference chain (typically ThreadLocal → SecurityContext → ClassLoader). 6) Correlate with code: find the ThreadLocal that stores request-scoped data referencing the ClassLoader.

**Q6: How would you fix the ThreadLocal leak described in the Netflix Zuul incident?**

**Answer:** 1) Add try-finally blocks around ThreadLocal usage so that remove() is called on every code path including exceptions. 2) Replace static ThreadLocal fields with WeakThreadLocal wrappers where possible. 3) If using InheritableThreadLocal, override childValue() to return a clean copy. 4) In a framework context, add a request-lifecycle listener or filter that clears all ThreadLocal values after request completion. 5) Consider migrating to Java 20+ ScopedValue (JEP 429) for request-scoped data.

**Q7: Explain the difference between a heap dump analysis and a GC log analysis for diagnosing memory leaks.**

**Answer:** A heap dump shows the exact object graph at a point in time — which objects are alive, how many, and their reference chains. This is best for identifying the specific objects causing the leak. GC logs show the trend over time — how heap usage changes between collections, whether objects are being promoted to old gen, and whether class unloading is happening. For heap leaks: start with heap dump to find the leaking type. For Metaspace leaks: start with GC logs to confirm no class unloading, then heap dump to find the retaining reference. Both are complementary.

**Q8: What are the Coffman conditions for a deadlock and how do they apply to memory leaks?**

**Answer:** The four Coffman conditions (Mutual Exclusion, Hold and Wait, No Preemption, Circular Wait) describe deadlocks. For memory leaks, an analogous set: 1) Strong Reference (an object holds a strong reference), 2) Reachability (the reference chains to a GC root), 3) No Clearing (the reference is never nullified or removed), 4) Long-lived Thread (the thread holding the reference lives indefinitely, e.g., pool thread). Breaking any one condition prevents the leak.

### Senior Level

**Q9: How would you design a ThreadLocal leak detection framework that runs in production?**

**Answer:** 1) Scheduled task using ThreadMXBean that captures stack traces for threads with long-local-variable retention. 2) Bytecode instrumentation that tracks ThreadLocal.set()/remove() calls and warns when time between set and remove exceeds a threshold (e.g., 30 seconds). 3) Periodic heap dump analysis in a sidecar process: capture heap dump, load into MAT programmatically via Eclipse MAT's OQL API, query for ClassLoader instances with retained heap > threshold, and alert if count exceeds baseline. 4) Export ThreadLocalMap statistics via JMX (size, stale entry count, oldest entry age). 5) Build a trend-analyzer that monitors Metaspace/ClassLoader count over rolling 24-hour windows.

**Q10: You cannot modify the third-party library that has the ThreadLocal leak. How do you mitigate the production issue?**

**Answer:** 1) Mitigation: use a servlet Filter that calls ThreadLocal.remove() via reflection on the library's internal ThreadLocal fields after each request. 2) Use a custom ClassLoader that wraps the library's ClassLoader and forces class unloading on a timer. 3) Apply a Java agent that instrument the library to add cleanup calls. 4) Restart the application on a schedule before Metaspace exhaustion (cron job every 4 hours during low traffic). 5) Increase MaxMetaspaceSize and max heap to give more runway. 6) Replace the library with an alternative that does not have the leak.

**Q11: Design a production monitoring system specifically for Metaspace leaks.**

**Answer:** 1) Export Metaspace metrics via JMX: jdk.management.jfr.Metaspace, java.lang.MemoryPool (Metaspace pool). 2) Collect into Prometheus using JMX exporter. 3) Grafana dashboard showing: Metaspace used vs. committed vs. max, ClassLoader count (by type), loaded vs. unloaded class count, rate of class loading. 4) Alerts: Metaspace growth rate > 50MB/hour for 2+ hours (warning), ClassLoader count increasing for 4+ hours (critical). 5) Auto-diagnosis: when alert fires, trigger a JFR recording with Metaspace events, capture thread dump, and upload to S3/GCS for later analysis. 6) Burn-rate alert: if Metaspace is projected to exhaust within 24 hours at current growth rate, page on-call.

**Q12: How does Java 20+'s ScopedValue (JEP 429) solve the ThreadLocal leak problem?**

**Answer:** ScopedValue provides a bounded lifetime for per-thread context. Unlike ThreadLocal where the value persists until explicitly removed, ScopedValue is automatically cleared when the scope exits (even on exception via try-with-resources). ScopedValue is immutable within a scope and cannot be inherited by child threads unless explicitly passed. This eliminates the root cause of ThreadLocal leaks: stale entries in ThreadLocalMap that are never cleared. ScopedValue also has better performance because it does not require per-thread map lookups.

## Behavioral Questions

**Q13: Tell me about a time you debugged a production memory issue. Use the STAR method.**

**Answer:**
- **Situation:** Our API gateway was crashing with OOM errors every 6 hours during peak traffic, causing 3-5 minute outages.
- **Task:** As the primary on-call SRE, I needed to identify the root cause and prevent recurrence.
- **Action:** I added `-XX:+HeapDumpOnOutOfMemoryError` and `-XX:+PrintGCDetails`. On the next crash, I loaded the heap dump in Eclipse MAT, ran the Leak Suspects report, and traced the retaining reference chain from a URLClassLoader through a ThreadLocal in our custom filter to the request SecurityContext. I discovered the filter stored request data in a static ThreadLocal field without a finally block to remove it. I added try-with-resources cleanup and replaced the ThreadLocal with a WeakThreadLocal wrapper.
- **Result:** Metaspace stabilized, OOM crashes stopped, and we added Checkstyle rules to enforce ThreadLocal cleanup in all future code. No recurrence in 6 months.

**Q14: How do you approach on-call and how do you handle being paged for a memory issue at 3 AM?**

**Answer:** I follow the incident response process: acknowledge within 2 minutes, triage severity, assess impact. For a memory issue, I first check if the service is still running or has crashed. If crashed, I check hs_err_pid files and OS logs. If still running but slow, I check GC activity with jstat, capture a thread dump, check heap utilization. I always focus on mitigation first (restart, scale up, failover) before diving into root cause. After stabilizing, I schedule a post-mortem within 72 hours.

**Q15: Describe a situation where you had to convince your team to invest in preventing a class of production issues rather than just fixing the symptom.**

**Answer:** After the OOM incident, I proposed adding static analysis rules (Checkstyle, Error Prone) to detect unguarded ThreadLocal usage, a mandatory try-with-resources pattern for all resource cleanup, and a 12-hour load test in CI/CD. The team was hesitant because the immediate OOM was fixed. I showed that similar ThreadLocal leaks had caused production issues at Netflix, Amazon, LinkedIn, and Twitter. I quantified the engineering hours wasted on investigation (120 hours) vs. the cost of adding the checks (8 hours). Management approved, and within 3 months the checks had caught 2 pre-production leaks that would have reached production.

## System Design Questions

**Q16: Design a memory leak detection system for a fleet of 10,000 JVM instances.**

**Answer:** 1) Agent-less: export JMX metrics from each JVM (heap usage by generation, Metaspace, ClassLoader count, GC pause times) to a central metrics system (Prometheus + Thanos). 2) Sidecar: run a lightweight diagnostic agent adjacent to each JVM that captures periodic heap summaries (class histogram, top objects by retained heap) and uploads to object storage. 3) Central anomaly detector: train models on normal memory patterns and alert when: heap usage after GC is increasing, ClassLoader count is trending up, Metaspace growth rate exceeds baseline 3x. 4) Auto-diagnosis: when anomaly detected, trigger heap dump on affected instances, capture JFR recording, upload for analysis. 5) Fleet-wide dashboard showing percentile of memory health, leak-free deployments, mean time to detect memory leaks.

**Q17: Design a safe threading architecture for a Java web application that prevents ThreadLocal leaks.**

**Answer:** 1) Use request-scoped context objects stored in a per-request container (e.g., ServletRequest attribute) instead of ThreadLocal. 2) If ThreadLocal is unavoidable, enforce the "set-use-remove" pattern with a wrapper that implements AutoCloseable. 3) At framework level, add a request lifecycle interceptor that clears ALL ThreadLocal values after request completion using reflection on ThreadLocalMap. 4) For async processing, use ScopedValue (Java 20+) instead of ThreadLocal or InheritableThreadLocal. 5) For thread pools, use a custom wrapper that clears InheritableThreadLocals on task completion. 6) Add integration tests that verify: no ThreadLocal values leak between requests, no stale ThreadLocal values after concurrent request processing.

## Troubleshooting Questions

**Q18: The GC log shows "Metaspace: 128M->118M(256M)" with "Class unloading: 0 classes, 0 loaders." Is this healthy?**

**Answer:** No, this is unhealthy. Although Metaspace decreased from 128M to 118M (10M reclaimed), the class unloading count is zero. The decrease is likely from internal Metaspace chunk reuse, not from actual class unloading. With ClassLoader leak, no classes are unloaded even though Metaspace is growing overall. A healthy scenario would show "Class unloading: N classes, M loaders" with non-zero values.

**Q19: After adding a ThreadLocal in a filter, the application runs fine for 24 hours then starts slowing down and eventually crashes with OOM. What's happening and what tools would you use to diagnose?**

**Answer:** This pattern strongly suggests a ThreadLocal leak: the filter sets data in a static ThreadLocal per-request but does not call remove(). Because the application uses a fixed thread pool, each thread accumulates one stale ThreadLocal entry per request. Over 24 hours, thousands of stale entries accumulate, each holding references to request-specific objects that cannot be GC'd. The heap grows until OOM. Tools: jstat to monitor heap growth, jmap -dump:live to capture heap dump, Eclipse MAT to analyze retaining references, jcmd VM.classloader_stats to check for ClassLoader leaks if the values reference classes.

**Q20: You see in Eclipse MAT that a URLClassLoader instance has 2GB retained heap. Its "Path to GC Roots" shows: ThreadLocal$ThreadLocalMap.Entry → SecurityContext → URLClassLoader. What does this tell you and how do you fix it?**

**Answer:** This tells us: 1) A ThreadLocal is holding a SecurityContext object as its value. 2) The SecurityContext holds a strong reference to a URLClassLoader. 3) The URLClassLoader and all its loaded classes (2GB) cannot be GC'd. 4) The thread doing this is likely a long-lived thread pool thread. Fix: 1) Find the code that sets this ThreadLocal — it's storing the SecurityContext but never removing it. 2) Add try { ... } finally { threadLocal.remove(); } around the code that uses the ThreadLocal. 3) If the SecurityContext doesn't need the ClassLoader reference, break that reference by using WeakReference<ClassLoader> or by clearing the context after use.
