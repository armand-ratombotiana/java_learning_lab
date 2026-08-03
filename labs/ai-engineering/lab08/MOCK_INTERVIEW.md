# Lab 08: Mock Interview — AI Observability

**Role**: AI Engineer / MLOps Engineer
**Duration**: 60 minutes
**Focus**: Metrics collection, cost tracking, latency percentiles, drift detection, budgets

---

**Interviewer**: "Walk me through the observability stack in this lab."

**Candidate**: "Four collectors feeding one picture. `MetricsCollector` is the backbone:
it records named `Metric` values — tokens, latency, cost — with a source and a
timestamp, and the lab aggregates them into a summary. `TokenTracker` attributes
token usage per model and per user. `CostCalculator` converts tokens to money using
per-model pricing, so every request carries a cost. `LatencyMonitor` computes
percentiles — p50, p95, p99 — instead of averages. On top sit the two detectors:
`DriftDetector` scores the input distribution with KL divergence, and a PSI-style
score for the prediction distribution, each compared against a threshold to flag
drift."

**Interviewer**: "Why collect metrics with sources and timestamps instead of plain
numbers?"

**Candidate**: "Because a number without provenance is useless for debugging. If the
platform shows a token count spike, you need to know which source — which user,
which model, which component — produced it and when, or you cannot act on it. The
lab's `Metric` model makes that provenance first-class: name, source, timestamp,
value, and the collector keeps them in order. This is what turns raw telemetry into
observability: the ability to ask 'what happened, where, and when' after the fact.
Without provenance, you have monitoring — numbers that move; with it, you have
observability — numbers you can interrogate."

**Interviewer**: "How does per-user cost tracking work?"

**Candidate**: "`TokenTracker` records usage per model and per user, and `CostCalculator`
applies each model's pricing — a prompt-token rate and a completion-token rate — to
produce a per-user cost. The lab's demo shows the payoff: alice's requests cost
thirty-three cents against an eighty-three-percent budget OK, bob's six cents
against thirty percent, and carol's fifteen cents blows through a ten-cent budget —
a one-hundred-fifty-percent overspend that triggers the alert. The design point: you
cannot manage cost you cannot attribute, so per-user, per-model attribution is the
unit of accountability, and budget checks are computed per user against their
allocated cap."

**Interviewer**: "How do you aggregate many requests without losing signal?"

**Candidate**: "You aggregate into summaries — totals, percents, percentiles — but you
never lose the shape: a summary of one request's latency is a number, a summary of a
thousand requests' latency is a distribution, and the p99 of the thousand is the
signal. The lab's collectors show the pattern: token metrics aggregate per model and
per user, latency metrics aggregate into percentiles per model, and cost metrics
aggregate per user against budgets — the walkthrough's thirty-six metrics come from
exactly this multi-dimensional summarization. The rule: aggregate on every axis you
might need later — model, user, time window — because you cannot re-derive detail
from a total after the fact."

**Interviewer**: "Why do you measure latency percentiles rather than averages?"

**Candidate**: "The average hides the tail: one request at ten seconds and nine at one
second average 1.9 seconds, which looks fine while ten percent of users experience a
ten-second wait. Percentiles tell the truth about the distribution — p50 is the
typical experience, p95 and p99 are the worst experiences. The lab's `LatencyMonitor`
computes them precisely and the walkthrough shows a concrete case: a p99 of 208.5
milliseconds against a 200-millisecond budget is a breach even when the average is
comfortable. When you alert on averages you get a calm dashboard and an angry
product owner; when you alert on p99 you catch the real problem."

**Interviewer**: "What is drift, and why do you detect it with distributions?"

**Candidate**: "Drift is the production input distribution moving away from the
distribution the model was validated on — users start sending questions the model
was never trained for, or in a different style. You detect it by comparing
distributions, not by watching a scalar: the lab's `DriftDetector` computes KL
divergence between the reference and recent input distributions and flags drift
when it crosses a threshold — in the walkthrough, a score of 0.0022 is in
distribution while 0.1417 triggers DRIFT. The reason distribution comparison
matters: a model's performance is a function of its inputs, and the first sign of
trouble is usually the inputs changing before the errors show up."

**Interviewer**: "What is PSI and how does it differ from KL divergence?"

**Candidate**: "PSI — population stability index — is a symmetric, bounded score for
comparing how two distributions distribute mass across bins, commonly used to
monitor prediction scores over time. KL divergence measures the information lost
when one distribution approximates another and is directional. The lab uses both
deliberately: KL on the input features to catch upstream changes, PSI on the
prediction distribution to catch behavior change — in the walkthrough the PSI score
of 0.0044 is stable while 0.2755 breaches its threshold. Two detectors, two views:
input drift and output drift are different failure classes, and one detector cannot
see both."

**Interviewer**: "How do you set drift thresholds without crying wolf?"

**Candidate**: "Thresholds come from history, not guesswork: collect the metric during
healthy operation, measure its normal variance, and set the alert boundary at the
point where real change exceeds noise — a KL of 0.0022 and a PSI of 0.0044 are
normal jitter in the walkthrough, so the alert must fire well above that band.
Then you tune against outcomes: a threshold that fires on every routine change
trains people to ignore alerts, and one that never fires is decoration. The
discipline that keeps thresholds honest: every threshold change is a deliberate,
reviewed act with a reason attached, and every fired alert is followed up to see
whether it was correct."

**Interviewer**: "What should be in an AI observability dashboard?"

**Candidate**: "Five panes: traffic — requests per model and per user; quality proxies —
token usage and completion rates; latency — p50, p95, p99 per model, which the lab's
`LatencyMonitor` produces; cost — spend per user and per model against budgets,
which `CostCalculator` attributes; and drift — the input and output distribution
scores with thresholds marked. The lab's collector suite maps one-to-one onto these
panes, and the demo's output is exactly the data each pane needs: twenty-seven token
metrics, nine latency metrics, thirty-six in total. The rule: a dashboard should
answer the question 'is anything wrong, and where', in one glance."

**Interviewer**: "How do you turn these metrics into alerts?"

**Candidate**: "Each metric gets an owner and a threshold, and alerts are structured:
what metric, what value, what threshold, what source. The lab's budget check shows
the pattern — carol's usage of $0.15 against a $0.10 budget is a named alert with the
violation visible — and the latency check shows the same for p99 against budget.
The discipline is to alert on the decision-relevant number, not on every metric: an
alert that says 'p99 breached budget for model X from user Y' is actionable; a raw
metric dump is not. Every alert should also decay — if the situation persists, the
alert repeats or escalates, it does not silently stay red forever."

**Interviewer**: "How do you track cost per token class instead of one flat rate?"

**Candidate**: "Because prompt and completion tokens are priced differently in every
real LLM API, a flat per-token average produces wrong cost estimates that drift
with the mix of traffic. The lab's `CostCalculator` applies the model's rate to
each token class separately, so the cost estimate is exact per request and per
model. That precision changes decisions: when you compare models or prompt
variants, the cost comparison is real money, not a guess, and when a user's budget
is breached the attribution is actionable. The lab's platform-level number — 6680
tokens across users at $0.1890 total — comes from exactly this class-aware
calculation."

**Interviewer**: "What is the most common failure you have seen in AI observability?"

**Candidate**: "Collecting everything and correlating nothing: dashboards full of raw
metrics — no attribution, no thresholds, no budgets — so when something breaks
nobody can answer 'what changed and for whom'. The second common failure is the
opposite: observability bolted on as an afterthought, so you cannot see per-user
cost, per-model latency, or input drift — the three things that actually break in
LLM systems. The lab's design is the counter-model: every metric with provenance,
every detector with a threshold, every cost with attribution. Observability is not
a dashboard; it is the ability to answer questions, and the lab builds the answer
structure in from the start."
