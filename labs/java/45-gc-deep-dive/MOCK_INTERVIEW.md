# Mock Interview Transcript: Garbage Collection Deep Dive

## Interviewer: Staff Engineer, Google
## Candidate: Senior Java developer
## Time: 50 minutes
## Focus: GC algorithms, heap structure, tuning

---

**Q1: Compare generational GC vs non-generational GC in HotSpot.**

**Candidate**: Generational GC (G1, Parallel) divides the heap into young and old generations. Rationale: (1) Most objects die young (infant mortality). (2) Young GC is fast (copying within a small region). (3) Old generation collects less frequently. Non-generational (ZGC, Shenandoah) doesn't split the heap. They treat all objects equally, using concurrent relocation. Trade-off: generational has better throughput for most apps; non-generational has more predictable low latency.

**Interviewer**: Explain the G1 concurrent marking cycle in detail.

**Candidate**: Phase 1: Concurrent Mark (started by Young GC, triggered by IHOP — Initiating Heap Occupancy Percent). Phase 2: Concurrent Root Region Scan (scan survivor regions for references to old gen). Phase 3: Concurrent Mark (traverse object graph from GC roots using SATB — Snapshot At The Beginning). Phase 4: Remark (STW — process SATB buffers, weak references). Phase 5: Cleanup (STW — compute live data per region, decide which regions to evacuate). After concurrent cycle, Mixed GC phases evacuate the selected old gen regions.

**Interviewer**: How does ZGC achieve sub-millisecond pauses with 1TB heaps?

**Candidate**: ZGC uses: (1) Colored pointers — metadata stored in 48-bit pointer (remapped, marked0, marked1, finalizable bits). Object address is resolved through memory views. (2) Load barriers — when Java reads an object reference, the barrier checks the colored pointer and fixes it if needed (lazy remapping). (3) Concurrent relocation — objects are moved while being accessed (load barrier fixes the pointer on access). (4) Multiple mapping — the same physical memory mapped at three different virtual addresses for coloring. (5) No generation — simple, single-pass collection.

**Interviewer**: How does Shenandoah's Brooks forwarding pointer differ from ZGC's colored pointers?

**Candidate**: Shenandoah adds a forwarding pointer field to each object header (Brooks pointer). When an object is relocated, the forwarding pointer points to the new location. ZGC's colored pointers store forwarding information IN the pointer (not the object). Trade-off: Shenandoah increases object size (an extra pointer, ~8 bytes). ZGC requires 64-bit and limits the heap to colored pointer range (but supports up to 16TB). Shenandoah has slightly higher per-object overhead but works on 32-bit and doesn't need pointer reservation.

**Interviewer**: When is a Full GC triggered in G1? What happens?

**Candidate**: Full GC (fallback) occurs when: (1) Mixed GC can't free enough space fast enough (evacuation failure). (2) Humongous allocation can't be satisfied. (3) Concurrent marking takes too long (allocation keeps going). Full GC is single-threaded, STW, full compaction — potentially very long (seconds to minutes). It should be avoided by: proper heap sizing, tuning IHOP, reducing allocation rate. Monitor: `-XX:G1HeapWastePercent` controls how aggressively mixed GC reclaims.

**Interviewer**: How do you choose between G1, ZGC, and Shenandoah?

**Candidate**: 
| Factor | G1 | ZGC | Shenandoah |
|--------|----|-----|-----------|
| Latency target | 200ms | <1ms | <10ms |
| Max heap tested | 100GB | 16TB | 2TB |
| Throughput | Good | Good | Moderate |
| JDK version | 9+ (default) | 15+ (production) | 12+ |
| Maturity | Very mature | Mature | Mature |
| CPU overhead | Low | Low | Moderate (load barriers) |

Rule: Default to G1 for most apps. Use ZGC if you need <1ms pauses or have >100GB heap. Use Shenandoah if you need low latency but can't use ZGC (32-bit, older JDK).

**Interviewer**: What GC metrics should you monitor in production?

**Candidate**: (1) Pause duration (avg, P99, max) — is it within SLA? (2) Frequency of GC pauses — how often? (3) Allocation rate (MB/s) — high rate means more GC. (4) Promotion rate — how much moves to old gen? (5) Heap occupancy after GC — trending up signals memory leak. (6) Concurrent cycle duration (ZGC/Shenandoah). (7) Full GC events — should be close to zero. (8) Time spent in GC vs application — >5-10% may need tuning.

**Interviewer**: Final: Tune ZGC for a latency-sensitive trading application.

**Candidate**: 
```bash
-Xms64g -Xmx64g
-XX:+UseZGC
-XX:ConcGCThreads=2             # Fewer concurrent threads = less CPU contention
-XX:ParallelGCThreads=4          # Parallel threads for short STW
-XX:SoftMaxHeapSize=56g          # Allow ZGC to GC earlier (before hard limit)
-XX:ZAllocationSpikeTolerance=2  # Handle allocation spikes
-XX:+ZUncommit                  # Return unused memory to OS
-XX:ZUncommitDelay=300           # Uncommit after 5 minutes idle
-Xlog:gc*:file=gc.log:time,level,tags
-Xlog:gc+init:file=gc-init.log   # Log initialization (sizing decisions)
```

---

## Feedback

**Strengths**:
- Comprehensive GC algorithm comparison
- G1 concurrent marking detail
- ZGC colored pointer mechanism
- Shenandoah vs ZGC trade-off
- Production metrics and tuning

**Areas for Improvement**:
- Could discuss `-XX:MaxGCPauseMillis` adaptation in G1
- Mention Epsilon GC (no-op) for testing

**Score**: 5/5 — Expert GC knowledge
