# Mock Interview — Network Observability

*Transcript of a senior-level interview covering flow logs, sampling, metrics, eBPF, and a design exercise.*

---

## Opening

**Interviewer:** What's your experience with network observability at scale?

**Candidate:** I've built and run the network telemetry pipeline for a platform carrying about 10 Gbps at peak across a few hundred services. The stack was flow logs at the edge, kernel-level metrics via eBPF for the service mesh, and a sampling/aggregation pipeline in between. My deepest work was in two places: the sampling mathematics — making sure the data we kept was *statistically honest* — and the flow-log aggregation that turned millions of flows per minute into a handful of queries people could actually run.

**Interviewer:** Start broad. What is the difference between metrics, logs, and flow records as network observability signals?

**Candidate:** Three different shapes of the same underlying traffic. **Metrics** are pre-aggregated numbers — throughput, packet loss, latency percentiles — cheap to store, fast to query, and blind to *which* conversations produced them. **Logs** are individual events — a packet capture is a log of every byte, an HTTP access log is a log of every request — maximally detailed, maximally expensive. **Flow records** are the middle ground: a *summary* of a conversation — five-tuple, start/end time, byte and packet counts — one row per flow instead of one row per packet. That's the sweet spot for network observability: enough structure to answer "who talked to whom, how much, when" without capturing everything. The art of the pipeline is choosing the shape per question: connectivity problems want flows, protocol problems want captures, trend problems want metrics.

---

## Sampling

**Interviewer:** Let's go deep on sampling, since you said that's where you spent real time. What's wrong with naive packet sampling?

**Candidate:** Naive sampling has one fatal bias: it samples *packets*, not *flows*. A flow that lasts five minutes with thousands of packets gets many samples; a short flow of ten packets gets few or none. When you aggregate, you systematically undercount short flows — the SYN scan, the health check, the DNS query. Every question you answer from sampled data inherits that bias.

**Interviewer:** So how do you sample correctly?

**Candidate:** Two orthogonal techniques, and the good pipelines use both. **Packet sampling** with a fixed probability p gives you an unbiased estimate of *volume* — bytes and packets — but you must multiply by 1/p when aggregating. **Flow sampling** (hash-based) selects whole flows deterministically: you hash the five-tuple and keep the flow when the hash falls under a threshold. That preserves flow *counts* correctly — every kept flow is complete, so you can count flows, sizes, and durations without bias. The hash-based trick is what makes it deterministic and stateless: the same tuple always maps to the same decision, across reboots and across replicas, so you never double-sample or half-sample.

**Interviewer:** And the error bars? Everyone quotes "1 in 100 sampling" like it's free.

**Candidate:** This is the part I'd push on. A sample of n=1000 flows gives you roughly ±6% error at the 95% confidence level on the *count* (1.96/√1000) — and the error scales with the square root of the sample size, not the sampling rate. If the flow volume is 10 million and you sample 1%, that's 100,000 flows — great precision. If the flow volume is 100,000 and you sample 1%, that's 1,000 flows — every number you report is noise. So the correct design is *adaptive*: the sampling rate is set so the *kept sample count* meets a target, not so the rate is a nice round number. And for volume estimates, packet sampling and flow sampling have different variance profiles — you should report which one you used, because the consumers of the data will mis-trust numbers they can't reproduce.

---

## Flow Logs and Aggregation

**Interviewer:** Flow logs arrive at millions per minute. What does the aggregation pipeline look like?

**Candidate:** Three stages. **Ingest**: parse and validate, drop malformed records, apply the sampling corrections (the 1/p multiplier). **Aggregate**: roll flows up by window and by key — source, destination, service, or the tuple — with a configurable cardinality budget. The design rule: *the number of keys you aggregate by is a budget you choose*, because cardinality is what kills the storage and query tiers, not volume. **Query**: expose the aggregates — top talkers, total bytes per service, connection attempts per source — with a few well-chosen pre-computed dimensions and a raw-flow store for drill-down at lower retention.

**Interviewer:** What's the failure mode you've actually seen with flow aggregation?

**Candidate:** The cardinality bomb. Someone adds a dimension with a million distinct values — the client IP on a service behind a NAT, say — and the aggregate store's memory goes vertical overnight. The fix is a two-tier design: *hot* aggregates keep high-cardinality detail for minutes, *cold* aggregates keep bounded-cardinality rollups for months. The lesson: cardinality is a first-class design constraint for observability systems, not an afterthought — it belongs in the design review for every new dimension.

---

## eBPF

**Interviewer:** Where does eBPF fit, and when is it the right tool?

**Candidate:** eBPF is the ability to run sandboxed programs inside the kernel at observability hook points — packet arrival (XDP), the network stack (TC), syscalls, socket events — without loading kernel modules or changing the kernel. The right time to use it: when you need per-connection visibility on *every* packet of *every* service without deploying agents into each one, or when the metric (retransmits, RTT, dropped packets) only exists inside the kernel. The wrong time: when you need to correlate application context — eBPF sees packets and syscalls, not the application's intent; the service mesh and tracing layers are the right tools there.

**Interviewer:** What should an interviewer know about eBPF's limits?

**Candidate:** Three limits worth naming. First, **verifier constraints**: programs must prove termination and bounded memory in a static analysis pass — complex logic gets rejected, so the classic pattern is "small kernel program, rich userspace consumer," not everything in-kernel. Second, **privilege and portability**: it needs privileged pods or a dedicated agent daemonset; kernel version differences mean programs are compiled per-host. Third, **data volume**: eBPF can generate events at line rate — the map-to-userspace transfer is the bottleneck, and that's where sampling and aggregation re-enter the design. The eBPF layer is not a separate system; it's the *collector* feeding the same aggregation pipeline.

---

## The Design Exercise

**Interviewer:** Design the network observability stack for a 200-service platform, using flow logs, eBPF metrics, and a sampling strategy. Budget: three engineers.

**Candidate:**

1. **Collect**: eBPF agent per host (it's kernel-level, so one per node, not one per service) collecting flow records — five-tuple, timestamps, byte/packet counts — plus kernel metrics: retransmits, RTT, drops. Agents are stateless: they sample and emit, nothing else.
2. **Sample**: hash-based flow sampling at the agent, adaptive rate targeting a fixed kept-sample count; packet sampling only where volume estimates matter more than flow counts. The rates are per-node config, set by the control plane, so a noisy node doesn't blow the global budget.
3. **Aggregate**: two tiers as I described — hot high-cardinality minutes, cold bounded rollups; the top-N queries are pre-computed so the most common questions never touch the raw store.
4. **Correlate**: a tracing layer that tags flows with service and request ids where the mesh can do it — this is the bridge from *network* observability to *application* observability; the same five-tuple appears in both, so the join is natural.
5. **Act**: alert on the kernel metrics (retransmit spikes, drop rates) with a hysteresis window, not on raw counters — and drive a *packet capture* trigger: when a suspicious flow pattern matches, capture that flow at full detail for diagnosis.

**Interviewer:** Three engineers is tight. What do you explicitly *not* build?

**Candidate:** We don't build a packet capture store — no pcap retention. Captures are on-demand, triggered, retained for hours. We don't build our own query language — we use the platform's metrics query. We buy, we don't build, the storage tier. And we don't attempt full-fidelity capture at the edge, ever — the whole design is built on the sampling contract. The three engineers go into the pipeline, the sampling math, and the alerting quality.

**Interviewer:** Last question — how do you know the observability stack is working?

**Candidate:** Three checks. **Completeness**: a synthetic flow generator emits known flows; the pipeline must report them with the correct sampling-corrected counts — this is the determinism test. **Accuracy**: replay a captured corpus through the pipeline and compare against the ground truth of the full capture; drift in the error bars means the sampling math regressed. And **utility**: the mean time to answer a class of network question — "is it the network or the app?" — which is the only metric that actually matters to the people using the stack.

---

## What the Interviewer Was Looking For

- Understanding flows as the *summary* signal between metrics and captures.
- Sampling bias awareness: packet vs flow sampling, and the confidence-interval math.
- Cardinality as a first-class design constraint.
- eBPF's real role (collector, not the whole system) and its limits.
- A pipeline that is honest about its error bars and tests its own accuracy.

## Common Mistakes Candidates Make

- Treating 1-in-N sampling as free precision — no mention of sample counts or error.
- Sampling packets and then counting flows as if they were equivalent.
- Proposing full-fidelity capture as the default answer.
- Forgetting the aggregation tier entirely and querying raw flow stores.
- Describing eBPF as if it were an application-level agent.
