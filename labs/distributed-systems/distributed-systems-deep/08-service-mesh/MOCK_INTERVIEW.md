# Lab 08: Mock Interview — Sidecar Proxy & Circuit Breaking

**Role**: Senior Platform Engineer / Service Mesh
**Duration**: 45 minutes
**Company style**: service-mesh / platform infrastructure

---

**Interviewer**: "What is a sidecar proxy, and what is the service mesh model? Why a sidecar instead of, say, a library?"

**Candidate**: "A sidecar is a proxy *co-located with each service instance* — same pod/machine, localhost to the app, intercepting inbound and outbound traffic. The mesh: a **data plane** of sidecars doing the per-request work (routing, retries, timeouts, circuit breaking, TLS, telemetry) and a **control plane** that distributes configuration and certificates (in Istio: Envoy sidecars + pilot/istiod as control plane; in Linkerd: a per-node proxy). The app sees localhost; the network sees the sidecar. The core argument for the sidecar over an in-process library: **polyglot and zero-code**. A library must be maintained in every language and version, requires app changes and releases to upgrade, and couples the app to the mesh's evolving features. The sidecar is language-agnostic — one proxy implementation, upgraded by the platform team without touching the service code; the app's only requirement is 'talk to localhost:PORT'. Costs: the extra hop adds latency (~sub-ms in-process, ~1ms+ with kernel hops), a resource tax per instance, and the operational surface of a proxy fleet. The nuanced answer: the sidecar wins when you have many languages/teams and want platform-owned traffic policy; a library wins when you need deep app-aware semantics (transaction-scoped retries), or when latency and memory matter more than operational decoupling."

**Interviewer**: "Circuit breaking — walk me through the states, the transitions, and the parameters."

**Candidate**: "Three states: **CLOSED** — traffic flows normally, failure rate is measured over a window (sliding or fixed); **OPEN** — failures exceeded the threshold; the breaker *fails fast*: requests are rejected immediately (usually with a 503 or a configurable fallback) without touching the failing service; **HALF-OPEN** — after the open timeout, a *probe* request is allowed through; success (often a configurable number: N successes or a single probe) → CLOSED; failure → back to OPEN. Parameters: (1) `failureThreshold` — e.g., 5 consecutive failures, or 50% of the last 100 requests (percentage windows are better for bursty traffic); (2) `openTimeout` — how long to stay open before probing (too short → flapping against a slow-failing service; too long → availability loss for a service that has recovered); (3) `probeCount` / `halfOpenRequests` — how many probes succeed to close; (4) `minRequests` — the minimum sample size before the breaker starts tripping (avoid tripping on tiny windows); (5) a **failure classification** — timeouts vs 5xx vs *network* errors; and crucially, the *fast-fail response*: the caller gets a fallback (stale cache, degraded response, or a hard error), not a hang. The design rule: circuit breaking protects the *caller's* availability from the *callee's* failure — the breaker's job is to convert 'the backend is down' into 'the caller fails fast with a bounded cost'."

**Interviewer**: "But isn't a circuit breaker just a timeout with extra steps? When does it actually add value over timeouts and retries alone?"

**Candidate**: "Good challenge — the distinction is *load protection and failure cost bounding*. Timeouts bound *latency* per request; retries bound *transient* failures. But without a breaker: (1) every request still *attempts* the failing backend — when the backend is down at scale, that's connection-queue saturation, thread-pool exhaustion, and *retry storms* — the caller's capacity dies even though it 'times out' correctly; (2) retries without a breaker *amplify* load onto the failing service — each of 1000 callers retrying 3× turns 1000 requests into 3000+; (3) the breaker converts the *amortized* failure cost into a near-zero fast-fail: an OPEN breaker rejects with O(1) work. The quantitative argument: with P99 timeout of 1s and 1000 concurrent callers, each timeout costs a worker thread ~1s; a breaker rejects in microseconds, freeing the caller's threads for healthy traffic. Retries and timeouts answer 'how long do I wait?'; the breaker answers 'should I even try?' — and the *combination* matters: retry *before* the breaker trips, respect the breaker's state on retry decisions, and back off the retry schedule (exponential + jitter) or you'll retry into an OPEN breaker."

**Interviewer**: "Retries in a mesh — what's the danger, and how do you bound them?"

**Candidate**: "The danger is **retry amplification**: a single user request fans out to a service that retries 3×, each of those retries calls a downstream that retries 3× — a burst on one upstream request can multiply into 9-27 downstream calls, and under a partial failure this *is* the outage amplifier (the classic 'retry storm' in multi-tier systems). Bounding strategies: (1) **budget**: each tier may only spend a fraction of its requests on retries — 'max 10% of calls may be retries' (a retry budget — Google's gRPC approach); (2) **per-request cap**: cap retries per logical request (typically 1-2), with the cap *not* inheritable across tiers (a global header/context counter — the mesh propagates a 'retries used so far' metadata); (3) **exponential backoff with jitter** — never synchronized retry waves (retry storms are partly *synchronized* storms); (4) **only retry idempotent calls** — a POST that already committed must not be retried blindly; use idempotency keys; (5) **timeouts on the retry budget**: a total deadline per logical request (a retry must fit inside the caller's overall budget, not restart it). The senior answer: retries are *anti-fragile* only with budgets and idempotency — otherwise they convert transient failures into cascading load."

**Interviewer**: "Design: checkout service calls 'payments', 'inventory', and 'shipping' — payments is a third-party API with 2% 5xx rate and high latency variance. Design the traffic control around it."

**Candidate**: "Three layers. **(1) Timeouts**: connect 500ms, request deadline 3s (payments' p95), and *never* a caller timeout shorter than the retry budget (retries must fit inside the deadline). **(2) Circuit breaker**: failure threshold at a *percentage window* — e.g., 30% of the last 50 requests fail (not '5 consecutive' — 2% baseline 5xx means consecutive-failure logic would flap on random bursts); minRequests = 20 so a quiet hour doesn't trip the breaker; openTimeout starts at 30s, doubling on repeated trips (exponential backoff of the probe interval); HALF-OPEN with a single probe first (cheap to try, instant retreat). Crucially, *classify failures*: 2% baseline 5xx is *expected* — the breaker must trip on *degradation beyond baseline* (failure rate vs a floor), and timeouts count as failures, 4xx don't. **(3) Retries**: max 1 retry on *idempotent* payment intents only (idempotency key per attempt), with the retry riding the *same* deadline; a retry budget of 10% of calls so a payments outage can't double inventory/shipping load. Plus: a **fallback** when OPEN — return 'payment pending' (store the intent) rather than an error, so checkout remains available with degraded semantics. The design answer: *timeouts bound latency, classification keeps the breaker honest, budgets bound amplification, fallback preserves availability*."

**Interviewer**: "How do you test a circuit breaker and the mesh behavior? Give me the concrete test list."

**Candidate**: "**(1) State-machine unit tests**: scripted failure injections — 5 consecutive failures → OPEN; probe success → CLOSED; probe failure → re-OPEN; parameter edge cases (minRequests=0, threshold=100%); assert every transition and the fast-fail behavior (OPEN requests never reach the backend — count backend calls and assert zero). **(2) Integration with a real/scripted backend**: a controllable failing endpoint; assert (a) caller latency collapses once OPEN (P99 goes from timeout-bound to micro/millisecond fast-fail), (b) the backend gets *zero* traffic while OPEN, (c) HALF-OPEN probes resume traffic correctly (a backend that recovers reopens within one probe window), (d) flapping: a backend alternating healthy/failing doesn't cause continuous tripping (openTimeout backoff works). **(3) Load/chaos**: 1000 concurrent callers against a failing backend — assert thread-pool/latency stays bounded (the breaker's load-protection purpose); retry amplification test: a 3-tier chain, fail the bottom tier, assert top-tier retries are capped by budgets (measure total downstream calls ≤ budget). **(4) Property tests**: random failure patterns — invariants: 'when OPEN, backend requests = 0', 'when CLOSED and healthy, requests flow', 'state machine never gets stuck' (HALF-OPEN always resolves). The interview answer: test the *transitions* deterministically, the *load behavior* statistically, and the *amplification* end-to-end."

---

## Debrief

### What the interviewer looked for

| Area | Signal |
|------|--------|
| Sidecar rationale | Polyglot, zero-code, control/data plane separation, costs |
| Breaker mechanics | CLOSED/OPEN/HALF-OPEN, parameters, classification |
| Why breakers | Load protection beyond timeouts; retry amplification |
| Retry bounds | Budgets, caps, idempotency, jittered backoff |
| Design | Threshold vs baseline, fallback semantics |
| Testing | Deterministic transitions + statistical load behavior |

### Candidate strengths
- "The breaker answers 'should I even try?', timeouts answer 'how long do I wait?'" — the sharpest possible framing.
- Correctly called out *classification* (timeouts count, 4xx don't) and baseline-vs-threshold tripping.
- The retry-budget answer (budget per tier, not per request) is production-grade.

### Gaps to work on
- Could have mentioned **Envoy-specific details** (outlier detection vs circuit breakers, `max_requests`/`max_pending_requests` *capacity* limits distinct from *error-rate* limits).
- No mention of **mesh security** (mTLS in the sidecar, SPIFFE identities) — worth one sentence for a platform role.
- The HALF-OPEN probe count discussion was brief (single probe vs N probes before closing).

## Follow-up study prompts
1. Envoy's outlier detection: what's the difference between `circuit_breakers` (capacity) and `outlier_detection` (error-rate ejection), and when does each matter?
2. Construct a 3-tier retry storm in a simulator: what budget per tier keeps total amplification ≤ 1.5×?
3. mTLS in the mesh: how do sidecars bootstrap SPIFFE identities, and what breaks if the control plane is down at pod startup?

---

## Extended Rounds — Deeper Dives

**Interviewer**: "Let's go deep on Envoy's two mechanisms. Capacity limits vs outlier ejection — what's the actual difference?"

**Candidate**: "**Circuit breakers (capacity limits)**: hard caps on *in-flight* activity — `max_connections`, `max_pending_requests`, `max_requests`, `max_retries` — for a cluster (the upstream). When a cap is hit, new requests are rejected (503) *without touching the upstream*. They protect against *overload*: a slow or saturated upstream builds up pending queues; the breaker caps the queue, converting the pile-up into fast failures. **Outlier detection (error-rate ejection)**: the proxy *ejects* individual upstream hosts from the load balancer for a duration when their error rate exceeds a threshold (e.g., 50% of the last 100 requests, with `success_rate_minimum_hosts` guarding small sample sizes). It's *adaptive* — it removes the sick host from rotation while leaving healthy hosts untouched. The relationship: outlier detection is the *surgical* tool (per-host), circuit breakers the *blunt* tool (whole cluster). The production answer: *use both — breakers cap the queue so a degraded cluster can't hang callers; outlier ejection drains the specific bad hosts so the cluster degrades gracefully*."

**Interviewer**: "mTLS in a mesh — walk me through identity and the certificate lifecycle. What's SPIFFE?"

**Candidate**: "**SPIFFE** is the identity standard: every workload gets an SPIFFE ID — a URI like `spiffe://trust-domain/ns/checkout/sa/payments-sa` — and a short-lived X.509 SVID (certificate) proving it. Lifecycle: (1) at pod startup, the sidecar's agent (e.g., Envoy's SDS or a node agent) authenticates the *node* to the control plane (via a trust anchor — the cloud provider's instance identity or a join token); (2) the control plane (SPIRE) issues an SVID bound to the workload's service account; (3) sidecars present SVIDs to each other; the receiving side verifies the chain against its trust bundle and enforces `AuthorizationPolicy` (which SPIFFE IDs may call which). The critical operational detail: **short-lived certs + rotation** — SVIDs last hours; the sidecar refreshes via SDS (secret discovery service) *without restarting*, and rotation must be **staggered** (no synchronized expiry). The failure case: the control plane is down at pod startup — the pod can't get its identity. The design answer: *the mesh must run a bootstrap trust store (a cached trust bundle) and a retry-with-backoff on SDS, plus 'fail closed' policies (no cert = no traffic) for security-first, with an override for emergency paths*."

**Interviewer**: "Traffic shifting and progressive delivery — how does the mesh implement canary or blue-green without app changes?"

**Candidate**: "The mesh's routing layer does it declaratively. **Canary**: route 5% of traffic to version v2 (`weight: 5` on the v2 route, 95% on v1), with a *header-based* override for internal testing (the team hits v2 with a `canary: true` header). The evaluation loop: watch v2's error rate and latency (the mesh emits per-route telemetry) against a release gate — promote to 50%, then 100%. **Blue-green**: switch the whole traffic weight between two fully deployed versions (instant rollback = flip the weight back). **Shadow (dark) traffic**: mirror a copy of production traffic to v2 without affecting responses — the safest validation for hard-to-test flows. The mesh details: weight changes are *atomic config pushes* (the control plane versions the config; a bad push is rolled back by reverting the config version), and **failure handling during shifting** — if v2's error rate trips the route's circuit breaker, the mesh can *auto-rollback* by reweighting. The interview point: *the mesh turns releases into a config operation — the app stays unchanged, and the release gate is telemetry, not code*."

**Interviewer**: "The mesh's control plane goes down — what exactly still works, and what breaks?"

**Candidate**: "This is the key availability design. **Data plane independence**: the sidecars are *stateless with respect to control-plane calls* — they hold the last-pushed config (routes, clusters, listeners, secrets via SDS cache) and keep enforcing it *in memory*. Traffic keeps flowing, circuit breakers keep tripping, mTLS keeps verifying with cached trust bundles — **the mesh degrades to its last-known-good config**. What breaks: (1) *new* config — new services, new routes, cert rotations (SVIDs expire → new workloads can't get certs → new pods fail closed); (2) discovery — new endpoints aren't added (a newly scaled pod isn't in the cluster until the control plane returns); (3) config fixes — you can't change policy during the outage. The production answers: (a) **signed configs with a long-lived fallback** — the sidecar validates a signed config snapshot it can trust without the control plane; (b) **graceful expiry** — trust bundles and SVIDs with *overlap* (renew before expiry, and a fail-open mode for cert renewal during control-plane outages if policy allows); (c) **control-plane HA** — replicas + Raft/etcd so 'control plane down' is rare. The interview point: *the mesh must run on last-known-good during control-plane outages, and the failure budget for 'new things' (new pods, new config) is the control plane's SLO*."

**Interviewer**: "Final: observability in the mesh — what does the sidecar give you that an in-app library doesn't?"

**Candidate**: "Four things. (1) **Uniform, app-agnostic telemetry**: every request produces spans/metrics regardless of language or framework — golden signals per service, per route, per *peer* (caller→callee matrix) without instrumenting every app. (2) **Traffic-level attribution**: the sidecar sees retries, timeouts, circuit trips, and ejected hosts — the *failure mechanics* of the call graph, not just latency — so a dashboard can show 'payments rejected 30% due to circuit breaker' as a first-class metric. (3) **Deep access logs**: per-request metadata (route, cluster, TLS cipher, response flags) — the sidecar's logs are the debugging substrate for traffic issues. (4) **Consistent topology**: service maps built from actual traffic (not config) — 'who calls whom, how often, and how healthy is each edge'. The cost comparison: an in-app library gives richer *application* context (business spans) but fragments the platform's view across languages; the mesh gives one telemetry plane for all traffic. The senior answer: *run both — mesh telemetry for the network view and SLOs, app telemetry for business semantics — and the mesh's per-edge circuit/retry metrics are the most important signal you get for free*."

---

## Post-Interview Self-Assessment

### What the candidate would do differently
- Open with the breaker-vs-outlier distinction (capacity vs error-rate) — it was buried mid-answer.
- Prepare the SPIFFE lifecycle as a four-step narrative (node auth → SVID → mutual verify → policy) — the cert-bootstrap case deserves a first-class answer.
- Rehearse the control-plane-outage answer as 'last-known-good + three failure modes' — the strongest structural answer in the interview.

### One-sentence takeaway
- "The mesh's value is moving traffic policy out of the app and into the platform: circuit breakers bound failure, retry budgets bound amplification, mTLS authenticates identity, and the data plane's last-known-good independence bounds control-plane outages."

### Self-check questions (run before the real interview)
1. Can I contrast Envoy circuit breakers (capacity) with outlier detection (error-rate) precisely?
2. Can I narrate the SPIFFE/SVID lifecycle and the control-plane-down-at-startup failure?
3. Can I design a canary release with weights, header overrides, and a telemetry gate?
4. Can I enumerate what breaks when the control plane is down (new pods, new config, rotations)?
5. Can I argue mesh telemetry vs app telemetry without conflating them?

---

## Quick-Fire Practice Rounds (30 minutes)

Answer each in under 60 seconds. Then check the hint line.

**Q1.** Why a sidecar instead of a library?
**Hint.** Polyglot, zero-code, platform-owned upgrades — at the cost of an extra hop and per-instance resources.

**Q2.** Order the breaker states and their triggers.
**Hint.** CLOSED (measure) → OPEN (fail fast) → HALF-OPEN (probe) → CLOSED or OPEN.

**Q3.** What does the breaker protect that timeouts alone don't?
**Hint.** Caller capacity: connection queues, thread pools, retry storms — fast-fail frees workers for healthy traffic.

**Q4.** Capacity breakers vs outlier ejection — one sentence each.
**Hint.** Breakers cap in-flight queue (whole cluster); ejection removes the sick host from rotation (per-host).

**Q5.** How do you bound retry amplification?
**Hint.** Retry budgets per tier, per-request caps, jittered backoff, idempotency-only retries, total deadlines.

**Q6.** What is a retry budget, exactly?
**Hint.** At most ~10% of calls may be retries — amplification stays bounded even under outage.

**Q7.** What is SPIFFE?
**Hint.** Workload identity standard — spiffe:// URIs + short-lived X.509 SVIDs, verified via trust bundles.

**Q8.** Control plane down — what still works?
**Hint.** Data plane runs last-known-good: routes, breakers, cached certs; new pods/config/cert-rotation break.

**Q9.** How does a canary release work in a mesh?
**Hint.** Weighted routing (5% → 50% → 100%) with header override + telemetry gate; flip back = reweight.

**Q10.** What does mesh telemetry add over app telemetry?
**Hint.** Uniform per-edge metrics, retry/circuit attribution, access logs, traffic-derived topology.

### Scoring
- **8-10 correct**: ready for the mesh loop.
- **5-7**: revise the breaker state machine and retry bounding.
- **<5**: re-read the walkthrough before the interview.

## One-Week Preparation Plan

**Day 1-2**: Implement the lab (`CircuitBreakerProxy`) and pass the state-machine and budget tests.
**Day 3**: Quick-Fire rounds; draw the breaker state machine with parameters from memory.
**Day 4**: Rehearse the retry-budget and SPIFFE lifecycle answers.
**Day 5**: Drill the extended rounds (Envoy mechanisms, control-plane outage, traffic shifting).
**Day 6**: Mock interview, 45 minutes, no notes.
**Day 7**: Score against the Debrief table; study the follow-up prompts.
