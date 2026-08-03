# Lab 08: Mock Interview — Model Monitoring & Observability

**Role**: MLOps Engineer / ML Platform Engineer
**Duration**: 60 minutes
**Focus**: Drift detection (PSI/KL/JS), performance monitoring, alerting, thresholds, monitoring architecture at scale

---

**Interviewer**: "Walk me through the drift detector. What statistics does it compute, and what does the demo show?"

**Candidate**: "The `DriftDetector` computes three divergences. `computePSI` — Population Stability Index, `Σ (actual−expected) × ln(actual/expected)` — is the industry workhorse. `computeKLDivergence` is the information-theoretic D_KL(P‖Q) = ΣP·log(P/Q). `computeJSDivergence` symmetrizes KL over the midpoint M = (P+Q)/2. The demo compares a training reference `{0.3, 0.25, 0.2, 0.15, 0.1}` against a slightly shifted current distribution and a heavily drifted one: the normal case yields `PSI=0.0119` classified `NONE`, the drifted case `PSI=0.2866` classified `CRITICAL`, with `JS-Div 0.0353` on the same drifted pair. `classifyDrift` maps PSI to severity: under 0.1 NONE, under 0.25 WARNING, at or above 0.25 CRITICAL — the standard credit-risk thresholds."

**Interviewer**: "PSI, KL, JS — when would you use each, per the lab's Q3?"

**Candidate**: "PSI is symmetric, bounded in practice, and the regulatory standard — credit risk monitoring is built on it, so for compliance-facing models it's the default. KL is asymmetric — D_KL(P‖Q) ≠ D_KL(Q‖P) — but information-theoretically precise, so it's good for dissecting *which features* contribute most to drift; the asymmetry matters when you always compare production against reference in a fixed direction. JS divergence is symmetric and bounded [0, 1], making it the most interpretable for general drift detection and dashboarding. The lab's take: PSI for regulatory compliance, JS for general detection, KL for attribution. All three are computed in one class, so switching detection method is a one-line call."

**Interviewer**: "The monitor tracks accuracy, P99 latency, and error count over a sliding window. Walk me through `PerformanceMonitor`."

**Candidate**: "`PerformanceMonitor(windowSize)` keeps a `ConcurrentLinkedQueue<PredictionRecord>` — each record is `correct` plus `latencyMs` — and `record()` adds and then evicts from the front while the size exceeds the window, so it's a true sliding window of the last 1,000 predictions. `getAccuracy` counts correct/total; `getP99Latency` sorts the latencies and takes index `ceil(0.99 × n) − 1` — the 99th percentile of the window; `getErrorCount` counts failures; `printReport` renders the three together. The demo feeds it 1,000 seeded predictions at 95% accuracy with ~100ms mean latency, injecting a degraded stretch — records 801-849 — with 20% errors and ~300ms latency; the report shows `Accuracy 96.10%`, `P99 Latency 369 ms`, `Errors 39 / 1000`."

**Interviewer**: "Why a sliding window instead of cumulative statistics since deployment?"

**Candidate**: "Cumulative statistics are memoryless in the wrong way: after 100,000 good predictions, a bad hour is invisible — the average barely moves. A sliding window over the *recent* behavior is what operators actually need: 'is the model healthy right now?'. The window size is the responsiveness/memory trade-off — 1,000 records smooths noise; a 100-record window would react faster but flappier. The interview notes mention LeetCode 346 (Moving Average from a Data Stream) for exactly this: the queue-based window where old samples fall off. The production refinement is time-based windows (last 15 minutes) rather than count-based, so a low-traffic period doesn't stretch the window indefinitely."

**Interviewer**: "The demo's alert section compares against four thresholds — accuracy < 90%, P99 > 500ms, PSI > 0.25. What actually fired?"

**Candidate**: "Exactly one alert: `CRITICAL: Data drift detected (PSI=0.2866)`. The accuracy came in at 96.10% — above the 90% floor, so no accuracy alert; P99 latency 369ms — under the 500ms bar; and the drifted PSI of 0.2866 clears the critical threshold, so `psiDrifted > 0.25` fires the critical drift alert while the intermediate warning branch (`0.1 < psi ≤ 0.25`) doesn't. The demo is deliberate: it separates the four signals — the model's performance is still fine (accuracy and latency in bounds) while the *input distribution* has moved — which is the textbook case for retraining *before* accuracy collapses. Drift is the leading indicator; accuracy is the lagging one."

**Interviewer**: "How do you detect concept drift without ground truth labels — the lab's Q2?"

**Candidate**: "Ground truth is delayed by days in most systems, so you need proxy signals: (1) prediction distribution shift — run PSI on model outputs; if the output mix changes, the model is seeing or learning something new. (2) Feature-importance drift — track the top-K features by importance; a reordering means the decision logic changed. (3) Uncertainty — rising entropy or spread of confidence scores signals unfamiliar inputs. (4) Business metrics — CTR, conversion, revenue — which move before labels arrive. The demo's `DriftDetector` gives you the first and second via PSI; the design rule is: monitor all four, because each catches what the others miss."

**Interviewer**: "The guide's metric table gives thresholds — PSI > 0.25, accuracy < 0.85, P99 > 500ms, error rate > 1%. How do you set thresholds properly instead of guessing — the lab's Q4?"

**Candidate**: "Statistically grounded, not vibes: compute the metric on a held-out validation set during training to establish a baseline distribution — for PSI, compare validation windows against training and take the 95th percentile of observed PSI as the alert threshold. Then layer severities: warning at PSI 0.1, critical at 0.25 — the lab's `classifyDrift` bands. For performance, thresholds should track SLOs: the error-rate alert at 1% comes from a latency/error SLO budget, not a guess. And thresholds need tuning loops: too many alerts → alert fatigue → ignored pages; too few → silent degradation. The interview notes add the automation: critical drift triggers retraining with human-in-the-loop approval for the production promotion."

**Interviewer**: "Design a monitoring system for 1,000+ deployed models — the lab's Q1."

**Candidate**: "Streaming architecture: every prediction → Kafka → Flink/Spark streaming computes drift and performance per model per window; reference distributions live in the feature store (Lab 04), so the streaming job reads the training-time baseline from there. Expose metrics to Prometheus per model — labels for model name, version, stage — and dashboards in Grafana; alerts to PagerDuty/Slack with severity from the thresholds. Everything lands in a time-series store (InfluxDB/TimescaleDB) for historical analysis: 'when did drift start', 'which model family drifts most'. The key scaling decision: compute drift in-stream at ingestion rather than querying it per model on demand — 1,000 models times hourly windows is a batch job sized problem, and only streaming keeps it cheap."

**Interviewer**: "The demo's `Math.random`-free simulation uses `new Random(42)` for the 1,000 predictions. Why does determinism matter here?"

**Candidate**: "Because a monitoring system's own correctness must be verifiable: with a seeded stream, the demo produces the exact same accuracy, latency tail, and alert set on every run — `96.10%`, `369ms`, `39/1000`, one CRITICAL drift alert — which makes the lab's expected output checkable and the monitor's math auditable. In production the analog is replay: you must be able to re-run monitoring over a stored prediction log (the audit trail from Lab 11) and reproduce the same alerts, because when a regulator or an incident review asks 'why did this alert fire', 'replay it' is the only acceptable answer. Seeded tests are the unit-test version of that discipline."

**Interviewer**: "How do drift alerts connect to retraining — the closed loop the guide's best practices mention?"

**Candidate**: "The loop: drift detection fires → the alert carries the model name and PSI → a retraining pipeline is triggered (Lab 07's data-trigger path) → the new model is validated against the drifted distribution → registry promotion (Lab 03) → the new model's reference distribution is updated in the feature store → monitoring restarts against the new baseline. The two failure modes to engineer around: triggering on noise (a single hour's blip triggers an expensive retrain — hence the warning/critical banding and the window size), and never updating the reference (a model trained on drifted data but monitored against the original baseline will alert forever). The lab's `classifyDrift` severity bands are the anti-noise machinery; the CI/CD lab's gate logic is the anti-staleness one."

**Interviewer**: "The lab's `printReport` and alert code live in `main`. What's wrong with that structure for production?"

**Candidate**: "It's demo wiring, not a product. Production needs the detector as a library — `DriftDetector` and `PerformanceMonitor` are already properly separated as classes, which is the right seam — and the alert logic as a rules engine reading thresholds from config, not hardcoded in `main`. The monitoring pipeline should run as a scheduled job or stream consumer that loads the reference, pulls the window, computes, and emits structured alert events; the dashboard and pager are consumers of those events. The lab's structure is the teaching version of the right architecture: detector, monitor, and evaluator as distinct units — the production work is moving the wiring out of `main` into the platform."

**Interviewer**: "Tie the lab to its LeetCode references: 346, metrics collection, dashboards."

**Candidate**: "Moving Average from Data Stream (346) is the sliding window — `PerformanceMonitor`'s queue with eviction is the class's design in miniature. Design a Metrics Collection System covers the production shape: an agent in the model server emitting latency/error samples (the lab's `PredictionRecord`), aggregators, and time-series storage. Design a Monitoring Dashboard is the visualization layer: Grafana panels per metric with the severity bands from the guide's table. The unifying idea: monitoring is a pipeline — sample, window, aggregate, alert — and the lab builds the middle of it in 100 lines."

**Interviewer**: "What's the most dangerous false belief about monitoring in ML, and what does this lab's design fight against?"

**Candidate**: "That monitoring is optional infrastructure you add after launch. The demo's structure fights that directly: drift detection and performance monitoring are the *first* classes, and the alert section proves they fire correctly — the lab treats observability as part of the model, not an afterthought. The specific danger: teams monitor accuracy alone and discover drift three weeks late, when the retrain cost is highest and the trust damage is done. The lab's answer is the multi-signal design — PSI on inputs, latency, error rate, accuracy — and the thresholds that turn each into an alert. If I had to state the thesis: a model in production is a running system, and running systems that aren't watched are already broken."
