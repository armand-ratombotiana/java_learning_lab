# Lab 06: Mock Interview — Senior Observability Engineer

**Role**: Senior Observability/Platform Engineer | **Topic**: Cloud Monitoring System | **Duration**: 45 minutes

---

## Interview Transcript

**Interviewer**: "Design a cloud monitoring system: metric ingestion, aggregation, and alerting at the scale of a large SaaS — millions of time series, hundreds of thousands of data points per second. Walk me through the architecture."

**Candidate**: "I'd split it into four planes: ingestion, storage, query/aggregation, and alerting. Ingestion takes raw samples — (metric name, labels, timestamp, value) — from agents, exporters, and cloud APIs. Storage is the time-series database — the design decisions there dominate everything else. Query/aggregation serves dashboards and the alerting engine. Alerting is a streaming evaluation layer on top of aggregated time series. The critical architectural principle: ingestion must never block on storage or alerting — telemetry is a firehose, and the system's availability must not depend on any single downstream."

**Interviewer**: "Let's go deep on storage first. Time-series data is write-heavy and append-only. What storage layout do you use?"

**Candidate**: "The industry answer is a **log-structured merge tree** tuned for time series — that's what Prometheus's TSDB, Thanos, Mimir, and VictoriaMetrics use. Incoming samples are appended to an in-memory block, the write-ahead log provides crash safety, and the in-memory block is periodically compacted into immutable sorted blocks on disk, with higher-level compaction merging blocks. Each block stores series in the **chunk format**: consecutive samples of the same series are delta-encoded into a compressed chunk — Gorilla compression achieves roughly 1-2 bytes per sample by XORing the previous value and storing only the deltas, with special handling for repeated values (all-zero XORs take 1 bit) and small deltas. The series index is a labels-to-series-id lookup — inverted indexes per label — and the block layout keeps both hot-write and read-query performance good. The key design property: queries read immutable blocks and the in-memory window, never mid-compaction state."

**Interviewer**: "How do you scale ingestion to hundreds of thousands of samples per second?"

**Candidate**: "Three techniques: sharding, batching, and decoupling. **Sharding**: the ingestion front-end hashes the series (metric + labels) to a shard — series-to-shard affinity is essential, because a series must always land in the same shard for consistent aggregation and query. **Batching**: the front-end buffers samples in per-shard batches (say 1000 samples or 2MB) and flushes on a timer — batching turns millions of tiny TCP writes into thousands of bulk writes, which is the difference between 50k and 500k samples/sec per node. **Decoupling**: samples land in a durable log first (Kafka-like) or are written with a WAL, and the store ingests from there — this absorbs spikes and lets the store compact in the background without backpressure to the agents. The front-end also applies admission control: rate-limited label cardinality — a single series with a high-cardinality label like `user_id` can destroy the whole system's index."

**Interviewer**: "Cardinality is a classic killer. How do you handle the label cardinality problem?"

**Candidate**: "Defense in depth. First, at ingestion: a **cardinality budget per tenant and per metric** — when `http_requests_total` grows beyond, say, 100k series, the front-end starts rejecting new label values with a clear error and a dropped-samples counter. Second, labeling conventions enforced by policy: a service owner must prove a label set has bounded cardinality — `user_id` belongs in a *dimension table* joined at query time, not in the metric label set, because the metric index lives in memory. Third, detection: a cardinality anomaly detector watches per-metric series counts and flags inflection points — a leaked label (accidentally adding `request_id` to a metric) shows up as a hockey stick within minutes, and the detector pages the owner before it OOMs the storage tier. And the honest answer: **downsampling** — older data is aggregated to 1-minute or 5-minute resolution, which bounds the long-term index cost."

**Interviewer**: "Now alerting. What's the architecture of the alerting engine, and what are the correctness concerns?"

**Candidate**: "Alerting is a separate streaming service that consumes the same storage as dashboards but evaluates rules on a fixed cadence — typically every 10-60 seconds, aligned to the aggregation windows. Each alert rule is (query, condition, duration, severity): the condition must be true for a *consecutive duration* before firing — that's the standard 'for' clause that kills noise. The engine keeps per-rule, per-label-set state: pending vs firing. The correctness concerns: first, **alert deduplication** — the same condition evaluated on two shards must produce the same alert, so rules are evaluated on *aggregated* series (per-shard evaluation happens only for fan-out queries). Second, **flapping suppression** — an alert that fires and resolves within minutes pager-fatigues operators; a resolved alert should only clear after the condition is absent for a configurable duration, and there's a minimum silence between re-fires. Third, **evaluation failure semantics** — if the query engine is down, alerting must *not* silently resolve all alerts: alerts stay in their last state or go to a 'no data' state that itself can page. Fourth, alert routing: severity → target (page/on-call, ticket, Slack) with grouping — alerts for the same service and same cause are grouped into one incident, not 500 pages."

**Interviewer**: "How do you aggregate metrics — the p99 of latency is not the sum of p99s. How does that work?"

**Candidate**: "Percentile aggregation is a great trap. If each instance computes its own p99 and you average those, you get a meaningless number — that's the classic 'p99 of p99s' fallacy. Correct approaches: (a) **histogram metrics** — each instance records latency into a fixed set of buckets, and aggregation sums the bucket counters across instances; the p99 is then estimated from the global histogram (with the trade-off of bucket-boundary error, bounded by histogram resolution); (b) **t-digest or HDR per-instance with merging** — quantile sketches that merge exactly; (c) for *rates*, sum rates — that's valid. Sums and averages aggregate linearly; quantiles need sketches. In the demo engine I'd implement histogram-based p99: fixed logarithmic buckets, per-instance counters, summed across the fleet, with a `quantile()` estimation over the cumulative distribution."

**Interviewer**: "What about downsampling — how do you keep p99 meaningful after aggregation to 1-minute resolution?"

**Candidate**: "Downsampling must be **quantile-preserving**, not arithmetic. Averaging p99 values across an hour destroys the tail — a 1-second p99 spike within a minute is invisible after averaging. The correct design: keep the per-minute histogram, and downsample histograms by summing the buckets — the p99 of the summed histogram is a good approximation of the p99 of the merged distribution. Alternatively, keep min, max, count, and t-digest per window and merge the sketches. The rule that separates good monitoring systems from bad ones: **never average percentiles; always merge distributions**."

**Interviewer**: "How do you make the monitoring system itself reliable? A monitoring outage during a production incident is the worst possible time for the dashboards to be down."

**Candidate**: "Three principles. First, **independent failure domains**: the monitoring cluster is a separate cluster with separate credentials, capacity for 3x its steady-state load, and its own on-call. Second, **graceful degradation**: if storage lags, dashboards show staleness indicators rather than empty graphs — an empty graph is interpreted as 'zero traffic' by panicked engineers, so we return 'data stale' markers; if alerting can't query, it holds state instead of resolving. Third, **self-monitoring**: the system metrics its own ingestion rate, queue depth, and evaluation lag, and pages itself — the 'who watches the watchers' answer is a small, deliberately boring set of black-box probes that don't share the monitoring stack's dependencies."

**Interviewer**: "How would you test an alerting engine? Alert rules fire in production — how do you know they fire correctly?"

**Candidate**: "Deterministic testing with simulated time series: feed a rule a crafted series (flat, then a step above threshold for exactly 3 minutes, then below) and assert the alert transitions idle→pending→firing→resolved with exact timing. Property tests: for any time series, an alert that fired at T implies the condition held for the full duration window ending at T. Then **canary alerts**: every environment has a synthetic metric that steps above threshold periodically — a real end-to-end proof that ingestion→storage→query→alerting→webhook works. And every alert rule change goes through the same review as code, with a recorded evaluation replay: run the new rule against the last 7 days of real data and show the alerts it *would* have fired — rule changes are the most common source of alert storms."

---

## Wrap-Up

**What the interviewer is looking for**:
- Ingestion architecture: sharding by series, batching, WAL/log decoupling
- Time-series storage literacy: LSM blocks, chunk encoding (Gorilla), inverted index, cardinality control
- Correct aggregation: histograms for percentiles, never averaging p99s, quantile-preserving downsampling
- Alerting correctness: 'for' duration, dedup, flapping suppression, no-data semantics, grouping
- The monitoring system's own reliability: failure domains, stale markers, self-paging

**Common mistakes candidates make**:
- Averaging instance-level percentiles to get a fleet percentile
- No cardinality control at ingestion
- Alerting that resolves alerts when the query engine fails
- Unbounded queues between ingestion and storage
- Ignoring downsampling, or downsampling with plain averages
