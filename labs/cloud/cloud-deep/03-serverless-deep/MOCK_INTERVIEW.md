# Lab 03: Mock Interview — Senior Cloud Engineer (Serverless)

**Role**: Senior Cloud/Serverless Engineer | **Topic**: Serverless Function Execution Engine | **Duration**: 45 minutes

---

## Interview Transcript

**Interviewer**: "Design a serverless function execution engine with cold start mitigation. Think of something like Lambda or Cloud Functions. What are the core components?"

**Candidate**: "I'd decompose it into four planes. The control plane: function registration, versioning, and scaling policy. The data plane: the worker fleet that actually executes invocations. The scheduling plane: how an invocation gets matched to a warm sandbox. And the observability plane. The cold start problem lives at the intersection of the scheduling and data planes — it's a warm-pool management problem: we hold a pool of pre-created sandboxes with the runtime loaded, and the scheduler's job is to keep the warm pool sized and shaped correctly so that invocations rarely find an empty pool."

**Interviewer**: "Walk me through the invocation path, end to end, for a warm invocation."

**Candidate**: "The front-end API accepts the invocation and enqueues it. The scheduler picks a sandbox from the warm pool that matches the function version — same runtime, same memory size, same code revision, because a sandbox is only reusable for the exact same function and version. The sandbox is marked busy, the payload is delivered, the handler runs with a timeout — I'll default to 15 seconds, configurable up to 15 minutes — and the response is returned. Critically, the sandbox is *not* destroyed after the response: it goes back into the pool, because reusing it for the next invocation of the same function is what turns cold starts into warm starts. The lifetime of a sandbox is governed by idle timeout — say 10-15 minutes of inactivity — after which it's reaped."

**Interviewer**: "What exactly makes a cold start cold? Break down the latency budget."

**Candidate**: "A cold start has four additive components. First, sandbox initialization — creating the container or microVM, typically 100-500ms with Firecracker-style fast boot. Second, runtime loading — the JVM or Node process boot, JIT warm-up; for Java this is the killer, often 300ms to seconds. Third, code loading — fetching the function package and initializing the handler, dependencies, static state. Fourth, orchestration — the scheduler matching an invocation to a sandbox and connecting the two, usually 10-50ms. The industry-cited 99th percentile cold start for a Java Lambda is often over 5 seconds, and the honest engineering answer is that you mitigate the components in this exact order: reuse (warm pool), faster initialization (snapshotting), and finally the classic language-level tricks (smaller JVM, GraalVM native image, less dependency loading at init)."

**Interviewer**: "How does snapshotting actually work — can you reuse a JVM across invocations safely?"

**Candidate**: "The modern answer is snapshot-based initialization — the approach behind AWS Lambda's snapshot restore and CRaC (Coordinated Restore at Checkpoint) in the JVM. The flow: a special init process starts a sandbox, loads the runtime and the function code, runs initialization to a safe point, then *checkpoints* the whole process memory to disk — or a page cache — and shuts the process down. A subsequent cold start is a *restore*: fork the snapshot, which skips runtime boot and init entirely, bringing a Java function cold start down from seconds to tens of milliseconds. The safety requirement is that the checkpoint happens at a well-defined point — after the handler is initialized but before any per-invocation state exists — and the JVM support must handle things like RNG state, open file descriptors, and thread state correctly at restore. CRaC does this via a context abstraction: resources that shouldn't be checkpointed are registered and closed before the checkpoint, then re-opened after restore. This is the single biggest lever for Java serverless, and it's the right answer when someone says 'Java is too slow for serverless' — it isn't anymore, if you design for it."

**Interviewer**: "Let's talk about concurrency. You have N functions, each with a concurrency limit. How do you schedule?"

**Candidate**: "The scheduler maintains a warm pool per (function, version, memory) key, with a target size and a minimum size. When an invocation arrives: try the warm pool; if empty and the function's concurrency budget allows, *spawn* a new sandbox — this is the cold path; if the budget is exhausted, the invocation is throttled with a 429 and retryable semantics, and the concurrency dashboard shows it. Pool sizing is the interesting control problem: too big wastes money on idle sandboxes; too small causes cold-start latency spikes. I'd use a reactive controller: pool size tracks the smoothed request rate times average execution time (Little's law — in-flight work = rate × latency), plus a guard — at least 1 warm sandbox per active function, at most the concurrency limit. When the rate drops, the idle-reaper brings the pool back down."

**Interviewer**: "What about burst traffic — a function that goes from 10 RPS to 10,000 RPS in a minute? Cold starts are unavoidable at that point. What do you do?"

**Candidate**: "You can't warm a pool for infinity; you need a three-layer defense. Layer one: predictive pre-warming — if we have a signal (cron schedule, prior day's pattern), pre-scale the pool ahead of the burst. Layer two: fast cold start — snapshot-based restore so even the cold path is ~50-100ms, which makes the burst's tail acceptable. Layer three: admission control with backpressure — when the pool is empty and spawn rate can't keep up, shed load politely: return 429 with `Retry-After` headers rather than queueing unboundedly, because unbounded queuing converts a burst into a zombie wave that overwhelms downstream. And there's a subtle one: batch arrival smoothing — the front-end can collect invocations for a few milliseconds and deliver them to already-warmed sandboxes in one batch, which is a cheap way to multiply throughput per sandbox."

**Interviewer**: "How do you handle the data plane's state — sandbox lifecycle, leases, and failures? This is a distributed system problem."

**Candidate**: "The sandbox registry is the source of truth: sandbox states are `CREATING → READY → BUSY → READY → REAPED`. The scheduler leases a READY sandbox before delivering the payload — lease with a TTL so a crashed scheduler's lease expires and the sandbox returns to the pool. If a worker node dies, its sandboxes go to `LOST`, the invocations in flight are retried at-least-once — with idempotency keys exposed to the function so duplicate execution is detectable — and the pool is refilled. The registry itself is a replicated KV store; the scheduler is sharded by function hash so each scheduler owns a subset and the lease table stays small. Crash recovery is the key test: the invariant is 'no invocation is delivered to two sandboxes simultaneously, and no sandbox is double-leased.'"

**Interviewer**: "How does the engine isolate functions from each other? One function shouldn't be able to see another's memory or disk."

**Candidate**: "Isolation is per-sandbox, and the sandbox is the trust boundary. Default is microVM isolation — Firecracker-style — with hardware-virtualized memory, a read-only root filesystem, an ephemeral writable layer that dies with the sandbox, and per-sandbox network namespace. This is heavier than containers — that's part of the cold-start budget — but it's the only honest answer for multi-tenant execution where the tenant code is untrusted. Per-invocation, the runtime tears down the writable layer between invocations so function A's invocation 1 can't leak state into invocation 2 of the same function, and definitely not into another tenant's sandbox. For trusted-internal functions there's an optional container-mode fast path, but the default stays microVM."

**Interviewer**: "How do you measure cold starts in production, and how do you know your mitigations work?"

**Candidate**: "Every invocation records an `initDurationMs` — time from sandbox creation to handler-ready — and `durationMs` for the handler itself. These are aggregated per function into p50/p95/p99 and surfaced as SLOs: the cold-start SLO is p99 initDuration below a budget per runtime — for Java with snapshots we target <150ms. We also track the warm-pool hit rate per function: percentage of invocations served by a READY sandbox. The pool controller's target is ≥95% hit rate for steady-state traffic. Alerting: p99 initDuration breach, warm-pool depletion for a function with active traffic, and throttling rate above 1%. And every release of the engine goes through a soak test that replays recorded production traffic — because nothing destroys cold-start performance like an innocent change to the snapshot restore path."

**Interviewer**: "Any closing thoughts on the design — what's the biggest architectural risk?"

**Candidate**: "The biggest risk is the warm pool becoming a money pit or a reliability trap — people either over-provision it (idle cost) or under-provision it (latency SLO breach), and both happen in the same system at different times of day. That's why the reactive pool controller with a floor and ceiling, driven by Little's law, is the heart of the engine — it's the difference between a demo and a system that runs a real workload at a real cost. The second risk is underestimating the blast radius of the snapshot-restore path; it deserves the same chaos testing as the scheduler itself."

---

## Wrap-Up

**What the interviewer is looking for**:
- Precise decomposition of the cold-start latency budget (sandbox init, runtime, code load, orchestration)
- Knowledge of snapshot-based restore (CRaC-style) as the modern Java answer
- Warm-pool sizing driven by Little's law and concurrency budgets, not guesswork
- Distributed-systems correctness: leases, TTLs, idempotent retry, no double-delivery
- Realistic burst handling with admission control rather than unbounded queues

**Common mistakes candidates make**:
- Treating cold starts as unavoidable and skipping mitigation design
- No discussion of the cost/latency trade-off of warm pools
- Forgetting the sandbox reuse lifecycle (idle reap, lease TTL)
- Ignoring multi-tenancy isolation in the sandbox design
- Only measuring averages instead of p95/p99 cold-start latency
