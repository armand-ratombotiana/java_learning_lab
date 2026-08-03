# Lab 14: Mock Interview — LLMOps (LLM Operations)

**Role**: ML Platform / MLOps Engineer
**Duration**: 60 minutes
**Focus**: metrics (p50/p95/p99), request tracing, anomaly detection, alerting, canary deployment, SLOs

---

**Interviewer**: "Walk me through the lab's operations toolkit."

**Candidate**: "Four instruments for running a model in production. `MetricsCollector`
records latency, token count, and errors per request, keeps a sliding window of up
to 1000 latency samples, and derives p50/p95/p99 via `percentile` — a sorted
nearest-rank lookup — plus `errorRate` and average tokens per request.
`RequestTracer` builds a span tree: root 'llm_request' with child spans
('embedding', 'generation'), each with UUID-ish ids, parent pointers, and
durations. `AnomalyDetector` compares live metrics against a baseline — p95 over
1.5x baseline or error rate over 2x baseline triggers alerts. `CanaryDeployer`
routes a percentage of traffic to a new version via `requestId.hashCode() % 100 <
canaryPercent` and supports rollback. The demo's numbers: 500 requests, p50 51.3ms,
p95 82.7ms, p99 91.8ms, 2.8% errors, and two alerts on the degraded window."

**Interviewer**: "Why p50, p95, and p99 rather than just the average?"

**Candidate**: "Because LLM latency is tail-heavy and the average hides the failure.
In my walkthrough's workload — 950 requests at 60ms, 30 at 500ms, 20 at 2000ms —
the average is about 106ms, which suggests a healthy service. But p99 is 2000ms:
one in a hundred users waits 33x longer. A latency SLO written as 'p99 < 200ms'
fails decisively — that's the check I run — while 'average < 200ms' would pass.
The lab's `percentile` implements nearest-rank: `ceil(p * n) - 1` into a sorted
copy of the window. p50 tells you the typical experience, p95 the degraded-tail
threshold worth monitoring, p99 the worst-case quality; per-request observability
is a 3-number summary."

**Interviewer**: "Walk through the SLO check in your walkthrough."

**Candidate**: "I define the budget as p99 < 200ms. The collector sees 1000 requests:
950 at 60ms (healthy), 30 at 500ms (slow), 20 at 2000ms with errors (timeouts).
Sorted, the p95 index lands inside the fast block — 60.0ms, within budget — and
the p99 index lands inside the 2000ms block — a breach. That's the classic
dashboard illusion: p95 green, p99 red, and if you only watch p95 you miss the
outage. The walkthrough prints both verdicts so the distinction is explicit, then
feeds the same collector into the anomaly detector as the baseline: p95 60.0ms,
error rate 2.0%. The degraded window — 400ms with 10% errors — trips both alerts:
400 > 1.5×60, and 0.10 > 2×0.02."

**Interviewer**: "The demo's trace report shows durations of 0ms. What's the point of
tracing that measures nothing?"

**Candidate**: "The lab's spans use `System.nanoTime()` for real durations, so the
demo's synthetic operations finish in sub-millisecond time — the *mechanism* is
real but the *numbers* are empty. That's why my walkthrough adds a `simulate`
helper that sets `endTime = startTime + ms` explicitly: the span tree becomes
legible — llm_request 180ms, guardrail 5ms, retrieval 35ms, generation 180ms,
all children of the root. The structural lesson survives the lab's toy durations:
spans carry parent ids so you can reconstruct the causal chain, and the root
duration is what the user sees while children attribute the time. In production
the same structure answers 'where did the 1.8s go' — guardrail, retrieval, or
generation — which a single latency number cannot."

**Interviewer**: "What makes the `AnomalyDetector` thresholds reasonable, and when are
they dangerous?"

**Candidate**: "1.5x on p95 and 2x on error rate are sane default tripwires: they
catch degradations without paging on noise — my degraded window (400ms vs 60ms
baseline, 10% vs 2% errors) trips both, while the healthy window doesn't. The
danger is assuming the baseline is stable: it's captured once from a collector
whose own window was healthy, and real baselines drift (traffic seasonality,
model changes). Static multiplicative rules also miss shape changes — error rate
doubling from 0.01% to 0.02% trips the rule but may be negligible, while a stable
p95 that hides a growing p99 escapes it. Production alerting layers this with
EWMA trend detection and time-of-day baselines; the lab's rules are the correct
*skeleton*."

**Interviewer**: "The canary in the demo routes 'req-42' to the canary — walk through
the mechanics and the caveat you found."

**Candidate**: "`shouldRouteToCanary` computes `requestId.hashCode() % 100 < 10` with
canaryPercent at 10. For 'req-42' that happens true — the demo prints it. But
here's the caveat my walkthrough surfaced: `hashCode()` can be negative, and Java
`%` on a negative value is negative — which is *also* `< 10`. With the 'req-N'
family, virtually every hash in the range I tested was negative, so the canary
was routing 10 of 20 requests instead of 2 — a 10x traffic overshoot. My
walkthrough curates ids with a non-negative modulo (e.g., req-1042 → 0, req-1079
→ 0) and demonstrates exactly 2/20 routed at 10%. The lesson is production-grade:
modulo bucketing needs `Math.floorMod`, and A/B routing ratios must be verified
empirically, not assumed."

**Interviewer**: "How would you extend this lab into a full LLMOps incident response?"

**Candidate**: "Wire the pieces together: the metrics collector feeds a dashboard and
the anomaly detector; alerts page on-call with the tracer's request id attached;
and the canary deployer is the remediation path — rollback for a model regression
or promote for a good one. The INTERVIEW guide's incident playbook maps directly:
detect (anomaly detector), kill switch (output blocking — lab 10's
`OutputGuardrail`), fallback to a safe mode (lab 15's orchestrator chain), root
cause (tracer + logs), and patch or rollback (canary deployer). The lab gives you
the four components; the playbook is the glue, and drift detection — embedding
drift, feedback signals, eval-set accuracy — is the monitoring layer above the
raw metrics."

**Interviewer**: "What's missing from the lab's metrics for a real LLM service?"

**Candidate**: "Semantic and cost signals. The lab tracks latency, errors, tokens —
all correct — but an LLM service also needs quality drift: response embedding
drift, thumbs up/down, accuracy on a shadow eval set (the lab 09 harness
periodically re-scored). And cost per request, which lab 12 attacks directly:
tokens-per-request and cache hit rate belong in this collector too. The lab's
`avgTokensPerSec` — 120.0 in my walkthrough — is the seed of that, but as an
average it hides the distribution: p90 tokens per request is the number that
matters for KV cache sizing. The architecture is right; the metric menu is
expandable."

**Interviewer**: "What's your takeaway for deployment strategy?"

**Candidate**: "Progressive rollout with measurement gates. The lab's canary is the
10%-then-promote-or-rollback pattern; production adds stages — 1% → 10% → 50% →
100% — with a gate at each step: compare p95, error rate, and quality metrics
against the baseline via the anomaly detector before widening. My walkthrough
shows both poles: a healthy 10% canary routing exactly its share, and the
rollback path returning traffic to 0% immediately (`rollback()` clears the
canary list and percent). The discipline is never to widen based on time, only
based on measured equivalence — which is why the metrics and the deployer belong
in the same lab."
