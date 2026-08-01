# Lab 02: Mock Interview — Senior Cloud Architect

**Role**: Senior Cloud Architect | **Topic**: Cloud Cost Optimization Engine | **Duration**: 45 minutes

---

## Interview Transcript

**Interviewer**: "Design a cloud cost optimization engine that produces rightsizing recommendations for a fleet of thousands of VMs across multiple accounts and clouds. Where do you start?"

**Candidate**: "I'd start by separating the problem into two halves: measurement and action. Measurement is collecting utilization data and normalizing it into a per-resource usage profile — CPU, memory, network I/O percentiles, and the duration of the observation window. Action is the recommendation engine: given a usage profile, what's the cheapest instance family that still satisfies the workload's headroom requirement? Both halves must be multi-cloud aware — an 'm5.large' and a 'Standard_D2s_v3' are different physical resources, so the engine works on normalized compute units, not provider SKUs, and maps back to concrete SKUs per cloud."

**Interviewer**: "What utilization data do you need, and for how long?"

**Candidate**: "At minimum, per-minute CPU and memory samples for 14 days, and I want percentiles, not averages — a workload with average CPU at 10% but p99 at 90% is bursty, and rightsizing it down will cause throttling. I'd track p50, p95, p99 for CPU, memory, and network. For the recommendation window, 14 days captures weekly seasonality; 30 days is better but delays the time-to-value. Newly launched resources should get a grace period — 7 days — before they're eligible for recommendations, because they may still be ramping up."

**Interviewer**: "How does the rightsizing algorithm actually decide to resize down? Walk me through the decision logic."

**Candidate**: "The core is a headroom policy. We define the utilization target — say p99 CPU below 75% on the recommended instance, with memory under 85% — and find the smallest instance size in the family where the simulated p99 fits. Simulate is the key word: you don't compare observed utilization to the current instance, you compare it to the *capacity of the candidate instance*, because moving from 8 vCPU to 4 vCPU doubles the utilization percentage. The algorithm steps down the family's size ladder — for a tiered family like general-purpose, from large to medium to small — and validates the simulated p99 stays under the headroom threshold. I also apply a minimum savings threshold: if a recommendation saves less than, say, 5% or $20/month, suppress it — noisy recommendations destroy trust in the tool."

**Interviewer**: "What about workloads that are clearly idle — the 'always on, never used' VM? Does rightsizing handle that?"

**Candidate**: "That's a separate recommendation class: shutdown candidates. If CPU, memory, and network are all near zero for 14 days — I use a low-water mark like p95 below 2% for all three — the resource is a shutdown candidate, not a resize candidate. These are often forgotten dev/test instances or old CI runners. The engine emits a 'shutdown with optional snapshot' action. I'd also add a third class: schedule-based stop/start for non-production workloads that are only used 9-to-5, which is where the biggest wins often are, since stop/start savings are ~100% of compute cost for the stopped hours."

**Interviewer**: "Now the hard part: how do you convert a recommendation into an action without breaking production? What's your rollout pipeline?"

**Candidate**: "The recommendation lifecycle has to be: candidate → proposed → approved → applied → verified → reverted if needed. Each recommendation carries a confidence score and a risk rating. Low-risk resizes (dev environments, no attached volumes that would be orphaned) can auto-apply after approval; high-risk ones (production, attached to a load balancer, in a stateful cluster) require explicit approval and a maintenance window. Application happens through the IaC layer, not by clicking in the console — the engine emits a Terraform PR or a change-set, which goes through the normal review pipeline, so everything is auditable and reversible. After the resize, a verification window of 24-48 hours compares actual utilization against the projected utilization; if the workload is throttling or memory pressure is high, we auto-revert. Auto-revert is essential, because manual rollback after a bad resize takes forever and kills trust."

**Interviewer**: "How do you prevent the engine from thrashing — resizing down and then back up next week?"

**Candidate**: "Three mechanisms. First, a cooldown period: a resource that was just resized is frozen for 30 days, and the same resource can't get an 'up' recommendation within 7 days of a 'down' resize. Second, trend awareness: before recommending a downsize, check the 14-day trend — if CPU is trending upward over the window (linear regression slope significantly positive), the engine must suppress the downsize recommendation even if the current p99 fits. Third, hysteresis: the threshold to resize *down* is a 75% p99 target, but to trigger a resize *up* the p99 must be above 90% — the gap prevents oscillation around a boundary. Thrashing is a trust-killer; a tool that flips its own recommendations is worse than a tool that's slightly conservative."

**Interviewer**: "Savings reporting — how do you compute realized savings without overclaiming?"

**Candidate**: "We compute realized savings only from *verifiable* evidence: the difference between the old instance's list price and the new instance's list price, pro-rated for the actual running hours, minus any data-transfer delta, and only counting the period after the resize completed. We explicitly do not count 'avoided future spend' as savings. Also important: rightsizing is one of four levers, and the report should show the full picture — rightsizing, shutdown/idle elimination, schedule-based stop/start, and purchase-plan optimization (reserved instances or committed-use discounts). A common mistake is measuring savings against list price when the account already has a private rate; the engine should consume the billing data to get the effective rate per resource."

**Interviewer**: "How does the engine deal with multi-cloud pricing? The SKUs and rate cards differ wildly."

**Candidate**: "I'd normalize to a compute unit — vCPU-hours and GB-hours with a memory coefficient — and build a pricing adapter per cloud that maps an instance SKU to (vCPU, memory, network capacity, price-per-hour). The recommendation engine reasons purely in normalized units; the adapter layer converts a normalized candidate back into a concrete SKU for each cloud, using that cloud's family ladder. Where clouds have different instance families, we also do cross-family ranking: a memory-optimized family may fit a memory-heavy workload cheaper than stepping down in the general-purpose family. The engine outputs an ordered list of candidates per cloud, ranked by projected monthly cost, with the fit-simulation results shown so a human can verify the reasoning."

**Interviewer**: "What about the data pipeline — collecting usage from thousands of instances?"

**Candidate**: "Each cloud has a metrics API; we pull with an exporter — CloudWatch, Azure Monitor, GCP Cloud Monitoring — into a time-series store, batched once an hour for cost and once per minute for utilization during the observation window. The pipeline has three stages: collection, normalization (align timestamps, fill gaps, convert units), and storage. Storage-wise, the 14-day per-minute utilization for thousands of instances is modest — a few GB — so a simple columnar store works. The interesting engineering is the scheduling: recalculate recommendations nightly, in a batch window, and publish changes to a recommendations topic that the approval workflow consumes."

**Interviewer**: "How would you test the recommendation engine?"

**Candidate**: "Synthetic workload profiles with known answers: generate a CPU profile that is flat at 30% and assert the engine recommends a half-size instance; generate a bursty profile with p99 at 90% and assert no downsize is recommended; generate a flat-zero profile and assert the shutdown class is hit. Then golden-file tests with real anonymized utilization traces: record the engine's output on historical data, and any code change must reproduce the same decisions for the same inputs — this catches silent regression in the algorithm. Finally, shadow mode in production: run the engine in parallel with the previous version for a week, diff the recommendation sets, and only promote when the diff is empty or reviewed."

**Interviewer**: "What's the single biggest source of wasted cloud spend you see in practice, and does your engine catch it?"

**Candidate**: "Orphaned and idle resources by far — unattached disks, forgotten test environments, load balancers with no backends, and instances that have been running for months at single-digit utilization. The best cost engines spend most of their detection budget on these because the savings per incident is enormous, and there's no workload risk: shutting down an idle resource can't break production. Rightsizing is the visible headline feature, but the shutdown and stop/start classes are where the real money is."

---

## Wrap-Up

**What the interviewer is looking for**:
- Percentile-based thinking, not average-based (p99 vs mean) — this separates senior candidates
- Simulated-fit logic that maps utilization to the *candidate* instance, not the current one
- A safe rollout lifecycle: approval, IaC-driven application, verification, auto-revert
- Anti-thrashing mechanisms: cooldowns, trend detection, hysteresis
- Honest savings measurement tied to billing data

**Common mistakes candidates make**:
- Recommending based on averages, which over-resizes bursty workloads
- Ignoring the rollout/approval pipeline and just emitting JSON
- No mention of verification and rollback after a resize
- Overclaiming savings by comparing to list price
- Treating all VMs as resize candidates instead of classifying idle/shutdown candidates separately
